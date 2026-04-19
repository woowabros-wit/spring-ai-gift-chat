package gift.chat.controller;

import java.util.UUID;

public record MessageRequestDto(
    UUID requestId,
    String message,
    Long durationMs
) {

}
