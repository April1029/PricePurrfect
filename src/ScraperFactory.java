import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import org.jsoup.nodes.Document;
import java.util.Scanner;

public class ScraperFactory {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    // Prompt for brand
    System.out.println("Enter the brand (e.g., 'Instinct'):");
    String brand = scanner.nextLine();

    // Prompt for item
    System.out.println("Enter the item (e.g., 'Dry cat food'):");
    String item = scanner.nextLine();

    PetSmartScraper petSmartScraper = new PetSmartScraper();
    PetSuppliesPlusScraper petSuppliesPlusScraper = new PetSuppliesPlusScraper(brand, item);
    AmazonScraper amazonScraper = new AmazonScraper();
    PetcoScraper petcoScraper = new PetcoScraper(brand, item);

    try {
      // Run PetSmart scraper
      String petSmarturl = petSmartScraper.assembleURL(brand, item);
      System.out.println("Fetching URL: " + petSmarturl);

      Document petSmartDoc  = petSmartScraper.performSearch(petSmarturl);
      System.out.println("Document from PetSmart fetched, parsing results...");
      petSmartScraper.parseResults(petSmartDoc, brand, item);
      List<Product> petSmartResults = petSmartScraper.getResults();

      // Run PetSuppliesPlus scraper
      String petSuppliesPlusturl = petSuppliesPlusScraper.assembleURL(brand, item);
      System.out.println("Fetching URL: " + petSuppliesPlusturl);

      Document petSuppliesPlusDoc  = petSuppliesPlusScraper.performSearch(petSuppliesPlusturl);
      System.out.println("Document from PetSuppliesPlus fetched, parsing results...");

      petSuppliesPlusScraper.parseResults(petSuppliesPlusDoc, brand, item);
      List<Product> petSuppliesPlusResults = petSuppliesPlusScraper.getResults();

      // Run Amazon scraper
      String amazonturl = amazonScraper.assembleURL(brand, item);
      System.out.println("Fetching URL: " + amazonturl);

      Document amazonDoc  = amazonScraper.performSearch(amazonturl);
      System.out.println("Document from Amazon fetched, parsing results...");

      amazonScraper.parseResults(amazonDoc, brand, item);
      List<Product> amazonResults = amazonScraper.getResults();

      // Run Petco scraper
      String petcoturl = petcoScraper.assembleURL(brand, item);
      System.out.println("Fetching URL: " + petcoturl);

      Document petcoDoc  = petcoScraper.performSearch(petcoturl);
      System.out.println("Document from Petco fetched, parsing results...");

      petcoScraper.parseResults(petcoDoc, brand, item);
      List<Product> petcoResults = petcoScraper.getResults();

      // Write results to CSV
      csvvWriter.writeToCSV(petSmartResults, petSuppliesPlusResults, amazonResults, petcoResults);

      // Print all results
      System.out.println("PetSmart Results:");
      petSmartResults.forEach(System.out::println);

      System.out.println("PetSuppliesPlus Results:");
      petSuppliesPlusResults.forEach(System.out::println);

      System.out.println("Amazon Results:");
      amazonResults.forEach(System.out::println);

      System.out.println("Petco Results:");
      petcoResults.forEach(System.out::println);

    } catch (IOException | InterruptedException e) {
      System.err.println("Failed to retrieve data: " + e.getMessage());
    } finally {
      scanner.close();
    }
  }
}
