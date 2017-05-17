/**
 * 
 */
package org.kablink.teaming.spring.security.web.authentication.session;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

/**
 * This class exists only to bring back the good old nice behavior of Spring security 3.
 * Apparently the guys developing Spring security 4 decided to go backward and make
 * life more difficult for application developers for no good reason. One of those
 * examples where some people try to fix what is not broken.
 * 
 * This class allows SessionFixationProtectionStrategy in our security context files
 * to work the way it has without altering the bean configuration or the behavior.
 * 
 * @author jong
 *
 */
public class SessionFixationProtectionStrategy extends org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy {

	private boolean migrateSessionAttributes = true;
	private List<String> retainedAttributes = null;

	@Override
    protected Map<String, Object> extractAttributes(HttpSession session) {
		return _createMigratedAttributeMap(session);
    }

	private HashMap<String, Object> _createMigratedAttributeMap(HttpSession session) {
		HashMap<String, Object> attributesToMigrate = null;

		if (migrateSessionAttributes || retainedAttributes == null) {
			attributesToMigrate = new HashMap<String, Object>();

			Enumeration enumer = session.getAttributeNames();

			while (enumer.hasMoreElements()) {
				String key = (String) enumer.nextElement();
				if (!migrateSessionAttributes && !key.startsWith("SPRING_SECURITY_")) {
					// Only retain Spring Security attributes
					continue;
				}
				attributesToMigrate.put(key, session.getAttribute(key));
			}
		}
		else {
			// Only retain the attributes which have been specified in the
			// retainAttributes list
			if (!retainedAttributes.isEmpty()) {
				attributesToMigrate = new HashMap<String, Object>();
				for (String name : retainedAttributes) {
					Object value = session.getAttribute(name);

					if (value != null) {
						attributesToMigrate.put(name, value);
					}
				}
			}
		}
		return attributesToMigrate;
	}

	// This method used to be in Spring security 3 class, but not any more with Spring security 4.
    public void setRetainedAttributes(List<String> retainedAttributes) {
        this.retainedAttributes = retainedAttributes;
    }
    
	public void setMigrateSessionAttributes(boolean migrateSessionAttributes) {
		super.setMigrateSessionAttributes(migrateSessionAttributes);
		this.migrateSessionAttributes = migrateSessionAttributes;
	}
}
