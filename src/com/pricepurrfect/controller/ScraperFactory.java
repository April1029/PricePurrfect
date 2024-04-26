package com.pricepurrfect.controller;

import com.pricepurrfect.model.Product;
import com.pricepurrfect.view.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Scanner;
import org.jsoup.nodes.Document;

/**
 * A factory class to manage multiple scrapers and coordinate the scraping process. This class is
 * designed to initialize scrapers for different stores, manage data retrieval, and output results.
 */
public class ScraperFactory {

  /**
   * The main method that initiates the scraping process by collecting user input for brand and
   * item, and calls the method to run scrapers.
   *
   * @param args The command line arguments (not used in this application).
   */
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);

    System.out.println("Enter the brand (e.g., 'Instinct'):");
    String brand = scanner.nextLine();

    System.out.println("Enter the item (e.g., 'Dry cat food'):");
    String item = scanner.nextLine();

    runScrapers(brand, item, scanner);
  }

  /**
   * Initializes specific scraper implementations and orchestrates the scraping process. Collects
   * results from each scraper and writes them to a CSV file.
   *
   * @param brand   The brand to be searched across different stores.
   * @param item    The item to be searched.
   * @param scanner The scanner object for user input (used here for maintaining a single scanner
   *                instance).
   */
  public static void runScrapers(String brand, String item, Scanner scanner) {
    PetSmartScraper petSmartScraper = new PetSmartScraper();
    PetSuppliesPlusScraper petSuppliesPlusScraper = new PetSuppliesPlusScraper(brand, item);
    AmazonScraper amazonScraper = new AmazonScraper();
    PetcoScraper petcoScraper = new PetcoScraper(brand, item);

    try {
      // Execute scraping operations for each scraper and collect results
      List<Product> petSmartResults = scrapeAndPrintResults(petSmartScraper, brand, item);
      List<Product> petSuppliesPlusResults = scrapeAndPrintResults(petSuppliesPlusScraper, brand,
          item);
      List<Product> amazonResults = scrapeAndPrintResults(amazonScraper, brand, item);
      List<Product> petcoResults = scrapeAndPrintResults(petcoScraper, brand, item);

      // Define the path where the CSV will be saved
      String userHome = System.getProperty("user.home");
      String filePath = userHome + "/Downloads/Pet_Products.csv";
      try (Writer csvWriter = new FileWriter(filePath)) {
        CSVWriter.writeToCSV(csvWriter, petSmartResults, petSuppliesPlusResults, amazonResults,
            petcoResults);
      }
    } catch (IOException | InterruptedException e) {
      System.err.println("Failed to retrieve data: " + e.getMessage());
    } finally {
      scanner.close();
    }
  }

  /**
   * Helper method to perform the scraping operation using a given scraper. It fetches the URL,
   * retrieves the document, and parses the results.
   *
   * @param scraper The scraper instance to use for fetching and parsing data.
   * @param brand   The brand to search for.
   * @param item    The item description to search for.
   * @return A list of products retrieved and processed by the scraper.
   * @throws IOException          If an IO error occurs during scraping.
   * @throws InterruptedException If the thread is interrupted during execution.
   */
  protected static List<Product> scrapeAndPrintResults(Scraper scraper, String brand, String item)
      throws IOException, InterruptedException {
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
