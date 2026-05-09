package gift.service;

import gift.client.PresentChatClient;
import gift.client.dto.ChatResponse;
import gift.controller.dto.PresentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PresentChatServiceTest {

    private PresentChatService service;
    private PresentChatClient presentChatClient;

    @BeforeEach
    void setUp() {
        presentChatClient = mock(PresentChatClient.class);
        service = new PresentChatService(presentChatClient);
    }

    @Test
    void 정상_요청_시_성공_응답_반환() {
        when(presentChatClient.chat(any())).thenReturn(new ChatResponse("레고 세트를 추천합니다.", "session-1"));

        var response = service.chat(new PresentRequest("친구 생일 선물 추천해줘", "session-1"));

        assertThat(response.message()).isEqualTo("레고 세트를 추천합니다.");
        assertThat(response.sessionId()).isEqualTo("session-1");
    }

    @Test
    void sessionId가_없으면_자동_생성() {
        when(presentChatClient.chat(any())).thenReturn(new ChatResponse("추천 결과", "auto-generated-session"));

        var response = service.chat(new PresentRequest("선물 추천해줘", null));

        assertThat(response.sessionId()).isNotBlank();
    }

    @Test
    void LLM_호출_실패_시_예외_발생() {
        when(presentChatClient.chat(any()))
            .thenThrow(new IllegalStateException("선물 추천을 생성하는 중 오류가 발생하였습니다. 다시 시도해주세요."));

        assertThatThrownBy(() -> service.chat(new PresentRequest("선물 추천해줘", "session-1")))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("선물 추천을 생성하는 중 오류가 발생하였습니다. 다시 시도해주세요.");
    }
}
