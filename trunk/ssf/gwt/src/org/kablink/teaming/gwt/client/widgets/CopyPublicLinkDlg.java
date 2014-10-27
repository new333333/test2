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
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent.Change;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetPublicLinksCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PublicLinksRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PublicLinkInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
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
import com.google.gwt.user.client.ui.TextBox;

/**
 * Implements a Copy Public Link dialog.
 *  
 * @author drfoster@novell.com
 */
public class CopyPublicLinkDlg extends DlgBox {
	private boolean						m_createImmediate;	// true -> Immediately create the public links.  false -> The user needs to press a button to do so.
	private GwtTeamingFilrImageBundle	m_filrImages;		// Access to Filr's images.
	private GwtTeamingImageBundle		m_images;			// Access to Vibe's images.
	private GwtTeamingMessages			m_messages;			// Access to Vibe's messages.
	private Image						m_headerImg;		// Image in the dialog's header representing what we're generating links for. 
	private Label						m_headerNameLabel;	// Name of what we're generating links for.
	private Label						m_headerPathLabel;	// Path to what we're generating links for, if there's only a single item.
	private Label						m_hintTail;			// Label that makes up the end of the hint just below the dialogs header. 
	private List<EntityId>				m_entityIds;		// List<EntityId> of the entities whose links are to be copied.
	private ScrollPanel					m_linksScroller;	// The ScrollPanel that contains the links.
	private String						m_imagesPath;		// Path to Vibe's images.
	private VibeFlowPanel				m_contentPanel;		// The panel containing the content of the dialog below the header.
	private VibeVerticalPanel			m_linksPanel;		// The panel containing the links themselves.
	
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
		// Create the panel to be used for the dialog content (below
		// the header) and add it to the main panel.
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

		// ...add an Image for whatever's selected...
		m_headerImg = new Image();
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
		// Show a busy indicator while we get the links from the
		// server.
		showReadingInProgress();

		// ...and request the links.
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
				// Extract the link information from the response.
				PublicLinksRpcResponseData plData = ((PublicLinksRpcResponseData) response.getResponseData());
				
				// Did we get any messages (errors, ...) from the
				// request?
				int c = plData.getTotalMessageCount();
				if (0 < c) {
					// Yes!  Display them.
					GwtClientHelper.displayMultipleErrors(
						m_messages.copyPublicLink_Error_ReadErrors(),
						plData.getErrorList());
				}

				// If we got any links, display them.  Otherwise,
				// display the create links button again.
				Map<String, List<PublicLinkInfo>> plMap = plData.getPublicLinksMap();
				if ((null != plMap) && (!(plMap.isEmpty())))
				     displayPublicLinks(plMap);
				else showCreateLinksButton();
				
				// The share has been copied, fire an event telling
				// everybody.
				GwtTeaming.fireEventAsync(new ContentChangedEvent(Change.SHARING));
			}
		});
	}

	/*
	 * Displays an individual link in the list.
	 */
	private void displayPublicLink(PublicLinkInfo pl, boolean showFileInfo) {
		// Do we need to show per file information?  (We do processing
		// multiple entities.  We don't with a single entity.)
		if (showFileInfo) {
			// Yes!  Create the panel for the header...
			VibeFlowPanel fiPanel = new VibeFlowPanel();
			fiPanel.addStyleName("vibe-copyPublicLinkDlg-linksHeaderPanel");
			m_linksPanel.add(fiPanel);

			// ...add an Image for entity...
			String imageUrl = pl.getImageUrl();
			if (GwtClientHelper.hasString(imageUrl))
			     imageUrl = (m_imagesPath + imageUrl);
			else imageUrl = m_filrImages.entry().getSafeUri().asString();
			Image fiImg = GwtClientHelper.buildImage(imageUrl);
			fiPanel.add(fiImg);

			// ...add widgets for the name...
			VibeFlowPanel fiNamePanel = new VibeFlowPanel();
			fiNamePanel.addStyleName("vibe-copyPublicLinkDlg-linksHeaderNamePanel");
			Label fiNameLabel = new Label(pl.getTitle());
			fiNameLabel.addStyleName("vibe-copyPublicLinkDlg-linksHeaderNameLabel");
			fiNamePanel.add(fiNameLabel);
			
			// ...and add widgets for the path.
			Label fiPathLabel = new Label(pl.getPath());
			fiPathLabel.addStyleName("vibe-copyPublicLinkDlg-linksHeaderPathLabel");
			fiNamePanel.add(fiPathLabel);
			fiPanel.add(fiNamePanel);
		}

		// If we have a view URL...
		String  url     = pl.getViewUrl();
		boolean hasView = GwtClientHelper.hasString(url);
		if (hasView) {
			// ...add that to the display...
			displayPublicLinkUrl(
				m_messages.copyPublicLink_ViewFileLink(),
				url,
				(!showFileInfo));	// true -> Display a spacer above the URL.  false -> Don't.
		}
		
		// ...and if we have a download URL...
		url = pl.getDownloadUrl();
		if (GwtClientHelper.hasString(url)) {
			// ...add that to the display.
			displayPublicLinkUrl(
				m_messages.copyPublicLink_DownloadFileLink(),
				url,
				((!showFileInfo) && (!hasView)));	// true -> Display a spacer above the URL.  false -> Don't.
		}
	}
	
	/*
	 * Displays the links from the map.
	 */
	private void displayPublicLinks(Map<String, List<PublicLinkInfo>> plMap) {
		// Clear the content panel...
		m_linksPanel.clear();
		
		// ...and adjust the sizes for scrolling.
		m_linksPanel.removeStyleName("vibe-copyPublicLinkDlg-scrollLimit");	// Limit on the ScrollPanel...
		m_linksScroller.addStyleName("vibe-copyPublicLinkDlg-scrollLimit");	// ...not the VerticalPanel.
		
		// Scan the files...
		boolean     showFileInfo = (1 < m_entityIds.size());
		Set<String> plKeys       = plMap.keySet();
		for (String key:  plKeys) {
			// ...and if there are any links for a file...
			List<PublicLinkInfo> plList = plMap.get(key);
			if (GwtClientHelper.hasItems(plList)) {
				// ...scan them...
				boolean perFileShowInfo = showFileInfo;
				for (PublicLinkInfo pl:  plList) {
					// ...adding each to the display.
					displayPublicLink(pl, perFileShowInfo);
					perFileShowInfo = false;	// Only show it for this first set of links.
				}
			}
		}
	}
	
	/*
	 * Displays an individual link URL in the ScrollPanel.
	 */
	private void displayPublicLinkUrl(final String urlLabel, final String url, boolean topSpacer) {
		// Add a label for the URL...
		Label label = new Label(urlLabel);
		label.addStyleName("vibe-copyPublicLinkDlg-linksLabel");
		if (topSpacer) {
			label.addStyleName("vibe-copyPublicLinkDlg-linksLabel2");
		}
		m_linksPanel.add(label);

		// ...add a text box the user can easily select from...
		final TextBox linkInput = new TextBox();
		linkInput.addStyleName("vibe-copyPublicLinkDlg-linksInput");
		m_linksPanel.add(linkInput);
		linkInput.setValue(url);
		linkInput.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				linkInput.selectAll();
			}
		});
		linkInput.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				// ...and if the user happens to change the link (since
				// ...it's an INPUT, they CAN edit it), we simply
				// ...restore it back to its initial value.
				linkInput.setValue(url);
				linkInput.selectAll();
			}
		});
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
					m_messages.rpcFailure_GetFolderEntry());
			}
	
			@Override
			public void onSuccess(VibeRpcResponse response) {
				final GwtFolderEntry feInfo = ((GwtFolderEntry) response.getResponseData());
				if (null != feInfo) {
					GwtClientHelper.deferCommand(new ScheduledCommand() {
						@Override
						public void execute() {
							// Update the name of the entity in the
							// header...
							m_headerNameLabel.setText( feInfo.getEntryName()       );
							m_headerPathLabel.setText( feInfo.getParentBinderName());
							m_headerPathLabel.setTitle(feInfo.getParentBinderName());
							
							// ...set an appropriate image in the
							// ...header...
							String imgUrl = feInfo.getFileImgUrl();
							if (GwtClientHelper.hasString(imgUrl))
							     imgUrl = (m_imagesPath + imgUrl);
							else imgUrl = m_filrImages.entry_large().getSafeUri().asString();
							m_headerImg.setUrl(imgUrl);

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
			hintTail = m_messages.copyPublicLink_HintMultiple();
		}
		else {
			hintTail = m_messages.copyPublicLink_HintSingle();
		}
		m_hintTail.getElement().setInnerText(hintTail);

		// Turn off any scrolling currently in force...
		m_linksPanel.addStyleName(      "vibe-copyPublicLinkDlg-scrollLimit");	// Limit on the VerticalPanel...
		m_linksScroller.removeStyleName("vibe-copyPublicLinkDlg-scrollLimit");	// ...not the ScrollPanel.
		
		// ...create the dialog's contents in an empty state that
		// ...allows the links to be created from scratch...
		showCreateLinksButton();

		// ...and show the dialog centered on the screen.
		center();
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		// Create the dialog's content.
		if (1 == m_entityIds.size())
		     loadPart1Async(m_entityIds.get(0));
		else populateDlgFromInfoAsync();
	}

	/*
	 * Asynchronously runs the given instance of the copy public link
	 * dialog.
	 */
	private static void runDlgAsync(final CopyPublicLinkDlg cplDlg, final String caption, final List<EntityId> entityIds, final boolean createImmediate) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				cplDlg.runDlgNow(caption, entityIds, createImmediate);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the copy public link
	 * dialog.
	 */
	private void runDlgNow(String caption, List<EntityId> entityIds, boolean createImmediate) {
		// Store the parameters...
		setCaption(caption);
		m_entityIds       = entityIds;
		m_createImmediate = createImmediate;

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
		// Clear the content of the links panel. 
		m_linksPanel.clear();
		m_linksPanel.setWidth("100%");

		// If we're supposed to immediately create the public links...
		if (m_createImmediate) {
			// ...simply act like the create links push button was
			// ...pressed...
			createLinksAsync();
		}
		
		else {
			// ...otherwise, add the create links push button.
			Button createLinksBtn = new Button(m_messages.copyPublicLink_Button());
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
			final List<EntityId>	entityIds,
			final boolean			createImmediate) {
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
					runDlgAsync(cplDlg, caption, entityIds, createImmediate);
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
		doAsyncOperation(cplDlgClient, null, null, null, false);
	}
	
	/**
	 * Initializes and shows the copy public link dialog.
	 * 
	 * @param cplDlg
	 * @param caption
	 * @param entityIds
	 * @param createImmediate
	 */
	public static void initAndShow(CopyPublicLinkDlg cplDlg, String caption, List<EntityId> entityIds, boolean createImmediate) {
		doAsyncOperation(null, cplDlg, caption, entityIds, createImmediate);
	}
	
	public static void initAndShow(CopyPublicLinkDlg cplDlg, String caption, List<EntityId> entityIds) {
		// Always use the initial form of the method.
		initAndShow(cplDlg, caption, entityIds, false);	// false -> Don't immediately create the public links.
	}
}
