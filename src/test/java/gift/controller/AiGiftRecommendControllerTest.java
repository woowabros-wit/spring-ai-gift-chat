package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.service.AiGiftRecommendService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiGiftRecommendController.class)
class AiGiftRecommendControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AiGiftRecommendService service;

    @Test
    void chat_endpoint_returns_service_response() throws Exception {
        // given
        AiGiftRecommendRequest request = new AiGiftRecommendRequest("친구 생일 선물 추천해줘", "testSessionId");
        AiGiftRecommendResponse response = new AiGiftRecommendResponse("testRequestId", "AI 추천 결과 메세지", "testSessionId", 1500L);
        given(service.recommend(any(AiGiftRecommendRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value("testRequestId"))
                .andExpect(jsonPath("$.message").value("AI 추천 결과 메세지"))
                .andExpect(jsonPath("$.sessionId").value("testSessionId"))
                .andExpect(jsonPath("$.durationMs").value(1500));
    }
}
