package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import entity.Balance;
import util.DBConnector;

public class BalanceDao {
	Connection conn = null;
	PreparedStatement preparedStatement = null;
	
	public void insertBalance(Balance balance) {
		int portfolioId= balance.getPortfolioId();
		int securityId = balance.getSecurityId();
		double quantity = balance.getQuantity();
		double value = balance.getValue();
		Timestamp time = balance.getTime();
		insertBalance(portfolioId, securityId, quantity, value, time);
	}
	public void insertBalance(int portfolioId, int securityId, double quantity, double value, Timestamp time) {
		String sqlQuery = "INSERT INTO `balance_history` "
				+ "(`portfolio_id`,`security_id`, `quantity`, `value (in USD)`, `time`) "
				+ "VALUES (?,?,?,?,?);";
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);

			preparedStatement.setInt(1, portfolioId);
			preparedStatement.setInt(2, securityId);
			preparedStatement.setDouble(3, quantity);
			preparedStatement.setDouble(4, value);
			preparedStatement.setTimestamp(5, time);
			
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement);
		}
	}
	public static void main(String[] args) {
//		BalanceDao balanceDao = new BalanceDao();
//		balanceDao.insertBalance(1,5, 11, 12, new Timestamp(System.currentTimeMillis()));
	}
}
