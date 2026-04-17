package gift.utils;

import io.micrometer.common.util.StringUtils;
import java.util.UUID;

public class SessionIdGenerator {

    public static String generateSessionId(String sessionId) {
        return StringUtils.isBlank(sessionId)
            ? UUID.randomUUID().toString()
            : sessionId;
    }
}
