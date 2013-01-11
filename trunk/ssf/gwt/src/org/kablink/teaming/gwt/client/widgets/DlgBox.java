/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HelpData;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;

/**
 * A general dialog box class for use throughout Vibe.
 * 
 * @author jwootton
 */
public abstract class DlgBox extends PopupPanel
	implements ClickHandler
{
	private EditSuccessfulHandler	m_editSuccessfulHandler;	// Handler to call when the user presses Ok.
	private EditCanceledHandler		m_editCanceledHandler;		// Handler to call when the user presses Cancel.
	private DlgButtonMode			m_dlgBtnMode;				//
	private boolean					m_showFooter;				//
	private Button					m_okBtn;					//
	private Button					m_cancelBtn;				//
	private HelpData				m_helpData;					//
	private FlowPanel 				m_closePanel;				//
	private Image 					m_closeImg;					//
	protected FocusWidget 			m_focusWidget;				// Widget that should receive the focus when this dialog is shown.
	protected boolean 				m_modal;					//
	protected boolean 				m_visible;					//
	private Label 					m_caption;					//
	private FlowPanel				m_captionImagePanel;		//
	private Panel					m_headerPanel;				//
	private FlowPanel				m_bodyPanel;
	private FlowPanel 				m_errorPanel;				//
	private Panel 					m_contentPanel;				//
	private FlowPanel				m_footerPanel;				//
	private FlowPanel				m_statusPanel;
	private InlineLabel 			m_statusLabel;				//
	private Image					m_statusImg;				//
	private int 					m_id;						//
	private boolean					m_fixedSize;				//
	private Integer					m_height;					//
	private boolean					m_useOverflowAutoOnContent = true;
    	
	protected static int			m_numDlgsVisible = 0;		// Number of dialogs that are currently visible.
	private   static int			m_uniqueId       = 100;		//
	
	public enum DlgButtonMode {
		Cancel,
		Close,
		Ok,
		OkCancel,
		YesNo,
	}
	
	/**
	 * 
	 */
	public DlgBox(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		this( autoHide, modal, xPos, yPos, null, null, DlgButtonMode.OkCancel, true, true );
	}
	
	/**
	 * 
	 */
	public DlgBox(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		boolean showFooter )
	{
		this( autoHide, modal, xPos, yPos, null, null, DlgButtonMode.OkCancel, showFooter, true );
	}
	
	/**
	 * 
	 */
	public DlgBox(
		boolean autoHide,
		boolean modal )
	{
		this( autoHide, modal, 0, 0, null, null, DlgButtonMode.OkCancel, true, true );
	}
	
	/**
	 * 
	 */
	public DlgBox(
		boolean autoHide,
		boolean modal,
		boolean showFooter )
	{
		this( autoHide, modal, 0, 0, null, null, DlgButtonMode.OkCancel, showFooter, true );
	}
	
	/**
	 * 
	 */
	public DlgBox(
		boolean autoHide,
		boolean modal,
		DlgButtonMode dlgBtnMode )
	{
		this( autoHide, modal, 0, 0, null, null, dlgBtnMode, true, true );
	}
	
	/**
	 * 
	 */
	public DlgBox(
		boolean autoHide,
		boolean modal,
		DlgButtonMode dlgBtnMode,
		boolean showFooter )
	{
		this( autoHide, modal, 0, 0, null, null, dlgBtnMode, showFooter, true );
	}
	
	/**
	 * 
	 */
	public DlgBox(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		DlgButtonMode dlgBtnMode )
	{
		this( autoHide, modal, xPos, yPos, null, null, dlgBtnMode, true, true );
	}
	
	/**
	 * 
	 */
	public DlgBox(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		Integer width,
		Integer height,
		DlgButtonMode dlgBtnMode )
	{
		this( autoHide, modal, xPos, yPos, width, height, dlgBtnMode, true, true );
	}
	
	/**
	 * 
	 */
	public DlgBox(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		Integer width,
		Integer height,
		DlgButtonMode dlgBtnMode,
		boolean useOverflowAutoOnContent )
	{
		this( autoHide, modal, xPos, yPos, width, height, dlgBtnMode, true, useOverflowAutoOnContent );
	}
	
	/**
	 * 
	 */
	public DlgBox(
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos,
		Integer width,
		Integer height,
		DlgButtonMode dlgBtnMode,
		boolean showFooter,
		boolean useOverflowAutoOnContent )
	{
		// Since we are providing the modal behavior, always pass false into super()
		super( autoHide, false );
		
		m_useOverflowAutoOnContent = useOverflowAutoOnContent;
		m_fixedSize = false;
		if ( width != null && height != null )
		{
			m_fixedSize = true;
			m_height = height;

			setWidth( width.toString() + "px" );
			setHeight( height.toString() + "px" );
		}
		
		// Should this dialog be modal?
		m_modal = modal;
	
		m_focusWidget = null;
		m_dlgBtnMode = dlgBtnMode;
		m_showFooter = showFooter;
		
		// Override the style used for PopupPanel
		m_id = m_uniqueId;
		++m_uniqueId;
		setStyleName( "teamingDlgBox" );
		setStyleName( "teamingDlgBox_NoClip", true );
		getElement().setId( "teamingDlgBox-" + String.valueOf( m_id ) );
		
		setAnimationEnabled( true );
		
		setPopupPosition( xPos, yPos );
	}// end DlgBox()
	
	
	/**
	 * 
	 */
	public void clearErrorPanel()
	{
		if ( m_errorPanel != null )
			m_errorPanel.clear();
	}
	
	/**
	 * Get the Composite that holds the widgets that make up the content of the dialog box.
	 */
	public abstract Panel createContent( Object propertiesObj );
	
	/**
	 * Create the header, content and footer for the dialog box.
	 */
	public void createAllDlgContent(
		String	caption,
		EditSuccessfulHandler editSuccessfulHandler,// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 	// This gets called when the user presses the Cancel button
		Object properties ) 					// Where properties used in the dialog are read from and saved to.
	{
		FlowPanel	panel;
		
		panel = new FlowPanel();
		
		// Create a close image that is positioned in the top-right-hand corner
		if ( m_dlgBtnMode == DlgButtonMode.Cancel || m_dlgBtnMode == DlgButtonMode.Close ||
			 m_dlgBtnMode == DlgButtonMode.OkCancel )
		{
			ImageResource imageResource;
			
			m_closePanel = new FlowPanel();
			m_closePanel.addStyleName( "dlgBox_closePanel" );
			
			imageResource = GwtTeaming.getImageBundle().closeBorder();
			m_closeImg = new Image( imageResource );
			m_closeImg.addClickHandler( this );
			m_closePanel.add( m_closeImg );
			panel.add( m_closePanel );
		}
		
		// Add the header.
		m_headerPanel = createHeader( caption );
		panel.add( m_headerPanel );
		
		// Create a panel where the error panel and the content panel live.
		m_bodyPanel = new FlowPanel();
		panel.add( m_bodyPanel );
		
		// Create a panel where errors can be displayed.
		m_errorPanel = new FlowPanel();
		m_errorPanel.addStyleName( "dlgErrorPanel" );
		m_errorPanel.setVisible( false );
		m_bodyPanel.add( m_errorPanel );

		// Add the main content of the dialog box.
		m_contentPanel = createContent( properties );
		m_bodyPanel.add( m_contentPanel );
		
		// Create the footer.
		m_footerPanel = createFooter();
		if ( m_footerPanel != null )
			panel.add( m_footerPanel );
		
		// Initialize the handlers
		initHandlers( editSuccessfulHandler, editCanceledHandler );
		
		setWidget( panel );
	}// end createAllDlgContent()

	
	/*
	 * Create the footer panel for this dialog box.
	 */
	public FlowPanel createFooter()
	{
		m_footerPanel = new FlowPanel();
		
		// Associate this panel with its stylesheet.
		m_footerPanel.setStyleName( "teamingDlgBoxFooter" );
		if (!m_showFooter) {
			m_footerPanel.addStyleName( "displayNone" );
		}
		
		// Add a panel that will display a status message
		{
			ImageResource imgResource;
			
			m_statusPanel = new FlowPanel();
			m_statusPanel.addStyleName( "dlgBox_statusPanel" );
			m_statusPanel.setVisible( false );
			m_footerPanel.add( m_statusPanel );
			
			imgResource = GwtTeaming.getImageBundle().spinner16();
			m_statusImg = new Image( imgResource );
			m_statusImg.getElement().setAttribute( "align", "absmiddle" );
			m_statusPanel.add( m_statusImg );

			m_statusLabel = new InlineLabel();
			m_statusLabel.addStyleName( "dlgBox_statusMsg" );
			m_statusPanel.add( m_statusLabel );
		}

		// Create the appropriate buttons based on the value of m_dlgBtnMode.
		createFooterButtons( m_dlgBtnMode );
		
		return m_footerPanel;
	}
	
	
	/**
	 * Create the appropriate buttons based on the value of dlgBtnMode.
	 */
	public void createFooterButtons( DlgButtonMode dlgBtnMode )
	{
		switch ( dlgBtnMode ) {
		case Cancel:
		case Close:
			if ( m_cancelBtn == null )
			{
				m_cancelBtn = new Button( (DlgButtonMode.Cancel == dlgBtnMode) ? GwtTeaming.getMessages().cancel() : GwtTeaming.getMessages().close() );
			
				m_cancelBtn.addClickHandler( this );
				m_cancelBtn.addStyleName( "teamingButton" );
				m_footerPanel.add( m_cancelBtn );
			}
			else
			{
				// m_cancelBtn is used for both Cancel and Close.  Make sure the button
				// says Close.
				m_cancelBtn.setText( GwtTeaming.getMessages().close() );
			}
			
			m_cancelBtn.setVisible( true );
			
			if ( m_okBtn != null )
				m_okBtn.setVisible( false );
			
			break;
			
		case Ok:
		case OkCancel:
		case YesNo:
			if ( m_okBtn == null )
			{
				m_okBtn = new Button( (DlgButtonMode.YesNo == m_dlgBtnMode) ? GwtTeaming.getMessages().yes() : GwtTeaming.getMessages().ok() );
				m_okBtn.addClickHandler( this );
				m_okBtn.addStyleName( "teamingButton" );
				m_footerPanel.add( m_okBtn );
			}
			m_okBtn.setVisible( true );
			
			if ((DlgButtonMode.OkCancel == m_dlgBtnMode) || (DlgButtonMode.YesNo == m_dlgBtnMode)) {
				String cancelTxt = ((DlgButtonMode.YesNo == m_dlgBtnMode) ? GwtTeaming.getMessages().no() : GwtTeaming.getMessages().cancel());
				if ( m_cancelBtn == null )
				{
					m_cancelBtn = new Button( cancelTxt );

					m_cancelBtn.addClickHandler( this );
					m_cancelBtn.addStyleName( "teamingButton" );
					m_footerPanel.add( m_cancelBtn );
				}
				else
				{
					// m_cancelBtn is used for both Cancel and Close.  Make sure the button
					// says Cancel.
					m_cancelBtn.setText( cancelTxt );
				}
			}
			else
			{
				if ( m_cancelBtn != null )
					m_cancelBtn.setVisible( false );
			}
			
			break;
		}
		
	}
	
	
	/**
	 * Get the Panel that holds the dialog box's header.
	 */
	public Panel createHeader( String caption )
	{
		FlowPanel	flowPanel;
		
		flowPanel = new FlowPanel();
		flowPanel.setStyleName( "teamingDlgBoxHeader" );
		if ( GwtClientHelper.jsIsIE() )
		     flowPanel.addStyleName( "teamingDlgBoxHeaderBG_IE"    );
		else flowPanel.addStyleName( "teamingDlgBoxHeaderBG_NonIE" );
		flowPanel.getElement().setId( "teamingDlgBoxHeader-" + String.valueOf( m_id ) );

		m_captionImagePanel = new FlowPanel();
		m_captionImagePanel.setStyleName( "teamingDlgBoxHeader-captionImagePanel" );
		flowPanel.add( m_captionImagePanel );
		
		m_caption = new Label( caption );
		m_caption.setStyleName( "teamingDlgBoxHeader-captionLabel" );
		flowPanel.add( m_caption );
		
		// Add a help link to the header if needed.
		m_helpData = getHelpData();
		if ( m_helpData != null )
		{
			ClickHandler clickHandler;
			ImageResource imageResource;
			Image img;
			
			imageResource = GwtTeaming.getImageBundle().help3();
			img = new Image( imageResource );
			img.addStyleName( "dlgHeaderHelpImg" );
			img.getElement().setId( "helpImg" );
			flowPanel.add( img );

			// Add a click handler for the Actions image.
			clickHandler = new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent clickEvent )
				{
					Scheduler.ScheduledCommand cmd;
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						@Override
						public void execute()
						{
							// Invoke help for this dialog.
							invokeHelp();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			};
			img.addClickHandler( clickHandler );
		}
		
		return flowPanel;
	}// end createHeader()
	
	
	/**
	 * This method will gather up the data from the controls in the dialog box.
	 */
	public abstract Object getDataFromDlg();
	
	
	/**
	 * Return the panel that holds the error messages.
	 */
	public FlowPanel getErrorPanel()
	{
		return m_errorPanel;
	}
	
	
	/**
	 * Get the widget that should receive the focus when this dialog is shown.
	 */
	public abstract FocusWidget getFocusWidget();
	

	/**
	 * Returns the dialog's header panel.
	 * 
	 * @return
	 */
	public Panel getHeaderPanel()
	{
		return m_headerPanel;
	}
	
	
	/**
	 * Returns the dialog's footer panel.
	 * 
	 * @return
	 */
	public Panel getFooterPanel()
	{
		return m_footerPanel;
	}
	
	/**
	 * If a dialog wants to have a help link in the header then override this method
	 * and return a HelpData object.
	 */
	public HelpData getHelpData()
	{
		return null;
	}

	
	/**
	 * Returns the cancel push button.
	 * 
	 * @return
	 */
	public Button getCancelButton()
	{
		return m_cancelBtn;
	}// end getCancelButton()

	
	/**
	 * Returns the OK push button.
	 * 
	 * @return
	 */
	public Button getOkButton()
	{
		return m_okBtn;
	}// getOkButton()
	

	/**
	 * Returns a simple EditCanceledHandler that simply lets the
	 * dialog close.
	 * 
	 * @return
	 */
	public static EditCanceledHandler getSimpleCanceledHandler() {
		return new EditCanceledHandler() {
			@Override
			public boolean editCanceled() {
				return true;
			}
		};
	}
	
	/**
	 * Returns a simple EditSuccessfulHandler that simply lets the
	 * dialog close.
	 * 
	 * @return
	 */
	public static EditSuccessfulHandler getSimpleSuccessfulHandler() {
		return new EditSuccessfulHandler() {
			@Override
			public boolean editSuccessful(Object callbackData) {
				return true;
			}
		};
	}
	
	/**
	 * Initialize the controls in the dialog with the values from the PropertiesObj.
	 */
//!!!	public abstract void init( PropertiesObj properties );
	
	/**
	 * Hide this dialog.
	 */
	@Override
	public void hide()
	{
		if ( m_visible )
			--m_numDlgsVisible;
		
		m_visible = false;
		
		super.hide();
	}// end hide()
	
	/**
	 * Hide the close image.
	 */
	public void hideCloseImg()
	{
		if ( m_closePanel != null )
			m_closePanel.setVisible( false );
	}
	
	/**
	 * Hide the panel that holds all the content.
	 */
	public void hideContentPanel()
	{
		if ( m_contentPanel != null )
			m_contentPanel.setVisible( false );
	}
	
	
	/**
	 * Hide the panel that displays the errors.
	 */
	public void hideErrorPanel()
	{
		m_errorPanel.setVisible( false );
	}
	
	/**
	 * 
	 */
	public void hideStatusMsg()
	{
		m_statusPanel.setVisible( false );
	}
	
	/**
	 * Initialize the edit/cancel handlers.
	 */
	public void initHandlers(
		EditSuccessfulHandler editSuccessfulHandler,// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler ) 	// This gets called when the user presses the Cancel button
	{
		// Remember the handlers to call when the user presses ok or cancel.
		m_editSuccessfulHandler = editSuccessfulHandler;
		m_editCanceledHandler = editCanceledHandler;
	}// end initHandlers()
	
	
	/**
	 * Invoke help for this dialog if there is help.
	 */
	private void invokeHelp()
	{
		// Do we have help data?
		if ( m_helpData != null )
		{
			GwtClientHelper.invokeHelp( m_helpData );
		}
	}
	
	/*
	 * Asynchronously makes the dialog dragable.
	 */
	private void makeDraggableAsync()
	{
		ScheduledCommand cmd;
		
		cmd = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				makeDraggableNow( String.valueOf( m_id ) );
				
				// Are we dealing with a fixed sized dialog?
				if ( m_fixedSize )
				{
					int spaceNeeded;
					int contentPanelHeight;
					
					// Yes, make sure the content panel takes up all the room minus the room
					// needed by the header and footer.
					spaceNeeded = 0;
					if ( m_headerPanel != null )
						spaceNeeded += m_headerPanel.getOffsetHeight();
					
					if ( m_errorPanel != null )
						spaceNeeded += m_errorPanel.getOffsetHeight();
					
					if ( m_footerPanel != null )
						spaceNeeded += m_footerPanel.getOffsetHeight();
					
					spaceNeeded += 20;
					
					contentPanelHeight = m_height - spaceNeeded;
					m_bodyPanel.setHeight( String.valueOf( contentPanelHeight ) + "px" );
					if ( m_useOverflowAutoOnContent )
						m_bodyPanel.getElement().getStyle().setOverflow( Overflow.AUTO );
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}// end makeDraggableAsync()
	
	/*
	 * Synchronously makes the dialog dragable.
	 * 
     * Makes this dialog draggable by using the native JQuery Draggable
     */
    private static native void makeDraggableNow( String id )
    /*-{
		$wnd.jQuery( "#teamingDlgBox-" + id ).draggable( { handle : '#teamingDlgBoxHeader-' + id } );
	}-*/;

    
    /*
	 * This method gets called when the user clicks on the ok or cancel button.
	 */
	@Override
	public void onClick( ClickEvent event )
	{
		Object	source;
		
		// Get the object that was clicked on.
		source = event.getSource();
		
		// Did the user click on ok?
		if ( source == m_okBtn )
		{
			Object props;
			
			// Yes
			// Get the data from the controls in the dialog box.
			props = getDataFromDlg();
			
			// If getDataFromDlg() returns null it means that the data entered by the user
			// is not valid.  getDataFromDlg() will notify the user of problems.
			// Is the data valid?
			if ( props != null )
			{
				// Yes
				// Do we have a handler we need to call?
				if ( m_editSuccessfulHandler != null )
				{
					// Yes
					if ( m_editSuccessfulHandler.editSuccessful( props ) )
						hide();
				}
			}
			
			return;
		}
		
		// Did the user click on cancel?
		if ( source == m_cancelBtn || source == m_closeImg )
		{
			// Yes
			// Do we have a handler we need to call?
			if ( m_editCanceledHandler != null )
			{
				// Yes
				if ( m_editCanceledHandler.editCanceled() )
					hide();
			}
			else
				hide();
		}
	}// end onClick()

	/**
	 * Enables/disables the cancel push button.
	 * 
	 * @param enabled
	 */
	public void setCancelEnabled( boolean enabled )
	{
		if ( null != m_cancelBtn )
		{
			m_cancelBtn.setEnabled(enabled);
		}
	}// end setCancelEnabled()
	
	/**
	 * Updates the dialog's caption.
	 * 
	 * @param caption
	 */
	public void setCaption( String caption )
	{
		m_caption.setText( caption );
	}// end setCaption()
	
	/**
	 * Updates the caption's image.
	 *  
	 * @param captionImg
	 */
	public void setCaptionImage( Image captionImg )
	{
		if ( null != captionImg )
		{
			m_captionImagePanel.clear();
			m_captionImagePanel.add( captionImg );
			m_captionImagePanel.addStyleName( "padding5R" );
		}
	}// end setCaptionImage()
	
	/**
	 * Enables/disables the OK push button.
	 * 
	 * @param enabled
	 */
	public void setOkEnabled( boolean enabled )
	{
		if ( null != m_okBtn )
		{
			m_okBtn.setEnabled(enabled);
		}
	}// end setOkEnabled()
	
	/**
	 * Show this dialog.
	 * 
	 * @param centered
	 */
	public void show( boolean centered )
	{
		// Is this dialog suppose to be modal
		if ( m_modal )
		{
			// Yes
			// If there is already a dialog visible then the glass panel is already visible.
			// We don't want 2 glass panels.
			if ( m_numDlgsVisible == 0 )
			{
				setGlassEnabled( true );
				setGlassStyleName( "teamingDlgBox_Glass" );
			}
		}
		
		if ( m_visible == false )
			++m_numDlgsVisible;
		
		m_visible = true;
		
		// Add a vertical scroll bar to the outer most frame to address
		// the dialog not being completely visible.
		GwtClientHelper.scrollUIForPopup( this );
		
		// Show this dialog.
		if ( centered )
		     super.center();
		else super.show();
		
		// Get the widget that should be given the focus when this dialog is displayed.
		m_focusWidget = getFocusWidget();
		if ( m_focusWidget != null )
		{
			// Yes
			// For some unknown reason if we give the focus to the a field
			// right now the cursor doesn't show up.  We need to set a timer and
			// wait for the dialog to be displayed.
			GwtClientHelper.setFocusDelayed( m_focusWidget );
		}
		
		makeDraggableAsync();
	}// end show()
	
	/**
	 * 
	 */
	@Override
	public void show()
	{
		// Always use the initial form of the method.
		show( false );
	}// end show()
	
	/**
	 * 
	 */
	public void showRelativeToTarget( final UIObject target )
	{
		PopupPanel.PositionCallback posCallback;
		
		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			@Override
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				int x;
				int y;
				
				x = target.getAbsoluteLeft() + target.getOffsetWidth();
				if ( x > Window.getClientWidth() )
					x = Window.getClientWidth();
				
				if ( (x + offsetWidth) > Window.getClientWidth() )
					x -= (offsetWidth + 75);
				
				y = target.getAbsoluteTop();
				
				// Sometimes in Firefox getAbsoluteTop() returns the value that would
				// normally be returned by getOffsetTop()
				// Make sure the y value is reasonable.
				if ( y > Window.getClientHeight() )
					y = Window.getClientHeight();
				
				if ( (y + offsetHeight) > Window.getClientHeight() )
				{
					y -= (offsetHeight + 10);
				}
				
				setPopupPosition( x, Math.max( y, 0 ) );
				makeDraggableAsync();
			}
		};
		setPopupPositionAndShow( posCallback );
	}
	
	/**
	 * 
	 */
	public void showStatusMsg( String statusMsg )
	{
		m_statusLabel.setText( statusMsg );
		m_statusLabel.setTitle( statusMsg );
		m_statusPanel.setVisible( true );
	}
	
	
	/**
	 * Show the panel that displays all the content.
	 */
	public void showContentPanel()
	{
		if ( m_contentPanel != null )
			m_contentPanel.setVisible( true );
	}
	
	
	/**
	 * Show the panel that displays the error, hide the content of the dialog and change
	 * the buttons to just a close button.
	 */
	public void showErrors()
	{
		// Show the error panel.
		showErrorPanel();
		
		// Hide the content panel.
		hideContentPanel();
		
		// Change the buttons on the dialog from Ok/Cancel to just Close
		createFooterButtons( DlgBox.DlgButtonMode.Close );
	}

	
	/**
	 * Show the panel that displays the errors.
	 */
	public void showErrorPanel()
	{
		m_errorPanel.setVisible( true );
	}
	
	/**
	 * Callback interface to interact with a dialog asynchronously
	 * after it loads. 
	 */
	public interface DlgBoxClient
	{
		void onSuccess( DlgBox dlg );
		void onUnavailable();
	}
}
