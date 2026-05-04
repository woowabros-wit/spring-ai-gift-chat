package gift.dto;

import jakarta.validation.constraints.NotBlank;

public record GiftReq(
        @NotBlank(message = "message는 필수 값 입니다.")
        String message,

        String sessionId
) {
}
