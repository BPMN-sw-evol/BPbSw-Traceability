'use strict';

var path = require("path");
var electron = require("electron");

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
