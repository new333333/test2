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

import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Description;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;

/**
 * 
 * @author Jong Kim
 */
public class ElementBuilderDescription extends AbstractElementBuilder {
	protected boolean build(Element element, Object obj) {
		if (obj instanceof Description) {
			Description desc = (Description) obj;
			element.setText(desc.getText());
			element.addAttribute("format", String.valueOf(desc.getFormat()));
		} else if (obj != null) {
			element.setText(obj.toString());
			element.addAttribute("format", String
					.valueOf(Description.FORMAT_NONE));
		}
		return true;
	}

}
