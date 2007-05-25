/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.definition.ws;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.util.ResolveIds;

/**
 *
 * @author Jong Kim
 */
public class ElementBuilderUserlist extends AbstractElementBuilder {

	   protected boolean build(Element element, CustomAttribute attribute) {
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
