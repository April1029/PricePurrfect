import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class BaseScraper {
  protected String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.190 Safari/537.36";

  protected Document connect(String url) throws IOException, InterruptedException {
    Thread.sleep(2000); // Basic rate limiting
    return Jsoup.connect(url)
        .userAgent(userAgent)
        .timeout(5000)
        .get();
  }

  public abstract List<Product> scrapeProducts(String query) throws IOException, InterruptedException;
}
