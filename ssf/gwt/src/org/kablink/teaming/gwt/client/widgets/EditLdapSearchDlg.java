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


import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtLdapSearchInfo;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;


/**
 * 
 * @author jwootton
 *
 */
public class EditLdapSearchDlg extends DlgBox
{
	private GwtLdapSearchInfo m_ldapSearch;
	
	private TextBox m_baseDnTextBox;
	private TextArea m_filterTextArea;
	private CheckBox m_searchSubtreeCheckBox;
	private FlowPanel m_homeDirInfoPanel;
	
	
	/**
	 * Callback interface to interact with the "edit ldap search" dialog
	 * asynchronously after it loads. 
	 */
	public interface EditLdapSearchDlgClient
	{
		void onSuccess( EditLdapSearchDlg elsDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private EditLdapSearchDlg(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		EditSuccessfulHandler editSuccessfulHandler )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.OkCancel );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent(
						GwtTeaming.getMessages().editLdapSearchDlg_Header(),
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
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		FlexTable table;
		FlexTable.FlexCellFormatter cellFormatter; 
		int row = 0;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		table = new FlexTable();
		cellFormatter = table.getFlexCellFormatter();
		table.setCellSpacing( 4 );

		mainPanel.add( table );
		
		// Add the base dn controls
		{
			FlowPanel tmpPanel;
			Label label;

			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapSearchDlg_BaseDnLabel() );
			tmpPanel.add( label );
			table.setHTML( row, 0, tmpPanel.getElement().getInnerHTML()  );
			
			m_baseDnTextBox = new TextBox();
			m_baseDnTextBox.setVisibleLength( 40 );
			table.setWidget( row, 1, m_baseDnTextBox );
			++row;
		}

		// Add the filter controls
		{
			FlowPanel tmpPanel;
			Label label;
			
			tmpPanel = new FlowPanel();
			label = new Label( messages.editLdapSearchDlg_FilterLabel() );
			tmpPanel.add( label );
			table.setHTML( row, 0, tmpPanel.getElement().getInnerHTML() );
			
			m_filterTextArea = new TextArea();
			m_filterTextArea.setVisibleLines( 4 );
			m_filterTextArea.setWidth( "650px" );
			table.setWidget( row, 1, m_filterTextArea );
			++row;
		}
		
		// Add a "search subtree" checkbox
		{
			FlowPanel tmpPanel;
			
			tmpPanel = new FlowPanel();
			tmpPanel.getElement().getStyle().setMarginTop( 8, Unit.PX );
			m_searchSubtreeCheckBox = new CheckBox( messages.editLdapSearchDlg_SearchSubtreeLabel() );
			tmpPanel.add( m_searchSubtreeCheckBox );
			
			mainPanel.add( tmpPanel );
		}
		
		// Add the panel that holds the controls that define how to create a home dir net folder
		{
			m_homeDirInfoPanel = new FlowPanel();
			m_homeDirInfoPanel.getElement().getStyle().setMarginTop( 10, Unit.PX );
			m_homeDirInfoPanel.add( new Label( "this is the home dir info panel" ) );
			mainPanel.add( m_homeDirInfoPanel );
		}
		
		return mainPanel;
	}

	/**
	 * Get the data from the controls in the dialog box.
	 */
	@Override
	public Object getDataFromDlg()
	{
		if ( m_ldapSearch != null )
		{
			m_ldapSearch.setBaseDn( m_baseDnTextBox.getValue() );
			m_ldapSearch.setFilter( m_filterTextArea.getValue() );
			m_ldapSearch.setSearchSubtree( m_searchSubtreeCheckBox.getValue() );
		}
		
		return m_ldapSearch;
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
	 * 
	 */
	public void init( GwtLdapSearchInfo ldapSearch, boolean showHomeDirInfoControls )
	{
		m_ldapSearch = ldapSearch;
		
		m_baseDnTextBox.setValue( "" );
		m_filterTextArea.setValue( "" );
		m_searchSubtreeCheckBox.setValue( false );

		m_homeDirInfoPanel.setVisible( showHomeDirInfoControls );

		if ( ldapSearch == null )
			return;
		
		m_baseDnTextBox.setValue( ldapSearch.getBaseDn() );
		m_filterTextArea.setValue( ldapSearch.getFilter() );
		m_searchSubtreeCheckBox.setValue( ldapSearch.getSearchSubtree() );
	}

	/**
	 * Loads the EditLdapSearchDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final int left,
							final int top,
							final EditSuccessfulHandler editSuccessfulHandler,
							final EditLdapSearchDlgClient elsDlgClient )
	{
		GWT.runAsync( EditLdapSearchDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditLdapSearchDlg() );
				if ( elsDlgClient != null )
				{
					elsDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				EditLdapSearchDlg elsDlg;
				
				elsDlg = new EditLdapSearchDlg(
											autoHide,
											modal,
											left,
											top,
											editSuccessfulHandler );
				elsDlgClient.onSuccess( elsDlg );
			}
		});
	}
}
