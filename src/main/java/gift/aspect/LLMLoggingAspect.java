package gift.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LLMLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LLMLoggingAspect.class);

    @Around("execution(* gift.client.LLMChatClient.chat(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        String userMessage = (String) joinPoint.getArgs()[0];
        long start = System.currentTimeMillis();
        Object response = joinPoint.proceed();
        logger.info("request: {}, response: {}, duration: {}ms",
                userMessage.length(), response, System.currentTimeMillis() - start);
        return response;
    }
}
