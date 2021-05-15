package com.example.maps.repositories;

import com.example.maps.models.Place;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends MongoRepository<Place, ObjectId> {
    List<Place> findByLocationNear( Point location, Distance distance);
}
