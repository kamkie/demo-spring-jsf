package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@Configuration
@EnableJdbcHttpSession
public class SessionConfiguration {

    @Bean
    public ConversionService springSessionConversionService(Converter<byte[], Object> deserializingConverter,
                                                            Converter<Object, byte[]> serializingConverter) {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(byte[].class, Object.class, deserializingConverter);
        conversionService.addConverter(Object.class, byte[].class, serializingConverter);
        return conversionService;
    }

}
