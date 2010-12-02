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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.widgets.DlgBox;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

/**
 * 
 * @author jwootton
 *
 */
public class TableDropWidget extends DropWidget
	implements HasDropZone
{
	private static TableWidgetDlgBox m_tableDlgBox = null;		// For efficiency sake, we only create one dialog box.
	private TableProperties	m_properties = null;
	private FlowPanel			m_mainPanel;
	private FlexTable			m_flexTable = null;
	

	/**
	 * 
	 */
	public TableDropWidget( LandingPageEditor lpe, TableConfig configData )
	{
		TableProperties properties;
		
		properties = null;
		if ( configData != null )
			properties = configData.getProperties();
		
		init( lpe, properties );
		
		// Create a widget for every child of this table as defined in the TableConfig data.
		addChildWidgetsFromConfigToTable( configData );
	}// end TableDropWidget()
	
	
	/**
	 * 
	 */
	public TableDropWidget( LandingPageEditor lpe, TableProperties properties )
	{
		init( lpe, properties );
	}// end TableDropWidget()
	

	/**
	 * Create a widget for every child defined in TableColConfig and add the children to
	 * the given col in the table.
	 */
	private void addChildWidgetsFromConfigToCell( ConfigItem configData, int col )
	{
		// Is the given col valid?
		if ( col < m_flexTable.getCellCount( 0 ) )
		{
			int i;
			DropZone dropZone;
			
			// Yes
			// Get the DropZone used in the given col.
			dropZone = (DropZone) m_flexTable.getWidget( 0, col );

			if ( dropZone != null )
			{
				DropWidget dropWidget;

				if ( configData instanceof TableColConfig )
				{
					TableColConfig tableColConfig;
					
					tableColConfig = (TableColConfig) configData;
					for (i = 0; i < tableColConfig.numItems(); ++i)
					{
						ConfigItem configItem;
						
						// Get the next piece of configuration information.
						configItem = tableColConfig.get( i );
						
						// Create the appropriate DropWidget based on the configuration data.
						dropWidget = DropWidget.createDropWidget( m_lpe, configItem );
						
						// Add the widget to the col's drop zone.
						dropZone.addWidgetToDropZone( dropWidget );
					}
				}
				else
				{
					// Create the appropriate DropWidget based on the configuration data.
					dropWidget = DropWidget.createDropWidget( m_lpe, configData );
					
					// Add the widget to the col's drop zone.
					dropZone.addWidgetToDropZone( dropWidget );
				}
			}
		}
	}// end addChildWidgetsFromConfigToCell()
	
	
	/**
	 * Create a widget for every child of this table as defined in the TableConfig data.
	 */
	public void addChildWidgetsFromConfigToTable( TableConfig configData )
	{
		int i;
		int col;
		
		col = 0;
		for (i = 0; i < configData.numItems(); ++i)
		{
			ConfigItem configItem;
			
			// Get the next ConfigItem.
			configItem = configData.get( i );
			
			// A TableConfig can only hold TableColConfig items.
			if ( configItem instanceof TableColConfig )
			{
				addChildWidgetsFromConfigToCell( configItem, col );
				++col;
			}
			else
			{
				// In theory a TableColConfig should be the only item contained by a TableConfig object.
				// However, the old landing page editor created invalid configuration data
				// where a tableStart did not contain a tableCol element.
				addChildWidgetsFromConfigToCell( configItem, col );
			}
		}
	}// end addChildWidgetsFromConfigToTable()
	
	
	/**
	 * We want the height of every DropZone in this table to be the same.  Figure out how tall the tallest DropZone is
	 * and make every DropZone that height.
	 */
	public void adjustTableHeight()
	{
		int maxHeight = 0;
		int i;
		int numColumns;
		Widget widget;
		
		numColumns = m_flexTable.getCellCount( 0 );

		// Make the height of all the drop zones in this table 100%
		for (i = 0; i < numColumns; ++i)
		{
			// Get the DropZone for this cell.
			widget = m_flexTable.getWidget( 0, i );
			
			// Is this widget a DropZone
			if ( widget instanceof DropZone )
			{
				DropZone dropZone;
				
				// Yes, set the height of the drop zone.
				dropZone = (DropZone) widget;
				dropZone.setHeight( "100%" );
			}
		}// end for()

		// Calculate the height of the DropZone found in each cell.
		for (i = 0; i < numColumns; ++i)
		{
			// Get the DropZone for this cell.
			widget = m_flexTable.getWidget( 0, i );
			
			// Is this widget a DropZone
			if ( widget instanceof DropZone )
			{
				DropZone dropZone;
				int height;
				
				// Yes, tell the drop zone to adjust the height of all its table widgets
				dropZone = (DropZone) widget;
				height = dropZone.adjustHeightOfAllTableWidgets();
				
				// Do we have a new tallest cell?
				if ( height > maxHeight )
					maxHeight = height;
			}
		}// end for()
		
		// Make the minimum height 50 pixels.
		if ( maxHeight < 50 )
			maxHeight = 50;
		
		// Make all of the drop zones the same height
		for (i = 0; i < numColumns; ++i)
		{
			// Get the DropZone for this cell.
			widget = m_flexTable.getWidget( 0, i );
			
			// Is this widget a DropZone
			if ( widget instanceof DropZone )
			{
				DropZone dropZone;
				
				// Yes, set the height of the drop zone.
				dropZone = (DropZone) widget;
				dropZone.setHeight( String.valueOf( maxHeight ) + "px" );
			}
		}// end for()
	}// end adjustTableHeight()
	
	
	/**
	 * Check to see if this widget contains the given DropZone.
	 */
	public boolean containsDropZone( DropZone dropZone )
	{
		int i;
		int numColumns;

		numColumns = m_flexTable.getCellCount( 0 );

		// For every cell, check to see if that cell holds the given DropZone
		for (i = 0; i < numColumns; ++i)
		{
			Widget widget;

			// Get the DropZone for this cell.
			widget = m_flexTable.getWidget( 0, i );
			
			// Is this widget a DropZone
			if ( widget instanceof DropZone )
			{
				DropZone nextDropZone;
				
				// Yes
				nextDropZone = (DropZone) widget;
				
				// Is this the DropZone we are looking for?
				if ( dropZone == nextDropZone )
				{
					// Yes
					return true;
				}
				
				// Does this drop zone contain the given drop zone?
				if ( nextDropZone.containsDropZone( dropZone ) )
				{
					// Yes
					return true;
				}
			}
		}// end for()

		// If we get here, we don't hold the given drop zone.
		return false;
	}
	
	
	/**
	 * Create a configuration string that represents this widget and that can be stored in the db.
	 */
	public String createConfigString()
	{
		String configStr;
		ArrayList<DropWidget> childWidgets;
		DropWidget nextWidget;
		Widget widget;
		int i;
		int numColumns;
		
		// Get the configuration string for the properties of this table.
		configStr = m_properties.createConfigString();
		
		numColumns = m_flexTable.getCellCount( 0 );

		// For every cell, get the widgets that live in that cell.
		for (i = 0; i < numColumns; ++i)
		{
			configStr += "tableCol,colWidth=" + m_properties.getColWidthStr( i ) + ";";
			
			// Get the DropZone for this cell.
			widget = m_flexTable.getWidget( 0, i );
			
			// Is this widget a DropZone
			if ( widget instanceof DropZone )
			{
				DropZone dropZone;
				
				// Yes, get all the widgets that live in this DropZone.
				dropZone = (DropZone) widget;
				childWidgets = dropZone.getWidgets();
				
				// Spin through the list of child widgets and get the configuration string from each one.
				if ( childWidgets != null )
				{
					int j;
					
					for (j = 0; j < childWidgets.size(); ++j)
					{
						String nextConfigStr;
						
						// Append the configuration string for the next widget.
						nextWidget = childWidgets.get( j );
						nextConfigStr = nextWidget.createConfigString();
						configStr += nextConfigStr;
					}
				}
			}
		}// end for()

		configStr += "tableEnd;";
		
		return configStr;
	}// end createConfigString()
	
	
	/**
	 * Return the drag proxy object that should be displayed when the user drags this item.
	 */
	public DragProxy getDragProxy()
	{
		if ( m_dragProxy == null )
		{
			// Create a drag proxy that will be displayed when the user drags this item.
			m_dragProxy = new DragProxy( GwtTeaming.getImageBundle().landingPageEditorTable(), GwtTeaming.getMessages().lpeTable() );
		}
		
		return m_dragProxy;
	}
	

	/**
	 * Return the dialog box used to edit the properties of this widget.
	 */
	public DlgBox getPropertiesDlgBox( int xPos, int yPos )
	{
		// Have we already created a dialog?
		if ( m_tableDlgBox == null )
		{
			// Pass in the object that holds all the properties for a TableDropWidget.
			m_tableDlgBox = new TableWidgetDlgBox( this, this, false, true, xPos, yPos, m_properties );
		}
		else
		{
			m_tableDlgBox.init( m_properties );
			m_tableDlgBox.initHandlers( this, this );
		}
		
		return m_tableDlgBox;
	}// end getPropertiesDlgBox()
	
	
	/**
	 * 
	 */
	public void init( LandingPageEditor lpe, TableProperties properties )
	{
		FlowPanel wrapperPanel;

		wrapperPanel = new FlowPanel();
		wrapperPanel.addStyleName( "dropWidgetWrapperPanel" );
		
		m_lpe = lpe;
		
		// Create an Edit/Delete control and position it at the top/right of this widget.
		// This control allows the user to edit the properties of this widget and to delete this widget.
		{
			ActionsControl ctrl;
			FlowPanel panel;
			
			ctrl = new ActionsControl( this, this, this );
			ctrl.addStyleName( "upperRight" );

			// Wrap the edit/delete control in a panel.  We position the edit/delete control on the right
			// side of the wrapper panel.
			panel = new FlowPanel();
			panel.addStyleName( "editDeleteWrapperPanel" );
			panel.add( ctrl );
			wrapperPanel.add( panel );
		}
		
		m_mainPanel = new FlowPanel();
		m_mainPanel.addStyleName( "lpeDropWidget" );
		
		// Create an object to hold all of the properties that define a table widget.
		m_properties = new TableProperties();
		
		// Create the widgets that make up this widget.
		updateWidget( properties );
		
		// All composites must call initWidget() in their constructors.
		wrapperPanel.add( m_mainPanel );
		initWidget( wrapperPanel );
	}// end init()
	

	/**
	 * Create the appropriate ui based on the given properties.
	 */
	public void updateWidget( PropertiesObj props )
	{
		int i;
		int numColumns;
		CellFormatter cellFormatter;
		
		// Save the properties that were passed to us.
		m_properties.copy( props );
		
		numColumns = m_properties.getNumColumnsInt();
		
		// Have we already created a FlexTable?
		if ( m_flexTable == null )
		{
			// No
			m_flexTable = new FlexTable();
			m_flexTable.addStyleName( "lpeTable" );
			m_flexTable.setWidth( "100%" );
			
			m_mainPanel.add( m_flexTable );
			
			// Add 1 row to the table.
			m_flexTable.insertRow( 0 );
			
			// Add the appropriate number of columns to the table.
			for (i = 0; i < numColumns; ++i)
			{
				DropZone	dropZone;
				
				m_flexTable.addCell( 0 );
				dropZone = new DropZone( m_lpe, "lpeTableDropZone" );
				m_flexTable.setWidget( 0, i, dropZone );
			}
		}
		else
		{
			// Do we need to remove columns from the existing table?
			if ( numColumns < m_flexTable.getCellCount( 0 ) )
			{
				// Yes.
				while ( numColumns < m_flexTable.getCellCount( 0 ) )
				{
					m_flexTable.removeCell( 0, m_flexTable.getCellCount( 0 )-1 );
				}
			}
			// Do we need to add columns to the existing table?
			else if ( numColumns > m_flexTable.getCellCount( 0 ) )
			{
				// Yes
				while( numColumns > m_flexTable.getCellCount( 0 ) )
				{
					DropZone dropZone;
					
					m_flexTable.addCell( 0 );
					dropZone = new DropZone( m_lpe, "lpeTableDropZone" );
					m_flexTable.setWidget( 0, m_flexTable.getCellCount( 0 )-1, dropZone );
				}
			}
		}
		
		int width;
		String widthStr;
		
		cellFormatter = m_flexTable.getFlexCellFormatter();
		for (i = 0; i < m_flexTable.getCellCount( 0 ); ++i )
		{
			// Get the width of this column.
			width = m_properties.getColWidthInt( i );
			
			// IE does not allow a width of 0%.  If the width is 0 set it to 1.
			if ( width == 0 )
				width = 1;
			
			widthStr = String.valueOf( width ) + "%";
			cellFormatter.setWidth( 0, i, widthStr );
			
			// Set the vertical alignment of this cell to "top".
			cellFormatter.setVerticalAlignment( 0, i, HasVerticalAlignment.ALIGN_TOP );
		}
	}// end updateWidget()
	
}// end TableDropWidget
