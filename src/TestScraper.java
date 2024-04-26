import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * A test implementation of the Scraper interface. This class is primarily used for testing purposes
 * to provide controlled, predictable interaction with the scraping process without connecting to real web resources.
 */
class TestScraper implements Scraper {
  private List<Product> products;

  /**
   * Constructs a new TestScraper with a predefined list of products.
   * This allows the scraper to return known data, useful in testing environments.
   *
   * @param products The list of products that this scraper will return when getResults() is called.
   */
  public TestScraper(List<Product> products) {
    this.products = products;
  }

  /**
   * Provides a mock URL for testing purposes. This URL is not expected to be used for real HTTP requests.
   *
   * @param brand The brand name, used in generating the mock URL.
   * @param item The item description, used in generating the mock URL.
   * @return A string representing a mock URL.
   */
  @Override
  public String assembleURL(String brand, String item) {
    return "http://example.com";
  }

  /**
   * Simulates the action of performing a search by returning a minimal, static HTML document.
   * This method does not perform real web requests.
   *
   * @param url The URL to simulate the search with. This parameter is not actually used in the method.
   * @return A Document object parsed from a basic, static HTML string.
   */
  @Override
  public Document performSearch(String url) {
    return Jsoup.parse("<html></html>"); // Minimal HTML for parsing
  }

  /**
   * A placeholder method for parsing results from a Document. In this mock class,
   * it performs no operation.
   *
   * @param doc The document to parse. Not used in this mock implementation.
   * @param brand The brand name to filter or parse results for. Not used in this mock implementation.
   * @param item The item description to filter or parse results for. Not used in this mock implementation.
   */
  @Override
  public void parseResults(Document doc, String brand, String item) {
    // No operation in this mock method
  }

  /**
   * Returns the list of products that were initialized with this scraper.
   *
   * @return A list of Product objects representing the predefined test data.
   */
  @Override
  public List<Product> getResults() {
    return products;
  }
}
