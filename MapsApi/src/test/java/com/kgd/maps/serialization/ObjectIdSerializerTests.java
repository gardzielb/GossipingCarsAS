package com.kgd.maps.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;

public class ObjectIdSerializerTests {
    @Test
    public void serializeShouldWriteValueAsHexString() {
        var id = ObjectId.get();
        var serializerProviderMock = Mockito.mock(SerializerProvider.class);
        var jsonGeneratorMock = Mockito.mock(JsonGenerator.class);

        var serializer = new ObjectIdSerializer();
        try {
            serializer.serialize(id, jsonGeneratorMock, serializerProviderMock);
        }
        catch (IOException e) {
            Assertions.fail(e.getMessage());
        }

        var idStrCaptor = ArgumentCaptor.forClass(String.class);
        try {
            Mockito.verify(jsonGeneratorMock, Mockito.times(1)).writeString(idStrCaptor.capture());
            Assertions.assertEquals(id.toHexString(), idStrCaptor.getValue());
        }
        catch (IOException e) {
            Assertions.fail(e.getMessage());
        }
    }
}
