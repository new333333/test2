package com.sitescape.team.ical.util;

import java.util.Iterator;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateList;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.Observance;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.component.VToDo;
import net.fortuna.ical4j.model.property.Completed;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStamp;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Due;
import net.fortuna.ical4j.model.property.ExDate;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.RDate;
import net.fortuna.ical4j.transform.Transformer;

/**
 * Makes VTimeZone component shorter.
 * Only VEVENT and VTODO are implemented.
 * 
 */
public class TimeZoneShorterTransformer extends Transformer {

	/*
	 * 
	 * 
	 * @see net.fortuna.ical4j.transform.Transformer#transform(net.fortuna.ical4j.model.Calendar)
	 */
	public Calendar transform(Calendar calendar) {
		Calendar resultCalendar = new Calendar();
		
		
		ComponentList vTimeZones = calendar.getComponents(Component.VTIMEZONE);
		ComponentList filteredVTimeZones = new ComponentList();
		
		Iterator vTimeZonesIt = vTimeZones.iterator();
		while (vTimeZonesIt.hasNext()) {
			VTimeZone vTimeZone = (VTimeZone)vTimeZonesIt.next();
			VTimeZone newVTimeZone = new VTimeZone();
			newVTimeZone.getProperties().addAll(vTimeZone.getProperties());
			filteredVTimeZones.add(newVTimeZone);
			copyApplicableObservancesForVEvents(vTimeZone, newVTimeZone, calendar.getComponents(Component.VEVENT));
			copyApplicableObservancesForVTodos(vTimeZone, newVTimeZone, calendar.getComponents(Component.VTODO));
		}
		
		resultCalendar.getComponents().addAll(filteredVTimeZones);
		resultCalendar.getComponents().addAll(calendar.getComponents(Component.VEVENT));
		resultCalendar.getComponents().addAll(calendar.getComponents(Component.VTODO));
		resultCalendar.getProperties().addAll(calendar.getProperties());
		
		return resultCalendar;
		
	}

	private void copyApplicableObservancesForVEvents(VTimeZone timeZone, VTimeZone newVTimeZone, ComponentList vEvents) {
		Iterator it = vEvents.iterator();
		while (it.hasNext()) {
			VEvent vEvent = (VEvent)it.next();
			copyApplicableObservancesForDtStart(timeZone, newVTimeZone, vEvent);
			copyApplicableObservancesForDtEnd(timeZone, newVTimeZone, vEvent);
			copyApplicableObservancesForExDate(timeZone, newVTimeZone, vEvent);
			copyApplicableObservancesForRDate(timeZone, newVTimeZone, vEvent);
		}
	}

	private void copyApplicableObservancesForVTodos(VTimeZone timeZone, VTimeZone newVTimeZone, ComponentList vToDos) {
		Iterator it = vToDos.iterator();
		while (it.hasNext()) {
			VToDo vToDo = (VToDo)it.next();
			copyApplicableObservancesForDtStart(timeZone, newVTimeZone, vToDo);
			copyApplicableObservancesForDue(timeZone, newVTimeZone, vToDo);
			copyApplicableObservancesForExDate(timeZone, newVTimeZone, vToDo);
			copyApplicableObservancesForRDate(timeZone, newVTimeZone, vToDo);
		}
	}
	
	private void copyApplicableObservancesForDue(VTimeZone timeZone, VTimeZone newVTimeZone, Component component) {
		Due due = (Due)component.getProperties().getProperty(Property.DUE);
		if (due != null) {
			copyApplicableObservancesForDate(timeZone, newVTimeZone, due.getDate());
		}
	}

	private void copyApplicableObservancesForRDate(VTimeZone timeZone, VTimeZone newVTimeZone, Component component) {
		RDate rDate = (RDate)component.getProperties().getProperty(Property.RDATE);
		if (rDate != null) {
			copyApplicableObservancesForDates(timeZone, newVTimeZone, rDate.getDates());
		}
	}
	
	private void copyApplicableObservancesForExDate(VTimeZone timeZone, VTimeZone newVTimeZone, Component component) {
		ExDate exDate = (ExDate)component.getProperties().getProperty(Property.EXDATE);
		if (exDate != null) {
			copyApplicableObservancesForDates(timeZone, newVTimeZone, exDate.getDates());
		}
	}
	
	private void copyApplicableObservancesForDtStart(VTimeZone timeZone, VTimeZone newVTimeZone, Component component) {
		DtStart dtStart = (DtStart)component.getProperties().getProperty(Property.DTSTART);
		if (dtStart != null) {
			copyApplicableObservancesForDate(timeZone, newVTimeZone, dtStart.getDate());
		}
	}
	
	private void copyApplicableObservancesForDtEnd(VTimeZone timeZone, VTimeZone newVTimeZone, Component component) {
		DtEnd dtEnd = (DtEnd)component.getProperties().getProperty(Property.DTEND);
		if (dtEnd != null) {
			copyApplicableObservancesForDate(timeZone, newVTimeZone, dtEnd.getDate());
		}
	}
	
	private void copyApplicableObservancesForDates(VTimeZone timeZone, VTimeZone newVTimeZone, DateList dates) {
		if (dates == null) {
			return;
		}
		
		Iterator datesIs = dates.iterator();
		while(datesIs.hasNext()) {
			Date date = (Date)datesIs.next();
			copyApplicableObservancesForDate(timeZone, newVTimeZone, date);
		}
	}

	private void copyApplicableObservancesForDate(VTimeZone timeZone, VTimeZone newVTimeZone, Date date) {
		if (date == null) {
			return;
		}
		Observance observance = timeZone.getApplicableObservance(date);
		if (!newVTimeZone.getObservances().contains(observance)) {
			newVTimeZone.getObservances().add(observance);
		}
	}
}
