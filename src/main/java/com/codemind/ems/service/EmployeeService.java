package com.codemind.ems.service;

import com.codemind.ems.entity.Employee;
import com.codemind.ems.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Page<Employee> getAllEmployees(int page, int size, String sortBy) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "id";
        }
        return employeeRepository.findAll(PageRequest.of(page, size, Sort.by(sortBy)));
    }

    public Employee updateEmployee(Long id, Employee updatedEmployee) {
        return employeeRepository.findById(id)
                .map(existingEmployee -> {
                    existingEmployee.setName(updatedEmployee.getName());
                    existingEmployee.setEmail(updatedEmployee.getEmail());
                    existingEmployee.setDepartment(updatedEmployee.getDepartment());
                    existingEmployee.setPosition(updatedEmployee.getPosition());
                    existingEmployee.setSalary(updatedEmployee.getSalary());
                    existingEmployee.setDateOfJoining(updatedEmployee.getDateOfJoining());
                    return employeeRepository.save(existingEmployee);
                })
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
    }

    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new RuntimeException("Employee not found with ID: " + id);
        }
        employeeRepository.deleteById(id);
    }
}
