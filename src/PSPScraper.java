import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PSPScraper {
  public static void main(String[] args) throws IOException {
    String brand = "arm&hammer";
    String item = "deodorizer";
    Document doc = Jsoup.connect("https://www.petsuppliesplus.com/search")
        .data("query",brand +" " + item)
        .cookie("auth","token")
        .timeout(5000)
        .get();
    System.out.println(doc);

    // Select all elements that have the class 'product-name'
    Elements itemTitles = doc.select(".CoveoResultLink.coveo-link-click.coveo-result-title");

    // Select all elements that have the class 'product-price'
    Elements itemPrices = doc.select(".price");

    for (int i = 0; i < itemTitles.size(); i++) {
      Element title = itemTitles.get(i);
      Element price = itemPrices.get(i);

      System.out.println("Title: " + title.text() + " | Price: " + price.text());
    }
  }
}
