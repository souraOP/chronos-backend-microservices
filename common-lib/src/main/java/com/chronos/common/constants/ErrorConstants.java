package com.chronos.common.constants;

public class ErrorConstants {
    public static final String RESOURCE_NOT_FOUND = "Resource not found!";

    public static final String EMPLOYEE_NOT_FOUND = "Employee not found ";

    public static final String MANAGER_NOT_FOUND = "Manager not found";

    public static final String MANAGER_TEAM_NOT_FOUND = "[NOT_FOUND]: Manager does not have any team";

    public static final String EMP_FETCH_TERMINATED_NOT_FOUND = "[Terminated Fetching Operation]: - Reason :  No such employee found with id: ";

    public static final String EMP_UPDATE_TERMINATED_NOT_FOUND = "[Terminated Update Operation]: - Reason : No such employee found with id: ";

    public static final String EMP_DELETE_TERMINATED_NOT_FOUND = "[Terminated Delete Operation]: - Reason : No such employee found with id: ";

    public static final String EMPLOYEE_LOGIN_FAILED = "[Employee Login Failed]: Invalid email or password!";

    public static final String MANAGER_LOGIN_FAILED = "[Manager Login Failed]: Invalid email or password!";

    public static final String LOGIN_CREDENTIALS_NOT_FOUND = "Login credentials not found for this email: ";

    public static final String NEW_PASSWORD_CONFIRM_PASSWORD_NOT_MATCH = "New Password and Confirm Passwords do not match !";

    public static final String EMPLOYEE_WITH_NO_TEAM = "Employee does not belong to any team";

    public static final String EMPLOYEE_NOT_IN_MANAGER_TEAM = "Employee does not belong to manager's team";
    
    public static final String MANAGER_WITH_NO_TEAM = "No team found for manager: ";

    public static final String LEAVE_BALANCE_ALREADY_EXISTS = "Leave Balance for this type already exists for the employee";

    public static final String LEAVE_BALANCE_NOT_FOUND = "Leave Balance not found";

    public static final String INVALID_LEAVE_REQUESTS = "End date must be after or equal to start date";

    public static final String LEAVE_REQUEST_NOT_FOUND = "Leave request not found";

    public static final String LEAVE_REQUEST_ALREADY_PROCESSED = "Leave Request already processed!";

    public static final String ACTIVE_ATTENDANCE_UNAVAILABLE = "No active attendance record found for employee";

    public static final String SWAP_REQUEST_NOT_FOUND = "Swap Request not found";

    public static final String INVALID_UUID_ERROR = "INVALID UUID FORMAT \n Expected UUID format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx";

    public static final String ALL_IDS_REQUIRED = "All ID's are required";

    public static final String DIFF_REQUESTER_REQUESTED_EMP = "Requester and requested employee should be different";

    public static final String REQUESTER_NOT_FOUND = "Requester employee Not Found";

    public static final String REQUESTED_NOT_FOUND = "Requested employee Not Found";

    public static final String OFFERING_SHIFT_NOT_FOUND = "Offering Shift Not Found";

    public static final String REQUESTING_SHIFT_NOT_FOUND = "Requesting Shift Not Found";

    public static final String INVALID_OFFERING_SHIFT = "Offering Shift does not belong to the requester employee";

    public static final String INVALID_REQUESTING_SHIFT = "Requesting shift does not belong to requested employee";

    public static final String PENDING_REQUEST_HANDLE_ONLY = "Only pending request can be approved or rejected";

    public static final String SHIFT_NOT_FOUND = "No available shifts found for this employee!";

    public static final String STARTED_SHIFT_SWAP_ERROR = "Cannot swap shifts that have already started";

    public static final String DUPLICATE_RESOURCE_FOUND = "Duplicate resource found!";

    public static final String ACTIVE_ATTENDANCE_NOT_FOUND = "No active attendance to clock out";

    public static final String INVALID_DATE_FORMAT = "Invalid date format. Expected yyyy-MM-dd";

    public static final String INVALID_SHIFT_TIMING = "Shift end time cannot be before start time";

    public static final String INSUFFICIENT_LEAVE_BALANCE = "Employee has insufficient leave balance";

    public static final String ALREADY_CHECKED_IN = "Employee already clocked in";

    public static final String CANNOT_SWAP_WITH_MANAGER = "Cannot swap with manager";
}
