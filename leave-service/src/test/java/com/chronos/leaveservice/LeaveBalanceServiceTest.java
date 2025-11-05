package com.chronos.leaveservice;

import com.chronos.common.constants.enums.LeaveType;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.common.exception.custom.DuplicateLeaveBalanceFound;
import com.chronos.common.exception.custom.EmployeeNotFoundException;
import com.chronos.common.exception.custom.LeaveBalanceNotFoundException;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceDTO;
import com.chronos.leaveservice.dto.leaveBalance.LeaveBalanceResponseDTO;
import com.chronos.leaveservice.entity.LeaveBalance;
import com.chronos.leaveservice.feign.EmployeeClient;
import com.chronos.leaveservice.repository.LeaveBalanceRepository;
import com.chronos.leaveservice.service.impl.LeaveBalanceServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class LeaveBalanceServiceTest {

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private EmployeeClient employeeClient;

    @InjectMocks
    private LeaveBalanceServiceImpl service;

    @Test
    @DisplayName("createLeaveBalance: succeeds and returns mapped DTO")
    void createLeaveBalance_Succeeds() {
        String employeeId = "11111111-1111-1111-1111-111111111111";
        UUID empUUID = UUID.fromString(employeeId);
        LeaveType type = LeaveType.VACATION;
        int balance = 10;

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mock(EmployeeDTO.class));
        when(leaveBalanceRepository.existsByEmployeeIdAndLeaveType(empUUID, type)).thenReturn(false);
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenAnswer(inv -> {
            LeaveBalance lb = inv.getArgument(0, LeaveBalance.class);
            if (lb.getId() == null) lb.setId(UUID.randomUUID());
            return lb;
        });

        LeaveBalanceDTO dto = service.createLeaveBalance(employeeId, type, balance);

        assertNotNull(dto);
        assertNotNull(dto.id());
        assertNotNull(dto.balanceId());
        assertTrue(dto.balanceId().startsWith("LB-"));
        assertEquals(type, dto.leaveType());
        assertEquals(balance, dto.leaveBalance());

        verify(employeeClient).getEmployeeById(employeeId);
        verify(leaveBalanceRepository).existsByEmployeeIdAndLeaveType(empUUID, type);
        verify(leaveBalanceRepository).save(any(LeaveBalance.class));
        verifyNoMoreInteractions(leaveBalanceRepository, employeeClient);
    }

    @Test
    @DisplayName("createLeaveBalance: throws when employee not found")
    void createLeaveBalance_EmployeeNotFound() {
        String employeeId = "22222222-2222-2222-2222-222222222222";

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(null);

        assertThrows(EmployeeNotFoundException.class,
                () -> service.createLeaveBalance(employeeId, LeaveType.VACATION, 5));

        verify(employeeClient).getEmployeeById(employeeId);
        verifyNoMoreInteractions(leaveBalanceRepository, employeeClient);
    }

    @Test
    @DisplayName("createLeaveBalance: throws when duplicate exists")
    void createLeaveBalance_Duplicate() {
        String employeeId = "33333333-3333-3333-3333-333333333333";
        UUID empUUID = UUID.fromString(employeeId);
        LeaveType type = LeaveType.VACATION;

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mock(EmployeeDTO.class));
        when(leaveBalanceRepository.existsByEmployeeIdAndLeaveType(empUUID, type)).thenReturn(true);

        assertThrows(DuplicateLeaveBalanceFound.class,
                () -> service.createLeaveBalance(employeeId, type, 7));

        verify(employeeClient).getEmployeeById(employeeId);
        verify(leaveBalanceRepository).existsByEmployeeIdAndLeaveType(empUUID, type);
        verify(leaveBalanceRepository, never()).save(any());
        verifyNoMoreInteractions(leaveBalanceRepository, employeeClient);
    }

    @Test
    @DisplayName("getLeaveBalancesByEmployeeId: returns list")
    void getLeaveBalancesByEmployeeId_Succeeds() {
        String employeeId = "44444444-4444-4444-4444-444444444444";
        UUID empUUID = UUID.fromString(employeeId);

        List<LeaveBalanceResponseDTO> list = List.of(
                new LeaveBalanceResponseDTO("LB-001", LeaveType.SICK, 5),
                new LeaveBalanceResponseDTO("LB-002", LeaveType.VACATION, 12)
        );
        when(leaveBalanceRepository.findLeaveBalanceViewByEmployeeId(empUUID)).thenReturn(Optional.of(list));

        List<LeaveBalanceResponseDTO> result = service.getLeaveBalancesByEmployeeId(employeeId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("LB-001", result.get(0).balanceId());
        assertEquals(LeaveType.SICK, result.get(0).leaveType());
        assertEquals(12, result.get(1).leaveBalance());

        verify(leaveBalanceRepository).findLeaveBalanceViewByEmployeeId(empUUID);
        verifyNoMoreInteractions(leaveBalanceRepository);
    }

    @Test
    @DisplayName("getLeaveBalancesByEmployeeId: throws when none found")
    void getLeaveBalancesByEmployeeId_NotFound() {
        String employeeId = "55555555-5555-5555-5555-555555555555";
        UUID empUUID = UUID.fromString(employeeId);

        when(leaveBalanceRepository.findLeaveBalanceViewByEmployeeId(empUUID)).thenReturn(Optional.empty());

        assertThrows(LeaveBalanceNotFoundException.class,
                () -> service.getLeaveBalancesByEmployeeId(employeeId));

        verify(leaveBalanceRepository).findLeaveBalanceViewByEmployeeId(empUUID);
        verifyNoMoreInteractions(leaveBalanceRepository);
    }
}
