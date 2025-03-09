package crawler.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssi.fcdata.FCDataClient;
import com.ssi.fcdata.DataContract.Response;
import com.ssi.fcdata.DataContract.SecuritiesDetailRequest;
import com.ssi.fcdata.DataContract.SecuritiesResponse;

import util.JsonConverter;

public class VnStockCrawler implements SecurityCrawler {
    public String getSecurityName(String symbol) {
    	String name = null;
		FCDataClient client = null;
		try {
            ObjectMapper objectMapper = new ObjectMapper();
            String filePath = Paths.get("").toAbsolutePath().toString() + "/fcdata.json";
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            
            String consumerId = jsonNode.get("consumerId").asText();
            String consumerSecret = jsonNode.get("consumerSecret").asText();
            String url = jsonNode.get("url").asText();
            
            client = new FCDataClient(consumerId, consumerSecret, url);
            client.init();
		}
        catch (Exception e) {
			e.printStackTrace();
		}
		
		SecuritiesDetailRequest req = new SecuritiesDetailRequest();
		req.symbol = symbol;
		
		Response<List<SecuritiesResponse>> res;
		try {
			res = client.GetSecuritiesDetail(req);
	        JsonNode jsonRes = JsonConverter.convertObjectToJsonNode(res);
	        name = jsonRes.get("data").get(0).get("RepeatedInfo").get(0).get("SymbolEngName").asText();
		} catch (Exception e) {
			e.printStackTrace();
		}
        return name;
    }
    
    public double getLastPrice(String symbol) {
    	String url = "https://cafef.vn/du-lieu/hose/"+symbol+".chn";
    	double price = 0;
    	Document doc;
		try {
			doc = Jsoup.connect(url).get();
	    	Element element = doc.getElementById("price__0");
	    	price = Double.parseDouble(element.text().strip());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return price*1000;
    }
    
	public static void main(String[] args) throws Exception {
		VnStockCrawler crawler = new VnStockCrawler();
		System.out.println(crawler.getLastPrice("ACB"));
	}
}
