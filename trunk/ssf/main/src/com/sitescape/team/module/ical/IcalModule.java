/**
 * 
 */
package com.sitescape.team.module.ical;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.List;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Event;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.folder.FolderModule;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;

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
	 * @param icalFile
	 * @return id list of created entries
	 * @throws IOException
	 * @throws ParserException
	 */
	List parseToEntries (final Long folderId, InputStream icalFile)
		throws IOException, ParserException;


	public Calendar generate(DefinableEntity entry, Collection events, String defaultTimeZoneId);
	
	public Calendar generate(List folderEntries, String defaultTimeZoneId);
}
