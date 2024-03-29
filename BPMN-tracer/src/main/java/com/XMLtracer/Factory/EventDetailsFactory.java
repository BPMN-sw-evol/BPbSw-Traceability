package com.XMLtracer.Factory;

import com.XMLtracer.Event.EndEventDetailsStrategy;
import com.XMLtracer.Event.IntermediateCatchEventDetailsStrategy;
import com.XMLtracer.Event.StartEventDetailsStrategy;
import com.XMLtracer.Interface.IEventDetailsFactory;
import com.XMLtracer.Interface.IEventDetailsStrategy;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.IntermediateCatchEvent;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

public class EventDetailsFactory implements IEventDetailsFactory {

    // Instancia única de la fábrica
    private static EventDetailsFactory instance;

    // Constructor privado para evitar la creación de instancias fuera de la clase
    private EventDetailsFactory() {}

    // Método estático para obtener la instancia única
    public static EventDetailsFactory getInstance() {
        if (instance == null) {
            instance = new EventDetailsFactory();
        }
        return instance;
    }

    @Override
    public IEventDetailsStrategy createStrategy(Event event) {
        if (event instanceof StartEvent) {
            return new StartEventDetailsStrategy();
        } else if (event instanceof IntermediateCatchEvent) {
            return new IntermediateCatchEventDetailsStrategy();
        } else if (event instanceof EndEvent) {
            return new EndEventDetailsStrategy();
        }
        return null;
    }

}
