package com.kgd.maps.api;

import com.kgd.maps.models.Stats;
import com.kgd.maps.repositories.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {
    private final StatsRepository statsRepository;

    public StatsController(@Autowired StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @PostMapping("/add")
    public Stats add(@RequestBody Stats stats) {
        return statsRepository.insert(stats);
    }

    @GetMapping("/all")
    public List<Stats> all() {
        return statsRepository.findAll();
    }

    @GetMapping("/all_by_arrived")
    public List<Stats> allArrived(@RequestParam("arrived") boolean arrived) {
        return statsRepository.findAllByArrived(arrived);
    }

    @DeleteMapping("/delete_all")
    public void deleteAll() {
        statsRepository.deleteAll();
    }

    @PostMapping("/upsert")
    public Stats upsert(@RequestBody Stats stats) {
        var value = statsRepository.findByAID(stats.AID);

        if(value != null)
        {
            if(value.cost == null && stats.cost != null)
            {
                value.cost = stats.cost;
            }
            if(value.distance == null && stats.distance != null)
            {
                value.distance = stats.distance;
            }
            if(value.time == null && stats.time != null)
            {
                value.time = stats.time;
            }
            if(value.arrived == null && stats.arrived != null)
            {
                value.arrived = stats.arrived;
            }

            return statsRepository.save(value);
        }
        else
        {
            return statsRepository.save(stats);
        }
    }
}
