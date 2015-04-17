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
package org.kablink.teaming.gwt.client.binderviews;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.FolderEntryCookies.Cookie;
import org.kablink.teaming.gwt.client.event.FolderEntryActionCompleteEvent;
import org.kablink.teaming.gwt.client.event.InvokeSimpleProfileEvent;
import org.kablink.teaming.gwt.client.rpc.shared.SetSeenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderIconSize;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails;
import org.kablink.teaming.gwt.client.util.PrincipalInfo;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails.UserInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ViewFolderEntryInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Class that holds the folder entry viewer header.
 * 
 * @author drfoster@novell.com
 */
public class FolderEntryHeader extends VibeFlowPanel {
	private FolderEntryCallback				m_fec;				// Callback to the folder entry composite.
	private FolderEntryDetails				m_fed;				// Details about the entry being viewed.
	private GwtTeamingDataTableImageBundle	m_images;			// Access to Vibe's images.
	private GwtTeamingFilrImageBundle		m_filrImages;		// Access to Filr's images.
	private GwtTeamingMessages				m_messages;			// Access to Vibe's messages.
	private Label							m_titleLabel;		// The Label that holds the title.
	private Label							m_sizeLabel;		// The label holding the size of the file.
	private VibeFlowPanel					m_descPanel;		// The panel that holds the entry's description.
	private VibeFlowPanel					m_showDescPanel;	// The panel that holds the widgets that allow the user to hide/show the description.

	private final static int MINIMUM_TITLE_WIDTH	= 150;	// Minimum width we allow the title text to be.
	private final static int TITLE_OVERHEAD			= 130;	// Amount to reduce the title width by to account for margins and other horizontal overhead.
	
	/*
	 * Template used to generate a <strong> label in the header.
	 */
	public interface StrongTemplate extends SafeHtmlTemplates {
		@Template("<strong>{0}</strong>  <span class=\"{2}\">{1}</span>")
		SafeHtml strongHtml(String stringResource, String strongDate, String strongStyle);
	}
	private final static StrongTemplate STRONG_TEMPLATE = GWT.create(StrongTemplate.class);
	
	/**
	 * Constructor method.
	 * 
	 * @param fec
	 * @param fed
	 */
	public FolderEntryHeader(FolderEntryCallback fec, FolderEntryDetails fed) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_fec = fec;
		m_fed = fed;
		
		// ...initialize the data members requiring it...
		m_filrImages = GwtTeaming.getFilrImageBundle();
		m_images     = GwtTeaming.getDataTableImageBundle();
		m_messages   = GwtTeaming.getMessages();
		
		// ...and construct the header's content.
		createContent();
	}

	/*
	 * Adds a additional row of information about a user's access to
	 * the entry to the header.
	 */
	private void addAdditionalUserInfoRow(VibeFlowPanel contentPanel, final UserInfo userInfo, String timeLabelStyle, String timeStyle, String timeText) {
		// Create the user panel...
		VibeFlowPanel userPanel = new VibeFlowPanel();
		userPanel.addStyleName("vibe-feView-headerContentPersonPanel");
		
		// ...create the user anchor...
		final Anchor userPanelAnchor = new Anchor();
		userPanelAnchor.addStyleName("vibe-feView-headerContentPersonAnchor");
		userPanelAnchor.getElement().appendChild(userPanel.getElement());
		userPanelAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				invokeSimpleProfile(userInfo.getPrincipalInfo(), userPanelAnchor);
			}
		});
		contentPanel.add(userPanelAnchor);
		
		// ...create the user avatar...
		String userAvatarUrl = userInfo.getPrincipalInfo().getAvatarUrl();
		if (!(GwtClientHelper.hasString(userAvatarUrl))) {
			userAvatarUrl = m_images.userPhoto().getSafeUri().asString();
		}
		Image userAvatar = GwtClientHelper.buildImage(userAvatarUrl);
		userAvatar.addStyleName("vibe-feView-headerContentPersonAvatar");
		userPanel.add(userAvatar);
		
		// ...create the user title...
		InlineLabel userTitle = new InlineLabel(userInfo.getPrincipalInfo().getTitle());
		userPanel.add(userTitle);

		// ...and finally, create the user time (modification,
		// ...lock, ...) associated with the user.
		InlineLabel timeLabel = new InlineLabel();
		timeLabel.addStyleName(timeStyle);
		timeLabel.getElement().setInnerSafeHtml(
			STRONG_TEMPLATE.strongHtml(
				timeText,
				userInfo.getDate(),
				timeLabelStyle));
		userPanel.add(timeLabel);
	}
	
	/*
	 * Creates an Anchor for navigating to a bread crumb link.
	 */
	private Anchor createBCAnchor(final ViewFolderEntryInfo bcItem) {
		Anchor bcAnchor = new Anchor();
		bcAnchor.addStyleName("vibe-feView-headerContentBCAnchor");
		Element bcAE = bcAnchor.getElement();
		bcAE.appendChild(new InlineLabel(bcItem.getTitle()).getElement());
		bcAE.appendChild(GwtClientHelper.buildImage(m_images.breadSpace()).getElement());
		bcAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						m_fec.doNavigate(bcItem);
					}
				});
			}
		});
		return bcAnchor;
	}
	
	/*
	 * Creates the header's content.
	 */
	private void createContent() {
		// Add the panel's style...
		addStyleName("vibe-feView-headerRoot");

		// ...create the header's content panel...
		VibeFlowPanel contentPanel = new VibeFlowPanel();
		contentPanel.addStyleName("vibe-feView-headerContentPanel");
		add(contentPanel);
		
		// ...create the header's content...
		createEntryImage();
		createEntryTitle(          contentPanel);
		createEntryUsers(          contentPanel);
		if (m_fed.hasDescripion()) {
			createEntryDescription(contentPanel);
		}

		// ...and tell the composite that we're ready.
		m_fec.viewComponentReady();
	}
	
	/*
	 * Creates the header's main content.
	 */
	private void createEntryDescription(VibeFlowPanel contentPanel) {
		// What's the visibility state for the description on this entry?
		boolean descVisible = FolderEntryCookies.getBooleanCookieValue(Cookie.DESCRIPTION_VISIBLE, m_fed.getEntityId(), true);
		
		// Add the description and hide description panels...
		m_descPanel = new VibeFlowPanel();
		m_descPanel.addStyleName("vibe-feView-headerDescription");
		contentPanel.add(m_descPanel);
		VibeFlowPanel descHidePanel = new VibeFlowPanel();
		descHidePanel.addStyleName("vibe-feView-headerDescriptionHide");
		m_descPanel.add(descHidePanel);
		Anchor descHideAnchor = new Anchor();
		descHideAnchor.addStyleName("vibe-feView-headerDescriptionAnchor");
		descHideAnchor.setTitle(m_messages.folderEntry_Alt_Hide());
		descHidePanel.add(descHideAnchor);
		descHideAnchor.getElement().setInnerText(m_messages.folderEntry_Hide());
		descHideAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setDescVisible(false);
			}
		});
		VibeFlowPanel descHtmlPanel = new VibeFlowPanel();
		descHtmlPanel.addStyleName("vibe-feView-headerDescriptionHtml");
		if (m_fed.isDescHtml())
		     descHtmlPanel.getElement().setInnerHTML(m_fed.getDesc()   );
		else descHtmlPanel.getElement().setInnerText(m_fed.getDescTxt());
		m_descPanel.add(descHtmlPanel);
		m_descPanel.setVisible(descVisible);

		// ...and add the show description panel.
		m_showDescPanel = new VibeFlowPanel();
		m_showDescPanel.addStyleName("vibe-feView-headerDescriptionShowOuter");
		contentPanel.add(m_showDescPanel);
		VibeFlowPanel showDescAnchorPanel = new VibeFlowPanel();
		showDescAnchorPanel.addStyleName("vibe-feView-headerDescriptionShowInner");
		m_showDescPanel.add(showDescAnchorPanel);
		Anchor showDescAnchor = new Anchor();
		showDescAnchor.addStyleName("vibe-feView-headerDescriptionAnchor");
		showDescAnchorPanel.add(showDescAnchor);
		showDescAnchor.getElement().setInnerText(m_messages.folderEntry_ShowDescription());
		showDescAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setDescVisible(true);
			}
		});
		m_showDescPanel.setVisible(!descVisible);
	}
	
	/*
	 * Creates the header's entry image, including a lock indication.
	 */
	private void createEntryImage() {
		// Create the entry image.
		String url = m_fed.getEntryIcon(BinderIconSize.LARGE);
		if (GwtClientHelper.hasString(url))
		     url = (GwtClientHelper.getRequestInfo().getImagesPath() + url);
		else url = m_filrImages.entry_large().getSafeUri().asString();
		Image i = GwtClientHelper.buildImage(url);
		i.addStyleName("vibe-feView-headerEntryImage");
		add(i);

		// Is the entry locked?
		if (m_fed.isEntryLocked()) {
			// Yes!  Added the entry locked indicator to the image.
			i = GwtClientHelper.buildImage(m_images.entryLock().getSafeUri().asString());
			i.addStyleName("vibe-feView-headerEntryLockImage");
			i.setTitle(m_messages.folderEntry_Alt_EntryLockedBy(m_fed.getEntryLocker().getPrincipalInfo().getTitle()));
			add(i);
			
			Label l = new Label(m_messages.folderEntry_Locked());
			l.addStyleName("vibe-feView-headerLockLabel");
			add(l);
		}
		
		// Is the file locked?
		if (m_fed.isFileLocked()) {
			// Yes!  Added the file locked indicator to the image...
			i = GwtClientHelper.buildImage(m_images.fileLock().getSafeUri().asString());
			i.addStyleName("vibe-feView-headerFileLockImage");
			i.setTitle(m_messages.folderEntry_Alt_FileLockedBy(m_fed.getFileLocker().getPrincipalInfo().getTitle()));
			add(i);

			// ...and if the entry isn't locked as well...
			if (!(m_fed.isEntryLocked())) {
				// ...and the locked label.
				Label l = new Label(m_messages.folderEntry_Locked());
				l.addStyleName("vibe-feView-headerLockLabel");
				add(l);
			}
		}
	}

	/*
	 * Creates the header's title information.
	 */
	private void createEntryTitle(VibeFlowPanel contentPanel) {
		// If this is not the top entry...
		if (!(m_fed.isTop())) {
			// ...add bread crumbs to the top entry...
			VibeFlowPanel bcPanel = new VibeFlowPanel();
			bcPanel.addStyleName("vibe-feView-headerContentBCPanel");
			List<ViewFolderEntryInfo> bcItems = m_fed.getCommentBreadCrumbs();
			for (ViewFolderEntryInfo bcItem:  bcItems) {
				bcPanel.add(createBCAnchor(bcItem));
			}
			contentPanel.add(bcPanel);
		}

		// ...if this entry hasn't been read...
		VibeFlowPanel titlePanel = new VibeFlowPanel();
		titlePanel.addStyleName("vibe-feView-headerContentTitlePanel");
		contentPanel.add(titlePanel);
		if (!(m_fed.isSeen())) {
			// ...let the user mark it read from there...
			Image i = GwtClientHelper.buildImage(m_images.unread(), m_messages.folderEntry_Alt_MarkRead());
			i.addStyleName("vibe-feView-headerContentTitleMarkRead");
			i.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					markReadAsync();
				}
			});
			titlePanel.add(i);
		}

		// Create the title label...
		m_titleLabel = new Label(m_fed.getTitle());
		m_titleLabel.addStyleName("vibe-feView-headerContentTitleAsLabel");
		
		// ...iIf the entry has a file download URL...
		String  dlUrl     = m_fed.getDownloadUrl();
		boolean isTrashed = m_fed.isTrashed();
		if ((!isTrashed) && GwtClientHelper.hasString(dlUrl)) {
			// ...add the title in an <A> that can download it...
			Anchor titleA = new Anchor();
			titleA.addStyleName("vibe-feView-headerContentTitleAsAnchor");
			titleA.setHref(dlUrl);
			titleA.setTarget("_blank");
			titleA.getElement().appendChild(m_titleLabel.getElement());
			titlePanel.add(titleA);

			// ...and if there's a file size to display...
			String fileSize = m_fed.getFileSizeDisplay();
			if (GwtClientHelper.hasString(fileSize)) {
				// ...add that to the right of the download URL...
				m_sizeLabel = new Label(m_messages.folderEntry_FileSize(fileSize));
				m_sizeLabel.addStyleName("vibe-feView-headerContentTitleFileSize displayInline");
				titlePanel.add(m_sizeLabel);
			}
		}
		
		else {
			// ...otherwise, just add the title Label directly...
			titlePanel.add(m_titleLabel);
			if (isTrashed) {
				Label trashLabel = new Label(m_messages.folderEntry_Trashed());
				trashLabel.addStyleName("wiki-noentries-panel vibe-feView-headerContentTitleTrashed displayInline");
				titlePanel.add(trashLabel);
			}
		}
		
		// ...and add the entry's path.
		Label path = new Label(m_fed.getPath());
		path.addStyleName("vibe-feView-headerContentPath");
		contentPanel.add(path);
	}
	
	/*
	 * Creates the header's user information including creator and
	 * modifier.
	 */
	private void createEntryUsers(VibeFlowPanel contentPanel) {
		// Add the entry's creator...
		final UserInfo creatorInfo = m_fed.getCreator();
		VibeFlowPanel creatorPanel = new VibeFlowPanel();
		creatorPanel.addStyleName("vibe-feView-headerContentPersonPanel");
		final Anchor creatorAnchor = new Anchor();
		creatorAnchor.addStyleName("vibe-feView-headerContentPersonAnchor");
		creatorAnchor.getElement().appendChild(creatorPanel.getElement());
		creatorAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				invokeSimpleProfile(creatorInfo.getPrincipalInfo(), creatorAnchor);
			}
		});
		contentPanel.add(creatorAnchor);
		String creatorAvatarUrl = creatorInfo.getPrincipalInfo().getAvatarUrl();
		if (!(GwtClientHelper.hasString(creatorAvatarUrl))) {
			creatorAvatarUrl = m_images.userPhoto().getSafeUri().asString();
		}
		Image creatorAvatar = GwtClientHelper.buildImage(creatorAvatarUrl);
		creatorAvatar.addStyleName("vibe-feView-headerContentPersonAvatar");
		creatorPanel.add(creatorAvatar);
		InlineLabel creator = new InlineLabel(creatorInfo.getPrincipalInfo().getTitle());
		creatorPanel.add(creator);

		// ...add the entry's creation time...
		InlineLabel creationLabel = new InlineLabel(creatorInfo.getDate());
		creationLabel.addStyleName("vibe-feView-headerContentPersonCreated");
		creatorPanel.add(creationLabel);

		// ...if the entry's creator is the same person as its
		// ...modifier...
		final UserInfo modifierInfo = m_fed.getModifier(); 
		if (m_fed.isModifierCreator()) {
			// ...and it was modified at some time other than when it
			// ...was created...
			if (!(modifierInfo.getDate().equals(creatorInfo.getDate()))) {
				// ...add the modification time...
				InlineLabel modifierLabel = new InlineLabel();
				modifierLabel.addStyleName("vibe-feView-headerContentPersonModified");
				modifierLabel.getElement().setInnerSafeHtml(
					STRONG_TEMPLATE.strongHtml(
						m_messages.folderEntry_Modified(),
						modifierInfo.getDate(),
						"vibe-feView-headerContentPersonModifiedLabel"));
				creatorPanel.add(modifierLabel);
			}
		}
		
		else {
			// ...otherwise, add the entry's modifier...
			addAdditionalUserInfoRow(
				contentPanel,
				modifierInfo,
				"vibe-feView-headerContentPersonModifiedLabel",
				"vibe-feView-headerContentPersonModified",
				m_messages.folderEntry_Modified());
		}

		// ...if the entry is locked...
		UserInfo lui = m_fed.getEntryLocker();
		if (null != lui) {
			// ...add the entry locker...
			addAdditionalUserInfoRow(
				contentPanel,
				lui,
				"vibe-feView-headerContentPersonLockedLabel",
				"vibe-feView-headerContentPersonLocked",
				m_messages.folderEntry_EntryLocked());
		}
		
		// ...if the file is locked...
		lui = m_fed.getFileLocker();
		if (null != lui) {
			// ...add the file locker.
			addAdditionalUserInfoRow(
				contentPanel,
				lui,
				"vibe-feView-headerContentPersonLockedLabel",
				"vibe-feView-headerContentPersonLocked",
				m_messages.folderEntry_FileLocked());
		}
	}
	
	/*
	 * Invokes the simple profile dialog on the principal.
	 */
	private void invokeSimpleProfile(PrincipalInfo pi, Widget referenceWidget) {
		Long wsId = pi.getPresenceUserWSId();
		String wsIdS = ((null == wsId) ? null : String.valueOf(wsId));
		GwtTeaming.fireEventAsync(
			new InvokeSimpleProfileEvent(
				new SimpleProfileParams(
					referenceWidget.getElement(),
					String.valueOf(pi.getId()),
					wsIdS,
					pi.getTitle())));
	}

	/*
	 * Asynchronously marks the entry as having been read.
	 */
	private void markReadAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				markReadNow();
			}
		});
	}
	
	/*
	 * Synchronously marks the entry as having been read.
	 */
	private void markReadNow() {
		final Long entryId = m_fed.getEntityId().getEntityId();
		SetSeenCmd cmd = new SetSeenCmd(entryId);
		GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					GwtTeaming.getMessages().rpcFailure_SetSeen(),
					String.valueOf(entryId));
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				GwtTeaming.fireEventAsync(new FolderEntryActionCompleteEvent(m_fed.getEntityId(), false));
			}
		});
	}

	/*
	 * Shows/hides the description.
	 */
	private void setDescVisible(boolean show) {
		// Set the visibility state...
		m_descPanel.setVisible(     show);
		m_showDescPanel.setVisible(!show);
		m_fec.resizeView();
		
		// ...and store the current state in a cookie.
		if (show)
		     FolderEntryCookies.removeCookieValue(    Cookie.DESCRIPTION_VISIBLE, m_fed.getEntityId()       );
		else FolderEntryCookies.setBooleanCookieValue(Cookie.DESCRIPTION_VISIBLE, m_fed.getEntityId(), false);
	}
	
	/**
	 * Called when the header needs to be resized.
	 */
	public void setHeaderSize() {
		m_titleLabel.addStyleName("width1px");	// Initially reduced so it doesn't affect the overall size calculations below.
		setHeaderSizeAsync();
	}

	/*
	 * Asynchronously set the size of the header.
	 */
	private void setHeaderSizeAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				setHeaderSizeNow();
			}
		});
	}
	
	/*
	 * Asynchronously set the size of the header.
	 */
	private void setHeaderSizeNow() {
		int headerWidth = getOffsetWidth();
		int sizeWidth   = ((null == m_sizeLabel) ? 0 : m_sizeLabel.getOffsetWidth());
		int titleWidth  = ((headerWidth - sizeWidth) - TITLE_OVERHEAD);
		if (MINIMUM_TITLE_WIDTH > titleWidth) {
			titleWidth = MINIMUM_TITLE_WIDTH;
		}
		m_titleLabel.removeStyleName("width1px");	// Remove the synthetic width value we initially set.
		m_titleLabel.getElement().getStyle().setProperty("maxWidth", (titleWidth + "px"));
	}
}
