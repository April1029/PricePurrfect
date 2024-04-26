import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

public class ProductTest {

  @Test
  public void testGetTitle() {
    // Arrange
    String title = "Test Title";
    String price = "10.00";
    Product product = new Product(title, price);

    // Act
    String actualTitle = product.getTitle();

    // Assert
    assertEquals(title, actualTitle);
    assertNotNull(actualTitle);
  }

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
