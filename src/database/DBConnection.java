package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

    // Database credentials (adjust these for MySQL or SQLite)
    private static final String URL = "jdbc:mysql://localhost:3306/sms_database";
    private static final String USER = "root";
    private static final String PASSWORD = "12345678";

    /**
     * Establishes the connection to the database.
     */
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

    /**
     * Validates the login credentials against the database.
     * @param username the username from the GUI
     * @param hashedPassword the SHA-256 hashed password from the GUI
     * @return true if credentials are valid, false otherwise
     */
    public static boolean validateLogin(String username, String hashedPassword) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ?";
        
        // Using try-with-resources to automatically close the connection and prevent memory leaks
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
             
            // Securely set the parameters to prevent SQL injection
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            
            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                // If rs.next() is true, a matching user was found!
                return rs.next();
            }
            
        } catch (SQLException ex) {
            System.err.println("Database authentication error:");
            ex.printStackTrace();
            return false;
        }
    }
}