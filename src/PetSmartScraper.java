import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Implements the Scraper interface for extracting product data from PetSmart's website.
 * This class handles the generation of URLs, fetching of the web page, and parsing the HTML content.
 */
public class PetSmartScraper implements Scraper {
  private List<Product> results = new ArrayList<>();

  /**
   * Constructs a search URL for PetSmart based on the provided brand and item.
   *
   * @param brand The brand to include in the search query.
   * @param item The item to include in the search query.
   * @return The fully constructed URL to perform the search.
   */
  @Override
  public String assembleURL(String brand, String item) {
    // Replace spaces with '+' to conform to URL encoding expectations
    return "https://www.petsmart.com/search/?q=" + brand.replace(" ", "+") + "+" + item.replace(" ", "+");
  }

  /**
   * Fetches the HTML content of the specified URL using Jsoup.
   *
   * @param url The URL from which to fetch the content.
   * @return A Document object representing the fetched HTML.
   * @throws IOException If there is a problem connecting to the website.
   */
  @Override
  public Document performSearch(String url) throws IOException {
    // Connect to the URL and fetch the document with specified user agent and timeout
    Document doc = Jsoup.connect(url)
        .cookie("auth", "token")
        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
        .timeout(30000)
        .get();

    // Debug statement to check the length of the fetched document
    System.out.println("Received document length from PetSmart: " + doc.html().length());
    return doc;
  }

  /**
   * Parses the fetched HTML document to extract product data.
   *
   * @param doc The Document object to parse.
   * @param brand The brand used for filtering the products.
   * @param item The item description used for filtering the products.
   */
  @Override
  public void parseResults(Document doc, String brand, String item) {
    // Select elements containing product names and prices
    Elements itemTitles = doc.select(".product-name");
    Elements itemPrices = doc.select(".product-price");
    System.out.println("Found " + itemTitles.size() + " items from PetSmart");

    // Iterate over the product elements and filter based on brand and item inclusion
    for (int i = 0; i < itemTitles.size(); i++) {
      Element title = itemTitles.get(i);
      Element price = itemPrices.get(i);
      // Filter the products containing the specified brand and item in the title
      if (title.text().toLowerCase().contains(item.toLowerCase()) && title.text().toLowerCase().contains(brand.toLowerCase())) {
        Product product = new Product(title.text(), price.text());
        results.add(product);
      }
    }
    // Debug statement to check the number of products after filtering
    System.out.println("Total products after filtering: " + results.size());
  }

  /**
   * Returns the list of products that have been found and added to the results list.
   *
   * @return A list of Product objects containing the search results.
   */
  @Override
  public List<Product> getResults() {
    return results;
  }
}
