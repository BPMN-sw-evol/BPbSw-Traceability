package com.DataBase.Processor;

import com.DataBase.Extraction.commonDataExtraction;
import com.DataBase.Interface.IDataExtractor;
import com.DataBase.Extraction.BPMNInfoExtractor;
import com.DataBase.Extraction.ProjectInfoExtractor;
import org.json.JSONObject;

import java.sql.Timestamp;


public class DataProcessor {

    public void executeAllExtractors(JSONObject projectFilePath, JSONObject bpmnFilePath, String name) {
        new BPMNInfoExtractor(bpmnFilePath).insertData();
        new ProjectInfoExtractor(projectFilePath).insertData();
        new commonDataExtraction(projectFilePath, bpmnFilePath, name).insertData();
    }

    public void executeAllDeletes(Timestamp date ) {
        new BPMNInfoExtractor().deleteData(date);
        new ProjectInfoExtractor().deleteData(date);
        new commonDataExtraction().deleteData(date);
    }

}
