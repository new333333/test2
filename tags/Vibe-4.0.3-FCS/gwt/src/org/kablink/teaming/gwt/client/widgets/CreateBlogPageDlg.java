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

import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.BlogPageCreatedEvent;
import org.kablink.teaming.gwt.client.rpc.shared.AddNewFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.CreateFolderRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.ErrorListRpcResponseData.ErrorInfo;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * ?
 *  
 * @author jwootton
 */
public class CreateBlogPageDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private TextBox	m_nameTxtBox;
	private Long m_folderId;			// The binder the new blog page is to be added to.
	private Long m_folderTemplateId;	// The ID of the folder template to use to create the blog page

	/**
	 * Callback interface to interact with the "create blog page" dialog
	 * asynchronously after it loads. 
	 */
	public interface CreateBlogPageDlgClient
	{
		void onSuccess( CreateBlogPageDlg cbpDlg );
		void onUnavailable();
	}


	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private CreateBlogPageDlg(
		boolean autoHide,
		boolean modal )
	{
		super( autoHide, modal );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().createBlogPageDlg_caption(), this, null, null ); 
	}
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		Label label;
		VibeFlowPanel mainPanel;
		FlexTable table;
		
		mainPanel = new VibeFlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		// Add label and edit control for "Blog page name"
		table = new FlexTable();
		table.setCellSpacing( 2 );
		label = new Label( GwtTeaming.getMessages().createBlogPageDlg_newPageNameLabel() );
		table.setWidget( 0, 0, label );
		m_nameTxtBox = new TextBox();
		m_nameTxtBox.setVisibleLength( 30 );
		m_nameTxtBox.addKeyPressHandler( new KeyPressHandler()
		{
			@Override
			public void onKeyPress( KeyPressEvent event )
			{
				int key;

				// Was the enter key pressed?
				key = event.getNativeEvent().getKeyCode();
				if ( KeyCodes.KEY_ENTER == key )
				{
					Scheduler.ScheduledCommand cmd;
					
					// Yes, act as if the ok button was pressed.
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							editSuccessful( null );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		});
		table.setWidget( 1, 0, m_nameTxtBox );
		mainPanel.add( table );
		
		mainPanel.add( table );

		return mainPanel;
	}
	
	
	/**
	 * This method gets called when the user presses ok.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		String pageName;
		
		pageName = m_nameTxtBox.getValue();
		if ( pageName == null || pageName.length() == 0 )
		{
			// Tell the user they must enter a blog page name.
			// No!  Tell them about the error and bail.
			GwtClientHelper.deferredAlert( GwtTeaming.getMessages().createBlogPageDlg_noNameSpecified() );
			return false;
		}
		
		// Issue an ajax request to create the new blog page.
		issueCreateBlogPageRequest( pageName );
		
		// Always return false to leave the dialog open.  If it's
		// contents validate and the blog page gets created, the dialog
		// will then be closed.
		return false;
	}
	
	/**
	 * Get the data from the controls in the dialog box and store the data in the properties obj.
	 */
	@Override
	public Object getDataFromDlg()
	{
		return "";
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_nameTxtBox;
	}
	
	
	/**
	 * Initialize the controls in the dialog with the values from the properties
	 */
	public void init( Long folderId, Long folderTemplateId )
	{
		m_folderId = folderId;
		m_folderTemplateId = folderTemplateId;
	}

	/**
	 * Issue an ajax request to create the blog page.
	 */
	private void issueCreateBlogPageRequest( final String pageName )
	{
		if ( pageName != null && pageName.length() > 0 && m_folderId != null && m_folderTemplateId != null )
		{
			AddNewFolderCmd cmd;

			// Issue an rpc request to create a new folder.
			cmd = new AddNewFolderCmd( m_folderId, m_folderTemplateId, pageName );
			GwtClientHelper.executeCommand(
									cmd,
									new AsyncCallback<VibeRpcResponse>() 
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_CreateBlogPage(),
						pageName );
				}
				
				/**
				 * 
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					CreateFolderRpcResponseData responseData;
					List<ErrorInfo> errors;

					// Were there any errors returned from the create folder request?
					responseData = ((CreateFolderRpcResponseData) response.getResponseData());
					errors = responseData.getErrorList();
					if ( errors != null && errors.size() > 0 )
					{
						// Yes!  Display them.
						GwtClientHelper.displayMultipleErrors( GwtTeaming.getMessages().createBlogPageDlg_createFailed(), errors );
					}
					else
					{
						BlogPageCreatedEvent event;

						// No, there weren't any errors.
						// Fire the BlogPageCreatedEvent so interested parties will know
						// that we just created a new blog page.
						event = new BlogPageCreatedEvent(
													responseData.getFolderId(),
													responseData.getFolderName() );
						GwtTeaming.fireEvent( event );
						
						// Close the dialog.
						hide();
					}
				}
			});
		}
	}
	
	/**
	 * Loads the CreateBlogPageDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync(
							final boolean autoHide,
							final boolean modal,
							final CreateBlogPageDlgClient cbpDlgClient )
	{
		GWT.runAsync( CreateBlogPageDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_CreateBlogPageDlg() );
				if ( cbpDlgClient != null )
				{
					cbpDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				CreateBlogPageDlg cbpDlg;
				
				cbpDlg = new CreateBlogPageDlg( autoHide, modal );
				cbpDlgClient.onSuccess( cbpDlg );
			}
		});
	}
}
