'use strict';

var path = require("path");
var electron = require("electron");

// var jarPath = path.join(__dirname, "api-traceability-1.0-SNAPSHOT.jar");
// console.log(jarPath);
// const comando = 'java -jar '+jarPath.replace("\\menu\\","\\"); // O cualquier comando que sea necesario para ejecutar tu archivo Java
// const child = exec(comando);

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
