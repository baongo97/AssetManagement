package asset_reader;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.bybit.api.client.config.BybitApiConfig;
import com.bybit.api.client.domain.account.AccountType;
import com.bybit.api.client.domain.asset.request.AssetDataRequest;
import com.bybit.api.client.restApi.BybitApiAssetRestClient;
import com.bybit.api.client.service.BybitApiClientFactory;
import com.fasterxml.jackson.databind.JsonNode;

import crawler.CrawlingApp;
import dao.AccountDao;
import dao.BalanceDao;
import dao.PortfolioDao;
import dao.SecurityDao;
import entity.ByBitApiKey;
import entity.SecurityCategory;
import entity.Currency;
import lombok.var;
import util.JsonConverter;

public class BybitReader implements Runnable {
	private final int accountId;
	private final int portfolioId;
	private final String broker = "Bybit";
	
	private AccountDao accountDao = new AccountDao();
	private BalanceDao balanceDao = new BalanceDao();
	private SecurityDao securityDao = new SecurityDao();
	private PortfolioDao portfolioDao = new PortfolioDao();
	private CrawlingApp crawler = new CrawlingApp();

	public BybitReader(String email) throws SQLException {
		accountId = accountDao.getAccountIdByEmail(email);
		portfolioId = portfolioDao.getPortfolioIdByAccountAndBroker(accountId, broker);
	}

	private Map<String, Double> readBalances() {
		BybitApiClientFactory factory = BybitApiClientFactory.newInstance(ByBitApiKey.API_KEY, ByBitApiKey.API_SECRETE,
				BybitApiConfig.TESTNET_DOMAIN);
		BybitApiAssetRestClient client = factory.newAssetRestClient();

		AssetDataRequest request = AssetDataRequest.builder().accountType(AccountType.FUND).build();
		var response =  client.getAssetAllCoinsBalance(request);
		
		Map<String, Double> bybitBalances = new HashMap<String, Double>();
        try {
            JsonNode responseJson = JsonConverter.convertObjectToJsonNode(response);
            JsonNode balances = responseJson.get("result").get("balance");
            
    		for (JsonNode balance : balances) {
    			String symbol = balance.get("coin").asText();
    			double quantity = balance.get("walletBalance").asDouble();

    			bybitBalances.put(symbol, quantity);
    		}
        } catch (Exception e) {
            e.printStackTrace();
        }
        
		return bybitBalances;
	}
	
	public void run() { // run on schedule
		Timestamp time = new Timestamp(System.currentTimeMillis());
		Map<String, Double> balances = readBalances();
		
		for (var entry : balances.entrySet()) {
			String symbol = entry.getKey();
			int securityId;
			try {
				securityId = securityDao.getSecurityIdBySymbolCategoryCurrency(symbol, SecurityCategory.CRYPTO, Currency.USD);
			} catch (SQLException e) {
				// No available symbol in database
				securityDao.insertSecurity(symbol, SecurityCategory.CRYPTO, Currency.USD);
				securityId = securityDao.getLastId();
			}

			double quantity = entry.getValue();
			double price = crawler.getSecurityLastPrice(symbol, SecurityCategory.CRYPTO, Currency.USD);
			double value = quantity*price;

			balanceDao.insertBalance(portfolioId, securityId, quantity, value, time);
		}
	}

	public static void main(String[] args) {
		BybitReader reader = null;
		try {
			reader = new BybitReader("ngbao128@gmail.com");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		reader.run();

	}
}
