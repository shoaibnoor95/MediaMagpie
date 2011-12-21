package de.wehner.mediamagpie.conductor.mail;

import com.google.common.base.CaseFormat;

public enum MailTemplateType {

    TEST(true), NEW_REGISTRATION(false), RESET_PASSWORD(false);

    private final boolean _isHtml;

    private MailTemplateType(boolean isHtml) {
        _isHtml = isHtml;
    }

    public boolean isHtml() {
        return _isHtml;
    }

    public String getTemplateFileName() {
        String ret = CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, this.toString());
        return ret;
    }
}
