import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CSVWriter {
  public static void writeToCSV(List<Product> smartResults, List<Product> petSuppliesPlusResults, List<Product> amazonResults, List<Product> petcoResults) throws IOException {
    String userHome = System.getProperty("user.home"); // Get the user's home directory
    String filePath = userHome + "/Downloads/Pet_Products.csv"; // Specify the file path

    try {
      FileWriter csvWriter = new FileWriter(filePath);
      csvWriter.append("Store,Product Title, Product Price\n");

      appendResultsToCSV("PetSmart", smartResults, csvWriter);
      appendResultsToCSV("PetSuppliesPlus", petSuppliesPlusResults, csvWriter);
      appendResultsToCSV("Amazon", amazonResults, csvWriter);
      appendResultsToCSV("Petco", petcoResults, csvWriter);

      csvWriter.flush();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void appendResultsToCSV(String storeName, List<Product> results, FileWriter csvWriter) throws IOException {
    for (Product product : results) {
      csvWriter.append(storeName).append(",")
          .append(product.getTitle().replace(",", " ")).append(",")
          .append(product.getPrice()).append("\n");

    }
  }

}
