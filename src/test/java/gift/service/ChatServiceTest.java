package gift.service;

import gift.client.LLMChatClient;
import gift.exception.ChatException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.retry.TransientAiException;
import gift.dto.ChatResponse;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static gift.service.ChatService.ERROR_MESSAGE;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {
    @Mock
    LLMChatClient llmChatClient;

    ChatService chatService;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(llmChatClient);
    }

    @Test
    void LLM_응답을_반환한다() {
        final String message = "test";
        final UUID sessionId = null;
        final String responseMessage = "추천 선물은 ...";

        given(llmChatClient.chat(anyString())).willReturn(responseMessage);

        ChatResponse response = chatService.chat(message, sessionId);

        assertThat(response.message()).isEqualTo(responseMessage);
        assertThat(response.durationMs()).isGreaterThanOrEqualTo(0L);
        assertThat(response.requestId()).isNotNull();
        assertThat(response.sessionId()).isEqualTo(sessionId);
    }

    @Test
    void LLM_호출_실패시_안내_메시지를_반환한다() {
        given(llmChatClient.chat(anyString())).willThrow(new TransientAiException("지연 발생"));

        assertThatThrownBy(() -> chatService.chat("test", null))
                .isInstanceOf(ChatException.class)
                .hasMessage(ERROR_MESSAGE);
    }

    @Test
    void LLM이_null을_반환하면_ChatException을_던진다() {
        given(llmChatClient.chat(anyString())).willReturn(null);

        assertThatThrownBy(() -> chatService.chat("test", null))
                .isInstanceOf(ChatException.class)
                .hasMessage(ERROR_MESSAGE);
    }

}
