package com.kgd.maps.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.data.geo.Point;

public class PointDeserializerTests {
    @Test
    public void deserializeShouldReturnValidPoint() {
        // here we test using this deserializer with ObjectMapper, since it is quite impossible to test it alone
        var objectMapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(Point.class, new PointDeserializer());
        objectMapper.registerModule(module);

        double x = 21.37;
        double y = 73.12;
        String pointStr = "{\"x\":" + x + ",\"y\":" + y + "}";

        try {
            var point = objectMapper.readValue(pointStr, Point.class);
            Assertions.assertEquals(x, point.getX());
            Assertions.assertEquals(y, point.getY());
        }
        catch (JsonProcessingException e) {
            Assertions.fail(e.getMessage());
        }
    }
}
