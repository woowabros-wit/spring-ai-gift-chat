package gift.exception;

public final class LLMInvalidResponseException extends ChatException {
    public LLMInvalidResponseException(String message) {
        super(message);
    }
}
