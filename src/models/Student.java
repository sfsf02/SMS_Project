package models;

import utils.StringHelper;
import database.DBConnection;
import java.sql.*;

public class Student extends Person implements DatabaseOperations { 
    private String id;
    private String course;
    private double marks;
    private String originalId;
    
    public Student(String name, String email, String id, String course, double marks) {
        super(name, email);
        this.id = id;
        this.originalId = id;
        this.course = course;
        this.marks = marks;
    }
    
    public Student() {
        super("", "");
        this.id = "";
        this.originalId = "";
        this.course = "";
        this.marks = 0;
    }

    // Getters & Defensive Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = (id == null) ? "" : id.trim().toUpperCase();
    }
    
    public String getOriginalId() {
        return originalId;
    }
    
    public void setOriginalId(String originalId) {
        this.originalId = (originalId == null) ? "" : originalId.trim().toUpperCase();
    }
    
    public String getCourse() {
        return course;
    }
    
    public void setCourse(String course) {
        this.course = (course == null) ? "" : StringHelper.toTitleCase(course);
    }
    
    public double getMarks() {
        return marks;
    }
    
    public void setMarks(double marks) {
        if (marks < 0) this.marks = 0;
        else if (marks > 100) this.marks = 100;
        else this.marks = marks;
    }
    
    @Override
    public void setName(String name) {
        super.setName((name == null) ? "" : StringHelper.toTitleCase(name));
    }
    
    @Override
    public void setEmail(String email) {
        super.setEmail((email == null) ? "" : email.trim().toLowerCase());
    }

    // Logic Helpers
    public void syncOriginalId() {
        this.originalId = this.id;
    }
    
    public boolean isPassing() {
        return this.marks >= 50;
    }
    
    public String getGrade() {
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }

    @Override
    public void displayInfo() {
        System.out.println("========================================");
        System.out.println("STUDENT DETAILS");
        System.out.println("========================================");
        System.out.println("ID: " + id);
        System.out.println("Name: " + getName());
        System.out.println("Email: " + getEmail());
        System.out.println("Course: " + course);
        System.out.println("Marks: " + marks);
        System.out.println("Grade: " + getGrade());
        System.out.println("Status: " + (isPassing() ? "PASS" : "FAIL"));
        System.out.println("========================================");
    }

    @Override
    public void add() {
        String insertStudentSql = "INSERT INTO students (student_id, name, email) VALUES (?, ?, ?)";
        // Assuming the user selects the course_id (like 'CSC101') from the dropdown
        String insertEnrollmentSql = "INSERT INTO enrollments (student_id, course_id, mark) VALUES (?, ?, ?)";

        java.sql.Connection conn = null;

        try {
            conn = database.DBConnection.getConnection();
            // Start Transaction: Don't save permanently until BOTH queries succeed
            conn.setAutoCommit(false); 

            // 1. Insert into students table
            try (java.sql.PreparedStatement pstmt1 = conn.prepareStatement(insertStudentSql)) {
                pstmt1.setString(1, this.getId());
                pstmt1.setString(2, this.getName());
                pstmt1.setString(3, this.getEmail());
                pstmt1.executeUpdate();
            }

            // 2. Insert into enrollments table
            try (java.sql.PreparedStatement pstmt2 = conn.prepareStatement(insertEnrollmentSql)) {
                pstmt2.setString(1, this.getId());
                pstmt2.setString(2, this.getCourse()); // This needs to be the course_id (e.g., CSC101)
                pstmt2.setDouble(3, this.getMarks());
                pstmt2.executeUpdate();
            }

            // Both succeeded! Commit to the database.
            conn.commit(); 
            this.syncOriginalId();
            System.out.println("SUCCESS: Student and Enrollment added for " + this.getName());

        } catch (java.sql.SQLException e) {
            // If anything fails, undo whatever was partially saved
            if (conn != null) {
                try { conn.rollback(); } catch (java.sql.SQLException ex) {}
            }
            throw new RuntimeException("Database error adding student: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (java.sql.SQLException ex) {}
            }
        }
    }

    @Override
    public void delete() {
        String sql = "DELETE FROM students WHERE student_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.id);
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("Student deleted: " + this.id);
            } else {
                System.out.println("Student not found: " + this.id);
            }
            
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
        }
    }

    @Override
    public void update() {
        String sql = "UPDATE students SET student_id = ?, name = ?, email = ?, course = ?, marks = ? WHERE student_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, this.id);
            pstmt.setString(2, this.getName());
            pstmt.setString(3, this.getEmail());
            pstmt.setString(4, this.course);
            pstmt.setDouble(5, this.marks);
            pstmt.setString(6, this.originalId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                this.syncOriginalId();
                System.out.println("Student updated: " + this.id);
            } else {
                System.out.println("Update failed: Student not found");
            }
            
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
        }
    }

    @Override
    public void search(String keyword) {
        String sql = "SELECT * FROM students WHERE student_id LIKE ? OR name LIKE ? OR email LIKE ? OR course LIKE ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            
            ResultSet rs = pstmt.executeQuery();
            int matchCount = 0;
            
            System.out.println("Search Results for: " + keyword);
            System.out.println("----------------------------------------");
            
            while (rs.next()) {
                matchCount++;
                System.out.println("ID: " + rs.getString("student_id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Email: " + rs.getString("email"));
                System.out.println("Course: " + rs.getString("course"));
                System.out.println("Marks: " + rs.getDouble("marks"));
                System.out.println("----------------------------------------");
            }
            
            if (matchCount == 0) {
                System.out.println("No students found for: " + keyword);
            } else {
                System.out.println("Total found: " + matchCount);
            }
            
        } catch (SQLException e) {
            System.err.println("Error searching students: " + e.getMessage());
        }
    }
}