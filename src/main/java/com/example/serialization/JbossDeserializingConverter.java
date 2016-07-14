package com.example.serialization;

import org.jboss.marshalling.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.SerializationFailedException;

import java.io.IOException;
import java.nio.ByteBuffer;

public class JbossDeserializingConverter implements Converter<byte[], Object> {
    private final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("river");
    private final MarshallingConfiguration configuration = new MarshallingConfiguration();

    public JbossDeserializingConverter(ClassLoader classLoader) {
        configuration.setClassResolver(new SimpleClassResolver(classLoader));
    }

    @Override
    public Object convert(byte[] source) {
        try {
            Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
            unmarshaller.start(Marshalling.createByteInput(ByteBuffer.wrap(source)));
            return unmarshaller.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new SerializationFailedException("Failed to deserialize payload. " +
                    "Is the byte array a result of corresponding serialization for jboss marshaling?", e);
        }
    }
}
