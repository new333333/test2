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
import org.kablink.teaming.gwt.client.rpc.shared.DumpHistoryInfoCmd;
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
 * When supported by the browser, history information is tracked in the
 * HTML5 session store as HistoryInfo's serialized to an XML stream.
 * The XML used to store history is constructed as follows:
 *		
 *	 	<history itemType="..." selectedMastheadCollection="...">
 *	 		<activityStream showSetting="..." activityStreamInfo="..." />
 *							- or -	 			
 *	 		<adminAction action="..." localizedName="..." url="..." />
 *							- or -	 			
 *	 		<url instigator="..." url="..." />
 *	 	</history>
 *
 * @author drfoster@novell.com
 */
public class HistoryHelper {
	// The following are used to control and provide access to the
	// HTML5 session store.
	private static boolean		m_html5Supported 	= Storage.isSessionStorageSupported();
	private static StorageMap	m_html5SessionStore	= (m_html5Supported ? new StorageMap(Storage.getSessionStorageIfSupported()) : null); 

	private final static String	HISTORY_MARKER			= "";						// Marker appended to a URL with a history token so that we can relocate the URL during browser navigation.
	private final static int	HISTORY_MARKER_LENGTH	= HISTORY_MARKER.length();	// Length of HISTORY_MARKER.
	private final static String	HTML5_STORAGE_KEY_BASE	= "historyStorage:";		// Used to construct keys into the HTML5 session store where serialized HistoryInfo's are stored.

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

	/*
	 * Constructor method. 
	 */
	private HistoryHelper() {
		// Inhibits this class from being instantiated.
	}

	/**
	 * Clears the user's history information. 
	 */
	public static void clearHistory() {
		// If history handling is not supported...
		if (!(isHistorySupported())) {
			// ...bail.
			return;
		}

		// Clear the history from the appropriate location.
		if (m_html5Supported)
		     clearHistoryFromHtml5Storage();
		else clearHistoryFromServer();
	}

	/*
	 * Clears this HistoryInfo's from the HTML5 session store.
	 */
	private static void clearHistoryFromHtml5Storage() {
		// Collect the HistoryInfo keys...
		List<String> historyKeys = new ArrayList<String>();
		for (String key:  m_html5SessionStore.keySet()) {
			if (key.startsWith(HTML5_STORAGE_KEY_BASE)) {
				historyKeys.add(key);
			}
		}
		
		// ...and delete them from the HTML5 session store.
		for (String key:  historyKeys) {
			m_html5SessionStore.remove(key);
		}

		// If we're in debug mode...
		if (GwtClientHelper.isDebugUI()) {
			// ...dump the clear to the system log.
			GwtClientHelper.executeCommand(new DumpHistoryInfoCmd("html5:clear", "all", null), new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {}	// Ignored.
				
				@Override
				public void onSuccess(VibeRpcResponse response) {}	// Ignored.
			});
		}
	}
	
	/*
	 * Clears this HistoryInfo's from the the server
	 */
	private static void clearHistoryFromServer() {
		// Request that the history be cleared from the server.
		GwtClientHelper.executeCommand(new ClearHistoryCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {}	// Ignored.
			
			@Override
			public void onSuccess(VibeRpcResponse response) {}	// Ignored.
		});
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

		// Read the requested HistoryInfo from the appropriate
		// location.
		if (m_html5Supported)
		     getHistoryInfoFromHtml5Storage(historyToken, historyCB);
		else getHistoryInfoFromServer(      historyToken, historyCB);
	}
	
	/*
	 * Request the HistoryInfo corresponding to the given historyToken
	 * from the HTML5 session store.
	 */
	private static void getHistoryInfoFromHtml5Storage(String historyToken, HistoryInfoCallback historyCB) {
		// Can we find the XML for the history token?
		HistoryInfo historyInfo = null;
		String xml = m_html5SessionStore.get(HTML5_STORAGE_KEY_BASE + historyToken);
		if (GwtClientHelper.hasString(xml)) {
			// Yes!  Parse it.
			Document xmlD     = XMLParser.parse(xml);
			Element  historyE = xmlD.getDocumentElement();
			assert ((null != historyE) && historyE.getNodeName().equals(XML_HISTORY)) : "HistoryHelper.getHistoryInfoFromHtml5Storage( *Internal Error* ):  Bogus history XML root";

			// What type of history item is it?
			CollectionType  selectedMastheadCollection = CollectionType.valueOf(historyE.getAttribute(XML_SELECTED_MASTHEAD_COLLECTION));  
			Element         childE                     = ((Element) historyE.getFirstChild());
			HistoryItemType historyType                = HistoryItemType.valueOf(historyE.getAttribute(XML_ITEM_TYPE));
			switch(historyType) {
			case ACTIVITY_STREAM: {
				// An Activity Stream.  Assert the have a valid child
				// element...
				assert ((null != childE) && childE.getNodeName().equals(XML_ACTIVITY_STREAM)) : "HistoryHelper.getHistoryInfoFromHtml5Storage( *Internal Error* ):  Bogus history <activityStream>";

				// ...and parse out the HistoryInfo for the activity
				// ...stream.
				ActivityStreamDataType showSetting = ActivityStreamDataType.valueOf(childE.getAttribute(XML_SHOW_SETTING));
				ActivityStreamInfo     asInfo      = ActivityStreamInfo.parse(      childE.getAttribute(XML_ACTIVITY_STREAM_INFO));
				historyInfo = new HistoryInfo(selectedMastheadCollection, asInfo, showSetting);
				
				break;
			}
				
			case ADMIN_ACTION: {
				// An Admin Action.  Assert the have a valid child
				// element...
				assert ((null != childE) && childE.getNodeName().equals(XML_ADMIN_ACTION)) : "HistoryHelper.getHistoryInfoFromHtml5Storage( *Internal Error* ):  Bogus history <adminAction>";
				
				// ...and parse out the HistoryInfo for the admin
				// ...action.
				GwtAdminAction adminAction = null;
				String actionS = childE.getAttribute(XML_ACTION);
				if (GwtClientHelper.hasString(actionS)) {
					AdminAction action        = AdminAction.valueOf(actionS);
					String      localizedName = childE.getAttribute(XML_LOCALIZED_NAME);
					String      url           = childE.getAttribute(XML_URL);
					adminAction = new GwtAdminAction();
					adminAction.init(localizedName, url, action);
				}
				historyInfo = new HistoryInfo(selectedMastheadCollection, adminAction);
				
				break;
			}
				
			case URL: {
				// A URL.  Assert the have a valid child element...
				assert ((null != childE) && childE.getNodeName().equals(XML_URL)) : "HistoryHelper.getHistoryInfoFromHtml5Storage( *Internal Error* ):  Bogus history <url>";
				
				// ...and parse out the HistoryInfo for the URL.
				Instigator instigator = Instigator.valueOf(childE.getAttribute(XML_INSTIGATOR));
				String     url        = childE.getAttribute(XML_URL);
				historyInfo = new HistoryInfo(selectedMastheadCollection, url, instigator);
				
				break;
			}
			
			default:
				// Whatever it is, code hasn't been written to handle
				// it yet!  Tell the user about the problem.
				GwtClientHelper.debugAlert(
					"HistoryHelper.getHistoryInfoFromHtml5Storage( Unhandled history type:  " + historyType.name() + " ):  ...this needs to be implemented...");
				
				break;
			}
		}

		// Pass the HistoryInfo back to the callback.
		historyCB.historyInfo(historyInfo);
		
		// If we're in debug mode...
		if (GwtClientHelper.isDebugUI()) {
			// ...dump the HistoryInfo to the system log.
			GwtClientHelper.executeCommand(new DumpHistoryInfoCmd("html5:get", historyToken, historyInfo), new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {}	// Ignored.
				
				@Override
				public void onSuccess(VibeRpcResponse response) {}	// Ignored.
			});
		}
	}
	
	/*
	 * Request the HistoryInfo corresponding to the given historyToken
	 * from the server.
	 */
	private static void getHistoryInfoFromServer(final String historyToken, final HistoryInfoCallback historyCB) {
		// Request the HistoryInfo from the server...
		GwtClientHelper.executeCommand(new GetHistoryInfoCmd(historyToken), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				// ...if the request fails, return null...
				historyCB.historyInfo(null);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// ...otherwise, return whatever we got back for the
				// ...HistoryInfo.
				historyCB.historyInfo((HistoryInfo) response.getResponseData());
			}
		});
	}

	/**
	 * Initializes browser history handling.
	 */
	public static void initializeBrowserHistory() {
		// If history handling is not supported...
		if (!(isHistorySupported())) {
			// ...bail.
			return;
		}

		// Connect to the GWT history manager.
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			/**
			 * The ValueChangeHandler will be triggered whenever we're
			 * supposed to navigate to place to someplace from the
			 * history as indicated by the history token contained in
			 * the event.
			 * 
			 * @param event
			 */
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				try {
					// If we can find the history token...
					String historyMarker = event.getValue();
					if (GwtClientHelper.hasString(historyMarker)) {
						if (historyMarker.substring(0, HISTORY_MARKER_LENGTH).equals(HISTORY_MARKER)) {
							String historyToken = historyMarker.substring(HISTORY_MARKER_LENGTH);
							if (GwtClientHelper.hasString(historyToken)) {
								// ...process it...
								processHistoryTokenAsync(historyToken);
								return;
							}
						}
					}
				}
				catch (Exception e) {}	// Ignored.
				
				// ...otherwise, simply force the content to refresh.
				FullUIReloadEvent.fireOneAsync();
			}
		});

		// It's required that we 'fire' the current history state when
		// we initialize.
		History.fireCurrentHistoryState();
	}

	/*
	 * Returns true if history is supported and false otherwise.
	 */
	private final static boolean isHistorySupported() {
		// Does the browser support HTML5 storage?
		boolean reply = m_html5Supported;
		if (reply) {
			// Yes!  Do we have access to the HTML5 session store?  
			reply = (null != m_html5SessionStore);
			if (!reply) {
				// No!  Then we'll act like the browser really doesn't
				// support it.
				m_html5Supported = false;
			}
		}

		// If we don't have access to the HTML5 session store...
		if (!reply) {
			// ...should we track history using RPC's to the server?
			reply = GwtClientHelper.getRequestInfo().isTrackNonHTML5HistoryOnServer();
		}
		
		// If we get here, reply is true if history is supported and
		// false otherwise.  Return it.
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
			
			// Note that in this case, the processing of the URL will
			// take care of the appropriate selection in the masthead.
			
			break;
			
		default:
			// Whatever it is, code hasn't been written to handle it
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
		// Read and process the history information from the
		// appropriate location... 
		getHistoryInfo(historyToken, new HistoryInfoCallback() {
			@Override
			public void historyInfo(HistoryInfo historyInfo) {
				// ...and process it.
				if (null != historyInfo)
				     processHistoryInfoAsync(historyInfo);
				//else FullUIReloadEvent.fireOneAsync(); //  Disbled by Lokesh.  Do we really need this? With references this will cause unnecessary page reload.  Disabling this for now to fix bug 985732.
			}
		} );
	}

	/*
	 * Stores a HistoryInfo in the HTML5 session store.
	 */
	private static void pushHistoryInfoToHtml5Storage(HistoryInfo historyInfo) {
		// Create the <history> XML document.
		Document xmlD     = XMLParser.createDocument();
		Element  historyE = xmlD.createElement(XML_HISTORY);
		xmlD.appendChild(historyE);
		historyE.setAttribute(XML_ITEM_TYPE,                    historyInfo.getItemType().name());
		historyE.setAttribute(XML_SELECTED_MASTHEAD_COLLECTION, historyInfo.getSelectedMastheadCollection().name());

		// What type of HistoryInto are we dealing with?
		HistoryItemType historyType = historyInfo.getItemType();
		switch (historyType) {
		case ACTIVITY_STREAM: {
			// An Activity Stream!  Add an <activityStream> to the
			// <history> for it.
			Element childE = xmlD.createElement(XML_ACTIVITY_STREAM);
			historyE.appendChild(childE);
			HistoryActivityStreamInfo asInfo = historyInfo.getActivityStreamInfo();
			childE.setAttribute(XML_SHOW_SETTING,         asInfo.getShowSetting().name());
			childE.setAttribute(XML_ACTIVITY_STREAM_INFO, asInfo.getActivityStreamInfo().getStringValue());
			break;
		}
			
		case ADMIN_ACTION: {
			// An Admin Action!  Add an <adminAction> to the <history>
			// for it.
			Element childE = xmlD.createElement(XML_ADMIN_ACTION);
			GwtAdminAction aaAction = historyInfo.getAdminActionInfo().getAdminAction();
			historyE.appendChild(childE);
			if (null != aaAction) {
				childE.setAttribute(XML_ACTION,         aaAction.getActionType().name());
				childE.setAttribute(XML_LOCALIZED_NAME, aaAction.getLocalizedName());
				childE.setAttribute(XML_URL,            aaAction.getUrl());
			}
			break;
		}
			
		case URL: {
			// A URL!  Add a <url> to the <history> for it.
			Element childE = xmlD.createElement(XML_URL);
			historyE.appendChild(childE);
			HistoryUrlInfo uInfo = historyInfo.getUrlInfo();
			childE.setAttribute(XML_INSTIGATOR, uInfo.getInstigator().name());
			childE.setAttribute(XML_URL,        uInfo.getUrl());
			break;
		}
		
		default:
			// Whatever it is, code hasn't been written to handle it
			// yet!  Tell the user about the problem.
			GwtClientHelper.debugAlert(
				"HistoryHelper.pushHistoryInfoToHtml5Storage( Unhandled history type:  " + historyType.name() + " ):  ...this needs to be implemented...");
			
			break;
		}

		// Write the XML to the HTML5 session store...
		String historyToken = String.valueOf(new Date().getTime());
		m_html5SessionStore.put((HTML5_STORAGE_KEY_BASE + historyToken), xmlD.toString());
		
		// ...and write the history token into the History.
		History.newItem(
			(HISTORY_MARKER + historyToken),	// History marker.
			false);								// false -> Don't fire a change event for this item.
		
		// Finally, if we're in debug mode...
		if (GwtClientHelper.isDebugUI()) {
			// ...dump the HistoryInfo to the system log.
			GwtClientHelper.executeCommand(new DumpHistoryInfoCmd("html5:push", historyToken, historyInfo), new AsyncCallback<VibeRpcResponse>() {
				@Override
				public void onFailure(Throwable t) {}	// Ignored.
				
				@Override
				public void onSuccess(VibeRpcResponse response) {}	// Ignored.
			});
		}
	}
	
	/*
	 * Stores a HistoryInfo on the server.
	 */
	private static void pushHistoryInfoToServer(HistoryInfo historyInfo) {
		// Push the HistoryInfo to the server.
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
	
	/**
	 * Asynchronously pushes an administrative action based HistoryInfo
	 * into the user's history cache.
	 * 
	 * @param adminAction
	 */
	public static void pushHistoryInfoAsync(final GwtAdminAction adminAction) {
		// If history handling is not supported...
		if (!(isHistorySupported())) {
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
		// If history handling is not supported...
		if (!(isHistorySupported())) {
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
		// If history handling is not supported...
		if (!(isHistorySupported())) {
			// ...bail.
			return;
		}

		// If the URL is simply forcing the UI to reload...
		if (Instigator.FORCE_FULL_RELOAD.equals(instigator)) {
			// ...we don't add that to the history.
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
		// Push the history information to the appropriate location.
		if (m_html5Supported)
		     pushHistoryInfoToHtml5Storage(historyInfo);
		else pushHistoryInfoToServer(      historyInfo);
	}
}
