package com.example.tests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.example.component.ExecutionTimeLogger;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class ExecutionTimeLoggerTest {
    @Test
    void beanAnnotatedWithTimed() {
        ExecutionTimeLogger executionTimeLogger = new ExecutionTimeLogger(new SimpleMeterRegistry());
        executionTimeLogger.beanAnnotatedWithTimed();
    }

    @Test
    void publicMethod() {
        ExecutionTimeLogger executionTimeLogger = new ExecutionTimeLogger(new SimpleMeterRegistry());
        executionTimeLogger.publicMethod();
    }

    @Test
    void publicMethodInsideAClassMarkedWithAtTimed() {
        ExecutionTimeLogger executionTimeLogger = new ExecutionTimeLogger(new SimpleMeterRegistry());
        executionTimeLogger.publicMethodInsideAClassMarkedWithAtTimed();
    }

    @Test
    void methodMarkedWithAtTimed() {
        ExecutionTimeLogger executionTimeLogger = new ExecutionTimeLogger(new SimpleMeterRegistry());
        executionTimeLogger.methodMarkedWithAtTimed();
    }

    @Test
    void noLogging() throws Throwable {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.OFF);

        String methodName = "methodName";
        String result = "[d6080736-4941-42d8-9e6d-291eb2807560, 75635b23-333d-42ca-a596-12744d882ba8," +
                " 749679ad-bb19-42c3-9431-f65014ba0551, 2cad3579-c97b-4532-b9dd-b58170f7e9ef, c81d9e4c-a391-466d-90a2-1b932ba0734e]";
        ExecutionTimeLogger executionTimeLogger = new ExecutionTimeLogger(new SimpleMeterRegistry());
        ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        JoinPoint.StaticPart staticPart = Mockito.mock(JoinPoint.StaticPart.class);
        given(joinPoint.getStaticPart()).willReturn(staticPart);
        Signature signature = Mockito.mock(Signature.class);
        given(staticPart.getSignature()).willReturn(signature);

        given(signature.getName()).willReturn(methodName);
        given(signature.getDeclaringTypeName()).willReturn(ExecutionTimeLoggerTest.class.getName());
        given(signature.getDeclaringType()).willReturn(ExecutionTimeLoggerTest.class);

        given(joinPoint.proceed()).willReturn(result);
        given(joinPoint.getArgs()).willReturn(null);
        given(joinPoint.getSignature()).willReturn(signature);
        assertThat(executionTimeLogger.around(joinPoint)).isEqualTo(result);

        root.setLevel(Level.DEBUG);
        assertThat(executionTimeLogger.around(joinPoint)).isEqualTo(result);

        root.setLevel(Level.INFO);
    }

}
