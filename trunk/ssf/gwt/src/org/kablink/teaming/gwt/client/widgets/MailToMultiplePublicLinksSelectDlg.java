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
package org.kablink.teaming.gwt.client.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.MailToPublicLinksRpcResponseData.MailToPublicLinkInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Implements Vibe's Mail To Multiple Public Links Select dialog.
 *  
 * @author drfoster@novell.com
 */
public class MailToMultiplePublicLinksSelectDlg extends DlgBox implements EditCanceledHandler {
	private FlexCellFormatter						m_linksFCF;			// The FlexCellFormatter for m_linksPanel.
	private GwtTeamingMessages						m_messages;			// Access to Vibe's messages.
	private List<MailToPublicLinkInfo>				m_plInfoList;		// The list of mail to public links the user can choose from.
	private MailToMultiplePublicLinksSelectCallback	m_mailToCallback;	// The callback to let the caller know what the user selected.
	private RowFormatter							m_linksRF;			// The RowFormatter for m_linksPanel.
	private ScrollPanel								m_linksScroller;	// The ScrollPanel that contains the links.
	private String									m_product;			// The product we're running as (Filr or Vibe.)
	private VibeFlexTable							m_linksPanel;		// The panel containing the links themselves.
	private VibeFlowPanel							m_contentPanel;		// The panel containing the content of the dialog below the header.
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private MailToMultiplePublicLinksSelectDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);
		
		// ...set the dialog's style...
		addStyleName("vibe-mailToMultiplePublicLinksSelectDlg");

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		m_product  = GwtClientHelper.getProductName();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mailToMultiplePublicLinksSelect_Caption(m_product),	// The dialog's caption.
			getSimpleSuccessfulHandler(),									// The dialog's EditSuccessfulHandler.
			this,															// The dialog's EditCanceledHandler.
			null);															// Create callback data.  Unused. 
	}

	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param callbackData (unused)
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object callbackData) {
		// Create the main panel for the dialog's content...
		VibeFlowPanel mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-mainPanel");

		// ...create the constituent parts and add them to the main
		// ...panel...
		createHeaderPanel( mainPanel);
		createContentPanel(mainPanel);

		// ...and return the main panel
		return mainPanel;
	}

	/*
	 * Create the controls needed in the content.
	 */
	private void createContentPanel(Panel mainPanel) {
		// Create the panel to be used for the dialog content (below
		// the header) and add it to the main panel.
		m_contentPanel = new VibeFlowPanel();
		m_contentPanel.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-contentPanel");
		mainPanel.add(m_contentPanel);
		
		// Add a ScrollPanel for the links.
		m_linksScroller = new ScrollPanel();
		m_linksScroller.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-scrollPanel");
		m_contentPanel.add(m_linksScroller);

		// Add a panel for the ScrollPanel's content.
		m_linksPanel = new VibeFlexTable();
		m_linksPanel.addStyleName("objlist vibe-mailToMultiplePublicLinksSelectDlg-linksPanel");
		m_linksPanel.setWidth("100%");
		m_linksScroller.add(m_linksPanel);
		m_linksFCF = m_linksPanel.getFlexCellFormatter();
		m_linksRF  = m_linksPanel.getRowFormatter();
	}
	
	/*
	 * Create the controls needed in the header.
	 */
	private void createHeaderPanel(Panel mainPanel) {
		// Create the panel for the header...
		VibeFlowPanel headerPanel = new VibeFlowPanel();
		headerPanel.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-headerPanel");
		mainPanel.add(headerPanel);

		// ...and add a label to it.
		Label l = new Label(m_messages.mailToMultiplePublicLinksSelect_Header(m_product));
		l.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-headerLabel");
		headerPanel.add(l);
	}

	/*
	 * Displays an individual link in the list.
	 */
	private void displayPublicLink(final MailToPublicLinkInfo pl) {
		// If this is the first public link...
		int row = m_linksPanel.getRowCount();
		if (0 == row) {
			// ...add a header row to the table.
			m_linksPanel.setText(  row, 0, m_messages.mailToMultiplePublicLinksSelect_Column_SharedOn()); m_linksFCF.addStyleName(row, 0, "vibe-mailToMultiplePublicLinksSelectDlg-tableColumnCell"         );
			m_linksPanel.setText(  row, 1, m_messages.mailToMultiplePublicLinksSelect_Column_Expires());  m_linksFCF.addStyleName(row, 1, "vibe-mailToMultiplePublicLinksSelectDlg-tableColumnCell"         );
			m_linksPanel.setText(  row, 2, m_messages.mailToMultiplePublicLinksSelect_Column_Note());     m_linksFCF.addStyleName(row, 2, "vibe-mailToMultiplePublicLinksSelectDlg-tableColumnCell rightend");
			m_linksFCF.setWidth(   row, 2, "100%");
			m_linksRF.addStyleName(row, "columnhead vibe-mailToMultiplePublicLinksSelectDlg-tableHeader");
			row += 1;
		}

		// ...and add row for the data.
		String sharedOn = pl.getSharedOn();   if (null == sharedOn) sharedOn = "";
		String note     = pl.getComment();    if (!(GwtClientHelper.hasString(note)))    note    = m_messages.mailToMultiplePublicLinksSelect_NoNote();
		String expires  = pl.getExpiration(); if (!(GwtClientHelper.hasString(expires))) expires = m_messages.mailToMultiplePublicLinksSelect_Never();
		Label l = new Label(sharedOn);
		l.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-tableLink");
		l.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				m_mailToCallback.onSelect(pl);
				hide();
			}
		});
		m_linksPanel.setWidget(    row, 0, l      ); m_linksFCF.addStyleName(row, 0, "vibe-mailToMultiplePublicLinksSelectDlg-tableCell"    );	// No-wrap.
		m_linksPanel.setText(      row, 1, expires); m_linksFCF.addStyleName(row, 1, "vibe-mailToMultiplePublicLinksSelectDlg-tableCell"    );	// No-wrap.
		m_linksPanel.setText(      row, 2, note   ); m_linksFCF.addStyleName(row, 2, "vibe-mailToMultiplePublicLinksSelectDlg-tableCellNote");	// Allows wrapping.
		m_linksRF.addStyleName(    row, "regrow vibe-mailToMultiplePublicLinksSelectDlg-tableRow");
		m_linksRF.setVerticalAlign(row, HasVerticalAlignment.ALIGN_TOP);
	}
	
	/*
	 * Displays the links from the map.
	 */
	private void displayPublicLinks() {
		// Clear the content panel...
		m_linksPanel.removeAllRows();
		
		// ...and adjust the sizes for scrolling.
		m_linksPanel.removeStyleName("vibe-mailToMultiplePublicLinksSelectDlg-scrollLimit");	// Limit on the ScrollPanel...
		m_linksScroller.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-scrollLimit");	// ...not the VerticalPanel.
		
		// Scan the links...
		for (MailToPublicLinkInfo plInfo:  m_plInfoList) {
			// ...adding each to the display.
			displayPublicLink(plInfo);
		}
	}
	
	/**
	 * Called if the user cancels the dialog.
	 * 
	 * Implements the EditCanceledHandler.editCanceled() method. 
	 */
	@Override
	public boolean editCanceled() {
		m_mailToCallback.onCancel();	// Tell the caller the dialog was canceled...
		return true;					// ...and let it close.
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
	 * Asynchronously populates the contents of the dialog.
	 */
	private void populateDlgAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		});
	}

	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Turn off any scrolling currently in force...
		m_linksPanel.addStyleName(      "vibe-mailToMultiplePublicLinksSelectDlg-scrollLimit");	// Limit on the VerticalPanel...
		m_linksScroller.removeStyleName("vibe-mailToMultiplePublicLinksSelectDlg-scrollLimit");	// ...not the ScrollPanel.
		
		// ...add the links to the dialog...
		displayPublicLinks();
		
		// ...and show the dialog centered on the screen.
		center();
	}

	/*
	 * Asynchronously runs the given instance of the mail to multiple
	 * public links select dialog.
	 */
	private static void runDlgAsync(final MailToMultiplePublicLinksSelectDlg mtmplsDlg, final List<MailToPublicLinkInfo> plInfoList, final MailToMultiplePublicLinksSelectCallback mailToCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				mtmplsDlg.runDlgNow(plInfoList, mailToCallback);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the mail to multiple
	 * public links select dialog.
	 */
	private void runDlgNow(List<MailToPublicLinkInfo> plInfoList, MailToMultiplePublicLinksSelectCallback mailToCallback) {
		// Store the parameters...
		m_plInfoList     = plInfoList;
		m_mailToCallback = mailToCallback;

		// Did we get any public links to run against?
		int c = (GwtClientHelper.hasItems(m_plInfoList) ? m_plInfoList.size() : 0);
		if (0 == c) {
			// No!  That should never happen.  Tell the user about the
			// problem and bail.
			GwtClientHelper.deferredAlert(m_messages.mailToMultiplePublicLinksSelect_InternalError_NoLinks());
			m_mailToCallback.onCancel();
			return;
		}

		// If we got only one public link...
		if (1 == c) {
			// ...simply act like that's the one the user selected.
			m_mailToCallback.onSelect(m_plInfoList.get(0));
			return;
		}

		// If we get here, the public links we received are valid!
		// Populate the dialog.
		populateDlgAsync();
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the mail to multiple public links select dialog and perform   */
	/* some operation on it.                                         */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the mail to multiple public
	 * links select dialog asynchronously after it loads. 
	 */
	public interface MailToMultiplePublicLinksSelectDlgClient {
		void onSuccess(MailToMultiplePublicLinksSelectDlg mtmplsDlg);
		void onUnavailable();
	}

	/**
	 * Callback interface for the caller get notified about what link
	 * the user selected to mail.
	 */
	public interface MailToMultiplePublicLinksSelectCallback {
		void onCancel();
		void onSelect(MailToPublicLinkInfo plInfo);
	}

	/*
	 * Asynchronously loads the MailToMultiplePublicLinksSelectDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// createAsync() parameters.
			final MailToMultiplePublicLinksSelectDlgClient	mtmplsDlgClient,
			
			// initAndShow)_ parameters,
			final MailToMultiplePublicLinksSelectDlg		mtmplsDlg,
			final List<MailToPublicLinkInfo>				plInfoList,
			final MailToMultiplePublicLinksSelectCallback	mailToCallback) {
		GWT.runAsync(MailToMultiplePublicLinksSelectDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_MailToMultiplePublicLinksSelectDlg());
				if (null != mtmplsDlgClient) {
					mtmplsDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != mtmplsDlgClient) {
					// Yes!  Create it and return it via the callback.
					MailToMultiplePublicLinksSelectDlg mtmplsDlg = new MailToMultiplePublicLinksSelectDlg();
					mtmplsDlgClient.onSuccess(mtmplsDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(mtmplsDlg, plInfoList, mailToCallback);
				}
			}
		});
	}
	
	/**
	 * Loads the MailToMultiplePublicLinksSelectDlg split point and
	 * returns an instance of it via the callback.
	 * 
	 * @param mtmplsDlgClient
	 */
	public static void createAsync(MailToMultiplePublicLinksSelectDlgClient mtmplsDlgClient) {
		doAsyncOperation(mtmplsDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the mail to multiple public links select
	 * dialog.
	 * 
	 * @param mtmplsDlg
	 * @param plInfoList
	 * @param mailToCallback
	 */
	public static void initAndShow(MailToMultiplePublicLinksSelectDlg mtmplsDlg, List<MailToPublicLinkInfo> plInfoList, MailToMultiplePublicLinksSelectCallback mailToCallback) {
		doAsyncOperation(null, mtmplsDlg, plInfoList, mailToCallback);
	}
}
