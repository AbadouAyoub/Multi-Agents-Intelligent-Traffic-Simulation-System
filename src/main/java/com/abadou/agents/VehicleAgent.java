package com.abadou.agents;

import com.abadou.ui.TrafficApp;
import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class VehicleAgent extends Agent {

    private String directionInit = "NORD";
    private String direction = "NORD";
    private String intentionDirection = "TOUT_DROIT";
    private boolean virageEffectue = false;

    private double x, y;
    private static final double VITESSE = 1.0;
    private static final double LONG_VHL = 30;
    private static final double STOP_MARGIN = 5;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0)
            directionInit = args[0].toString().toUpperCase();
        if (args != null && args.length > 1)
            intentionDirection = args[1].toString().toUpperCase();

        direction = directionInit;

        switch (directionInit) {
            case "NORD" -> { x = 360; y = 0; }
            case "SUD"  -> { x = 440; y = 800; }
            case "EST"  -> { x = 800; y = 360; }
            case "OUEST"-> { x = 0;   y = 440; }
        }

        addBehaviour(new TickerBehaviour(this, 50) {
            protected void onTick() {
                boolean feuVert = switch (direction) {
                    case "NORD" -> TrafficApp.isFeuVert("FeuN");
                    case "SUD"  -> TrafficApp.isFeuVert("FeuS");
                    case "EST"  -> TrafficApp.isFeuVert("FeuE");
                    case "OUEST"-> TrafficApp.isFeuVert("FeuO");
                    default     -> true;
                };

                boolean doitStop = switch (direction) {
                    case "NORD"  -> !feuVert && (y + LONG_VHL >= 340 - STOP_MARGIN) && (y + LONG_VHL <= 340 + STOP_MARGIN);
                    case "SUD"   -> !feuVert && (y <= 460 + STOP_MARGIN) && (y >= 460 - STOP_MARGIN);
                    case "EST"   -> !feuVert && (x <= 460 + STOP_MARGIN) && (x >= 460 - STOP_MARGIN);
                    case "OUEST" -> !feuVert && (x + LONG_VHL >= 340 - STOP_MARGIN) && (x + LONG_VHL <= 340 + STOP_MARGIN);
                    default      -> false;
                };

                if (!doitStop) {
                    if (!virageEffectue && intentionDirection.equals("TOURNER_DROITE")) {
                        if (directionInit.equals("NORD") && y >= 380) {
                            direction = "EST"; y = 360; virageEffectue = true;
                        } else if (directionInit.equals("SUD") && y <= 420) {
                            direction = "OUEST"; y = 440; virageEffectue = true;
                        } else if (directionInit.equals("EST") && x <= 420) {
                            direction = "SUD"; x = 440; virageEffectue = true;
                        } else if (directionInit.equals("OUEST") && x >= 380) {
                            direction = "NORD"; x = 360; virageEffectue = true;
                        }
                    } else if (!virageEffectue && intentionDirection.equals("TOURNER_GAUCHE")) {
                        if (directionInit.equals("NORD") && y >= 460) {
                            direction = "OUEST"; y = 440; virageEffectue = true;
                        } else if (directionInit.equals("SUD") && y <= 340) {
                            direction = "EST"; y = 360; virageEffectue = true;
                        } else if (directionInit.equals("EST") && x <= 340) {
                            direction = "NORD"; x = 360; virageEffectue = true;
                        } else if (directionInit.equals("OUEST") && x >= 460) {
                            direction = "SUD"; x = 440; virageEffectue = true;
                        }
                    }

                    switch (direction) {
                        case "NORD"  -> y += VITESSE;
                        case "SUD"   -> y -= VITESSE;
                        case "EST"   -> x -= VITESSE;
                        case "OUEST" -> x += VITESSE;
                    }
                }

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("UI", AID.ISLOCALNAME));
                msg.setContent("POSITION:" + getLocalName() + ":" + x + ":" + y + ":" + direction);
                send(msg);
            }
        });
    }
}
