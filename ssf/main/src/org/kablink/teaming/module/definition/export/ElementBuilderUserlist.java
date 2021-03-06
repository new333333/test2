/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.module.definition.export;

import java.util.Collection;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.module.definition.export.ElementBuilder.BuilderContext;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.ExportHelper;

/**
 *
 * @author Brian Kim
 */
public class ElementBuilderUserlist extends AbstractElementBuilder {

    public boolean buildElement(Element element, DefinableEntity entity, String dataElemType, String dataElemName, BuilderContext context) {
    	this.context = context;
    	if(element != null) {
	    	element.addAttribute("name", dataElemName + ".principalNames");
	    	element.addAttribute("type", dataElemType);
    	}
        CustomAttribute attribute = entity.getCustomAttribute(dataElemName);
		try {
			if (attribute != null) 
    			return build(element, entity, dataElemType, dataElemName, attribute);
			else 
    			return build(element, entity, dataElemType, dataElemName);
		} catch (Exception e) {
			logger.debug(e);
			if(element != null) {
				element.setText(NLT.get("export.error.attribute"));
			}
			return true;
    	}
    }
	protected boolean build(Element element, DefinableEntity entity, String dataElemType, String dataElemName, CustomAttribute attribute) {
    	return build(element, entity, attribute);
	}

	protected boolean build(Element element, DefinableEntity entity, CustomAttribute attribute) {
	    Collection<Principal> users = ResolveIds.getPrincipals(attribute);
	    if ((users != null) && !users.isEmpty()) {
    		for (Principal p:users) {
    			DefinitionHelper.addPrincipalToDocument(element, p);
    			ExportHelper.addPrincipalToDocument(element, p);
    		}
    	}
    	return true;
	}
	    
}
