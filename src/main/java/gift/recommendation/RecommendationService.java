package gift.recommendation;

import gift.chat.ChatService;
import gift.request.RequestIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final RequestIdGenerator requestIdGenerator;
    private final ChatService chatService;

    public RecommendationService(RequestIdGenerator requestIdGenerator, ChatService chatService) {
        this.requestIdGenerator = requestIdGenerator;
        this.chatService = chatService;
    }

    public RecommendationResponse recommend(RecommendationRequest request) {
        var requestId = requestIdGenerator.generate();

        var startNs = System.nanoTime();
        var content = chatService.chat(request.message());
        var durationMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        log.info("Recommendation generated in {} ms. requestId: {}, message: {}", durationMs, requestId, request.message());

        return new RecommendationResponse(requestId, content, durationMs);
    }
