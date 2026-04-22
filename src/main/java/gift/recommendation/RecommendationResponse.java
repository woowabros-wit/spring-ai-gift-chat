package gift.recommendation;

public record RecommendationResponse(
    String requestId,
    String message,
    long durationMs
) {

}
