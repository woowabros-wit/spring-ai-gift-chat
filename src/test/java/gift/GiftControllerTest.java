package gift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GiftControllerTest {

    private MockMvc mockMvc;
    private GiftService spyGiftService;

    @BeforeEach
    void setUp() {
        spyGiftService = mock(GiftService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new GiftController(spyGiftService))
                .build();
    }

    /**
     * 사용자는 자연어로 선물 추천을 요청할 수 있다.
     *  예: "친구 생일 선물 추천해 줘"
     * 서비스는 선물 추천 도우미 역할로 사용자의 메시지에 응답한다.
     * LLM 호출 중 오류가 발생하면 사용자에게 오류를 노출하지 않고, 안내 메시지를 반환한다.
     * 각 요청의 주요 정보(요청 식별자, 사용자 입력, 응답 생성 시간)를 로그로 남긴다.
     */
    @Test
    void gift_returnsOkHttpStatus() throws Exception {
        mockMvc.perform(post("/api/chat"))
                .andExpect(status().isOk());
    }

    @Test
    void gift_callsGift_inGiftService() throws Exception {
        mockMvc.perform(post("/api/chat"));

        verify(spyGiftService).gift(any(), any());
    }

    @Test
    void gift_passMessage_inGiftService() throws Exception {
        mockMvc.perform(post("/api/chat").param("message", "안녕하세요"));

        verify(spyGiftService).gift(eq("안녕하세요"), any());
    }

    @Test
    void gift_passSessionId_inGiftService() throws Exception {
        mockMvc.perform(post("/api/chat")
                .param("message", "안녕하세요")
                .param("sessionId", "sessionid")
        );

        verify(spyGiftService).gift(eq("안녕하세요"), eq("sessionid"));
    }

    @Test
    void gift_returnsGiftResponse_inGiftService() throws Exception {
        given(spyGiftService.gift(any(), any()))
                .willReturn(new GiftResponse("requestId", "message", "durationMs", null));

        mockMvc.perform(post("/api/chat"))
                .andExpect(jsonPath("$.requestId").value("requestId"))
                .andExpect(jsonPath("$.message").value("message"))
                .andExpect(jsonPath("$.durationMs").value("durationMs"));
    }
}