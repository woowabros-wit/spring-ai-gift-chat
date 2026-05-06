package gift.chat.external

import org.springaicommunity.claude.agent.sdk.ClaudeClient
import org.springaicommunity.claude.agent.sdk.ClaudeSyncClient
import org.springaicommunity.claude.agent.sdk.config.PermissionMode
import org.springaicommunity.claude.agent.sdk.transport.CLIOptions
import org.springaicommunity.claude.agent.sdk.types.AssistantMessage
import org.springaicommunity.claude.agent.sdk.types.ResultMessage
import java.nio.file.Path

class ClaudeCodeGiftRecommendChatClient(
    private val systemPrompt: String,
) : GiftRecommendChatClient {

    override fun call(message: String, sessionId: String?): Result<ChatResponse> {
        return runCatching {
            val options = CLIOptions.builder()
                .systemPrompt(systemPrompt)
                .permissionMode(PermissionMode.BYPASS_PERMISSIONS)
                .apply { sessionId?.let { resume(it) } }
                .build()
            val client = ClaudeClient.sync(options)
                .workingDirectory(Path.of("."))
                .build()

            client.use { c -> chat(c, message) }
        }
    }

    private fun chat(client: ClaudeSyncClient, message: String): ChatResponse {
        val messages = client.connectAndReceive(message).toList()
        val responseText = messages
            .filterIsInstance<AssistantMessage>()
            .joinToString("") { it.text() }
        val resultSessionId = messages
            .filterIsInstance<ResultMessage>()
            .last().sessionId()

        require(responseText.isNotEmpty()) { "content is empty" }
        return ChatResponse(sessionId = resultSessionId, message = responseText)
    }
}
