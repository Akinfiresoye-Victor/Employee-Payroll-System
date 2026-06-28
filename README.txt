======================================================================
EMPLOYEE PAYROLL SYSTEM
======================================================================

An offline, minimal-class Java application built with SQLite and JDBC for managing employee records, attendance tracking, and payroll calculations.

----------------------------------------------------------------------
FEATURES IMPLEMENTED
----------------------------------------------------------------------
1. Add Employee: Register new employees with ID (auto-incremented), name, base salary, department, and registration date.
2. Record Attendance: Record daily attendance status (Present/Absent/Leave) for a specific date, preventing duplicate records.
3. Calculate Salaries: Process monthly payroll with the following formula:
   - Gross Salary = (Base Salary / 30) * Days Worked (count of "Present" status in the month)
   - Deductions = 10% tax (fixed deduction rate)
   - Net Salary = Gross Salary - Deductions
4. View Employee List Report: View list of all employees and export to a text report file.
5. View Attendance Report: Track total present, absent, and leave days for all employees in a specific month and export to a text report file.
6. View Payroll Report: Summary of gross salary, deductions, and net salary for all employees in a specific month and export to a text report file.

----------------------------------------------------------------------
TEST DATA PROVIDED (AUTO-SEEDED ON FIRST RUN)
----------------------------------------------------------------------
The system automatically creates a `payroll.db` file and seeds the database with the following:
- 3 Employees:
  1. Alice Smith (Base: $3000.00, Dept: Engineering, Joined: 2026-06-01)
  2. Bob Jones (Base: $4500.00, Dept: Marketing, Joined: 2026-06-05)
  3. Charlie Brown (Base: $2500.00, Dept: HR, Joined: 2026-06-10)
- 10 Attendance records across June 2026:
  - Alice Smith: 5 Present records (2026-06-01 to 2026-06-05)
  - Bob Jones: 3 Present records (2026-06-08 to 2026-06-10), 1 Absent record (2026-06-11)
  - Charlie Brown: 1 Leave record (2026-06-12)
- Calculated payroll for Alice Smith (June 2026).

----------------------------------------------------------------------
HOW TO COMPILE AND RUN
----------------------------------------------------------------------
Requirements:
- Java 8 or higher (Java SDK installed and in PATH).
- SQLite JDBC driver and SLF4J library jar files (pre-configured in the lib/ folder).

To Compile (from the project root directory):
   javac -cp "lib/*" -d bin src/com/payroll/model/*.java src/com/payroll/database/*.java src/com/payroll/report/*.java src/com/payroll/Main.java

To Run:
   java -cp "bin;lib/*" com.payroll.Main

----------------------------------------------------------------------
KNOWN LIMITATIONS
----------------------------------------------------------------------
- Calculations assume a standard 30-day month regardless of the actual calendar length of the targeted month.
- Attendance dates must be manually input in YYYY-MM-DD format.
- Output reports are generated in plain text format inside the `reports/` folder.
