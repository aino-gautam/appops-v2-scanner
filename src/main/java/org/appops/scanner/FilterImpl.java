package org.appops.scanner;

/**
 * Basic implementation to skip well-known packages and allow only *.class files
 * 
 * @author Debasish Padhy
 */
public class FilterImpl implements Filter {

    /** The ignored packages. */
    private transient String[] ignoredPackages = { "javax", "java", "sun", "com.sun", "javassist"};

    /* @see org.appops.scanner.Filter#accepts(java.lang.String) */
    @Override
    public final boolean accepts(String filename) {
        if (filename.endsWith(".class")) {
            if (filename.startsWith("/")) {
                filename = filename.substring(1);
            }
            if (!ignoreScan(filename.replace('/', '.'))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param intf
     * @return
     */
    private boolean ignoreScan(String intf) {
        for (String ignored : ignoredPackages) {
            if (intf.startsWith(ignored + ".")) {
                return true;
            }
        }
        return false;
    }

}
