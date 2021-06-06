package com.kgd.agents.trafficLigths;

import com.kgd.agents.models.geodata.Route;
import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.trafficLigths.behaviors.UpdateTrafficLightsQueueBehavior;
import jade.core.Agent;

import java.util.List;

public class TrafficLightsCarController extends Agent {

    @Override
    protected void setup() {
        super.setup();
        addBehaviour(new UpdateTrafficLightsQueueBehavior(this));
    }

    public void updateLightsQueue(Route route, List<TrafficLights> trafficLights) {
        System.out.println("Received new route, updating TL queue");
    }
}
