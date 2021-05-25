package com.kgd.maps.repositories;

import com.kgd.maps.models.CarData;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CarDataRepository extends MongoRepository<CarData, ObjectId> {}
