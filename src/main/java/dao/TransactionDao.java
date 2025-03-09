package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import entity.Transaction;
import entity.TransactionType;
import util.DBConnector;

public class TransactionDao {
	Connection conn = null;
	PreparedStatement preparedStatement = null;
	ResultSet result = null;	
	
	public void insertTransaction(Transaction transaction) {
		String insertQuery = "INSERT INTO transaction (`account_id`, `security_id`, `quantity`, `price`, `type`, `time`) VALUES (?,?,?,?,?,?)";
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(insertQuery);
			
			preparedStatement.setInt(1, transaction.getAccountId());
			preparedStatement.setInt(2, transaction.getSecurityId());
			preparedStatement.setDouble(3, transaction.getQuantity());
			preparedStatement.setDouble(4, transaction.getPrice());
			preparedStatement.setString(5, transaction.getTypeString());
	        preparedStatement.setTimestamp(6, transaction.getTime());
	        
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement);
		}
	}
	public Transaction getTransactionById(int id) {
		String sqlQuery = "SELECT * FROM transaction WHERE id = ?";
		Transaction transaction = null;
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			preparedStatement.setInt(1, id);
			
			result = preparedStatement.executeQuery();
			if (result.next()) {
				int accountId = result.getInt("account_id");
				int securityId = result.getInt("security_id");
				double quantity = result.getDouble("quantity");
				double price = result.getDouble("price");
				
				String typeString = result.getString("type");
				TransactionType type = TransactionType.valueOf(typeString);
				
				Timestamp time = result.getTimestamp("time");
				transaction = new Transaction(accountId, securityId, quantity, price, type, time);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		return transaction;
	}
	
	public static void main(String[] args) {
		TransactionDao transactionDao = new TransactionDao();
//		Transaction transaction = new Transaction(3, 3, 11, 1, TransactionType.SELL, Timestamp.valueOf("2025-11-01 12:30:35"));
//		transactionDao.insertTransaction(transaction);
		Transaction transaction2 = transactionDao.getTransactionById(33);
		System.out.println(transaction2);
	}
}
