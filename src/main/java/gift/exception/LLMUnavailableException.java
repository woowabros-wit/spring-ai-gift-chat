package gift.exception;

public final class LLMUnavailableException extends ChatException {
    public LLMUnavailableException(String message) {
        super(message);
    }
}
