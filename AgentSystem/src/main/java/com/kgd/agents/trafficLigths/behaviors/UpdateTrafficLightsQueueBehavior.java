package com.kgd.agents.trafficLigths.behaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.Route;
import com.kgd.agents.models.messages.CarLocationData;
import com.kgd.agents.services.HttpTrafficLightsService;
import com.kgd.agents.services.TrafficLightsService;
import com.kgd.agents.trafficLigths.TrafficLightsCarController;
import com.kgd.agents.utility.CarLocationQuery;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;

public class UpdateTrafficLightsQueueBehavior extends CyclicBehaviour {

    private final TrafficLightsCarController agent;
    private final TrafficLightsService lightsService = new HttpTrafficLightsService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UpdateTrafficLightsQueueBehavior(TrafficLightsCarController agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        var routeMessage = agent.blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
        if (routeMessage == null)
            return;

        try {
            var route = objectMapper.readValue(routeMessage.getContent(), Route.class);
            var trafficLights = lightsService.findAllByRouteTag(route.tag());
            agent.updateLightsQueue(route, trafficLights);
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
