package com.abadou.core;

import java.util.*;

public class Intersection {
    private final String id;
    private final List<ITrafficLight> trafficLights;
    private final Map<ITrafficLight, Long> lastGreenTime;
    private final long MIN_GREEN_DURATION = 10_000; // 10 sec
    private int currentIndex = 0;

    public Intersection(String id, List<ITrafficLight> trafficLights) {
        this.id = id;
        this.trafficLights = trafficLights;
        this.lastGreenTime = new HashMap<>();
        long now = System.currentTimeMillis();
        for (ITrafficLight light : trafficLights) {
            lastGreenTime.put(light, now);
            light.setEtat("ROUGE");
        }
    }

    public void step() {
        long now = System.currentTimeMillis();
        ITrafficLight current = trafficLights.get(currentIndex);

        if (canTurnGreen(current, now)) {
            setOnlyGreen(current);
            lastGreenTime.put(current, now);
            System.out.println("[INTERSECTION " + id + "] -> " + current.getLocalName() + " passe au VERT.");
        }

        currentIndex = (currentIndex + 1) % trafficLights.size();
    }

    private boolean canTurnGreen(ITrafficLight light, long now) {
        long lastGreen = lastGreenTime.getOrDefault(light, 0L);
        return (now - lastGreen) >= MIN_GREEN_DURATION;
    }

    private void setOnlyGreen(ITrafficLight toGreen) {
        for (ITrafficLight light : trafficLights) {
            light.setEtat(light == toGreen ? "VERT" : "ROUGE");
        }
    }

    public String getId() {
        return id;
    }
}