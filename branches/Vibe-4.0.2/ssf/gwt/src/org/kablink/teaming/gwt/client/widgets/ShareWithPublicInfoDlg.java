/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetDownloadFileUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntityPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;

/**
 * ?
 *  
 * @author jwootton
 */
public class ShareWithPublicInfoDlg extends DlgBox
{
	private TextBox m_viewEntryPermalinkTextBox;
	private TextBox m_downloadFilePermalinkTextBox;
	private Label m_instructions1;
	private Label m_instructions2;
	private Image m_headerImg;
	private Label m_headerNameLabel;
	private Label m_headerPathLabel;

	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] 
	{
	};
	

	/**
	 * Callback interface to interact with the "share with public info" dialog
	 * asynchronously after it loads. 
	 */
	public interface ShareWithPublicInfoDlgClient
	{
		void onSuccess( ShareWithPublicInfoDlg swpiDlg );
		void onUnavailable();
	}

	
	/**
	 * 
	 */
	private ShareWithPublicInfoDlg()
	{
		super( false, true, DlgButtonMode.Close );
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers(
									GwtTeaming.getEventBus(),
									m_registeredEvents,
									this );
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().shareWithPublicInfoDlg_Header(), null, null, null );
	}

	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		GwtTeamingMessages messages;
		FlowPanel mainPanel = null;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		mainPanel.setStyleName( "teamingDlgBoxContentOverride" );

		// Create the controls needed in the header
		{
			FlowPanel headerPanel;
			FlowPanel namePanel;
			
			headerPanel = new FlowPanel();
			headerPanel.addStyleName( "shareThisDlg_HeaderPanel" );
		
			m_headerImg = new Image();
			m_headerImg.addStyleName( "shareThisDlg_HeaderImg" );
			headerPanel.add( m_headerImg );
			
			namePanel = new FlowPanel();
			namePanel.addStyleName( "shareThisDlg_HeaderNamePanel" );
			
			m_headerNameLabel = new Label();
			m_headerNameLabel.addStyleName( "shareThisDlg_HeaderNameLabel" );
			namePanel.add( m_headerNameLabel );
			
			m_headerPathLabel = new Label();
			m_headerPathLabel.addStyleName( "shareThisDlg_HeaderPathLabel" );
			namePanel.add( m_headerPathLabel );
			
			headerPanel.add( namePanel );
			
			mainPanel.add( headerPanel );
		}
		
		{
			FlowPanel contentPanel;
			
			contentPanel = new FlowPanel();
			contentPanel.addStyleName( "shareWithPublicDlg_content" );

			// Add controls for the view entry permalink
			{
				m_instructions1 = new Label( messages.shareWithPublicInfoDlg_InstructionsEntry() );
				m_instructions1.addStyleName( "shareWithPublicDlg_instructionsLabel" );
				contentPanel.add( m_instructions1 );
				
				m_viewEntryPermalinkTextBox = new TextBox();
				m_viewEntryPermalinkTextBox.setVisibleLength( 60 );
				contentPanel.add( m_viewEntryPermalinkTextBox );
			}
			
			// Add controls for the download file permalink
			{
				m_instructions2 = new Label( messages.shareWithPublicInfoDlg_Instructions3() );
				m_instructions2.addStyleName( "margintop2" );
				m_instructions2.addStyleName( "shareWithPublicDlg_instructionsLabel" );
				contentPanel.add( m_instructions2 );
				
				m_downloadFilePermalinkTextBox = new TextBox();
				m_downloadFilePermalinkTextBox.setVisibleLength( 60 );
				contentPanel.add( m_downloadFilePermalinkTextBox );
			}
			
			mainPanel.add( contentPanel );
		}
		return mainPanel;
	}
	
	/**
	 * 
	 */
	@Override
	public Object getDataFromDlg()
	{
		// We can return anything.
		return Boolean.TRUE;
	}
	
	/**
	 * Issue an rpc request to get the download file permalink.
	 */
	private void getDownloadFilePermalink( final EntityId entityId )
	{
		GetDownloadFileUrlCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;
		
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetDownloadFileUrl(),
												String.valueOf( entityId.getEntityId() ) );
			}
			
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				final String downloadFileUrl;
				StringRpcResponseData responseData;
				Scheduler.ScheduledCommand cmd;

				responseData = (StringRpcResponseData) response.getResponseData();
				downloadFileUrl = responseData.getStringValue();
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						updateDownloadFilePermalink( downloadFileUrl );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
			}
		};
		
		cmd = new GetDownloadFileUrlCmd( null, entityId.getEntityId() );
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * Issue an rpc request to get information about the given entity.
	 */
	private void getEntityInfoFromServer( final EntityId entityId )
	{
		if ( entityId == null )
			return;
		
		// Are we working with a folder entry?
		if ( entityId.isEntry() )
		{
			GetEntryCmd cmd;
			AsyncCallback<VibeRpcResponse> callback;

			// Yes
			callback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetFolderEntry(),
						entityId.getEntityId() );
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					final GwtFolderEntry gwtFolderEntry;
					
					gwtFolderEntry = (GwtFolderEntry) response.getResponseData();
					
					if ( gwtFolderEntry != null )
					{
						Scheduler.ScheduledCommand cmd;
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								String imgUrl;
								
								updateInstructions( gwtFolderEntry.getEntryName(), true );
								
								// Update the name of the entity in the header.
								m_headerNameLabel.setText( gwtFolderEntry.getEntryName() );
								m_headerPathLabel.setText( gwtFolderEntry.getParentBinderName() );

								// Do we have a url for the file image?
								imgUrl = gwtFolderEntry.getFileImgUrl();
								if ( imgUrl != null && imgUrl.length() > 0 )
								{
									m_headerImg.setUrl( GwtClientHelper.getRequestInfo().getImagesPath() + imgUrl );
								}
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				}
			};

			cmd = new GetEntryCmd( null, entityId.getEntityId().toString() );
			GwtClientHelper.executeCommand( cmd, callback );
		}
		else
		{
			GetFolderCmd cmd;
			AsyncCallback<VibeRpcResponse> callback;
			
			callback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetFolder(),
						entityId.getEntityId() );
				}
		
				/**
				 * 
				 * @param result
				 */
				@Override
				public void onSuccess( VibeRpcResponse response )
				{
					final GwtFolder gwtFolder;
					
					gwtFolder = (GwtFolder) response.getResponseData();
					
					if ( gwtFolder != null )
					{
						Scheduler.ScheduledCommand cmd;

						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Update the name of the entity in the header
								updateInstructions( gwtFolder.getFolderName(), false );

								m_headerNameLabel.setText( gwtFolder.getFolderName() );
								m_headerPathLabel.setText( gwtFolder.getParentBinderName() );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				}
			};

			cmd = new GetFolderCmd( null, entityId.getEntityId().toString() );
			GwtClientHelper.executeCommand( cmd, callback );
		}
	}
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_viewEntryPermalinkTextBox;
	}
	
	/**
	 * Issue an rpc request to get the view entry permalink.
	 */
	private void getViewEntryPermalink( final EntityId entityId )
	{
		GetEntityPermalinkCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;

		// Issue an rpc request to get the entity's permalink
		callback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetEntityPermalink(),
												entityId.getEntityId() );
			}
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				StringRpcResponseData responseData;
				final String permalink;
				
				responseData = (StringRpcResponseData) response.getResponseData();
				permalink = responseData.getStringValue();
				
				if ( permalink != null )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							updateViewEntryPermalink( permalink );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};

		cmd = new GetEntityPermalinkCmd( entityId );
		GwtClientHelper.executeCommand( cmd, callback );
	}
	
	/**
	 * 
	 */
	public void init( final EntityId entityId )
	{
		Scheduler.ScheduledCommand cmd;
		
		// Update the header with info about the item.
		updateHeader( entityId );
		
		// Are we dealing with a folder?
		if ( entityId.isBinder() )
		{
			// Yes, hide the controls dealing with the url to download the entity.
			m_instructions2.setVisible( false );
			m_downloadFilePermalinkTextBox.setVisible( false );
		}
		else
		{
			m_instructions2.setVisible( true );
			m_downloadFilePermalinkTextBox.setVisible( true );
		}
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				getViewEntryPermalink( entityId );
				
				if ( entityId.isBinder() == false )
					getDownloadFilePermalink( entityId );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * 
	 */
	private void updateDownloadFilePermalink( String permalink )
	{
		Scheduler.ScheduledCommand cmd;
		
		m_downloadFilePermalinkTextBox.setValue( permalink );
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				m_downloadFilePermalinkTextBox.setCursorPos( 0 );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Update the header that displays the name of the entity we are working with.
	 * If we are dealing with > 1 entity we don't show a header.
	 */
	private void updateHeader( EntityId entityId )
	{
		ImageResource imgResource;

		m_headerNameLabel.setText( "" );
		m_headerPathLabel.setText( "" );

		// Issue an rpc request to get information about this entity
		getEntityInfoFromServer( entityId );

		// Are we dealing with a folder entry?
		if ( entityId .isEntry() )
		{
			// Yes
			imgResource = GwtTeaming.getFilrImageBundle().entry_large();
		}
		else
		{
			// We must be dealing with a binder.
			imgResource = GwtTeaming.getFilrImageBundle().folder_large();
		}

		m_headerImg.setResource( imgResource );
	}
	
	/*
	 * Update the instructions with the name of the item
	 */
	private void updateInstructions( String itemName, boolean itemIsEntry )
	{
		String instructions1;
		if (itemIsEntry)
		     instructions1 = GwtTeaming.getMessages().shareWithPublicInfoDlg_InstructionsEntry();
		else instructions1 = GwtTeaming.getMessages().shareWithPublicInfoDlg_InstructionsFolder();
		m_instructions1.setText( instructions1 );
		m_instructions2.setText( GwtTeaming.getMessages().shareWithPublicInfoDlg_Instructions3() );
	}
	
	/**
	 * 
	 */
	private void updateViewEntryPermalink( String permalink )
	{
		Scheduler.ScheduledCommand cmd;
		
		m_viewEntryPermalinkTextBox.setValue( permalink );
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				m_viewEntryPermalinkTextBox.setFocus( true );
				m_viewEntryPermalinkTextBox.setCursorPos( 0 );
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Loads the ShareWithPublicInfoDlg split point and returns an instance
	 * of it via the callback.
	 * 
	 */
	public static void createAsync( final ShareWithPublicInfoDlgClient swpiDlgClient )
	{
		GWT.runAsync( ShareWithPublicInfoDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ShareWithPublicInfoDlg() );
				if ( swpiDlgClient != null )
				{
					swpiDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				ShareWithPublicInfoDlg swpiDlg;
				
				swpiDlg = new ShareWithPublicInfoDlg();
				swpiDlgClient.onSuccess( swpiDlg );
			}
		});
	}
}
