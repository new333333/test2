package com.sitescape.team.ical;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import net.fortuna.ical4j.data.CalendarParser;
import net.fortuna.ical4j.data.CalendarParserFactory;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.data.UnfoldingReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.folder.FolderModule;

public class IcalParser {

	protected static Log logger = LogFactory.getLog(IcalParser.class);
	
	/**
	 * 
	 * 
	 * @param binderModule
	 * @param folderModule
	 * @param folderId
	 * @param icalFile
	 * @return id list of created entries
	 * @throws IOException
	 * @throws ParserException
	 */
	public static List parse (BinderModule binderModule, FolderModule folderModule, Long folderId, InputStream icalFile) throws IOException, ParserException {
		CalendarParserFactory parser = CalendarParserFactory.getInstance();
		CalendarParser calendarParser = parser.createParser();
		ICalContentHandler handler = new ICalContentHandler(binderModule, folderModule, folderId); 
		try {
			calendarParser.parse(new UnfoldingReader(new InputStreamReader(icalFile)), handler);
		} catch (net.fortuna.ical4j.data.ParserException exc) {
			logger.error("Can not parse iCalendar file. Maybe it's not iCalendar file but I try to parse all mailed in attachments. ", exc);
		}
		return handler.getAddedEntriesIds();
	}
	
}
