import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CSVWriter {
  public static void writeToCSV(FileWriter csvWriter, List<Product> smartResults, List<Product> petSuppliesPlusResults, List<Product> amazonResults, List<Product> petcoResults) throws IOException {
    // Use the passed FileWriter directly to append data
    csvWriter.append("Store,Product Title, Product Price\n");

    appendResultsToCSV("PetSmart", smartResults, csvWriter);
    appendResultsToCSV("PetSuppliesPlus", petSuppliesPlusResults, csvWriter);
    appendResultsToCSV("Amazon", amazonResults, csvWriter);
    appendResultsToCSV("Petco", petcoResults, csvWriter);

    csvWriter.flush();  // Ensure all data is written to the file
  }

  private static void appendResultsToCSV(String storeName, List<Product> results, FileWriter csvWriter) throws IOException {
    Collections.sort(results, new Comparator<Product>() {
      @Override
      public int compare(Product p1, Product p2) {
        double price1 = parsePrice(p1.getPrice());
        double price2 = parsePrice(p2.getPrice());
        return Double.compare(price1, price2);
      }
    });

    for (Product product : results) {
      csvWriter.append(storeName).append(",")
          .append(product.getTitle().replace(",", " ")).append(",")
          .append(product.getPrice()).append("\n");
    }
  }

  private static double parsePrice(String priceStr) {
    try {
      // Remove any non-numeric characters (e.g., $, commas) before parsing
      priceStr = priceStr.replaceAll("[^\\d.]", "");
      return Double.parseDouble(priceStr);
    } catch (NumberFormatException e) {
      return Double.MAX_VALUE;
    }
  }
}
