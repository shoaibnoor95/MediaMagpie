package de.wehner.mediamagpie.conductor.performingjob;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.wehner.mediamagpie.common.persistence.entity.JobExecution;


@Component
public class JobFactory {

    private Map<Class<?>, JobCreator> _creators = new HashMap<Class<?>, JobCreator>();

    @Autowired
    public JobFactory(List<JobCreator> creators) {
        for (JobCreator creator : creators) {
            _creators.put(creator.getJobExecutionClass(), creator);
        }
    }

    public PerformingJob createDapJob(/*DapJobConfiguration dapJobConfiguration, */JobExecution execution) {
        for (Entry<Class<?>, JobCreator> entry : _creators.entrySet()) {
            if (entry.getKey().isAssignableFrom(execution.getClass())) {
                return entry.getValue().create(/*dapJobConfiguration,*/ execution);
            }
        }
        throw new IllegalArgumentException("No DapJobCreator for " + execution.getClass().getName());
    }
}
