package com.kgd.maps.services;

import com.kgd.maps.models.AgentLocation;
import com.kgd.maps.repositories.AgentLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoAgentLocationService implements AgentLocationService{
    private final AgentLocationRepository agentLocations;

    public MongoAgentLocationService(@Autowired AgentLocationRepository repository) {
        this.agentLocations = repository;
    }

    @Override
    public AgentLocation findAgentLocation(String AID) {
        return agentLocations.findAgentLocationByAIDEquals(AID);
    }

    @Override
    public List<AgentLocation> findAll() {
        return agentLocations.findAll();
    }
}
