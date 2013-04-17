package de.wehner.mediamagpie.conductor.webapp.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.wehner.mediamagpie.persistence.entity.PasswordConfiguration;

public class PasswordConfirmValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return PasswordConfiguration.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors e) {
        PasswordConfiguration conf = (PasswordConfiguration) obj;
        if (!conf.getPassword().equals(conf.getPasswordConfirm())) {
            e.rejectValue("passwordConfirm", "passwordConfirm.differs", null, "The password is different");
        }
    }

}
