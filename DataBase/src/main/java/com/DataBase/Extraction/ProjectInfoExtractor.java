package com.DataBase.Extraction;

import com.DataBase.DAO.*;
import com.DataBase.Interface.IDataExtractor;
import java.sql.Timestamp;
import java.util.List;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

public class ProjectInfoExtractor implements IDataExtractor {
    private  JSONObject projectInfo;
    private final ArrayList<String> projects = new ArrayList<>();
    private final ArrayList<String> paths = new ArrayList<>();
    private final DAOManager daoManager = DAOManager.getInstance();;

    public ProjectInfoExtractor() {}


    public ProjectInfoExtractor(JSONObject projectFilePath) {

        // Obtener la ruta del archivo
        this.projectInfo = projectFilePath;

    }

    @Override
    public void insertData() {

        setProjects();
        setClasses();
        setMethod();
        setContainer();

    }

    @Override
    public void deleteData(Timestamp date) {

        deleteMethod(date);
        deleteContainer(date);
    }

    private void setProjects(){
        for (String i : this.projectInfo.keySet()) {
            for (Object j : this.projectInfo.getJSONArray("ProjectPath")) {
                String[] p = j.toString().split("\\\\");
                if(p[p.length-1].equals(i)){
                    this.projects.add(i);
                    this.paths.add(j.toString());
                }
            }
        }

        for (int i=0;i<this.projects.size();i++){
            if(daoManager.getProjectDAO().searchProject(this.projects.get(i),this.paths.get(i))==-1){
                daoManager.getProjectDAO().insertProject(this.projects.get(i),this.paths.get(i));
            }
        }
    }

    private void setClasses(){
        for (int i=0;i<this.projects.size();i++){
            int id_project = daoManager.getProjectDAO().searchProject(this.projects.get(i),this.paths.get(i));
            for (String j : this.projectInfo.getJSONObject(this.projects.get(i)).keySet()) {
                if(daoManager.getClassDAO().searchClass(j.split(": ")[1], id_project)==-1){
                    daoManager.getClassDAO().insertClass(id_project, j.split(": ")[1]);
                }
            }
        }
    }

    private void setContainer(){
        ArrayList<String> container = new ArrayList<String>();
        for (int i=0;i<this.projects.size();i++){
            int id_project = daoManager.getProjectDAO().searchProject(this.projects.get(i),this.paths.get(i));
            if(daoManager.getContainerDAO().searchContainer("NA", id_project)==-1){
                daoManager.getContainerDAO().insertContainer("NA",id_project);
            }
            for (String j : this.projectInfo.getJSONObject(this.projects.get(i)).keySet()) {
                for (String k: this.projectInfo.getJSONObject(this.projects.get(i)).getJSONObject(j).keySet()) {
                    if(this.projectInfo.getJSONObject(this.projects.get(i)).getJSONObject(j).get(k).toString().contains("container")){
                        String p = this.projectInfo.getJSONObject(this.projects.get(i)).getJSONObject(j).getJSONObject(k).getString("container");
                        if(!container.contains(p) && daoManager.getContainerDAO().searchContainer(p, id_project)==-1){
                            container.add(p);
                            daoManager.getContainerDAO().insertContainer(p,id_project);
                        }
                    }
                }
            }
        }
    }

    private void setMethod(){
        for (int i=0;i<this.projects.size();i++){
            int id_project = daoManager.getProjectDAO().searchProject(this.projects.get(i),this.paths.get(i));
            for (String j : this.projectInfo.getJSONObject(this.projects.get(i)).keySet()) {
                int id_class = daoManager.getClassDAO().searchClass(j.split(": ")[1],id_project);
                for (String k: this.projectInfo.getJSONObject(this.projects.get(i)).getJSONObject(j).keySet()) {
                    if(k.contains("Method")){
                        daoManager.getMethodDAO().insertMethod(id_class, k.split(": ")[1].split(" ")[0]);
                    }
                }
            }
        }
    }

    private void deleteMethod(Timestamp date){
        int id = daoManager.getHistoryDAO().searchHistory(date);
        if(id!=-1){
            List<Integer> variables = daoManager.getVariableDAO().getVariablesHistory(id);

            for(int i : variables){
                daoManager.getMethodDAO().deleteMethod(i);
            }
        }
    }

    private void deleteContainer(Timestamp date){
        int id = daoManager.getHistoryDAO().searchHistory(date);
        if(id!=-1){
            List<Integer> variables = daoManager.getVariableDAO().getVariablesHistory(id);

            for(int i : variables){
                daoManager.getContainerDAO().deleteContainer(i);
            }
        }
    }

}
