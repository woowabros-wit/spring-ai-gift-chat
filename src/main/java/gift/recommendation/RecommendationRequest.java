package gift.recommendation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecommendationRequest(
    @NotBlank
    @Size(max = 128)
    String sessionId,
    @NotBlank
    @Size(max = 512)
    String message
) {

}
