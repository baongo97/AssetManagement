package crawler.util;

public interface SecurityCrawler {
	public String getSecurityName(String symbol);
	public double getLastPrice(String symbol);
}
