package com.abadou.agents;

import com.abadou.ui.TrafficApp;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class VehicleAgent extends Agent {

    private String direction;
    private double x, y;
    private static final double LONGUEUR_VEHICULE = 10;
    private static final double STOP_MARGIN = 10;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        direction = (args != null && args.length > 0) ? args[0].toString().toUpperCase() : "NORD";

        // Position initiale selon la direction
        switch (direction) {
            case "NORD" -> { x = 380; y = 0; }     // ligne blanche = 400 ; droite = +10
            case "SUD"  -> { x = 420; y = 800; }
            case "EST"  -> { x = 800; y = 380; }   // ligne blanche = 400 ; droite = +10
            case "OUEST"-> { x = 0;   y = 420; }
        }


        addBehaviour(new TickerBehaviour(this, 50) {
            protected void onTick() {

                // Vérification de l’état du feu
                boolean feuVert = switch (direction) {
                    case "NORD"  -> TrafficApp.isFeuVert("FeuN");
                    case "SUD"   -> TrafficApp.isFeuVert("FeuS");
                    case "EST"   -> TrafficApp.isFeuVert("FeuE");
                    case "OUEST" -> TrafficApp.isFeuVert("FeuO");
                    default      -> true;
                };

                // Condition d’arrêt au feu rouge
                boolean doitStop = switch (direction) {
                    case "NORD" -> !feuVert && (y + LONGUEUR_VEHICULE >= 340 - STOP_MARGIN) && (y + LONGUEUR_VEHICULE <= 340 + STOP_MARGIN);
                    case "SUD"  -> !feuVert && (y <= 460 + STOP_MARGIN) && (y >= 460 - STOP_MARGIN);
                    case "EST"  -> !feuVert && (x <= 460 + STOP_MARGIN) && (x >= 460 - STOP_MARGIN);
                    case "OUEST"-> !feuVert && (x + LONGUEUR_VEHICULE >= 340 - STOP_MARGIN) && (x + LONGUEUR_VEHICULE <= 340 + STOP_MARGIN);
                    default     -> false;
                };

                // Déplacement s’il n’y a pas de feu rouge
                if (!doitStop) {
                    switch (direction) {
                        case "NORD"  -> y += 1;
                        case "SUD"   -> y -= 1;
                        case "EST"   -> x -= 1;
                        case "OUEST" -> x += 1;
                    }
                }

                // Envoi de la position à l’UIAgent
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(getAID("UI"));
                msg.setContent("POSITION:" + getLocalName() + ":" + x + ":" + y);
                send(msg);

                // Réapparition hors écran
                if (x < 0) x = 800;
                if (x > 800) x = 0;
                if (y < 0) y = 800;
                if (y > 800) y = 0;
            }
        });
    }
}
