#!/bin/bash

# Check if the correct number of arguments are provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <docker_username> <docker_password>"
    exit 1
fi

# Variables
DOCKER_USERNAME="$1"
DOCKER_PASSWORD="$2"
IMAGE_NAME="mmieczkowski/serverguard:latest"
CONTAINER_NAME="serverguard-api"

# Log in to Docker Hub
echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin

# Pull the new image
docker pull "$IMAGE_NAME"

# Stop the existing container
docker stop "$CONTAINER_NAME"

# Remove the existing container
docker rm "$CONTAINER_NAME"

# Run the new image
docker run -d --name "$CONTAINER_NAME" "$IMAGE_NAME"