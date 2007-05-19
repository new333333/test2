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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.text.DateFormat;
import java.util.Date;
import java.sql.Timestamp;

import net.fortuna.ical4j.model.Calendar;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;

public class Notify {
	public static final String FULL="full";
	public static final String SUMMARY="summary";
	
	protected boolean full=false;
	protected Locale locale;
	protected DateFormat dateFormat;
	protected Set files= null;
	protected Map events= null;// sorted by entry
	protected Timestamp startTs;
	protected boolean includeAttachments=false;
	
	public Set getAttachments() {
		if (files == null) files = new HashSet();
		return files;
	}
	public void addAttachment(FileAttachment att) {
		getAttachments().add(att);
	}
	public void clearAttachments() {
		if (files != null) files.clear();
	}
	public boolean isAttachmentsIncluded() {
		return includeAttachments;
	}
	public void setAttachmentsIncluded(boolean includeAttachments) {
		this.includeAttachments = includeAttachments;
	}
	public Map getEvents() {
		if (events == null) events = new HashMap();
		return events;
	}
	public void addEvent(DefinableEntity entry, Event event) {
		Map events = getEvents();
		if (events.get(entry) == null) {
			events.put(entry, new ArrayList());
		}
		List entryEventsList = (List)events.get(entry);
		entryEventsList.add(event);
	}
	public void clearEvents() {
		if (events != null) events.clear();
	}
	public String getType() {
		if (isFull()) return FULL;
		return SUMMARY;
	}
	public void setType(String type) {
		if (FULL.equals(type)) setFull(true);
		else if (SUMMARY.equals(type)) setSummary(true);
		else throw new IllegalArgumentException("Illegal type value");
	}
	public boolean isFull() {
		return full;
	}
	public void setFull(boolean full) {
		this.full = full;
	}
	public boolean isSummary() {
		return !full;
	}
	public void setSummary(boolean summary) {
		this.full = !summary;
	}

	public Locale getLocale() {
		return this.locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public DateFormat getDateFormat() {
		if (dateFormat == null) dateFormat=DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL);
		return dateFormat;
	}
	public void setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
	/**
	 * The dates returned from the database are java.sql.Timestamp
	 * compareTo doesn't work on a Date that is not a Timestamp
	 * @return
	 */
	public Date getStartDate() {
		return startTs;
	}
	public void setStartDate(Date startDate) {
		this.startTs = new Timestamp(startDate.getTime());
	}
}
