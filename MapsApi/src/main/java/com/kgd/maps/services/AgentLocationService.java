package com.kgd.maps.services;

import com.kgd.maps.models.AgentLocation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AgentLocationService {
    AgentLocation findAgentLocation(String AID);
    List<AgentLocation> findAll();
}
