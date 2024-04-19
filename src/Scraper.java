import java.io.IOException;
import java.util.List;

public interface Scraper {
  List<Product> scrapeProducts(String query) throws IOException;
}
