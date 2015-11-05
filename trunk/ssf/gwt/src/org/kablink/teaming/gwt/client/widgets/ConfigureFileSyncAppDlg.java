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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtDesktopApplicationsLists;
import org.kablink.teaming.gwt.client.GwtDesktopApplicationsLists.GwtAppInfo;
import org.kablink.teaming.gwt.client.GwtDesktopApplicationsLists.GwtAppListMode;
import org.kablink.teaming.gwt.client.GwtDesktopApplicationsLists.GwtAppPlatform;
import org.kablink.teaming.gwt.client.GwtFileSyncAppConfiguration;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.SaveFileSyncAppConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.MultiPromptDlg.MultiPromptDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * ?
 * 
 * @author drfoster@novell.com
 */
public class ConfigureFileSyncAppDlg extends DlgBox implements KeyPressHandler, EditSuccessfulHandler {
	private boolean				m_isFilr;						//
	private CheckBox			m_allowPwdCacheCB;				//
	private CheckBox			m_enableDeployCB;				//
	private CheckBox			m_enableFileSyncAccessCB;		//
	private FlexTable			m_autoUpdateChoiceTable;		//
	private FlexTable			m_autoUpdateUrlOnlyTable;		//
	private FlowPanel			m_appListPanel;					//
	private GwtTeamingMessages	m_messages;						//
	private MultiPromptDlg		m_mpDlg;						// The dialog used to prompt the user for information about an application.
	private ListBox				m_macLB;						// The list of Mac     applications.
	private ListBox				m_windowsLB;					// The list of Windows applications.
	private RadioButton			m_blacklistRB;					// The radio button specifying the lists are part of a blacklist.
	private RadioButton			m_disabledRB;					// The radio button specifying the lists are to be ignored.
	private RadioButton 		m_useLocalApps;					//
	private RadioButton 		m_useRemoteApps;				//
	private RadioButton			m_whitelistRB;					// The radio button specifying the lists are part of a whitelist.
	private String				m_productName;					//
	private TextBox				m_autoUpdateUrlTextBox;			//
	private TextBox				m_autoUpdateUrlTextBox_Choice;	//
	private TextBox				m_autoUpdateUrlTextBox_UrlOnly;	//
	private TextBox				m_maxFileSizeTextBox;			//
	private TextBox				m_syncIntervalTextBox;			//
	
	private final static int	DESCRIPTION_INDEX	= 1;
	private final static int	PROCESS_NAME_INDEX	= 0;
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ConfigureFileSyncAppDlg(boolean autoHide, boolean modal, int xPos, int yPos, int width, int height, ConfigureFileSyncAppDlgClient cfsaDlgClient) {
		// Initialize the super class...
		super(
			autoHide,
			modal,
			xPos,
			yPos,
			width,
			height,
			DlgButtonMode.OkCancel);
		
		// ...initialize everything else that requires it...
		m_isFilr      = GwtClientHelper.isLicenseFilr();
		m_messages    = GwtTeaming.getMessages();
		m_productName = GwtClientHelper.getProductName();

		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.fileSyncAppDlgHeader(),
			this,	// EditSuccessful handler.
			null,	// null -> No EditCanceled handler.
			cfsaDlgClient); 
	}

	/*
	 * Constructs the string to use as the display value in an
	 * application whitelist / blacklist listbox.
	 */
	private static String buildAppListDisplayValue(String description, String processName) {
		return (processName + " (" + description + ")");
	}
	
	/*
	 * Creates the dialog content for the application
	 * whitelist / blacklist.
	 */
	private void createAppListContent(FlowPanel mainPanel) {
		// Create a panel for the application whitelist / blacklist.
		m_appListPanel = new FlowPanel();
		mainPanel.add(m_appListPanel);

		// Create a header for this section of the dialog.
		Label label = new Label(m_messages.fileSyncAppHeader4());
		label.addStyleName("margintop3");
		m_appListPanel.add(label);

		// Add the widgets for the lists.
		m_appListPanel.add(createListHint()   );
		m_appListPanel.add(createModeWidgets());
		m_appListPanel.add(createWindowsList());
		m_appListPanel.add(createMacList()    );
		
		// If this isn't Filr...
		if (!(m_isFilr)) {
			// ...hide the application list panel.  It's only used in Filr.
			m_appListPanel.setVisible(false);
		}
	}
	
	/**
	 * Create all the controls that make up the dialog box.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param props
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object props) {
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("teamingDlgBoxContent");

		Label label = new Label(m_messages.fileSyncAppHeader2());
		mainPanel.add(label);
		
		FlowPanel ckboxPanel = new FlowPanel();
		ckboxPanel.addStyleName("marginleft1 margintop2");
		mainPanel.add(ckboxPanel);
		
		// Add the controls for enable/disable File Sync Application.
		m_enableFileSyncAccessCB = new CheckBox(m_messages.fileSyncAppAllowAccess(m_productName));
		FlowPanel tmpPanel = new FlowPanel();
		tmpPanel.add(m_enableFileSyncAccessCB);
		ckboxPanel.add(tmpPanel);
		
		// Create the "Allow desktop application to cache password"
		m_allowPwdCacheCB = new CheckBox(m_messages.fileSyncAppAllowCachePwd());
		tmpPanel = new FlowPanel();
		tmpPanel.add(m_allowPwdCacheCB);
		ckboxPanel.add(tmpPanel);
		
		// Create the "Allow deployment of Desktop application" checkbox
		m_enableDeployCB = new CheckBox(m_messages.fileSyncAppEnableDeployLabel());
		tmpPanel = new FlowPanel();
		tmpPanel.add(m_enableDeployCB);
		ckboxPanel.add(tmpPanel);
		
		// Create the controls for auto-update URL.
		m_autoUpdateChoiceTable = new FlexTable();
		m_autoUpdateChoiceTable.addStyleName("marginleft1");
		m_autoUpdateChoiceTable.setCellSpacing(0);
		m_autoUpdateChoiceTable.setCellPadding(0);

		m_useLocalApps = new RadioButton("fileSyncAppDlg_Location");
		m_useLocalApps.addStyleName(     "fileSyncAppDlg_Radio"   );
		m_useLocalApps.setValue(true);
		m_autoUpdateChoiceTable.setWidget(0, 0, m_useLocalApps);
		label = new InlineLabel(m_messages.fileSyncAppAutoUpdateUrlLabel_UseLocal());
		label.addStyleName("gwtUI_nowrap");
		m_autoUpdateChoiceTable.setWidget(0, 1, label);

		m_useRemoteApps = new RadioButton("fileSyncAppDlg_Location");
		m_useRemoteApps.addStyleName(     "fileSyncAppDlg_Radio"   );
		m_useRemoteApps.setValue(false);
		m_autoUpdateChoiceTable.setWidget(1, 0, m_useRemoteApps);
		label = new InlineLabel(m_messages.fileSyncAppAutoUpdateUrlLabel_UseRemote());
		label.addStyleName("gwtUI_nowrap");
		m_autoUpdateChoiceTable.setWidget(1, 1, label);
		
		// Create a text box for the user to enter the auto-update URL.
		m_autoUpdateUrlTextBox_Choice = new TextBox();
		m_autoUpdateUrlTextBox_Choice.setVisibleLength(40);
		m_autoUpdateChoiceTable.setWidget(2, 1, m_autoUpdateUrlTextBox_Choice);
		
		mainPanel.add(m_autoUpdateChoiceTable);
		
		m_autoUpdateUrlOnlyTable = new FlexTable();
		m_autoUpdateUrlOnlyTable.addStyleName("marginleft1");
		m_autoUpdateUrlOnlyTable.setCellSpacing(4);
		
		label = new InlineLabel(m_messages.fileSyncAppAutoUpdateUrlLabel());
		label.addStyleName("gwtUI_nowrap");
		m_autoUpdateUrlOnlyTable.setWidget(0, 0, label);
		
		// Create a text box for the user to enter the auto-update URL.
		m_autoUpdateUrlTextBox_UrlOnly = new TextBox();
		m_autoUpdateUrlTextBox_UrlOnly.setVisibleLength(40);
		m_autoUpdateUrlOnlyTable.setWidget(0, 1, m_autoUpdateUrlTextBox_UrlOnly);
		
		mainPanel.add(m_autoUpdateUrlOnlyTable);
		
		// Create the controls for File Sync interval
		label = new Label(m_messages.fileSyncAppHeader3());
		label.addStyleName("margintop3");
		mainPanel.add(label);

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.addStyleName("marginleft1");
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(4);
		
		Label intervalLabel = new Label(m_messages.fileSyncAppIntervalLabel());
		hPanel.add(intervalLabel);
		
		m_syncIntervalTextBox = new TextBox();
		m_syncIntervalTextBox.addKeyPressHandler(this);
		m_syncIntervalTextBox.setVisibleLength(3);
		hPanel.add(m_syncIntervalTextBox);
		
		intervalLabel = new Label(m_messages.fileSyncAppMinutesLabel());
		intervalLabel.addStyleName("gray3");
		hPanel.add(intervalLabel);

		mainPanel.add(hPanel);
		
		// Create the controls for the max file size
		FlexTable tmpTable = new FlexTable();
		tmpTable.addStyleName("marginleft1");
		tmpTable.setCellSpacing(4);
		
		label = new InlineLabel(m_messages.fileSyncAppMaxFileSizeLabel());
		tmpTable.setWidget(0, 0, label);
		
		m_maxFileSizeTextBox = new TextBox();
		m_maxFileSizeTextBox.addKeyPressHandler(this);
		m_maxFileSizeTextBox.setVisibleLength(3);
		tmpTable.setWidget(0, 1, m_maxFileSizeTextBox);
		
		label = new InlineLabel(m_messages.fileSyncAppMBLabel());
		label.addStyleName("gray3");
		tmpTable.setWidget(0, 2, label);

		mainPanel.add(tmpTable);

		// Create the application whitelist / blacklist widgets.
		createAppListContent(mainPanel);

		// Create a MultipPromptDlg for use in editing the application
		// whitelist / blacklist.
		loadPart1Async(((ConfigureFileSyncAppDlgClient) props));

		// Finally, return the dialog's main panel.
		return mainPanel;
	}
	
	/*
	 * Creates the widgets for entering applications.
	 */
	private ListBox createList(final FlowPanel contentPanel, final String listLabel, final List<String> addPrompts) {
		// Add a label for the list widgets.
		InlineLabel il = new InlineLabel(listLabel);
		il.addStyleName("fileSyncAppDlg_SectionHeader fileSyncAppDlg_ListHeader");
		contentPanel.add(il);

		// Create a HorizontalPanel to hold the list widgets.
		HorizontalPanel horizontalListPanel = new HorizontalPanel();
		horizontalListPanel.addStyleName("fileSyncAppDlg_ListPanel");
		horizontalListPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

		// Create the ListBox itself.
		final ListBox listBox = new ListBox();
		listBox.setMultipleSelect(true);	// true -> Multi-select ListBox.
		listBox.setVisibleItemCount(5);
		listBox.addStyleName("fileSyncAppDlg_List");

		// Create a VerticalPanel to hold buttons for adding to and
		// removing from the list.
		VerticalPanel verticalButtonPanel = new VerticalPanel();
		verticalButtonPanel.addStyleName("fileSyncAppDlg_ListButtons");
		verticalButtonPanel.setVerticalAlignment(  HasVerticalAlignment.ALIGN_TOP   );
		verticalButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		// Create a button for adding to the list.
		Button b = new Button(m_messages.fileSyncApp_Add(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				List<String> addValues = new ArrayList<String>();
				int c = addPrompts.size();
				for (int i = 0; i < c; i += 1) {
					addValues.add("");
				}
				promptForDataAsync(listBox, addPrompts, addValues);
			}
		});
		b.addStyleName("fileSyncAppDlg_ListButton");
		verticalButtonPanel.add(b);

		// Create button for removing from the list.
		b = new Button(m_messages.fileSyncApp_Delete(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						// Scan the list...
						int c = listBox.getItemCount();
						for (int i = (c - 1); i >= 0; i -= 1) {
							if (listBox.isItemSelected(i)) {
								// ...and remove the selected items.
								listBox.removeItem(i);
							}
						}
					}
				});
			}
		});
		b.addStyleName("margintop3pxb fileSyncApp_ListButton");
		verticalButtonPanel.add(b);
		
		// Connect the panels together.
		horizontalListPanel.add(listBox            );
		horizontalListPanel.add(verticalButtonPanel);
		contentPanel.add(       horizontalListPanel);

		// If we get here, reply refers to the ListBox widget we
		// created.  Return it.
		return listBox;
	}
	
	/*
	 * Creates the widgets for hint about the application lists.
	 */
	private Widget createListHint() {
		Label hintLabel = new Label(m_messages.fileSyncAppListHint(m_productName));
		hintLabel.addStyleName("fileSyncAppDlg_ListHintPanel marginleft1");
		return hintLabel;
	}

	/*
	 * Creates the widgets for entering Mac applications.
	 */
	private Widget createMacList() {
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("fileSyncAppDlg_MacListPanel marginleft1");
		
		// Note that the list will be added to the FlowPanel by
		// createList().
		List<String> prompts = new ArrayList<String>();
		prompts.add(m_messages.fileSyncApp_MacApps_AddPrompt());
		prompts.add(m_messages.fileSyncApp_Description()      );
		m_macLB = createList(
			fp,
			m_messages.fileSyncApp_MacApps(),
			prompts);
		
		return fp;
	}

	/*
	 * Creates the widgets used for determining whether the lists are a
	 * whitelist or blacklist.
	 */
	private Widget createModeWidgets() {
		VerticalPanel vt = new VerticalPanel();
		vt.addStyleName("fileSyncAppDlg_ModePanel marginleft1");
		
		InlineLabel il = new InlineLabel(m_messages.fileSyncApp_Mode());
		il.addStyleName("fileSyncAppDlg_SectionHeader fileSyncApp_ModeHeader");
		vt.add(il);
		
		m_disabledRB = new RadioButton("modeGroup", m_messages.fileSyncApp_ModeDisabled());
		m_disabledRB.addStyleName("fileSyncAppDlg_ModeRadio");
		m_disabledRB.removeStyleName("gwt-RadioButton");
		vt.add(m_disabledRB);
		
		m_whitelistRB = new RadioButton("modeGroup", m_messages.fileSyncApp_ModeWhitelist());
		m_whitelistRB.addStyleName("fileSyncAppDlg_ModeRadio");
		m_whitelistRB.removeStyleName("gwt-RadioButton");
		vt.add(m_whitelistRB);
		
		m_blacklistRB = new RadioButton("modeGroup", m_messages.fileSyncApp_ModeBlacklist());
		m_blacklistRB.addStyleName("fileSyncAppDlg_ModeRadio");
		m_blacklistRB.removeStyleName("gwt-RadioButton");
		vt.add(m_blacklistRB);
		
		return vt;
	}

	/*
	 * Creates the widgets for entering Windows applications.
	 */
	private Widget createWindowsList() {
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("fileSyncAppDlg_WindowsListPanel marginleft1");
		
		// Note that the list will be added to the FlowPanel by
		// createList().
		List<String> prompts = new ArrayList<String>();
		prompts.add(m_messages.fileSyncApp_WindowsApps_AddPrompt());
		prompts.add(m_messages.fileSyncApp_Description()          );
		m_windowsLB = createList(
			fp,
			m_messages.fileSyncApp_WindowsApps(),
			prompts);
		
		return fp;
	}

	/**
	 * This method gets called when user user presses OK.
	 * 
	 * Implements the EditSuccessful.editSuccessful() method.
	 * 
	 * @param obj
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object obj) {
		// Issue a GWT RPC request to save the File Sync Applications
		// configuration to the database.  rpcSaveCallback will be
		// called when we get the response back.
		SaveFileSyncAppConfigurationCmd cmd = new SaveFileSyncAppConfigurationCmd(((GwtFileSyncAppConfiguration) obj));
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Get the panel that holds the errors.
				FlowPanel errorPanel = getErrorPanel();
				errorPanel.clear();
				
				String errMsg = null;
				if (caught instanceof GwtTeamingException) {
					GwtTeamingException ex = ((GwtTeamingException) caught);
					if (ex.getExceptionType().equals(ExceptionType.INVALID_AUTO_UPDATE_URL)) {
						errMsg = m_messages.fileSyncApp_InvalidAutoUpdateUrlText();
					}
				}
				
				if (null == errMsg) {
					errMsg = m_messages.fileSyncApp_OnSaveUnknownException(caught.toString());
				}
				
				Label label = new Label(errMsg);
				label.addStyleName("dlgErrorLabel");
				errorPanel.add(label);
				
				showErrorPanel();
				m_autoUpdateUrlTextBox.setFocus(true);
			}
	
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Close this dialog.
				hide();
			}
		});
		
		// Returning false will prevent the dialog from closing.  We
		// will close the dialog after we successfully save the
		// configuration.
		return false;
	}

	/*
	 * Return whether the value entered by the user for allowing the
	 * file sync application to cache passwords
	 */
	private boolean getAllowCachePwd() {
		return m_allowPwdCacheCB.getValue();
	}

	/*
	 * Fills in a List<GwtAppInfo> corresponding to the given ListBox's
	 * contents.
	 */
	private void getAppList(ListBox lb, List<GwtAppInfo> appList) {
		appList.clear();
		int c = lb.getItemCount();
		for (int i = 0; i < c; i += 1) {
			String pn   = lb.getValue(i);
			String desc = lb.getItemText(i);
			
			int pPos = desc.indexOf("(", pn.length());
			desc = desc.substring(pPos + 1);				// Strip off the processName through the '('...
			desc = desc.substring(0, (desc.length() - 1));	// ...and Strip off the trailing ')'.
			
			appList.add(new GwtAppInfo(desc, pn));
		}
	}
	
	/*
	 * Return the string entered by the user for the auto-update URL.
	 */
	private String getAutoUpdateUrl() {
		return m_autoUpdateUrlTextBox.getText();
	}
	
	/*
	 * Returns whether to use the desktop applications that are local
	 * to the system.
	 */
	private boolean getUseLocalApps() {
		return (m_autoUpdateChoiceTable.isVisible() ? m_useLocalApps.getValue() : false);
	}
	
	/*
	 * Returns whether to use desktop applications from a remote
	 * location.
	 */
	private boolean getUseRemoteApps() {
		return (m_autoUpdateChoiceTable.isVisible() ? m_useRemoteApps.getValue() : true);
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the
	 * data in a GwtFileSyncAppConfiguration object.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		GwtFileSyncAppConfiguration fileSyncAppConfig = new GwtFileSyncAppConfiguration();

		boolean useLocalApps  = getUseLocalApps();
		boolean useRemoteApps = getUseRemoteApps();
		String  autoUpdateUrl = getAutoUpdateUrl();
		boolean deployEnabled = getIsFileSyncAppDeployEnabled();

		// Get whether the File Sync Application is enabled.
		fileSyncAppConfig.setIsFileSyncAppEnabled(getIsFileSyncAppEnabled());
		
		// Get the sync interval from the dialog.
		fileSyncAppConfig.setSyncInterval(getIntervalInt());
		
		// Get the auto-update URL from the dialog.
		fileSyncAppConfig.setAutoUpdateUrl(autoUpdateUrl);
		
		// Get the location of the desktop applications from the dialog.
		fileSyncAppConfig.setUseLocalApps( useLocalApps );
		fileSyncAppConfig.setUseRemoteApps(useRemoteApps);
		
		// Get whether the file sync application can be deployed.
		fileSyncAppConfig.setIsDeploymentEnabled(deployEnabled);
		
		// Get whether the file sync application can cache the user's
		// password.
		fileSyncAppConfig.setAllowCachePwd(getAllowCachePwd());
		
		// Get the max file size the file sync application can sync.
		fileSyncAppConfig.setMaxFileSize(getMaxFileSize());

		// If the 'allow deployment...' checkbox is checked the user
		// must have an auto-update URL.
		if (deployEnabled && getUseRemoteApps() && (!(GwtClientHelper.hasString(autoUpdateUrl)))) {
			GwtClientHelper.deferredAlert(m_messages.fileSyncAppAutoUpdateUrlRequiredPrompt());
			m_autoUpdateUrlTextBox.setFocus(true);
			return null;
		}
		
		// Are we running in Filr?
		if (m_isFilr) {
			// Yes!  Store a GwtDesktopApplicationsLists object
			// corresponding to the current selections.
			GwtAppListMode mode;
			if      (m_blacklistRB.getValue()) mode = GwtAppListMode.BLACKLIST;
			else if (m_whitelistRB.getValue()) mode = GwtAppListMode.WHITELIST;
			else                               mode = GwtAppListMode.DISABLED;
			
			GwtDesktopApplicationsLists appLists = new GwtDesktopApplicationsLists();
			appLists.setAppListMode(mode);
			getAppList(m_macLB,     appLists.getApplications(GwtAppPlatform.MAC)    );
			getAppList(m_windowsLB, appLists.getApplications(GwtAppPlatform.WINDOWS));

			fileSyncAppConfig.setGwtDesktopApplicationsLists(appLists);
		}
		
		return fileSyncAppConfig;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is
	 * shown.
	 * 
	 * Implements the DlgBog.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return m_syncIntervalTextBox;
	}
	
	/**
	 * Returns information for running the dialog's help page.
	 * 
	 * Implements the DlgBox.getHelpData() method.
	 * 
	 * @return 
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("desktopapp");
		return helpData;
	}

	/*
	 * Return the interval entered by the user.
	 */
	private int getIntervalInt() {
		String intervalStr = m_syncIntervalTextBox.getText();
		int interval;
		if (GwtClientHelper.hasString(intervalStr))
		     interval = Integer.parseInt( intervalStr );
		else interval = 0;
		return interval;
	}
	
	/*
	 * Return whether the File Sync Application is enabled.
	 */
	private boolean getIsFileSyncAppEnabled() {
		return m_enableFileSyncAccessCB.getValue();
	}
	
	/*
	 * Return whether deployment of the file sync application is
	 * enabled.
	 */
	private boolean getIsFileSyncAppDeployEnabled() {
		return m_enableDeployCB.getValue();
	}
	
	/*
	 * Get the max file size entered by the user.
	 */
	private int getMaxFileSize() {
		String maxStr = m_maxFileSizeTextBox.getValue();
		int max;
		if (GwtClientHelper.hasString(maxStr))
		     max = Integer.parseInt(maxStr);
		else max = (-1);
		return max;
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the
	 * given values.
	 * 
	 * @param
	 */
	public void init(GwtFileSyncAppConfiguration fileSyncAppConfiguration) {
		// If local desktop applications are available for download...
		if (fileSyncAppConfiguration.getLocalAppsExist()) {
			// ...we give the user the choice to use them...
			m_autoUpdateChoiceTable.setVisible( true );
			m_autoUpdateUrlOnlyTable.setVisible(false);
			m_autoUpdateUrlTextBox = m_autoUpdateUrlTextBox_Choice;
			m_useLocalApps.setValue( fileSyncAppConfiguration.getUseLocalApps() );
			m_useRemoteApps.setValue(fileSyncAppConfiguration.getUseRemoteApps());
		}
		
		else {
			// ...otherwise, we only allow a URL to be specified.
			m_autoUpdateChoiceTable.setVisible( false);
			m_autoUpdateUrlOnlyTable.setVisible(true );
			m_autoUpdateUrlTextBox = m_autoUpdateUrlTextBox_UrlOnly;
			m_useLocalApps.setValue( false);
			m_useRemoteApps.setValue(true );
		}
		
		// Initialize the on/off radio buttons.
		m_enableFileSyncAccessCB.setValue(fileSyncAppConfiguration.getIsFileSyncAppEnabled());
			
		// Initialize the deployment enabled checkbox.
		m_enableDeployCB.setValue(fileSyncAppConfiguration.getIsDeploymentEnabled());

		// Initialize the allow password cache checkbox.
		m_allowPwdCacheCB.setValue(fileSyncAppConfiguration.getAllowCachePwd());
		
		// Initialize the interval text box.
		int interval = fileSyncAppConfiguration.getSyncInterval();
		m_syncIntervalTextBox.setText(String.valueOf(interval));
		
		// Initialize the auto-update URL.
		m_autoUpdateUrlTextBox.setText(fileSyncAppConfiguration.getAutoUpdateUrl());
		
		// Initialize the max file size
		int size = fileSyncAppConfiguration.getMaxFileSize();
		String value;
		if (size < 0)
		     value = "";
		else value = String.valueOf(size);
		m_maxFileSizeTextBox.setText(value);

		// Initialize the application list widgets.
		initAppLists(fileSyncAppConfiguration.getGwtDesktopApplicationsLists());

		// Finally, ensure the error panel is hidden in the dialog when
		// it's shown.
		hideErrorPanel();
	}
	
	private void initAppLists(GwtDesktopApplicationsLists appLists) {
		// Are we running in Filr?
		m_appListPanel.setVisible(m_isFilr);
		if (m_isFilr) {
			// Yes!  Check the appropriate radio button...
			RadioButton rb;
			switch (appLists.getAppListMode()) {
			default:
			case DISABLED:   rb = m_disabledRB;  break;
			case BLACKLIST:  rb = m_blacklistRB; break;
			case WHITELIST:  rb = m_whitelistRB; break;
			}
			rb.setValue(true);

			// ...and populate the platform list boxes.
			populateAppList(m_macLB,     appLists.getApplications(GwtAppPlatform.MAC)    );
			populateAppList(m_windowsLB, appLists.getApplications(GwtAppPlatform.WINDOWS));
		}
		
		else {
			// No, we are not running in Filr!  Initialize the
			// widgets accordingly.
			m_disabledRB.setValue(true);
			m_macLB.clear();
			m_windowsLB.clear();
		}
	}
	
	/*
	 * Returns true if a ListBox contains a value and false otherwise.
	 */
	private static boolean listContains(ListBox lb, String value) {
		if (null != value) {
			if (0 < value.length()) {
				value = value.toLowerCase();
				for (int i = 0; i < lb.getItemCount(); i += 1) {
					String v = lb.getValue(i);
					if (v.toLowerCase().equals(value)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/*
	 * Asynchronously loads the next part of the dialog.
	 */
	private void loadPart1Async(final ConfigureFileSyncAppDlgClient csfaDlgClient) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now(csfaDlgClient);
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the dialog.
	 * 
	 * Creates a MultiPromptDlg. 
	 */
	private void loadPart1Now(final ConfigureFileSyncAppDlgClient csfaDlgClient) {
		final ConfigureFileSyncAppDlg thisDlg = this;
		MultiPromptDlg.createAsync(new MultiPromptDlgClient() {
			@Override
			public void onSuccess(MultiPromptDlg mpDlg) {
				m_mpDlg = mpDlg;
				m_mpDlg.setCaption(m_messages.fileSyncApp_PromptHeader());
				if (null != csfaDlgClient) {
					csfaDlgClient.onSuccess(thisDlg);
				}
			}

			@Override
			public void onUnavailable() {
				if (null != csfaDlgClient) {
					csfaDlgClient.onUnavailable();
				}
			}
		});
	}
	
	/**
	 * This method gets called when the user types in the 'number of
	 * entries to show' text box.
	 * 
	 * We only allow the user to enter numbers.
	 * 
	 * Implement the KeyPressHandler.onKeyPress() abstract method.
	 * 
	 * @param event
	 */
	@Override
	public void onKeyPress(KeyPressEvent event) {
        // Get the key the user pressed
        int keyCode = event.getNativeEvent().getKeyCode();
        if (!(GwtClientHelper.isKeyValidForNumericField(event.getCharCode(), keyCode))) {
        	// Make sure we are dealing with a text box.
        	Object source = event.getSource();
        	if (source instanceof TextBox) {
        		// Suppress the current keyboard event.
            	TextBox txtBox = ((TextBox) source);
        		txtBox.cancelKey();
        	}
        }
	}

	/*
	 * Populates the given ListBox with the applications in the given
	 * List<GwtAppInfo>.
	 */
	private void populateAppList(ListBox lb, List<GwtAppInfo> apps) {
		lb.clear();
		if (GwtClientHelper.hasItems(apps)) {
			for (GwtAppInfo app:  apps) {
				String pn = app.getProcessName();
				lb.addItem(buildAppListDisplayValue(app.getDescription(), pn), pn);
			}
		}
	}

	/*
	 * Asynchronously prompts for an entry for a ListBox.
	 */
	private void promptForDataAsync(final ListBox listBox, final List<String> addPrompts, final List<String> addValues) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				promptForDataNow(listBox, addPrompts, addValues);
			}
		});
	}
	
	/*
	 * Synchronously prompts for an entry for a ListBox.
	 */
	private void promptForDataNow(final ListBox listBox, final List<String> addPrompts, final List<String> addValues) {
		// Prompt the user for something to add.
		MultiPromptDlg.initAndShow(
			m_mpDlg,
			new MultiPromptCallback() {
				@Override
				public int applied(List<String> addValues) {
					// Did they enter a process name?
					String processName = addValues.get(PROCESS_NAME_INDEX).trim();
					if (!(GwtClientHelper.hasString(processName))) {
						// No!  Tell the user about the problem and
						// bail.
						GwtClientHelper.deferredAlert(m_messages.fileSyncApp_Error_NoProcessName());
						return PROCESS_NAME_INDEX;	// Put's the focus in the process name <INPUT>.
					}
					
					// Did they enter a description?
					String description = addValues.get(DESCRIPTION_INDEX).trim();
					if (!(GwtClientHelper.hasString(description))) {
						// No!  Just use the process name.
						description = processName;
					}
					
					// Yes!  If this isn't already in the list...
					String listDisplay = buildAppListDisplayValue(description, processName);
					if (!(listContains(listBox, processName))) {
						// ...add and select it...
						listBox.addItem(listDisplay, processName);
						listBox.setSelectedIndex(listBox.getItemCount() - 1);
					}
					
					// ...and bail.  We're done with the add.
					return MultiPromptCallback.NO_ERROR;
				}

				@Override
				public void canceled() {
					// Nothing to do.
				}
			},
			addPrompts,
			addValues);
	}


	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the configure file sync application dialog and perform some   */
	/* operation on it.                                              */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

	/**
	 * Callback interface to interact with the 'configure file sync
	 * application' dialog asynchronously after it loads. 
	 */
	public interface ConfigureFileSyncAppDlgClient {
		void onSuccess(ConfigureFileSyncAppDlg cfsaDlg);
		void onUnavailable();
	}
	
	/**
	 * Executes code through the GWT.runAsync() method to ensure that
	 * all of the executing code is in this split point.
	 */
	public static void createDlg(
		final Boolean autoHide,
		final Boolean modal,
		final Integer left,
		final Integer top,
		final Integer width,
		final Integer height,
		final ConfigureFileSyncAppDlgClient cfsaDlgClient)
	{
		GWT.runAsync(ConfigureFileSyncAppDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ConfigureFileSyncAppDlg());
				if (null != cfsaDlgClient) {
					cfsaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				new ConfigureFileSyncAppDlg(
					autoHide,
					modal,
					left,
					top,
					width,
					height,
					cfsaDlgClient);
			}
		});
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that
	 * all of the executing code is in this split point.
	 */
	public static void initAndShow(
		final ConfigureFileSyncAppDlg dlg,
		final GwtFileSyncAppConfiguration config,
		final Integer left,
		final Integer top,
		final Integer width,
		final Integer height,
		final ConfigureFileSyncAppDlgClient cfsaDlgClient)
	{
		GWT.runAsync(ConfigureFileSyncAppDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ConfigureFileSyncAppDlg());
				if (null != cfsaDlgClient) {
					cfsaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				if ((null != width) && (null != height)) {
					dlg.setPixelSize(width, height);
				}
				
				dlg.init(config);
				if ((null != left) && (null != top)) {
					dlg.setPopupPosition(left, top);
				}

				dlg.show();
			}
		});
	}
}
