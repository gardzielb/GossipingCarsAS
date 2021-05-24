package com.kgd.agents.services;

import com.kgd.agents.models.AgentLocation;

import java.io.IOException;
import java.net.URISyntaxException;

public interface AgentLocationService {
    AgentLocation getAgentLocationByAID(String AID) throws URISyntaxException, IOException, InterruptedException;
    AgentLocation addOrUpdateAgentLocation(AgentLocation location);
}
