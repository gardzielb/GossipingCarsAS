package com.kgd.maps.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.data.geo.Point;

import java.io.IOException;

public class PointDeserializer extends JsonDeserializer<Point> {
    @Override
    public Point deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        var pointJson = jsonParser.readValueAsTree();
        double x = Double.parseDouble(pointJson.get("x").toString());
        double y = Double.parseDouble(pointJson.get("y").toString());
        return new Point(x, y);
    }
}
