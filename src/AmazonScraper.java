import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AmazonScraper {

  public static void main(String[] args) {
    String brand = "arm & hammer";
    String item = "deodorizer";
    try {
      String url = assembleURL(brand, item);
      System.out.println("Fetching URL: " + url); // Debug URL fetching
      Document doc = performSearch(url);
      System.out.println("Document fetched, parsing results..."); // Debug document fetch
      parseResult(doc, brand, item);
    } catch (IOException e) {
      System.err.println("Failed to retrieve data: " + e.getMessage());
    }
  }

  private static String assembleURL(String brand, String item) {
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

  private static Document performSearch(String url) throws IOException {
    Document doc = Jsoup.connect(url)
        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36")
        .cookie("auth", "token")
        .timeout(3000)
        .get();

      System.out.println("Received document length: " + doc.html().length()); // Debug document length
      return doc;
  }

  private static void parseResult(Document doc, String brand, String item) {
    // Select all items in the search result
    Elements items = doc.select(".s-result-item");
    //System.out.println(items);
    System.out.println("Found " + items.size() + " items"); // Debug number of items found


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

      //System.out.println("Title: " + title + " | Price: " + whole + "." + fraction);

      // Only print out items that have a title and a price
      // Do another layer of filtering here
      // problem encountered: what if the user typed in brand with/without proper space?
      if (!title.isEmpty() && !whole.isEmpty()) {
        if (title.toLowerCase().contains(item) && title.toLowerCase().contains(brand)) {
          System.out.println("Title: " + title + " | Price: " + whole + "." + fraction);
        }
      }
    }
  }
}
