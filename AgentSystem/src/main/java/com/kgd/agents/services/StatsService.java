package com.kgd.agents.services;

import com.kgd.agents.models.geodata.Stats;

public interface StatsService {
    Stats upsert(Stats stats);
}
