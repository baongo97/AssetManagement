package crawler.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.text.NumberFormat;
import java.util.Locale;

import entity.Currency;

public class ExchangeRateCrawler {
	public double getRate(Currency fromCurrency, Currency toCurrency) {
		String from = fromCurrency.name();
		String to = toCurrency.name();
    	String url = "https://wise.com/us/currency-converter/"+ from +"-to-" + to + "-rate";
    	double rate = 0;
    	Document doc;
		try {
			doc = Jsoup.connect(url).get();
	    	Elements elements = doc.getElementsByClass("text-success");
	    	
	    	for (Element e : elements) {
	    		String rateStr = e.text();
	    		try {
	    			NumberFormat format = NumberFormat.getNumberInstance(Locale.US);
	    			Number number = format.parse(rateStr);
	    			
	    			rate = number.doubleValue();
					break;
				} catch (Exception e2) {
				}
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return rate;
	}
	
	public static void main(String[] args) {
		ExchangeRateCrawler crawler = new ExchangeRateCrawler();
		double rate = crawler.getRate(Currency.USD, Currency.AUD);
		System.out.println(rate);
	}
}
