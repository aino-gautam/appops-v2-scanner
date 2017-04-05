
package org.appops.scanner.resource;

import java.io.InputStream;


public interface ResourceIterator {

    /**
     * Please close after use.
     * 
     * @return null if no more streams left to iterate on
     */
    InputStream next();

    /**
     * Close.
     */
    void close();
}
