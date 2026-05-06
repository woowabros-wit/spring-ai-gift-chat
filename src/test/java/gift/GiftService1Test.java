package gift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GiftService1Test {

    private GiftService sut;
    private GiftChatClient spyGiftChatClient;

    @BeforeEach
    void setUp() {
        spyGiftChatClient = mock(GiftChatClient.class);
        sut = new GiftService(spyGiftChatClient);
    }


    @Test
    void gift_callsChat_inChatClient() throws Exception {
        sut.gift("", "");

        verify(spyGiftChatClient).chat(anyString(), anyString());
    }

    @Test
    void gift_passSystemPrompt_inChatClient() throws Exception {
        sut.gift("", "");

        verify(spyGiftChatClient).chat(eq("당신은 선물 추천 도우미에요."), anyString());
    }

    @Test
    void gift_passMessage_inChatClient() throws Exception {
        sut.gift("친구 생일 선물 추천해 줘", "");

        verify(spyGiftChatClient).chat(anyString(), eq("친구 생일 선물 추천해 줘"));
    }


}