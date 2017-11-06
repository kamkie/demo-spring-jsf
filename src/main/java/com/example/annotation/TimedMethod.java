package com.example.annotation;

import io.micrometer.core.annotation.Timed;

import java.lang.annotation.*;

@Inherited
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Timed
public @interface TimedMethod {
}
