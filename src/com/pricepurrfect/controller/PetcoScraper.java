package com.pricepurrfect.controller;

import com.pricepurrfect.model.Product;
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
import org.jsoup.select.Evaluator;
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

/**
 * Scraper implementation for the Petco website. It uses Selenium WebDriver to interact with the
 * website and Jsoup to parse the HTML content.
 */
public class PetcoScraper implements Scraper {

  private List<Product> results = new ArrayList<>();

  private WebDriver driver;
  private WebDriverWait wait;
  private Actions actions;
  private String brand;
  private String item;
  private Random random;

  /**
   * Constructor initializes the scraper with a brand and item.
   *
   * @param brand The brand name to search for.
   * @param item  The item description to search for.
   */
  public PetcoScraper(String brand, String item) {
    this.brand = brand;
    this.item = item;
    FirefoxOptions options = new FirefoxOptions();
    options.addArguments("--headless"); // Run browser in headless mode
    this.driver = new FirefoxDriver(options);
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    this.actions = new Actions(driver);
    this.random = new Random();
  }

  /**
   * Assembles a search URL for the Petco website based on the provided brand and item.
   *
   * @param brand The brand name to include in the query.
   * @param item  The item title to include in the query.
   * @return A fully and properly encoded URL ready for browsing.
   * @throws UnsupportedEncodingException if the encoding process fails.
   */
  @Override
  public String assembleURL(String brand, String item) throws UnsupportedEncodingException {
    String baseurl = "https://www.petco.com/shop/en/petcostore/search?query=";
    String query = brand + " " + item;
    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8).replace("+", "%20");
    return baseurl + encodedQuery;
  }


  /**
   * Uses Selenium to perform a search on the Petco website. Handles modals and inputs search terms
   * into the search box.
   *
   * @param url The URL to fetch using the web driver.
   * @return A Document object parsed from the HTML of the web page.
   * @throws IOException          if an error occurs during web navigation.
   * @throws InterruptedException if the thread sleep is interrupted.
   */
  @Override
  public Document performSearch(String url) throws IOException, InterruptedException {
    this.driver.get(url);
    try {
      WebElement modal = this.wait.until(
          ExpectedConditions.visibilityOfElementLocated(By.id("suggested-store-modal")));
      if (modal.isDisplayed()) {
        WebElement closeModalButton = this.driver.findElement(By.cssSelector(
            "button-to-close-modal")); // Replace with the actual selector to close the modal
        closeModalButton.click();
      }
    } catch (Exception e) {
      // If modal is not found or not visible, proceed
    }

    WebElement searchBox = this.wait.until(
        ExpectedConditions.presenceOfElementLocated(By.id("header-search")));
    JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
    jsExecutor.executeScript("arguments[0].click();", searchBox);
    searchBox.clear();
    searchBox.sendKeys(brand + " " + item);
    Thread.sleep(1000 + this.random.nextInt(2000)); // Simulate a more human-like interaction
    String enteredSearchTerm = searchBox.getAttribute("value");
    System.out.println("Entered Search Term: " + enteredSearchTerm);
    WebElement searchButton = this.wait.until(
        ExpectedConditions.elementToBeClickable(By.cssSelector("input[aria-label='Search']")));
    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", searchButton);
    this.actions.moveToElement(searchButton).click().perform();
    String pageSource = this.driver.getPageSource();
    Document doc = Jsoup.parse(pageSource);
    return doc;
  }

  /**
   * Parses the HTML document to extract relevant product data.
   *
   * @param doc   The document to parse.
   * @param brand The brand used in filtering results.
   * @param item  The item description used in filtering results.
   */
  @Override
  public void parseResults(Document doc, String brand, String item) {
    System.out.println("Received document length: " + doc.html().length()); // Debug document length
    Elements uls = doc.select("ul");
    for (int i = 0; i < uls.size(); i++) {
      if (!uls.get(i).attr("title").contains("out of 5 stars")) {
        continue;
      }
      Element title = uls.get(i).parent().previousElementSibling();
      Element price = uls.get(i).parent().nextElementSibling();
      if (title != null && price != null) {
        String titleText = title.text();
        String priceText = price.text();

        if (titleText.toLowerCase().contains(item.toLowerCase()) && titleText.toLowerCase()
            .contains(brand.toLowerCase())) {
          Product product = new Product(title.text(), price.text());
          results.add(product);
        }
      }
    }
    System.out.println("Total products after filtering: " + results.size());
  }

  /**
   * Returns the list of products found during the scraping process.
   *
   * @return A list of Product objects.
   */
  @Override
  public List<Product> getResults() {
    return results;
  }
}
