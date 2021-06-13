package com.kgd.maps.repositories;

import com.kgd.maps.models.TrafficLights;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrafficLightsRepository extends MongoRepository<TrafficLights, ObjectId> {
    List<TrafficLights> findAllByRouteTagsContains(String routeTag);
}
