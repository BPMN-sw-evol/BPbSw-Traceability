package com.BPMNTracer.Event.IntermediateEvents;

import org.camunda.bpm.model.bpmn.instance.TimerEventDefinition;

public class TimerIntermediateEvent {
    private TimerEventDefinition timerEvent;

    public TimerIntermediateEvent(TimerEventDefinition timerEvent) {
        this.timerEvent = timerEvent;
    }

    public String getTaskType() {
        return "Timer Intermediate Event";
    }

    public String getTaskImplementationType() {
        // Implementa la lógica para determinar el tipo de implementación del temporizador
        if (timerEvent.getTimeCycle() != null) {
            return "Cycle";
        } else if (timerEvent.getTimeDuration() != null) {
            return "Duration";
        } else if (timerEvent.getTimeDate() != null) {
            return "Date";
        } else {
            return "Unknown";
        }
    }

    public String getTaskReferenceOrImplementation() {
        // Implementa la lógica para obtener la referencia o implementación del temporizador
        if (timerEvent.getTimeCycle() != null) {
            return timerEvent.getTimeCycle().getTextContent();
        } else if (timerEvent.getTimeDuration() != null) {
            return timerEvent.getTimeDuration().getTextContent();
        } else if (timerEvent.getTimeDate() != null) {
            return timerEvent.getTimeDate().getTextContent();
        } else {
            return "Unknown";
        }
    }
}
