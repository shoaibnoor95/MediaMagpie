package de.wehner.mediamagpie.conductor.webapp.validator;

import java.io.File;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.wehner.mediamagpie.persistence.entity.properties.UserConfiguration;


public class UserConfigurationValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return UserConfiguration.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors e) {
        UserConfiguration conf = (UserConfiguration) obj;
        for (String path : conf.getRootMediaPathes()) {
            if (!new File(path).exists()) {
                e.rejectValue("rootMediaPathes", "dir.does.not.exists", new String[] { path }, "Pfad '" + path + "' existiert nicht");
            }
        }
    }

}
