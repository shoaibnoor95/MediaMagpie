package de.wehner.mediamagpie.core.util.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a {@link java.beans.PropertyEditor} for the translation of a field to string and back.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyDef {

    Class<? extends java.beans.PropertyEditor> editorClass();
}
