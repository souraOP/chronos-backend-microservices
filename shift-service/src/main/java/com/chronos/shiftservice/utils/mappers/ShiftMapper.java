package com.chronos.shiftservice.utils.mappers;


import com.chronos.shiftservice.dto.shift.CreateNewShiftDTO;
import com.chronos.shiftservice.dto.shift.ShiftResponseDTO;
import com.chronos.shiftservice.entity.Shift;

public class ShiftMapper {
    public static ShiftResponseDTO shiftEntityToDto(Shift shift) {
        return new ShiftResponseDTO(
                shift.getId(),
                shift.getPublicId(),
                shift.getShiftDate(),
                shift.getShiftStartTime(),
                shift.getShiftEndTime(),
                shift.getShiftStatus(),
                shift.getShiftType(),
                shift.getShiftLocation()
        );
    }

    public static CreateNewShiftDTO createShiftEntityToDto(Shift shift) {
        return new CreateNewShiftDTO(
                shift.getEmployeeId(),
                shift.getShiftDate(),
                shift.getShiftStartTime(),
                shift.getShiftEndTime(),
                shift.getShiftStatus(),
                shift.getShiftType(),
                shift.getShiftLocation()
        );
    }

}
