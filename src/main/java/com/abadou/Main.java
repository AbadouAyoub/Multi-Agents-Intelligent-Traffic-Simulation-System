package com.abadou;

import com.abadou.ui.TrafficApp;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        // ✅ Lancer l'interface graphique JavaFX
        new Thread(() -> Application.launch(TrafficApp.class)).start();

        try {
            Runtime rt = Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.GUI, "true");
            ContainerController container = rt.createMainContainer(profile);

            // Agents standards
            container.createNewAgent("UI", "com.abadou.agents.UIAgent", null).start();
            container.createNewAgent("FeuN", "com.abadou.agents.TrafficLightAgent", null).start();
            container.createNewAgent("FeuS", "com.abadou.agents.TrafficLightAgent", null).start();
            container.createNewAgent("FeuE", "com.abadou.agents.TrafficLightAgent", null).start();
            container.createNewAgent("FeuO", "com.abadou.agents.TrafficLightAgent", null).start();
            container.createNewAgent("Coordinator", "com.abadou.agents.CoordinatorAgent", null).start();

            // 🚗 Scénario 1 : 4 véhicules, un par direction
            container.createNewAgent("VehicleNord", "com.abadou.agents.VehicleAgent", new Object[]{"NORD"}).start();
            container.createNewAgent("VehicleSud", "com.abadou.agents.VehicleAgent", new Object[]{"SUD"}).start();
            container.createNewAgent("VehicleEst", "com.abadou.agents.VehicleAgent", new Object[]{"EST"}).start();
            container.createNewAgent("VehicleOuest", "com.abadou.agents.VehicleAgent", new Object[]{"OUEST"}).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
