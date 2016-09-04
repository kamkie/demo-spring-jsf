package com.example.tests;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.example.component.ExecutionTimeLogger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class ExecutionTimeLoggerTest {
    @Test
    public void beanAnnotatedWithTimed() throws Exception {
        ExecutionTimeLogger executionTimeLogger = new ExecutionTimeLogger();
        executionTimeLogger.beanAnnotatedWithTimed();
    }

    @Test
    public void publicMethod() throws Exception {
        ExecutionTimeLogger executionTimeLogger = new ExecutionTimeLogger();
        executionTimeLogger.publicMethod();
    }

    @Test
    public void publicMethodInsideAClassMarkedWithAtTimed() throws Exception {
        ExecutionTimeLogger executionTimeLogger = new ExecutionTimeLogger();
        executionTimeLogger.publicMethodInsideAClassMarkedWithAtTimed();
    }

    @Test
    public void methodMarkedWithAtTimed() throws Exception {
        ExecutionTimeLogger executionTimeLogger = new ExecutionTimeLogger();
        executionTimeLogger.methodMarkedWithAtTimed();
    }

    @Test
    public void noLogging() throws Throwable {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.OFF);

        String methodName = "methodName";
        String result = "[d6080736-4941-42d8-9e6d-291eb2807560, 75635b23-333d-42ca-a596-12744d882ba8," +
                " 749679ad-bb19-42c3-9431-f65014ba0551, 2cad3579-c97b-4532-b9dd-b58170f7e9ef, c81d9e4c-a391-466d-90a2-1b932ba0734e]";
        ExecutionTimeLogger executionTimeLogger = new ExecutionTimeLogger();
        ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
        Signature signature = Mockito.mock(Signature.class);

        given(signature.getName()).willReturn(methodName);
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
