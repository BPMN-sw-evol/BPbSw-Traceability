package com.xmltracer.Factory;

import com.xmltracer.Activity.SendTaskDetailsStrategy;
import com.xmltracer.Activity.ServiceTaskDetailsStrategy;
import com.xmltracer.Activity.UserTaskDetailsStrategy;
import com.xmltracer.Interface.ITaskDetailsFactory;
import com.xmltracer.Interface.ITaskDetailsStrategy;
import org.camunda.bpm.model.bpmn.instance.*;

public class TaskDetailsFactory implements ITaskDetailsFactory {

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

    @Override
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
