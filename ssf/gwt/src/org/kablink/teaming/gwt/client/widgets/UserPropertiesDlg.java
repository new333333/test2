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
import java.util.Set;
import java.util.Map;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingDataTableImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.InvokeUserShareRightsDlgEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetUserPropertiesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ProfileEntryInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ProfileEntryInfoRpcResponseData.ProfileAttribute;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData.AccountInfo;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData.HomeInfo;
import org.kablink.teaming.gwt.client.rpc.shared.UserPropertiesRpcResponseData.QuotaInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntryTitleInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.PerUserShareRightsInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Implements Vibe's user properties dialog.
 *  
 * @author drfoster@novell.com
 */
public class UserPropertiesDlg extends DlgBox {
	private GwtTeamingDataTableImageBundle	m_images;					// Access to the Vibe images resources we need for this cell.
	private GwtTeamingMessages				m_messages;					// Access to Vibe's messages.
	private List<HandlerRegistration>		m_registeredEventHandlers;	// Event handlers that are currently registered.
	private Long							m_userId;					// The user we're dealing with.
	private UIObject						m_showRelativeTo;			// UIObject to show the dialog relative to.  null -> Center it on the screen.
	private UserPropertiesRpcResponseData	m_userProperties;			// Information about managing the user, once read from the server.
	private VibeFlowPanel					m_fp;						// The panel holding the dialog's content.

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private final static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private UserPropertiesDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.Close);

		// ...initialize everything else...
		m_images   = GwtTeaming.getDataTableImageBundle();
		m_messages = GwtTeaming.getMessages();
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.userPropertiesDlgHeader(),	// The dialog's header.
			getSimpleSuccessfulHandler(),		// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),			// The dialog's EditCanceledHandler.
			null);								// Create callback data.  Unused.
	}

	/*
	 * Adds information about the user's account to the grid.
	 */
	private void addAccountInfo(FlexTable grid, FlexCellFormatter cf, RowFormatter rf, AccountInfo account, boolean newSection) {
		// Add the last login date/time stamp...
		int row = grid.getRowCount();
		if (newSection) {
			// If this is supposed to be in a new section, add the
			// section style to the row.
			rf.addStyleName(row, "vibe-userPropertiesDlg-sectionRow");
		}
		InlineLabel il = new InlineLabel(m_messages.userPropertiesDlgAccount());
		il.addStyleName("vibe-userPropertiesDlg-buttonLook");
		grid.setWidget(           row, 0, il);
		cf.setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
		addLabeledText(grid, row, m_messages.userPropertiesDlgLabel_LastLogin(), account.getLastLogin(), true);

		// ...add the type of account...
		row = grid.getRowCount();
		boolean internal = account.isInternal();
		ImageResource ir = (internal ? m_images.internalUser() : m_images.externalUser());
		Image i = GwtClientHelper.buildImage(
			ir.getSafeUri().asString(),
			(internal                                       ?
				m_messages.vibeDataTable_Alt_InternalUser() :
				m_messages.vibeDataTable_Alt_ExternalUser()));
		i.addStyleName("vibe-userPropertiesDlg-attrImage");
		addLabeledWidget(grid, row, m_messages.userPropertiesDlgLabel_Type(), i);

		// ...for internal users...
		if (internal) {
			// ...add how the account got created...
			row = grid.getRowCount();
			boolean ldap = account.isFromLdap();
			addLabeledText(
				grid, 
				row,
				m_messages.userPropertiesDlgLabel_Source(),
				(ldap                                        ?
					m_messages.userPropertiesDlgSourceLDAP() :
					m_messages.userPropertiesDlgSourceLocal()));

			// ...and if from LDAP...
			if (ldap) {
				// ...and what we know about the LDAP information.
				String ldapAttr = account.getLdapDN();
				if (GwtClientHelper.hasString(ldapAttr)) {
					row = grid.getRowCount();
					addLabeledText(grid, row, m_messages.userPropertiesDlgLabel_LdapDN(), ldapAttr);
				}
				ldapAttr = account.getEDirContainer();
				if (GwtClientHelper.hasString(ldapAttr)) {
					row = grid.getRowCount();
					addLabeledText(grid, row, m_messages.userPropertiesDlgLabel_eDirContainer(), ldapAttr);
				}
			}
		}

		// ...finally, add whether the user has access to adHoc
		// ...folders.
		row = grid.getRowCount();
		addLabeledText(
			grid,
			row,
			m_messages.userPropertiesDlgLabel_PersonalStorage(),
			(account.hasAdHocFolders()            ?
				m_messages.userPropertiesDlgYes() :
				m_messages.userPropertiesDlgNo()));
	}

	/*
	 * Adds information about the user's Home folder to the grid.
	 */
	private void addHomeInfo(FlexTable grid, FlexCellFormatter cf, RowFormatter rf, HomeInfo home, boolean newSection) {
//!		...this needs to be implemented...
	}
	
	/*
	 * Adds information about the user's identity to the grid.
	 */
	private void addIdentityInfo(FlexTable grid, FlexCellFormatter cf) {
		int		row;
		String	title;

		// Add the user's title...
		ProfileEntryInfoRpcResponseData	profile = m_userProperties.getProfile();
		Map<String, ProfileAttribute>	attrMap = profile.getProfileEntryInfo();
		ProfileAttribute				pa      = attrMap.get("title");
		if (null != pa) {
			attrMap.remove(pa);
			row = grid.getRowCount();
			title = pa.getAttributeValue();
			InlineLabel il = new InlineLabel(title);
			il.addStyleName("vibe-userPropertiesDlg-title");
			il.setWordWrap(false);
			grid.setWidget(row, 0, il);
			cf.setColSpan( row, 0, 3);
		}
		else {
			title = "";
		}
		
		// ...add the user's avatar...
		row = grid.getRowCount();
		Image avatarImg = new Image();
		avatarImg.addStyleName("vibe-userPropertiesDlg-avatar");
		String avatarUrl = profile.getAvatarUrl();
		if (!(GwtClientHelper.hasString(avatarUrl)))
		     avatarImg.setUrl(GwtTeaming.getDataTableImageBundle().userPhoto().getSafeUri());
		else avatarImg.setUrl(avatarUrl);
		avatarImg.setTitle(title);
		grid.setWidget(row, 0, avatarImg);

		// ...and add the user's 'About Me' HTML.
		FlowPanel aboutMe = new FlowPanel();
		aboutMe.addStyleName("vibe-userPropertiesDlg-aboutMe");
		String aboutMeHtml = profile.getAboutMeHtml();
		if (GwtClientHelper.hasString(aboutMeHtml)) {
			aboutMe.getElement().setInnerHTML(aboutMeHtml);
		}
		grid.setWidget(         row, 1, aboutMe);
		cf.setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_TOP);
		cf.setColSpan(          row, 1, 2);
	}
	
	/*
	 * Adds a labeled item to the grid.
	 */
	private void addLabeledText(FlexTable grid, int row, String label, String text, boolean showUnknown) {
		// Validate the text...
		if (null == text) {
			text = "";
		}
		if (showUnknown && (!(GwtClientHelper.hasString(text)))) {
			text = m_messages.userPropertiesDlgUnknown();
		}
		
		// ...create a widget for it...
		InlineLabel il = new InlineLabel(text);
		il.addStyleName("vibe-userPropertiesDlg-attrValue");
		il.setWordWrap(false);

		// ...and add the labeled widget to the grid.
		addLabeledWidget(grid, row, label, il);
	}
	
	private void addLabeledText(FlexTable grid, int row, String label, String value) {
		// Always use the initial form of the method.
		addLabeledText(grid, row, label, value, false);
	}

	/*
	 * Adds a labeled widget to the grid.
	 */
	private void addLabeledWidget(FlexTable grid, int row, String label, Widget w) {
		// Add the items label...
		InlineLabel il = new InlineLabel(label);
		il.addStyleName("vibe-userPropertiesDlg-attrCaption");
		il.setWordWrap(false);
		grid.setWidget(row, 1, il);
		
		// ...and widget.
		grid.setWidget(row, 2, w );
	}

	/*
	 * Adds information about the user's Net Folders to the grid.
	 */
	private void addNetFoldersInfo(FlexTable grid, FlexCellFormatter cf, RowFormatter rf, List<EntryTitleInfo> netFolders, boolean newSection) {
//!		...this needs to be implemented...
	}
	
	/*
	 * Adds information about the user's profile to the grid.
	 */
	private void addProfileInfo(FlexTable grid, FlexCellFormatter cf, RowFormatter rf, ProfileEntryInfoRpcResponseData profile, boolean newSection) {
		// Are there any profile attributes to add?
		Map<String, ProfileAttribute>	attrMap   = profile.getProfileEntryInfo();
		Set<String>						attrKeys  = attrMap.keySet();
		int								attrCount = ((null == attrKeys) ? 0 : attrKeys.size());
		if (0 == attrCount) {
			// No!  Bail.
			return;
		}
		
		int row = grid.getRowCount();
		if (newSection) {
			// If this is supposed to be in a new section, add the
			// section style to the row.
			rf.addStyleName(row, "vibe-userPropertiesDlg-sectionRow");
		}
		
		// Do we have a URL to edit this user's profile?
		final String modifyUrl = profile.getModifyUrl();
		if (GwtClientHelper.hasString(modifyUrl)) {
			// Yes!  Create a push button to do so...
			Button button = new Button(m_messages.userPropertiesDlgEdit_Profile());
			button.addStyleName("vibe-userPropertiesDlg-buttonAct vibe-userPropertiesDlg-buttonLook");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					GwtClientHelper.jsLaunchToolbarPopupUrl(modifyUrl, 850, 600);
				}
			});

			// ...and add the button to the grid.
			grid.setWidget(           row, 0, button);
			cf.setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
		}
		
		// Scan the profile attributes we have for the user...
		for (String attrKey:  attrKeys) {
			// ...adding each to the grid.
			ProfileAttribute pa = attrMap.get(attrKey);
			addLabeledText(
				grid,
				row,
				labelizeCaption(pa.getAttributeCaption()),
				pa.getAttributeValue());
			row = grid.getRowCount();
		}
	}
	
	/*
	 * Adds information about the user's disk quota to the grid.
	 */
	private void addQuotaInfo(FlexTable grid, FlexCellFormatter cf, RowFormatter rf, QuotaInfo home, boolean newSection) {
//!		...this needs to be implemented...
	}
	
	/*
	 * Adds information about the user's sharing rights to the grid.
	 */
	private void addSharingInfo(FlexTable grid, FlexCellFormatter cf, RowFormatter rf, PerUserShareRightsInfo share, boolean newSection) {
		int row = grid.getRowCount();
		if (newSection) {
			// If this is supposed to be in a new section, add the
			// section style to the row.
			rf.addStyleName(row, "vibe-userPropertiesDlg-sectionRow");
		}
		
		// Does the user have a workspace that we could get the sharing
		// rights off of?
		boolean hasWorkspace = (null != share);
		if (hasWorkspace) {
			// Yes!  Add a button so that the rights can be set.
			final Button button = new Button(m_messages.userPropertiesDlgEdit_Sharing());
			button.addStyleName("vibe-userPropertiesDlg-buttonAct vibe-userPropertiesDlg-buttonLook");
			button.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					List<Long> userIds = new ArrayList<Long>();
					userIds.add(m_userId);
					GwtTeaming.fireEventAsync(
						new InvokeUserShareRightsDlgEvent(
							userIds,
							button));
				}
			});

			// ...and add the button to the grid.
			grid.setWidget(           row, 0, button);
			cf.setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);

			// Construct a string with the user's current sharing
			// rights...
			StringBuffer	rightsBuf = new StringBuffer();
			boolean			needJoint = false;
			if (share.isAllowInternal()) {
				rightsBuf.append(m_messages.userPropertiesDlgSharing_Internal());
				needJoint = true;
			}
			if (share.isAllowExternal()) {
				if (needJoint) rightsBuf.append("+");
				else           needJoint = true;
				rightsBuf.append(m_messages.userPropertiesDlgSharing_External());
			}
			if (share.isAllowPublic()) {
				if (needJoint) rightsBuf.append("+");
				else           needJoint = true;
				rightsBuf.append(m_messages.userPropertiesDlgSharing_Public());
			}
			if (share.isAllowForwarding()) {
				if (needJoint) rightsBuf.append("/");
				else           needJoint = true;
				rightsBuf.append(m_messages.userPropertiesDlgSharing_Forwarding());
			}
			String rights = rightsBuf.toString();
			if (!(GwtClientHelper.hasString(rights))) {
				rights = m_messages.userPropertiesDlgSharing_NoRights();
			}
			
			// ...and add them to the grid.
			addLabeledText(
				grid,
				row,
				m_messages.userPropertiesDlgLabel_Sharing(),
				rights);
		}
		
		else {
			// No, the user doesn't have a workspace.  Their sharing
			// rights could not be determined.
			InlineLabel il = new InlineLabel(m_messages.userPropertiesDlgSharing());
			il.addStyleName("vibe-userPropertiesDlg-buttonLook");
			grid.setWidget(           row, 0, il);
			cf.setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);			
			addLabeledText(
				grid,
				row,
				m_messages.userPropertiesDlgLabel_Sharing(),
				m_messages.userPropertiesDlgNoWS());
		}
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
		m_fp = new VibeFlowPanel();
		m_fp.addStyleName("vibe-userPropertiesDlg-rootPanel");
		return m_fp;
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
	 * Ensures a string being used for label ends with a ':'.
	 */
	private String labelizeCaption(String s) {
		if (null != s) s = s.trim();
		int l = ((null == s) ? 0 : s.length());
		if ((0 < l) && (':' != s.charAt(l - 1))) {
			s = m_messages.userPropertiesDlgLabelize(s);
		}
		return s;
	}
	
	/*
	 * Asynchronously loads the entry types the user can select from.
	 */
	private void loadPart1Async() {
		ScheduledCommand doLoad = new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		};
		Scheduler.get().scheduleDeferred(doLoad);
	}
	
	/*
	 * Synchronously loads the entry types the user can select from.
	 */
	private void loadPart1Now() {
		GwtClientHelper.executeCommand(
				new GetUserPropertiesCmd(m_userId),
				new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetUserProperties());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Store the user properties and complete the
				// population of the dialog.
				m_userProperties = ((UserPropertiesRpcResponseData) response.getResponseData());
				populateDlgAsync();
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
		// Clear anything already in the dialog (from a previous
		// usage, ...)
		m_fp.clear();
		
		// ...create the grid to hold the dialog's contents...
		FlexTable grid = new FlexTable();
		grid.addStyleName("vibe-userPropertiesDlg-grid");
		m_fp.add(grid);
		FlexCellFormatter	cf = grid.getFlexCellFormatter();
		RowFormatter		rf = grid.getRowFormatter();

		// ...add the various components of what we know about the
		// ...user...
		addIdentityInfo(  grid, cf                                                );
		addProfileInfo(   grid, cf, rf, m_userProperties.getProfile(),       false);	// false -> Don't add with a section header.
		addAccountInfo(   grid, cf, rf, m_userProperties.getAccountInfo(),   true );	// true  ->       Add with a section header.
		addSharingInfo(   grid, cf, rf, m_userProperties.getSharingRights(), true );	// true  ->       Add with a section header.
		addQuotaInfo(     grid, cf, rf, m_userProperties.getQuotaInfo(),     true );	// true  ->       Add with a section header.
		addHomeInfo(      grid, cf, rf, m_userProperties.getHomeInfo(),      true );	// true  ->       Add with a section header.
		addNetFoldersInfo(grid, cf, rf, m_userProperties.getNetFolders(),    true );	// true  ->       Add with a section header.
		
		// ...and finally, show the dialog.
		if (null == m_showRelativeTo)
		     center();
		else showRelativeTo(m_showRelativeTo);
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
	 * Asynchronously runs the given instance of the user properties
	 * dialog.
	 */
	private static void runDlgAsync(final UserPropertiesDlg upDlg, final Long userId, final UIObject showRelativeTo) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				upDlg.runDlgNow(userId, showRelativeTo);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the user properties
	 * dialog.
	 */
	private void runDlgNow(Long userId, UIObject showRelativeTo) {
		// Store the parameters and populate the dialog.
		m_userId         = userId;
		m_showRelativeTo = showRelativeTo;
		loadPart1Async();
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
	/* the user properties dialog and perform some operation on it.  */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the user properties dialog
	 * asynchronously after it loads. 
	 */
	public interface UserPropertiesDlgClient {
		void onSuccess(UserPropertiesDlg upDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the UserPropertiesDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final UserPropertiesDlgClient upDlgClient,
			
			// initAndShow parameters,
			final UserPropertiesDlg	upDlg,
			final Long				userId,
			final UIObject			showRelativeTo) {
		GWT.runAsync(UserPropertiesDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_UserPropertiesDlg());
				if (null != upDlgClient) {
					upDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != upDlgClient) {
					// Yes!  Create it and return it via the callback.
					UserPropertiesDlg upDlg = new UserPropertiesDlg();
					upDlgClient.onSuccess(upDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(upDlg, userId, showRelativeTo);
				}
			}
		});
	}
	
	/**
	 * Loads the UserPropertiesDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param upDlgClient
	 */
	public static void createAsync(UserPropertiesDlgClient upDlgClient) {
		doAsyncOperation(upDlgClient, null, null, null);
	}
	
	/**
	 * Initializes and shows the user properties dialog.
	 * 
	 * @param upDlg
	 * @param userId
	 * @param showRelativeTo
	 */
	public static void initAndShow(UserPropertiesDlg upDlg, Long userId, UIObject showRelativeTo) {
		doAsyncOperation(null, upDlg, userId, showRelativeTo);
	}
	
	/**
	 * Initializes and shows the user properties dialog.
	 * 
	 * @param upDlg
	 * @param userId
	 */
	public static void initAndShow(UserPropertiesDlg upDlg, Long userId) {
		// Always use the initial form of the method.
		initAndShow(upDlg, userId, null);	// null -> No relative widget, show centered.
	}
}
