package de.wehner.mediamagpie.common.persistence.entity.properties;

import de.wehner.mediamagpie.common.persistence.entity.User;
import de.wehner.mediamagpie.common.util.properties.PropertiesBacked;

/**
 * A marker interface for "entities" that are assigned to a {@linkplain User} which are translated into {@link Property}s and back. They
 * should be annotated with {@link PropertiesBacked}.
 * 
 */
public interface UserPropertyBackedConfiguration {
}
