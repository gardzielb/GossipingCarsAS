package com.kgd.agents.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.geom.Point2D;

public record AgentLocation(
        @JsonProperty("id") String id,
        @JsonProperty("AID") String AID,
        @JsonProperty("location") GeoPoint location
) { }
