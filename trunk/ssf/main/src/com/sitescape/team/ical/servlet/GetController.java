package com.sitescape.team.ical.servlet;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.User;
import com.sitescape.team.util.XmlFileUtil;
import com.sitescape.team.web.servlet.SAbstractController;

/**
 * Outputs iCalendar for given entry. The output contains all entry events or empty calendar (only iCalendar header) 
 * if entry doesn't have any events. 
 * 
 * @author Pawel Nowicki
 */
public class GetController extends SAbstractController {

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Long binderId = new Long(RequestUtils.getRequiredStringParameter(request, "bi"));
		Long entryId = new Long(RequestUtils.getRequiredStringParameter(request, "entry"));
		Map folderEntries = getFolderModule().getEntryTree(binderId, entryId);
		FolderEntry entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
		
		if (getFolderModule().testAccess(entry, "getEntry")) {
			response.resetBuffer();
			response.setContentType("text/calendar; charset=" + XmlFileUtil.FILE_ENCODING);
			response.setHeader("Cache-Control", "private");
			response.setHeader("Pragma", "no-cache");
			
			CalendarOutputter calendarOutputter = new CalendarOutputter();
			Calendar calendar = getIcalGenerator().getICalendarForEntryEvents(entry);
			calendarOutputter.output(calendar, response.getWriter());
		}	
		
		response.flushBuffer();
		return null;
	}

}
