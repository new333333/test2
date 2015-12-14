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
import org.kablink.teaming.gwt.client.widgets.VibeFlexTable;
import org.kablink.teaming.gwt.client.widgets.VibeHorizontalPanel;
import org.kablink.teaming.gwt.client.widgets.VibeVerticalPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Implements Vibe's email notification (i.e., Subscribe) dialog.
 *  
 * @author drfoster@novell.com
 */
public class EmailNotificationDlg extends DlgBox implements EditSuccessfulHandler {
	private CheckBox								m_footerCB;					// The override checkbox at the bottom of the dialog when run against a single binder.
	private EmailNotificationInfoRpcResponseData	m_emailNotificationInfo;	// The email notification information once it's read from the server.
	private Long									m_binderId;					// The ID of a binder we're running against.
	private List<EntityId>							m_entityIds;				// List<EntityId> of entity subscriptions.  null -> Binder email notification mode.
	private UIObject								m_showRelativeTo;			// The UIObject to popup the dialog relative to.  null -> Center the dialog.
	private VerticalPanel							m_vp;						// The vertical panel containing the dialog's content.

	protected final static GwtTeamingFilrImageBundle		m_filrImages = GwtTeaming.getFilrImageBundle();		// Access to Filr's      image resources.
	protected final static GwtTeamingImageBundle			m_images     = GwtTeaming.getImageBundle();			// Access to Vibe's      image resources.
	protected final static GwtTeamingMainMenuImageBundle	m_menuImages = GwtTeaming.getMainMenuImageBundle();	// Access to Vibe's menu image resources.
	protected final static GwtTeamingMessages				m_messages   = GwtTeaming.getMessages();			// Access to Vibe's localized string resources.

	// The following are use as the base of the IDs generated for the
	// widgets in each section of the dialog.
	private final static String EMA_DIGEST_BASE			= "digest_";
	private final static String EMA_MESSAGE_BASE		= "message_";
	private final static String EMA_MESSAGE_ONLY_BASE	= "messageOnly_";
	private final static String EMA_TEXT_BASE			= "text_";
	
	// The following are used as the tail part of the IDs generated for
	// the built-in checkbox generated for the multiple selection
	// version of the dialog.
	private final static String EMA_CLEAR_TAIL			= "clear";
	private final static String EMA_NO_CHANGES_TAIL		= "*no-change*";
	
	// The following is used to store type type from an email address
	// on its checkbox within any given panel.
	private final static String EMA_TYPE_ATTR			= "n-emaType";
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EmailNotificationDlg() {
		// Initialize the super class...
		super(false, true, DlgButtonMode.OkCancel);

		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.mainMenuEmailNotificationDlgHeader(),
			this,						// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),	// The dialog's EditCanceledHandler.
			null);						// Create callback data.  (Unused.) 
	}

	/*
	 * Builds the widgets for an email address selection group.
	 */
	private void buildEMAListWidgets(
		final String		panelBaseId,	// The ID to use the as the base of the ID generated for the section.
		final String		panelLabel,		// The label above the widgets.
		final List<String>	emaList,		// The style to use for the email address ListBox.
		final boolean		lastWidget)		// true -> This is the last widget on the dialog.  false -> It's not.
	{
		// Create a grid to hold the email address list.
		FlexTable grid = new VibeFlexTable();
		grid.addStyleName("vibe-emailNotifDlg_EMARootPanel");
		m_vp.add(grid);
		
		// Create a vertical panel to hold this message type section.
		final VerticalPanel vp = new VibeVerticalPanel(null, null);
		vp.addStyleName("vibe-emailNotifDlg_EMASectionPanel");
		vp.setVisible(false);
		grid.setWidget(1, 1, vp);

		// Create the expander image to hide/show the section.
		final Image expander = GwtClientHelper.buildImage(m_images.expander());
		expander.addStyleName("vibe-emailNotifDlg_ExpanderImg");
		expander.setTitle(m_messages.mainMenuEmailNotificationDlgAltListExpand());
		expander.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setListExpansion(vp, expander, (!(vp.isVisible())));
			}
		});
		grid.setWidget(0, 0, expander);

		// Create the label for the section.
		InlineLabel il = new InlineLabel(panelLabel);
		il.addStyleName("vibe-emailNotifDlg_SectionLabel");
		il.setWordWrap(false);
		grid.setWidget(0, 1, il);

		// Scan the email addresses...
    	HorizontalPanel emaHP;
    	final List<EmailAddressInfo> emaInfoList = m_emailNotificationInfo.getEmailAddresses();
    	int checkCount = 0;
	    for (EmailAddressInfo emi:  emaInfoList) {
	    	// ...creating a horizontal panel for each...
	    	emaHP = new VibeHorizontalPanel(null, null);
	    	emaHP.addStyleName("vibe-emailNotifDlg_EMAPanel");
	    	vp.add(emaHP);

	    	// ...that contains an selection checkbox...
	    	String ema = emi.getAddress();
			final CheckBox emaCB = new CheckBox();
	    	emaCB.addStyleName("vibe-emailNotifDlg_EMACBox");
	    	Element emaCBE = emaCB.getElement();
	    	emaCBE.setId(panelBaseId + ema);
	    	emaCBE.setAttribute(EMA_TYPE_ATTR, emi.getType());
	    	if (emaList.contains(ema)) {
	    		emaCB.setValue(true);
	    		checkCount += 1;
	    	}
	    	emaCB.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					clearEMAChecks(panelBaseId, null, EMA_CLEAR_TAIL, EMA_NO_CHANGES_TAIL);
				}
	    	});
	    	emaHP.add(emaCB);

	    	// ...and the email address.
	    	InlineLabel emaIL = new InlineLabel(ema);
	    	emaIL.addStyleName("vibe-emailNotifDlg_EMALabel");
	    	emaHP.add(emaIL);
	    }

	    // Are we handling subscription for multiple items? 
	    if (isMultipleEntitySubscriptions()) {
	    	{
	    		// Yes!  Create a horizontal panel for clearing all the
	    		// subscriptions...
		    	emaHP = new VibeHorizontalPanel(null, null);
		    	emaHP.addStyleName("vibe-emailNotifDlg_EMAPanel");
		    	vp.add(emaHP);
		    	
		    	// ...create its checkbox...
				final CheckBox emaCB = new CheckBox();
		    	emaCB.addStyleName("vibe-emailNotifDlg_EMACBox");
		    	emaCB.getElement().setId(panelBaseId + EMA_CLEAR_TAIL);
		    	emaCB.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (emaCB.getValue()) {
							clearEMAChecks(panelBaseId, emaInfoList, EMA_NO_CHANGES_TAIL);
						}
					}
		    	});
		    	emaHP.add(emaCB);

		    	// ...and its label.
		    	InlineLabel emaIL = new InlineLabel(m_messages.mainMenuEmailNotificationDlgClearEntrySubscription());
		    	emaIL.addStyleName("vibe-emailNotifDlg_EMALabel");
		    	emaHP.add(emaIL);
	    	}
	    	
	    	{
	    		// Create a horizontal panel for not changing any of
	    		// the selections...
		    	emaHP = new VibeHorizontalPanel(null, null);
		    	emaHP.addStyleName("vibe-emailNotifDlg_EMAPanel");
		    	vp.add(emaHP);
		    	
		    	// ...create its checkbox...
				final CheckBox emaCB = new CheckBox();
		    	emaCB.addStyleName("vibe-emailNotifDlg_EMACBox");
		    	emaCB.getElement().setId(panelBaseId + EMA_NO_CHANGES_TAIL);
		    	if (0 == checkCount) {
		    		emaCB.setValue(true);
		    		checkCount += 1;
		    	}
		    	emaCB.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if (emaCB.getValue()) {
							clearEMAChecks(panelBaseId, emaInfoList, EMA_CLEAR_TAIL);
						}
					}
		    	});
		    	emaHP.add(emaCB);
		    	
		    	// ...and its label.
		    	InlineLabel emaIL = new InlineLabel(m_messages.mainMenuEmailNotificationDlgNoChanges());
		    	emaIL.addStyleName("vibe-emailNotifDlg_EMALabel");
		    	emaHP.add(emaIL);
	    	}
	    }

	    // If we checked anything in the list...
	    if (0 < checkCount) {
	    	// ...we default the list to being expanded.
	    	setListExpansion(vp, expander, true);
	    }

	    // If this is the last widget in the dialog...
	    if (lastWidget) {
	    	// ...add the appropriate bottom spacing style.
	    	grid.addStyleName("vibe-emailNotifDlg_LastWidget");
	    }
	}
	
	/*
	 * Constructs and returns an Image with a spinner in it.
	 */
	private Image buildSpinnerImage() {
		return GwtClientHelper.buildImage(m_menuImages.spinner());
	}

	/*
	 * Given its ID, clears a checkbox.
	 */
	private void clearCheck(String id) {
		Element emaCBE = DOM.getElementById(id);
		if (null != emaCBE) {
			InputElement emaCBIE = emaCBE.getFirstChild().cast();
			emaCBIE.setChecked(false);
		}
	}
	
	/*
	 * Clears the checks corresponding to the email addresses in the
	 * List<EmailAddressInfo>.
	 */
	private void clearEMAChecks(String panelBaseId, List<EmailAddressInfo> emaInfoList, String ... others) {
		// If we have any email addresses...
		if (GwtClientHelper.hasItems(emaInfoList)) {
			// ...scan them...
			for (EmailAddressInfo emi:  emaInfoList) {
				// ...and clear their checkboxes.
				clearCheck(panelBaseId + emi.getAddress());
			}
		}

		// If we have any other checkboxes that need clearing...
		if ((null != others) && (0 < others.length)) {
			// ...scan them...
			for (String other:  others) {
				// ...and clear them too.
				clearCheck(panelBaseId + other);
			}
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
		m_vp = new VerticalPanel();
		m_vp.addStyleName("vibe-emailNotifDlg_Content");
		return m_vp;
	}

	/*
	 * Creates the selection description widget(s) and returns the
	 * outermost containing Widget.
	 */
	private Widget createSelectionDesc() {
		// Create a horizontal panel to hold the selection description.
		HorizontalPanel hp = new VibeHorizontalPanel(null, null);
		hp.addStyleName("vibe-emailNotifDlg_DescPanel");

		// Add an image for the selection(s) to the left side of the
		// description panel.
		String imageUrl;
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
		il.addStyleName("vibe-emailNotifDlg_ReadingLabel");
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
		setOkEnabled(false);
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
	private void getSelectedEMAs(List<String> emaTypeList, String emaIdBase) {
		// Does the user have any email addresses?
		List<EmailAddressInfo> emaInfoList = m_emailNotificationInfo.getEmailAddresses();
		if (GwtClientHelper.hasItems(emaInfoList)) {
			// Yes!  Scan them.
			for (EmailAddressInfo emi:  emaInfoList) {
				// Is the checkbox for this email address checked?
				String ema = emi.getAddress();
				String emaID = (emaIdBase + ema);
				if (isCBChecked(emaID)) {
					// Yes!  Add it to the type list.
					emaTypeList.add(DOM.getElementById(emaID).getAttribute(EMA_TYPE_ATTR));
				}
			}
		}

		// Did the user not check any email addresses for the multiple
		// selection version of this dialog?
		if (emaTypeList.isEmpty() && isMultipleEntitySubscriptions()) {
			// Yes!  If the checked 'No Changes' (or nothing at all)...
			if (isCBChecked(emaIdBase + EMA_NO_CHANGES_TAIL) || (!(isCBChecked(emaIdBase + EMA_CLEAR_TAIL)))) {
				// ...we need to tell the server not to change anything.
				emaTypeList.add(EMA_NO_CHANGES_TAIL);
			}
		}
	}

	/*
	 * Returns false if the dialog is running in entity subscription
	 * mode or true if it's running in binder email notification mode.
	 */
	private boolean isBinderSubscription() {
		return (null == m_entityIds);
	}
	
	/*
	 * Returns true if a checkbox is checked and false otherwise. 
	 */
	private boolean isCBChecked(String id) {
		InputElement emaCBIE = DOM.getElementById(id).getFirstChild().cast();
		return emaCBIE.isChecked();
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateDlgNow();
			}
		});
	}
	
    /**
     * Called after the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingEnded() method.
     */
	@Override
    protected void okBtnProcessingEnded() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
    }
    
    /**
     * Called before the EditSuccessfulHandler has been called by
     * DlgBox.
     * 
     * Overrides the DlgBox.okBtnProcessingStarted() method.
     */
	@Override
    protected void okBtnProcessingStarted() {
		// Ignored!  This dialog is handling enabling and disabling of
		// the OK button itself.
    }
    
	/*
	 * Synchronously populates the contents of the dialog.
	 */
	private void populateDlgNow() {
		GetEmailNotificationInfoCmd geniCmd;
		if      (isBinderSubscription())       geniCmd = new GetEmailNotificationInfoCmd(new EntityId(m_binderId, EntityId.FOLDER));
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				populateFromEmailNotificationInfoNow();
			}
		});
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
			GwtClientHelper.isLicenseFilr()                     ?
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
			buildEMAListWidgets(
				EMA_DIGEST_BASE,
				m_messages.mainMenuEmailNotificationDlgDigest(),
				m_emailNotificationInfo.getDigestAddresses(),
				false);	// Not the list widget in the dialog.
		}
		
		// ...create the widgets for the individual messages without
		// ...attachments selection...
		buildEMAListWidgets(
			EMA_MESSAGE_ONLY_BASE,
			m_messages.mainMenuEmailNotificationDlgIndividualMessagesNoAttachments(),
			m_emailNotificationInfo.getMsgNoAttAddresses(),
			false);	// Not the list widget in the dialog.
		
		// ...create the widgets for the individual messages
		// ...selection...
		buildEMAListWidgets(
			EMA_MESSAGE_BASE,
			m_messages.mainMenuEmailNotificationDlgIndividualMessages(),
			m_emailNotificationInfo.getMsgAddresses(),
			false);	// Not the list widget in the dialog.
		
		// ...create the widgets for the text messaging selection...
		buildEMAListWidgets(
			EMA_TEXT_BASE,
			m_messages.mainMenuEmailNotificationDlgTextMessaging(),
			m_emailNotificationInfo.getTextAddresses(),
			(!(isBinderSubscription())));

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
		setCancelEnabled(true);
		setOkEnabled(    true);
		if (null == m_showRelativeTo)
		     show(                true            );
		else showRelativeToTarget(m_showRelativeTo);
	}
	
	/*
	 * Asynchronously runs the given instance of the email notification
	 * dialog.
	 */
	private static void runDlgAsync(final EmailNotificationDlg enDlg, final BinderInfo bi, final List<EntityId> entityIds, final UIObject showRelativeTo) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				enDlg.runDlgNow(bi, entityIds, showRelativeTo);
			}
		});
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
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				saveEmailNotificationInfoNow();
			}
		});
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
		getSelectedEMAs(seniCmd.getMsgAddressTypes(),      EMA_MESSAGE_BASE     );
		getSelectedEMAs(seniCmd.getMsgNoAttAddressTypes(), EMA_MESSAGE_ONLY_BASE);
		getSelectedEMAs(seniCmd.getTextAddressTypes(),     EMA_TEXT_BASE        );
		if (isBinderSubscription()) {
			seniCmd.setOverridePresets(m_footerCB.getValue());
			getSelectedEMAs(seniCmd.getDigestAddressTypes(), EMA_DIGEST_BASE);
		}

		// Can we perform the save?
		GwtClientHelper.executeCommand(seniCmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_SaveEmailNotificationInfo());
				setOkEnabled(true);
			}
			
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Yes, the save was successful.  Simply close the
				// dialog.
				setOkEnabled(true);
				hide();
			}
		});
	}

	/*
	 * Expands/collapses the list corresponding to a vertical panel
	 * and its expander image.
	 */
	private void setListExpansion(final VerticalPanel vp, final Image expander, final boolean expand) {
		if (expand) {
			vp.setVisible(true);
			expander.setResource(m_images.collapser());
			expander.setTitle(m_messages.mainMenuEmailNotificationDlgAltListCollapse());
		}
		else {
			vp.setVisible(false);
			expander.setResource(m_images.expander());
			expander.setTitle(m_messages.mainMenuEmailNotificationDlgAltListExpand());
		}
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
		// createAsync() parameters.
		final EmailNotificationDlgClient enDlgClient,
		
		// initAndShow() parameters,
		final EmailNotificationDlg	enDlg,
		final BinderInfo			bi,
		final List<EntityId>		entityIds,
		final UIObject				showRelativeTo)
	{
		GWT.runAsync(EmailNotificationDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(m_messages.codeSplitFailure_EmailNotificationDlg());
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
