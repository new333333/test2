/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.module.definition.notify;

import java.text.DateFormat;
import java.util.Calendar;

import org.apache.velocity.VelocityContext;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.EventsViewHelper;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Event;


/**
*
* @author Janet McCann
*/
public class NotifyBuilderEvent extends AbstractNotifyBuilder {
	
    public String getDefaultTemplate() {
    	return "event.vm";
    }
	protected void build(NotifyVisitor visitor, String template, VelocityContext ctx, CustomAttribute attr) {
		Object obj = attr.getValue();
		if (!(obj instanceof Event)) return;
		Event event = (Event)obj;
		//if processing a calendar or task attach events to mail as icals
    	String family = (String)visitor.getParam("org.kablink.teaming.notify.params.family");
    	if (ObjectKeys.FAMILY_CALENDAR.equals(family) || ObjectKeys.FAMILY_TASK.equals(family)) {
    		visitor.getNotifyDef().addEvent(visitor.getEntity(), event);
    	}
		Calendar st = event.getDtStart();
		Calendar en = event.getDtEnd();
			
		DateFormat dateFormat = null;
		if (!event.isAllDayEvent()) {
			dateFormat = visitor.getNotifyDef().getDateTimeFormat();
		} else {
			dateFormat = DateFormat.getDateInstance(DateFormat.LONG, visitor.getNotifyDef().getLocale());
			dateFormat.setTimeZone(visitor.getNotifyDef().getTimeZone());
		}

		ctx.put("ssEvent_startString", dateFormat.format(st.getTime()));
		ctx.put("ssEvent_endString", dateFormat.format(en.getTime()));
		ctx.put("ssEvent_repeatString", EventsViewHelper.eventToRepeatHumanReadableString(event, visitor.getNotifyDef().getLocale()));
		super.build(visitor, template, ctx);
	}
}
