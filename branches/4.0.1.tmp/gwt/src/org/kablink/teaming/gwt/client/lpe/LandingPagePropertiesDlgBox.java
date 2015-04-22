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
import org.kablink.teaming.gwt.client.widgets.ColorCtrl;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TabPanel;
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
	private CheckBox m_inheritPropertiesCb;
	private CheckBox m_hideMastheadCB;
	private CheckBox m_hideSidebarCB;
	private CheckBox m_hideFooterCB;
	private CheckBox m_hideMenuCB;
	private ListBox m_bgImgListbox;
	private ListBox m_bgImgRepeatListbox;
	private ColorCtrl m_bgColorCtrl;
	private ColorCtrl m_headerBgColorCtrl;
	private ColorCtrl m_headerTextColorCtrl;
	private ColorCtrl m_contentTextColorCtrl;
	private ColorCtrl m_borderColorCtrl;
	private TextBox m_borderWidthCtrl;
	private GwtLandingPageProperties m_origLPProperties;
	private AddFileAttachmentDlg m_addFileAttachmentDlg = null;
	private String m_selectedBgImgName;
	private VibeGlassPanel m_glassPanel = null;
	private RadioButton m_lightRB;
	private RadioButton m_darkRB;
	private TabPanel m_tabPanel;
	
	/**
	 * 
	 */
	public class VibeGlassPanel extends VibeFlowPanel
	{
		/**
		 * 
		 */
		public VibeGlassPanel()
		{
			addStyleName( "vibeGlassPanel" );
		}
	}
	
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
			@Override
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
			@Override
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
	 * Create a panel that holds the controls used for defining background properties
	 */
	private Panel createBackgroundPanel()
	{
		FlowPanel panel;
		FlexTable table;
		HTMLTable.CellFormatter cellFormatter;
		int row;
		
		panel = new FlowPanel();
		
		table = new FlexTable();
		table.setCellSpacing( 4 );
		panel.add( table );
		
		cellFormatter = table.getCellFormatter();
		
		row = 0;
		
		// Add the controls for "Background Image"
		{
			HorizontalPanel hPanel;
			
			table.setText( row, 0, GwtTeaming.getMessages().backgroundImgLabel() );

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
					@Override
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
					@Override
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
					@Override
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
			
			table.setWidget( row, 1, hPanel );
			++row;
		}
		
		// Add the controls for "Background Image Repeat"
		{
			cellFormatter.setWordWrap( row, 0, false );
			
			table.setText( row, 0, GwtTeaming.getMessages().backgroundRepeatLabel() );

			// Create a list box to hold the possible values for the background image repeat
			m_bgImgRepeatListbox = new ListBox( false );
			m_bgImgRepeatListbox.setVisibleItemCount( 1 );

			// Add all the possible values for the background image repeat
			m_bgImgRepeatListbox.addItem( GwtTeaming.getMessages().backgroundRepeat(), "repeat" );
			m_bgImgRepeatListbox.addItem( GwtTeaming.getMessages().backgroundRepeatX(), "repeat-x" );
			m_bgImgRepeatListbox.addItem( GwtTeaming.getMessages().backgroundRepeatY(), "repeat-y" );
			m_bgImgRepeatListbox.addItem( GwtTeaming.getMessages().backgroundNoRepeat(), "no-repeat" );
			
			table.setWidget( row, 1, m_bgImgRepeatListbox );
			++row;
		}
		
		// Add a color control for the background color.
		{
			table.setText( row, 0, GwtTeaming.getMessages().backgroundColorLabel() );
			
			m_bgColorCtrl = new ColorCtrl();
			table.setWidget( row, 1, m_bgColorCtrl );
			++row;
		}
		
		return panel;
	}
	
	/**
	 * Create a panel for the controls used to set background properties
	 */
	private Panel createBorderPanel()
	{
		FlowPanel panel;
		FlexTable table;
		int row;
		
		panel = new FlowPanel();
		
		table = new FlexTable();
		table.setCellSpacing( 4 );
		panel.add( table );
		
		row = 0;
		
		// Add the controls for border width and color
		table.setText( row, 0, GwtTeaming.getMessages().borderColorLabel() );
		m_borderColorCtrl = new ColorCtrl();
		table.setWidget( row, 1, m_borderColorCtrl );
		++row;

		// Add the label and controls for the width
		{
			table.setText( row, 0, GwtTeaming.getMessages().borderWidthLabel() );
			
			m_borderWidthCtrl = new TextBox();
			m_borderWidthCtrl.addKeyPressHandler( new KeyPressHandler()
			{
				@Override
				public void onKeyPress(KeyPressEvent event)
				{
			        int keyCode;

			        // Get the key the user pressed
			        keyCode = event.getNativeEvent().getKeyCode();
			        
			        if ( GwtClientHelper.isKeyValidForNumericField( event.getCharCode(), keyCode ) == false )
			        {
			        	TextBox txtBox;
			        	Object source;
			        	
			        	// Make sure we are dealing with a text box.
			        	source = event.getSource();
			        	if ( source instanceof TextBox )
			        	{
			        		// Suppress the current keyboard event.
			        		txtBox = (TextBox) source;
			        		txtBox.cancelKey();
			        	}
			        }
				}
			} );
			m_borderWidthCtrl.setVisibleLength( 3 );
			table.setWidget( row, 1, m_borderWidthCtrl );
		}

		return panel;
	}
	
	/**
	 * 
	 */
	@Override
	public Panel createContent( Object propertiesObj )
	{
		FlowPanel mainPanel = null;
		FlowPanel propertiesPanel;

		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );
		
		m_inheritPropertiesCb = new CheckBox( GwtTeaming.getMessages().inheritPropertiesLabel() );
		m_inheritPropertiesCb.addClickHandler( new ClickHandler()
		{
			@Override
			public void onClick( ClickEvent event )
			{
				// Enable/disable the property controls based on whether "inherit properties" checkbox is checked.
				dancePropertyControls();
			}
		});
		mainPanel.add( m_inheritPropertiesCb );
		
		propertiesPanel = new FlowPanel();
		propertiesPanel.addStyleName ( "lpPropertiesPanel" );
		mainPanel.add( propertiesPanel );
		
		// Create a glass panel that will be used to disable the properties control.
		m_glassPanel = new VibeGlassPanel();
		m_glassPanel.setVisible( false );
		propertiesPanel.add( m_glassPanel );
		
		m_tabPanel = new TabPanel();
		m_tabPanel.addStyleName( "vibe-tabPanel" );

		propertiesPanel.add( m_tabPanel );

		// Create a panel to hold misc information
		{
			Panel panel;
			
			panel = createMiscPanel();
			m_tabPanel.add( panel, GwtTeaming.getMessages().landingPagePropertiesDlg_MiscTab() );
		}

		// Create a panel to hold the background information
		{
			Panel panel;
			
			panel = createBackgroundPanel();
			m_tabPanel.add( panel, GwtTeaming.getMessages().landingPagePropertiesDlg_BackgroundTab() );
		}
		
		// Create a panel to hold the header information
		{
			Panel panel;
			
			panel = createHeaderPanel();
			m_tabPanel.add( panel, GwtTeaming.getMessages().landingPagePropertiesDlg_HeaderTab() );
		}
		
		// Create a panel to hold the border information
		{
			Panel panel;
			
			panel = createBorderPanel();
			m_tabPanel.add( panel, GwtTeaming.getMessages().landingPagePropertiesDlg_BorderTab() );
		}

		// Select the misc tab
		m_tabPanel.selectTab( 0 );

		return mainPanel;
	}

	/**
	 * Create a panel used to hold the controls to set the header properties
	 */
	private Panel createHeaderPanel()
	{
		FlowPanel panel;
		FlexTable table;
		int row;
		
		panel = new FlowPanel();
		
		table = new FlexTable();
		table.setCellSpacing( 4 );
		panel.add( table );
		
		row = 0;
		
		// Add the controls for the header background color and text color
		table.setText( row, 0, GwtTeaming.getMessages().headerBackgroundColorLabel() );
		m_headerBgColorCtrl = new ColorCtrl();
		table.setWidget( row, 1, m_headerBgColorCtrl );
		++row;

		table.setText( row, 0, GwtTeaming.getMessages().headerTextColorLabel() );
		m_headerTextColorCtrl = new ColorCtrl();
		table.setWidget( row, 1, m_headerTextColorCtrl );
		++row;
		
		return panel;
	}
	
	/**
	 * Create a panel used to hold the controls to set misc landing page properties
	 */
	private Panel createMiscPanel()
	{
		FlowPanel panel;
		FlexTable table;
		int row;
		
		panel = new FlowPanel();
		
		table = new FlexTable();
		table.setCellSpacing( 4 );
		panel.add( table );
		
		row = 0;
		
		// Add the controls for hiding masthead, sidebar, footer and menu
		{
			m_hideMastheadCB = new CheckBox( GwtTeaming.getMessages().hideMasthead() );
			table.setWidget( row, 0, m_hideMastheadCB );
			
			m_hideSidebarCB = new CheckBox( GwtTeaming.getMessages().hideSidebar() );
			table.setWidget( row, 1, m_hideSidebarCB );
			++row;
			
			m_hideFooterCB = new CheckBox( GwtTeaming.getMessages().hideFooter() );
			table.setWidget( row, 0, m_hideFooterCB );
			
			m_hideMenuCB = new CheckBox( GwtTeaming.getMessages().hideMenu() );
			table.setWidget( row, 1, m_hideMenuCB );
			++row;
		}
		
		// Add the controls for content text color
		{
			table = new FlexTable();
			table.setCellSpacing( 4 );
			panel.add( table );
			row = 0;
			
			table.setText( row, 0, GwtTeaming.getMessages().contentTextColorLabel() );
			m_contentTextColorCtrl = new ColorCtrl();
			table.setWidget( row, 1, m_contentTextColorCtrl );
			++row;
		}
		
		// Add the controls for landing page style
		{
			FlowPanel tmpPanel;
			
			table.setText( row, 0, GwtTeaming.getMessages().landingPagePropertiesDlg_PageStyle() );
			
			tmpPanel = new FlowPanel();
			m_lightRB = new RadioButton( "pageStyle", GwtTeaming.getMessages().landingPagePropertiesDlg_PageStyleLight() );
			tmpPanel.add( m_lightRB );
			m_darkRB = new RadioButton( "pageStyle", GwtTeaming.getMessages().landingPagePropertiesDlg_PageStyleDark() );
			tmpPanel.add( m_darkRB );
			table.setWidget( row, 1, tmpPanel );
			++row;
		}

		return panel;
	}
	
	/**
	 * Enable/disable the controls used to set the various property values based on whether
	 * the "inherit properties" checkbox is checked.
	 */
	private void dancePropertyControls()
	{
		m_glassPanel.setVisible( m_inheritPropertiesCb.getValue() );
	}
	
	
	/**
	 * 
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtLandingPageProperties lpProperties;
		String imgName;
		String color;
		int index;
		
		lpProperties = new GwtLandingPageProperties( null );
		
		// Save the "inherit properties" selection
		lpProperties.setInheritProperties( m_inheritPropertiesCb.getValue() );
		
		lpProperties.setHideMasthead( m_hideMastheadCB.getValue() );
		lpProperties.setHideSidebar( m_hideSidebarCB.getValue() );
		lpProperties.setHideFooter( m_hideFooterCB.getValue() );
		lpProperties.setHideMenu( m_hideMenuCB.getValue() );
		
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
		color = m_bgColorCtrl.getColor();
		if ( color != null && color.length() > 0 )
		{
			// Is this background color valid?
			if ( m_bgColorCtrl.isColorValid( color ) == false )
			{
				// No, tell the user about the problem.
				Window.alert( GwtTeaming.getMessages().invalidBackgroundColor( color ) );
				return null;
			}
		}
		lpProperties.setBackgroundColor( color );
		
		// Get the header background color from the dialog
		color = m_headerBgColorCtrl.getColor();
		if ( color != null && color.length() > 0 )
		{
			// Is this color valid?
			if ( m_headerBgColorCtrl.isColorValid( color ) == false )
			{
				// No, tell the user about the problem.
				Window.alert( GwtTeaming.getMessages().invalidHeaderBgColor( color ) );
				return null;
			}
		}
		lpProperties.setHeaderBgColor( color );
		
		// Get the header text color from the dialog
		color = m_headerTextColorCtrl.getColor();
		if ( color != null && color.length() > 0 )
		{
			// Is this color valid?
			if ( m_headerTextColorCtrl.isColorValid( color ) == false )
			{
				// No, tell the user about the problem.
				Window.alert( GwtTeaming.getMessages().invalidHeaderTextColor( color ) );
				return null;
			}
		}
		lpProperties.setHeaderTextColor( color );
		
		// Get the content text color from the dialog
		color = m_contentTextColorCtrl.getColor();
		if ( color != null && color.length() > 0 )
		{
			// Is this color valid?
			if ( m_contentTextColorCtrl.isColorValid( color ) == false )
			{
				// No, tell the user about the problem.
				Window.alert( GwtTeaming.getMessages().invalidContentTextColor( color ) );
				return null;
			}
		}
		lpProperties.setContentTextColor( color );
		
		// Get the border color from the dialog
		color = m_borderColorCtrl.getColor();
		if ( color != null && color.length() > 0 )
		{
			// Is this color valid?
			if ( m_borderColorCtrl.isColorValid( color ) == false )
			{
				// No, tell the user about the problem.
				Window.alert( GwtTeaming.getMessages().invalidBorderColor( color ) );
				return null;
			}
		}
		lpProperties.setBorderColor( color );
		
		// Get the border width
		{
			String width;
			
			width = m_borderWidthCtrl.getText();
			if ( width == null )
				width = "";
			
			lpProperties.setBorderWidth( width );
		}
		
		// Get the style
		{
			String style = "mashup_dark.css";
			Boolean value;
			
			value = m_lightRB.getValue();
			if ( value != null && value == true )
				style = "mashup_light.css";
			
			lpProperties.setStyle( style );
		}
		
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
	public void init( GwtLandingPageProperties lpProperties, String binderId )
	{
		int index;
		
		// Remember the properties we started with.
		m_origLPProperties = lpProperties;
		
		m_binderId = binderId;
		
		m_inheritPropertiesCb.setValue( m_origLPProperties.getInheritProperties() );

		m_hideMastheadCB.setValue( m_origLPProperties.getHideMasthead() );
		m_hideSidebarCB.setValue( m_origLPProperties.getHideSidebar() );
		m_hideFooterCB.setValue( m_origLPProperties.getHideFooter() );
		m_hideMenuCB.setValue( m_origLPProperties.getHideMenu() );
		
		// Initialize the style
		{
			String style;
			
			m_lightRB.setValue( false );
			m_darkRB.setValue( false );
			
			style = m_origLPProperties.getStyle();
			if ( style != null && style.equalsIgnoreCase( "mashup_light.css" ) )
				m_lightRB.setValue( true );
			else
				m_darkRB.setValue( true );
		}
		
		// Issue an ajax request to get the list of file attachments for this binder.
		// When we get the response, updateListOfFileAttachments() will be called.
		m_selectedBgImgName = m_origLPProperties.getBackgroundImageName();
		getListOfFileAttachmentsFromServer();

		// Select the appropriate option in the background repeat listbox
		index = GwtClientHelper.selectListboxItemByValue( m_bgImgRepeatListbox, m_origLPProperties.getBackgroundRepeat() );
		if ( index == -1 )
			m_bgImgRepeatListbox.setSelectedIndex( 0 );

		// Initialize the selected background color
		m_bgColorCtrl.init( m_origLPProperties.getBackgroundColor() );
		
		// Initialize the header background color
		m_headerBgColorCtrl.init( m_origLPProperties.getHeaderBgColor() );
		
		// Initialize the header text color
		m_headerTextColorCtrl.init( m_origLPProperties.getHeaderTextColor() );
		
		// Initialize the content text color
		m_contentTextColorCtrl.init( m_origLPProperties.getContentTextColor() );
		
		// Initialize the border color.
		m_borderColorCtrl.init( m_origLPProperties.getBorderColor() );
		
		// Initialize the border width
		m_borderWidthCtrl.setText( m_origLPProperties.getBorderWidth() );
		
		// Enable/disable the property controls based on whether "inherit properties" checkbox is checked.
		dancePropertyControls();
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
				@Override
				public boolean editSuccessful( Object obj )
				{
					m_addFileAttachmentDlg.hide();
					
					if ( obj != null )
					{
						ArrayList<String> listOfFileNames;
						
						// Get the list of files that were added.
						listOfFileNames = (ArrayList<String>) obj;
						if ( listOfFileNames.size() > 0 )
						{
							// Select the first image that was added.
							m_selectedBgImgName = listOfFileNames.get( 0 );
						}
					}
					
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
				@Override
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
			@Override
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				m_addFileAttachmentDlg.setPopupPosition( x - offsetWidth + 100, y );
			}
		};
		m_addFileAttachmentDlg.setPopupPositionAndShow( posCallback );
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
		selectImageInListbox( m_bgImgListbox, m_selectedBgImgName );
	}

}
