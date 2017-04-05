package org.appops.scanner;

import static org.junit.Assert.*;

import java.net.URL;

import org.junit.Test;

public class DiscovererTest {

	//@Test
	public void testUrlsFromPath() {

		String[] paths = new String[2];

		paths[0] = "D:/git/appops-v2.1/appops/appops-infra/appops-infra-web/WebContent/WEB-INF/";
		paths[1] = "D:/git/appops-v2.1/appops/appops-infra/appops-infra-web/target/appops-infra-web-0.0.1-SNAPSHOT/WEB-INF";

		ClasspathDiscoverer clsDiscoverer = new ClasspathDiscoverer(paths, true);

		URL[] urls = clsDiscoverer.findResources();

		assertTrue(urls != null);

	}

	@Test
	public void testUrlsFromClasspath() {
		ClasspathDiscoverer clsDiscoverer = new ClasspathDiscoverer(new URL[1]);

		URL[] urls = clsDiscoverer.findResources();

		assertTrue(urls != null);
		
		/**
		 * D:\git\appops-v2.1\appops\appops-infra\appops-infra-scanner\target\test-classes;D:\git\appops-v2.1\appops\appops-infra\appops-infra-scanner\target\classes;D:\debasish\.m2\repository\org\javassist\javassist\3.19.0-GA\javassist-3.19.0-GA.jar;D:\debasish\.m2\repository\com\google\inject\guice\4.0\guice-4.0.jar;D:\debasish\.m2\repository\javax\inject\javax.inject\1\javax.inject-1.jar;D:\debasish\.m2\repository\aopalliance\aopalliance\1.0\aopalliance-1.0.jar;D:\debasish\.m2\repository\com\google\guava\guava\16.0.1\guava-16.0.1.jar;D:\debasish\.m2\repository\junit\junit\4.12\junit-4.12.jar;D:\debasish\.m2\repository\org\hamcrest\hamcrest-core\1.3\hamcrest-core-1.3.jar;/C:/Users/admin/eclipse/jee-mars/eclipse/configuration/org.eclipse.osgi/384/0/.cp/;/C:/Users/admin/eclipse/jee-mars/eclipse/configuration/org.eclipse.osgi/383/0/.cp/
		 */
	}

}
