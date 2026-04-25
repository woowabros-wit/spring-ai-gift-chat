package gift.service;

import gift.dto.PresentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PresentChatServiceTest {

    private PresentChatService service;
    private ChatClientRequestSpec requestSpec;
    private CallResponseSpec callResponseSpec;

    @BeforeEach
    void setUp() {
        ChatClient chatClient = mock(ChatClient.class);
        requestSpec = mock(ChatClientRequestSpec.class);
        callResponseSpec = mock(CallResponseSpec.class);

        when(chatClient.prompt()).thenReturn(requestSpec);
        when(requestSpec.user(anyString())).thenReturn(requestSpec);
        when(requestSpec.advisors(any(Consumer.class))).thenReturn(requestSpec);
        when(requestSpec.call()).thenReturn(callResponseSpec);

        service = new PresentChatService(chatClient);
    }

    @Test
    void 정상_요청_시_성공_응답_반환() {
        when(callResponseSpec.content()).thenReturn("레고 세트를 추천합니다.");

        var response = service.chat(new PresentRequest("친구 생일 선물 추천해줘", "session-1"));

        assertThat(response.message()).isEqualTo("레고 세트를 추천합니다.");
        assertThat(response.sessionId()).isEqualTo("session-1");
    }

    @Test
    void sessionId가_없으면_자동_생성() {
        when(callResponseSpec.content()).thenReturn("추천 결과");

        var response = service.chat(new PresentRequest("선물 추천해줘", null));

        assertThat(response.sessionId()).isNotBlank();
    }

    @Test
    void LLM_호출_실패_시_예외_발생() {
        when(requestSpec.call()).thenThrow(new RuntimeException("API 오류"));

        assertThatThrownBy(() -> service.chat(new PresentRequest("선물 추천해줘", "session-1")))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("선물 추천을 생성하는 중 오류가 발생하였습니다. 다시 시도해주세요.");
    }
}
