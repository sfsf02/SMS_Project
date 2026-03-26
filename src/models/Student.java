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
        String updateStudentSql = "UPDATE students SET name = ?, email = ? WHERE student_id = ?";
        String updateEnrollmentSql = "UPDATE enrollments SET mark = ? WHERE student_id = ? AND course_id = ?";
        
        java.sql.Connection conn = null;
        try {
            conn = database.DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // 1. Update personal info using the object's internal state
            try (java.sql.PreparedStatement pstmt1 = conn.prepareStatement(updateStudentSql)) {
                pstmt1.setString(1, this.getName());
                pstmt1.setString(2, this.getEmail());
                pstmt1.setString(3, this.getId());    // The WHERE clause
                pstmt1.executeUpdate();
            }
            
            // 2. Update the specific course mark
            try (java.sql.PreparedStatement pstmt2 = conn.prepareStatement(updateEnrollmentSql)) {
                pstmt2.setDouble(1, this.getMarks());
                pstmt2.setString(2, this.getId());    // The WHERE clause
                pstmt2.setString(3, this.getCourse());// The WHERE clause (course_id)
                pstmt2.executeUpdate();
            }
            
            // Both succeeded! Commit to the database.
            conn.commit(); 
            System.out.println("SUCCESS: Updated details for " + this.getName());
            
        } catch (java.sql.SQLException e) {
            // Undo if anything fails
            if (conn != null) {
                try { conn.rollback(); } catch (java.sql.SQLException ex) {}
            }
            // Throw as a RuntimeException just like your add() method does
            throw new RuntimeException("Database error updating student: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (java.sql.SQLException ex) {}
            }
        }
    }

    
    public static java.util.ArrayList<Object[]> getData(String keyword, String sortBy, double minMarks) {
        java.util.ArrayList<Object[]> studentList = new java.util.ArrayList<>();
        
        // 1. The core JOIN query to get data from all 3 tables
        StringBuilder sql = new StringBuilder(
            "SELECT s.student_id, s.name, s.email, c.course_name, e.mark " +
            "FROM students s " +
            "JOIN enrollments e ON s.student_id = e.student_id " +
            "JOIN courses c ON e.course_id = c.course_id " +
            "WHERE (s.student_id LIKE ? OR s.name LIKE ? OR s.email LIKE ? OR c.course_name LIKE ?) " +
            "AND e.mark >= ?"
        );
        
        // 2. Add the dynamic sorting
        if (sortBy.equals("NAME")) {
            sql.append(" ORDER BY s.name ASC");
        } else if (sortBy.equals("MARKS")) {
            sql.append(" ORDER BY e.mark DESC");
        } else {
            sql.append(" ORDER BY s.student_id ASC");
        }
        
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            // 3. Fill in the ? placeholders
            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            pstmt.setString(4, searchPattern);
            pstmt.setDouble(5, minMarks); // From the slider or passing checkbox
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 4. Package each row as an Object array
                    Object[] row = new Object[5];
                    row[0] = rs.getString("student_id");
                    row[1] = rs.getString("name");
                    row[2] = rs.getString("email");
                    row[3] = rs.getString("course_name");
                    row[4] = rs.getDouble("mark");
                    
                    studentList.add(row);
                }
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Error loading student table: " + e.getMessage());
        }
        
        return studentList;
    }
    
    public static boolean checkEnrollmentExists(String studentId, String courseId) {
        String sql = "SELECT * FROM enrollments WHERE student_id = ? AND course_id = ?";
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            
            try (java.sql.ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // Returns TRUE if a record is found
            }
        } catch (java.sql.SQLException e) {
            System.err.println("Database error checking enrollment: " + e.getMessage());
        }
        return false;
    }
    
    public static void addGrade(String studentId, String courseId, double mark) throws java.sql.SQLException {
        String sql = "INSERT INTO enrollments (student_id, course_id, mark) VALUES (?, ?, ?)";
        
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, studentId);
            pstmt.setString(2, courseId);
            pstmt.setDouble(3, mark);
            
            pstmt.executeUpdate();
        }
    }
    public void deleteEnrollment() {
        // We strictly target the enrollments table, NOT the students table!
        String sql = "DELETE FROM enrollments WHERE student_id = ? AND course_id = ?";
        
        try (java.sql.Connection conn = database.DBConnection.getConnection();
             java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, this.getId());
            pstmt.setString(2, this.getCourse()); // This will hold the translated course_id
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new RuntimeException("No record found to delete. It may have already been removed.");
            }
            
            System.out.println("SUCCESS: Deleted enrollment for " + this.getId());
            
        } catch (java.sql.SQLException e) {
            throw new RuntimeException("Database error deleting record: " + e.getMessage(), e);
        }
    }
}