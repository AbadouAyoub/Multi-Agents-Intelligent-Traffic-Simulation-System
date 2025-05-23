package com.abadou;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Main {
    public static void main(String[] args) {
        Runtime rt = Runtime.instance();
        Profile profile = new ProfileImpl();
        profile.setParameter(Profile.GUI, "true"); // active l'interface JADE
        profile.setParameter(Profile.MAIN_HOST, "localhost");
        ContainerController mainContainer = rt.createMainContainer(profile);

        try {
            AgentController vehicle = mainContainer.createNewAgent("Vehicle1", "com.abadou.agents.VehicleAgent", null);
            AgentController light = mainContainer.createNewAgent("TrafficLight1", "com.abadou.agents.TrafficLightAgent", null);
            vehicle.start();
            light.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}