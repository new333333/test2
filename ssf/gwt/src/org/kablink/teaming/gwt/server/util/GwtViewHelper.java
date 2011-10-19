/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.server.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderColumn;
import org.kablink.teaming.gwt.client.binderviews.folderdata.FolderRow;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.rpc.shared.FolderColumnsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderDisplayDataRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.ViewType;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.util.ViewInfo;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.util.search.Constants;


/**
 * Helper methods for the GWT binder views.
 *
 * @author drfoster@novell.com
 */
public class GwtViewHelper {
	protected static Log m_logger = LogFactory.getLog(GwtViewHelper.class);

	/*
	 * Dumps the contents of a ViewInfo object.
	 */
	private static void dumpViewInfo(ViewInfo vi) {
		// If debug tracing isn't enabled...
		if (!(m_logger.isDebugEnabled())) {
			// ...bail.
			return;
		}

		// If we weren't given a ViewInfo to dump...
		if (null == vi) {
			// ...trace that fact and bail.
			m_logger.debug("...dumpViewInfo( null ):  No ViewInfo to dump.");
			return;
		}
		
		ViewType vt = vi.getViewType();
		m_logger.debug("...dumpViewInfo( " + vt.name() + " )");
		switch (vt) {
		case BINDER:
			BinderInfo bi = vi.getBinderInfo();
			BinderType bt = bi.getBinderType();
			m_logger.debug(".....dumpViewInfo( BINDER ):  " + bt.name());
			switch (bt) {
			case FOLDER:
				m_logger.debug("........dumpViewInfo( BINDER:FOLDER     ):  " + bi.getFolderType().name());
				break;
				
			case WORKSPACE:
				m_logger.debug("........dumpViewInfo( BINDER:WORKSPACE  ):  " + bi.getWorkspaceType().name());
				break;
			
			case OTHER:
				m_logger.debug("........dumpViewInfo( BINDER:OTHER      )");
				break;
				
			default:
				m_logger.debug(".........dumpViewInfo( BINDER:Not Handled ):  This BinderType is not implemented by the dumper.");
				break;
			}
			
			m_logger.debug("........dumpViewInfo( BINDER:Id         ):  " + bi.getBinderId());
			m_logger.debug("........dumpViewInfo( BINDER:Title      ):  " + bi.getBinderTitle());
			m_logger.debug("........dumpViewInfo( BINDER:EntityType ):  " + bi.getEntityType());
			
			break;
			
		case ADVANCED_SEARCH:
		case OTHER:
			break;
			
		default:
			m_logger.debug("......dumpViewInfo( Not Handled ):  This ViewType is not implemented by the dumper.");
			break;
		}
	}

	/**
	 * Reads the current user's columns for a folder and returns them
	 * as a FolderColumnsRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * @param folderType
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static FolderColumnsRpcResponseData getFolderColumns(AllModulesInjected bs, HttpServletRequest request, Long folderId, FolderType folderType) throws GwtTeamingException {
		try {
			Folder			folder               = ((Folder) bs.getBinderModule().getBinder(folderId));
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderId);
			
			Map    columnNames;
			Map    columnTitles      = null;
			String columnOrderString = null;

			// Are we showing the trash on this folder?
			if (FolderType.TRASH == folderType) {
				// Yes!  The columns in a trash view are not
				// configurable.  Use the default trash columns.
				columnNames = getColumnsLHMFromAS(TrashHelper.trashColumns);
			}
			
			else {
				// No, we aren't showing the trash on this folder!  Are
				// there user defined columns on this folder?
				columnNames = ((Map) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMNS));
				if (null == columnNames) {
					// No!  Are there defaults stored on the binder?
					columnNames = ((Map) folder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMNS));
					if (null == columnNames) {
						// No!  Use the default as setup in
						// folder_column_defaults.jsp.
						String[] defaultCols;
						if (FolderType.FILE == folderType)
						     defaultCols = new String[]{"title", "comments", "size", "download", "html", "state", "author", "date"};
						else defaultCols = new String[]{"number", "title", "comments", "state", "author", "date", "rating"};
						columnNames = getColumnsLHMFromAS(defaultCols);
					}
					
					else {
						// Yes, there are defaults from the binder!
						// Read and names and sort order from there as
						// well.
						columnTitles      = ((Map)    folder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMN_TITLES    ));
						columnOrderString = ((String) folder.getProperty(ObjectKeys.BINDER_PROPERTY_FOLDER_COLUMN_SORT_ORDER));
					}
				}
				
				else {
					// Yes, there are user defined columns on the
					// folder!  Read and names and sort order from
					// there as well.
					columnTitles      = ((Map)    userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMN_TITLES    ));
					columnOrderString = ((String) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_FOLDER_COLUMN_SORT_ORDER));
				}
			}

			// If we don't have any column names...
			if (null == columnTitles) {
				// ...just use an empty map.
				columnTitles = new HashMap();
			}
			
			// If we don't have any column sort order...
			if (!(MiscUtil.hasString(columnOrderString))) {
				// ...define one based on the column names.
				Set<String> keySet = columnNames.keySet();
				boolean firstCol = true;
				StringBuffer sb = new StringBuffer("");
				for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
					if (!firstCol) {
						sb.append("|");
					}
					sb.append(ksIT.next());
					firstCol = false;
				}
				columnOrderString = sb.toString();
			}

			// Finally, generate a List<String> from the raw column
			// order string...
			List<String> columnSortOrder = new ArrayList<String>();
			String[] sortOrder = columnOrderString.split("\\|");
			for (String columnName:  sortOrder) {
				if (MiscUtil.hasString(columnName)) {
					columnSortOrder.add(columnName);
				}
			}
			
			// ...and ensure all the columns are accounted for in it.
			Set<String> keySet = columnNames.keySet();
			for (Iterator<String> ksIT = keySet.iterator(); ksIT.hasNext(); ) {
				String columnName = ksIT.next();
				if (!(columnSortOrder.contains(columnName))) {
					columnSortOrder.add(columnName);
				}
			}

			// If we get here, we've got all the data we need to define
			// the List<FolderColumn> for this folder.  Allocate the
			// list that we can fill from that data.
			List<FolderColumn> fcList = new ArrayList<FolderColumn>();
			for (String columnName:  columnSortOrder) {
				// Is this column to be shown?
				String columnValue = ((String) columnNames.get(columnName));
				if (!(MiscUtil.hasString(columnValue))) {
					// No!  Skip it.
					continue;
				}

				// Is there a custom title for this column?
				String columnTitle = ((String) columnTitles.get(columnName));
				if (!(MiscUtil.hasString(columnTitle))) {
					// No!  Use the default.
					columnTitle = NLT.get("folder.column." + columnName, columnName, true);
				}

				// Add a FolderColumn for this to the list we're
				// going to return.
				fcList.add(new FolderColumn(columnName, columnTitle));
			}

			// Finally, use the data we obtained to create a
			// FolderColumnsRpcResponseData and return that. 
			return new FolderColumnsRpcResponseData(fcList);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}

	/**
	 * Reads the current user's display data for a folder and returns
	 * them as a FolderDisplayDataRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * 
	 * @return
	 */
	public static FolderDisplayDataRpcResponseData getFolderDisplayData(AllModulesInjected bs, HttpServletRequest request, Long folderId) throws GwtTeamingException {
		try {
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userProperties       = bs.getProfileModule().getUserProperties(user.getId());
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), folderId);
			
			// How should the folder be sorted?
			String	sortBy = ((String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_BY));
			boolean sortDescend;
			if (MiscUtil.hasString(sortBy)) {
				String sortDescendS = ((String) userFolderProperties.getProperty(ObjectKeys.SEARCH_SORT_DESCEND));
				sortDescend = (("true").equalsIgnoreCase(sortDescendS));
			}
			else {
				sortBy      = Constants.SORTNUMBER_FIELD;
				sortDescend = true;
			}

			// How many entries per page should the folder display?
			int pageSize;
			try                  {pageSize = Integer.parseInt(MiscUtil.entriesPerPage(userProperties));}
			catch (Exception ex) {pageSize = 25;                                                       }
			
			// Finally, use the data we obtained to create a
			// FolderDisplayDataRpcResponseData and return that. 
			return new FolderDisplayDataRpcResponseData(sortBy, sortDescend, pageSize);
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw GwtServerHelper.getGwtTeamingException(e);
		}
	}
	
	/**
	 * Reads the row data from a folder and returns it as a
	 * FolderRowsRpcResponseData.
	 * 
	 * @param bs
	 * @param request
	 * @param folderId
	 * @param folderColumns
	 * @param start
	 * @param length
	 * 
	 * @return
	 */
	private static Long dummyEntryId = 1L;
	public static FolderRowsRpcResponseData getFolderRows(AllModulesInjected bs, HttpServletRequest request, Long folderId, List<FolderColumn> folderColumns, int start, int length) throws GwtTeamingException {
		// Generate a List<FolderRow> of the rows read.
		List<FolderRow> folderRows = new ArrayList<FolderRow>();
		
//!		...this needs to be implemented...
		for (int i = 0; i < length; i += 1) {
			FolderRow fr = new FolderRow(dummyEntryId++, folderColumns);
			for (FolderColumn fc:  folderColumns) {
				fr.setColumnValue(fc, (folderId + ":" + start + ":" + fc.getColumnName() + ":" + fc.getColumnTitle()));
			}
			folderRows.add(fr);
		}

		// Finally, return the List<FolderRow> wrapped in a
		// FolderRowsRpcResponseData.
		return new FolderRowsRpcResponseData(folderRows);
	}
	
	/*
	 * Returns a LinkedHashMap of the column names from a String[]
	 * of them.
	 */
	@SuppressWarnings("unchecked")
	private static Map getColumnsLHMFromAS(String[] columnNames) {
		Map reply = new LinkedHashMap();
		for (String columnName:  columnNames) {
			reply.put(columnName, columnName);
		}
		return reply;
	}
	
	/*
	 * Returns a Map<String, String> for the query parameters from a
	 * URL.
	 */
	private static Map<String, String> getQueryParameters(String url) {
		URI uri;
		try {
			// Can we parse the URL?
			uri = new URI(url);
		} catch (URISyntaxException e) {
			// No!  Log the error and bail.
			m_logger.error("GwtViewHelper.getQueryParameters( URL Parsing Exception ):  ", e);
			return null;
		}
		
		// Allocate a Map<String, String> to return the parameters.
		Map<String, String> reply = new HashMap<String, String>();

		// Does the URL contain a query?
		String query = uri.getQuery();
		if (null != query) {
			// Yes!  Split it at the &'s.
			String[] parameters = query.split("&");
			
			// Scan the parameters.
			for (String p:  parameters) {
				// Does this parameter contain an '='?
				int eq = p.indexOf('=');
				if ((-1) != eq) {
					// Yes!  Does it have a name part?
					String name = p.substring(0, eq);
					if (MiscUtil.hasString(name)) {
						// Yes!  Add it to the map.
						reply.put(name.toLowerCase(), p.substring(  eq + 1));
					}
				}
			}
		}
		
		else {
			// No, the URL didn't contain a query!  Does it contain a
			// path?
			String path = uri.getPath();
			if (null != path) {
				// Yes!  Split it at the /'s.
				String[] parameters = path.split("/");
				
				// Scan the parameters by 2's.
				int c = (parameters.length - 1);
				for (int i = 0; i < c; i += 2) {
					// Does this parameter have a name?
					String name = parameters[i];
					if (MiscUtil.hasString(name)) {
						// Yes!  Add it to the map.
						reply.put(name.toLowerCase(), parameters[i + 1]);
					}
				}
			}
		}
		
		// If we get here, reply refers to a Map<String, String> of the
		// parameters from the URL.  Return it.
		return reply;
	}

	/*
	 * Searches a Map<String, String> for a named parameter containing
	 * a Boolean value.
	 * 
	 * If the value can't be found or it can't be parsed, null is
	 * returned. 
	 */
	private static boolean getQueryParameterBoolean(Map<String, String> nvMap, String name) {
		String v = getQueryParameterString(nvMap, name);
		if (0 < v.length()) {
			boolean reply;
			try                 {reply = Boolean.parseBoolean(v);}
			catch (Exception e) {reply = false;                  }
			return reply;
		}		
		return false;
	}
	
	/*
	 * Searches a Map<String, String> for a named parameter containing
	 * a Boolean value.
	 * 
	 * If the value can't be found or it can't be parsed, null is
	 * returned. 
	 */
	@SuppressWarnings("unused")
	private static int getQueryParameterInt(Map<String, String> nvMap, String name) {
		String v = getQueryParameterString(nvMap, name);
		if (0 < v.length()) {
			int reply;
			try                 {reply = Integer.parseInt(v);}
			catch (Exception e) {reply = (-1);               }
			return reply;
		}		
		return (-1);
	}
	
	/*
	 * Searches a Map<String, String> for a named parameter containing
	 * a Long value.
	 * 
	 * If the value can't be found or it can't be parsed, null is
	 * returned. 
	 */
	private static Long getQueryParameterLong(Map<String, String> nvMap, String name) {
		String v = getQueryParameterString(nvMap, name);
		if (0 < v.length()) {
			Long reply;
			try                 {reply = Long.parseLong(v);}
			catch (Exception e) {reply = null;             }
			return reply;
		}		
		return null;
	}
	
	/*
	 * Searches a Map<String, String> for a named parameter.
	 * 
	 * If the value can't be found, null is returned. 
	 */
	private static String getQueryParameterString(Map<String, String> nvMap, String name) {
		String reply;
		if ((null != nvMap) && (!(nvMap.isEmpty())))
		     reply = nvMap.get(name.toLowerCase());
		else reply = null;
		return ((null == reply) ? "" : reply);
	}
	
	/**
	 * Returns a ViewInfo used to control folder views based on a URL.
	 * 
	 * @param bs
	 * @param request
	 * @param url
	 * 
	 * @return
	 */
	public static ViewInfo getViewInfo(AllModulesInjected bs, HttpServletRequest request, String url) {
		// Trace the URL we're working with.
		m_logger.debug("GwtViewHelper.getViewInfo():  " + url);

		// Can we parse the URL?
		Map<String, String> nvMap = getQueryParameters(url);
		if ((null == nvMap) || nvMap.isEmpty()) {
			// No!  Then we can't get a BinderInfo.
			m_logger.debug("GwtViewHelper.getViewInfo():  1:Could not determine a view.");
			return null;
		}

		// Construct a ViewInfo we can setup with the information for
		// viewing this URL.
		ViewInfo vi = new ViewInfo();

		// What's URL requesting?
		String action = getQueryParameterString(nvMap, WebKeys.URL_ACTION).toLowerCase();		
		if (action.equals(WebKeys.ACTION_VIEW_PERMALINK)) {
			// A view on a permalink!  What type of entity is being
			// viewed?
			String entityType = getQueryParameterString(nvMap, WebKeys.URL_ENTITY_TYPE).toLowerCase();
			if (entityType.equals("user")) {
				// A user!  Can we access the user?
				Long entryId = getQueryParameterLong(nvMap, WebKeys.URL_ENTRY_ID);			
				if (!(initVIFromUser(bs, GwtServerHelper.getUserFromId(bs, entryId), vi))) {
					m_logger.debug("GwtViewHelper.getViewInfo():  2:Could not determine a view.");
					return null;
				}
			}
			else if (entityType.equals("folder") || entityType.equals("workspace") || entityType.equals("profiles")) {
				// A folder, workspace or the profiles binder!  Setup
				// a binder view based on the binder ID.
				if (!(initVIFromBinderId(bs, nvMap, WebKeys.URL_BINDER_ID, vi, true))) {
					m_logger.debug("GwtViewHelper.getViewInfo():  3:Could not determine a view.");
					return null;
				}
			}
		}
		
		else if (action.equals(WebKeys.ACTION_VIEW_WS_LISTING) || action.equals(WebKeys.ACTION_VIEW_FOLDER_LISTING)) {
			// A view workspace listing!  Setup a binder view based
			// on the binder ID.
			if (!(initVIFromBinderId(bs, nvMap, WebKeys.URL_BINDER_ID, vi, true))) {
				m_logger.debug("GwtViewHelper.getViewInfo():  4:Could not determine a view.");
				return null;
			}
		}
		
		else if (action.equals(WebKeys.ACTION_ADVANCED_SEARCH)) {
			// An advanced search!  Simply mark the ViewInfo as such.
			vi.setViewType(ViewType.ADVANCED_SEARCH);
		}
		
//!		...this needs to be implemented...

		// If we get here reply refers to the BinderInfo requested or
		// is null.  Return it.
		if (m_logger.isDebugEnabled()) {
			dumpViewInfo(vi);
		}
		return vi;
	}

	/*
	 * Initializes a ViewInfo based on a binder ID.
	 * 
	 * Returns true if the ViewInfo was initialized and false
	 * otherwise.
	 */
	private static boolean initVIFromBinderId(AllModulesInjected bs, Map<String, String> nvMap, String binderIdName, ViewInfo vi, boolean checkForTrash) {
		// Initialize as a binder based on the user's workspace.
		Long binderId = getQueryParameterLong(nvMap, binderIdName);
		BinderInfo bi = GwtServerHelper.getBinderInfo(bs, String.valueOf(binderId));
		if (null == bi) {
			return false;
		}
		vi.setViewType(ViewType.BINDER);
		vi.setBinderInfo(bi);

		// Do we need to check for a show trash flag?
		if (checkForTrash) {
			// Yes!  Are we showing the trash on a this binder?
			boolean showTrash = getQueryParameterBoolean(nvMap, WebKeys.URL_SHOW_TRASH);
			if (showTrash) {
				// Yes!  Update the folder/workspace type
				// accordingly.
				if      (bi.isBinderFolder())    bi.setFolderType(   FolderType.TRASH   );
				else if (bi.isBinderWorkspace()) bi.setWorkspaceType(WorkspaceType.TRASH);
			}
		}
		return true;
	}
	
	/*
	 * Initializes a ViewInfo based on a User.
	 * 
	 * Returns true if the ViewInfo was initialized and false
	 * otherwise.
	 */
	private static boolean initVIFromUser(AllModulesInjected bs, User user, ViewInfo vi) {
		// Were we given a User object to initialize from?
		if (null == user) {
			// No!  Bail.
			return false;
		}

		// Initialize as a binder based on the user's workspace.
		BinderInfo bi = GwtServerHelper.getBinderInfo(bs, String.valueOf(user.getWorkspaceId()));
		if (null == bi) {
			return false;
		}
		vi.setViewType(ViewType.BINDER);
		vi.setBinderInfo(bi);
		
		return true;
	}
}
