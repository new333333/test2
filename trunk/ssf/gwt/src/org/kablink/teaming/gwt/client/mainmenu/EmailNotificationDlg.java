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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
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
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements Vibe's email notification dialog.
 *  
 * @author drfoster@novell.com
 */
public class EmailNotificationDlg extends DlgBox implements EditSuccessfulHandler {
	private CheckBox								m_footerCB;					//
	private EmailNotificationInfoRpcResponseData	m_emailNotificationInfo;	//
	private GwtTeamingMainMenuImageBundle			m_images;					// Access to Vibe's images.
	private GwtTeamingMessages						m_messages;					// Access to Vibe's messages.
	private Long									m_binderId;					//
	private List<EntityId>							m_entityIds;				// List<EntityId> of entity subscriptions.  null -> Binder email notification mode.
	private ListBox									m_digestList;				//
	private ListBox									m_msgList;					//
	private ListBox									m_msgNoAttList;				//
	private ListBox									m_textList;					//
	private VerticalPanel							m_vp;						//

	// The following are used as the values for the non-email address
	// values put in to the email address list box.
	private final static String EMA_VALUE_CLEAR_SUBSCRIPTION	= "";
	private final static String EMA_VALUE_MAKE_SELECTION		= "";
	private final static String EMA_VALUE_NO_CHANGES			= "*no-change*";
	
	/*
	 * Inner class that wraps items displayed in the dialog's content.
	 */
	private class DlgLabel extends InlineLabel {
		/**
		 * Constructor method.
		 * 
		 * @param label
		 * @param title
		 */
		public DlgLabel(String label, String title) {
			super(label);
			if (GwtClientHelper.hasString(title)) {
				setTitle(title);
			}
			addStyleName("vibe-emailNotifDlg_Label");
		}
		
		/**
		 * Constructor method.
		 * 
		 * @param label
		 */
		public DlgLabel(String label) {
			// Always use the initial form of the method.
			this(label, null);
		}
	}

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

		// ...initialize everything else...
		m_images   = GwtTeaming.getMainMenuImageBundle();
		m_messages = GwtTeaming.getMessages();
	
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
		VerticalPanel vp = new VerticalPanel();
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
		return new Image(m_images.spinner());
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
	 * Clears the contents of the dialog and displays a message that
	 * we're reading the contents of the current email notification
	 * settings.
	 */
	private void displayReading() {
		m_vp.clear();
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("vibe-emailNotifDlg_ReadingPanel");
		fp.add(buildSpinnerImage());
		DlgLabel l = new DlgLabel(m_messages.mainMenuEmailNotificationDlgReading());
		l.addStyleName("vibe-emailNotifDlg_ReadingLabel");
		fp.add(l);
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
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("vibe-emailNotifDlg_BannerPanel");
		InlineLabel il = new InlineLabel(m_messages.mainMenuEmailNotificationDlgBanner());
		il.addStyleName("vibe-emailNotifDlg_BannerLabel");
		il.setWordWrap(false);
		fp.add(il);
		final String bannerHelpUrl = m_emailNotificationInfo.getBannerHelpUrl();
		if (GwtClientHelper.hasString(bannerHelpUrl)) {
			Image img = new Image(m_images.help());
			img.addStyleName("vibe-emailNotifDlg_BannerHelp");
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
		
		// ...create the widgets for the individual messages
		// ...selection...
		m_msgList = buildEMAListWidgets(
			"vibe-emailNotifDlg_MsgPanel",
			m_messages.mainMenuEmailNotificationDlgIndividualMessages(),
			"vibe-emailNotifDlg_Msg",
			m_emailNotificationInfo.getMsgAddresses(),
			"vibe-emailNotifDlg_MsgList");
		
		// ...create the widgets for the individual messages without
		// ...attachments selection...
		m_msgNoAttList = buildEMAListWidgets(
			"vibe-emailNotifDlg_MsgNoAttPanel",
			m_messages.mainMenuEmailNotificationDlgIndividualMessagesNoAttachments(),
			"vibe-emailNotifDlg_MsgNoAtt",
			m_emailNotificationInfo.getMsgNoAttAddresses(),
			"vibe-emailNotifDlg_MsgNoAttList");
		
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
				Image img = new Image(m_images.help());
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
		
		// Show the dialog (perhaps again) so that it can be positioned
		// correctly based on its new content.
		show(true);
	}
	
	/*
	 * Asynchronously runs the given instance of the email notification
	 * dialog.
	 */
	private static void runDlgAsync(final EmailNotificationDlg enDlg, final BinderInfo bi, final List<EntityId> entityIds) {
		ScheduledCommand doRun = new ScheduledCommand() {
			@Override
			public void execute() {
				enDlg.runDlgNow(bi, entityIds);
			}
		};
		Scheduler.get().scheduleDeferred(doRun);
	}
	
	/*
	 * Synchronously runs the given instance of the email notification
	 * dialog.
	 */
	private void runDlgNow(BinderInfo bi, List<EntityId> entityIds) {
		// Store the parameters...
		EntityId singleEID = (((null != entityIds) && (1 == entityIds.size())) ? entityIds.get(0) : null);
		if ((null != singleEID) && singleEID.isBinder()) {
			m_binderId = singleEID.getEntityId();
			entityIds  = null;
		}
		else {
			m_binderId = ((null == bi) ? null : bi.getBinderIdAsLong());
		}
		m_entityIds = entityIds;

		// ...and display a reading message, start populating the
		// ...dialog and show it.
		displayReading();
		populateDlgAsync();
		show(true);
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
			final List<EntityId>		entityIds) {
		GWT.runAsync(EmailNotificationDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				Window.alert(GwtTeaming.getMessages().codeSplitFailure_EmailNotificationDlg());
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
					runDlgAsync(enDlg, bi, entityIds);
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
		doAsyncOperation(enDlgClient, null, null, null);
	}
	
	/*
	 * Initializes and shows the email notification dialog.
	 */
	private static void initAndShowImpl(EmailNotificationDlg enDlg, BinderInfo bi, List<EntityId> entityIds) {
		doAsyncOperation(null, enDlg, bi, entityIds);
	}
	
	/**
	 * Initializes and shows the email notification dialog.
	 * 
	 * @param enDlg
	 * @param bi
	 */
	public static void initAndShow(EmailNotificationDlg enDlg, BinderInfo bi) {
		// Always use the implementation form of the method.
		initAndShowImpl(enDlg, bi, ((List<EntityId>) null));
	}
	
	/**
	 * Initializes and shows the email notification dialog for entity
	 * subscriptions.
	 * 
	 * @param enDlg
	 * @param entityIds
	 */
	public static void initAndShow(EmailNotificationDlg enDlg, List<EntityId> entityIds) {
		// Always use the implementation form of the method.
		initAndShowImpl(enDlg, null, entityIds);
	}
	
	/**
	 * Initializes and shows the email notification dialog for entity
	 * subscriptions.
	 * 
	 * @param enDlg
	 * @param entityId
	 */
	public static void initAndShow(EmailNotificationDlg enDlg, EntityId entityId) {
		// Always use the implementation form of the method.
		List<EntityId> entityIds = new ArrayList<EntityId>();
		entityIds.add(entityId);
		initAndShowImpl(enDlg, null, entityIds);
	}
	
}
