package gift;

import gift.chat.ChatClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({
        MethodArgumentNotValidException.class,
        IllegalArgumentException.class,
        IllegalStateException.class
    })
    public ErrorResponse handleValidationException(Exception e) {
        log.error("Validation error occurred.", e);
        return ErrorResponse.builder(e, HttpStatus.BAD_REQUEST, "올바르지 않은 정보를 입력했어요.").build();
    }

    @ExceptionHandler(ChatClientException.class)
    public ErrorResponse handleChatClientException(Exception e) {
        log.error("ChatClientException occurred.", e);
        return ErrorResponse.builder(e, HttpStatus.INTERNAL_SERVER_ERROR, "AI에게 요청하는 과정에서 문제가 발생했어요.").build();
    }

    @ExceptionHandler(RuntimeException.class)
    public ErrorResponse handleRuntimeException(Exception e) {
        log.error("Unexpected error occurred.", e);
        return ErrorResponse.builder(e, HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했어요.").build();
    }
}
