
package org.appops.scanner;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.appops.scanner.listener.ClassAnnotationDiscoveryListener;
import org.appops.scanner.listener.FieldAnnotationDiscoveryListener;
import org.appops.scanner.listener.MethodAnnotationDiscoveryListener;
import org.appops.scanner.resource.ClassFileIterator;
import org.appops.scanner.resource.JarFileIterator;
import org.appops.scanner.resource.ResourceIterator;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;


public abstract class Discoverer {
    
    /** map to hold ClassAnnotation listeners */
    private static final Map<String, Set<ClassAnnotationDiscoveryListener>> classAnnotationListeners = 
    	new HashMap<String, Set<ClassAnnotationDiscoveryListener>>();
    
    /** map to hold FieldAnnotation listeners */
    private static final Map<String, Set<FieldAnnotationDiscoveryListener>> fieldAnnotationListeners = 
    	new HashMap<String, Set<FieldAnnotationDiscoveryListener>>();
    
    /** map to hold MethodAnnotation listeners */
    private static final Map<String, Set<MethodAnnotationDiscoveryListener>> methodAnnotationListeners = 
    	new HashMap<String, Set<MethodAnnotationDiscoveryListener>>();
    
    /**
     * Instantiates a new Discoverer.
     */
    public Discoverer() {
    }

    /**
     * Adds ClassAnnotationDiscoveryListener
     * 
     * @param listener
     */
    public final void addAnnotationListener (ClassAnnotationDiscoveryListener listener) {
    	addAnnotationListener (classAnnotationListeners, listener, listener.supportedAnnotations());
    }
    
    /**
     * Adds FieldAnnotationDiscoveryListener
     * 
     * @param listener
     */
    public final void addAnnotationListener (FieldAnnotationDiscoveryListener listener) {
    	addAnnotationListener (fieldAnnotationListeners, listener, listener.supportedAnnotations());
    }

    /**
     * Adds MethodAnnotationDiscoveryListener
     * 
     * @param listener
     */
    public final void addAnnotationListener (MethodAnnotationDiscoveryListener listener) {
    	addAnnotationListener (methodAnnotationListeners, listener, listener.supportedAnnotations());
    }
    
    /**
     * Helper class to find supported annotations of a listener and register them
     * 
     * @param <L>
     * @param map
     * @param listener
     * @param annotations
     */
    private <L> void addAnnotationListener (Map<String, Set<L>> map, L listener, String... annotations) {
    	// throw exception if the listener doesn't support any annotations. what's the point of
    	// registering then?
    	if (null == annotations || annotations.length == 0) {
    		throw new IllegalArgumentException(listener.getClass() + " has no supporting Annotations. Check method supportedAnnotations");
    	}
    	
		for (String annotation : annotations) {
			Set<L> listeners = map.get(annotation);
			if (null == listeners) {
				listeners = new HashSet<L>();
				map.put(annotation, listeners);
			}
			listeners.add(listener);
		}
    }
    
    /**
     * Gets the filter implementation.
     * 
     * @return the filter
     */
    public abstract Filter getFilter();

	/**
	 * Finds resources to scan for
	 * 
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public abstract URL[] findResources();
	
	
    /**
     * that's my buddy! this is where all the discovery starts.
     */
    public final void discover() {
        URL[] resources = findResources();
		DataInputStream dstream;
		ClassFile classFile;
		
		for (URL resource : resources) {
			try {
                ResourceIterator itr = getResourceIterator(resource, getFilter());
                
                InputStream is = null;
                while ((is = itr.next()) != null) {
                	// make a data input stream
                	dstream = new DataInputStream(new BufferedInputStream(is));
                	System.err.println(resource.getFile());
                    try {
                    	// get java-assist class file
                    	classFile = new ClassFile(dstream);
                    	
                    	// discover class-level annotations
                    	discoverAndIntimateForClassAnnotations (classFile);
                    	// discover field annotations
                    	discoverAndIntimateForFieldAnnotations (classFile);
                    	// discover method annotations
                    	discoverAndIntimateForMethodAnnotations(classFile);
                    } finally {
                    	 dstream.close();
                         is.close();
                    }
                }
            } catch (IOException e) {
                
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Discovers Class Annotations
     * 
     * @param classFile
     */
    private void discoverAndIntimateForClassAnnotations (ClassFile classFile) {
    	Set<Annotation> annotations = new HashSet<Annotation>();
    	
		AnnotationsAttribute visible 	= (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.visibleTag);
		AnnotationsAttribute invisible 	= (AnnotationsAttribute) classFile.getAttribute(AnnotationsAttribute.invisibleTag);

		if (visible != null) {
			annotations.addAll(Arrays.asList(visible.getAnnotations()));
		}
		if (invisible != null) {
			annotations.addAll(Arrays.asList(invisible.getAnnotations()));
		}
		
		// now tell listeners
		for (Annotation annotation : annotations) {
			Set<ClassAnnotationDiscoveryListener> listeners = classAnnotationListeners.get(annotation.getTypeName());
			if (null == listeners) {
				continue;
			}

			for (ClassAnnotationDiscoveryListener listener : listeners) {
				listener.discovered(classFile.getName(), annotation.getTypeName());
			}
		}
    }
    
    /**
     * Discovers Field Annotations
     * 
     * @param classFile
     */
    private void discoverAndIntimateForFieldAnnotations (ClassFile classFile) {
		@SuppressWarnings("unchecked") 
		List<FieldInfo> fields = classFile.getFields();
		if (fields == null) {
			return;
		}
		
		for (FieldInfo fieldInfo : fields) {
			Set<Annotation> annotations = new HashSet<Annotation>();
			
			AnnotationsAttribute visible = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.visibleTag);
			AnnotationsAttribute invisible = (AnnotationsAttribute) fieldInfo.getAttribute(AnnotationsAttribute.invisibleTag);

			if (visible != null) {
				annotations.addAll(Arrays.asList(visible.getAnnotations()));
			}
			if (invisible != null) {
				annotations.addAll(Arrays.asList(invisible.getAnnotations()));
			}
			
			// now tell listeners
			for (Annotation annotation : annotations) {
				Set<FieldAnnotationDiscoveryListener> listeners = fieldAnnotationListeners.get(annotation.getTypeName());
				if (null == listeners) {
					continue;
				}
				
				for (FieldAnnotationDiscoveryListener listener : listeners) {
					listener.discovered(classFile.getName(), fieldInfo.getName(), annotation.getTypeName());
				}
			}
		}
    }
    
    /**
     * Discovers Method Annotations
     * 
     * @param classFile
     */
    private void discoverAndIntimateForMethodAnnotations(ClassFile classFile) {
    	@SuppressWarnings("unchecked") 
		List<MethodInfo> methods = classFile.getMethods();
		if (methods == null) {
			return;
		}
		
		for (MethodInfo methodInfo : methods) {
			Set<Annotation> annotations = new HashSet<Annotation>();
			
			AnnotationsAttribute visible 	= (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.visibleTag);
			AnnotationsAttribute invisible 	= (AnnotationsAttribute) methodInfo.getAttribute(AnnotationsAttribute.invisibleTag);
			
			if (visible != null) {
				annotations.addAll(Arrays.asList(visible.getAnnotations()));
			}
			if (invisible != null) {
				annotations.addAll(Arrays.asList(invisible.getAnnotations()));
			}
			
			// now tell listeners
			for (Annotation annotation : annotations) {
				Set<MethodAnnotationDiscoveryListener> listeners = methodAnnotationListeners.get(annotation.getTypeName());
				if (null == listeners) {
					continue;
				}
				
				for (MethodAnnotationDiscoveryListener listener : listeners) {
					listener.discovered(classFile.getName(), methodInfo.getName(), methodInfo.getDescriptor(), annotation.getTypeName());
				}
			}
		}	
	}


    /**
     * Gets the Resource iterator for URL with Filter.
     * 
     * Returns a ClassFileIterator if the encountered URL is a folder else returns a JarFileIterator
     * 
     * @param url
     * @param filter
     * @return
     * @throws IOException
     */
    private ResourceIterator getResourceIterator(URL url, Filter filter) throws IOException {
        String urlString = url.toString();
        if (urlString.endsWith("!/")) {
            urlString = urlString.substring(4);
            urlString = urlString.substring(0, urlString.length() - 2);
            url = new URL(urlString);
        }

        if (!urlString.endsWith("/")) {
            return new JarFileIterator(url.openStream(), filter);
        } else {

        	if (!url.getProtocol().equals("file")) {
                throw new IOException("Unable to understand protocol: " + url.getProtocol());
            }

            File f = new File(url.getPath());
            
            if (f.isDirectory()) {
                return new ClassFileIterator(f, filter);
            } else {
                return new JarFileIterator(url.openStream(), filter);
            }
        }
    }

}
