package com.kgd.maps.api;

import com.kgd.maps.models.AgentLocation;
import com.kgd.maps.repositories.AgentLocationRepository;
import com.kgd.maps.services.AgentLocationService;
import com.kgd.maps.services.MongoAgentLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agent_location")
public class AgentLocationController {
    private final AgentLocationService agentLocationService;
    private final AgentLocationRepository agentLocationRepository;

    public AgentLocationController(@Autowired MongoAgentLocationService agentLocationService,
                                   @Autowired AgentLocationRepository agentLocationRepository) {
        this.agentLocationRepository = agentLocationRepository;
        this.agentLocationService = agentLocationService;
    }

    @GetMapping("/all")
    public List<AgentLocation> getAllLocations() {
        return agentLocationService.findAll();
    }

    @GetMapping("/find")
    public ResponseEntity<AgentLocation> getRoute(@RequestParam("aid") String AID) {
        var agentLocation = agentLocationService.findAgentLocation(AID);

        if (agentLocation != null) {
            return ResponseEntity.ok(agentLocation);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @PostMapping("/add")
    public AgentLocation addOrUpdateAgentLocation(@RequestBody AgentLocation location) {
        var mongoResult = agentLocationRepository.findAgentLocationByAIDEquals(location.AID());

        if (mongoResult == null) {
            agentLocationRepository.insert(location);
        } else {
            var updated = new AgentLocation(mongoResult.id(), location.AID(), location.location());
            agentLocationRepository.save(updated);
        }

        return agentLocationRepository.findAgentLocationByAIDEquals(location.AID());
    }
}
