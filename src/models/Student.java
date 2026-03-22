package models;

import utils.StringHelper;

public class Student extends Person implements DatabaseOperations { 
    // Fields include id, course, and marks 
    private String id;
    private String course;
    private double marks;
    private String originalId;  // Track ID changes
    
    // Constructor to initialize Person fields and Student fields
    public Student(String name, String email, String id, String course, double marks) {
        super(name, email);
        this.id = id;
        this.originalId = id;  // Set original ID to match current ID
        this.course = course;
        this.marks = marks;
    }

    // ============================================================
    // GETTERS & DEFENSIVE SETTERS
    // ============================================================
    
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

    // ============================================================
    // COURSE & MARKS GETTERS/SETTERS
    // ============================================================
    
    public String getCourse() {
        return course;
    }
    
    public void setCourse(String course) {
        this.course = StringHelper.toTitleCase(course);
    }
    
    public double getMarks() {
        return marks;
    }
    
    public void setMarks(double marks) {
        if (marks < 0) this.marks = 0;
        else if (marks > 100) this.marks = 100;
        else this.marks = marks;
    }

    // ============================================================
    // PERSON GETTERS/SETTERS OVERRIDE
    // ============================================================
    
    @Override
    public String getName() {
        return super.getName();
    }
    
    @Override
    public void setName(String name) {
        super.setName(StringHelper.toTitleCase(name));
    }
    
    @Override
    public String getEmail() {
        return super.getEmail();
    }
    
    @Override
    public void setEmail(String email) {
        super.setEmail(email == null ? "" : email.trim().toLowerCase());
    }

    // ============================================================
    // LOGIC HELPERS
    // ============================================================
    
    /**
     * Resets the originalId to match the current id.
     * Call this after successful database updates.
     */
    public void syncOriginalId() {
        this.originalId = this.id;
    }
    
    /**
     * Checks if the student is passing (marks >= 50)
     * @return true if marks are 50 or above
     */
    public boolean isPassing() {
        return this.marks >= 50;
    }
    
    /**
     * Returns letter grade based on marks
     * @return A, B, C, D, or F
     */
    public String getGrade() {
        if (marks >= 80) return "A";
        if (marks >= 70) return "B";
        if (marks >= 60) return "C";
        if (marks >= 50) return "D";
        return "F";
    }

    // ============================================================
    // DISPLAY INFO
    // ============================================================
    
    @Override
    public void displayInfo() {
        String border = "╔══════════════════════════════════════════╗";
        String separator = "╠══════════════════════════════════════════╣";
        String bottom = "╚══════════════════════════════════════════╝";
        
        System.out.println(border);
        System.out.println("║            STUDENT DETAILS               ║");
        System.out.println(separator);
        System.out.printf("║ %-10s: %-25s ║%n", "ID", id);
        System.out.printf("║ %-10s: %-25s ║%n", "Name", getName());
        System.out.printf("║ %-10s: %-25s ║%n", "Email", getEmail());
        System.out.printf("║ %-10s: %-25s ║%n", "Course", course);
        System.out.printf("║ %-10s: %-25.1f ║%n", "Marks", marks);
        System.out.printf("║ %-10s: %-25s ║%n", "Grade", getGrade());
        System.out.printf("║ %-10s: %-25s ║%n", "Status", (isPassing() ? "PASS" : "FAIL"));
        System.out.println(bottom);
    }

    // ============================================================
    // DATABASE OPERATIONS
    // ============================================================
    
    @Override
    public void add() {
        // JDBC PreparedStatement logic to insert this student 
        String sql = "INSERT INTO students (student_id, name, email, course, marks) VALUES (?, ?, ?, ?, ?)";
    
    try (java.sql.Connection conn = database.DBConnection.getConnection();
         java.sql.PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, this.id);
        pstmt.setString(2, this.getName());
        pstmt.setString(3, this.getEmail());
        pstmt.setString(4, this.course);
        pstmt.setDouble(5, this.marks);
        
        pstmt.executeUpdate();
        
        this.syncOriginalId();
        System.out.println("✅ SUCCESS: Student added - " + this.getName() + " (ID: " + this.id + ")");
        
    } catch (java.sql.SQLException e) {
        throw new RuntimeException("Database error adding student: " + e.getMessage(), e);
    }
    }

    @Override
    public void delete() {
        // JDBC logic to delete this student
    }

    @Override
    public void update() {
        // JDBC logic to update this student
    }

    @Override
    public void search(String keyword) {
        // JDBC logic with case-insensitive search 
    }
}