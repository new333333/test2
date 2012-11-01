/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.DesktopAppDownloadInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetDesktopAppDownloadInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements the desktop application download dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class DesktopAppDownloadDlg extends DlgBox {
	private boolean									m_hasMacUrl;				//
	private boolean									m_hasWin32Url;				//
	private boolean									m_hasWin64Url;				//
	private boolean									m_isFilr;					// true -> We're in Filr mode.  false -> We're in Vibe mode.
	private DesktopAppDownloadInfoRpcResponseData	m_desktopAppDownloadInfo;	// Information about downloading the desktop application.  Read via a GWT RPC call when the dialog runs.
	private GwtTeamingFilrImageBundle				m_filrImages;				// Access to Filr's images.
	private GwtTeamingMessages						m_messages;					// Access to Vibe's messages.
	private List<HandlerRegistration>				m_registeredEventHandlers;	// Event handlers that are currently registered.
	private String									m_company;					// Initialized with the company name (i.e., Novell.)
	private String									m_product;					// Initialized with the current product name (i.e., Filr or Vibe.)
	private VibeFlowPanel							m_rootPanel;				// The main panel holding the dialog's content.

	// Indexes of the various table cells containing the dialog's
	// content.
	private final static int PRODUCT_COL		= 0;
	private final static int LOGO_COL			= 1;
	private final static int DOWNLOADS_COL		= 2;
	private final static int INSTRUCTIONS_COL	= 3;
	
	// Indexes of the various table rows containing the dialog's
	// content.
	private final static int HEADER_ROW		= 0;
	private final static int WINDOWS_ROW	= 1;
	private final static int MAC_ROW		= 2;
	private final static int ANDROID_ROW	= 3;
	private final static int IOS_ROW		= 4;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private DesktopAppDownloadDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_isFilr     = GwtClientHelper.isLicenseFilr();
		m_filrImages = GwtTeaming.getFilrImageBundle();
		m_messages   = GwtTeaming.getMessages();
		m_company    = m_messages.downloadAppDlg_Novell();
		m_product    = (m_isFilr ? m_messages.downloadAppDlg_Filr() : m_messages.downloadAppDlg_Vibe());
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.downloadAppDlgHeader(m_product),	// The dialog's header.
			getSimpleSuccessfulHandler(),				// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),					// The dialog's EditCanceledHandler.
			null);										// Create callback data.  Unused.
	}

	/*
	 * Creates the HTML for a bold label.
	 */
	private String createBoldLabelHTML(String label, boolean labelIsHTML) {
		InlineLabel w = new InlineLabel();
		Element wE = w.getElement();
		if (labelIsHTML)
		     wE.setInnerHTML(label);
		else wE.setInnerText(label);
		w.addStyleName("vibe-desktopAppPage-bold");
		return GwtClientHelper.getWidgetHTML(w);
	}
	
	private String createBoldLabelHTML(String label) {
		// Always use the initial form of the method.
		return createBoldLabelHTML(label, false);
	}
	
	/**
	 * Creates all the widgets that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData (unused)
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Create and return a panel to hold the dialog's content.
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-desktopAppPage-body");
		return m_rootPanel;
	}

	/*
	 * Creates the masthead content of the page.
	 */
	private void createContentMasthead() {
		// Create the masthead panel...
		VibeFlowPanel mhPanel = new VibeFlowPanel();
		mhPanel.addStyleName("vibe-desktopAppPage-masthead");
		m_rootPanel.add(mhPanel);

		// ...and image.
		Image i = GwtClientHelper.buildImage(m_filrImages.filrBackground().getSafeUri().asString());
		i.addStyleName("head_bg");
		mhPanel.add(i);
	}
	
	/*
	 * Creates the sub-head of the page.
	 */
	private void createContentSubhead() {
		// Create the sub-head panel...
		VibeFlowPanel shPanel = new VibeFlowPanel();
		shPanel.addStyleName("vibe-desktopAppPage-subhead");
		m_rootPanel.add(shPanel);

		// ...and content.
		shPanel.getElement().setInnerHTML(m_messages.downloadAppDlgSubhead(createNovellProductHTML()));
	}
	
	/*
	 * Creates the body content of the page.
	 */
	private void createContentBody() {
		// Create the body panel...
		VibeFlowPanel bodyPanel = new VibeFlowPanel();
		bodyPanel.addStyleName("vibe-desktopAppPage-content");
		m_rootPanel.add(bodyPanel);

		// ...create the <TABLE> containing the body's content...
		VibeFlexTable ft = new VibeFlexTable();
		ft.addStyleName("vibe-desktopAppPage-contentTable");
		ft.setCellPadding(0);
		ft.setCellSpacing(0);
		bodyPanel.add(ft);
		FlexCellFormatter fcf = ft.getFlexCellFormatter();

		// ...create the table's header row...
		createHeaderCell(ft, fcf, PRODUCT_COL,      m_messages.downloadAppDlgBody_Product(),      "vibe-desktopAppPage-columnhead vibe-desktopAppPage-columnhead2");
		createHeaderCell(ft, fcf, LOGO_COL,         m_messages.downloadAppDlgBody_Type(),         "vibe-desktopAppPage-logo");
		createHeaderCell(ft, fcf, DOWNLOADS_COL,    m_messages.downloadAppDlgBody_Downloads(),    "vibe-desktopAppPage-linkhead");
		createHeaderCell(ft, fcf, INSTRUCTIONS_COL, m_messages.downloadAppDlgBody_Instructions(), "vibe-desktopAppPage-instructionhead");

		// ...create the Windows client content...
		createProductTitleCell(        ft, fcf, WINDOWS_ROW, m_messages.downloadAppDlgProductWindows(m_product));
		createLogoCell(                ft, fcf, WINDOWS_ROW, m_filrImages.logoWindows(), m_messages.downloadAppDlgAlt_WindowsDownloads());
		createDownloadsCell_Windows(   ft, fcf);
		createInstructionsCell_Windows(ft, fcf);
		
		// ...create the MacOS client content...
		createProductTitleCell(    ft, fcf, MAC_ROW, m_messages.downloadAppDlgProductMac(m_product));
		createLogoCell(            ft, fcf, MAC_ROW, m_filrImages.logoMac(), m_messages.downloadAppDlgAlt_MacDownloads());
		createDownloadsCell_Mac(   ft, fcf);
		createInstructionsCell_Mac(ft, fcf);
		
		// ...create the Android client content...
		createProductTitleCell(        ft, fcf, ANDROID_ROW, m_messages.downloadAppDlgProductAndroid(m_product));
		createLogoCell(                ft, fcf, ANDROID_ROW, m_filrImages.logoAndroid(), m_messages.downloadAppDlgAlt_AndroidDownloads());
		createDownloadsCell_Android(   ft, fcf);
		createInstructionsCell_Android(ft, fcf);
		
		// ...and create the iOS client content
		createProductTitleCell(    ft, fcf, IOS_ROW, m_messages.downloadAppDlgProductIOS(m_product));
		createLogoCell(            ft, fcf, IOS_ROW, m_filrImages.logoIOS(), m_messages.downloadAppDlgAlt_IOSDownloads());
		createDownloadsCell_IOS(   ft, fcf);
		createInstructionsCell_IOS(ft, fcf);
	}

	/*
	 * Creates the downloads cell content for Android.
	 */
	private void createDownloadsCell_Android(VibeFlexTable ft, FlexCellFormatter fcf) {
		// Construct the strings to display in the first block...
		String s1 = m_messages.downloadAppDlgDownloadAndroid1(m_company, m_product);
		String s2 = m_messages.downloadAppDlgDownloadAndroid2(createBoldLabelHTML(m_messages.downloadAppDlgDownloadAndroid3()));
		String s4 = m_messages.downloadAppDlgDownloadAndroid4(m_company, m_product, createBoldLabelHTML(createNovellProductHTML(), true));
		StringBuffer sb = new StringBuffer(s1);
		sb.append("  "); sb.append(s2);
		sb.append("  "); sb.append(s4);
		Label l = new Label();
		l.getElement().setInnerHTML(sb.toString());

		// ...add them to the cell...
		ft.setText(ANDROID_ROW, DOWNLOADS_COL, "");
		Element rE = fcf.getElement(ANDROID_ROW, DOWNLOADS_COL);
		rE.appendChild(l.getElement());

		// ...and append the second string block to the cell.
		l = new Label(m_messages.downloadAppDlgDownloadAndroid5(m_company, m_product));
		l.addStyleName("marginTop10px");
		rE.appendChild(l.getElement());
	}
	
	/*
	 * Creates the downloads cell content for iOS.
	 */
	private void createDownloadsCell_IOS(VibeFlexTable ft, FlexCellFormatter fcf) {
		// Construct the string to display in the first block...
		String s1 = m_messages.downloadAppDlgDownloadIOS1(
			m_company,
			m_product,
			createBoldLabelHTML(m_messages.downloadAppDlgDownloadIOS2()),
			createBoldLabelHTML(m_messages.downloadAppDlgDownloadIOS3()),
			createBoldLabelHTML(createNovellProductHTML(), true));
		Label l = new Label();
		l.getElement().setInnerHTML(s1);
		
		// ...add them to the cell...
		ft.setText(IOS_ROW, DOWNLOADS_COL, "");
		Element rE = fcf.getElement(IOS_ROW, DOWNLOADS_COL);
		rE.appendChild(l.getElement());

		// ...and append the second string block to the cell.
		l = new Label(m_messages.downloadAppDlgDownloadIOS4(m_company, m_product));
		l.addStyleName("marginTop10px");
		rE.appendChild(l.getElement());
	}
	
	/*
	 * Creates the downloads cell content for MacOS.
	 */
	private void createDownloadsCell_Mac(VibeFlexTable ft, FlexCellFormatter fcf) {
		// If we have a URL...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("displayBlock");
		if (m_hasMacUrl) {
			// ...add an <A> for it...
			Anchor a = new Anchor();
			a.addStyleName("vibe-desktopAppPage-linkAnchor");
			a.setTarget("_blank");
			a.setHref(m_desktopAppDownloadInfo.getMacUrl());
			a.getElement().setInnerText(m_messages.downloadAppDlgUrlMac());
			fp.add(a);
		}
		
		else {
			// ...otherwise, add a no URL message...
			InlineLabel il = new InlineLabel(m_messages.downloadAppDlgError_NoMacUrl());
			il.addStyleName("vibe-desktopAppPage-linkError");
			fp.add(il);
		}
		
		// ...and add that to the table.
		ft.setWidget(    MAC_ROW, DOWNLOADS_COL, fp);
		fcf.addStyleName(MAC_ROW, DOWNLOADS_COL, "vibe-desktopAppPage-links bottom");
	}
	
	/*
	 * Creates the downloads cell content for Windows.
	 */
	private void createDownloadsCell_Windows(VibeFlexTable ft, FlexCellFormatter fcf) {
		// Add the Win32 link...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("displayBlock");
		if (m_hasWin32Url) {
			Anchor a = new Anchor();
			a.addStyleName("vibe-desktopAppPage-linkAnchor");
			a.setTarget("_blank");
			a.setHref(m_desktopAppDownloadInfo.getWin32Url());
			a.getElement().setInnerText(m_messages.downloadAppDlgUrlWin32());
			fp.add(a);
		}
		else {
			InlineLabel il = new InlineLabel(m_messages.downloadAppDlgError_NoWin32Url());
			il.addStyleName("vibe-desktopAppPage-linkError");
			fp.add(il);
		}
		ft.setText(WINDOWS_ROW, DOWNLOADS_COL, "");
		Element rE = fcf.getElement(WINDOWS_ROW, DOWNLOADS_COL);
		rE.appendChild(fp.getElement());
		
		// ...add the Win64 link...
		fp = new VibeFlowPanel();
		fp.addStyleName("displayBlock");
		if (m_hasWin64Url) {
			Anchor a = new Anchor();
			a.addStyleName("vibe-desktopAppPage-linkAnchor");
			a.setTarget("_blank");
			a.setHref(m_desktopAppDownloadInfo.getWin64Url());
			a.getElement().setInnerText(m_messages.downloadAppDlgUrlWin64());
			fp.add(a);
		}
		else {
			InlineLabel il = new InlineLabel(m_messages.downloadAppDlgError_NoWin64Url());
			il.addStyleName("vibe-desktopAppPage-linkError");
			fp.add(il);
		}
		rE.appendChild(fp.getElement());

		// ...and style the row.
		fcf.addStyleName(WINDOWS_ROW, DOWNLOADS_COL, "vibe-desktopAppPage-links bottom");
	}

	/*
	 * Creates a header cell.
	 */
	private static void createHeaderCell(VibeFlexTable ft, FlexCellFormatter fcf, int col, String label, String style) {
		ft.setText(      HEADER_ROW, col, label);
		fcf.addStyleName(HEADER_ROW, col, style);
	}
	
	/*
	 * Creates the instructions cell content for Android.
	 */
	private static void createInstructionsCell_Android(VibeFlexTable ft, FlexCellFormatter fcf) {
		// No instructions to display. 
	}
	
	/*
	 * Creates the instructions cell content for iOS.
	 */
	private static void createInstructionsCell_IOS(VibeFlexTable ft, FlexCellFormatter fcf) {
		// No instructions to display. 
	}
	
	/*
	 * Creates the instructions cell content for MacOS.
	 */
	private static void createInstructionsCell_Mac(VibeFlexTable ft, FlexCellFormatter fcf) {
//!		...this needs to be implemented...
		ft.setWidget(    MAC_ROW, INSTRUCTIONS_COL, new Label("MacOS:  ...this needs to be implemented..."));
		fcf.addStyleName(MAC_ROW, INSTRUCTIONS_COL, "vibe-desktopAppPage-instructions bottom gwtUI_nowrap");
	}
	
	/*
	 * Creates the instructions cell content for Windows.
	 */
	private static void createInstructionsCell_Windows(VibeFlexTable ft, FlexCellFormatter fcf) {
//!		...this needs to be implemented...
		ft.setWidget(    WINDOWS_ROW, INSTRUCTIONS_COL, new Label("Windows:  ...this needs to be implemented..."));
		fcf.addStyleName(WINDOWS_ROW, INSTRUCTIONS_COL, "vibe-desktopAppPage-instructions bottom gwtUI_nowrap");
	}
	
	/*
	 * Creates a logo cell.
	 */
	private static void createLogoCell(VibeFlexTable ft, FlexCellFormatter fcf, int row, ImageResource ir, String altText) {
		Image i = GwtClientHelper.buildImage(ir.getSafeUri().asString());
		i.addStyleName("vibe-desktopAppPage-logoImg");
		i.setTitle(altText);
		ft.setWidget(    row, LOGO_COL, i);
		fcf.addStyleName(row, LOGO_COL, "vibe-desktopAppPage-logo bottom");
	}

	/*
	 * Creates the HTML for the Novell Filr/Vibe with registered
	 * trademark HTML.
	 */
	private String createNovellProductHTML() {
		StringBuffer reply = new StringBuffer(m_company);
		InlineLabel il = new InlineLabel();
		il.addStyleName("vibe-desktopAppPage-reg");
		il.getElement().setInnerHTML("&reg;");
		reply.append(GwtClientHelper.getWidgetHTML(il));
		reply.append(" ");
		reply.append(m_product);
		return reply.toString();
	}
	
	/*
	 * Creates a product title cell.
	 */
	private static void createProductTitleCell(VibeFlexTable ft, FlexCellFormatter fcf, int row, String productName) {
		ft.setText(      row, PRODUCT_COL, productName);
		fcf.addStyleName(row, PRODUCT_COL, "vibe-desktopAppPage-product-title bottom");
	}

	/**
	 * Unused.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Unused.
		return "";
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return null;
	}

	/*
	 * Asynchronously loads the next part of the dialog.
	 */
	private void loadPart1Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the next part of the dialog.
	 */
	private void loadPart1Now() {
		// Can we get the information about downloading the desktop application?
		GetDesktopAppDownloadInfoCmd cmd = new GetDesktopAppDownloadInfoCmd();
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// No!  Tell the user about the error.
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetDesktopAppDownloadInfo());
			}

			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Yes!  Use it to populate the dialog.
				m_desktopAppDownloadInfo = ((DesktopAppDownloadInfoRpcResponseData) response.getResponseData());
				
				// Did we get any URLs?
				m_hasMacUrl   = GwtClientHelper.hasString(m_desktopAppDownloadInfo.getMacUrl());
				m_hasWin32Url = GwtClientHelper.hasString(m_desktopAppDownloadInfo.getWin32Url());
				m_hasWin64Url = GwtClientHelper.hasString(m_desktopAppDownloadInfo.getWin64Url());
				if ((!m_hasMacUrl) && (!m_hasWin32Url) && (!m_hasWin64Url)) {
					// No!  Then there's not much point running the
					// dialog.  Hide it and tell the user about the
					// problem.
					hide();
					GwtClientHelper.deferredAlert(m_messages.downloadAppDlgError_NoUrls());
				}
				
				else {
					populateDlgAsync();
				}
			}
		});
	}

	/**
	 * Called when the data table is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the data table is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}

	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Add our specific footer style to the dialog.
		getFooterPanel().addStyleName("vibe-desktopAppPage-footer");
		
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_rootPanel.clear();

		// ..create new content...
		createContentMasthead();
		createContentSubhead();
		createContentBody();
		
		// ...and show the dialog.
		show(true);
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we having allocated a list to track events we've
		// registered yet...
		if (null == m_registeredEventHandlers) {
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}
		
		// If the list of registered events is empty...
		if (m_registeredEventHandlers.isEmpty()) {
			// ...register the events.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_registeredEvents,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Asynchronously runs the given instance of the desktop
	 * application download dialog.
	 */
	private static void runDlgAsync(final DesktopAppDownloadDlg dadDlg) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				dadDlg.runDlgNow();
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the desktop application
	 * download dialog.
	 */
	private void runDlgNow() {
		// Populate the dialog.
		loadPart1Async();
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if (GwtClientHelper.hasItems(m_registeredEventHandlers)) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the desktop application download dialog and perform some      */
	/* operation on it.                                              */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the desktop application
	 * download dialog asynchronously after it loads. 
	 */
	public interface DesktopAppDownloadDlgClient {
		void onSuccess(DesktopAppDownloadDlg dadDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the DesktopAppDownloadDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final DesktopAppDownloadDlgClient dadDlgClient,
			
			// initAndShow parameters,
			final DesktopAppDownloadDlg dadDlg) {
		GWT.runAsync(DesktopAppDownloadDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_DesktopAppDownloadDlg());
				if (null != dadDlgClient) {
					dadDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != dadDlgClient) {
					// Yes!  Create it and return it via the callback.
					DesktopAppDownloadDlg dadDlg = new DesktopAppDownloadDlg();
					dadDlgClient.onSuccess(dadDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(dadDlg);
				}
			}
		});
	}
	
	/**
	 * Loads the DesktopAppDownloadDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param dadDlgClient
	 */
	public static void createAsync(DesktopAppDownloadDlgClient dadDlgClient) {
		doAsyncOperation(dadDlgClient, null);
	}
	
	/**
	 * Initializes and shows the desktop application download dialog.
	 * 
	 * @param dadDlg
	 */
	public static void initAndShow(DesktopAppDownloadDlg dadDlg) {
		doAsyncOperation(null, dadDlg);
	}
}
