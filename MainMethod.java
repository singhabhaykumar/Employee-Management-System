package com.mainmethod;

import com.employee.Employee;
import com.employee.EmployeeService;
import com.employee.exception.DuplicateEmployeeException;
import com.employee.exception.EmployeeNotFoundException;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MainMethod {
    public static void main(String[] args) {
        EmployeeService service = new EmployeeService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- Employee Management System ---");
            System.out.println("1. Add Employee");
            System.out.println("2. Display All Employees");
            System.out.println("3. Search Employee by ID");
            System.out.println("4. Update Employee");
            System.out.println("5. Delete Employee");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    try {
                        scanner.nextLine(); // Consume newline
                        System.out.print("Enter Employee Name: ");
                        String name = scanner.nextLine();
                        if (!name.matches("[a-zA-Z ]+")) {
                            System.out.println("Error: Employee name must contain only alphabetic characters.");
                            break;
                        }
                        System.out.print("Enter Employee Salary: ");
                        double salary = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline
                        System.out.print("Enter Employee Department: ");
                        String dept = scanner.nextLine();

                        Employee employee = new Employee(0, name, salary, dept); // ID is set to 0, handled by DB
                        service.addEmployee(employee);
                    } catch (DuplicateEmployeeException | SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 2:
                    try {
                        List<Employee> employees = service.getAllEmployees();
                        if (employees.isEmpty()) {
                            System.out.println("No employees found.");
                        } else {
                            for (Employee emp : employees) {
                                System.out.println(emp);
                            }
                        }
                    } catch (SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 3:
                    try {
                        System.out.print("Enter Employee ID to search: ");
                        int searchId = scanner.nextInt();
                        Employee emp = service.getEmployeeById(searchId);
                        System.out.println(emp);
                    } catch (EmployeeNotFoundException | SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 4:
                    try {
                        System.out.print("Enter Employee ID to update: ");
                        int updateId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline
                        System.out.print("Enter New Name: ");
                        String newName = scanner.nextLine();
                        if (!newName.matches("[a-zA-Z ]+")) {
                            System.out.println("Error: Employee name must contain only alphabetic characters.");
                            break;
                        }
                        System.out.print("Enter New Salary: ");
                        double newSalary = scanner.nextDouble();
                        scanner.nextLine(); // Consume newline
                        System.out.print("Enter New Department: ");
                        String newDept = scanner.nextLine();

                        Employee updatedEmployee = new Employee(updateId, newName, newSalary, newDept);
                        service.updateEmployee(updatedEmployee);
                    } catch (EmployeeNotFoundException | SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 5:
                    try {
                        System.out.print("Enter Employee ID to delete: ");
                        int deleteId = scanner.nextInt();
                        service.deleteEmployee(deleteId);
                    } catch (EmployeeNotFoundException | SQLException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;

                case 6:
                    System.out.println("Exiting Employee Management System. Goodbye!");
                    scanner.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
                    break;
            }
        }
    }
}
