package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sms_database";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678";


    public static Connection getConnection() throws SQLException {
        try {
        // This forces Java to check if the driver is actually in your classpath
        Class.forName("com.mysql.cj.jdbc.Driver"); 
    } catch (ClassNotFoundException e) {
        System.err.println("MySQL Driver not found! Check  classpath.");
        e.printStackTrace();
    }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static boolean validateLogin(String username, String hashedPassword) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException ex) {
            System.err.println("Database authentication error: " + ex.getMessage());
            return false;
        }
    }
}