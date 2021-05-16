package com.kgd.maps.repositories;

import com.kgd.maps.models.Route;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends MongoRepository<Route, ObjectId> {
    List<Route> findByOriginEqualsAndDestinationEquals(Point origin, Point destination);
}
