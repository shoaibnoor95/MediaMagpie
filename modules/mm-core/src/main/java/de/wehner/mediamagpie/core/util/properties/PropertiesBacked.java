package de.wehner.mediamagpie.core.util.properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for a POJO which can be initialized from properties out of a property file. The
 * properties are translated to the fields of the POJO.
 * 
 * <p>
 * Imagine a class ScreenConfiguration with the {@link PropertiesBacked} annotation and prefix set
 * to 'org.tv'.<br>
 * A property 'org.tv.screenSize' would then automatically translated to the field
 * ScreenConfiguration.screenSize.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PropertiesBacked {

    String prefix();

    boolean initFromProperties() default true;

}
