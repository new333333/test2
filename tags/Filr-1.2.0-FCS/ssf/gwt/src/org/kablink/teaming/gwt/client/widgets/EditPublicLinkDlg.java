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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.ShareExpirationValueChangedEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent.Change;
import org.kablink.teaming.gwt.client.rpc.shared.BooleanRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteSharesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetPublicLinksCmd;
import org.kablink.teaming.gwt.client.rpc.shared.PublicLinksRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.SaveShareExpirationValueCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PublicLinkInfo;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.ConfirmDlg.ConfirmDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements a Edit Public Link dialog.
 *  
 * @author drfoster@novell.com
 */
public class EditPublicLinkDlg extends DlgBox
	implements
		// Event handlers implemented by this class.
		ShareExpirationValueChangedEvent.Handler
{
	private GwtTeamingFilrImageBundle	m_filrImages;				// Access to Filr's images.
	private GwtTeamingMessages			m_messages;					// Access to Vibe's messages.
	private EntityId					m_entityId;					// EntityId of the entity whose link is being edited.
	private Image						m_headerImg;				// Image in the dialog's header representing what we're editing the links for. 
	private Label						m_headerNameLabel;			// Name of what we're editing the links for.
	private Label						m_headerPathLabel;			// Path to what we're editing links for, if there's only a single item.
	private Label						m_hintTail;					// Label that makes up the end of the hint just below the dialogs header. 
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Event handlers that are currently registered.
	private ScrollPanel					m_linksScroller;			// The ScrollPanel that contains the links.
	private String						m_imagesPath;				// Path to Vibe's images.
	private ShareExpirationWidget		m_expWidget;				// The ShareExpirationWidget for this dialog.
	private VibeFlowPanel				m_contentPanel;				// The panel containing the content of the dialog below the header.
	private VibeVerticalPanel			m_linksPanel;				// The panel containing the links themselves.

	// Used as the attribute name on a ShareExpirationWidget to
	// identify the share.
	private final static String EXPIRATION_SHARE_ID_ATTR	= "n-shareId";
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static final TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
		TeamingEvents.SHARE_EXPIRATION_VALUE_CHANGED,
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EditPublicLinkDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_messages   = GwtTeaming.getMessages();
		m_filrImages = GwtTeaming.getFilrImageBundle();
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
		mainPanel.addStyleName("vibe-editPublicLinkDlg-mainPanel");

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
		m_contentPanel.addStyleName("vibe-editPublicLinkDlg-contentPanel");
		mainPanel.add(m_contentPanel);
		
		// Add a ScrollPanel for the links.
		m_linksScroller = new ScrollPanel();
		m_linksScroller.addStyleName("vibe-editPublicLinkDlg-scrollPanel");
		m_contentPanel.add(m_linksScroller);

		// Add a vertical panel for the ScrollPanel's content.
		m_linksPanel = new VibeVerticalPanel(null, null);
		m_linksPanel.addStyleName("vibe-editPublicLinkDlg-linksPanel");
		m_linksScroller.add(m_linksPanel);
	}
	
	/*
	 * Create the controls needed in the header.
	 */
	private void createHeaderPanel(Panel mainPanel) {
		// Create the panel for the header...
		VibeFlowPanel headerPanel = new VibeFlowPanel();
		headerPanel.addStyleName("vibe-editPublicLinkDlg-headerPanel");
		mainPanel.add(headerPanel);

		// ...add an Image for whatever's selected...
		m_headerImg = new Image();
		headerPanel.add(m_headerImg);

		// ...add widgets for the name...
		VibeFlowPanel namePanel = new VibeFlowPanel();
		namePanel.addStyleName("vibe-editPublicLinkDlg-headerNamePanel");
		m_headerNameLabel = new Label();
		m_headerNameLabel.addStyleName("vibe-editPublicLinkDlg-headerNameLabel");
		namePanel.add(m_headerNameLabel);
		
		// ...add widgets for the path...
		m_headerPathLabel = new Label();
		m_headerPathLabel.addStyleName("vibe-editPublicLinkDlg-headerPathLabel");
		namePanel.add(m_headerPathLabel);
		headerPanel.add(namePanel);
		
		// ...and add widgets for the hint.
		VibeFlowPanel hintPanel = new VibeFlowPanel();
		hintPanel.addStyleName("vibe-editPublicLinkDlg-hintPanel");
		mainPanel.add(hintPanel);
		m_hintTail = new Label();
		m_hintTail.addStyleName("vibe-editPublicLinkDlg-hintTail");
		hintPanel.add(m_hintTail);
	}

	/*
	 * Asynchronously confirms and deletes the file link.
	 */
	private void deleteShareAsync(final PublicLinkInfo pl) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				deleteShareNow(pl);
			}
		});
	}
	
	/*
	 * Synchronously confirms and deletes the file link.
	 */
	private void deleteShareNow(final PublicLinkInfo pl) {
		ConfirmDlg.createAsync(new ConfirmDlgClient() {
			@Override
			public void onUnavailable() {
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess(ConfirmDlg cDlg) {
				ConfirmDlg.initAndShow(
					cDlg,
					new ConfirmCallback() {
						@Override
						public void dialogReady() {
							// Ignored.  We don't really care when the
							// dialog is ready.
						}

						@Override
						public void accepted() {
							// Yes, they're sure!  Delete it.
							GwtClientHelper.deferCommand(new ScheduledCommand() {
								@Override
								public void execute() {
									List<Long> shareIds = new ArrayList<Long>();
									shareIds.add(pl.getShareId());
									DeleteSharesCmd cmd = new DeleteSharesCmd(shareIds);
									GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
										@Override
										public void onFailure(Throwable t) {
											GwtClientHelper.handleGwtRPCFailure(
												t,
												m_messages.rpcFailure_DeleteShares());
										}
								
										@Override
										public void onSuccess(VibeRpcResponse response) {
											// The share has been
											// deleted, fire an event
											// telling everybody and
											// hide the dialog.
											GwtTeaming.fireEventAsync(new ContentChangedEvent(Change.SHARING));
											hide();
										}
									});
								}
							});
						}

						@Override
						public void rejected() {
							// No, they're not sure!
						}
					},
					m_messages.editPublicLink_ConfirmDelete());
			}
		});
	}
	
	/*
	 * Displays an individual link in the list.
	 */
	private void displayPublicLink(final PublicLinkInfo pl, boolean showFileInfo) {
		// Do we need to show per file information?  (We do processing
		// multiple entities.  We don't with a single entity.)
		if (showFileInfo) {
			// Yes!  Create the panel for the header...
			VibeFlowPanel fiPanel = new VibeFlowPanel();
			fiPanel.addStyleName("vibe-editPublicLinkDlg-linksHeaderPanel");
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
			fiNamePanel.addStyleName("vibe-editPublicLinkDlg-linksHeaderNamePanel");
			Label fiNameLabel = new Label(pl.getTitle());
			fiNameLabel.addStyleName("vibe-editPublicLinkDlg-linksHeaderNameLabel");
			fiNamePanel.add(fiNameLabel);
			
			// ...and add widgets for the path.
			Label fiPathLabel = new Label(pl.getPath());
			fiPathLabel.addStyleName("vibe-editPublicLinkDlg-linksHeaderPathLabel");
			fiNamePanel.add(fiPathLabel);
			fiPanel.add(fiNamePanel);
		}

		// If we have a view URL...
		String  url     = pl.getViewUrl();
		boolean hasView = GwtClientHelper.hasString(url);
		if (hasView) {
			// ...add that to the display...
			displayPublicLinkUrl(
				m_messages.editPublicLink_ViewFileLink(),
				url,
				(!showFileInfo));	// true -> Display a spacer above the URL.  false -> Don't.
		}
		
		// ...and if we have a download URL...
		url = pl.getDownloadUrl();
		if (GwtClientHelper.hasString(url)) {
			// ...add that to the display.
			displayPublicLinkUrl(
				m_messages.editPublicLink_DownloadFileLink(),
				url,
				((!showFileInfo) && (!hasView)));	// true -> Display a spacer above the URL.  false -> Don't.
		}
		
		// Finally, add a delete button...
		VibeHorizontalPanel hp = new VibeHorizontalPanel(null, null);
		hp.addStyleName("vibe-editPublicLinkDlg-actionsPanel");
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		m_linksPanel.add(hp);
		Button delButton = new Button(m_messages.editPublicLink_DeleteLink());
		delButton.addStyleName("vibe-editPublicLinkDlg-deleteButton");
		delButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				deleteShareAsync(pl);
			}
		});
		hp.add(delButton);
		
		// ...and an expiration widget.
		m_expWidget = new ShareExpirationWidget("vibe-editPublicLinkDlg-shareExpirationTable");
		m_expWidget.addStyleName("vibe-editPublicLinkDlg-shareExpirationWidget");
		hp.add(m_expWidget);
		m_expWidget.getElement().setAttribute(EXPIRATION_SHARE_ID_ATTR, String.valueOf(pl.getShareId()));
		m_expWidget.init(pl.getExpirationValue());
	}
	
	/*
	 * Displays the links from the map.
	 */
	private void displayPublicLinks(Map<String, List<PublicLinkInfo>> plMap) {
		// Clear the content panel...
		m_linksPanel.clear();
		m_linksPanel.setWidth("100%");
		
		// ...and adjust the sizes for scrolling.
		m_linksPanel.removeStyleName("vibe-editPublicLinkDlg-scrollLimit");	// Limit on the ScrollPanel...
		m_linksScroller.addStyleName("vibe-editPublicLinkDlg-scrollLimit");	// ...not the VerticalPanel.
		
		// Scan the files...
		Set<String> plKeys = plMap.keySet();
		for (String key:  plKeys) {
			// ...and if there are any links for a file...
			List<PublicLinkInfo> plList = plMap.get(key);
			if (GwtClientHelper.hasItems(plList)) {
				// ...scan them...
				for (PublicLinkInfo pl:  plList) {
					// ...adding each to the display.
					displayPublicLink(pl, false);
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
		label.addStyleName("vibe-editPublicLinkDlg-linksLabel");
		if (topSpacer) {
			label.addStyleName("vibe-editPublicLinkDlg-linksLabel2");
		}
		m_linksPanel.add(label);

		// ...add a text box the user can easily select from...
		final TextBox linkInput = new TextBox();
		linkInput.addStyleName("vibe-editPublicLinkDlg-linksInput");
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
							loadPart2Async();
						}
					});
				}
			}
		});
	}
	
	/*
	 * Asynchronously populates the contents of the dialog.
	 */
	private void loadPart2Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		});
	}
	
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void loadPart2Now() {
		GetPublicLinksCmd cmd = new GetPublicLinksCmd(m_entityId);
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

				// Display the public links and show the dialog.
				final Map<String, List<PublicLinkInfo>> plMap = plData.getPublicLinksMap();
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
					    displayPublicLinks(plMap);
					    center();
					}
				});
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
	
	/**
	 * Handles ShareExpirationValueChangedEvent's received by this class.
	 * 
	 * Implements the ShareExpirationValueChangedEvent.Handler.onShareExpirationValueChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShareExpirationValueChanged(ShareExpirationValueChangedEvent event) {
		// Is the expiration change targeted to this dialog share
		// expiration widget?
		if (event.getWidget() == m_expWidget) {
			// Yes!  Does the event contain a valid expiration value?
			ShareExpirationValue expirationValue = event.getValue();
			if ((null != expirationValue) && expirationValue.isValid()) {
				// Yes!  Update the share with the expiration change.
				Long shareId = Long.parseLong(m_expWidget.getElement().getAttribute(EXPIRATION_SHARE_ID_ATTR));
				SaveShareExpirationValueCmd cmd = new SaveShareExpirationValueCmd(shareId, expirationValue);
				GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
					@Override
					public void onFailure(Throwable t) {
						GwtClientHelper.handleGwtRPCFailure(
							t,
							m_messages.rpcFailure_SaveShareExpirationValue());
					}
			
					@Override
					public void onSuccess(VibeRpcResponse response) {
						// If the expiration change was saved...
						boolean expirationChanged = ((BooleanRpcResponseData) response.getResponseData()).getBooleanValue();
						if (expirationChanged) {
							// ...fire an event telling everybody it
							// ...changed.
							GwtTeaming.fireEventAsync(new ContentChangedEvent(Change.SHARING));
						}
					}
				});
			}
		}
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
	    loadPart1Async();
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
				REGISTERED_EVENTS,
				this,
				m_registeredEventHandlers);
		}
	}

	/*
	 * Asynchronously runs the given instance of the edit public link
	 * dialog.
	 */
	private static void runDlgAsync(final EditPublicLinkDlg cplDlg, final String caption, final List<EntityId> entityIds) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				cplDlg.runDlgNow(caption, entityIds);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the edit public link
	 * dialog.
	 */
	private void runDlgNow(String caption, List<EntityId> entityIds) {
		// Store the parameters...
		setCaption(caption);

		// Did we get any entities to run against?
		int c = (GwtClientHelper.hasItems(entityIds) ? entityIds.size() : 0);
		if (0 == c) {
			// No!  That should never happen.  Tell the user about the
			// problem and bail.
			GwtClientHelper.deferredAlert(m_messages.editPublicLink_InternalError_NoEntries());
			return;
		}

		// Is there more than one entry?
		if (1 < c) {
			// Yes!  That's not supported yet.  Tell the user about the
			// problem and bail.
			GwtClientHelper.deferredAlert(m_messages.editPublicLink_InternalError_MoreThanOneEntry());
			return;
		}

		// Is this entity other than an entry?
		m_entityId = entityIds.get(0);
		if (!(m_entityId.isEntry())) {
			// Yes!  That should never happen.  Tell the user about
			// the problem and bail.
			GwtClientHelper.deferredAlert(m_messages.editPublicLink_InternalError_NotAnEntry());
			return;
		}

		// If we get here, the entity we received is valid!  Populate
		// the dialog.
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
	/* the edit public link dialog and perform some operation on it. */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the edit public link dialog
	 * asynchronously after it loads. 
	 */
	public interface EditPublicLinkDlgClient {
		void onSuccess(EditPublicLinkDlg cplDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the EditPublicLinkDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final EditPublicLinkDlgClient cplDlgClient,
			
			// initAndShow parameters,
			final EditPublicLinkDlg	cplDlg,
			final String			caption,
			final List<EntityId>	entityIds) {
		GWT.runAsync(EditPublicLinkDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_EditPublicLinkDlg());
				if (null != cplDlgClient) {
					cplDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != cplDlgClient) {
					// Yes!  Create it and return it via the callback.
					EditPublicLinkDlg cplDlg = new EditPublicLinkDlg();
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
	 * Loads the EditPublicLinkDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param cplDlgClient
	 */
	public static void createAsync(EditPublicLinkDlgClient cplDlgClient) {
		doAsyncOperation(cplDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the edit public link dialog.
	 * 
	 * @param cplDlg
	 * @param caption
	 * @param entityIds
	 */
	public static void initAndShow(EditPublicLinkDlg cplDlg, String caption, List<EntityId> entityIds) {
		doAsyncOperation(null, cplDlg, caption, entityIds);
	}
}
