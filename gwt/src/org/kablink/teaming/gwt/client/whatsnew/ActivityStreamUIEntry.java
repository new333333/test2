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
package org.kablink.teaming.gwt.client.whatsnew;

import java.util.HashMap;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.InvokeSimpleProfileEvent;
import org.kablink.teaming.gwt.client.event.MarkEntryReadEvent;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.presence.PresenceControl;
import org.kablink.teaming.gwt.client.rpc.shared.ActivityStreamEntryRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ClickOnTitleActionRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EditEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetClickOnTitleActionCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ReplyToEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetSeenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SetUnseenCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.SimpleProfileParams;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.DescViewFormat;
import org.kablink.teaming.gwt.client.widgets.HTMLWithJavaScript;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

/**
 * This class is the base class for the entries that are displayed in
 * the Activity Stream.
 * 
 * @author drfoster@novell.com
 */
public abstract class ActivityStreamUIEntry extends Composite implements ClickHandler, MouseOverHandler, MouseOutHandler {
    private ActivityStreamCtrl					m_activityStreamCtrl;	//
	private ActivityStreamReply					m_editWidget;			//
	private ActivityStreamReply					m_replyWidget;			//
	private Anchor								m_titleAnchor;			//
	private boolean								m_entrySeen;			//
	private boolean								m_showTitle;			//
	private ClickHandler						m_descClickHandler;		//
	private ClickHandler						m_presenceClickHandler;	//
	private ClickOnTitleActionRpcResponseData	m_titleClickAction;		//
	private DescViewFormat						m_descViewFormat;		//
	private FlowPanel							m_commentsPanel;		//
	private FlowPanel							m_descPanel;			//
	private FlowPanel							m_mainPanel;			//
	private FlowPanel							m_presencePanel;		//
	private GwtTeamingImageBundle				m_images;				//
	private GwtTeamingMessages					m_messages;				//
	private HandlerRegistration					m_descClickHandlerReg;	//
	private HandlerRegistration					m_titleClickHandlerReg;	//
	private Image								m_actionsImg1;			//
	private Image								m_actionsImg2;			//
	private Image								m_avatarImg;			//
	private Image								m_unreadImg;			//
	private InlineLabel							m_actionsLabel;			//
	private Label								m_author;				//
	private Label								m_date;					//
	private String								m_authorId;				//
	private String								m_authorWSId;			// ID of the author's workspace.
	private String								m_binderId;				//
	private String								m_description;			//
	private String								m_entryId;				//
	private String								m_titleBase;			//
	private String								m_title;				//
	
    public static final int FORMAT_HTML = 1;
    public static final int FORMAT_NONE = 2;

    // The following controls how the activating a title's <A> is
    // handled.  If true, the <a> will be set with the appropriate
    // URL (when possible) so that when clicked, the <A> will be
    // used directly.  When false, the click handler on the <A>
    // will always launch the URL in a new window.
    //
    // The difference between these is where the link is opened.  When
    // an <a> is clicked by the user, it will typically be opened in a
    // tab.  The other method will open in it a new windows.
    //
    // Note that there is no way I know of to simulate a true
    // click on an <A> in code.  When activated programmatically, the
    // <A> always results in a new window.  This happens the first
    // time it's clicked and when its avatar is clicked.
    private static final boolean USE_TITLE_ANCHOR_CLICKS	= false;	// Leaving false so that the links always work the same.

	/**
	 * Constructor Method.
	 * 
	 * @param activityStreamCtrl
	 * @param descViewFormat
	 * @param showTitle
	 */
	public ActivityStreamUIEntry(ActivityStreamCtrl activityStreamCtrl, DescViewFormat descViewFormat, boolean showTitle) {
		super();
		
		m_activityStreamCtrl = activityStreamCtrl;
		m_descViewFormat = descViewFormat;
		m_showTitle = showTitle;

		m_images   = GwtTeaming.getImageBundle();
		m_messages = GwtTeaming.getMessages();
		
		m_mainPanel = new FlowPanel();
		m_mainPanel.addStyleName(getMainPanelStyleName());
		
		// Add a mouse over/out handler for the main panel.
		m_mainPanel.addDomHandler(this, MouseOverEvent.getType());
		m_mainPanel.addDomHandler(this, MouseOutEvent.getType() );

		// Add a place to show the avatar.
		m_avatarImg = new Image();
		m_avatarImg.addStyleName("cursorPointer");
		m_avatarImg.setVisible(false);
		m_mainPanel.add(m_avatarImg);
		
		// Add a click handler to the avatar.
		m_avatarImg.addClickHandler(this);
		
		// Create the panel that holds the entry's header.
		FlowPanel panel = createHeaderPanel();
		m_mainPanel.add(panel);
		
		// Create the panel that holds the content.
		panel = createContentPanel();
		m_mainPanel.add(panel);
		
		// Create an edit widget and hide it.
		m_editWidget = new ActivityStreamReply(false, new EditSuccessfulHandler() {	// false -> Don't show the 'Add a comment' hint.
			@Override
			@SuppressWarnings("unchecked")
			public boolean editSuccessful(Object editData) {
				if (editData instanceof HashMap) {
					// editData is a HashMap that holds the title and
					// the description.
					HashMap<String, String> map = ((HashMap<String,String>) editData);
					editEntry(map.get("title"), map.get("description"));
				}
				return true;
			}
		});
		m_editWidget.setVisible(false);
		m_mainPanel.add(m_editWidget);
		
		// Create a reply widget and hide it.
		m_replyWidget = new ActivityStreamReply(new EditSuccessfulHandler() {
			@Override
			@SuppressWarnings("unchecked")
			public boolean editSuccessful(Object replyData) {
				if (replyData instanceof HashMap) {
					// replyData is a HashMap that holds the title and
					// the description.
					HashMap<String, String> map = ((HashMap<String,String>) replyData);
					replyToEntry(map.get("title"), map.get("description"));
				}
				return true;
			}
			
		});
		m_replyWidget.setVisible(false);
		m_mainPanel.add(m_replyWidget);
		
		// Create a panel for comments to go in.
		m_commentsPanel = createCommentsPanel();
		if (null != m_commentsPanel) {
			m_mainPanel.add(m_commentsPanel);
		}

		// Create a click handler that will be used for the presence
		// control.
		m_presenceClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final Object src = event.getSource();
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						handleClickOnAuthor(((Widget) src).getElement());
					}
				});
			}
		};
		
		// All composites must call initWidget() in their constructors.
		initWidget(m_mainPanel);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param activityStreamCtrl
	 * @param descViewFormat
	 */
	public ActivityStreamUIEntry(ActivityStreamCtrl activityStreamCtrl, DescViewFormat descViewFormat) {
		// Always use the initial form of the constructor.
		this(activityStreamCtrl, descViewFormat, true);	// true -> Show the title.
	}
	
	/**
	 * Add the Actions menu to the given panel.
	 * 
	 * @param panel
	 */
	private void addActionsMenu(FlowPanel panel) {
		// Add an image the user can click on to invoke the Actions
		// menu.  Image 1 will be visible when the mouse is not over
		// the entry.  Image 2 will be visible when the mouse is over
		// the entry.
		FlexTable table = new FlexTable();
		table.addStyleName("activityStreamActionsTable");
		panel.add(table);
		
		// Get the panel that will hold the number of comments.
		FlowPanel numCommentsPanel = getNumCommentsPanel();
		if (null != numCommentsPanel) {
			table.setWidget(0, 0, numCommentsPanel);
		}
		
		FlowPanel actionsPanel = new FlowPanel();
		actionsPanel.addStyleName("activityStreamActionsPanel");
		table.setWidget(0, 1, actionsPanel);
		
		m_actionsLabel = new InlineLabel(m_messages.actionsLabel());
		m_actionsLabel.addStyleName("activityStreamActionsLabel");
		actionsPanel.add(m_actionsLabel);
		
		ImageResource imageResource = m_images.activityStreamActions1();
		m_actionsImg1 = new Image(imageResource);
		m_actionsImg1.addStyleName("activityStreamActionsImg1");
		m_actionsImg1.getElement().setId("activityStreamActionsImg1");
		actionsPanel.add(m_actionsImg1);
		imageResource = m_images.activityStreamActions2();
		m_actionsImg2 = new Image(imageResource);
		m_actionsImg2.addStyleName("activityStreamActionsImg2");
		m_actionsImg2.getElement().setId("activityStreamActionsImg2");
		m_actionsImg2.setVisible(false);
		actionsPanel.add(m_actionsImg2);

		// Add a click handler for the Actions image.
		ClickHandler clickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent clickEvent) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// Hide the actions2 image.
						m_actionsImg2.setVisible(false);
						m_actionsImg1.setVisible(true );

						m_actionsLabel.removeStyleName("activityStreamActionsLabelBold");
						
						// Invoke the Actions menu.
						invokeActionsMenu(m_actionsImg1);
					}
				});
			}
		};
		m_actionsImg1.addClickHandler( clickHandler);
		m_actionsImg2.addClickHandler( clickHandler);
		m_actionsLabel.addClickHandler(clickHandler);
	}
	
	/**
	 * This abstract method gives classes that extend this class an
	 * opportunity to add addition UI to the header.
	 * 
	 * @param headerPanel
	 */
	public abstract void addAdditionalHeaderUI(FlowPanel headerPanel);
	
	
	/**
	 * Clear all of the entry specific information such as the title,
	 * avatar URL, etc.
	 */
	public void clearEntrySpecificInfo() {
		m_avatarImg.setUrl(    ""   );
		m_avatarImg.setVisible(false);
		if (null != m_titleAnchor) { 
			m_titleAnchor.getElement().setInnerHTML("");
		}
		m_author.setText(                     "");
		m_date.setText(                       "");
		m_descPanel.getElement().setInnerHTML("");
		m_authorId         = null;
		m_authorWSId       = null;
		m_entryId          = null;
		m_binderId         = null;
		m_titleClickAction = null;
		
		// Remove the presence control from the presence panel.
		m_presencePanel.clear();
		
		// Hide the edit and reply UI's if we have one.
		if (null != m_editWidget)  m_editWidget.close();
		if (null != m_replyWidget) m_replyWidget.close();
		
		m_descViewFormat = m_activityStreamCtrl.getDefaultDescViewFormat();
	}

	/**
	 * If your class has comments, override this method and create a
	 * panel for the comments to live in.
	 * 
	 * @return
	 */
	public FlowPanel createCommentsPanel() {
		return null;
	}
	
	/**
	 * Create the panel that holds the author's name, entry's date and
	 * a 2 line description.
	 * 
	 * @return
	 */
	public FlowPanel createContentPanel() {
		final FlowPanel panel = new FlowPanel();
		panel.addStyleName(getContentPanelStyleName());

		m_presencePanel = new FlowPanel();
		m_presencePanel.addStyleName(getPresencePanelStyleName());
		panel.add(m_presencePanel);
		
		m_author = new Label();
		m_author.addStyleName(getAuthorStyleName());
		panel.add(m_author);
		
		// Add a mouse-over and mouse-out handlers for the author.
		m_author.addMouseOverHandler(this);
		m_author.addMouseOutHandler( this);
		
		// Add a click handler for the author.
		m_author.addClickHandler(this);
		
		m_date = new Label();
		m_date.addStyleName(getDateStyleName());
		panel.add(m_date);

		// Add a <DIV> for the description to live in.
		m_descPanel = new FlowPanel();
		m_descPanel.addStyleName(getDescStyleName());
		panel.add(m_descPanel);

		// Create a click handler that can be added to the description
		// panel so the user can click on the description to expand or
		// collapse it.
		m_descClickHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						if (m_descViewFormat == DescViewFormat.FULL)
						     setDescViewFormat(DescViewFormat.PARTIAL);
						else setDescViewFormat(DescViewFormat.FULL   );
					}
				});
			}
		};
		
		return panel;
	}
	
	/**
	 * Create the panel that holds the entry's header: avatar, title
	 * 
	 * @return
	 */
	public FlowPanel createHeaderPanel() {
		FlowPanel headerPanel = new FlowPanel();
		headerPanel.addStyleName(getEntryHeaderStyleName());
		
		// Add the actions menu to the header.
		addActionsMenu(headerPanel);
		
		// Are we supposed to show the title?
		if (m_showTitle) {
			// Yes!  Create a <SPAN> to hold the title.
			FlowPanel titlePanel = new FlowPanel();
			titlePanel.addStyleName(getTitlePanelStyleName());
			titlePanel.addStyleName("activityStreamTitlePanel");
			headerPanel.add(titlePanel);
			
			// Add an image that indicates this entry has not been
			// read.
			ImageResource imageResource = m_images.sunburst();
			m_unreadImg = new Image(imageResource);
			m_unreadImg.addStyleName("unreadImg");
			m_unreadImg.setTitle(m_messages.markEntryAsReadHint());
			m_unreadImg.setVisible(false);
			titlePanel.add(m_unreadImg);

			// Add a click handler for the 'unread' image.
			ClickHandler clickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent clickEvent) {
					// Mark this entry as read.
					GwtTeaming.fireEventAsync(new MarkEntryReadEvent(getThis()));
				}
			};
			m_unreadImg.addClickHandler(clickHandler);
			
			m_titleAnchor = new Anchor();
			m_titleAnchor.addStyleName(getTitleStyleName());
			titlePanel.add(m_titleAnchor);
			
			// Add a mouse-over handler for the title.
			m_titleAnchor.addMouseOverHandler(this);
			
			// Add a mouse-out handler for the activity stream source
			// name.
			m_titleAnchor.addMouseOutHandler(this);
			
			// Add a click handler for the activity stream source name.
			m_titleClickHandlerReg = m_titleAnchor.addClickHandler(this);
			
			// Add any additional UI to the header.  This gives classes
			// that extend this class an opportunity to put additional
			// data in the header.
			addAdditionalHeaderUI(headerPanel);
		}
		
		return headerPanel;
	}
	
	/**
	 * Return the activity stream we are associated with.
	 * 
	 * @return
	 */
	public ActivityStreamCtrl getActivityStreamCtrl() {
		return m_activityStreamCtrl;
	}
	
	/**
	 * Return the id of the author of this entry
	 * 
	 * @return
	 */
	public Long getAuthorId() {
		return Long.valueOf(m_authorId);
	}
	
	/**
	 * Return the name of the style used with the author.
	 * 
	 * @return
	 */
	public String getAuthorStyleName() {
		return "activityStreamEntryAuthor";
	}
	
	/**
	 * Return the name of the style used with the avatar image.
	 * 
	 * @param asEntry
	 * 
	 * @return
	 */
	public abstract String getAvatarImageStyleName(ActivityStreamEntry asEntry);

	/**
	 * Return the panel that holds the comments.
	 * 
	 * @return
	 */
	public FlowPanel getCommentsPanel() {
		return m_commentsPanel;
	}
	
	/**
	 * Return the name of the style used with the content panel.
	 * 
	 * @return
	 */
	public abstract String getContentPanelStyleName();
	
	/**
	 * Return the name of the style used with the date.
	 * 
	 * @return
	 */
	public String getDateStyleName() {
		return "activityStreamEntryDate";
	}
	
	/**
	 * Return the name of the style used with the description
	 * 
	 * @return
	 */
	public String getDescStyleName() {
		if (m_descViewFormat == DescViewFormat.FULL) {
			return getFullDescStyleName();
		}
		return getPartialDescStyleName();
	}
	
	/**
	 * Return how we should display the description
	 * 
	 * @return
	 */
	public DescViewFormat getDescViewFormat() {
		return m_descViewFormat;
	}
	
	/**
	 * Return the URL of the image that should be used for the given
	 * ActivityStreamEntry.
	 * 
	 * @param asEntry
	 * 
	 * @return
	 */
	public abstract String getEntryImgUrl(ActivityStreamEntry asEntry);
	
	/**
	 * ?
	 *  
	 * @return
	 */
	public abstract String getFullDescStyleName();
	
	/**
	 * ?
	 *  
	 * @return
	 */
	public abstract String getPartialDescStyleName();
	
	/**
	 * Return the description for the given entry.  If the format of
	 * the description is not HTML we will create safe HTML by escaping
	 * any HTML mark up in the description.
	 * 
	 * @param entry
	 * 
	 * @return
	 */
	public String getEntryDesc(ActivityStreamEntry entry) {
		String desc = entry.getEntryDescription();
		
		// Get the format of the description.
		int format = entry.getEntryDescriptionFormat();
		
		// Is the format plain text?
		if ((format == FORMAT_NONE) && GwtClientHelper.hasString(desc)) {
			// Yes!
			SafeHtmlBuilder builder = new SafeHtmlBuilder();
			builder = builder.appendEscaped(desc);
			desc = builder.toSafeHtml().asString();
		}
		
		if (null == desc) {
			desc = "";
		}
		
		return desc;
	}

	/**
	 * Return the name of the style used with the header.
	 * 
	 * @return
	 */
	public abstract String getEntryHeaderStyleName();
	
	/**
	 * Return the id of this entry.
	 * 
	 * @return
	 */
	public String getEntryId() {
		return m_entryId;
	}
	
	/**
	 * Return the id of this entry's binder.
	 * 
	 * @return
	 */
	public String getBinderId() {
		return m_binderId;
	}
	
	/**
	 * Returns an EntityId for this entry.
	 * 
	 * @return
	 */
	public EntityId getEntryEntityId() {
		return
			new EntityId(
				Long.parseLong(m_binderId),
				Long.parseLong(m_entryId ),
				EntityId.FOLDER_ENTRY);
	}
	
	/**
	 * Return the title that is being displayed for this entry.
	 * 
	 * @return
	 */
	public String getEntryTitle() {
		if (null != m_titleAnchor) {
			return m_titleAnchor.getText();
		}
		return "";
	}
	
	/*
	 * Return the title for the given entry.  If the entry is a reply,
	 * the title will be the reply number + title.  For example:
	 * 
	 * 		1.6.3 RE: What do you think you are doing?
	 * 
	 * If the entry is not a reply, the title will just be the title
	 * from the entry.
	 */
	private String getEntryTitle(ActivityStreamEntry entry) {
		String title;
		String entryType = entry.getEntryType();
		if (GwtClientHelper.hasString(entryType)) {
			// Does the entry have a reply number?
			String replyNum = entry.getEntryDocNum();
			if (GwtClientHelper.hasString(replyNum)) {
				int pPos = replyNum.indexOf(".");
				if (0 < pPos) {
			  		replyNum = replyNum.substring(pPos + 1);
				}
				title = (replyNum + ". ");
				
				// Yes!  Does the entry have a title?
				String tmp = entry.getEntryTitle();
				if (GwtClientHelper.hasString(tmp))
				     title += tmp;
				else title += m_messages.noTitle();
			}
			
			else {
				title = entry.getEntryTitle();
			}
		}
		
		else {
			title = entry.getEntryTitle();
		}
		
		return title;
	}

	/**
	 * ?
	 *  
	 * @return
	 */
	public FlowPanel getMainPanel() {
		return ((FlowPanel) getWidget());
	}
	
	/**
	 * Return the name of the style used with the <DIV> that holds the
	 * entry.
	 * 
	 * @return
	 */
	public abstract String getMainPanelStyleName();
	
	/**
	 * Return the panel that holds the number of comments.  Can return
	 * null.
	 * 
	 * @return
	 */
	public abstract FlowPanel getNumCommentsPanel();
	
	/**
	 * Return the name of the style used with the panel that holds the
	 * presence control.
	 * 
	 * @return
	 */
	public String getPresencePanelStyleName() {
		return "activityStreamPresencePanel";
	}
	
	/**
	 * ?
	 *  
	 * @return
	 */
	public boolean getShowTitle() {
		return m_showTitle;
	}
	
	/**
	 * Return the name of the style used with the panel that holds the
	 * title.
	 * 
	 * @return
	 */
	public abstract String getTitlePanelStyleName();
	
	/**
	 * Return the name of the style used with title.
	 * 
	 * @return
	 */
	public abstract String getTitleStyleName();
	
	/*
	 * Return this object.
	 */
	private ActivityStreamUIEntry getThis() {
		return this;
	}
	
	/**
	 * This method gets invoked when the user clicks on the author.
	 * 
	 * @param element
	 * 
	 * @return
	 */
	public void handleClickOnAuthor(Element element) {
		// Invoke the Simple Profile dialog.
		GwtTeaming.fireEvent(
			new InvokeSimpleProfileEvent(
				new SimpleProfileParams(
					element,
					m_authorId,
					m_authorWSId,
					m_author.getText())));
	}
	
	/**
	 * This method gets called when the user clicks on the avatar/file
	 * image.
	 * 
	 * @param element
	 */
	public abstract void handleClickOnAvatar(Element element);
	
	/**
	 * This method gets invoked when the user clicks on the title.
	 * Open the entry for the user to read.
	 */
	public void handleClickOnTitle() {
		connectTitleClickAsync();
	}

	/**
	 * Insert the given reply as the first reply to the top entry.
	 * 
	 * @param reply
	 */
	public abstract void insertReply(ActivityStreamEntry reply);
	
	/*
	 */
	private void invokeActionsMenu(UIObject target) {
		// Show the Actions popup menu.
		ActionsPopupMenu popupMenu = m_activityStreamCtrl.getActionsMenu();
		if (null != popupMenu) {
			// Show the Actions popup menu.
			popupMenu.showActionsMenu(this, target);
		}
	}
	
	/**
	 * Show the Edit UI.
	 */
	public void invokeEditUI() {
		if (null != m_editWidget) {
			m_editWidget.show(m_titleBase, m_description);
		}
	}

	/**
	 * Show the Reply UI.
	 */
	public void invokeReplyUI() {
		if (null != m_replyWidget) {
			String title = m_messages.defaultReplyTitle(getEntryTitle());
			m_replyWidget.show(title);
		}
	}

	/*
	 * Determine whether the description is totally visible.
	 */
	private boolean isDescTotallyVisible() {
		// Do we have a description?
		String desc = m_descPanel.getElement().getInnerHTML();
		if (GwtClientHelper.hasString(desc)) {
			// Yes!  Get the height of the panel that holds the
			// description.
			int panelHeight  = m_descPanel.getOffsetHeight();
			int scrollHeight = m_descPanel.getElement().getScrollHeight();
			if (scrollHeight > panelHeight) {
				return false;
			}
		}
		
		// If we get here the description is totally visible.
		return true;
	}
	
	/**
	 * Return whether this entry is unread.
	 * 
	 * @return
	 */
	public boolean isEntryUnread() {
		// First, try basing our decision of whether the entry is
		// unread on the visibility of the unread image.
		if (null != m_unreadImg) {
			return m_unreadImg.isVisible();
		}

		// Otherwise, base it on the entry seen flag copied from the
		// ActivityStreamEntry.
		return (!m_entrySeen);
	}
	
	/**
	 * Return true if the 'edit entry' widget is open and the user has
	 * changed the text in it and false otherwise.
	 * 
	 * @return
	 */
	public boolean isEditInProgress() {
		if (m_editWidget.isVisible()) {
			String desc = m_editWidget.getDesc();
			if (null == desc) desc = "";
			
			String thisDesc = m_description;
			if (null == thisDesc) thisDesc = "";
			
			return (desc.equals(thisDesc));
		}
		return false;
	}
	
	/**
	 * Return true if the 'reply to entry' widget is open and the user
	 * has entered text into it.
	 * 
	 * @return
	 */
	public boolean isReplyInProgress() {
		if (m_replyWidget.isVisible()) {
			String desc = m_replyWidget.getDesc();
			if (GwtClientHelper.hasString(desc)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * Make it so the user can click on the description to expand or
	 * collapse it.
	 */
	private void makeDescClickable() {
		// Does the description panel already have a click handler on it?
		if (null == m_descClickHandlerReg) {
			// No!  Add a click handler on the description panel.
			m_descClickHandlerReg = m_descPanel.addDomHandler(m_descClickHandler, ClickEvent.getType());
		}

		// Make the cursor a pointer when the user mouses over the
		// description.
		m_descPanel.addStyleName("cursorPointer");
		
		// Set the title of the description panel.
		if (m_descViewFormat == DescViewFormat.FULL)
		     m_descPanel.setTitle(m_messages.showPartialDescHint());
		else m_descPanel.setTitle(m_messages.showEntireDescHint() );
	}
	
	/*
	 * Make it so the user can't click on the description.
	 */
	private void makeDescNotClickable() {
		if (null != m_descClickHandlerReg) {
			m_descClickHandlerReg.removeHandler();
			m_descClickHandlerReg = null;
		}

		m_descPanel.removeStyleName("cursorPointer");
		m_descPanel.setTitle("");
	}
	
	/**
	 * Mark this entry as being read.
	 * 
	 * @param hideEntry
	 */
	public void markEntryAsRead(final boolean hideEntry) {
		// Issue a GWT RPC request to mark this entry as read.
		Long entryIdL = Long.valueOf(m_entryId);
		SetSeenCmd cmd = new SetSeenCmd(entryIdL);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetSeen(),
					m_entryId);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// Update the UI to reflect the fact that this
						// entry is now read.
						updateReadUnreadUI(true);
						
						// Do we need to hide this entry.
						if (hideEntry) {
							setVisible(false);
						}
					}
				});
			}			
		});
	}
	
	/**
	 * Mark this entry as being unread.
	 */
	public void markEntryAsUnread() {
		// Issue a GWT RPC request to mark this entry as unread.
		Long entryIdL = Long.valueOf(m_entryId);
		SetUnseenCmd cmd = new SetUnseenCmd(entryIdL);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SetUnseen(),
					m_entryId);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// Update the UI to reflect the fact that this
						// entry is now read.
						updateReadUnreadUI(false);
					}
				});
			}			
		});
	}
	
	/**
	 * ?
	 * 
	 * @param event
	 */
	@Override
	public void onClick(final ClickEvent event) {
		final Object src = event.getSource();
		if ((src == m_titleAnchor) || (src == m_author) || (src == m_avatarImg)) {
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					if      (src == m_titleAnchor) handleClickOnTitle();
					else if (src == m_author)      handleClickOnAuthor(((Widget) src).getElement());
					else if (src == m_avatarImg)   handleClickOnAvatar(((Widget) src).getElement());
				}
			});
		}
	}

	/**
	 * Remove the mouse-over style from the given label.
	 * 
	 * @param event
	 */
	@Override
	public void onMouseOut(MouseOutEvent event) {
		Object src = event.getSource();
		if ((src == m_titleAnchor) || (src == m_author)) {
			((Widget)src).removeStyleName("activityStreamHover");
		}
		else if (src == getWidget()) {
			// Hide the actions2 image.
			m_actionsImg2.setVisible(false);
			m_actionsImg1.setVisible(true );

			m_actionsLabel.removeStyleName("activityStreamActionsLabelBold");
		}
	}

	/**
	 * Add the mouse-over style to the given label.
	 * 
	 * @param event
	 */
	@Override
	public void onMouseOver(MouseOverEvent event) {
		Object src = event.getSource();
		if ((src == m_titleAnchor) || (src == m_author)) {
			((Widget)src).addStyleName("activityStreamHover");
		}
		else if (src == getWidget()) {
			// Hide the actions1 image and show the image the user
			// clicks on to invoke the Actions menu.
			m_actionsImg1.setVisible(false);
			m_actionsImg2.setVisible(true );

			m_actionsLabel.addStyleName("activityStreamActionsLabelBold");
		}
	}

	/*
	 * Edit this entry with the given text.
	 */
	private void editEntry(String title, String desc) {
		EditEntryCmd cmd = new EditEntryCmd(Long.parseLong(m_entryId), desc, title);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ReplyToEntry(),
					m_entryId);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				final ActivityStreamEntry asEntry = ((ActivityStreamEntryRpcResponseData) result.getResponseData()).getActivityStreamEntry();
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// Update the entry with the modified entry's
						// data.
						setData(asEntry);
					}
				});
			}
		});
	}
	
	/*
	 * Reply to this entry with the given text
	 */
	private void replyToEntry(String title, String desc) {
		ReplyToEntryCmd cmd = new ReplyToEntryCmd(getEntryId(), desc, title);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ReplyToEntry(),
					m_entryId);
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				final ActivityStreamEntry asEntry = ((ActivityStreamEntryRpcResponseData) result.getResponseData()).getActivityStreamEntry();
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// Add the reply to the top entry.
						insertReply(asEntry);
					}
				});
			}
		});
	}
	
	/**
	 * Set the data this we should display from the given
	 * ActivityStreamEntry.
	 * 
	 * @param
	 */
	public void setData(ActivityStreamEntry entryItem) {
		String avatarUrl = getEntryImgUrl(entryItem);
		m_avatarImg.removeStyleName("activityStreamTopEntryFileImg"  );
		m_avatarImg.removeStyleName("activityStreamTopEntryAvatarImg");
		m_avatarImg.removeStyleName("activityStreamCommentAvatarImg" );
		m_avatarImg.addStyleName(getAvatarImageStyleName(entryItem));
		m_avatarImg.setUrl(avatarUrl);
		m_avatarImg.setVisible(true);
		
		m_titleBase = entryItem.getEntryTitle();
		m_title     = getEntryTitle(entryItem);
		if (!(GwtClientHelper.hasString(m_title))) {
			m_title = m_messages.noTitle();
		}
		if (null != m_titleAnchor) {
			m_titleAnchor.getElement().setInnerHTML(m_title);
		}
		
		m_entrySeen = entryItem.getEntrySeen();
		updateReadUnreadUI(m_entrySeen);
		
		m_author.setText(entryItem.getAuthorName());
		m_authorId = entryItem.getAuthorId();
		m_authorWSId = entryItem.getAuthorWorkspaceId();
		
		m_date.setText(entryItem.getEntryModificationDate());
		
		// Set the description
		m_description = getEntryDesc(entryItem);
		m_descPanel.clear();
		if (m_activityStreamCtrl.getUsage().isBlog())
		     m_descPanel.add(new HTMLWithJavaScript(m_description));	// Bugzilla 941513:  Ensures that embedded YouTube videos get properly initialized.
		else m_descPanel.getElement().setInnerHTML(m_description);
		
		// Do we have a description?
		if (GwtClientHelper.hasString(m_description)) {
			// Yes
			makeDescClickable();
			
			// Schedule a command that will determine if the
			// description is totally visible.
			GwtClientHelper.deferCommand(new ScheduledCommand() {
				@Override
				public void execute() {
					// Is the description view format partial and the
					// description is totally visible anyway?
					if ((getDescViewFormat() == DescViewFormat.PARTIAL) && isDescTotallyVisible()) {
						// Yes, no need to make the description
						// clickable.
						makeDescNotClickable();
					}
				}
			});
		}
		else {
			// No, remove the click handler on the description panel.
			makeDescNotClickable();
		}
		
		m_entryId  = entryItem.getEntryId();
		m_binderId = entryItem.getParentBinderId();
		
		// Has the author's workspace been deleted?
		if (GwtClientHelper.hasString(m_authorWSId)) {
			// No!  Create a presence control for the author.
			PresenceControl presenceCtrl = new PresenceControl(m_authorId, m_authorWSId, false, false, false, entryItem.getAuthorPresence());
			presenceCtrl.setImageAlignment("top");
			presenceCtrl.addClickHandler(m_presenceClickHandler);
			presenceCtrl.addStyleName("displayInline");
			presenceCtrl.addStyleName("verticalAlignTop");
			presenceCtrl.setAnchorStyleName("cursorPointer");
			m_presencePanel.clear();	// Fixes bug 650204.
			m_presencePanel.add(presenceCtrl);
		}
		
		m_titleClickAction = null;
		
		// Set the format of how to view the description back to the
		// default.
		setDescViewFormat(m_activityStreamCtrl.getDefaultDescViewFormat());
	}
	
	/*
	 * Set the format used to display the description
	 */
	private void setDescViewFormat(DescViewFormat format) {
		if (null != m_descPanel) {
			// Remove the current style on the description panel.
			m_descPanel.removeStyleName(getDescStyleName());
			
			// Change the format used to display the description
			m_descViewFormat = format;
			
			// Add the appropriate style on the description panel.
			m_descPanel.addStyleName(getDescStyleName());
		}
	}
	
	/**
	 * Set the appropriate style on the title based on whether the
	 * entry has been read.
	 * 
	 * @param read
	 */
	public void updateReadUnreadUI(boolean read) {
		if ((null != m_titleAnchor) && (null != m_unreadImg)) {
			m_titleAnchor.removeStyleName("readEntry"  );
			m_titleAnchor.removeStyleName("unreadEntry");
	
			if (read) {
				m_titleAnchor.addStyleName("readEntry");
				m_unreadImg.setVisible(false);
			}
			else {
				m_titleAnchor.addStyleName("unreadEntry");
				m_unreadImg.setVisible(true);
			}
		}
	}
	
	/*
	 * Asynchronously connects the title <a> so that it performs an
	 * appropriate action and performs that action.
	 */
	private void connectTitleClickAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				connectTitleClickNow();
			}
		});
	}
	
	/*
	 * Synchronously connects the title <a> so that it performs an
	 * appropriate action and performs that action.
	 */
	private void connectTitleClickNow() {
		// If we've already connected the anchor...
		if (null != m_titleClickAction) {
			// ...simply perform its action.
			processTitleClickNow();
			return;
		}
		
		GetClickOnTitleActionCmd cmd = new GetClickOnTitleActionCmd(getEntryEntityId());
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetClickOnTitleAction());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				m_titleClickAction = ((ClickOnTitleActionRpcResponseData) response.getResponseData());
				switch (m_titleClickAction.getClickAction()) {
				case DOWNLOAD_FILE:
				case VIEW_AS_HTML:
					if (USE_TITLE_ANCHOR_CLICKS) {
						m_titleAnchor.setHref(m_titleClickAction.getUrl());
						m_titleAnchor.setTarget("_blank");
						m_titleClickHandlerReg.removeHandler();
					}
					break;
					
				case VIEW_DETAILS:
					m_titleAnchor.setHref("javascript:;");
					m_titleAnchor.setTarget("");
					break;
				}
				processTitleClickAsync();
			}
		});
	}

	/*
	 * Asynchronously processes a click on a title. 
	 */
	private void processTitleClickAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				processTitleClickNow();
			}
		});
	}
	
	/*
	 * Synchronously processes a click on a title. 
	 */
	private void processTitleClickNow() {
		GwtTeaming.fireEvent(new MarkEntryReadEvent(this));			
		switch (m_titleClickAction.getClickAction()) {
		case DOWNLOAD_FILE:
		case VIEW_AS_HTML:
			if (USE_TITLE_ANCHOR_CLICKS)
			     GwtClientHelper.jsClickWidget(m_titleAnchor);
			else GwtClientHelper.jsLaunchUrlInWindow(m_titleClickAction.getUrl(), "_blank");
			break;
			
		case VIEW_DETAILS:
			GwtTeaming.fireEvent(new ViewForumEntryEvent(m_titleClickAction.getUrl()));
			break;
		}
	}
}
