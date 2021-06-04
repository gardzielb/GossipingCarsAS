package com.kgd.agents.services;

import com.kgd.agents.models.geodata.AgentLocation;

import java.io.IOException;

public interface AgentLocationService {
    AgentLocation getAgentLocationByAID(String AID) throws IOException, InterruptedException;
    AgentLocation addOrUpdateAgentLocation(AgentLocation location);
    void deleteAgentLocationByAID(String AID);
}
