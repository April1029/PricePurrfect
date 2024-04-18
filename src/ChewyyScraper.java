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
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.openqa.selenium.JavascriptExecutor;
// other imports


public class ChewyyScraper {
  public static void main(String[] args) {
    String brand = "arm & hammer";
    String item = "deodorizer";

    // Set path to the ChromeDriver executable
    System.setProperty("webdriver.chrome.driver", "/Users/jujuba/Downloads/chromedriver-mac-arm64/chromedriver");

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless=new");
    options.addArguments("--disable-gpu");
    options.addArguments("--window-size=1920,1200");
    options.addArguments("--ignore-certificate-errors");
    options.addArguments("--disable-blink-features=AutomationControlled");
    options.addArguments("--remote-debugging-port=8888");
//    options.addArguments("--test-third-party-cookie-phaseout");

    WebDriver driver = new ChromeDriver(options);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    Actions actions = new Actions(driver);

    try {
      // Construct the URL with search query parameters

      String baseurl = "https://www.chewy.com/s?query=";
      String query = brand + " " + item;
      String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8).replace("+", "%20");
      String newUrl = baseurl + encodedQuery;
      driver.get(newUrl);
      System.out.println(driver.getCurrentUrl());
      System.out.println("The assembled url is:" + newUrl);
      System.out.println(driver.getPageSource());

      // Check if a modal is present and close it if it is
      try {
        WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("suggested-store-modal")));
        if (modal.isDisplayed()) {
          WebElement closeModalButton = driver.findElement(By.cssSelector("button-to-close-modal")); // Replace with the actual selector to close the modal
          closeModalButton.click();
        }
      } catch (Exception e) {
        // If modal is not found or not visible, proceed
      }

      WebElement websiteHeader = wait.until(ExpectedConditions.presenceOfElementLocated(
          By.cssSelector(".desktop-header__search")));

      WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
          By.id("search-autocomplete")));

      JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
      jsExecutor.executeScript("arguments[0].click();", searchBox);

      // Wait for the search box to be present in the DOM and enter the search terms
      searchBox.sendKeys(brand + " " + item);
      Thread.sleep(1000);
      String enteredSearchTerm = searchBox.getAttribute("value");
      System.out.println("Entered Search Term: " + enteredSearchTerm);



      // Scroll to the search button and click it
      WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[aria-label='Submit search'']")));
      ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchButton);
      actions.moveToElement(searchButton).click().perform();


      // Wait for the results to load using an appropriate condition
      // This should be updated with an actual condition that reflects the results being loaded
      wait.until(ExpectedConditions.visibilityOfElementLocated(
          By.className("ProductListingGrid_gridContainer__FNS4h.js-tracked-product-list")
      ));

      // Parse the loaded results
      String pageSource = driver.getPageSource();
      Document doc = Jsoup.parse(pageSource);

      // Extract and print titles and prices using Jsoup selectors that match the page content
      Elements itemTitles = doc.select(".CoveoResultLink.coveo-link-click.coveo-result-title");
      Elements itemPrices = doc.select(".price");

      for (int i = 0; i < itemTitles.size(); i++) {
        Element title = itemTitles.get(i);
        // Handle cases where there might be more titles than prices
        Element price;
        if (itemPrices.size() > i) {
          price = itemPrices.get(i);
        } else {
          price = null;
        }

        if (title.text().toLowerCase().contains(item) && title.text().toLowerCase().contains(brand)){
          String outputMessage = "Title: " + title.text();
          if (price != null) {
            outputMessage += " | Price: " + price.text();
          } else {
            outputMessage += "Price not found";
          }
          System.out.println(outputMessage);
        }
      }

    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
