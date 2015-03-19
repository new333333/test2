/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.definition.notify;

import java.text.DateFormat;
import java.util.Calendar;

import org.apache.velocity.VelocityContext;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Event;

/**
 * ?
 * 
 * @author Janet McCann
 */
public class NotifyBuilderEvent extends AbstractNotifyBuilder {
    @Override
	public String getDefaultTemplate() {
    	return "event.vm";
    }
    
    @Override
	protected void build(NotifyVisitor visitor, String template, VelocityContext ctx, CustomAttribute attr) {
		Object obj = attr.getValue();
		if (!(obj instanceof Event)) return;
		Event event = (Event)obj;
		
		// If processing a calendar or task attach events to mail as
		// iCals.
    	String family = (String)visitor.getParam("org.kablink.teaming.notify.params.family");
    	if (ObjectKeys.FAMILY_CALENDAR.equals(family) || ObjectKeys.FAMILY_TASK.equals(family)) {
    		visitor.getNotifyDef().addEvent(visitor.getEntity(), event);
    	}
		Calendar st = event.getLogicalStart();
		Calendar en = event.getLogicalEnd();
			
		DateFormat dateFormat;
		if (event.isAllDayEvent()) {
			dateFormat = DateFormat.getDateInstance(DateFormat.LONG, visitor.getNotifyDef().getLocale());
			dateFormat.setTimeZone(TimeZoneHelper.getTimeZone("GMT"));
		} else {
			dateFormat = visitor.getNotifyDef().getDateTimeFormat();
		}

		ctx.put("ssEvent_startString", ((null == st) ? "" : dateFormat.format(st.getTime())));
		ctx.put("ssEvent_endString",   ((null == en) ? "" : dateFormat.format(en.getTime())));
		ctx.put("ssEvent_repeatString", EventsViewHelper.eventToRepeatHumanReadableString(event, visitor.getNotifyDef().getLocale()));
		super.build(visitor, template, ctx);
	}
}
