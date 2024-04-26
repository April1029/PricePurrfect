package com.pricepurrfect.model;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Product class.
 * This class tests various aspects of the Product object, including title retrieval, price retrieval,
 * and the string representation of a product.
 */
public class ProductTest {

  /**
   * Tests the getTitle method of the Product class.
   * Ensures that the title returned is accurate and not null.
   */
  @Test
  public void testGetTitle() {
    // Arrange: Setup the Product object with a specific title and price
    String title = "Test Title";
    String price = "10.00";
    Product product = new Product(title, price);

    // Act: Retrieve the title from the Product
    String actualTitle = product.getTitle();

    // Assert: Check that the title matches the expected value and is not null
    assertEquals(title, actualTitle);
    assertNotNull(actualTitle);
  }

  /**
   * Tests the getPrice method of the Product class.
   * Ensures that the price returned is accurate and not null.
   */
  @Test
  public void testGetPrice() {
    // Arrange
    String title = "Test Title";
    String price = "10.00";
    Product product = new Product(title, price);

    // Act
    String actualPrice = product.getPrice();

    // Assert
    assertEquals(price, actualPrice);
    assertNotNull(actualPrice);
  }

  /**
   * Tests the toString method of the Product class.
   * Ensures that the string representation is formatted correctly and not null.
   */
  @Test
  public void testToString() {
    // Arrange
    String title = "Test Title";
    String price = "10.00";
    Product product = new Product(title, price);

    // Act
    String expectedString = "Title: " + title + " | Price: " + price;
    String actualString = product.toString();

    // Assert
    assertEquals(expectedString, actualString);
    assertNotNull(actualString);
  }
}
