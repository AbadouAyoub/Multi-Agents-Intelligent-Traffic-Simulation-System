package com.abadou.agents;

import com.abadou.ui.TrafficApp;
import com.abadou.utils.Sensor;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VehicleAgent extends Agent {

    private String direction;
    private double x, y;
    private static final double LONGUEUR_VEHICULE = 30;
    private static final double STOP_MARGIN = 5;
    private static final double VITESSE = 1.0;

    private Sensor capteur; // Capteur de proximité
    private Map<String, double[]> voisins = new ConcurrentHashMap<>();

    @Override
    protected void setup() {
        Object[] args = getArguments();
        direction = (args != null && args.length > 0) ? args[0].toString().toUpperCase() : "NORD";

        // modification vers la voie de droite voie 1
        switch (direction) {
            case "NORD"  -> { x = 360; y = 0; }
            case "SUD"   -> { x = 440; y = 800; }
            case "EST"   -> { x = 800; y = 360; }
            case "OUEST" -> { x = 0;   y = 440; }
        }


        capteur = new Sensor(40); // Rayon de détection = 40 px

        // ✅ Boucle principale du déplacement
        addBehaviour(new TickerBehaviour(this, 50) {
            protected void onTick() {

                // Vérifier si feu devant est vert
                boolean feuVert = switch (direction) {
                    case "NORD"  -> TrafficApp.isFeuVert("FeuN");
                    case "SUD"   -> TrafficApp.isFeuVert("FeuS");
                    case "EST"   -> TrafficApp.isFeuVert("FeuE");
                    case "OUEST" -> TrafficApp.isFeuVert("FeuO");
                    default      -> true;
                };

                // Arrêt au feu rouge (zone d’arrêt devant la ligne)
                boolean doitStop = switch (direction) {
                    case "NORD" -> !feuVert && (y + LONGUEUR_VEHICULE >= 340 - STOP_MARGIN) && (y + LONGUEUR_VEHICULE <= 340 + STOP_MARGIN);
                    case "SUD"  -> !feuVert && (y <= 460 + STOP_MARGIN) && (y >= 460 - STOP_MARGIN);
                    case "EST"  -> !feuVert && (x <= 460 + STOP_MARGIN) && (x >= 460 - STOP_MARGIN);
                    case "OUEST"-> !feuVert && (x + LONGUEUR_VEHICULE >= 340 - STOP_MARGIN) && (x + LONGUEUR_VEHICULE <= 340 + STOP_MARGIN);
                    default     -> false;
                };

                // Vérifie s’il y a un véhicule devant
                boolean obstacleDevant = voisins.entrySet().stream().anyMatch(entry -> {
                    double[] pos = entry.getValue();
                    return capteur.isInRange(x, y, pos[0], pos[1]) && estDevant(pos[0], pos[1]);
                });

                // Si obstacle trop proche → envoyer un message DANGER
                if (obstacleDevant) {
                    ACLMessage alerte = new ACLMessage(ACLMessage.INFORM);
                    alerte.setContent("DANGER:" + getLocalName() + ":Obstacle devant");
                    for (String nom : voisins.keySet()) {
                        alerte.addReceiver(new AID(nom, AID.ISLOCALNAME));
                    }
                    send(alerte);
                }

                // Avancer si pas d’obstacle ni feu rouge
                if (!doitStop && !obstacleDevant) {
                    switch (direction) {
                        case "NORD"  -> y += VITESSE;
                        case "SUD"   -> y -= VITESSE;
                        case "EST"   -> x -= VITESSE;
                        case "OUEST" -> x += VITESSE;
                    }
                }

                // Envoi de position à UIAgent
                ACLMessage uiMsg = new ACLMessage(ACLMessage.INFORM);
                uiMsg.addReceiver(getAID("UI"));
                uiMsg.setContent("POSITION:" + getLocalName() + ":" + x + ":" + y);
                send(uiMsg);

                // Envoi de position aux autres véhicules connus
                for (String nom : voisins.keySet()) {
                    ACLMessage pos = new ACLMessage(ACLMessage.INFORM);
                    pos.setContent("POSITION:" + getLocalName() + ":" + x + ":" + y);
                    pos.addReceiver(new AID(nom, AID.ISLOCALNAME));
                    send(pos);
                }

                // Sortie d’écran → reset
                if (x < 0) x = 800;
                if (x > 800) x = 0;
                if (y < 0) y = 800;
                if (y > 800) y = 0;
            }
        });

        // ✅ Réception des messages ACL : POSITION, DANGER, INTENTION
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String content = msg.getContent();
                    String[] parts = content.split(":");

                    if (parts[0].equals("POSITION")) {
                        // Mise à jour position du voisin
                        String nom = parts[1];
                        if (!nom.equals(getLocalName())) {
                            double x2 = Double.parseDouble(parts[2]);
                            double y2 = Double.parseDouble(parts[3]);
                            voisins.put(nom, new double[]{x2, y2});
                        }
                    } else if (parts[0].equals("DANGER")) {
                        System.out.println(getLocalName() + " a reçu un DANGER de " + parts[1] + ": " + parts[2]);
                        // TODO : ralentir, contourner ou s’arrêter
                    } else if (parts[0].equals("INTENTION")) {
                        System.out.println(getLocalName() + " a reçu une INTENTION de " + parts[1] + ": " + parts[2]);
                        // TODO : coordination (laisser passer, etc.)
                    }
                } else {
                    block();
                }
            }
        });
    }

    // Détecte si une autre voiture est devant moi
    private boolean estDevant(double x2, double y2) {
        return switch (direction) {
            case "NORD"  -> y2 > y && Math.abs(x2 - x) < 20;
            case "SUD"   -> y2 < y && Math.abs(x2 - x) < 20;
            case "EST"   -> x2 < x && Math.abs(y2 - y) < 20;
            case "OUEST" -> x2 > x && Math.abs(y2 - y) < 20;
            default      -> false;
        };
    }
}
