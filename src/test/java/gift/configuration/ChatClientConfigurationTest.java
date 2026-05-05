package gift.configuration;

import gift.client.ClaudeLLMChatClient;
import gift.client.GoogleLLMChatClient;
import gift.client.LLMChatClient;
import gift.prompt.SystemPromptHolder;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

class ChatClientConfigurationTest {
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(ChatClientConfiguration.class)
            .withBean(ChatClient.Builder.class, () -> mock(ChatClient.Builder.class, RETURNS_DEEP_STUBS))
            .withBean(SystemPromptHolder.class);

    @Test
    void google_프로바이더_설정시_GoogleLlmChatClient가_등록된다() {
        contextRunner
                .withPropertyValues("chat.provider=google")
                .run(context -> assertThat(context.getBean(LLMChatClient.class))
                        .isInstanceOf(GoogleLLMChatClient.class));
    }

    @Test
    void claude_프로바이더_설정시_ClaudeLlmChatClient가_등록된다() {
        contextRunner
                .withPropertyValues("chat.provider=claude")
                .run(context -> assertThat(context.getBean(LLMChatClient.class))
                        .isInstanceOf(ClaudeLLMChatClient.class));
    }

    @Test
    void claude_프로바이더_설정시_LlmChatClient_빈이_하나만_등록된다() {
        contextRunner
                .withPropertyValues("chat.provider=claude")
                .run(context -> assertThat(context.getBeansOfType(LLMChatClient.class).size()).isEqualTo(1));
    }

    @Test
    void google_프로바이더_설정시_LlmChatClient_빈이_하나만_등록된다() {
        contextRunner
                .withPropertyValues("chat.provider=google")
                .run(context -> assertThat(context.getBeansOfType(LLMChatClient.class).size()).isEqualTo(1));
    }

    @Test
    void 프로바이더_미설정시_GoogleLlmChatClient가_기본으로_등록된다() {
        contextRunner
                .run(context -> assertThat(context.getBean(LLMChatClient.class))
                        .isInstanceOf(GoogleLLMChatClient.class));
    }
}
