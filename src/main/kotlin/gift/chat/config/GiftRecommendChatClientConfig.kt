package gift.chat.config

import gift.chat.external.ClaudeCodeGiftRecommendChatClient
import gift.chat.external.GiftRecommendChatClient
import gift.chat.external.SpringAiGiftRecommendChatClient
import org.springframework.ai.chat.client.ChatClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GiftRecommendChatClientConfig {

    @Bean
    @ConditionalOnProperty(name = ["llm.provider"], havingValue = "spring-ai")
    fun springAiGiftRecommendChatClient(chatClientBuilder: ChatClient.Builder): GiftRecommendChatClient {
        return SpringAiGiftRecommendChatClient(chatClientBuilder, GIFT_RECOMMEND_SYSTEM)
    }

    @Bean
    @ConditionalOnProperty(name = ["llm.provider"], havingValue = "claude-code")
    fun claudeCodeGiftRecommendChatClient(): GiftRecommendChatClient {
        return ClaudeCodeGiftRecommendChatClient(GIFT_RECOMMEND_SYSTEM)
    }

    companion object {
        private val GIFT_RECOMMEND_SYSTEM = """
            당신은 선물 추천 전문가입니다.
            사용자가 선물 받을 대상과 상황을 설명하면 적절한 상품을 추천하세요.
            ## 규칙
            - 한국어로 답변
            - 추천은 1개만 제안
            - 선물과 관련 없는 질문에는 정중히 거절하고, 선물 추천으로 대화를 유도
        """.trimIndent()
    }
}
