package org.kablink.teaming.context.request;

import java.util.Map;
import java.util.HashMap;
import javax.portlet.PortletSession;

import org.kablink.teaming.ObjectKeys;

/**
 * Saves properties for life of session.  Only accessed if use is shared, otherwise use database
 * @author Janet
 *
 */
public class PortletSessionContext extends BaseSessionContext {
	private PortletSession session;
	public PortletSessionContext(PortletSession session) {
		this.session = session;
	}

	protected Map getProperties() {
    	Map props = (Map)session.getAttribute(ObjectKeys.SESSION_USERPROPERTIES, PortletSession.APPLICATION_SCOPE);	
    	if (props == null) {
    		props = new HashMap();
    		session.setAttribute(ObjectKeys.SESSION_USERPROPERTIES, props, PortletSession.APPLICATION_SCOPE);
    	}
    	return props;
    }
}
