package de.wehner.mediamagpie.core.util.properties;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation has the same meaning like the <code>javax.persistence.Transient</code>.
 * 
 * @author Ralf Wehner
 *
 */
@Target({ METHOD, FIELD })
@Retention(RUNTIME)
public @interface PropertyTransient {

}
