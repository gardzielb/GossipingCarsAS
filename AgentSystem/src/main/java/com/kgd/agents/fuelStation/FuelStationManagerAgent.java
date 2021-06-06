package com.kgd.agents.fuelStation;

import com.kgd.agents.fuelStation.managerBehaviours.NegotiatePriceBehaviour;
import com.kgd.agents.models.geodata.GeoPoint;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class FuelStationManagerAgent extends Agent {

    private final float MAX_BASE_PRICE = 5.50f;
    private final float MIN_BASE_PRICE = 5.05f;

    private float fuelPrice;

    public GeoPoint stationLocation;
    private String stationId;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        stationLocation = (GeoPoint) args[0];
        stationId = (String) args[1];
        fuelPrice = (float) ThreadLocalRandom.current().nextDouble(MIN_BASE_PRICE, MAX_BASE_PRICE);

        registerStationInDF();

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
