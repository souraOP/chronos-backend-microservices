package com.chronos.leaveservice.util.mapper;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.chronos.common.constants.enums.LeaveStatus;
import com.chronos.common.util.NanoIdGenerator;
import com.chronos.leaveservice.dto.EmployeeDTO;
import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestCreateDTO;
import com.chronos.leaveservice.dto.leaveRequests.LeaveRequestResponseDTO;
import com.chronos.leaveservice.dto.leaveRequests.ManagerLeaveRequestDTO;
import com.chronos.leaveservice.entity.LeaveRequest;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.chronos.leaveservice.util.CalculateLeaveRequestDays.getLeaveRequestDays;


public class LeaveRequestMapper {

    public static LeaveRequest leaveRequestDtoToEntity(LeaveRequestCreateDTO leaveRequestCreateDTO, UUID employeeId) {
        LeaveRequest leaveRequestEntity = new LeaveRequest();

        int leaveRequestIdLength = 10;

        String nanoId = NanoIdUtils.randomNanoId(
                NanoIdGenerator.DEFAULT_NUMBER_GENERATOR,
                NanoIdGenerator.DEFAULT_ALPHABET,
                leaveRequestIdLength
        );

        leaveRequestEntity.setLeaveRequestId("LR-" + nanoId);
        leaveRequestEntity.setEmployeeId(employeeId);
        leaveRequestEntity.setLeaveType(leaveRequestCreateDTO.leaveType());
        leaveRequestEntity.setStartDate(leaveRequestCreateDTO.startDate());
        leaveRequestEntity.setEndDate(leaveRequestCreateDTO.endDate());
        leaveRequestEntity.setReason(leaveRequestCreateDTO.reason());
        leaveRequestEntity.setLeaveStatus(LeaveStatus.PENDING);
        leaveRequestEntity.setRequestDate(OffsetDateTime.now());

        return leaveRequestEntity;
    }


    public static ManagerLeaveRequestDTO leaveRequestManagerEntityToDto(LeaveRequest lr, EmployeeDTO emp) {
        return new ManagerLeaveRequestDTO(
                lr.getId(),
                lr.getEmployeeId(),
                emp.displayEmployeeId(),
                emp.firstName(),
                emp.lastName(),
                lr.getLeaveType(),
                lr.getStartDate(),
                lr.getEndDate(),
                getLeaveRequestDays(lr),
                lr.getLeaveStatus(),
                lr.getReason()
        );
    }

    public static LeaveRequestResponseDTO leaveRequestEntityToResponse(LeaveRequest lr) {
        return new LeaveRequestResponseDTO(
                lr.getLeaveRequestId(),
                lr.getLeaveType(),
                lr.getStartDate(),
                lr.getEndDate(),
                getLeaveRequestDays(lr),
                lr.getLeaveStatus(),
                lr.getRequestDate(),
                lr.getReason()
        );
    }
}
