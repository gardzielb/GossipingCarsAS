package com.kgd.agents.trafficLigths.managerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.Vec2;
import com.kgd.agents.trafficLigths.TrafficLightSignalerAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ChangeLightColorBehavior extends CyclicBehaviour {

    private final TrafficLightSignalerAgent agent;
    private final ObjectMapper deserializer = new ObjectMapper();

    public ChangeLightColorBehavior(TrafficLightSignalerAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        var msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
        var changeColorRequest = agent.receive(msgTemplate);

        if (changeColorRequest != null) {
            var dirJson = changeColorRequest.getContent();
            try {
                var dirVec = deserializer.readValue(dirJson, Vec2.class);
                agent.changeLight(dirVec);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else {
            block();
        }
    }
}
