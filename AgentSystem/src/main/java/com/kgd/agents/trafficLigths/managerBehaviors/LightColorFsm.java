package com.kgd.agents.trafficLigths.managerBehaviors;

import com.kgd.agents.models.geodata.TrafficLights;
import com.kgd.agents.services.LoggerFactory;
import jade.core.Agent;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class LightColorFsm {

    private static final Logger logger = LoggerFactory.getLogger("TL manager FSM");

    private LightState state;

    private final long defaultColorChangeTimeout;
    private final Map<String, PassingCarsData> carDataMap;

    public LightColorFsm(Agent managerAgent, TrafficLights lights1, TrafficLights lights2,
                         long defaultColorChangeTimeout) {
        this.defaultColorChangeTimeout = defaultColorChangeTimeout;
        carDataMap = Map.of(
                lights1.id(), new PassingCarsData(),
                lights2.id(), new PassingCarsData()
        );

        String greenLightsId = lights1.isGreen() ? lights1.id() : lights2.id();
        String redLightsId = lights1.isGreen() ? lights2.id() : lights1.id();
        state = new GreenRedLightState(this, managerAgent, greenLightsId, redLightsId, defaultColorChangeTimeout);
    }

    public void handleCarApproaching(String lightsId) {
        carDataMap.get(lightsId).approachingCount++;
        state.handleCarApproaching(lightsId);
    }

    public void handleCarPassing(String lightsId) {
        var carsData = carDataMap.get(lightsId);
        if (carsData.approachingCount > 0)
            carsData.approachingCount--;
        carsData.passingCount++;
    }

    public void handleCarExiting(String lightsId) {
        carDataMap.get(lightsId).passingCount--;
        state.handleCarExiting(lightsId);
    }

    public void setState(LightState state) {
        this.state = state;
    }

    public PassingCarsData getCarsData(String lightsId) {
        return carDataMap.get(lightsId);
    }

    public String getOtherLightsId(String lightsId) {
        return carDataMap.keySet().stream().filter(id -> !id.equals(lightsId)).findFirst().orElse("");
    }

    public long computeLightsTimeout(String lightsId) {
//        return defaultColorChangeTimeout;

        var lightsCarData = carDataMap.get(lightsId);
        var otherLightsCarData = carDataMap.get(getOtherLightsId(lightsId));

        logger.debug(
                "There are {} cars approaching to {} lights and {} from the other side",
                lightsCarData.approachingCount, lightsId, otherLightsCarData.approachingCount
        );

        if (otherLightsCarData.approachingCount == 0)
            return 2 * defaultColorChangeTimeout;

        float timeoutRatio = (float) lightsCarData.approachingCount / (float) otherLightsCarData.approachingCount;
        logger.debug("Timeout ratio for lights {} is {}", lightsId, timeoutRatio);

        return Math.min((long) (defaultColorChangeTimeout * timeoutRatio), 2 * defaultColorChangeTimeout);
    }

    static class PassingCarsData {
        public int approachingCount = 0;
        public int passingCount = 0;
    }
}
