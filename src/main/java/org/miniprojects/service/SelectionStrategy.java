package org.miniprojects.service;

import org.miniprojects.entity.Server;

import java.util.List;

public interface SelectionStrategy {
    Server select(List<Server> listOfServers);
}
