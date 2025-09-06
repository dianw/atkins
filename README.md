# Atkins Spring Boot Application

A Spring Boot web application with REST API endpoints and ScyllaDB integration.

## Features

- Spring Boot 3.1.5
- Java 21
- Maven build system
- Spring Web starter for REST APIs
- Spring Data Cassandra for ScyllaDB integration
- ScyllaDB cluster support
- Docker Compose for development environment

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Docker and Docker Compose

### ScyllaDB Cluster Setup

This project includes a Docker Compose configuration for a 3-node ScyllaDB cluster for development and testing.

#### Starting the ScyllaDB Cluster

Use the provided management script:

```bash
# Start the basic cluster
./scripts/scylla-cluster.sh start

# Start with monitoring (Prometheus + Grafana)
./scripts/scylla-cluster.sh start-with-monitoring

# Check cluster status
./scripts/scylla-cluster.sh status

# View cluster logs
./scripts/scylla-cluster.sh logs

# Connect to CQL shell
./scripts/scylla-cluster.sh shell

# Run nodetool commands
./scripts/scylla-cluster.sh nodetool status
```

#### Manual Docker Compose Commands

```bash
# Start the cluster
docker-compose -f docker/docker-compose.yml up -d

# Start with monitoring
docker-compose -f docker/docker-compose.yml --profile monitoring up -d

# Stop the cluster
docker-compose -f docker/docker-compose.yml down

# Clean up (removes volumes)
docker-compose -f docker/docker-compose.yml down -v
```

#### ScyllaDB Connection Details

- **Node 1**: `localhost:9042`
- **Node 2**: `localhost:9043`
- **Node 3**: `localhost:9044`

#### Monitoring (when enabled)

- **Prometheus**: http://localhost:9090
- **Grafana**: http://localhost:3000 (admin/admin)

### Running the Application

1. Start the ScyllaDB cluster:
```bash
./scripts/scylla-cluster.sh start
```

2. Wait for the cluster to be ready (check with `./scripts/scylla-cluster.sh status`)

3. Run the application:
```bash
# Using the convenient application runner script
./scripts/run-app.sh start dev          # With development profile for detailed logging
./scripts/run-app.sh start              # With default profile
./scripts/run-app.sh start light        # With light profile

# Or directly with Maven
mvn spring-boot:run -Dspring-boot.run.profiles=dev
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Application Management

The `run-app.sh` script provides convenient commands for managing the application:

```bash
# Start application with different profiles
./scripts/run-app.sh start dev          # Development profile
./scripts/run-app.sh start light        # Light profile for resource-constrained environments

# Build and test
./scripts/run-app.sh build              # Build the application
./scripts/run-app.sh test               # Run tests
./scripts/run-app.sh package            # Build and package

# Monitor and control
./scripts/run-app.sh status             # Check if application is running
./scripts/run-app.sh logs               # Show application logs
./scripts/run-app.sh stop               # Stop the application
```

### Creating Keyspace and Tables

Connect to the ScyllaDB cluster and create the required keyspace:

```bash
./scripts/scylla-cluster.sh shell
```

Then in the CQL shell:

```cql
CREATE KEYSPACE IF NOT EXISTS atkins_keyspace
WITH replication = {
  'class': 'NetworkTopologyStrategy',
  'datacenter1': 3
}
AND durable_writes = true;

USE atkins_keyspace;

-- Example table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY,
    username TEXT,
    email TEXT,
    created_at TIMESTAMP
);
```

### Available Endpoints

- `GET /api/hello` - Returns a hello message
- `GET /api/status` - Returns application status

### Configuration Profiles

- **default**: Production-ready settings with LOCAL_QUORUM consistency
- **dev**: Development settings with LOCAL_ONE consistency and debug logging
- **test**: Test-specific settings (uses embedded test database)

### Building the Application

To build the application:

```bash
mvn clean package
```

To run tests:

```bash
mvn test
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── atkins/
│   │           ├── AtkinsApplication.java
│   │           └── controller/
│   │               └── HelloController.java
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── application-light.yml
├── test/
│   └── java/
│       └── com/
│           └── atkins/
│               └── AtkinsApplicationTests.java
docker/
├── docker-compose.yml
└── docker-compose.light.yml
scripts/
├── scylla-cluster.sh
└── run-app.sh
logs/
└── (application logs)
monitoring/
└── prometheus.yml
```

## Development Tips

1. **Cluster Management**: Use `./scripts/scylla-cluster.sh` for easy cluster management
2. **Application Management**: Use `./scripts/run-app.sh` for convenient application control
3. **Monitoring**: Enable monitoring profile to track cluster performance
4. **Development Profile**: Use development profile for detailed logging and debugging
5. **Data Persistence**: ScyllaDB data is persisted in Docker volumes between restarts
6. **Clean Start**: Use `./scripts/scylla-cluster.sh clean` to completely reset the cluster
7. **Log Management**: Application logs are stored in the `logs/` directory

## Troubleshooting

### Cluster Won't Start
- Ensure Docker has enough memory allocated (4GB+ recommended)
- Check Docker logs: `docker-compose -f docker/docker-compose.yml logs`
- Verify no port conflicts on 9042, 9043, 9044

### Application Can't Connect
- Verify cluster is running: `./scripts/scylla-cluster.sh status`
- Check if keyspace exists: `./scripts/scylla-cluster.sh shell`
- Ensure correct contact points in application.yml

### Performance Issues
- Monitor cluster with Grafana: http://localhost:3000
- Use `nodetool` commands via `./scripts/scylla-cluster.sh nodetool status`
- Check ScyllaDB logs for warnings
