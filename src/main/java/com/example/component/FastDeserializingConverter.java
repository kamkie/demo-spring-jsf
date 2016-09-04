package com.example.component;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class FastDeserializingConverter implements Converter<byte[], Object> {
    private final FSTConfiguration conf;

    public FastDeserializingConverter() {
        this.conf = FSTConfiguration.createDefaultConfiguration();
        this.conf.setClassLoader(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Object convert(byte[] source) {
        return conf.asObject(source);
    }
}
