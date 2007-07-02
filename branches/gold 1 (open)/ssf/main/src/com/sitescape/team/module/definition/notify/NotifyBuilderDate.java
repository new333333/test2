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
package com.sitescape.team.module.definition.notify;

import java.util.Date;
import java.util.Map;

import org.dom4j.Element;

import com.sitescape.team.domain.CustomAttribute;

/**
 *
 * @author Janet McCann
 */
public class NotifyBuilderDate extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
	    	Object obj = attribute.getValue();
	    	if (obj instanceof Date) {
	    		Date date = (Date)obj;
	    		element.setText(notifyDef.getDateFormat().format(date));
	    	} else if (obj != null) {
	    		element.setText(obj.toString());
	    	}
	    	return true;
	    }
}
