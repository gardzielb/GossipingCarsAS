package com.kgd.maps.repositories;

import com.kgd.maps.models.Route;
import com.kgd.maps.models.Stats;
import org.bson.types.ObjectId;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatsRepository extends MongoRepository<Stats, ObjectId> {
    Stats findByAID(String AID);
    List<Stats> findAllByArrived(boolean arrived);
}
