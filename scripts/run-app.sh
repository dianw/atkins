#!/bin/bash

# Application Runner Script for Atkins Spring Boot Application

set -e

# Get the directory where this script is located and the project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

function show_help() {
    echo "Atkins Application Runner"
    echo ""
    echo "Usage: ./run-app.sh [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  start [profile]      Start the application with optional profile (dev, light, test)"
    echo "  build               Build the application"
    echo "  test                Run tests"
    echo "  clean               Clean build artifacts"
    echo "  package             Build and package the application"
    echo "  logs                Show application logs"
    echo "  status              Check if application is running"
    echo "  stop                Stop the application (if running in background)"
    echo "  help                Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./run-app.sh start           # Start with default profile"
    echo "  ./run-app.sh start dev       # Start with development profile"
    echo "  ./run-app.sh start light     # Start with light profile"
    echo "  ./run-app.sh build           # Build the application"
    echo "  ./run-app.sh test            # Run tests"
    echo "  ./run-app.sh logs            # Show application logs"
}

function start_app() {
    local profile=${1:-""}
    echo "Starting Atkins application..."
    
    cd "$PROJECT_ROOT"
    
    if [ -n "$profile" ]; then
        echo "Using profile: $profile"
        mvn spring-boot:run -Dspring-boot.run.profiles=$profile
    else
        echo "Using default profile"
        mvn spring-boot:run
    fi
}

function build_app() {
    echo "Building Atkins application..."
    cd "$PROJECT_ROOT"
    mvn clean compile
}

function test_app() {
    echo "Running tests..."
    cd "$PROJECT_ROOT"
    mvn test
}

function clean_app() {
    echo "Cleaning build artifacts..."
    cd "$PROJECT_ROOT"
    mvn clean
}

function package_app() {
    echo "Building and packaging application..."
    cd "$PROJECT_ROOT"
    mvn clean package
}

function show_logs() {
    echo "Showing application logs..."
    
    if [ -f "$PROJECT_ROOT/logs/app.log" ]; then
        tail -f "$PROJECT_ROOT/logs/app.log"
    else
        echo "No application log file found at $PROJECT_ROOT/logs/app.log"
        echo "You can check if the application is running and generating logs."
    fi
}

function check_status() {
    echo "Checking application status..."
    
    # Check if Spring Boot application is running on port 8080
    if curl -s http://localhost:8080/api/hello > /dev/null 2>&1; then
        echo "✅ Application is running on http://localhost:8080"
        echo "   Try: curl http://localhost:8080/api/hello"
    else
        echo "❌ Application is not responding on port 8080"
        echo "   Use './run-app.sh start' to start the application"
    fi
}

function stop_app() {
    echo "Stopping application..."
    # Find and kill Java processes running Spring Boot
    local pids=$(ps aux | grep 'spring-boot:run' | grep -v grep | awk '{print $2}')
    
    if [ -n "$pids" ]; then
        echo "Found running application processes: $pids"
        for pid in $pids; do
            echo "Stopping process $pid..."
            kill $pid
        done
        echo "Application stopped."
    else
        echo "No running application found."
    fi
}

# Main script logic
case "${1:-help}" in
    start)
        start_app "${2:-}"
        ;;
    build)
        build_app
        ;;
    test)
        test_app
        ;;
    clean)
        clean_app
        ;;
    package)
        package_app
        ;;
    logs)
        show_logs
        ;;
    status)
        check_status
        ;;
    stop)
        stop_app
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
