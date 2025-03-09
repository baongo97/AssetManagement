package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import entity.Portfolio;
import entity.PortfolioValue;
import util.DBConnector;

public class PortfolioDao {
	Connection conn = null;
	PreparedStatement preparedStatement = null;
	ResultSet result = null;
	
	public void insertPortflio(Portfolio portfolio) {
		int accountId= portfolio.getAccountId();
		String broker = portfolio.getBroker();
		insertPortflio(accountId, broker);
	}
	public void insertPortflio(int accountId, String broker) {
		String sqlQuery = "INSERT INTO `portfolio` "
				+ "(`account_id`, `broker`) "
				+ "VALUES (?,?);";
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);

			preparedStatement.setInt(1, accountId);
			preparedStatement.setString(2, broker);
			
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement);
		}
	}
	public int getPortfolioIdByAccountAndBroker(int accountId, String broker) {
		String sqlQuery = "SELECT * FROM portfolio WHERE account_id = ? AND broker = ?";
		int portfolioId = 0;
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			preparedStatement.setInt(1, accountId);
			preparedStatement.setString(2, broker);
			
			result = preparedStatement.executeQuery();
			if (result.next()) {
				portfolioId = result.getInt("portfolio_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		if (portfolioId == 0) {
			insertPortflio(accountId, broker);
			portfolioId = getPortfolioIdByAccountAndBroker(accountId,broker);
		}
		
		return portfolioId;
	}
	public PortfolioValue getPortfolioValue(int portfolioId){
		PortfolioValue portfolioValue = new PortfolioValue();	
		String sqlQuery = "SELECT time, SUM(`value (in USD)`) as value "
				+ "FROM asset_management.balance_history "
				+ "WHERE portfolio_id = ? "
				+ "GROUP BY time;";
		
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			preparedStatement.setInt(1, portfolioId);
			
			result = preparedStatement.executeQuery();
			List<Timestamp> timeList = new LinkedList<Timestamp>();
			List<Double> valueList = new LinkedList<Double>();
			
			while (result.next()) {
				Timestamp time = result.getTimestamp("time");
				double value  = result.getDouble("value");
				timeList.add(time);
				valueList.add(value);
			}
			portfolioValue.setTime(timeList);
			portfolioValue.setValue(valueList);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		return portfolioValue;
	}
	public static void main(String[] args) {
		PortfolioDao portfolioDao = new PortfolioDao();
//		portfolioDao.insertPortflio(2, "SSI");
		PortfolioValue portfolioValue = portfolioDao.getPortfolioValue(2);
		System.out.println(portfolioValue.getTime());
		System.out.println(portfolioValue.getValue());

	}
}
