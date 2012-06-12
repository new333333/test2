/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;


public class Notify {
	//string values need to match definitionBuilder
	public enum NotifyType {
		summary ,
		full,
		text,
		interactive;
		public boolean includeICalAsAlternative() {
			if (this.equals(NotifyType.full) || 
					this.equals(NotifyType.interactive)) return true;
			return false;
		}
	};
	
	protected NotifyType type=NotifyType.summary;
	protected Locale locale;
	protected DateFormat dateFormat,dateTimeFormat;
	protected Set files= null;
	protected Map<DefinableEntity, Set> events= null;// sorted by entry
	protected Timestamp startTs;
	protected boolean includeAttachments=false;
	protected boolean redacted=false;
	protected TimeZone timezone;
	protected Map<String, Object> variables;

	public Notify(NotifyType type, Locale locale, TimeZone timezone, Date startDate) {
		this.type = type;
		this.locale = locale;
		/*
		 * The dates returned from the database are java.sql.Timestamp
		 * compareTo doesn't work on a Date that is not a Timestamp
		 */
		this.startTs = new Timestamp(startDate.getTime());
		this.dateTimeFormat=DateFormat.getDateTimeInstance(DateFormat.MEDIUM,  DateFormat.FULL, locale);
		if (timezone == null) timezone = TimeZoneHelper.getDefault();
		this.timezone = timezone;
		this.dateTimeFormat.setTimeZone(timezone);
		
		this.dateFormat=DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
		this.timezone = timezone;
		this.dateFormat.setTimeZone(timezone);
		this.variables = new HashMap<String, Object>();
		

	}
	public Set getAttachments() {
		if (files == null) files = new HashSet();//may be called for same attachment
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
	public boolean isRedacted() {
		return redacted;
	}
	public void setRedacted(boolean redacted) {
		this.redacted = redacted;
	}
	public Map<DefinableEntity, Set> getEvents() {
		if (events == null) events = new HashMap();
		return events;
	}
	public void addEvent(DefinableEntity entry, Event event) {
		Map events = getEvents();
		if (events.get(entry) == null) {
			events.put(entry, new HashSet()); //may be called for same event
		}
		Set entryEventsList = (Set)events.get(entry);
		entryEventsList.add(event);
	}
	public void clearEvents() {
		if (events != null) events.clear();
	}
	public NotifyType getType() {
		return type;
	}
	public Locale getLocale() {
		return this.locale;
	}
	public TimeZone getTimeZone() {
		return this.timezone;
	}
	public DateFormat getDateFormat() {
		return dateFormat;
	}
	public DateFormat getDateTimeFormat() {
		return dateTimeFormat;
	}
	public Date getStartDate() {
		return startTs;
	}
	public void setVariable(String name, Object value) {
		this.variables.put(name, value);
	}
	public Object getVariable(String name) {
		return this.variables.get(name);
	}

}
