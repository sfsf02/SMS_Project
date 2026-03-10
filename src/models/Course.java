package models;

import database.DBConnection;
import java.sql.*;
import models.DatabaseOperations;

public class Course implements DatabaseOperations {
    private String courseId;
    private String courseName;
    private double creditWeight;

    // Constructor with all fields
    public Course(String courseId, String courseName, double creditWeight) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.creditWeight = creditWeight;
    }

    // Empty constructor
    public Course() {
    }

    // CREATE - Add course to database
    @Override
    public void add() {
        String sql = "INSERT INTO courses (course_id, course_name, credit_weight) VALUES (?, ?, ?)";
        
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // 1. Sanitize the inputs before they hit the database!
            String cleanId = this.courseId.trim().toUpperCase(); // Forces IDs to look like "CSC101"
            
            // Call YOUR custom method to perfectly capitalize the course name!
            String cleanName = utils.StringHelper.toTitleCase(this.courseName); 
            
            // 2. Set the sanitized variables into the Prepared Statement
            pstmt.setString(1, cleanId);
            pstmt.setString(2, cleanName);
            pstmt.setDouble(3, this.creditWeight);
            
            // 3. Execute
            pstmt.executeUpdate();
            
            // Update the object's internal state to match the clean database version
            this.courseId = cleanId;
            this.courseName = cleanName;
            
            System.out.println("✅ Course permanently added: " + cleanName);
            
        } catch (java.sql.SQLException e) {
            System.err.println("❌ Error adding course: " + e.getMessage());
        }
    }
    // DELETE - Remove course from database
    @Override
    public void delete() {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // Ensure the ID is formatted correctly before searching the database
            String targetId = this.courseId.trim().toUpperCase();
            pstmt.setString(1, targetId); 
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Use YOUR custom method to build a clean log string
                String logMessage = utils.StringHelper.concatenateStrings(
                    " | ", 
                    "✅ SUCCESS", 
                    "Action: DELETED", 
                    "Target ID: " + targetId
                );
                System.out.println(logMessage);
            } else {
                System.out.println("❌ Course not found. ID: " + targetId);
            }
            
        } catch (java.sql.SQLException e) {
            System.err.println("❌ Error deleting course: " + e.getMessage());
        }
    }
    // UPDATE method stub
    @Override
    public void update() {
        System.out.println("Update method called");
    }

    // SEARCH method stub
    @Override
    public void search(String keyword) {
        System.out.println("Search method called for: " + keyword);
    }
}