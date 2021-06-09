package com.kgd.agents.models.messages;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kgd.agents.models.geodata.GeoPoint;

public record TrafficLightExitData(
        @JsonProperty("agentName") String agentName,
        @JsonProperty("exitPoint") GeoPoint exitPoint
) {}
