package com.DataBase.Extraction;

import com.DataBase.DAO.DAOManager;
import com.DataBase.DAO.ElementDAO;
import com.DataBase.DAO.ProcessDAO;
import com.DataBase.Interface.IDataExtractor;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class BPMNInfoExtractor implements IDataExtractor {

    private final JSONObject bpmnInfo;
    private final ArrayList<String> type = new ArrayList<String>();
    private final DAOManager daoManager;

    public BPMNInfoExtractor(JSONObject bpmnFilePath) {


        // Obtener la ruta del archivo
        this.bpmnInfo = bpmnFilePath;

        // Obtener la instancia del DAOManager
        this.daoManager = DAOManager.getInstance();

    }

    @Override
    public void insertData() {

        setProcess();
        setElementType();
        setElement();

    }

    private void setProcess(){
        daoManager.getProcessDAO().insertProcess(this.bpmnInfo.getString("bpmNameProcess"),this.bpmnInfo.getString("bpmNameFile"),this.bpmnInfo.getString("bpmPath"));
    }

    private void setElementType(){
        JSONArray p = this.bpmnInfo.getJSONArray("trace");
        for(Object i : p){
            JSONObject j = (JSONObject) i;

            if(!this.type.contains(j.getString("taskType")) && daoManager.getElementDAO().searchElementType(j.getString("taskType"))==-1){
                this.type.add(j.getString("taskType"));
                daoManager.getElementDAO().insertElementType(j.getString("taskType"));
            }
        }
    }

    private void setElement(){
        JSONArray p = this.bpmnInfo.getJSONArray("trace");
        for(Object i : p){
            JSONObject j = (JSONObject) i;
            int id_type = daoManager.getElementDAO().searchElementType(j.getString("taskType"));
            String name = j.getString("taskName");
            String lane = "Lane"; //cuando BPMN-Tracer traiga el lane, modificar esta linea
            int id_process = daoManager.getProcessDAO().searchProcess(this.bpmnInfo.getString("bpmNameProcess"),this.bpmnInfo.getString("bpmNameFile"),this.bpmnInfo.getString("bpmPath"));
            daoManager.getElementDAO().insertElement(id_type, name, lane, id_process);
        }
    }
}
