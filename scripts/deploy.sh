#!/bin/bash

# Check if the correct number of arguments are provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <docker_username> <docker_password>"
    exit 1
fi

# Variables
DOCKER_USERNAME="$1"
DOCKER_PASSWORD="$2"
PG_PASSWORD="$3"
CLICKHOUSE_PASSWORD="$4"

# Log in to Docker Hub
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

cd ./deploy

# Replace the placeholder with the actual password in the docker-compose.yml file
sed -i "s/{PG_PASSWORD}/$PG_PASSWORD/g" docker-compose.yml
sed -i "s/{CLICKHOUSE_PASSWORD}/$CLICKHOUSE_PASSWORD/g" docker-compose.yml


# Stop and remove existing Docker Compose services
docker-compose down

# Pull the latest images (optional, if you want to ensure you have the latest images)
docker-compose pull

# Start a new Docker Compose setup
docker-compose up -d