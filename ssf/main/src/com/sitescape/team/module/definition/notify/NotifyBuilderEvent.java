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
package com.sitescape.team.module.definition.notify;

import java.text.DateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.dom4j.Element;
import org.dom4j.Node;
import org.joda.time.DateTime;
import org.joda.time.YearMonthDay;

import com.sitescape.team.calendar.EventsViewHelper;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebUrlUtil;

/**
*
* @author Janet McCann
*/
public class NotifyBuilderEvent extends AbstractNotifyBuilder {
	
	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
		   DefinableEntity entity = attribute.getOwner().getEntity();
		   	Object obj = attribute.getValue();
	    	if (obj == null) return true;
	    	if (obj instanceof Event) {
	    		Event event = (Event)obj;
	    		
	    		YearMonthDay startDate = (new DateTime(event.getDtStart().getTime())).toYearMonthDay();
	    		YearMonthDay endDate = (new DateTime(event.getDtEnd().getTime())).toYearMonthDay();
	    		
	    		DateFormat dateFormat = notifyDef.getDateFormat();
	    		if (event.isAllDayEvent()) {
	    			dateFormat = DateFormat.getDateInstance(DateFormat.LONG, notifyDef.getLocale());
	    			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	    		}
	    		
	    		Element startDateEl = element.addElement("startDate");
	    		startDateEl.setText(dateFormat.format(event.getDtStart().getTime()));
	    		Element endDateEl = element.addElement("endDate");
	    		if (((event.isAllDayEvent() && startDate.isEqual(endDate))) || !event.hasDuration()) {
	    			endDateEl.setText("");
	    		} else {
	    			endDateEl.setText(dateFormat.format(event.getDtEnd().getTime())); 
	    		}
	    		
	    		String freqString = event.getFrequencyString();
	    		
	    		Element frequencyEl = element.addElement("frequency");
	    		if (freqString == null) {
	    			frequencyEl.setText("");
	    		} else {
	    			frequencyEl.setText(NLT.get("calendar.frequency", notifyDef.getLocale()) + ": " + EventsViewHelper.eventToRepeatHumanReadableString(event, notifyDef.getLocale()));
	    		}
	    		  
	    		notifyDef.addEvent(entity, event);
	    	} else { 
	    		element.setText(obj.toString());
	    	}
	    	return true;
	    }
}
