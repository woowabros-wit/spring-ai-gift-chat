package gift;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GiftController {

    private final GiftService giftService;

    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @PostMapping("/api/chat")
    GiftResponse gift(String message, String sessionId) {
        return giftService.gift(message, sessionId);
    }
}
