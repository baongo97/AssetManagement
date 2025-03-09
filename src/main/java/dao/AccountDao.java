package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import entity.Account;
import util.DBConnector;

public class AccountDao {
	Connection conn = null;
	PreparedStatement preparedStatement = null;
	ResultSet result = null;
	
	public void insertAccount(Account account) {
		String name = account.getName();
		String email = account.getEmail();
		String phone = account.getPhone();
		String address = account.getAddress();
		insertAccount(name, email, phone, address);
	}
		
	public void insertAccount(String name, String email, String phone, String address) {
		String insertQuery = "INSERT INTO account (`name`, `email`, `phone`, `address`) VALUES (?,?,?,?)";
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(insertQuery);
			preparedStatement.setString(1, name);
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, phone);
			preparedStatement.setString(4, address);
	        
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement);
		}
	}

	public int getAccountIdByEmail(String email) throws SQLException {
		String sqlQuery = "SELECT * FROM account WHERE email = ?";
		int accountId = 0;
		try {
			conn = DBConnector.makeConnection();
			preparedStatement = conn.prepareStatement(sqlQuery);
			preparedStatement.setString(1, email);
			result = preparedStatement.executeQuery();
			if (result.next()) {
				accountId = result.getInt("account_id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnector.closeResources(conn, preparedStatement, result);
		}
		if (accountId == 0) {
			throw new SQLException("No avaiable account");
		}
		return accountId;
	}
	public static void main(String[] args) {
//		AccountDao accountDao = new AccountDao();
	}
}
