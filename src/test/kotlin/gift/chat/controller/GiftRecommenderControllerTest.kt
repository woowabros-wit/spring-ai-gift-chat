package gift.chat.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import gift.chat.dto.RecommendGiftRequest
import gift.chat.dto.RecommendGiftResponse
import gift.chat.exception.GiftRecommendException
import gift.chat.service.GiftRecommenderService
import io.kotest.core.extensions.ApplyExtension
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WebMvcTest(GiftRecommenderController::class)
@ApplyExtension(SpringExtension::class)
class GiftRecommenderControllerTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean private val giftRecommenderService: GiftRecommenderService
) : FunSpec() {

    init {
        context("recommendGift") {
            test("추천된 상품 정보를 응답한다") {
                every {
                    giftRecommenderService.recommend(
                        RecommendGiftRequest(
                            sessionId = "550e8400-e29b-41d4-a716-446655440000",
                            message = "친구 생일 선물 추천해 줘"
                        )
                    )
                } returns RecommendGiftResponse(
                    sessionId = "550e8400-e29b-41d4-a716-446655440000",
                    message = "생일 추천 선물은 케이크"
                )

                mockMvc.post("/api/gifts/recommend") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        RecommendGiftRequest(
                            sessionId = "550e8400-e29b-41d4-a716-446655440000",
                            message = "친구 생일 선물 추천해 줘",
                        )
                    )
                }.andExpect {
                    status { isOk() }
                    jsonPath("$.sessionId") { value("550e8400-e29b-41d4-a716-446655440000") }
                    jsonPath("$.message") { value("생일 추천 선물은 케이크") }
                }
            }

            test("요청 메시지가 빈 경우 400 에러를 응답한다") {
                mockMvc.post("/api/gifts/recommend") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        RecommendGiftRequest(
                            sessionId = null,
                            message = ""
                        )
                    )
                }.andExpect {
                    status { isBadRequest() }
                    jsonPath("$.message") { value("선물 추천을 위한 메시지가 필요합니다.") }
                }
            }

            test("AI 요청이 실패하는 경우 500 에러를 응답한다") {
                every {
                    giftRecommenderService.recommend(
                        RecommendGiftRequest(
                            sessionId = null,
                            message = "친구 생일 선물 추천해줘"
                        )
                    )
                } throws GiftRecommendException()

                mockMvc.post("/api/gifts/recommend") {
                    contentType = MediaType.APPLICATION_JSON
                    content = objectMapper.writeValueAsString(
                        RecommendGiftRequest(
                            sessionId = null,
                            message = "친구 생일 선물 추천해줘"
                        )
                    )
                }.andExpect {
                    status { isInternalServerError() }
                    jsonPath("$.message") { value("선물 추천에 실패했습니다. 잠시 후 다시 시도해주세요.") }
                }
            }
        }
    }
}
