#!/bin/bash

#docker compose down

# 💡 Detener script si ocurre un error
set -e

echo "📦 Compilando proyecto con Maven..."
mvn clean package -DskipTests

# Ejecutar el creador de base de datos
echo "Ejecutando db_Initializer..."
java -jar db_Initializer/target/db_Initializer-1.0-SNAPSHOT.jar &
PID_CREATE=$!

# Ejecutar el DataBase
# echo "Ejecutando DataBase..."
# java -jar DataBase/target/DataBase-1.0-SNAPSHOT.jar &
# PID_DATABASE=$!

#wait $PID_DATABASE
wait $PID_CREATE

echo "⚙️ Instalando NVM si no está presente..."
if [ ! -d "$HOME/.nvm" ]; then
  curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.39.7/install.sh | bash
fi

# 👉 Cargar NVM en esta sesión
export NVM_DIR="$HOME/.nvm"
# shellcheck source=/dev/null
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"

echo "📥 Instalando Node.js v20 con NVM (si es necesario)..."
nvm install 20
nvm use 20

echo "📦 Instalando paquete global bpmn-to-image..."
npm install -g bpmn-to-image

echo "📁 Copiando .jar a la carpeta Plugin..."
cp ./api-traceability/target/api-traceability-1.0-SNAPSHOT.jar ./Plugin/

echo "✅ Build and setup completado."

#docker compose build
#docker compose up -d