package com.chronos.shiftservice.utils.mappers;


import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapQueryResponseDTO;
import com.chronos.shiftservice.dto.shiftSwapRequest.ShiftSwapResponseDTO;
import com.chronos.shiftservice.entity.Shift;
import com.chronos.shiftservice.entity.ShiftSwapRequest;


public class  ShiftSwapMapper {

    private ShiftSwapMapper() {}


    public static ShiftSwapResponseDTO shiftSwapEntityToDto(ShiftSwapRequest shiftSwapRequestEntity, String requesterName, String requestedName, String approvedByName) {
        return new ShiftSwapResponseDTO(
                shiftSwapRequestEntity.getId(),
                shiftSwapRequestEntity.getPublicId(),
                requesterName,
                requestedName,
                shiftSwapRequestEntity.getStatus(),
                shiftInfoHelper(shiftSwapRequestEntity.getOfferingShift()),
                shiftInfoHelper(shiftSwapRequestEntity.getRequestingShift()),
                shiftSwapRequestEntity.getReason(),
                approvedByName,
                shiftSwapRequestEntity.getApprovedDate()
        );
    }

    public static ShiftSwapQueryResponseDTO toQueryDto(ShiftSwapRequest ssr, String requesterName, String requestedName, String approvedByName) {
        return new ShiftSwapQueryResponseDTO(
                ssr.getId(),
                ssr.getPublicId(),
                requesterName,
                requestedName,
                ssr.getStatus(),
                ssr.getOfferingShift().getShiftType() != null ? ssr.getOfferingShift().getShiftType().name() : null,
                ssr.getOfferingShift().getShiftDate(),
                ssr.getOfferingShift().getShiftStartTime(),
                ssr.getOfferingShift().getShiftEndTime(),
                ssr.getOfferingShift().getShiftLocation(),
                ssr.getRequestingShift().getShiftType() != null ? ssr.getRequestingShift().getShiftType().name() : null,
                ssr.getRequestingShift().getShiftDate(),
                ssr.getRequestingShift().getShiftStartTime(),
                ssr.getRequestingShift().getShiftEndTime(),
                ssr.getRequestingShift().getShiftLocation(),
                ssr.getReason(),
                approvedByName,
                ssr.getApprovedDate()
        );
    }

    private static ShiftSwapResponseDTO.ShiftInfo shiftInfoHelper(Shift shift){
        return new ShiftSwapResponseDTO.ShiftInfo(
                shift.getId(),
                shift.getShiftType() != null ? shift.getShiftType().name() : null,
                shift.getShiftDate(),
                shift.getShiftStartTime(),
                shift.getShiftEndTime(),
                shift.getShiftLocation()
        );
    }
}
