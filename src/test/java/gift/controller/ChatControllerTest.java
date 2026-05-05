package gift.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.exception.ChatException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import gift.dto.ChatRequest;
import gift.dto.ChatResponse;
import gift.service.ChatService;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {
    @MockitoBean
    ChatService chatService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void 선물_추천_요청시_응답을_반환한다() throws Exception {
        given(chatService.chat(any(), any())).willReturn(new ChatResponse(UUID.randomUUID(), "message", 10, UUID.randomUUID()));

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ChatRequest("친구한테 선물 추천해주세요", null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.durationMs").isNumber())
                .andExpect(jsonPath("$.requestId").isNotEmpty())
        ;
    }

    @Test
    void message가_없으면_400을_반환한다() throws Exception {
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(new ChatRequest(null, null))))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 선물_추천_요청시_sessionId를_응답에_포함한다() throws Exception {
        UUID sessionId = UUID.randomUUID();
        given(chatService.chat(any(), any()))
                .willReturn(new ChatResponse(UUID.randomUUID(), "message", 10, sessionId));

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatRequest("친구한테 선물 추천해줘", sessionId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").isNotEmpty());
    }

    @Test
    void ChatException_발생시_500을_반환한다() throws Exception {
        given(chatService.chat(any(), any())).willThrow(new ChatException("다시 요청해주세요."));

        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatRequest("test", null))))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value("다시 요청해주세요."));
    }

    @Test
    void message가_빈_문자열이면_400을_반환한다() throws Exception {
        mockMvc.perform(
                post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatRequest("", null)))
        ).andExpect(status().isBadRequest());
    }
}
