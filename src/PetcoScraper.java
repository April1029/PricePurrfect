import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Random;

public class PetcoScraper implements Scraper {

  private WebDriver driver;
  private WebDriverWait wait;
  private Actions actions;
  private String brand;
  private  String item;
  private Random random;

  public PetcoScraper (String brand, String item) {
    this.brand = brand;
    this.item = item;
    FirefoxOptions options = new FirefoxOptions();
    options.addArguments("--headless");
    this.driver = new FirefoxDriver(options);
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    this.actions = new Actions(driver);
    this.random = new Random();

  }

  // Method to assemble the URL with search query parameters
  @Override
  public String assembleURL(String brand, String item) throws UnsupportedEncodingException {
    String baseurl = "https://www.petco.com/shop/en/petcostore/search?query=";
    String query = brand + " " + item;
    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8).replace("+", "%20");
    return baseurl + encodedQuery;
  }


  // Method to perform search operation
  @Override
  public Document performSearch(String url) throws IOException, InterruptedException {
    this.driver.get(url);
    try {
      WebElement modal = this.wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("suggested-store-modal")));
      if (modal.isDisplayed()) {
        WebElement closeModalButton = this.driver.findElement(By.cssSelector("button-to-close-modal")); // Replace with the actual selector to close the modal
        closeModalButton.click();
      }
    } catch (Exception e) {
      // If modal is not found or not visible, proceed
    }

    WebElement searchBox = this.wait.until(ExpectedConditions.presenceOfElementLocated(By.id("header-search")));
    JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
    jsExecutor.executeScript("arguments[0].click();", searchBox);
    searchBox.clear();
    searchBox.sendKeys(brand + " " + item);
    Thread.sleep(1000 + this.random.nextInt(2000));
    String enteredSearchTerm = searchBox.getAttribute("value");
    System.out.println("Entered Search Term: " + enteredSearchTerm);
    WebElement searchButton = this.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[aria-label='Search']")));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchButton);
    this.actions.moveToElement(searchButton).click().perform();
    String pageSource = this.driver.getPageSource();
    Document doc = Jsoup.parse(pageSource);
    return doc;
  }

  // Method to parse and extract data from the loaded results
  @Override
  public void parseResults(Document doc, String brand, String item) {
    System.out.println("Received document length: " + doc.html().length()); // Debug document length
    Elements itemTitles = doc.select(".ProductTile-styled__ProductInfoContainer-sc-8250527c-2.coylKt");
    Elements itemPrices = doc.select(".typography__StyledTypography-sc-787b37da-0.typography___StyledStyledTypography-sc-787b37da-1.gKxANB.cbuIEA.price___StyledTypography-sc-e02ddb13-0.fvnbvH");
    System.out.println("Found " + itemTitles.size() + " items"); // Debug number of items found

    for (int i = 0; i < itemTitles.size(); i++) {
      Element title = itemTitles.get(i);
      Element price = (itemPrices.size() > i) ? itemPrices.get(i) : null;

      if (title.text().toLowerCase().contains(item) && title.text().toLowerCase().contains(brand)) {
        String outputMessage = "Title: " + title.text();
        if (price != null) {
          outputMessage += " | Price: " + price.text();
        } else {
          outputMessage += "Price not found";
        }
        System.out.println(outputMessage);
      }
    }
  }

  public static void main(String[] args) {
    String brand = "arm & hammer";
    String item = "deodorizer";

    PetcoScraper scraper = new PetcoScraper(brand,item);

    try {
      String newUrl = scraper.assembleURL(brand, item);
      System.out.println("Fetching URL: " + newUrl); // Debug URL fetching
      //driver.get(newUrl);
      Document doc = scraper.performSearch(newUrl);
      System.out.println("Document fetched, parsing results..."); // Debug document fetch
      scraper.parseResults(doc,brand, item);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (scraper.driver != null) {
        scraper.driver.quit();
      }
    }
  }
}
