package models;

import database.DBConnection;
import utils.StringHelper;
import java.sql.*;

public class Course implements DatabaseOperations {
    private String courseId;
    private String courseName;

    public Course(String courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
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

    

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = (courseName == null) ? "" : StringHelper.toTitleCase(courseName);
    }

    

   

   

    @Override
    public void add() {
        // We use an INSERT statement specifically for the courses table
        String sql = "INSERT INTO courses (course_id, course_name) VALUES (?, ?)";
        
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            // Read from the "suitcase"
            pstmt.setString(1, this.getCourseId());   
            pstmt.setString(2, this.getCourseName()); 
            
            pstmt.executeUpdate();
            System.out.println("SUCCESS: Added new course: " + this.getCourseName());
            
        } catch (java.sql.SQLException e) {
            // If the user tries to add an ID that already exists, it will throw an error here!
            throw new RuntimeException("Database error. The Course ID might already exist: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete() {
        // 1. First, delete the links in the enrollments table
        String deleteEnrollmentsSql = "DELETE FROM enrollments WHERE course_id = ?";
        // 2. Then, delete the course itself
        String deleteCourseSql = "DELETE FROM courses WHERE course_id = ?";
        
        java.sql.Connection conn = null;
        try {
            conn = database.DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Step 1: Wipe the enrollments
            try (java.sql.PreparedStatement pstmt1 = conn.prepareStatement(deleteEnrollmentsSql)) {
                pstmt1.setString(1, this.getCourseId());
                pstmt1.executeUpdate(); // We don't check rows affected, because it's okay if 0 students were enrolled
            }
            
            // Step 2: Wipe the course
            try (java.sql.PreparedStatement pstmt2 = conn.prepareStatement(deleteCourseSql)) {
                pstmt2.setString(1, this.getCourseId());
                int rowsAffected = pstmt2.executeUpdate();
                
                if (rowsAffected == 0) {
                    throw new RuntimeException("Could not find the course to delete.");
                }
            }
            
            // Both succeeded! Commit to the database.
            conn.commit(); 
            System.out.println("SUCCESS: Deleted course " + this.getCourseName());
            
        } catch (java.sql.SQLException e) {
            // Undo everything if it crashes
            if (conn != null) {
                try { conn.rollback(); } catch (java.sql.SQLException ex) {}
            }
            throw new RuntimeException("Database error deleting course: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (java.sql.SQLException ex) {}
            }
        }
    }

    @Override
    public void update() {
        // We strictly target the course_name, using the course_id to find the exact row
        String sql = "UPDATE courses SET course_name = ? WHERE course_id = ?";
        
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            // Read the data directly out of the "suitcase"
            pstmt.setString(1, this.getCourseName());
            pstmt.setString(2, this.getCourseId());
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new RuntimeException("Could not find the course to update.");
            }
            
            System.out.println("SUCCESS: Updated course to " + this.getCourseName());
            
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Database error updating course: " + e.getMessage(), e);
        }
    }

    public static java.util.ArrayList<String[]> getData(String keyword, boolean sortByName) {
        java.util.ArrayList<String[]> courseList = new java.util.ArrayList<>();
        
        // 1. Build the SQL query
        // We search in BOTH the course_id and course_name
        StringBuilder sql = new StringBuilder(
            "SELECT course_id, course_name FROM courses " +
            "WHERE course_id LIKE ? OR course_name LIKE ?"
        );
        
        // 2. Add the dynamic sorting based on the radio buttons
        if (sortByName) {
            sql.append(" ORDER BY course_name ASC");
        } else {
            sql.append(" ORDER BY course_id ASC");
        }
        
        // 3. Connect to DB and execute
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            // Add the % wildcard symbols to allow partial matching
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 4. Package each row as a String array: [ID, Name]
                    String[] row = new String[2];
                    row[0] = rs.getString("course_id");
                    row[1] = rs.getString("course_name");
                    
                    courseList.add(row);
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error loading course table: " + e.getMessage());
        }
        
        return courseList;
    }
    
    public static String getIdByName(String courseName) {
        String sql = "SELECT course_id FROM courses WHERE course_name = ?";
        
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, courseName);
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("course_id"); 
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error looking up course ID: " + e.getMessage());
        }
        return null; // Returns null if no course is found
    }
    
    public static java.util.ArrayList<String> getAllCourseNames() {
        java.util.ArrayList<String> courseNames = new java.util.ArrayList<>();
        // Sorting alphabetically is a standard UX best practice
        String sql = "SELECT course_name FROM courses ORDER BY course_name ASC";
        
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                courseNames.add(rs.getString("course_name"));
            }
            
        } catch (java.sql.SQLException e) {
            System.err.println("Error loading courses: " + e.getMessage());
        }
        
        return courseNames;
    }
}