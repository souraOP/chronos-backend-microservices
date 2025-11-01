package com.chronos.attendanceservice.dto.attendance;

import java.time.LocalDate;
import java.util.List;

public record ManagerAttendanceDisplayByDateResponseDTO(
        LocalDate date,
        List<ManagerAttendanceRowDTO> attendanceRows
) {
}
