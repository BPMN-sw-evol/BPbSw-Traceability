package com.DataBase.Extraction;

import com.DataBase.DAO.*;
import com.DataBase.Interface.IDataExtractor;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

public class ProjectInfoExtractor implements IDataExtractor {
    private final JSONObject projectInfo;
    private final ArrayList<String> projects = new ArrayList<>();
    private final ArrayList<String> paths = new ArrayList<>();
    private final ContainerDAO containerDAO;
    private final ProjectDAO projectDAO;
    private final ClassDAO classDAO;
    private final MethodDAO methodDAO;

    public ProjectInfoExtractor(JSONObject projectFilePath) {

        // Obtener la ruta del archivo
        this.projectInfo = projectFilePath;

        // Obtener la instancia del DAOManager
        DAOManager daoManager = DAOManager.getInstance();

        // Obtener intancias de los DAO's
        this.containerDAO = daoManager.getContainerDAO();
        this.projectDAO = daoManager.getProjectDAO();
        this.classDAO = daoManager.getClassDAO();
        this.methodDAO = daoManager.getMethodDAO();
    }

    @Override
    public void insertData() {

        setProjects();
        setClasses();
        setMethod();
        setContainer();

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
            if(projectDAO.searchProject(this.projects.get(i),this.paths.get(i))==-1){
                projectDAO.insertProject(this.projects.get(i),this.paths.get(i));
            }
        }
    }

    private void setClasses(){
        for (int i=0;i<this.projects.size();i++){
            int id_project = projectDAO.searchProject(this.projects.get(i),this.paths.get(i));
            for (String j : this.projectInfo.getJSONObject(this.projects.get(i)).keySet()) {
                if(classDAO.searchClass(j.split(": ")[1], id_project)==-1){
                    classDAO.insertClass(id_project, j.split(": ")[1]);
                }
            }
        }
    }

    private void setContainer(){
        ArrayList<String> container = new ArrayList<String>();
        for (int i=0;i<this.projects.size();i++){
            int id_project = projectDAO.searchProject(this.projects.get(i),this.paths.get(i));
            if(containerDAO.searchContainer("NA", id_project)==-1){
                containerDAO.insertContainer("NA",id_project);
            }
            for (String j : this.projectInfo.getJSONObject(this.projects.get(i)).keySet()) {
                for (String k: this.projectInfo.getJSONObject(this.projects.get(i)).getJSONObject(j).keySet()) {
                    if(this.projectInfo.getJSONObject(this.projects.get(i)).getJSONObject(j).get(k).toString().contains("container")){
                        String p = this.projectInfo.getJSONObject(this.projects.get(i)).getJSONObject(j).getJSONObject(k).getString("container");
                        if(!container.contains(p) && containerDAO.searchContainer(p, id_project)==-1){
                            container.add(p);
                            containerDAO.insertContainer(p,id_project);
                        }
                    }
                }
            }
        }
    }

    private void setMethod(){
        for (int i=0;i<this.projects.size();i++){
            int id_project = projectDAO.searchProject(this.projects.get(i),this.paths.get(i));
            for (String j : this.projectInfo.getJSONObject(this.projects.get(i)).keySet()) {
                int id_class = classDAO.searchClass(j.split(": ")[1],id_project);
                for (String k: this.projectInfo.getJSONObject(this.projects.get(i)).getJSONObject(j).keySet()) {
                    if(k.contains("Method")){
                        methodDAO.insertMethod(id_class, k.split(": ")[1].split(" ")[0]);
                    }
                }
            }
        }
    }


}
