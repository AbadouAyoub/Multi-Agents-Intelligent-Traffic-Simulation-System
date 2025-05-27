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
    private String voie = "voie1";
    private String intention = "AVANCER";

    private double x, y;
    private static final double LONGUEUR_VEHICULE = 30;
    private static final double STOP_MARGIN = 5;
    private static final double VITESSE = 1.0;

    private Sensor capteur;
    private Map<String, double[]> voisins = new ConcurrentHashMap<>();
    private Map<String, String> intentions = new ConcurrentHashMap<>();

    @Override
    protected void setup() {
        Object[] args = getArguments();
        direction = (args != null && args.length > 0) ? args[0].toString().toUpperCase() : "NORD";

        switch (direction) {
            case "NORD" -> { x = 360; y = 0; }
            case "SUD" -> { x = 440; y = 800; }
            case "EST" -> { x = 800; y = 360; }
            case "OUEST" -> { x = 0;   y = 440; }
        }

        capteur = new Sensor(50);

        addBehaviour(new TickerBehaviour(this, 50) {
            protected void onTick() {
                boolean feuVert = switch (direction) {
                    case "NORD" -> TrafficApp.isFeuVert("FeuN");
                    case "SUD" -> TrafficApp.isFeuVert("FeuS");
                    case "EST" -> TrafficApp.isFeuVert("FeuE");
                    case "OUEST" -> TrafficApp.isFeuVert("FeuO");
                    default -> true;
                };

                boolean doitStopFeu = switch (direction) {
                    case "NORD" -> !feuVert && (y + LONGUEUR_VEHICULE >= 340 - STOP_MARGIN) && (y + LONGUEUR_VEHICULE <= 340 + STOP_MARGIN);
                    case "SUD" -> !feuVert && (y <= 460 + STOP_MARGIN) && (y >= 460 - STOP_MARGIN);
                    case "EST" -> !feuVert && (x <= 460 + STOP_MARGIN) && (x >= 460 - STOP_MARGIN);
                    case "OUEST" -> !feuVert && (x + LONGUEUR_VEHICULE >= 340 - STOP_MARGIN) && (x + LONGUEUR_VEHICULE <= 340 + STOP_MARGIN);
                    default -> false;
                };

                boolean dangerCollision = voisins.entrySet().stream().anyMatch(entry -> {
                    double[] pos = entry.getValue();
                    boolean estDevant = estDevant(pos[0], pos[1]);
                    boolean estProche = capteur.isInRange(x, y, pos[0], pos[1]);
                    boolean directionOpposee = directionOpposee(entry.getKey());
                    return estDevant && estProche && directionOpposee;
                });

                boolean intersectionOccupee = voisins.values().stream().anyMatch(pos ->
                        pos[0] >= 340 && pos[0] <= 460 && pos[1] >= 340 && pos[1] <= 460
                );

                boolean conflitDePriorite = intentions.entrySet().stream().anyMatch(entry -> {
                    String autreNom = entry.getKey();
                    String autreIntention = entry.getValue();
                    double[] pos = voisins.get(autreNom);
                    if (pos == null || autreIntention == null) return false;

                    boolean directionCroisee = directionCroiseeAvec(autreNom);
                    boolean procheIntersection = pos[0] >= 320 && pos[0] <= 480 && pos[1] >= 320 && pos[1] <= 480;

                    // Si l'autre veut avancer et j'ai PAS priorité sur lui → je m'arrête
                    return directionCroisee && procheIntersection &&
                            autreIntention.equals("AVANCER") &&
                            !aPrioriteSur(autreNom);
                });

                boolean doitStop = doitStopFeu || dangerCollision || intersectionOccupee || conflitDePriorite;

                if (!doitStop) {
                    switch (direction) {
                        case "NORD" -> y += VITESSE;
                        case "SUD" -> y -= VITESSE;
                        case "EST" -> x -= VITESSE;
                        case "OUEST" -> x += VITESSE;
                    }
                    intention = "AVANCER";
                } else {
                    intention = "ATTENTE";
                }

                ACLMessage uiMsg = new ACLMessage(ACLMessage.INFORM);
                uiMsg.addReceiver(getAID("UI"));
                uiMsg.setContent("POSITION:" + getLocalName() + ":" + x + ":" + y);
                send(uiMsg);

                for (Map.Entry<String, double[]> entry : voisins.entrySet()) {
                    String nom = entry.getKey();
                    double[] pos = entry.getValue();
                    if (capteur.isInRange(x, y, pos[0], pos[1])) {
                        sendMessage(nom, "POSITION:" + getLocalName() + ":" + x + ":" + y);
                        sendMessage(nom, "INTENTION:" + getLocalName() + ":" + intention);
                    }
                }

                if (x < 0) x = 800;
                if (x > 800) x = 0;
                if (y < 0) y = 800;
                if (y > 800) y = 0;
            }

            private void sendMessage(String nom, String content) {
                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.setContent(content);
                msg.addReceiver(new AID(nom, AID.ISLOCALNAME));
                send(msg);
            }
        });

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    String[] parts = msg.getContent().split(":");
                    if (parts[0].equals("POSITION") && parts.length >= 4) {
                        String nom = parts[1];
                        if (!nom.equals(getLocalName())) {
                            double x2 = Double.parseDouble(parts[2]);
                            double y2 = Double.parseDouble(parts[3]);
                            voisins.put(nom, new double[]{x2, y2});
                        }
                    } else if (parts[0].equals("INTENTION")) {
                        String nom = parts[1];
                        String intn = parts[2];
                        if (!nom.equals(getLocalName())) {
                            intentions.put(nom, intn);
                        }
                    }
                } else {
                    block();
                }
            }
        });
    }

    private boolean estDevant(double x2, double y2) {
        return switch (direction) {
            case "NORD" -> y2 > y && Math.abs(x2 - x) < 20;
            case "SUD" -> y2 < y && Math.abs(x2 - x) < 20;
            case "EST" -> x2 < x && Math.abs(y2 - y) < 20;
            case "OUEST" -> x2 > x && Math.abs(y2 - y) < 20;
            default -> false;
        };
    }

    private boolean directionOpposee(String nomAgent) {
        String nom = nomAgent.toUpperCase();
        return (direction.equals("NORD") && nom.contains("SUD")) ||
                (direction.equals("SUD") && nom.contains("NORD")) ||
                (direction.equals("EAST") && nom.contains("WEST")) ||
                (direction.equals("WEST") && nom.contains("EAST"));
    }

    private boolean directionCroiseeAvec(String nomAgent) {
        String nom = nomAgent.toUpperCase();
        return (direction.equals("NORD") || direction.equals("SUD")) && (nom.contains("EST") || nom.contains("OUEST")) ||
                (direction.equals("EST") || direction.equals("OUEST")) && (nom.contains("NORD") || nom.contains("SUD"));
    }

    private boolean aPrioriteSur(String nomAgent) {
        String dirOppose = "";
        if (nomAgent.toUpperCase().contains("NORD")) dirOppose = "NORD";
        else if (nomAgent.toUpperCase().contains("SUD")) dirOppose = "SUD";
        else if (nomAgent.toUpperCase().contains("EST")) dirOppose = "EST";
        else if (nomAgent.toUpperCase().contains("OUEST")) dirOppose = "OUEST";

        return switch (direction) {
            case "NORD" -> dirOppose.equals("EST");
            case "EST" -> dirOppose.equals("SUD");
            case "SUD" -> dirOppose.equals("OUEST");
            case "OUEST" -> dirOppose.equals("NORD");
            default -> false;
        };
    }
}
