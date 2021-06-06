package com.kgd.agents.fuelStation.controllerBehaviours;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.maps.model.PlaceType;
import com.kgd.agents.fuelStation.FuelCarControllerAgent;
import com.kgd.agents.fuelStation.FuelStationData;
import com.kgd.agents.models.geodata.GeoPoint;
import com.kgd.agents.models.geodata.Place;
import com.kgd.agents.models.messages.CarLocationData;
import com.kgd.agents.services.HttpPlaceService;
import com.kgd.agents.services.HttpRouteService;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.Property;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class PrepareForNegotiationsBehaviour extends OneShotBehaviour {
    private final FuelCarControllerAgent agent;
    public PrepareForNegotiationsBehaviour(FuelCarControllerAgent agent) { this.agent = agent; }

    @Override
    public void action() {
        double kmRadius = agent.currentCapacity / agent.combustion * 100;

        var carLocationInfo = getCarLocationData();

        var nearbyStations = findNearbyStations(carLocationInfo.position(), kmRadius);

        if (nearbyStations.isEmpty()) {
            agent.onRouteToStation = false;
            return;
        }

        var stationsData = nearbyStations.stream().map(
                station -> findStationDetails(station, carLocationInfo.position(), carLocationInfo.destinationId())
        ).toList();

        if(agent.isDumb())
        {
            var closestStation = stationsData.stream().min(Comparator.comparingDouble(FuelStationData::routeDistance));
            System.out.printf("I'm dumb as a brick and %f,%f is the closest, going there%n", closestStation.orElseThrow().location().x(), closestStation.orElseThrow().location().y());
            agent.addBehaviour(new DumbStationSelectionBehaviour(agent, closestStation.orElseThrow()));
        }
        else
        {
            agent.addBehaviour(new PriceNegotiationInitiatorBehavior(agent, stationsData));
        }
    }

    private CarLocationData getCarLocationData() {
        var name = agent.getLocalName();
        var destinationName = name.substring(0, name.length() - "_fuel_controller".length());
        var destinationAID = new AID(destinationName, AID.ISLOCALNAME);

        var message = new ACLMessage(ACLMessage.REQUEST);
        message.addReceiver(destinationAID);
        agent.send(message);

        var mt = MessageTemplate.and(
                MessageTemplate.MatchSender(destinationAID),
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST)
        );

        var reply = agent.blockingReceive(mt);
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

    private FuelStationData findStationDetails(Place station, GeoPoint currentLocation, String destinationId) {
        double routeDistance = findDistanceForStation(station, currentLocation, destinationId);

        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setName(station.id());
        dfd.addServices(sd);

        try {
            DFAgentDescription[] result = DFService.search(agent, dfd);
            ServiceDescription sf = (ServiceDescription) (result[0].getAllServices().next());
            var iter = sf.getAllProperties();

            Property p = new Property();

            while (!"price".equals(p.getName()) && iter.hasNext()) {
                p = (Property) (iter.next());
            }

            return new FuelStationData(
                    result[0].getName(),
                    station.location(),
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
}
