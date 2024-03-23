package com.Trazability.Factory;

import com.Trazability.Extraction.BPMNInfoExtractor;
import com.Trazability.Extraction.ProjectInfoExtractor;
import com.Trazability.Extraction.commonDataExtraction;
import com.Trazability.Interface.DataExtractor;

import java.io.IOException;

public class DataExtractorFactory {


    public static boolean createAndExtractAllData(String projectFilePath, String bpmnFilePath, String name) throws IOException {

        // Crear instancias de los extractores de datos y extraer la información
        commonDataExtraction commonDataExtractor = new commonDataExtraction(projectFilePath, bpmnFilePath, name);
        DataExtractor bpmnDataExtractor = new BPMNInfoExtractor(bpmnFilePath);
        DataExtractor ProjectDataExtractor = new ProjectInfoExtractor(projectFilePath);

        // Extraer los datos
        commonDataExtractor.extractInfo();
        commonDataExtractor.insertData();
        bpmnDataExtractor.extractInfo();
        bpmnDataExtractor.insertData();
        ProjectDataExtractor.extractInfo();
        ProjectDataExtractor.insertData();

        return commonDataExtractor.isDataInitialized();
    }

}
