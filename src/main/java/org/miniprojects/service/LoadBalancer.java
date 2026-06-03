package org.miniprojects.service;

import org.miniprojects.entity.Server;

public interface LoadBalancer {
    void register(Server server);
    void remove(String serverId);
    Server getNextServer();
    void markServerHealthy(String serverId);
    void markServerUnhealthy(String serverId);
}
