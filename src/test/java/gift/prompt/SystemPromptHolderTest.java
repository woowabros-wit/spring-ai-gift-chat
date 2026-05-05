package gift.prompt;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SystemPromptHolderTest {

    @Test
    void 클래스패스_파일에서_시스템_프롬프트를_로드한다() {
        Resource resource = new ClassPathResource("prompts/system.st");
        SystemPromptHolder holder = new SystemPromptHolder(resource);

        assertThat(holder.get()).contains("선물 추천");
    }
}
