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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Event;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;

public class ElementBuilderEvent extends AbstractElementBuilder {
	protected boolean build(Element element, Object obj) {
		if (obj instanceof Event) {
			Event event = (Event) obj;
			Element value = element.addElement("startDate");
			value.setText(DateFormat.getInstance().format(
					event.getDtStart().getTime()));
			value = element.addElement("endDate");
			value.setText(DateFormat.getInstance().format(
					event.getDtEnd().getTime()));
		} else {
			element.setText(obj.toString());
		}
		return true;
	}

}
