package gift;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JokeController {

    private final ChatClient chatClient;

    public JokeController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/joke")
    public String joke(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        var response = chatClient.prompt()
            .system("답변 후에는 답변과 관련된 농담 하나 추가해")
            .user(message)
            .call()
            .content();

        return response;
    }
}
