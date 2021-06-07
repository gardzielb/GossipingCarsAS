package com.kgd.agents.models.geodata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Vec2(
        @JsonProperty("x") double x,
        @JsonProperty("y") double y
) {
    public static Vec2 fromGeoPoint(GeoPoint point) {
        return new Vec2(point.x(), point.y());
    }

    public double norm() {
        return Math.sqrt(x * x + y * y);
    }

    public Vec2 normalized() {
        double length = norm();
        return new Vec2(x / length, y / length);
    }

    public Vec2 minus(Vec2 v) {
        return new Vec2(x - v.x, y - v.y);
    }

    public double dot(Vec2 v) {
        return x * v.x + y * v.y;
    }
}
