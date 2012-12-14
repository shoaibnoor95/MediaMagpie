package de.wehner.mediamagpie.conductor.webapp.controller.commands;

import org.apache.commons.lang3.StringUtils;

public class CheckResultCommand {

    /**
     * <code>true</code> if check was ok, otherwise <code>false</code>
     */
    private final boolean _ok;

    /**
     * The message key in case of erroneous check
     */
    private final String _messageKey;

    /**
     * optional technical details
     */
    private final String _details;

    public CheckResultCommand(boolean result, String messageKey) {
        this(result, messageKey, null);
    }

    public CheckResultCommand(boolean result, String messageKey, String details) {
        super();
        this._ok = result;
        this._messageKey = messageKey;
        this._details = details;
    }

    public boolean isOk() {
        return _ok;
    }

    public String getMessageKey() {
        return _messageKey;
    }

    public String getDetails() {
        if (StringUtils.isEmpty(_details)) {
            return "";
        }
        return '(' + _details + ')';
    }

    public String getDivClass() {
        return (_ok ? "ok" : "error");
    }
}
