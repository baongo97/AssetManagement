package crawler.util;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.json.JSONObject;

import com.bybit.api.client.domain.CategoryType;
import com.bybit.api.client.domain.market.request.MarketDataRequest;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.var;
import util.JsonConverter;

@var
public class CoinCrawler implements SecurityCrawler{
	private String API_KEY = "abd6de6e-0279-4b42-8cf0-933001662f3f";
	
    public String getSecurityName(String symbol){
		String API_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/quotes/latest?symbol="+symbol;
    	String name = null;
    	
    	try (CloseableHttpClient httpClient = HttpClients.createDefault()){
    		
    		ClassicHttpRequest httpGet = ClassicRequestBuilder.get(API_URL)
    	            .build();
    		
    		httpGet.addHeader("Accept", "application/json");
    		httpGet.addHeader("X-CMC_PRO_API_KEY", API_KEY);
    		
    		String responseBody = httpClient.execute(httpGet, response -> {
    	        final HttpEntity entity1 = response.getEntity();
    	        return EntityUtils.toString(entity1); //response is closed
    	    });
    		
    		JSONObject jsonResponse = new JSONObject(responseBody);
    		JSONObject data = jsonResponse.getJSONObject("data")
   											.getJSONObject(symbol);
    		
    		name = data.get("name").toString();
    	} catch (Exception e) {
			e.printStackTrace();
		} 
    	return name;
    }
	public double getLastPrice(String symbol) {
		if (symbol.equals("USDT")) {
			return 1;
		}
		double price = 0;
		var client = BybitApiClientFactory.newInstance().newMarketDataRestClient();
		var tickerRequest  = MarketDataRequest.builder().category(CategoryType.SPOT).symbol(symbol+"USDT").build();
		var response = client.getMarketTickers(tickerRequest);
		
		try {
            JsonNode responseJson = JsonConverter.convertObjectToJsonNode(response);
            price = responseJson.get("result").get("list").get(0).get("lastPrice").asDouble();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return price;
	}
    
    public static void main(String[] args) {
        try {
        	CoinCrawler crawler = new CoinCrawler();
        	System.out.println(crawler.getLastPrice("ETH"));
        } catch (Exception e) {
            System.err.println("Error fetching price: " + e.getMessage());
        }
    }
}

