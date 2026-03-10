package models;

import database.DBConnection;
import java.sql.*;
import models.DatabaseOperations;

public class Course implements DatabaseOperations {
    private int courseId;
    private String courseName;
    private double creditWeight;

    // Constructor with all fields
    public Course(int courseId, String courseName, double creditWeight) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.creditWeight = creditWeight;
    }

    // Empty constructor
    public Course() {
    }

    // ADD METHOD
    @Override
    public void add() {
        String sql = "INSERT INTO courses (course_name, credit_weight) VALUES (?, ?)";
        
        try (Connection conn = DBConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.courseName);
            pstmt.setDouble(2, this.creditWeight);
            pstmt.executeUpdate();
            
            System.out.println("✅ Course added: " + this.courseName);
            
        } catch (SQLException e) {
            System.out.println("❌ Error adding course: " + e.getMessage());
        }
    }

    // DELETE method stub
    @Override
    public void delete() {
        // TODO: Implement delete method
        System.out.println("Delete method called");
    }

    // UPDATE method stub
    @Override
    public void update() {
        // TODO: Implement update method
        System.out.println("Update method called");
    }

    // SEARCH method stub
    @Override
    public void search(String keyword) {
        // TODO: Implement search method
        System.out.println("Search method called for: " + keyword);
    }
}