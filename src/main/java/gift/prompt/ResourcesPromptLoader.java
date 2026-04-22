package gift.prompt;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class ResourcesPromptLoader implements PromptLoader {

    private static final String SYSTEM_PROMPT_PATH = "prompts/system.txt";

    private final String systemPrompt;

    public ResourcesPromptLoader() {
        try {
            this.systemPrompt = new ClassPathResource(SYSTEM_PROMPT_PATH).getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PromptLoadException("Failed to load system prompt. path: " + SYSTEM_PROMPT_PATH, e);
        }
    }

    @Override
    public String loadSystemPrompt() {
        return systemPrompt;
    }
}
