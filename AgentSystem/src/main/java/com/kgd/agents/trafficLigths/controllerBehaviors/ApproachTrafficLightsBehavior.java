package com.kgd.agents.trafficLigths.controllerBehaviors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.Main;
import com.kgd.agents.models.messages.TrafficLightExitData;
import com.kgd.agents.services.LoggerFactory;
import com.kgd.agents.trafficLigths.TrafficLightsCarControllerAgent;
import jade.core.AID;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ApproachTrafficLightsBehavior extends SimpleBehaviour {

    private static final Logger logger = LoggerFactory.getLogger("TL Car Controller");

    private final TrafficLightsCarControllerAgent controllerAgent;
    private final AID tlSignalerId;
    private final ObjectMapper deserializer = new ObjectMapper();
    private final LocalDateTime initTime = LocalDateTime.now();

    private boolean canPassTL = false;
    private boolean isAsking = true;
    private boolean isCarStopped = false;
    private LocalDateTime tlAskTime = LocalDateTime.now();

    public ApproachTrafficLightsBehavior(TrafficLightsCarControllerAgent controllerAgent, AID tlSignalerId) {
        this.controllerAgent = controllerAgent;
        this.tlSignalerId = tlSignalerId;
    }

    @Override
    public void action() {
        long waitingPeriod = ChronoUnit.MILLIS.between(tlAskTime, LocalDateTime.now());
        long tlResponseTimeout = 10 * 1000;
        boolean responseTimeoutReached = waitingPeriod > tlResponseTimeout * Main.getSimulationSpeed();

        if (isAsking || responseTimeoutReached) {
            logger.debug("Asking TL signaler if passage possible");

            var canPassQuery = new ACLMessage(ACLMessage.QUERY_IF);
            canPassQuery.addReceiver(tlSignalerId);
            controllerAgent.send(canPassQuery);

            isAsking = false;
            tlAskTime = LocalDateTime.now();
        }

        logger.debug("Waiting for TL signaler response");

        var msgTemplate = MessageTemplate.or(
                MessageTemplate.MatchPerformative(ACLMessage.AGREE),
                MessageTemplate.MatchPerformative(ACLMessage.REFUSE)
        );
        var canPassResponse = controllerAgent.receive(msgTemplate);

        if (canPassResponse != null) {
            handleTrafficLightsResponse(canPassResponse);
        }
        else {
            block();
        }
    }

    @Override
    public boolean done() {
        return canPassTL;
    }

    private void handleTrafficLightsResponse(ACLMessage canPassResponse) {
        String performative = canPassResponse.getPerformative() == ACLMessage.AGREE ? "AGREE" : "REFUSE";
        logger.debug("Received response {} from signaler", performative);

        if (canPassResponse.getPerformative() == ACLMessage.AGREE) {
            logger.debug("Starting the car");
            changeCarMovement("start");

            long tlInteractionTime = ChronoUnit.MILLIS.between(initTime, LocalDateTime.now());
            controllerAgent.saveTLWaitingTime(tlInteractionTime);

            try {
                var exitData = deserializer.readValue(canPassResponse.getContent(), TrafficLightExitData.class);
                controllerAgent.passBetweenTrafficLights(
                        new AID(exitData.agentName(), AID.ISLOCALNAME), exitData.exitPoint(),
                        tlSignalerId.getLocalName()
                );
                canPassTL = true;
            }
            catch (JsonProcessingException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        else if (!isCarStopped) {
            logger.debug("Stopping the car");
            changeCarMovement("stop");
            isCarStopped = true;
        }

        isAsking = canPassResponse.getPerformative() == ACLMessage.REFUSE;
    }

    private void changeCarMovement(String command) {
        String agentName = controllerAgent.getLocalName();
        String carName = agentName.substring(0, agentName.length() - "_TL_controller".length());
        var destinationAID = new AID(carName, AID.ISLOCALNAME);

        var stopRequest = new ACLMessage(ACLMessage.PROPOSE);
        stopRequest.addReceiver(destinationAID);
        stopRequest.setContent(command);

        controllerAgent.send(stopRequest);
    }
}
