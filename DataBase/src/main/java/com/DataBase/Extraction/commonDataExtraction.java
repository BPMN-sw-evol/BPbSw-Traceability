package com.DataBase.Extraction;

import java.io.IOException;
import java.util.ArrayList;

import com.DataBase.DAO.*;
import com.DataBase.Interface.IDataExtractor;
import org.json.JSONArray;
import org.json.JSONObject;


public class commonDataExtraction implements IDataExtractor {
    private final JSONObject projectInfo;
    private final JSONObject bpmnInfo;
    private final int history;
    private final ArrayList<String> variables = new ArrayList<String>();
    private final ArrayList<String> projects = new ArrayList<String>();
    private final ArrayList<String> paths = new ArrayList<String>();
    private final VariableDAO variableDAO;
    private final ContainerDAO containerDAO;
    private final ProjectDAO projectDAO;
    private final ClassDAO classDAO;
    private final MethodDAO methodDAO;
    private final ElementDAO elementDAO;

    public commonDataExtraction(JSONObject projectFilePath, JSONObject bpmnFilePath, String name) {
        // Obtener las rutas de los archivos
        this.projectInfo = projectFilePath;
        this.bpmnInfo = bpmnFilePath;

        // Obtener la instancia del DAOManager
        DAOManager daoManager = DAOManager.getInstance();

        // Obtener intancias de los DAO's
        this.variableDAO = daoManager.getVariableDAO();
        this.containerDAO = daoManager.getContainerDAO();
        this.projectDAO = daoManager.getProjectDAO();
        this.classDAO = daoManager.getClassDAO();
        this.methodDAO = daoManager.getMethodDAO();
        this.elementDAO = daoManager.getElementDAO();

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

    public boolean isDataInitialized() {
        return projectInfo != null && bpmnInfo != null && history != -1;
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
                        variableDAO.insertVariable(k.opt(l).toString(),this.history);
                    }
                }
            }
        }

        for (String i : this.variables) {
            int variable = variableDAO.searchVariable(i,this.history);
            for (int j=0;j<this.projects.size();j++){
                int project = projectDAO.searchProject(this.projects.get(j),this.paths.get(j));
                for (String k : this.projectInfo.getJSONObject(this.projects.get(j)).keySet()) {
                    for (String l: this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).keySet()) {
                        JSONArray v = this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).optJSONArray("variables");

                        if(v!=null){

                            for(Object m : v){
                                if(m.toString().equals(i) && this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).keySet().contains("container")){
                                    int container = containerDAO.searchContainer(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).getString("container"),project);
                                    if(containerDAO.searchContainedIn(variable, container)==-1){
                                        containerDAO.insertContainedIn(variable, container);
                                    }
                                    break;
                                }
                            }
                        }else if(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).optString("variables").equals(i) && this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).keySet().contains("container")){
                            int container = containerDAO.searchContainer(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).getString("container"),project);
                            if(containerDAO.searchContainedIn(variable, container)==-1){
                                containerDAO.insertContainedIn(variable, container);
                            }
                        }else if(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).optString("variables").equals(i)){
                            int container = containerDAO.searchContainer("NA",project);
                            if(containerDAO.searchContainedIn(variable, container)==-1){
                                containerDAO.insertContainedIn(variable, container);
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
            int id_variable = variableDAO.searchVariable(i,this.history);
            for (int j=0;j<this.projects.size();j++){
                int id_project = projectDAO.searchProject(this.projects.get(j),this.paths.get(j));
                for (String k : this.projectInfo.getJSONObject(this.projects.get(j)).keySet()) {
                    for (String l: this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).keySet()) {
                        if(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).keySet().contains("variables")){
                            JSONArray v = this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).optJSONArray("variables");
                            if(v!=null){
                                for(Object m : v){
                                    if(m.toString().equals(i)){
                                        int id_class = classDAO.searchClass(k.split(": ")[1],id_project);
                                        int id_method = methodDAO.searchMethod(id_class,l.split(": ")[1].split(" ")[0]);

                                        if(l.toString().contains("set")){
                                            methodDAO.insertMethodUsed(id_variable,id_method, true);
                                        }else{
                                            methodDAO.insertMethodUsed(id_variable,id_method, false);
                                        }

                                        break;
                                    }
                                }
                            }else if(this.projectInfo.getJSONObject(this.projects.get(j)).getJSONObject(k).getJSONObject(l).getString("variables").equals(i)){
                                int id_class = classDAO.searchClass(k.split(": ")[1],id_project);
                                int id_method = methodDAO.searchMethod(id_class,l.split(": ")[1].split(" ")[0]);

                                if(l.toString().contains("set")){
                                    methodDAO.insertMethodUsed(id_variable,id_method, true);
                                }else{
                                    methodDAO.insertMethodUsed(id_variable,id_method, false);
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
            if(j.getString("taskType").equals("Service Task")){
                for (String k : this.projectInfo.keySet()) {
                    if(!k.equals("ProjectPath")){
                        for (String l : this.projectInfo.getJSONObject(k).keySet()) {
                            boolean flag = false;
                            String element = null;
                            for (String m: this.projectInfo.getJSONObject(k).getJSONObject(l).keySet()) {
                                if(m.contains("Class") && this.projectInfo.getJSONObject(k).getJSONObject(l).getJSONObject(m).getString("name").equals(j.getString("taskName"))){
                                    flag = true;
                                    element = this.projectInfo.getJSONObject(k).getJSONObject(l).getJSONObject(m).getString("name");
                                    continue;
                                }
                                if(flag){
                                    JSONArray v = this.projectInfo.getJSONObject(k).getJSONObject(l).getJSONObject(m).optJSONArray("variables");
                                    if(v!=null){
                                        for(Object n : v){
                                            if(!serviceVariables.contains(n.toString())){
                                                serviceVariables.add(n.toString());
                                            }
                                        }
                                    }else{
                                        if(!serviceVariables.contains(this.projectInfo.getJSONObject(k).getJSONObject(l).getJSONObject(m).getString("variables"))){
                                            serviceVariables.add(this.projectInfo.getJSONObject(k).getJSONObject(l).getJSONObject(m).getString("variables"));
                                        }
                                    }
                                }
                            }

                            if(!serviceVariables.isEmpty()){
                                for (String n : serviceVariables) {
                                    int id_variable = variableDAO.searchVariable(n,this.history);
                                    int id_element = elementDAO.searchElement(element);
                                    elementDAO.insertElementUsed(id_variable, id_element,"NA");
                                }
                                serviceVariables = new ArrayList<String>();
                            }
                        }
                    }
                }
            }else{
                JSONArray k = j.optJSONArray("variables");
                if(k!=null){
                    for(int l=0; l<k.length();l++){
                        int id_variable = variableDAO.searchVariable(k.opt(l).toString(),this.history);
                        int id_element = elementDAO.searchElement(j.getString("taskName"));
                        elementDAO.insertElementUsed(id_variable, id_element,"NA");
                    }
                }else if(j.optString("variables")!=""){
                    String v = j.optString("variables");
                    int id_variable = variableDAO.searchVariable(v,this.history);
                    int id_element = elementDAO.searchElement(j.getString("taskName"));
                    elementDAO.insertElementUsed(id_variable, id_element,"NA");
                }
            }
        }
    }
}
