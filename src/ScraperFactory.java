public class ScraperFactory {
  public static Scraper getScraper(String source) {
    switch (source) {
      case "Amazon":
        return new AmazonScraper();
      case "PetSmart":
        return new PetSmartScraper();
      // Add more cases for different sources as needed
      default:
        throw new IllegalArgumentException("Unknown source: " + source);
    }
  }
}
