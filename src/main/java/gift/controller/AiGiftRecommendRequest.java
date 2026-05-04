package gift.controller;

public record AiGiftRecommendRequest(
        String message,
        String sessionId
) {
}
