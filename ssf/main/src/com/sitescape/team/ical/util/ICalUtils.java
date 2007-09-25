package com.sitescape.team.ical.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.property.Summary;

public class ICalUtils {

	public static String getSummary(Calendar calendar) {
		
		Iterator componentIt = calendar.getComponents().iterator();
		while (componentIt.hasNext()) {
			Component component = (Component)componentIt.next();
			Summary summary = (Summary)component.getProperty(Property.SUMMARY);
			if (summary != null) {
				return summary.getValue();
			}
		}
		return null;
	}
	
	public static String getMethod(Calendar calendar) {
		
		Property method = calendar.getProperty(Property.METHOD);
		if (method != null) {
			return method.getValue();
		}
		
		return null;
	}

	public static ByteArrayOutputStream toOutputStraem(Calendar calendar) throws IOException, ValidationException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		CalendarOutputter calendarOutputter = new CalendarOutputter();
		calendarOutputter.output(calendar, out);
		return out;
	}
	
}
