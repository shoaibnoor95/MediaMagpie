package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class SendTestMailCommand {

    private String _to;

    private String _subject;

    private String _message;

    @NotEmpty
    @Email
    public String getTo() {
        return _to;
    }

    public void setTo(String to) {
        _to = to;
    }

    @NotEmpty
    public String getSubject() {
        return _subject;
    }

    public void setSubject(String subject) {
        _subject = subject;
    }

    @NotEmpty
    public String getMessage() {
        return _message;
    }

    public void setMessage(String message) {
        _message = message;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

}
