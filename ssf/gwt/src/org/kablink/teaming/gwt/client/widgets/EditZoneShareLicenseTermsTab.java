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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.ZoneShareTerms;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetZoneShareTermsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.SaveZoneShareTermsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * This composite is used to set the zone share licensing terms, i.e., the access to the 
 * shared resource for external users is granted only after accepting terms and conditions...
 * 
 * @author lokeshreddy
 */
public class EditZoneShareLicenseTermsTab extends EditZoneShareTabBase
{
	private TextArea m_termsAndConditionsTxtArea;
	private EditZoneShareSettingsDlg m_shareDlg;
	private List<HandlerRegistration> m_registeredEventHandlers;
	private ZoneShareTerms m_zoneShareTerms;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private static TeamingEvents[] REGISTERED_EVENTS = new TeamingEvents[]
    {
	};

	
	/**
	 * Constructor method. 
	 */
	public EditZoneShareLicenseTermsTab(EditZoneShareSettingsDlg shareDlg)
	{
		// Initialize the super class...
		super();
		
		// ...save the parameter...
		m_shareDlg = shareDlg;
		
		// ...and create the of the tab.
		initWidget( createContent() );
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
	 * Create all the controls that make up the tab.
	 */
	private Panel createContent()
	{
		FlowPanel mainPanel;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "editZoneShareTermsTab_Content" );
		
		// Create the panel that will hold the controls for terms and conditions
		{
			Panel termsPanel;
			
			termsPanel = createTermsPanel();
			mainPanel.add( termsPanel );
		}
		
		return mainPanel;
	}

	/**
	 * Create the panel that holds all of the controls for the share terms and conditions
	 */
	private Panel createTermsPanel()
	{
		FlowPanel mainPanel;
		final FlexTable table;
		FlexCellFormatter cellFormatter;
		Label label;
		int nextRow = 0;
		GwtTeamingMessages messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		
		table = new FlexTable();
		cellFormatter = table.getFlexCellFormatter();
		mainPanel.add( table );
		
		// Add a hint
		cellFormatter.setColSpan( nextRow, 0, 2 );
		cellFormatter.setWordWrap( nextRow, 0, false );
		cellFormatter.addStyleName( nextRow, 0, "editZoneShareTermsTab_SectionHeader editZoneShareTermsTab_Header" );
		label = new InlineLabel( messages.editZoneShareTermsTab_LicensingTerms() );
		table.setHTML( nextRow, 0, label.getElement().getInnerHTML() );
		++nextRow;
		
		cellFormatter.setColSpan( nextRow, 0, 2 );
		m_termsAndConditionsTxtArea=new TextArea();
		m_termsAndConditionsTxtArea.setCharacterWidth(80);
		m_termsAndConditionsTxtArea.setVisibleLines(50);
		table.setWidget( nextRow, 0, m_termsAndConditionsTxtArea );		
		
		return mainPanel;
	}
	
	/**
	 * Return the terms and conditions the user defined
	 */
	private String getTermsAndConditions()
	{
		return m_termsAndConditionsTxtArea.getText();
	}

	/**
	 * Issue an rpc request to get the zone share terms and conditions from the server.
	 */
	private void getTermsAndConditionsFromServer()
	{
		AsyncCallback<VibeRpcResponse> rpcCallback;

		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				m_shareDlg.hideStatusMsg();
				Window.alert("Failed "+caught.getMessage());
				GwtClientHelper.handleGwtRPCFailure(
												caught,
												GwtTeaming.getMessages().rpcFailure_GetZoneShareTerms() );
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				Object obj;
				Scheduler.ScheduledCommand cmd;
				obj = result.getResponseData();
				if ( obj != null && obj instanceof ZoneShareTerms )
				{
					m_zoneShareTerms = (ZoneShareTerms) obj;
				}


				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						initTermsAndConditions();
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
				
				m_shareDlg.hideStatusMsg();
				m_shareDlg.setOkEnabled( true );
			}						
		};
		
		// Issue an rpc request to read the terms and conditions
		{
			GetZoneShareTermsCmd cmd;

			m_shareDlg.showStatusMsg( GwtTeaming.getMessages().editZoneShareTermsTab_ReadingLicensingTerms() );
			m_shareDlg.setOkEnabled( false );

			cmd = new GetZoneShareTermsCmd();
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}	
	
	/**
	 * 
	 */
	@Override
	public void init()
	{
		m_shareDlg.clearErrorPanel();
		m_shareDlg.hideErrorPanel();
		m_shareDlg.hideStatusMsg();
		m_shareDlg.setOkEnabled( true );
		m_zoneShareTerms = null;
		
		// Issue an rpc request to get the zone share terms.
		getTermsAndConditionsFromServer();
	}

	/**
	 * 
	 */
	private void initTermsAndConditions()
	{
		if(m_zoneShareTerms == null || m_zoneShareTerms.getTermsAndConditions() == null) return;
		m_termsAndConditionsTxtArea.setText(new HTML(m_zoneShareTerms.getTermsAndConditions()).getText());			
	}
	
	/**
	 * Called when the dialog is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
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
	public void onDetach()
	{
		// Let the widget detach and then unregister our event handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we haven't allocated a list to track events we've registered yet...
		if ( null == m_registeredEventHandlers )
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() )
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
											GwtTeaming.getEventBus(),
											REGISTERED_EVENTS,
											this,
											m_registeredEventHandlers );
		}
	}
	
	/**
	 * Called if the user OKs the dialog.
	 * 
	 * Implements the EditZoneShareTabBase.save() method.
	 */
	@Override
	public void save( EditZoneShareTabCallback callback )
	{
		m_shareDlg.clearErrorPanel();
		m_shareDlg.hideErrorPanel();

		// Disable the Ok button.
		m_shareDlg.setOkEnabled( false );

		// Issue an rpc request to save the share terms and conditions.  If the rpc request is successful,
		// close this dialog.
		saveTermsAndConditionsAndClose( callback );
	}
	
	/**
	 * Issue an rpc request to save the share terms and conditions.  If the save was successful, close the dialog
	 */
	private void saveTermsAndConditionsAndClose( final EditZoneShareTabCallback callback )
	{
		AsyncCallback<VibeRpcResponse> rpcCallback;
		
		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable caught )
			{
				m_shareDlg.hideStatusMsg();
				m_shareDlg.setOkEnabled( true );
				
				GwtClientHelper.handleGwtRPCFailure(
												caught,
												GwtTeaming.getMessages().rpcFailure_SaveZoneShareTerms() );
				
				callback.failure();
			}

			@Override
			public void onSuccess( VibeRpcResponse result )
			{
				m_shareDlg.hideStatusMsg();
				m_shareDlg.setOkEnabled( true );
				
				callback.success();
			}						
		};
		
		{
			SaveZoneShareTermsCmd cmd=new SaveZoneShareTermsCmd();
			ZoneShareTerms terms=new ZoneShareTerms();
			SafeHtml escapedHtml=SafeHtmlUtils.fromString(m_termsAndConditionsTxtArea.getText());
			terms.setTermsAndConditions(escapedHtml.asString());
			cmd.setZoneShareTerms(terms);
			GwtClientHelper.executeCommand(cmd, rpcCallback);
		}
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( ( null != m_registeredEventHandlers ) && ( ! ( m_registeredEventHandlers.isEmpty() ) ) )
		{
			// ...unregister them.  (Note that this will also empty the list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
	
	/**
	 * Called to validate the contents of the tab.
	 * 
	 * Implements the EditZoneShareTabBase.validate() method.
	 */
	@Override
	public void validate(EditZoneShareTabCallback callback) {
		// The terms tab is always considered valid.
		callback.success();
	}
}
