import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class PetSuppliesPlusScraper implements Scraper {
  private WebDriver driver;
  private WebDriverWait wait;
  private Actions actions;
  private String brand;
  private  String item;
  private List<Product> results = new ArrayList<>();

  public PetSuppliesPlusScraper (String brand, String item) {
    this.brand = brand;
    this.item = item;
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    options.addArguments("--disable-gpu");
    options.addArguments("--window-size=1920,1200");
    options.addArguments("--ignore-certificate-errors");

    this.driver = new ChromeDriver(options);
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    this.actions = new Actions(driver);

  }

  // Method to assemble the URL with search query parameters
  @Override
  public String assembleURL(String brand, String item) throws UnsupportedEncodingException {
    String baseurl = "https://www.petsuppliesplus.com/search?query=";
    String query = brand + " " + item;
    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8).replace("+", "%20");
    return baseurl + encodedQuery + "#q=" + encodedQuery;
  }

  // Method to perform the search
  @Override
  public Document performSearch(String url) throws IOException, InterruptedException {
    this.driver.get(url);
    WebElement searchBox = this.wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[aria-label='Search']")));
    searchBox.clear();
    searchBox.sendKeys(this.brand + " " + this.item);
    Thread.sleep(1000);
    String enteredSearchTerm = searchBox.getAttribute("value");
    System.out.println("Entered Search Term: " + enteredSearchTerm);

    try {
      WebElement modal = this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("suggested-store-modal")));
      if (modal.isDisplayed()) {
        WebElement closeModalButton = this.driver.findElement(By.cssSelector("button-to-close-modal")); // Replace with the actual selector to close the modal
        closeModalButton.click();
      }
    } catch (Exception e) {
      // If modal is not found or not visible, proceed
    }

    WebElement searchButton = this.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".CoveoSearchButton.coveo-accessible-button")));
    ((JavascriptExecutor) this.driver).executeScript("arguments[0].scrollIntoView(true);", searchButton);
    this.actions.moveToElement(searchButton).click().perform();
    String pageSource = this.driver.getPageSource();
    Document doc = Jsoup.parse(pageSource);
    return doc;
  }

  // Method to parse and extract data from the loaded results
  @Override
  public void parseResults(Document doc, String brand, String item) {

    System.out.println("Received document length: " + doc.html().length()); // Debug document length
    Elements itemTitles = doc.select(".CoveoResultLink.coveo-link-click.coveo-result-title");
    Elements itemPrices = doc.select(".price");
    System.out.println("Found " + itemTitles.size() + " items"); // Debug number of items found

    for (int i = 0; i < itemTitles.size(); i++) {
      Element title = itemTitles.get(i);
      Element price = (itemPrices.size() > i) ? itemPrices.get(i) : null;

      if (title.text().toLowerCase().contains(this.item.toLowerCase()) && title.text().toLowerCase().contains(this.brand.toLowerCase())) {

        //String outputMessage = "Title: " + title.text();
        //if (price != null) {
        //  outputMessage += " | Price: " + price.text();
        //} else {
        //  outputMessage += "Price not found";
        //}
        // results.add(outputMessage);
        Product product = new Product(title.text(), price.text());
        results.add(product);
      }
    }
  }

  @Override
  public List<Product> getResults() {
    return results;
  }

}
