'use strict';

var path = require("path");
var electron = require("electron");

// const { exec } = require('child_process');

// const comando = 'java -jar D:\\Documentos\\EPI\\BPbSw-Traceability\\api-traceability\\target\\api-traceability-1.0-SNAPSHOT.jar'; // O cualquier comando que sea necesario para ejecutar tu archivo Java
// const child = exec(comando);

// // Manejar la salida del proceso
// child.stdout.on('data', (data) => {
//   console.log(`Salida estándar: ${data}`);
// });

// child.stderr.on('data', (data) => {
//   console.error(`Error en la salida estándar: ${data}`);
// });

// child.on('close', (code) => {
//   console.log(`Proceso hijo finalizado con código de salida ${code}`);
// });

module.exports = function (electronApp, menuState) {
  return menu(electronApp, menuState);
}

function menu(electronApp, menuState){

  var menu = [
    {
        label: 'Toggle Tooltips',
        accelerator: 'Alt+t',
        enabled: function () {
            return menuState.bpmn;
        },

        action: function () {
            electronApp.emit('menu:action', 'toggleTooltipInfos');
        }
    },{
        label: "Open Traceability",
        accelerator: "CommandOrControl+[",
        enabled: function () {
          return menuState.bpmn;
        },
        action: function () {
          openFile();
        },
      }
  ]
  return menu;
}

function openFile() {
  var shell = electron.shell;

  // Ruta completa al archivo .exe que deseas abrir
  var exePath = path.join(__dirname, "Traceability.exe");

  // Abrir el archivo .exe
  shell.openPath("file://" + exePath.replace("\\menu\\","\\"));
}
