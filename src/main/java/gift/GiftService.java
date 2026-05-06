package gift;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GiftService {

    private static final String SYSTEM_PROMPT = "당신은 선물 추천 도우미에요.";

    private final GiftChatClient chatClient;

    public GiftService(GiftChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public GiftResponse gift(String message, String sessionId) {
        long st = System.nanoTime();

        String response = chatClient.chat(SYSTEM_PROMPT, message);

        return new GiftResponse(
                UUID.randomUUID().toString(),
                response,
                String.valueOf(System.nanoTime() - st),
                sessionId
        );
    }
}