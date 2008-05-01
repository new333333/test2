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
/**
 * 
 */
package com.sitescape.team.module.ical;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.List;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.Definition;

/**
 * Convert back and forth between iCal text and Events
 * 
 * @author Joe
 *
 */
public interface IcalModule {

	/*
	 * Users of parseEvents need to provide an EventHandler implementation that
	 *  processes the Events as they are parsed.
	 */
	public static interface EventHandler {
		/**
		 * handleEvent
		 * 
		 * Called each time a VEVENT in the ical input is converted to an Event.  The
		 *  DESCRIPTION and SUMMARY from the VEVENT are passed along, but will be null
		 *  if they are absent from the VEVENT.
		 * 
		 * @param event
		 * @param description
		 * @param summary
		 */
		void handleEvent(Event event, String description, String summary);
		
		/**
		 * handleTodo
		 * 
		 * Called each time a VTODO in the ical input is converted to an Event.  The
		 *  DESCRIPTION and SUMMARY from the VTODO are passed along, but will be null
		 *  if they are absent from the VTODO.
		 * 
		 */
		void handleTodo(Event event, String description, String summary, String priority, String status, String completed, String location, List attendee);		
	}
	
	/**
	 * parseEvents
	 * 
	 * Extracts the VEVENTs from an ical input stream, converts each to an Event, and calls
	 *   the supplied handler with the event, along with the SUMMARY and DESCRIPTION, if any,
	 *   from the VEVENT
	 * 
	 * @param icalData
	 * @param handler
	 * @throws IOException
	 * @throws ParserException
	 */
	void parseEvents(Reader icalData, EventHandler handler)
		throws IOException, ParserException;

	/**
	 * parseEvents
	 * 
	 * Convenience method that returns the parsed events as a list, rather than calling a handler.
	 *  SUMMARY and DESCRIPTION from the VEVENTS is discarded.
	 * 
	 * @param icalData
	 * @param handler
	 * @throws IOException
	 * @throws ParserException
	 */
	List<Event> parseEvents(Reader icalData)
		throws IOException, ParserException;

	/**
	 * parseToEntries
	 * 
	 * Creates an entry in the given folder for each VEVENT in the given ical input stream, returning a list of
	 *  the added IDs.
	 * 
	 * @param folderId
	 * @param defId Definition id or null
	 * @param icalFile
	 * @return id list of created entries
	 * @throws IOException
	 * @throws ParserException
	 */
	List parseToEntries (final Long folderId, InputStream icalFile)
		throws IOException, ParserException;

	List parseToEntries (final Folder folder, Definition def, InputStream icalFile)
		throws IOException, ParserException;

	public Calendar generate(DefinableEntity entry, Collection events, String defaultTimeZoneId);
	
	public Calendar generate(List folderEntries, String defaultTimeZoneId);
}
