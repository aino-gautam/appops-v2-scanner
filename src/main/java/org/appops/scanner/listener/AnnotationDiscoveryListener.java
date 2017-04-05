package org.appops.scanner.listener;

public interface AnnotationDiscoveryListener {

	/**
	 * @return Array of supported annotations names
	 */
	String[] supportedAnnotations();
}
