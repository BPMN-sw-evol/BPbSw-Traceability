#!/bin/bash

#docker compose down

mvn clean package -DskipTests

npm install -g bpmn-to-image

# Copiar el archivo desde la carpeta .\api-traceability\target\ a la carpeta .\Plugin\
cp ./api-traceability/target/api-traceability-1.0-SNAPSHOT.jar ./Plugin/

# docker compose build
# docker compose up -d