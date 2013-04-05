package de.wehner.mediamagpie.conductor.spring.deploy;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.wehner.mediamagpie.core.util.ExceptionUtil;


@Service
public class DataInjectorFactory {

    private List<DataInjector> _injectors;

    @Autowired
    public DataInjectorFactory(List<DataInjector> injectors) {
        _injectors = injectors;
    }

    public List<DataInjector> getDataInjectors() {
        return _injectors;
    }

    @PostConstruct
    public void injectData() {
        for (DataInjector injector : getDataInjectors()) {
            try {
                injector.injectData();
            } catch (Exception e) {
                throw ExceptionUtil.convertToRuntimeException(e);
            }
        }
    }
}
