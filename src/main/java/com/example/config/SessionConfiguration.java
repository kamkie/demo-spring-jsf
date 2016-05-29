package com.example.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.devtools.restart.classloader.RestartClassLoader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.serializer.support.DeserializingConverter;
import org.springframework.core.serializer.support.SerializingConverter;

@Configuration
public class SessionConfiguration {

    @Bean
    @ConditionalOnClass(RestartClassLoader.class)
    public ConversionService springSessionConversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new DeserializingConverter(Thread.currentThread().getContextClassLoader()));
        conversionService.addConverter(Object.class, byte[].class, new SerializingConverter());
        return conversionService;
    }

}
