#!/bin/bash

docker compose down

mvn clean package -DskipTests

docker compose build
docker compose up -d