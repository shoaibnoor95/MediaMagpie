package de.wehner.mediamagpie.persistence;

import org.hibernate.annotations.common.util.StringHelper;
import org.hibernate.cfg.EJB3NamingStrategy;

@SuppressWarnings("serial")
public class DatabaseNamingStrategy extends EJB3NamingStrategy {

    @Override
    public String classToTableName(String className) {
        String firstChar = className.substring(0, 1).toLowerCase();
        String classNameTail = className.substring(1);
        return addUnderscores(StringHelper.unqualify(firstChar + classNameTail));
    }

    @Override
    public String columnName(String columnName) {
        if (columnName.startsWith("_")) {
            return addUnderscores(columnName.substring(1));
        }
        return addUnderscores(columnName);
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return columnName(propertyName);
    }

    protected static String addUnderscores(String name) {
        StringBuffer buf = new StringBuffer(name.replace('.', '_'));
        for (int i = 1; i < buf.length() - 1; i++) {
            if (Character.isLowerCase(buf.charAt(i - 1)) && Character.isUpperCase(buf.charAt(i)) && Character.isLowerCase(buf.charAt(i + 1))) {
                buf.insert(i++, '_');
            }
        }
        return buf.toString().toLowerCase();
    }
}
