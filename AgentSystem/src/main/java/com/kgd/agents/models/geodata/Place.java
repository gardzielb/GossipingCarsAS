package com.kgd.agents.models.geodata;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.maps.model.PlaceType;

public record Place(
        @JsonProperty("id") String id,
        @JsonProperty("name") String name,
        @JsonProperty("type") PlaceType type,
        @JsonProperty("location") GeoPoint location,
        @JsonProperty("address") String address,
        @JsonProperty("rating") double rating
) {}

