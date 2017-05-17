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
package org.kablink.teaming.gwt.client.binderviews;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.binderviews.FolderEntryCookies.Cookie;
import org.kablink.teaming.gwt.client.util.CommentAddedCallback;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.FolderEntryDetails;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Class that holds the folder entry viewer's sidebar manager.
 * 
 * @author drfoster@novell.com
 */
public class FolderEntrySidebar extends VibeFlowPanel implements CommentAddedCallback {
	private boolean							m_commentsVisible;		// true -> The comments are visible.  false -> They're hidden.
	private boolean							m_sharingAreaShown;		// true -> We're showing a sharing area.  false -> We're not.
	private FolderEntryCallback				m_fec;					// Callback to the folder entry composite.
	private FolderEntryComments				m_commentsArea;			// The are containing the comment display and entry widget.
	private FolderEntryDetails				m_fed;					// Details about the folder entry being viewed.
	private FolderEntrySharing				m_sharingArea;			// The are containing the sharing display.
	private GwtTeamingDataTableImageBundle	m_images;				// Access to Vibe's images.
	private GwtTeamingMessages				m_messages;				// Access to Vibe's messages.
	private Image							m_commentsExpanderImg;	// The arrow in the comments header that shows the state of the comments            (open or closed.)
	private Image							m_sharingExpanderImg;	// The arrow in the sharing  header that shows the state of the sharing information (open or closed.)
	private int								m_sidebarHeight;		// The current height of the sidebar.
	private InlineLabel						m_commentsHeader;		// The comment header label.
	private VibeFlowPanel					m_commentHeaderPanel;	// The comment header panel (contains the label and arrow.)
	private VibeFlowPanel					m_sharingHeaderPanel;	// The sharing header panel (contains the label and arrow.)
	private VibeFlowPanel					m_slider;				// The <DIV> containing the slider for hiding/showing the sidebar.
	
	/**
	 * Constructor method.
	 * 
	 * @param fec
	 */
	public FolderEntrySidebar(FolderEntryCallback fec, FolderEntryDetails fed) {
		// Initialize the super class...
		super();
		
		// ...store the parameters...
		m_fec = fec;
		m_fed = fed;
		
		// ...initialize the data members requiring it...
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
		
		// ...and construct the comment manager's content.
		createContent();
	}
	
	/**
	 * Called when a comment gets added.
	 * 
	 * Implements the CommentAddedCallback.commentAdded() method.
	 * 
	 * @param callbackData
	 */
	@Override
	public void commentAdded(Object callbackData) {
		// Update the count in the header.
		int commentsCount = (m_fed.getComments().getCommentsCount() + 1);
		m_fed.getComments().setCommentsCount(commentsCount);
		setCommentsCount(commentsCount);

		// If the comments widget is not currently visible...
		if (!m_commentsVisible) {
			// ...show it.
			toggleCommentsVisibility();
		}
	}

	/*
	 * Creates a panel that contains the comments portion of the
	 * caption.
	 */
	private void createCommentsCaption() {
		// What's the visibility state for comments?
		m_commentsVisible = FolderEntryCookies.getBooleanCookieValue(Cookie.COMMENTS_VISIBLE, true);	// true -> Defaults to visible.

		// Create the outer panel for the comment caption...
		m_commentHeaderPanel = new VibeFlowPanel();
		m_commentHeaderPanel.addStyleName("vibe-feView-sidebarSectionHeadOuter");
		add(m_commentHeaderPanel);

		// ...add an anchor to it so that it can be clicked on...
		Anchor commentsAnchor = new Anchor();
		commentsAnchor.addStyleName("cursorPointer");
		commentsAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleCommentsVisibility();
			}
		});
		m_commentHeaderPanel.add(commentsAnchor);

		// ...add the inner panel to the anchor...
		VibeFlowPanel innerPanel = new VibeFlowPanel();
		innerPanel.addStyleName("vibe-feView-sidebarSectionHeadInner");
		commentsAnchor.getElement().appendChild(innerPanel.getElement());

		// ...add the title panel to the inner panel...
		VibeFlowPanel titlePanel = new VibeFlowPanel();
		titlePanel.addStyleName("vibe-feView-sidebarSectionHeadTitle");
		innerPanel.add(titlePanel);

		// ...add the expander image to the title panel...
		ImageResource expanderImg = (m_commentsVisible ? m_images.slideUp() : m_images.slideDown());
		m_commentsExpanderImg = GwtClientHelper.buildImage(expanderImg.getSafeUri().asString());
		m_commentsExpanderImg.addStyleName("vibe-feView-sidebarSectionHeadExpander");
		titlePanel.add(m_commentsExpanderImg);

		// ...and finally, add the label to the title panel.
		m_commentsHeader = new InlineLabel();
		m_commentsHeader.addStyleName("vibe-feView-sidebarSectionHeadText");
		titlePanel.add(m_commentsHeader);
		setCommentsCount(m_fed.getComments().getCommentsCount());
	}

	/*
	 * Creates the comment area widgets.
	 */
	private void createCommentsArea() {
		m_commentsArea = new FolderEntryComments(m_fec, m_fed.getComments(), this);
		add(m_commentsArea);
		if (!m_commentsVisible) {
			m_commentsArea.setCommentsVisible(false);
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					// We do this again 1/2 second later to ensure it
					// gets hidden after its fully initialized.  On the
					// first display, sometimes it doesn't stay hidden.
					m_commentsArea.setCommentsVisible(false);
				}
			},
			500);
		}
	}
	
	/*
	 * Creates the sidebar's content.
	 */
	private void createContent() {
		// Add the panel's style.
		addStyleName("vibe-feView-sidebar");

		// Create the comment components.
		createCommentsCaption();
		createCommentsArea();
	
		// If this is a non-comment and if the logged in user isn't
		// Guest or an external user...
		m_sharingAreaShown = (m_fed.isTop() && (!(GwtClientHelper.isGuestUser() || GwtClientHelper.isExternalUser())));
		if (m_sharingAreaShown) {
			// ...create the sharing components...
			createSharingCaption();
			createSharingArea();
		}
		
		else {
			// ...otherwise, make an extra call into the view so that
			// ...its component counts are correct.
			m_fec.viewComponentReady();
		}
		
		// Create the sidebar's slider components.
		createSidebarSlider();
		
		// Hide/show it based on what the composite says.
		setSidebarVisible(m_fec.isSidebarVisible());
		
		// Finally, tell the composite that we're ready.
		m_fec.viewComponentReady();
	}

	/*
	 * Creates a panel that contains the sharing portion of the
	 * caption.
	 */
	private void createSharingCaption() {
		// Create the outer panel for the sharing caption...
		m_sharingHeaderPanel = new VibeFlowPanel();
		m_sharingHeaderPanel.addStyleName("vibe-feView-sidebarSectionHeadOuter");
		add(m_sharingHeaderPanel);

		// ...add an anchor to it so that it can be clicked on...
		Anchor sharingAnchor = new Anchor();
		sharingAnchor.addStyleName("cursorPointer");
		sharingAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				toggleCommentsVisibility();
			}
		});
		m_sharingHeaderPanel.add(sharingAnchor);

		// ...add the inner panel to the anchor...
		VibeFlowPanel innerPanel = new VibeFlowPanel();
		innerPanel.addStyleName("vibe-feView-sidebarSectionHeadInner");
		sharingAnchor.getElement().appendChild(innerPanel.getElement());

		// ...add the title panel to the inner panel...
		VibeFlowPanel titlePanel = new VibeFlowPanel();
		titlePanel.addStyleName("vibe-feView-sidebarSectionHeadTitle");
		innerPanel.add(titlePanel);

		// ...add the expander image to the title panel...
		ImageResource expanderImg = (m_commentsVisible ? m_images.slideUp() : m_images.slideDown());
		m_sharingExpanderImg = GwtClientHelper.buildImage(expanderImg.getSafeUri().asString());
		m_sharingExpanderImg.addStyleName("vibe-feView-sidebarSectionHeadExpander");
		titlePanel.add(m_sharingExpanderImg);

		// ...and finally, add the label to the title panel.
		InlineLabel sharingHeader = new InlineLabel(m_messages.folderEntry_ShareInfo());
		sharingHeader.addStyleName("vibe-feView-sidebarSectionHeadText");
		titlePanel.add(sharingHeader);
	}

	/*
	 * Creates the sharing area widgets.
	 */
	private void createSharingArea() {
		m_sharingArea = new FolderEntrySharing(m_fec, m_fed.getSharedByItems(), m_fed.getSharedWithItems());
		add(m_sharingArea);
	}
	
	/*
	 * Creates the components for hiding/showing the sidebar.
	 */
	private void createSidebarSlider() {
		// Create the <DIV> to hold the slider...
		m_slider = new VibeFlowPanel();
		m_slider.addStyleName("vibe-feView-sidebarSliderDiv");
		add(m_slider);

		// ...create the slider image...
		Image sliderImg = GwtClientHelper.buildImage(m_images.touchSlide().getSafeUri().asString());
		sliderImg.addStyleName("vibe-feView-sidebarSliderImg");
		sliderImg.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// Toggle the sidebar's state...
						setSidebarVisible(!(m_fec.isSidebarVisible()));
						
						// ...and tell the composite to do its part.
						m_fec.toggleSidebarVisibility();
					}
				});
			}
		});
		m_slider.add(sliderImg);
	}

	/**
	 * Navigate the entry viewer to the given ViewFolderEntryInfo.
	 * 
	 * @param eid
	 */
	public void doNavigate(EntityId eid) {
		if (m_commentsVisible != FolderEntryCookies.getBooleanCookieValue(Cookie.COMMENTS_VISIBLE, true)) {
			toggleCommentsVisibility();
		}
	}

	/**
	 * Called when the sidebar needs to be resized.
	 * 
	 * @param sidebarHeight
	 */
	public void setSidebarHeight(int sidebarHeight) {
		m_sidebarHeight = sidebarHeight;
		setSharingHeightAsync();
	}
	
	/*
	 * Asynchronously sets the height of the sharing area.
	 */
	private void setSharingHeightAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				setSharingHeightNow();
			}
		});
	}
	
	/*
	 * Synchronously sets the height of the sharing area.
	 */
	private void setSharingHeightNow() {
		// If we're showing a sharing area...
		if (m_sharingAreaShown) {
			// ...adjust its height based on the height of the sidebar.
			int commentsHeight      = (m_commentHeaderPanel.getOffsetHeight() + m_commentsArea.getOffsetHeight());
			int sharingHeaderHeight =  m_sharingHeaderPanel.getOffsetHeight();
			int sharingHeight       = (m_sidebarHeight - (commentsHeight + sharingHeaderHeight));
			if (FolderEntryComposite.MINIMUM_SHARING_HEIGHT > sharingHeight) {
				sharingHeight = FolderEntryComposite.MINIMUM_SHARING_HEIGHT;
			}
			m_sharingArea.setHeight(sharingHeight + "px");
		}
	}
	
	/*
	 * Updates the comments count in the header.
	 */
	private void setCommentsCount(int commentCount) {
		// Updates the comments header with the current comment count.
		m_commentsHeader.setText(m_messages.folderEntry_Comments(commentCount));
	}
	
	/*
	 * Hides/shows the sidebar.
	 */
	private void setSidebarVisible(boolean visible) {
		// Are we showing the sidebar?
		if (visible) {
			// Yes!  Show its components...
            m_commentHeaderPanel.setVisible(true);
            m_commentsArea.setVisible(      true);
            if (m_sharingAreaShown) {
                m_sharingHeaderPanel.setVisible(true);
                m_sharingArea.setVisible(       true);
            }

            // ...and adjust the slider and sidebar styles accordingly.
			m_slider.removeStyleName("vibe-feView-sidebarSliderDiv-hidden");
                     removeStyleName("vibe-feView-sidebar-hidden"         );
		}
		
		else {
			// No, we must be hiding it!  Hide its components...
			m_commentHeaderPanel.setVisible(false);
			m_commentsArea.setVisible(      false);
            if (m_sharingAreaShown) {
                m_sharingHeaderPanel.setVisible(false);
                m_sharingArea.setVisible(       false);
            }
			
            // ...and adjust the slider and sidebar styles accordingly.
			m_slider.addStyleName("vibe-feView-sidebarSliderDiv-hidden");
                     addStyleName("vibe-feView-sidebar-hidden"         );
		}
	}

	/*
	 * Toggle the state of the comment widgets visibility.
	 */
	private void toggleCommentsVisibility() {
		// Toggle the state of the comments...
		m_commentsVisible = (!m_commentsVisible);
		ImageResource expanderRes;
		if (m_commentsVisible)
		     expanderRes = m_images.slideUp();
		else expanderRes = m_images.slideDown();
		String expanderResUri = expanderRes.getSafeUri().asString();
		m_commentsExpanderImg.setUrl(expanderResUri);
		m_commentsArea.setCommentsVisible(m_commentsVisible);

		// ...toggle the state of the sharing...
        if (m_sharingAreaShown) {
        	m_sharingExpanderImg.setUrl(expanderResUri);
        }

		// ...store the current state in a cookie...
		if (m_commentsVisible)
		     FolderEntryCookies.removeCookieValue(    Cookie.COMMENTS_VISIBLE       );
		else FolderEntryCookies.setBooleanCookieValue(Cookie.COMMENTS_VISIBLE, false);

		// ...and force the sharing ares to resize to fit the new
		// ...dimensions available to it.
		setSharingHeightAsync();
	}
}
