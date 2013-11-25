/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.lpe;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.GetFileAttachmentsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFileAttachmentsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.AddFileAttachmentDlg;
import org.kablink.teaming.gwt.client.widgets.ColorPickerDlg;
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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;


/**
 * This dialog allows the user to edit the properties of the landing page such as the
 * background color, or background image.
 * @author jwootton
 *
 */
public class LandingPagePropertiesDlgBox extends DlgBox
{
	private static final String NO_IMAGE = "__no image__";
	private static final String NO_AVAILABLE_IMAGES = "no available images";

	private AsyncCallback<VibeRpcResponse> m_rpcReadCallback;
	private String m_binderId;
	private ListBox m_bgImgListbox;
	private ListBox m_bgImgRepeatListbox;
	private TextBox m_bgColorTextbox;
	private InlineLabel m_sampleBGColor;
	private LandingPageProperties m_origLPProperties;
	private AddFileAttachmentDlg m_addFileAttachmentDlg = null;
	private ColorPickerDlg m_colorPickerDlg;
	private TextBox m_destColorTextbox;
	
	/**
	 * 
	 */
	public LandingPagePropertiesDlgBox(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos );
		
		// Create the callback that will be used when we issue an ajax call to get
		// the list of files attached to the given binder.
		m_rpcReadCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
			public void onFailure( Throwable t )
			{
				GwtClientHelper.handleGwtRPCFailure(
					t,
					GwtTeaming.getMessages().rpcFailure_GetListOfAttachments(),
					m_binderId );
				
				// Update the list of files the user can select from for the background image.
				updateListOfFileAttachments( null );
			}
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( VibeRpcResponse response )
			{
				ArrayList<String> listOfFileAttachments;
				GetFileAttachmentsRpcResponseData responseData;
				
				responseData = (GetFileAttachmentsRpcResponseData) response.getResponseData();
				listOfFileAttachments = responseData.getFileNames();
				
				// Update the list of files the user can select from for the background image.
				updateListOfFileAttachments( listOfFileAttachments );
			}
		};
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().landingPagePropertiesDlgHeader(), editSuccessfulHandler, editCanceledHandler, null ); 
	}

	/**
	 * 
	 */
	public Panel createContent( Object propertiesObj )
	{
		FlowPanel mainPanel = null;
		Label spacer;
		FlexTable table = null;
		int nextRow;
		HTMLTable.CellFormatter cellFormatter;

		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		cellFormatter = table.getCellFormatter();
		
		nextRow = 0;
		
		// Add the controls for "Background Image"
		{
			HorizontalPanel hPanel;
			
			table.setText( nextRow, 0, GwtTeaming.getMessages().backgroundImgLabel() );

			// Create a list box to hold the list of attachments for the given binder.
			// User can select one of these files to use as the background image.
			m_bgImgListbox = new ListBox( false );
			m_bgImgListbox.setVisibleItemCount( 1 );

			hPanel = new HorizontalPanel();
			hPanel.add( m_bgImgListbox );
			
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
				flowPanel.addStyleName( "addAttachmentLink" );
				flowPanel.addStyleName( "landingPagePropertiesLink" );
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
						Widget anchor;
						
						// Get the anchor the user clicked on.
						anchor = (Widget) event.getSource();
						
						removeMouseOverStyles( anchor.getParent() );

						// Invoke the "Add file attachment" dialog.
						invokeAddFileAttachmentDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop() );
					}
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
					}
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
						removeMouseOverStyles( widget.getParent() );
					}
				};
				addFileAnchor.addMouseOutHandler( mouseOutHandler );

				hPanel.add( flowPanel );
			}
			
			table.setWidget( nextRow, 1, hPanel );
			++nextRow;
		}
		
		// Add the controls for "Background Image Repeat"
		{
			cellFormatter.setWordWrap( nextRow, 0, false );
			
			table.setText( nextRow, 0, GwtTeaming.getMessages().backgroundRepeatLabel() );

			// Create a list box to hold the possible values for the background image repeat
			m_bgImgRepeatListbox = new ListBox( false );
			m_bgImgRepeatListbox.setVisibleItemCount( 1 );

			// Add all the possible values for the background image repeat
			m_bgImgRepeatListbox.addItem( GwtTeaming.getMessages().backgroundRepeat(), "repeat" );
			m_bgImgRepeatListbox.addItem( GwtTeaming.getMessages().backgroundRepeatX(), "repeat-x" );
			m_bgImgRepeatListbox.addItem( GwtTeaming.getMessages().backgroundRepeatY(), "repeat-y" );
			m_bgImgRepeatListbox.addItem( GwtTeaming.getMessages().backgroundNoRepeat(), "no-repeat" );
			
			table.setWidget( nextRow, 1, m_bgImgRepeatListbox );
			++nextRow;
		}
		
		// Add an empty row to add some space between the "background image repeat" listbox and the "background color" textbox.
		spacer = new Label( " " );
		spacer.addStyleName( "marginTop10px" );
		table.setWidget( nextRow, 0, spacer );
		++nextRow;
		
		// Add the controls for "Background color"
		{
			KeyUpHandler keyUpHandler;
			ClickHandler clickHandler;
			HorizontalPanel hPanel;
			
			table.setText( nextRow, 0, GwtTeaming.getMessages().backgroundColorLabel() );

			// Create a panel where the background color control and hint will live.
			hPanel = new HorizontalPanel();
			
			// Add a text box where the user can enter the background color
			{
				m_bgColorTextbox = new TextBox();
				m_bgColorTextbox.setVisibleLength( 20 );
				hPanel.add( m_bgColorTextbox );

				// Add a keyup handler so we can update the "sample text" background color.
				keyUpHandler = new KeyUpHandler()
				{
					/**
					 * Update the background color of the sample text.
					 */
					public void onKeyUp( KeyUpEvent event )
					{
						updateSampleTextBgColor();
					}
				};
				m_bgColorTextbox.addKeyUpHandler( keyUpHandler );
			}
			
			// Add a link next to the background color textbox the user can click on to invoke a color picker.
			{
				Anchor colorLink;

				colorLink = new Anchor();
				colorLink.setTitle( GwtTeaming.getMessages().displayColorPicker() );
				colorLink.addStyleName( "colorPickerLink" );
				colorLink.addStyleName( "landingPagePropertiesLink" );
				colorLink.addStyleName( "subhead-control-bg1" );
				colorLink.addStyleName( "roundcornerSM" );
				hPanel.add( colorLink );

				// Add the browse image to the link.
				{
					Image colorPickerImg;
					Element linkElement;
					Element imgElement;
					
					colorPickerImg = new Image( GwtTeaming.getImageBundle().colorPicker() );
					linkElement = colorLink.getElement();
					imgElement = colorPickerImg.getElement();
					imgElement.getStyle().setMarginTop( 2, Style.Unit.PX );
					linkElement.appendChild( imgElement );
				}

				// Add a clickhandler to the color hint.  When the user clicks on the hint we
				// will invoke the color picker.
				clickHandler = new ClickHandler()
				{
					/**
					 * Invoke the color picker.
					 */
					public void onClick( ClickEvent event )
					{
						Widget anchor;
						
						// Get the anchor the user clicked on.
						anchor = (Widget) event.getSource();
						
						// Invoke the "Color Picker" dialog.
						invokeColorPickerDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop(), m_bgColorTextbox );
					}
				};
				colorLink.addClickHandler( clickHandler );
			}
			
			table.setWidget( nextRow, 1, hPanel );
			
			++nextRow;
			
			// Add a hint below the background color text box.
			{
				Label colorHint;
				
				colorHint = new Label( GwtTeaming.getMessages().colorHint() );
				table.setWidget( nextRow, 1, colorHint );
				
				++nextRow;
			}
		}
		
		// Add a "sample text" field that will display the selected background color.
		{
			cellFormatter.addStyleName( nextRow, 1, "paddingTop8px" );

			m_sampleBGColor = new InlineLabel( "" );
			m_sampleBGColor.getElement().setInnerHTML( "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" );
			m_sampleBGColor.addStyleName( "editBrandingSampleText" );
			table.setWidget( nextRow, 1, m_sampleBGColor );
			++nextRow;
		}
		
		mainPanel.add( table );
		
		return mainPanel;
	}

	/**
	 * 
	 */
	@Override
	public Object getDataFromDlg()
	{
		LandingPageProperties lpProperties;
		String imgName;
		String color;
		int index;
		
		lpProperties = new LandingPageProperties( null );
		
		// Get the selected background image.
		{
			// Is something selected in the "background image" listbox?
			imgName = "";
			index = m_bgImgListbox.getSelectedIndex();
			if ( index != -1 )
			{
				// Yes
				imgName = m_bgImgListbox.getValue( index );
				
				// Is "none" or "no available images" selected.
				if ( imgName.equalsIgnoreCase( NO_IMAGE ) || imgName.equalsIgnoreCase( NO_AVAILABLE_IMAGES ) )
				{
					// Yes, revert to nothing
					imgName = "";
				}
			}
			
			lpProperties.setBackgroundImgName( imgName );
		}
		
		// Get the selected background repeat value
		{
			String repeat = "";
			
			if ( m_bgImgRepeatListbox.isVisible() )
			{
				index = m_bgImgRepeatListbox.getSelectedIndex();
				if ( index != -1 )
					repeat = m_bgImgRepeatListbox.getValue( index );
			}

			lpProperties.setBackgroundRepeat( repeat );
		}
		
		// Get the background color from the dialog
		color = m_bgColorTextbox.getText();
		if ( color != null && color.length() > 0 )
		{
			// Is this background color valid?
			if ( isColorValid( color ) == false )
			{
				// No, tell the user about the problem.
				Window.alert( GwtTeaming.getMessages().invalidBackgroundColor( color ) );
				return null;
			}
		}
		lpProperties.setBackgroundColor( color );
		
		return lpProperties;
	}

	/**
	 * 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_bgImgListbox;
	}

	/**
	 * Issue an ajax request to get the list of file attachments for this binder.
	 * When we get the response, updateListOfFileAttachments() will be called.
	 */
	private void getListOfFileAttachmentsFromServer()
	{
		GetFileAttachmentsCmd cmd;
		
		// Issue an ajax request to get the list of file attachments for this binder.
		cmd = new GetFileAttachmentsCmd( m_binderId );
		GwtClientHelper.executeCommand( cmd, m_rpcReadCallback );
	}
	
	
	/**
	 * Initialize the controls in the dialog with the values from the landing page properties.
	 */
	public void init( LandingPageProperties lpProperties, String binderId )
	{
		int index;
		
		// Remember the properties we started with.
		m_origLPProperties = lpProperties;
		
		m_binderId = binderId;
		
		// Issue an ajax request to get the list of file attachments for this binder.
		// When we get the response, updateListOfFileAttachments() will be called.
		getListOfFileAttachmentsFromServer();

		// Select the appropriate option in the background repeat listbox
		index = GwtClientHelper.selectListboxItemByValue( m_bgImgRepeatListbox, m_origLPProperties.getBackgroundRepeat() );
		if ( index == -1 )
			m_bgImgRepeatListbox.setSelectedIndex( 0 );

		// Add the background color to the appropriate textbox.
		m_bgColorTextbox.setText( m_origLPProperties.getBackgroundColor() );
		
		// Update the sample text with the given background color and font color.
		updateSampleTextBgColor();
	}
	

	/**
	 * Invoke the "Add file attachment" dialog.
	 */
	private void invokeAddFileAttachmentDlg( final int x, final int y )
	{
		PopupPanel.PositionCallback posCallback;

		// Have we already created an "Add file attachment" dialog?
		if ( m_addFileAttachmentDlg != null )
		{
			// Yes, clear any content from a previous invocation.
			m_addFileAttachmentDlg.setBinderId( m_binderId );
			m_addFileAttachmentDlg.clearContent();
			m_addFileAttachmentDlg.setPopupPosition( x, y );
		}
		else
		{
			EditSuccessfulHandler editSuccessfulHandler;
			EditCanceledHandler editCanceledHandler;
			
			editSuccessfulHandler = new EditSuccessfulHandler()
			{
				/**
				 * This method gets called when user user presses ok in the "Add File Attachment" dialog.
				 */
				public boolean editSuccessful( Object obj )
				{
					m_addFileAttachmentDlg.hide();
					
					// Issue an ajax request to get the list of file attachments for this binder.
					// When we get the response, updateListOfFileAttachments() will be called.
					getListOfFileAttachmentsFromServer();

					m_addFileAttachmentDlg.hide();
					
					return true;
				}
			};
				
			editCanceledHandler = new EditCanceledHandler()
			{
				/**
				 * This method gets called when the user presses cancel in the "Add file attachment" dialog.
				 */
				public boolean editCanceled()
				{
					m_addFileAttachmentDlg.hide();
					
					return true;
				}
			};

			// No, create an "Add file attachment" dialog.
			m_addFileAttachmentDlg = new AddFileAttachmentDlg(
														editSuccessfulHandler,
														editCanceledHandler,
														false,
														true,
														x,
														y,
														m_binderId );
		}

		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				m_addFileAttachmentDlg.setPopupPosition( x - offsetWidth + 100, y );
			}
		};
		m_addFileAttachmentDlg.setPopupPositionAndShow( posCallback );
	}
	
	/**
	 * Invoke the "Color Picker" dialog.
	 */
	public void invokeColorPickerDlg( final int x, final int y, TextBox destTextbox )
	{
		PopupPanel.PositionCallback posCallback;
		
		// Remember the textbox we should put the selected color in when the user presses ok
		// in the "color picker" dialog.
		m_destColorTextbox = destTextbox;
		
		// Have we already created a "Color Picker" dialog?
		if ( m_colorPickerDlg != null )
		{
			// Yes
			m_colorPickerDlg.setPopupPosition( x, y );
		}
		else
		{
			EditSuccessfulHandler editSuccessfulHandler;
			EditCanceledHandler editCanceledHandler;
			
			editSuccessfulHandler = new EditSuccessfulHandler()
			{
				/**
				 * This method gets called when user user presses ok in the "Color Picker" dialog.
				 */
				public boolean editSuccessful( Object obj )
				{
					m_colorPickerDlg.hide();

					if ( obj instanceof ColorPickerDlg.Color )
					{
						ColorPickerDlg.Color color;
						
						color = (ColorPickerDlg.Color) obj;
						
						// Put the selected color in the appropriate textbox.
						m_destColorTextbox.setText( color.getName() );

						// Update the sample text with the selected color.
						updateSampleTextBgColor();
					}
					
					return true;
				}
			};
				
			editCanceledHandler = new EditCanceledHandler()
			{
				/**
				 * This method gets called when the user presses cancel in the "Color Picker" dialog.
				 */
				public boolean editCanceled()
				{
					m_colorPickerDlg.hide();
					
					return true;
				}
			};

			// No, create a "color picker" dialog.
			m_colorPickerDlg = new ColorPickerDlg(
												editSuccessfulHandler,
												editCanceledHandler,
												false,
												true,
												x,
												y,
												null );
		}

		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				m_colorPickerDlg.setPopupPosition( x - offsetWidth + 50, y );
			}
		};
		m_colorPickerDlg.setPopupPositionAndShow( posCallback );
	}


	/**
	 * Validate that the given color is valid.
	 */
	private boolean isColorValid( String color )
	{
		Element element;
		Style style;
		String origColor;
		boolean valid;
		
		if ( color == null )
			return false;
		
		element = m_sampleBGColor.getElement();
		style = element.getStyle();
		origColor = style.getBackgroundColor();
		
		try
		{
			String tmpColor;
			
			valid = true;
			
			// On IE, setBackgroundColor() will throw an exception if the color is not valid.
			// On FF, setBackgroundColor() will not set the color if is an invalid color.
			style.setBackgroundColor( color );
			
			// Get the background color.  If it is empty then we were passed an invalid color.
			tmpColor = style.getBackgroundColor();
			if ( tmpColor == null || tmpColor.length() == 0 )
				valid = false;
		}
		catch( Exception ex )
		{
			valid = false;
		}
		
		style.setBackgroundColor( origColor );
		
		return valid;
	}
	
	
	/**
	 * Remove the styles that were added to the given widget when the user moved the mouse over the widget.
	 */
	private void removeMouseOverStyles( Widget widget )
	{
		widget.removeStyleName( "subhead-control-bg2" );
		widget.addStyleName( "subhead-control-bg1" );
	}
	
	
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
			index = GwtClientHelper.selectListboxItemByValue( listbox, imgName );
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
			if ( value != null && value.equalsIgnoreCase( NO_AVAILABLE_IMAGES ) )
			{
				// No, select the "no images available" option.
				listbox.setSelectedIndex( 0 );
			}
			else
			{
				// Yes, select "no image"
				GwtClientHelper.selectListboxItemByValue( listbox, NO_IMAGE );
			}
		}
	}
	
	
	/**
	 * Update the listbox that allows the user to select the background image
	 * from the list of files attached to the binder.
	 */
	private void updateListOfFileAttachments( ArrayList<String> listOfFileAttachments )
	{
		int i;

		// Empty the "background image" listbox.
		m_bgImgListbox.clear();
		
		// Do we have any file attachments?
		if ( listOfFileAttachments != null && listOfFileAttachments.size() > 0 )
		{
			// Yes
			// Add an entry called "None" to the background listbox.  The user can select "None" if
			// they don't want to use a background image.
			m_bgImgListbox.addItem( GwtTeaming.getMessages().imgNone(), NO_IMAGE );

			// Add each file name to the "background image" listbox.
			for (i = 0; listOfFileAttachments != null && i < listOfFileAttachments.size(); ++i)
			{
				String fileName;
				
				fileName = listOfFileAttachments.get( i );
				m_bgImgListbox.addItem( fileName, fileName );
			}
		}
		else
		{
			// No
			// Add an entry called "No available images" to the listbox.
			m_bgImgListbox.addItem( GwtTeaming.getMessages().noImagesAvailable(), NO_AVAILABLE_IMAGES );
		}
		
		// Select the background image file in the listbox that is defined in the original landing page properties. 
		selectImageInListbox( m_bgImgListbox, m_origLPProperties.getBackgroundImageName() );
	}

	/**
	 * Update the background color of the sample text with the color that is found in the textbox.
	 */
	private void updateSampleTextBgColor()
	{
		Element element;
		Style style;
		String color;
		
		element = m_sampleBGColor.getElement();
		style = element.getStyle();
		
		// Get the background color
		color = m_bgColorTextbox.getText();
		style.clearBackgroundColor();
		if ( color != null && color.length() > 0 )
		{
			try
			{
				style.setBackgroundColor( color );
			}
			catch (Exception ex)
			{
				// Nothing to do
			}
		}
	}
}
