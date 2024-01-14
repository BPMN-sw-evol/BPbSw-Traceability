package com.Trazability.DataBase;

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

public class Data {
    private int history;
    private JSONObject projectInfo;
    private JSONObject bpmnInfo;
    private ArrayList<String> variables = new ArrayList<String>();
    private ArrayList<String> type = new ArrayList<String>();
    private Connections con = new Connections();
    
    public Data(String ruta,String ruta2,String name) throws IOException{
        try {
            BufferedReader file = new BufferedReader(new java.io.FileReader(ruta));
            String s = "";
            String line;
            while ((line = file.readLine()) != null) {
                s+=line;
            }
            this.projectInfo = new JSONObject(s);

            file = new BufferedReader(new java.io.FileReader(ruta2));
            s = "";
            line="";
            while ((line = file.readLine()) != null) {
                s+=line;
            }
            this.bpmnInfo = new JSONObject(s);
            
            this.history = con.createHistory(name);

           setProjects();
           setClasses();
           setContainer();
           setVariables();
           setMethod();
           setMethodUsed();
           setProcess();
           setElementType();
           setElement();
           setElementUsed();
        } catch (FileNotFoundException e) {
    System.out.println("Error al leer el archivo: " + e.getMessage());
}

    }
    
    public boolean isDataInitialized() {
    return projectInfo != null && bpmnInfo != null && history != -1;
}


    private void setProjects(){
        for (String i : this.projectInfo.keySet()) {
            con.insertProject(i);
        }
    }

    private void setClasses(){
        for (String i : this.projectInfo.keySet()) {
            int id_project = con.searchProject(i);
            for (String j : this.projectInfo.getJSONObject(i).keySet()) {
                con.insertClass(id_project, j.split(": ")[1]);
            }
        }
    }

    private void setContainer(){
        ArrayList<String> container = new ArrayList<String>();
        for (String i : this.projectInfo.keySet()) {
            int id_project = con.searchProject(i);
            for (String j : this.projectInfo.getJSONObject(i).keySet()) {
                for (String k: this.projectInfo.getJSONObject(i).getJSONObject(j).keySet()) {
                    if(this.projectInfo.getJSONObject(i).getJSONObject(j).get(k).toString().contains("container")){
                        String p = this.projectInfo.getJSONObject(i).getJSONObject(j).getJSONObject(k).getString("container");
                        if(!container.contains(p)){
                            container.add(p);
                            con.insertContainer(p,id_project);
                        }
                    }
                }
            }
        }
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
                        con.insertVariable(k.opt(l).toString(),this.history);
                    }
                }
            }
        }

        for (String i : this.variables) {
            int variable = con.searchVariable(i,this.history);
            for (String j : this.projectInfo.keySet()) {
                int project = con.searchProject(j);
                for (String k : this.projectInfo.getJSONObject(j).keySet()) {
                    for (String l: this.projectInfo.getJSONObject(j).getJSONObject(k).keySet()) {
                        JSONArray v = this.projectInfo.getJSONObject(j).getJSONObject(k).getJSONObject(l).optJSONArray("variables");
                        if(v!=null){
                            for(Object m : v){
                                if(m.toString().equals(i) && this.projectInfo.getJSONObject(j).getJSONObject(k).getJSONObject(l).keySet().contains("container")){
                                    int container = con.searchContainer(this.projectInfo.getJSONObject(j).getJSONObject(k).getJSONObject(l).getString("container"),project);

                                    if(con.searchContainedIn(variable, container)==-1){
                                        con.insertContainedIn(variable, container);
                                    }
                                    break;
                                }
                            }
                        }else if(this.projectInfo.getJSONObject(j).getJSONObject(k).getJSONObject(l).optString("variables").equals(i) && this.projectInfo.getJSONObject(j).getJSONObject(k).getJSONObject(l).keySet().contains("container")){
                            int container = con.searchContainer(this.projectInfo.getJSONObject(j).getJSONObject(k).getJSONObject(l).getString("container"),project);
                            if(con.searchContainedIn(variable, container)==-1){
                                con.insertContainedIn(variable, container);
                            }
                        }else if(this.projectInfo.getJSONObject(j).getJSONObject(k).getJSONObject(l).optString("variables").equals(i)){
                            int container = con.searchContainer("NA",1);
                            if(con.searchContainedIn(variable, container)==-1){
                                con.insertContainedIn(variable, container);
                            }
                        }
                    }
                }
            }
        }
    }

    private void setMethod(){
        for (String i : this.projectInfo.keySet()) {
            for (String j : this.projectInfo.getJSONObject(i).keySet()) {
                int id_class = con.searchClass(j.split(": ")[1]);
                for (String k: this.projectInfo.getJSONObject(i).getJSONObject(j).keySet()) {
                    if(k.contains("Method")){
                        con.insertMethod(id_class, k.split(": ")[1].split(" ")[0]);
                    }
                }
            }
        }
    }

    private void setMethodUsed(){
        //setVariables();
        for (String i : this.variables) {
            int id_variable = con.searchVariable(i,this.history);
            for (String j : this.projectInfo.keySet()) {
                for (String k : this.projectInfo.getJSONObject(j).keySet()) {
                    for (String l: this.projectInfo.getJSONObject(j).getJSONObject(k).keySet()) {
                        if(this.projectInfo.getJSONObject(j).getJSONObject(k).getJSONObject(l).keySet().contains("variables")){
                            JSONArray v = this.projectInfo.getJSONObject(j).getJSONObject(k).getJSONObject(l).optJSONArray("variables");
                            if(v!=null){
                                for(Object m : v){
                                    if(m.toString().equals(i)){
                                        int id_class = con.searchClass(k.split(": ")[1]);
                                        int id_method = con.searchMethod(id_class,l.split(": ")[1].split(" ")[0]);

                                        if(l.toString().contains("set")){
                                            con.insertMethodUsed(id_variable,id_method, true);
                                        }else{
                                           con.insertMethodUsed(id_variable,id_method, false);
                                        }
                                        
                                        break;
                                   }
                               }
                            }else if(this.projectInfo.getJSONObject(j).getJSONObject(k).getJSONObject(l).getString("variables").equals(i)){
                               int id_class = con.searchClass(k.split(": ")[1]);
                               int id_method = con.searchMethod(id_class,l.split(": ")[1].split(" ")[0]);

                                if(l.toString().contains("set")){
                                    con.insertMethodUsed(id_variable,id_method, true);
                                }else{
                                    con.insertMethodUsed(id_variable,id_method, false);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void setProcess(){
        con.insertProcess(this.bpmnInfo.getString("bpmName"));
    }

    private void setElementType(){
        JSONArray p = this.bpmnInfo.getJSONArray("trace");
        for(Object i : p){
            JSONObject j = (JSONObject) i;

            if(!this.type.contains(j.getString("taskImplementationType"))){
                this.type.add(j.getString("taskImplementationType"));
                con.insertElementType(j.getString("taskImplementationType"));
            }    
        }
    }

    private void setElement(){
        JSONArray p = this.bpmnInfo.getJSONArray("trace");
        for(Object i : p){
            JSONObject j = (JSONObject) i;
            int id_type = con.searchElementType(j.getString("taskImplementationType"));
            String name = j.getString("taskName");
            String lane = "Lane"; //cuando BPMN-Tracer traiga el lane, modificar esta linea
            int id_process = con.searchProcess(this.bpmnInfo.getString("bpmName"));

            con.insertElement(id_type, name, lane, id_process);
        }
    }

    private void setElementUsed(){
        JSONArray p = this.bpmnInfo.getJSONArray("trace");
        ArrayList<String> serviceVariables = new ArrayList<String>();
        for(Object i : p){
            JSONObject j = (JSONObject) i;
            if(j.getString("taskType").equals("serviceTask")){
                for (String k : this.projectInfo.keySet()) {
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
                                int id_variable = con.searchVariable(n,this.history);
                                int id_element = con.searchElement(element);
                                con.insertElementUsed(id_variable, id_element,"NA");
                            }
                        }
                    }
                }
            }else{
                JSONArray k = j.optJSONArray("variables");
                if(k!=null){
                    for(int l=0; l<k.length();l++){
                        int id_variable = con.searchVariable(k.opt(l).toString(),this.history);
                        int id_element = con.searchElement(j.getString("taskName"));
                        con.insertElementUsed(id_variable, id_element,"NA");
                    }
                }
            }
        }
    }
}
