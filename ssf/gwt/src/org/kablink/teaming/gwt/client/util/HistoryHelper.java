/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.gwt.client.admin.AdminAction;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.admin.GwtAdminAction;
import org.kablink.teaming.gwt.client.event.ActivityStreamEnterEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamEvent;
import org.kablink.teaming.gwt.client.event.AdministrationActionEvent;
import org.kablink.teaming.gwt.client.event.AdministrationEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.GetSidebarCollectionEvent;
import org.kablink.teaming.gwt.client.event.SetFilrActionFromCollectionTypeEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.GetSidebarCollectionEvent.CollectionCallback;
import org.kablink.teaming.gwt.client.rpc.shared.ClearHistoryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetHistoryInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PushHistoryInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.HistoryInfo.HistoryActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.HistoryInfo.HistoryAdminActionInfo;
import org.kablink.teaming.gwt.client.util.HistoryInfo.HistoryItemType;
import org.kablink.teaming.gwt.client.util.HistoryInfo.HistoryUrlInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

/**
 * Helper methods for the GWT UI client code that deals with browser
 * history.
 *
 * An example of the XML used to store history in the HTML5 session
 * store is as follows:
 *		
 *	 <history itemType="..." selectedMastheadCollection="...">
 *	 	<activityStream showSetting="..." activityStreamInfo="..." />
 *				- or -	 			
 *	 	<adminAction action="..." localizedName="..." url="..." />
 *				- or -	 			
 *	 	<url instigator="..." url="..." />
 *	 </history>
 *
 * @author drfoster@novell.com
 */
public class HistoryHelper {
	private final static boolean ENABLE_BROWSER_HISTORY	= false;	//! DRF (20140103):  Leave false on checkin until it's all working.
	private final static boolean HTML5_BROWSER_HISTORY	= false;	//! DRF (20140103):  Leave false on checkin until it's all working.
	
	private final static String	HISTORY_MARKER				= "history";				// Marker appended to a URL with a history token so that we can relocate the URL during browser navigations.
	private final static int	HISTORY_MARKER_LENGTH		= HISTORY_MARKER.length();	// Length of HISTORY_MARKER.
	private final static String	HISTORY_STORAGE_KEY_BASE	= "historyStorage:";		// Used to construct keys into m_html5Storage where serialized HistoryInfo's are stored.

	// The following are used as the tag and attribute names for the
	// XML used to represent a serialized HistoryInfo object.
	private final static String XML_ACTION							= "action";
	private final static String XML_ACTIVITY_STREAM					= "activityStream";
	private final static String XML_ACTIVITY_STREAM_INFO			= "activityStreamInfo";
	private final static String XML_ADMIN_ACTION					= "adminAction";
	private final static String XML_HISTORY							= "history";
	private final static String XML_INSTIGATOR						= "instigator";
	private final static String XML_ITEM_TYPE						= "itemType";
	private final static String	XML_LOCALIZED_NAME					= "localizedName";
	private final static String	XML_SELECTED_MASTHEAD_COLLECTION	= "selectedMastheadCollection";
	private final static String XML_SHOW_SETTING					= "showSetting";
	private final static String XML_URL								= "url";
	
	private static StorageMap	m_html5Storage;	// Initialized upon first use.

	/*
	 * Constructor method. 
	 */
	private HistoryHelper() {
		// Inhibits this class from being instantiated.
	}

	/**
	 * Clears the user's history information that's cached on the
	 * server. 
	 */
	public static void clearHistory() {
		// If browser history handling is not enabled...
		if (!(isBrowserHistoryEnabled())) {	//! Note that this is still in development !!!
			// ...bail.
			return;
		}

		// Are we using the HTML5 store for history?
		if (HTML5_BROWSER_HISTORY) {
			// Yes!  Clear the HistoryInfo's stored there.
			clearHistoryFromHtml5Storage();
		}
		
		else {
			// No, we're using the server for storage!  Request that
			// the history be cleared from the server.
			GwtClientHelper.executeCommand(new ClearHistoryCmd(), new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {}	// Ignored.
				
				@Override
				public void onSuccess(VibeRpcResponse response) {}	// Ignored.
			});
		}
	}

	/*
	 * Clears this HistoryInfo's from the HTML5 store.
	 */
	private static void clearHistoryFromHtml5Storage() {
		// Collect the HistoryInfo keys...
		List<String> historyKeys = new ArrayList<String>();
		StorageMap hs = getHistoryStorage();
		for (String key:  hs.keySet()) {
			if (key.startsWith(HISTORY_STORAGE_KEY_BASE)) {
				historyKeys.add(key);
			}
		}
		
		// ...and delete them from the HTML5 store.
		for (String key:  historyKeys) {
			hs.remove(key);
		}
	}
	
	/**
	 * Returns the the browser history token currently stored in the
	 * Window.Location object.  If there is no history token there,
	 * null is returned.
	 * 
	 * @return
	 */
	public static String getCurrentBrowserHistoryToken() {
		// Is there a Window.Location URL?
		String reply = null;
		String url   = Window.Location.createUrlBuilder().buildString();
		if (GwtClientHelper.hasString(url)) {
			// Yes!  Does it contain a history token?
			int historyPos = url.lastIndexOf("#" + HISTORY_MARKER);
			if ((-1) != historyPos) {
				// Yes!  Extract it.
				reply = url.substring(historyPos + HISTORY_MARKER_LENGTH + 1);
			}
		}
		
		// If we get here, reply refers to the current browser history
		// token or null if there isn't one.  Return it. 
		return reply;
	}

	/**
	 * Returns the HistoryInfo corresponding to a history token via a
	 * callback.
	 * 
	 * @param historyToken
	 * @param historyCB
	 */
	public static void getHistoryInfo(String historyToken, final HistoryInfoCallback historyCB) {
		// If we don't have a callback...
		if (null == historyCB) {
			// ...we can't do anything.
			return;
		}

		// If we don't have a history token...
		if (!(GwtClientHelper.hasString(historyToken))) {
			// ...there can be no corresponding HistoryInfo.
			historyCB.historyInfo(null);
			return;
		}

		// Are we using the HTML5 store for history?
		if (HTML5_BROWSER_HISTORY) {
			// Yes!  Write the HistoryInfo to the HTML5 store and
			// return its key.
			historyCB.historyInfo(getHistoryInfoFromHtml5Storage(historyToken));
		}
		
		else {
			// No, we're using the server for storage!  Request the
			// HistoryInfo from the server...
			GwtClientHelper.executeCommand(new GetHistoryInfoCmd(historyToken), new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					// ...if the request fails, return null...
					historyCB.historyInfo(null);
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// ...otherwise, return the HistoryInfo we got back.
					historyCB.historyInfo((HistoryInfo) response.getResponseData());
				}
			});
		}
	}
	
	/*
	 * Returns the HistoryInfo corresponding to the given historyToken
	 * from the HTML5 store.
	 */
	private static HistoryInfo getHistoryInfoFromHtml5Storage(String historyToken) {
		HistoryInfo reply = null;
		StorageMap hs = getHistoryStorage();
		String xml = hs.get(HISTORY_STORAGE_KEY_BASE + historyToken);
		if (GwtClientHelper.hasString(xml)) {
			Document xmlD = XMLParser.parse(xml);
			Element historyE = xmlD.getDocumentElement();
			assert ((null != historyE) && historyE.getNodeName().equals(XML_HISTORY)) : "HistoryHelper.getHistoryInfoFromHtml5Storage( *Internal Error* ):  Bogus history XML root";
			CollectionType selectedMastheadCollection = CollectionType.valueOf(historyE.getAttribute(XML_SELECTED_MASTHEAD_COLLECTION));  
			Element childE = ((Element) historyE.getFirstChild());
			HistoryItemType historyType = HistoryItemType.valueOf(historyE.getAttribute(XML_ITEM_TYPE));
			switch(historyType) {
			case ACTIVITY_STREAM: {
				assert ((null != childE) && childE.getNodeName().equals(XML_ACTIVITY_STREAM)) : "HistoryHelper.getHistoryInfoFromHtml5Storage( *Internal Error* ):  Bogus history <activityStream>";
				ActivityStreamDataType showSetting = ActivityStreamDataType.valueOf(childE.getAttribute(XML_SHOW_SETTING));
				ActivityStreamInfo asInfo = ActivityStreamInfo.parse(childE.getAttribute(XML_ACTIVITY_STREAM_INFO));
				reply = new HistoryInfo(selectedMastheadCollection, asInfo, showSetting);
				break;
			}
				
			case ADMIN_ACTION: {
				assert ((null != childE) && childE.getNodeName().equals(XML_ADMIN_ACTION)) : "HistoryHelper.getHistoryInfoFromHtml5Storage( *Internal Error* ):  Bogus history <adminAction>";
				GwtAdminAction adminAction = null;
				String actionS = childE.getAttribute(XML_ACTION);
				if (GwtClientHelper.hasString(actionS)) {
					AdminAction action = AdminAction.valueOf(actionS);
					String localizedName = childE.getAttribute(XML_LOCALIZED_NAME);
					String url = childE.getAttribute(XML_URL);
					adminAction = new GwtAdminAction();
					adminAction.init(localizedName, url, action);
				}
				reply = new HistoryInfo(selectedMastheadCollection, adminAction);
				break;
			}
				
			case URL: {
				assert ((null != childE) && childE.getNodeName().equals(XML_URL)) : "HistoryHelper.getHistoryInfoFromHtml5Storage( *Internal Error* ):  Bogus history <url>";
				Instigator instigator = Instigator.valueOf(childE.getAttribute(XML_INSTIGATOR));
				String url = childE.getAttribute(XML_URL);
				reply = new HistoryInfo(selectedMastheadCollection, url, instigator);
				break;
			}
			
			default:
				// Whatever it is, code hasn't been written to handle this
				// yet!  Tell the user about the problem.
				GwtClientHelper.debugAlert(
					"HistoryHelper.getHistoryInfoFromHtml5Storage( Unhandled history type:  " + historyType.name() + " ):  ...this needs to be implemented...");
				
				break;
			}
		}
		return reply;
	}

	/*
	 * Returns the HTML5 StorageMap to use for storing history.
	 */
	private static StorageMap getHistoryStorage() {
		// If we haven't cached the HTML5 StorageMap...
		if (null == m_html5Storage) {
			// ...create and cache it now...
			m_html5Storage = new StorageMap(Storage.getSessionStorageIfSupported());
		}
		
		// ...and return it.
		return m_html5Storage;
	}
	
	/**
	 * Initializes browser history handling.
	 */
	public static void initializeBrowserHistory() {
		// If browser history handling is not enabled...
		if (!(isBrowserHistoryEnabled())) {	//! Note that this is still in development !!!
			// ...bail.
			return;
		}
		
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				try {
					// If we can find the history token...
					String historyMarker = event.getValue();
					if (historyMarker.substring(0, HISTORY_MARKER_LENGTH).equals(HISTORY_MARKER)) {
						String historyToken = historyMarker.substring(HISTORY_MARKER_LENGTH);
						if (GwtClientHelper.hasString(historyToken)) {
							// ...process it...
							processHistoryTokenAsync(historyToken);
							return;
						}
					}
				}
				catch (Exception e) {}	// Ignored.
				
				// ...otherwise, simply force the content to refresh.
				FullUIReloadEvent.fireOneAsync();
			}
		});
	}

	/**
	 * Returns true if browser history support is enabled and false
	 * otherwise.
	 */
	public final static boolean isBrowserHistoryEnabled() {
		// Is browser history enabled?
		boolean reply = ENABLE_BROWSER_HISTORY;	//! Note that this is still in development !!!
		if (reply) {
			// Yes!  Are we supposed to use the HTML5 store for
			// history?
			if (HTML5_BROWSER_HISTORY) {	//! Note that this is still in development !!!
				// Yes!  Return true if HTML5 storage is supported and
				// false otherwise.
				reply = Storage.isSessionStorageSupported();
			}
		}
		
		// If we get here, reply is true if browser history is
		// supported and false otherwise.  Return it.
		return reply;
	}
	
	/**
	 * Asynchronously puts a HistoryInfo into effect.
	 * 
	 * @param historyInfo
	 */
	public static void processHistoryInfoAsync(final HistoryInfo historyInfo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				processHistoryInfoNow(historyInfo);
			}
		});
	}
	
	/*
	 * Synchronously puts a HistoryInfo into effect.
	 */
	private static void processHistoryInfoNow(final HistoryInfo historyInfo) {
		// What type of HistoryInfo are we processing?
		HistoryItemType historyType = historyInfo.getItemType();
		switch (historyType) {
		case ACTIVITY_STREAM:
			// An Activity Stream!  Put it into effect...
			HistoryActivityStreamInfo asInfo = historyInfo.getActivityStreamInfo();
			VibeEventBase<?> asEvent = null;
			if (GwtTeaming.getMainPage().isActivityStreamActive())
			     asEvent = new ActivityStreamEvent(     asInfo.getActivityStreamInfo()                         );
			else asEvent = new ActivityStreamEnterEvent(asInfo.getActivityStreamInfo(), asInfo.getShowSetting());
			asEvent.setHistoryAction(true);
			asEvent.setHistorySelectedMastheadCollection(historyInfo.getSelectedMastheadCollection());
			GwtTeaming.fireEvent(asEvent);
			
			// ...and make sure the masthead selection is what it
			// ...should be for this HistoryInfo.
			GwtTeaming.fireEventAsync(
				new SetFilrActionFromCollectionTypeEvent(
					historyInfo.getSelectedMastheadCollection()));

			break;

		case ADMIN_ACTION:
			// An Administration Action!  Put it into effect... 
			HistoryAdminActionInfo aaInfo = historyInfo.getAdminActionInfo();
			GwtAdminAction adminAction = aaInfo.getAdminAction();
			VibeEventBase<?> aEvent;
			if (null == adminAction)
			     aEvent = new AdministrationEvent();
			else aEvent = new AdministrationActionEvent(adminAction);
			aEvent.setHistoryAction(true);
			aEvent.setHistorySelectedMastheadCollection(historyInfo.getSelectedMastheadCollection());
			GwtTeaming.fireEvent(aEvent);
			
			// ...and make sure the masthead selection is what it
			// ...should be for this HistoryInfo.
			GwtTeaming.fireEventAsync(
				new SetFilrActionFromCollectionTypeEvent(
					historyInfo.getSelectedMastheadCollection()));
			
			break;
			
		case URL:
			// A URL!  Put it into effect.
			HistoryUrlInfo     urlInfo = historyInfo.getUrlInfo();
			OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(urlInfo.getUrl(), urlInfo.getInstigator());
			ChangeContextEvent ccEvent = new ChangeContextEvent(osbInfo);
			ccEvent.setHistoryAction(true);
			ccEvent.setHistorySelectedMastheadCollection(historyInfo.getSelectedMastheadCollection());
			GwtTeaming.fireEvent(ccEvent);
			
			break;
			
		default:
			// Whatever it is, code hasn't been written to handle this
			// yet!  Tell the user about the problem.
			GwtClientHelper.debugAlert(
				"HistoryHelper.processHistoryInfoNow( Unhandled history type:  " + historyType.name() + " ):  ...this needs to be implemented...");
			
			break;
		}
	}
	
	/**
	 * Asynchronously processes a history token.
	 * 
	 * @param historyToken
	 */
	public static void processHistoryTokenAsync(final String historyToken) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				processHistoryTokenNow(historyToken);
			}
		});
	}
	
	/*
	 * Synchronously processes a history token.
	 */
	private static void processHistoryTokenNow(final String historyToken) {
		// Are we using the HTML5 store for history?
		if (HTML5_BROWSER_HISTORY) {
			// Yes!  Read the history from the HTML5 store and process
			// it.
			HistoryInfo historyInfo = getHistoryInfoFromHtml5Storage(historyToken);
			if (null != historyInfo)
			     processHistoryInfoAsync(historyInfo);
			else FullUIReloadEvent.fireOneAsync();
		}
		
		else {
			// No, we're using the server for storage!  Get the
			// HistoryInfo from the server.
			GwtClientHelper.executeCommand(new GetHistoryInfoCmd(historyToken), new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {
					// On any failure, we simply force the content to
					// reload.
					FullUIReloadEvent.fireOneAsync();
				}
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					// If we have the HistoryInfo, put it into effect,
					// otherwise, simply force the content to reload.
					HistoryInfo historyInfo = ((HistoryInfo) response.getResponseData());
					if (null != historyInfo)
					     processHistoryInfoAsync(historyInfo);
					else FullUIReloadEvent.fireOneAsync();
				}
			});
		}
	}

	/*
	 * Stores a HistoryInfo in Html5 storage.
 *
 * 
 *	 <history itemType="..." selectedMastheadCollection="...">
 *	 	<activityStream showSetting="..." activityStreamInfo="..." />
 *				- or -	 			
 *	 	<adminAction action="..." localizedName="..." url="..." />
 *				- or -	 			
 *	 	<url instigator="..." url="..." />
 *	 </history>
 *
 *
	 */
	private static void pushHistoryInfoToHtml5Storage(HistoryInfo historyInfo) {
		Document xmlD = XMLParser.createDocument();
		Element historyE = xmlD.createElement(XML_HISTORY);
		xmlD.appendChild(historyE);
		historyE.setAttribute(XML_ITEM_TYPE, historyInfo.getItemType().name());
		historyE.setAttribute(XML_SELECTED_MASTHEAD_COLLECTION, historyInfo.getSelectedMastheadCollection().name());
		HistoryItemType historyType = historyInfo.getItemType();
		switch (historyType) {
		case ACTIVITY_STREAM: {
			HistoryActivityStreamInfo asInfo = historyInfo.getActivityStreamInfo();
			Element childE = xmlD.createElement(XML_ACTIVITY_STREAM);
			historyE.appendChild(childE);
			childE.setAttribute(XML_SHOW_SETTING, asInfo.getShowSetting().name());
			childE.setAttribute(XML_ACTIVITY_STREAM_INFO, asInfo.getActivityStreamInfo().getStringValue());
			break;
		}
			
		case ADMIN_ACTION: {
			GwtAdminAction aaAction = historyInfo.getAdminActionInfo().getAdminAction();
			Element childE = xmlD.createElement(XML_ADMIN_ACTION);
			historyE.appendChild(childE);
			if (null != aaAction) {
				childE.setAttribute(XML_ACTION, aaAction.getActionType().name());
				childE.setAttribute(XML_LOCALIZED_NAME, aaAction.getLocalizedName());
				childE.setAttribute(XML_URL, aaAction.getUrl());
			}
			break;
		}
			
		case URL: {
			HistoryUrlInfo uInfo = historyInfo.getUrlInfo();
			Element childE = xmlD.createElement(XML_URL);
			historyE.appendChild(childE);
			childE.setAttribute(XML_INSTIGATOR, uInfo.getInstigator().name());
			childE.setAttribute(XML_URL, uInfo.getUrl());
			break;
		}
		
		default:
			// Whatever it is, code hasn't been written to handle this
			// yet!  Tell the user about the problem.
			GwtClientHelper.debugAlert(
				"HistoryHelper.pushHistoryInfoToHtml5Storage( Unhandled history type:  " + historyType.name() + " ):  ...this needs to be implemented...");
			
			break;
		}

		// Write the XML to the HTML5 store...
		String historyToken = String.valueOf(new Date().getTime());
		getHistoryStorage().put((HISTORY_STORAGE_KEY_BASE + historyToken), xmlD.toString());
		
		// ...and put the history token in the History.
		History.newItem(
			(HISTORY_MARKER + historyToken),	// History marker.
			false);								// false -> Don't fire a change event for this item.
	}
	
	/**
	 * Asynchronously pushes an administrative action based HistoryInfo
	 * into the user's history cache.
	 * 
	 * @param adminAction
	 */
	public static void pushHistoryInfoAsync(final GwtAdminAction adminAction) {
		// If browser history handling is not enabled...
		if (!(isBrowserHistoryEnabled())) {	//! Note that this is still in development !!!
			// ...bail.
			return;
		}

		// Get the collection that's selection in the masthead...
		GwtTeaming.fireEventAsync(new GetSidebarCollectionEvent(new CollectionCallback() {
			@Override
			public void collection(final CollectionType selectedMastheadCollection) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// ...and push the history information.
						pushHistoryInfoNow(selectedMastheadCollection, adminAction);
					}
				});
			}
		}));
	}
	
	/*
	 * Synchronously pushes an administration action based HistoryInfo
	 * into the user's history cache.
	 */
	private static void pushHistoryInfoNow(final CollectionType selectedMastheadCollection, final GwtAdminAction adminAction) {
		// Construct the HistoryInfo and store it.
		HistoryInfo historyInfo = new HistoryInfo(selectedMastheadCollection, adminAction);
		pushHistoryInfoImpl(historyInfo);
	}
	
	/**
	 * Asynchronously pushes an activity stream based HistoryInfo into
	 * the user's history cache.
	 * 
	 * @param asi
	 * @param asdt
	 */
	public static void pushHistoryInfoAsync(final ActivityStreamInfo asi, final ActivityStreamDataType asdt) {
		// If browser history handling is not enabled...
		if (!(isBrowserHistoryEnabled())) {	//! Note that this is still in development !!!
			// ...bail.
			return;
		}

		// Get the collection that's selection in the masthead...
		GwtTeaming.fireEventAsync(new GetSidebarCollectionEvent(new CollectionCallback() {
			@Override
			public void collection(final CollectionType selectedMastheadCollection) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// ...and push the history information.
						pushHistoryInfoNow(selectedMastheadCollection, asi, asdt);
					}
				});
			}
		}));
	}
	
	/*
	 * Synchronously pushes an activity stream based HistoryInfo into
	 * the user's history cache.
	 */
	private static void pushHistoryInfoNow(final CollectionType selectedMastheadCollection, final ActivityStreamInfo asi, final ActivityStreamDataType asdt) {
		// Construct the HistoryInfo and store it.
		HistoryInfo historyInfo = new HistoryInfo(selectedMastheadCollection, asi, asdt);
		pushHistoryInfoImpl(historyInfo);
	}

	/**
	 * Asynchronously pushes a URL based HistoryInfo into the user's
	 * history cache.
	 * 
	 * @param url
	 * @param instigator
	 */
	public static void pushHistoryInfoAsync(final String url, final Instigator instigator) {
		// If browser history handling is not enabled...
		if (!(isBrowserHistoryEnabled())) {	//! Note that this is still in development !!!
			// ...bail.
			return;
		}

		// If the URL is simply forcing the UI to reload...
		if (Instigator.FORCE_FULL_RELOAD.equals(instigator)) {
			// ...we don't add that to the history.
			return;
		}
		
		GwtTeaming.fireEventAsync(new GetSidebarCollectionEvent(new CollectionCallback() {
			@Override
			public void collection(final CollectionType selectedMastheadCollection) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						pushHistoryInfoNow(selectedMastheadCollection, url, instigator);
					}
				});
			}
		}));
	}
	
	/*
	 * Synchronously pushes a URL based HistoryInfo into the user's
	 * history cache.
	 */
	private static void pushHistoryInfoNow(final CollectionType selectedMastheadCollection, final String url, final Instigator instigator) {
		// Construct the HistoryInfo and store it.
		HistoryInfo historyInfo = new HistoryInfo(selectedMastheadCollection, url, instigator);
		pushHistoryInfoImpl(historyInfo);
	}
	
	/*
	 * Implementation method that actually stores a HistoryInfo.
	 */
	private static void pushHistoryInfoImpl(final HistoryInfo historyInfo) {
		// Are we using HTML5 storage for history?
		if (HTML5_BROWSER_HISTORY) {
			// Yes!  Push the HistoryInfo to the HTML5 store.
			pushHistoryInfoToHtml5Storage(historyInfo);
		}
		
		else {
			// No, we're using the server for storage!  Push the
			// HistoryInfo to the server.
			PushHistoryInfoCmd phiCmd = new PushHistoryInfoCmd(historyInfo);
			GwtClientHelper.executeCommand(phiCmd, new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {}	// Ignored.
				
				@Override
				public void onSuccess(VibeRpcResponse response) {
					String historyToken = ((StringRpcResponseData) response.getResponseData()).getStringValue();
					if (GwtClientHelper.hasString(historyToken)) {
						History.newItem(
							(HISTORY_MARKER + historyToken),	// History marker.
							false);								// false -> Don't fire a change event for this item.
					}
				}
			});
		}
	}
}
