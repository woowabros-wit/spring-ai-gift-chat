package gift.controller;

public record AiGiftRecommendResponse(
        String requestId,
        String message,
        String sessionId,
        Long durationMs
) {
}
