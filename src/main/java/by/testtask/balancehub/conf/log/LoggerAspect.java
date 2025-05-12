package by.testtask.balancehub.conf.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Aspect
@Component
public class LoggerAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || " +
            "within(@org.springframework.stereotype.Service *) || " +
            "within(@org.springframework.stereotype.Repository *)")
    public void springBeanPointcut() {}

    @Before("springBeanPointcut()")
    public void logMethodEntry(JoinPoint jp) {
        if (log.isDebugEnabled()) {
            String args = Arrays.stream(jp.getArgs())
                    .map(arg -> arg != null ? arg.toString() : "null")
                    .collect(Collectors.joining(", "));
            log.debug("→ {}.{}() with args = [{}]",
                    jp.getSignature().getDeclaringTypeName(),
                    jp.getSignature().getName(),
                    args);
        }
    }

    @AfterReturning(pointcut = "springBeanPointcut()", returning = "result")
    public void logMethodExit(JoinPoint jp, Object result) {
        if (log.isDebugEnabled()) {
            log.debug("← {}.{}() with result = [{}]",
                    jp.getSignature().getDeclaringTypeName(),
                    jp.getSignature().getName(),
                    result != null ? result.toString() : "null");
        }
    }

    @AfterThrowing(pointcut = "springBeanPointcut()", throwing = "e")
    public void logException(JoinPoint jp, Exception e) {
        log.error("✗ Exception in {}.{}() with cause = {} and message = {}",
                jp.getSignature().getDeclaringTypeName(),
                jp.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e.getMessage() != null ? e.getMessage() : "NULL",
                e);
    }
}