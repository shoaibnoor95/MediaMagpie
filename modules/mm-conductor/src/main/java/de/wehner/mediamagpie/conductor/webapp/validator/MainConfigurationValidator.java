package de.wehner.mediamagpie.conductor.webapp.validator;

import java.io.File;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.wehner.mediamagpie.persistence.entity.properties.MainConfiguration;

public class MainConfigurationValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return MainConfiguration.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors e) {
        MainConfiguration conf = (MainConfiguration) obj;
        if (!new File(conf.getTempMediaPath()).exists()) {
            e.rejectValue("tempMediaPath", "dir.does.not.exists", new String[] { conf.getTempMediaPath() }, "Pfad '" + conf.getTempMediaPath()
                    + "' existiert nicht");
        }
        if (!new File(conf.getBaseUploadPath()).exists()) {
            e.rejectValue("baseUploadPath", "dir.does.not.exists", new String[] { conf.getBaseUploadPath() }, "Pfad '" + conf.getBaseUploadPath()
                    + "' existiert nicht");
        }
    }

}
