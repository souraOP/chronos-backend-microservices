package com.chronos.shiftservice.exception;

import com.chronos.shiftservice.constants.ErrorConstants;
import com.chronos.shiftservice.exception.custom.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandling {
    // 404 ERRORS
    @ExceptionHandler({
            ResourceNotFoundException.class,
            EmployeeNotFoundException.class,
            ShiftNotFoundException.class,
            LeaveBalanceNotFoundException.class,
            ActiveAttendanceNotFoundException.class
    })
    public ResponseEntity<?> handleResourceNotFound(RuntimeException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.toString());
        body.put("error", ErrorConstants.RESOURCE_NOT_FOUND);
        body.put("message", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateLeaveBalanceFound.class)
    public ResponseEntity<?> handleDuplicateResource(DuplicateLeaveBalanceFound e) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.CONFLICT.toString());
        body.put("error", ErrorConstants.DUPLICATE_RESOURCE_FOUND);
        body.put("message", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<?> handleLoginFailed(LoginFailedException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.UNAUTHORIZED.toString());
        body.put("error", "Unauthorized personnel!");
        body.put("message", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidLeaveRequestException.class)
    public ResponseEntity<?> handleInvalidLeaveRequest(InvalidLeaveRequestException e){
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.NOT_ACCEPTABLE.toString());
        body.put("error", ErrorConstants.INVALID_LEAVE_REQUESTS);
        body.put("message", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST);
        body.put("error", "Validation Failed");

        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        body.put("message", errors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            InvalidUUIDException.class,
            PasswordDoNotMatchException.class,
            ActiveAttendanceExistsException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleBadRequests(RuntimeException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.toString(),
                e.getMessage(),
                request.getDescription(false)

        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralException(Exception e) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", OffsetDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.toString());
        body.put("error", "BAD REQUEST");
        body.put("message", e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
