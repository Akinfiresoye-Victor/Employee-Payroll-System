package com.payroll.model;

// Declaring the Salary class to store calculated salary details
public class Salary {
    // Declaring the employee ID field
    private int employeeId;
    // Declaring the month field for which salary is calculated (YYYY-MM)
    private String month;
    // Declaring the gross salary field
    private double grossSalary;
    // Declaring the deductions field (e.g., tax)
    private double deductions;
    // Declaring the net salary field
    private double netSalary;
    // Declaring the date on which the salary record was generated
    private String dateGenerated;

    // Constructor to initialize all salary fields
    public Salary(int employeeId, String month, double grossSalary, double deductions, double netSalary, String dateGenerated) {
        // Assigning the employeeId parameter to the field
        this.employeeId = employeeId;
        // Assigning the month parameter to the field
        this.month = month;
        // Assigning the grossSalary parameter to the field
        this.grossSalary = grossSalary;
        // Assigning the deductions parameter to the field
        this.deductions = deductions;
        // Assigning the netSalary parameter to the field
        this.netSalary = netSalary;
        // Assigning the dateGenerated parameter to the field
        this.dateGenerated = dateGenerated;
    }

    // Getter method for employee ID
    public int getEmployeeId() {
        // Returning the employee ID
        return this.employeeId;
    }

    // Getter method for month
    public String getMonth() {
        // Returning the month
        return this.month;
    }

    // Getter method for gross salary
    public double getGrossSalary() {
        // Returning the gross salary
        return this.grossSalary;
    }

    // Getter method for deductions
    public double getDeductions() {
        // Returning the deductions
        return this.deductions;
    }

    // Getter method for net salary
    public double getNetSalary() {
        // Returning the net salary
        return this.netSalary;
    }

    // Getter method for date generated
    public String getDateGenerated() {
        // Returning the date generated
        return this.dateGenerated;
    }
}
