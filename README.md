# Load Balancer Library

A lightweight Java load balancer implementation demonstrating multiple server selection strategies, thread-safe design, and health-aware routing.

## Features

* Random Selection Strategy
* Round Robin Strategy
* Weighted Round Robin Strategy
* Health-aware Server Selection
* Thread-safe implementation using ReadWriteLock
* AtomicInteger-based round robin counters
* Unit Tests using JUnit 5

## Design

The library follows the Strategy Pattern to allow different server selection algorithms to be plugged into the load balancer.

### Supported Strategies

#### Random Selection

Selects a server randomly from the available healthy servers.

#### Round Robin

Distributes requests evenly across healthy servers.

#### Weighted Round Robin

Distributes requests based on server weight.

Example:

Server A (weight=3)

Server B (weight=1)

Request distribution:

A → A → A → B → A → A → A → B

## Health Checks

Servers can be marked healthy or unhealthy.

Only healthy servers participate in request routing.

## Thread Safety

The implementation supports concurrent access using:

* ReadWriteLock for server registry operations
* AtomicInteger for round robin counters

## Technologies

* Java 21
* Maven
* JUnit 5

## Usage

```java
LoadBalancer loadBalancer =
        new LoadBalancerImpl(
                new RoundRobinSelectionStrategy());

loadBalancer.register(
        new Server("server-1", 1));

loadBalancer.register(
        new Server("server-2", 1));

Server server =
        loadBalancer.getNextServer();
```

## Future Enhancements

* Smooth Weighted Round Robin
* Dynamic Health Check Monitoring
* Metrics Collection
* Load-based Routing
* Least Connections Strategy

## Learning Goals

This project was built to practice:

* Object-Oriented Design
* Strategy Pattern
* Concurrent Programming
* Thread Safety
* Unit Testing
* Backend Infrastructure Concepts
