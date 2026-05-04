package gift.controller;

import gift.dto.GiftReq;
import gift.dto.GiftRes;
import gift.service.GiftService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GiftController {
    private final GiftService giftService;

    public GiftController(GiftService giftService) {
        this.giftService = giftService;
    }

    @PostMapping("/api/gift-chat")
    public GiftRes giftChat(@Valid @RequestBody GiftReq request) {
        // 응답내에 message, requestId, durationMs 가 필수가 필요
        // sessionId는 클라이언트가 생성하는 대화 단위 식별자이고, requestId는 서버가 생성하는 요청 단위 식별자이다. 이 두 식별자는 1:n 관계이다.
        return giftService.chat(request);
    }
}
