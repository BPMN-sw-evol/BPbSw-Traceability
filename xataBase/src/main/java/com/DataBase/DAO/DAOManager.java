package com.DataBase.DAO;

import com.DataBase.DataBase.DatabaseConnection;

import java.sql.Connection;

public class DAOManager {
    private static DAOManager instance;
    private final Connection con;

    private DAOManager() {
        // Inicializa tus DAOs aquí
        this.con = DatabaseConnection.getInstance().getConnection();
    }

    public static DAOManager getInstance() {
        if (instance == null) {
            instance  = new DAOManager();
        }
        return instance;
    }

    public HistoryDAO getHistoryDAO() {
        return new HistoryDAO(con);
    }

    public VariableDAO getVariableDAO() {
        return new VariableDAO(con);
    }

    public ContainerDAO getContainerDAO() {
        return new ContainerDAO(con);
    }

    public ProjectDAO getProjectDAO() {
        return new ProjectDAO(con);
    }

    public ClassDAO getClassDAO() {
        return new ClassDAO(con);
    }

    public MethodDAO getMethodDAO() {
        return new MethodDAO(con);
    }

    public ProcessDAO getProcessDAO() {
        return new ProcessDAO(con);
    }

    public ElementDAO getElementDAO() {
        return new ElementDAO(con);
    }

    public PathDAO getPathDAO() {
        return new PathDAO(con);
    }
}
