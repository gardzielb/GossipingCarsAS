package com.kgd.maps.api;

import com.kgd.maps.models.Stats;
import com.kgd.maps.models.Summary;
import com.kgd.maps.repositories.StatsRepository;
import io.opencensus.stats.Aggregation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.ToDoubleFunction;

@RestController
@RequestMapping("/stats")
public class StatsController {
    private final StatsRepository statsRepository;

    public StatsController(@Autowired StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<Stats> add(@RequestBody Stats stats) {
        return ResponseEntity.ok(statsRepository.insert(stats));
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

    @DeleteMapping("/delete_arrived")
    public void deleteArrived(@RequestParam("arrived") boolean arrived) {
        statsRepository.deleteByArrived(arrived);
    }

    @PostMapping("/upsert")
    public ResponseEntity<Stats> upsert(@RequestBody Stats stats) {
        var value = statsRepository.findById(stats.id);

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

            return ResponseEntity.ok(statsRepository.save(value));
        }
        else
        {
            return ResponseEntity.ok(statsRepository.save(stats));
        }
    }

    private double StatsAverage(List<Stats> list, ToDoubleFunction<Stats> selector) {
        return list.stream().mapToDouble(selector).average().orElse(0);
    }

    @GetMapping("/summary")
    public Summary summary() {
        ToDoubleFunction<Stats> costSelector = m -> m.cost != null ? m.cost : 0.0;
        ToDoubleFunction<Stats> distanceSelector = m -> m.distance != null ? m.distance : 0.0;
        ToDoubleFunction<Stats> timeSelector = m -> m.time != null ? m.time : 0.0;

        var results = statsRepository.findAllByArrived(true);

        var dumbResults = results.stream()/*.filter(r -> r.dumb != null && r.dumb)*/.toList();
        var dumbCostAvg = StatsAverage(dumbResults, costSelector);
        var dumbDistAvg = StatsAverage(dumbResults, distanceSelector);
        var dumbTimeAvg = StatsAverage(dumbResults, timeSelector);

        var smartResults = results.stream()/*.filter(r -> r.dumb != null && !r.dumb)*/.toList();
        var smartCostAvg = StatsAverage(smartResults, costSelector);
        var smartDistAvg = StatsAverage(smartResults, distanceSelector);
        var smartTimeAvg = StatsAverage(smartResults, timeSelector);

        return new Summary(dumbCostAvg, dumbDistAvg, dumbTimeAvg, smartCostAvg, smartDistAvg, smartTimeAvg);
    }
}
