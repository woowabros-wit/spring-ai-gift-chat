package gift.chat.external

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.api.Advisor
import java.util.function.Consumer

class SpringAiGiftRecommendChatClientTest : FunSpec({
    isolationMode = IsolationMode.InstancePerTest

    val chatClient = mockk<ChatClient>()
    val chatClientBuilder = mockk<ChatClient.Builder>()

    beforeTest {
        every { chatClientBuilder.defaultSystem(any<String>()) } returns chatClientBuilder
        every { chatClientBuilder.defaultAdvisors(any<Advisor>()) } returns chatClientBuilder
        every { chatClientBuilder.build() } returns chatClient
    }

    val springAiGiftRecommendChatClient by lazy {
        SpringAiGiftRecommendChatClient(chatClientBuilder = chatClientBuilder, systemPrompt = "test system prompt")
    }

    context("call") {
        test("ai 요청의 응답을 내린다") {
            every {
                chatClient.prompt()
                    .user("친구 생일 선물 추천해줘")
                    .advisors(any<Consumer<ChatClient.AdvisorSpec>>())
                    .call()
                    .content()
            } returns "생일 추천 선물은 케이크"

            val actual = springAiGiftRecommendChatClient.call(
                message = "친구 생일 선물 추천해줘",
                sessionId = "550e8400-e29b-41d4-a716-446655440000"
            )
            actual.getOrNull()!!.sessionId shouldBe "550e8400-e29b-41d4-a716-446655440000"
            actual.getOrNull()!!.message shouldBe "생일 추천 선물은 케이크"
        }

        test("sessionId가 null이면 새로운 sessionId를 생성한다") {
            every {
                chatClient.prompt()
                    .user("친구 생일 선물 추천해줘")
                    .advisors(any<Consumer<ChatClient.AdvisorSpec>>())
                    .call()
                    .content()
            } returns "생일 추천 선물은 케이크"

            val actual = springAiGiftRecommendChatClient.call(
                message = "친구 생일 선물 추천해줘",
                sessionId = null
            )
            actual.getOrNull()!!.sessionId shouldNotBe null
            actual.getOrNull()!!.message shouldBe "생일 추천 선물은 케이크"
        }

        test("ai 요청의 응답이 null인 경우 fail Result를 내린다") {
            every {
                chatClient.prompt()
                    .user("친구 생일 선물 추천해줘")
                    .advisors(any<Consumer<ChatClient.AdvisorSpec>>())
                    .call()
                    .content()
            } returns null

            val actual = springAiGiftRecommendChatClient.call(
                message = "친구 생일 선물 추천해줘",
                sessionId = "550e8400-e29b-41d4-a716-446655440000"
            )
            actual.isFailure shouldBe true
            actual.exceptionOrNull()!!.message shouldBe "content is empty"
        }

        test("ai 요청 응답이 에러가 나는 경우 fail Result를 내린다") {
            every {
                chatClient.prompt()
                    .user("친구 생일 선물 추천해줘")
                    .advisors(any<Consumer<ChatClient.AdvisorSpec>>())
                    .call()
                    .content()
            } throws RuntimeException("chat 에러")

            val actual = springAiGiftRecommendChatClient.call(
                message = "친구 생일 선물 추천해줘",
                sessionId = "550e8400-e29b-41d4-a716-446655440000"
            )
            actual.isFailure shouldBe true
            actual.exceptionOrNull()!!.message shouldBe "chat 에러"
        }
    }
})
