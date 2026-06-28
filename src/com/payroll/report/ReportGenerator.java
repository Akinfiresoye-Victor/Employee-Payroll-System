package com.payroll.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.payroll.database.DatabaseManager;

// ReportGenerator class contains methods to query DB and export text-based reports
public class ReportGenerator {

    // Method to create the reports output directory if it does not exist
    private static void ensureReportsDirectoryExists() {
        // Instantiating a File object representing the reports directory
        File directory = new File("reports");
        // Checking if the directory doesn't exist
        if (!directory.exists()) {
            // Creating directory structure
            directory.mkdirs();
        }
    }

    // Method to generate the Employee List Report as a text file
    public static String generateEmployeeListReport() {
        // Creating the directory if it is not present
        ensureReportsDirectoryExists();
        // File path for the employee list report text file
        String filePath = "reports/employee_list_report.txt";
        // Connection object reference
        Connection connection = null;
        // Statement object reference
        Statement statement = null;
        // ResultSet object reference
        ResultSet resultSet = null;
        // FileWriter object reference
        FileWriter writer = null;
        try {
            // Opening connection
            connection = DatabaseManager.getConnection();
            // Instantiating statement
            statement = connection.createStatement();
            // Querying all employees
            String sql = "SELECT id, name, baseSalary, department, joinDate FROM employees;";
            // Executing query
            resultSet = statement.executeQuery(sql);
            // Opening the file writer for outputting text report
            writer = new FileWriter(filePath);
            
            // Writing headers to text file
            writer.write("=================================================================\n");
            writer.write("                     EMPLOYEE LIST REPORT                        \n");
            writer.write("=================================================================\n");
            writer.write(String.format("%-5s | %-20s | %-12s | %-15s | %-10s\n", "ID", "Name", "Base Salary", "Department", "Join Date"));
            writer.write("-----------------------------------------------------------------\n");
            
            // Printing same headers to standard console
            System.out.println("=================================================================");
            System.out.println("                     EMPLOYEE LIST REPORT                        ");
            System.out.println("=================================================================");
            System.out.printf("%-5s | %-20s | %-12s | %-15s | %-10s\n", "ID", "Name", "Base Salary", "Department", "Join Date");
            System.out.println("-----------------------------------------------------------------");

            // Iterating through query results
            while (resultSet.next()) {
                // Reading id
                int id = resultSet.getInt("id");
                // Reading name
                String name = resultSet.getString("name");
                // Reading baseSalary
                double baseSalary = resultSet.getDouble("baseSalary");
                // Reading department
                String department = resultSet.getString("department");
                // Reading joinDate
                String joinDate = resultSet.getString("joinDate");
                
                // Formatted data row
                String row = String.format("%-5d | %-20s | $%-11.2f | %-15s | %-10s\n", id, name, baseSalary, department, joinDate);
                // Writing row to text file
                writer.write(row);
                // Printing row to standard console
                System.out.print(row);
            }
            
            // Writing footer
            writer.write("=================================================================\n");
            System.out.println("=================================================================");
            
            // Returning the output file path on success
            return filePath;
        } catch (SQLException e) {
            // Printing SQL exceptions
            System.err.println("Database error generating employee list: " + e.getMessage());
            // Return null indicating failure
            return null;
        } catch (IOException e) {
            // Printing IO exception
            System.err.println("File IO error writing employee list: " + e.getMessage());
            // Return null indicating failure
            return null;
        } finally {
            // Resource cleanup
            if (writer != null) {
                try {
                    // Closing file writer
                    writer.close();
                } catch (IOException e) {
                    // Print stack trace
                    e.printStackTrace();
                }
            }
            if (resultSet != null) {
                try {
                    // Closing result set
                    resultSet.close();
                } catch (SQLException e) {
                    // Print stack trace
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    // Closing statement
                    statement.close();
                } catch (SQLException e) {
                    // Print stack trace
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    // Closing connection
                    connection.close();
                } catch (SQLException e) {
                    // Print stack trace
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to generate the Attendance Report for a given month
    public static String generateAttendanceReport(String month) {
        // Ensuring output directory is present
        ensureReportsDirectoryExists();
        // File path for the attendance report containing month in name
        String filePath = "reports/attendance_report_" + month.replace("-", "_") + ".txt";
        // Connection object reference
        Connection connection = null;
        // Statement object reference for employees query
        Statement employeeStatement = null;
        // ResultSet object reference for employees list
        ResultSet employeeResultSet = null;
        // PreparedStatement object reference for counts
        PreparedStatement countStatement = null;
        // ResultSet object reference for attendance counts
        ResultSet countResultSet = null;
        // FileWriter object reference
        FileWriter writer = null;
        try {
            // Connecting
            connection = DatabaseManager.getConnection();
            // Opening statement
            employeeStatement = connection.createStatement();
            // SQL query to retrieve all employees
            String employeeSql = "SELECT id, name FROM employees;";
            // Executing query
            employeeResultSet = employeeStatement.executeQuery(employeeSql);
            // Instantiating file writer
            writer = new FileWriter(filePath);

            // Writing headers to text file
            writer.write("=================================================================\n");
            writer.write("                  ATTENDANCE REPORT FOR " + month + "            \n");
            writer.write("=================================================================\n");
            writer.write(String.format("%-5s | %-20s | %-9s | %-8s | %-7s\n", "ID", "Name", "Present", "Absent", "Leave"));
            writer.write("-----------------------------------------------------------------\n");

            // Printing same headers to console
            System.out.println("=================================================================");
            System.out.println("                  ATTENDANCE REPORT FOR " + month);
            System.out.println("=================================================================");
            System.out.printf("%-5s | %-20s | %-9s | %-8s | %-7s\n", "ID", "Name", "Present", "Absent", "Leave");
            System.out.println("-----------------------------------------------------------------");

            // SQL query to fetch status counts for an employee and month pattern
            String countSql = "SELECT "
                    + "SUM(CASE WHEN status = 'Present' THEN 1 ELSE 0 END) AS presentCount, "
                    + "SUM(CASE WHEN status = 'Absent' THEN 1 ELSE 0 END) AS absentCount, "
                    + "SUM(CASE WHEN status = 'Leave' THEN 1 ELSE 0 END) AS leaveCount "
                    + "FROM attendance WHERE employeeId = ? AND date LIKE ?;";
            
            // Preparing count statement
            countStatement = connection.prepareStatement(countSql);

            // Iterating through all employees
            while (employeeResultSet.next()) {
                // Reading employee ID
                int id = employeeResultSet.getInt("id");
                // Reading employee name
                String name = employeeResultSet.getString("name");

                // Binding employee ID parameter
                countStatement.setInt(1, id);
                // Binding month pattern parameter
                countStatement.setString(2, month + "%");
                // Querying attendance counts
                countResultSet = countStatement.executeQuery();

                // Reading counts
                int present = 0;
                // Reading counts
                int absent = 0;
                // Reading counts
                int leave = 0;
                if (countResultSet.next()) {
                    // Extracting present days
                    present = countResultSet.getInt("presentCount");
                    // Extracting absent days
                    absent = countResultSet.getInt("absentCount");
                    // Extracting leave days
                    leave = countResultSet.getInt("leaveCount");
                }
                
                // Formatting data row
                String row = String.format("%-5d | %-20s | %-9d | %-8d | %-7d\n", id, name, present, absent, leave);
                // Writing to file
                writer.write(row);
                // Printing to console
                System.out.print(row);

                // Closing intermediate result set
                countResultSet.close();
            }

            // Writing footer
            writer.write("=================================================================\n");
            System.out.println("=================================================================");

            // Returning generated file path
            return filePath;
        } catch (SQLException e) {
            // Logging query exceptions
            System.err.println("Database error generating attendance report: " + e.getMessage());
            // Returning null indicating failure
            return null;
        } catch (IOException e) {
            // Logging file write exceptions
            System.err.println("File IO error writing attendance report: " + e.getMessage());
            // Returning null indicating failure
            return null;
        } finally {
            // Finalizing resources cleanup
            if (writer != null) {
                try {
                    // Closing file writer
                    writer.close();
                } catch (IOException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            if (countResultSet != null) {
                try {
                    // Closing
                    countResultSet.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            if (countStatement != null) {
                try {
                    // Closing
                    countStatement.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            if (employeeResultSet != null) {
                try {
                    // Closing
                    employeeResultSet.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            if (employeeStatement != null) {
                try {
                    // Closing
                    employeeStatement.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    // Closing
                    connection.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to generate the Payroll Report for a given month
    public static String generatePayrollReport(String month) {
        // Ensuring output directory is created
        ensureReportsDirectoryExists();
        // File path for payroll report containing month in name
        String filePath = "reports/payroll_report_" + month.replace("-", "_") + ".txt";
        // Connection object reference
        Connection connection = null;
        // PreparedStatement object reference for salaries query
        PreparedStatement preparedStatement = null;
        // ResultSet object reference for salaries list
        ResultSet resultSet = null;
        // FileWriter object reference
        FileWriter writer = null;
        try {
            // Getting database connection
            connection = DatabaseManager.getConnection();
            // SQL query to join salaries and employees
            String sql = "SELECT s.employeeId, e.name, s.grossSalary, s.deductions, s.netSalary, s.dateGenerated "
                    + "FROM salaries s JOIN employees e ON s.employeeId = e.id WHERE s.month = ?;";
            // Compiling statement
            preparedStatement = connection.prepareStatement(sql);
            // Binding month parameter
            preparedStatement.setString(1, month);
            // Executing query
            resultSet = preparedStatement.executeQuery();
            // Creating file writer
            writer = new FileWriter(filePath);

            // Writing headers to text file
            writer.write("=================================================================================\n");
            writer.write("                        PAYROLL REPORT FOR " + month + "                         \n");
            writer.write("=================================================================================\n");
            writer.write(String.format("%-5s | %-20s | %-12s | %-12s | %-12s | %-12s\n", "ID", "Name", "Gross Salary", "Deductions", "Net Salary", "Date Calc"));
            writer.write("---------------------------------------------------------------------------------\n");

            // Printing same headers to standard console
            System.out.println("=================================================================================");
            System.out.println("                        PAYROLL REPORT FOR " + month);
            System.out.println("=================================================================================");
            System.out.printf("%-5s | %-20s | %-12s | %-12s | %-12s | %-12s\n", "ID", "Name", "Gross Salary", "Deductions", "Net Salary", "Date Calc");
            System.out.println("---------------------------------------------------------------------------------");

            // Local boolean helper to trace if we have records
            boolean hasRecords = false;
            // Fetching rows
            while (resultSet.next()) {
                // Tracking record availability
                hasRecords = true;
                // Reading employee ID
                int id = resultSet.getInt("employeeId");
                // Reading employee name
                String name = resultSet.getString("name");
                // Reading gross salary
                double gross = resultSet.getDouble("grossSalary");
                // Reading deductions
                double deductions = resultSet.getDouble("deductions");
                // Reading net salary
                double net = resultSet.getDouble("netSalary");
                // Reading calculated date
                String dateGenerated = resultSet.getString("dateGenerated");

                // Formatted string record row
                String row = String.format("%-5d | %-20s | $%-11.2f | $%-11.2f | $%-11.2f | %-12s\n", id, name, gross, deductions, net, dateGenerated);
                // Writing row to file
                writer.write(row);
                // Printing row to console
                System.out.print(row);
            }

            // Validating record presence
            if (!hasRecords) {
                // Write placeholder details to file
                writer.write("No payroll records found for the month of " + month + ".\n");
                // Print notice to standard console
                System.out.println("No payroll records found for the month of " + month + ".");
            }

            // Writing footer to text file
            writer.write("=================================================================================\n");
            // Printing footer to standard console
            System.out.println("=================================================================================");

            // Return path
            return filePath;
        } catch (SQLException e) {
            // SQL error logging
            System.err.println("Database error generating payroll report: " + e.getMessage());
            // Return null
            return null;
        } catch (IOException e) {
            // File error logging
            System.err.println("File IO error writing payroll report: " + e.getMessage());
            // Return null
            return null;
        } finally {
            // Close resources
            if (writer != null) {
                try {
                    // Close
                    writer.close();
                } catch (IOException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            if (resultSet != null) {
                try {
                    // Close
                    resultSet.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    // Close
                    preparedStatement.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    // Close
                    connection.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
        }
    }
}
