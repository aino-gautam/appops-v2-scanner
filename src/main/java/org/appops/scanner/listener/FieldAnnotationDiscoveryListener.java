
package org.appops.scanner.listener;


public interface FieldAnnotationDiscoveryListener extends AnnotationDiscoveryListener {

    /**
     * Gets called by the Discoverer with class-name of the class, field-name of the field
     * where annotation is found.
     * 
     * @param clazz
     * @param field
     * @param annotation
     */
    void discovered(String clazz, String field, String annotation);
}
