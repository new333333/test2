package org.kablink.teaming.context.request;

import java.util.HashMap;
import java.util.Map;
/**
 * Base class saves properties for life of request.  Only accessed if use is shared, otherwise use database
 * @author Janet
 *
 */
public class BaseSessionContext implements SessionContext {
	private Map properties;
    public synchronized Object getProperty(Object key) {
    	return getProperties().get(key);
    }
    public synchronized void setProperty(Object key, Object value) {
    	getProperties().put(key, value);

    }
    protected Map getProperties() {
    	if (properties == null) {
    		properties = new HashMap();
    	}
    	return properties;
    }
}
