import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AmazonScraper implements Scraper {
  public static void main(String[] args) {
    String brand = "arm & hammer";
    String item = "deodorizer";
    try {
      Document doc = Jsoup.connect("https://www.amazon.com/s")
          .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36")
          .data("k", brand + " " + item)
          .cookie("auth", "token")
          .timeout(3000)
          .get();

      // Select all items in the search result
      Elements items = doc.select(".s-result-item");

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

        // Only print out items that have a title and a price
        // Do another layer of filtering here
        // problem encountered: what if the user typed in brand with/without proper space?
        if (!title.isEmpty() && !whole.isEmpty()) {
          if (title.toLowerCase().contains(item) && title.toLowerCase().contains(brand)) {
            System.out.println("Title: " + title + " | Price: " + whole + "." + fraction);
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public List<Product> scrapeProducts(String query) {
    return null;
  }
}
