package models;

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
        this.course = utils.StringHelper.toTitleCase(course);
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
    // DISPLAY INFO
    // ============================================================
    
    // Override displayInfo() 
    @Override
    public void displayInfo() {
        // Code to display student details goes here
    }

    // ============================================================
    // DATABASE OPERATIONS
    // ============================================================
    
    @Override
    public void add() {
        // JDBC PreparedStatement logic to insert this student 
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