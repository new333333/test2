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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Helper methods for the GWT UI client code that deals with browser
 * history.
 *
 * @author drfoster@novell.com
 */
public class HistoryHelper {
	private final static boolean ENABLE_BROWSER_HISTORY	= false;	//! DRF (20141231):  Leave false on checkin until it's all working.
	
	private final static String	HISTORY_MARKER			= "history";				// Marker appended to a URL with a history token so that we can relocate the URL during browser navigations.
	private final static int	HISTORY_MARKER_LENGTH	= HISTORY_MARKER.length();	// Length of HISTORY_MARKER.

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
		if (!HistoryHelper.ENABLE_BROWSER_HISTORY) {	//! Note that this is still in development !!!
			// ...bail.
			return;
		}

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

		// Request the HistoryInfo from the server...
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
	
	/**
	 * Initializes browser history handling.
	 */
	public static void initializeBrowserHistory() {
		// If browser history handling is not enabled...
		if (!HistoryHelper.ENABLE_BROWSER_HISTORY) {	//! Note that this is still in development !!!
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
	
	/**
	 * Asynchronously pushes an administrative action based HistoryInfo
	 * into the user's history cache.
	 * 
	 * @param adminAction
	 */
	public static void pushHistoryInfoAsync(final GwtAdminAction adminAction) {
		// If browser history handling is not enabled...
		if (!HistoryHelper.ENABLE_BROWSER_HISTORY) {	//! Note that this is still in development !!!
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
		HistoryInfo        hi     = new HistoryInfo(selectedMastheadCollection, adminAction);
		PushHistoryInfoCmd phiCmd = new PushHistoryInfoCmd(hi);
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
	 * Asynchronously pushes an activity stream based HistoryInfo into
	 * the user's history cache.
	 * 
	 * @param asi
	 * @param asdt
	 */
	public static void pushHistoryInfoAsync(final ActivityStreamInfo asi, final ActivityStreamDataType asdt) {
		// If browser history handling is not enabled...
		if (!HistoryHelper.ENABLE_BROWSER_HISTORY) {	//! Note that this is still in development !!!
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
		HistoryInfo        hi     = new HistoryInfo(selectedMastheadCollection, asi, asdt);
		PushHistoryInfoCmd phiCmd = new PushHistoryInfoCmd(hi);
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
	 * Asynchronously pushes a URL based HistoryInfo into the user's
	 * history cache.
	 * 
	 * @param url
	 * @param instigator
	 */
	public static void pushHistoryInfoAsync(final String url, final Instigator instigator) {
		// If browser history handling is not enabled...
		if (!HistoryHelper.ENABLE_BROWSER_HISTORY) {	//! Note that this is still in development !!!
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
		HistoryInfo        hi     = new HistoryInfo(selectedMastheadCollection, url, instigator);
		PushHistoryInfoCmd phiCmd = new PushHistoryInfoCmd(hi);
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
