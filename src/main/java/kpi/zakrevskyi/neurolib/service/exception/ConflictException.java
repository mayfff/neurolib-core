package kpi.zakrevskyi.neurolib.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {
    public ConflictException(String reason) {
        super(reason);
    }
}
