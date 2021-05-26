package com.kgd.agents.fuelStation;

import com.kgd.agents.models.GeoPoint;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Locale;
import java.util.Random;

public class FuelStationManagerAgent extends Agent {

    private final float MAX_PRICE = 5.50f;
    private final float MIN_PRICE = 5.05f;
    private float fuelPrice;

    private GeoPoint stationLocation;

    private String stationName;

    private String stationId;

    @Override
    protected void setup() {

        Object[] args = getArguments();
        stationLocation = (GeoPoint) args[0];
        stationId = (String) args[1];

        Random random = new Random();
        fuelPrice = MIN_PRICE + random.nextFloat() * (MAX_PRICE - MIN_PRICE);

        registerStationInDF();

        SearchConstraints sc = new SearchConstraints();

        addBehaviour(new NegotiatePriceBehaviour(this, fuelPrice));
    }

    void registerStationInDF() {
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("fuelStation");
        sd.setName(stationId);
        sd.addProperties(new Property("location", String.format(Locale.ROOT, "%f,%f", stationLocation.x(), stationLocation.y())));
        sd.addProperties(new Property("price", fuelPrice));
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }
}
