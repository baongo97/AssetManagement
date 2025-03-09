package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import crawler.CrawlingApp;
import entity.Security;
import entity.SecurityCategory;
import entity.Currency;
import util.DBConnector;

public class SecurityDao {
	Connection conn = null;
	PreparedStatement preparedStatement = null;
	ResultSet result = null;
	
	public void insertSecurity(Security security) {
		String name = security.getName();
		String symbol = security.getSymbol();
		SecurityCategory category = security.getCategory();
		Currency currency = security.getCurrency();
		this.insertSecurity(name, symbol, category, currency);
	} 
	public void insertSecurity(String symbol, SecurityCategory cat, Currency cur) {
		CrawlingApp crawler = new CrawlingApp();
		String securityName = crawler.getSecurityName(symbol, cat, cur);
		insertSecurity(securityName, symbol, cat, cur);
	}
	public void insertSecurity(String name, String symbol, SecurityCategory cat, Currency cur) {
		String insertQuery = "INSERT INTO security (`name`,`symbol`, `category`, `currency`) VALUES (?,?,?,?)";
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(insertQuery);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, symbol);
			preparedStatement.setString(3, cat.name());
			preparedStatement.setString(4, cur.name());
	        
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement);
		}
	}
	public int getSecurityIdBySymbolCategoryCurrency(String symbol, SecurityCategory cat, Currency cur) throws SQLException {
		String sqlQuery = "SELECT * FROM security WHERE symbol = ?"
						+ "AND category = ?"
						+ "AND currency = ?";
		int securityId = 0;
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			preparedStatement.setString(1, symbol);
			preparedStatement.setString(2, cat.name());
			preparedStatement.setString(3, cur.name());
			result = preparedStatement.executeQuery();
			if (result.next()) {
				securityId = result.getInt("security_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		if (securityId == 0) {
			throw new SQLException("No avaiable symbol in database");
		}
		
		return securityId;
	}
	public int getLastId() {
		String sqlQuery = "SELECT MAX(security_id) FROM security";
		int securityId = 0;
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				securityId = result.getInt("MAX(security_id)");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		
		return securityId;
	}
	public static void main(String[] args) {
		SecurityDao securityDao = new SecurityDao();
		try {
			System.out.println(securityDao.getSecurityIdBySymbolCategoryCurrency("AUD", SecurityCategory.FIAT, Currency.AUD));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
