package com.payroll;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import com.payroll.database.DatabaseManager;
import com.payroll.model.Employee;
import com.payroll.report.ReportGenerator;

// Main class representing the entry point of the Employee Payroll System CLI application
public class Main {
    // Regular expression pattern to validate date format (YYYY-MM-DD)
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    // Regular expression pattern to validate month format (YYYY-MM)
    private static final Pattern MONTH_PATTERN = Pattern.compile("^\\d{4}-\\d{2}$");

    // Main execution method
    public static void main(String[] args) {
        // Initializing the database and tables
        DatabaseManager.initializeDatabase();
        // Seeding database with initial test data if empty
        seedInitialTestData();
        // Creating BufferedReader to read user inputs from the console
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        // Flag to control the main menu loop
        boolean running = true;

        // Loop to display the menu repeatedly until the user exits
        while (running) {
            try {
                // Printing the main menu headers
                System.out.println("\n========== PAYROLL SYSTEM ==========");
                System.out.println("1. Add Employee");
                System.out.println("2. Record Attendance");
                System.out.println("3. Calculate Salaries (for a month)");
                System.out.println("4. View Employee List Report");
                System.out.println("5. View Attendance Report");
                System.out.println("6. View Payroll Report");
                System.out.println("7. Exit");
                System.out.println("====================================");
                System.out.print("Select an option (1-7): ");

                // Reading user choice
                String choice = reader.readLine();
                if (choice == null) {
                    // Exiting if input stream is closed
                    running = false;
                    // Continuing to exit
                    continue;
                }
                // Trimming the input choice
                choice = choice.trim();

                // Branching logic based on user choice
                if (choice.equals("1")) {
                    // Invoking add employee procedure
                    addNewEmployee(reader);
                } else if (choice.equals("2")) {
                    // Invoking record attendance procedure
                    recordAttendance(reader);
                } else if (choice.equals("3")) {
                    // Invoking calculate salaries procedure
                    calculateSalaries(reader);
                } else if (choice.equals("4")) {
                    // Invoking view employee list report procedure
                    viewEmployeeListReport();
                } else if (choice.equals("5")) {
                    // Invoking view attendance report procedure
                    viewAttendanceReport(reader);
                } else if (choice.equals("6")) {
                    // Invoking view payroll report procedure
                    viewPayrollReport(reader);
                } else if (choice.equals("7")) {
                    // Setting flag to stop running loop
                    running = false;
                    // Printing exit confirmation
                    System.out.println("Exiting Payroll System. Goodbye!");
                } else {
                    // Handling invalid choices
                    System.out.println("Invalid option. Please enter a number between 1 and 7.");
                }
            } catch (Exception e) {
                // Logging general execution errors
                System.err.println("An unexpected error occurred in the menu loop: " + e.getMessage());
            }
        }

        try {
            // Closing input reader
            reader.close();
        } catch (IOException e) {
            // Logging close error
            e.printStackTrace();
        }
    }

    // Method to seed initial test data into the database
    private static void seedInitialTestData() {
        // Retrieve all employees to check if table is empty
        java.util.ArrayList employees = DatabaseManager.getAllEmployees();
        // If employee list is empty, proceed to seed test data
        if (employees.isEmpty()) {
            // Logging seeding action
            System.out.println("Seeding database with test employees, attendance, and payroll...");

            // Adding 3 test employees
            DatabaseManager.addEmployee("Alice Smith", 3000.0, "Engineering", "2026-06-01");
            DatabaseManager.addEmployee("Bob Jones", 4500.0, "Marketing", "2026-06-05");
            DatabaseManager.addEmployee("Charlie Brown", 2500.0, "HR", "2026-06-10");

            // Seeding 10 attendance records across 2 weeks (June 1st to June 14th)
            DatabaseManager.recordAttendance(1, "2026-06-01", "Present");
            DatabaseManager.recordAttendance(1, "2026-06-02", "Present");
            DatabaseManager.recordAttendance(1, "2026-06-03", "Present");
            DatabaseManager.recordAttendance(1, "2026-06-04", "Present");
            DatabaseManager.recordAttendance(1, "2026-06-05", "Present");
            
            DatabaseManager.recordAttendance(2, "2026-06-08", "Present");
            DatabaseManager.recordAttendance(2, "2026-06-09", "Present");
            DatabaseManager.recordAttendance(2, "2026-06-10", "Present");
            DatabaseManager.recordAttendance(2, "2026-06-11", "Absent");
            
            DatabaseManager.recordAttendance(3, "2026-06-12", "Leave");

            // Getting current time string
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            // Formatting current date
            String dateToday = formatter.format(new Date());

            // Pre-calculate salary for employee 1 for June 2026: (3000.0 / 30) * 5 worked = 500.0 gross, 50.0 deductions, 450.0 net
            DatabaseManager.addSalary(1, "2026-06", 500.0, 50.0, 450.0, dateToday);

            // Logging seeding completion
            System.out.println("Seed complete.");
        }
    }

    // Method to guide user to add a new employee
    private static void addNewEmployee(BufferedReader reader) {
        try {
            // Prompting for employee name
            System.out.print("Enter employee name: ");
            // Reading name input
            String name = reader.readLine();
            if (name == null || name.trim().isEmpty()) {
                // Informing user of invalid name
                System.out.println("Error: Employee name cannot be empty.");
                // Terminating method execution
                return;
            }

            // Prompting for base salary
            System.out.print("Enter base salary (e.g. 3000.00): ");
            // Reading base salary input
            String baseSalaryInput = reader.readLine();
            if (baseSalaryInput == null) {
                // Terminating on empty input
                return;
            }
            // Local variable for parsed salary value
            double baseSalary = 0;
            try {
                // Parsing salary to double value
                baseSalary = Double.parseDouble(baseSalaryInput.trim());
            } catch (NumberFormatException e) {
                // Informing user of invalid numeric input
                System.out.println("Error: Salary must be a valid positive number.");
                // Terminating method
                return;
            }
            if (baseSalary <= 0) {
                // Validating base salary is positive
                System.out.println("Error: Salary must be greater than zero.");
                // Terminating method
                return;
            }

            // Prompting for department
            System.out.print("Enter department: ");
            // Reading department input
            String department = reader.readLine();
            if (department == null || department.trim().isEmpty()) {
                // Validating department input is not empty
                System.out.println("Error: Department cannot be empty.");
                // Terminating method
                return;
            }

            // Formatting join date string from current date
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            // Fetching current formatted date
            String joinDate = formatter.format(new Date());

            // Adding the employee to the database
            boolean success = DatabaseManager.addEmployee(name.trim(), baseSalary, department.trim(), joinDate);
            if (success) {
                // Reporting success to the user
                System.out.println("Employee added successfully!");
            } else {
                // Reporting database insertion failure
                System.out.println("Error: Failed to add employee to the database.");
            }
        } catch (IOException e) {
            // Logging input-output error
            System.err.println("IO Error reading input: " + e.getMessage());
        }
    }

    // Method to guide user to record an attendance record
    private static void recordAttendance(BufferedReader reader) {
        try {
            // Prompting for employee ID
            System.out.print("Enter employee ID: ");
            // Reading employee ID input
            String idInput = reader.readLine();
            if (idInput == null) {
                // Terminating on empty input
                return;
            }
            // Variable to store parsed integer ID
            int employeeId = 0;
            try {
                // Parsing employee ID
                employeeId = Integer.parseInt(idInput.trim());
            } catch (NumberFormatException e) {
                // Outputting format error message
                System.out.println("Error: Employee ID must be a valid integer.");
                // Terminating method
                return;
            }

            // Checking if the employee exists in the database
            if (!DatabaseManager.isEmployeeExists(employeeId)) {
                // Informing user of non-existent employee ID
                System.out.println("Error: Employee with ID " + employeeId + " does not exist.");
                // Terminating method
                return;
            }

            // Prompting for attendance date
            System.out.print("Enter date (YYYY-MM-DD): ");
            // Reading date input
            String date = reader.readLine();
            if (date == null || !DATE_PATTERN.matcher(date.trim()).matches()) {
                // Checking format regex matches YYYY-MM-DD
                System.out.println("Error: Date must be in the format YYYY-MM-DD.");
                // Terminating method
                return;
            }
            // Trimming date input
            date = date.trim();

            // Prompting for status
            System.out.print("Enter status (Present/Absent/Leave): ");
            // Reading status input
            String status = reader.readLine();
            if (status == null) {
                // Terminating method
                return;
            }
            // Trimming status input
            status = status.trim();
            // Validating status constraints
            if (!status.equals("Present") && !status.equals("Absent") && !status.equals("Leave")) {
                // Reporting invalid status values
                System.out.println("Error: Status must be 'Present', 'Absent', or 'Leave'.");
                // Terminating method
                return;
            }

            // Recording the attendance record
            boolean success = DatabaseManager.recordAttendance(employeeId, date, status);
            if (success) {
                // Outputting confirmation of attendance entry
                System.out.println("Attendance recorded successfully!");
            } else {
                // Reporting failure (usually duplicate entry or invalid input)
                System.out.println("Error: Failed to record attendance. Duplicate entries for the same employee and date are not allowed.");
            }
        } catch (IOException e) {
            // Handling input output errors
            System.err.println("IO Error reading input: " + e.getMessage());
        }
    }

    // Method to calculate monthly salaries for all employees
    private static void calculateSalaries(BufferedReader reader) {
        try {
            // Prompting for the calculation month
            System.out.print("Enter month (YYYY-MM): ");
            // Reading month input
            String month = reader.readLine();
            if (month == null || !MONTH_PATTERN.matcher(month.trim()).matches()) {
                // Verifying format matches YYYY-MM
                System.out.println("Error: Month must be in the format YYYY-MM.");
                // Terminating method
                return;
            }
            // Trimming month input
            month = month.trim();

            // Fetching all employees from database
            java.util.ArrayList employeeList = DatabaseManager.getAllEmployees();
            if (employeeList.isEmpty()) {
                // Aborting if no employees found
                System.out.println("Error: No employees found to calculate salaries.");
                // Terminating method
                return;
            }

            // Getting current time string
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            // Setting local date generated string
            String dateToday = formatter.format(new Date());

            // Variables to track success counters
            int calculatedCount = 0;
            // Variables to track duplicate skipped records
            int skippedCount = 0;

            // Iterating through all employees without using streams or generics
            for (int i = 0; i < employeeList.size(); i++) {
                // Reading employee object from raw list
                Employee employee = (Employee) employeeList.get(i);
                // Getting individual employee ID
                int empId = employee.getEmployeeId();

                // Checking if salary was already calculated for this employee and month
                if (DatabaseManager.isSalaryCalculated(empId, month)) {
                    // Incrementing skipped count
                    skippedCount++;
                    // Continuing to next employee
                    continue;
                }

                // Retrieving days worked count (Present)
                int daysWorked = DatabaseManager.getDaysWorked(empId, month);
                // Extracting employee base salary
                double baseSalary = employee.getBaseSalary();

                // Formula: Gross Salary = (Base Salary / 30) * Days Worked
                double grossSalary = (baseSalary / 30.0) * daysWorked;
                // Deductions = Gross * 10% tax
                double deductions = grossSalary * 0.10;
                // Net Salary = Gross - Deductions
                double netSalary = grossSalary - deductions;

                // Storing calculated salary in the database
                boolean success = DatabaseManager.addSalary(empId, month, grossSalary, deductions, netSalary, dateToday);
                if (success) {
                    // Incrementing success counter
                    calculatedCount++;
                }
            }

            // Reporting results of calculations
            System.out.println("Salary calculation completed for month: " + month);
            System.out.println("Calculated records: " + calculatedCount);
            System.out.println("Skipped (already calculated): " + skippedCount);
        } catch (IOException e) {
            // General exception logging
            System.err.println("IO Error reading input: " + e.getMessage());
        }
    }

    // Method to view and generate the Employee List Report
    private static void viewEmployeeListReport() {
        // Logging operation initiation
        System.out.println("Generating Employee List Report...");
        // Generating report using the ReportGenerator class
        String path = ReportGenerator.generateEmployeeListReport();
        if (path != null) {
            // Report output path confirmation
            System.out.println("Report successfully saved to file: " + path);
        } else {
            // Reporting failure to generate
            System.out.println("Error: Failed to generate report.");
        }
    }

    // Method to view and generate the Attendance Report for a month
    private static void viewAttendanceReport(BufferedReader reader) {
        try {
            // Prompting for report month
            System.out.print("Enter month (YYYY-MM): ");
            // Reading input
            String month = reader.readLine();
            if (month == null || !MONTH_PATTERN.matcher(month.trim()).matches()) {
                // Validation error output
                System.out.println("Error: Month must be in the format YYYY-MM.");
                // Terminating method
                return;
            }
            // Trimming month input
            month = month.trim();

            // Informing user of generation status
            System.out.println("Generating Attendance Report for " + month + "...");
            // Triggering report generator
            String path = ReportGenerator.generateAttendanceReport(month);
            if (path != null) {
                // Reporting generated file location
                System.out.println("Report successfully saved to file: " + path);
            } else {
                // Reporting generation failure
                System.out.println("Error: Failed to generate report.");
            }
        } catch (IOException e) {
            // General exception logging
            System.err.println("IO Error reading input: " + e.getMessage());
        }
    }

    // Method to view and generate the Payroll Report for a month
    private static void viewPayrollReport(BufferedReader reader) {
        try {
            // Prompting for report month
            System.out.print("Enter month (YYYY-MM): ");
            // Reading input
            String month = reader.readLine();
            if (month == null || !MONTH_PATTERN.matcher(month.trim()).matches()) {
                // Validation error output
                System.out.println("Error: Month must be in the format YYYY-MM.");
                // Terminating method
                return;
            }
            // Trimming month input
            month = month.trim();

            // Informing user of generation status
            System.out.println("Generating Payroll Report for " + month + "...");
            // Running database and file creation logic
            String path = ReportGenerator.generatePayrollReport(month);
            if (path != null) {
                // Report output path confirmation
                System.out.println("Report successfully saved to file: " + path);
            } else {
                // Report generation failure
                System.out.println("Error: Failed to generate report.");
            }
        } catch (IOException e) {
            // General exception logging
            System.err.println("IO Error reading input: " + e.getMessage());
        }
    }
}
