package gift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GiftServiceTest {

    private ChatClient spyChatClient;
    private ChatClient.ChatClientRequestSpec spyPrompt;
    private GiftService sut;

    @BeforeEach
    void setUp() {
        ChatClient.CallResponseSpec spyResponseSpec = mock(ChatClient.CallResponseSpec.class);

        ChatClient.ChatClientRequestSpec spySystem = mock(ChatClient.ChatClientRequestSpec.class);
        given(spySystem.user(anyString())).willReturn(spySystem);
        given(spySystem.call()).willReturn(spyResponseSpec);


        spyPrompt = mock(ChatClient.ChatClientRequestSpec.class);
        given(spyPrompt.system(anyString())).willReturn(spySystem);

        spyChatClient = mock(ChatClient.class);
        given(spyChatClient.prompt()).willReturn(spyPrompt);

        ChatClient.Builder mockChatClientBuilder = mock(ChatClient.Builder.class);
        given(mockChatClientBuilder.build())
                .willReturn(spyChatClient);

        sut = new GiftService(mockChatClientBuilder);
    }

    @Test
    void gift_callsPrompt_inChatClient() throws Exception {
        sut.gift("", "");

        verify(spyChatClient).prompt();
    }

    @Test
    void gift_passSystemPrompt_inChatClient() throws Exception {
        sut.gift("", "");

        verify(spyChatClient.prompt()).system("당신은 선물 추천 도우미에요.");
    }

    @Test
    void gift_passMessage_inChatClient() throws Exception {
        sut.gift("친구 생일 선물 추천해 줘", "");

        verify(spyPrompt.system(anyString())).user("친구 생일 선물 추천해 줘");
    }

}