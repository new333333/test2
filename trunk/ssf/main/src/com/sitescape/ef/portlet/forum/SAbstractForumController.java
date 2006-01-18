
package com.sitescape.ef.portlet.forum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.shared.EntryIndexUtils;
import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.portlet.SAbstractController;
import com.sitescape.ef.web.util.DateHelper;
import com.sitescape.ef.web.util.DefinitionUtils;
import com.sitescape.ef.web.util.PortletRequestUtils;
import com.sitescape.ef.web.util.Toolbar;
import com.sitescape.ef.web.util.WebHelper;

import org.dom4j.Document;
import org.dom4j.Element;
import org.springframework.web.portlet.bind.PortletRequestBindingException;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Janet McCann
 *
 */
public class SAbstractForumController extends SAbstractController {
	public ModelAndView returnToViewForum(RenderRequest request, RenderResponse response, Map formData, Long folderId) throws Exception {
		request.setAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		Map model = getShowFolder(formData, request, response, folderId);
		Object obj = model.get(WebKeys.CONFIG_ELEMENT);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		obj = model.get(WebKeys.CONFIG_DEFINITION);
		if ((obj == null) || (obj.equals(""))) 
			return new ModelAndView(WebKeys.VIEW_NO_DEFINITION, model);
		return new ModelAndView(WebKeys.VIEW_LISTING, model);
	}
	protected Map getShowFolder(Map formData, RenderRequest req, RenderResponse response,Long folderId) throws PortletRequestBindingException {
		Map folderEntries;
		Map model = new HashMap();
		String forumId = folderId.toString();
		folderEntries = getFolderModule().getFolderEntries(folderId);
		Folder folder = (Folder)folderEntries.get(ObjectKeys.BINDER);
	   	User user = RequestContextHolder.getRequestContext().getUser();
		//Build the beans depending on the operation being done
		model.put(WebKeys.USER_PRINCIPAL, user);
		model.put(WebKeys.FOLDER, folder);
		Folder topFolder = folder.getTopFolder();
		if (topFolder == null) {
			model.put(WebKeys.FOLDER_DOM_TREE, getFolderModule().getDomFolderTree(folderId, new TreeBuilder()));
		} else {
			model.put(WebKeys.FOLDER_DOM_TREE, getFolderModule().getDomFolderTree(topFolder.getId(), new TreeBuilder()));			
		}
		model.put(WebKeys.FOLDER_ENTRIES, folderEntries.get(ObjectKeys.ENTRIES));
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()).getProperties());
		model.put(WebKeys.USER_FOLDER_PROPERTIES, getProfileModule().getUserFolderProperties(user.getId(), folderId));
		model.put(WebKeys.SEEN_MAP,getProfileModule().getUserSeenMap(user.getId()));
		DefinitionUtils.getDefinitions(folder, model);
		ArrayList entries = (ArrayList) folderEntries.get(ObjectKeys.ENTRIES);
		Element view = (Element)model.get(WebKeys.CONFIG_ELEMENT);
		if (view != null) {
			List cals = view.selectNodes("./item[@name='calendarView']");
//		if ((cals != null) && !cals.isEmpty()) {
			getEvents(folder, entries, model, req, response);
//		}
		}
		req.setAttribute(WebKeys.URL_BINDER_ID,forumId);
		model.put(WebKeys.FOLDER_TOOLBAR, buildFolderToolbar(response, folder, forumId).getToolbar());
		return model;
	}  
	protected Toolbar buildFolderToolbar(RenderResponse response, Folder folder, String forumId) {
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		//	The "Add" menu
		List defaultEntryDefinitions = folder.getEntryDefs();
		PortletURL url;
		if (!defaultEntryDefinitions.isEmpty()) {
			int count = 1;
			toolbar.addToolbarMenu("1_add", NLT.get("toolbar.add"));
			Map qualifiers = new HashMap();
			String onClickPhrase = "if (self.ss_addEntry) {return(self.ss_addEntry(this))} else {return true;}";
			qualifiers.put(ObjectKeys.TOOLBAR_QUALIFIER_ONCLICK, onClickPhrase);
			for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
				Definition def = (Definition) defaultEntryDefinitions.get(i);
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL("ss_forum", true);
				adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_ADD_ENTRY);
				adapterUrl.setParameter(WebKeys.URL_BINDER_ID, forumId);
				adapterUrl.setParameter(WebKeys.URL_ENTRY_TYPE, def.getId());
				String title = NLT.get(def.getTitle());
				if (toolbar.checkToolbarMenuItem("1_add", "entries", title)) {
					title = title + " (" + String.valueOf(count++) + ")";
				}
				toolbar.addToolbarMenuItem("1_add", "entries", title, adapterUrl.toString(), qualifiers);
			}
		}
    
		//The "Administration" menu
		toolbar.addToolbarMenu("2_administration", NLT.get("toolbar.administration"));
		//Configuration
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_CONFIGURE_FORUM);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.configuration"), url);
		//Definition builder
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.definition_builder"), url);
		
		//	The "Display styles" menu
		toolbar.addToolbarMenu("3_display_styles", NLT.get("toolbar.display_styles"));
		//vertical
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_VERTICAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_vertical"), url);
		//accessible
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_accessible"), url);
		//iframe
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_iframe"), url);
		//popup
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.URL_BINDER_ID, forumId);
		url.setParameter(WebKeys.URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_POPUP);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_popup"), url);
		return toolbar;
	}
	/* 
	 * getEvents ripples through all the entries in the current entry list, finds their
	 * associated events, checks each event against the session's current calendar view mode
	 * and current selected date, and populates the bean with a list of dates that fall in range.
	 * Returns: side-effects the bean "model" and adds a key called CALENDAR_EVENTDATES which is a
	 * hashMap whose keys are dates and whose values are lists of events that occur on the given day.
	 */
	protected void getEvents(Folder folder, ArrayList entrylist, Map model, RenderRequest req, RenderResponse response) {
		Iterator entryIterator = entrylist.listIterator();
		PortletSession ps = WebHelper.getRequiredPortletSession(req);
		// view mode is one of day, week, or month
		String viewMode = (String) ps.getAttribute(WebKeys.CALENDAR_VIEWMODE);
		if (viewMode == null) {
			ps.setAttribute(WebKeys.CALENDAR_VIEWMODE, WebKeys.CALENDAR_VIEW_WEEK);		
			viewMode = WebKeys.CALENDAR_VIEW_WEEK;
		}
		String folderId = folder.getId().toString();
		model.put(WebKeys.CALENDAR_VIEWMODE, viewMode);
		// currentDate is the date selected by the user; we make sure this date is in view 
		// whatever viewMode is set to
		Date currentDate = (Date) ps.getAttribute(WebKeys.CALENDAR_CURRENT_DATE);
		if (currentDate == null) {
			ps.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, new Date());	
			currentDate = new Date();
		} 
		model.put(WebKeys.CALENDAR_CURRENT_DATE, currentDate);
		// urls for common calendar links
		PortletURL url;

		// calendar navigation via nav bar; must be an action so form data is transmitted
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, folderId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_CALENDAR_GOTO_DATE);
		model.put("goto_form_url", url.toString());
		
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, folderId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_MODE);
		url.setParameter(WebKeys.URL_VALUE, WebKeys.CALENDAR_VIEW_DAY);
		model.put("set_day_view", url.toString());

		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, folderId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_MODE);
		url.setParameter(WebKeys.URL_VALUE, WebKeys.CALENDAR_VIEW_WEEK);
		model.put("set_week_view", url.toString());
		
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
		url.setParameter(WebKeys.URL_BINDER_ID, folderId);
		url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_MODE);
		url.setParameter(WebKeys.URL_VALUE, WebKeys.CALENDAR_VIEW_MONTH);
		model.put("set_month_view", url.toString());
		
		// calculate the start and end of the range as defined by current date and current view
		GregorianCalendar startViewCal = new GregorianCalendar();
		// this trick zeros the low order parts of the time
		startViewCal.setTimeInMillis(0);
		startViewCal.setTime(currentDate);
		GregorianCalendar endViewCal = new GregorianCalendar();
		endViewCal.setTimeInMillis(0);
		endViewCal.setTime(currentDate);
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_DAY)) {
			endViewCal.add(Calendar.DATE, 1);
		} else if (viewMode.equals(WebKeys.CALENDAR_VIEW_WEEK)) {
			startViewCal.set(Calendar.DAY_OF_WEEK, startViewCal.getFirstDayOfWeek());
			endViewCal.setTime(startViewCal.getTime());
			endViewCal.add(Calendar.DATE, 7);
		} else if (viewMode.equals(WebKeys.CALENDAR_VIEW_MONTH)) {
			startViewCal.set(Calendar.DAY_OF_MONTH, 1);
			endViewCal.setTime(startViewCal.getTime());
			endViewCal.add(Calendar.MONTH, 1);
		}
		startViewCal.set(Calendar.HOUR_OF_DAY, 0);
		startViewCal.set(Calendar.MINUTE, 0);
		startViewCal.set(Calendar.SECOND, 0);
		endViewCal.set(Calendar.HOUR_OF_DAY, 0);
		endViewCal.set(Calendar.MINUTE, 0);
		endViewCal.set(Calendar.SECOND, 0);
		model.put(WebKeys.CALENDAR_CURRENT_VIEW_STARTDATE, startViewCal.getTime());
		model.put(WebKeys.CALENDAR_CURRENT_VIEW_ENDDATE, endViewCal.getTime());
		// these two longs will be used to determine if an event is in range
		long startMillis = startViewCal.getTime().getTime();
		long endMillis = endViewCal.getTime().getTime();
		
		HashMap results = new HashMap();  
		while (entryIterator.hasNext()) {
			int count = 0;
			HashMap e = (HashMap) entryIterator.next();
			//Entry e = (Entry) entryIterator.next();
			String ec = (String)e.get(EntryIndexUtils.EVENT_COUNT_FIELD);
			if (ec != null)
				count = new Integer(ec).intValue();
			// look through the custom attrs of this entry for any of type EVENT
			for (int j = 0; j < count; j++) {
				String name = (String)e.get(EntryIndexUtils.EVENT_FIELD + j);
				Date evStartDate = (Date)e.get(EntryIndexUtils.EVENT_FIELD + BasicIndexUtils.DELIMITER + name + BasicIndexUtils.DELIMITER + EntryIndexUtils.EVENT_FIELD_START_DATE);
				Date evEndDate = (Date)e.get(EntryIndexUtils.EVENT_FIELD + BasicIndexUtils.DELIMITER + name + BasicIndexUtils.DELIMITER + EntryIndexUtils.EVENT_FIELD_END_DATE);
				Event ev = new Event();
				GregorianCalendar gcal = new GregorianCalendar();
				gcal.setTime(evStartDate);
				ev.setDtStart(gcal);
				gcal.setTime(evEndDate);
				ev.setDtEnd(gcal);				
				long thisDateMillis = evStartDate.getTime();
				if (thisDateMillis < endMillis && startMillis < thisDateMillis) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					String dateKey = sdf.format(evStartDate);
					ArrayList entryList = new ArrayList();
					// reslist is going to be a list of maps; each map will carry the entry and 
					// also the event that caused this entry to be in range
					ArrayList resList = new ArrayList();
					Map res = new HashMap();
					res.put("event", ev);
					res.put("entry", e);
					entryList  = (ArrayList) results.get(dateKey);
					if (entryList == null) {
						resList.add(res);
					} else {
						resList.addAll(entryList);
						resList.add(res);
					}
					results.put(dateKey, resList);
				}
			}
		}
		model.put(WebKeys.CALENDAR_EVENTDATES, results);
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_WEEK)) {
			getCalendarViewBean(startViewCal, endViewCal, response, results, viewMode, model);
		}
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_DAY)) {
			
			getCalendarViewBean(startViewCal, endViewCal, response, results, viewMode, model);
		}
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_MONTH)) {
			
			getCalendarViewBean(startViewCal, endViewCal, response, results, viewMode, model);
		}
	}
	
	/**
	 * populate the bean for weekly and monthly calendar view.
	 * used by getEvents
	 * returns a bean for the entire month, regardless of which view you are in
	 * this bean contains month headers, and a list of weeks. Each week is a map which
	 * contains the week number and a list of days.
	 * Each entry in the dayss list is a daymap, 
	 * a map with info about the day, such as the day of the week, day of the month, and a boolean 
	 * indicating whether the day is today. The daymap also contains a sorted map of event info,
	 * called eventdatamap, whose keys are the event times in millis; this is so that the interator
	 * will return the day's events in chronological order. Each key-value is a list of events 
	 * at that starting time (since you can have multiple events that start at the same time.
	 * and each list entry is a dataMap, which contains both event and entry information for the event 
	 * suitable for displaying on the view calendar page.
	 * 
	 * So the picture looks like this:
	 *  monthBean -- map 
	 *   dayHeaders -- list of day header strings for the month grid
	 *   weekList -- list of week beans
	 *     weekMap -- map
	 *       weekNum -- string
	 *       weekURL -- link to that week
	 *       dayList -- list of days
	 *         dayMap -- map for each day of the week
	 *          day-of-wee  - string
	 *          day-of-month - string
	 *          isToday - Boolean
	 *          dayEvents -- sorted map of event occurrences for the day, keyed by start time
	 *            timeEvents -- list of event occurrences for a specific time
	 *              dataMap -- for each occurrence, a map of stuff about the instance
	 *                 entry
	 *                 event
	 *                 starttime -- string
	 *                 endtime -- string
	 *              
	 */
	private void getCalendarViewBean (Calendar startCal, Calendar endCal, RenderResponse response, Map eventDates, String viewMode, Map model) {
		HashMap monthBean = new HashMap();
		ArrayList dayheaders = new ArrayList();
		GregorianCalendar loopCal = new GregorianCalendar();
		int j = loopCal.getFirstDayOfWeek();
		for (int i=0; i< 7; i++) {
			dayheaders.add(DateHelper.getDayAbbrevString(j));
			// we don't know for sure that the d-o-w won't wrap, so prepare to wrap it
			if (j++ == 7) {
				j = 0;
			}
		}
		monthBean.put("dayHeaders",dayheaders);
		loopCal.setTime(startCal.getTime());
		List weekList = new ArrayList();
		
		HashMap weekMap = null;
		ArrayList dayList = null;
		// this trick enables the main loop code to start a new week and reset/wrap dayCtr at same time
		int dayCtr = 6;
		// build string for date to stick in url -- note that it cannot contain "/"s so we use "_"
		SimpleDateFormat urldatesdf = new SimpleDateFormat("yyyy_MM_dd");
		String urldatestring;
		String urldatestring2;
		PortletURL url;
		// main loop, loops through days in the range, periodically recycling the week stuff
		while (loopCal.getTime().getTime() < endCal.getTime().getTime()) {
			urldatestring = urldatesdf.format(loopCal.getTime());
			if (++dayCtr > 6) {
				dayCtr = 0;
				// before starting a new week, write out the old one (except first time through)
				if (weekMap != null) {
					weekMap.put("dayList", dayList);
					weekList.add(weekMap);
				}
				weekMap = new HashMap();
				// "w" is format pattern for week number in the year
				SimpleDateFormat sdfweeknum = new SimpleDateFormat("w");
				String wn = sdfweeknum.format(loopCal.getTime());
				weekMap.put("weekNum", wn);

				// before starting a new dayList, check if this is the first week of a month view
				if (dayList == null && viewMode.equals(WebKeys.CALENDAR_VIEW_MONTH)) {
					dayList = new ArrayList();
					GregorianCalendar gcal = new GregorianCalendar();
					gcal.setTime(startCal.getTime());
					// when does the week that includes the first day of the month begin?
					gcal.set(Calendar.DAY_OF_WEEK, gcal.getFirstDayOfWeek());
					// note that the week url must include this date instead of the startCal date
					urldatestring = urldatesdf.format(gcal.getTime());
					while (gcal.getTime().getTime() < startCal.getTime().getTime()) {
						// fill in the dayList with blank days
						HashMap emptyDayMap = new HashMap();
						emptyDayMap.put(WebKeys.CALENDAR_DOM, Integer.toString(gcal.get(Calendar.DAY_OF_MONTH)));
						emptyDayMap.put("inView", new Boolean(false));
						// because this loop is adding extra days, we need to build their URLs into the daymap here
						// and we don't want to clobber urldatestring because it's used later for the week URL
						urldatestring2 = urldatesdf.format(gcal.getTime());
						url = response.createRenderURL();
						url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
						url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_DATE);
						url.setParameter(WebKeys.CALENDAR_URL_VIEWMODE, "day");
						url.setParameter(WebKeys.CALENDAR_URL_NEWVIEWDATE, urldatestring2);
						emptyDayMap.put("dayURL", url.toString());
						dayCtr++;
						dayList.add(emptyDayMap);
						gcal.add(Calendar.DATE, 1);
					}
				} else {
					dayList = new ArrayList();
				}
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_DATE);
				url.setParameter(WebKeys.CALENDAR_URL_VIEWMODE, "week");
				url.setParameter(WebKeys.CALENDAR_URL_NEWVIEWDATE, urldatestring);
				weekMap.put("weekURL", url.toString());
			}
			HashMap daymap = new HashMap();
			daymap.put(WebKeys.CALENDAR_DOW, DateHelper.getDayAbbrevString(loopCal.get(Calendar.DAY_OF_WEEK)));
			daymap.put(WebKeys.CALENDAR_DOM, Integer.toString(loopCal.get(Calendar.DAY_OF_MONTH)));
			url = response.createRenderURL();
			url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
			url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_DATE);
			url.setParameter(WebKeys.CALENDAR_URL_VIEWMODE, "day");
			url.setParameter(WebKeys.CALENDAR_URL_NEWVIEWDATE, urldatestring);
			daymap.put("dayURL", url.toString());

			
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String dateKey = sdf.format(loopCal.getTime());
			// is this loop date today? We need to beanify that fact so that the calendar view can shade it
			GregorianCalendar today = new GregorianCalendar();
			if (sdf.format(today.getTime()).equals(dateKey)) {
				daymap.put("isToday", new Boolean(true));
			} else {
				daymap.put("isToday", new Boolean(false));
			}
			daymap.put("inView", new Boolean(true));
			if (eventDates.containsKey(dateKey)) {
				List evList = (List) eventDates.get(dateKey);
				Iterator evIt = evList.iterator();
				TreeMap dayEvents = new TreeMap();
				while (evIt.hasNext()) {
					// thisMap is the next entry, event pair
					HashMap thisMap = (HashMap) evIt.next();
					// dataMap is the map of data for the bean, to be keyed by the time
					HashMap dataMap = new HashMap();
					HashMap e = (HashMap) thisMap.get("entry");
					Event ev = (Event) thisMap.get("event");
					SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
					// we build up the dataMap for this instance
					dataMap.put("entry", e);
					dataMap.put("entry_tostring", e.get(BasicIndexUtils.UID_FIELD).toString());
					dataMap.put(WebKeys.CALENDAR_STARTTIMESTRING, sdf2.format(ev.getDtStart().getTime()));
					dataMap.put(WebKeys.CALENDAR_ENDTIMESTRING, sdf2.format(ev.getDtEnd().getTime()));
					
					// dayEvents is sorted by time in millis; must make a Long object though
					Long millis = new Long(ev.getDtStart().getTime().getTime());
					// must see if this key already has stuff; 
					// build a list of all dataMaps that occur at the same time on this particular day
					ArrayList thisTime = (ArrayList) dayEvents.get(millis);
					ArrayList resList = new ArrayList();
					if (thisTime == null) {
						resList.add(dataMap);
					} else {
						resList.addAll(thisTime);
						resList.add(dataMap);
					}
					dayEvents.put(millis, resList);
				}
				daymap.put(WebKeys.CALENDAR_EVENTDATAMAP, dayEvents);
			}
			
			loopCal.add(Calendar.DATE, 1);
			dayList.add(daymap);
		}
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_MONTH)) {
			// note that the week url must include this date instead of the startCal date
			urldatestring = urldatesdf.format(loopCal.getTime());
			while (dayCtr++ < 6) {
				// fill in the dayList with blank days
				HashMap emptyDayMap = new HashMap();
				emptyDayMap.put(WebKeys.CALENDAR_DOM, Integer.toString(loopCal.get(Calendar.DAY_OF_MONTH)));
				emptyDayMap.put("inView", new Boolean(false));
				// because this loop is adding extra days, we need to build their URLs into the daymap here
				// and we don't want to clobber urldatestring because it's used later for the week URL
				urldatestring2 = urldatesdf.format(loopCal.getTime());
				url = response.createRenderURL();
				url.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
				url.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_DATE);
				url.setParameter(WebKeys.CALENDAR_URL_VIEWMODE, "day");
				url.setParameter(WebKeys.CALENDAR_URL_NEWVIEWDATE, urldatestring2);
				emptyDayMap.put("dayURL", url.toString());
				dayList.add(emptyDayMap);
				loopCal.add(Calendar.DATE, 1);
			}
		}

		
		weekMap.put("dayList", dayList);
		weekList.add(weekMap);
		monthBean.put("weekList", weekList);
		model.put(WebKeys.CALENDAR_VIEWBEAN, monthBean);
	}

	protected class TreeBuilder implements DomTreeBuilder {
		
		public Element setupDomElement(String type, Object source, Element element) {
	
			if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				element.addAttribute("type", "forum");
				element.addAttribute("title", f.getTitle());
				element.addAttribute("id", f.getId().toString());
				element.addAttribute("image", "forum");
				Element url = element.addElement("url");
				url.addAttribute(WebKeys.ACTION, WebKeys.ACTION_VIEW_LISTING);
				url.addAttribute(WebKeys.URL_BINDER_ID, f.getId().toString());
			} else return null;
			return element;
		}
	}
}
