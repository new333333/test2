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
import org.kablink.teaming.gwt.client.GwtBrandingDataExt;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtTeamingException.ExceptionType;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


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
	private CheckBox m_stretchBgImgCb;
	private TextBox m_backgroundColorTextbox;
	private TextBox m_textColorTextbox;
	private InlineLabel m_sampleText;
	private AsyncCallback<ArrayList<String>> m_rpcReadCallback = null;
	private GwtBrandingData m_origBrandingData;		// The original branding data we started with.
	private final String m_noAvailableImages = "no available images";

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
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().brandingDlgHeader(), editSuccessfulHandler, editCanceledHandler, brandingData ); 
	}// end EditBrandingDlg()
	

	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
		FlowPanel mainPanel = null;
		Label spacer;
		FlexTable table = null;
		GwtBrandingData brandingData;
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
			HorizontalPanel hPanel;
			
			m_useBrandingImgRb = new RadioButton( "brandingType", GwtTeaming.getMessages().useBrandingImgLabel() );
			table.setWidget( nextRow, 0, m_useBrandingImgRb );

			hPanel = new HorizontalPanel();
			
			// Create a list box to hold the list of attachments for the given binder.
			// The user can select one of these files to use as the branding image.
			m_brandingImgListbox = new ListBox( false );
			m_brandingImgListbox.setVisibleItemCount( 1 );
			hPanel.add( m_brandingImgListbox );
			
			// Add a link the user can click on to add a file
			{
				Anchor addFileAnchor;
				ClickHandler clickHandler;
				MouseOverHandler mouseOverHandler;
				MouseOutHandler mouseOutHandler;
				FlowPanel flowPanel;
				Element linkElement;
				Element imgElement;
				Image browseImg;
				
				flowPanel = new FlowPanel();
				flowPanel.getElement().getStyle().setMarginTop( 2, Style.Unit.PX );
				flowPanel.addStyleName( "editBrandingBrowseLink" );
				flowPanel.addStyleName( "editBrandingLink" );
				flowPanel.addStyleName( "subhead-control-bg1" );
				flowPanel.addStyleName( "roundcornerSM" );

				addFileAnchor = new Anchor();
				addFileAnchor.setTitle( GwtTeaming.getMessages().addImage() );
				flowPanel.add( addFileAnchor );
				
				// Add a browse image to the link.
				browseImg = new Image( GwtTeaming.getImageBundle().browseHierarchy() );
				linkElement = addFileAnchor.getElement();
				imgElement = browseImg.getElement();
				imgElement.getStyle().setMarginTop( 2, Style.Unit.PX );
				linkElement.appendChild( imgElement );

				// Add a clickhandler to the "add file" link.  When the user clicks on the hint we
				// will invoke the "add file" dialog.
				clickHandler = new ClickHandler()
				{
					/**
					 * Invoke the "add file" dialog
					 */
					public void onClick( ClickEvent event )
					{
						Window.alert( "not yet implemented" );
					}//end onClick()
				};
				addFileAnchor.addClickHandler( clickHandler );
				
				// Add a mouse-over handler
				mouseOverHandler = new MouseOverHandler()
				{
					/**
					 * 
					 */
					public void onMouseOver( MouseOverEvent event )
					{
						Widget widget;
						
						widget = (Widget)event.getSource();
						widget.getParent().addStyleName( "subhead-control-bg2" );
					}// end onMouseOver()
				};
				addFileAnchor.addMouseOverHandler( mouseOverHandler );

				// Add a mouse-out handler
				mouseOutHandler = new MouseOutHandler()
				{
					/**
					 * 
					 */
					public void onMouseOut( MouseOutEvent event )
					{
						Widget widget;
						
						widget = (Widget)event.getSource();
						widget.getParent().removeStyleName( "subhead-control-bg2" );
					}// end onMouseOut()
				};
				addFileAnchor.addMouseOutHandler( mouseOutHandler );

				hPanel.add( flowPanel );
			}
			
			table.setWidget( nextRow, 1, hPanel );
			++nextRow;
		}
		
		// Add the controls for "Use Advanced Branding"
		{
			m_useAdvancedBrandingRb = new RadioButton( "brandingType", GwtTeaming.getMessages().useAdvancedBrandingLabel() );
			
			// Add a link the user can click on to edit the advanced branding.
			{
				Anchor advancedAnchor;
				ClickHandler clickHandler;
				MouseOverHandler mouseOverHandler;
				MouseOutHandler mouseOutHandler;
				
				advancedAnchor = new Anchor( GwtTeaming.getMessages().advancedBtn() );
				advancedAnchor.setTitle( GwtTeaming.getMessages().editAdvancedBranding() );
				advancedAnchor.addStyleName( "editBrandingLink" );
				advancedAnchor.addStyleName( "editBrandingAdvancedLink" );
				advancedAnchor.addStyleName( "subhead-control-bg1" );
				advancedAnchor.addStyleName( "roundcornerSM" );
				
				// Add a clickhandler to the "advanced" link.  When the user clicks on the link we
				// will invoke the "edit advanced branding" dialog.
				clickHandler = new ClickHandler()
				{
					/**
					 * Invoke the "edit advanced branding" dialog
					 */
					public void onClick( ClickEvent event )
					{
						Window.alert( "not yet implemented" );
					}//end onClick()
				};
				advancedAnchor.addClickHandler( clickHandler );
				
				// Add a mouse-over handler
				mouseOverHandler = new MouseOverHandler()
				{
					/**
					 * 
					 */
					public void onMouseOver( MouseOverEvent event )
					{
						Widget widget;
						
						widget = (Widget)event.getSource();
						widget.removeStyleName( "subhead-control-bg1" );
						widget.addStyleName( "subhead-control-bg2" );
					}// end onMouseOver()
				};
				advancedAnchor.addMouseOverHandler( mouseOverHandler );

				// Add a mouse-out handler
				mouseOutHandler = new MouseOutHandler()
				{
					/**
					 * 
					 */
					public void onMouseOut( MouseOutEvent event )
					{
						Widget widget;
						
						widget = (Widget)event.getSource();
						widget.removeStyleName( "subhead-control-bg2" );
						widget.addStyleName( "subhead-control-bg1" );
					}// end onMouseOut()
				};
				advancedAnchor.addMouseOutHandler( mouseOutHandler );

				table.setWidget( nextRow, 1,advancedAnchor );
			}
			
			table.setWidget( nextRow, 0, m_useAdvancedBrandingRb );
			++nextRow;
		}
		
		// Add an empty row to add some space between the "use advanced branding" radio button and the "background image" listbox.
		spacer = new Label( " " );
		spacer.addStyleName( "marginTop10px" );
		table.setWidget( nextRow, 0, spacer );
		++nextRow;
		
		// Add the controls for "Background Image"
		{
			HorizontalPanel hPanel;
			
			table.setText( nextRow, 0, GwtTeaming.getMessages().backgroundImgLabel() );

			// Create a list box to hold the list of attachments for the given binder.
			// User can select one of these files to use as the background image.
			m_backgroundImgListbox = new ListBox( false );
			m_backgroundImgListbox.setVisibleItemCount( 1 );

			hPanel = new HorizontalPanel();
			hPanel.add( m_backgroundImgListbox );
			
			// Add a link the user can click on to add a file
			{
				Anchor addFileAnchor;
				ClickHandler clickHandler;
				MouseOverHandler mouseOverHandler;
				MouseOutHandler mouseOutHandler;
				FlowPanel flowPanel;
				Element linkElement;
				Element imgElement;
				Image browseImg;
				
				flowPanel = new FlowPanel();
				flowPanel.getElement().getStyle().setMarginTop( 2, Style.Unit.PX );
				flowPanel.addStyleName( "editBrandingBrowseLink" );
				flowPanel.addStyleName( "editBrandingLink" );
				flowPanel.addStyleName( "subhead-control-bg1" );
				flowPanel.addStyleName( "roundcornerSM" );

				addFileAnchor = new Anchor();
				addFileAnchor.setTitle( GwtTeaming.getMessages().addImage() );
				flowPanel.add( addFileAnchor );
				
				// Add the browse image to the link.
				browseImg = new Image( GwtTeaming.getImageBundle().browseHierarchy() );
				linkElement = addFileAnchor.getElement();
				imgElement = browseImg.getElement();
				imgElement.getStyle().setMarginTop( 2, Style.Unit.PX );
				linkElement.appendChild( imgElement );

				// Add a clickhandler to the "add file" link.  When the user clicks on the hint we
				// will invoke the "add file" dialog.
				clickHandler = new ClickHandler()
				{
					/**
					 * Invoke the "add file" dialog
					 */
					public void onClick( ClickEvent event )
					{
						Window.alert( "not yet implemented" );
					}//end onClick()
				};
				addFileAnchor.addClickHandler( clickHandler );

				// Add a mouse-over handler
				mouseOverHandler = new MouseOverHandler()
				{
					/**
					 * 
					 */
					public void onMouseOver( MouseOverEvent event )
					{
						Widget widget;
						
						widget = (Widget)event.getSource();
						widget.getParent().addStyleName( "subhead-control-bg2" );
					}// end onMouseOver()
				};
				addFileAnchor.addMouseOverHandler( mouseOverHandler );

				// Add a mouse-out handler
				mouseOutHandler = new MouseOutHandler()
				{
					/**
					 * 
					 */
					public void onMouseOut( MouseOutEvent event )
					{
						Widget widget;
						
						widget = (Widget)event.getSource();
						widget.getParent().removeStyleName( "subhead-control-bg2" );
					}// end onMouseOut()
				};
				addFileAnchor.addMouseOutHandler( mouseOutHandler );

				hPanel.add( flowPanel );
			}
			
			table.setWidget( nextRow, 1, hPanel );
			++nextRow;
			
			// Add a "stretch image" checkbox.
			m_stretchBgImgCb = new CheckBox( GwtTeaming.getMessages().stretchImg() );
			table.setWidget( nextRow, 1, m_stretchBgImgCb );
			++nextRow;
		}
		
		// Add an empty row to add some space between the "background image" listbox and the "background color" textbox.
		spacer = new Label( " " );
		spacer.addStyleName( "marginTop10px" );
		table.setWidget( nextRow, 0, spacer );
		++nextRow;
		
		// Add the controls for "Background color"
		{
			KeyUpHandler keyUpHandler;
			ClickHandler clickHandler;
			HorizontalPanel hPanel;
			Anchor colorHint;
			
			table.setText( nextRow, 0, GwtTeaming.getMessages().backgroundColorLabel() );

			// Create a panel where the background color control and hint will live.
			hPanel = new HorizontalPanel();
			
			// Add a text box where the user can enter the background color
			m_backgroundColorTextbox = new TextBox();
			m_backgroundColorTextbox.setVisibleLength( 20 );
			hPanel.add( m_backgroundColorTextbox );

			// Add a hint next to the background color textbox the user can click on to invoke a color picker.
			colorHint = new Anchor( GwtTeaming.getMessages().colorHint() );
			colorHint.setTitle( GwtTeaming.getMessages().displayColorPicker() );
			colorHint.addStyleName( "editBrandingLink" );
			hPanel.add( colorHint );

			table.setWidget( nextRow, 1, hPanel );
			
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
			
			// Add a clickhandler to the color hint.  When the user clicks on the hint we
			// will invoke the color picker.
			clickHandler = new ClickHandler()
			{
				/**
				 * Invoke the color picker.
				 */
				public void onClick( ClickEvent event )
				{
					Window.alert( "not yet implemented" );
				}//end onClick()
			};
			colorHint.addClickHandler( clickHandler );

			++nextRow;
		}
		
		// Add the controls for "Text color"
		{
			KeyUpHandler keyUpHandler;
			ClickHandler clickHandler;
			Anchor textColorHint;
			HorizontalPanel hPanel;
			
			table.setText( nextRow, 0, GwtTeaming.getMessages().textColorLabel() );

			// Create a panel where the text color control and hint will live.
			hPanel = new HorizontalPanel();
			
			// Add a text box where the user can enter the font color.
			m_textColorTextbox = new TextBox();
			m_textColorTextbox.setVisibleLength( 20 );
			hPanel.add( m_textColorTextbox );
			
			// Add a hint next to the text color textbox the user can click on to invoke a color picker.
			textColorHint = new Anchor( GwtTeaming.getMessages().colorHint() );
			textColorHint.setTitle( GwtTeaming.getMessages().displayColorPicker() );
			textColorHint.addStyleName( "editBrandingLink" );
			hPanel.add( textColorHint );
			
			table.setWidget( nextRow, 1, hPanel );
			
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
			
			// Add a clickhandler to the color hint.  When the user clicks on the hint we
			// will invoke the color picker.
			clickHandler = new ClickHandler()
			{
				/**
				 * Invoke the color picker.
				 */
				public void onClick( ClickEvent event )
				{
					Window.alert( "not yet implemented" );
				}//end onClick()
			};
			textColorHint.addClickHandler( clickHandler );

			++nextRow;
		}
		
		// Add a "sample text" field that will display the selected background color and text color.
		{
			Element element;
			Style style;
			
			m_sampleText = new InlineLabel( GwtTeaming.getMessages().sampleText() );
			element = m_sampleText.getElement();
			style = element.getStyle();
			style.setPadding( .4, Style.Unit.EM );
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
		
		// Get whether "use branding image" or "used advanced branding" is selected.
		{
			String type;
			
			if ( m_useAdvancedBrandingRb.getValue() == true )
				type = GwtBrandingDataExt.BRANDING_TYPE_ADVANCED;
			else
				type = GwtBrandingDataExt.BRANDING_TYPE_IMAGE;
			
			brandingData.setBrandingType( type );
		}
		
		// Get the selected branding image.
		{
			// Is something selected in the "branding image" listbox?
			imgName = "";
			index = m_brandingImgListbox.getSelectedIndex();
			if ( index != -1 )
			{
				// Yes
				imgName = m_brandingImgListbox.getValue( index );
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
				if ( imgName.equalsIgnoreCase( MastHead.NO_IMAGE ) || imgName.equalsIgnoreCase( m_noAvailableImages ) )
				{
					// Yes, revert to nothing
					imgName = "";
				}
			}
			
			brandingData.setBgImageName( imgName );
		}
		
		// Get the "stretch background image" value.
		brandingData.setBgImageStretchValue( m_stretchBgImgCb.getValue() );
		
		// Get the background color from the dia)log
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
		String type;
		
		// Issue an ajax request to get the list of file attachments for this binder.
		// When we get the response, updateListOfFileAttachments() will be called.
		getListOfFileAttachmentsFromServer();

		// Select the appropriate checkbox depending on whether "use branding image" or "use advanced branding" is selected.
		type = brandingData.getBrandingType();
		if ( type != null && type.equalsIgnoreCase( GwtBrandingDataExt.BRANDING_TYPE_IMAGE ) )
			m_useBrandingImgRb.setValue( true );
		else
			m_useAdvancedBrandingRb.setValue( true );
		
		// Initialize the "stretch image" checkbox.
		m_stretchBgImgCb.setValue( brandingData.getBgImageStretchValue() );
		
		// Add the background color to the appropriate textbox.
		m_backgroundColorTextbox.setText( brandingData.getBgColor() );
		
		// Add the text color to the appropriate textbox.
		m_textColorTextbox.setText( brandingData.getFontColor() );
		
		// Update the sample text with the given background color and font color.
		updateSampleTextBgColor();
		updateSampleTextColor();
	}// end init()
	
	
	/**
	 * For the given image name, select the appropriate file name in the given listbox.
	 */
	private void selectImageInListbox( ListBox listbox, String imgName )
	{
		boolean foundImgName = false;
		int index;
		
		// Do we have an image name.
		if ( imgName != null && imgName.length() > 0 )
		{
			// Yes, try to select the image name in the given listbox.
			index = selectListboxItemByValue( listbox, imgName );
			if ( index != -1 )
				foundImgName = true;
		}
		else
		{
			// No, try to select the default Teaming image option.
			index = selectListboxItemByValue( listbox, MastHead.DEFAULT_TEAMING_IMAGE );
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
				// Try to select the default Teaming image option in the listbox.
				index = selectListboxItemByValue( listbox, MastHead.DEFAULT_TEAMING_IMAGE );
				
				// Did we select the default teaming image?
				if ( index == -1 )
				{
					// No, select "no image"
					selectListboxItemByValue( listbox, MastHead.NO_IMAGE );
				}
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
		
		// Add an entry called "None" to the branding listbox.  The user can select "None" if
		// they don't want to use a branding image.
		m_brandingImgListbox.addItem( GwtTeaming.getMessages().imgNone(), MastHead.NO_IMAGE );
		
		// Add a Novell Teaming or a Kablink Teaming entry to the branding listbox depending on
		// whether we are running Novell or Kablink Teaming.  The user can select this entry if
		// they want to use the Novell/Kablink Teaming branding image.
		if ( GwtMainPage.m_novellTeaming )
			m_brandingImgListbox.addItem( GwtTeaming.getMessages().novellTeaming(), MastHead.DEFAULT_TEAMING_IMAGE );
		else
			m_brandingImgListbox.addItem( GwtTeaming.getMessages().kablinkTeaming(), MastHead.DEFAULT_TEAMING_IMAGE );

		// Do we have any file attachments?
		if ( listOfFileAttachments.size() > 0 )
		{
			// Yes
			// Add an entry called "None" to the background listbox.  The user can select "None" if
			// they don't want to use a background image.
			m_backgroundImgListbox.addItem( GwtTeaming.getMessages().imgNone(), MastHead.NO_IMAGE );

			// Add each file name to the "branding image" listbox and to the "background image" listbox.
			for (i = 0; listOfFileAttachments != null && i < listOfFileAttachments.size(); ++i)
			{
				String fileName;
				
				fileName = listOfFileAttachments.get( i );
				m_brandingImgListbox.addItem( fileName, fileName );
				m_backgroundImgListbox.addItem( fileName, fileName );
			}
		}
		else
		{
			// No
			// Add an entry called "No available images" to the listboxes.
			m_backgroundImgListbox.addItem( GwtTeaming.getMessages().noImagesAvailable(), m_noAvailableImages );
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
