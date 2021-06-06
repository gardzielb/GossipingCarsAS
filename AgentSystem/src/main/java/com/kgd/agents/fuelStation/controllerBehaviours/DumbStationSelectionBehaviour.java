package com.kgd.agents.fuelStation.controllerBehaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kgd.agents.fuelStation.FuelCarControllerAgent;
import com.kgd.agents.fuelStation.FuelStationData;
import com.kgd.agents.fuelStation.PriceSuggestion;
import com.kgd.agents.models.geodata.GeoPoint;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;

import java.util.*;
import java.util.stream.Collectors;

public class DumbStationSelectionBehaviour extends OneShotBehaviour {

    private final FuelCarControllerAgent agent;
    private final HashMap<AID, GeoPoint> locations = new HashMap<>();
    private final FuelStationData closestStation;

    public DumbStationSelectionBehaviour(FuelCarControllerAgent agent, FuelStationData closestStation) {
        this.agent = agent;
        this.closestStation = closestStation;
    }

    @Override
    public void action() {
        agent.negotiatedPrice = new PriceSuggestion(closestStation.stationId(), closestStation.location(), (float) closestStation.fuelPrice());

        String name = agent.getLocalName();
        name = name.substring(0, name.length() - "_fuel_controller".length());

        // request a new waypoint
        var message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(new AID(name+"_route_navigator", AID.ISLOCALNAME));
        try {
            message.setContent((new ObjectMapper()).writeValueAsString(
                    new GeoPoint[] { closestStation.location() }
            ));
        } catch (JsonProcessingException ignore) { }
        agent.send(message);
        agent.removeBehaviour(this);
    }
}
