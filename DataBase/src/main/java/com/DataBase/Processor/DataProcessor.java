package com.DataBase.Processor;

import com.DataBase.Extraction.commonDataExtraction;
import com.DataBase.Interface.IDataExtractor;
import com.DataBase.Extraction.BPMNInfoExtractor;
import com.DataBase.Extraction.ProjectInfoExtractor;
import org.json.JSONObject;

public class DataProcessor {
    private final commonDataExtraction commonDataExtractor;
    private final BPMNInfoExtractor bpmnDataExtractor;
    private final ProjectInfoExtractor projectDataExtractor;

    public DataProcessor(JSONObject projectFilePath, JSONObject bpmnFilePath, String name) {
        // Inicializar instancias de los extractores de datos
        commonDataExtractor = new commonDataExtraction(projectFilePath, bpmnFilePath, name);
        bpmnDataExtractor = new BPMNInfoExtractor(bpmnFilePath);
        projectDataExtractor = new ProjectInfoExtractor(projectFilePath);

        // Ejecutar los métodos de extracción e inserción
        executeAllExtractors();
    }

    private void executeAllExtractors() {
        executeExtractor(bpmnDataExtractor);
        executeExtractor(projectDataExtractor);
        executeExtractor(commonDataExtractor);
    }

    private void executeExtractor(IDataExtractor extractor) {
        extractor.insertData();
    }
}
