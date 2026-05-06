package gift.client.dto;

public record ChatRequest(
    String message,
    String sessionId
) {

}
