package gift.chat.external

interface GiftRecommendChatClient {
    fun call(message: String, sessionId: String?): Result<ChatResponse>
}

data class ChatResponse(
    val sessionId: String,
    val message: String,
)
