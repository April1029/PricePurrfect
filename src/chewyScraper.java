import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class chewyScraper {

  public static void main(String[] args) {
    String brand = "arm&hammer";
    String item = "deodorizer";

    try {
      // Adding delay to respect the server's rate limiting
      Thread.sleep(2000);

      // Fetch the document from the URL
      Document doc = Jsoup.connect("https://www.chewy.com/s")
          .data("query", brand + " " + item)
          .userAgent("Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Mobile Safari/537.36,gzip(gfe)")
          .header("Accept-Language", "en-US,en;q=0.5")
          .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
          .timeout(3000)
          .get();

      // Select all elements that have the specified classes
      Elements itemTitles = doc.select(".product-name");
      Elements itemPrices = doc.select(".product-price");

      // Loop through the elements and print the text content
      for (int i = 0; i < itemTitles.size(); i++) {
        Element title = itemTitles.get(i);
        Element price = itemPrices.get(i);

        // Check if the title contains the specified item keyword
        if (title.text().toLowerCase().contains(item.toLowerCase())) {
          System.out.println("Title: " + title.text() + " | Price: " + price.text());
        }
      }
    } catch (InterruptedException e) {
      // Handle the interrupted exception from Thread.sleep
      e.printStackTrace();
    } catch (IOException e) {
      // Handle potential IO exceptions from Jsoup.connect
      e.printStackTrace();
    }
  }
}
