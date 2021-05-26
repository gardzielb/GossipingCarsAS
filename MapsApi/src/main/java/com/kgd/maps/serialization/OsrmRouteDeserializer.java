package com.kgd.maps.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.maps.model.EncodedPolyline;
import com.google.maps.model.LatLng;
import com.kgd.maps.models.Route;
import com.kgd.maps.models.RouteSegment;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;

import java.io.IOException;
import java.util.ArrayList;

public class OsrmRouteDeserializer extends JsonDeserializer<Route> {

    private final ObjectId destId;

    public OsrmRouteDeserializer(ObjectId destId) {
        this.destId = destId;
    }

    @Override
    public Route deserialize(JsonParser jsonParser, DeserializationContext context)
            throws IOException, JsonProcessingException {
        var json = jsonParser.readValueAsTree();
        var routeNode = json.get("routes").get(0);
        var waypointsNode = json.get("waypoints");

        var segments = new ArrayList<RouteSegment>();
        for (var legNode : (ArrayNode) routeNode.get("legs")) {
            segments.add(segmentFromLegNode(legNode));
        }

        return new Route(
                ObjectId.get(),
                pointFromLocationNode((ArrayNode) waypointsNode.get(0).get("location")),
                destId, segments,
                Double.parseDouble(routeNode.get("distance").toString())
        );
    }

    private RouteSegment segmentFromLegNode(JsonNode legNode) {
        var stepsNode = (ArrayNode) legNode.get("steps");
        var polyline = new ArrayList<LatLng>();

        for (var step : stepsNode) {
            var encodedPolyline = new EncodedPolyline(step.get("geometry").asText());
            polyline.addAll(encodedPolyline.decodePath());
        }

        var origin = polyline.get(0);
        var dest = polyline.get(polyline.size() - 1);

        return new RouteSegment(
                new Point(origin.lng, origin.lat), new Point(dest.lng, dest.lat),
                new EncodedPolyline(polyline).getEncodedPath()
        );
    }

    private Point pointFromLocationNode(ArrayNode locationNode) {
        double x = locationNode.get(0).asDouble();
        double y = locationNode.get(1).asDouble();
        return new Point(x, y);
    }
}
