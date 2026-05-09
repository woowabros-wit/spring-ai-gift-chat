package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.controller.dto.PresentRequest;
import gift.controller.dto.PresentResponse;
import gift.handler.GlobalExceptionHandler;
import gift.service.PresentChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PresentController.class)
@Import(GlobalExceptionHandler.class)
class PresentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PresentChatService service;

    @Test
    void 선물_추천_요청_성공() throws Exception {
        var request = new PresentRequest("친구 생일 선물 추천해줘", "session-1");
        when(service.chat(any())).thenReturn(new PresentResponse("레고 세트를 추천합니다.", "session-1"));

        mockMvc.perform(post("/present")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("레고 세트를 추천합니다."))
            .andExpect(jsonPath("$.sessionId").value("session-1"));
    }

    @Test
    void 빈_메시지_요청_시_에러_응답_반환() throws Exception {
        var request = new PresentRequest("", "session-3");

        mockMvc.perform(post("/present")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void 공백_메시지_요청_시_에러_응답_반환() throws Exception {
        var request = new PresentRequest("         ", "session-3");

        mockMvc.perform(post("/present")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void LLM_호출_오류_시_에러_응답_반환() throws Exception {
        var request = new PresentRequest("선물 추천해줘", "session-2");
        when(service.chat(any())).thenThrow(new IllegalStateException("선물 추천을 생성하는 중 오류가 발생하였습니다. 다시 시도해주세요."));

        mockMvc.perform(post("/present")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isInternalServerError());
    }
}
