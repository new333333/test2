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

import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.ZoneInfo;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.ExportHelper;
import org.kablink.teaming.web.util.MarkupUtil;


/**
 * 
 * @author Brian Kim
 */
public class ElementBuilderMashupCanvas extends AbstractElementBuilder {
	protected boolean build(Element element, Object obj, String dataElemType, String dataElemName) {
		if (obj != null && element != null) {
			element.setText(MarkupUtil.markupStringReplacementForMashupCanvasExport(obj.toString()));
			ZoneInfo zoneInfo = ExportHelper.getZoneInfo();
			element.addAttribute("zoneUUID", String.valueOf(zoneInfo.getId()));
		}
		return true;
	}

    protected boolean build(Element element, Object obj, DefinableEntity entity, String dataElemType, String dataElemName) {
		//First, save all of the mashup flags (dataElemName.*)
    	Element parent = element.getParent();
		if (parent != null) {
			Map attrs = entity.getCustomAttributes();
			Iterator itAttrs = attrs.keySet().iterator();
			while (itAttrs.hasNext()) {
				String key = (String) itAttrs.next();
				if (key.startsWith(dataElemName + "__")) {
					CustomAttribute attr = (CustomAttribute) attrs.get(key);
					Element newEle = DocumentHelper.createElement("attribute");
					newEle.addAttribute("name", key);
					Object attrValue = attr.getValue();
					if (attrValue instanceof Document) {
						//Output document objects as XML (bug #768475)
						newEle.addAttribute("type", "xml");
						newEle.setText(((Document)attrValue).asXML());
					} else {
						newEle.addAttribute("type", "text");
						newEle.setText(attrValue.toString());
					}
					parent.add(newEle);
				}
			}
		}
		String mashupValue = DefinitionHelper.fixupMashupCanvasGraphics(obj.toString(), entity);
		mashupValue = MarkupUtil.markupStringReplacementForExport(mashupValue);
    	return build(element, mashupValue, dataElemType, dataElemName);
    }
}
