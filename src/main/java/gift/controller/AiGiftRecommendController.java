package gift.controller;

import gift.service.AiGiftRecommendService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiGiftRecommendController {

    private final AiGiftRecommendService aiGiftRecommendService;

    public AiGiftRecommendController(AiGiftRecommendService aiGiftRecommendService) {
        this.aiGiftRecommendService = aiGiftRecommendService;
    }

    @PostMapping("/api/chat")
    public AiGiftRecommendResponse chat(@RequestBody AiGiftRecommendRequest request){
        return aiGiftRecommendService.recommend(request);
    }
}
