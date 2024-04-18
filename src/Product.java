public class Product {
  private String title;
  private String price;

  public Product(String title, String price) {
    this.title = title;
    this.price = price;
  }

  // Getters and setters for title and price
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  @Override
  public String toString() {
    return "Title: " + title + " | Price: " + price;
  }
}
