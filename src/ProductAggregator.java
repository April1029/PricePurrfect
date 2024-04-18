import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProductAggregator {
  private List<BaseScraper> scrapers = new ArrayList<>();

  public ProductAggregator() {
    scrapers.add(new PetcoScraper());
    // Add other scrapers
  }

  public List<Product> searchForProducts(String query) {
    List<Product> allProducts = new ArrayList<>();
    for (BaseScraper scraper : scrapers) {
      try {
        allProducts.addAll(scraper.scrapeProducts(query));
      } catch (IOException | InterruptedException e) {
        e.printStackTrace();
      }
    }
    return allProducts;
  }

  // Method for price comparison and option recommendation
}
