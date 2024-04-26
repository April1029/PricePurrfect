import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

class TestScraper implements Scraper {
  private List<Product> products;

  public TestScraper(List<Product> products) {
    this.products = products;
  }

  @Override
  public String assembleURL(String brand, String item) {
    return "http://example.com";
  }

  @Override
  public Document performSearch(String url) {
    return Jsoup.parse("<html></html>"); // Minimal HTML for parsing
  }

  @Override
  public void parseResults(Document doc, String brand, String item) {
    // Simulated parsing logic, if needed
  }

  @Override
  public List<Product> getResults() {
    return products;
  }
}
