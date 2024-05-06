#!/bin/bash

docker compose down

mvn clean package -DskipTests

npm install -g bpmn-to-image

docker compose build
docker compose up -d