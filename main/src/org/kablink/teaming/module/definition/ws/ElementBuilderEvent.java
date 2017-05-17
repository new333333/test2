/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.definition.ws;

import java.io.IOException;
import java.io.StringWriter;

import java.util.Arrays;
import java.util.TimeZone;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.dom4j.Element;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.Event.FreeBusyType;
import org.kablink.teaming.ical.util.ICalUtils;
import org.kablink.teaming.remoting.ws.model.CustomStringField;
import org.kablink.teaming.remoting.ws.model.CustomEventField;

public class ElementBuilderEvent extends AbstractElementBuilder {
	protected boolean build(Element element, org.kablink.teaming.remoting.ws.model.DefinableEntity entityModel, Object obj, DefinableEntity entity, String dataElemType, String dataElemName) {
		if (obj instanceof Event) {
			Event event = (Event) obj;
			if(element != null)
				element.add(org.dom4j.DocumentHelper.createCDATA(eventToIcalString(entity,event)));
			if(entityModel != null) {
				if(entityModel.isEventAsIcalString())
					entityModel.addCustomStringField(new CustomStringField(dataElemName, dataElemType, eventToIcalString(entity,event)));
				else
					entityModel.addCustomEventField(new CustomEventField(dataElemName, dataElemType, eventFromDomainToRemote(event)));
			}
		} else {
			throw new IllegalArgumentException("Unexpected class for event data: " + obj.getClass().getName());
		}
		return true;
	}

	private String eventToIcalString(DefinableEntity entity, Event event) {
		StringWriter writer = new StringWriter();
		Calendar cal = context.getIcalModule().generate(entity, Arrays.asList(event), null);
		CalendarOutputter out = ICalUtils.getCalendarOutputter();
		try {
			out.output(cal, writer);
		} catch(IOException e) {
			
		} catch(ValidationException e) {
		}
		String result = writer.toString();
		try {
			writer.close();
		} catch (IOException e) {
		}
		return result;
	}
	
	private org.kablink.teaming.remoting.ws.model.Event eventFromDomainToRemote(Event event) {
		org.kablink.teaming.remoting.ws.model.Event remoteModel = new org.kablink.teaming.remoting.ws.model.Event();
		
		remoteModel.setDtStart(event.getDtStart());
		remoteModel.setDtCalcStart(event.getDtCalcStart());
		remoteModel.setDtEnd(event.getDtEnd());
		remoteModel.setDtCalcEnd(event.getDtCalcEnd());
		remoteModel.setDuration(org.kablink.teaming.remoting.ws.model.Duration.toRemoteModel(event.getDuration()));
		remoteModel.setFrequency(event.getFrequency());
		remoteModel.setInterval(event.getInterval());
		remoteModel.setUntil(event.getUntil());
		remoteModel.setCount(event.getCount());
		remoteModel.setWeekStart(event.getWeekStart());
		remoteModel.setTimeZoneSensitive(event.isTimeZoneSensitive());
		TimeZone timeZone = event.getTimeZone();
		if(timeZone != null)
			remoteModel.setTimeZone(timeZone.getID());
		remoteModel.setUid(event.getUid());
		FreeBusyType freeBusy = event.getFreeBusy();
		if(freeBusy != null)
			remoteModel.setFreeBusy(freeBusy.name());
		remoteModel.setBySecond(event.getBySecond());
		remoteModel.setByMinute(event.getByMinute());
		remoteModel.setByHour(event.getByHour());
		remoteModel.setByDay(org.kablink.teaming.remoting.ws.model.Event.DayAndPosition.toRemoteModel(event.getByDay()));
		remoteModel.setByMonthDay(event.getByMonthDay());
		remoteModel.setByYearDay(event.getByYearDay());
		remoteModel.setByWeekNo(event.getByWeekNo());
		remoteModel.setByMonth(event.getByMonth());
		
		return remoteModel;
	}

}
