package com.Trazability.Extraction;

import com.Trazability.DAO.*;
import com.Trazability.Interface.IDataExtractor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class BPMNInfoExtractor implements IDataExtractor {

    private String bpmnFilePath;
    private JSONObject bpmnInfo;
    private ArrayList<String> type = new ArrayList<String>();
    private ProcessDAO processDAO;
    private ElementDAO elementDAO;

    public BPMNInfoExtractor(String bpmnFilePath) {
        // Obtener la ruta del archivo
        this.bpmnFilePath = bpmnFilePath;

        // Obtener la instancia del DAOManager
        DAOManager daoManager = DAOManager.getInstance();

        // Obtener intancias de los DAO's
        this.processDAO = daoManager.getProcessDAO();
        this.elementDAO = daoManager.getElementDAO();
    }

    @Override
    public void extractInfo() throws IOException {
        try {
            BufferedReader file = new BufferedReader(new java.io.FileReader(bpmnFilePath));
            StringBuilder s = new StringBuilder();
            String line;
            while ((line = file.readLine()) != null) {
                s.append(line);
            }
            this.bpmnInfo = new JSONObject(s.toString());
        } catch (FileNotFoundException e) {
            System.out.println("Error al leer el archivo BPMN: " + e.getMessage());
        }
    }

    @Override
    public void insertData() throws IOException {

        extractInfo();
        setProcess();
        setElementType();
        setElement();

    }

    private void setProcess(){
        processDAO.insertProcess(this.bpmnInfo.getString("bpmNameProcess"),this.bpmnInfo.getString("bpmNameFile"),this.bpmnInfo.getString("bpmPath"));
    }

    private void setElementType(){
        JSONArray p = this.bpmnInfo.getJSONArray("trace");
        for(Object i : p){
            JSONObject j = (JSONObject) i;

            if(!this.type.contains(j.getString("taskType")) && elementDAO.searchElementType(j.getString("taskType"))==-1){
                this.type.add(j.getString("taskType"));
                elementDAO.insertElementType(j.getString("taskType"));
            }
        }
    }

    private void setElement(){
        JSONArray p = this.bpmnInfo.getJSONArray("trace");
        for(Object i : p){
            JSONObject j = (JSONObject) i;
            int id_type = elementDAO.searchElementType(j.getString("taskType"));
            String name = j.getString("taskName");
            String lane = "Lane"; //cuando BPMN-Tracer traiga el lane, modificar esta linea
            int id_process = processDAO.searchProcess(this.bpmnInfo.getString("bpmNameProcess"),this.bpmnInfo.getString("bpmNameFile"),this.bpmnInfo.getString("bpmPath"));
            elementDAO.insertElement(id_type, name, lane, id_process);
        }
    }
}
