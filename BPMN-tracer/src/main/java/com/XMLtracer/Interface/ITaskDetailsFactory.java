package com.XMLtracer.Interface;

import org.camunda.bpm.model.bpmn.instance.Activity;

public interface ITaskDetailsFactory {
    ITaskDetailsStrategy createStrategy(Activity activity);

}
