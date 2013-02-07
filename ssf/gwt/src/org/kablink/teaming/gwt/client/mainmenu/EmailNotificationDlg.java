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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMainMenuImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.EmailNotificationInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.EmailNotificationInfoRpcResponseData.EmailAddressInfo;
import org.kablink.teaming.gwt.client.rpc.shared.GetEmailNotificationInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveEmailNotificationInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeHorizontalPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements Vibe's email notification dialog.
 *  
 * @author drfoster@novell.com
 */
public class EmailNotificationDlg extends DlgBox implements EditSuccessfulHandler {
	private boolean									m_isFilr;					//
	private CheckBox								m_footerCB;					//
	private EmailNotificationInfoRpcResponseData	m_emailNotificationInfo;	//
	private Long									m_binderId;					//
	private List<EntityId>							m_entityIds;				// List<EntityId> of entity subscriptions.  null -> Binder email notification mode.
	private ListBox									m_digestList;				//
	private ListBox									m_msgList;					//
	private ListBox									m_msgNoAttList;				//
	private ListBox									m_textList;					//
	private UIObject								m_showRelativeTo;			//
	private VerticalPanel							m_vp;						//

	protected final static GwtTeamingFilrImageBundle		m_filrImages = GwtTeaming.getFilrImageBundle();		// Access to Filr's      image resources.
	protected final static GwtTeamingImageBundle			m_images     = GwtTeaming.getImageBundle();			// Access to Vibe's      image resources.
	protected final static GwtTeamingMainMenuImageBundle	m_menuImages = GwtTeaming.getMainMenuImageBundle();	// Access to Vibe's menu image resources.
	protected final static GwtTeamingMessages				m_messages   = GwtTeaming.getMessages();			// Access to Vibe's localized string resources.
	
	// The following are used as the values for the non-email address
	// values put in to the email address list box.
	private final static String EMA_VALUE_CLEAR_SUBSCRIPTION	= "";
	private final static String EMA_VALUE_MAKE_SELECTION		= "";
	private final static String EMA_VALUE_NO_CHANGES			= "*no-change*";
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EmailNotificationDlg() {
		// Initialize the superclass...
		super(false, true, DlgButtonMode.OkCancel);

		// ...initialize anything else requiring initialization...
		m_isFilr = GwtClientHelper.isLicenseFilr();
		
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuEmailNotificationDlgHeader(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Create callback data.  Unused. 
	}

	/*
	 * Builds the widgets for an email address selection group.
	 */
	private ListBox buildEMAListWidgets(
		String       panelStyle,	// The style to use for the panel containing the widgets.
		String       panelLabel,	// The label above the widgets.
		String       labelStyle,	// The style to use for the label above the widgets.
		List<String> emaList,		// The list of email addresses that apply to this selection group.
		String       emaBoxStyle)	// The style to use for the email address ListBox.
	{
		// Create a panel for the email address list widgets...
		VerticalPanel vp = new VibeVerticalPanel(null, null);
		vp.addStyleName(panelStyle);
		m_vp.add(vp);
		
		// ...create the label above the ListBox...
		InlineLabel il = new InlineLabel(panelLabel);
		il.addStyleName("vibe-emailNotifDlg_SectionLabel " + labelStyle);
		il.setWordWrap(false);
		vp.add(il);

		// ...create the ListBox...
		ListBox emaBox = new ListBox(true);
		emaBox.addStyleName("vibe-emailNotifDlg_SectionList " + emaBoxStyle);
	    emaBox.setVisibleItemCount(4);
	    if (isMultipleEntitySubscriptions()) {
	    	emaBox.addItem(m_messages.mainMenuEmailNotificationDlgNoChanges(),              EMA_VALUE_NO_CHANGES        );
	    	emaBox.addItem(m_messages.mainMenuEmailNotificationDlgClearEntrySubscription(), EMA_VALUE_CLEAR_SUBSCRIPTION);
	    }
	    else {
	    	emaBox.addItem(m_messages.mainMenuEmailNotificationDlgMakeSelection(), EMA_VALUE_MAKE_SELECTION);
	    }
    	emaBox.setItemSelected(0, emaList.isEmpty());
	    for (EmailAddressInfo emi:  m_emailNotificationInfo.getEmailAddresses()) {
	    	String emiA = emi.getAddress();
	    	emaBox.addItem(emiA, emi.getType());
	    	emaBox.setItemSelected((emaBox.getItemCount() - 1), emaList.contains(emiA));
	    }
	    vp.add(emaBox);

	    // ...and return the ListBox that contains the list.
		return emaBox;
	}
	
	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	private Image buildSpinnerImage() {
		return GwtClientHelper.buildImage(m_menuImages.spinner());
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
		m_vp = new VerticalPanel();
		m_vp.addStyleName("vibe-emailNotifDlg_Content");
		return m_vp;
	}

	/*
	 * Creates the selection description widget(s) and returns the
	 * containing most Widget.
	 */
	private Widget createSelectionDesc() {
		// Create a horizontal panel to hold the selection description.
		HorizontalPanel hp = new VibeHorizontalPanel(null, null);
		hp.addStyleName("vibe-emailNotifDlg_DescPanel");

		// Add an image for the selection(s) to the left side of the
		// description panel.
		String imageUrl;;
		if (isMultipleEntitySubscriptions()) {
			imageUrl = m_filrImages.multipleItems().getSafeUri().asString();
		}
		else {
			imageUrl = m_emailNotificationInfo.getSingleEntityIconUrl();
			if (GwtClientHelper.hasString(imageUrl)) {
				imageUrl = (GwtClientHelper.getImagesPath() + imageUrl); 
			}
			else {
				if (isBinderSubscription())
					 imageUrl = m_filrImages.folder_medium().getSafeUri().asString();
				else imageUrl = m_filrImages.entry_medium().getSafeUri().asString();
			}
		}
		Image img = GwtClientHelper.buildImage(imageUrl);
		img.addStyleName("vibe-emailNotifDlg_DescImg");
		hp.add(img);

		// Create a vertical panel to hold the name/path if the
		// selection(s).
		VerticalPanel vp = new VibeVerticalPanel(null, null);
		vp.addStyleName("vibe-emailNotifDlg_DescNamePanel");
		hp.add(vp);
		
		InlineLabel il;
		if (isMultipleEntitySubscriptions()) {
			// Add the title for the selections.
			il = new InlineLabel(m_messages.mainMenuEmailNotificationDlgMultipleItems(m_entityIds.size()));
			il.addStyleName("vibe-emailNotifDlg_DescNameTitle");
			vp.add(il);
			
			// For multiple selections, we don't show a path.
		}
		else {
			// Add the title for the selection.
			String seString = m_emailNotificationInfo.getSingleEntityTitle();
			if (!(GwtClientHelper.hasString(seString))) {
				seString = m_messages.mainMenuEmailNotificationDlgNoTitle();
			}
			il = new InlineLabel(seString);
			il.addStyleName("vibe-emailNotifDlg_DescNameTitle");
			vp.add(il);
			
			// Add the path for the selection.
			seString = m_emailNotificationInfo.getSingleEntityPath();
			if (GwtClientHelper.hasString(seString)) {
				il = new InlineLabel(seString);
				il.addStyleName("vibe-emailNotifDlg_DescNamePath");
				vp.add(il);
			}
		}

		// Return the Widget containing the description information.
		return hp;
	}
	
	/*
	 * Clears the contents of the dialog and displays a message that
	 * we're reading the contents of the current email notification
	 * settings.
	 */
	private void displayReading() {
		m_vp.clear();
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("vibe-emailNotifDlg_ReadingPanel");
		fp.add(buildSpinnerImage());
		InlineLabel il = new InlineLabel(m_messages.mainMenuEmailNotificationDlgReading());
		il.addStyleName("vibe-emailNotifDlg_Label vibe-emailNotifDlg_ReadingLabel");
		fp.add(il);
		m_vp.add(fp);
	}

	/**
	 * This method gets called when user user presses the OK push
	 * button.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() interface
	 * method.
	 * 
	 * @param callbackData
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object callbackData) {
		// Start saving the contents of the dialog and return false.
		// We'll keep the dialog open until the save is successful, at
		// which point, we'll close it. 
		saveEmailNotificationInfoAsync();
		return false;
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
	 * Adds the selected email addresses from a ListBox to a
	 * List<String>.
	 * 
	 * Returns true if the selections made are valid and false
	 * otherwise.  If false is returned, the user will have been
	 * informed about the problem.
	 */
	private boolean getSelectedEMAs(List<String> emaTypeList, ListBox emaBox) {
		boolean	builtInSelected = false;
		int		selCount        = 0;
		int		c               = emaBox.getItemCount();
		for (int i = 0; i < c; i += 1) {
			// Is this item selected?
			if (emaBox.isItemSelected(i)) {
				// Yes!  Count it.
				selCount += 1;
				
				// Is this one of the built-in (i.e.,
				// '--make a selection--', ...) items?
				String itemValue = emaBox.getValue(i);
				boolean isBuiltIn = ((0 == i) || ((1 == i) && isMultipleEntitySubscriptions()));
				if (isBuiltIn) {
					// Yes!  We only add those that will involve a
					// change on the server to the list.
					builtInSelected = true;
					if (itemValue.equals(EMA_VALUE_MAKE_SELECTION) ||
							itemValue.equals(EMA_VALUE_CLEAR_SUBSCRIPTION)) {
						continue;
					}
				}
				
				// Add its type (i.e., its value) to the
				// List<String>.
				emaTypeList.add(itemValue);
			}
		}

		// Did the user select more than one item that includes at
		// least one of the built-in items?
		if (builtInSelected && (1 < selCount)) {
			// Yes!  That doesn't make sense.  Tell them about the
			// problem and return false.
			GwtClientHelper.deferredAlert(m_messages.mainMenuEmailNotificationDlgErrorSelection());
			return false;
		}
		
		// If we get here, the selections from this ListBox were valid.
		// Return true.
		return true;
	}

	/*
	 * Returns false if the dialog is running in entity subscription
	 * mode or true if it's running in binder email notification mode.
	 */
	private boolean isBinderSubscription() {
		return (null == m_entityIds);
	}
	
	/*
	 * Returns true if the dialog is running in multiple entity
	 * subscription mode or false if it's running in binder or single
	 * entity subscription mode.
	 */
	private boolean isMultipleEntitySubscriptions() {
		return ((null != m_entityIds) && (1 < m_entityIds.size()));
	}
	
	/*
	 * Returns true if the dialog is running in single entity
	 * subscription mode or false if it's running in binder or multiple
	 * entity subscription mode.
	 */
	private boolean isSingleEntitySubscription() {
		return ((null != m_entityIds) && (1 == m_entityIds.size()));
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
		GetEmailNotificationInfoCmd geniCmd;
		if      (isBinderSubscription())       geniCmd = new GetEmailNotificationInfoCmd(new EntityId(null, m_binderId, EntityId.FOLDER));
		else if (isSingleEntitySubscription()) geniCmd = new GetEmailNotificationInfoCmd(m_entityIds.get(0));
		else                                   geniCmd = new GetEmailNotificationInfoCmd();
		GwtClientHelper.executeCommand(geniCmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetEmailNotificationInfo());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Extract the email notification information from the
				// response data and use it to populate the dialog.
				m_emailNotificationInfo = ((EmailNotificationInfoRpcResponseData) response.getResponseData());
				populateFromEmailNotificationInfoAsync();
			}
		});
	}

	/*
	 * Asynchronously populates the dialog from an
	 * EmailNotificationInfoRpcResponseData object.
	 */
	private void populateFromEmailNotificationInfoAsync() {
		ScheduledCommand doPopulate = new ScheduledCommand() {
			@Override
			public void execute() {
				populateFromEmailNotificationInfoNow();
			}
		};
		Scheduler.get().scheduleDeferred(doPopulate);
	}
	
	/*
	 * Synchronously populates the dialog from an
	 * EmailNotificationInfoRpcResponseData object.
	 */
	private void populateFromEmailNotificationInfoNow() {
		// Clear the current content of the content panel.
		m_vp.clear();
		
		// Create the banner text...
		Label l = new Label(
			m_isFilr                                                ?
				m_messages.mainMenuEmailNotificationDlgBannerFilr() :
				m_messages.mainMenuEmailNotificationDlgBannerVibe());
		l.addStyleName("vibe-emailNotifDlg_BannerLabel");
		m_vp.add(l);

		// ...create the selection description widget(s)...
		m_vp.add(createSelectionDesc());

		// ...create the message type label...
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("vibe-emailNotifDlg_MsgTypePanel");
		InlineLabel il = new InlineLabel(m_messages.mainMenuEmailNotificationDlgMessageType());
		il.addStyleName("vibe-emailNotifDlg_MsgTypeLabel");
		il.setWordWrap(false);
		fp.add(il);
		final String bannerHelpUrl = m_emailNotificationInfo.getBannerHelpUrl();
		if (GwtClientHelper.hasString(bannerHelpUrl)) {
			Image img = GwtClientHelper.buildImage(m_menuImages.help());
			img.addStyleName("vibe-emailNotifDlg_MsgTypeHelp");
			img.setTitle(m_messages.mainMenuEmailNotificationDlgAltHelpAll());
			img.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open(bannerHelpUrl, "teaming_help_window", "resizeable,scrollbar");
				}
			});
			fp.add(img);
		}
		m_vp.add(fp);
		
		// ...if we're in binder email notification mode...
		if (isBinderSubscription()) {
			// ...create the widgets for the digest selection...
			m_digestList = buildEMAListWidgets(
				"vibe-emailNotifDlg_DigestPanel",
				m_messages.mainMenuEmailNotificationDlgDigest(),
				"vibe-emailNotifDlg_Digest",
				m_emailNotificationInfo.getDigestAddresses(),
				"vibe-emailNotifDlg_DigestList");
		}
		
		// ...create the widgets for the individual messages without
		// ...attachments selection...
		m_msgNoAttList = buildEMAListWidgets(
			"vibe-emailNotifDlg_MsgNoAttPanel",
			m_messages.mainMenuEmailNotificationDlgIndividualMessagesNoAttachments(),
			"vibe-emailNotifDlg_MsgNoAtt",
			m_emailNotificationInfo.getMsgNoAttAddresses(),
			"vibe-emailNotifDlg_MsgNoAttList");
		
		// ...create the widgets for the individual messages
		// ...selection...
		m_msgList = buildEMAListWidgets(
			"vibe-emailNotifDlg_MsgPanel",
			m_messages.mainMenuEmailNotificationDlgIndividualMessages(),
			"vibe-emailNotifDlg_Msg",
			m_emailNotificationInfo.getMsgAddresses(),
			"vibe-emailNotifDlg_MsgList");
		
		// ...create the widgets for the text messaging selection...
		m_textList = buildEMAListWidgets(
			"vibe-emailNotifDlg_TextPanel",
			m_messages.mainMenuEmailNotificationDlgTextMessaging(),
			"vibe-emailNotifDlg_Text",
			m_emailNotificationInfo.getTextAddresses(),
			"vibe-emailNotifDlg_TextList" + (isBinderSubscription() ? "" : " vibe-emailNotifDlg_TextListBottom"));

		// ...finally, if we're in binder email notification mode...
		if (isBinderSubscription()) {
			// ...create the widgets for the override presets checkbox
			// ...label.
			fp = new FlowPanel();
			fp.addStyleName("vibe-emailNotifDlg_FooterPanel");
			m_footerCB = new CheckBox();
			m_footerCB.addStyleName("vibe-emailNotifDlg_FooterCheck");
			m_footerCB.setValue(m_emailNotificationInfo.getOverridePresets());
			fp.add(m_footerCB);
			il = new InlineLabel(m_messages.mainMenuEmailNotificationDlgOverride());
			il.addStyleName("vibe-emailNotifDlg_FooterLabel");
			il.setWordWrap(false);
			fp.add(il);
			final String overrideHelpUrl = m_emailNotificationInfo.getOverrideHelpUrl();
			if (GwtClientHelper.hasString(overrideHelpUrl)) {
				Image img = GwtClientHelper.buildImage(m_menuImages.help());
				img.addStyleName("vibe-emailNotifDlg_FooterHelp");
				img.setTitle(m_messages.mainMenuEmailNotificationDlgAltHelpOverride());
				img.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Window.open(overrideHelpUrl, "teaming_help_window", "resizeable,scrollbar");
					}
				});
				fp.add(img);
			}
			m_vp.add(fp);
		}
		
		// Show the dialog so that it can be positioned correctly based
		// on its new content.
		if (null == m_showRelativeTo)
		     show(                true            );
		else showRelativeToTarget(m_showRelativeTo);
	}
	
	/*
	 * Asynchronously runs the given instance of the email notification
	 * dialog.
	 */
	private static void runDlgAsync(final EmailNotificationDlg enDlg, final BinderInfo bi, final List<EntityId> entityIds, final UIObject showRelativeTo) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				enDlg.runDlgNow(bi, entityIds, showRelativeTo);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the email notification
	 * dialog.
	 */
	private void runDlgNow(BinderInfo bi, List<EntityId> entityIds, UIObject showRelativeTo) {
		// Store the parameters...
		EntityId singleEID = (((null != entityIds) && (1 == entityIds.size())) ? entityIds.get(0) : null);
		if ((null != singleEID) && singleEID.isBinder()) {
			m_binderId = singleEID.getEntityId();
			entityIds  = null;
		}
		else {
			m_binderId = ((null == bi) ? null : bi.getBinderIdAsLong());
		}
		m_entityIds      = entityIds;
		m_showRelativeTo = showRelativeTo;

		// ...and display a reading message, start populating the
		// ...dialog and show it.
		displayReading();
		populateDlgAsync();
	}
	
	/*
	 * Asynchronously saves the contents of the dialog.
	 */
	private void saveEmailNotificationInfoAsync() {
		ScheduledCommand doSave = new ScheduledCommand() {
			@Override
			public void execute() {
				saveEmailNotificationInfoNow();
			}
		};
		Scheduler.get().scheduleDeferred(doSave);
	}
	
	/*
	 * Synchronously saves the contents of the dialog.
	 */
	private void saveEmailNotificationInfoNow() {
		// Create a save command with the contents of the dialog.
		SaveEmailNotificationInfoCmd seniCmd;
		if (isBinderSubscription())
		     seniCmd = new SaveEmailNotificationInfoCmd(m_binderId );
		else seniCmd = new SaveEmailNotificationInfoCmd(m_entityIds);
		if (!(getSelectedEMAs(seniCmd.getMsgAddressTypes(),      m_msgList     ))) return;
		if (!(getSelectedEMAs(seniCmd.getMsgNoAttAddressTypes(), m_msgNoAttList))) return;
		if (!(getSelectedEMAs(seniCmd.getTextAddressTypes(),     m_textList    ))) return;
		if (isBinderSubscription()) {
			seniCmd.setOverridePresets(m_footerCB.getValue());
			if (!(getSelectedEMAs(seniCmd.getDigestAddressTypes(), m_digestList))) return;
		}

		// Can we perform the save?
		GwtClientHelper.executeCommand(seniCmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SaveEmailNotificationInfo());
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Yes, the save was successful.  Simply close the
				// dialog.
				hide();
			}
		});
	}

	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the email notification dialog and perform some operation on   */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	
	/**
	 * Callback interface to interact with the email notification
	 * dialog asynchronously after it loads. 
	 */
	public interface EmailNotificationDlgClient {
		void onSuccess(EmailNotificationDlg enDlg);
		void onUnavailable();
	}

	/*
	 * Asynchronously loads the EmailNotificationDlg and performs some
	 * operation against the code.
	 */
	private static void doAsyncOperation(
			// Required creation parameters.
			final EmailNotificationDlgClient enDlgClient,
			
			// initAndShow parameters,
			final EmailNotificationDlg	enDlg,
			final BinderInfo			bi,
			final List<EntityId>		entityIds,
			final UIObject				showRelativeTo) {
		GWT.runAsync(EmailNotificationDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(m_messages.codeSplitFailure_EmailNotificationDlg());
				if (null != enDlgClient) {
					enDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				// Is this a request to create a dialog?
				if (null != enDlgClient) {
					// Yes!  Create it and return it via the callback.
					EmailNotificationDlg enDlg = new EmailNotificationDlg();
					enDlgClient.onSuccess(enDlg);
				}
				
				else {
					// No, it's not a request to create a dialog!  It
					// must be a request to run an existing one.  Run
					// it.
					runDlgAsync(enDlg, bi, entityIds, showRelativeTo);
				}
			}
		});
	}
	
	/**
	 * Loads the EmailNotificationDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param enDlgClient
	 */
	public static void createAsync(EmailNotificationDlgClient enDlgClient) {
		doAsyncOperation(enDlgClient, null, null, null, null);
	}
	
	/*
	 * Initializes and shows the email notification dialog.
	 */
	private static void initAndShowImpl(EmailNotificationDlg enDlg, BinderInfo bi, List<EntityId> entityIds, UIObject showRelativeTo) {
		doAsyncOperation(null, enDlg, bi, entityIds, showRelativeTo);
	}
	
	/**
	 * Initializes and shows the email notification dialog.
	 * 
	 * @param enDlg
	 * @param bi
	 */
	public static void initAndShow(EmailNotificationDlg enDlg, BinderInfo bi, UIObject showRelativeTo) {
		// Always use the implementation form of the method.
		initAndShowImpl(enDlg, bi, ((List<EntityId>) null), showRelativeTo);
	}
	
	public static void initAndShow(EmailNotificationDlg enDlg, BinderInfo bi) {
		// Always use the previous form of the method.
		initAndShow(enDlg, bi, null);
	}
	
	/**
	 * Initializes and shows the email notification dialog for entity
	 * subscriptions.
	 * 
	 * @param enDlg
	 * @param entityIds
	 */
	public static void initAndShow(EmailNotificationDlg enDlg, List<EntityId> entityIds, UIObject showRelativeTo) {
		// Always use the implementation form of the method.
		initAndShowImpl(enDlg, null, entityIds, showRelativeTo);
	}
	
	public static void initAndShow(EmailNotificationDlg enDlg, List<EntityId> entityIds) {
		// Always use the previous form of the method.
		initAndShow(enDlg, entityIds, null);
	}
	
	/**
	 * Initializes and shows the email notification dialog for entity
	 * subscriptions.
	 * 
	 * @param enDlg
	 * @param entityId
	 */
	public static void initAndShow(EmailNotificationDlg enDlg, EntityId entityId, UIObject showRelativeTo) {
		// Always use the implementation form of the method.
		List<EntityId> entityIds = new ArrayList<EntityId>();
		entityIds.add(entityId);
		initAndShowImpl(enDlg, null, entityIds, showRelativeTo);
	}
	
	public static void initAndShow(EmailNotificationDlg enDlg, EntityId entityId) {
		// Always use the previous form of the method.
		initAndShow(enDlg, entityId, null);
	}
}
