package com.payroll.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import com.payroll.model.Employee;
import com.payroll.model.Attendance;
import com.payroll.model.Salary;

// DatabaseManager class handles all SQLite database connections and operations
public class DatabaseManager {
    // Database file path URL for SQLite JDBC
    private static final String DATABASE_URL = "jdbc:sqlite:payroll.db";

    // Static initializer block to load the SQLite JDBC driver class
    static {
        try {
            // Loading the SQLite JDBC driver class dynamically
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            // Printing stack trace if driver class is not found
            e.printStackTrace();
        }
    }

    // Method to establish and return a database connection
    public static Connection getConnection() throws SQLException {
        // Establishing connection to SQLite database and returning it
        return DriverManager.getConnection(DATABASE_URL);
    }

    // Method to initialize the database tables if they do not exist
    public static void initializeDatabase() {
        // Connection object reference
        Connection connection = null;
        // Statement object reference
        Statement statement = null;
        try {
            // Establishing connection
            connection = getConnection();
            // Creating a Statement to execute SQL queries
            statement = connection.createStatement();
            
            // Defining SQL query to create employees table
            String createEmployeesTable = "CREATE TABLE IF NOT EXISTS employees ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT, "
                    + "baseSalary REAL, "
                    + "department TEXT, "
                    + "joinDate TEXT"
                    + ");";
            // Executing the employees table creation statement
            statement.execute(createEmployeesTable);

            // Defining SQL query to create attendance table
            String createAttendanceTable = "CREATE TABLE IF NOT EXISTS attendance ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "employeeId INTEGER, "
                    + "date TEXT, "
                    + "status TEXT, "
                    + "FOREIGN KEY(employeeId) REFERENCES employees(id)"
                    + ");";
            // Executing the attendance table creation statement
            statement.execute(createAttendanceTable);

            // Defining SQL query to create salaries table
            String createSalariesTable = "CREATE TABLE IF NOT EXISTS salaries ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "employeeId INTEGER, "
                    + "month TEXT, "
                    + "grossSalary REAL, "
                    + "deductions REAL, "
                    + "netSalary REAL, "
                    + "dateGenerated TEXT, "
                    + "FOREIGN KEY(employeeId) REFERENCES employees(id)"
                    + ");";
            // Executing the salaries table creation statement
            statement.execute(createSalariesTable);

        } catch (SQLException e) {
            // Printing error messages to the console if database initialization fails
            System.err.println("Database initialization error: " + e.getMessage());
        } finally {
            // Closing statement resource
            if (statement != null) {
                try {
                    // Invoking close on statement
                    statement.close();
                } catch (SQLException e) {
                    // Printing close exception
                    e.printStackTrace();
                }
            }
            // Closing connection resource
            if (connection != null) {
                try {
                    // Invoking close on connection
                    connection.close();
                } catch (SQLException e) {
                    // Printing close exception
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to add a new employee into the database
    public static boolean addEmployee(String name, double baseSalary, String department, String joinDate) {
        // Checking if name is null or empty
        if (name == null || name.trim().isEmpty()) {
            // Returning false as validation failed
            return false;
        }
        // Checking if baseSalary is non-positive
        if (baseSalary <= 0) {
            // Returning false as validation failed
            return false;
        }
        // Checking if department is null or empty
        if (department == null || department.trim().isEmpty()) {
            // Returning false as validation failed
            return false;
        }
        // Connection object reference
        Connection connection = null;
        // PreparedStatement object reference
        PreparedStatement preparedStatement = null;
        try {
            // Establishing connection
            connection = getConnection();
            // Preparing SQL insert query for employees table
            String insertSql = "INSERT INTO employees (name, baseSalary, department, joinDate) VALUES (?, ?, ?, ?);";
            // Instantiating the PreparedStatement with parameters
            preparedStatement = connection.prepareStatement(insertSql);
            // Binding name parameter
            preparedStatement.setString(1, name);
            // Binding baseSalary parameter
            preparedStatement.setDouble(2, baseSalary);
            // Binding department parameter
            preparedStatement.setString(3, department);
            // Binding joinDate parameter
            preparedStatement.setString(4, joinDate);
            // Executing insert and getting rows affected count
            int rowsAffected = preparedStatement.executeUpdate();
            // Returning true if insertion was successful
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Printing SQL database exception
            System.err.println("Error adding employee: " + e.getMessage());
            // Returning false on exception
            return false;
        } finally {
            // Cleaning up PreparedStatement
            if (preparedStatement != null) {
                try {
                    // Closing statement
                    preparedStatement.close();
                } catch (SQLException e) {
                    // Printing close exception
                    e.printStackTrace();
                }
            }
            // Cleaning up connection
            if (connection != null) {
                try {
                    // Closing connection
                    connection.close();
                } catch (SQLException e) {
                    // Printing close exception
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to verify if an employee exists in database by ID
    public static boolean isEmployeeExists(int employeeId) {
        // Connection reference
        Connection connection = null;
        // PreparedStatement reference
        PreparedStatement preparedStatement = null;
        // ResultSet reference
        ResultSet resultSet = null;
        try {
            // Getting connection
            connection = getConnection();
            // Preparing SELECT SQL query to count matches
            String selectSql = "SELECT COUNT(*) FROM employees WHERE id = ?;";
            // Instantiating statement
            preparedStatement = connection.prepareStatement(selectSql);
            // Binding the employeeId
            preparedStatement.setInt(1, employeeId);
            // Executing query
            resultSet = preparedStatement.executeQuery();
            // Checking if result exists
            if (resultSet.next()) {
                // Getting row count
                int count = resultSet.getInt(1);
                // Returning true if count is greater than zero
                return count > 0;
            }
            // Returning false if no records
            return false;
        } catch (SQLException e) {
            // Logging query exception
            System.err.println("Error checking employee existence: " + e.getMessage());
            // Returning false
            return false;
        } finally {
            // Closing result set
            if (resultSet != null) {
                try {
                    // Closing resource
                    resultSet.close();
                } catch (SQLException e) {
                    // Printing close exception
                    e.printStackTrace();
                }
            }
            // Closing prepared statement
            if (preparedStatement != null) {
                try {
                    // Closing resource
                    preparedStatement.close();
                } catch (SQLException e) {
                    // Printing close exception
                    e.printStackTrace();
                }
            }
            // Closing database connection
            if (connection != null) {
                try {
                    // Closing resource
                    connection.close();
                } catch (SQLException e) {
                    // Printing close exception
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to check if attendance entry already exists for employee and date
    public static boolean isAttendanceDuplicate(int employeeId, String date) {
        // Connection reference
        Connection connection = null;
        // PreparedStatement reference
        PreparedStatement preparedStatement = null;
        // ResultSet reference
        ResultSet resultSet = null;
        try {
            // Connecting to database
            connection = getConnection();
            // SQL query to verify existence of entry
            String selectSql = "SELECT COUNT(*) FROM attendance WHERE employeeId = ? AND date = ?;";
            // Compiling statement
            preparedStatement = connection.prepareStatement(selectSql);
            // Binding parameters
            preparedStatement.setInt(1, employeeId);
            // Binding date parameter
            preparedStatement.setString(2, date);
            // Executing query
            resultSet = preparedStatement.executeQuery();
            // Extracting query results
            if (resultSet.next()) {
                // Reading the counts
                int count = resultSet.getInt(1);
                // Returning true if a record exists
                return count > 0;
            }
            // Default return
            return false;
        } catch (SQLException e) {
            // Handling exception
            System.err.println("Error checking duplicate attendance: " + e.getMessage());
            // Safe return
            return false;
        } finally {
            // Resources clean up
            if (resultSet != null) {
                try {
                    // Closing result set
                    resultSet.close();
                } catch (SQLException e) {
                    // Printing close exception
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    // Closing prepared statement
                    preparedStatement.close();
                } catch (SQLException e) {
                    // Printing close exception
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    // Closing database connection
                    connection.close();
                } catch (SQLException e) {
                    // Printing close exception
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to record attendance status in the database
    public static boolean recordAttendance(int employeeId, String date, String status) {
        // Checking database to see if employee exists
        if (!isEmployeeExists(employeeId)) {
            // Reject recording if invalid employee ID
            return false;
        }
        // Checking for duplicate attendance records
        if (isAttendanceDuplicate(employeeId, date)) {
            // Reject duplicate entry
            return false;
        }
        // Validating attendance status string
        if (status == null || (!status.equals("Present") && !status.equals("Absent") && !status.equals("Leave"))) {
            // Reject invalid statuses
            return false;
        }
        // Connection reference
        Connection connection = null;
        // PreparedStatement reference
        PreparedStatement preparedStatement = null;
        try {
            // Fetching database connection
            connection = getConnection();
            // Preparing SQL query
            String insertSql = "INSERT INTO attendance (employeeId, date, status) VALUES (?, ?, ?);";
            // Creating prepared statement
            preparedStatement = connection.prepareStatement(insertSql);
            // Binding employeeId
            preparedStatement.setInt(1, employeeId);
            // Binding date
            preparedStatement.setString(2, date);
            // Binding status
            preparedStatement.setString(3, status);
            // Executing write operation
            int rowsAffected = preparedStatement.executeUpdate();
            // Returning operation status success flag
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Processing SQLException
            System.err.println("Error recording attendance: " + e.getMessage());
            // Return failure state
            return false;
        } finally {
            // Closing statement resource
            if (preparedStatement != null) {
                try {
                    // Closing
                    preparedStatement.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            // Closing connection resource
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

    // Method to count the total days worked ("Present") for an employee in a given month (YYYY-MM)
    public static int getDaysWorked(int employeeId, String month) {
        // Connection reference
        Connection connection = null;
        // PreparedStatement reference
        PreparedStatement preparedStatement = null;
        // ResultSet reference
        ResultSet resultSet = null;
        // Local variable to store present days count
        int daysWorked = 0;
        try {
            // Getting connection
            connection = getConnection();
            // Querying count of present statuses for a month
            String querySql = "SELECT COUNT(*) FROM attendance WHERE employeeId = ? AND status = 'Present' AND date LIKE ?;";
            // Compiling prepared statement
            preparedStatement = connection.prepareStatement(querySql);
            // Binding employee id
            preparedStatement.setInt(1, employeeId);
            // Binding pattern for month matching (YYYY-MM%)
            preparedStatement.setString(2, month + "%");
            // Running database query
            resultSet = preparedStatement.executeQuery();
            // Parsing results
            if (resultSet.next()) {
                // Reading count from query results
                daysWorked = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            // Exception handler logging
            System.err.println("Error counting days worked: " + e.getMessage());
        } finally {
            // Cleanup resultSet
            if (resultSet != null) {
                try {
                    // Closing
                    resultSet.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            // Cleanup preparedStatement
            if (preparedStatement != null) {
                try {
                    // Closing
                    preparedStatement.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            // Cleanup connection
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
        // Returning count
        return daysWorked;
    }

    // Method to insert a new calculated salary record into database
    public static boolean addSalary(int employeeId, String month, double grossSalary, double deductions, double netSalary, String dateGenerated) {
        // Connection reference
        Connection connection = null;
        // PreparedStatement reference
        PreparedStatement preparedStatement = null;
        try {
            // Getting database connection
            connection = getConnection();
            // SQL statement to insert values
            String insertSql = "INSERT INTO salaries (employeeId, month, grossSalary, deductions, netSalary, dateGenerated) VALUES (?, ?, ?, ?, ?, ?);";
            // Creating statement object
            preparedStatement = connection.prepareStatement(insertSql);
            // Binding employee id
            preparedStatement.setInt(1, employeeId);
            // Binding month
            preparedStatement.setString(2, month);
            // Binding gross salary
            preparedStatement.setDouble(3, grossSalary);
            // Binding deductions
            preparedStatement.setDouble(4, deductions);
            // Binding net salary
            preparedStatement.setDouble(5, netSalary);
            // Binding generation date
            preparedStatement.setString(6, dateGenerated);
            // Executing the write
            int rowsAffected = preparedStatement.executeUpdate();
            // Returning verification value
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Writing details of the execution error
            System.err.println("Error adding salary record: " + e.getMessage());
            // Safe return
            return false;
        } finally {
            // Clean resources
            if (preparedStatement != null) {
                try {
                    // Closing
                    preparedStatement.close();
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

    // Method to check if salary calculation already exists for employee and month
    public static boolean isSalaryCalculated(int employeeId, String month) {
        // Connection reference
        Connection connection = null;
        // PreparedStatement reference
        PreparedStatement preparedStatement = null;
        // ResultSet reference
        ResultSet resultSet = null;
        try {
            // Connecting
            connection = getConnection();
            // Querying count of records matching conditions
            String querySql = "SELECT COUNT(*) FROM salaries WHERE employeeId = ? AND month = ?;";
            // Pre-compiling query
            preparedStatement = connection.prepareStatement(querySql);
            // Binding employeeId
            preparedStatement.setInt(1, employeeId);
            // Binding month
            preparedStatement.setString(2, month);
            // Reading query results
            resultSet = preparedStatement.executeQuery();
            // Verifying status
            if (resultSet.next()) {
                // Return query matching counts boolean state
                return resultSet.getInt(1) > 0;
            }
            // Return default
            return false;
        } catch (SQLException e) {
            // Log exceptions
            System.err.println("Error checking salary calculation state: " + e.getMessage());
            // Return safe default
            return false;
        } finally {
            // Cleaning up resources
            if (resultSet != null) {
                try {
                    // Closing
                    resultSet.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            if (preparedStatement != null) {
                try {
                    // Closing
                    preparedStatement.close();
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

    // Method to retrieve all employee records as a raw list (no generics to comply with rules)
    public static java.util.ArrayList getAllEmployees() {
        // Raw ArrayList instantiation to avoid generic types
        java.util.ArrayList employeeList = new java.util.ArrayList();
        // Connection reference
        Connection connection = null;
        // Statement reference
        Statement statement = null;
        // ResultSet reference
        ResultSet resultSet = null;
        try {
            // Connecting
            connection = getConnection();
            // Creating standard statement
            statement = connection.createStatement();
            // Formulating SELECT query
            String selectSql = "SELECT id, name, baseSalary, department, joinDate FROM employees;";
            // Executing query
            resultSet = statement.executeQuery(selectSql);
            // Parsing results list
            while (resultSet.next()) {
                // Reading employee ID
                int id = resultSet.getInt("id");
                // Reading name
                String name = resultSet.getString("name");
                // Reading baseSalary
                double baseSalary = resultSet.getDouble("baseSalary");
                // Reading department
                String department = resultSet.getString("department");
                // Reading joinDate
                String joinDate = resultSet.getString("joinDate");
                // Creating new Employee object POJO
                Employee employee = new Employee(id, name, baseSalary, department, joinDate);
                // Inserting record into list
                employeeList.add(employee);
            }
        } catch (SQLException e) {
            // Printing error messages
            System.err.println("Error loading employee records: " + e.getMessage());
        } finally {
            // Finalizer resource cleanup
            if (resultSet != null) {
                try {
                    // Closing
                    resultSet.close();
                } catch (SQLException e) {
                    // Print
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    // Closing
                    statement.close();
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
        // Returning employees
        return employeeList;
    }
}
