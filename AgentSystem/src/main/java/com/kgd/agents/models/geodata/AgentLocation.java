package com.kgd.agents.models.geodata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AgentLocation(
        @JsonProperty("id") String id,
        @JsonProperty("AID") String AID,
        @JsonProperty("location") GeoPoint location
) { }
