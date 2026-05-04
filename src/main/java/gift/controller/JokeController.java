package gift.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JokeController {
    private final ChatClient client;

    public JokeController(ChatClient.Builder builder) {
        this.client = builder.build();
    }

    @GetMapping("/joke")
    public String joke(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return client
                .prompt()
                .system("당신은 시인 입니다.")
                .user(message)
                .call()
                .content();
    }
}
