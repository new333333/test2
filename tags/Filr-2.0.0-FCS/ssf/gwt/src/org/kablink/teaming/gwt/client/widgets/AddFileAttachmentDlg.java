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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.GetModifyBinderUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;

/**
 * ?
 *  
 * @author jwootton
 */
public class AddFileAttachmentDlg extends DlgBox
	implements EditSuccessfulHandler
{
	private FlexTable m_table = null;
	private EditSuccessfulHandler m_editSuccessfulHandler = null;
	private FormPanel m_formPanel = null;
	private String m_binderId = null;
	private int m_uniqueId = 1;
	private AsyncCallback<VibeRpcResponse> m_rpcCallback = null;
	
	/**
	 * 
	 */
	public AddFileAttachmentDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		Object properties )
	{
		super( autoHide, modal, xPos, yPos );
	
		// Replace the editSuccessfulHandler that was passed to us with our own.
		m_editSuccessfulHandler = editSuccessfulHandler;
		
		// Create the callback that will be used when we issue an ajax call to get
		// the "modify binder" url.
		m_rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			@Override
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetBranding(),
					m_binderId );
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				String modifyBinderUrl;
				StringRpcResponseData responseData;
				
				responseData = (StringRpcResponseData) response.getResponseData();
				modifyBinderUrl = responseData.getStringValue();
				
				// Put the "modify binder" url in the form.
				// First add &gwtAddFileAttachment=1 so ModifyBinderController.java will process the "modify binder" request.
				m_formPanel.setAction( modifyBinderUrl + "&gwtAddFileAttachment=1" );
			}// end onSuccess()
		};

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().addFileAttachmentDlgHeader(), this, editCanceledHandler, properties ); 
	}// end AddFileAttachmentDlg()
	

	/**
	 * Add a FileUpload control to the dialog.
	 */
	private FileUpload addFileUploadControl()
	{
		FileUpload fileUpload;
		String name;
		int count;
		
		count = m_table.getRowCount();

		// Add a link the user can use to remove this FileUpload control
		{
			Anchor removeAnchor;
			Image removeImg;
			Element linkElement;
			Element imgElement;
			ClickHandler clickHandler;
			
			removeAnchor = new Anchor();
			removeAnchor.addStyleName( "editBrandingLink" );
			
			// Add a "remove" image to the link.
			removeImg = new Image( GwtTeaming.getImageBundle().delete16() );
			linkElement = removeAnchor.getElement();
			imgElement = removeImg.getElement();
			linkElement.appendChild( imgElement );

			// Add a clickhandler to the "remove file" link.  When the user clicks on the link we
			// will remove the corresponding FileUpload control from the dialog.
			clickHandler = new ClickHandler()
			{
				/**
				 * Invoke the remove the corresponding FileUpload control from this dialog
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					Widget anchor;
					
					// Get the anchor the user clicked on.
					anchor = (Widget) event.getSource();
					
					// Remove the FileUpload control from the dialog.
					removeFileUploadControl( anchor );
					
				}//end onClick()
			};
			removeAnchor.addClickHandler( clickHandler );

			m_table.setWidget( count, 0, removeAnchor );
		}
		
		// Add the FileUpload control after any existing FileUpload controls.
		fileUpload = new FileUpload();
		name = "ss_attachFile" + String.valueOf( m_uniqueId );
		fileUpload.setName( name );
		fileUpload.getElement().setId( name );
		++m_uniqueId;
		m_table.setWidget( count, 1, fileUpload );

		return fileUpload;
	}// end addFileUploadControl()
	

	/**
	 * Clear all the content from the dialog and start fresh.
	 */
	public void clearContent()
	{
		// Remove all the rows that contain FileUpload controls.
		m_table.removeAllRows();
		m_uniqueId = 1;
		
		// Add one FileUpload control to the dialog.
		addFileUploadControl();
	}// end clearContent()
	
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		FlowPanel mainPanel = null;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		m_formPanel = new FormPanel();
		m_formPanel.setEncoding( FormPanel.ENCODING_MULTIPART );
		m_formPanel.setMethod( FormPanel.METHOD_POST );
	
		// Add a handler that will get called when we get the results from the request to
		// add an attachment to the binder.
		m_formPanel.addSubmitCompleteHandler( new SubmitCompleteHandler()
		{
			/**
			 * 
			 */
			@Override
			public void onSubmitComplete( SubmitCompleteEvent event )
			{
				// Tell the dialog box we are finished doing are work
				finishedUploadingFiles();

				// When the form submission is successfully completed, this event is
		        // fired. Assuming the service returned a response of type text/html,
		        // we can get the result text here (see the FormPanel documentation for
		        // further explanation).
				if ( event != null )
				{
					String result;
					
					result = event.getResults();
					if( GwtClientHelper.hasString( result ) )
					{
						if ( result.contains( "ss_error_msg" ) )
						{
							int beginIndex;
							int endIndex;
							String part1;
							String part2;
							String msg;
						
							// The html that we received should look like the following:
							/*
								<script type="text/javascript">
									var ss_error_msg = "Some error message." ;
									var ss_error_code = 1;
								</script>
								
								<div class="ss_style ss_portlet">
									<h1>Error</h1>
								
									<p>Some error message.<br></p>
								
									<br>
									<input value="Back" class="ss_submit" onclick="setTimeout('self.window.history.back();', 2000);" type="button">
								</div>
							 */
							// See defCodedError.jsp.  Note that
							// exceptions must be mapped to this in
							// applicationContext.xml for everything
							// to work properly.
							msg = GwtTeaming.getMessages().unknownFileUploadError( result );
							beginIndex = result.indexOf( "ss_error_msg" );
							if ( beginIndex > 0 )
							{
								endIndex = result.indexOf( ";" );
								if ( endIndex > 0 )
								{
									part1 = result.substring( beginIndex, endIndex );
									
									// Find the starting quote of the error message.
									beginIndex = part1.indexOf( '\'' );
									if ( beginIndex == -1 )
									{
										// Find the starting double quote of the error message.
										beginIndex = part1.indexOf( '"' );
									}
									
									if ( beginIndex >= 0 )
									{
										part2 = part1.substring( beginIndex+1 );
										
										// Find the ending quote of the error message.
										endIndex = part2.indexOf( '\'' );
										if ( endIndex == -1 )
										{
											// Find the ending double quote of the error message
											endIndex = part2.indexOf( '"' );
										}
										
										if ( endIndex >= 0 )
											msg = part2.substring( 0, endIndex );
									}
								}
							}
							Window.alert (msg );
						}
						else
						{
							// Do we have an editSuccessfulHandler?
							if ( m_editSuccessfulHandler != null )
							{
								// Yes, call it.
								m_editSuccessfulHandler.editSuccessful( getDataFromDlg() );
							}
						}
					}
				}
			}
	    }); 

		// Remember the id of the binder we are working with.
		m_binderId = (String) props;
		
		// Issue an ajax request to get the "modify binder" url.
		getModifyBinderUrl();
		
		mainPanel.add( m_formPanel );

		m_table = new FlexTable();
		m_table.setCellSpacing( 4 );
		m_table.addStyleName( "dlgContent" );
		
		// Add a FileUpload control to the dialog.
		addFileUploadControl();
		
		// Add a link the user can click on to add a FileUpload control.
		{
			Anchor addFileAnchor;
			ClickHandler clickHandler;
			MouseOverHandler mouseOverHandler;
			MouseOutHandler mouseOutHandler;
			FlexTable table;
			
			table = new FlexTable();
			
			addFileAnchor = new Anchor( GwtTeaming.getMessages().addFileLabel() );
			addFileAnchor.setTitle( GwtTeaming.getMessages().addFileLabel() );
			addFileAnchor.addStyleName( "editBrandingLink" );
			addFileAnchor.addStyleName( "editBrandingAdvancedLink" );
			addFileAnchor.addStyleName( "subhead-control-bg1" );
			addFileAnchor.addStyleName( "roundcornerSM" );
			
			// Add a clickhandler to the "Add file" link.  When the user clicks on the link we
			// will add a FileUpload control to this dialog.
			clickHandler = new ClickHandler()
			{
				/**
				 * Add a FileUpload control to this dialog
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					addFileUploadControl();
				}//end onClick()
			};
			addFileAnchor.addClickHandler( clickHandler );
			
			// Add a mouse-over handler
			mouseOverHandler = new MouseOverHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onMouseOver( MouseOverEvent event )
				{
					Widget widget;
					
					widget = (Widget)event.getSource();
					widget.removeStyleName( "subhead-control-bg1" );
					widget.addStyleName( "subhead-control-bg2" );
				}// end onMouseOver()
			};
			addFileAnchor.addMouseOverHandler( mouseOverHandler );

			// Add a mouse-out handler
			mouseOutHandler = new MouseOutHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onMouseOut( MouseOutEvent event )
				{
					Widget widget;
					
					// Remove the background color we added to the anchor when the user moved the mouse over the anchor.
					widget = (Widget)event.getSource();
					widget.removeStyleName( "subhead-control-bg2" );
					widget.addStyleName( "subhead-control-bg1" );
				}// end onMouseOut()
			};
			addFileAnchor.addMouseOutHandler( mouseOutHandler );
			table.setWidget( 0, 0, addFileAnchor );
			
			mainPanel.add( table );
		}

		m_formPanel.add( m_table );
		
		init( props );

		return mainPanel;
	}// end createContent()
	
	
	/**
	 * This method gets called when user user presses ok.
	 */
	@Override
	public boolean editSuccessful( Object obj )
	{
		boolean noFilesSelected = true;
		String action;
		
		// Check to see if the user selected any files to upload.
		if ( m_table.getRowCount() > 0 )
		{
			int i;
			
			for (i = 0; i < m_table.getRowCount() && noFilesSelected == true; ++i)
			{
				FileUpload fileUpload;
				String fileName;
				
				// Get the next FileUpload control in the dialog.
				fileUpload = (FileUpload) m_table.getWidget( i, 1 );
				fileName = fileUpload.getFilename();
				if ( fileName != null && fileName.length() > 0 )
					noFilesSelected = false;
			}
		}
			
		// Did the user select any files to upload?
		if ( noFilesSelected )
		{
			// No, tell the user about it.
			Window.alert( GwtTeaming.getMessages().noFilesSelectedMsg() );
			return false;
		}
		
		// Did we set the form's action?
		action = m_formPanel.getAction();
		if ( action != null && action.length() > 0 )
		{
			// Yes
			// Submit the form that will upload the files and add them as attachments to the given binder.
			m_formPanel.submit();
		}
		
		// Returning false will prevent the DlgBox class from closing this dialog.
		return false;
	}// end editSuccessful()

	
	/**
	 * This gets called after we have finished all our work uploading files
	 */
	private void finishedUploadingFiles()
	{
		super.okBtnProcessingEnded();
	}
	
	/**
	 * Return a list of the file names that were selected by the user.
	 */
	@Override
	public Object getDataFromDlg()
	{
		ArrayList<String> listOfFileNames;
		int i;
		
		listOfFileNames = new ArrayList<String>();
		for (i = 0; i < m_table.getRowCount(); ++i)
		{
			FileUpload fileUpload;
			
			// Get the next FileUpload control in the list.
			fileUpload = (FileUpload) m_table.getWidget( i, 1 );
			listOfFileNames.add( fileUpload.getFilename() );
		}
		
		return listOfFileNames;
	}// end getDataFromDlg()
	
	
	/**
	 * We don't have a widget to give the focus to. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}// end getFocusWidget()
	
	
	/**
	 * Issue an ajax request to get the "modify binder" url.
	 */
	private void getModifyBinderUrl()
	{
		GetModifyBinderUrlCmd cmd;
		
		// Issue an ajax request to get the "modify binder" url.
		cmd = new GetModifyBinderUrlCmd( m_binderId );
		GwtClientHelper.executeCommand( cmd, m_rpcCallback );
	}

	
	/**
	 * Initialize the controls in the dialog with the values from the given object.
	 * Currently there is nothing to initialize.
	 */
	public void init( Object props )
	{
	}// end init()
	
	
    /**
     * 
     */
    @Override
	protected void okBtnProcessingEnded()
    {
    	// We don't need to do anything yet because we aren't finished doing what we need to do.
    }
    
	/**
	 * Given a "remove file" anchor, find the corresponding FileUpload control and remove it from the dialog.
	 */
	private void removeFileUploadControl( Widget anchor )
	{
		int row = -1;
		int i;
		
		// Look in the table that holds the FileUpload controls and find the given anchor.
		for (i = 0; i < m_table.getRowCount() && row == -1; ++i)
		{
			Widget tmpWidget;
			
			// Get the widget in the first column of this row.
			tmpWidget = m_table.getWidget( i, 0 );
			
			// Is this the anchor we are looking for?
			if ( tmpWidget == anchor )
			{
				// Yes
				row = i;
			}
		}
		
		// Did we find the row?
		if ( row != -1 )
		{
			// Yes, remove the row from the table.
			m_table.removeRow( row );
		}
		
		// Does the dialog have any FileUpload controls left?
		if ( m_table.getRowCount() == 0 )
		{
			// No, add a FileUpload control.
			addFileUploadControl();
		}
	}// end removeFileUploadControl()
	
	
	/**
	 * Set the id of the binder we are working with.
	 */
	public void setBinderId( String binderId )
	{
		m_binderId = binderId;
		
		// Issue an ajax request to get the url needed to modify this binder.
		getModifyBinderUrl();
	}
}// end AddFileAttachmentDlg
