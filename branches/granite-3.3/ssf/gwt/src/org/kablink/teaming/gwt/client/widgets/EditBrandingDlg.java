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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtBrandingData;
import org.kablink.teaming.gwt.client.GwtBrandingDataExt;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtBrandingDataExt.BrandingRule;
import org.kablink.teaming.gwt.client.rpc.shared.GetFileAttachmentsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetFileAttachmentsRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.TinyMCEDlg.TinyMCEDlgClient;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
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
	private AsyncCallback<VibeRpcResponse> m_rpcReadCallback = null;
	private GwtBrandingData m_origBrandingData;		// The original branding data we started with.
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
					GwtTeaming.getMessages().rpcFailure_GetBranding(),
					m_origBrandingData.getBinderId() );
				
				// Update the list of files the user can select from for the branding image and background image.
				updateListOfFileAttachments( null );
			}// end onFailure()
	
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
				
				// Update the list of files the user can select from for the branding image and background image.
				updateListOfFileAttachments( listOfFileAttachments );
			}// end onSuccess()
		};
		
		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().brandingDlgHeader(), editSuccessfulHandler, editCanceledHandler, null ); 
	}// end EditBrandingDlg()
	

	/**
	 * Clear out all branding information so no branding exists.
	 */
	private void clearBranding()
	{
		m_advancedBranding = null;
		m_brandingImgListbox.setSelectedIndex( -1 );
		m_backgroundImgListbox.setSelectedIndex( -1 );
		m_backgroundColorTextbox.setText( "" );
		m_textColorTextbox.setText( "" );
	}// end clearBranding()
	
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	public Panel createContent( Object props )
	{
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
						Widget anchor;
						
						// Get the anchor the user clicked on.
						anchor = (Widget) event.getSource();
						
						removeMouseOverStyles( anchor.getParent() );
						
						// Invoke the "Add file attachment" dialog.
						invokeAddFileAttachmentDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop() );
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
						removeMouseOverStyles( widget.getParent() );
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
						Anchor anchor;
						
						anchor = (Anchor) event.getSource();
						
						// Invoke the "Edit Advanced Branding" dialog.
						invokeEditAdvancedBrandingDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop() );
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
						
						// Remove the background color we added to the anchor when the user moved the mouse over the anchor.
						widget = (Widget)event.getSource();
						removeMouseOverStyles( widget );
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
						Widget anchor;
						
						// Get the anchor the user clicked on.
						anchor = (Widget) event.getSource();
						
						removeMouseOverStyles( anchor.getParent() );

						// Invoke the "Add file attachment" dialog.
						invokeAddFileAttachmentDlg( anchor.getAbsoluteLeft(), anchor.getAbsoluteTop() );
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
						removeMouseOverStyles( widget.getParent() );
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

			m_sampleText = new InlineLabel( GwtTeaming.getMessages().sampleText() );
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
			
			label = new Label( GwtTeaming.getMessages().brandingRulesLabel() );
			m_rulesPanel.add( label );

			m_ruleSiteBrandingOnlyRb = new RadioButton( "brandingRule", GwtTeaming.getMessages().siteBrandingOnlyLabel() );
			wrapperPanel = new FlowPanel();
			wrapperPanel.add( m_ruleSiteBrandingOnlyRb );
			m_rulesPanel.add( wrapperPanel );
			
			m_ruleBothSiteAndBinderBrandingRb = new RadioButton( "brandingRule", GwtTeaming.getMessages().siteAndBinderBrandingLabel() );
			wrapperPanel = new FlowPanel();
			wrapperPanel.add( m_ruleBothSiteAndBinderBrandingRb );
			m_rulesPanel.add( wrapperPanel );
			
			m_ruleBinderOverridesRb = new RadioButton( "brandingRule", GwtTeaming.getMessages().binderOverridesBrandingLabel() );
			wrapperPanel = new FlowPanel();
			wrapperPanel.add( m_ruleBinderOverridesRb );
			m_rulesPanel.add( wrapperPanel );
			
			cellFormatter = table.getFlexCellFormatter();
			cellFormatter.setColSpan( nextRow, 0, 2 );
			table.setWidget( nextRow, 0, m_rulesPanel );
			++nextRow;
		}
		
		// Add a link the user can click on to clear all branding information
		{
			Anchor clearBrandingAnchor;
			ClickHandler clickHandler;
			MouseOverHandler mouseOverHandler;
			MouseOutHandler mouseOutHandler;
			
			clearBrandingAnchor = new Anchor( GwtTeaming.getMessages().clearBrandingLabel() );
			clearBrandingAnchor.setTitle( GwtTeaming.getMessages().clearBrandingLabel() );
			clearBrandingAnchor.addStyleName( "editBrandingLink" );
			clearBrandingAnchor.addStyleName( "editBrandingAdvancedLink" );
			clearBrandingAnchor.addStyleName( "subhead-control-bg1" );
			clearBrandingAnchor.addStyleName( "roundcornerSM" );
			
			// Add a clickhandler to the "Clear branding" link.  When the user clicks on the link we
			// will clear all branding information.
			clickHandler = new ClickHandler()
			{
				/**
				 * Clear all branding information.
				 */
				public void onClick( ClickEvent event )
				{
					clearBranding();
				}//end onClick()
			};
			clearBrandingAnchor.addClickHandler( clickHandler );
			
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
			clearBrandingAnchor.addMouseOverHandler( mouseOverHandler );

			// Add a mouse-out handler
			mouseOutHandler = new MouseOutHandler()
			{
				/**
				 * 
				 */
				public void onMouseOut( MouseOutEvent event )
				{
					Widget widget;
					
					// Remove the background color we added to the anchor when the user moved the mouse over the anchor.
					widget = (Widget)event.getSource();
					widget.removeStyleName( "subhead-control-bg2" );
					widget.addStyleName( "subhead-control-bg1" );
				}// end onMouseOut()
			};
			clearBrandingAnchor.addMouseOutHandler( mouseOutHandler );

			table.setWidget( nextRow, 0, clearBrandingAnchor );
			++nextRow;
		}

		mainPanel.add( table );
		
		return mainPanel;
	}// end createContent()
	
	
	/**
	 * Get the data from the controls in the dialog box and store the data in a GwtBrandingData obj.
	 */
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
				Window.alert( GwtTeaming.getMessages().invalidBackgroundColor( color ) );
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
				Window.alert( GwtTeaming.getMessages().invalidTextColor( color ) );
				return null;
			}
		}
		brandingData.setFontColor( color );
		
		// Are we dealing with site branding?
		if ( m_origBrandingData.isSiteBranding() )
		{
			GwtBrandingDataExt.BrandingRule rule = GwtBrandingDataExt.BrandingRule.BRANDING_RULE_UNDEFINED;
			
			// Yes.  Get the branding rule.
			if ( m_ruleBinderOverridesRb.getValue() == true )
				rule = GwtBrandingDataExt.BrandingRule.BINDER_BRANDING_OVERRIDES_SITE_BRANDING;
			else if ( m_ruleBothSiteAndBinderBrandingRb.getValue() == true )
				rule = GwtBrandingDataExt.BrandingRule.DISPLAY_BOTH_SITE_AND_BINDER_BRANDING;
			else if ( m_ruleSiteBrandingOnlyRb.getValue() == true )
				rule = GwtBrandingDataExt.BrandingRule.DISPLAY_SITE_BRANDING_ONLY;
			
			brandingData.setBrandingRule( rule );
			
			brandingData.setIsSiteBranding( true );
		}
		
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

			// Yes, show the panel that holds the branding rules.
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
			GwtTeaming.getMessages().editAdvancedBranding(),
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
	private void removeMouseOverStyles( Widget widget )
	{
		widget.removeStyleName( "subhead-control-bg2" );
		widget.addStyleName( "subhead-control-bg1" );
	}// end removeMouseOverStyles()
	
	
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
		
		// Add an entry called "None" to the branding listbox.  The user can select "None" if
		// they don't want to use a branding image.
		m_brandingImgListbox.addItem( GwtTeaming.getMessages().imgNone(), BrandingPanel.NO_IMAGE );
		
		// Add a Novell Teaming or a Kablink Teaming entry to the branding listbox depending on
		// whether we are running Novell or Kablink Teaming.  The user can select this entry if
		// they want to use the Novell/Kablink Teaming branding image.
		if ( GwtMainPage.m_novellTeaming )
			m_brandingImgListbox.addItem( GwtTeaming.getMessages().novellTeaming(), BrandingPanel.DEFAULT_TEAMING_IMAGE );
		else
			m_brandingImgListbox.addItem( GwtTeaming.getMessages().kablinkTeaming(), BrandingPanel.DEFAULT_TEAMING_IMAGE );

		// Do we have any file attachments?
		if ( listOfFileAttachments.size() > 0 )
		{
			// Yes
			// Add an entry called "None" to the background listbox.  The user can select "None" if
			// they don't want to use a background image.
			m_backgroundImgListbox.addItem( GwtTeaming.getMessages().imgNone(), BrandingPanel.NO_IMAGE );

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
		
		element = m_sampleText.getElement();
		style = element.getStyle();
		
		// Set the text color
		color = m_textColorTextbox.getText();
		style.clearColor();
		if ( color != null && color.length() > 0 )
		{
			try
			{
				style.setColor( color );
			}
			catch( Exception ex )
			{
				// Nothing to do.
			}
		}
	}// end updateSampleTextColor()
	
	/**
	 * Callback interface to interact with the edit branding dialog
	 * asynchronously after it loads. 
	 */
	public interface EditBrandingDlgClient {
		void onSuccess(EditBrandingDlg ebDlg);
		void onUnavailable();
	}

	/**
	 * Loads the EditBrandingDlg split point and returns an instance of it
	 * via the callback.
	 *
	 * @param editSuccessfulHandler
	 * @param editCanceledHandler
	 * @param autoHide
	 * @param modal
	 * @param xPos
	 * @param yPos 
	 * @param ebDlgClient
	 */
	public static void createAsync(
		final EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		final EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		final boolean autoHide,
		final boolean modal,
		final int xPos,
		final int yPos,
		final EditBrandingDlgClient ebDlgClient )
	{
		GWT.runAsync( EditBrandingDlg.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				EditBrandingDlg ebDlg = new EditBrandingDlg( editSuccessfulHandler, editCanceledHandler, autoHide, modal, xPos, yPos );
				ebDlgClient.onSuccess( ebDlg );
			}// end onSuccess()
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_EditBrandingDlg() );
				ebDlgClient.onUnavailable();
			}// end onFailure()
		} );
	}// end createAsync()
}// end EditBrandingDlg
