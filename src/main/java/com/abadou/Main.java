package com.abadou;

import com.abadou.ui.TrafficApp;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import javafx.application.Application;

public class Main {

    public static void main(String[] args) {
        // Lancer l'interface graphique JavaFX
        new Thread(() -> Application.launch(TrafficApp.class)).start();

        try {
            Runtime rt = Runtime.instance();
            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.GUI, "true");
            ContainerController container = rt.createMainContainer(profile);

            // Agents communs
            container.createNewAgent("UI", "com.abadou.agents.UIAgent", null).start();
            container.createNewAgent("FeuN", "com.abadou.agents.TrafficLightAgent", null).start();
            container.createNewAgent("FeuS", "com.abadou.agents.TrafficLightAgent", null).start();
            container.createNewAgent("FeuE", "com.abadou.agents.TrafficLightAgent", null).start();
            container.createNewAgent("FeuO", "com.abadou.agents.TrafficLightAgent", null).start();
            container.createNewAgent("Coordinator", "com.abadou.agents.CoordinatorAgent", null).start();

            // ================================
            // 🔁 ACTIVER LE SCÉNARIO DÉSIRÉ :
            // ================================
<<<<<<< HEAD

            // scenario1_CroisementBasique(container); // ✅ Inactif pour ce test
            // scenario2_Depassement(container);        // ✅ Actif pour ce test
            // scenario3_ViragesOpposes(container);
            scenario4_DemiTour(container);
=======
            // scenario1_CroisementBasique(container);
            // scenario2_FluxIntelligent(container);
            // scenario3_ViragesOpposes1(container);
            // scenario3_ViragesOpposes2(container);
            // scenario4_DemiTour(container);
            scenario5_ObstacleDetection(container);
>>>>>>> 6370002 (ajout de scenarios)

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

<<<<<<< HEAD
    // 🔷 Scénario 1 : Croisement simple 4 véhicules
=======
    // Scénario 1 : Croisement simple 4 véhicules
>>>>>>> 6370002 (ajout de scenarios)
    public static void scenario1_CroisementBasique(ContainerController container) throws Exception {
        container.createNewAgent("VehicleNord", "com.abadou.agents.VehicleAgent", new Object[]{"NORD"}).start();
        container.createNewAgent("VehicleSud", "com.abadou.agents.VehicleAgent", new Object[]{"SUD"}).start();
        container.createNewAgent("VehicleEst", "com.abadou.agents.VehicleAgent", new Object[]{"EST"}).start();
        container.createNewAgent("VehicleOuest", "com.abadou.agents.VehicleAgent", new Object[]{"OUEST"}).start();
    }
<<<<<<< HEAD

    // 🔷 Scénario 2 : Véhicule fixe à mi-distance + dépassement voie 2
    public static void scenario2_Depassement(ContainerController container) throws Exception {
        // 🚗 Véhicule fixe (obstacle)
        container.createNewAgent("VehicleFixeNord", "com.abadou.agents.VehicleAgent", new Object[]{"NORD", "ARRET"}).start();

        // 🚙 Véhicule mobile qui dépasse
        Thread.sleep(2000); // laisse le temps à l'obstacle de s'installer
        container.createNewAgent("VehicleDepasseurNord", "com.abadou.agents.VehicleAgent", new Object[]{"NORD", "DEPASSER"}).start();
    }

    // 🔷 Scénario 3 : Deux véhicules se croisent, l’un tourne à droite (Nord→Est), l’autre à gauche (Sud→Est)
    public static void scenario3_ViragesOpposes(ContainerController container) throws Exception {
=======
    // Scenario 2: Analyse intelligente du flux
    public static void scenario2_FluxIntelligent(ContainerController container) throws Exception {
        container.createNewAgent("VehicleNord1", "com.abadou.agents.VehicleAgent", new Object[]{"NORD"}).start();
        Thread.sleep(500);
        container.createNewAgent("VehicleNord2", "com.abadou.agents.VehicleAgent", new Object[]{"NORD"}).start();
        Thread.sleep(500);
        container.createNewAgent("VehicleNord3", "com.abadou.agents.VehicleAgent", new Object[]{"NORD"}).start();
        Thread.sleep(500);
        container.createNewAgent("VehicleNord4", "com.abadou.agents.VehicleAgent", new Object[]{"NORD"}).start();
        Thread.sleep(500);
        container.createNewAgent("VehicleOuest", "com.abadou.agents.VehicleAgent", new Object[]{"OUEST"}).start();
    }

    // 🔷 Scénario 3 : Deux véhicules se croisent, l’un tourne à droite (Nord→Est), l’autre à gauche (Sud→Est)
    public static void scenario3_ViragesOpposes1(ContainerController container) throws Exception {
>>>>>>> 6370002 (ajout de scenarios)
        // 🚗 Véhicule venant du Nord et tournant à droite (vers l'Est)
        container.createNewAgent("VehicleNordTourneDroite", "com.abadou.agents.VehicleAgent",
                new Object[]{"NORD", "TOURNER_DROITE"}).start();

        // ⏳ Petit délai pour éviter les collisions immédiates
        Thread.sleep(1000);

        // 🚗 Véhicule venant du Sud et tournant à gauche (vers l'Est aussi)
        container.createNewAgent("VehicleSudTourneGauche", "com.abadou.agents.VehicleAgent",
                new Object[]{"SUD", "TOURNER_GAUCHE"}).start();
    }

<<<<<<< HEAD
    // 🔷 Scénario 4 : Demi-tour d’un véhicule (NORD → SUD via 2 virages à gauche)
    public static void scenario4_DemiTour(ContainerController container) throws Exception {
        container.createNewAgent("VehicleDemiTour", "com.abadou.agents.VehicleAgent", new Object[]{"NORD", "DEMI_TOUR"}).start();
    }

}
=======


    // Scenario 3: Virages opposés
    public static void scenario3_ViragesOpposes2(ContainerController container) throws Exception {
        container.createNewAgent("VehicleNord", "com.abadou.agents.VehicleAgent", new Object[]{"NORD", "TOURNER_GAUCHE"}).start();
        Thread.sleep(2000);
        container.createNewAgent("VehicleSud", "com.abadou.agents.VehicleAgent", new Object[]{"SUD", "TOURNER_GAUCHE"}).start();
    }

    // Scenario 4: Demi-tour
    public static void scenario4_DemiTour(ContainerController container) throws Exception {
        container.createNewAgent("VehicleNord", "com.abadou.agents.VehicleAgent", new Object[]{"NORD", "DEMI_TOUR"}).start();
    }



    // Scenario 5: Détection d'obstacle
    public static void scenario5_ObstacleDetection(ContainerController container) throws Exception {
        TrafficApp.setObstacle(360, 610);

        container.createNewAgent("VehicleNord1", "com.abadou.agents.VehicleAgent", new Object[]{"NORD", "TOUT_DROIT"}).start();
        Thread.sleep(16000); // VehicleNord1 à y≈320
        container.createNewAgent("VehicleNord2", "com.abadou.agents.VehicleAgent", new Object[]{"NORD", "TOUT_DROIT"}).start();
    }
}
>>>>>>> 6370002 (ajout de scenarios)
