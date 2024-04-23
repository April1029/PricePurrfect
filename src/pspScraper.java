import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class pspScraper implements Scraper {
  private List<Product> results = new ArrayList<>();

  @Override
  public String assembleURL(String brand, String item) {
    String baseurl = "https://www.petsuppliesplus.com/search?query=";
    String query = brand + " " + item;
    String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8).replace("+", "%20");
    return baseurl + encodedQuery + "#q=" + encodedQuery;
  }

  @Override
  public Document performSearch(String url) throws IOException {
    Document doc = Jsoup.connect(url)
        .cookie("auth", "token")
        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36")
        .timeout(30000)
        .get();

    System.out.println("Received document length from PetSuppliesPlus: " + doc.html().length());
    return doc;
  }

  @Override
  public void parseResults(Document doc, String brand, String item) {
    Elements itemTitles = doc.select(".CoveoResultLink.coveo-link-click.coveo-result-title");
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
  }

  @Override
  public List<Product> getResults() {
    return results;
  }

  public static void main(String[] args) {
    pspScraper scraper = new pspScraper();
    String brand = "Purina";  // Example brand
    String item = "Cat Food"; // Example item
    try {
      String url = scraper.assembleURL(brand, item);
      Document doc = scraper.performSearch(url);
      // You can add a call to parseResults if you want to parse and print the parsed products as well
      // scraper.parseResults(doc, brand, item);
      // List<Product> products = scraper.getResults();
      // products.forEach(System.out::println);
      System.out.println(doc);  // Print the entire HTML content of the document
    } catch (IOException e) {
      System.err.println("An error occurred while attempting to connect to the site: " + e.getMessage());
    }
  }
}
