package com.kgd.agents.models.geodata;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TrafficLightSystem(
        @JsonProperty("id") String id,
        @JsonProperty("physicalLights") TrafficLights[] physicalLights
) {}
