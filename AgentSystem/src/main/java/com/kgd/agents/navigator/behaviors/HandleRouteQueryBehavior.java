package com.kgd.agents.navigator.behaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.navigator.RouteNavigatorAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class HandleRouteQueryBehavior extends CyclicBehaviour {

    private final RouteNavigatorAgent agent;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HandleRouteQueryBehavior(RouteNavigatorAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        var template = MessageTemplate.MatchPerformative(ACLMessage.QUERY_REF);
        var message = agent.receive(template);

        if (message != null) {
            var response = message.createReply();
            try {
                var serializedRoute = objectMapper.writeValueAsString(agent.getCurrentRoute());
                response.setPerformative(ACLMessage.INFORM);
                response.setContent(serializedRoute);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
                response.setPerformative(ACLMessage.FAILURE);
                response.setContent(e.getMessage());
            }
            finally {
                agent.send(response);
            }
        }
        else
        {
            block();
        }
    }
}
