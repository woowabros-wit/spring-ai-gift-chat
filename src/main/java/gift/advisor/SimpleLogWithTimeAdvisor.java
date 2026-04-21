package gift.advisor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;

public class SimpleLogWithTimeAdvisor implements CallAdvisor {

    private static final Logger log = LoggerFactory.getLogger(SimpleLogWithTimeAdvisor.class);

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        long start = System.currentTimeMillis();
        ChatClientResponse chatClientResponse = null;
        try {
            chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
            return chatClientResponse;
        } finally {
            long elapsed = System.currentTimeMillis() - start;
            log.info("execute time = {}, request = {}, response = {}", elapsed, chatClientRequest, chatClientResponse);
        }
    }

    @Override
    public String getName() {
        return "TimeAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
