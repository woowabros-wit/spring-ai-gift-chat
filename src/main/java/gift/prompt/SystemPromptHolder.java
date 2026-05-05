package gift.prompt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SystemPromptHolder {
    private final String prompt;

    public SystemPromptHolder(@Value("classpath:prompts/system.st") Resource resource) {
        try {
            this.prompt = resource.getContentAsString(StandardCharsets.UTF_8);
            if (this.prompt.isBlank()) {
                throw new IllegalStateException("시스템 프롬프트 파일이 비어있습니다.");
            }
        } catch (IOException e) {
            throw new IllegalStateException("시스템 프롬프트 파일을 읽을 수 없습니다.", e);
        }
    }

    public String get() { return prompt; }
}
