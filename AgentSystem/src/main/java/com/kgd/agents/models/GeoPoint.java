package com.kgd.agents.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeoPoint(
        @JsonProperty("x") double x,
        @JsonProperty("y") double y
) {}
