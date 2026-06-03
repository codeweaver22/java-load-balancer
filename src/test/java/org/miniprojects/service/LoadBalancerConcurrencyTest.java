package org.miniprojects.service;

import org.junit.jupiter.api.Test;
import org.miniprojects.entity.Server;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

public class LoadBalancerConcurrencyTest {
    LoadBalancer loadBalancer;

    @Test
    void shouldHandleConcurrentServerSelectionWithTC50() throws InterruptedException {
        loadBalancer = new LoadBalancerImpl(new RandomSelectionStrategy());

        Server server1 =
                new Server("server-1", 1);
        Server server2 =
                new Server("server-2", 1);
        Server server3 =
                new Server("server-3", 1);

        loadBalancer.register(server1);
        loadBalancer.register(server2);
        loadBalancer.register(server3);

        int threadCount = 50;

        ExecutorService executorService =
                Executors.newFixedThreadPool(threadCount);

        Set<String> returnedServers = ConcurrentHashMap.newKeySet();

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(
                    () -> {
                        try {
                            startLatch.await();
                            Server server = loadBalancer.getNextServer();
                            returnedServers.add(server.getServerId());
                        } catch (Exception e) {
                            fail(e);
                        } finally {
                            finishLatch.countDown();
                        }
                    }
            );
        }
        startLatch.countDown();
        finishLatch.await();

        Set<String> expectedServers =
                Set.of(
                        "server-1",
                        "server-2",
                        "server-3");

        assertTrue(expectedServers.containsAll(returnedServers));

        executorService.shutdown();
    }

    @Test
    void shouldHandleConcurrentServerSelectionWithTC5000() throws InterruptedException {
        loadBalancer = new LoadBalancerImpl(new RoundRobinSelectionStrategy());

        Server server1 =
                new Server("server-1", 1);
        Server server2 =
                new Server("server-2", 1);
        Server server3 =
                new Server("server-3", 1);

        loadBalancer.register(server1);
        loadBalancer.register(server2);
        loadBalancer.register(server3);

        int threadCount = 5000;

        ExecutorService executorService =
                Executors.newFixedThreadPool(threadCount);

        Map<String, AtomicInteger> serverCounts = new ConcurrentHashMap<>();

        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(
                    () -> {
                        try {
                            startLatch.await();
                            Server server = loadBalancer.getNextServer();
                            serverCounts.computeIfAbsent(server.getServerId(), k -> new AtomicInteger()).incrementAndGet();
                        } catch (Exception e) {
                            fail(e);
                        } finally {
                            finishLatch.countDown();
                        }
                    }
            );
        }
        startLatch.countDown();
        finishLatch.await();

        serverCounts.forEach(
                (serverId, count) -> {
                    System.out.println(serverId + "->" + count);
                }
        );

        executorService.shutdown();
    }
}
