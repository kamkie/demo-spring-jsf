package com.example.serialization;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.SerializationFailedException;

public class FastSerializingConverter implements Converter<Object, byte[]> {
    private final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    @Override
    public byte[] convert(Object source) {
        try {
            return conf.asByteArray(source);
        } catch (Exception e) {
            throw new SerializationFailedException("Failed to serialize object using jboss marshaling", e);
        }
    }
}
