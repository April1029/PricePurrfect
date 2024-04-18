import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PetSmartScraper implements Scraper {
  public static void main(String[] args) throws IOException {
    PetSmartScraper scraper = new PetSmartScraper();
    String brand = "arm&hammer";
    String item = "deodorizer";
    List<Product> products = scraper.scrapeProducts(brand + " " + item);
    for (Product product : products) {
      System.out.println(product);
    }
  }

  @Override
  public List<Product> scrapeProducts(String query) throws IOException {
    List <Product> products = new ArrayList<>();
    Document doc = Jsoup.connect("https://www.petsmart.com/search/")
        .data("q", query)
        .cookie("auth","token")
        .timeout(3000)
        .get();



    // Select all elements that have the class 'product-name'
    Elements itemTitles = doc.select(".product-name");

    // Select all elements that have the class 'product-price'
    Elements itemPrices = doc.select(".product-price");

    for (int i = 0; i < itemTitles.size(); i++) {
      Element title = itemTitles.get(i);
      Element price = itemPrices.get(i);

      // Add one more layer of filter to make sure that it is the result
      if (title.text().toLowerCase().contains(query.toLowerCase())){
        products.add(new Product((title.text()), price != null ? price.text() : "Price not available"));
      }
    }
    return products;
  }

}
