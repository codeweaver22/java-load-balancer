package org.miniprojects.service;

import org.miniprojects.entity.Server;

import java.util.List;
import java.util.Random;

public class RandomSelectionStrategy implements SelectionStrategy {
    private final Random random = new Random();

    @Override
    public Server select(List<Server> listOfServers) {
        return listOfServers.get(random.nextInt(listOfServers.size()));
    }
}
