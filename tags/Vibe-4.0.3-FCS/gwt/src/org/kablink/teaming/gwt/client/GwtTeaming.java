/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client;

import org.kablink.teaming.gwt.client.admin.ExtensionsConfig;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.GwtMainPage.GwtMainPageClient;
import org.kablink.teaming.gwt.client.lpe.LandingPageEditor;
import org.kablink.teaming.gwt.client.lpe.LandingPageEditor.LandingPageEditorClient;
import org.kablink.teaming.gwt.client.profile.widgets.GwtProfilePage;
import org.kablink.teaming.gwt.client.profile.widgets.UserStatusControl;
import org.kablink.teaming.gwt.client.profile.widgets.UserStatusControl.UserStatusControlClient;
import org.kablink.teaming.gwt.client.service.GetGwtRpcServiceCallback;
import org.kablink.teaming.gwt.client.service.GwtRpcService;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.tasklisting.TaskListing;
import org.kablink.teaming.gwt.client.tasklisting.TaskListing.TaskListingClient;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HistoryHelper;
import org.kablink.teaming.gwt.client.util.HistoryInfo;
import org.kablink.teaming.gwt.client.util.HistoryInfoCallback;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.HasRpcToken;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.XsrfToken;
import com.google.gwt.user.client.rpc.XsrfTokenService;
import com.google.gwt.user.client.rpc.XsrfTokenServiceAsync;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author drfoster@novell.com
 */
public class GwtTeaming implements EntryPoint {
	private static final CommonImageBundle					m_ldapBrowserImageBundle	= GWT.create(CommonImageBundle.class                 );
	private static final GwtTeamingMessages					m_stringMessages			= GWT.create(GwtTeamingMessages.class                );
	private static final GwtTeamingCloudFoldersImageBundle	m_cloudFoldersImageBundle	= GWT.create(GwtTeamingCloudFoldersImageBundle.class );
	private static final GwtTeamingDataTableImageBundle		m_dataTableImageBundle		= GWT.create(GwtTeamingDataTableImageBundle.class    );
	private static final GwtTeamingFilrImageBundle			m_filrImageBundle			= GWT.create(GwtTeamingFilrImageBundle.class         );
	private static final GwtTeamingImageBundle				m_imageBundle				= GWT.create(GwtTeamingImageBundle.class             );
	private static final GwtTeamingMainMenuImageBundle		m_mainMenuImageBundle		= GWT.create(GwtTeamingMainMenuImageBundle.class     );
	private static final GwtTeamingTaskListingImageBundle	m_taskListingImageBundle	= GWT.create(GwtTeamingTaskListingImageBundle.class  );
	private static final GwtTeamingWorkspaceTreeImageBundle	m_wsTreeImageBundle			= GWT.create(GwtTeamingWorkspaceTreeImageBundle.class);
	private static final SimpleEventBus 					m_eventBus 					= GWT.create(TeamingEventBus.class                    );
	
	private static GwtMainPage	m_mainPage;							// The application's main page.	
	private static HistoryInfo	m_browserReloadInfo;				// History information for reloading the browser page.
	public  static RequestInfo	m_requestInfo = jsGetRequestInfo();	// The current RequestInfo block loaded by the JSP.

	// The following are used to interact with the server in obtaining
	// an XsrfToken to use for GWT RPC requests and for executing the
	// GWT RPC commands themselves.
	private static final String					GWT_RPC_PATH				= "/ssf/gwt/";
	private static final String					GWT_RPC_ENTRY_POINT			= "gwtTeaming.rpc";
	private static final String					GWT_RPC_ENTRY_POINT_PATH	= (GWT_RPC_PATH + GWT_RPC_ENTRY_POINT);
	private static final XsrfTokenServiceAsync	m_xsrfTokenService			= ((XsrfTokenServiceAsync) GWT.create(XsrfTokenService.class));
	static {
		((ServiceDefTarget) m_xsrfTokenService).setServiceEntryPoint(GWT_RPC_ENTRY_POINT_PATH);
	}
	private static GwtRpcServiceAsync	m_rpcService;
	private static XsrfToken			m_xsrfToken;

	/*
	 * Returns the XSRF token to use for GWT RPC calls via the given
	 * AsyncCallback.
	 */
	private static void getXsrfToken(final AsyncCallback<XsrfToken> callback) {
		// Do we have an XsrfToken cached yet?
		if (null == m_xsrfToken) {
			// No!  Request one now.
			m_xsrfTokenService.getNewXsrfToken(new AsyncCallback<XsrfToken>() {
				@Override
				public void onFailure(Throwable caught) {
					GwtClientHelper.handleGwtRPCFailure(
						caught,
						getMessages().rpcFailure_GetXsrfToken());
				}

				@Override
				public void onSuccess(final XsrfToken token) {
					// We have the token!  Cache it and return it to
					// the caller.
					m_xsrfToken = token;
					callback.onSuccess(m_xsrfToken);
				}
			});
		}
		
		else {
			// Yes, we have an XsrfToken cached!  Simply return it.
			callback.onSuccess(m_xsrfToken);
		}
	}
	
	/**
	 * Returns the object that is used to retrieve Cloud Folder images.
	 * 
	 * @return
	 */
	public static CommonImageBundle getLdapBrowserImageBundle() {
		return m_ldapBrowserImageBundle;
	}
	
	
	/**
	 * Returns the object that is used to retrieve Cloud Folder images.
	 * 
	 * @return
	 */
	public static GwtTeamingCloudFoldersImageBundle getCloudFoldersImageBundle() {
		return m_cloudFoldersImageBundle;
	}
	
	
	/**
	 * Returns the object that is used to retrieve data table images.
	 * 
	 * @return
	 */
	public static GwtTeamingDataTableImageBundle getDataTableImageBundle() {
		return m_dataTableImageBundle;
	}
	
	
	/**
	 * Returns the object that is used to retrieve Filr images.
	 * 
	 * @return
	 */
	public static GwtTeamingFilrImageBundle getFilrImageBundle() {
		return m_filrImageBundle;
	}
	
	
	/**
	 * Returns the object that is used to retrieve images.
	 * 
	 * @return
	 */
	public static GwtTeamingImageBundle getImageBundle() {
		return m_imageBundle;
	}
	
	
	/**
	 * Returns the object that is used to retrieve Main Menu images.
	 * 
	 * @return
	 */
	public static GwtTeamingMainMenuImageBundle getMainMenuImageBundle() {
		return m_mainMenuImageBundle;
	}
	
	
	/**
	 * Returns the object that is used to retrieve Task Listing images.
	 * 
	 * @return
	 */
	public static GwtTeamingTaskListingImageBundle getTaskListingImageBundle() {
		return m_taskListingImageBundle;
	}
	
	
	/**
	 * Returns the object that is used to retrieve Workspace Tree
	 * images.
	 * 
	 * @return
	 */
	public static GwtTeamingWorkspaceTreeImageBundle getWorkspaceTreeImageBundle() {
		return m_wsTreeImageBundle;
	}
	
	
	/**
	 * Returns the GwtMainPage object.
	 * 
	 * @return
	 */
	public static GwtMainPage getMainPage() {
		return m_mainPage;
	}
	
	
	/**
	 * Returns the object that is used to retrieve strings.
	 * 
	 * @return
	 */
	public static GwtTeamingMessages getMessages() {
		return m_stringMessages;
	}
	
	
	/**
	 * Returns an object used to issue GWT RPC requests (i.e., AJAX)
	 * via a callback.
	 *
	 * @param callback
	 */
	public static void getRpcService(final GetGwtRpcServiceCallback callback) {
		// Have we cached the XSRF token used for GWT RPC requests yet?
		if (null == m_xsrfToken) {
			// No!  Can we get the XSRF token for GWT RPC requests now?
			getXsrfToken(new AsyncCallback<XsrfToken>() {
				@Override
				public void onFailure(Throwable caught) {
					// No!  getXsrfToken() will have told the user
					// about the error.  Simply tell the callback about
					// the failure.
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(XsrfToken xsrfToken) {
					// Yes, we got the XSRF token for GWT RPC requests!
					// Cache it, store it in the m_rpcService object
					// and pass that back through the callback.
					m_xsrfToken  = xsrfToken;
					m_rpcService = ((GwtRpcServiceAsync) GWT.create(GwtRpcService.class));
					((ServiceDefTarget) m_rpcService).setServiceEntryPoint(GWT_RPC_ENTRY_POINT_PATH);
					((HasRpcToken) m_rpcService).setRpcToken(m_xsrfToken);
					callback.onSuccess(m_rpcService);
				}
			});
		}
		
		else {
			// Yes, we've cached the XSRF token used for GWT RPC
			// requests!  Simply pass the m_rpcService object back
			// through the callback.
			callback.onSuccess(m_rpcService);
		}
	}

	
	/**
	 * Forces the GWT RPC service to reset itself.
	 */
	public static void resetRpcService() {
		m_rpcService = null;
		m_xsrfToken  = null;
	}
	

	/**
	 * Returns the HistoryInfo to use if the browser is being reloaded.
	 * 
	 * @return
	 */
	public static HistoryInfo getBrowserReloadInfo() {
		return m_browserReloadInfo;
	}
	
	
	/*
	 * Use JSNI to get the name of the parent window.
	 */
	private native String getParentWindowName() /*-{
		// Return the name of the parent window.
		if ($wnd.parent != null) {
			return $wnd.parent.name;
		}
		
		return "";
	}-*/;
	

	/*
	 * Use JSNI to see if we are running inside a landing page.
	 */
	private native boolean isInsideLandingPage() /*-{
		if ($wnd.parent != null) {
			return $wnd.parent.m_isLandingPage;
		}
			
		return false;
	}-*/;
	

	/*
	 * Loads the main page's split point.
	 */
	private void loadGwtMainPage() {
		// Load the main page's split point.
		GwtMainPage.createAsync(new GwtMainPageClient() {				
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(GwtMainPage mainPage) {
				m_mainPage = mainPage;
				
				RootLayoutPanel rlPanel = RootLayoutPanel.get();
				rlPanel.add(mainPage);
			}
		});
	}
	
	
	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {
		// Is the Vibe ui being loaded inside the content iframe?
		String parentWndName = getParentWindowName();
		if ((parentWndName != null) && parentWndName.equalsIgnoreCase("gwtContentIframe")) {
			// Yes!  Is the content <IFRAME> inside a landing page?
			if (isInsideLandingPage()) {
				RootPanel rootPanel = RootPanel.get();
				Label label = new Label(getMessages().vibeInsideLandingPage());
				rootPanel.add(label);
				return;
			}
		}
		
		// Are we in the the Landing Page Editor?
		final RootPanel lpRootPanel = RootPanel.get("gwtLandingPageEditorDiv");
		if (lpRootPanel != null) {
			// Yes!  Load the landing page editor's split point.
			LandingPageEditor.createAsync(new LandingPageEditorClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(LandingPageEditor lpe) {
					// Add the new Landing Page Editor to the page.
					lpRootPanel.add(lpe);
				}
			});
			
			return;
		}

		// Are we in the the Extensions page?
		final RootPanel extRootPanel = RootPanel.get("gwtExtensionsConfigDiv");
		if (extRootPanel != null) {
			// Yes!  Load the extensions page split point.
			ExtensionsConfig.createAsync(new ExtensionsConfig.ExtensionsConfigClient() {				
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(ExtensionsConfig ec) {
					// Add extensions configuration utility to the page.
					extRootPanel.add(ec);
				}
			});
			
			return;
		}
		
		// Are we in the main page?
		RootPanel mainRootPanel = RootPanel.get("gwtMainPageDiv");
		if (mainRootPanel != null) {
			// Yes!  Does the current browser Window.Location URL have
			// a history token?  (I.e., is the browser being reloaded?)
			String historyToken = HistoryHelper.getCurrentBrowserHistoryToken();
			if (null == historyToken) {
				// No!  Simply load the main page's split point.
				loadGwtMainPage();
			}
			
			else {
				// Yes, we have a history token!  We need to pull the
				// corresponding HistoryInfo from the server so we can
				// reload it with the construction of the main page's
				// content.
				HistoryHelper.getHistoryInfo(historyToken, new HistoryInfoCallback() {
					@Override
					public void historyInfo(HistoryInfo hi) {
						// Save the HistoryInfo and load the main
						// page's split point.  GwtMainPage will use it
						// to construct its content.
						m_browserReloadInfo = hi;
						loadGwtMainPage();
					}
				});
			}

			return;
		}
		
		
		// Are we loading the profile page?
		final RootPanel profileRootPanel = RootPanel.get("gwtProfileDiv");
		if (profileRootPanel != null) {
			// Yes!  Load the profile page's split point.
			GwtProfilePage.createAsync(
					new GwtProfilePage.GwtProfilePageClient() {				
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(GwtProfilePage profilePage) {
					profileRootPanel.add(profilePage);
				}
			});
					
			return;
		}

		
		// Are we loading the profile page?
		final RootPanel usRootPanel = RootPanel.get("gwtUserStatusDiv");
		if ((usRootPanel != null) && (!m_requestInfo.isLicenseFilr())) {
			// Yes!  Load the user status control's split point.
			UserStatusControl.createAsync(new UserStatusControlClient() {				
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(UserStatusControl usc) {
					usRootPanel.add(usc);
				}
			});
					
			return;
		}
		
		// Are we loading the task listing?
		final RootPanel taskRootPanel = RootPanel.get("gwtTasks");
		if (taskRootPanel != null) {
			// Yes!  Load the task listing's split point.
			TaskListing.createAsync(
					null,	// null -> No TaskFolderView -> Embedded JSP version.
					new TaskListingClient() {				
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in
					// asynchronous provider.
				}
				
				@Override
				public void onSuccess(TaskListing taskListing) {
					taskRootPanel.add(taskListing);
				}
			});
			
			return;
		}

	}


	/**
	 * ?
	 * 
	 * @return
	 */
	public static native String getContentPanelUrl() /*-{
		return window.top.m_contentPanelUrl;
	}-*/;
	

	/**
	 * ?
	 * 
	 * @param url
	 * 
	 * @return
	 */
	public static native String setContentPanelUrl(String url) /*-{
		window.top.m_contentPanelUrl = url;
	}-*/;
	

	/**
	 * This method will return true if the GWT Main page has already
	 * been loaded.  We want to prevent the GWT main page from being
	 * loaded into the Content Panel.  When the GWT main page loads it
	 * will create a hidden input with the id, 'gwtMainPageLoaded'.
	 * 
	 * @return
	 */
	public static native boolean isGwtMainPageLoaded() /*-{
		// Does the hidden input, "gwtMainPageLoaded", exist?
		var input = window.top.document.getElementById('gwtMainPageLoaded');
		if (input != null) {
			return true;
		}
			
		return false;
	}-*/;
	

	/**
	 * ?
	 * 
	 * @param url
	 * 
	 * @return
	 */
	public static native void passUrlToMainPage(String url) /*-{
		window.top.location.href = url;
	}-*/;
	

	/**
	 * Returns the event bus for Vibe OnPrem.
	 * 
	 * @return
	 */
	public static SimpleEventBus getEventBus() {
		return m_eventBus;
	}

	
	/**
	 * Synchronously fires a Vibe event on the event bus.
	 * 
	 * @param event
	 * @param source
	 */
	public static void fireEvent(VibeEventBase<?> event, Object source) {
		if (null == source)
		     m_eventBus.fireEvent(          event        );
		else m_eventBus.fireEventFromSource(event, source);
	}
	
	
	/**
	 * Synchronously fires a Vibe event on the event bus.
	 * 
	 * @param event
	 */
	public static void fireEvent(VibeEventBase<?> event) {
		fireEvent(event, null);
	}
	
	
	/**
	 * Asynchronously fires a Vibe event on the event bus.
	 * 
	 * @param event
	 * @param source
	 */
	public static void fireEventAsync(final VibeEventBase<?> event, final Object source) {
		// Use a scheduled command to fire the event.
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				fireEvent(event, source);
			}
		});
	}
	
	public static void fireEventAsync(VibeEventBase<?> event) {
		// Always use the initial form of the method.
		fireEventAsync(event, null);	// null -> No source.
	}
	
	
	/*
	 * Uses JSNI to grab the JavaScript object that holds the
	 * information about the request we are dealing with.
	 */
	private static native RequestInfo jsGetRequestInfo() /*-{
		// Return a reference to the JavaScript variable called, m_requestInfo.
		return $wnd.top.m_requestInfo;
	}-*/;
	
	public static boolean isEventHandled(Event.Type<?> eventKey){
		return ((TeamingEventBus)m_eventBus).isEventHandled(eventKey);
	}
}
