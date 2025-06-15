package com.DataBase.Extraction;

import java.io.IOException;
import java.util.ArrayList;
import java.sql.Timestamp;

import com.DataBase.DAO.*;
import com.DataBase.Interface.IDataExtractor;
import org.json.JSONArray;
import org.json.JSONObject;


public class commonDataExtraction implements IDataExtractor {
    private JSONObject projectInfo;
    private JSONObject bpmnInfo;
    private int history;
    private final ArrayList<String> variables = new ArrayList<String>();
    private final ArrayList<String> projects = new ArrayList<String>();
    private final ArrayList<String> paths = new ArrayList<String>();
    private final DAOManager daoManager = DAOManager.getInstance();;

    public commonDataExtraction() {}


    public commonDataExtraction(JSONObject projectFilePath, JSONObject bpmnFilePath, String name) {
        // Obtener las rutas de los archivos
        this.projectInfo = projectFilePath;
        this.bpmnInfo = bpmnFilePath;

        // Utiliza DAO History
        this.history = daoManager.getHistoryDAO().createHistory(name);
    }

    @Override
    public void insertData() {

        for (String i : this.projectInfo.keySet()) {
            for (Object j : this.projectInfo.getJSONArray("ProjectPath")) {
                String[] p = j.toString().split("\\\\");
                if (p[p.length - 1].equals(i)) {
                    this.projects.add(i);
                    this.paths.add(j.toString());
                }
            }
        }

        setVariables();
        setMethodUsed();
        setElementUsed();

    }

    @Override
    public void deleteData(Timestamp date) {

        deleteVariable(date);
        deleteHistory(date);

    }

    private void setVariables(){
        JSONArray p = this.bpmnInfo.getJSONArray("trace");
        for(Object i : p){
            JSONObject j = (JSONObject) i;
            JSONArray k = j.optJSONArray("variables");
            if(k!=null){
                for(int l=0; l<k.length();l++){
                    if(!this.variables.contains(k.opt(l))){
                        this.variables.add(k.opt(l).toString());
                        daoManager.getVariableDAO().insertVariable(k.opt(l).toString(),this.history);
                    }
                }
            }
        }

        for (String i : this.variables) {
            int variable = daoManager.getVariableDAO().searchVariable(i,this.history);
            for (int j=0;j<this.projects.size();j++){
                int project = daoManager.getProjectDAO().searchProject(this.projects.get(j),this.paths.get(j));
                for (String k : this.projectInfo.getJSONObject(this.projects.get(j)).keySet()) {
                    for (String l: this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).keySet()) {
                        JSONArray v = this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).optJSONArray("variables");

                        if(v!=null){

                            for(Object m : v){
                                if(m.toString().equals(i) && this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).keySet().contains("container")){
                                    int container = daoManager.getContainerDAO().searchContainer(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).getString("container"),project);
                                    if(daoManager.getContainerDAO().searchContainedIn(variable, container)==-1){
                                        daoManager.getContainerDAO().insertContainedIn(variable, container);
                                    }
                                    break;
                                }
                            }
                        }else if(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).optString("variables").equals(i) && this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).keySet().contains("container")){
                            int container = daoManager.getContainerDAO().searchContainer(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).getString("container"),project);
                            if(daoManager.getContainerDAO().searchContainedIn(variable, container)==-1){
                                daoManager.getContainerDAO().insertContainedIn(variable, container);
                            }
                        }else if(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).optString("variables").equals(i)){
                            int container = daoManager.getContainerDAO().searchContainer("NA",project);
                            if(daoManager.getContainerDAO().searchContainedIn(variable, container)==-1){
                                daoManager.getContainerDAO().insertContainedIn(variable, container);
                            }
                        }
                    }
                }
            }
        }
    }



    private void setMethodUsed(){
        //setVariables();
        for (String i : this.variables) {
            int id_variable = daoManager.getVariableDAO().searchVariable(i,this.history);
            for (int j=0;j<this.projects.size();j++){
                int id_project = daoManager.getProjectDAO().searchProject(this.projects.get(j),this.paths.get(j));
                for (String k : this.projectInfo.getJSONObject(this.projects.get(j)).keySet()) {
                    for (String l: this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).keySet()) {
                        if(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).keySet().contains("variables")){
                            JSONArray v = this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).optJSONArray("variables");
                            if(v!=null){
                                for(Object m : v){
                                    if(m.toString().equals(i)){
                                        int id_class = daoManager.getClassDAO().searchClass(k.split(": ")[1],id_project);
                                        int id_method = daoManager.getMethodDAO().searchMethod(id_class,l.split(": ")[1].split(" ")[0]);

                                        if(l.toString().contains("set")){
                                            daoManager.getMethodDAO().insertMethodUsed(id_variable,id_method, true);
                                        }else{
                                            daoManager.getMethodDAO().insertMethodUsed(id_variable,id_method, false);
                                        }

                                        break;
                                    }
                                }
                            }else if(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).getString("variables").equals(i)){
                                int id_class = daoManager.getClassDAO().searchClass(k.split(": ")[1],id_project);
                                int id_method = daoManager.getMethodDAO().searchMethod(id_class,l.split(": ")[1].split(" ")[0]);

                                if(l.toString().contains("set")){
                                    daoManager.getMethodDAO().insertMethodUsed(id_variable,id_method, true);
                                }else{
                                    daoManager.getMethodDAO().insertMethodUsed(id_variable,id_method, false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    private void setElementUsed(){
        JSONArray p = this.bpmnInfo.getJSONArray("trace");
        ArrayList<String> serviceVariables = new ArrayList<String>();
        for(Object i : p){
            JSONObject j = (JSONObject) i;
            // if(j.getString("taskType").equals("Service Task")){
            //     for (String k : this.projectInfo.keySet()) {
            //         if(!k.equals("ProjectPath")){
            //             for (String l : this.projectInfo.getJSONObject(k).keySet()) {
            //                 boolean flag = false;
            //                 String element = null;
            //                 for (String m: this.projectInfo.getJSONObject(k).getJSONObject(l).keySet()) {
            //                     if(m.contains("Class") && this.projectInfo.getJSONObject(k).getJSONObject(l).getJSONObject(m).getString("name").equals(j.getString("taskName"))){
            //                         flag = true;
            //                         element = this.projectInfo.getJSONObject(k).getJSONObject(l).getJSONObject(m).getString("name");
            //                         continue;
            //                     }
            //                     if(flag){
            //                         JSONArray v = this.projectInfo.getJSONObject(k).getJSONObject(l).getJSONObject(m).optJSONArray("variables");
            //                         if(v!=null){
            //                             for(Object n : v){
            //                                 if(!serviceVariables.contains(n.toString())){
            //                                     serviceVariables.add(n.toString());
            //                                 }
            //                             }
            //                         }else{
            //                             if(!serviceVariables.contains(this.projectInfo.getJSONObject(k).getJSONObject(l).getJSONObject(m).getString("variables"))){
            //                                 serviceVariables.add(this.projectInfo.getJSONObject(k).getJSONObject(l).getJSONObject(m).getString("variables"));
            //                             }
            //                         }
            //                     }
            //                 }

            //                 if(!serviceVariables.isEmpty()){
            //                     for (String n : serviceVariables) {
            //                         int id_variable = daoManager.getVariableDAO().searchVariable(n,this.history);
            //                         int id_element = daoManager.getElementDAO().searchElement(element);
            //                         daoManager.getElementDAO().insertElementUsed(id_variable, id_element,"NA");
            //                     }
            //                     serviceVariables = new ArrayList<String>();
            //                 }
            //             }
            //         }
            //     }
            // }else{
                JSONArray k = j.optJSONArray("variables");
                if(k!=null){
                    for(int l=0; l<k.length();l++){
                        int id_variable = daoManager.getVariableDAO().searchVariable(k.opt(l).toString(),this.history);
                        int id_element = daoManager.getElementDAO().searchElement(j.getString("taskName"));
                        daoManager.getElementDAO().insertElementUsed(id_variable, id_element,"NA");
                    }
                }else if(j.optString("variables")!=""){
                    String v = j.optString("variables");
                    int id_variable = daoManager.getVariableDAO().searchVariable(v,this.history);
                    int id_element = daoManager.getElementDAO().searchElement(j.getString("taskName"));
                    daoManager.getElementDAO().insertElementUsed(id_variable, id_element,"NA");
                }
            //}
        }
    }

    private void deleteVariable(Timestamp date){
        int id = daoManager.getHistoryDAO().searchHistory(date);
        if(id!=-1){
            daoManager.getVariableDAO().deleteVariable(id);
        }
    }

    private void deleteHistory(Timestamp date){
        daoManager.getHistoryDAO().deleteHistory(date);
    }

}
