import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PetSmartScraper implements Scraper {
  private List<Product> results = new ArrayList<>();

  @Override
  public String assembleURL(String brand, String item) {
    return "https://www.petsmart.com/search/?q=" + brand.replace(" ", "+") + "+" + item.replace(" ", "+");
  }

  @Override
  public Document performSearch(String url) throws IOException {
    Document doc = Jsoup.connect(url)
        .cookie("auth", "token")
        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
        .timeout(30000)
        .get();

    System.out.println("Received document length from PetSmart: " + doc.html().length());
    return doc;
  }

  @Override
  public void parseResults(Document doc, String brand, String item) {
    Elements itemTitles = doc.select(".product-name");
    Elements itemPrices = doc.select(".product-price");
    System.out.println("Found " + itemTitles.size() + " items from PetSmart");

    for (int i = 0; i < itemTitles.size(); i++) {
      Element title = itemTitles.get(i);
      Element price = itemPrices.get(i);
      if (title.text().toLowerCase().contains(item.toLowerCase()) && title.text().toLowerCase().contains(brand.toLowerCase())) {
        Product product = new Product(title.text(), price.text());
        results.add(product);
      }
    }
    System.out.println("Total products after filtering: " + results.size());
  }

  @Override
  public List<Product> getResults() {
    return results;
  }
}
