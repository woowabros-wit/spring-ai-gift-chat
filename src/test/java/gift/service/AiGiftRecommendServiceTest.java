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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AiGiftRecommendServiceTest {

    @InjectMocks
    private AiGiftRecommendService service;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient.Builder chatClientBuilder;

    @Test
    @DisplayName("AI 선물 추천 서비스가 ChatClient를 통해 AI로부터 선물 추천을 받아 응답을 반환한다")
    void recommend() {
        String recommendRequestMessage = "친구 생일 선물 추천해줘";
        String sessionId = "testSessionId";
        String aiResponse = "책, 향수, 액세서리를 추천드려요.";

        given(chatClientBuilder.build()
                .prompt()
                .user(recommendRequestMessage)
                .call()
                .content()).willReturn(aiResponse);

        AiGiftRecommendResponse response = service.recommend(new AiGiftRecommendRequest(recommendRequestMessage, sessionId));

        assertThat(response.message()).isEqualTo(aiResponse);
        assertThat(response.sessionId()).isEqualTo(sessionId);
        assertThat(response.requestId()).isNotBlank();
        assertThat(response.durationMs()).isGreaterThanOrEqualTo(0L);
    }
}
