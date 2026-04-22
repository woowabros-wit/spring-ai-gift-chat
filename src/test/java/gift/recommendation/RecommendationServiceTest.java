package gift.recommendation;

import gift.chat.ChatClientException;
import gift.chat.ChatService;
import gift.request.RequestIdGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private RequestIdGenerator requestIdGenerator;

    @Mock
    private ChatService chatService;

    @InjectMocks
    private RecommendationService sut;


    @Test
    void recommend() {
        var message = "친구 생일 선물 추천해줘";
        var request = new RecommendationRequest("sessionId", message);
        var response = "선물 추천 응답";

        when(requestIdGenerator.generate()).thenReturn("requestId");
        when(chatService.chat(message)).thenReturn(response);
        RecommendationResponse recommend = sut.recommend(request);

        assertThat(recommend.message()).isEqualTo(response);
    }

    @Test
    void recommend_chatClientError() {
        var message = "친구 생일 선물 추천해줘";
        var request = new RecommendationRequest("sessionId", message);

        when(requestIdGenerator.generate()).thenReturn("requestId");
        when(chatService.chat(message)).thenThrow(new ChatClientException());

        assertThatThrownBy(() -> sut.recommend(request))
            .isInstanceOf(ChatClientException.class);
    }
}