package com.kgd.maps.services;

import org.springframework.data.geo.Point;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;

public class OsrmRequestBuilder {

    private final Map<String, String> properties = new HashMap<>() {{
        put("service", "route");
        put("profile", "driving");
        put("steps", "false");
        put("overview", "full");
    }};

    public OsrmRequestBuilder service(String service) {
        properties.replace("service", service);
        return this;
    }

    public OsrmRequestBuilder profile(String profile) {
        properties.replace("profile", profile);
        return this;
    }

    public OsrmRequestBuilder overview(String overview) {
        properties.replace("overview", overview);
        return this;
    }

    public OsrmRequestBuilder includeSteps() {
        properties.replace("steps", "true");
        return this;
    }

    public OsrmRequestBuilder origin(Point origin) {
        return setPointProperty("origin", origin);
    }

    public OsrmRequestBuilder destination(Point destination) {
        return setPointProperty("destination", destination);
    }

    public OsrmRequestBuilder waypoints(Point[] waypoints) {
        var builder = new StringBuilder();
        for (Point waypoint : waypoints) {
            builder.append(waypoint.getX()).append(',').append(waypoint.getY()).append(';');
        }
        properties.put("waypoints", builder.toString());
        return this;
    }

    public HttpRequest build() throws URISyntaxException {
        String url = "https://router.project-osrm.org/route/v1/" + properties.get("profile") + '/' +
                properties.get("origin") + ";" + properties.get("waypoints") + properties.get("destination") +
                "?overview=" + properties.get("overview") + "&steps=" + properties.get("steps");
        System.out.println(url);
        return HttpRequest.newBuilder(new URI(url)).GET().build();
    }

    private OsrmRequestBuilder setPointProperty(String key, Point value) {
        var builder = new StringBuilder();
        builder.append(value.getX()).append(',').append(value.getY());
        properties.put(key, builder.toString());
        return this;
    }
}
