package org.miniprojects.service;

import org.miniprojects.entity.Server;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WeightedRoundRobinSelectionStrategy implements SelectionStrategy {
    private final AtomicInteger currentIndex = new AtomicInteger();

    @Override
    public Server select(List<Server> listOfServers) {
        List<Server> weightedServers = new ArrayList<>();
        for (Server server : listOfServers) {
            for (int i = 0; i < server.getWeight(); i++) {
                weightedServers.add(server);
            }
        }

        int index = currentIndex.getAndIncrement();
        return weightedServers.get(index % weightedServers.size());
    }
}
