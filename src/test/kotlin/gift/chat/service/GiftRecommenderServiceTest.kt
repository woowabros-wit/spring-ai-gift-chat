package gift.chat.service

import gift.chat.dto.RecommendGiftRequest
import gift.chat.exception.GiftRecommendException
import gift.chat.external.ChatResponse
import gift.chat.external.GiftRecommendChatClient
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class GiftRecommenderServiceTest : FunSpec({
    isolationMode = IsolationMode.InstancePerTest

    val chatClient = mockk<GiftRecommendChatClient>()
    val giftRecommenderService = GiftRecommenderService(chatClient = chatClient)

    context("recommend") {
        test("선물 추천을 ai client를 통해 요청한다") {
            val request = RecommendGiftRequest(
                sessionId = null,
                message = "친구 생일 선물 추천해줘"
            )
            every {
                chatClient.call("친구 생일 선물 추천해줘", null)
            } returns Result.success(
                ChatResponse(sessionId = "550e8400-e29b-41d4-a716-446655440000", message = "생일 추천 선물은 케이크")
            )

            val actual = giftRecommenderService.recommend(request)
            actual.sessionId shouldBe "550e8400-e29b-41d4-a716-446655440000"
            actual.message shouldBe "생일 추천 선물은 케이크"
        }

        test("ai 요청이 실패하는 경우 에러를 던진다") {
            val request = RecommendGiftRequest(
                sessionId = null,
                message = "친구 생일 선물 추천해줘"
            )
            every {
                chatClient.call("친구 생일 선물 추천해줘", null)
            } returns Result.failure(RuntimeException("chat 에러"))

            shouldThrow<GiftRecommendException> {
                giftRecommenderService.recommend(request)
            }
        }
    }
})
