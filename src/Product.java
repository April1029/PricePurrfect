public class Product {
  private String title;
  private String price;


  public Product(String title, String price) {
    this.title = title;
    this.price = price;
  }

  public String getTitle() {
    return title;
  }

  public String getPrice() {
    return price;
  }

  @Override
  public String toString() {
    return "Title: " + title + " | Price: " + price;
  }
}
