package kpi.zakrevskyi.neurolib.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import org.springframework.http.HttpStatus;

public record ExceptionResponseDto(
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    Date timestamp,
    int statusCode,
    String reasonPhrase,
    String message
) {
    public ExceptionResponseDto(HttpStatus httpStatus, String message) {
        this(new Date(System.currentTimeMillis()), httpStatus.value(), httpStatus.getReasonPhrase(), message);
    }
}
