package gift.request;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RandomRequestIdGenerator implements RequestIdGenerator {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }

}
