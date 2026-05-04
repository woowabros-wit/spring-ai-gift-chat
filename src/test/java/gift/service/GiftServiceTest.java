package gift.service;

import gift.dto.GiftReq;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class GiftServiceTest {
    @Mock
    private ChatClient chatClient;
    @Mock
    private ChatClient.Builder builder;
    @Mock
    private ChatClient.ChatClientRequestSpec requestSpec;
    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    private GiftService giftService;

    @BeforeEach
    void setUp() {
        given(builder.build()).willReturn(chatClient);
        given(chatClient.prompt()).willReturn(requestSpec);
        given(requestSpec.system(anyString())).willReturn(requestSpec);
        given(requestSpec.user(anyString())).willReturn(requestSpec);
        given(requestSpec.call()).willReturn(responseSpec);

        giftService = new GiftService(builder);
    }

    @Test
    void 정상적인_채팅() {
        // give
        String message = "친구의 생일 선물을 추천해주세요";
        given(responseSpec.content()).willReturn("친구 생일 선물을 추천해드리겠습니다.");

        // when
        var res = giftService.chat(new GiftReq(message, "sessionId-1"));

        // then
        assertThat(res.requestId()).isNotBlank();
        assertThat(UUID.fromString(res.requestId())).isNotNull();
        assertThat(res.message()).isEqualTo("친구 생일 선물을 추천해드리겠습니다.");
        assertThat(res.durationMs()).isGreaterThanOrEqualTo(0);
    }

    @Test
    void ChatClient_호출_실패() {
        // given
        given(responseSpec.content()).willThrow(new RuntimeException("AI provider error"));

        // when & then
        assertThatThrownBy(() -> giftService.chat(new GiftReq("선물 추천", "sessionId-1")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("선물 추천에 실패했습니다")
                .hasCauseInstanceOf(RuntimeException.class);
    }
}
