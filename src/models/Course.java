package models;

import database.DBConnection;
import java.sql.*;
import models.DatabaseOperations;

public class Course implements DatabaseOperations {
    private String courseId;
    private String courseName;
    private double creditWeight;
    private String originalCourseId;

    // Constructor with all fields
    public Course(String courseId, String courseName, double creditWeight,String originalCourseId) {
        this.courseId = courseId;
        this.originalCourseId = courseId;
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
   // UPDATE method - Modifies existing course in database
@Override
    public void update() {
        // Notice we are now updating the course_id column too!
        String sql = "UPDATE courses SET course_id = ?, course_name = ?, credit_weight = ? WHERE course_id = ?";
        
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String newCleanId = this.courseId.trim().toUpperCase();
            String cleanName = utils.StringHelper.toTitleCase(this.courseName);
            
            // Set the new values
            pstmt.setString(1, newCleanId);
            pstmt.setString(2, cleanName);
            pstmt.setDouble(3, this.creditWeight);
            
            // Set the WHERE clause to look for the ORIGINAL ID!
            pstmt.setString(4, this.originalCourseId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Success! Now the original ID matches the new ID.
                this.originalCourseId = newCleanId; 
                this.courseId = newCleanId;
                this.courseName = cleanName;
                System.out.println("✅ SUCCESS: Course updated.");
            } else {
                throw new RuntimeException("Update failed: Original Course ID '" + this.originalCourseId + "' not found.");
            }
            
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Database error during update: " + e.getMessage(), e);
        }
    }

    // SEARCH method stub
    @Override
    public void search(String keyword) {
        System.out.println("Search method called for: " + keyword);
    }
}