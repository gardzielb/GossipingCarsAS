package com.kgd.agents.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.maps.model.PlaceType;

import java.awt.*;

public record Place(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("type") PlaceType type,
        @JsonProperty("location") Point location,
        @JsonProperty("address") String address,
        @JsonProperty("rating") double rating
) {}

