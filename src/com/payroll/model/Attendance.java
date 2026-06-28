package com.payroll.model;

// Declaring the Attendance class to store employee daily attendance
public class Attendance {
    // Declaring the employee ID field
    private int employeeId;
    // Declaring the date field (YYYY-MM-DD)
    private String date;
    // Declaring the attendance status field (Present, Absent, Leave)
    private String status;

    // Constructor to initialize all attendance fields
    public Attendance(int employeeId, String date, String status) {
        // Assigning the employeeId parameter to the field
        this.employeeId = employeeId;
        // Assigning the date parameter to the field
        this.date = date;
        // Assigning the status parameter to the field
        this.status = status;
    }

    // Getter method for employee ID
    public int getEmployeeId() {
        // Returning the employee ID
        return this.employeeId;
    }

    // Getter method for date
    public String getDate() {
        // Returning the date
        return this.date;
    }

    // Getter method for status
    public String getStatus() {
        // Returning the status
        return this.status;
    }
}
