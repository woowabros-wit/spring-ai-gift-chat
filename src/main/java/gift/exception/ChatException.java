package gift.exception;

public sealed class ChatException extends RuntimeException
        permits LLMUnavailableException, LLMInvalidResponseException {
    public ChatException(String message) {
        super(message);
    }
}
