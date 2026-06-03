package org.miniprojects.service;

import org.miniprojects.entity.Server;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LoadBalancerImpl implements LoadBalancer {
    private final List<Server> listOfServers = new ArrayList<>();
    private final Map<String, Server> serverMap = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final SelectionStrategy selectionStrategy;

    public LoadBalancerImpl(SelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    @Override
    public void register(Server server) {
        lock.writeLock().lock();
        try {
            if (!serverMap.containsKey(server.getServerId())) {
                listOfServers.add(server);
                serverMap.put(server.getServerId(), server);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void remove(String serverId) {
        lock.writeLock().lock();
        try {
            if (serverMap.containsKey(serverId)) {
                Server server = serverMap.remove(serverId);
                listOfServers.remove(server);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Server getNextServer() {
        lock.readLock().lock();
        try {
            if (listOfServers.isEmpty()) {
                throw new IllegalStateException("No Servers registered");
            }

            List<Server> healthyServers = listOfServers.stream().filter(Server::isHealthy).toList();
            if (healthyServers.isEmpty()) {
                throw new IllegalStateException("No Healthy Server");
            }

            return selectionStrategy.select(healthyServers);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void markServerHealthy(String serverId) {
        lock.writeLock().lock();
        try {
            Server server = serverMap.get(serverId);
            if (server != null) {
                server.setHealthy(true);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void markServerUnhealthy(String serverId) {
        lock.writeLock().lock();
        try {
            Server server = serverMap.get(serverId);
            if (server != null) {
                server.setHealthy(false);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
