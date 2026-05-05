package gift.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springaicommunity.claude.agent.sdk.Query;
import org.springaicommunity.claude.agent.sdk.QueryOptions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;

class ClaudeLLMChatClientTest {

    ClaudeLLMChatClient claudeLLMChatClient;
    private static final String SYSTEM_PROMPT = "SYSTEM_PROMPT";

    @BeforeEach
    void setup() {
        claudeLLMChatClient = new ClaudeLLMChatClient(SYSTEM_PROMPT);
    }

    @Test
    void LLM_응답을_반환한다() {
        String userMessage = "친구 생일 선물 추천해줘";
        String expected = "무선 이어폰을 추천합니다.";

        try (MockedStatic<Query> queryMock = mockStatic(Query.class)) {
            queryMock.when(() -> Query.text(eq(userMessage), any(QueryOptions.class)))
                    .thenReturn(expected);

            assertThat(claudeLLMChatClient.chat(userMessage))
                    .isEqualTo(expected);
        }
    }

    @Test
    void LLM이_null을_반환하면_null을_반환한다() {
        try (MockedStatic<Query> queryMock = mockStatic(Query.class)) {
            queryMock.when(() -> Query.text(any(), any(QueryOptions.class)))
                    .thenReturn(null);

            assertThat(claudeLLMChatClient.chat("message")).isNull();
        }
    }

    @Test
    void LLM_호출_실패시_예외를_전파한다() {
        try (MockedStatic<Query> queryMock = mockStatic(Query.class)) {
            queryMock.when(() -> Query.text(any(), any(QueryOptions.class)))
                    .thenThrow(new RuntimeException("CLI 오류"));

            assertThatThrownBy(() -> claudeLLMChatClient.chat("message"))
                    .isInstanceOf(RuntimeException.class);
        }
    }

}
