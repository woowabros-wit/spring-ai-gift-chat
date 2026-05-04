package gift.dto;

public record GiftRes(
        String requestId,
        String message,
        long durationMs
) {
}
