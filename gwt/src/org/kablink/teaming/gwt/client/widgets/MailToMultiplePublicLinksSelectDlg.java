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
package org.kablink.teaming.gwt.client.widgets;

import java.util.List;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PublicLinkInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Implements a Mail To Multiple Public Links Select dialog.
 *  
 * @author drfoster@novell.com
 */
public class MailToMultiplePublicLinksSelectDlg extends DlgBox implements EditCanceledHandler {
	private EntityId								m_entityId;			// The entity whose links are being mailed.
	private FlexCellFormatter						m_linksFCF;			// The FlexCellFormatter for m_linksPanel.
	private GwtTeamingMessages						m_messages;			// Access to Vibe's messages.
	private Image									m_headerImg;		// Image in the dialog's header representing what we're mailing links for. 
	private Label									m_headerNameLabel;	// Name of what we're mailing links for.
	private Label									m_headerPathLabel;	// Path to what we're mailing links for.
	private List<PublicLinkInfo>					m_plInfoList;		// The list of mail to public links the user can choose from.
	private MailToMultiplePublicLinksSelectCallback	m_mailToCallback;	// The callback to let the caller know what the user selects.
	private RowFormatter							m_linksRF;			// The RowFormatter for m_linksPanel.
	private ScrollPanel								m_linksScroller;	// The ScrollPanel that contains the links.
	private String									m_imagesPath;		// Path to Vibe's images.
	private VibeFlexTable							m_linksPanel;		// The panel containing the links themselves.
	private VibeFlowPanel							m_contentPanel;		// The panel containing the content of the dialog below the header.

	// Column indexes of the columns holding the links.
	private final static int COLINDEX_SHARED_ON	= 0;	//
	private final static int COLINDEX_EXPIRES	= 1;	//
	private final static int COLINDEX_NOTE		= 2;	//
	
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

		// ...initialize everything that requires it...
		m_messages   = GwtTeaming.getMessages();
		m_imagesPath = GwtClientHelper.getRequestInfo().getImagesPath();
	
		// ...and create the dialog's content.
		createAllDlgContent(
				"",							// The dialog's caption is set when the dialog is shown.
			getSimpleSuccessfulHandler(),	// The dialog's EditSuccessfulHandler.
			this,							// The dialog's EditCanceledHandler.
			null);							// Create callback data.  Unused. 
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
	 * Create the controls needed in the main content.
	 */
	private void createContentPanel(Panel mainPanel) {
		// Create the panel to be used for the dialog content (below
		// the header) and add it to the main panel...
		m_contentPanel = new VibeFlowPanel();
		m_contentPanel.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-contentPanel");
		mainPanel.add(m_contentPanel);
		
		// ...add a ScrollPanel for the links...
		m_linksScroller = new ScrollPanel();
		m_linksScroller.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-scrollPanel");
		m_contentPanel.add(m_linksScroller);

		// ...and add a panel for the ScrollPanel's content.
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

		// ...add an Image for whatever we mailing links for...
		m_headerImg = new Image();
		headerPanel.add(m_headerImg);

		// ...add widgets for the name...
		VibeFlowPanel namePanel = new VibeFlowPanel();
		namePanel.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-headerNamePanel");
		m_headerNameLabel = new Label();
		m_headerNameLabel.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-headerNameLabel");
		namePanel.add(m_headerNameLabel);
		
		// ...add widgets for the path...
		m_headerPathLabel = new Label();
		m_headerPathLabel.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-headerPathLabel");
		namePanel.add(m_headerPathLabel);
		headerPanel.add(namePanel);
		
		// ...and add widgets for the hint.
		VibeFlowPanel hintPanel = new VibeFlowPanel();
		hintPanel.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-hintPanel");
		mainPanel.add(hintPanel);
		Label hintStart = new Label(m_messages.mailToMultiplePublicLinksSelect());
		hintStart.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-hintStart");
		hintPanel.add(hintStart);
		Label hintTail = new Label(m_messages.mailToMultiplePublicLinksSelect_HeaderTail());
		hintTail.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-hintTail");
		hintPanel.add(hintTail);
	}

	/*
	 * Displays an individual link in the list.
	 */
	private void displayPublicLink(final PublicLinkInfo pl) {
		// If this is the first public link...
		int row = m_linksPanel.getRowCount();
		if (0 == row) {
			// ...add a header row to the table.
			m_linksPanel.setText(  row, COLINDEX_SHARED_ON, m_messages.mailToMultiplePublicLinksSelect_Column_SharedOn()); m_linksFCF.addStyleName(row, COLINDEX_SHARED_ON, "vibe-mailToMultiplePublicLinksSelectDlg-tableColumnCell"         );
			m_linksPanel.setText(  row, COLINDEX_EXPIRES,   m_messages.mailToMultiplePublicLinksSelect_Column_Expires());  m_linksFCF.addStyleName(row, COLINDEX_EXPIRES,   "vibe-mailToMultiplePublicLinksSelectDlg-tableColumnCell"         );
			m_linksPanel.setText(  row, COLINDEX_NOTE,      m_messages.mailToMultiplePublicLinksSelect_Column_Note());     m_linksFCF.addStyleName(row, COLINDEX_NOTE,      "vibe-mailToMultiplePublicLinksSelectDlg-tableColumnCell rightend");
			m_linksFCF.setWidth(   row, COLINDEX_NOTE,      "100%");
			m_linksRF.addStyleName(row, "columnhead vibe-mailToMultiplePublicLinksSelectDlg-tableHeader");
			row += 1;
		}

		// ...and add row for the link's data.
		String sharedOn = pl.getSharedOn();   if (null == sharedOn) sharedOn = "";
		String note     = pl.getComment();    if (!(GwtClientHelper.hasString(note)))    note    = m_messages.mailToMultiplePublicLinksSelect_NoNote();
		String expires  = pl.getExpiration(); if (!(GwtClientHelper.hasString(expires))) expires = m_messages.mailToMultiplePublicLinksSelect_Never();
		Label sharedOnLink = new Label(sharedOn);
		sharedOnLink.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-tableLink");
		sharedOnLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				m_mailToCallback.onSelect(pl);
				hide();
			}
		});
		m_linksPanel.setWidget(    row, COLINDEX_SHARED_ON, sharedOnLink); m_linksFCF.addStyleName(row, COLINDEX_SHARED_ON, "vibe-mailToMultiplePublicLinksSelectDlg-tableCell"    );	// No-wrap.
		m_linksPanel.setText(      row, COLINDEX_EXPIRES,   expires     ); m_linksFCF.addStyleName(row, COLINDEX_EXPIRES,   "vibe-mailToMultiplePublicLinksSelectDlg-tableCell"    );	// No-wrap.
		m_linksPanel.setText(      row, COLINDEX_NOTE,      note        ); m_linksFCF.addStyleName(row, COLINDEX_NOTE,      "vibe-mailToMultiplePublicLinksSelectDlg-tableCellNote");	// Allows wrapping.
		if (pl.isExpired()) {
			m_linksFCF.addStyleName(row, COLINDEX_EXPIRES, "vibe-mailToMultiplePublicLinksSelectDlg-tableCellExpires");
		}
		m_linksRF.addStyleName(    row, "regrow vibe-mailToMultiplePublicLinksSelectDlg-tableRow");
		m_linksRF.setVerticalAlign(row, HasVerticalAlignment.ALIGN_TOP);
	}
	
	/*
	 * Displays the links from the list.
	 */
	private void displayPublicLinks() {
		// Clear the content panel...
		m_linksPanel.removeAllRows();
		
		// ...and adjust the sizes for scrolling.
		m_linksPanel.removeStyleName("vibe-mailToMultiplePublicLinksSelectDlg-scrollLimit");	// Limit on the ScrollPanel...
		m_linksScroller.addStyleName("vibe-mailToMultiplePublicLinksSelectDlg-scrollLimit");	// ...not the VerticalPanel.
		
		// Scan the links...
		for (PublicLinkInfo plInfo:  m_plInfoList) {
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
		return true;					// ...and let the dialog close.
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
	private void loadPart1Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void loadPart1Now() {
		GetEntryCmd cmd = new GetEntryCmd(null, m_entityId.getEntityId().toString());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolderEntry());
			}
	
			@Override
			public void onSuccess(VibeRpcResponse response) {
				final GwtFolderEntry feInfo = ((GwtFolderEntry) response.getResponseData());
				if (null != feInfo) {
					// Update the name of the entity in the header.
					m_headerNameLabel.setText( feInfo.getEntryName()       );
					m_headerPathLabel.setText( feInfo.getParentBinderName());
					m_headerPathLabel.setTitle(feInfo.getParentBinderName());
					
					// If we have a URL for the file image...
					String imgUrl = feInfo.getFileImgUrl();
					if (GwtClientHelper.hasString(imgUrl)) {
						// ...set it into the header.
						m_headerImg.setUrl(m_imagesPath + imgUrl);
					}

					// ...and finish the population.
					populateDlgAsync();
				}
			}
		});
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
	private static void runDlgAsync(final MailToMultiplePublicLinksSelectDlg mtmplsDlg, final EntityId entityId, final List<PublicLinkInfo> plInfoList, final MailToMultiplePublicLinksSelectCallback mailToCallback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				mtmplsDlg.runDlgNow(entityId, plInfoList, mailToCallback);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the mail to multiple
	 * public links select dialog.
	 */
	private void runDlgNow(EntityId entityId, List<PublicLinkInfo> plInfoList, MailToMultiplePublicLinksSelectCallback mailToCallback) {
		// Store the parameters.
		m_entityId       = entityId;
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
		// Set the dialog's caption....
		setCaption(m_messages.mailToMultiplePublicLinksSelect_Caption(c));

		// ...and populate the dialog.
		loadPart1Async();
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
	 * the user selects to mail.
	 */
	public interface MailToMultiplePublicLinksSelectCallback {
		void onCancel();
		void onSelect(PublicLinkInfo plInfo);
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
			final EntityId									entityId,
			final List<PublicLinkInfo>						plInfoList,
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
					runDlgAsync(mtmplsDlg, entityId, plInfoList, mailToCallback);
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
		doAsyncOperation(mtmplsDlgClient, null, null, null, null);
	}
	
	/**
	 * Initializes and shows the mail to multiple public links select
	 * dialog.
	 * 
	 * @param mtmplsDlg
	 * @param entityId
	 * @param plInfoList
	 * @param mailToCallback
	 */
	public static void initAndShow(MailToMultiplePublicLinksSelectDlg mtmplsDlg, EntityId entityId, List<PublicLinkInfo> plInfoList, MailToMultiplePublicLinksSelectCallback mailToCallback) {
		doAsyncOperation(null, mtmplsDlg, entityId, plInfoList, mailToCallback);
	}
}
