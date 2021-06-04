package com.kgd.agents.fuelStation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.model.PlaceType;
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
    @Override
    protected void setup() {
        super.setup();

        double kmRadius = Double.parseDouble(getArguments()[0].toString());

        var carLocationInfo = getCarLocationData();
        System.out.println(carLocationInfo);

        var nearbyStations = findNearbyStations(carLocationInfo.position(), kmRadius);
        nearbyStations.forEach(System.out::println);

        var stationsData = nearbyStations.stream().map(
                station -> findStationDetails(station, carLocationInfo.position(), carLocationInfo.destinationId())
        );

        var asList = stationsData.toList();

        var optimalPrices = OptimalFuelPriceCalculator
                .calculateOptimalFuelPricesAsPriceSuggestion(asList);

        addBehaviour(new PriceNegotiationInitiatorBehavior(this, optimalPrices));
    }

    private FuelStationData findStationDetails(Place station, GeoPoint currentLocation, String destinationId) {
        double routeDistance = findDistanceForStation(station, currentLocation, destinationId);

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(station.id());
        dfd.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(this, dfd);
            ServiceDescription sf = (ServiceDescription) (result[0].getAllServices().next());
            var iter = sf.getAllProperties();

            Property p = new Property();

            while (!"price".equals(p.getName()) && iter.hasNext()) {
                p = (Property) (iter.next());
            }

            return new FuelStationData(
                    result[0].getName(),
                    Float.parseFloat((String) p.getValue()),
                    routeDistance
            );
        } catch (FIPAException e) {
            e.printStackTrace();
            return null;
        }
    }

    private double findDistanceForStation(Place station, GeoPoint location, String destinationId) {
        var routeService = new HttpRouteService();
        try {
            var route = routeService.findRoute(location, destinationId, new GeoPoint[]{station.location()});
            return route.distance();
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<Place> findNearbyStations(GeoPoint location, double kmRadius) {
        var placeService = new HttpPlaceService();
        try {
            return placeService.findNearbyByType(PlaceType.GAS_STATION, location, kmRadius);
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private CarLocationData getCarLocationData() {
        var name = getLocalName();
        var destinationName = name.substring(0, name.length() - "_fuel_controller".length());
        var destinationAID = new AID(destinationName, AID.ISLOCALNAME);

        var message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(destinationAID);
        send(message);

        var reply = blockingReceive(MessageTemplate.MatchSender(destinationAID));
        if (reply != null) {
            try {
                return new ObjectMapper().readValue(reply.getContent(), CarLocationData.class);
            }
            catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        throw new RuntimeException();
    }
}
