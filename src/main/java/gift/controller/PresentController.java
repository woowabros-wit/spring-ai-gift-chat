package gift.controller;

import gift.dto.PresentRequest;
import gift.dto.PresentResponse;
import gift.service.PresentChatService;
import io.micrometer.common.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PresentController {

    private final PresentChatService service;

    public PresentController(PresentChatService service) {
        this.service = service;
    }

    @PostMapping("/present")
    public PresentResponse recommend(
        @RequestBody PresentRequest request
    ) {
        if (StringUtils.isBlank(request.message())) {
            return PresentResponse.error("메시지를 입력해주세요.", request.sessionId());
        }

        try {
            return service.chat(request);
        } catch (Exception e) {
            return PresentResponse.error(e.getMessage(), request.sessionId());
        }
    }
}
