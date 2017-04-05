package org.appops.scanner;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.appops.scanner.listener.ClassAnnotationDiscoveryListener;

/**
 * use the two utility methods to find either a set of classes or a setof interfaces annotated with a specific type whose class you specify in the parameter 
 * 
 * Classes are found using two different system environment variables i.e. java.classpath and java.class.path
 * 
 * @author Debasish.Padhy
 *
 */
public class AnnotatedClassScanner {
	
	
	/**
	 * returns the set of classes annotated with specified annotation 
	 * @param t class object representing the annotation
	 * @return
	 * @throws IOException
	 */
	
	private String[] paths = null ;
	private URL[] toScan = null ;
	
	public Set<Class<?>> getClassesAnnotatedWith(Class<?> t) throws IOException{
	
		Discoverer discoverer = null;
		
		if (toScan != null){
			discoverer = new ClasspathDiscoverer(toScan);
		}else
			discoverer = new ClasspathDiscoverer(paths, true);
		
		AnnotatedTypeDiscoveryListener listener = new AnnotatedTypeDiscoveryListener(new String[] {t.getCanonicalName()});
		discoverer.addAnnotationListener(listener);
		discoverer.discover();
		return listener.getDiscovered() ;
	}
	
	public Set<Class<?>> getInterfacesAnnotatedWith(Class<?> t) throws IOException{
		
		Discoverer discoverer = null;
		
		if (toScan != null){
			discoverer = new ClasspathDiscoverer(toScan);
		}else
			discoverer = new ClasspathDiscoverer(paths, true);

		AnnotatedTypeDiscoveryListener listener = new AnnotatedInterfaceDiscoveryListener(new String[] {t.getCanonicalName()});
		discoverer.addAnnotationListener(listener);
		discoverer.discover();
		return listener.getDiscovered() ;
	}

	/**
	 * Only finds annotated interfaces and not classes
	 * 
	 * @author Debasish.Padhy
	 */
	static class AnnotatedInterfaceDiscoveryListener extends  AnnotatedTypeDiscoveryListener{

		public AnnotatedInterfaceDiscoveryListener(String[] annotations) {
			super(annotations);
		}

		@Override
		public void discovered(String clazz, String annotation) {
			try {
				Class<?> discovered = Class.forName(clazz) ;
				
				if (discovered.isInterface())
					discoveredClasses.add(discovered) ;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Finds all types annotated with specified annotation
	 * @author Debasish.Padhy
	 */
	static class AnnotatedTypeDiscoveryListener implements ClassAnnotationDiscoveryListener {

		String[] supportedAnnotations ; 
		HashSet<Class<?>> discoveredClasses = new HashSet<Class<?>> () ;
		
		/**
		 * @param annotations canonical names of annotations to look for
		 */
		public AnnotatedTypeDiscoveryListener(String[] annotations){
			supportedAnnotations = annotations ;
		}
		
		public Set<Class<?>> getDiscovered(){
			return discoveredClasses ;
		}
		
		@Override
		public String[] supportedAnnotations() {
			return supportedAnnotations ;
		}

		@Override
		public void discovered(String clazz, String annotation) {
			try {
				discoveredClasses.add(Class.forName(clazz)) ;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void setPathsToScan(String[] path) {
		paths = path ;
	}

	public void setUrlsToScan(URL[] urlsToScan) {
		toScan = urlsToScan ;
		
	}
}
