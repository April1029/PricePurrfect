package com.pricepurrfect.view;

import com.pricepurrfect.model.Product;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

/**
 * A test class for CSVWriter. It verifies the CSV output functionality under various conditions,
 * including normal operation with typical input and operation with empty input lists.
 */
public class CSVWriterTest {

  /**
   * Tests the writeToCSV method with typical product data. This test verifies that the CSV output
   * is formatted correctly and contains expected product data.
   *
   * @throws IOException if an I/O error occurs, which can happen during the write operations.
   */
  @Test
  public void testWriteToCSV() throws IOException {
    // Create a StringWriter to capture the output
    StringWriter stringWriter = new StringWriter();

    // Prepare mock sample data
    List<Product> smartResults = Arrays.asList(new Product("Cat Toy Springs", "$10.99"));
    List<Product> petSuppliesPlusResults = Arrays.asList(new Product("Cat Litter", "$14.99"));
    List<Product> amazonResults = Arrays.asList(new Product("Orijen cat can", "$5.49"));
    List<Product> petcoResults = Arrays.asList(new Product("Orijen cat dry food", "$48.00"));

    // Use StringWriter with CSVWriter
    CSVWriter.writeToCSV(stringWriter, smartResults, petSuppliesPlusResults, amazonResults,
        petcoResults);

    // Convert StringWriter buffer to string
    String output = stringWriter.toString();

    // Assert the correct CSV format and content
    assertEquals("Store,Product Title, Product Price\n"
        + "PetSmart,Cat Toy Springs,$10.99\n"
        + "PetSuppliesPlus,Cat Litter,$14.99\n"
        + "Amazon,Orijen cat can,$5.49\n"
        + "Petco,Orijen cat dry food,$48.00\n", output);

    assertTrue(output.startsWith("Store,Product Title, Product Price\n"));
    assertTrue(output.contains("PetSmart,Cat Toy Springs,$10.99"));
    assertTrue(output.contains("Petco,Orijen cat dry food,$48.00"));
    assertEquals(5, output.split("\n").length); // Including the header, there should be 5 lines

    // Close the StringWriter
    stringWriter.close();
  }

  /**
   * Tests the writeToCSV method with empty lists to ensure it handles empty inputs gracefully. This
   * test checks that the output only contains the CSV header when there are no products to list.
   *
   * @throws IOException if an I/O error occurs, which can happen during the write operations.
   */
  @Test
  public void testEmptyLists() throws IOException {
    StringWriter stringWriter = new StringWriter();
    List<Product> emptyList = new ArrayList<>();

    CSVWriter.writeToCSV(stringWriter, emptyList, emptyList, emptyList, emptyList);
    // Convert StringWriter buffer to string
    String output = stringWriter.toString();

    // Assert that the output correctly only contains the header line
    assertEquals("Store,Product Title, Product Price\n", output);
    assertTrue(output.startsWith("Store,Product Title, Product Price"));
    assertEquals(1, output.split("\n").length); // Only the header line should be present
  }
}
