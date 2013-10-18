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
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetPublicLinksCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PublicLinksRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.PublicLinksRpcResponseData.PublicLinkInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;

/**
 * Implements Vibe's Copy Public Link dialog.
 *  
 * @author drfoster@novell.com
 */
public class CopyPublicLinkDlg extends DlgBox {
	private GwtTeamingFilrImageBundle	m_filrImages;		// Access to Filr's images.
	private GwtTeamingImageBundle		m_images;			// Access to Vibe's images.
	private GwtTeamingMessages			m_messages;			// Access to Vibe's messages.
	private Image						m_headerImg;		//
	private Label						m_headerNameLabel;	//
	private Label						m_headerPathLabel;	//
	private Label						m_hintTail;			//
	private List<EntityId>				m_entityIds;		// List<EntityId> of the entities whose public links are to be copied.
	private ScrollPanel					m_linksScroller;	//
	private String						m_imagesPath;		//
	private String						m_product;			//
	private VibeFlowPanel				m_contentPanel;		//
	private VibeVerticalPanel			m_linksPanel;		//
	
	private final static int	SCROLL_WHEN	= 3;	// Count of items we display when scroll bars are enabled.
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private CopyPublicLinkDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_messages   = GwtTeaming.getMessages();
		m_filrImages = GwtTeaming.getFilrImageBundle();
		m_images     = GwtTeaming.getImageBundle();
		m_imagesPath = GwtClientHelper.getRequestInfo().getImagesPath();
		m_product    = GwtClientHelper.getProductName();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			"",								// Dialog caption set when the dialog runs.
			getSimpleSuccessfulHandler(),	// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),		// The dialog's EditCanceledHandler.
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
		mainPanel.addStyleName("vibe-copyPublicLinkDlg-mainPanel");

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
		// Create the panel to be used for the dialog content and add
		// it to the main panel.
		m_contentPanel = new VibeFlowPanel();
		m_contentPanel.addStyleName("vibe-copyPublicLinkDlg-contentPanel");
		mainPanel.add(m_contentPanel);
		
		// Add a ScrollPanel for the links.
		m_linksScroller = new ScrollPanel();
		m_linksScroller.addStyleName("vibe-copyPublicLinkDlg-scrollPanel");
		m_contentPanel.add(m_linksScroller);

		// Add a vertical panel for the ScrollPanel's content.
		m_linksPanel = new VibeVerticalPanel(null, null);
		m_linksPanel.addStyleName("vibe-copyPublicLinkDlg-linksPanel");
		m_linksScroller.add(m_linksPanel);
	}
	
	/*
	 * Create the controls needed in the header.
	 */
	private void createHeaderPanel(Panel mainPanel) {
		// Create the panel for the header...
		VibeFlowPanel headerPanel = new VibeFlowPanel();
		headerPanel.addStyleName("vibe-copyPublicLinkDlg-headerPanel");
		mainPanel.add(headerPanel);

		// ...add and Image for whatever's selected...
		m_headerImg = new Image();
		m_headerImg.addStyleName("vibe-copyPublicLinkDlg-headerImg");
		headerPanel.add(m_headerImg);

		// ...add widgets for the name...
		VibeFlowPanel namePanel = new VibeFlowPanel();
		namePanel.addStyleName("vibe-copyPublicLinkDlg-headerNamePanel");
		m_headerNameLabel = new Label();
		m_headerNameLabel.addStyleName("vibe-copyPublicLinkDlg-headerNameLabel");
		namePanel.add(m_headerNameLabel);
		
		// ...add widgets for the path...
		m_headerPathLabel = new Label();
		m_headerPathLabel.addStyleName("vibe-copyPublicLinkDlg-headerPathLabel");
		namePanel.add(m_headerPathLabel);
		headerPanel.add(namePanel);
		
		// ...and add widgets for the hint.
		VibeFlowPanel hintPanel = new VibeFlowPanel();
		hintPanel.addStyleName("vibe-copyPublicLinkDlg-hintPanel");
		mainPanel.add(hintPanel);
		Label hintStart = new Label(m_messages.copyPublicLink(m_product));
		hintStart.addStyleName("vibe-copyPublicLinkDlg-hintStart");
		hintPanel.add(hintStart);
		m_hintTail = new Label();
		m_hintTail.addStyleName("vibe-copyPublicLinkDlg-hintTail");
		hintPanel.add(m_hintTail);
	}

	/*
	 * Asynchronously creates the links for the entities.
	 */
	private void createLinksAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				createLinksNow();
			}
		});
	}
	
	/*
	 * Synchronously creates the links for the entities.
	 */
	private void createLinksNow() {
		// Clear the content of the links panel (it should only be the
		// create push button)... 
		m_linksPanel.clear();

		// ...add a busy indicator while we get the links from the
		// ...server...
		showReadingInProgress();
		
		GetPublicLinksCmd cmd = new GetPublicLinksCmd(m_entityIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetPublicLinks());
			}
	
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Extract the public link information from the
				// response.
				PublicLinksRpcResponseData plData = ((PublicLinksRpcResponseData) response.getResponseData());
				
				// Did we get any messages from the request?
				int c = plData.getTotalMessageCount();
				if (0 < c) {
					// Yes!  Display them.
					GwtClientHelper.displayMultipleErrors(
						m_messages.copyPublicLink_Error_ReadErrors(),
						plData.getErrorList());
				}

				// If we got any public links, display them.
				// Otherwise, display the create links button again.
				Map<EntityId, PublicLinkInfo> plMap = plData.getPublicLinksMap();
				if ((null != plMap) && (!(plMap.isEmpty())))
				     displayPublicLinks(plMap);
				else showCreateLinksButton();
			}
		});
	}

	/*
	 * Displays an individual public link in the list.
	 */
	private void displayPublicLink(PublicLinkInfo pl) {
//!		...this needs to be implemented...
	}
	
	/*
	 * Displays the public links in the map.
	 */
	private void displayPublicLinks(Map<EntityId, PublicLinkInfo> plMap) {
		// Clear the content panel.
		m_linksPanel.clear();
		m_linksPanel.removeStyleName("vibe-copyPublicLinkDlg-scrollLimit");
		
		// Count the links that we need to display.
		int linkCount = 0;
		for (EntityId eid:  m_entityIds) {
			PublicLinkInfo pl = plMap.get(eid);
			if (GwtClientHelper.hasString(pl.getViewUrl()))     linkCount += 1;
			if (GwtClientHelper.hasString(pl.getDownloadUrl())) linkCount += 1;
		}

		// Enable/disable scrolling, based on the number of links.
		if (linkCount >= SCROLL_WHEN)
		     m_linksScroller.addStyleName(   "vibe-copyPublicLinkDlg-scrollLimit");
		else m_linksScroller.removeStyleName("vibe-copyPublicLinkDlg-scrollLimit");

		// Scan the links...
		for (EntityId eid:  m_entityIds) {
			// ...adding each to the display.
			displayPublicLink(plMap.get(eid));
		}
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
	private void loadPart1Async(final EntityId entityId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now(entityId);
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void loadPart1Now(final EntityId entityId) {
		GetEntryCmd cmd = new GetEntryCmd(null, entityId.getEntityId().toString());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetFolderEntry(),
					entityId.getEntityId());
			}
	
			@Override
			public void onSuccess(VibeRpcResponse response) {
				final GwtFolderEntry feInfo = ((GwtFolderEntry) response.getResponseData());
				if (null != feInfo) {
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							// Update the name of the entity in the
							// header....
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
							populateDlgFromInfoNow();
						}
					});
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
	 * Asynchronously populates the contents of the dialog with the
	 * information obtained from the server.
	 */
	private void populateDlgFromInfoAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgFromInfoNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog with the
	 * information obtained from the server.
	 */
	private void populateDlgFromInfoNow() {
		// Are we dealing with more than one entity?
		String hintTail;
		int numItems = m_entityIds.size();
		if (1 < numItems) {
			// Yes!  Use the generic entry image and multiple items
			// label.
			m_headerNameLabel.setText(m_messages.copyPublicLink_MultipleItems(numItems));
			m_headerPathLabel.getElement().setInnerHTML("&nbsp;");
			ImageResource imgResource = m_filrImages.entry_large();
			m_headerImg.setUrl(imgResource.getSafeUri().asString());
			hintTail = m_messages.copyPublicLink_HintMultiple(m_product);
		}
		else {
			hintTail = m_messages.copyPublicLink_HintSingle(m_product);
		}
		m_headerImg.setVisible(true);
		m_hintTail.getElement().setInnerText(hintTail);

		// Turn off any scrolling currently in force.
		m_linksScroller.removeStyleName("vibe-copyPublicLinkDlg-scrollLimit");
		
		// Create the dialog's contents in an empty state that allows
		// the links to be created from scratch.
		showCreateLinksButton();

		// ...and show the dialog centered on the screen.
		center();
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Create the dialogs content.
		if (1 == m_entityIds.size())
		     loadPart1Async(m_entityIds.get(0));
		else populateDlgFromInfoAsync();
	}

	/*
	 * Asynchronously runs the given instance of the copy public link
	 * dialog.
	 */
	private static void runDlgAsync(final CopyPublicLinkDlg cplDlg, final String caption, final List<EntityId> entityIds) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				cplDlg.runDlgNow(caption, entityIds);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the copy public link
	 * dialog.
	 */
	private void runDlgNow(String caption, List<EntityId> entityIds) {
		// Store the parameters...
		setCaption(caption);
		m_entityIds = entityIds;

		// Did we get any entities to run against?
		int c = (GwtClientHelper.hasItems(m_entityIds) ? m_entityIds.size() : 0);
		if (0 == c) {
			// No!  That should never happen.  Tell the user about the
			// problem and bail.
			GwtClientHelper.deferredAlert(m_messages.copyPublicLink_InternalError_NoEntries());
			return;
		}

		// Scan the entities we were given.
		for (EntityId eid:  m_entityIds) {
			// Is this entity other than an entry?
			if (!(eid.isEntry())) {
				// Yes!  That should never happen.  Tell the user about
				// the problem and bail.
				GwtClientHelper.deferredAlert(m_messages.copyPublicLink_InternalError_NotAnEntry());
				return;
			}
		}

		// If we get here, the entities we received are valid!
		// Populate the dialog.
		populateDlgAsync();
	}

	/*
	 * Shows the widgets for the user to create the links.
	 */
	private void showCreateLinksButton() {
		// Clear the content of the links panel... 
		m_linksPanel.clear();
		m_linksPanel.setWidth("100%");
		m_linksPanel.addStyleName("vibe-copyPublicLinkDlg-scrollLimit");

		// ...and add the create links push button.
		Button createLinksBtn = new Button(m_messages.copyPublicLink_Button(m_product));
		createLinksBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				createLinksAsync();
			}
		});
		createLinksBtn.addStyleName("vibe-copyPublicLinkDlg-createButton");
		m_linksPanel.add(createLinksBtn);
		m_linksPanel.setCellHorizontalAlignment(createLinksBtn, HasHorizontalAlignment.ALIGN_CENTER);
		m_linksPanel.setCellVerticalAlignment(  createLinksBtn, HasVerticalAlignment.ALIGN_MIDDLE  );
		m_linksPanel.setCellWidth(              createLinksBtn, "100%"                             );
	}
	
	/*
	 * Shows the widgets that indicate we're reading the links from the
	 * server.
	 */
	private void showReadingInProgress() {
		// Clear the content of the links panel (it should only be the
		// create push button)... 
		m_linksPanel.clear();
		m_linksPanel.setWidth("100%");
		m_linksPanel.addStyleName("vibe-copyPublicLinkDlg-scrollLimit");

		// ...and add a busy indicator that we display while we get the
		// ...links from the server.
		VibeFlowPanel readingPanel = new VibeFlowPanel();
		readingPanel.addStyleName("vibe-copyPublicLinkDlg-readingPanel");
		Image readingSpinner = GwtClientHelper.buildImage(m_images.spinner32().getSafeUri().asString());
		readingSpinner.addStyleName("vibe-copyPublicLinkDlg-readingImage");
		readingPanel.add(readingSpinner);
		Label readingLabel = new Label(m_messages.copyPublicLink_Reading());
		readingLabel.addStyleName("vibe-copyPublicLinkDlg-readingLabel");
		readingPanel.add(readingLabel);
		m_linksPanel.add(readingPanel);
		m_linksPanel.setCellHorizontalAlignment(readingPanel, HasHorizontalAlignment.ALIGN_CENTER);
		m_linksPanel.setCellVerticalAlignment(  readingPanel, HasVerticalAlignment.ALIGN_MIDDLE  );
		m_linksPanel.setCellWidth(              readingPanel, "100%"                             );
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the copy public link dialog and perform some operation on it. */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the copy public link dialog
	 * asynchronously after it loads. 
	 */
	public interface CopyPublicLinkDlgClient {
		void onSuccess(CopyPublicLinkDlg cplDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the CopyPublicLinkDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final CopyPublicLinkDlgClient cplDlgClient,
			
			// initAndShow parameters,
			final CopyPublicLinkDlg	cplDlg,
			final String			caption,
			final List<EntityId>	entityIds) {
		GWT.runAsync(CopyPublicLinkDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_CopyPublicLinkDlg());
				if (null != cplDlgClient) {
					cplDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != cplDlgClient) {
					// Yes!  Create it and return it via the callback.
					CopyPublicLinkDlg cplDlg = new CopyPublicLinkDlg();
					cplDlgClient.onSuccess(cplDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(cplDlg, caption, entityIds);
				}
			}
		});
	}
	
	/**
	 * Loads the CopyPublicLinkDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param cplDlgClient
	 */
	public static void createAsync(CopyPublicLinkDlgClient cplDlgClient) {
		doAsyncOperation(cplDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the copy public link dialog.
	 * 
	 * @param cplDlg
	 * @param caption
	 * @param entityIds
	 */
	public static void initAndShow(CopyPublicLinkDlg cplDlg, String caption, List<EntityId> entityIds) {
		doAsyncOperation(null, cplDlg, caption, entityIds);
	}
}
