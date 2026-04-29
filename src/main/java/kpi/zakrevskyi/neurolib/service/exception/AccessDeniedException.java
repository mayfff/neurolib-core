package kpi.zakrevskyi.neurolib.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Access Denied");
    }

    public AccessDeniedException(String message) {
        super(message);
    }
}
