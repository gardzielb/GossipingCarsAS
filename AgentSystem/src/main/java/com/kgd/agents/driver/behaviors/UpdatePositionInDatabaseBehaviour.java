package com.kgd.agents.driver.behaviors;

import com.kgd.agents.driver.DriverAgent;
import com.kgd.agents.models.AgentLocation;
import com.kgd.agents.models.GeoPoint;
import com.kgd.agents.services.AgentLocationService;
import com.kgd.agents.services.HttpAgentLocationService;
import jade.core.behaviours.TickerBehaviour;

public class UpdatePositionInDatabaseBehaviour extends TickerBehaviour {
    private final AgentLocationService service = new HttpAgentLocationService();
    private final DriverAgent agent;

    public UpdatePositionInDatabaseBehaviour(DriverAgent agent, long period) {
        super(agent, period);
        this.agent = agent;
    }

    @Override
    protected void onTick() {
        if (agent.route == null || agent.routeSegment == agent.route.segments.size()) return;

        GeoPoint location = agent.getPosition();
        AgentLocation agentLocation = new AgentLocation(null, agent.getAID().toString(), new GeoPoint(location.x(), location.y()));

        service.addOrUpdateAgentLocation(agentLocation);
    }
}
