package org.miniprojects.entity;

import java.util.Objects;

public class Server {
    private final String serverId;
    private final int weight;
    private boolean healthy = true;

    public Server(String serverId, int weight) {
        if(weight <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
        this.serverId = serverId;
        this.weight = weight;
    }

    public String getServerId() {
        return serverId;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return Objects.equals(serverId, server.serverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serverId);
    }
}
