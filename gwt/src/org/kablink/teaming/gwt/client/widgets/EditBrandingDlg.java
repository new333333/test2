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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtBrandingDataExt;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.GwtBrandingDataExt.BrandingRule;
import org.kablink.teaming.gwt.client.event.GetMastHeadLeftEdgeEvent;
import org.kablink.teaming.gwt.client.event.GetMastHeadLeftEdgeEvent.MastHeadLeftEdgeCallback;
import org.kablink.teaming.gwt.client.rpc.shared.GetFileAttachmentsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFileAttachmentsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.TinyMCEDlg.TinyMCEDlgClient;

import com.eemi.gwt.tour.client.Placement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
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
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * ?
 *  
 * @author jwootton
 */
public class EditBrandingDlg extends DlgBox
{
	private RadioButton m_useBrandingImgRb;
	private RadioButton m_useAdvancedBrandingRb;
	private ListBox m_brandingImgListbox;
	private ListBox m_backgroundImgListbox;
	private ListBox m_loginDlgImgListbox;
	private CheckBox m_stretchBgImgCb;
	private TextBox m_backgroundColorTextbox;
	private TextBox m_textColorTextbox;
	private InlineLabel m_sampleText;
	private AsyncCallback<VibeRpcResponse> m_rpcReadCallback = null;
	private GwtBrandingData m_origBrandingData;		// The original branding data we started with.
	private GwtTeamingMessages m_messages;
	private TinyMCEDlg m_editAdvancedBrandingDlg = null;
	private ArrayList<String> m_listOfFileAttachments = null;
	private final String m_noAvailableImages = "no available images";
	private AddFileAttachmentDlg m_addFileAttachmentDlg = null;
	private ColorPickerDlg m_colorPickerDlg = null;
	private TextBox m_destColorTextbox = null;
	private String m_advancedBranding = null;
	private FlowPanel m_rulesPanel = null;
	private RadioButton m_ruleSiteBrandingOnlyRb = null;
	private RadioButton m_ruleBothSiteAndBinderBrandingRb = null;
	private RadioButton m_ruleBinderOverridesRb = null;
	private CaptionPanel m_loginDlgCaptionPanel;
	private VibeTour m_siteBrandingTour;
	private String m_productName;
	private boolean m_siteBranding;

	// Offsets controlling how things are positioned within the site
	// branding tour.
	private final static int TOUR_BRANDING_X_OFFSET	=   50;
	private final static int TOUR_BRANDING_Y_OFFSET	=   10;
	private final static int TOUR_OTHER_X_OFFSET	=    0;
	private final static int TOUR_OTHER_Y_OFFSET	=    0;
	private final static int TOUR_RIGHT_X_OFFSET	=    0;
	private final static int TOUR_RIGHT_Y_OFFSET	= (-20);

	/*
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private EditBrandingDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		Integer width,
		Integer height )
	{
		super( autoHide, modal, xPos, yPos, width, height, DlgButtonMode.OkCancel );
		
		m_messages = GwtTeaming.getMessages();
		m_productName = GwtClientHelper.getProductName();
		
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
					m_messages.rpcFailure_GetBranding(),
					m_origBrandingData.getBinderId() );
				
				// Update the list of files the user can select from for the branding image and background image.
				updateListOfFileAttachments( null );
			}// end onFailure()
	
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
				
				// Update the list of files the user can select from for the branding image and background image.
				updateListOfFileAttachments( listOfFileAttachments );
			}// end onSuccess()
		};
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( m_messages.brandingDlgHeader(), editSuccessfulHandler, editCanceledHandler, null ); 
	}// end EditBrandingDlg()
	

	/**
	 * Clear out all branding information so no branding exists.
	 */
	private void clearBranding()
	{
		m_advancedBranding = null;
		m_brandingImgListbox.setSelectedIndex( -1 );
		m_backgroundImgListbox.setSelectedIndex( -1 );
		
		if ( m_loginDlgCaptionPanel.isVisible() )
			m_loginDlgImgListbox.setSelectedIndex( -1 );
		
		m_backgroundColorTextbox.setText( "" );
		m_textColorTextbox.setText( "" );
	}// end clearBranding()
	
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		m_siteBrandingTour = new VibeTour( "siteBrandingTour" );
		addBrandingAreaTourStep( m_messages.editBrandingDlg_Tour_start( m_productName ) );
		
		FlowPanel mainPanel = null;
		Label spacer;
		FlexTable table = null;
		int nextRow;
		
		mainPanel = new FlowPanel();
		mainPanel.setStyleName( "teamingDlgBoxContent" );

		table = new FlexTable();
		table.setCellSpacing( 4 );
		table.addStyleName( "dlgContent" );
		
		nextRow = 0;
		
		// Add the controls for "Use Branding image"
		{
			HorizontalPanel hPanel;
			
			m_useBrandingImgRb = new RadioButton( "brandingType", m_messages.useBrandingImgLabel() );
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
				FlowPanel flowPanel;
				Element linkElement;
				Element imgElement;
				Image browseImg;
				
				flowPanel = new FlowPanel();
//				flowPanel.getElement().getStyle().setMarginTop( 2, Style.Unit.PX );
				flowPanel.addStyleName( "editBrandingBrowseLink" );

				addFileAnchor = new Anchor();
				addFileAnchor.setTitle( m_messages.addImage() );
				flowPanel.add( addFileAnchor );
				
				addTourStep( Placement.RIGHT, flowPanel, m_messages.editBrandingDlg_Tour_brandingImage() );
				
				// Add a browse image to the link.
				browseImg = new Image( GwtTeaming.getImageBundle().browseHierarchy() );
				linkElement = addFileAnchor.getElement();
				imgElement = browseImg.getElement();
//				imgElement.getStyle().setMarginTop( 2, Style.Unit.PX );
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
						Scheduler.ScheduledCommand cmd;
						final Widget anchor;
						
						// Get the anchor the user clicked on.
						anchor = (Widget) event.getSource();
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Invoke the "Add file attachment" dialog.
								invokeAddFileAttachmentDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop() );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}//end onClick()
				};
				addFileAnchor.addClickHandler( clickHandler );
				
				hPanel.add( flowPanel );
			}
			
			table.setWidget( nextRow, 1, hPanel );
			++nextRow;
		}
		
		// Add the controls for "Use Advanced Branding"
		{
			m_useAdvancedBrandingRb = new RadioButton( "brandingType", m_messages.useAdvancedBrandingLabel() );
			
			// Add a link the user can click on to edit the advanced branding.
			{
				Anchor advancedAnchor;
				ClickHandler clickHandler;
				
				advancedAnchor = new Anchor( m_messages.advancedBtn() );
				advancedAnchor.setTitle( m_messages.editAdvancedBranding() );
				advancedAnchor.addStyleName( "editBrandingAdvancedLink" );
				
				addTourStep( Placement.RIGHT, advancedAnchor, m_messages.editBrandingDlg_Tour_advancedBranding() );
				
				// Add a clickhandler to the "advanced" link.  When the user clicks on the link we
				// will invoke the "edit advanced branding" dialog.
				clickHandler = new ClickHandler()
				{
					/**
					 * Invoke the "edit advanced branding" dialog
					 */
					@Override
					public void onClick( ClickEvent event )
					{
						Scheduler.ScheduledCommand cmd;
						final Anchor anchor;
						
						anchor = (Anchor) event.getSource();
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute() 
							{
								
								// Invoke the "Edit Advanced Branding" dialog.
								invokeEditAdvancedBrandingDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop() );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}//end onClick()
				};
				advancedAnchor.addClickHandler( clickHandler );

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
			
			table.setText( nextRow, 0, m_messages.backgroundImgLabel() );

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
				FlowPanel flowPanel;
				Element linkElement;
				Element imgElement;
				Image browseImg;
				
				flowPanel = new FlowPanel();
//				flowPanel.getElement().getStyle().setMarginTop( 2, Style.Unit.PX );
				flowPanel.addStyleName( "editBrandingBrowseLink" );

				addFileAnchor = new Anchor();
				addFileAnchor.setTitle( m_messages.addImage() );
				flowPanel.add( addFileAnchor );
				
				addTourStep( Placement.RIGHT, flowPanel, m_messages.editBrandingDlg_Tour_backgroundImage() );
				
				// Add the browse image to the link.
				browseImg = new Image( GwtTeaming.getImageBundle().browseHierarchy() );
				linkElement = addFileAnchor.getElement();
				imgElement = browseImg.getElement();
//				imgElement.getStyle().setMarginTop( 2, Style.Unit.PX );
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
						Scheduler.ScheduledCommand cmd;
						final Widget anchor;
						
						// Get the anchor the user clicked on.
						anchor = (Widget) event.getSource();

						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Invoke the "Add file attachment" dialog.
								invokeAddFileAttachmentDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop() );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}//end onClick()
				};
				addFileAnchor.addClickHandler( clickHandler );

				hPanel.add( flowPanel );
			}
			
			table.setWidget( nextRow, 1, hPanel );
			++nextRow;
			
			// Add a "stretch image" checkbox.
			m_stretchBgImgCb = new CheckBox( m_messages.stretchImg() );
			table.setWidget( nextRow, 1, m_stretchBgImgCb );
			++nextRow;
		}
		
		// Add an empty row to add some space between the "background image" listbox and the "background color" textbox.
		spacer = new Label( m_messages.colorDescription() );
		spacer.addStyleName( "margintop3" );
		spacer.addStyleName( "gray3" );
		table.setWidget( nextRow, 1, spacer );
		++nextRow;


		// Add the controls for "Background color"
		{
			KeyUpHandler keyUpHandler;
			ClickHandler clickHandler;
			HorizontalPanel hPanel;
			Anchor colorHint;
			Element linkElement;
			Element imgElement;
			Image colorBrowseImg;
			
			table.setText( nextRow, 0, m_messages.backgroundColorLabel() );

			// Create a panel where the background color control and hint will live.
			hPanel = new HorizontalPanel();
			
			// Add a text box where the user can enter the background color
			m_backgroundColorTextbox = new TextBox();
			m_backgroundColorTextbox.setVisibleLength( 20 );
			hPanel.add( m_backgroundColorTextbox );

			// Add a button next to the background color textbox the user can click on to invoke a color picker.

			colorHint = new Anchor();
			colorHint.setTitle( m_messages.displayColorPicker() );
			colorHint.addStyleName( "editBrandingBrowseLink" );
			colorHint.addStyleName( "displayInlineBlock" );
			
			addTourStep( Placement.RIGHT, hPanel, m_messages.editBrandingDlg_Tour_backgroundColor() );

			// Add the browse image to the link.
			colorBrowseImg = new Image( GwtTeaming.getImageBundle().colorPicker() );
			linkElement = colorHint.getElement();
			imgElement = colorBrowseImg.getElement();
			linkElement.appendChild( imgElement );
			hPanel.add( colorHint );
			table.setWidget( nextRow, 1, hPanel );

			// Add a keyup handler so we can update the "sample text" background color.
			keyUpHandler = new KeyUpHandler()
			{
				/**
				 * Update the background color of the sample text.
				 */
				@Override
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
				@Override
				public void onClick( ClickEvent event )
				{
					Widget anchor;
					
					// Get the anchor the user clicked on.
					anchor = (Widget) event.getSource();
					
					// Invoke the "Color Picker" dialog.
					invokeColorPickerDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop(), m_backgroundColorTextbox );
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
			Element linkElement;
			Element imgElement;
			Image colorBrowseImg;
			
			table.setText( nextRow, 0, m_messages.textColorLabel() );

			// Create a panel where the text color control and button will live.
			hPanel = new HorizontalPanel();
			
			// Add a text box where the user can enter the font color.
			m_textColorTextbox = new TextBox();
			m_textColorTextbox.setVisibleLength( 20 );
			hPanel.add( m_textColorTextbox );
			
			// Add a button next to the text color textbox the user can click on to invoke a color picker.
			textColorHint = new Anchor();
			textColorHint.setTitle( m_messages.displayColorPicker() );
			textColorHint.addStyleName( "editBrandingBrowseLink" );
			textColorHint.addStyleName( "displayInlineBlock" );
			
			addTourStep( Placement.RIGHT, hPanel, m_messages.editBrandingDlg_Tour_textColor() );
			addBrandingAreaTourStep( m_messages.editBrandingDlg_Tour_brandingArea( m_productName ) );

			// Add the browse image to the link.
			colorBrowseImg = new Image( GwtTeaming.getImageBundle().colorPicker() );
			linkElement = textColorHint.getElement();
			imgElement = colorBrowseImg.getElement();
			linkElement.appendChild( imgElement );

			hPanel.add( textColorHint );
			table.setWidget( nextRow, 1, hPanel );

			// Add a keyup handler so we can update the "sample text" color
			keyUpHandler = new KeyUpHandler()
			{
				/**
				 * Update the text color of the sample text.
				 */
				@Override
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
				@Override
				public void onClick( ClickEvent event )
				{
					Widget anchor;
					
					anchor = (Widget) event.getSource();
					
					// Invoke the "color picker" dialog
					invokeColorPickerDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop(), m_textColorTextbox );
				}//end onClick()
			};
			textColorHint.addClickHandler( clickHandler );

			++nextRow;
		}
		
		// Add a "sample text" field that will display the selected background color and text color.
		{
			HTMLTable.CellFormatter cellFormatter;

			cellFormatter = table.getCellFormatter();
			cellFormatter.addStyleName( nextRow, 1, "paddingTop8px" );

			m_sampleText = new InlineLabel( m_messages.sampleText() );
			m_sampleText.addStyleName( "editBrandingSampleText" );
			table.setWidget( nextRow, 1, m_sampleText );
			++nextRow;
		}
		
		// Create the controls that will be used to define the branding rules.
		{
			FlowPanel wrapperPanel;
			FlexTable.FlexCellFormatter cellFormatter;
			Label label;
			
			m_rulesPanel = new FlowPanel();
			
			label = new Label( m_messages.brandingRulesLabel() );
			m_rulesPanel.add( label );

			m_ruleSiteBrandingOnlyRb = new RadioButton( "brandingRule", m_messages.siteBrandingOnlyLabel() );
			wrapperPanel = new FlowPanel();
			wrapperPanel.add( m_ruleSiteBrandingOnlyRb );
			m_rulesPanel.add( wrapperPanel );
			
			m_ruleBothSiteAndBinderBrandingRb = new RadioButton( "brandingRule", m_messages.siteAndBinderBrandingLabel() );
			wrapperPanel = new FlowPanel();
			wrapperPanel.add( m_ruleBothSiteAndBinderBrandingRb );
			m_rulesPanel.add( wrapperPanel );
			
			m_ruleBinderOverridesRb = new RadioButton( "brandingRule", m_messages.binderOverridesBrandingLabel() );
			wrapperPanel = new FlowPanel();
			wrapperPanel.add( m_ruleBinderOverridesRb );
			m_rulesPanel.add( wrapperPanel );
			
			cellFormatter = table.getFlexCellFormatter();
			cellFormatter.setColSpan( nextRow, 0, 2 );
			table.setWidget( nextRow, 0, m_rulesPanel );
			++nextRow;
		}
		
		mainPanel.add( table );
		
		// Add a GroupBox to hold the controls needed to select the image used in the login dialog.
		{
			FlowPanel captionPanelMainPanel;
			FlexTable captionTable;
			Label hint;
			
			m_loginDlgCaptionPanel = new CaptionPanel( m_messages.editBrandingDlg_LoginDialogCaption() );
			m_loginDlgCaptionPanel.addStyleName( "editBrandingDlg_LoginDialogCaptionPanel" );
			
			addTourStep( Placement.TOP, m_loginDlgCaptionPanel, m_messages.editBrandingDlg_Tour_loginDlgImage( m_productName ) );
			
			captionPanelMainPanel = new FlowPanel();
			m_loginDlgCaptionPanel.add( captionPanelMainPanel );

			hint = new Label( m_messages.editBrandingDlg_LoginDialogImgHint() );
			hint.addStyleName( "editBrandingDlg_LoginDialogHint" );
			captionPanelMainPanel.add( hint );
			
			captionTable = new FlexTable();
			captionTable.setText( 0, 0, m_messages.editBrandingDlg_CurrentImage() );

			// Add a listbox the user can use to select the default image or a custom image.
			{
				m_loginDlgImgListbox = new ListBox( false );
				m_loginDlgImgListbox.setVisibleItemCount( 1 );
			
				captionTable.setWidget( 0, 1, m_loginDlgImgListbox );
			}
			
			// Add a link the user can click on to add a file
			{
				Anchor addFileAnchor;
				ClickHandler clickHandler;
				FlowPanel flowPanel;
				Element linkElement;
				Element imgElement;
				Image browseImg;
				
				flowPanel = new FlowPanel();
//				flowPanel.getElement().getStyle().setMarginTop( 2, Style.Unit.PX );
				flowPanel.addStyleName( "editBrandingBrowseLink" );

				addFileAnchor = new Anchor();
				addFileAnchor.setTitle( m_messages.addImage() );
				flowPanel.add( addFileAnchor );
				
				// Add the browse image to the link.
				browseImg = new Image( GwtTeaming.getImageBundle().browseHierarchy() );
				linkElement = addFileAnchor.getElement();
				imgElement = browseImg.getElement();
//				imgElement.getStyle().setMarginTop( 2, Style.Unit.PX );
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
						Scheduler.ScheduledCommand cmd;
						final Widget anchor;
						
						// Get the anchor the user clicked on.
						anchor = (Widget) event.getSource();

						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Invoke the "Add file attachment" dialog.
								invokeAddFileAttachmentDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop() );
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
				addFileAnchor.addClickHandler( clickHandler );

				captionTable.setWidget( 0, 2, flowPanel );
			}

			captionPanelMainPanel.add( captionTable );
			
			mainPanel.add( m_loginDlgCaptionPanel );
		}

		// Add a link the user can click on to clear all branding information
		{
			FlowPanel panel;
			Button clearBrandingBtn;
			ClickHandler clickHandler;
			
			panel = new FlowPanel();
			panel.addStyleName( "margintop3" );
			
			// Add "Clear branding" button
			clearBrandingBtn = new Button( m_messages.clearBrandingLabel() );
			panel.add( clearBrandingBtn );
			
			// Add a clickhandler to the "Clear branding" link.  When the user clicks on the link we
			// will clear all branding information.
			clickHandler = new ClickHandler()
			{
				/**
				 * Clear all branding information.
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					Scheduler.ScheduledCommand cmd;
					
					cmd = new Scheduler.ScheduledCommand()
					{
						@Override
						public void execute()
						{
							clearBranding();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			clearBrandingBtn.addClickHandler( clickHandler );
			
			mainPanel.add( panel );
		}
		
		addTourStep( Placement.BOTTOM, getHeaderPanel(), m_messages.editBrandingDlg_Tour_finish( m_productName ) );
		
		return mainPanel;
	}// end createContent()
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtBrandingData obj.
	 */
	@Override
	public Object getDataFromDlg()
	{
		GwtBrandingData brandingData;
		String imgName;
		String color;
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
		
		// Save away the advanced branding.
		brandingData.setBranding( m_advancedBranding );
		
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
				if ( imgName.equalsIgnoreCase( BrandingPanel.NO_IMAGE ) || imgName.equalsIgnoreCase( m_noAvailableImages ) )
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
		color = m_backgroundColorTextbox.getText();
		if ( color != null && color.length() > 0 )
		{
			// Is this background color valid?
			if ( isColorValid( color ) == false )
			{
				// No, tell the user about the problem.
				Window.alert( m_messages.invalidBackgroundColor( color ) );
				return null;
			}
		}
		brandingData.setBgColor( color );
		
		// Get the font color from the dialog.
		color = m_textColorTextbox.getText();
		if ( color != null && color.length() > 0 )
		{
			// Is this font color valid?
			if ( isColorValid( color ) == false )
			{
				// No, tell the user about the problem.
				Window.alert( m_messages.invalidTextColor( color ) );
				return null;
			}
		}
		brandingData.setFontColor( color );
		
		// Are we dealing with site branding?
		if ( m_origBrandingData.isSiteBranding() )
		{
			GwtBrandingDataExt.BrandingRule rule = GwtBrandingDataExt.BrandingRule.BRANDING_RULE_UNDEFINED;
			
			// Yes.  Get the branding rule.
			if ( GwtTeaming.m_requestInfo.isLicenseFilr() )
				rule = GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY;
			
			if ( m_ruleBinderOverridesRb.getValue() == true )
				rule = GwtBrandingDataExt.BrandingRule.BINDER_BRANDING_OVERRIDES_SITE_BRANDING;
			else if ( m_ruleBothSiteAndBinderBrandingRb.getValue() == true )
				rule = GwtBrandingDataExt.BrandingRule.DISPLAY_BOTH_SITE_AND_BINDER_BRANDING;
			else if ( m_ruleSiteBrandingOnlyRb.getValue() == true )
				rule = GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY;
			
			brandingData.setBrandingRule( rule );
			
			brandingData.setIsSiteBranding( true );
		
			// Save the login dialog image
			if ( m_loginDlgCaptionPanel.isVisible() )
			{
				// Is something selected in the "login dialog image" listbox?
				imgName = "";
				index = m_loginDlgImgListbox.getSelectedIndex();
				if ( index != -1 )
				{
					// Yes
					imgName = m_loginDlgImgListbox.getValue( index );
				}
				
				brandingData.setLoginDlgImageName( imgName );
			}
		}
		
		return brandingData;
	}// end getDataFromDlg()
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return m_backgroundColorTextbox;
	}// end getFocusWidget()
	
	
	/**
	 * 
	 */
	@Override
	public HelpData getHelpData()
	{
		HelpData helpData;
		
		helpData = new HelpData();
		helpData.setGuideName( HelpData.ADMIN_GUIDE );
		helpData.setPageId( "site-wide-brand" );
		
		return helpData;
	}

	/**
	 * Issue an ajax request to get the list of file attachments for this binder.
	 * When we get the response, updateListOfFileAttachments() will be called.
	 */
	private void getListOfFileAttachmentsFromServer()
	{
		GetFileAttachmentsCmd cmd;
		
		// Issue an ajax request to get the list of file attachments for this binder.
		cmd = new GetFileAttachmentsCmd( m_origBrandingData.getBinderId() );
		GwtClientHelper.executeCommand( cmd, m_rpcReadCallback );
	}// end getListOfFileAttachmentsFromServer()
	
	
	/**
	 * Initialize the controls in the dialog with the values from the branding data.
	 */
	public void init( GwtBrandingData brandingData )
	{
		String type;
		
		// Are we dealing with site branding?
		m_siteBranding = brandingData.isSiteBranding();
		if ( m_siteBranding )
		{
			// Yes
			setCaption( m_messages.brandingDlgSiteBrandingHeader() );
			setTourEnabled( true );
		}
		else
		{
			// No
			setCaption( m_messages.brandingDlgHeader() );
			setTourEnabled( false );
		}
		
		// Remember the branding data we started with.
		m_origBrandingData = brandingData;
		
		// Get the advanced branding.
		m_advancedBranding = m_origBrandingData.getBranding();
		
		// Issue an ajax request to get the list of file attachments for this binder.
		// When we get the response, updateListOfFileAttachments() will be called.
		getListOfFileAttachmentsFromServer();
		
		// Select the appropriate checkbox depending on whether "use branding image" or "use advanced branding" is selected.
		m_useBrandingImgRb.setValue( false );
		m_useAdvancedBrandingRb.setValue( false );
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
		
		// Are we dealing with site branding?
		if ( brandingData.isSiteBranding() )
		{
			BrandingRule brandingRule;

			// Yes
			// Show the login dialog branding controls
			m_loginDlgCaptionPanel.setVisible( true );
			
			// Are we running Filr?
			if ( GwtTeaming.m_requestInfo.isLicenseFilr() == false )
			{
				// No
				// Show the panel that holds the branding rules.
				m_rulesPanel.setVisible( true );

				// Select the appropriate radio button.
				brandingRule = brandingData.getBrandingRule(); 
				switch( brandingRule )
				{
				case DISPLAY_SITE_BRANDING_ONLY:
					m_ruleSiteBrandingOnlyRb.setValue( true );
					break;
					
				case DISPLAY_BOTH_SITE_AND_BINDER_BRANDING:
					m_ruleBothSiteAndBinderBrandingRb.setValue( true );
					break;
					
				case BINDER_BRANDING_OVERRIDES_SITE_BRANDING:
					m_ruleBinderOverridesRb.setValue( true );
					break;

				default:
					m_ruleSiteBrandingOnlyRb.setValue( false );
					m_ruleBothSiteAndBinderBrandingRb.setValue( false );
					m_ruleBinderOverridesRb.setValue( false );
					break;
				}// end switch()
			}
			else
			{
				// No, hide the panel that holds the branding rules.
				m_rulesPanel.setVisible( false );
			}
		}
		else
		{
			// No
			// Hide the panel that holds the branding rules.
			m_rulesPanel.setVisible( false );
			
			// Hide the login dialog branding controls
			m_loginDlgCaptionPanel.setVisible( false );
		}
		
	}// end init()
	

	/**
	 * Invoke the "Add file attachment" dialog.
	 */
	public void invokeAddFileAttachmentDlg( int x, int y )
	{
		// Have we already created an "Add file attachment" dialog?
		if ( m_addFileAttachmentDlg != null )
		{
			// Yes, clear any content from a previous invocation.
			m_addFileAttachmentDlg.setBinderId( m_origBrandingData.getBinderId() );
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
					
					// Issue an ajax request to get the list of file attachments for this binder.
					// When we get the response, updateListOfFileAttachments() will be called.
					getListOfFileAttachmentsFromServer();

					m_addFileAttachmentDlg.hide();
					
					return true;
				}// end editSuccessful()
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
				}// end editCanceled()
			};

			// No, create an "Add file attachment" dialog.
			m_addFileAttachmentDlg = new AddFileAttachmentDlg(
														editSuccessfulHandler,
														editCanceledHandler,
														false,
														true,
														x,
														y,
														m_origBrandingData.getBinderId() );
		}

		m_addFileAttachmentDlg.show();
	}// end invokeAddFileAttachmentDlg()
	
	/**
	 * Invoke the "Color Picker" dialog.
	 */
	public void invokeColorPickerDlg( int x, int y, TextBox destTextbox )
	{
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
				@Override
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
						updateSampleTextColor();
					}
					
					return true;
				}// end editSuccessful()
			};
				
			editCanceledHandler = new EditCanceledHandler()
			{
				/**
				 * This method gets called when the user presses cancel in the "Color Picker" dialog.
				 */
				@Override
				public boolean editCanceled()
				{
					m_colorPickerDlg.hide();
					
					return true;
				}// end editCanceled()
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

		m_colorPickerDlg.show();
	}// end invokeColorPickerDlg()


	/**
	 * Invoke the "Edit Advanced Branding" dialog.
	 */
	public void invokeEditAdvancedBrandingDlg( final int x, final int y )
	{
		final EditSuccessfulHandler editSuccessfulHandler = new EditSuccessfulHandler()
		{
			/**
			 * This method gets called when user user presses ok in the "Edit Advanced Branding" dialog.
			 */
			@Override
			@SuppressWarnings("unused")
			public boolean editSuccessful( Object obj )
			{
				if ( obj instanceof String )
				{
					if ( obj != null )
						m_advancedBranding = new String( (String) obj );
					else
						m_advancedBranding = null;
				}
				
				m_editAdvancedBrandingDlg.hide();

				return true;
			}// end editSuccessful()
		};
			
		final EditCanceledHandler editCanceledHandler = new EditCanceledHandler()
		{
			/**
			 * This method gets called when the user presses cancel in the "Edit Advanced Branding" dialog.
			 */
			@Override
			public boolean editCanceled()
			{
				m_editAdvancedBrandingDlg.hide();
				
				return true;
			}// end editCanceled()
		};

		// No, create a "Edit Advanced Branding" dialog.
		TinyMCEDlg.createBrandingTinyMCEConfiguration(
			m_origBrandingData.getBinderId(),
			new TinyMCEDlgClient()
		{				
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// end onUnavailable()
			
			@Override
			public void onSuccess( AbstractTinyMCEConfiguration config )
			{
				final AbstractTinyMCEConfiguration tinyMCEConfig = new BrandingTinyMCEConfiguration( m_origBrandingData.getBinderId() );
				tinyMCEConfig.setListOfFileAttachments( m_listOfFileAttachments );
				Scheduler.ScheduledCommand dlgCreator;
				dlgCreator = new Scheduler.ScheduledCommand() {
					@Override
					public void execute()
					{
						createTinyMCE(
							tinyMCEConfig,
							editSuccessfulHandler,
							editCanceledHandler,
							x,
							y );
					}// end execute()
				};
				Scheduler.get().scheduleDeferred( dlgCreator );
			}// end onSuccess()
			
			@Override
			public void onSuccess( TinyMCEDlg dlg )
			{
				// Unused.
			}// end onSuccess()
		} );
	}// end invokeEditAdvancedBrandingDlg()
	
	private void createTinyMCE(
		final AbstractTinyMCEConfiguration tinyMCEConfig,
		final EditSuccessfulHandler editSuccessfulHandler,
		final EditCanceledHandler editCanceledHandler,
		final int x,
		final int y)
	{		
		TinyMCEDlg.createAsync(
			m_messages.editAdvancedBranding(),
			tinyMCEConfig,
			editSuccessfulHandler,
			editCanceledHandler,
			false,
			true,
			x,
			y,
			null,
			new TinyMCEDlgClient()
		{				
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}// onUnavailable()
			
			@Override
			public void onSuccess( AbstractTinyMCEConfiguration config )
			{
				// Unused.
			}// end onSuccess()
			
			@Override
			public void onSuccess( TinyMCEDlg dlg )
			{
				m_editAdvancedBrandingDlg = dlg;
				PopupPanel.PositionCallback posCallback = new PopupPanel.PositionCallback()
				{
					/**
					 * 
					 */
					@Override
					public void setPosition( int offsetWidth, int offsetHeight )
					{
						int xPos;
						int yPos;
						
						xPos = getAbsoluteLeft() - 50;
						yPos = y;
						
						m_editAdvancedBrandingDlg.setPopupPosition( xPos, yPos );
					}// end setPosition()
				};
				m_editAdvancedBrandingDlg.setPopupPositionAndShow( posCallback );
				m_editAdvancedBrandingDlg.setText( m_advancedBranding );
			}// end onSuccess()
		} );
	}// end createTinyMCE()


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
		
		element = m_sampleText.getElement();
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
	}// end isColorValid()
	
	
	/**
	 * Remove the styles that were added to the given widget when the user moved the mouse over the widget.
	 */
//	private void removeMouseOverStyles( Widget widget )
//	{
//		widget.removeStyleName( "subhead-control-bg2" );
//		widget.addStyleName( "subhead-control-bg1" );
//	}// end removeMouseOverStyles()
	
	
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
		else
		{
			// No, try to select the default Teaming image option.
			index = GwtClientHelper.selectListboxItemByValue( listbox, BrandingPanel.DEFAULT_TEAMING_IMAGE );
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
				index = GwtClientHelper.selectListboxItemByValue( listbox, BrandingPanel.DEFAULT_TEAMING_IMAGE );
				
				// Did we select the default teaming image?
				if ( index == -1 )
				{
					// No, select "no image"
					GwtClientHelper.selectListboxItemByValue( listbox, BrandingPanel.NO_IMAGE );
				}
			}
		}
	}// end selectImageInListbox()
	
	
	/**
	 * Update the 2 listboxes that allow the user to select the branding image and the background image
	 * from the list of files attached to the binder.
	 */
	private void updateListOfFileAttachments( ArrayList<String> listOfFileAttachments )
	{
		int i;

		m_listOfFileAttachments = listOfFileAttachments;
		
		// Empty the "branding image" and "background image" listboxes.
		m_brandingImgListbox.clear();
		m_backgroundImgListbox.clear();
		if ( m_loginDlgCaptionPanel.isVisible() )
			m_loginDlgImgListbox.clear();
		
		// Add an entry called "None" to the branding listbox.  The user can select "None" if
		// they don't want to use a branding image.
		m_brandingImgListbox.addItem( m_messages.imgNone(), BrandingPanel.NO_IMAGE );
		
		// Add an entry called "None" to the login dialog image listbox.  The user can select "None" if
		// they don't want to use an image in the login dialog.
		if ( m_loginDlgCaptionPanel.isVisible() )
			m_loginDlgImgListbox.addItem( m_messages.imgNone(), BrandingPanel.NO_IMAGE );
		
		// Add a Novell Teaming or a Kablink Teaming entry to the branding listbox depending on
		// whether we are running Novell or Kablink Teaming.  The user can select this entry if
		// they want to use the Novell/Kablink Teaming branding image.
		if ( GwtTeaming.m_requestInfo.isLicenseFilr() )
		{
			m_brandingImgListbox.addItem( m_messages.novellFilr(), BrandingPanel.DEFAULT_TEAMING_IMAGE );
			if ( m_loginDlgCaptionPanel.isVisible() )
				m_loginDlgImgListbox.addItem( m_messages.novellFilr(), BrandingPanel.DEFAULT_TEAMING_IMAGE );
		}
		else if ( GwtMainPage.m_novellTeaming )
		{
			m_brandingImgListbox.addItem( m_messages.novellTeaming(), BrandingPanel.DEFAULT_TEAMING_IMAGE );
			if ( m_loginDlgCaptionPanel.isVisible() )
				m_loginDlgImgListbox.addItem( m_messages.novellTeaming(), BrandingPanel.DEFAULT_TEAMING_IMAGE );
		}
		else
		{
			m_brandingImgListbox.addItem( m_messages.kablinkTeaming(), BrandingPanel.DEFAULT_TEAMING_IMAGE );
			if ( m_loginDlgCaptionPanel.isVisible() )
				m_loginDlgImgListbox.addItem( m_messages.kablinkTeaming(), BrandingPanel.DEFAULT_TEAMING_IMAGE );
		}

		// Do we have any file attachments?
		if ( listOfFileAttachments.size() > 0 )
		{
			// Yes
			// Add an entry called "None" to the background listbox.  The user can select "None" if
			// they don't want to use a background image.
			m_backgroundImgListbox.addItem( m_messages.imgNone(), BrandingPanel.NO_IMAGE );

			// Add each file name to the "branding image" listbox and to the "background image" listbox.
			for (i = 0; listOfFileAttachments != null && i < listOfFileAttachments.size(); ++i)
			{
				String fileName;
				
				fileName = listOfFileAttachments.get( i );
				m_brandingImgListbox.addItem( fileName, fileName );
				m_backgroundImgListbox.addItem( fileName, fileName );
				if ( m_loginDlgCaptionPanel.isVisible() )
					m_loginDlgImgListbox.addItem( fileName, fileName );
			}
		}
		else
		{
			// No
			// Add an entry called "No available images" to the listboxes.
			m_backgroundImgListbox.addItem( m_messages.noImagesAvailable(), m_noAvailableImages );
		}
		
		// Select the branding image file in the listbox that is defined in the original branding data.
		selectImageInListbox( m_brandingImgListbox, m_origBrandingData.getBrandingImageName() );
		
		// Select the background image file in the listbox that is defined in the original branding data. 
		selectImageInListbox( m_backgroundImgListbox, m_origBrandingData.getBgImageName() );

		// Select the login dialog image file in the listbox that is defined in the original branding data.
		if ( m_loginDlgCaptionPanel.isVisible() )
			selectImageInListbox( m_loginDlgImgListbox, m_origBrandingData.getLoginDlgImageName() );
		
	}// end updateListOfFileAttachments()
	
	
	/**
	 * Update the background color of the sample text with the color that is found in the textbox.
	 */
	private void updateSampleTextBgColor()
	{
		String color;
		
		// Get the background color
		color = m_backgroundColorTextbox.getText();
		updateSampleTextBgColor( color );
	}// end updateSampleTextBgColor()
	
	
	/**
	 * Update the background color of the sample text with the color that is found in the textbox.
	 */
	private void updateSampleTextBgColor( String color )
	{
		Element element;
		Style style;
		
		element = m_sampleText.getElement();
		style = element.getStyle();
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
	}// end updateSampleTextBgColor()
	
	
	/**
	 * Update the text color of the sample text with the color found in the textbox.
	 */
	private void updateSampleTextColor()
	{
		Element element;
		Style style;
		String color;
		String bgColor;
		
		element = m_sampleText.getElement();
		style = element.getStyle();
		
		// Set the text color
		color = m_textColorTextbox.getText();
		style.clearColor();

		// Default text color is white
		if ( color == null || color.length() == 0 )
			color = "white";
		
		// Did the user specify a background color?
		bgColor = m_backgroundColorTextbox.getText();
		if ( bgColor == null || bgColor.length() == 0 )
		{
			// No, use a grey background
			updateSampleTextBgColor( "grey" );
		}
		
		try
		{
			style.setColor( color );
		}
		catch( Exception ex )
		{
			// Nothing to do.
		}
	}// end updateSampleTextColor()

	/*
	 * Adds a tour step to the tour.
	 */
	private void addTourStep( Placement placement, Widget widget, String content, int xOffset, int yOffset )
	{
		VibeTourStep step = new VibeTourStep( placement, widget );
		step.setContent( content );
		setStepPosition( placement, step, xOffset, yOffset );
		m_siteBrandingTour.addStep( step );
	}
	
	private void addTourStep( Placement placement, Widget widget, String content )
	{
		// Calculate the default offsets...
		int xOffset;
		int yOffset;
		switch ( placement )
		{
		default:
		case TOP:
		case BOTTOM:
		case LEFT:
			xOffset = TOUR_OTHER_X_OFFSET;
			yOffset = TOUR_OTHER_Y_OFFSET;
			break;
			
		case RIGHT:
			xOffset = TOUR_RIGHT_X_OFFSET;
			yOffset = TOUR_RIGHT_Y_OFFSET;
			break;
		}
		
		// ...and always use the initial form of the method.
		addTourStep( placement, widget, content, xOffset, yOffset );
	}
	
	
	/*
	 * Adds a tour step for the branding area to the tour.
	 */
	private void addBrandingAreaTourStep( final String content )
	{
		GwtTeaming.fireEvent(
			new GetMastHeadLeftEdgeEvent(
				new MastHeadLeftEdgeCallback()
				{
					@Override
					public void mhLeftEdgeWidget( Widget mhLeftEdge )
					{
						addTourStep(
							Placement.RIGHT,
							mhLeftEdge,
							content,
							TOUR_BRANDING_X_OFFSET,		// x and...
							TOUR_BRANDING_Y_OFFSET );	// ...y offsets within the branding panel.
					}
				} ) );
	}
	
	/*
	 * Sets the step's position based on it placement.
	 */
	private void setStepPosition( Placement placement, VibeTourStep step, int xOffset, int yOffset )
	{
		step.setXOffset( xOffset );
		step.setYOffset( yOffset );
		if ( Placement.BOTTOM.equals( placement ))
		{
			step.centerXOffset();
		}
	}

	/**
	 * Overrides DlgBox.invokeTour() to connect the site branding tour.
	 */
	@Override
	public void invokeTour()
	{
		if ( m_siteBranding && ( null != m_siteBrandingTour ) )
		{
			m_siteBrandingTour.start();
		}
	}
	
	/**
	 * Overrides DlgBox.stopTour() to connect the site branding tour.
	 */
	@Override
	public void stopTour()
	{
		if ( m_siteBranding && ( null != m_siteBrandingTour ) )
		{
			m_siteBrandingTour.stop();
		}
	}
	
	/**
	 * Callback interface to interact with the edit branding dialog
	 * asynchronously after it loads. 
	 */
	public interface EditBrandingDlgClient {
		void onSuccess(EditBrandingDlg ebDlg);
		void onUnavailable();
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void createDlg(
		final boolean autoHide,
		final boolean modal,
		final int left,
		final int top,
		final Integer width,
		final Integer height,
		final EditSuccessfulHandler editSuccessfulHandler,
		final EditCanceledHandler editCanceledHandler,
		final EditBrandingDlgClient ebDlgClient )
	{
		GWT.runAsync( EditBrandingDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditBrandingDlg() );
				ebDlgClient.onUnavailable();
			}

			@Override
			public void onSuccess()
			{
				EditBrandingDlg ebDlg;
				
				ebDlg = new EditBrandingDlg(
											editSuccessfulHandler,
											editCanceledHandler,
											autoHide,
											modal,
											left,
											top,
											width,
											height );
				
				if ( ebDlgClient != null )
					ebDlgClient.onSuccess( ebDlg );
			}
		} );
	}

	/**
	 * Executes code through the GWT.runAsync() method to ensure that all of the
	 * executing code is in this split point.
	 */
	public static void initAndShow(
		final EditBrandingDlg dlg,
		final GwtBrandingData brandingData,
		final Integer left,
		final Integer top,
		final Integer width,
		final Integer height,
		final EditBrandingDlgClient ebDlgClient )
	{
		GWT.runAsync( EditBrandingDlg.class, new RunAsyncCallback()
		{
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditBrandingDlg() );
				ebDlgClient.onUnavailable();
			}

			@Override
			public void onSuccess()
			{
				if ( width != null && height != null )
					dlg.setPixelSize( width, height );
				
				dlg.init( brandingData );
				
				if ( left != null && top != null )
					dlg.setPopupPosition( left, top );
				
				dlg.show();
			}
		} );
	}
}// end EditBrandingDlg
