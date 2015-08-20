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
package org.kablink.teaming.gwt.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.RequestInfo;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.WindowTitleSetEvent;
import org.kablink.teaming.gwt.client.lpe.LandingPageEditor;
import org.kablink.teaming.gwt.client.profile.widgets.GwtProfilePage;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.MainPageInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmdType;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.service.GetGwtRpcServiceCallback;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.tasklisting.TaskListing;
import org.kablink.teaming.gwt.client.widgets.AlertDlg;
import org.kablink.teaming.gwt.client.widgets.AlertDlg.AlertDlgCallback;
import org.kablink.teaming.gwt.client.widgets.AlertDlg.AlertDlgClient;
import org.kablink.teaming.gwt.client.widgets.ConfirmCallback;
import org.kablink.teaming.gwt.client.widgets.MultiErrorAlertDlg;
import org.kablink.teaming.gwt.client.widgets.DlgBox.DlgButtonMode;
import org.kablink.teaming.gwt.client.widgets.MultiErrorAlertDlg.MultiErrorAlertDlgClient;
import org.kablink.teaming.gwt.client.widgets.WidgetStyles;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TeamingPopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

/**
 * Helper methods for the GWT UI client code.
 *
 * @author drfoster@novell.com
 */
public class GwtClientHelper {
	private final static boolean USE_JAVASCRIPT_ALERT	= false;	// Controls whether alerts are displayed using a JavaScript alert or our GWT AlertDlg.
	
	// Holds an instantiated AlertDlg when one is create.
	private static AlertDlg	m_alertDlg;
	
	// Holds an instantiated MultiErrorAlertDlg when one is create.
	private static MultiErrorAlertDlg	m_meaDlg;
	
	// String used to recognized an '&' formatted URL vs. a '/'
	// formatted permalink URL.
	private final static String AMPERSAND_FORMAT_MARKER = "a/do?";
	
	// Marker string used to recognize the format of a URL.
	private final static String PERMALINK_MARKER = "view_permalink";

	// Number of milliseconds in a day.
    public static final long DAY_IN_MS = (24L * 60L * 60L * 1000L);
    
	// Enumeration value used to interact with scroll bars on
	// something.
	public enum ScrollType {
		BOTH,
		HORIZONTAL,
		VERTICAL,
	}

	/**
	 * Inner class used to convert an Element to a UIObject.
	 */
	public static class ElementWrapper extends UIObject {
		/**
		 * Constructor method.
		 * 
		 * @param e
		 */
		public ElementWrapper(Element e) {
			setElement(e);	// setElement() is protected, so we have to subclass and call here
		}
	}
	
	/*
	 * Constructor method. 
	 */
	private GwtClientHelper() {
		// Inhibits this class from being instantiated.
	}

	/**
	 * Add some <br/>'s to a Widget.
	 * 
	 * @param w
	 * @param c
	 */
	public static void addBR(Widget w, int c) {
		if (0 < c) {
			StringBuffer brBuf = new StringBuffer();
			for (int i = 0; i < c; i += 1) {
				brBuf.append("<br/>");
			}
			String html = w.getElement().getInnerHTML();
			w.getElement().setInnerHTML(html + brBuf.toString());
		}
	}
	
	/**
	 * Adds a Long to a List<Long> if it's not already there.
	 * 
	 * @param lList
	 * @param l
	 */
	public static void addLongToListLongIfUnique(List<Long> lList, Long l) {
		// If the List<Long> doesn't contain the Long...
		if (!(lList.contains(l))) {
			// ...add it.
			lList.add(l);
		}
	}

	/**
	 * Displays an alert using a GWT alert dialog.
	 * 
	 * @param msg
	 * @param callback
	 */
	public static void alertViaDlg(final String msg, final AlertDlgCallback callback) {
		// Have we created an instance of an AlertDlg yet?
		if (null == m_alertDlg) {
			// No!  Create one now...
			AlertDlg.createAsync(new AlertDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to display.  Error display in
					// asynchronous provider.  However, if we were
					// given a callback interface...
					if (null != callback) {
						// ...tell the caller the dialog was closed.
						callback.closed();
					}
				}
				
				@Override
				public void onSuccess(AlertDlg aDlg) {
					// ...save it and use it to display the message.
					m_alertDlg = aDlg;
					alertViaDlg(msg, callback);
				}
			});
		}
		
		else {
			// Yes, we've created an instance of an AlertDlg!  Use it
			// to display the message.
			deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					AlertDlg.initAndShow(m_alertDlg, msg, callback);
				}
			});
		}
	}
	
	public static void alertViaDlg(final String msg) {
		// Always use the initial form of the method.
		alertViaDlg(msg, null);
	}
	
	/**
	 * Displays an alert using a GWT alert dialog.
	 * 
	 * @param
	 */
	public static void alertViaDlg(final Panel contentPanel, final AlertDlgCallback callback) {
		// Have we created an instance of an AlertDlg yet?
		if (null == m_alertDlg) {
			// No!  Create one now...
			AlertDlg.createAsync(new AlertDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to display.  Error display in
					// asynchronous provider.  However, if we were
					// given a callback interface...
					if (null != callback) {
						// ...tell the caller the dialog was closed.
						callback.closed();
					}
				}
				
				@Override
				public void onSuccess(AlertDlg aDlg) {
					// ...save it and use it to display the message.
					m_alertDlg = aDlg;
					alertViaDlg(contentPanel, callback);
				}
			});
		}
		
		else {
			// Yes, we've created an instance of an AlertDlg!  Use it
			// to display the message.
			deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					AlertDlg.initAndShow(m_alertDlg, contentPanel, callback);
				}
			});
		}
	}
	
	public static void alertViaDlg(final Panel contentPanel) {
		// Always use the previous form of the method.
		alertViaDlg(contentPanel, null);
	}
	
	/**
	 * Appends a <br /> to the HTML of a Widget.
	 * 
	 * @param w
	 */
	public static void appendBR(Widget w) {
		Element wE = w.getElement();
		String html = wE.getInnerHTML();
		wE.setInnerHTML(html + "<br />");
	}
	
	/**
	 * Appends a parameter to to a URL.
	 * 
	 * @param urlString
	 * @param pName
	 * @param pValue
	 * 
	 * @return
	 */
	public static String appendUrlParam(String urlString, String pName, String pValue) {
		String param;
		boolean useAmpersand = (0 < urlString.indexOf(AMPERSAND_FORMAT_MARKER));
		if (useAmpersand)
			 param = ("&" + pName + "=" + pValue);
		else param = ("/" + pName + "/" + pValue);
		if (0 > urlString.indexOf(param)) {
			urlString += param;
		}
		return urlString;
	}

	/**
	 * Determine if the two string are equal
	 * 
	 * @param s1
	 * @param s2
	 * 
	 * @return
	 */
	public static boolean areStringsEqual(String s1, String s2) {
		if ((s1 != null) && (s2 == null)) {
			return false;
		}
		
		if ((s1 == null) && (s2 != null)) {
			return false;
		}
		
		if ((s1 != null) && (!(s1.equalsIgnoreCase(s2)))) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Returns the boolean value stored in a string.
	 * 
	 * @param s
	 * @param def
	 * 
	 * @return
	 */
	public static boolean bFromS(String s, boolean def) {
		return (hasString(s) ? Boolean.parseBoolean(s) : def);
	}
	
	public static boolean bFromS(String s) {
		return bFromS(s, false);
	}

	/**
	 * Returns true if the browser supports NPAPI's and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean browserSupportsNPAPI() {
		// Can we access GwtMainPage?
		GwtMainPage mainPage = GwtTeaming.getMainPage();
		if (null != mainPage) {
			// Yes!  Can we get it's information object?
			MainPageInfoRpcResponseData mpData = mainPage.getMainPageInfo();
			if (null != mpData) {
				// Yes!  That contains the NPAPI flag.
				return mpData.browserSupportsNPAPI();
			}
		}
		
		// If we get here, we just assume NPAPI support.
		return true;
	}
	
	/**
	 * Returns a base Anchor widget.
	 * 
	 * @param styles
	 * 
	 * @return
	 */
	public static Anchor buildAnchor(List<String> styles) {
		Anchor reply = new Anchor();
		for (String style:  styles) {
			reply.addStyleName(style);
		}
		return reply;
	}
	
	public static Anchor buildAnchor(String style) {
		List<String> styles = new ArrayList<String>();
		styles.add(style);
		if (!(style.equals("cursorPointer"))) {
			styles.add("cursorPointer");
		}
		return buildAnchor(styles);
	}
	
	public static Anchor buildAnchor() {
		return buildAnchor("cursorPointer");
	}

	/**
	 * Returns a base Image widget.
	 * 
	 * @param res
	 * @param title
	 * 
	 * @return
	 */
	public static Image buildImage(ImageResource res, String title) {
		Image reply = new Image();
		if (null != res) {
			reply.setResource(res);
		}
		buildImageImpl(reply, title);
		return reply;
	}
	
	public static Image buildImage(ImageResource res) {
		return buildImage(res, null);
	}
	
	/**
	 * Returns a base Image widget.
	 * 
	 * @param resUri
	 * @param title
	 * 
	 * @return
	 */
	public static Image buildImage(SafeUri resUri, String title) {
		Image reply = new Image();
		if (null != resUri) {
			reply.setUrl(resUri);
		}
		buildImageImpl(reply, title);
		return reply;
	}
	
	public static Image buildImage(SafeUri resUri) {
		return buildImage(resUri, null);
	}
	
	/**
	 * Returns a base Image widget.
	 * 
	 * @param resUrl
	 * @param title
	 * 
	 * @return
	 */
	public static Image buildImage(String resUrl, String title) {
		Image reply = new Image();
		if (hasString(resUrl)) {
			reply.setUrl(resUrl);
		}
		buildImageImpl(reply, title);
		return reply;
	}
	
	public static Image buildImage(String resUrl) {
		return buildImage(resUrl, null);
	}
	
	/*
	 * Returns a base Image widget.
	 */
	private static void buildImageImpl(Image img, String title) {
		img.getElement().setAttribute("align", "absmiddle");
		if (hasString(title)) {
			img.setTitle(title);
		}
	}

	/**
	 * Constructs and returns a BinderInfo object that represents the
	 * current user's My Files collection.
	 * 
	 * @return
	 */
	public static BinderInfo buildMyFilesBinderInfo() {
		BinderInfo reply = new BinderInfo();
		reply.setBinderType(BinderType.COLLECTION);
		reply.setCollectionType(CollectionType.MY_FILES);
		reply.setBinderId(getRequestInfo().getCurrentUserWorkspaceId());
		reply.setBinderTitle(GwtTeaming.getMessages().myFiles());
		return reply;
	}
	
	/**
	 * If we're in debug UI mode, displays an alert.
	 * 
	 * @param msg
	 */
	public static void debugAlert(String msg) {
		if (isDebugUI()) {
			deferredAlert(msg);
		}
	}
	
	/**
	 * If we're in debug UI mode, assert that a condition has been met
	 * and display an alert if it hasn't.
	 * 
	 * @param condition
	 * @param msg
	 */
	public static void debugAssert(boolean condition, String msg) {
		if (isDebugUI()) {
			if (!condition) {
				deferredAlert(msg);
			}
		}
	}
	
	/**
	 * Displays a messages in an 'deferred' alert box.
	 * 
	 * @param msg
	 * @param delay
	 */
	public static void deferredAlert(final String msg, int delay, final AlertDlgCallback callback) {
		// Were we given a message to display?
		if (hasString(msg)) {
			// Yes!  Display it when appropriate.
			deferCommand(
				new ScheduledCommand() {
					@Override
					public void execute() {
						deferredAlertImpl(msg, callback);
					}
				},
				delay);
		}
	}
	public static void deferredAlert(final String msg, int delay) {
		// Always use the initial form of the method.
		deferredAlert(msg, 0, null);
	}
	
	public static void deferredAlert(final String msg, AlertDlgCallback callback) {
		// Always use the initial form of the method.
		deferredAlert(msg, 0, callback);
	}

	public static void deferredAlert(final String msg) {
		// Always use the initial form of the method.
		deferredAlert(msg, 0, null);
	}
	
	/*
	 * Implementation method for deferredAlert().
	 */
	private static void deferredAlertImpl(final String msg, AlertDlgCallback callback) {
		// If we're supposed to use a JavaScript.alert()...
		if (USE_JAVASCRIPT_ALERT) {
			// ...use it and bail...
			Window.alert(msg);
			return;
		}

		// ...otherwise use the GWT dialog for it.
		alertViaDlg(msg, callback);
	}

	/**
	 * Displays a message to the user regarding possibly multiple
	 * errors.
	 * 
	 * @param baseError
	 * @param multiErrors
	 * @param delay
	 * @param confirmCallback
	 */
	public static void displayMultipleErrors(final String baseError, final List<ErrorInfo> multiErrors, final int delay, final ConfirmCallback confirmCallback, final DlgButtonMode confirmButtons) {
		if (null == m_meaDlg) {
			MultiErrorAlertDlg.createAsync(new MultiErrorAlertDlgClient() {
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
				
				@Override
				public void onSuccess(MultiErrorAlertDlg meaDlg) {
					m_meaDlg = meaDlg;
					displayMultipleErrorsAsync(baseError, multiErrors, delay, confirmCallback, confirmButtons);
				}
			});
		}
		
		else {
			displayMultipleErrorsAsync(baseError, multiErrors, delay, confirmCallback, confirmButtons);
		}
			
	}
	
	public static void displayMultipleErrors(final String baseError, final List<ErrorInfo> multiErrors, final int delay) {
		// Always use the initial form of the method.
		displayMultipleErrors(baseError, multiErrors, delay, null, null);
	}
	
	public static void displayMultipleErrors(String baseError, List<ErrorInfo> multiErrors, ConfirmCallback confirmCallback, DlgButtonMode confirmButtons) {
		// Always use the initial form of the method.
		displayMultipleErrors(baseError, multiErrors, 0, confirmCallback, confirmButtons);
	}

	public static void displayMultipleErrors(String baseError, List<ErrorInfo> multiErrors) {
		// Always use the initial form of the method.
		displayMultipleErrors(baseError, multiErrors, 0, null, null);
	}

	/*
	 * Asynchronously displays the list of multiple error messages.
	 */
	private static void displayMultipleErrorsAsync(final String baseError, final List<ErrorInfo> multiErrors, final int delay, final ConfirmCallback confirmCallback, final DlgButtonMode confirmButtons) {
		// Do we have anything to display?
		if (hasString(baseError) && hasItems(multiErrors)) {
			// Yes!  If we don't have a specific amount of time to
			// delay...
			deferCommand(
				new ScheduledCommand() {
					@Override
					public void execute() {
						displayMultipleErrorsNow(baseError, multiErrors, confirmCallback, confirmButtons);
					}
				},
				delay);
		}
	}

	/*
	 * Synchronously displays the list of multiple error messages.
	 */
	private static void displayMultipleErrorsNow(String baseError, List<ErrorInfo> multiErrors, ConfirmCallback confirmCallback, DlgButtonMode confirmButtons) {
		MultiErrorAlertDlg.initAndShow(m_meaDlg, baseError, multiErrors, confirmCallback, confirmButtons);
	}

	/**
	 * Execute the given command via GWT's RPC mechanism.
	 * 
	 * @param cmd
	 * @param callback
	 */
	public static void executeCommand(VibeRpcCmd cmd, AsyncCallback<VibeRpcResponse> callback) {
		// Does this command have a 'run as admin' setting?
		Boolean runAsAdmin = cmd.isRunAsAdmin();
		if (null == runAsAdmin) {
			// No!  Are we in the admin console?
			GwtMainPage mp = GwtTeaming.getMainPage();
			if ((null != mp) && mp.isAdminActive()) {
				// Yes!  Since we are in the admin console, run the
				// command as admin.  However, we never run the
				// following commands as admin.
				switch (VibeRpcCmdType.getEnum(cmd.getCmdType())) {
				case CHANGE_PASSWORD:
				case DUMP_HISTORY_INFO:
				case GET_DISK_USAGE_INFO:
				case GET_HORIZONTAL_TREE:
				case GET_PERSONAL_PREFERENCES:
				case GET_PASSWORD_EXPIRATION:
				case GET_SITE_ADMIN_URL:
				case GET_SYSTEM_BINDER_PERMALINK:
				case GET_UPGRADE_INFO:
				case GET_VERTICAL_ACTIVITY_STREAMS_TREE:
				case GET_VERTICAL_TREE:
				case GET_VIEW_INFO:
					runAsAdmin = Boolean.FALSE;
					break;
					
				default:
					runAsAdmin = Boolean.TRUE;
					break;
				}
				cmd.setRunAsAdmin(runAsAdmin);
			}		
		}

		// Finally, execute the command.
		executeCommand(cmd, HttpRequestInfo.createHttpRequestInfo(), callback);
	}	

	/**
	 * Execute the given command via GWT's RPC mechanism.
	 * 
	 * @param cmd
	 * @param httpRequestInfo
	 * @param callback
	 */
	public static void executeCommand(final VibeRpcCmd cmd, final HttpRequestInfo httpRequestInfo, final AsyncCallback<VibeRpcResponse> callback) {
		// Can we get the GWT RPC service to execute the command?
		GwtTeaming.getRpcService(new GetGwtRpcServiceCallback() {
			@Override
			public void onFailure(Throwable caught) {
				// No!  The user will have been told about the error.
				// Simply tell the callback.
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(GwtRpcServiceAsync rpc) {
				// Yes, we have the GWT RPC service to execute the
				// command!  Can we execute it.
				rpc.executeCommand(httpRequestInfo, cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable caught) {
						// No!  Did we get an RpcTokenException on the initial
						// try of the RPC request?
						if ((!(httpRequestInfo.isRetry()))&& (caught instanceof RpcTokenException)) {
							// Yes!  Reset the RPC service and try it
							// again!  This may happen, for instance,
							// when dealing with a session timeout on
							// the server.
							GwtTeaming.resetRpcService();
							httpRequestInfo.setRetry(true);
							deferCommand(new ScheduledCommand() {
								@Override
								public void execute() {
									debugAlert("GwtClientHelper.executeCommand( Retrying Command ):  " + VibeRpcCmdType.getEnum(cmd.getCmdType()).name());
									executeCommand(cmd, httpRequestInfo, callback);
								}
							});
						}
						
						else {
							// No, this isn't the initial try or it's
							// not an RpcTokenException!  Simply tell
							// the callback about the error.
							callback.onFailure(caught);
						}
					}

					@Override
					public void onSuccess(VibeRpcResponse result) {
						// Yes!  Pass the result back to the caller.
						callback.onSuccess(result);
					}
				});
			}
		});
	}	

	/**
	 * Returns the ImageResource to use for a group type <IMG>.
	 * 
	 * @param groupType
	 * 
	 * @return
	 */
	public static ImageResource getGroupTypeImage(GroupType groupType) {
		GwtTeamingDataTableImageBundle images = GwtTeaming.getDataTableImageBundle();
		ImageResource reply;
		if (null == groupType) {
			reply = images.groupType_Unknown();
		}
		else {
			if (groupType.isAdmin()) {
				switch (groupType.getGroupClass()) {
				case INTERNAL_LDAP:    reply = images.groupType_LDAPAdmin();   break;
				case INTERNAL_SYSTEM:  reply = images.groupType_SystemAdmin(); break;
				case INTERNAL_LOCAL:   reply = images.groupType_LocalAdmin();  break;
				default:               reply = images.groupType_Unknown();     break;
				}
			}
			else {
				switch (groupType.getGroupClass()) {
				case INTERNAL_LDAP:    reply = images.groupType_LDAP();        break;
				case INTERNAL_SYSTEM:  reply = images.groupType_System();      break;
				case INTERNAL_LOCAL:   reply = images.groupType_Local();       break;
				default:               reply = images.groupType_Unknown();     break;
				}
			}
		}
		return reply;		
	}
	

	/**
	 * Returns the path to Vibe's images.
	 * 
	 * @return
	 */
	public static String getImagesPath() {
		return getRequestInfo().getImagesPath();
	}

	/**
	 * Returns the license type that we're currently running under.
	 * 
	 * @return
	 */
	public static LicenseType getLicenseType() {
		LicenseType reply = LicenseType.NO_LICENSE;
		RequestInfo ri = getRequestInfo();
		if (null != ri) {
			if      (ri.isLicenseFilr())        reply = LicenseType.FILR;
			else if (ri.isLicenseFilrAndVibe()) reply = LicenseType.FILR_AND_VIBE;
			else if (ri.isLicenseVibe())        reply = LicenseType.VIBE;
		}
		return reply;
	}

	/**
	 * Returns the current product name, Filr or Vibe.
	 * 
	 * @return
	 */
	public static String getProductName() {
		GwtTeamingMessages messages = GwtTeaming.getMessages();
		String reply =
			(GwtClientHelper.isLicenseFilr() ?
				messages.productFilr() :
				messages.productVibe());
		return reply;
	}
	
	/**
	 * Returns the RequestInfo object from whatever component we're
	 * running as.
	 *
	 * Returns null if a RequestInfo cannot be determined.
	 * 
	 * @return
	 */
	public static RequestInfo getRequestInfo() {
		RequestInfo reply;
		if      (null != GwtMainPage.m_requestInfo)         reply = GwtMainPage.m_requestInfo;
		else if (null != GwtProfilePage.profileRequestInfo) reply = GwtProfilePage.profileRequestInfo;
		else if (null != TaskListing.m_requestInfo)         reply = TaskListing.m_requestInfo;
		else if (null != LandingPageEditor.m_requestInfo)   reply = LandingPageEditor.m_requestInfo;
		else                                                reply = null;
		if (null == reply) {
			// Virtually NOTHING with the GWT code will work without a
			// RequestInfo object.  Tell the user about the problem.
			//
			// Some potential causes of this problem:
			// 1. Missing check in the if/else-if above.
			// 2. The GWT UI component failed to load. 
			deferredAlert(GwtTeaming.getMessages().missingRequestInfo());
		}
		return reply;
	}

	/**
	 * Returns the DateTimeFormat to use to format a date in short form
	 * based on the user's locale.
	 * 
	 * @return
	 */
	public static DateTimeFormat getShortDateFormat() {
		RequestInfo ri = getRequestInfo();
		String pattern = ((null == ri) ? null : getRequestInfo().getShortDatePattern());
		DateTimeFormat reply;
		if (hasString(pattern))
		     reply = DateTimeFormat.getFormat(pattern);
		else reply = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
		return reply;
	}
	
	/**
	 * Returns the DateTimeFormat to use to format a date in short form
	 * based on the user's locale.
	 * 
	 * @return
	 */
	public static DateTimeFormat getShortTimeFormat() {
		RequestInfo ri = getRequestInfo();
		String pattern = ((null == ri) ? null : getRequestInfo().getShortTimePattern());
		DateTimeFormat reply;
		if (hasString(pattern))
		     reply = DateTimeFormat.getFormat(pattern);
		else reply = DateTimeFormat.getFormat(PredefinedFormat.TIME_SHORT);
		return reply;
	}

	/*
	 * Returns the detailed information from a Throwable.
	 */
	private static String getThrowableDetails(Throwable t) {
		String reply = t.getLocalizedMessage();
		if (!(hasString(reply))) {
			reply = t.getMessage();
			if (!(hasString(reply))) {
				reply = t.toString();
			}
		}
		return reply;
	}
	
	/**
     * Returns the client's timezone offset.
	 * 
	 * Note:
	 *    We use deprecated APIs here since GWT's client side has no
	 *    GregorianCalendar equivalent.  This is the only way to
	 *    manipulate a date and time.
     * 
     * @param date
     * 
     * @return
     */
	@SuppressWarnings("deprecation")
	public static int getTimeZoneOffset(Date date) {
		final int tzo = date.getTimezoneOffset();
		return tzo;
	}
	
	/**
	 * Returns the client's timezone offset in milliseconds.
	 * 
	 * @param date
	 * 
	 * @return
	 */
	public static long getTimeZoneOffsetMillis(Date date) {
		return (((long) getTimeZoneOffset(date)) * 60L * 1000L);
	}

	/**
	 * Returns 12:00 AM tomorrow morning.
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Date getTomorrow() {
		Date reply = new Date();
		reply.setHours( 24);
		reply.setMinutes(0);
		reply.setSeconds(0);
		return reply;
	}
	
	/**
	 * Converts an Element to a UIObject.
	 * 
	 * @param e
	 * 
	 * @return
	 */
	public static UIObject getUIObjectFromElement(Element e) {
		return new ElementWrapper(e);
	}

	/**
	 * Returns the HTML representation of a widget.
	 * 
	 * @param w
	 * 
	 * @return
	 */
	public static String getWidgetHTML(Widget w) {
		FlowPanel html = new FlowPanel();
		html.add(w);
		return html.getElement().getInnerHTML();
	}
	
    /**
     * Converts a time in GMT to a GWT Date object which is in the
     * timezone of the browser.
     * 
     * @param time
     * 
     * @return
     */
    public static final Date gmtToLocal(Date date) {       
        // Add the timezone offset.
        return new Date(date.getTime() + getTimeZoneOffsetMillis(date));
    }

    /**
     * Returns true if the given BinderInfo represents a user's Home
     * folder where their Home Folder is used as their My Files
     * collection repository.  Returns false otherwise.
     * 
     * @param bi
     * 
     * @return
     */
    public static boolean isBinderInfoMyFilesHome(BinderInfo bi) {
    	return (bi.isFolderHome() && GwtTeaming.getMainPage().getMainPageInfo().isUseHomeAsMyFiles());
    }
    
    /**
     * Converts a GWT Date in the timezone of the browser to a time in
     * GMT.
     * 
     * @param date
     * 
     * @return
     */
    public static final Date localToGmt(Date date) {
        // Remove the timezone offset.        
        return new Date(date.getTime() - getTimeZoneOffsetMillis(date));
    }
   
	/**
	 * Applies patches to a message string.
	 * 
	 * @param msg
	 * @param patches
	 * 
	 * @return
	 */
	public static String patchMessage(String msg, String[] patches) {
		int count = ((null == patches) ? 0 : patches.length);
    	for (int i = 0; i < count; i += 1) {
            String delimiter = ("[" + i + "]");
            String patch = patches[i];
            while(msg.contains(delimiter)) {
                msg = msg.replace(delimiter, patch);
            }
        }
		return msg;
	}
	
	public static String patchMessage(String msg, String patch) {
		// Always use the initial form of the method.
		return patchMessage(msg, new String[]{patch});
	}
	
	/**
	 * Handles Throwable's received by GWT RPC onFailure() methods.
	 * 
	 * Notes:
	 * 1) Passing no error message string here allows for the proper
	 *    exception handling to occur but will NOT display an error to
	 *    the user.
	 * 2) On 20100803 I (Dennis) discussed this with Jay and we agreed
	 *    that in the Durango release, we'll only display errors here
	 *    for those exceptions that we really know something about.
	 *    The others will be ignored.
	 * 
	 * @param t
	 * @param errorMessage
	 * @param patches
	 */
	public static void handleGwtRPCFailure(Throwable t, String errorMessage, String[] patches) {
		boolean displayAlert   = false;
		boolean ensureMsgPatch = false;
		if (null != t) {
			GwtTeamingMessages messages = GwtTeaming.getMessages();
			String cause;
			if (t instanceof GwtTeamingException) {
				switch (((GwtTeamingException) t).getExceptionType()) {
				case ACCESS_CONTROL_EXCEPTION:
					displayAlert = true;
					cause = patchMessage(messages.rpcFailure_AccessToFolderDenied(), patches);
					break;
					
				case APPLICATION_EXISTS_EXCEPTION:
					displayAlert = true;
					cause = patchMessage(messages.rpcFailure_CreateApplicationAlreadyExists(), patches);
					break;
					
				case APPLICATION_GROUP_EXISTS_EXCEPTION:
					displayAlert = true;
					cause = patchMessage(messages.rpcFailure_CreateApplicationGroupAlreadyExists(), patches);
					break;
					
				case FAVORITES_LIMIT_EXCEEDED:
					errorMessage = messages.rpcFailure_AddFavoriteLimitExceeded();
					cause = "";
					displayAlert = true;
					break;
					
				case GROUP_ALREADY_EXISTS:
					displayAlert = true;
					cause = patchMessage(messages.rpcFailure_CreateGroupAlreadyExists(), patches);
					break;
					
				case LDAP_GUID_NOT_CONFIGURED:
					displayAlert = true;
					cause = messages.rpcFailure_LdapGuidNotConfigured();
					break;
					
				case NO_BINDER_BY_THE_ID_EXCEPTION:
					displayAlert = true;
					cause = patchMessage(messages.rpcFailure_FolderDoesNotExist(), patches);
					break;
					
				case USER_ALREADY_EXISTS:
					displayAlert = true;
					cause = patchMessage(messages.rpcFailure_CreateUserAlreadyExists(), patches);
					break;
					
				case USER_NOT_LOGGED_IN:
					cause = null;
					GwtTeaming.getMainPage().handleSessionExpired();
					break;
					
				default:
					cause = patchMessage(messages.rpcFailure_UnknownException(), patches);
					break;
				}
			}
			
			else if (t instanceof RpcTokenException) {
				// No matter what, we want to make sure this failure
				// gets displayed to the user.
				displayAlert   =
				ensureMsgPatch = true;
				cause = patchMessage(messages.rpcFailure_XsrfTokenFailure(), getThrowableDetails(t));
			}
			
			else {
				cause = getThrowableDetails(t);
			}
			
			if (!(hasString(cause))) {
				cause = messages.rpcFailure_UnknownCause();
			}
			patches = new String[]{cause};
		}
		
		if (hasString(errorMessage) && (displayAlert || isDebugUI())) {
			if (ensureMsgPatch && (0 > errorMessage.indexOf("[0]"))) {
				errorMessage += "  '[0]'.";
			}
			errorMessage = patchMessage(errorMessage, patches);
			deferredAlert(errorMessage);
		}
	}
	
	public static void handleGwtRPCFailure(Throwable t, String errorMessage, String patch) {
		// Always use the initial form of the method.
		handleGwtRPCFailure(t, errorMessage, new String[]{patch});
	}
	
	public static void handleGwtRPCFailure(Throwable t, String errorMessage, Long patch) {
		// Always use the initial form of the method.
		handleGwtRPCFailure(t, errorMessage, new String[]{String.valueOf(patch)});
	}
	
	public static void handleGwtRPCFailure(Throwable t, String errorMessage) {
		// Always use the initial form of the method.
		handleGwtRPCFailure(t, errorMessage, ((String[]) null));
	}
	
	public static void handleGwtRPCFailure(Throwable t) {
		// Always use the initial form of the method.
		handleGwtRPCFailure(t, null, ((String[]) null));
	}
	
	/**
	 * Returns true if a Collection has anything in it and false
	 * otherwise.
	 * 
	 * @param c
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean hasItems(Collection c) {
		return ((null != c) && (!(c.isEmpty())));
	}
	
	/**
	 * Returns true if a Map has anything in it and false otherwise.
	 * 
	 * @param m
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean hasItems(Map m) {
		return ((null != m) && (!(m.isEmpty())));
	}
	
	/**
	 * Returns true is s refers to a non null, non 0 length String and
	 * false otherwise.
	 * 
	 * @param s
	 * @return
	 */
	public static boolean hasString(String s) {
		return ((null != s) && (0 < s.length()));
	}

	/**
	 * Returns the integer value stored in a string.
	 * 
	 * @param s
	 * @param def
	 * 
	 * @return
	 */
	public static int iFromS(String s, int def) {
		return (hasString(s) ? Integer.parseInt(s) : def);
	}
	
	public static int iFromS(String s) {
		return iFromS(s, (-1));
	}

	/**
	 * Open a window with the URL that points to the appropriate help
	 * documentation.
	 * 
	 * @param helpData
	 */
	public static void invokeHelp(HelpData helpData) {
		if (null != helpData) {
			// Get the URL that points to the appropriate help
			// documentation.
			String url = helpData.getUrl();
			if (hasString(url)) {
				Window.open(url, "teaming_help_window", "resizeable,scrollbars");
			}
		}
	}	

	/**
	 * Invokes the simple profile dialog off an HTML Element.
	 * 
	 * @param htmlElement
	 * @param userId
	 * @param binderId
	 * @param userName
	 */
	public static native void invokeSimpleProfile(Element htmlElement, String userId, String binderId, String userName) /*-{
		$wnd.top.ss_invokeSimpleProfile(htmlElement, userId, binderId, userName);
	}-*/;

	/**
	 * Returns true if the given group is belongs to the "all external users" group.
	 * 
	 * @param groupId
	 * 
	 * @return
	 */
	public static boolean isAllExternalUsersGroup(String groupId) {
		if (groupId == null) {
			return false;
		}

		return groupId.equalsIgnoreCase(getRequestInfo().getAllExternalUsersGroupId());
	}
	
	/**
	 * Returns true if the given group is belongs to the "all internal users" group.
	 * 
	 * @param groupId
	 * 
	 * @return
	 */
	public static boolean isAllInternalUsersGroup(String groupId) {
		if (groupId == null) {
			return false;
		}
		
		return groupId.equalsIgnoreCase(getRequestInfo().getAllInternalUsersGroupId());
	}
	
	/**
	 * Returns true if the logged in user is built-in admin user and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static boolean isBuiltInAdmin() {
		return getRequestInfo().isBuiltInAdmin();
	}

	/**
	 * Returns true if Cloud Folders are enabled and false otherwise.
	 * 
	 * @return
	 */
	public static boolean isCloudFoldersEnabled() {
		RequestInfo ri = getRequestInfo();
		if (null == ri) {
			return false;
		}
		return ri.isCloudFoldersEnabled();
	}
	
	/**
	 * Returns true if the requested key is currently pressed and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isAltKeyDown()     {GwtMainPage mp = GwtTeaming.getMainPage(); return ((null != mp) && mp.isAltKeyDown());    }
	public static boolean isControlKeyDown() {GwtMainPage mp = GwtTeaming.getMainPage(); return ((null != mp) && mp.isControlKeyDown());}
	public static boolean isMetaKeyDown()    {GwtMainPage mp = GwtTeaming.getMainPage(); return ((null != mp) && mp.isMetaKeyDown());   }
	public static boolean isShiftKeyDown()   {GwtMainPage mp = GwtTeaming.getMainPage(); return ((null != mp) && mp.isShiftKeyDown());  }
	
	/**
	 * Returns true if the UI is in debug mode for the landing page
	 * 
	 * @return
	 */
	public static boolean isDebugLP()
	{
		RequestInfo ri = getRequestInfo();
		if (null == ri)
		{
			return false;
		}
		
		return ri.isDebugLP();
	}
	
	/**
	 * Returns true if the UI is in debug mode and false otherwise.
	 * 
	 * @return
	 */
	public static boolean isDebugUI() {
		RequestInfo ri = getRequestInfo();
		if (null == ri) {
			return false;
		}
		return ri.isDebugUI();
	}
	
	/**
	 * Returns true if the logged in user is an external user and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isExternalUser() {
		return getRequestInfo().isExternalUser();
	}

	/**
	 * Returns true if the given id is belongs to the "guest" user.
	 * 
	 * @param id
	 * 
	 * @return
	 */
	public static boolean isGuest(String id) {
		if (id == null) {
			return false;
		}

		return id.equalsIgnoreCase(getRequestInfo().getGuestId());
	}
	
	/**
	 * Returns true if the logged in user is the Guest user and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isGuestUser() {
		return getRequestInfo().isGuestUser();
	}

	/**
	 * Returns true if the current user is guest
	 * 
	 * @return
	 */
	public static boolean isCurrentUserGuest() {
		// Get the ID if the current user.
		String currentUserId = getRequestInfo().getUserId();
		return isGuest(currentUserId);
	}
	
	/**
	 * Return true if the key that was pressed is valid in a numeric field. 
	 *
	 * @param charCode
	 * @param keyCode
	 * 
	 * @return
	 */
	public static boolean isKeyValidForNumericField(char charCode, int keyCode) {
        if ((!(Character.isDigit(charCode)))     &&
        	 (keyCode != KeyCodes.KEY_TAB)       &&
        	 (keyCode != KeyCodes.KEY_BACKSPACE) &&
        	 (keyCode != KeyCodes.KEY_DELETE)    &&
        	 (keyCode != KeyCodes.KEY_ENTER)     &&
        	 (keyCode != KeyCodes.KEY_HOME)      &&
        	 (keyCode != KeyCodes.KEY_END)       &&
        	 (keyCode != KeyCodes.KEY_LEFT)      &&
        	 (keyCode != KeyCodes.KEY_UP)        &&
        	 (keyCode != KeyCodes.KEY_RIGHT)     &&
        	 (keyCode != KeyCodes.KEY_DOWN)) {
        	return false;
        }

        // On Chrome, the keyCode for '.' is the same as for KEY_DELETE.
        if (charCode == '.') {
        	return false;
        }
        
        return true;
	}
	
	/**
	 * Returns true if we're running with an expired license and false
	 * otherwise.
	 * 
	 * Note:  Kablink Vibe will NEVER have an expired license.
	 * 
	 * @return
	 */
	public static boolean isLicenseExpired() {
		RequestInfo ri = getRequestInfo(); 
		return ((null == ri) || ri.isLicenseExpired());
	}

	/**
	 * Returns true if we're running in Filr mode and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isLicenseFilr() {
		return getLicenseType().isFilr();
	}

	/**
	 * Returns true if we're running in Filr and Vibe mode and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isLicenseFilrAndVibe() {
		return getLicenseType().isFilrAndVibe();
	}

	/**
	 * Returns true if we're running in a mode with Filr enabled and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static boolean isLicenseFilrEnabled() {
		return getLicenseType().isFilrEnabled();
	}

	/**
	 * Returns true if we're running in Vibe mode and false otherwise.
	 * 
	 * @return
	 */
	public static boolean isLicenseVibe() {
		return getLicenseType().isVibe();
	}

	/**
	 * Returns true if we're running in Kablink mode and false otherwise.
	 * 
	 * @return
	 */
	public static boolean isLicenseKablink() {
		return !(getLicenseType().isLicensed());
	}

	/**
	 * Returns true if we're running with a valid license and false
	 * otherwise.
	 * 
	 * Note:  Kablink Vibe will ALWAYS have a valid license.
	 * 
	 * @return
	 */
	public static boolean isLicenseValid() {
		RequestInfo ri = getRequestInfo(); 
		return ((null != ri) && ri.isLicenseValid());
	}

	/**
	 * Returns true if we're running in a mode with Vibe enabled and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static boolean isLicenseVibeEnabled() {
		return getLicenseType().isVibeEnabled();
	}
	
	/**
	 * Returns true if the given keyCode is a navigation key.
	 * 
	 * @param keyCode
	 * 
	 * @return
	 */
	public static boolean isNavigationKey(int keyCode) {
		boolean result = false;
        if ((keyCode == KeyCodes.KEY_TAB)   ||
        	(keyCode == KeyCodes.KEY_HOME)  ||
        	(keyCode == KeyCodes.KEY_END)   ||
        	(keyCode == KeyCodes.KEY_LEFT)  ||
        	(keyCode == KeyCodes.KEY_UP)    ||
            (keyCode == KeyCodes.KEY_RIGHT) ||
            (keyCode == KeyCodes.KEY_DOWN)) {
        	result = true;
        }

        return result;
	}
	
	/**
	 * Returns true if password policy is enabled and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isPasswordPolicyEnabled() {
		RequestInfo ri = getRequestInfo();
		return ((null != ri) && ri.isPasswordPolicyEnabled());
	}

	/**
	 * Returns true if we should expose Filr features and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean showFilrFeatures() {
		RequestInfo ri = getRequestInfo();
		return ((null != ri) && ri.showFilrFeatures());
	}

	/**
	 * Returns true if we should expose Vibe features and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean showVibeFeatures() {
		RequestInfo ri = getRequestInfo();
		return ((null != ri) && ri.showVibeFeatures());
	}

	/*
	 * Returns true of the browser is running on the specified OS and
	 * false otherwise.
	 */
	private static boolean isOSImpl(String osCheck) {
		boolean reply;
		String platform = Navigator.getPlatform();
		reply = (hasString(platform) && hasString(osCheck));
		if (reply) {
			reply = ((-1) != platform.toLowerCase().indexOf(osCheck.toLowerCase()));
		}
		return reply;
	}
	
	/**
	 * Returns true of the browser is running on Linux and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isOSLinux() {
		return isOSImpl("linux");
	}
	
	/**
	 * Returns true of the browser is running on a Mac and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isOSMac() {
		return isOSImpl("mac");
	}
	
	/**
	 * Returns true of the browser is running on Windows and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static boolean isOSWindows() {
		return isOSImpl("windows");
	}
	
	/**
	 * Returns true if a URL is a permalink URL and false otherwise.
	 * 
	 * @param url
	 * @return
	 */
	public static boolean isPermalinkUrl(String url) {
		boolean reply = hasString(url);
		if (reply) {
			reply = (0 < url.indexOf(PERMALINK_MARKER));
		}
		return reply;
	}

	/**
	 * Returns true if the logged in user should see the 'Public'
	 * collection.
	 * 
	 * @return
	 */
	public static boolean isShowPublicCollection() {
		return getRequestInfo().isShowPublicCollection();
	}

	/**
	 * Returns true if the logged in user is a site administrator and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static boolean isSiteAdmin() {
		return getRequestInfo().isSiteAdmin();
	}

	/**
	 * Returns true if the browser supports the HTML5 file APIs and false
	 * otherwise.
	 */
	public static native boolean jsBrowserSupportsHtml5FileAPIs() /*-{
//!		alert("HTML5 support: $wnd.File: " + $wnd.File + ", $wnd.FileReader: " + $wnd.FileReader + ", $wnd.FileList: " + $wnd.FileList + ", $wnd.Blob: " + $wnd.Blob);
		if ($wnd.File && $wnd.FileReader && $wnd.FileList && $wnd.Blob) {
			return true;
		}
		else {
			return false;
		}
	}-*/;
	
	/**
	 * Appends an HTML element to the top document.
	 * 
	 * @param htmlElement
	 */
	public static native void jsAppendDocumentElement(Element htmlElement) /*-{
		$wnd.top.document.documentElement.appendChild(htmlElement);
	}-*/;

	/*
	 * Returns the URL to launch a search for the given tag.
	 */
	public static native String jsBuildTagSearchUrl(String tag) /*-{
		// Find the base tag search result URL...
		var searchUrl;
	   	                      try {searchUrl =             ss_tagSearchResultUrl;} catch(e) {searchUrl="";}
		if (searchUrl == "") {try {searchUrl = self.parent.ss_tagSearchResultUrl;} catch(e) {searchUrl="";}}
		if (searchUrl == "") {try {searchUrl = self.opener.ss_tagSearchResultUrl;} catch(e) {searchUrl="";}}
		if (searchUrl == "") {try {searchUrl =    $wnd.top.ss_tagSearchResultUrl;} catch(e) {searchUrl="";}}

		// ...and return it with the tag patched in.
		searchUrl = $wnd.top.ss_replaceSubStrAll(searchUrl, "ss_tagPlaceHolder", tag);
		return searchUrl;
	}-*/;
	
	/**
	 * Simulates a click on an HTML Element.
	 * 
	 * @param htmlElement
	 */
	public static native void jsClickElement(Element htmlElement) /*-{
		htmlElement.click();
	}-*/;

	/**
	 * Synchronously simulates a click on a Widget.
	 * 
	 * @param w
	 */
	public static void jsClickWidget(Widget w){
		jsClickElement(w.getElement());
	}

	/**
	 * Asynchronously simulates a click on a Widget.
	 * 
	 * @param w
	 */
	public static void jsClickWidgetAsync(final Widget w){
		deferCommand(
			new ScheduledCommand() {
				@Override
				public void execute() {
					jsClickWidget(w);
				}
			});
	}

	/**
	 * Asynchronously dumps the current agent information via an alert
	 * dialog.
	 */
	public static void jsDumpAgentInfoAsync() {
		if (isDebugUI()) {
			deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					AgentBase agent        = GWT.create(Agent.class);
					String    gwtAgent     = agent.getAgentName();
					String    browserAgent = jsDumpAgentInfo();
					
					alertViaDlg("Browser Agent: " + browserAgent + ", GWT agent: " + gwtAgent);
				}
			});
		}
	}
	
	public static native String jsDumpAgentInfo() /*-{
		return navigator.userAgent;
	}-*/;
	
	/**
	 * Invokes edit-in-place on a file using the applet.
	 * 
	 * @param binderId
	 * @param entryId
	 * @param os
	 * @param attachmentId
	 */
	public static native void jsEditInPlace_Applet(String binderId, String entryId, String namespace, String os, String attachmentId) /*-{
		$wnd.top.ss_openWebDAVFile(
			binderId,
			entryId,
			namespace,
			os, 
			attachmentId);
	}-*/;
	
	public static void jsEditInPlace_Applet(Long binderId, Long entryId, String namespace, String os, String attachmentId) {
		// Always use the initial form of the method.
		jsEditInPlace_Applet(String.valueOf(binderId), String.valueOf(entryId), namespace, os, attachmentId);
	}
	
	/**
	 * Invokes edit-in-place on a file using the WebDAV.
	 * 
	 *  @param attachmentUrl
	 */
	public static void jsEditInPlace_WebDAV(String attachmentUrl) {
		jsLaunchUrlInWindow(attachmentUrl, "_blank");
	}
	
	/**
	 * Uses JavaScript native method to URI encode a string.
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static native String jsEncodeURIComponent(String s) /*-{
		return encodeURIComponent(s);
	}-*/;
	
	/**
	 * Evaluates a JavaScript string containing embedded
	 * JavaScript. 
	 * 
	 * Note:  The code that's now in GwtMainPage.jsEvalStringImpl() was
	 *    originally inside this method but GWT's obfuscation used for
	 *    our production compiles broke it.
	 * 
	 * @param url
	 * @param jsString
	 */
	public static native void jsEvalString(String url, String jsString) /*-{
		$wnd.top.jsEvalStringImpl(url, jsString);
	}-*/;
	
	/*
	 * ?
	 */
	private static native void jsFixFirstCol(Element eTD) /*-{
		eTD.colSpan = 2;
		eTD.width = "100%";
	}-*/;

	/**
	 * Searches for <SCRIPT> elements in the given HTML element and
	 * executes the JavaScript.
	 * 
	 * Notes:
	 * 1) This is done in 2 steps to facilitate setting a breakpoint
	 *    within this method.
	 * 2) Only <SRCIPT> tags without a src="..." are executed.
	 * 
	 * @param htmlElement
	 * @param globelScope
	 */
	public static void jsExecuteJavaScript(Element htmlElement, boolean globalScope) {
		// Always use the implementation form of the method.
		jsExecuteJavaScriptImpl(htmlElement, globalScope);
	}
	
	public static void jsExecuteJavaScript(Element htmlElement) {
		// Always use the initial form of the method.
		jsExecuteJavaScript(htmlElement, false);
	}

	private static native void jsExecuteJavaScriptImpl(Element htmlElement, boolean globalScope) /*-{
		$wnd.parent.ss_executeJavascript(htmlElement, globalScope);
	}-*/;

	/**
	 * Searches for <SCRIPT> elements in the given HTML element and
	 * executes the JavaScript.
	 * 
	 * Notes:
	 * 1) This is done in 2 steps to facilitate setting a breakpoint
	 *    within this method.
	 * 2) Executes <SCRIPT src="..."> tags first followed by <SRCIPT>
	 *    tags without a src="...".
	 * 
	 * @param htmlElement
	 */
	public static void jsExecutePhasedJavaScript(Element htmlElement) {
		// Always use the implementation form of the method.
		jsExecutePhasedJavaScriptImpl(htmlElement);
	}
	
	private static native void jsExecutePhasedJavaScriptImpl(Element htmlElement) /*-{
		$wnd.parent.ss_executePhasedJavascript(htmlElement);
	}-*/;

	/**
	 * Used to fire a simple Vibe event to the outer most GwtMainpage's
	 * event bus from anywhere within the application.
	 * 
	 * @param eventEnum
	 */
	public static native void jsFireVibeEventOnMainEventBus(TeamingEvents eventEnum) /*-{
		$wnd.top.ss_fireVibeEventOnMainEventBus(eventEnum);
	}-*/;

	/**
	 * Returns the JavaScript variable ss_allowNextPrevOnView.
	 * 
	 * @return
	 */
	public static native boolean jsGetAllowNextPrevOnView() /*-{
		return $wnd.top.ss_allowNextPrevOnView;
	}-*/;

	/**
	 * Returns the binder ID from the content IFRAME.
	 * 
	 * @return
	 */
	public static native String jsGetContentBinderId() /*-{
		return $wnd.top.gwtContentIframe.ss_binderId;
	}-*/;
	
	/**
	 * Returns the contributor IDs from the content IFRAME.
	 * 
	 * @return
	 */
	public static native String jsGetContentContributorIds() /*-{
		return $wnd.top.gwtContentIframe.ss_clipboardIdsAsJSString;
	}-*/;
	
	/**
	 * Returns the left position of the content <IFRAME>'s <DIV>.
	 * 
	 * @return
	 */
	public static native int jsGetContentIFrameLeft() /*-{
		var iFrameDIV = $wnd.top.document.getElementById('contentControl');
		return $wnd.top.ss_getObjectLeft(iFrameDIV);
	}-*/;

	/**
	 * Returns the top position of the content <IFRAME>'s <DIV>.
	 * 
	 * @return
	 */
	public static native int jsGetContentIFrameTop() /*-{
		var iFrameDIV = $wnd.top.document.getElementById('contentControl');
		return $wnd.top.ss_getObjectTop(iFrameDIV);
	}-*/;

	/**
	 * Returns the text on the main GWT page's <title>.
	 * 
	 * @return
	 */
	public static native String jsGetMainTitle() /*-{
		return $wnd.top.document.title;
	}-*/;

	/**
	 * Returns the view type of what's being viewed in the content
	 * panel.
	 * 
	 * @return
	 */
	public static native String jsGetViewType() /*-{
		return $wnd.top.gwtContentIframe.ss_viewType;
	}-*/;
	
	/**
	 * Hides the popup entry iframe div if one exists.
	 */
	public static native void jsHideEntryPopupDiv() /*-{
		if ($wnd.ss_hideEntryDivOnLoad !== undefined) {
			$wnd.ss_hideEntryDivOnLoad();
		}
	}-*/;

	/**
	 * Called to hide any open entry view DIV that's in new page mode.
	 */
	public static native void jsHideNewPageEntryViewDIV() /*-{
		if ($wnd.top.ss_getUserDisplayStyle() == "newpage") {
			if (typeof $wnd.top.ss_hideEntryDivOnLoad != "undefined") {
				$wnd.top.ss_hideEntryDivOnLoad();
			}
		}
	}-*/;
	
	/**
	 * Invoke the "define editor overrides" dialog.
	 */
	public static native void jsInvokeDefineEditorOverridesDlg() /*-{
		$wnd.top.ss_editAppConfig();
	}-*/;

	/**
	 * Returns true if we're running in any flavor of Chrome and false
	 * otherwise.
	 * 
	 * Mimics the check in BrowserSniffer.is_chrome().
	 * 
	 * @return
	 */
	public static native boolean jsIsChrome() /*-{
		var agent = navigator.userAgent.toLowerCase();
		if (agent.indexOf("chrome") != (-1)) {
			return true;
		}
		return false;
	}-*/;
	
	/**
	 * Returns true if we're running in any flavor of Firefox and false
	 * otherwise.
	 * 
	 * Mimics the check in BrowserSniffer.is_mozilla().
	 * 
	 * @return
	 */
	public static native boolean jsIsFirefox() /*-{
		var agent = navigator.userAgent.toLowerCase();
		if ((agent.indexOf("mozilla")    != (-1)) &&
			(agent.indexOf("spoofer")    == (-1)) &&
			(agent.indexOf("compatible") == (-1)) &&
			(agent.indexOf("opera")      == (-1)) &&
			(agent.indexOf("webtv")      == (-1)) &&
			(agent.indexOf("hotjava")    == (-1))) {
			return true;
		}
		return false;
	}-*/;
	
	/**
	 * Returns true if we're running in any Gecko based browser and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static native boolean jsIsGecko() /*-{
		var agent = navigator.userAgent.toLowerCase();
		return (agent.indexOf("gecko") != (-1));
	}-*/;
	
	/**
	 * Returns true if we're running in any flavor of IE and false
	 * otherwise.
	 * 
	 * Mimics the check in BrowserSniffer.is_ie().
	 * 
	 * @return
	 */
	public static boolean jsIsAnyIE() {
		return (jsIsIE() || jsIsIE11());
	}
	
	/**
	 * Returns true if we're running in any flavor of IE other than
	 * IE11 and false otherwise.
	 * 
	 * @return
	 */
	public static native boolean jsIsIE() /*-{
		var agent = navigator.userAgent.toLowerCase();
		if (agent.indexOf("msie") != (-1)) {
			return true;
		}
		return false;
	}-*/;
	
	/**
	 * Returns true if we're running in IE11 and false otherwise.
	 * 
	 * Mimics the check in BrowserSniffer.is_ie_11().
	 * 
	 * @return
	 */
	public static native boolean jsIsIE11() /*-{
		var agent = navigator.userAgent.toLowerCase();
		if (agent.indexOf("like gecko") != (-1) && (agent.indexOf("rv:11.0") != (-1))) {
			return true;
		}
		return false;
	}-*/;
	
	/**
	 * Returns true if we're running in any flavor of Safari and false
	 * otherwise.
	 * 
	 * @return
	 */
	public static native boolean jsIsSafari() /*-{
		var agent = navigator.userAgent.toLowerCase();
		if (agent.indexOf("safari") != (-1)) {
			return (agent.indexOf("chrome") == (-1));
		}
		return false;
	}-*/;
	
	/**
	 * Returns true if we're running in any Webkit based browser and
	 * false otherwise.
	 * 
	 * @return
	 */
	public static native boolean jsIsWebkit() /*-{
		var agent = navigator.userAgent.toLowerCase();
		return (agent.indexOf("webkit") != (-1));
	}-*/;
	
	/**
	 * Uses Teaming's existing ss_common JavaScript to launch a toolbar
	 * popup URL.
	 * 
	 * @param url
	 */
	public static native void jsLaunchToolbarPopupUrl(String url, String w, String h) /*-{
		$wnd.ss_toolbarPopupUrl(url, '_blank', w, h);
	}-*/;
	
	public static void jsLaunchToolbarPopupUrl(String url, int w, int h) {
		// Always use the initial form of the method.
		jsLaunchToolbarPopupUrl(url, String.valueOf(w), String.valueOf(h));
	}

	public static void jsLaunchToolbarPopupUrl(String url) {
		// Always use the initial form of the method.
		jsLaunchToolbarPopupUrl(url, "", "");
	}

	/**
	 * Uses Teaming's existing ss_common JavaScript to launch a URL in
	 * a new window.
	 * 
	 * @param url
	 * @param windowName
	 * @param windowHeight
	 * @param windowWidth
	 */
	public static native void jsLaunchUrlInWindow(String url, String windowName, int windowHeight, int windowWidth) /*-{
		$wnd.top.ss_openUrlInWindow({href: url}, windowName, windowWidth, windowHeight);
	}-*/;
	
	public static void jsLaunchUrlInWindowAsync(final String url, final String windowName, final int windowHeight, final int windowWidth) {
		deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				jsLaunchUrlInWindow(url, windowName, windowHeight, windowWidth);
			}
		});
	}
	
	public static native void jsLaunchUrlInWindow(String url, String windowName) /*-{
		$wnd.top.ss_openUrlInWindow({href: url}, windowName);
	}-*/;
	
	public static void jsLaunchUrlInWindowAsync(final String url, final String windowName) {
		deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				jsLaunchUrlInWindow(url, windowName);
			}
		});
	}

	/**
	 * Loads a URL into the current window.
	 * 
	 * @param url
	 */
	public static native void jsLoadUrlInCurrentWindow(String url) /*-{
		$wnd.location.href = url;
	}-*/;
	
	/**
	 * Loads a URL into the top window.
	 * 
	 * @param url
	 */
	public static native void jsLoadUrlInTopWindow(String url) /*-{
		$wnd.top.location.href = url;
	}-*/;
	
	/**
	 * Use Teaming's existing JavaScript to logout of Teaming.
	 */
	public static native void jsLogout() /*-{
		if ($wnd.top.ss_logoff != null) {
			$wnd.top.ss_logoff();
		}
	}-*/;

	/**
	 * Invokes the ss_onLoadInit() method from ss_common.js.
	 */
	public static native void jsOnLoadInit() /*-{
		if ($wnd.ss_onLoadInit) {
			$wnd.ss_onLoadInit();
		}
	}-*/;

	/**
	 * Called to force the GWT UI content area to resize itself based
	 * on its current content.
	 * 
	 * @param reason
	 */
	public static native void jsResizeGwtContent(String reason) /*-{
		$wnd.top.resizeGwtContent(reason);
	}-*/;

	/**
	 * Set the JavaScript variable, ss_allowNextPrevOnView, to the
	 * given value.
	 * 
	 * @param allowNextPrevOnView
	 */
	public static native void jsSetAllowNextPrevOnView(boolean allowNextPrevOnView) /*-{
		$wnd.top.ss_allowNextPrevOnView = allowNextPrevOnView;
	}-*/;

	/**
	 * Set the JavaScript variable, ss_userDisplayStyle, to the given
	 * value.
	 */
	public static native void jsSetEntryDisplayStyle(String style) /*-{
		$wnd.top.ss_userDisplayStyle = style;
	}-*/;

	/**
	 * Call ss_setEntryPopupIframeSize() to set the size and position of the view entry popup div.
	 */
	public static native void jsSetEntryPopupIframeSize() /*-{
		$wnd.top.ss_setEntryPopupIframeSize();
	}-*/;

	/*
	 * Sets the text on the main GWT page's <title>.
	 */
	private static native void jsSetMainTitleImpl(String title) /*-{
		$wnd.top.document.title = title;
	}-*/;

	/**
	 * Sets the text on the main GWT page's <title>.
	 * 
	 * @param title
	 * @param fireTitleSetEvent
	 */
	public static void jsSetMainTitle(String title, boolean fireTitleSetEvent) {
		jsSetMainTitleImpl(title);
		if (fireTitleSetEvent) {
			GwtTeaming.fireEventAsync(new WindowTitleSetEvent(title));
		}
	}
	
	public static void jsSetMainTitle(String title) {
		// Always use the initial form of the method.
		jsSetMainTitle(title, true);
	}

	/**
	 * Runs an entry view URL in the content frame.
	 * 
	 * @param url
	 */
	public static native void jsShowForumEntry(String entryUrl) /*-{
		$wnd.top.ss_showForumEntry(entryUrl);
	}-*/;
	
	public static void jsShowForumEntryAsync(final String entryUrl) {
		deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				jsShowForumEntry(entryUrl);
			}
		});
	}
	
	/**
	 * Compares two strings by collation.
	 * 
	 * Returns:
	 *    -1 if s1 <  s2;
	 *     0 if s1 == s2; and
	 *     1 if s1 >  s2.
	 *     
	 * @param s1
	 * @param s2
	 * 
	 * @return
	 */
	public static native int jsStringCompare(String s1, String s2) /*-{
		return s1.localeCompare(s2);
	}-*/;

	/**
	 * Percent encodes a string, stripping any newlines.
	 * 
	 * See the following for the algorithm implemented:
	 *		http://shadow2531.com/opera/testcases/mailto/modern_mailto_uri_scheme.html
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static native String jsUTF8PercentEncodeWithNewlinesStripped(String s) /*-{
	    try {
	        return encodeURIComponent(s.replace(/\r|\n/g, ""));
	    }
	    
	    catch (e) {
	        return "Error%20encoding%20data.";
	    }
	}-*/;

	/**
	 * Percent encodes a string, normalizing newlines.
	 * 
	 * See the following for the algorithm implemented:
	 *		http://shadow2531.com/opera/testcases/mailto/modern_mailto_uri_scheme.html
	 * 
	 * @param s
	 * 
	 * @return
	 */
	public static native String jsUTF8PercentEncodeWithNormalizedNewlines(String s) /*-{
	    try {
	        // Normalize raw newlines first so that *if* there are any
	        // newlines in s, \r\n, stray \r and \n all come out as
	        // %0D%0A.
	        return encodeURIComponent(s.replace(/\r\n|\r|\n/g, "\r\n"));
	    }
	    
	    catch (e) {
	        return "Error%20encoding%20data.";
	    }
	}-*/;

	/**
	 * Does a JavaScript window.open() on the given URI.
	 *  
	 * @param uri
	 */
	public static native void jsWindowOpen(String uri) /*-{
		window.open(uri);
	}-*/;
	
	/**
	 * Sets a TeamingPopupPanel to use one-way-corner animation to
	 * open.
	 * 
	 * @param popup
	 */
	public static void oneWayCornerPopup(TeamingPopupPanel popup) {
		popup.setAnimationEnabled(true);
		popup.setAnimationType(PopupPanel.AnimationType.ONE_WAY_CORNER);
	}
	
	/**
	 * Removes all the child Node's from a DOM Element.
	 *  
	 * @param e
	 */
	public static void removeAllChildren(Element e) {
		// If we have a DOM Element... 
		if (null != e) {
			// ...scan its child Node's...
			Node child = e.getFirstChild(); 
			while (null != child) {
				// ...removing each from the Element.
				e.removeChild(child);
				child = e.getFirstChild();
			}
		}
	}

	/**
	 * Renders a non-breaking space as HTML.
	 * 
	 * @param sb
	 */
	public static void renderEmptyHtml(SafeHtmlBuilder sb) {
		sb.append(SafeHtmlUtils.fromTrustedString("&nbsp;"));
	}
	
	/**
	 * Sets a TeamingPopupPanel to use roll-down animation to open.
	 * 
	 * @param popup
	 */
	public static void rollDownPopup(TeamingPopupPanel popup) {
		popup.setAnimationEnabled(true);
		popup.setAnimationType(PopupPanel.AnimationType.ROLL_DOWN);
	}
	
	/**
	 * Performs a collated compare on two strings without generating any
	 * exceptions.
	 * 
	 * Returns:
	 *    -1 if s1 <  s2;
	 *     0 if s1 == s2; and
	 *     1 if s1 >  s2.
	 *     
	 * @param s1
	 * @param s2
	 * 
	 * @return
	 */
	public static int safeSColatedCompare(String s1, String s2) {
		return
			jsStringCompare(
				((null == s1) ? "" : s1),
				((null == s2) ? "" : s2));
	}
	
	/**
	 * Executes a SceduledCommand.
	 * 
	 * @param cmd
	 * @param delay
	 */
	public static void deferCommand(final ScheduledCommand cmd, final int delay) {
		// Yes!  If we don't have a specific amount of time to
		// delay...
		if (0 == delay) {
			// ...defer the command...
			Scheduler.get().scheduleDeferred(cmd);
		}

		// ...otherwise, if the delay is less than 0...
		else if (0 > delay) {
			// ...execute the command inline...
			cmd.execute();
		}
		
		else {
			// ...otherwise, delay the amount of time requested and
			// ...then execute the command.
			Timer timer = new Timer() {
				@Override
				public void run() {
					cmd.execute();
				}
			};
			timer.schedule(delay);
		}
	}
	
	public static void deferCommand(ScheduledCommand cmd) {
		// Always use the initial form of the method.
		deferCommand(cmd, 0);
	}

	/**
	 * Look for the given value in the given ListBox.
	 * 
	 * @param listbox
	 * @param value
	 * 
	 * @return
	 */
	public static int doesListboxContainValue(ListBox listbox, String value) {
		if ((null == listbox) || (null == value)) {
			return (-1);
		}
		
		for (int i = 0; i < listbox.getItemCount(); i += 1) {
			String nextValue = listbox.getValue(i);
			if (value.equalsIgnoreCase(nextValue)) {
				return i;
			}
		}
		
		// If we get here we did not find the value.
		return (-1);
	}
	
	/**
	 * For the given list box, select the item in the list box that has
	 * the given value.
	 * 
	 * @param listbox
	 * @param value
	 */
	public static int selectListboxItemByValue(ListBox listbox, String value) {
		for (int i = 0; i < listbox.getItemCount(); i += 1) {
			String tmp = listbox.getValue(i);
			if (tmp != null && tmp.equalsIgnoreCase(value)) {
				listbox.setSelectedIndex(i);
				return i;
			}
		}
		
		// If we get here it means we did not find an item in the
		// list box with the given value.
		return (-1);
	}
	
	/**
	 * Sets up a colspan="span" that spans the cells of a row in a
	 * Grid beginning at index start in a way that works on IE, FF, ...
	 *
	 * Note:  This is a hack to get around the inability to natively
	 *        set a colspan on cells of GWT Grid.
	 *
	 * @param grid
	 * @param row
	 * @param cell
	 */
	public static void setGridColSpan(Grid grid, int row, int start, int span) {
		jsFixFirstCol(grid.getCellFormatter().getElement(row, start));
		for (int i = 1; i < span; i += 1) {
			start += 1;
			grid.getCellFormatter().getElement(row, start).addClassName("grid_HideCell");
		}
		
	}
	
	/**
	 * Set the background color if specified in the WidgetStyles.
	 * 
	 * @param element
	 * @param color
	 */
	public static void setElementBackgroundColor(Element element, String color) {
		if ((null != element) && hasString(color)) {
			Style style = element.getStyle();
			style.setBackgroundColor(color);
		}
	}
	
	/**
	 * Set the border width and color if specified in the WidgetStyles.
	 * 
	 * @param element
	 * @param widgetStyles
	 */
	public static void setElementBorderStyles(Element element, WidgetStyles widgetStyles) {
		if (null != element) {
			Style  style = element.getStyle();
			String width = widgetStyles.getBorderWidth();
			if (hasString(width)) {
				style.setBorderWidth(Double.valueOf(width), Unit.PX);
			}
			
			String color = widgetStyles.getBorderColor();
			if (hasString(color)) {
				style.setBorderColor(color);
			}
		}
	}
	
	/**
	 * Set the text color if one is specified in the WidgetStyles.
	 * 
	 * @param element
	 * @param color
	 */
	public static void setElementTextColor(Element element, String color) {
		if ((null != element) && hasString(color)) {
			Style style = element.getStyle();
			style.setColor(color);
		}
	}
	
	/**
	 * Set the overflow style on the given UIObject.
	 * 
	 * @param overflow
	 * @param uiObj
	 */
	public static void setOverflow(Style.Overflow overflow, UIObject uiObj) {
		Style style = uiObj.getElement().getStyle();
		if (null != style) {
			style.setOverflow(overflow);
		}
	}
	
	/**
	 * Set the height of the given UIObject
	 * 
	 * @param height
	 * @param unit
	 * @param uiObj
	 */
	public static void setHeight(int height, Unit unit, UIObject uiObj) {
		Style style = uiObj.getElement().getStyle();
		if (null != style) {
			// Don't set the height if it is set to 100%.  This causes
			// a scroll bar to appear.
			if ((100 != height) || (Unit.PCT != unit)) {
				style.setHeight(height, unit);
			}
		}
	}
	
	/**
	 * Set the width of the given UIObject
	 * 
	 * @param width
	 * @param unit
	 * @param uiObj
	 */
	public static void setWidth(int width, Unit unit, UIObject uiObj) {
		Style style = uiObj.getElement().getStyle();
		if (null != style) {
			// Don't set the width if it is 100%.  This causes a scroll
			// bar to appear.
			if ((100 != width) || (Unit.PCT != unit)) {
				style.setWidth(width, unit);
			}
		}
	}

	/**
	 * Replaces all occurrences of oldSub with newSub in s.
	 * 
	 * The implementation was copied from StringUtil.replace().
	 * 
	 * @param s
	 * @param oldSub
	 * @param newSub
	 * 
	 * @return
	 */
	public static String replace(String s, String oldSub, String newSub) {
		if ((s == null) || (oldSub == null) || (newSub == null)) {
			return null;
		}

		int y = s.indexOf(oldSub);

		if (y >= 0) {
			StringBuffer sb = new StringBuffer();
			int length = oldSub.length();
			int x = 0;

			while (x <= y) {
				sb.append(s.substring(x, y));
				sb.append(newSub);
				x = y + length;
				y = s.indexOf(oldSub, x);
			}

			sb.append(s.substring(x));

			return sb.toString();
		}
		else {
			return s;
		}
	}
	
	/**
	 * Adds scroll bars to the main content panel for the duration of a
	 * PopupPanel.
	 *  
	 * @param popup
	 * @param scrollType
	 */	
	public static void scrollUIForPopup(PopupPanel popup, ScrollType scrollType) {
		// What class name do we use for the requested scroll type?
		final String scrollClass;
		switch (scrollType) {
		default:
		case VERTICAL:    scrollClass = "gwtUI_ss_forceVerticalScroller";   break;
		case BOTH:        scrollClass = "gwtUI_ss_forceBothScrollers";      break;
		case HORIZONTAL:  scrollClass = "gwtUI_ss_forceHorizontalScroller"; break;
		}

		// If the <body> doesn't currently have this class...
		final Element bodyElement = RootPanel.getBodyElement();
		String className = bodyElement.getClassName();
		if ((!(hasString(className))) || (0 > className.indexOf(scrollClass))) {
			// ...add it...
			bodyElement.addClassName(scrollClass);

			// ...and when the popup closes...
			popup.addCloseHandler(new CloseHandler<PopupPanel>() {
				@Override
				public void onClose(CloseEvent<PopupPanel> event) {
					// ...remove it.
					bodyElement.removeClassName(scrollClass);
				}
			});
		}
	}

	public static void scrollUIForPopup(PopupPanel popup) {
		// Always use the initial form of the method, defaulting to a
		// vertical scroll bar only.
		scrollUIForPopup(popup, ScrollType.VERTICAL);
	}

	/**
	 * Puts the focus into the given widget after a 1/2 second delay.
	 * @param focusWidget
	 */
	public static void setFocusDelayed(final FocusWidget focusWidget) {
		// Set the focus in the given widget after 1/2 second delay.
		deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				// Give the focus to the widget.
				setFocusNow(focusWidget);
			}
		},
		500);
	}
	
	public static void setFocusNow(FocusWidget focusWidget) {
		focusWidget.setFocus(true);
	}

	/**
	 * Set's the visibility state of a non-null UIObject.
	 * 
	 * @param uio
	 * @param visible
	 */
	public static void setVisibile(UIObject uio, boolean visible) {
		if (null != uio) {
			uio.setVisible(visible);
		}
	}
	
	/**
	 * Simulates a click event on the given element.
	 * 
	 * @param e
	 */
	public static void simulateElementClick(Element e) {
		NativeEvent clickEvent = Document.get().createClickEvent(1, 0, 0, 0, 0, false, false, false, false);
		e.dispatchEvent(clickEvent);
	}

	/**
	 * ?
	 *  
	 * @param s
	 * @param delimiter
	 */
	public static String[] split(String s, String delimiter) {
		if ((s == null) || (delimiter == null)) {
			return new String[0];
		}

		s = s.trim();
		if (!(s.endsWith(delimiter))) {
			s += delimiter;
		}

		if (s.equals(delimiter)) {
			return new String[0];
		}

		List<String> nodeValues = new ArrayList<String>();
		int offset = 0;
		int pos = s.indexOf(delimiter, offset);
		while (pos != (-1)) {
			nodeValues.add(s.substring(offset, pos));

			offset = (pos + delimiter.length());
			pos = s.indexOf(delimiter, offset);
		}

		return (String[])nodeValues.toArray(new String[0]);
	}


	/**
	 * Validates we have a URL in an OnSelectBinderInfo object.
	 * 
	 * Optionally displays an error if there isn't and returns false.
	 * Otherwise, returns true.
	 * 
	 * @param osbi
	 * @param displayError
	 * 
	 * @return
	 */
	public static boolean validateOSBI(OnSelectBinderInfo osbi, boolean displayError) {
		// If we the OnSelectBinderInfo doesn't have a permalink to the
		// binder...
		if (!(hasString(osbi.getBinderUrl()))) {
			// ...tell the user and return false.
			if (displayError) {
				deferredAlert(GwtTeaming.getMessages().cantAccessFolder());
			}
			return false;
		}
		
		// If we get here, the OnSelectBinderInfo has a permalink to
		// the binder.  Return true.
		return true;
	}
	
	public static boolean validateOSBI(OnSelectBinderInfo osbi) {
		// Always use the initial form of the method.
		return validateOSBI(osbi, true);
	}
}
