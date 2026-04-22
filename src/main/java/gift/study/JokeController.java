package gift.study;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JokeController {

    private final ChatClient chatClient;

    public JokeController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/joke")
    public String joke(String message) {
        return chatClient.prompt()
            .system("당신은 친절한 선물 추천 도우미입니다.")
            .user(message)
            .call()
            .content();
    }
}
