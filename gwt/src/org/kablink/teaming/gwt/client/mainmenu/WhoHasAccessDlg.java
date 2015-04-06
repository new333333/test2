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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetWhoHasAccessCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.WhoHasAccessInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.WhoHasAccessInfoRpcResponseData.AccessInfo;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.GroupMembershipPopup;
import org.kablink.teaming.gwt.client.widgets.VibeFlexTable;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements Vibe's Who Has Access dialog.
 *  
 * @author drfoster@novell.com
 */
public class WhoHasAccessDlg extends DlgBox {
	private GwtTeamingFilrImageBundle		m_filrImages;		// Access to Filr's images.
	private GwtTeamingMainMenuImageBundle	m_images;			// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;			// Access to Vibe's messages.
	private EntityId						m_entityId;			// EntityId of the entity whose access is being viewed.
	private FlexCellFormatter				m_ftCF;				// Cell formatter for the FlexTable.
	private FlexTable 						m_ft;				// Table that holds the dialog's content.
	private WhoHasAccessInfoRpcResponseData	m_whoHasAccessInfo;	// The 'Who Has Access' information to populate the table with.
	
	private final static int	HEADER_ROW			= 0;	// Row indexes into the dialog's content.
	private final static int	LIST_CAPTION_ROW	= 1;	//
	private final static int	LIST_ROW			= 2;	//
	private final static int    SCROLL_WHEN			= 6;	// Count of items in one of the ScrollPanel's when scroll bars are enabled.

	/*
	 * Inner class that wraps items displayed in the dialog's content.
	 */
	private class DlgLabel extends InlineLabel {
		private GroupMembershipPopup	m_gmp;
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 * @param style
		 * @param title
		 */
		public DlgLabel(String label, String style, String title) {
			super(label);
			addStyleName("vibe-whoHasAccessDlg-label");
			if (GwtClientHelper.hasString(style)) {
				addStyleName(style);
			}
			if (GwtClientHelper.hasString(title)) {
				setTitle(title);
			}
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 */
		public DlgLabel(String label) {
			// Always use the initial form of the method.
			this(label, null, null);
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 * @param style
		 */
		public DlgLabel(String label, String style) {
			// Always use the initial form of the method.
			this(label, style, null);
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public GroupMembershipPopup getGroupMembershipPopup() {return m_gmp;}
		
		/**
		 * Set'er method.
		 * 
		 * @param gmp
		 */
		public void setGroupMembershipPopup(GroupMembershipPopup gmp) {m_gmp = gmp;}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private WhoHasAccessDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_filrImages = GwtTeaming.getFilrImageBundle();
		m_images     = GwtTeaming.getMainMenuImageBundle();
		m_messages   = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			"",								// Dialog caption set when the dialog runs.
			getSimpleSuccessfulHandler(),	// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),		// The dialog's EditCanceledHandler.
			null);							// Create callback data.  Unused. 
	}

	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	private Image buildSpinnerImage() {
		return GwtClientHelper.buildImage(m_images.spinner());
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
		// Create and return a panel to hold the dialog's content.
		m_ft = new VibeFlexTable();
		m_ft.addStyleName("vibe-whoHasAccessDlg-content");
		m_ftCF = m_ft.getFlexCellFormatter();
		return m_ft;
	}

	/*
	 * Clears the contents of the dialog and displays a message that
	 * we're reading the access information for the entity.
	 */
	private void displayReading() {
		m_ft.removeAllRows();
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("vibe-whoHasAccessDlg-readingPanel");
		fp.add(buildSpinnerImage());
		DlgLabel l = new DlgLabel(m_messages.mainMenuWhoHasAccessDlgReading());
		l.addStyleName("vibe-whoHasAccessDlg-readingLabel");
		fp.add(l);
		m_ft.setWidget(0, 0, fp);
	}

	/**
	 * Returns the edited List<FavoriteInfo>.
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
	 * Returns the URL to the image to display for the avatar.
	 */
	private String getAvatarUrl(AccessInfo ai, boolean isUser) {
		String reply = ai.getAvatarUrl();
		if (!(GwtClientHelper.hasString(reply))) {
			reply = (isUser ? m_images.userPhoto().getSafeUri().asString() : m_images.spacer1px().getSafeUri().asString());
		}
		return reply;
	}
	
	/*
	 * Returns the URL to use for a row's image.
	 */
	private String getEntityImageUrl() {
		// Is the entity a binder?
		BinderIconSize	bis = BinderIconSize.getListViewIconSize();
		ImageResource	imgRes;
		String			reply;
		if (m_entityId.isBinder()) {
			// Yes!  Is it a Home folder?
			if (m_whoHasAccessInfo.isEntityHomeFolder()) {
				// Yes!  Display the Home folder image for it.
				switch (bis) {
				default:
				case SMALL:   imgRes = m_filrImages.folderHome();        break; 
				case MEDIUM:  imgRes = m_filrImages.folderHome_medium(); break;
				case LARGE:   imgRes = m_filrImages.folderHome_large();  break;
				}
				reply  = imgRes.getSafeUri().asString();
				
			}
			
			else {
				// No, it isn't a Home folder!  Do we have a specific
				// image for it?
				String binderIcon = m_whoHasAccessInfo.getEntityIcon(bis);
				if (GwtClientHelper.hasString(binderIcon)) {
					// Yes!  Use it to construct the URL.
					String imagesPath = GwtClientHelper.getRequestInfo().getImagesPath();
					if (binderIcon.startsWith("/"))
					     reply = (imagesPath + binderIcon.substring(1));
					else reply = (imagesPath + binderIcon);
				}
				
				else {
					// No, we don't have a specific image for it!  Use
					// the generic folder image.
					switch (bis) {
					default:
					case SMALL:   imgRes = m_filrImages.folder();        break;
					case MEDIUM:  imgRes = m_filrImages.folder_medium(); break;
					case LARGE:   imgRes = m_filrImages.folder_large();  break;
					}
					reply = imgRes.getSafeUri().asString();
				}
			}
		}
		
		else {
			// No, the entity isn't a binder!  Do we have a specific
			// image for it?
			String entryIcon = m_whoHasAccessInfo.getEntityIcon(bis);
			if (GwtClientHelper.hasString(entryIcon)) {
				// Yes!  Use it to construct the URL.
				String imagesPath = GwtClientHelper.getRequestInfo().getImagesPath();
				if (entryIcon.startsWith("/"))
				     reply = (imagesPath + entryIcon.substring(1));
				else reply = (imagesPath + entryIcon);
			}
			
			else {
				// No, we don't have a specific image for it!  Use
				// the generic entry image.
				switch (bis) {
				default:
				case SMALL:   imgRes = m_filrImages.entry();        break;
				case MEDIUM:  imgRes = m_filrImages.entry_medium(); break;
				case LARGE:   imgRes = m_filrImages.entry_large();  break;
				}
				reply = imgRes.getSafeUri().asString();
			}
		}
		
		// If we get here, reply refers to the URL for the entity's
		// image.  Return it.
		return reply;
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
		GetWhoHasAccessCmd gwhaCmd = new GetWhoHasAccessCmd(m_entityId);
		GwtClientHelper.executeCommand(gwhaCmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetWhoHasAccess());
				
				hide();
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Extract the who has access information from the
				// response data and use it to populate the dialog.
				m_whoHasAccessInfo = ((WhoHasAccessInfoRpcResponseData) response.getResponseData());
				populateFromWhoHasAccessInfoAsync();
			}
		});
	}

	/*
	 * Asynchronously populates the dialog from a
	 * WhoHasAccessInfoRpcResponseData object.
	 */
	private void populateFromWhoHasAccessInfoAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateFromWhoHasAccessInfoNow();
			}
		});
	}
	
	/*
	 * Synchronously populates the dialog from a
	 * WhoHasAccessInfoRpcResponseData object.
	 */
	private void populateFromWhoHasAccessInfoNow() {
		// Set the dialog's caption...
		setCaption(m_messages.mainMenuWhoHasAccessDlgHeader());
		
		// ...clear the current content of the dialog...
		m_ft.removeAllRows();

		// ...create header panel containing the entity's image and
		// ...name...
		FlowPanel headerPanel = new FlowPanel();
		headerPanel.addStyleName("vibe-whoHasAccessDlg-headerPanel");
		Image headerImg = GwtClientHelper.buildImage(getEntityImageUrl());
		headerImg.addStyleName("vibe-whoHasAccessDlg-headerImg");
		headerPanel.add(headerImg);
		DlgLabel headerLabel = new DlgLabel(m_whoHasAccessInfo.getEntityTitle(), "vibe-whoHasAccessDlg-headerLabel");
		headerPanel.add(headerLabel);
		m_ft.setWidget(   HEADER_ROW, 0, headerPanel);
		m_ftCF.setColSpan(HEADER_ROW, 0, 2);

		// ...extract the lists from the who has access info...
		List<AccessInfo>	userList  = m_whoHasAccessInfo.getUsers();  int users  = userList.size(); 
		List<AccessInfo>	groupList = m_whoHasAccessInfo.getGroups(); int groups = groupList.size();
		boolean				scroll    = ((users >= SCROLL_WHEN) || (groups >= SCROLL_WHEN));

		// ...create the user list...
		ScrollPanel usersPanel = new ScrollPanel();
		usersPanel.addStyleName("vibe-whoHasAccessDlg-scrollPanel vibe-whoHasAccessDlg-scrollPanelUsers");
		if (scroll) {
			usersPanel.addStyleName("vibe-whoHasAccessDlg-scrollPanelLimit");
		}
		populateScrollPanel(usersPanel, userList, true);
		
		// ...create the group list...
		ScrollPanel groupsPanel = new ScrollPanel();
		groupsPanel.addStyleName("vibe-whoHasAccessDlg-scrollPanel vibe-whoHasAccessDlg-scrollPanelGroups");
		if (scroll) {
			groupsPanel.addStyleName("vibe-whoHasAccessDlg-scrollPanelLimit");
		}
		populateScrollPanel(groupsPanel, groupList, false);
		
		// ...and tie it all together.
		populateTableColumn(0, m_messages.mainMenuWhoHasAccessDlgUsersWithAccess(),  usersPanel );
		populateTableColumn(1, m_messages.mainMenuWhoHasAccessDlgGroupsWithAccess(), groupsPanel);
		
		// Show the dialog (perhaps again) so that it can be positioned
		// correctly based on its new content.
		show(true);
	}

	/*
	 * Populates a ScrollPanel with the information from a
	 * List<AccessInfo>.
	 */
	private void populateScrollPanel(ScrollPanel scrollPanel, List<AccessInfo> accessList, boolean isUserList) {
		// Are there any AccessInfo's to populate the ScrollPanel with?
		if (GwtClientHelper.hasItems(accessList)) {
			// Yes!  Create a panel to hold them...
			VerticalPanel vp = new VibeVerticalPanel(null, null);
			scrollPanel.add(vp);

			// ...scan them...
			boolean showAvatars = (isUserList || AccessInfo.listContainsAvatars(accessList));
			for (final AccessInfo ai:  accessList) {
				// ...and add items for them to the ScrollPanel.
				final DlgLabel	aiLabel = new DlgLabel(ai.getName(), "gwtUI_nowrap", ai.getHover());
				final Widget	aiWidget;
				if (showAvatars) {
					FlowPanel fp = new VibeFlowPanel();
					fp.addStyleName("vibe-whoHasAccessDlg-scrollPanelWithImg");
					Image aiAvatar = GwtClientHelper.buildImage(getAvatarUrl(ai, isUserList));
					aiAvatar.addStyleName("vibe-whoHasAccessDlg-scrollPanelImg");
					fp.add(aiAvatar);
					fp.add(aiLabel);
					aiWidget = fp;
				}
				
				else {
					aiWidget = aiLabel;
				}
				
				vp.add(aiWidget);
				
				// Are we populating the group list?
				if (!isUserList) {
					// Yes!  Groups can be expanded to show their
					// membership.  Add styles and a click handler to
					// do so.
					aiLabel.addStyleName("vibe-whoHasAccessDlg-group");
					aiLabel.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							GroupMembershipPopup gmp = aiLabel.getGroupMembershipPopup();
							if (null == gmp) {
								gmp = new GroupMembershipPopup(
									true,	// true  -> Auto hide.
									false,	// false -> Not modal.
									ai.getName(),
									String.valueOf(ai.getId()));
								aiLabel.setGroupMembershipPopup(gmp);
							}
							gmp.showRelativeTo(aiWidget);
						}
					});
				}
			}
		}
		
		else {
			// No, there aren't any AccessInfo's!  Added a message to
			// the ScrollPanel saying as much.
			scrollPanel.add(new DlgLabel(m_messages.mainMenuWhoHasAccessDlgNone()));
		}
	}

	/*
	 * Populates a column in the who has access FlexTable
	 */
	private void populateTableColumn(int column, String header, ScrollPanel sp) {
		m_ft.setWidget(     LIST_CAPTION_ROW, column, new DlgLabel(header, "vibe-whoHasAccessDlg-scrollPanelHeader"));
		m_ft.setWidget(     LIST_ROW,         column, sp);
		m_ftCF.addStyleName(LIST_ROW,         column, "vibe-whoHasAccessDlg-scrollPanelCell");
	}
	
	/*
	 * Asynchronously runs the given instance of the who has access
	 * dialog.
	 */
	private static void runDlgAsync(final WhoHasAccessDlg whaDlg, final EntityId entityId) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				whaDlg.runDlgNow(entityId);
			}
		});
	}
	
	/*
	 * Synchronously runs the given instance of the who has access
	 * dialog.
	 */
	private void runDlgNow(EntityId entityId) {
		// Store the parameter...
		m_entityId = entityId;

		// ...and start populating the dialog and show it.
		displayReading();
		populateDlgAsync();
		show(true);
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the who has access dialog and perform some operation on it.   */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the who has access dialog
	 * asynchronously after it loads. 
	 */
	public interface WhoHasAccessDlgClient {
		void onSuccess(WhoHasAccessDlg whaDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the WhoHasAccessDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final WhoHasAccessDlgClient whaDlgClient,
			
			// initAndShow parameters,
			final WhoHasAccessDlg	whaDlg,
			final EntityId			entityId) {
		GWT.runAsync(WhoHasAccessDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_WhoHasAccessDlg());
				if (null != whaDlgClient) {
					whaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != whaDlgClient) {
					// Yes!  Create it and return it via the callback.
					WhoHasAccessDlg whaDlg = new WhoHasAccessDlg();
					whaDlgClient.onSuccess(whaDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(whaDlg, entityId);
				}
			}
		});
	}
	
	/**
	 * Loads the WhoHasAccessDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param whaDlgClient
	 */
	public static void createAsync(WhoHasAccessDlgClient whaDlgClient) {
		doAsyncOperation(whaDlgClient, null, null);
	}
	
	/**
	 * Initializes and shows the who has access to an entity dialog.
	 * 
	 * @param whaDlg
	 * @param entityId
	 */
	public static void initAndShow(WhoHasAccessDlg whaDlg, EntityId entityId) {
		doAsyncOperation(null, whaDlg, entityId);
	}
}
