#!/bin/bash

# ScyllaDB Cluster Management Script

set -e

# Get the directory where this script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
COMPOSE_FILE="$PROJECT_ROOT/docker/docker-compose.yml"

function show_help() {
    echo "ScyllaDB Cluster Management"
    echo ""
    echo "Usage: ./scylla-cluster.sh [COMMAND]"
    echo ""
    echo "Commands:"
    echo "  start                Start the ScyllaDB cluster (3 nodes)"
    echo "  start-light          Start lightweight single-node cluster (low memory)"
    echo "  start-with-monitoring Start the cluster with monitoring (Prometheus + Grafana)"
    echo "  stop                 Stop the cluster"
    echo "  stop-light           Stop the lightweight cluster"
    echo "  restart              Restart the cluster"
    echo "  status               Show cluster status"
    echo "  logs                 Show logs from all nodes"
    echo "  logs [node]          Show logs from specific node (scylla1, scylla2, scylla3)"
    echo "  shell [node]         Connect to ScyllaDB shell (default: scylla1)"
    echo "  nodetool [command]   Run nodetool command on scylla1"
    echo "  clean                Stop and remove all containers and volumes"
    echo "  clean-light          Clean lightweight cluster"
    echo "  memory-check         Check system memory and Docker limits"
    echo "  help                 Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./scylla-cluster.sh start"
    echo "  ./scylla-cluster.sh start-light     # For low-memory systems"
    echo "  ./scylla-cluster.sh shell scylla2"
    echo "  ./scylla-cluster.sh nodetool status"
    echo "  ./scylla-cluster.sh logs scylla1"
    echo "  ./scylla-cluster.sh memory-check"
}

function start_cluster() {
    echo "Starting ScyllaDB cluster..."
    docker-compose -f $COMPOSE_FILE up -d scylla1 scylla2 scylla3
    echo "Cluster is starting. This may take a few minutes..."
    echo "You can check the status with: ./scylla-cluster.sh status"
    echo ""
    echo "Connection details:"
    echo "  Node 1: localhost:9042"
    echo "  Node 2: localhost:9043"
    echo "  Node 3: localhost:9044"
}

function start_light_cluster() {
    echo "Starting lightweight ScyllaDB single-node cluster..."
    docker-compose -f "$PROJECT_ROOT/docker/docker-compose.light.yml" up -d
    echo "Lightweight cluster is starting. This may take a few minutes..."
    echo "You can check the status with: docker-compose -f $PROJECT_ROOT/docker/docker-compose.light.yml ps"
    echo ""
    echo "Connection details:"
    echo "  ScyllaDB: localhost:9042"
    echo "  API: localhost:10000"
    echo ""
    echo "Note: This is a single-node setup optimized for low-memory environments."
}

function start_with_monitoring() {
    echo "Starting ScyllaDB cluster with monitoring..."
    docker-compose -f $COMPOSE_FILE --profile monitoring up -d
    echo "Cluster and monitoring are starting..."
    echo ""
    echo "Connection details:"
    echo "  ScyllaDB Node 1: localhost:9042"
    echo "  ScyllaDB Node 2: localhost:9043"
    echo "  ScyllaDB Node 3: localhost:9044"
    echo "  Prometheus: http://localhost:9090"
    echo "  Grafana: http://localhost:3000 (admin/admin)"
}

function stop_cluster() {
    echo "Stopping ScyllaDB cluster..."
    docker-compose -f $COMPOSE_FILE down
}

function restart_cluster() {
    echo "Restarting ScyllaDB cluster..."
    stop_cluster
    sleep 5
    start_cluster
}

function show_status() {
    echo "ScyllaDB Cluster Status:"
    echo "========================"
    docker-compose -f $COMPOSE_FILE ps
    echo ""
    echo "Health status:"
    docker-compose -f $COMPOSE_FILE exec scylla1 nodetool status 2>/dev/null || echo "Cluster not ready yet or nodes are starting..."
}

function show_logs() {
    local node=${1:-""}
    if [ -n "$node" ]; then
        echo "Showing logs for $node:"
        docker-compose -f $COMPOSE_FILE logs -f $node
    else
        echo "Showing logs for all nodes:"
        docker-compose -f $COMPOSE_FILE logs -f scylla1 scylla2 scylla3
    fi
}

function connect_shell() {
    local node=${1:-"scylla1"}
    echo "Connecting to $node CQL shell..."
    docker-compose -f $COMPOSE_FILE exec $node cqlsh
}

function run_nodetool() {
    local command="$*"
    if [ -z "$command" ]; then
        command="status"
    fi
    echo "Running: nodetool $command"
    docker-compose -f $COMPOSE_FILE exec scylla1 nodetool $command
}

function clean_cluster() {
    echo "Warning: This will remove all containers and data volumes!"
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "Cleaning up..."
        docker-compose -f $COMPOSE_FILE down -v
        docker-compose -f $COMPOSE_FILE --profile monitoring down -v
        echo "Cleanup complete."
    else
        echo "Cleanup cancelled."
    fi
}

function clean_light_cluster() {
    echo "Warning: This will remove lightweight cluster containers and data volumes!"
    read -p "Are you sure? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        echo "Cleaning up lightweight cluster..."
        docker-compose -f "$PROJECT_ROOT/docker/docker-compose.light.yml" down -v
        echo "Cleanup complete."
    else
        echo "Cleanup cancelled."
    fi
}

function stop_light_cluster() {
    echo "Stopping lightweight ScyllaDB cluster..."
    docker-compose -f "$PROJECT_ROOT/docker/docker-compose.light.yml" down
}

function check_memory() {
    echo "System Memory Check:"
    echo "==================="
    
    # Check system memory
    if command -v free &> /dev/null; then
        echo "System memory:"
        free -h
    elif [[ "$OSTYPE" == "darwin"* ]]; then
        echo "System memory (macOS):"
        echo "Total: $(sysctl -n hw.memsize | awk '{print $1/1024/1024/1024 " GB"}')"
        echo "Available: $(vm_stat | grep "Pages free" | awk '{print $3*4096/1024/1024 " MB"}' | sed 's/\.//')"
    fi
    
    echo ""
    echo "Docker Memory Settings:"
    echo "======================"
    
    # Check Docker memory limit
    if command -v docker &> /dev/null; then
        echo "Docker system info:"
        docker system info | grep -E "(Total Memory|Memory)"
        
        echo ""
        echo "Running containers memory usage:"
        docker stats --no-stream --format "table {{.Container}}\t{{.MemUsage}}\t{{.MemPerc}}" 2>/dev/null || echo "No running containers"
    else
        echo "Docker not found"
    fi
    
    echo ""
    echo "Recommendations:"
    echo "==============="
    echo "- For full 3-node cluster: 4GB+ system RAM recommended"
    echo "- For lightweight single-node: 2GB+ system RAM recommended"
    echo "- On macOS: Increase Docker Desktop memory allocation in Preferences"
    echo "- On Linux: Ensure sufficient system memory and swap"
    echo ""
    echo "If memory issues persist, try:"
    echo "  ./scylla-cluster.sh start-light  # Single node, minimal memory"
}

# Main script logic
case "${1:-help}" in
    start)
        start_cluster
        ;;
    start-light)
        start_light_cluster
        ;;
    start-with-monitoring)
        start_with_monitoring
        ;;
    stop)
        stop_cluster
        ;;
    stop-light)
        stop_light_cluster
        ;;
    restart)
        restart_cluster
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs "${2:-}"
        ;;
    shell)
        connect_shell "${2:-scylla1}"
        ;;
    nodetool)
        shift
        run_nodetool "$@"
        ;;
    clean)
        clean_cluster
        ;;
    clean-light)
        clean_light_cluster
        ;;
    memory-check)
        check_memory
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo "Unknown command: $1"
        echo ""
        show_help
        exit 1
        ;;
esac
