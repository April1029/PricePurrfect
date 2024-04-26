import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import org.jsoup.nodes.Document;

public class ScraperFactory {

  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Enter the brand (e.g., 'Instinct'):");
    String brand = scanner.nextLine();

    System.out.println("Enter the item (e.g., 'Dry cat food'):");
    String item = scanner.nextLine();

    runScrapers(brand, item, scanner);
  }

  public static void runScrapers(String brand, String item, Scanner scanner) {
    PetSmartScraper petSmartScraper = new PetSmartScraper();
    PetSuppliesPlusScraper petSuppliesPlusScraper = new PetSuppliesPlusScraper(brand, item);
    AmazonScraper amazonScraper = new AmazonScraper();
    PetcoScraper petcoScraper = new PetcoScraper(brand, item);

    try {
      List<Product> petSmartResults = scrapeAndPrintResults(petSmartScraper, brand, item);
      List<Product> petSuppliesPlusResults = scrapeAndPrintResults(petSuppliesPlusScraper, brand, item);
      List<Product> amazonResults = scrapeAndPrintResults(amazonScraper, brand, item);
      List<Product> petcoResults = scrapeAndPrintResults(petcoScraper, brand, item);

      // Define the path where the CSV will be saved
      String userHome = System.getProperty("user.home");
      String filePath = userHome + "/Downloads/Pet_Products.csv";
      try (FileWriter csvWriter = new FileWriter(filePath)) {
        CSVWriter.writeToCSV(csvWriter, petSmartResults, petSuppliesPlusResults, amazonResults, petcoResults);
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("Failed to retrieve data: " + e.getMessage());
    } finally {
      scanner.close();
    }
  }

  protected static List<Product> scrapeAndPrintResults(Scraper scraper, String brand, String item) throws IOException, InterruptedException {
    String url = scraper.assembleURL(brand, item);
    System.out.println("Fetching URL: " + url);

    Document doc = scraper.performSearch(url);
    System.out.println("Document fetched, parsing results...");

    scraper.parseResults(doc, brand, item);
    List<Product> results = scraper.getResults();

    System.out.println("Results:");
    results.forEach(System.out::println);
    return results;
  }
}
