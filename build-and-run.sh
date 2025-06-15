#!/bin/bash

#docker compose down

# 💡 Detener script si ocurre un error
set -e

echo "📦 Compilando proyecto con Maven..."
mvn clean package -DskipTests

# Ejecutar el creador de base de datos
echo "Ejecutando db_Initializer..."
java -jar dbInitializer/target/dbInitializer-1.0-SNAPSHOT.jar &
PID_CREATE=$!

wait $PID_CREATE

echo "📁 Copiando .jar a la carpeta Plugin..."
cp ./apiTraceability/target/apiTraceability-1.0-SNAPSHOT.jar ./plugin/

echo "✅ Build and setup completado."

#docker compose build
#docker compose up -d