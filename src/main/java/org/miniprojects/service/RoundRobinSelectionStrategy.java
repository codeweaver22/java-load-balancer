package org.miniprojects.service;

import org.miniprojects.entity.Server;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinSelectionStrategy implements SelectionStrategy {
    private final AtomicInteger currentIndex = new AtomicInteger();

    @Override
    public Server select(List<Server> listOfServers) {
        int index = currentIndex.getAndIncrement();
        return listOfServers.get(index % listOfServers.size());
    }
}
