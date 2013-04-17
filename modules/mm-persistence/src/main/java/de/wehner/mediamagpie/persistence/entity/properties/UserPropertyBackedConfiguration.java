package de.wehner.mediamagpie.persistence.entity.properties;

import de.wehner.mediamagpie.core.util.properties.PropertiesBacked;
import de.wehner.mediamagpie.persistence.entity.User;

/**
 * A marker interface for "entities" that are assigned to a {@linkplain User} which are translated into {@link Property}s and back. They
 * should be annotated with {@link PropertiesBacked}.
 * 
 */
public interface UserPropertyBackedConfiguration {
}
