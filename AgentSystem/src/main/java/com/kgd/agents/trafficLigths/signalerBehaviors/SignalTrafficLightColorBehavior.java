package com.kgd.agents.trafficLigths.signalerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.messages.TrafficLightExitData;
import com.kgd.agents.models.messages.TrafficLightNotification;
import com.kgd.agents.trafficLigths.NotificationType;
import com.kgd.agents.trafficLigths.TrafficLightSignalerAgent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SignalTrafficLightColorBehavior extends CyclicBehaviour {

    private final TrafficLightSignalerAgent agent;
    private final GeoPoint exitPoint;
    private final String managerName;
    private final ObjectMapper serializer = new ObjectMapper();

    public SignalTrafficLightColorBehavior(TrafficLightSignalerAgent agent, GeoPoint exitPoint, String managerName) {
        this.agent = agent;
        this.exitPoint = exitPoint;
        this.managerName = managerName;
    }

    @Override
    public void action() {
        var msgTemplate = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
        var isGreenQuery = agent.receive(msgTemplate);

        if (isGreenQuery != null) {
            var isGreenReply = isGreenQuery.createReply();
            if (agent.isGreen()) {
                try {
                    var managerNotification = new ACLMessage(ACLMessage.INFORM);
                    managerNotification.addReceiver(new AID(managerName, AID.ISLOCALNAME));

                    var notificationContent = new TrafficLightNotification(
                            NotificationType.PASS_THROUGH, isGreenQuery.getSender().getLocalName()
                    );
                    managerNotification.setContent(serializer.writeValueAsString(notificationContent));
                    agent.send(managerNotification);

                    isGreenReply.setPerformative(ACLMessage.AGREE);
                    var exitData = new TrafficLightExitData(managerName, exitPoint);
                    isGreenReply.setContent(serializer.writeValueAsString(exitData));
                }
                catch (JsonProcessingException e) {
                    throw new RuntimeException(e.getMessage());
                }
            }
            else {
                isGreenReply.setPerformative(ACLMessage.REFUSE);
            }

            agent.send(isGreenReply);
        }
        else {
            block();
        }
    }
}
