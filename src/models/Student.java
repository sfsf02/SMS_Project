package models;

public class Student extends Person implements DatabaseOperations { 
    // Fields include id, course, and marks 
    private String id;
    private String course;
    private double marks;
    private String originalId;  // ← ADDED: Track ID changes
    
    // Constructor to initialize Person fields and Student fields
    public Student(String name, String email, String id, String course, double marks) {
        super(name, email);
        this.id = id;
        this.originalId = id;  // ← ADDED: Set original ID to match current ID
        this.course = course;
        this.marks = marks;
    }

    // Override displayInfo() 
    @Override
    public void displayInfo() {
        // Code to display student details goes here
    }

    // Implement database methods 
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