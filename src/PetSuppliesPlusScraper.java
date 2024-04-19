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
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class PetSuppliesPlusScraper {

  // Method to assemble the URL with search query parameters
  private static String assembleURL(String brand, String item) throws UnsupportedEncodingException {
    String baseurl = "https://www.petsuppliesplus.com/search?query=";
    String query = brand + " " + item;
    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8).replace("+", "%20");
    return baseurl + encodedQuery + "#q=" + encodedQuery;
  }

  // Method to perform the search
  private static void performSearch(WebDriver driver, WebDriverWait wait, Actions actions, String brand, String item) throws InterruptedException {
    WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[aria-label='Search']")));
    searchBox.clear();
    searchBox.sendKeys(brand + " " + item);
    Thread.sleep(1000);
    String enteredSearchTerm = searchBox.getAttribute("value");
    System.out.println("Entered Search Term: " + enteredSearchTerm);

    try {
      WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("suggested-store-modal")));
      if (modal.isDisplayed()) {
        WebElement closeModalButton = driver.findElement(By.cssSelector("button-to-close-modal")); // Replace with the actual selector to close the modal
        closeModalButton.click();
      }
    } catch (Exception e) {
      // If modal is not found or not visible, proceed
    }

    WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".CoveoSearchButton.coveo-accessible-button")));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchButton);
    actions.moveToElement(searchButton).click().perform();
  }




  // Method to parse and extract data from the loaded results
  private static void parseResults(WebDriver driver, String brand, String item) {
    String pageSource = driver.getPageSource();
    Document doc = Jsoup.parse(pageSource);
    Elements itemTitles = doc.select(".CoveoResultLink.coveo-link-click.coveo-result-title");
    Elements itemPrices = doc.select(".price");

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
    String brand = "instinct";
    String item = "dry cat food";

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    options.addArguments("--disable-gpu");
    options.addArguments("--window-size=1920,1200");
    options.addArguments("--ignore-certificate-errors");

    WebDriver driver = new ChromeDriver(options);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    Actions actions = new Actions(driver);

    try {
      String newUrl = assembleURL(brand, item);
      System.out.println("The url we are visiting is:" + newUrl);
      driver.get(newUrl);

      performSearch(driver, wait, actions, brand, item);
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("coveo-results-column")));
      parseResults(driver, brand, item);

    } catch (UnsupportedEncodingException | InterruptedException e) {
      e.printStackTrace();
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
