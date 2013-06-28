/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.mainmenu.TeamInfo;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author jwootton
 *
 */
public class ShareWithTeamsDlg extends DlgBox
{
	private FlowPanel m_myTeamsPanel;
	
	/**
	 * Callback interface to interact with the "Share with teams" dialog asynchronously after it loads. 
	 */
	public interface ShareWithTeamsDlgClient
	{
		void onSuccess( ShareWithTeamsDlg cwtDlg );
		void onUnavailable();
	}

	/**
	 * This class is a checkbox with a TeamInfo object associated with it.
	 */
	public class TeamCheckBox extends Composite
	{
		private TeamInfo m_teamInfo;
		private CheckBox m_checkbox;
		
		/**
		 * 
		 */
		public TeamCheckBox( TeamInfo teamInfo, String label )
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			m_checkbox = new CheckBox( label );
			m_checkbox.addStyleName( "fontSize75em" );
			panel.add( m_checkbox );
			
			m_teamInfo = teamInfo;
			
			initWidget( panel );
		}
		
		/**
		 * 
		 */
		public TeamInfo getTeamInfo()
		{
			return m_teamInfo;
		}
		
		/**
		 * 
		 */
		public Boolean getValue()
		{
			return m_checkbox.getValue();
		}
	}

	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ShareWithTeamsDlg(
		boolean autoHide,
		boolean modal,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		super( autoHide, modal );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().shareWithTeamsDlg_caption(),
						editSuccessfulHandler,
						null,
						null ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		VibeFlowPanel mainPanel;
		Label label;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		label = new Label( GwtTeaming.getMessages().shareWithTeamsDlg_Instructions() );
		label.addStyleName( "shareWithTeamsDlg_InstructionsLabel" );
		mainPanel.add( label );
		
		m_myTeamsPanel = new FlowPanel();
		m_myTeamsPanel.addStyleName( "shareWithTeamsDlg_MyTeamsPanel" );
		mainPanel.add( m_myTeamsPanel );

		return mainPanel;
	}
	
	
	/**
	 * Get a list of the selected teams
	 */
	@Override
	public Object getDataFromDlg()
	{
		int i;
		ArrayList<TeamInfo> listOfSelectedTeams;
		
		listOfSelectedTeams = new ArrayList<TeamInfo>();
		
		for (i = 0; i < m_myTeamsPanel.getWidgetCount(); ++i)
		{
			Widget nextWidget;
			
			// Get the next widget in the "my teams" panel.
			nextWidget = m_myTeamsPanel.getWidget( i );
			
			// Is this widget a TeamCheckbox widget? 
			if ( nextWidget instanceof TeamCheckBox )
			{
				TeamCheckBox teamCheckbox;
				
				// Yes, is the team selected?
				teamCheckbox = (TeamCheckBox) nextWidget;
				if ( teamCheckbox.getValue() == Boolean.TRUE )
				{
					// Yes, add it to the list.
					listOfSelectedTeams.add( teamCheckbox.getTeamInfo() );
				}
			}
		}

		return listOfSelectedTeams;
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}

	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( List<TeamInfo> listOfTeams )
	{
		// Clear any previous list of teams
		m_myTeamsPanel.clear();
		
		if ( listOfTeams != null )
		{
			for ( TeamInfo nextTeamInfo : listOfTeams )
			{
				TeamCheckBox checkbox;

				// Create a checkbox for this team.
				checkbox = new TeamCheckBox( nextTeamInfo, nextTeamInfo.getTitle() );
				m_myTeamsPanel.add( checkbox );
			}
		}
	}
	
	/**
	 * Loads the ShareWithTeamsDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final EditSuccessfulHandler editSuccessfulHandler,
							final ShareWithTeamsDlgClient swtDlgClient )
	{
		GWT.runAsync( ShareWithTeamsDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ShareWithTeamsDlg() );
				if ( swtDlgClient != null )
				{
					swtDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ShareWithTeamsDlg swtDlg;
				
				swtDlg = new ShareWithTeamsDlg( autoHide, modal, editSuccessfulHandler );
				swtDlgClient.onSuccess( swtDlg );
			}
		});
	}
}
