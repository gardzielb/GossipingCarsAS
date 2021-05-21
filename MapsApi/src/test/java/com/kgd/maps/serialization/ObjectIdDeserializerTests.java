package com.kgd.maps.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

public class ObjectIdDeserializerTests {
    @Test
    public void deserializeShouldReturnValidPoint() {
        var id = ObjectId.get();
        var contextMock = Mockito.mock(DeserializationContext.class);
        var jsonParserMock = Mockito.mock(JsonParser.class);
        try {
            Mockito.when(jsonParserMock.getValueAsString()).thenReturn(id.toHexString());
        }
        catch (IOException e) {
            Assertions.fail(e.getMessage());
        }

        var deserializer = new ObjectIdDeserializer();
        ObjectId deserialized = null;
        try {
            deserialized = deserializer.deserialize(jsonParserMock, contextMock);
        }
        catch (IOException e) {
            Assertions.fail(e.getMessage());
        }

        Assertions.assertEquals(id, deserialized);
    }
}
