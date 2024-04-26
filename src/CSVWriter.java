import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Handles writing product data to a CSV file.
 */
public class CSVWriter {

  /**
   * Writes product data from multiple retailer stores into a CSV format using the provided Writer.
   *
   * @param csvWriter The writer to use for outputting the CSV data.
   * @param smartResults List of products from PetSmart.
   * @param petSuppliesPlusResults List of products from PetSuppliesPlus.
   * @param amazonResults List of products from Amazon.
   * @param petcoResults List of products from Petco.
   * @throws IOException if an I/O error occurs writing to the writer.
   */
  public static void writeToCSV(Writer csvWriter, List<Product> smartResults, List<Product> petSuppliesPlusResults, List<Product> amazonResults, List<Product> petcoResults) throws IOException {
    // Start the CSV with header columns
    csvWriter.append("Store,Product Title, Product Price\n");
    // Append results from each store to the CSV
    appendResultsToCSV("PetSmart", smartResults, csvWriter);
    appendResultsToCSV("PetSuppliesPlus", petSuppliesPlusResults, csvWriter);
    appendResultsToCSV("Amazon", amazonResults, csvWriter);
    appendResultsToCSV("Petco", petcoResults, csvWriter);

    csvWriter.flush();  // Ensure all data is written to the file
  }

  /**
   * Appends a list of products for a specific store to the CSV.
   *
   * @param storeName Name of the store the products are from.
   * @param results List of products from the store.
   * @param csvWriter The writer to use for outputting the CSV data.
   * @throws IOException if an I/O error occurs writing to the writer.
   */
  private static void appendResultsToCSV(String storeName, List<Product> results, Writer csvWriter) throws IOException {
    // Sort products by price in ascending order before writing to CSV
    Collections.sort(results, new Comparator<Product>() {
      @Override
      public int compare(Product p1, Product p2) {
        double price1 = parsePrice(p1.getPrice());
        double price2 = parsePrice(p2.getPrice());
        return Double.compare(price1, price2);
      }
    });

    // Write each product's data to the CSV
    for (Product product : results) {
      csvWriter.append(storeName).append(",")
          .append(product.getTitle().replace(",", " ")).append(",")
          .append(product.getPrice()).append("\n");
    }
  }

  /**
   * Parses a price string to a double, removing any non-numeric characters.
   *
   * @param priceStr The price string to parse.
   * @return The parsed price as a double, or Double.MAX_VALUE if parsing fails.
   */

  private static double parsePrice(String priceStr) {
    try {
      // Remove any non-numeric characters (e.g., $, commas) before parsing
      priceStr = priceStr.replaceAll("[^\\d.]", "");
      return Double.parseDouble(priceStr);
    } catch (NumberFormatException e) {
      // Return a default high value if parsing fails, to sort these items last
      return Double.MAX_VALUE;
    }
  }
}
