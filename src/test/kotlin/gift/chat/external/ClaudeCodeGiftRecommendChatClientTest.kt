package gift.chat.external

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.springaicommunity.claude.agent.sdk.ClaudeClient
import org.springaicommunity.claude.agent.sdk.ClaudeSyncClient
import org.springaicommunity.claude.agent.sdk.transport.CLIOptions
import org.springaicommunity.claude.agent.sdk.types.AssistantMessage
import org.springaicommunity.claude.agent.sdk.types.ResultMessage
import org.springaicommunity.claude.agent.sdk.types.TextBlock

class ClaudeCodeGiftRecommendChatClientTest : FunSpec({
    isolationMode = IsolationMode.InstancePerTest

    val claudeCodeGiftRecommendChatClient = ClaudeCodeGiftRecommendChatClient(systemPrompt = "test system prompt")
    val syncClient = mockk<ClaudeSyncClient>(relaxed = true)
    val resultMessage = mockk<ResultMessage>()

    beforeTest {
        mockkStatic(ClaudeClient::class)
        val syncSpec = mockk<ClaudeClient.SyncSpec> {
            every { workingDirectory(any()) } returns this@mockk
            every { systemPrompt(any<String>()) } returns this@mockk
            every { permissionMode(any()) } returns this@mockk
            every { build() } returns syncClient
        }
        val syncSpecWithOptions = mockk<ClaudeClient.SyncSpecWithOptions> {
            every { workingDirectory(any()) } returns this@mockk
            every { build() } returns syncClient
        }

        every { ClaudeClient.sync() } returns syncSpec
        every { ClaudeClient.sync(any<CLIOptions>()) } returns syncSpecWithOptions
    }

    afterTest {
        unmockkStatic(ClaudeClient::class)
    }

    context("call") {
        test("새 대화를 시작하고 SDK sessionId를 반환한다") {
            every { resultMessage.sessionId() } returns "550e8400-e29b-41d4-a716-446655440000"
            every { syncClient.connectAndReceive("친구 생일 선물 추천해줘") } returns listOf(
                AssistantMessage(listOf(TextBlock("생일 추천 선물은 케이크"))),
                resultMessage,
            )

            val actual = claudeCodeGiftRecommendChatClient.call(
                message = "친구 생일 선물 추천해줘",
                sessionId = null,
            )
            actual.getOrNull()!!.sessionId shouldBe "550e8400-e29b-41d4-a716-446655440000"
            actual.getOrNull()!!.message shouldBe "생일 추천 선물은 케이크"
        }

        test("이전 대화의 컨텍스트를 이어나가는 경우 sessionId에 이어서 응답한다") {
            every { resultMessage.sessionId() } returns "550e8400-e29b-41d4-a716-446655440000"
            every { syncClient.connectAndReceive("다른 선물 추천해줘") } returns listOf(
                AssistantMessage(listOf(TextBlock("이어서 추천합니다"))),
                resultMessage,
            )

            val actual = claudeCodeGiftRecommendChatClient.call(
                message = "다른 선물 추천해줘",
                sessionId = "550e8400-e29b-41d4-a716-446655440000",
            )
            actual.getOrNull()!!.sessionId shouldBe "550e8400-e29b-41d4-a716-446655440000"
            actual.getOrNull()!!.message shouldBe "이어서 추천합니다"
        }

        test("응답이 빈 문자열인 경우 fail Result를 내린다") {
            every { resultMessage.sessionId() } returns "550e8400-e29b-41d4-a716-446655440000"
            every { syncClient.connectAndReceive(any()) } returns listOf(
                AssistantMessage(listOf(TextBlock(""))),
                resultMessage,
            )

            val actual = claudeCodeGiftRecommendChatClient.call(
                message = "친구 생일 선물 추천해줘",
                sessionId = null,
            )
            actual.isFailure shouldBe true
            actual.exceptionOrNull()!!.message shouldBe "content is empty"
        }

        test("ResultMessage가 없는 경우 fail Result를 내린다") {
            every { syncClient.connectAndReceive(any()) } returns listOf(
                AssistantMessage(listOf(TextBlock("응답"))),
            )

            val actual = claudeCodeGiftRecommendChatClient.call(
                message = "친구 생일 선물 추천해줘",
                sessionId = null,
            )
            actual.isFailure shouldBe true
        }

        test("요청이 에러가 나는 경우 fail Result를 내린다") {
            every { syncClient.connectAndReceive(any()) } throws RuntimeException("chat 에러")

            val actual = claudeCodeGiftRecommendChatClient.call(
                message = "친구 생일 선물 추천해줘",
                sessionId = null,
            )
            actual.isFailure shouldBe true
            actual.exceptionOrNull()!!.message shouldBe "chat 에러"
        }
    }
})
