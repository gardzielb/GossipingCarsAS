package com.kgd.maps.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import com.kgd.maps.models.Route;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

public class OsrmRouteDeserializerTests {

    @Test
    public void deserializeShouldReturnValidRoute() {
        var destId = ObjectId.get();
        double distance = 2137.7312;
        var poly1 = List.of(
                new LatLng(12.34, 56.78), new LatLng(87.65, 43.21), new LatLng(43.12, 65.21)
        );
        var poly2 = List.of(
                new LatLng(43.12, 65.21), new LatLng(56.78, 12.34), new LatLng(43.21, 87.65)
        );

        String routeTag = "Warszawa-Hel";
        String routeJson = "{" +
                "\"code\":\"Ok\"," +
                "\"waypoints\":[" +
                "{\"location\":[" + poly1.get(0).lng + "," + poly1.get(0).lat + "]}," +
                "{\"location\":[" + poly1.get(2).lng + "," + poly1.get(2).lat + "]}," +
                "{\"location\":[" + poly2.get(2).lng + "," + poly2.get(2).lat + "]}" +
                "]," +
                "\"routes\":[" +
                "{" +
                "\"legs\":[" +
                "{\"steps\":[" +
                "{\"geometry\":\"" + new EncodedPolyline(poly1).getEncodedPath() + "\"}" +
                "]}," +
                "{\"steps\":[" +
                "{\"geometry\":\"" + new EncodedPolyline(poly2).getEncodedPath() + "\"}" +
                "]}]," +
                "\"distance\":" + distance +
                "}" +
                "]" +
                "}";

        // here we test using this deserializer with ObjectMapper, since it is quite impossible to test it alone
        var objectMapper = new ObjectMapper();
        var module = new SimpleModule();
        module.addDeserializer(Route.class, new OsrmRouteDeserializer(destId, routeTag));
        objectMapper.registerModule(module);

        try {
            var route = objectMapper.readValue(routeJson, Route.class);
            Assertions.assertEquals(destId, route.destinationId());
            Assertions.assertEquals(poly1.get(0).lng, route.origin().getX());
            Assertions.assertEquals(poly1.get(0).lat, route.origin().getY());
            Assertions.assertEquals(distance, route.distance());
            Assertions.assertEquals(2, route.segments().size());
            Assertions.assertEquals(routeTag, route.tag());
            Assertions.assertEquals(
                    new EncodedPolyline(poly1).getEncodedPath(), route.segments().get(0).encodedPolyline()
            );
            Assertions.assertEquals(
                    new EncodedPolyline(poly2).getEncodedPath(), route.segments().get(1).encodedPolyline()
            );
        }
        catch (JsonProcessingException e) {
            Assertions.fail(e.getMessage());
        }
    }
}
