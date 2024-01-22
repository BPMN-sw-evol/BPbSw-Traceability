package com.Trazability.ImageGenerate;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageCapture {
    
   public void imageCapture() {
    try {
        // Ruta relativa al modelo desde la raíz del proyecto
        String relativePathToModel = Paths.get("output", "ColorModel.bpmn").toString();

        // Ruta completa al modelo desde la raíz del proyecto
        String diagramFile = Paths.get(System.getProperty("user.dir"), relativePathToModel).toString();

        // Nombre del archivo de salida
        String outputConfig = "MSGF-Test-Color.png";

        // Construir el comando completo
        String fullCommand = String.format("bpmn-to-image %s;%s", diagramFile, outputConfig);

        // Ruta del directorio que contiene el archivo BPMN
        Path directorio = Paths.get("output");

        // Crear el proceso para ejecutar el comando en el directorio especificado
        ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/C", fullCommand);
        processBuilder.directory(directorio.toFile());

        // Iniciar el proceso
        Process p = processBuilder.start();

        // Esperar a que el proceso termine
        int exitCode = p.waitFor();

        // Imprimir el resultado
        // System.out.println((exitCode == 0) ? "Image generated successfully." : "Error generating image.");

    } catch (IOException | InterruptedException e) {
        e.printStackTrace();
    }
}

}

