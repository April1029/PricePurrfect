import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class petssmartScraper implements Scraper {
  public static void main(String[] args) throws IOException {

    String brand = "arm&hammer";
    String item = "deodorizer";
    Document doc = Jsoup.connect("https://www.petsmart.com/search/")
        .data("q",brand +" " + item)
        .cookie("auth","token")
        .timeout(3000)
        .get();
    System.out.println(doc);


    // Select all elements that have the class 'product-name'
    Elements itemTitles = doc.select(".product-name");

    // Select all elements that have the class 'product-price'
    Elements itemPrices = doc.select(".product-price");

    for (int i = 0; i < itemTitles.size(); i++) {
      Element title = itemTitles.get(i);
      Element price = itemPrices.get(i);

      // Add one more layer of filter to make sure that it is the result
      if (title.text().toLowerCase().contains(item)){
        System.out.println("Title: " + title.text() + " | Price: " + price.text());
      }
    }
  }

  @Override
  public List<Product> scrapeProducts(String query) {
    return null;
  }
}
