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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;


/**
 * 
 * @author jwootton
 *
 */
public class EditBrandingDlg extends DlgBox
{
	private RadioButton m_useBrandingImgRb;
	private RadioButton m_useAdvancedBrandingRb;
	private ListBox m_brandingImgListbox;
	private ListBox m_backgroundImgListbox;
	private RadioButton m_noRepeatRb;
	private RadioButton m_repeatRb;
	private RadioButton m_repeatXRb;
	private RadioButton m_repeatYRb;
	private TextBox m_backgroundColorTextbox;
	private TextBox m_textColorTextbox;
	private TextBox m_sampleText;
	private AsyncCallback<ArrayList<String>> m_rpcReadCallback = null;
	private AsyncCallback<Boolean> m_rpcSaveCallback = null;
	private GwtBrandingData m_origBrandingData;		// The original branding data we started with.
	private final String m_noAvailableImages = "no available images";
	private final String m_noImage = "no image";

	/**
	 * 
	 */
	public EditBrandingDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		GwtBrandingData brandingData ) // Where properties used in the dialog are read from.
	{
		super( autoHide, modal, xPos, yPos );
		
		// Remember the branding data we started with.
		m_origBrandingData = brandingData;
		
		// Create the callback that will be used when we issue an ajax call to get
		// the list of files attached to the given binder.
		m_rpcReadCallback = new AsyncCallback<ArrayList<String>>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				String errMsg;
				String cause;
				GwtTeamingMessages messages;
				
				messages = GwtTeaming.getMessages();
				
				if ( t instanceof GwtTeamingException )
				{
					ExceptionType type;
				
					// Determine what kind of exception happened.
					type = ((GwtTeamingException)t).getExceptionType();
					if ( type == ExceptionType.ACCESS_CONTROL_EXCEPTION )
						cause = messages.errorAccessToFolderDenied( m_origBrandingData.getBinderId() );
					else if ( type == ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION )
						cause = messages.errorFolderDoesNotExist( m_origBrandingData.getBinderId() );
					else
						cause = messages.errorUnknownException();
				}
				else
				{
					cause = t.getLocalizedMessage();
					if ( cause == null )
						cause = t.toString();
				}
				
				errMsg = messages.getBrandingRPCFailed( cause );
				Window.alert( errMsg );
				
				// Update the list of files the user can select from for the branding image and background image.
				updateListOfFileAttachments( null );
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( ArrayList<String> listOfFileAttachments )
			{
				// Update the list of files the user can select from for the branding image and background image.
				updateListOfFileAttachments( listOfFileAttachments );
			}// end onSuccess()
		};
		
		// Create the callback that will be used when we issue an ajax call to save the branding data.
		m_rpcSaveCallback = new AsyncCallback<Boolean>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				String errMsg;
				String cause;
				GwtTeamingMessages messages;
				
				messages = GwtTeaming.getMessages();
				
				if ( t instanceof GwtTeamingException )
				{
					ExceptionType type;
				
					// Determine what kind of exception happened.
					type = ((GwtTeamingException)t).getExceptionType();
					if ( type == ExceptionType.ACCESS_CONTROL_EXCEPTION )
						cause = messages.errorAccessToFolderDenied( m_origBrandingData.getBinderId() );
					else if ( type == ExceptionType.NO_BINDER_BY_THE_ID_EXCEPTION )
						cause = messages.errorFolderDoesNotExist( m_origBrandingData.getBinderId() );
					else
						cause = messages.errorUnknownException();
				}
				else
				{
					cause = t.getLocalizedMessage();
					if ( cause == null )
						cause = t.toString();
				}
				
				errMsg = messages.getBrandingRPCFailed( cause );
				Window.alert( errMsg );
			}// end onFailure()
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( Boolean result )
			{
				// Nothing to do.
			}// end onSuccess()
		};
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().brandingDlgHeader(), editSuccessfulHandler, editCanceledHandler, brandingData ); 
	}// end EditBrandingDlg()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		FlowPanel mainPanel = null;
		FlexTable table = null;
		GwtBrandingData brandingData;
		HorizontalPanel hPanel;
		int nextRow;
		
		brandingData = (GwtBrandingData) props;

		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		nextRow = 0;
		
		// Add the controls for "Use Branding image"
		{
			m_useBrandingImgRb = new RadioButton( "brandingType", GwtTeaming.getMessages().useBrandingImgLabel() );
			table.setWidget( nextRow, 0, m_useBrandingImgRb );

			// Create a list box to hold the list of attachments for the given binder.
			// The user can select one of these files to use as the branding image.
			m_brandingImgListbox = new ListBox( false );
			m_brandingImgListbox.setVisibleItemCount( 1 );
			table.setWidget( nextRow, 1, m_brandingImgListbox );
			++nextRow;
		}
		
		// Add the controls for "Use Advanced Branding"
		{
			m_useAdvancedBrandingRb = new RadioButton( "brandingType", GwtTeaming.getMessages().useAdvancedBrandingLabel() );
			table.setWidget( nextRow, 0, m_useAdvancedBrandingRb );
			++nextRow;
		}
		
		// Add the controls for "Background Image"
		{
			table.setText( nextRow, 0, GwtTeaming.getMessages().backgroundImgLabel() );

			// Create a list box to hold the list of attachments for the given binder.
			// User user can select of of these files to use as the background image.
			m_backgroundImgListbox = new ListBox( false );
			m_backgroundImgListbox.setVisibleItemCount( 1 );
			m_backgroundImgListbox.addStyleName( "marginTop5" );
			table.setWidget( nextRow, 1, m_backgroundImgListbox );
			++nextRow;
			
			// Add the radio buttons that will be used to select how the background image repeats.
			m_noRepeatRb = new RadioButton( "backgroundRepeat", GwtTeaming.getMessages().noRepeatLabel() );
			m_repeatRb = new RadioButton( "backgroundRepeat", GwtTeaming.getMessages().repeatLabel() );
			m_repeatXRb = new RadioButton( "backgroundRepeat", GwtTeaming.getMessages().repeatXLabel() );
			m_repeatYRb = new RadioButton( "backgroundRepeat", GwtTeaming.getMessages().repeatYLabel() );
			hPanel = new HorizontalPanel();
			hPanel.add( m_noRepeatRb );
			hPanel.add( m_repeatRb );
			hPanel.add( m_repeatXRb );
			hPanel.add( m_repeatYRb );
			table.setWidget( nextRow, 1, hPanel );
			++nextRow;
		}
		
		// Add the controls for "Background color"
		{
			KeyUpHandler keyUpHandler;
			
			table.setText( nextRow, 0, GwtTeaming.getMessages().backgroundColorLabel() );

			// Add a text box where the user can enter the background color
			m_backgroundColorTextbox = new TextBox();
			m_backgroundColorTextbox.setVisibleLength( 20 );
			table.setWidget( nextRow, 1, m_backgroundColorTextbox );
			
			// Add a keyup handler so we can update the "sample text" background color.
			keyUpHandler = new KeyUpHandler()
			{
				/**
				 * Update the background color of the sample text.
				 */
				public void onKeyUp( KeyUpEvent event )
				{
					updateSampleTextBgColor();
				}// end onKeyUp()
			};
			m_backgroundColorTextbox.addKeyUpHandler( keyUpHandler );
			
			++nextRow;
		}
		
		// Add the controls for "Text color"
		{
			KeyUpHandler keyUpHandler;
			
			table.setText( nextRow, 0, GwtTeaming.getMessages().textColorLabel() );

			// Add a text box where the user can enter the font color.
			m_textColorTextbox = new TextBox();
			m_textColorTextbox.setVisibleLength( 20 );
			table.setWidget( nextRow, 1, m_textColorTextbox );
			
			// Add a keyup handler so we can update the "sample text" color
			keyUpHandler = new KeyUpHandler()
			{
				/**
				 * Update the text color of the sample text.
				 */
				public void onKeyUp( KeyUpEvent event )
				{
					updateSampleTextColor();
				}//end onKeyUp()
			};
			m_textColorTextbox.addKeyUpHandler( keyUpHandler );
			
			++nextRow;
		}
		
		// Add a "sample text" field that will display the selected background color and text color.
		{
			m_sampleText = new TextBox();
			m_sampleText.setText( GwtTeaming.getMessages().sampleText() );
			m_sampleText.setReadOnly( true );
			table.setWidget( nextRow, 1, m_sampleText );
			++nextRow;
		}
		
		mainPanel.add( table );
		
		init( brandingData );

		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtBrandingData obj.
	 */
	public Object getDataFromDlg()
	{
		GwtBrandingData brandingData;
		String imgName;
		int index;
		
		brandingData = new GwtBrandingData();
		brandingData.setBinderId( m_origBrandingData.getBinderId() );
		
		// Get the selected branding image.
		{
			// Is something selected in the "branding image" listbox?
			imgName = "";
			index = m_brandingImgListbox.getSelectedIndex();
			if ( index != -1 )
			{
				// Yes
				imgName = m_brandingImgListbox.getValue( index );
				
				// Is "none" or "no available images" selected.
				if ( imgName.equalsIgnoreCase( m_noImage ) || imgName.equalsIgnoreCase( m_noAvailableImages ) )
				{
					// Yes, revert to nothing
					imgName = "";
				}
			}
			
			brandingData.setBrandingImageName( imgName );
		}
		
		// Get the selected background image.
		{
			// Is something selected in the "background image" listbox?
			imgName = "";
			index = m_backgroundImgListbox.getSelectedIndex();
			if ( index != -1 )
			{
				// Yes
				imgName = m_backgroundImgListbox.getValue( index );
				
				// Is "none" or "no available images" selected.
				if ( imgName.equalsIgnoreCase( m_noImage ) || imgName.equalsIgnoreCase( m_noAvailableImages ) )
				{
					// Yes, revert to nothing
					imgName = "";
				}
			}
			
			brandingData.setBgImageName( imgName );
		}
		
		// Get the background color from the dialog
		brandingData.setBgColor( m_backgroundColorTextbox.getText() );
		
		// Get the font color from the dialog.
		brandingData.setFontColor( m_textColorTextbox.getText() );
		
		return brandingData;
	}// end getDataFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	public FocusWidget getFocusWidget()
	{
		return m_backgroundColorTextbox;
	}// end getFocusWidget()
	
	
	/**
	 * Issue an ajax request to get the list of file attachments for this binder.
	 * When we get the response, updateListOfFileAttachments() will be called.
	 */
	private void getListOfFileAttachmentsFromServer()
	{
		GwtRpcServiceAsync rpcService;

		rpcService = GwtTeaming.getRpcService();
		
		// Issue an ajax request to get the list of file attachments for this binder.
		rpcService.getFileAttachments( m_origBrandingData.getBinderId(), m_rpcReadCallback );
	}// end getListOfFileAttachmentsFromServer()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the branding data.
	 */
	public void init( GwtBrandingData brandingData )
	{
		// Issue an ajax request to get the list of file attachments for this binder.
		// When we get the response, updateListOfFileAttachments() will be called.
		getListOfFileAttachmentsFromServer();
		
		// Add the background color to the appropriate textbox.
		m_backgroundColorTextbox.setText( brandingData.getBgColor() );
		
		// Add the text color to the appropriate textbox.
		this.m_textColorTextbox.setText( brandingData.getFontColor() );
		
		// Update the sample text with the given background color and font color.
		updateSampleTextBgColor();
		updateSampleTextColor();
	}// end init()
	
	
	/**
	 * Issue an ajax request to save the branding data to the db.
	 */
	public void saveData( Object data )
	{
		GwtRpcServiceAsync rpcService;

		rpcService = GwtTeaming.getRpcService();
		
		// Issue an ajax request to save the branding data to the db.
		rpcService.saveBrandingData( m_origBrandingData.getBinderId(), (GwtBrandingData)data, m_rpcSaveCallback );
		
	}// end saveData()
	
	
	/**
	 * For the given image name, select the appropriate file name in the given listbox.
	 */
	private void selectImageInListbox( ListBox listbox, String imgName )
	{
		boolean foundImgName = false;
		
		// Do we have an image name.
		if ( imgName != null && imgName.length() > 0 )
		{
			int index;
			
			// Yes, try to select the image name in the given listbox.
			index = selectListboxItemByValue( listbox, imgName );
			if ( index != -1 )
				foundImgName = true;
		}
		
		// Did we find the image name in the listbox?
		if ( foundImgName == false )
		{
			String value;
			
			// No
			// Are there files to select from in the listbox?
			value = listbox.getValue( 0 );
			if ( value != null && value.equalsIgnoreCase( m_noAvailableImages ) )
			{
				// No, select the "no images available" option.
				listbox.setSelectedIndex( 0 );
			}
			else
			{
				// Yes
				// Select the "no image" option in the listbox.
				selectListboxItemByValue( listbox, m_noImage );
			}
		}
	}// end selectImageInListbox()
	
	
	/**
	 * For the given listbox, select the item in the listbox that has the given value.
	 */
	private int selectListboxItemByValue( ListBox listbox, String value )
	{
		int i;
		
		for (i = 0; i < listbox.getItemCount(); ++i)
		{
			String tmp;
			
			tmp = listbox.getValue( i );
			if ( tmp != null && tmp.equalsIgnoreCase( value ) )
			{
				listbox.setSelectedIndex( i );
				return i;
			}
		}
		
		// If we get here it means we did not find an item in the listbox with the given value.
		return -1;
	}// end selectListboxItemByValue()
	
	
	/**
	 * Update the 2 listboxes that allow the user to select the branding image and the background image
	 * from the list of files attached to the binder.
	 */
	private void updateListOfFileAttachments( ArrayList<String> listOfFileAttachments )
	{
		int i;

		// Empty the "branding image" and "background image" listboxes.
		m_brandingImgListbox.clear();
		m_backgroundImgListbox.clear();
		
		// Add each file name to the "branding image" listbox and to the "background image" listbox.
		for (i = 0; listOfFileAttachments != null && i < listOfFileAttachments.size(); ++i)
		{
			String fileName;
			
			fileName = listOfFileAttachments.get( i );
			m_brandingImgListbox.addItem( fileName, fileName );
			m_backgroundImgListbox.addItem( fileName, fileName );
		}
		
		// Do we have any file attachments?
		if ( i == 0 )
		{
			// No
			// Add an entry called "No available images" to the listboxes.
			m_brandingImgListbox.addItem( GwtTeaming.getMessages().noImagesAvailable(), m_noAvailableImages );
			m_backgroundImgListbox.addItem( GwtTeaming.getMessages().noImagesAvailable(), m_noAvailableImages );
		}
		else
		{
			// Yes
			// Add an entry called "None" to the listboxes.  The user can select "None" if
			// they don't want to use a branding or background image.
			m_brandingImgListbox.addItem( GwtTeaming.getMessages().imgNone(), m_noImage );
			m_backgroundImgListbox.addItem( GwtTeaming.getMessages().imgNone(), m_noImage );
		}
		
		// Select the branding image file in the listbox that is defined in the original branding data.
		selectImageInListbox( m_brandingImgListbox, m_origBrandingData.getBrandingImageName() );
		
		// Select the background image file in the listbox that is defined in the original branding data. 
		selectImageInListbox( m_backgroundImgListbox, m_origBrandingData.getBgImageName() );
	}// end updateListOfFileAttachments()
	
	
	/**
	 * Update the background color of the sample text with the color that is found in the textbox.
	 */
	private void updateSampleTextBgColor()
	{
		Element element;
		Style style;
		String color;
		
		element = m_sampleText.getElement();
		style = element.getStyle();
		
		// Get the background color
		color = m_backgroundColorTextbox.getText();
		style.clearBackgroundColor();
		if ( color != null && color.length() > 0 )
			style.setBackgroundColor( color );
	}// end updateSampleTextBgColor()
	
	
	/**
	 * Update the text color of the sample text with the color found in the textbox.
	 */
	private void updateSampleTextColor()
	{
		Element element;
		Style style;
		String color;
		
		element = m_sampleText.getElement();
		style = element.getStyle();
		
		// Set the text color
		color = m_textColorTextbox.getText();
		style.clearColor();
		if ( color != null && color.length() > 0 )
			style.setColor( color );
	}// end updateSampleTextColor()
}// end EditBrandingDlg
