package org.appops.scanner.listener;


public interface ClassAnnotationDiscoveryListener extends AnnotationDiscoveryListener {

	/**
	 * Gets called by the Discoverer with class-name of the class where annotation is found.
	 * 
	 * @param clazz			
	 * @param annotation
	 */
	void discovered(String clazz, String annotation);
}
