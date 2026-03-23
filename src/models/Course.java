package models;

import database.DBConnection;
import utils.StringHelper;
import java.sql.*;

public class Course implements DatabaseOperations {
    private String courseId;
    private String courseName;
    private double creditWeight;
    private String originalCourseId;

    public Course(String courseId, String courseName, double creditWeight) {
        this.courseId = courseId;
        this.originalCourseId = courseId;
        this.courseName = courseName;
        this.creditWeight = creditWeight;
    }

    public Course() {
    }

    // Getters & Defensive Setters
    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = (courseId == null) ? "" : courseId.trim().toUpperCase();
    }

    public String getOriginalCourseId() {
        return originalCourseId;
    }

    public void setOriginalCourseId(String originalCourseId) {
        this.originalCourseId = (originalCourseId == null) ? "" : originalCourseId.trim().toUpperCase();
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = (courseName == null) ? "" : StringHelper.toTitleCase(courseName);
    }

    public double getCreditWeight() {
        return creditWeight;
    }

    public void setCreditWeight(double creditWeight) {
        this.creditWeight = (creditWeight < 0) ? 0 : creditWeight;
    }

    public void syncOriginalId() {
        this.originalCourseId = this.courseId;
    }

    public boolean isIdChanged() {
        if (this.courseId == null || this.originalCourseId == null) {
            return false;
        }
        return !this.courseId.trim().equalsIgnoreCase(this.originalCourseId.trim());
    }

    public void displayInfo() {
        String idStatus = isIdChanged() ? " (ID changed from: " + originalCourseId + ")" : "";
        System.out.println("Course ID: " + courseId + ", Name: " + courseName + ", Weight: " + creditWeight + idStatus);
    }

    @Override
    public void add() {
        String sql = "INSERT INTO courses (course_id, course_name, credit_weight) VALUES (?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String cleanId = this.courseId.trim().toUpperCase();
            String cleanName = StringHelper.toTitleCase(this.courseName);
            
            pstmt.setString(1, cleanId);
            pstmt.setString(2, cleanName);
            pstmt.setDouble(3, this.creditWeight);
            pstmt.executeUpdate();
            
            this.courseId = cleanId;
            this.courseName = cleanName;
            System.out.println("Course added: " + cleanName);
            
        } catch (SQLException e) {
            System.err.println("Error adding course: " + e.getMessage());
        }
    }

    @Override
    public void delete() {
        String sql = "DELETE FROM courses WHERE course_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String targetId = this.courseId.trim().toUpperCase();
            pstmt.setString(1, targetId);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Course deleted: " + targetId);
            } else {
                System.out.println("Course not found: " + targetId);
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting course: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        String sql = "UPDATE courses SET course_id = ?, course_name = ?, credit_weight = ? WHERE course_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String newCleanId = this.courseId.trim().toUpperCase();
            String cleanName = StringHelper.toTitleCase(this.courseName);
            
            pstmt.setString(1, newCleanId);
            pstmt.setString(2, cleanName);
            pstmt.setDouble(3, this.creditWeight);
            pstmt.setString(4, this.originalCourseId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                this.originalCourseId = newCleanId;
                this.courseId = newCleanId;
                this.courseName = cleanName;
                System.out.println("Course updated");
            } else {
                System.out.println("Update failed: Course not found");
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating course: " + e.getMessage());
        }
    }

    @Override
    public void search(String keyword) {
        String sql = "SELECT * FROM courses WHERE course_id LIKE ? OR course_name LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            int matchCount = 0;
            
            System.out.println("Search Results for: " + keyword);
            System.out.println("----------------------------------------");
            
            while (rs.next()) {
                matchCount++;
                System.out.println("ID: " + rs.getString("course_id"));
                System.out.println("Name: " + rs.getString("course_name"));
                System.out.println("Weight: " + rs.getDouble("credit_weight"));
                System.out.println("----------------------------------------");
            }
            
            if (matchCount == 0) {
                System.out.println("No courses found for: " + keyword);
            } else {
                System.out.println("Total found: " + matchCount);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching courses: " + e.getMessage());
        }
    }
}