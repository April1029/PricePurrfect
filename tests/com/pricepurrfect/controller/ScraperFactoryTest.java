package com.pricepurrfect.controller;

import com.pricepurrfect.model.Product;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for the ScraperFactory class. This class verifies the functionality of the ScraperFactory
 * in orchestrating the scraping process and outputting results to a CSV file.
 */
public class ScraperFactoryTest {

  /**
   * Tests the runScrapers method of the ScraperFactory to ensure it handles the scraping process correctly
   * and outputs the results to a CSV file. This test verifies that the expected CSV file is created.
   *
   * @throws IOException if an error occurs during file operations.
   * @throws InterruptedException if the thread is interrupted during the execution.
   */
  @Test
  public void testRunScrapers() throws IOException, InterruptedException {

    // Create a Scanner object from a string input
    String input = "Instinct\nDry cat food\n";
    ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
    Scanner scanner = new Scanner(in);

    // Execution
    ScraperFactory.runScrapers("Instinct", "Dry cat food", scanner);

    // Verification
    String userHome = System.getProperty("user.home");
    String filePath = userHome + "/Downloads/Pet_Products.csv";
    boolean csvFileExists = Files.exists(Paths.get(filePath));
    Assert.assertTrue("CSV file should exist", csvFileExists);

    scanner.close();
  }
}


