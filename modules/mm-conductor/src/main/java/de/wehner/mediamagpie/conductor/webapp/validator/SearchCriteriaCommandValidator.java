package de.wehner.mediamagpie.conductor.webapp.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import de.wehner.mediamagpie.persistence.dto.SearchCriteriaCommand;

public class SearchCriteriaCommandValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return SearchCriteriaCommand.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors error) {
        SearchCriteriaCommand command = (SearchCriteriaCommand) obj;
        try {
            command.getYearStartFromInputField();
            command.getYearEndFromInputField();
        } catch (NumberFormatException e) {
            error.rejectValue("yearCriteria", "only.whole.years.expected", new String[] {}, "Only one or two nummeric year values are valid.");
        }
    }

}
