package com.codemind.ems.service;

import com.codemind.ems.entity.Employee;
import com.codemind.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setName("Mahadev");
        employee.setEmail("mahadev@gmail.com");
        employee.setDepartment("Engineering");
        employee.setPosition("Software Engineer");
        employee.setSalary(60000.0);
    }

    @Test
    void testSaveEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        Employee savedEmployee = employeeService.saveEmployee(employee);
        assertThat(savedEmployee).isNotNull();
        assertThat(savedEmployee.getName()).isEqualTo("Mahadev");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testGetEmployeeById() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        Optional<Employee> foundEmployee = employeeService.getEmployeeById(1L);
        assertThat(foundEmployee).isPresent();
        assertThat(foundEmployee.get().getName()).isEqualTo("Mahadev");
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllEmployees() {
        List<Employee> employees = Arrays.asList(employee);
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));
        Page<Employee> employeePage = new PageImpl<>(employees, pageable, employees.size());

        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(employeePage);

        Page<Employee> result = employeeService.getAllEmployees(0, 10, "id");

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Mahadev");

        verify(employeeRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void testUpdateEmployee() {
        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("Mahadev Erande");
        updatedEmployee.setEmail("mahadevm24@gmail.com");
        updatedEmployee.setDepartment("HR");
        updatedEmployee.setPosition("Manager");
        updatedEmployee.setSalary(70000.0);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        Employee result = employeeService.updateEmployee(1L, updatedEmployee);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Mahadev Erande");
        assertThat(result.getEmail()).isEqualTo("mahadevm24@gmail.com");

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testDeleteEmployee() {
        when(employeeRepository.existsById(1L)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(1L);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).existsById(1L);
        verify(employeeRepository, times(1)).deleteById(1L);
    }
}
