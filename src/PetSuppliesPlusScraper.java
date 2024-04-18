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
  public static void main(String[] args) {
    String brand = "instinct";
    String item = "dry cat food";

    // Set path to the ChromeDriver executable
    System.setProperty("webdriver.chrome.driver", "/Users/jujuba/Downloads/chromedriver-mac-arm64/chromedriver");

    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    options.addArguments("--disable-gpu");
    options.addArguments("--window-size=1920,1200");
    options.addArguments("--ignore-certificate-errors");

    WebDriver driver = new ChromeDriver(options);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    Actions actions = new Actions(driver);

    try {
      // Construct the URL with search query parameters

      String baseurl = "https://www.petsuppliesplus.com/search?query=";
      String query = brand + " " + item;
      String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8).replace("+", "%20");
      String newUrl = baseurl + encodedQuery + "#q=" + encodedQuery;
      driver.get(newUrl);
      System.out.println("The url we are visiting is:" + newUrl);

      //driver.get("https://www.petsuppliesplus.com/search?query=kjj?query=arm%20&%20hammer%20deodorizer#q=arm%26hammer%20deodorizer&sort=relevancy&f:availablestores=[4132]");

      // Wait for the search box to be present in the DOM and enter the search terms
      WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(
          By.cssSelector("input[aria-label='Search']")
      ));
      searchBox.clear();
      searchBox.sendKeys(brand + " " + item);
      Thread.sleep(1000);
      String enteredSearchTerm = searchBox.getAttribute("value");
      System.out.println("Entered Search Term: " + enteredSearchTerm);

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

      // Scroll to the search button and click it
      WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".CoveoSearchButton.coveo-accessible-button")));
      ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchButton);
      actions.moveToElement(searchButton).click().perform();


      // Wait for the results to load using an appropriate condition
      // This should be updated with an actual condition that reflects the results being loaded
      wait.until(ExpectedConditions.visibilityOfElementLocated(
          By.className("coveo-results-column")
      ));

      // Now you can parse the loaded results
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
