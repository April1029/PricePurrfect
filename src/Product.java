/**
 * Represents a product with a title and price.
 * This class provides a structured way to store information about a product,
 * which includes the product's title and its price.
 */
public class Product {
  private String title;
  private String price;

  /**
   * Constructs a new Product with the specified title and price.
   *
   * @param title The title of the product.
   * @param price The price of the product, typically as a string that includes currency.
   */
  public Product(String title, String price) {
    this.title = title;
    this.price = price;
  }

  /**
   * Retrieves the title of the product.
   *
   * @return The title of the product.
   */
  public String getTitle() {
    return title;
  }

  /**
   * Retrieves the price of the product.
   *
   * @return The price of the product.
   */
  public String getPrice() {
    return price;
  }

  /**
   * Returns a string representation of the product, combining title and price.
   *
   * @return A string in the format "Title: [title] | Price: [price]", which provides a concise
   *         description of the product suitable for printing or logging.
   */
  @Override
  public String toString() {
    return "Title: " + title + " | Price: " + price;
  }
}
