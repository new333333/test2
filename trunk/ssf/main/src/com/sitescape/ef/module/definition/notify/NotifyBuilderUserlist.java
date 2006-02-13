package com.sitescape.ef.module.definition.notify;

import java.util.Iterator;
import java.util.Collection;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.util.ResolveIds;
import com.sitescape.ef.domain.Principal;
/**
*
* @author Janet McCann
*/
public class NotifyBuilderUserlist extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
		   Collection users = ResolveIds.getPrincipals(attribute);
		   if ((users != null) && !users.isEmpty()) {
	    		for (Iterator iter=users.iterator();iter.hasNext();) {
		    		Element value = element.addElement("value");		    		
		    		value.setText(((Principal)iter.next()).getTitle());
	    		}
	    	} else {
	    		element.addElement("value");
	    	}
	    	return true;
	   }
}
