package com.kgd.agents.services;

import com.kgd.agents.models.CarData;

import java.util.List;

public interface CarDataService {
    List<CarData> getAll();
    void deleteAll();
}
