package com.chronos.common.exception;


import com.chronos.common.constants.ErrorConstants;
import com.chronos.common.exception.custom.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // 404 ERRORS
    @ExceptionHandler({
            ResourceNotFoundException.class,
            EmployeeNotFoundException.class,
            ShiftNotFoundException.class,
            LeaveBalanceNotFoundException.class,
            ActiveAttendanceNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleResourceNotFound(RuntimeException e, WebRequest request) {
        ErrorResponse body = build(HttpStatus.NOT_FOUND,
                ErrorConstants.RESOURCE_NOT_FOUND + ": " + e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DuplicateLeaveBalanceFound.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateLeaveBalanceFound e, WebRequest request) {
        ErrorResponse body = build(HttpStatus.CONFLICT,
                ErrorConstants.DUPLICATE_RESOURCE_FOUND + ": " + e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ErrorResponse> handleLoginFailed(LoginFailedException e, WebRequest request) {
        ErrorResponse body = build(HttpStatus.UNAUTHORIZED,
                "Unauthorized personnel!: " + e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(InvalidLeaveRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidLeaveRequest(InvalidLeaveRequestException e, WebRequest request){
        ErrorResponse body = build(HttpStatus.NOT_ACCEPTABLE,
                ErrorConstants.INVALID_LEAVE_REQUESTS + ": " + e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidationExceptions(MethodArgumentNotValidException e, WebRequest request) {
        String msg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + "=" + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        ErrorResponse body = build(HttpStatus.BAD_REQUEST, "Validation Failed: " + msg, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({
            InvalidUUIDException.class,
            PasswordDoNotMatchException.class,
            ActiveAttendanceExistsException.class,
    })
    public ResponseEntity<ErrorResponse> handleBadRequests(RuntimeException e, WebRequest request) {
        ErrorResponse body = build(HttpStatus.BAD_REQUEST, e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception e, WebRequest request) {
        ErrorResponse body = build(HttpStatus.BAD_REQUEST, "BAD REQUEST: " + e.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private ErrorResponse build(HttpStatus status, String message, WebRequest request) {
        return new ErrorResponse(
                OffsetDateTime.now(),
                status.toString(),
                message,
                request.getDescription(false)
        );
    }
}
