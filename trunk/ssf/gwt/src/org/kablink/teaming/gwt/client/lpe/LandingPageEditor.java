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

package org.kablink.teaming.gwt.client.lpe;

import java.util.ArrayList;
import java.util.Stack;

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This widget is the Landing Page Editor.  As its name implies, it is used to edit a 
 * landing page configuration.
 * @author jwootton
 *
 */
public class LandingPageEditor extends Composite
	implements MouseDownHandler,
				MouseUpHandler,
				 HasMouseUpHandlers,
				 EditSuccessfulHandler, EditCanceledHandler,
				 Event.NativePreviewHandler
{
	private Palette		m_palette;
	private DropZone		m_canvas;
	private boolean		m_paletteItemDragInProgress;
	private DragProxy		m_paletteItemDragProxy;
	private PaletteItem		m_paletteItemBeingDragged = null;
	private boolean		m_existingItemDragInProgress;
	private DragProxy		m_existingItemDragProxy = null;
	private DropWidget		m_existingItemBeingDragged = null;
	private DropZone		m_selectedDropZone = null;
	private Stack<DropZone>		m_enteredDropZones = null;
	private HandlerRegistration		m_previewHandlerReg = null;
	private HandlerRegistration		m_mouseUpHandlerReg = null;
	private Hidden m_configResultsInputCtrl = null;
	private ArrayList<DropZone> m_dropZones = null;
	private LandingPageConfig m_lpeConfig = null;
	private LPETinyMCEConfiguration m_tinyMCEConfig = null;
	private static TextArea m_textBox = null;//!!!
	
	/**
	 * 
	 */
	public LandingPageEditor()
	{
		ConfigData		configData;
		HorizontalPanel	hPanel;
		VerticalPanel 	vPanel;
		Label			hintLabel;
		int				i;
		int				numItems;
		boolean			doLog;

		// Create an ArrayList that will hold all DropZones that are created.
		m_dropZones = new ArrayList<DropZone>();
		
		// Get the configuration data that defines this landing page.
		m_lpeConfig = getLandingPageConfig();
		configData = new ConfigData( m_lpeConfig.getConfigStr() );
		
		// Parse the configuration data.
		configData.parse();
		
		// Initialize a tinyMCE configuration in case we need one.
		initTinyMCEConfig();
		
		// Create a stack that will keep track of when we enter and leave drop zones.
		m_enteredDropZones = new Stack<DropZone>();
		
		// Create a vertical panel for the hint and the horizontal panel to live in.
		vPanel = new VerticalPanel();
		
		// Create a hint
		hintLabel = new Label( GwtTeaming.getMessages().lpeHint() );
		hintLabel.addStyleName( "lpeHint" );
		vPanel.add( hintLabel );
		
		// Create a panel for the palette and canvas to live in.
		hPanel = new HorizontalPanel();
		
		// Create a palette and a canvas.
		m_palette = new Palette( this );
		m_canvas = new DropZone( this, "lpeCanvas" );
		m_canvas.setParentDropZone( null );
		
		// Add items to the canvas that are defined in the configuration.
		numItems = configData.size();
		for (i = 0; i < numItems; ++i)
		{
			ConfigItem configItem;
			
			// Get the next item in the list.
			configItem = configData.get( i );
			if ( configItem != null )
			{
				DropWidget dropWidget;
				
				// Create the appropriate widget based on the given ConfigItem.
				dropWidget = DropWidget.createDropWidget( this, configItem );
				
				if ( dropWidget != null )
					m_canvas.addWidgetToDropZone( dropWidget );
			}
		}
		
		// Add the palette and canvas to the panel.
		hPanel.add( m_palette );
		hPanel.add( m_canvas );
		
		vPanel.add( hPanel );
		
		// Create a hidden input control where we will put the configuration string when
		// the user presses the ok button.
		m_configResultsInputCtrl = new Hidden( m_lpeConfig.getMashupPropertyName() );
		vPanel.add( m_configResultsInputCtrl );
		
		doLog = false;
		if ( doLog == true )
		{
			LandingPageEditor.m_textBox = new TextArea();
			LandingPageEditor.m_textBox.setVisibleLines( 20 );
			LandingPageEditor.m_textBox.setWidth( "500px" );
			vPanel.add( m_textBox );
		}
		
		m_paletteItemDragInProgress = false;
		m_existingItemDragInProgress = false;
		
		// All composites must call initWidget() in their constructors.
		initWidget( vPanel );

		Event.addNativePreviewHandler( new Event.NativePreviewHandler() {
			/**
			 * This handler looks for when the user clicks on the Ok button.  When we detect this
			 * we will create a configuration string based on the widgets that have been added to
			 * the canvas.  We will then put the configuration string in a hidden input control
			 * and it will be sent with the other data.
			 */
			public void onPreviewNativeEvent( Event.NativePreviewEvent previewEvent )
			{
				NativeEvent nativeEvent;
				EventTarget eventTarget;
				InputElement inputElement;
				Element targetElement;
				String name;
				int eventType;

				eventType = previewEvent.getTypeInt();
				
				// We are only interested in mouse-up events.
				if ( eventType != Event.ONMOUSEUP )
					return;

				// Get the target for the mouse-up event
				nativeEvent = previewEvent.getNativeEvent();
				eventTarget = nativeEvent.getEventTarget();
				
				if ( eventTarget != null && Element.is( eventTarget ) )
				{
					targetElement = Element.as( eventTarget );
					if ( targetElement instanceof InputElement )
					{
						inputElement = (InputElement) targetElement;
						name = inputElement.getName();
						if ( name != null && name.equalsIgnoreCase( "okBtn" ) )
						{
							String configStr;
							
							// Get the configuration string for the mashup.
							configStr = createConfigString();
							
							// Put the configuration string in a hidden input control.
							m_configResultsInputCtrl.setValue( configStr );
						}
					}
				}
			}// end onPreviewNativeEvent()
		});
		
		// Adjust the height of all the tables we added.  We can't do this right now because the browser hasn't
		// rendered anything yet.  So set a timer to do the work later.
		{
			Timer timer;
			
			timer = new Timer()
			{
				/**
				 * 
				 */
				@Override
				public void run()
				{
					adjustHeightOfAllTableWidgets();
				}// end run()
			};
			
			timer.schedule( 250 );
		}
	}// end LandingPageEditor()
	
	
	/**
	 * Method to add mouse up handlers to this landing page editor.
	 */
	public HandlerRegistration addMouseUpHandler( MouseUpHandler handler )
	{
		return addDomHandler( handler, MouseUpEvent.getType() );
	}// end addMouseUpHandler()
	
	
	/**
	 * Adjust the height of all the table widgets so all the DropZones in a table are the same height.
	 */
	public void adjustHeightOfAllTableWidgets()
	{
		// Adjust the height of all TableDropWidgets found in the canvas.
		m_canvas.adjustHeightOfAllTableWidgets();
	}// end adjustHeightOfAllTableWidgets()
	
	
	/**
	 * 
	 */
	public void cacheDropZone( DropZone dropZone )
	{
		m_dropZones.add( dropZone );
	}
	
	
	/**
	 * Create the configuration string that will be stored in the db.
	 */
	public String createConfigString()
	{
		String configStr;
		ArrayList<DropWidget> dropWidgets;
		int i;
		
		configStr = "";
		
		// Get the list of widgets that are on the canvas.
		dropWidgets = m_canvas.getWidgets();
		
		// Spin through the list of widgets and ask each widget to generate its configuration string.
		if ( dropWidgets != null )
		{
			for (i = 0; i < dropWidgets.size(); ++i)
			{
				DropWidget nextWidget;
				String nextConfigStr;
				
				// Get the next widget in the list.
				nextWidget = dropWidgets.get( i );
				
				// Get the configuration string for this widget.
				nextConfigStr = nextWidget.createConfigString(); 
				configStr += nextConfigStr;
			}// end for()
		}
		
		return configStr;
	}// end createConfigString()
	
	
	/**
	 * This method gets called when the user presses cancel in a DropWidget's properties dialog.
	 */
	public boolean editCanceled()
	{
		m_selectedDropZone = null;
		
		return true;
	}// end editCanceled()
	
	
	/**
	 * This method gets called when the user presses the Ok button in a DropWidget's properties dialog.
	 */
	public boolean editSuccessful( Object obj )
	{
		if ( m_selectedDropZone != null && (obj instanceof DropWidget) )
		{
			DropWidget dropWidget;
			
			dropWidget = (DropWidget) obj;
			
			// Add the DropWidget to the DropZone it was dropped on.
			m_selectedDropZone.addWidgetToDropZone( dropWidget );
		}
		
		m_selectedDropZone = null;
		
		return true;
	}// end editSuccessful()
	
	
	/**
	 * This method is called by a DropZone when the mouse is entering the DropZone.  We will add the
	 * DropZone to our stack of drop zones.
	 */
	public void enteringDropZone( DropZone dropZone, int clientX, int clientY )
	{
		DropZone previousDropZone = null;

		GWT.log( "entering DropZone: " + dropZone.getDebugName(), null );
		
		// Get the DropZone at the top of the stack.
		if ( !m_enteredDropZones.empty() )
		{
			previousDropZone = m_enteredDropZones.peek();
		}
		
		// Make sure we aren't adding the same drop zone twice.
		if ( dropZone != previousDropZone )
		{
			m_enteredDropZones.push( dropZone );
		}
		
		// Remember the active drop zone.
		setDropZone( dropZone, clientX, clientY );
	}// end enteringDropZone()
	

	/**
	 * Return the id of the binder we are working with.
	 */
	public String getBinderId()
	{
		return m_lpeConfig.getBinderId();
	}
	
	
	/**
	 * Return the height of the canvas.
	 */
	public int getCanvasHeight()
	{
		return m_canvas.getOffsetHeight();
	}
	
	
	/**
	 * Return the absolute X position of the canvas.
	 */
	public int getCanvasLeft()
	{
		return m_canvas.getAbsoluteLeft();
	}// end getCanvasTop()
	
	
	/**
	 * Return the absolute Y position of the canvas.
	 */
	public int getCanvasTop()
	{
		return m_canvas.getAbsoluteTop();
	}// end getCanvasTop()
	
	
	/**
	 * Return the width of the canvas.
	 */
	public int getCanvasWidth()
	{
		return m_canvas.getOffsetWidth();
	}// end getCanvasWidth()
	
	
	/**
	 * Return how much the canvas has been scrolled vertically
	 */
	public int getCanvasScrollY()
	{
		return m_canvas.getScrollY();
	}// end getCanvasScrollY()
	
	
	/**
	 * 
	 */
	private DropZone getDropZoneById( String id )
	{
		int i;
		
		if ( id == null || id.length() == 0 )
			return null;
		
		for (i = 0; i < m_dropZones.size(); ++i)
		{
			DropZone dropZone;
			String nextId;
			
			dropZone = m_dropZones.get( i );
			if ( dropZone != null )
			{
				nextId = dropZone.getId();
				if ( id.equalsIgnoreCase( nextId ) )
					return dropZone;
			}
		}
		
		// If we get here we didn't find a DropZone with the given id.
		return null;
	}

	
	/**
	 * Use JSNI to grab the JavaScript object that holds the list of file attachments.
	 */
	public static native FileAttachments getFileAttachments() /*-{
		// Return a reference to the JavaScript variable called, m_fileAttachments.
		return $wnd.m_fileAttachments;
	}-*/;
	

	/**
	 * Return the url to the content css
	 */
	public String getContentCss()
	{
		return m_lpeConfig.getContentCss();
	}
	
	
	/**
	 * Return the language we are running in.
	 */
	public String getLanguage()
	{
		return m_lpeConfig.getLanguage();
	}
	
	
	/**
	 * Use JSNI to grab the JavaScript object that holds the landing page configuration data.
	 */
	private native LandingPageConfig getLandingPageConfig() /*-{
		// Return a reference to the JavaScript variable called, m_landingPageConfig.
		return $wnd.m_landingPageConfig;
	}-*/;
	
	
	/**
	 * Get the configuration that can be used to create a TinyMCEDlg 
	 */
	public LPETinyMCEConfiguration getTinyMCEConfig()
	{
		return m_tinyMCEConfig;
	}
	
	/**
	 * 
	 */
	public void handleMouseMove( int clientX, int clientY )
	{
		int mouseX;
		int mouseY;
		
		// Is the user dragging an item from the palette?
		if ( m_paletteItemDragInProgress )
		{
			// Yes, update the position of the drag proxy.
			mouseX = clientX + Window.getScrollLeft();
			mouseY = clientY + Window.getScrollTop();
			m_paletteItemDragProxy.setPopupPosition( mouseX+10, mouseY+10 );
			
			// Is the mouse over a drop zone?
			if ( m_selectedDropZone != null )
			{
				// Yes, have the drop zone update the visual drop clue.
				m_selectedDropZone.showDropClue( clientX, clientY );
			}
		}
		// Is the user dragging an existing item?
		else if ( m_existingItemDragInProgress )
		{
			// Yes, update the position of the drag proxy.
			mouseX = clientX + Window.getScrollLeft();
			mouseY = clientY + Window.getScrollTop();
			m_existingItemDragProxy.setPopupPosition( mouseX+10, mouseY+10 );
			
			// Is the mouse over a drop zone?
			if ( m_selectedDropZone != null )
			{
				// Yes, have the drop zone update the visual drop clue.
				m_selectedDropZone.showDropClue( clientX, clientY );
			}
		}
	}// end handleMouseMove()
	
	/**
	 * Initialize a tinyMCE configuration
	 */
	private void initTinyMCEConfig()
	{
		// Create the configuration object used by the tinyMCE dialog.  We need to do this
		// here because the LPETinyMCEConfiguration() class makes an ajax request and we need
		// time for it to complete before we invoke the tinyMCE dialog.
		if ( m_tinyMCEConfig == null )
		{
			FileAttachments fileAttachments;
			ArrayList<String> listOfFileNames = null;

			m_tinyMCEConfig = new LPETinyMCEConfiguration( this, getBinderId() );
			
			// Get the file attachments for the landing page.
			listOfFileNames = new ArrayList<String>();
			fileAttachments = getFileAttachments();
			if ( fileAttachments != null )
			{
				int i;
				
				for (i = 0; i < fileAttachments.getNumAttachments(); ++i)
				{
					String fileName;
					
					fileName = fileAttachments.getFileName( i );
					listOfFileNames.add( fileName );
				}
			}
			m_tinyMCEConfig.setListOfFileAttachments( listOfFileNames );
		}
	}
	

	/**
	 * Return a boolean indicating whether or not the user is current dragging an item from
	 * the palette or an existing item.
	 */
	public boolean isDragInProgress()
	{
		if ( isExistingItemDragInProgress() || isPaletteItemDragInProgress() )
			return true;
		
		return false;
	}
	
	
	/**
	 * Return a boolean indicating whether or not the user is currently dragging an existing item.
	 */
	private boolean isExistingItemDragInProgress()
	{
		return m_existingItemDragInProgress;
	}

	
	/**
	 * Return a boolean indicating whether or not the user is currently dragging a palette item.
	 */
	private boolean isPaletteItemDragInProgress()
	{
		return m_paletteItemDragInProgress;
	}// end isPaletteItemDragInProgress()

	
	/**
	 * This method is called by a DropZone when the mouse is leaving the DropZone.
	 */
	public void leavingDropZone( DropZone dropZone, int clientX, int clientY )
	{
		GWT.log( "leaving DropZone: " + dropZone.getDebugName(), null );
		if ( m_enteredDropZones.empty() )
		{
			// This should never happen.
			GWT.log( "in leavingDropZone(), m_enteredDropZones is empty.", null );
			return;
		}
		
		// The given drop zone should be the top drop zone in our stack.
		if ( dropZone != m_enteredDropZones.peek() )
		{
			// This should never happen.
			GWT.log( "in leavingDropZone(), leaving drop zone is not on top of stack.", null );
			return;
		}
		
		// Remove the given drop zone from the stack.
		m_enteredDropZones.pop();
		
		// Do we have a previously entered drop zone.
		if ( !m_enteredDropZones.empty() )
		{
			// Yes, select it is the active drop zone.
			setDropZone( m_enteredDropZones.peek(), clientX, clientY );
		}
		else
		{
			// No, set the active drop zone to null.
			setDropZone( null, clientX, clientY );
		}
	}// end leavingDropZone()
	

	/**
	 * This method should be called whenever an item in the canvas has been updated.
	 */
	public void notifyWidgetUpdated( DropWidget dropWidget )
	{
		// Because the given widget has been updated we may need to adjust the height of
		// the tables in the canvas.
		adjustHeightOfAllTableWidgets();
	}// end notifyWidgetUpdated()
	
	
	/**
	 * Handles the MouseDownEvent.  This will initiate the dragging of an item from the palette.
	 */
	public void onMouseDown( MouseDownEvent event )
	{
		Object	eventSender;
		
		// Is the object that sent this event a PaletteItem object?
		eventSender = event.getSource();
		if ( eventSender instanceof PaletteItem )
		{
			Scheduler.ScheduledCommand cmd;
			final PaletteItem paletteItem;
			final int x;
			final int y;
			
			// Yes
			paletteItem = (PaletteItem) eventSender;
			x = event.getClientX();
			y = event.getClientY();
			
			cmd = new Scheduler.ScheduledCommand()
			{
				public void execute()
				{
					startDragPaletteItem( paletteItem, x, y );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
			
			// Kill this mouse-down event so text on the page does not get highlighted when the user moves the mouse.
			event.getNativeEvent().preventDefault();
			//!!!event.getNativeEvent().stopPropagation();
			//!!!event.preventDefault();
			//!!!event.stopPropagation();
		}
	}// end onMouseDown()
	
	
	/**
	 * Handle the MouseUpEvent.  If the user is dragging an item from the palette, we will drop the item
	 * on a drop target.
	 */
	public void onMouseUp( MouseUpEvent event )
	{
		Scheduler.ScheduledCommand cmd1;
		final int clientX;
		final int clientY;
		final LandingPageEditor lpe;

		clientX = event.getClientX();
		clientY = event.getClientY();
		lpe = this;
		
		cmd1 = new Scheduler.ScheduledCommand()
		{
			/**
			 * 
			 */
			public void execute()
			{
				// Is the user currently dragging an item from the palette?
				if ( m_paletteItemDragInProgress && m_paletteItemDragProxy != null && m_paletteItemBeingDragged!= null )
				{
					// Yes
					m_paletteItemDragInProgress = false;
					
					// Hide the drag proxy widget.
					m_paletteItemDragProxy.hide();
					m_paletteItemDragProxy = null;
					
					// Remove the mouse-up event handler
					if ( m_mouseUpHandlerReg != null )
					{
						m_mouseUpHandlerReg.removeHandler();
						m_mouseUpHandlerReg = null;
					}
					
					// Remove the preview event handler
					if ( m_previewHandlerReg != null )
					{
						m_previewHandlerReg.removeHandler();
						m_previewHandlerReg = null;
					}
					
					// Clear the stack of drop zones.
					m_enteredDropZones.clear();
					
					// Did the user drop the palette item on a drop zone?
					if ( m_selectedDropZone != null )
					{
						DropWidget	dropWidget;
						
						// Yes, hide the drop clue.
						m_selectedDropZone.hideDropClue();
						
						// Let the drop zone figure out where to insert the dropped widget.
						m_selectedDropZone.setDropLocation( clientX, clientY );
						
						// Create a DropWidget that will be added to the drop zone.
						dropWidget = m_paletteItemBeingDragged.createDropWidget( lpe );
						
						// Invoke the Edit Properties dialog for the DropWidget.  If the user
						// cancels the dialog we won't do anything.  If the user presses ok,
						// our editSuccessful() will be called and we will add the DropWidget to the
						// selected DropZone.  If the user pressed cancel, our editCanceled() method
						// will be called.
						dropWidget.editProperties( lpe, lpe, clientX, clientY );
					}
				}
				// Is the user currently dragging an item from the palette?
				else if ( m_existingItemDragInProgress && m_existingItemDragProxy != null && m_existingItemBeingDragged!= null )
				{
					// Yes
					m_existingItemDragInProgress = false;
					
					// Hide the drag proxy widget.
					m_existingItemDragProxy.hide();
					m_existingItemDragProxy = null;
					
					// Remove the mouse-up event handler
					if ( m_mouseUpHandlerReg != null )
					{
						m_mouseUpHandlerReg.removeHandler();
						m_mouseUpHandlerReg = null;
					}
					
					// Remove the preview event handler
					if ( m_previewHandlerReg != null )
					{
						m_previewHandlerReg.removeHandler();
						m_previewHandlerReg = null;
					}
					
					// Clear the stack of drop zones.
					m_enteredDropZones.clear();
					
					// Did the user drop the existing item on a drop zone?
					if ( m_selectedDropZone != null )
					{
						Scheduler.ScheduledCommand cmd;
						
						// Yes, hide the drop clue.
						m_selectedDropZone.hideDropClue();
						
						// Let the drop zone figure out where to insert the dropped widget.
						m_selectedDropZone.setDropLocation( clientX, clientY );
						
						// Add the DropWidget to the DropZone it was dropped on.
						m_selectedDropZone.addWidgetToDropZone( m_existingItemBeingDragged );
						
						// Did we just drop a Google Gadget widget?
						if ( m_existingItemBeingDragged instanceof GoogleGadgetDropWidget )
						{
							// Yes, for some reason the Google gadget widget needs to be refreshed
							// after we move it.
							((GoogleGadgetDropWidget) m_existingItemBeingDragged).refresh();
						}
						
						// Adjust the height of things to make sure everything fits.
						cmd = new Scheduler.ScheduledCommand()
						{
							public void execute()
							{
								m_canvas.adjustHeightOfAllTableWidgets();
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd1 );
		
	}// end onMouseUp()
	
	
	/**
	 * 
	 */
	public void onPreviewNativeEvent( Event.NativePreviewEvent previewEvent )
	{
		int eventType;

		eventType = previewEvent.getTypeInt();
		
		if ( eventType == Event.ONMOUSEOVER && isDragInProgress() )
		{
			NativeEvent nativeEvent;
			EventTarget eventTarget;

			nativeEvent = previewEvent.getNativeEvent();

			// Get the target for the mouse-over event
			eventTarget = nativeEvent.getEventTarget();
			
			if ( eventTarget != null && Element.is( eventTarget ) )
			{
				Element targetElement;
				String id;

				// Is the mouse over a DropZone?
				targetElement = Element.as( eventTarget );
				id = targetElement.getId();
				if ( id != null && id.startsWith( "dropZone" ) )
				{
					DropZone dropZone;
					
					// Yes
					dropZone = getDropZoneById( id );
					if ( dropZone != null )
						enteringDropZone( dropZone, nativeEvent.getClientX(), nativeEvent.getClientY() );
				}
			}
			
			return;
		}
		
		// We are only interested in mouse-move events.
		if ( eventType != Event.ONMOUSEMOVE )
			return;
		
		// Is the user dragging an item from the palette or dragging an existing item?
		if ( isPaletteItemDragInProgress() || isExistingItemDragInProgress() )
		{
			NativeEvent nativeEvent;
			final int x;
			final int y;
			int scrollTop;
			int scrollLeft;
			int windowHeight;
			Scheduler.ScheduledCommand cmd;

			// Yes
			nativeEvent = previewEvent.getNativeEvent();
			x = nativeEvent.getClientX();
			y = nativeEvent.getClientY();
			
			cmd = new Scheduler.ScheduledCommand()
			{
				public void execute()
				{
					handleMouseMove( x, y );
				}
			};
			Scheduler.get().scheduleDeferred( cmd );
			
			scrollTop = Window.getScrollTop();
			scrollLeft = Window.getScrollLeft();
			
			// Is the mouse within 10 pixels of the bottom of the page?
			windowHeight = Window.getClientHeight();
			if ( (windowHeight - y) <= 10 )
			{
				// Yes, scroll the window down.
				Window.scrollTo( scrollLeft, scrollTop + 30 );
			}
			// Is the mouse within 10 pixels of the top of the page?
			else if ( y <= 10 )
			{
				// Yes, scroll the window up.
				if ( scrollTop > 30 )
					scrollTop -= 30;
				else
					scrollTop = 0;
				Window.scrollTo( scrollLeft, scrollTop );
			}
			
			// Kill this mouse-move event so text on the page does not get highlighted when the user moves the mouse.
			//previewEvent.cancel();
			nativeEvent.preventDefault();
			//nativeEvent.stopPropagation();
		}
	}// end onPreviewNativeEvent()
	
	
	/**
	 * Set the DropZone object that will be used on the mouse-up event.
	 */
	private void setDropZone( DropZone dropZone, int clientX, int clientY )
	{
		// Is the user dragging an item from the palette or dragging an existing item?
		if ( m_paletteItemDragInProgress || m_existingItemDragInProgress )
		{
			// Yes
			// If we are dragging an existing item, make sure the drop zone is not the item
			// being dragged or a child of the item being dragged.
			if ( m_existingItemDragInProgress && m_existingItemBeingDragged instanceof HasDropZone )
			{
				// Does the given drop zone live inside the item being dragged?
				if ( ((HasDropZone) m_existingItemBeingDragged).containsDropZone( dropZone ) )
				{
					// Yes, we can't let the user drop the item on itself, so bail.
					GWT.log( "in setDropZone(), containsDropZone() returned true", null );
					return;
				}
			}
			
			// Do we currently have a selected drop zone that is different from the new drop zone?
			if ( m_selectedDropZone != null && dropZone != m_selectedDropZone )
			{
				// Yes
				// Hide the visual drop-zone clue.
				m_selectedDropZone.hideDropClue();
			}
			
			// Is a new drop zone becoming the selected drop zone?
			if ( dropZone != null)
			{
				// Yes.
				// Show the visual drop-zone clue for the new drop zone.
				dropZone.showDropClue( clientX, clientY );
			}
			
			// Remember the new selected drop zone.
			m_selectedDropZone = dropZone;
		}
	}// end setDropZone()
	
	
	/**
	 * This method initiates the drag/drop of an existing item in the canvas.
	 */
	public void startDragExistingItem( DropWidget dropWidget, int x, int y )
	{
		DropZone dropZone;
		
		GWT.log( "******** startDragExistingItem() ***********" );
		// Get the drag proxy widget we should display and display it.
		m_existingItemDragProxy = dropWidget.getDragProxy();
		
		// Remember the existing item we are dragging.
		m_existingItemBeingDragged = dropWidget;

		// Position the drag proxy widget at the cursor position.
		m_existingItemDragProxy.setPopupPosition( x+10, y+10 );
		
		// Show the drag-proxy widget.
		m_existingItemDragProxy.show();
		
		// Register for mouse up event.
		m_mouseUpHandlerReg = addMouseUpHandler( this );
		
		// Register a preview-event handler.  We do this so we can consume the mouse-move
		// event when the user is dragging an existing item.
		m_previewHandlerReg = Event.addNativePreviewHandler( this );
//!!!		sinkEvents( Event.ONMOUSEMOVE );

		// Remember that the drag process has started.
		m_existingItemDragInProgress = true;

		// Set the initial drop zone to be the DropZone that holds the widget being dragged.
		dropZone = dropWidget.getParentDropZone();
		if ( dropZone != null )
		{
			m_enteredDropZones.clear();
			
			// Configure the stack of entered drop zones.
			{
				DropZone parentDropZone;
				ArrayList<DropZone> parentDropZones;
				int i;
				
				parentDropZones = new ArrayList<DropZone>();
				
				parentDropZone = dropZone.getParentDropZone();
				while( parentDropZone != null )
				{
					parentDropZones.add( parentDropZone );
					
					parentDropZone = parentDropZone.getParentDropZone();
				}
				
				for (i = parentDropZones.size(); i > 0; --i)
				{
					m_enteredDropZones.push( parentDropZones.get( i-1 ) );
					GWT.log( "pushing parent DropZone onto stack: " + parentDropZones.get(i-1).getDebugName(), null );
				}
			}

			m_enteredDropZones.push( dropZone );
			GWT.log( "pushing DropZone that contains the widget being dragged: " + dropZone.getDebugName(), null );
			setDropZone( dropZone, x, y );
		}
	}
	
	
	/**
	 * 
	 */
	private void startDragPaletteItem( PaletteItem paletteItem, int x, int y )
	{
		GWT.log( "!!!!!!!!! startDragPaletteItem() !!!!!!!!!!" );
		// Get the drag proxy widget we should display and display it.
		m_paletteItemDragProxy = paletteItem.getDragProxy();
		
		// Remember the palette item we are dragging.
		m_paletteItemBeingDragged = paletteItem;

		// Position the drag proxy widget at the cursor position.
		m_paletteItemDragProxy.setPopupPosition( x+10, y+10 );
		
		// Show the drag-proxy widget.
		m_paletteItemDragProxy.show();
		
		// Register for mouse up event.
		m_mouseUpHandlerReg = addMouseUpHandler( this );
		
		// Register a preview-event handler.  We do this so we can consume the mouse-move
		// event when the user is dragging an item from the palette.
		m_previewHandlerReg = Event.addNativePreviewHandler( this );
//!!!		sinkEvents( Event.ONMOUSEMOVE );

		// Remember that the drag process has started.
		m_paletteItemDragInProgress = true;
	}// end startDragPaletteItem()
	
	
	//!!!
	/**
	 * 
	 */
	public static void log( final String msg )
	{
		Scheduler.ScheduledCommand cmd;
		
		cmd = new Scheduler.ScheduledCommand()
		{
			public void execute()
			{
				String msg1;
				String msg2;
				
				if ( LandingPageEditor.m_textBox != null )
				{
					msg1 = LandingPageEditor.m_textBox.getText();
					if ( msg1 != null )
						msg2 = msg1 + "\n" + msg;
					else
						msg2 = msg;
					LandingPageEditor.m_textBox.setText( msg2 );
					LandingPageEditor.m_textBox.setCursorPos( msg2.length() );
				}
			}
		};
		Scheduler.get().scheduleDeferred( cmd );
	}
}// end LandingPageEditor
