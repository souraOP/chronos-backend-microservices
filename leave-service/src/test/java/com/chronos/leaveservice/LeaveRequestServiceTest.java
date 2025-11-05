package com.chronos.leaveservice;

import com.chronos.common.constants.enums.LeaveStatus;
import com.chronos.common.constants.enums.LeaveType;
import com.chronos.common.dto.EmployeeDTO;
import com.chronos.common.exception.custom.EmployeeNotFoundException;
import com.chronos.common.exception.custom.InvalidLeaveRequestException;
import com.chronos.common.exception.custom.LeaveBalanceNotFoundException;
import com.chronos.leaveservice.dto.leaveRequests.EmployeeLeaveRequestDashboardResponseDTO;
import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestCreateRequestDTO;
import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestResponseDTO;
import com.chronos.leaveservice.entity.LeaveBalance;
import com.chronos.leaveservice.entity.LeaveRequest;
import com.chronos.leaveservice.feign.EmployeeClient;
import com.chronos.leaveservice.repository.LeaveBalanceRepository;
import com.chronos.leaveservice.repository.LeaveRequestRepository;
import com.chronos.leaveservice.service.impl.LeaveRequestServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaveRequestServiceTest {
    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;

    @Mock
    private EmployeeClient employeeClient;

    @InjectMocks
    private LeaveRequestServiceImpl leaveRequestService;

    @Test
    @DisplayName("createLeaveRequest: succeeds with valid input and sufficient balance")
    void createLeaveRequest_Succeeds() {
        String employeeId = "11111111-1111-1111-1111-111111111111";
        UUID empUuid = UUID.fromString(employeeId);
        LocalDate start = LocalDate.of(2025, 1, 10);
        LocalDate end = LocalDate.of(2025, 1, 10);
        LeaveType leaveType = LeaveType.PERSONAL;

        LeaveRequestCreateRequestDTO request = new LeaveRequestCreateRequestDTO(
                leaveType, start, end, "Just went to family function"
        );

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mock(EmployeeDTO.class));
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(empUuid, leaveType))
                .thenReturn(Optional.of(new LeaveBalance(
                        UUID.randomUUID(), "LB-001", empUuid, leaveType, 10
                )));


        when(leaveRequestRepository.save(any(LeaveRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0, LeaveRequest.class));

        LeaveRequestResponseDTO response = leaveRequestService.createLeaveRequest(employeeId, request);

        assertNotNull(response);
        assertEquals(leaveType, response.leaveType());
        assertEquals(start, response.startDate());
        assertEquals(end, response.endDate());
        assertEquals(LeaveStatus.PENDING, response.status());
        assertNotNull(response.leaveRequestId());
        assertNotNull(response.requestDate());

        verify(employeeClient, times(1)).getEmployeeById(employeeId);
        verify(leaveBalanceRepository, times(1)).findByEmployeeIdAndLeaveType(empUuid, leaveType);
        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
    }


    @Test
    @DisplayName("createLeaveRequest: throws EmployeeNotFoundException when employee client returns null")
    void createLeaveRequest_EmployeeNotFound() {

        String employeeId = "22222222-2222-2222-2222-222222222222";
        LeaveRequestCreateRequestDTO req = new LeaveRequestCreateRequestDTO(
                LeaveType.SICK, LocalDate.now(), LocalDate.now(), "Valid reason text"
        );

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(null);


        assertThrows(EmployeeNotFoundException.class,
                () -> leaveRequestService.createLeaveRequest(employeeId, req));

        verify(employeeClient, times(1)).getEmployeeById(employeeId);
        verifyNoInteractions(leaveBalanceRepository);
        verifyNoInteractions(leaveRequestRepository);
    }

    @Test
    @DisplayName("createLeaveRequest: throws InvalidLeaveRequestException for start date after end date")
    void createLeaveRequest_InvalidDates() {

        String employeeId = "33333333-3333-3333-3333-333333333333";
        LocalDate start = LocalDate.of(2025, 2, 5);
        LocalDate end = LocalDate.of(2025, 2, 1); // invalid

        LeaveRequestCreateRequestDTO req = new LeaveRequestCreateRequestDTO(
                LeaveType.SICK, start, end, "Some valid reason for leave"
        );

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mock(EmployeeDTO.class));


        assertThrows(InvalidLeaveRequestException.class,
                () -> leaveRequestService.createLeaveRequest(employeeId, req));

        verify(employeeClient, times(1)).getEmployeeById(employeeId);
        verifyNoInteractions(leaveBalanceRepository);
        verifyNoInteractions(leaveRequestRepository);
    }

    @Test
    @DisplayName("createLeaveRequest: throws LeaveBalanceNotFoundException when no balance present")
    void createLeaveRequest_LeaveBalanceNotFound() {

        String employeeId = "44444444-4444-4444-4444-444444444444";
        UUID empUUID = UUID.fromString(employeeId);
        LocalDate d = LocalDate.of(2025, 3, 3);

        LeaveRequestCreateRequestDTO req = new LeaveRequestCreateRequestDTO(
                LeaveType.SICK, d, d, "One day sick leave"
        );

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mock(EmployeeDTO.class));
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(empUUID, LeaveType.SICK))
                .thenReturn(Optional.empty());


        assertThrows(LeaveBalanceNotFoundException.class,
                () -> leaveRequestService.createLeaveRequest(employeeId, req));

        verify(employeeClient, times(1)).getEmployeeById(employeeId);
        verify(leaveBalanceRepository, times(1))
                .findByEmployeeIdAndLeaveType(empUUID, LeaveType.SICK);
        verifyNoInteractions(leaveRequestRepository);
    }

    @Test
    @DisplayName("createLeaveRequest: throws InvalidLeaveRequestException when balance is insufficient")
    void createLeaveRequest_InsufficientBalance() {

        String employeeId = "55555555-5555-5555-5555-555555555555";
        UUID empUUID = UUID.fromString(employeeId);
        LocalDate d = LocalDate.of(2025, 4, 1);

        LeaveRequestCreateRequestDTO req = new LeaveRequestCreateRequestDTO(
                LeaveType.SICK, d, d, "Need a day off"
        );

        when(employeeClient.getEmployeeById(employeeId)).thenReturn(mock(EmployeeDTO.class));
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(empUUID, LeaveType.SICK))
                .thenReturn(Optional.of(new LeaveBalance(
                        UUID.randomUUID(), "LB-002", empUUID, LeaveType.SICK, 0 // 0 days available
                )));


        assertThrows(InvalidLeaveRequestException.class,
                () -> leaveRequestService.createLeaveRequest(employeeId, req));

        verify(employeeClient, times(1)).getEmployeeById(employeeId);
        verify(leaveBalanceRepository, times(1))
                .findByEmployeeIdAndLeaveType(empUUID, LeaveType.SICK);
        verifyNoInteractions(leaveRequestRepository);
    }

    @Test
    @DisplayName("getLeaveRequestEmployeeDashboard: returns list and uses parsed UUID")
    void getLeaveRequestEmployeeDashboard_Succeeds() {

        String employeeId = "66666666-6666-6666-6666-666666666666";
        UUID expectedUUID = UUID.fromString(employeeId);

        LocalDate s = LocalDate.of(2025, 5, 1);
        LocalDate e = LocalDate.of(2025, 5, 2);

        List<EmployeeLeaveRequestDashboardResponseDTO> repoResult = List.of(
                new EmployeeLeaveRequestDashboardResponseDTO(
                        "LR-XYZ", LeaveType.SICK, s, e, LeaveStatus.APPROVED
                )
        );

        when(leaveRequestRepository.leaveRequestEmployeeDashboard(expectedUUID))
                .thenReturn(repoResult);


        List<EmployeeLeaveRequestDashboardResponseDTO> out =
                leaveRequestService.getLeaveRequestEmployeeDashboard(employeeId);


        assertNotNull(out);
        assertEquals(1, out.size());
        assertEquals("LR-XYZ", out.get(0).leaveRequestId());
        assertEquals(LeaveType.SICK, out.get(0).leaveType());
        assertEquals(LeaveStatus.APPROVED, out.get(0).status());

        ArgumentCaptor<UUID> uuidCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(leaveRequestRepository, times(1))
                .leaveRequestEmployeeDashboard(uuidCaptor.capture());
        assertEquals(expectedUUID, uuidCaptor.getValue());
    }
}
