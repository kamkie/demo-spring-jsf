package com.example.serialization;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.SerializationFailedException;

public class FastDeserializingConverter implements Converter<byte[], Object> {
    private final FSTConfiguration conf;

    public FastDeserializingConverter(ClassLoader classLoader) {
        conf = FSTConfiguration.createDefaultConfiguration();
        conf.setClassLoader(classLoader);
    }

    @Override
    public Object convert(byte[] source) {
        try {
            return conf.asObject(source);
        } catch (Exception e) {
            throw new SerializationFailedException("Failed to deserialize payload. " +
                    "Is the byte array a result of corresponding serialization for jboss marshaling?", e);
        }
    }
}
