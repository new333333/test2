/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.GetSidebarCollectionEvent;
import org.kablink.teaming.gwt.client.event.GetSidebarCollectionEvent.CollectionCallback;
import org.kablink.teaming.gwt.client.rpc.shared.GetHistoryInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PushHistoryInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.HistoryInfo.UrlInfo;
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
	public final static boolean	ENABLE_BROWSER_HISTORY	= false;	//! DRF (20131231):  Leave false on checkin until it's all working.
	
	private final static String	HISTORY_MARKER			= "history";				// Marker appended to a URL with a history token so that we can relocate the URL during browser navigations.
	private final static int	HISTORY_MARKER_LENGTH	= HISTORY_MARKER.length();	// Length of HISTORY_MARKER.
	
	/*
	 * Constructor method. 
	 */
	private HistoryHelper() {
		// Inhibits this class from being instantiated.
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
		switch (historyInfo.getItemType()) {
		case ACTIVITY_STREAM:
			// An Activity Stream!  Put it into effect.
//!			...this needs to be implemented...
			GwtClientHelper.deferredAlert("HistoryHelper.processHistoryInfoNow( ACTIVITY_STREAM ):  ...this needs to be implemented...");
			
			break;
			
		case URL:
			// A URL!  Put it into effect.
			UrlInfo urlInfo = historyInfo.getUrlInfo();
			OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
				urlInfo.getUrl(),
				Instigator.HISTORY_ACTION);	//! urlInfo.getInstigator());
			osbInfo.setHistorySelectedMastheadCollection(historyInfo.getSelectedMastheadCollection());
			GwtTeaming.fireEvent(new ChangeContextEvent(osbInfo));
			
			break;
		}
	}
	
	/**
	 * Asynchronously processes a history token.
	 * 
	 * @param token
	 */
	public static void processHistoryTokenAsync(final String token) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				processHistoryTokenNow(token);
			}
		});
	}
	
	/*
	 * Synchronously processes a history token.
	 */
	private static void processHistoryTokenNow(final String token) {
		GwtClientHelper.executeCommand(new GetHistoryInfoCmd(token), new AsyncCallback<VibeRpcResponse>() {
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
	 * Asynchronously pushes a URL based HistoryInfo on the user's
	 * history stack.
	 * 
	 * @param url
	 * @param instigator
	 */
	public static void pushHistoryUrlInfoAsync(final String url, final Instigator instigator) {
		GwtTeaming.fireEventAsync(new GetSidebarCollectionEvent(new CollectionCallback() {
			@Override
			public void collection(final CollectionType selectedMastheadCollection) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						pushHistoryUrlInfoNow(selectedMastheadCollection, url, instigator);
					}
				});
			}
		}));
	}
	
	/*
	 * Synchronously pushes a URL based HistoryInfo on the user's
	 * history stack.
	 */
	private static void pushHistoryUrlInfoNow(final CollectionType selectedMastheadCollection, final String url, final Instigator instigator) {
		PushHistoryInfoCmd phiCmd = new PushHistoryInfoCmd(selectedMastheadCollection, url, instigator);
		GwtClientHelper.executeCommand(phiCmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {/* Ignored. */}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				String token = ((StringRpcResponseData) response.getResponseData()).getStringValue();
				if (GwtClientHelper.hasString(token)) {
					History.newItem(
						(HISTORY_MARKER + token),	// History token.
						false);						// false -> Don't fire a change event for this item.
				}
			}
		});
	}

	/**
	 * Initializes browser history handling.
	 */
	public static void setupHistory() {
		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				try {
					// If we can find the history token...
					String historyToken = event.getValue();
					if (historyToken.substring(0, HISTORY_MARKER_LENGTH).equals(HISTORY_MARKER)) {
						String token = historyToken.substring(HISTORY_MARKER_LENGTH);
						if (GwtClientHelper.hasString(token)) {
							// ...process it...
							processHistoryTokenAsync(token);
							return;
						}
					}
				}
				catch (Exception e) {/* Ignored. */}
				
				// ...otherwise, simply force the content to refresh.
				FullUIReloadEvent.fireOneAsync();
			}
		});
	}
}
