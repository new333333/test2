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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.DeleteSharesCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetShareListsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveShareListsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ShareListsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateShareListsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ValidateShareListsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtShareLists;
import org.kablink.teaming.gwt.client.util.GwtShareLists.ShareListMode;
import org.kablink.teaming.gwt.client.widgets.DlgBox.DlgButtonMode;
import org.kablink.teaming.gwt.client.widgets.PromptDlg.PromptDlgClient;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This composite is used to set the zone share whitelist/blacklist,
 * i.e., which external email addresses/domains can/cannot be shared with.
 * 
 * @author drfoster@novell.com
 */
public class EditZoneShareListsTab extends EditZoneShareTabBase {
	private CheckBox					m_cleanupCB;				// The checkbox used to indicate that invalid shares should be deleted.
	private List<HandlerRegistration>	m_registeredEventHandlers;	// Events registered for notification by this tab.
	private GwtShareLists				m_shareLists;				// The share lists being edited.
	private GwtTeamingMessages			m_messages;					// Access to the GWT localized string resources.
	private List<Long>					m_invalidShareIds;			// A List<Long> of the share IDs to be deleted because they're invalid.  Setup in validate().
	private ListBox						m_domainsLB;				// The list of domains.
	private ListBox						m_emailAddressesLB;			// The list of email addresses.
	private PromptDlg					m_pDlg;						// The dialog used to prompt the user for information.
	private RadioButton					m_blacklistRB;				// The radio button specifying the lists are part of a blacklist.
	private RadioButton					m_disabledRB;				// The radio button specifying the lists are to be ignored.
	private RadioButton					m_whitelistRB;				// The radio button specifying the lists are part of a whitelist.
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[] {
	};

	
	/**
	 * Constructor method. 
	 */
	public EditZoneShareListsTab(EditZoneShareSettingsDlg shareDlg) {
		// Initialize the super class...
		super();
		
		// ...initialize anything else that requires it...
		m_messages = GwtTeaming.getMessages();
		
		// ...and create the of the tab.
		initWidget(createTabContent());
	}

	/**
	 * Called if the user cancels the dialog.
	 * 
	 * Implements the EditZoneShareTabBase.cancel() method.
	 */
	@Override
	public void cancel(EditZoneShareTabCallback callback) {
		// We always allow the tab to be canceled.
		callback.success();
	}

	/*
	 * Creates the widgets for specifying whether existing shares that
	 * don't meet the whitelist/blacklist criteria should be deleted
	 * or not.
	 */
	private Widget createCleanupCheckbox() {
		// Create a panel to contain the cleanup checkbox...
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("editZoneShareListsTab_CleanupPanel");

		// ...create the checkbox...
		m_cleanupCB = new CheckBox(m_messages.editZoneShareListsTab_Cleanup());
		m_cleanupCB.addStyleName("editZoneShareListsTab_CleanupCB");
		m_cleanupCB.removeStyleName("gwt-CheckBox");
		m_cleanupCB.setValue(false);
		fp.add(m_cleanupCB);
		
		// ...and return the panel. 
		return fp;
	}
	
	/*
	 * Creates the widgets for entering domains.
	 */
	private Widget createDomainList() {
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("editZoneShareListsTab_DomainPanel");

		// Note that the list will be added to the FlowPanel by
		// createList().
		m_domainsLB = createList(
			fp,
			m_messages.editZoneShareListsTab_Domains(),
			m_messages.editZoneShareListsTab_Domains_AddPrompt(),
			false);	// false -> Not an email address list.
		
		return fp;
	}
	
	/*
	 * Creates the widgets for entering email addresses.
	 */
	private Widget createEmailAddressList() {
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("editZoneShareListsTab_EmailAddressPanel");
		
		// Note that the list will be added to the FlowPanel by
		// createList().
		m_emailAddressesLB = createList(
			fp,
			m_messages.editZoneShareListsTab_EMAs(),
			m_messages.editZoneShareListsTab_EMA_AddPrompt(),
			true);	// true -> Email address list.
		
		return fp;
	}

	/*
	 * Creates the widgets for tab's header
	 */
	private Widget createHeader() {
		// Create a panel to contain the header...
		FlowPanel fp = new FlowPanel();
		fp.addStyleName("editZoneShareListsTab_HeaderPanel");

		// ...create the header...
		InlineLabel header = new InlineLabel(m_messages.editZoneShareListsTab_Header());
		header.addStyleName("editZoneShareListsTab_Header");
		fp.add(header);

		// ...and return the panel. 
		return fp;
	}
	
	/*
	 * Creates the widgets for entering email addresses or domains.
	 */
	private ListBox createList(final FlowPanel contentPanel, final String listLabel, final String addPrompt, final boolean isEMAList) {
		// Add a label for the list widgets.
		InlineLabel il = new InlineLabel(listLabel);
		il.addStyleName("editZoneShareListsTab_SectionHeader editZoneShareListsTab_ListHeader");
		contentPanel.add(il);

		// Create a HorizontalPanel to hold the list widgets.
		HorizontalPanel horizontalListPanel = new HorizontalPanel();
		horizontalListPanel.addStyleName("editZoneShareListsTab_ListPanel");
		horizontalListPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

		// Create the ListBox itself.
		final ListBox listBox = new ListBox(true);	// true -> Multi-select ListBox.
		listBox.setVisibleItemCount(5);
		listBox.addStyleName("editZoneShareListsTab_List");

		// Create a VerticalPanel to hold buttons for adding to and
		// removing from the list.
		VerticalPanel verticalButtonPanel = new VerticalPanel();
		verticalButtonPanel.addStyleName("editZoneShareListsTab_ListButtons");
		verticalButtonPanel.setVerticalAlignment(  HasVerticalAlignment.ALIGN_TOP   );
		verticalButtonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

		// Create a button for adding to the list.
		Button b = new Button(m_messages.editZoneShareListsTab_Add(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				promptForDataAsync(isEMAList, listBox, addPrompt);
			}
		});
		b.addStyleName("editZoneShareListsTab_ListButton");
		verticalButtonPanel.add(b);

		// Create button for removing from the list.
		b = new Button(m_messages.editZoneShareListsTab_Delete(), new ClickHandler() {
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
		b.addStyleName("margintop3pxb editZoneShareListsTab_ListButton");
		verticalButtonPanel.add(b);
		
		// Connect the panels together.
		horizontalListPanel.add(   listBox            );
		horizontalListPanel.add(   verticalButtonPanel);
		contentPanel.add(horizontalListPanel          );

		// If we get here, reply refers to the ListBox widget we
		// created.  Return it.
		return listBox;
	}
	
	/*
	 * Creates the widgets used for determining whether the lists are a
	 * whitelist or blacklist.
	 */
	private Widget createModeWidgets() {
		VerticalPanel vt = new VerticalPanel();
		vt.addStyleName("editZoneShareListsTab_ModePanel");
		
		InlineLabel il = new InlineLabel(m_messages.editZoneShareListsTab_Mode());
		il.addStyleName("editZoneShareListsTab_SectionHeader editZoneShareListsTab_ModeHeader");
		vt.add(il);
		
		m_disabledRB = new RadioButton("modeGroup", m_messages.editZoneShareListsTab_ModeDisabled());
		m_disabledRB.addStyleName("editZoneShareListsTab_ModeRadio");
		m_disabledRB.removeStyleName("gwt-RadioButton");
		vt.add(m_disabledRB);
		
		m_whitelistRB = new RadioButton("modeGroup", m_messages.editZoneShareListsTab_ModeWhitelist());
		m_whitelistRB.addStyleName("editZoneShareListsTab_ModeRadio");
		m_whitelistRB.removeStyleName("gwt-RadioButton");
		vt.add(m_whitelistRB);
		
		m_blacklistRB = new RadioButton("modeGroup", m_messages.editZoneShareListsTab_ModeBlacklist());
		m_blacklistRB.addStyleName("editZoneShareListsTab_ModeRadio");
		m_blacklistRB.removeStyleName("gwt-RadioButton");
		vt.add(m_blacklistRB);
		
		return vt;
	}

	/*
	 * Create all the controls that make up the tab.
	 */
	private Panel createTabContent() {
		// Create the main content panel...
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.setStyleName("editZoneShareListsTab_Content");
		
		// ...create its contents...
		mainPanel.add(createHeader()          );
		mainPanel.add(createModeWidgets()     );
		mainPanel.add(createEmailAddressList());
		mainPanel.add(createDomainList()      );
		mainPanel.add(createCleanupCheckbox() );

		// ...and return it.
		return mainPanel;
	}
	
	/*
	 * Asynchronously deletes the invalid shares.
	 */
	private void doCleanupAsync(final EditZoneShareTabCallback callback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doCleanupNow(callback);
			}
		});
	}
	
	/*
	 * Synchronously deletes the invalid shares.
	 */
	private void doCleanupNow(final EditZoneShareTabCallback callback) {
		// Send the request for the invalid shares to be deleted.
		DeleteSharesCmd cmd = new DeleteSharesCmd(m_invalidShareIds);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_DeleteShares() );
				
				callback.failure();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				// Did all the invalid shares get deleted?
				ErrorListRpcResponseData responseData = ((ErrorListRpcResponseData) result.getResponseData());
				if (responseData.hasErrors()) {
					// No!  Tell the user about the problem.
					GwtClientHelper.displayMultipleErrors(
						m_messages.editZoneShareListsTab_Error_DeleteSharesFailed(),
						responseData.getErrorList());
					callback.failure();
				}

				else {
					// Yes, all the invalid shares got deleted!
					callback.success();
				}
			}
		});
	}
	
	/*
	 * Asynchronously saves the share lists and deletes any invalid
	 * shares.
	 */
	private void doSaveAndCleanupAsync(final EditZoneShareTabCallback callback) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				doSaveAndCleanupNow(callback);
			}
		});
	}
	
	/*
	 * Synchronously saves the share lists via a GWT RPC.
	 */
	private void doSaveAndCleanupNow(final EditZoneShareTabCallback callback) {
		// Construct a GwtShareLists containing the tab's content...
		GwtShareLists saveThis = getShareListsFromTab();
		
		// ...and save it via a GWT RPC request.
		final boolean needsCleanup = (m_cleanupCB.getValue() && GwtClientHelper.hasItems(m_invalidShareIds)); 
		SaveShareListsCmd cmd = new SaveShareListsCmd(saveThis);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_SaveShareLists() );
				
				callback.failure();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				if (needsCleanup)
				     doCleanupAsync(callback);
				else callback.success();
			}
		});
	}
	
	/*
	 * Returns a ShareListMode enumeration value based on the radio
	 * button selected in the tab.
	 */
	private ShareListMode getShareListModeFromTab() {
		ShareListMode reply;
		if      (m_blacklistRB.getValue()) reply = ShareListMode.BLACKLIST;
		else if (m_whitelistRB.getValue()) reply = ShareListMode.WHITELIST;
		else                               reply = ShareListMode.DISABLED;
		return reply;
	}
	
	/*
	 * Returns a GwtShareLists object based on contents of the tab.
	 */
	public GwtShareLists getShareListsFromTab() {
		GwtShareLists reply = new GwtShareLists();
		reply.setShareListMode(getShareListModeFromTab());
		int c = m_emailAddressesLB.getItemCount();
		for (int i = 0; i < c; i += 1) {
			reply.addEmailAddress(m_emailAddressesLB.getItemText(i));
		}
		c = m_domainsLB.getItemCount();
		for (int i = 0; i < c; i += 1) {
			reply.addDomain(m_domainsLB.getItemText(i));
		}
		return reply;
	}
	
	/**
	 * Called to allow the tab to initialize.
	 * 
	 * Implements the EditZoneShareTabBase.init() method.
	 */
	@Override
	public void init() {
		// Simply continue loading the tab.
		loadPart1Async();
	}

	/*
	 * Asynchronously initializes the dialog's contents from a
	 * GwtShareLists object.
	 */
	private void initFromShareListsAsync() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				initFromShareListsNow();
			}
		});
	}

	/*
	 * Synchronously initializes the dialog's contents from a
	 * GwtShareLists object.
	 */
	private void initFromShareListsNow() {
		// Select the appropriate mode button for how the lists are to
		// be interpreted (i.e., as a whitelist or blacklist.)
		switch (m_shareLists.getShareListMode()) {
		case BLACKLIST:  m_blacklistRB.setValue(true); break;
		case DISABLED:   m_disabledRB.setValue( true); break;
		case WHITELIST:  m_whitelistRB.setValue(true); break;
		}

		// Populate the email address list.
		m_emailAddressesLB.clear();
		List<String> list = m_shareLists.getEmailAddresses();
		if (GwtClientHelper.hasItems(list)) {
			for (String ema:  list) {
				m_emailAddressesLB.addItem(ema);
			}
		}
		
		// Populate the domains list.
		m_domainsLB.clear();
		list = m_shareLists.getDomains();
		if (GwtClientHelper.hasItems(list)) {
			for (String domain:  list) {
				m_domainsLB.addItem(domain);
			}
		}
		
		// By default, we won't delete shares that don't meet the
		// criteria.
		m_cleanupCB.setValue(false);
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
	
	/*
	 * Asynchronously loads the next part of the tab.
	 */
	private void loadPart1Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart1Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the tab.
	 * 
	 * Creates a PromptDlg if we haven't already created one. 
	 */
	private void loadPart1Now() {
		// Have we created a PromptDlg yet?
		if (null == m_pDlg) {
			// No!  Create one now...
			PromptDlg.createAsync(new PromptDlgClient() {
				@Override
				public void onSuccess(PromptDlg pDlg) {
					// ...and continue loading.
					m_pDlg = pDlg;
					loadPart2Async();
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
			// continue loading.
			loadPart2Now();
		}
	}
	
	/*
	 * Asynchronously loads the next part of the tab.
	 */
	private void loadPart2Async() {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				loadPart2Now();
			}
		});
	}
	
	/*
	 * Synchronously loads the next part of the tab.
	 * 
	 * Sends an RPC request to the server for a GwtShareLists object
	 * and uses it to complete the initialization of the tab.
	 */
	private void loadPart2Now() {
		GwtClientHelper.executeCommand(new GetShareListsCmd(), new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_GetShareLists() );
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				m_shareLists = ((ShareListsRpcResponseData) result.getResponseData()).getShareLists();
				initFromShareListsAsync();
			}
		});
	}
	
	/**
	 * Called when the dialog is attached.
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
	 * Called when the dialog is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		// Let the widget detach and then unregister our event handlers.
		super.onDetach();
		unregisterEvents();
	}

	/*
	 * Asynchronously prompts for an entry for a ListBox.
	 */
	private void promptForDataAsync(final boolean isEMAList, final ListBox listBox, final String addPrompt, final String addThis) {
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				promptForDataNow(isEMAList, listBox, addPrompt, addThis);
			}
		});
	}
	
	private void promptForDataAsync(final boolean isEMAList, final ListBox listBox, final String addPrompt) {
		// Always use the initial form of the method.
		promptForDataAsync(isEMAList, listBox, addPrompt, "");
	}
	
	/*
	 * Synchronously prompts for an entry for a ListBox.
	 */
	private void promptForDataNow(final boolean isEMAList, final ListBox listBox, final String addPrompt, final String addThis) {
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
						// Yes!  Are we working with an email address
						// list?
						int atPos = addThis.indexOf('@');
						if (isEMAList) {
							// Yes!  is it valid?
							int parts = ((0 < atPos) ? addThis.split("@").length : 0);
							if ((2 != parts) || ((addThis.length() - 1) == atPos)) {
								// No!  Tell the user about the problem
								// and let them try again.
								Window.alert(m_messages.editZoneShareListsTab_Error_InvalidEMA());
								promptForDataAsync(isEMAList, listBox, addPrompt, addThis);
								return;
							}
						}
						
						else {
							// No, we aren't working with an email
							// address list!  It must be a domain list.
							// Is this a valid domain?
							if (0 < atPos) {
								// No!  Tell the user about the problem
								// and let them try again.
								Window.alert(m_messages.editZoneShareListsTab_Error_InvalidDomain());
								promptForDataAsync(isEMAList, listBox, addPrompt, addThis);
								return;
							}
							if ((0 == atPos) && ((1 == addThis.length()) || ((-1) != addThis.substring(1).indexOf('@')))) {
								// No!  Tell the user about the problem
								// and let them try again.
								Window.alert(m_messages.editZoneShareListsTab_Error_InvalidDomain());
								promptForDataAsync(isEMAList, listBox, addPrompt, addThis);
								return;
							}
							
							// If the domain they entered starts with
							// an '@'...
							if (0 == atPos) {
								// ...strip it off.
								addThis = addThis.substring(1);
							}
						}

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
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents() {
		// If we haven't allocated a list to track events we've registered yet...
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
	
	/**
	 * Called if the user OKs the dialog.
	 * 
	 * Implements the EditZoneShareTabBase.save() method.
	 */
	@Override
	public void save(EditZoneShareTabCallback callback) {
		// Save the share lists.  This will call the appropriate
		// callback method when the request completes.
		doSaveAndCleanupAsync(callback);
	}
	
	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents() {
		// If we have a non-empty list of registered events...
		if ((null != m_registeredEventHandlers) && (!(m_registeredEventHandlers.isEmpty()))) {
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers(m_registeredEventHandlers);
		}
	}
	
	/**
	 * Called to validate the contents of the tab.
	 * 
	 * Implements the EditZoneShareTabBase.validate() method.
	 */
	@Override
	public void validate(final EditZoneShareTabCallback callback) {
		// Before validating, no shares should be set for deletion. 
		m_invalidShareIds = null;

		// If the checkbox isn't checked that says to delete invalid
		// shares...
		if (!(m_cleanupCB.getValue())) {
			// ...nothing needs validation.  Simply call the success
			// ...callback.
			callback.success();
			return;
		}
		
		// Construct a GwtShareLists containing the tab's content.
		GwtShareLists validateThis = getShareListsFromTab();
		
		// If share list restrictions are disabled...
		if (validateThis.getShareListMode().isDisable()) {
			// ...nothing needs validation.  Simply call the success
			// ...callback.
			callback.success();
			return;
		}

		// If we're restricting a blacklist but nothing has been
		// entered...
		if (validateThis.getShareListMode().isBlacklist() &&
				(!(GwtClientHelper.hasItems(validateThis.getDomains()))) &&
				(!(GwtClientHelper.hasItems(validateThis.getEmailAddresses())))) {
			// ...nothing needs validation.  Simply call the success
			// ...callback.
			callback.success();
			return;
		}
		
		// Are there any shares that will be deleted if we save?
		ValidateShareListsCmd cmd = new ValidateShareListsCmd(validateThis);
		GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>() {
			@Override
			public void onFailure(Throwable caught) {
				GwtClientHelper.handleGwtRPCFailure(
					caught,
					m_messages.rpcFailure_ValidateShareLists() );
				
				callback.failure();
			}

			@Override
			public void onSuccess(VibeRpcResponse result) {
				final ValidateShareListsRpcResponseData responseData = ((ValidateShareListsRpcResponseData) result.getResponseData());
				List<ErrorInfo> errors = responseData.getErrorList();
				int count = ((null == errors) ? 0 : errors.size());
				if (0 == count) {
					// No!  Simply invoke the success callback.
					callback.success();
				}
				
				else {
					// Yes, there are shares that will be deleted if we
					// save!  Is the user sure they want to do that?
					GwtClientHelper.displayMultipleErrors(
							m_messages.editZoneShareListsTab_Confirm_DeleteShares(),
							errors,
							new ConfirmCallback() {
						@Override
						public void dialogReady() {
							// Ignored.  We don't care when the dialog is
							// ready.
						}
		
						@Override
						public void accepted() {
							// Yes, the user said do it!  Let the save
							// happen.
							m_invalidShareIds = responseData.getInvalidShareIds();
							callback.success();
						}
		
						@Override
						public void rejected() {
							// No, the user said don't do it!
							callback.failure();
						}
					},
					DlgButtonMode.OkCancel);
				}
			}
		});
	}
}
