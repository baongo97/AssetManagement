package asset_reader;

import java.io.File;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssi.fctrading.FCTradingClient;
import com.ssi.fctrading.DataContract.AccountBalanceRequest;
import com.ssi.fctrading.DataContract.AccountPositionRequest;
import com.ssi.fctrading.DataContract.CashBalanceResponse;
import com.ssi.fctrading.DataContract.Response;
import com.ssi.fctrading.DataContract.StockPositionResponse;

import crawler.CrawlingApp;
import dao.AccountDao;
import dao.BalanceDao;
import dao.PortfolioDao;
import dao.SecurityDao;
import entity.Currency;
import entity.SecurityCategory;
import lombok.var;
import util.JsonConverter;

public class SSIReader implements Runnable{
	private final int accountId;
	private final int portfolioId;
	private final String broker = "SSI";
	private final String SSI_ACCOUNT = "Q743011";
	
	private AccountDao accountDao = new AccountDao();
	private BalanceDao balanceDao = new BalanceDao();
	private SecurityDao securityDao = new SecurityDao();
	private PortfolioDao portfolioDao = new PortfolioDao();
	private CrawlingApp crawler = new CrawlingApp();

	public SSIReader(String email) throws SQLException {
		accountId = accountDao.getAccountIdByEmail(email);
		portfolioId = portfolioDao.getPortfolioIdByAccountAndBroker(accountId, broker);
	}
	private FCTradingClient getApiClient() {
		FCTradingClient client = null;
		try {
            ObjectMapper objectMapper = new ObjectMapper();
            String filePath = Paths.get("").toAbsolutePath().toString() + "/fctrading.json";
            JsonNode jsonNode = objectMapper.readTree(new File(filePath));
            
            String consumerId = jsonNode.get("consumerId").asText();
            String consumerSecret = jsonNode.get("consumerSecret").asText();
            String privateKey = jsonNode.get("privateKey").asText();
            String url = jsonNode.get("url").asText();

            client = new FCTradingClient(consumerId, consumerSecret, privateKey, url);
            client.init();
		}
        catch (Exception e) {
			e.printStackTrace();
		}
        return client;
	}
	private double readCashBalance(){
		double cashBal = 0;
		
		FCTradingClient client = getApiClient();
        AccountBalanceRequest req = new AccountBalanceRequest();
        req.account = SSI_ACCOUNT;
        Response<CashBalanceResponse> res;
		try {
			res = client.GetCashBalance(req);
	        JsonNode jsonRes = JsonConverter.convertObjectToJsonNode(res);
	        cashBal = jsonRes.get("data").get("cashBal").asDouble();
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cashBal;
	}
	private Map<String, Double> readStockBalances(){
		Map<String, Double> SSIbalances = new HashMap<>();
		
		FCTradingClient client = getApiClient();
		
		AccountPositionRequest req = new AccountPositionRequest();
        req.account = SSI_ACCOUNT;
        Response<StockPositionResponse> res;
		try {
			res = client.GetStockPosition(req);
	        JsonNode jsonRes = JsonConverter.convertObjectToJsonNode(res);
	        JsonNode data = jsonRes.get("data");
	        JsonNode balances = data.get("stockPositions");
	        for (JsonNode balance : balances) {
	        	String symbol = balance.get("instrumentID").asText();
	        	
	        	double onHand = balance.get("onHand").asDouble();
	        	double buyT0 = balance.get("buyT0").asDouble();
	        	double buyT1 = balance.get("buyT1").asDouble();
	        	double holdForTrade = balance.get("holdForTrade").asDouble();
	        	double quantity = onHand + buyT0 + buyT1  + holdForTrade;
	        	
	        	SSIbalances.put(symbol, quantity);	
	        }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return SSIbalances;
	}
	@Override
	public void run() { // run on schedule
		Timestamp time = new Timestamp(System.currentTimeMillis());
		Map<String, Double> balances = readStockBalances();
		double UsdRate = crawler.getExchangeRate(Currency.VND, Currency.USD);

		for (var entry : balances.entrySet()) {
			String symbol = entry.getKey();
			int securityId;
			try {
				securityId = securityDao.getSecurityIdBySymbolCategoryCurrency(symbol, SecurityCategory.STOCK, Currency.VND);
			} catch (SQLException e) {
				// No available symbol in database
				securityDao.insertSecurity(symbol, SecurityCategory.STOCK, Currency.VND);
				securityId = securityDao.getLastId();
			}

			double quantity = entry.getValue();
			double price = crawler.getSecurityLastPrice(symbol, SecurityCategory.STOCK, Currency.VND);
			double value = quantity*price*UsdRate;

			balanceDao.insertBalance(portfolioId, securityId, quantity, value, time);
		}
		
		double cashBal = readCashBalance();
		int cashId = 0;
		try {
			cashId = securityDao.getSecurityIdBySymbolCategoryCurrency("VND", SecurityCategory.FIAT, Currency.VND);
		} catch (SQLException e) {
		}
		double value = cashBal*UsdRate;
		balanceDao.insertBalance(portfolioId, cashId, cashBal, value, time);
	}
	
	public static void main(String[] args) {
		SSIReader reader = null;
		try {
			reader = new SSIReader("ngbao128@gmail.com");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		reader.run();
	}
}
