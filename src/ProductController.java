import java.util.List;

public class ProductController {
  private ProductView view;

  public ProductController(ProductView view) {
    this.view = view;
  }

  public void scrapeAndDisplayProducts(String query, String source) {
    Scraper scraper = ScraperFactory.getScraper(source);
    List<Product> products = scraper.scrapeProducts(query);
    for (Product product : products) {
      view.printProductDetails(product);
    }
  }
}
