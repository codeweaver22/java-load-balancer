package org.miniprojects.service;

import org.junit.jupiter.api.Test;
import org.miniprojects.entity.Server;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class LoadBalancerImplTest {

    LoadBalancerImpl loadBalancer;

    @Test
    void shouldReturnOnlyRegisteredServersUsingRandomStrategy() {
        loadBalancer = new LoadBalancerImpl(new RandomSelectionStrategy());

        Server server1 = new Server("server-1", 1);
        Server server2 = new Server("server-2", 1);
        Server server3 = new Server("server-3", 1);

        loadBalancer.register(server1);
        loadBalancer.register(server2);
        loadBalancer.register(server3);

        Set<Server> expectedServers = Set.of(server1, server2, server3);
        Set<Server> returnedServers = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            Server server = loadBalancer.getNextServer();
            returnedServers.add(server);
        }

        assertEquals(expectedServers, returnedServers);
    }

    @Test
    void shouldReturnServerInRoundRobin() {
        loadBalancer = new LoadBalancerImpl(new RoundRobinSelectionStrategy());

        Server server1 = new Server("server-1", 1);
        Server server2 = new Server("server-2", 1);
        Server server3 = new Server("server-3", 1);

        loadBalancer.register(server1);
        loadBalancer.register(server2);
        loadBalancer.register(server3);

        assertEquals(server1, loadBalancer.getNextServer());
        assertEquals(server2, loadBalancer.getNextServer());
        assertEquals(server3, loadBalancer.getNextServer());
        assertEquals(server1, loadBalancer.getNextServer());
        assertEquals(server2, loadBalancer.getNextServer());
        assertEquals(server3, loadBalancer.getNextServer());

    }

    @Test
    void shouldNotReturnRemovedServer() {
        loadBalancer = new LoadBalancerImpl(new RandomSelectionStrategy());
        Server server1 = new Server("server-1", 1);
        Server server2 = new Server("server-2", 1);
        Server server3 = new Server("server-3", 1);

        loadBalancer.register(server1);
        loadBalancer.register(server2);
        loadBalancer.register(server3);

        loadBalancer.remove("server-2");

        for (int i = 0; i < 100; i++) {
            Server server = loadBalancer.getNextServer();
            assertNotEquals("server-2", server.getServerId());
        }
    }

    @Test
    void shouldReturnExceptionWhenNoServerRegistered() {
        loadBalancer = new LoadBalancerImpl(new RandomSelectionStrategy());

        assertThrows(IllegalStateException.class,
                () -> loadBalancer.getNextServer());
    }

    @Test
    void shouldIgnoreDuplicateRegistration() {
        loadBalancer = new LoadBalancerImpl(new RandomSelectionStrategy());

        Server server1 = new Server("server-1", 1);
        Server server1Duplicate = new Server("server-1", 1);

        loadBalancer.register(server1);
        loadBalancer.register(server1Duplicate);

        assertEquals(server1, loadBalancer.getNextServer());
        assertEquals(server1, loadBalancer.getNextServer());

    }

    @Test
    void shouldNotFailWhenRemovingUnknownServer() {
        loadBalancer = new LoadBalancerImpl(new RandomSelectionStrategy());

        Server server1 = new Server("server-1", 1);

        loadBalancer.register(server1);

        loadBalancer.remove("server-2");

        assertDoesNotThrow(
                () -> loadBalancer.getNextServer()
        );
    }

    @Test
    void shouldReturnServerAccordingToWeight() {
        loadBalancer = new LoadBalancerImpl(new WeightedRoundRobinSelectionStrategy());

        Server server1 = new Server("server-1", 3);
        Server server2 = new Server("server-2", 2);
        Server server3 = new Server("server-3", 1);

        loadBalancer.register(server1);
        loadBalancer.register(server2);
        loadBalancer.register(server3);

        Map<String, Integer> serverCount = new HashMap();
        for (int i = 0; i < 6; i++) {
            Server server = loadBalancer.getNextServer();
            serverCount.merge(server.getServerId(), 1, Integer::sum);
        }

        assertEquals(3, serverCount.get("server-1"));
        assertEquals(2, serverCount.get("server-2"));
        assertEquals(1, serverCount.get("server-3"));
    }

}