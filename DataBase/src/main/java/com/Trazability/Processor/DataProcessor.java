package com.Trazability.Processor;

import com.Trazability.Extraction.BPMNInfoExtractor;
import com.Trazability.Extraction.ProjectInfoExtractor;
import com.Trazability.Extraction.commonDataExtraction;
import com.Trazability.Interface.IDataExtractor;

import java.io.IOException;

public class DataProcessor {
    private static DataProcessor instance;
    private final commonDataExtraction commonDataExtractor;
    private final BPMNInfoExtractor bpmnDataExtractor;
    private final ProjectInfoExtractor projectDataExtractor;

    private DataProcessor(String projectFilePath, String bpmnFilePath, String name) throws IOException {
        // Inicializar instancias de los extractores de datos
        commonDataExtractor = new commonDataExtraction(projectFilePath, bpmnFilePath, name);
        bpmnDataExtractor = new BPMNInfoExtractor(bpmnFilePath);
        projectDataExtractor = new ProjectInfoExtractor(projectFilePath);

        // Ejecutar los métodos de extracción e inserción
        executeAllExtractors();
    }

    public static void getInstance(String projectFilePath, String bpmnFilePath, String name) throws IOException {
        if (instance == null) {
            instance = new DataProcessor(projectFilePath, bpmnFilePath, name);
        }
    }

    private void executeAllExtractors() throws IOException {
        executeExtractor(bpmnDataExtractor);
        executeExtractor(projectDataExtractor);
        executeExtractor(commonDataExtractor);

    }

    private void executeExtractor(IDataExtractor extractor) throws IOException {
        extractor.extractInfo();
        extractor.insertData();
    }
}
