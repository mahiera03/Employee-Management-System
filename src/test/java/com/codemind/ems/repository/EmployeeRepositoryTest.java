package com.codemind.ems.repository;

import com.codemind.ems.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee employee1, employee2;

    @BeforeEach
    void setUp() {
        employee1 = new Employee();
        employee1.setName("Mahadev");
        employee1.setEmail("mahadev@gmail.com");
        employee1.setSalary(50000.0);

        employee2 = new Employee();
        employee2.setName("Smith");
        employee2.setEmail("smith@gmail.com");
        employee2.setSalary(60000.0);
    }

    @Test
    void shouldSaveEmployee() {
        Employee savedEmployee = employeeRepository.save(employee1);

        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getId()).isNotNull();
        assertThat(savedEmployee.getName()).isEqualTo("Mahadev");
        assertThat(savedEmployee.getEmail()).isEqualTo("mahadev@gmail.com");
    }

    @Test
    void shouldFindEmployeeById() {
        Employee savedEmployee = employeeRepository.save(employee1);
        Optional<Employee> foundEmployee = employeeRepository.findById(savedEmployee.getId());

        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getName()).isEqualTo("Mahadev");
        assertThat(foundEmployee.get().getEmail()).isEqualTo("mahadev@gmail.com");
    }

    @Test
    void shouldFindAllEmployees() {
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        List<Employee> employees = employeeRepository.findAll();

        assertThat(employees).hasSize(2);
        assertThat(employees).extracting(Employee::getName).containsExactlyInAnyOrder("Mahadev", "Smith");
    }

    @Test
    void shouldDeleteEmployee() {
        Employee savedEmployee = employeeRepository.save(employee1);
        employeeRepository.deleteById(savedEmployee.getId());

        Optional<Employee> deletedEmployee = employeeRepository.findById(savedEmployee.getId());
        assertThat(deletedEmployee).isEmpty();
    }

    @Test
    void shouldUpdateEmployeeSalary() {
        Employee savedEmployee = employeeRepository.save(employee1);
        savedEmployee.setSalary(55000.0);
        Employee updatedEmployee = employeeRepository.save(savedEmployee);

        assertThat(updatedEmployee.getSalary()).isEqualTo(55000.0);
    }
}
