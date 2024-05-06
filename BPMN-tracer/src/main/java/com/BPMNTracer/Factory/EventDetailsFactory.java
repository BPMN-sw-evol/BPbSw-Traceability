package com.BPMNTracer.Factory;

import com.BPMNTracer.Event.EndEventDetailsStrategy;
import com.BPMNTracer.Event.IntermediateCatchEventDetailsStrategy;
import com.BPMNTracer.Event.StartEventDetailsStrategy;
import com.BPMNTracer.Interface.IEventDetailsStrategy;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.IntermediateCatchEvent;
import org.camunda.bpm.model.bpmn.instance.StartEvent;

public class EventDetailsFactory {

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
