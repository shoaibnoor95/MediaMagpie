package de.wehner.mediamagpie.core.util.properties;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;

import de.wehner.mediamagpie.core.util.CipherService;
import de.wehner.mediamagpie.core.util.ExceptionUtil;

/**
 * Util around properties files and mapping of properties to POJO's via the {@link PropertiesBacked} mechanism.
 */
public class PropertiesUtil {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtil.class);

    public static List<String> getPropertyKeys(Class<?> clazz) {
        final Class<?> annotatedClass = getAnnotatedClass(clazz);
        String prefix = getPrefix(annotatedClass);
        Method[] methods = annotatedClass.getMethods();
        List<String> properties = new ArrayList<String>();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                String fieldName = Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4);
                properties.add(prefix + "." + fieldName);
            }
        }
        return properties;
    }

    /**
     * Throws an exception if not all set-able fields of the given class are contained in the contained properties.
     * 
     * @param clazz
     * @param properties
     * @param prohibitEmptyValues
     */
    public static void checkPropertyCompleteness(Class<?> clazz, Properties properties, boolean prohibitEmptyValues) {
        List<String> propertyKeys = getPropertyKeys(clazz);
        List<String> missingProperties = new ArrayList<String>();
        for (String key : propertyKeys) {
            if (!properties.containsKey(key)) {
                missingProperties.add(key);
            } else {
                if (prohibitEmptyValues && properties.getProperty(key).length() == 0) {
                    LOG.warn("Property '" + key + "' has empty value.");
                    missingProperties.add(key);
                }
            }
        }
        if (!missingProperties.isEmpty()) {
            throw new IllegalArgumentException("properties " + properties + " does not contain following properties " + missingProperties
                    + " in order instantiate " + clazz.getName() + " properly");
        }
    }

    public static Properties transformToProperties(CipherService cipherService, Object object) {
        String prefix = getPrefix(object.getClass());
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(object);
        PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
        Properties properties = new Properties();
        final Class<?> annotatedClass = getAnnotatedClass(object.getClass()); // only the field in
        // the annotated class are relevant!
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getWriteMethod() != null) {
                try {
                    Method readMethod = propertyDescriptor.getReadMethod();
                    if (readMethod == null) {
                        LOG.debug("For property '" + propertyDescriptor.getName() + "' a write method (" + propertyDescriptor.getWriteMethod().getName()
                                + "()) exists but NO read method! Skip this property.");
                        continue;
                    }
                    Object returnValue = readMethod.invoke(object);
                    try {
                        java.beans.PropertyEditor propertyEditor = getCustomPropertyEditor(annotatedClass, propertyDescriptor.getName());
                        if (propertyEditor != null) {
                            propertyEditor.setValue(returnValue);
                            returnValue = propertyEditor.getAsText();
                        }
                        if (returnValue != null) {
                            String propertyValue = returnValue.toString();
                            if (hasEncryptedAnnotation(annotatedClass, propertyDescriptor.getName())) {
                                propertyValue = cipherService.encryptToBase64(propertyValue);
                            }
                            properties.setProperty(prefix + "." + propertyDescriptor.getName(), propertyValue);
                        }
                    } catch (NoSuchFieldException ex) {
                        // This can happen when object is a subclass of the
                        // 'PropertiesBacked'-annoted class. (object.getClass() != annotatedClass)
                        // It means that propertyDescriptor points to a field in the subclass, but
                        // this is not relevant to persist.
                    }
                } catch (Exception e) {
                    throw ExceptionUtil.convertToRuntimeException(e);
                }

            }
        }
        return properties;
    }

    @SuppressWarnings("unchecked")
    public static <T> T readFromProperties(CipherService cipherService, Class<T> clazz, Properties properties) {
        final Class<? super T> annotatedClass = getAnnotatedClass(clazz);
        String prefix = getPrefix(annotatedClass);
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(clazz);
        for (String key : properties.stringPropertyNames()) {
            Object value = properties.getProperty(key);
            if (key.startsWith(prefix)) {
                String fieldName = key.substring(prefix.length() + 1);
                try {
                    if (hasEncryptedAnnotation(annotatedClass, fieldName)) {
                        value = cipherService.decryptFromBase64((String) value);
                    }
                    java.beans.PropertyEditor propertyEditor = getCustomPropertyEditor(annotatedClass, fieldName);
                    if (propertyEditor != null) {
                        propertyEditor.setAsText((String) value);
                        value = propertyEditor.getValue();
                    }
                } catch (Exception ex) {
                    throw ExceptionUtil.convertToRuntimeException(ex);
                }
                beanWrapper.setPropertyValue(fieldName, value);
            }
        }
        return (T) beanWrapper.getRootInstance();
    }

    public static String getPrefix(Class<?> clazz) {
        PropertiesBacked annotation = getPropertiesBackedAnnotation(clazz);
        return annotation.prefix();
    }

    static <T> Class<? super T> getAnnotatedClass(Class<T> clazz) {
        PropertiesBacked annotation = clazz.getAnnotation(PropertiesBacked.class);
        if (annotation == null) {
            Class<? super T> superclass = clazz.getSuperclass();
            Class<? super T> foundClass = null;

            if (!superclass.getName().equals(Object.class.getName())) {
                foundClass = getAnnotatedClass(superclass);
            }
            if (foundClass == null) {
                throw new IllegalArgumentException(clazz.getName() + " isn't annotated with @" + PropertiesBacked.class.getSimpleName() + " annotation");
            }
            return foundClass;
        }
        return clazz;
    }

    public static <T> PropertiesBacked getPropertiesBackedAnnotation(Class<T> clazz) {
        final Class<? super T> annotatedClass = getAnnotatedClass(clazz);
        PropertiesBacked annotation = annotatedClass.getAnnotation(PropertiesBacked.class);
        if (annotation.prefix().endsWith(".")) {
            throw new IllegalArgumentException("prefix of annotation @" + PropertiesBacked.class.getSimpleName() + " on class " + clazz.getName()
                    + " must not end with a '.'");
        }
        return annotation;
    }

    private static java.beans.PropertyEditor getCustomPropertyEditor(Class<?> clazz, String fieldName) throws SecurityException, NoSuchFieldException,
            InstantiationException, IllegalAccessException {
        PropertyDef annotation = findDeclaredFieldByStrategie(clazz, fieldName).getAnnotation(PropertyDef.class);
        if (annotation == null) {
            return null;
        }
        return annotation.editorClass().newInstance();
    }

    private static boolean hasEncryptedAnnotation(Class<?> clazz, String fieldName) throws SecurityException, NoSuchFieldException {
        Encrypted annotation = findDeclaredFieldByStrategie(clazz, fieldName).getAnnotation(Encrypted.class);
        return annotation != null;
    }

    private static Field findDeclaredFieldByStrategie(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField("_" + fieldName);
        } catch (NoSuchFieldException e) {
            return clazz.getDeclaredField(fieldName);
        }
    }
}
