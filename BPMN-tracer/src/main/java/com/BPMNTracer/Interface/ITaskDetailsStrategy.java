package com.BPMNTracer.Interface;

import com.google.gson.JsonObject;
import org.camunda.bpm.model.bpmn.instance.Activity;

public interface ITaskDetailsStrategy {
    JsonObject getTaskDetails(Activity activity);

}
