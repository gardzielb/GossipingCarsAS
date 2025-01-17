package com.kgd.maps.repositories;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.maps.model.PlaceType;
import com.kgd.maps.models.Place;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends MongoRepository<Place, ObjectId> {
    List<Place> findByLocationNear(Point location, Distance distance);

    List<Place> findByType(@JsonProperty("type") String type);
}
