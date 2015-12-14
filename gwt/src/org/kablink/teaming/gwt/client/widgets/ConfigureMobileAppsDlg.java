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
import org.kablink.teaming.gwt.client.GwtZoneMobileAppsConfig;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.GetMobileAppsConfigCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveMobileAppsConfigurationCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtMobileOpenInSetting;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.PromptDlg.PromptDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements the Mobile Applications dialog for the administration
 * console.
 *  
 * @author drfoster@novell.com
 */
public class ConfigureMobileAppsDlg extends DlgBox implements KeyPressHandler, EditSuccessfulHandler {
	private boolean				m_initialAllowPlayWithOtherApps;	//
	private CheckBox			m_allowOfflineContentCB;			//
	private CheckBox			m_allowPwdCacheCB;					//
	private CheckBox			m_cutCopyEnabledCB;					//
	private CheckBox			m_enableMobileAppsAccessCB;			//
	private CheckBox			m_disableJailBrokenCB;				//
	private CheckBox			m_forcePinCodeCB;					//
	private CheckBox			m_screenCaptureEnabledAndroidCB;	//
	private FlowPanel			m_androidApplicationsPanel;			//
	private FlowPanel			m_iosApplicationsPanel;				//
	private GwtTeamingMessages	m_messages;							//
	private ListBox				m_androidApplicationsLB;			//
	private ListBox				m_iosApplicationsLB;				//
	private ListBox				m_openInLB;							//
	private PromptDlg			m_pDlg;								//
	private TextBox				m_syncIntervalTextBox;				//
	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ConfigureMobileAppsDlg(boolean autoHide, boolean modal, int xPos, int yPos, int width, int height) {
		// Initialize the superclass...
		super(autoHide, modal, xPos, yPos, new Integer(width), new Integer(height), DlgButtonMode.OkCancel);
		
		// ...initialize everything else that requires it....
		m_messages = GwtTeaming.getMessages();

		// ...and create the header, content and footer of this dialog
		// ...box.
		createAllDlgContent(m_messages.configureMobileAppsDlgHeader(), this, null, null); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 * 
	 * Implements the DlgBox.createContent() method.
	 * 
	 * @param props
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object props) {
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("teamingDlgBoxContent");
		
		Label label = new Label(m_messages.configureMobileAppsDlgHeader2());
		mainPanel.add(label);
		
		FlowPanel ckboxPanel = new FlowPanel();
		ckboxPanel.addStyleName("marginleft1");
		ckboxPanel.addStyleName("margintop2");
		mainPanel.add(ckboxPanel);
		
		// Add the controls for the 'allow mobile applications to
		// access Filr'.
		String productName = GwtClientHelper.getProductName();
		
		m_enableMobileAppsAccessCB = new CheckBox(m_messages.configureMobileAppsDlgAllowAccess(productName));
		FlowPanel tmpPanel = new FlowPanel();
		tmpPanel.add(m_enableMobileAppsAccessCB);
		ckboxPanel.add(tmpPanel);
		
		// Create the 'Allow mobile applications to cache password'.
		m_allowPwdCacheCB = new CheckBox(m_messages.configureMobileAppsDlgAllowCachePwd());
		tmpPanel = new FlowPanel();
		tmpPanel.add(m_allowPwdCacheCB);
		ckboxPanel.add(tmpPanel);
		
		// Create the 'Allow mobile applications to cache content
		// offline'.
		m_allowOfflineContentCB = new CheckBox(m_messages.configureMobileAppsDlgAllowCacheContent());
		tmpPanel = new FlowPanel();
		tmpPanel.add(m_allowOfflineContentCB);
		ckboxPanel.add(tmpPanel);
		
		// Create the 'Force Pin Code'.
		m_forcePinCodeCB = new CheckBox(m_messages.configureMobileAppsDlgForcePinCode());
		tmpPanel = new FlowPanel();
		tmpPanel.add(m_forcePinCodeCB);
		ckboxPanel.add(tmpPanel);		
		
		// Create the 'Mobile Application Management (MAM)' settings.
		// Create the 'Cut/Copy enabled'.
		m_cutCopyEnabledCB = new CheckBox(m_messages.configureMobileAppsDlgCutCopy());
		tmpPanel = new FlowPanel();
		tmpPanel.add(m_cutCopyEnabledCB);
		ckboxPanel.add(tmpPanel);
		
		// Create the 'Screen capture enabled (Android only)'.
		m_screenCaptureEnabledAndroidCB = new CheckBox(m_messages.configureMobileAppsDlgScreenCaptureAndroid());
		tmpPanel = new FlowPanel();
		tmpPanel.add(m_screenCaptureEnabledAndroidCB);
		ckboxPanel.add(tmpPanel);
		
		// Create the 'Disable applications on rooted or jail broken
		// devices'.
		m_disableJailBrokenCB = new CheckBox(m_messages.configureMobileAppsDlgDisableApplicationsOnRootedOrJailBrokenDevices());
		tmpPanel = new FlowPanel();
		tmpPanel.add(m_disableJailBrokenCB);
		ckboxPanel.add(tmpPanel);		
		
		// Create the controls for open in
		HorizontalPanel hp = new HorizontalPanel();
		hp.addStyleName("margintop3");
		
		Label oiLabel = new Label(m_messages.configureMobileAppsDlgOpenIn());
		oiLabel.addStyleName("configMobileAppsDlg_OpenInLabel");
		hp.add(oiLabel);
		
		m_openInLB = new ListBox();
		m_openInLB.setMultipleSelect(false);
		m_openInLB.addStyleName("configMobileAppsDlg_OpenInSelect");
		m_openInLB.setVisibleItemCount(1);
		m_openInLB.addItem(m_messages.configureMobileAppsDlgOpenIn_Disabled(),  "0");
		m_openInLB.addItem(m_messages.configureMobileAppsDlgOpenIn_AllApps(),   "1");
		m_openInLB.addItem(m_messages.configureMobileAppsDlgOpenIn_WhiteList(), "2");
		m_openInLB.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				danceDlg();
			}
		});
		hp.add(m_openInLB);
		
		ckboxPanel.add(hp);
		
		// Create the controls for the open in White Lists
		m_androidApplicationsPanel = createAndroidWhiteList();
		ckboxPanel.add(m_androidApplicationsPanel);
		
		m_iosApplicationsPanel = createIosWhiteList();
		ckboxPanel.add(m_iosApplicationsPanel);
		
		// Create the controls for sync interval
		label = new Label(m_messages.configureMobileAppsDlgHeader3());
		label.addStyleName("margintop3");
		mainPanel.add(label);
		
		HorizontalPanel hPanel;
		Label intervalLabel;
		
		hPanel = new HorizontalPanel();
		hPanel.addStyleName("marginleft1");
		hPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		hPanel.setSpacing(4);
		
		intervalLabel = new Label(m_messages.configureMobileAppsSyncIntervalLabel());
		hPanel.add(intervalLabel);
		
		m_syncIntervalTextBox = new TextBox();
		m_syncIntervalTextBox.addKeyPressHandler(this);
		m_syncIntervalTextBox.setVisibleLength(3);
		hPanel.add(m_syncIntervalTextBox);
		
		intervalLabel = new Label(m_messages.configureMobileAppsSyncMinutesLabel());
		intervalLabel.addStyleName("gray3");
		hPanel.add(intervalLabel);
		
		mainPanel.add(hPanel);

		return mainPanel;
	}
	
	/*
	 * Creates the widgets for entering Android applications.
	 */
	private FlowPanel createAndroidWhiteList() {
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("configMobileAppsDlg_AndroidPanel");

		// Note that the list will be added to the FlowPanel by
		// createWhiteList().
		m_androidApplicationsLB = createWhiteList(
			fp,
			m_messages.configureMobileAppsDlgWhiteListAndroid(),
			m_messages.configureMobileAppsDlgAddAndroid());
		
		return fp;
	}
	
	/*
	 * Creates the widgets for entering iOS applications.
	 */
	private FlowPanel createIosWhiteList() {
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("configMobileAppsDlg_IosPanel");

		// Note that the list will be added to the FlowPanel by
		// createWhiteList().
		m_iosApplicationsLB = createWhiteList(
			fp,
			m_messages.configureMobileAppsDlgWhiteListIos(),
			m_messages.configureMobileAppsDlgAddIos());
		
		return fp;
	}
	
	/*
	 * Creates the widgets for entering Android or iOS application
	 * white lists.
	 */
	private ListBox createWhiteList(final FlowPanel contentPanel, final String listLabel, final String addPrompt) {
		// Add a label for the list widgets.
		InlineLabel il = new InlineLabel(listLabel);
		il.addStyleName("configMobileAppsDlg_ListHeader");
		contentPanel.add(il);

		// Create a HorizontalPanel to hold the list widgets.
		HorizontalPanel horizontalListPanel = new HorizontalPanel();
		horizontalListPanel.addStyleName("configMobileAppsDlg_ListPanel");
		horizontalListPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

		// Create the ListBox itself.
		final ListBox listBox = new ListBox();
		listBox.setMultipleSelect(true);
		listBox.setVisibleItemCount(5);
		listBox.addStyleName("configMobileAppsDlg_List");

		// Create a VerticalPanel to hold buttons for adding to and
		// removing from the list.
		VerticalPanel verticalButtonPanel = new VerticalPanel();
		verticalButtonPanel.addStyleName("configMobileAppsDlg_ListButtons");
		verticalButtonPanel.setVerticalAlignment(  HasVerticalAlignment.ALIGN_TOP   );
		verticalButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		// Create a button for adding to the list.
		Button b = new Button(m_messages.configureMobileAppsDlgButton_Add(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				promptForDataAsync(listBox, addPrompt);
			}
		});
		b.addStyleName("configMobileAppsDlg_ListButton");
		verticalButtonPanel.add(b);

		// Create button for removing from the list.
		b = new Button(m_messages.configureMobileAppsDlgButton_Delete(), new ClickHandler() {
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
		b.addStyleName("margintop3pxb configMobileAppsDlg_ListButton");
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
	 * Hides/shows the application white lists based on what's selected
	 * in the open in list box.
	 */
	private void danceDlg() {
		int i = m_openInLB.getSelectedIndex();
		if (0 <= i) {
			i = Integer.parseInt(m_openInLB.getValue(i));
			boolean show = (2 == i);
			m_androidApplicationsPanel.setVisible(show);
			m_iosApplicationsPanel.setVisible(    show);
		}
	}
	
	/**
	 * This method gets called when user user presses OK.
	 * 
	 * Implements the EditSuccessfulHandler.editSuccessful() method.
	 * 
	 * @param obj
	 * 
	 * @return
	 */
	@Override
	public boolean editSuccessful(Object obj) {
		GwtZoneMobileAppsConfig mobileAppsConfig = ((GwtZoneMobileAppsConfig) obj);
		
		// Issue a GWT RPC request to save the Mobile Applications
		// Configuration to the database.
		SaveMobileAppsConfigurationCmd cmd = new SaveMobileAppsConfigurationCmd(mobileAppsConfig);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				// Get the panel that holds the errors.
				FlowPanel errorPanel = getErrorPanel();
				errorPanel.clear();
				
				Label label = new Label(m_messages.configureMobileAppsDlgOnSaveUnknownException(caught.toString()));
				label.addStyleName("dlgErrorLabel");
				errorPanel.add(label);
				
				showErrorPanel();
			}
	
			@Override
			public void onSuccess(VibeRpcResponse response) {
				// Close this dialog.
				hide();
			}
		});
		
		// Returning false will prevent the dialog from closing.  We will close the dialog
		// after we successfully save the configuration.
		return false;
	}

	/*
	 * Return whether the value entered by the user for allowing mobile
	 * applications to cache passwords.
	 */
	private boolean getAllowCachePwd() {
		return m_allowPwdCacheCB.getValue();
	}
	
	/*
	 * Return whether the mobile applications can cache content
	 * offline.
	 */
	private boolean getAllowOfflineContent() {
		return m_allowOfflineContentCB.getValue();
	}
	
	/*
	 * Return whether mobile applications can interact with other
	 * applications.
	 */
	private boolean getAllowPlayWithOtherApps() {
		return m_initialAllowPlayWithOtherApps;
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the
	 * data in a GwtMobileAppsConfiguration object.
	 * 
	 * Implements the DlgBox.getDataFromDlg() method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		GwtZoneMobileAppsConfig mobileAppsConfig = new GwtZoneMobileAppsConfig();

		// Get whether mobile applications can access Filr.
		mobileAppsConfig.setMobileAppsEnabled(getMobileAppsEnabled());
		
		// Get whether mobile applications can cache the user's
		// password.
		mobileAppsConfig.setAllowCachePwd(getAllowCachePwd());
		
		// Get whether mobile applications can cache content.
		mobileAppsConfig.setAllowCacheContent(getAllowOfflineContent());
		
		// Get whether mobile applications should force a pin code.
		mobileAppsConfig.setForcePinCode(getForcePinCode());
		
		// Get whether mobile applications can interact with other
		// applications.
		mobileAppsConfig.setAllowPlayWithOtherApps(getAllowPlayWithOtherApps());
		
		// Get the sync interval from the dialog.
		mobileAppsConfig.setSyncInterval(getIntervalInt());
		
		// Get the various values for the Mobile Application Management
		// (MAM) settings.
		mobileAppsConfig.setMobileCutCopyEnabled(                    getCutCopyEnabled()             );
		mobileAppsConfig.setMobileAndroidScreenCaptureEnabled(       getScreenCaptureAndoridEnabled());
		mobileAppsConfig.setMobileDisableOnRootedOrJailBrokenDevices(getDisableJailBroken()          );
		mobileAppsConfig.setMobileOpenIn(                            getOpenIn()                     );
		mobileAppsConfig.setAndroidApplications(                     getAndroidApplicatons()         );
		mobileAppsConfig.setIosApplications(                         getIosApplicatons()             );
		
		return mobileAppsConfig;
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is
	 * shown.
	 * 
	 * Implements the DlgBox.getFocusWidget() method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		return m_syncIntervalTextBox;
	}
	
	/**
	 * Returns a HelpData object describing the help to be invoked for
	 * this dialog.
	 * 
	 * Implements the DlgBox.getHelpData() method.
	 * 
	 * @return
	 */
	@Override
	public HelpData getHelpData() {
		HelpData helpData = new HelpData();
		helpData.setGuideName(HelpData.ADMIN_GUIDE);
		helpData.setPageId("mobile_site");
		return helpData;
	}

	/*
	 * Return the interval entered by the user.
	 */
	private int getIntervalInt() {
		String intervalStr = m_syncIntervalTextBox.getText();
		int interval;
		if (GwtClientHelper.hasString(intervalStr))
		     interval = Integer.parseInt(intervalStr);
		else interval = 0;
		return interval;
	}
	
	/*
	 * Return whether mobile applications are enabled.
	 */
	private boolean getMobileAppsEnabled() {
		return m_enableMobileAppsAccessCB.getValue();
	}
	
	/*
	 * Return whether cut/copy is enabled.
	 */
	private boolean getCutCopyEnabled() {
		return m_cutCopyEnabledCB.getValue();
	}
	
	/*
	 * Return whether Android screen capture is enabled.
	 */
	private boolean getScreenCaptureAndoridEnabled() {
		return m_screenCaptureEnabledAndroidCB.getValue();
	}
	
	/*
	 * Return whether to disable the device if it's jail broken.
	 */
	private boolean getDisableJailBroken() {
		return m_disableJailBrokenCB.getValue();
	}

	/*
	 * Return whether to force a pin code.
	 */
	private boolean getForcePinCode() {
		return m_forcePinCodeCB.getValue();
	}

	/*
	 * Returns the open in setting.
	 */
	private GwtMobileOpenInSetting getOpenIn() {
		int moi = Integer.parseInt(m_openInLB.getValue(m_openInLB.getSelectedIndex()));
		return GwtMobileOpenInSetting.valueOf(moi);
	}
	
	/*
	 * Returns the Android application list.
	 */
	private List<String> getAndroidApplicatons() {
		List<String> reply = new ArrayList<String>();
		for (int i = 0; i < m_androidApplicationsLB.getItemCount(); i += 1) {
			reply.add(m_androidApplicationsLB.getItemText(i));
		}
		return reply;
	}
	
	/*
	 * Returns the iOS application list.
	 */
	private List<String> getIosApplicatons() {
		List<String> reply = new ArrayList<String>();
		for (int i = 0; i < m_iosApplicationsLB.getItemCount(); i += 1) {
			reply.add(m_iosApplicationsLB.getItemText(i));
		}
		return reply;
	}
	
	/**
	 * Initializes this instance of the dialog. 
	 */
	public void init() {
		initPart1Async();
	}

	/*
	 * Asynchronously performs the next part of the initializations.
	 */
	private void initPart1Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				initPart1Now();
			}
		});
	}
	
	/*
	 * Synchronously performs the next part of the initializations.
	 */
	private void initPart1Now() {
		// Have we created a prompt dialog yet?
		if (null == m_pDlg) {
			// No!  Create one now...
			PromptDlg.createAsync(new PromptDlgClient() {
				@Override
				public void onSuccess(PromptDlg pDlg) {
					// ...and continue initializing.
					m_pDlg = pDlg;
					initPart2Async();
				}
	
				@Override
				public void onUnavailable() {
					// Nothing to do.  Error handled in asynchronous
					// provider.
				}
			});
		}
		else {
			// Yes, we've already created a PromptDlg!  Simply
			// continue initializing.
			initPart2Now();
		}
	}
	
	/*
	 * Asynchronously performs the next part of the initializations.
	 */
	private void initPart2Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				initPart2Now();
			}
		});
	}
	
	/*
	 * Synchronously performs the next part of the initializations.
	 */
	private void initPart2Now() {
		// Issue a GWT RPC request to get the Mobile Applications
		// Configuration from the database.
		GwtClientHelper.executeCommand(new GetMobileAppsConfigCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable t) {
				GwtClientHelper.handleGwtRPCFailure(
					t,
					m_messages.rpcFailure_GetMobileAppsConfiguration());
			}
	
			@Override
			public void onSuccess(VibeRpcResponse response) {
				final GwtZoneMobileAppsConfig mobileAppsConfiguration = ((GwtZoneMobileAppsConfig) response.getResponseData());
				GwtClientHelper.deferCommand(new ScheduledCommand() {
					@Override
					public void execute() {
						initUsingConfigData(mobileAppsConfiguration);
					}
				});
			}
		});
	}
	
	/*
	 * Initialize the controls in the dialog with the values from the
	 * given values.
	 */
	private void initUsingConfigData(GwtZoneMobileAppsConfig mobileAppsConfiguration) {
		// Initialize whether mobile applications can access Filr.
		m_enableMobileAppsAccessCB.setValue(mobileAppsConfiguration.getMobileAppsEnabled());
			
		// Initialize the allow password cache checkbox.
		m_allowPwdCacheCB.setValue(mobileAppsConfiguration.getAllowCachePwd());

		// Initialize the offline content.
		m_allowOfflineContentCB.setValue(mobileAppsConfiguration.getAllowCacheContent());
		
		// Initialize the force pin code.
		m_forcePinCodeCB.setValue(mobileAppsConfiguration.getForcePinCode());
		
		// Initialize the allow mobile applications to play with
		// others.
		m_initialAllowPlayWithOtherApps = mobileAppsConfiguration.getAllowPlayWithOtherApps();
		
		// Initialize the interval TextBox.
		int interval = mobileAppsConfiguration.getSyncInterval();
		m_syncIntervalTextBox.setText(String.valueOf(interval));
		
		// Initialize the various widgets for the Mobile Application
		// Management (MAM) settings.
		m_cutCopyEnabledCB.setValue(mobileAppsConfiguration.getMobileCutCopyEnabled());
		m_screenCaptureEnabledAndroidCB.setValue(mobileAppsConfiguration.getMobileAndroidScreenCaptureEnabled());
		m_disableJailBrokenCB.setValue(mobileAppsConfiguration.getMobileDisableOnRootedOrJailBrokenDevices());
		String moi = String.valueOf(mobileAppsConfiguration.getMobileOpenIn().ordinal());
		int si = 0;
		for (int i = 0; i < m_openInLB.getItemCount(); i += 1) {
			if (m_openInLB.getValue(i).equals(moi)) {
				si = i;
				break;
			}
		}
		m_openInLB.setSelectedIndex(si);
		m_androidApplicationsLB.clear();
		m_iosApplicationsLB.clear();
		for (String aApp:  mobileAppsConfiguration.getAndroidApplications()) {m_androidApplicationsLB.addItem(aApp);}
		for (String iApp:  mobileAppsConfiguration.getIosApplications()    ) {m_iosApplicationsLB.addItem(    iApp);}

		// Dance the dialog for the open in setting.
		danceDlg();
		
		hideErrorPanel();
	}
	
	
	/*
	 * Returns true if a ListBox contains a string and false otherwise.
	 */
	private static boolean listContains(ListBox lb, String s) {
		if (null != s) {
			s = s.trim();
			if (0 < s.length()) {
				s = s.toLowerCase();
				for (int i = 0; i < lb.getItemCount(); i += 1) {
					String v = lb.getValue(i);
					if (v.toLowerCase().equals(s)) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * This method gets called when the user types in the 'interval'
	 * text box. We only allow the user to enter numbers.
	 * 
	 * @param
	 */
	@Override
	public void onKeyPress(KeyPressEvent event) {
        // Get the key the user pressed.
        int keyCode = event.getNativeEvent().getKeyCode();
        if (!(GwtClientHelper.isKeyValidForNumericField(event.getCharCode(), keyCode))) {
        	// Make sure we are dealing with a TextBox.
        	Object source = event.getSource();
        	if (source instanceof TextBox) {
        		// Suppress the current keyboard event.
            	TextBox txtBox = ((TextBox) source);
        		txtBox.cancelKey();
        	}
        }
	}

	/*
	 * Asynchronously prompts for an entry for a ListBox.
	 */
	private void promptForDataAsync(final ListBox listBox, final String addPrompt, final String addThis) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				promptForDataNow(listBox, addPrompt, addThis);
			}
		});
	}
	
	private void promptForDataAsync(final ListBox listBox, final String addPrompt) {
		// Always use the initial form of the method.
		promptForDataAsync(listBox, addPrompt, "");
	}
	
	/*
	 * Synchronously prompts for an entry for a ListBox.
	 */
	private void promptForDataNow(final ListBox listBox, final String addPrompt, final String addThis) {
		// Prompt the user for something to add.
		PromptDlg.initAndShow(
			m_pDlg,
			new PromptCallback() {
				@Override
				public void applied(String addThis) {
					// Did they enter something?
					if (!(GwtClientHelper.hasString(addThis))) {
						// No!  Bail.
						return;
					}
					
					addThis = addThis.trim();
					if (0 < addThis.length()) {
						// If this isn't already in the list...
						if (!(listContains(listBox, addThis))) {
							// ...add it...
							listBox.addItem(addThis);
						}
						
						// ...and bail.  We're done with the add.
						return;
					}
				}

				@Override
				public void canceled() {
					// Nothing to do.
				}
			},
			addPrompt,
			addThis);
	}
	
	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the configure mobile applications dialog and perform some     */
	/* operation on it.                                              */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the 'configure mobile
	 * applications' dialog asynchronously after it loads. 
	 */
	public interface ConfigureMobileAppsDlgClient {
		void onSuccess(ConfigureMobileAppsDlg cmaDlg);
		void onUnavailable();
	}

	/**
	 * Loads the ConfigureMobileAppsDlg split point and returns an
	 * instance of it via the callback.
	 * 
	 * @param autoHide
	 * @param modal
	 * @param left
	 * @param top
	 * @param width
	 * @param height
	 * @param cmaDlgClient
	 */
	public static void createAsync(
			final boolean						autoHide,
			final boolean						modal,
			final int							left,
			final int							top,
			final int							width,
			final int							height,
			final ConfigureMobileAppsDlgClient	cmaDlgClient)
	{
		GWT.runAsync(ConfigureMobileAppsDlg.class, new RunAsyncCallback() {
			@Override
			public void onFailure(Throwable reason) {
				GwtClientHelper.deferredAlert(GwtTeaming.getMessages().codeSplitFailure_ConfigureMobileAppsDlg());
				if (cmaDlgClient != null) {
					cmaDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess() {
				ConfigureMobileAppsDlg cmaDlg = new ConfigureMobileAppsDlg(
					autoHide,
					modal,
					left,
					top,
					width,
					height);
				cmaDlgClient.onSuccess(cmaDlg);
			}
		});
	}
}
