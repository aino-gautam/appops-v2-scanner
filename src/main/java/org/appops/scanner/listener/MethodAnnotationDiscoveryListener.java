
package org.appops.scanner.listener;



public interface MethodAnnotationDiscoveryListener extends AnnotationDiscoveryListener {

    /**
     * Gets called by the Discoverer with class-name of the class, method-name of the method
     * where annotation is found.
     * 
     * 
     * @param clazz
     * @param method
     * @param signature	(read: http://java.sun.com/docs/books/jvms/second_edition/ClassFileFormat-Java5.pdf)
     * @param annotation
     */
    void discovered(String clazz, String method, String signature, String annotation);
}
