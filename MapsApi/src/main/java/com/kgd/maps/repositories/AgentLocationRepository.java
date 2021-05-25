package com.kgd.maps.repositories;

import com.kgd.maps.models.AgentLocation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AgentLocationRepository extends MongoRepository<AgentLocation, ObjectId> {
    AgentLocation findAgentLocationByAIDEquals(String AID);
}
