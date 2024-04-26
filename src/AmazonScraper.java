import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Scraper implementation for Amazon. This class handles constructing search URLs,
 * executing the search, parsing the results, and returning a list of products.
 */
public class AmazonScraper implements Scraper {

  private List<Product> results = new ArrayList<>();

  /**
   * Assembles a search URL for Amazon based on the specified brand and item.
   *
   * @param brand the brand of the product to search for
   * @param item the type of the product to search for
   * @return a properly encoded URL for searching on Amazon
   */
  @Override
  public String assembleURL(String brand, String item) {
    try {
      // Encode the brand and item strings properly
      String encodedBrand = URLEncoder.encode(brand, StandardCharsets.UTF_8);
      String encodedItem = URLEncoder.encode(item, StandardCharsets.UTF_8);

      // Return the assembled URL
      return "https://www.amazon.com/s?k=" + encodedBrand + "+" + encodedItem;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Performs a web search on Amazon using the provided URL and retrieves the document.
   *
   * @param url the URL to connect to
   * @return the Document object after connecting to the specified URL
   * @throws IOException if an I/O error occurs
   */
  @Override
  public Document performSearch(String url) throws IOException {
    Document doc = Jsoup.connect(url)
        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36")
        .cookie("auth", "token")
        .timeout(3000)
        .get();

      System.out.println("Received document length: " + doc.html().length()); // Debug document length
      return doc;
  }

  /**
   * Parses the HTML document to extract relevant product data.
   *
   * @param doc the document to parse
   * @param brand the brand of the product
   * @param item the title of the product
   */
  @Override
  public void parseResults(Document doc, String brand, String item) {
    // Select all items in the search result
    Elements items = doc.select(".s-result-item");
    System.out.println("Found " + items.size() + " items from Amazon"); // Debug number of items found

    // Loop through each item element
    for (int i = 0; i < items.size(); i++) {
      // Get the current item element
      Element itemElement = items.get(i);

      // Extract the title from the current item element
      String title = itemElement.select(".a-size-base-plus.a-color-base.a-text-normal").text();

      // Extract the whole part of the price from the current item element
      String whole = itemElement.select(".a-price-whole").text().replaceAll("[^0-9]", "");  // Removing non-numeric characters

      // Extract the fractional part of the price from the current item element
      String fraction = itemElement.select(".a-price-fraction").text().replaceAll("[^0-9]", "");  // Removing non-numeric characters

      // If the fraction part is missing, default it to '00'
      if (fraction.isEmpty()) {
        fraction = "00";
      }
      String price = whole + "." + fraction;

      // Only print out items that have a title and a price
      // Do another layer of filtering here to validate the search results
      if (!title.isEmpty() && !whole.isEmpty()) {
        if (title.toLowerCase().contains(item.toLowerCase()) && title.toLowerCase().contains(brand.toLowerCase())) {
          //results.add("Title: " + title+ " | Price: " + whole + "." + fraction);
          Product product = new Product(title, price);
          results.add(product);
        }
      }

    }
    System.out.println("Total products after filtering: " + results.size());
  }

  /**
   * Returns the list of products found during the search.
   *
   * @return a list of Product objects
   */
  @Override
  public List<Product> getResults() {
    return results;
  }

}
