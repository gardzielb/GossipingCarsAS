package com.kgd.maps.repositories;

import com.kgd.maps.models.TrafficLightSystem;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrafficLightSystemRepository extends MongoRepository<TrafficLightSystem, ObjectId> {}
