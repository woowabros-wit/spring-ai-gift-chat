package gift.recommendation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RecommendationService recommendationService;

    @Test
    void return200WithResponse() throws Exception {
        var message = "친구 생일 선물 추천해줘";
        var request = new RecommendationRequest("sessionId", message);
        var response = new RecommendationResponse("requestId", message, 0L);

        when(recommendationService.recommend(request)).thenReturn(response);

        var requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                post("/api/v1/recommendations/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )
            .andExpect(status().isOk())
            .andExpect(content().json(objectMapper.writeValueAsString(response)))
            .andDo(print());
    }

    @Test
    void return400WhenMessageExceedsMaxLength() throws Exception {
        var message = "a".repeat(513);
        var request = new RecommendationRequest("sessionId", message);

        var requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                post("/api/v1/recommendations/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )
            .andExpect(status().isBadRequest())
            .andDo(print());
    }

    @NullAndEmptySource
    @ParameterizedTest
    void return400WhenNullAndEmptyMessage(String message) throws Exception {
        var request = new RecommendationRequest("sessionId", message);

        var requestBody = objectMapper.writeValueAsString(request);
        mockMvc.perform(
                post("/api/v1/recommendations/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
            )
            .andExpect(status().isBadRequest())
            .andDo(print());
    }
}