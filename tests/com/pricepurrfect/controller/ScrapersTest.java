package com.pricepurrfect.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.pricepurrfect.model.Product;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

/**
 * Tests for various scraper implementations to verify URL assembly, search execution, results
 * parsing, and final result retrieval.
 */
public class ScrapersTest {

  /**
   * Tests URL assembly across multiple scraper implementations to ensure that each scraper
   * correctly formats the query URL based on given input parameters.
   *
   * @throws UnsupportedEncodingException if URL encoding is not supported.
   */
  @Test
  public void testAssembleURL() throws UnsupportedEncodingException {
    // Define the brand and item
    String brand = "instinct";
    String item = "dry cat food";

    // Initialize scrapers
    Scraper scraper1 = new PetSuppliesPlusScraper(brand, item);
    Scraper scraper2 = new PetSmartScraper();
    Scraper scraper3 = new PetcoScraper(brand, item);
    Scraper scraper4 = new AmazonScraper();

    // Expected URL
    String expected1 = "https://www.petsuppliesplus.com/search?query=instinct%20dry%20cat%20food#q=instinct%20dry%20cat%20food";
    String expected2 = "https://www.petsmart.com/search/?q=instinct+dry+cat+food";
    String expected3 = "https://www.petco.com/shop/en/petcostore/search?query=instinct%20dry%20cat%20food";
    String expected4 = "https://www.amazon.com/s?k=instinct+dry+cat+food";

    // Test the assembleURL method
    String result1 = scraper1.assembleURL(brand, item);
    String result2 = scraper2.assembleURL(brand, item);
    String result3 = scraper3.assembleURL(brand, item);
    String result4 = scraper4.assembleURL(brand, item);
    assertEquals(expected1, result1);
    assertEquals(expected2, result2);
    assertEquals(expected3, result3);
    assertEquals(expected4, result4);
  }

  /**
   * Tests the execution of a search using a scraper, ensuring that a non-null, non-empty document
   * is retrieved as a result of the search operation.
   *
   * @throws IOException if an I/O error occurs during search execution.
   */
  @Test
  public void testPerformSearch() throws IOException {
    // Create an instance of TestScraper
    TestScraper testScraper = new TestScraper(new ArrayList<>());

    // Call performSearch method
    String url = "http://example.com";
    Document document = testScraper.performSearch(url);

    // Assert that the document is not null
    assertNotNull(document);

    // Assert that the document has been parsed correctly
    assertTrue(document.html().length() > 0); // Ensure that the HTML content is not empty
  }

  /**
   * Tests the parsing functionality of various scrapers to ensure they correctly interpret HTML and
   * extract expected product details.
   */
  @Test
  public void testParseResults() {
    // Call parseResults method
    String brand = "brand";
    String item = "Item";

    // Create instances of TestScrapers
    Scraper scraper1 = new PetSuppliesPlusScraper(brand, item);
    Scraper scraper2 = new PetSmartScraper();
    Scraper scraper3 = new PetcoScraper(brand, item);
    Scraper scraper4 = new AmazonScraper();

    // Create sample HTML documents to parse for scrapers
    // PSP
    String html1 = "<html><body>" +
        "<div class='CoveoResultLink coveo-link-click coveo-result-title'>Product 1 brand item</div>"
        +
        "<div class='CoveoResultLink coveo-link-click coveo-result-title'>Product 2 brand item</div>"
        +
        "<div class='price'>$10.00</div>" +
        "<div class='price'>$20.00</div>" +
        "</body></html>";
    Document document1 = Jsoup.parse(html1);
    scraper1.parseResults(document1, brand, item);

    // PetSmart
    String html2 = "<html><body>" +
        "<div class='product-name'>brand item Product 1</div>" +
        "<div class='product-name'>brand item Product 2</div>" +
        "<div class='product-price'>$10.00</div>" +
        "<div class='product-price'>$20.00</div>" +
        "</body></html>";
    Document document2 = Jsoup.parse(html2);
    scraper2.parseResults(document2, brand, item);

    // Petco
    String html3 = "\n"
        + "  \n"
        + "            <div title=\"dummyTitle\" class=\"dummyClass01\">\n"
        + "                <p> R=brand item product</p>\n"
        + "            </div>\n"
        + "            <div class=\"dummyClass02\">\n"
        + "                <ul title=\"4.7 out of 5 stars\" class=\"dummyClass03\">\n"
        + "                    \n"
        + "                </ul>\n"
        + "                <p aria-label=\"dummyLabel\" class=\"dummyClass04\"></p>\n"
        + "            </div>\n"
        + "            <div class =\"dummyClass05\">\n"
        + "                <span aria-label=\"$10.00\n"
        + "            </div>\n"
        + "           ";
    Document document3 = Jsoup.parse(html3);
    scraper3.parseResults(document3, brand, item);

    // Amazon
    String html4 = "<html><body>" +
        "<div class='s-result-item'>" +
        "<span class='a-size-base-plus a-color-base a-text-normal'>brand item 1</span>" +
        "<span class='a-price-whole'>25</span>" +
        "<span class='a-price-fraction'>99</span>" +
        "</div>" +
        "<div class='s-result-item'>" +
        "<span class='a-size-base-plus a-color-base a-text-normal'>item 2</span>" +
        "<span class='a-price-whole'>15</span>" +
        "<span class='a-price-fraction'>49</span>" +
        "</div>" +
        "</body></html>";
    Document document4 = Jsoup.parse(html4);
    scraper4.parseResults(document4, brand, item);

    // Get the results from the scrappers
    List<Product> results1 = scraper1.getResults();
    List<Product> results2 = scraper2.getResults();
    List<Product> results3 = scraper3.getResults();
    List<Product> results4 = scraper4.getResults();

    // Assert that the results are not null
    assertNotNull(results1);
    assertNotNull(results2);
    assertNotNull(results3);
    assertNotNull(results4);

    // Assert that the results list contains the expected number of products
    assertEquals(2, results1.size());
    assertEquals(2, results2.size());
    assertEquals(1, results3.size());
    assertEquals(1, results4.size());

    // Assert that each product in the results list has the expected title and price
    assertEquals("Product 1 brand item", results1.get(0).getTitle());
    assertEquals("$10.00", results1.get(0).getPrice());
    assertEquals("Product 2 brand item", results1.get(1).getTitle());
    assertEquals("$20.00", results1.get(1).getPrice());

    assertEquals("brand item Product 1", results2.get(0).getTitle());
    assertEquals("$10.00", results2.get(0).getPrice());
    assertEquals("brand item Product 2", results2.get(1).getTitle());
    assertEquals("$20.00", results2.get(1).getPrice());

    String title = results3.get(0).getTitle().toString();
    boolean containsBrandItem = title.contains("brand item");
    assertTrue(containsBrandItem);

    assertEquals("brand item 1", results4.get(0).getTitle());
    assertEquals("25.99", results4.get(0).getPrice());
  }

  /**
   * Tests the retrieval of results from scrapers to confirm that the correct products, as added in
   * test setup, are returned.
   */
  @Test
  public void testGetResults() {
    // Create a sample list of products
    List<Product> expectedResults = new ArrayList<>();
    expectedResults.add(new Product("Product 1", "10.99"));
    expectedResults.add(new Product("Product 2", "20.49"));

    // Create an instance of the TestScraper with the sample list of products
    TestScraper scraper = new TestScraper(expectedResults);

    // Call the getResults method
    List<Product> actualResults = scraper.getResults();

    // Assert that the results are not null
    assertNotNull(actualResults);

    // Assert that the returned results are equal to the expected results
    assertEquals(expectedResults.size(), actualResults.size());
    for (int i = 0; i < expectedResults.size(); i++) {
      assertEquals(expectedResults.get(i).getTitle(), actualResults.get(i).getTitle());
      assertEquals(expectedResults.get(i).getPrice(), actualResults.get(i).getPrice());
    }
  }
}

