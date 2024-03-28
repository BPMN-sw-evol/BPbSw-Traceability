package com.DataBase.DAO;

import com.DataBase.DataBase.DatabaseConnection;

import java.sql.Connection;

public class DAOManager {
    private static final DAOManager instance = new DAOManager();

    // Agrega instancias de tus DAOs aquí
    private final HistoryDAO historyDAO;
    private final VariableDAO variableDAO;
    private final ContainerDAO containerDAO;
    private final ProjectDAO projectDAO;
    private final ClassDAO classDAO;
    private final MethodDAO methodDAO;
    private final ProcessDAO processDAO;
    private final ElementDAO elementDAO;
    private final PathDAO pathDAO;

    private DAOManager() {
        // Inicializa tus DAOs aquí
        Connection con = DatabaseConnection.getInstance().getConnection();
        historyDAO = new HistoryDAO(con);
        variableDAO = new VariableDAO(con);
        containerDAO = new ContainerDAO(con);
        projectDAO = new ProjectDAO(con);
        classDAO = new ClassDAO(con);
        methodDAO = new MethodDAO(con);
        processDAO = new ProcessDAO(con);
        elementDAO = new ElementDAO(con);
        pathDAO = new PathDAO(con);
    }

    public static DAOManager getInstance() {
        return instance;
    }

    public HistoryDAO getHistoryDAO() {
        return historyDAO;
    }

    public VariableDAO getVariableDAO() {
        return variableDAO;
    }

    public ContainerDAO getContainerDAO() {
        return containerDAO;
    }

    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    public ClassDAO getClassDAO() {
        return classDAO;
    }

    public MethodDAO getMethodDAO() {
        return methodDAO;
    }

    public ProcessDAO getProcessDAO() {
        return processDAO;
    }

    public ElementDAO getElementDAO() {
        return elementDAO;
    }

    public PathDAO getPathDAO() {
        return pathDAO;
    }
}
