package com.kgd.agents.services;

import com.kgd.agents.models.geodata.CarData;

import java.util.List;

public interface CarDataService {
    List<CarData> getAll();
    void deleteById(String Id);
}
