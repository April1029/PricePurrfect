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
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.Random;

public class PetcoScraper {

  // Method to assemble the URL with search query parameters
  private static String assembleURL(String brand, String item) throws Exception {
    String baseurl = "https://www.petco.com/shop/en/petcostore/search?query=";
    String query = brand + " " + item;
    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8).replace("+", "%20");
    return baseurl + encodedQuery;
  }


  // Method to perform search operation
  private static void performSearch(WebDriver driver, WebDriverWait wait, Actions actions, Random random, String brand, String item) throws InterruptedException {
    try {
      WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("suggested-store-modal")));
      if (modal.isDisplayed()) {
        WebElement closeModalButton = driver.findElement(By.cssSelector("button-to-close-modal")); // Replace with the actual selector to close the modal
        closeModalButton.click();
      }
    } catch (Exception e) {
      // If modal is not found or not visible, proceed
    }

    WebElement searchBox = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("header-search")));
    JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
    jsExecutor.executeScript("arguments[0].click();", searchBox);
    searchBox.clear();
    searchBox.sendKeys(brand + " " + item);
    Thread.sleep(1000 + random.nextInt(2000));
    String enteredSearchTerm = searchBox.getAttribute("value");
    System.out.println("Entered Search Term: " + enteredSearchTerm);
    WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input[aria-label='Search']")));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchButton);
    actions.moveToElement(searchButton).click().perform();
  }

  // Method to parse and extract data from the loaded results
  private static void parseResults(WebDriver driver, String brand, String item) {
    String pageSource = driver.getPageSource();
    Document doc = Jsoup.parse(pageSource);
    Elements itemTitles = doc.select(".ProductTile-styled__ProductInfoContainer-sc-8250527c-2.coylKt");
    Elements itemPrices = doc.select(".typography__StyledTypography-sc-787b37da-0.typography___StyledStyledTypography-sc-787b37da-1.gKxANB.cbuIEA.price___StyledTypography-sc-e02ddb13-0.fvnbvH");

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

    FirefoxOptions options = new FirefoxOptions();
    options.addArguments("--headless");
    WebDriver driver = new FirefoxDriver(options);
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    Actions actions = new Actions(driver);
    Random random = new Random();

    try {
      String newUrl = assembleURL(brand, item);
      System.out.println("The assembled url is:" + newUrl);
      driver.get(newUrl);
      performSearch(driver, wait, actions, random, brand, item);
      parseResults(driver,brand, item);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (driver != null) {
        driver.quit();
      }
    }
  }
}
