// java
package com.chronos.employeeservice;

import com.chronos.common.constants.enums.Gender;
import com.chronos.common.constants.enums.Role;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.employeeservice.dto.employee.EmployeeNameResponseDTO;
import com.chronos.employeeservice.entity.Employee;
import com.chronos.employeeservice.entity.Team;
import com.chronos.employeeservice.repository.EmployeeRepository;
import com.chronos.employeeservice.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    EmployeeServiceImpl service;

    @Test
    void createEmployee_returnsSavedDto() {
        UUID newId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        EmployeeDTO input = new EmployeeDTO(
                null, "EMP-1", "John", "Doe", "john@example.com",
                Gender.MALE, "123", "Dev", true, "Engineering", Role.EMPLOYEE, null
        );

        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> {
            Employee e = inv.getArgument(0);
            e.setId(newId);
            return e;
        });

        EmployeeDTO out = service.createEmployee(input);

        assertNotNull(out);
        assertEquals(newId, out.id());
        assertEquals("EMP-1", out.displayEmployeeId());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void getEmployeeById_returnsProjection() {
        UUID id = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        EmployeeDTO projected = new EmployeeDTO(
                id, "EMP-2", "Mani", "Kumar", "mani@example.com",
                Gender.FEMALE, "555", "QA", true, "QA", Role.EMPLOYEE, "[Not in a team]"
        );

        when(employeeRepository.findEmployeeByID(id)).thenReturn(Optional.of(projected));

        EmployeeDTO out = service.getEmployeeById(id.toString());

        assertEquals(projected, out);
        verify(employeeRepository).findEmployeeByID(id);
    }

    @Test
    void getAllEmployees_mapsEntitiesToDtos() {
        Employee e1 = employee(
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                "EMP-3", "Tom", "A", Gender.MALE, "TEAM-1"
        );
        Employee e2 = employee(
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                "EMP-4", "Jerry", "B", Gender.MALE, "TEAM-2"
        );
        when(employeeRepository.findAll()).thenReturn(List.of(e1, e2));

        List<EmployeeDTO> all = service.getAllEmployees();

        assertEquals(2, all.size());
        assertEquals("TEAM-1", all.get(0).teamId());
        assertEquals("TEAM-2", all.get(1).teamId());
        verify(employeeRepository).findAll();
    }

    @Test
    void updateEmployee_updatesFieldsAndReturnsDto() {
        UUID id = UUID.fromString("33333333-3333-3333-3333-333333333333");
        Employee existing = employee(
                id, "EMP-5", "OldFirst", "OldLast", Gender.MALE, null
        );
        EmployeeDTO updates = new EmployeeDTO(
                id, "EMP-5", "NewFirst", "NewLast", "new@example.com",
                Gender.MALE, "999", "Sr Dev", false, "NewDept", Role.MANAGER, null
        );

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existing));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        EmployeeDTO out = service.updateEmployee(id.toString(), updates);

        assertEquals("NewFirst", out.firstName());
        assertEquals("NewLast", out.lastName());
        assertEquals("new@example.com", out.email());
        assertEquals("999", out.phoneNumber());
        assertEquals("Sr Dev", out.jobTitle());
        assertFalse(out.isActive());
        assertEquals("NewDept", out.departmentName());
        assertEquals(Role.MANAGER, out.role());
        verify(employeeRepository).findById(id);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void patchEmployee_updatesProvidedFieldsOnly() {
        UUID id = UUID.fromString("44444444-4444-4444-4444-444444444444");

        // Base projection used by patchEmployee to compute merged DTO
        EmployeeDTO base = new EmployeeDTO(
                id, "EMP-6", "Dinesh", "Ram", "dinesh@example.com",
                Gender.MALE, "111", "Dev", true, "Engineering", Role.EMPLOYEE, null
        );
        when(employeeRepository.findEmployeeByID(id)).thenReturn(Optional.of(base));

        // updateEmployee path: find entity -> save -> map back
        Employee entity = employee(id, "EMP-6", "Sourasish", "Mondal", Gender.MALE, null);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(entity));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", "Patched");
        updates.put("active", "false");
        updates.put("role", "MANAGER");

        EmployeeDTO out = service.patchEmployee(id.toString(), updates);

        assertEquals("Patched", out.firstName());
        assertFalse(out.isActive());
        assertEquals(Role.MANAGER, out.role());
        verify(employeeRepository).findEmployeeByID(id);
        verify(employeeRepository).findById(id);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_callsRepository() {
        UUID id = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        when(employeeRepository.existsById(id)).thenReturn(true);

        service.deleteEmployee(id.toString());

        verify(employeeRepository).deleteById(id);
    }

    @Test
    void getEmployeeName_returnsFirstAndLast() {
        UUID id = UUID.fromString("55555555-5555-5555-5555-555555555555");

        when(employeeRepository.findById(id)).thenReturn(Optional.of(new Employee()));
        when(employeeRepository.findEmployeeName(id))
                .thenReturn(new EmployeeNameResponseDTO("Sourasish", "Mondal"));

        EmployeeNameResponseDTO name = service.getEmployeeName(id.toString());

        assertEquals("Sourasish", name.firstName());
        assertEquals("Mondal", name.lastName());
        verify(employeeRepository).findById(id);
        verify(employeeRepository).findEmployeeName(id);
    }

    @Test
    void getEmployeeByDisplayId_mapsEntityToDto() {
        Employee emp = employee(
                UUID.fromString("66666666-6666-6666-6666-666666666666"),
                "EMP-7", "Ishika", "Dutta", Gender.MALE, "TEAM-X"
        );
        when(employeeRepository.findByDisplayEmployeeId("EMP-7"))
                .thenReturn(Optional.of(emp));

        EmployeeDTO out = service.getEmployeeByDisplayId("EMP-7");

        assertEquals("EMP-7", out.displayEmployeeId());
        assertEquals("Ishika", out.firstName());
        assertEquals("TEAM-X", out.teamId());
        verify(employeeRepository).findByDisplayEmployeeId("EMP-7");
    }

    // helper
    private static Employee employee(UUID id, String displayId, String first, String last, Gender gender, String teamId) {
        Team t = null;
        if (teamId != null) {
            t = new Team();
            t.setTeamId(teamId);
        }
        Employee e = new Employee();
        e.setId(id);
        e.setDisplayEmployeeId(displayId);
        e.setFirstName(first);
        e.setLastName(last);
        e.setEmail(first.toLowerCase() + "@example.com");
        e.setGender(gender);
        e.setPhoneNumber("000");
        e.setJobTitle("Dev");
        e.setActive(true);
        e.setDepartmentName("Eng");
        e.setRole(Role.EMPLOYEE);
        e.setTeam(t);
        return e;
    }
}
