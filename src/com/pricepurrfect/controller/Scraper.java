package com.pricepurrfect.controller;

import com.pricepurrfect.model.Product;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.jsoup.nodes.Document;

/**
 * The Scraper interface defines the structure for scraping web data related to specific items and
 * brands. It includes methods for assembling a search URL, performing the search to fetch web
 * content, and parsing the fetched content to extract relevant data.
 */
public interface Scraper {

  /**
   * Assembles a URL for searching an item by its brand and name.
   *
   * @param brand The brand of the item to search for.
   * @param item  The name of the item to search for.
   * @return A URL string assembled based on the provided brand and item parameters.
   * @throws UnsupportedEncodingException If encoding issues occur while encoding the URL
   *                                      parameters.
   */
  String assembleURL(String brand, String item) throws UnsupportedEncodingException;

  /**
   * Performs a search using the provided URL and fetches the resulting document.
   *
   * @param url The URL to fetch the document from.
   * @return The Document object containing the fetched web content.
   * @throws IOException          If an I/O error occurs during the fetching process.
   * @throws InterruptedException If the thread running the search is interrupted.
   */
  Document performSearch(String url) throws IOException, InterruptedException;

  /**
   * Parses the fetched document to extract data relevant to the specified brand and item.
   *
   * @param doc   The document to parse.
   * @param brand The brand associated with the search.
   * @param item  The item name associated with the search.
   */
  void parseResults(Document doc, String brand, String item);

  /**
   * Retrieves a list of formatted result strings that contain title and price information.
   *
   * @return A list of strings each representing a found item with its price details.
   */
  List<Product> getResults();
}
