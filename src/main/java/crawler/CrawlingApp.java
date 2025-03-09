package crawler;

import crawler.util.CoinCrawler;
import crawler.util.ExchangeRateCrawler;
import crawler.util.SecurityCrawler;
import crawler.util.VnStockCrawler;
import entity.SecurityCategory;
import entity.Currency;

public class CrawlingApp {
	public String getSecurityName(String symbol, SecurityCategory category, Currency currency)
			throws IllegalArgumentException {
		String securityName = null;

		if (category == SecurityCategory.STOCK && currency == Currency.VND) {
			SecurityCrawler crawler = new VnStockCrawler();
			securityName = crawler.getSecurityName(symbol);
		} else if (category == SecurityCategory.CRYPTO) {
			SecurityCrawler crawler = new CoinCrawler();
			securityName = crawler.getSecurityName(symbol);
		} else {
			throw new IllegalArgumentException("Category and Currency are not matched");
		}
		return securityName;
	}
	
	public double getSecurityLastPrice(String symbol, SecurityCategory category, Currency currency)
			throws IllegalArgumentException {
		double securityPrice = -1;

		if (category == SecurityCategory.STOCK && currency == Currency.VND) {
			SecurityCrawler crawler = new VnStockCrawler();
			securityPrice = crawler.getLastPrice(symbol);
		} else if (category == SecurityCategory.CRYPTO) {
			SecurityCrawler crawler = new CoinCrawler();
			securityPrice = crawler.getLastPrice(symbol);
		} else {
			throw new IllegalArgumentException("Category and Currency are not matched");
		}
		return securityPrice;
	}
	public double getExchangeRate(Currency fromCurrency, Currency toCurrency) {
		ExchangeRateCrawler crawler = new ExchangeRateCrawler();
		return crawler.getRate(fromCurrency, toCurrency);
	}

	public static void main(String[] args) {
		CrawlingApp crawlerApp = new CrawlingApp();
		try {
			double rate = crawlerApp.getExchangeRate(Currency.VND, Currency.USD);
			System.out.println(rate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
