package com.example.serialization;

import org.jboss.marshalling.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.serializer.support.SerializationFailedException;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

public class JbossSerializingConverter implements Converter<Object, byte[]> {
    private final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("river");
    private final MarshallingConfiguration configuration = new MarshallingConfiguration();

    public JbossSerializingConverter() {
        configuration.setVersion(3);
        configuration.setObjectPreResolver(new ChainingObjectResolver(Collections.singletonList(new HibernateDetachResolver())));
    }

    @Override
    public byte[] convert(Object source) {
        try {
            Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            marshaller.start(Marshalling.createByteOutput(os));
            marshaller.writeObject(source);
            marshaller.flush();
            return os.toByteArray();
        } catch (Exception e) {
            throw new SerializationFailedException("Failed to serialize object using jboss marshaling", e);
        }
    }
}
