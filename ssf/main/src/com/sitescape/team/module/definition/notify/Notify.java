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
		if (dateFormat == null) dateFormat=DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, getLocale());
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
