package gift.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalControllerHandler {

    @ExceptionHandler(ChatException.class)
    public ProblemDetail globalException(ChatException ex) {
        HttpStatus status = switch (ex) {
            case LLMUnavailableException e -> HttpStatus.SERVICE_UNAVAILABLE;
            case LLMInvalidResponseException e -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };

        return ProblemDetail.forStatusAndDetail(status, ex.getMessage());
    }
}
