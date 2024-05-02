#!/bin/bash

# Function to run commands and log output with date and time stamps
run_and_log() {
    # Check if output file exists, create if it doesn't
    if [ ! -e output.log ]; then
        touch output.log
    fi
    # Run command and append output with date and time stamps to output.log
    {
        echo "[$(date +'%Y-%m-%d %H:%M:%S')] Running command: $@"
        "$@"
    } >> output.log 2>&1
}

# Check if Java 21 is installed
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F. '{print $1}')
if [[ $java_version -ne 21 ]]; then
    echo "Java 21 is not installed. Installing..."
    # Install Java 21 (assuming a Linux environment with apt package manager)
    sudo apt update
    sudo apt install openjdk-21-jdk -y
fi

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Maven is not installed. Installing..."
    # Install Maven (assuming a Linux environment with apt package manager)
    sudo apt update
    sudo apt install maven -y
fi

# Run Maven clean package command
echo "Running Maven clean package command..."
run_and_log mvn -DJAR_NAME=build clean package -DskipTests

# Run Java command in the background
echo "Running Java command in the background..."
run_and_log java -jar -Dserver.port=8080 ./target/build.jar &

echo "Process started in the background. Check output.log for logs."
