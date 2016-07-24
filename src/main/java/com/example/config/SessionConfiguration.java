package com.example.config;

import com.example.serialization.FastDeserializingConverter;
import com.example.serialization.FastSerializingConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class SessionConfiguration {

    @Bean
    public ConversionService springSessionConversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new FastDeserializingConverter(Thread.currentThread().getContextClassLoader()));
        conversionService.addConverter(Object.class, byte[].class, new FastSerializingConverter());
        return conversionService;
    }

}
