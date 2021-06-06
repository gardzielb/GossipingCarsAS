package com.kgd.agents.fuelStation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.model.PlaceType;
import com.kgd.agents.fuelStation.controllerBehaviours.UpdateFuelLevelBehaviour;
import com.kgd.agents.models.messages.CarLocationData;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.Place;
import com.kgd.agents.services.HttpPlaceService;
import com.kgd.agents.services.HttpRouteService;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.List;

public class FuelCarControllerAgent extends Agent {
    public final double combustion = 8.0;
    public final double capacity = 20.0;
    public double currentCapacity = 2.05;
    public boolean onRouteToStation = false;

    private boolean isDumb = false;

    public PriceSuggestion negotiatedPrice;

    @Override
    protected void setup() {
        super.setup();
        Object[] args = getArguments();
        isDumb = Boolean.parseBoolean((String) args[0]);
        addBehaviour(new UpdateFuelLevelBehaviour(this));
    }

    public boolean isDumb() {
        return isDumb;
    }
}
