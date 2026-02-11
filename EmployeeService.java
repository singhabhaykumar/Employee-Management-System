package com.employee;

import com.employee.database.DBConnection;
import com.employee.exception.DuplicateEmployeeException;
import com.employee.exception.EmployeeNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {

	public void addEmployee(Employee employee) throws SQLException, DuplicateEmployeeException {
	    try (Connection connection = DBConnection.getConnection()) {
	        String checkQuery = "SELECT COUNT(*) FROM empdata WHERE name = ? AND dept = ?";
	        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
	            checkStmt.setString(1, employee.getName());
	            checkStmt.setString(2, employee.getDepartment());
	            ResultSet resultSet = checkStmt.executeQuery();
	            if (resultSet.next() && resultSet.getInt(1) > 0) {
	                throw new DuplicateEmployeeException("Employee with the same name and department already exists.");
	            }
	        }

	        // Insert the employee into the table, without specifying the ID
	        String insertQuery = "INSERT INTO empdata (name, salary, dept) VALUES (?, ?, ?)";
	        try (PreparedStatement stmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
	            stmt.setString(1, employee.getName());
	            stmt.setDouble(2, employee.getSalary());
	            stmt.setString(3, employee.getDepartment());
	            stmt.executeUpdate();

	            // Retrieve the generated ID
	            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	                if (generatedKeys.next()) {
	                    int generatedId = generatedKeys.getInt(1);
	                    System.out.println("Employee added successfully with ID: " + generatedId);
	                } else {
	                    throw new SQLException("Failed to retrieve auto-generated ID.");
	                }
	            }
	        }
	    }
	}

    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employeeList = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM empdata";
            try (Statement stmt = connection.createStatement();
                 ResultSet resultSet = stmt.executeQuery(query)) {
                while (resultSet.next()) {
                    Employee employee = new Employee(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getDouble("salary"),
                            resultSet.getString("dept")
                    );
                    employeeList.add(employee);
                }
            }
        }
        return employeeList;
    }

    public Employee getEmployeeById(int id) throws SQLException, EmployeeNotFoundException {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM empdata WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                try (ResultSet resultSet = stmt.executeQuery()) {
                    if (resultSet.next()) {
                        return new Employee(
                                resultSet.getInt("id"),
                                resultSet.getString("name"),
                                resultSet.getDouble("salary"),
                                resultSet.getString("dept")
                        );
                    } else {
                        throw new EmployeeNotFoundException("Employee with ID " + id + " not found.");
                    }
                }
            }
        }
    }

    public void deleteEmployee(int id) throws SQLException, EmployeeNotFoundException {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "DELETE FROM empdata WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, id);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new EmployeeNotFoundException("Employee with ID " + id + " not found.");
                }
                System.out.println("Employee deleted successfully.");
            }
        }
    }

    public void updateEmployee(Employee employee) throws SQLException, EmployeeNotFoundException {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "UPDATE empdata SET name = ?, salary = ?, dept = ? WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, employee.getName());
                stmt.setDouble(2, employee.getSalary());
                stmt.setString(3, employee.getDepartment());
                stmt.setInt(4, employee.getId());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    throw new EmployeeNotFoundException("Employee with ID " + employee.getId() + " not found.");
                }
                System.out.println("Employee updated successfully.");
            }
        }
    }
}
