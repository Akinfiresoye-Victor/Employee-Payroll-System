package com.payroll.model;

// Declaring the Employee class to store employee information
public class Employee {
    // Declaring the employee ID field
    private int employeeId;
    // Declaring the employee name field
    private String name;
    // Declaring the employee base salary field
    private double baseSalary;
    // Declaring the employee department field
    private String department;
    // Declaring the employee join date field
    private String joinDate;

    // Constructor to initialize all employee fields
    public Employee(int employeeId, String name, double baseSalary, String department, String joinDate) {
        // Assigning the employeeId parameter to the field
        this.employeeId = employeeId;
        // Assigning the name parameter to the field
        this.name = name;
        // Assigning the baseSalary parameter to the field
        this.baseSalary = baseSalary;
        // Assigning the department parameter to the field
        this.department = department;
        // Assigning the joinDate parameter to the field
        this.joinDate = joinDate;
    }

    // Getter method for employee ID
    public int getEmployeeId() {
        // Returning the employee ID
        return this.employeeId;
    }

    // Getter method for employee name
    public String getName() {
        // Returning the employee name
        return this.name;
    }

    // Getter method for base salary
    public double getBaseSalary() {
        // Returning the base salary
        return this.baseSalary;
    }

    // Getter method for department
    public String getDepartment() {
        // Returning the department
        return this.department;
    }

    // Getter method for join date
    public String getJoinDate() {
        // Returning the join date
        return this.joinDate;
    }
}
