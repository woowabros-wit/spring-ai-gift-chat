package gift;

public record GiftResponse(
        String requestId,
        String message,
        String durationMs,
        String sessionId
){
}
