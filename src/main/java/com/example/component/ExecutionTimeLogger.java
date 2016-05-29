package com.example.component;

import com.example.utils.MicrosOfMilliSecond;
import com.example.utils.NanosOfMicroSecond;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import static java.time.temporal.ChronoField.*;

@Slf4j
@Aspect
@Component
public class ExecutionTimeLogger {

    private static final int MAX_CHARS = 60;
    private static final String STR_SUFFIX = " ...>";
    public static final DateTimeFormatter TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(HOUR_OF_DAY, 2)
            .appendLiteral(':')
            .appendValue(MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendLiteral('.')
            .appendFraction(MILLI_OF_SECOND, 0, 3, false)
            .optionalStart()
            .appendLiteral("ms ")
            .optionalStart()
            .appendFraction(MicrosOfMilliSecond.INSTANCE, 0, 3, false)
            .appendLiteral("us ")
            .appendFraction(NanosOfMicroSecond.INSTANCE, 0, 3, false)
            .appendLiteral("ns")
            .toFormatter(Locale.ROOT);

    @Pointcut("within(@com.example.annotation.Timed *)")
    public void beanAnnotatedWithTimed() {
    }

    @Pointcut("execution(public * *(..))")
    public void publicMethod() {
    }

    @Pointcut("publicMethod() && beanAnnotatedWithTimed()")
    public void publicMethodInsideAClassMarkedWithAtTimed() {
    }

    @Pointcut("execution(* *(..)) && @annotation(com.example.annotation.Timed)")
    public void methodMarkedWithAtTimed() {
    }

    @Around("publicMethodInsideAClassMarkedWithAtTimed() || methodMarkedWithAtTimed()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long start = System.nanoTime();
        Object result = point.proceed();

        if (log.isInfoEnabled()) {
            long nanos = System.nanoTime() - start;
            String duration = formatDuration(nanos);
            Signature signature = point.getSignature();
            Class declaringType = signature.getDeclaringType();

            if (log.isDebugEnabled()) {
                log.debug("class: {}, method: {}, time {}, args {}, result {}", declaringType.getSimpleName(),
                        signature.getName(), duration, formatLongString(point.getArgs()), formatLongString(result));
            } else {
                log.info("class: {}, method: {}, time {}", declaringType.getSimpleName(), signature.getName(),
                        duration);
            }
        }

        return result;
    }

    public static String formatDuration(long nanos) {
        return TIME_FORMATTER.format(LocalTime.ofNanoOfDay(nanos));
    }

    public static String formatLongString(Object input) {
        if (input == null) {
            return "{null}";
        }
        String string = input.toString();
        if ("".equals(string)) {
            return "{empty}";
        }

        int length = string.length();
        if (length <= MAX_CHARS) {
            return string;
        } else {
            String substring = string.substring(0, MAX_CHARS);
            substring = substring.substring(0, substring.lastIndexOf(' '));
            return substring.concat(STR_SUFFIX);
        }
    }

}
