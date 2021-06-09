package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.Route;
import com.kgd.agents.services.HttpTrafficLightsService;
import com.kgd.agents.services.TrafficLightsService;
import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;

public class UpdateTrafficLightsQueueBehavior extends CyclicBehaviour {

    private final TrafficLightsCarControllerAgent agent;
    private final TrafficLightsService lightsService = new HttpTrafficLightsService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UpdateTrafficLightsQueueBehavior(TrafficLightsCarControllerAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        var routeMessage = agent.receive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (routeMessage != null) {
            try {
                var route = objectMapper.readValue(routeMessage.getContent(), Route.class);
                var trafficLights = lightsService.findAllByRouteTag(route.tag());
                agent.updateLightsQueue(route, trafficLights);
            }
            catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            block();
        }
    }
}
