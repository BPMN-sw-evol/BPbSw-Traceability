package com.XMLtracer.Factory;

import com.XMLtracer.Activity.SendTaskDetailsStrategy;
import com.XMLtracer.Activity.ServiceTaskDetailsStrategy;
import com.XMLtracer.Activity.UserTaskDetailsStrategy;
import com.XMLtracer.Interface.ITaskDetailsStrategy;
import org.camunda.bpm.model.bpmn.instance.*;

public class TaskDetailsFactory {

    // Instancia única de la fábrica
    private static TaskDetailsFactory instance;

    // Constructor privado para evitar la creación de instancias fuera de la clase
    private TaskDetailsFactory() {}

    // Método estático para obtener la instancia única
    public static TaskDetailsFactory getInstance() {
        if (instance == null) {
            instance = new TaskDetailsFactory();
        }
        return instance;
    }

    public ITaskDetailsStrategy createStrategy(Activity activity) {
        if (activity instanceof UserTask) {
            return new UserTaskDetailsStrategy();
        } else if (activity instanceof ServiceTask) {
            return new ServiceTaskDetailsStrategy();
        } else if (activity instanceof SendTask) {
            return new SendTaskDetailsStrategy();
        }
        return null;
    }
}
