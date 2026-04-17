package gift.dto;

public record PresentResponse (
    boolean success,
    String message,
    String sessionId
){

    public static PresentResponse ok(String message, String sessionId) {
        return new PresentResponse(true, message, sessionId);
    }

    public static PresentResponse error(String message, String sessionId) {
        return new PresentResponse(false, message, sessionId);
    }
}
