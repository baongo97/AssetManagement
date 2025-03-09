package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnector {
	public static Connection makeConnection() {
		String PATH = "jdbc:mysql://localhost:3306/asset_management";
		String USERNAME = "root";
		String PASSWORD = "root";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection(PATH, USERNAME, PASSWORD);
			
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    public static void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void closeResources(Connection connection, Statement statement) {
        closeResources(connection, statement, null);
    }
    public static void closeResources(Connection connection) {
        closeResources(connection, null, null);
    }
}
