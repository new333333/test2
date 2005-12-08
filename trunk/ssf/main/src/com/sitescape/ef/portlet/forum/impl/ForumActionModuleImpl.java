
package com.sitescape.ef.portlet.forum.impl;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.portlet.PortletURL;
import javax.portlet.PortletSession;
import javax.portlet.RenderResponse;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.HistoryMap;
import com.sitescape.ef.domain.NoDefinitionByTheIdException;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.UserPerFolderPK;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.portlet.forum.HistoryCache;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.impl.AbstractModuleImpl;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.shared.DomTreeBuilder;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.util.PortletRequestUtils;
import com.sitescape.ef.portlet.forum.ActionUtil;
import com.sitescape.ef.portlet.forum.ForumActionModule;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.util.DateHelper;
import com.sitescape.ef.util.Toolbar;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.WebHelper;
import com.sitescape.ef.domain.DefinitionInvalidException;
import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.portlet.bind.PortletRequestBindingException;

import org.dom4j.Document;
import org.dom4j.Element;

/**
 * @author Janet McCann
 *
 */
public class ForumActionModuleImpl extends AbstractModuleImpl implements ForumActionModule,DomTreeBuilder {

	protected WorkspaceModule workspaceModule;;
	protected ProfileModule profileModule;
	protected AdminModule adminModule;
	protected FolderModule folderModule;
	protected DefinitionModule definitionModule;
	protected MailModule mailModule;
	    
	/**
	 * @param adminModule The adminModule to set.
	 */
	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}
	/**	
	 * @param definitionModule The definitionModule to set.
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}

	/**
	 * @param folderModule The folderModule to set.
	 */
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}

	/**
	 * @param mailModule The mailModule to set.
	 */
	public void setMailModule(MailModule mailModule) {
		this.mailModule = mailModule;
	}
	/**
	 * @param profileModule The profileModule to set.
	 */
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
		
	/**
	 * @return Returns the adminModule.
	 */
	protected AdminModule getAdminModule() {
		return adminModule;
	}
	/**
	 * @return Returns the definitionModule.
	 */
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}

	/**
	 * @return Returns the folderModule.
	 */
	protected FolderModule getFolderModule() {
		return folderModule;
	}
	/**
	 * @return Returns the mailModule.
	 */
	protected MailModule getMailModule() {
		return mailModule;
	}
	/**
	 * @return Returns the profileModule.
	 */
	protected ProfileModule getProfileModule() {
		return profileModule;
	}

	/**
	 * @return Returns the workspaceModule.
	 */
	protected WorkspaceModule getWorkspaceModule() {
		return workspaceModule;
	}
	/**
	 * @param workspaceModule The workspaceModule to set.
	 */
	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}
	
	private Definition getDefinition(String id) {
		String companyId = RequestContextHolder.getRequestContext().getZoneName();
 		return getCoreDao().loadDefinition(id, companyId);
	}
	
	protected void getDefinitions(Folder folder, Map model) {
		List folderViewDefs = folder.getForumViewDefs();
		if (!folderViewDefs.isEmpty()) {
			Definition defaultForumDefinition = (Definition)folderViewDefs.get(0);
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION, defaultForumDefinition);
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION_ID, defaultForumDefinition.getId());
			Document forumViewDoc = defaultForumDefinition.getDefinition();
			if (forumViewDoc != null) {
				Element forumViewElement ;
				forumViewElement = forumViewDoc.getRootElement();
				forumViewElement = (Element) forumViewElement.selectSingleNode("//item[@name='forumView']");
				model.put(WebKeys.CONFIG_ELEMENT, forumViewElement);
			} else {
				model.put(WebKeys.CONFIG_ELEMENT, null);
			}
			
		} else {
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION, null);
			model.put(WebKeys.DEFAULT_FOLDER_DEFINITION_ID, "");
			model.put(WebKeys.CONFIG_ELEMENT, null);
		
		}
		Map defaultEntryDefinitions = ActionUtil.getEntryDefsAsMap(folder);
		model.put(WebKeys.ENTRY_DEFINTION_MAP, defaultEntryDefinitions);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.CONFIG_DEFINITION, getDefinitionModule().getDefinitionConfig());
	}
	public void getDefinitions(Map model) {
		List defs = getDefinitionModule().getDefinitions();
		model.put(WebKeys.PUBLIC_DEFINITIONS, defs);
		Iterator itPublicDefinitions = defs.listIterator();
		Map publicEntryDefinitions = new HashMap();
		Map publicForumDefinitions = new HashMap();
		while (itPublicDefinitions.hasNext()) {
			Definition def = (Definition) itPublicDefinitions.next();
			if (def.getType() == Definition.COMMAND) {
				publicEntryDefinitions.put(def.getId(), def);
			} else if (def.getType() == Definition.FORUM_VIEW) {
				publicForumDefinitions.put(def.getId(), def);
			}
		}
		model.put(WebKeys.PUBLIC_ENTRY_DEFINITIONS, publicEntryDefinitions);
		model.put(WebKeys.PUBLIC_FOLDER_DEFINITIONS, publicForumDefinitions);

	}
	
	/* 
	 * getEvents ripples through all the entries in the current entry list, finds their
	 * associated events, checks each event against the session's current calendar view mode
	 * and current selected date, and populates the bean with a list of dates that fall in range.
	 * Returns: side-effects the bean "model" and adds a key called CALENDAR_EVENTDATES which is a
	 * hashMap whose keys are dates and whose values are lists of events that occur on the given day.
	 */
	public void getEvents(ArrayList entrylist, Map model, RenderRequest req, RenderResponse response) {
		Iterator entryIterator = entrylist.listIterator();
		PortletSession ps = WebHelper.getRequiredPortletSession(req);
		// view mode is one of day, week, or month
		String viewMode = (String) ps.getAttribute(WebKeys.CALENDAR_VIEWMODE);
		if (viewMode == null) {
			ps.setAttribute(WebKeys.CALENDAR_VIEWMODE, WebKeys.CALENDAR_VIEW_WEEK);		
			viewMode = WebKeys.CALENDAR_VIEW_WEEK;
		}
		model.put(WebKeys.CALENDAR_VIEWMODE, viewMode);
		// currentDate is the date selected by the user; we make sure this date is in view 
		// whatever viewMode is set to
		Date currentDate = (Date) ps.getAttribute(WebKeys.CALENDAR_CURRENT_DATE);
		if (currentDate == null) {
			ps.setAttribute(WebKeys.CALENDAR_CURRENT_DATE, new Date());	
			currentDate = new Date();
		} 
		// urls for common calendar links
		PortletURL url;
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_MODE);
		url.setParameter(WebKeys.FORUM_URL_VALUE, WebKeys.CALENDAR_VIEW_DAY);
		model.put("set_day_view", url.toString());

		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_MODE);
		url.setParameter(WebKeys.FORUM_URL_VALUE, WebKeys.CALENDAR_VIEW_WEEK);
		model.put("set_week_view", url.toString());
		
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_CALENDAR_DISPLAY_MODE);
		url.setParameter(WebKeys.FORUM_URL_VALUE, WebKeys.CALENDAR_VIEW_MONTH);
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
			endViewCal.roll(Calendar.DATE, 1);
		} else if (viewMode.equals(WebKeys.CALENDAR_VIEW_WEEK)) {
			startViewCal.set(Calendar.DAY_OF_WEEK, startViewCal.getFirstDayOfWeek());
			endViewCal.setTime(startViewCal.getTime());
			endViewCal.roll(Calendar.DATE, 7);
		} else if (viewMode.equals(WebKeys.CALENDAR_VIEW_MONTH)) {
			startViewCal.set(Calendar.DAY_OF_MONTH, 1);
			endViewCal.setTime(startViewCal.getTime());
			endViewCal.roll(Calendar.MONTH, 1);
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
			Entry e = (Entry) entryIterator.next();
			Map customAttrs = e.getCustomAttributes();
			Set keyset = customAttrs.keySet();
			Iterator attIt = keyset.iterator();
			// look through the custom attrs of this entry for any of type EVENT
			while (attIt.hasNext()) {
				CustomAttribute att = (CustomAttribute) customAttrs.get(attIt.next());
				if (att.getValueType() == CustomAttribute.EVENT) {
					Event ev = (Event) att.getValue();
					// range check to see if this event is in range
					long thisDateMillis = ev.getDtStart().getTime().getTime();
					if (thisDateMillis < endMillis && startMillis < thisDateMillis) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						String dateKey = sdf.format(ev.getDtStart().getTime());
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
		}
		model.put(WebKeys.CALENDAR_EVENTDATES, results);
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_WEEK)) {
			getWeekBean(startViewCal, endViewCal, results, model);
		}
		if (viewMode.equals(WebKeys.CALENDAR_VIEW_DAY)) {
			
			getWeekBean(startViewCal, endViewCal, results, model);
		}
	}
	
	/**
	 * populate the bean for weekly calendar view.
	 * used by getEvents
	 * returns a list of length 7, one for each day of the week. Each entry in the list is a daymap, 
	 * a map with info about the day, such as the day of the week, day of the month, and a boolean 
	 * indicating whether the day is today. The daymap also contains a sorted map of event info,
	 * called eventdatamap, whose keys are the event times in millis; this is so that the interator
	 * will return the day's events in chronological order. Each key-value is a list of events 
	 * at that starting time (since you can have multiple events that start at the same time.
	 * and each list entry is a dataMap, which contains both event and entry information for the event 
	 * suitable for displaying on the view calendar page.
	 * 
	 * So the picture looks like this:
	 *   weekBean -- list for the whole week
	 *     dayMap -- map for each day of the week
	 *        day-of-wee  - string
	 *        day-of-month - string
	 *        isToday - Boolean
	 *        dayEvents -- sorted map of event occurrences for the day, keyed by start time
	 *           timeEvents -- list of event occurrences for a specific time
	 *              dataMap -- for each occurrence, a map of stuff about the instance
	 *                 e -- entry
	 *                 ev -- event
	 *                 starttime -- string
	 *                 endtime -- string
	 *              
	 */
	private void getWeekBean (Calendar startCal, Calendar endCal, Map eventDates, Map model) {
		List weekBean = new ArrayList();
		GregorianCalendar loopCal = new GregorianCalendar();
		loopCal.setTime(startCal.getTime());
		int i;
		for (i=0; i<7; i++) {
			HashMap daymap = new HashMap();
			daymap.put(WebKeys.CALENDAR_DOW, DateHelper.getDayAbbrevString(loopCal.get(Calendar.DAY_OF_WEEK)));
			daymap.put(WebKeys.CALENDAR_DOM, Integer.toString(loopCal.get(Calendar.DAY_OF_MONTH)));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String dateKey = sdf.format(loopCal.getTime());
			// is this loop date today? We need to beanify that fact so that the calendar view can shade it
			GregorianCalendar today = new GregorianCalendar();
			if (sdf.format(today.getTime()).equals(dateKey)) {
				daymap.put("isToday", new Boolean(true));
			} else {
				daymap.put("isToday", new Boolean(false));
			}
			if (eventDates.containsKey(dateKey)) {
				List evList = (List) eventDates.get(dateKey);
				Iterator evIt = evList.iterator();
				TreeMap dayEvents = new TreeMap();
				while (evIt.hasNext()) {
					// thisMap is the next entry, event pair
					HashMap thisMap = (HashMap) evIt.next();
					// dataMap is the map of data for the bean, to be keyed by the time
					HashMap dataMap = new HashMap();
					Entry e = (Entry) thisMap.get("entry");
					Event ev = (Event) thisMap.get("event");
					SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
					// we build up the dataMap for this instance
					dataMap.put("entry", e);
					dataMap.put("entry_tostring", e.getId().toString());
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
			
			loopCal.roll(Calendar.DATE, true);
			weekBean.add(daymap);
		}
		model.put(WebKeys.CALENDAR_VIEWBEAN, weekBean);
	}
	
	
	/**
	 * Fill in the model values for a definition.  Return false if definition isn't
	 * complete, true otherwise
	 * @param currentDef
	 * @param model
	 * @param node
	 * @return
	 */
	private boolean getDefinition(Definition currentDef, Map model, String node) {
		model.put(WebKeys.ENTRY_DEFINITION, currentDef);
		model.put(WebKeys.CONFIG_DEFINITION, getDefinitionModule().getDefinitionConfig());
		if (currentDef == null) {
			model.put(WebKeys.CONFIG_ELEMENT, null);
			return false;
		}
		Document configDoc = currentDef.getDefinition();
		if (configDoc == null) { 
			model.put(WebKeys.CONFIG_ELEMENT, null);
			return false;
		} else {
			Element configRoot = configDoc.getRootElement();
			if (configRoot == null) {
				model.put(WebKeys.CONFIG_ELEMENT, null);
				return false;
			} else {
				Element configEle = (Element) configRoot.selectSingleNode(node);
				model.put(WebKeys.CONFIG_ELEMENT, configEle);
				if (configEle == null) return false;
			}
		}
		return true;
		
	}
	//Routine to build a definition file on the fly for viewing entries with no definition
	private void getDefaultEntryView(Map model) {
		//Create an empty entry definition
		Map formData = new HashMap();
		Document def = getDefinitionModule().getDefaultDefinition("ss_default_entry_view","__definition_default_entry_view", Definition.COMMAND, formData);
		
		//Add the "default viewer" item
		Element entryView = (Element) def.getRootElement().selectSingleNode("//item[@name='entryView']");
		if (entryView != null) {
			String itemId = entryView.attributeValue("id", "");
			try {
				Element newItem = getDefinitionModule().addItemToDefinitionDocument("default", def, itemId, "defaultEntryView", formData);
			}
			catch (DefinitionInvalidException e) {
				//An error occurred while processing the operation; pass the error message back to the jsp
				//SessionErrors.add(req, e.getClass().getName(),e.getMessage());
			}
		}
		model.put(WebKeys.CONFIG_ELEMENT, entryView);
	}
	private HistoryMap getHistory(RenderRequest req, Long folderId) {
		HistoryCache cache = (HistoryCache)req.getAttribute(WebKeys.HISTORY_CACHE);
		return getHistory(cache, folderId);
	}
	private HistoryMap getHistory(HttpServletRequest req, Long folderId) {
		HistoryCache cache = (HistoryCache)req.getAttribute(WebKeys.HISTORY_CACHE);
		return getHistory(cache, folderId);
	}
	private HistoryMap getHistory(HistoryCache cache, Long folderId) {
		HistoryMap history;
		//check if cached first
		if (cache == null) {
			history = getProfileModule().getUserHistory(null, folderId);
		} else {
			UserPerFolderPK key = new UserPerFolderPK(RequestContextHolder.getRequestContext().getUser().getId(), folderId);
			if (!key.equals(cache.getId())) {
				history = getProfileModule().getUserHistory(null, folderId);
			} else {
				history = cache.getHistory();
			}
		}
		return history; 
	}
	protected void buildEntryToolbar(RenderResponse response, Map model, String folderId, String entryId) {
	   	User user = RequestContextHolder.getRequestContext().getUser();
		Element entryViewElement = (Element)model.get(WebKeys.CONFIG_ELEMENT);
		Document entryView = entryViewElement.getDocument();
		Definition def = (Definition)model.get(WebKeys.ENTRY_DEFINITION);
		String entryDefId="";
		if (def != null)
			entryDefId= def.getId().toString();
	    //Build the toolbar array
		Toolbar toolbar = new Toolbar();
	    //The "Reply" menu
		List replyStyles = entryView.getRootElement().selectNodes("properties/property[@name='replyStyle']");
		PortletURL url;
		if (!replyStyles.isEmpty()) {
			if (replyStyles.size() == 1) {
				//There is only one reply style, so show it not as a drop down menu
				String replyStyleId = ((Element)replyStyles.get(0)).attributeValue("value", "");
				if (!replyStyleId.equals("")) {
					Map params = new HashMap();
					params.put(WebKeys.ACTION, WebKeys.FORUM_ACTION_ADD_REPLY);
					params.put(WebKeys.FORUM_URL_FORUM_ID, folderId);
					params.put(WebKeys.FORUM_URL_ENTRY_TYPE, replyStyleId);
					params.put(WebKeys.FORUM_URL_ENTRY_ID, entryId);
					Map qualifiers = new HashMap();
					qualifiers.put("popup", new Boolean(true));
					toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"), params, qualifiers);
				}
			} else {
				toolbar.addToolbarMenu("1_reply", NLT.get("toolbar.reply"));
				for (int i = 0; i < replyStyles.size(); i++) {
					String replyStyleId = ((Element)replyStyles.get(i)).attributeValue("value", "");
			        try {
			        	Definition replyDef = getCoreDao().loadDefinition(replyStyleId, user.getZoneName());
						url = response.createActionURL();
						url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_ADD_REPLY);
						url.setParameter(WebKeys.FORUM_URL_FORUM_ID, folderId);
						url.setParameter(WebKeys.FORUM_URL_ENTRY_TYPE, replyStyleId);
						url.setParameter(WebKeys.FORUM_URL_ENTRY_ID, entryId);
						toolbar.addToolbarMenuItem("1_reply", "replies", replyDef.getTitle(), url);
			        } catch (NoDefinitionByTheIdException e) {
			        	continue;
			        }
				}
			}
		}
	    
	    //The "Modify" menu
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_MODIFY_ENTRY);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, folderId);
		url.setParameter(WebKeys.FORUM_URL_ENTRY_TYPE, entryDefId);
		url.setParameter(WebKeys.FORUM_URL_ENTRY_ID, entryId);
		toolbar.addToolbarMenu("2_modify", NLT.get("toolbar.modify"), url);
		
	    
	    //The "Delete" menu
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_DELETE_ENTRY);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, folderId);
		url.setParameter(WebKeys.FORUM_URL_ENTRY_TYPE, entryDefId);
		url.setParameter(WebKeys.FORUM_URL_ENTRY_ID, entryId); 
		toolbar.addToolbarMenu("3_delete", NLT.get("toolbar.delete"), url);
	    
		model.put(WebKeys.FOLDER_ENTRY_TOOLBAR, toolbar.getToolbar());
		
	}
	protected void buildFolderToolbar(RenderResponse response, Map model, String folderId) {
		//Build the toolbar array
		Toolbar toolbar = new Toolbar();
		String forumId = folderId.toString();
		//	The "Add" menu
		Folder folder = (Folder)model.get(WebKeys.FOLDER);
		List defaultEntryDefinitions = folder.getEntryDefs();
		PortletURL url;
		if (!defaultEntryDefinitions.isEmpty()) {
			toolbar.addToolbarMenu("1_add", NLT.get("toolbar.add"));
			for (int i=0; i<defaultEntryDefinitions.size(); ++i) {
				Definition def = (Definition) defaultEntryDefinitions.get(i);
				url = response.createActionURL();
				url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_ADD_ENTRY);
				url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
				url.setParameter(WebKeys.FORUM_URL_ENTRY_TYPE, def.getId());
				toolbar.addToolbarMenuItem("1_add", "entries", def.getTitle(), url);
			}
		}
    
		//The "Administration" menu
		toolbar.addToolbarMenu("2_administration", NLT.get("toolbar.administration"));
		//Configuration
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_CONFIGURE_FORUM);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.configuration"), url);
		//Definition builder
		url = response.createActionURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_DEFINITION_BUILDER);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		toolbar.addToolbarMenuItem("2_administration", "", NLT.get("toolbar.menu.definition_builder"), url);
		
		//	The "Display styles" menu
		toolbar.addToolbarMenu("3_display_styles", NLT.get("toolbar.display_styles"));
		/**
		//horizontal
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		url.setParameter(WebKeys.FORUM_URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_HORIZONTAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_horizontal"), url);
		*/
		//vertical
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		url.setParameter(WebKeys.FORUM_URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_VERTICAL);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_vertical"), url);
		//accessible
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		url.setParameter(WebKeys.FORUM_URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_accessible"), url);
		//iframe
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		url.setParameter(WebKeys.FORUM_URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_IFRAME);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_iframe"), url);
		model.put(WebKeys.FOLDER_TOOLBAR, toolbar.getToolbar());
		//popup
		url = response.createRenderURL();
		url.setParameter(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
		url.setParameter(WebKeys.FORUM_URL_OPERATION, WebKeys.FORUM_OPERATION_SET_DISPLAY_STYLE);
		url.setParameter(WebKeys.FORUM_URL_FORUM_ID, forumId);
		url.setParameter(WebKeys.FORUM_URL_VALUE, ObjectKeys.USER_DISPLAY_STYLE_POPUP);
		toolbar.addToolbarMenuItem("3_display_styles", "", NLT.get("toolbar.menu.display_style_popup"), url);
		model.put(WebKeys.FOLDER_TOOLBAR, toolbar.getToolbar());
		
	}
	public Map getDeleteEntry(Map formData, RenderRequest req, Long folderId) throws PortletRequestBindingException {
		Map model = new HashMap();
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(req, WebKeys.FORUM_URL_ENTRY_ID));
		FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
		model.put(WebKeys.FOLDER_ENTRY, entry);
		model.put(WebKeys.FOLDER, entry.getParentFolder());
		return model;
	}
	public Map getModifyEntry(Map formData, RenderRequest req, Long folderId) throws PortletRequestBindingException {
		Map model = new HashMap();
		FolderEntry entry=null;
		Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(req, WebKeys.FORUM_URL_ENTRY_ID));
		entry  = getFolderModule().getEntry(folderId, entryId);
		
		model.put(WebKeys.FOLDER_ENTRY, entry);
		model.put(WebKeys.FOLDER, entry.getParentFolder());
		model.put(WebKeys.CONFIG_JSP_STYLE, "form");
		getDefinition(entry.getEntryDef(), model, "//item[@name='entryForm']");

		return model;
		
	}


	public Map getShowEntry(String entryId, Map formData, RenderRequest req, RenderResponse response, Long folderId)  {
		Map model = new HashMap();
		HistoryMap history = getHistory(req, folderId);
		model.put(WebKeys.HISTORY_MAP, history);
		String op = PortletRequestUtils.getStringParameter(req, WebKeys.FORUM_URL_OPERATION, "");
		Folder folder = null;
		FolderEntry entry = null;
		Map folderEntries = null;
		if (op.equals("")) {
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
		
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY)) {
			if (!entryId.equals("")) {
				folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
			}
	
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_NEXT)) {
			folder = getFolderModule().getFolder(folderId);
			Long currentEntryId = null;
			if (formData.containsKey(WebKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_ENTRY_VIEWED);
			}
			if (formData.containsKey(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED) && 
					(Long)formData.get(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED) != null) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED);
			}
			if (currentEntryId == null) {
				Long nextEntryId = history.getNextHistoryEntry();
				if (nextEntryId != null) {
					entryId = nextEntryId.toString();
				} else {
					entryId = "";
				}
			} else {
				Long nextEntryId = history.getNextHistoryEntry(currentEntryId);
				if (nextEntryId != null) {
					entryId = nextEntryId.toString();
				} else {
					entryId = "";
				}
			}
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
	
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_HISTORY_PREVIOUS)) {
			folder = getFolderModule().getFolder(folderId);
			Long currentEntryId = null;
			if (formData.containsKey(WebKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_ENTRY_VIEWED);
			}
			if (formData.containsKey(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_HISTORY_ENTRY_VIEWED);
			}
			if (currentEntryId != null) {
				Long previousEntryId = history.getPreviousHistoryEntry(currentEntryId);
				if (previousEntryId != null) {
					entryId = previousEntryId.toString();
				} else {
					entryId = "";
				}
			} else {
				entryId = "";
			}
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId));
	
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_NEXT)) {
			Long currentEntryId = null;
			if (formData.containsKey(WebKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_ENTRY_VIEWED);
			}
			if (currentEntryId != null) {
				entryId = currentEntryId.toString();
			}
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId), FolderModule.NEXT_ENTRY);
	
		} else if (op.equals(WebKeys.FORUM_OPERATION_VIEW_ENTRY_PREVIOUS)) {
			Long currentEntryId = null;
			if (formData.containsKey(WebKeys.SESSION_LAST_ENTRY_VIEWED)) {
				currentEntryId = (Long)formData.get(WebKeys.SESSION_LAST_ENTRY_VIEWED);
			}
			if (currentEntryId != null) {
				//entryId = seenMap.getPreviousHistoryEntry(currentEntryId).toString();
				entryId = "";
			}
			if (!entryId.equals("")) folderEntries  = getFolderModule().getEntryTree(folderId, Long.valueOf(entryId), FolderModule.PREVIOUS_ENTRY);
		}
		if (entryId.equals("")) {
			folder = getFolderModule().getFolder(folderId);
		}
		if (folderEntries != null) {
			entry = (FolderEntry)folderEntries.get(ObjectKeys.FOLDER_ENTRY);
			folder = entry.getParentFolder();
			model.put(WebKeys.FOLDER_ENTRY_DESCENDANTS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_DESCENDANTS));
			model.put(WebKeys.FOLDER_ENTRY_ANCESTORS, folderEntries.get(ObjectKeys.FOLDER_ENTRY_ANCESTORS));
		}
		model.put(WebKeys.ENTRY_ID, entryId);
		model.put(WebKeys.SEEN_MAP, getProfileModule().getUserSeenMap(null, folder.getId()));
		model.put(WebKeys.FOLDER_ENTRY, entry);
		model.put(WebKeys.DEFINITION_ENTRY, entry);
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(null).getProperties());
		if (entry == null) {
			getDefinition(null, model, "//item[@name='entryView']");
			return model;
		}
		if (getDefinition(entry.getEntryDef(), model, "//item[@name='entryView']") == false) {
			getDefaultEntryView(model);
		}
		if (!entryId.equals("")) {
			buildEntryToolbar(response, model, folderId.toString(), entryId);
		}
		return model;
	}
	public Map getShowFolder(Map formData, RenderRequest req, RenderResponse response,Long folderId) throws PortletRequestBindingException {
		Map folderEntries;
		Map model = new HashMap();
		String forumId = folderId.toString();
		folderEntries = getFolderModule().getFolderEntries(folderId);
		Folder folder = (Folder)folderEntries.get(ObjectKeys.FOLDER);
	   	User user = RequestContextHolder.getRequestContext().getUser();
		//Build the beans depending on the operation being done
		model.put(WebKeys.FOLDER, folder);
		HistoryMap history = getHistory(req, folderId);
		model.put(WebKeys.HISTORY_MAP, history);
		Folder topFolder = folder.getTopFolder();
		if (topFolder == null) {
			model.put(WebKeys.FOLDER_DOM_TREE, getFolderModule().getDomFolderTree(folderId, this));
		} else {
			model.put(WebKeys.FOLDER_DOM_TREE, getFolderModule().getDomFolderTree(topFolder.getId(), this));			
		}
		model.put(WebKeys.FOLDER_ENTRIES, folderEntries.get(ObjectKeys.FOLDER_ENTRIES));
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()).getProperties());
		model.put(WebKeys.SEEN_MAP,getProfileModule().getUserSeenMap(user.getId(), folder.getId()));
		getDefinitions(folder, model);
		ArrayList entries = (ArrayList) folderEntries.get(ObjectKeys.FOLDER_ENTRIES);
		getEvents(entries, model, req, response);
		req.setAttribute(WebKeys.FORUM_URL_FORUM_ID,forumId);
		buildFolderToolbar(response, model, forumId);
		return model;
	}
	public Map getDefinitionXml(HttpServletRequest req, String currentId) throws PortletRequestBindingException {
		Map model = new HashMap();
		if (!currentId.equals("")) {
			model.put(WebKeys.DEFINITION, getDefinitionModule().getDefinition(currentId));
		}
		return model;
	}
	public Map getDefinitionBuilder(Map formData, RenderRequest req, String currentId) throws PortletRequestBindingException {
		Map model = new HashMap();
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.CONFIG_DEFINITION, getDefinitionModule().getDefinitionConfig());
			
		getDefinitions(model);
		if (!currentId.equals("")) {
			model.put(WebKeys.DEFINITION, getDefinitionModule().getDefinition(currentId));
		}
		return model;
	}
	public Map getConfigureForum(Map formData, RenderRequest req, Long folderId) {
		Map model = new HashMap();
		User user = RequestContextHolder.getRequestContext().getUser();
		Folder folder = getFolderModule().getFolder(folderId);
		
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.CONFIG_JSP_STYLE, "view");
		model.put(WebKeys.USER_PROPERTIES, getProfileModule().getUserProperties(user.getId()));
			
		getDefinitions(model);
		getDefinitions(folder, model);
		return model;
	}
	public Map getAddEntry(Map formData, RenderRequest req, Long folderId) throws PortletRequestBindingException {
		Map model = new HashMap();
		Folder folder = getFolderModule().getFolder(folderId);
		//Adding an entry; get the specific definition
		Map folderEntryDefs = ActionUtil.getEntryDefsAsMap(folder);
		String entryType = PortletRequestUtils.getStringParameter(req, WebKeys.FORUM_URL_ENTRY_TYPE, "");
		model.put(WebKeys.FOLDER, folder);
		model.put(WebKeys.ENTRY_DEFINTION_MAP, folderEntryDefs);
		model.put(WebKeys.CONFIG_JSP_STYLE, "form");
		//Make sure the requested definition is legal
		if (folderEntryDefs.containsKey(entryType)) {
			getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@name='entryForm']");
		} else {
			getDefinition(null, model, "//item[@name='entryForm']");
		}
		return model;
		
	}
    public Map getAddReply(Map formData, RenderRequest req, Long folderId) throws PortletRequestBindingException {
    	Long entryId = new Long(PortletRequestUtils.getRequiredLongParameter(req, WebKeys.FORUM_URL_ENTRY_ID));
    	req.setAttribute(WebKeys.FORUM_URL_ENTRY_ID,entryId.toString());
    	Map model = new HashMap();
    	FolderEntry entry = getFolderModule().getEntry(folderId, entryId);
    	model.put(WebKeys.DEFINITION_ENTRY, entry);
    	Folder folder = entry.getParentFolder();
    	model.put(WebKeys.FOLDER, folder); 
		
    	//Get the legal reply types from the parent entry definition
		Document entryView = null;
		Definition entryDefinition = entry.getEntryDef();
		if (entryDefinition != null) {
			entryView = entryDefinition.getDefinition();
		}
		Iterator replyStyles = null;
		if (entryView != null) {
			//See if there is a reply style for this entry definition
			replyStyles = entryView.getRootElement().selectNodes("properties/property[@name='replyStyle']").iterator();
		}
   	
    	//Adding an entry; get the specific definition
		Map folderEntryDefs = ActionUtil.getEntryDefsAsMap(folder);
    	String entryType = PortletRequestUtils.getStringParameter(req, WebKeys.FORUM_URL_ENTRY_TYPE, "");
    	model.put(WebKeys.ENTRY_DEFINTION_MAP, folderEntryDefs);
    	model.put(WebKeys.CONFIG_JSP_STYLE, "form");
    	
        //Make sure the requested reply definition is legal
    	boolean replyStyleIsGood = false;
    	while (replyStyles.hasNext()) {
    		if (((String)((Element)replyStyles.next()).attributeValue("value", "")).equals(entryType)) {
    			replyStyleIsGood = true;
    			break;
    		}
    	}
    	
		if (replyStyleIsGood) {
			getDefinition(getDefinitionModule().getDefinition(entryType), model, "//item[@name='entryForm']");
		} else {
			getDefinition(null, model, "//item[@name='entryForm']");
		}
    	return model;
    }
    


	public Element setupDomElement(String type, Object source, Element element) {
		if (type.equals(DomTreeBuilder.TYPE_FOLDER)) {
			Folder f = (Folder)source;
			element.addAttribute("type", "forum");
			element.addAttribute("title", f.getTitle());
			element.addAttribute("id", f.getId().toString());
			element.addAttribute("image", "forum");
        	Element url = element.addElement("url");
	    	url.addAttribute(WebKeys.ACTION, WebKeys.FORUM_ACTION_VIEW_FORUM);
	     	url.addAttribute(WebKeys.FORUM_URL_FORUM_ID, f.getId().toString());
		} else return null;
		return element;
	}
}
