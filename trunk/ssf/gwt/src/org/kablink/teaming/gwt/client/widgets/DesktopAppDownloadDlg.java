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
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetFooterToolbarItemsCmd;
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
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements Vibe's desktop application download dialog.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class DesktopAppDownloadDlg extends DlgBox {
	private boolean						m_isFilr;					// true -> We're in Filr mode.  false -> We're in Vibe mode.
	private GwtTeamingFilrImageBundle	m_filrImages;				// Access to Filr's images.
	private GwtTeamingImageBundle		m_images;					// Access to Vibe's images.
	private GwtTeamingMessages			m_messages;					// Access to Vibe's messages.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private String						m_company;					//
	private String						m_product;					//
	private VibeFlowPanel				m_fp;						// The panel holding the dialog's content.

	// Indexes of the various cells.
	private final static int PRODUCT_COL		= 0;
	private final static int LOGO_COL			= 1;
	private final static int DOWNLOADS_COL		= 2;
	private final static int INSTRUCTIONS_COL	= 3;
	
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
		m_images     = GwtTeaming.getImageBundle();
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
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-desktopAppPage-body");
		return m_fp;
	}

	/*
	 * Creates the masthead content of the page.
	 */
	private void createContentMasthead() {
		// Create the masthead panel...
		VibeFlowPanel mhPanel = new VibeFlowPanel();
		mhPanel.addStyleName("vibe-desktopAppPage-masthead");
		m_fp.add(mhPanel);

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
		m_fp.add(shPanel);

		// ...and content.
		StringBuffer sb = new StringBuffer(m_company);
		sb.append(createRegHTML());
		sb.append(" ");
		sb.append(m_messages.downloadAppDlgSubhead(m_product));
		shPanel.getElement().setInnerHTML(sb.toString());
	}
	
	/*
	 * Creates the body content of the page.
	 */
	private void createContentBody() {
		// Create the body panel...
		VibeFlowPanel bodyPanel = new VibeFlowPanel();
		bodyPanel.addStyleName("vibe-desktopAppPage-content");
		m_fp.add(bodyPanel);

		// ...create the <TABLE> containing the body's content...
		VibeFlexTable ft = new VibeFlexTable();
		ft.addStyleName("vibe-desktopAppPage-contentTable");
		ft.setCellPadding(0);
		ft.setCellSpacing(0);
		bodyPanel.add(ft);
		FlexCellFormatter fcf = ft.getFlexCellFormatter();

		// ...create the table's header row...
		ft.setText(      0,  PRODUCT_COL,      m_messages.downloadAppDlgBody_Product());
		fcf.addStyleName(0,  PRODUCT_COL,      "vibe-desktopAppPage-columnhead vibe-desktopAppPage-columnhead2");
		ft.setText(      0,  LOGO_COL,         m_messages.downloadAppDlgBody_Type());
		fcf.addStyleName(0,  LOGO_COL,         "vibe-desktopAppPage-logo");
		ft.setText(      0,  DOWNLOADS_COL,    m_messages.downloadAppDlgBody_Downloads());
		fcf.addStyleName(0,  DOWNLOADS_COL,    "vibe-desktopAppPage-linkhead");
		ft.setText(      0,  INSTRUCTIONS_COL, m_messages.downloadAppDlgBody_Instructions());
		fcf.addStyleName(0,  INSTRUCTIONS_COL, "vibe-desktopAppPage-instructionhead");

		// ...create the Windows client content...
		createProductTitleCell(        ft, fcf, 1, m_messages.downloadAppDlgProductWindows(m_product));
		createLogoCell(                ft, fcf, 1, m_filrImages.logoWindows(), m_messages.downloadAppDlgAlt_WindowsDownloads());
		createDownloadsCell_Windows(   ft, fcf, 1);
		createInstructionsCell_Windows(ft, fcf, 1);
		
		// ...create the MacOS client content...
		createProductTitleCell(    ft, fcf, 2, m_messages.downloadAppDlgProductMac(m_product));
		createLogoCell(            ft, fcf, 2, m_filrImages.logoMac(), m_messages.downloadAppDlgAlt_MacDownloads());
		createDownloadsCell_Mac(   ft, fcf, 2);
		createInstructionsCell_Mac(ft, fcf, 2);
		
		// ...create the Android client content...
		createProductTitleCell(        ft, fcf, 3, m_messages.downloadAppDlgProductAndroid(m_product));
		createLogoCell(                ft, fcf, 3, m_filrImages.logoAndroid(), m_messages.downloadAppDlgAlt_AndroidDownloads());
		createDownloadsCell_Android(   ft, fcf, 3);
		createInstructionsCell_Android(ft, fcf, 3);
		
		// ...and create the iOS client content
		createProductTitleCell(    ft, fcf, 4, m_messages.downloadAppDlgProductIOS(m_product));
		createLogoCell(            ft, fcf, 4, m_filrImages.logoIOS(), m_messages.downloadAppDlgAlt_IOSDownloads());
		createDownloadsCell_IOS(   ft, fcf, 4);
		createInstructionsCell_IOS(ft, fcf, 4);
	}

	/*
	 * Creates the downloads cell content for Android.
	 */
	private void createDownloadsCell_Android(VibeFlexTable ft, FlexCellFormatter fcf, int row) {
		// Construct the strings to display in the first block...
		String s1 = m_messages.downloadAppDlgDownloadAndroid1(m_company, m_product);
		InlineLabel s3 = new InlineLabel(m_messages.downloadAppDlgDownloadAndroid3());
		s3.addStyleName("vibe-desktopAppPage-bold");
		String s2 = m_messages.downloadAppDlgDownloadAndroid2(GwtClientHelper.getWidgetHTML(s3));
		StringBuffer sb = new StringBuffer(m_company);
		sb.append(createRegHTML());
		sb.append(" ");
		sb.append(m_product);
		InlineLabel il = new InlineLabel();
		il.getElement().setInnerHTML(sb.toString());
		il.addStyleName("vibe-desktopAppPage-bold");
		String s4 = m_messages.downloadAppDlgDownloadAndroid4(m_company, m_product, GwtClientHelper.getWidgetHTML(il));
		sb = new StringBuffer(s1);
		sb.append("  "); sb.append(s2);
		sb.append("  "); sb.append(s4);
		Label l = new Label();
		l.getElement().setInnerHTML(sb.toString());

		// ...add them to the cell...
		ft.setText(row, DOWNLOADS_COL, "");
		Element rE = fcf.getElement(row, DOWNLOADS_COL);
		rE.appendChild(l.getElement());

		// ...and append the second string block to the cell.
		l = new Label(m_messages.downloadAppDlgDownloadAndroid5(m_company, m_product));
		l.addStyleName("marginTop10px");
		rE.appendChild(l.getElement());
	}
	
	/*
	 * Creates the downloads cell content for iOS.
	 */
	private void createDownloadsCell_IOS(VibeFlexTable ft, FlexCellFormatter fcf, int row) {
//!		"...this needs to be implemented..."
		ft.setText(      row, DOWNLOADS_COL, "...this needs to be implemented...");
		fcf.addStyleName(row, DOWNLOADS_COL, "gwtUI_nowrap");
	}
	
	/*
	 * Creates the downloads cell content for MacOS.
	 */
	private void createDownloadsCell_Mac(VibeFlexTable ft, FlexCellFormatter fcf, int row) {
//!		"...this needs to be implemented..."
		ft.setText(      row, DOWNLOADS_COL, "...this needs to be implemented...");
		fcf.addStyleName(row, DOWNLOADS_COL, "gwtUI_nowrap");
	}
	
	/*
	 * Creates the downloads cell content for Windows.
	 */
	private void createDownloadsCell_Windows(VibeFlexTable ft, FlexCellFormatter fcf, int row) {
//!		"...this needs to be implemented..."
		ft.setText(      row, DOWNLOADS_COL, "...this needs to be implemented...");
		fcf.addStyleName(row, DOWNLOADS_COL, "gwtUI_nowrap");
	}
	
	/*
	 * Creates the instructions cell content for Android.
	 */
	private void createInstructionsCell_Android(VibeFlexTable ft, FlexCellFormatter fcf, int row) {
//!		"...this needs to be implemented..."
		ft.setText(      row, INSTRUCTIONS_COL, "...this needs to be implemented...");
		fcf.addStyleName(row, INSTRUCTIONS_COL, "gwtUI_nowrap");
	}
	
	/*
	 * Creates the instructions cell content for iOS.
	 */
	private void createInstructionsCell_IOS(VibeFlexTable ft, FlexCellFormatter fcf, int row) {
//!		"...this needs to be implemented..."
		ft.setText(      row, INSTRUCTIONS_COL, "...this needs to be implemented...");
		fcf.addStyleName(row, INSTRUCTIONS_COL, "gwtUI_nowrap");
	}
	
	/*
	 * Creates the instructions cell content for MacOS.
	 */
	private void createInstructionsCell_Mac(VibeFlexTable ft, FlexCellFormatter fcf, int row) {
//!		"...this needs to be implemented..."
		ft.setText(      row, INSTRUCTIONS_COL, "...this needs to be implemented...");
		fcf.addStyleName(row, INSTRUCTIONS_COL, "gwtUI_nowrap");
	}
	
	/*
	 * Creates the instructions cell content for Windows.
	 */
	private void createInstructionsCell_Windows(VibeFlexTable ft, FlexCellFormatter fcf, int row) {
//!		"...this needs to be implemented..."
		ft.setText(      row, INSTRUCTIONS_COL, "...this needs to be implemented...");
		fcf.addStyleName(row, INSTRUCTIONS_COL, "gwtUI_nowrap");
	}
	
	/*
	 * Creates a logo cell.
	 */
	private void createLogoCell(VibeFlexTable ft, FlexCellFormatter fcf, int row, ImageResource ir, String altText) {
		Image i = GwtClientHelper.buildImage(ir.getSafeUri().asString());
		i.addStyleName("vibe-desktopAppPage-logoImg");
		i.setTitle(altText);
		ft.setWidget(    row, LOGO_COL, i);
		fcf.addStyleName(row, LOGO_COL, "vibe-desktopAppPage-logo bottom");
	}
	
	/*
	 * Creates a product title cell.
	 */
	private void createProductTitleCell(VibeFlexTable ft, FlexCellFormatter fcf, int row, String productName) {
		ft.setText(      row, PRODUCT_COL, productName);
		fcf.addStyleName(row, PRODUCT_COL, "vibe-desktopAppPage-product-title bottom");
	}

	/*
	 * Creates the HTML containing a registered by symbol.
	 */
	private static String createRegHTML() {
		InlineLabel il = new InlineLabel();
		il.addStyleName("vibe-desktopAppPage-reg");
		il.getElement().setInnerHTML("&reg;");
		return GwtClientHelper.getWidgetHTML(il);
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
		m_fp.clear();

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
		populateDlgAsync();
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
