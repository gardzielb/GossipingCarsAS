package com.kgd.agents;

import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class JadeTest {
    public static void main(String[] args) {
        var jadeRuntime = jade.core.Runtime.instance();
        var containerController = createContainer(jadeRuntime, "Sracz");
        startAgent("AndrzejDupa", TestingAgent.class, new Object[]{"21.37", "37.21"}, containerController);
    }

    public static ContainerController createContainer(Runtime jadeRuntime, String name) {
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.CONTAINER_NAME, name);
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        return jadeRuntime.createAgentContainer(profile);
    }

    public static void startAgent(String agentName, Class<? extends Agent> agentClass, Object[] args,
                                  ContainerController container) {
        try {
            AgentController agentController = container.createNewAgent(agentName, agentClass.getName(), args);
            agentController.start();
        }
        catch (StaleProxyException e) {
            e.printStackTrace();
        }
    }
}
