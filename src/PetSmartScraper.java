import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PetSmartScraper implements Scraper {

  public static void main(String[] args) {
    PetSmartScraper scraper = new PetSmartScraper();
    String brand = "arm & hammer";
    String item = "deodorizer";
    try {
      String url = scraper.assembleURL(brand, item);
      System.out.println("Fetching URL: " + url); // Debug URL fetching
      Document doc = scraper.performSearch(url);
      System.out.println("Document fetched, parsing results..."); // Debug document fetch
      scraper.parseResults(doc, brand, item);
    } catch (IOException e) {
      System.err.println("Failed to retrieve data: " + e.getMessage());
    }
  }

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

    System.out.println("Received document length: " + doc.html().length());
    return doc;
  }

  @Override
  public void parseResults(Document doc, String brand, String item) {
    Elements itemTitles = doc.select(".product-name");
    Elements itemPrices = doc.select(".product-price");
    System.out.println("Found " + itemTitles.size() + " items");

    for (int i = 0; i < itemTitles.size(); i++) {
      Element title = itemTitles.get(i);
      Element price = itemPrices.get(i);
      if (title.text().toLowerCase().contains(item.toLowerCase()) && title.text().toLowerCase().contains(brand.toLowerCase())) {
        System.out.println("Title: " + title.text() + " | Price: " + price.text());
      }
    }
  }
}
