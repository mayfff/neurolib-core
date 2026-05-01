package kpi.zakrevskyi.neurolib.controller;

import kpi.zakrevskyi.neurolib.domain.dto.response.ExceptionResponseDto;
import kpi.zakrevskyi.neurolib.service.exception.AccessDeniedException;
import kpi.zakrevskyi.neurolib.service.exception.BadRequestException;
import kpi.zakrevskyi.neurolib.service.exception.ConflictException;
import kpi.zakrevskyi.neurolib.service.exception.NotFoundException;
import kpi.zakrevskyi.neurolib.service.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleNotFound(NotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ExceptionResponseDto> handleConflict(ConflictException ex) {
        return build(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler({BadRequestException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionResponseDto> handleBadRequest(Exception ex) {
        String message = ex instanceof BadRequestException ? ex.getMessage() : "Malformed request body";
        return build(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ExceptionResponseDto> handleUnauthorized(UnauthorizedException ex) {
        return build(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponseDto> handleForbidden(Exception ex) {
        String message = ex.getMessage() == null ? "Access Denied" : ex.getMessage();
        return build(HttpStatus.FORBIDDEN, message);
    }

    private ResponseEntity<ExceptionResponseDto> build(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(new ExceptionResponseDto(status, message));
    }
}
