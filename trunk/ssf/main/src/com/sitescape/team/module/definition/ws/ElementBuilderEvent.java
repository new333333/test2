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

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.ValidationException;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Event;
import com.sitescape.team.module.mail.MailModule;

public class ElementBuilderEvent extends AbstractElementBuilder {
	protected boolean build(Element element, Object obj, DefinableEntity entity) {
		if (obj instanceof Event) {
			Event event = (Event) obj;
			StringWriter writer = new StringWriter();
			Calendar cal = moduleSource.getIcalModule().generate(entity, Arrays.asList(event), MailModule.DEFAULT_TIMEZONE);
			CalendarOutputter out = new CalendarOutputter();
			try {
				out.output(cal, writer);
			} catch(IOException e) {
			} catch(ValidationException e) {
			}
			element.add(org.dom4j.DocumentHelper.createCDATA(writer.toString()));
		} else {
			element.setText(obj.toString());
		}
		return true;
	}

}
