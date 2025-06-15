'use strict';

var path = require("path");
var electron = require("electron");
const { exec } = require('child_process');

var jarPath = path.join(__dirname, "apiTraceability-1.0-SNAPSHOT.jar");
const comando = 'java -jar '+jarPath.replace("\\menu\\","\\"); // O cualquier comando que sea necesario para ejecutar tu archivo Java
const child = exec(comando);

module.exports = function (electronApp, menuState) {
  electronApp.on('will-quit', () => {
    fetch('http://localhost:8080/exit')
  })

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
      }
  ]
  return menu;
}