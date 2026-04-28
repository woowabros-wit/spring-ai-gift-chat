package gift.service;

import gift.controller.AiGiftRecommendRequest;
import gift.controller.AiGiftRecommendResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AiGiftRecommendServiceTest {

    @InjectMocks
    private AiGiftRecommendService service;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient.Builder chatClientBuilder;
    @Mock
    private Resource aiRoleResource;

    @Test
    @DisplayName("AI 선물 추천 서비스가 ChatClient를 통해 AI로부터 선물 추천을 받아 응답을 반환한다")
    void recommend() {
        String recommendRequestMessage = "친구 생일 선물 추천해줘";
        String sessionId = "testSessionId";
        String aiResponse = "책, 향수, 액세서리를 추천드려요.";

        given(chatClientBuilder.build()
                .prompt()
                .user(recommendRequestMessage)
                .system(aiRoleResource)
                .call()
                .content()).willReturn(aiResponse);

        AiGiftRecommendResponse response = service.recommend(new AiGiftRecommendRequest(recommendRequestMessage, sessionId));

        assertThat(response.message()).isEqualTo(aiResponse);
        assertThat(response.sessionId()).isEqualTo(sessionId);
        assertThat(response.requestId()).isNotBlank();
        assertThat(response.durationMs()).isGreaterThanOrEqualTo(0L);
    }

    @Test
    @DisplayName("LLM 호출 중 오류가 발생하면 사용자에게 오류를 노출하지 않고 안내 메시지를 반환한다")
    void whenLLMFails() {
        String recommendRequestMessage = "친구 생일 선물 추천해줘";
        String sessionId = "testSessionId";

        given(chatClientBuilder.build()
                .prompt()
                .user(recommendRequestMessage)
                .system(aiRoleResource)
                .call()
                .content()).willThrow(new RuntimeException("LLM 호출 실패"));

        AiGiftRecommendResponse response = service.recommend(
                new AiGiftRecommendRequest(recommendRequestMessage, sessionId));

        assertThat(response.message()).isEqualTo(AiGiftRecommendService.FALLBACK_MESSAGE);
        assertThat(response.sessionId()).isEqualTo(sessionId);
        assertThat(response.requestId()).isNotBlank();
        assertThat(response.durationMs()).isGreaterThanOrEqualTo(0L);
    }
}
