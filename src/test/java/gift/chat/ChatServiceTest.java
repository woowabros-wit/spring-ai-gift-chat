package gift.chat;

import gift.prompt.PromptLoader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private PromptLoader promptLoader;

    @Mock
    private ChatClient.Builder builder;

    @Mock(answer = RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    private ChatService sut;

    @BeforeEach
    void setUp() {
        when(promptLoader.loadSystemPrompt()).thenReturn("system prompt");
        when(builder.defaultSystem("system prompt")).thenReturn(builder);
        when(builder.build()).thenReturn(chatClient);

        sut = new ChatService(builder, promptLoader);
    }

    @Test
    void chat() {
        var message = "친구 생일 선물 추천해줘";
        var response = "선물 추천 응답";

        when(chatClient.prompt().user(message).call().content()).thenReturn(response);
        var content = sut.chat(message);

        assertThat(content).isEqualTo(response);
    }

    @Test
    void chat_runtimeError() {
        var message = "친구 생일 선물 추천해줘";

        when(chatClient.prompt().user(message).call().content()).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> sut.chat(message))
            .isInstanceOf(ChatClientException.class);
    }
}