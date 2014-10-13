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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtEmailPublicLinkResults;
import org.kablink.teaming.gwt.client.GwtFolder;
import org.kablink.teaming.gwt.client.GwtFolderEntry;
import org.kablink.teaming.gwt.client.GwtShareEntryResults;
import org.kablink.teaming.gwt.client.GwtShareItemResult;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent;
import org.kablink.teaming.gwt.client.event.ContentChangedEvent.Change;
import org.kablink.teaming.gwt.client.rpc.shared.EmailPublicLinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFolderCmd;
import org.kablink.teaming.gwt.client.rpc.shared.ShareEntryResultsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.GwtEmailPublicLinkData;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue;
import org.kablink.teaming.gwt.client.util.ShareExpirationValue.ShareExpirationType;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;
import org.kablink.teaming.gwt.client.widgets.ShareThisDlg2.ShareThisDlgMode;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.user.datepicker.client.DatePicker;

/**
 * Implements Vibe's Email Public Link dialog.
 *  
 * @author jwootton@novell.com
 */
@SuppressWarnings("unused")
public class EmailPublicLinkDlg extends DlgBox implements EditSuccessfulHandler
{
	private List<EntityId> m_entityIds;	// List<EntityId> of the entities whose public links are to be e-mailed.
	private Image m_headerImg;
	private Label m_headerNameLabel;
	private Label m_headerPathLabel;
	private Label m_emailHint;
	private Label m_messageHint;
	private TextBox m_emailAddressesTB;
	private TextArea m_messageTA;
	private ShareExpirationWidget m_expirationWidget;

	
	/*
	 * Class constructor.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EmailPublicLinkDlg(
		boolean autoHide,
		boolean modal )
	{
		// Initialize the superclass...
		super( autoHide, modal, DlgButtonMode.OkCancel );

		// ...and create the dialog's content.
		createAllDlgContent(
			"",							// Dialog caption set when the dialog runs.
			this,						// The dialog's EditSuccessfulHandler.
			null,						// The dialog's EditCanceledHandler.
			null );						// Create callback data.  Unused. 
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
	public Panel createContent( Object callbackData )
	{
		GwtTeamingMessages messages;
		VibeFlowPanel mainPanel;
		
		messages = GwtTeaming.getMessages();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "vibe-emailPublicLinkDlg-content" );
		
		// Create the controls needed in the header
		{
			FlowPanel headerPanel;
			FlowPanel namePanel;
			
			headerPanel = new FlowPanel();
			headerPanel.addStyleName( "emailPublicLinkDlg_HeaderPanel" );
		
			m_headerImg = new Image();
			m_headerImg.addStyleName( "emailPublicLinkDlg_HeaderImg" );
			headerPanel.add( m_headerImg );
			
			namePanel = new FlowPanel();
			namePanel.addStyleName( "emailPublicLinkDlg_HeaderNamePanel" );
			
			m_headerNameLabel = new Label();
			m_headerNameLabel.addStyleName( "emailPublicLinkDlg_HeaderNameLabel" );
			namePanel.add( m_headerNameLabel );
			
			m_headerPathLabel = new Label();
			m_headerPathLabel.addStyleName( "emailPublicLinkDlg_HeaderPathLabel" );
			namePanel.add( m_headerPathLabel );
			
			headerPanel.add( namePanel );
			
			mainPanel.add( headerPanel );
		}
		
		// Add a hint
		{
			Label label;
			
			label = new Label( messages.emailPublicLinkDlg_Hint1() );
			label.addStyleName( "emailPublicLinkDlg_Hint" );
			label.getElement().getStyle().setMarginTop( 6, Unit.PX );
			mainPanel.add( label );
			
		}

		// Add the control for entering the email addresses.
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.getElement().getStyle().setPosition( Position.RELATIVE );
			m_emailAddressesTB = new TextBox();
			m_emailAddressesTB.setVisibleLength( 75 );
			m_emailAddressesTB.getElement().getStyle().setMarginTop( 10, Unit.PX );
			m_emailAddressesTB.addKeyPressHandler( new KeyPressHandler()
			{
				@Override
				public void onKeyPress( KeyPressEvent event )
				{
					// Hide the hint
					m_emailHint.setVisible( false );
				}
			});
			panel.add( m_emailAddressesTB );
			
			// Create a hint we will put over the top of the text box
			m_emailHint = new Label( messages.emailPublicLinkDlg_EmailHint() );
			m_emailHint.addStyleName( "emailPublicLinkDlg_FloatingHint" );
			m_emailHint.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							m_emailAddressesTB.setFocus( true );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} ); 
			panel.add( m_emailHint );
			
			mainPanel.add( panel );
		}
		
		// Add the control for entering the message that will be sent with the email.
		{
			FlowPanel panel;
			
			panel = new FlowPanel();
			panel.getElement().getStyle().setPosition( Position.RELATIVE );
			m_messageTA = new TextArea();
			m_messageTA.addStyleName( "emailPublicLinkDlg_MessageBox" );
			m_messageTA.getElement().getStyle().setMarginTop( 6, Unit.PX );
			m_messageTA.setCharacterWidth( 75 );
			m_messageTA.setVisibleLines( 10 );
			m_messageTA.addKeyPressHandler( new KeyPressHandler()
			{
				@Override
				public void onKeyPress( KeyPressEvent event )
				{
					// Hide the hint
					m_messageHint.setVisible( false );
				}
			});
			panel.add( m_messageTA );
			
			// Create a hint we will put over the top of the text area
			m_messageHint = new Label( messages.emailPublicLinkDlg_MessageHint() );
			m_messageHint.addStyleName( "emailPublicLinkDlg_FloatingHint" );
			m_messageHint.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							m_messageTA.setFocus( true );
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			} );
			panel.add( m_messageHint );
			
			mainPanel.add( panel );
		}
		
		// Add the expiration controls.
		createExpirationContent( mainPanel );
		
		return mainPanel;
	}

	/**
	 * 
	 */
	private void createExpirationContent( VibeFlowPanel panel )
	{
		VibeFlowPanel mainPanel;

		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "emailPublicLinkDlg_expirationPanel" );
		
		m_expirationWidget = new ShareExpirationWidget();
		mainPanel.add( m_expirationWidget );

		panel.add( mainPanel );
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
	public boolean editSuccessful( Object data )
	{
		EmailPublicLinkCmd cmd;
		AsyncCallback<VibeRpcResponse> callback;
		
		if ( data == null || (data instanceof GwtEmailPublicLinkData) == false )
			return false;

		callback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( final Throwable caught )
			{
				Scheduler.ScheduledCommand ofCmd;
				
				ofCmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						GwtClientHelper.handleGwtRPCFailure(
								caught,
								GwtTeaming.getMessages().rpcFailure_ShareEntry() );
	
						// Enable the Ok button.
						Window.alert( GwtTeaming.getMessages().rpcFailure_EmailPublicLink() );
						hideStatusMsg();
						setOkEnabled( true );
					}
				};
				Scheduler.get().scheduleDeferred( ofCmd );
			}

			@Override
			public void onSuccess( final VibeRpcResponse vibeResult )
			{
				Scheduler.ScheduledCommand osCmd;
				
				osCmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						GwtEmailPublicLinkResults results;
						String[] errorMessages;
						FlowPanel errorPanel;

						results = (GwtEmailPublicLinkResults) vibeResult.getResponseData();
						
						// Get the panel that holds the errors.
						errorPanel = getErrorPanel();
						errorPanel.clear();
						
						// Were there any errors?
						errorMessages = results.getErrors();
						if ( errorMessages != null && errorMessages.length > 0 )
						{
							// Yes
							// Add each error message to the error panel.
							{
								Label label;
								
								label = new Label( GwtTeaming.getMessages().shareErrors() );
								label.addStyleName( "dlgErrorLabel" );
								errorPanel.add( label );
								
								for ( String nextErrMsg : errorMessages )
								{
									label = new Label( nextErrMsg );
									label.addStyleName( "bulletListItem" );
									errorPanel.add( label );
								}
							}

							// Make the error panel visible.
							showErrorPanel();

							// Enable the Ok button.
							hideStatusMsg();
							setOkEnabled( true );
						}
						else
						{
							// Close this dialog.
							hide();
						}
					}
				};
				Scheduler.get().scheduleDeferred( osCmd );
			}				
		};

		// Disable the Ok button.
		showStatusMsg( GwtTeaming.getMessages().emailPublicLinkDlg_SendingEmails() );
		setOkEnabled( false );

		cmd = new EmailPublicLinkCmd();
		cmd.setEmailPublicLinkData( (GwtEmailPublicLinkData) data );
		
		GwtClientHelper.executeCommand( cmd, callback );

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
	public Object getDataFromDlg()
	{
		GwtEmailPublicLinkData data;
		ArrayList<String> listOfEmailAddresses = null;
		
		data = new GwtEmailPublicLinkData();
		
		// Did the user enter any email addresses?
		{
			listOfEmailAddresses = getEmailAddresses();
			if ( listOfEmailAddresses == null || listOfEmailAddresses.size() == 0 )
			{
				Scheduler.ScheduledCommand cmd;
				
				Window.alert( GwtTeaming.getMessages().emailPublicLinkDlg_NoEmailAddresses() );
				
				cmd = new Scheduler.ScheduledCommand()
				{
					@Override
					public void execute()
					{
						m_emailAddressesTB.setFocus( true );
					}
				};
				Scheduler.get().scheduleDeferred( cmd );
				
				return null;
			}
		}
		
		// Is the expiration value ok?
		if ( m_expirationWidget.validateExpirationValue() == false )
			return null;

		data.setListOfEntityIds( m_entityIds );
		data.setListOfEmailAddresses( listOfEmailAddresses );
		data.setMessage( m_messageTA.getValue() );
		data.setExpirationValue( m_expirationWidget.getExpirationValue() );
		
		return data;
	}

	/**
	 * Return the email addresses entered by the user.
	 */
	private ArrayList<String> getEmailAddresses()
	{
		ArrayList<String> listOfEmailAddresses;
		String text;
		
		listOfEmailAddresses = new ArrayList<String>();
		
		text = m_emailAddressesTB.getValue();
		if ( text != null && text.length() > 0 )
		{
			String[] emailAddresses;
			
			// Separate the email addresses using ',' or ' ' as a delimiter
			emailAddresses = text.split( "[, ]" );
			if ( emailAddresses != null && emailAddresses.length > 0 )
			{
				for ( String nextEmailAddr : emailAddresses )
				{
					nextEmailAddr = nextEmailAddr.trim();
					if ( nextEmailAddr.length() > 0 )
						listOfEmailAddresses.add( nextEmailAddr );
				}
			}
			else
			{
				text = text.trim();
				if ( text.length() > 0 )
					listOfEmailAddresses.add( text );
			}
		}
		
		return listOfEmailAddresses;
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
								
								// Update the name of the entity in the header.
								m_headerNameLabel.setText( gwtFolderEntry.getEntryName() );
								m_headerPathLabel.setText( gwtFolderEntry.getParentBinderName() );
								m_headerPathLabel.setTitle( gwtFolderEntry.getParentBinderName() );
								
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
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_emailAddressesTB;
	}

	/*
	 * Initialize this dialog
	 */
	public void init( String caption, List<EntityId> entityIds )
	{
		setCaption( caption );
		m_entityIds = entityIds;

		updateHeader();
		hideStatusMsg();
		setOkEnabled( true );
		
		m_emailAddressesTB.setValue( "" );
		m_messageTA.setValue( "" );
		
		// Change the "Ok" button to "Send"
		{
			Button btn;
			
			btn = getOkButton();
			if ( btn != null )
			{
				btn.setText( GwtTeaming.getMessages().emailPublicLinkDlg_SendBtn() );
			}
		}
	}

	/**
	 * 
	 */
	@Override
	public void show( boolean centered )
	{
		Scheduler.ScheduledCommand cmd;
		
		super.show( centered );
		
		cmd = new Scheduler.ScheduledCommand()
		{
			@Override
			public void execute()
			{
				// Make the text area for the email message the same width as the text box for email addresses
				{
					int width;
					
					width = m_emailAddressesTB.getOffsetWidth();
					m_messageTA.getElement().getStyle().setWidth( width-10, Unit.PX );
				}
				
				// Show the email address hint
				{
					int left;
					int top;
					Style style;
					
					left = m_emailAddressesTB.getAbsoluteLeft() - m_emailAddressesTB.getParent().getAbsoluteLeft();
					top = m_emailAddressesTB.getAbsoluteTop() - m_emailAddressesTB.getParent().getAbsoluteTop();
					
					style = m_emailHint.getElement().getStyle(); 
					style.setLeft( left+13, Unit.PX );
					style.setTop( top+6, Unit.PX );
					
					m_emailHint.setVisible( true );
				}

				// Show the email message hint
				{
					int left;
					int top;
					Style style;
					
					left = m_messageTA.getAbsoluteLeft() - m_messageTA.getParent().getAbsoluteLeft();
					top = m_messageTA.getAbsoluteTop() - m_messageTA.getParent().getAbsoluteTop();
					
					style = m_messageHint.getElement().getStyle(); 
					style.setLeft( left+13, Unit.PX );
					style.setTop( top+13, Unit.PX );
					
					m_messageHint.setVisible( true );
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
	
	/**
	 * Update the header that displays the name of the entity we are working with.
	 * If we are dealing with > 1 entity we don't show a header.
	 */
	private void updateHeader()
	{
		ImageResource imgResource;
		int numItems;

		if ( m_entityIds == null )
		{
			m_headerImg.setVisible( false );
			return;
		}
		
		// Are we dealing with > 1 entities?
		numItems = m_entityIds.size();
		if ( numItems == 1 )
		{
			EntityId entityId;
			
			// No
			entityId = m_entityIds.get( 0 );

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
		}
		else
		{
			// We are sharing multiple items.  Use the entry image.
			imgResource = GwtTeaming.getFilrImageBundle().entry_large();
			
			m_headerNameLabel.setText( GwtTeaming.getMessages().sharingMultipleItems( numItems ) );
			
			// Put a non-breaking space in the path so that it gets a
			// height.  This fixes the layout so that the header
			// doesn't overlap a make public button.
			m_headerPathLabel.getElement().setInnerHTML( "&nbsp;" );
		}

		m_headerImg.setVisible( true );
		m_headerImg.setResource( imgResource );
	}

	
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	/* The following code is used to load the split point containing */
	/* the e-mail public link dialog and perform some operation on   */
	/* it.                                                           */
	/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
	
	/**
	 * Callback interface to interact with the e-mail public link
	 * dialog asynchronously after it loads. 
	 */
	public interface EmailPublicLinkDlgClient {
		void onSuccess(EmailPublicLinkDlg eplDlg);
		void onUnavailable();
	}

	/**
	 * Loads the EmailPublicLinkDlg split point and returns an instance of it
	 * via the callback.
	 * 
	 * @param eplDlgClient
	 */
	public static void createAsync(
		final boolean autoHide,
		final boolean modal,
		final EmailPublicLinkDlgClient eplDlgClient )
	{
		GWT.runAsync( EmailPublicLinkDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure(Throwable reason)
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EmailPublicLinkDlg() );
				if ( null != eplDlgClient )
				{
					eplDlgClient.onUnavailable();
				}
			}

			@Override
			public void onSuccess()
			{
				// Is this a request to create a dialog?
				if ( null != eplDlgClient )
				{
					EmailPublicLinkDlg eplDlg;

					eplDlg = new EmailPublicLinkDlg( autoHide, modal );
					eplDlgClient.onSuccess( eplDlg );
				}
			}
		});
	}
}
