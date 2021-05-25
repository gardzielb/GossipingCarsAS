package com.kgd.maps.api;

import com.kgd.maps.models.AgentLocation;
import com.kgd.maps.repositories.AgentLocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/agent_location")
public class AgentLocationController {
    private final AgentLocationRepository agentLocationRepository;

    public AgentLocationController(@Autowired AgentLocationRepository agentLocationRepository) {
        this.agentLocationRepository = agentLocationRepository;
    }

    @GetMapping("/all")
    public List<AgentLocation> getAllLocations() {
        return agentLocationRepository.findAll();
    }

    @GetMapping("/find")
    public ResponseEntity<AgentLocation> getAgentLocation(@RequestParam("aid") String AID) {
        var agentLocation = agentLocationRepository.findAgentLocationByAIDEquals(AID);

        if (agentLocation != null) {
            return ResponseEntity.ok(agentLocation);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    }

    @DeleteMapping("/find")
    public void deleteAgentLocation(@RequestParam("aid") String AID) {
        var agentLocation = agentLocationRepository.findAgentLocationByAIDEquals(AID);

        agentLocationRepository.delete(agentLocation);
    }

    @PostMapping("/add")
    public ResponseEntity<AgentLocation> addOrUpdateAgentLocation(@RequestBody AgentLocation location) {
        var mongoResult = agentLocationRepository.findAgentLocationByAIDEquals(location.AID());

        if (mongoResult == null) {
            agentLocationRepository.insert(location);
        } else {
            var updated = new AgentLocation(mongoResult.id(), location.AID(), location.location());
            agentLocationRepository.save(updated);
        }

        return ResponseEntity.ok(agentLocationRepository.findAgentLocationByAIDEquals(location.AID()));
    }
}
