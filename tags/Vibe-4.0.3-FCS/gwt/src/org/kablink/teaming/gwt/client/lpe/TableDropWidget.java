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
package org.kablink.teaming.gwt.client.lpe;

import java.util.ArrayList;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox.DlgBoxClient;
import org.kablink.teaming.gwt.client.widgets.PropertiesObj;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

/**
 * ?
 *  
 * @author jwootton
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
	 * Create a widget for every child defined in TableRowConfig and add the children
	 * to the given row.
	 */
	private void addChildWidgetsFromConfigToRow( TableRowConfig tableRowConfigData, int row )
	{
		// Is the given row valid?
		if ( row < m_flexTable.getRowCount() )
		{
			int col;
			
			// Yes
			// Get the items that belong in this row.
			for (col = 0; col < m_flexTable.getCellCount( row ) && col < tableRowConfigData.numItems(); ++col)
			{
				ConfigItem configItem;
				
				// Get the next item that should go in this row.
				configItem = tableRowConfigData.get( col );
				
				// A TableRowConfig should only hold TableColConfig items.
				if ( configItem instanceof TableColConfig )
				{
					addChildWidgetsFromConfigToCell( configItem, row, col );
				}
				else
				{
					Window.alert( "Invalid landing page configuration: TableRowConfig contains something other than a TableColConfig." );
				}
			}
		}
	}
	
	
	/**
	 * Create a widget for every child defined in TableColConfig and add the children to
	 * the given col in the table.
	 */
	private void addChildWidgetsFromConfigToCell( ConfigItem configData, int row, int col )
	{
		// Is the given col valid?
		if ( col < m_flexTable.getCellCount( row ) )
		{
			int i;
			DropZone dropZone;
			
			// Yes
			// Get the DropZone used in the given col.
			dropZone = (DropZone) m_flexTable.getWidget( row, col );

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
						dropWidget = configItem.createDropWidget( m_lpe );
						
						// Add the widget to the col's drop zone.
						if ( dropWidget != null )
							dropZone.addWidgetToDropZone( dropWidget );
					}
				}
				else
				{
					// Create the appropriate DropWidget based on the configuration data.
					dropWidget = configData.createDropWidget( m_lpe );
					
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
		int row;
		
		row = 0;
		for (i = 0; i < configData.numItems(); ++i)
		{
			ConfigItem configItem;
			
			// Get the next ConfigItem.
			configItem = configData.get( i );
			
			// A TableConfig can only hold TableRowConfig items.
			if ( configItem instanceof TableRowConfig )
			{
				addChildWidgetsFromConfigToRow( (TableRowConfig)configItem, row );
				++row;
			}
			else
			{
				Window.alert( "Invalid landing page configuration: Table contains something other than a TableRowConfig." );
				
				// In theory a TableRowConfig should be the only item contained by a TableConfig object.
				// However, the old landing page editor created invalid configuration data
				// where a tableStart did not contain a tableCol element.
				addChildWidgetsFromConfigToCell( configItem, row, 0 );
			}
		}
	}// end addChildWidgetsFromConfigToTable()
	
	
	/**
	 * We want the height of every DropZone in this table to be the same.  Figure out how tall the tallest DropZone is
	 * and make every DropZone that height.
	 */
	public void adjustTableHeight()
	{
		int row;
		int col;
		int numColumns;
		int numRows;
		Widget widget;
		
		numRows = m_flexTable.getRowCount();
		
		// Make the height of all the drop zones in this table 100%
		for (row = 0; row < numRows; ++row )
		{
			numColumns = m_flexTable.getCellCount( row );

			for (col = 0; col < numColumns; ++col)
			{
				// Get the DropZone for this cell.
				widget = m_flexTable.getWidget( row, col );
				
				// Is this widget a DropZone
				if ( widget instanceof DropZone )
				{
					DropZone dropZone;
					
					// Yes, set the height of the drop zone.
					dropZone = (DropZone) widget;
					dropZone.setZoneHeight( 100, Unit.PCT );
				}
			}
		}

		for (row = 0; row < numRows; ++row)
		{
			int maxHeight;
			
			maxHeight = 0;
			numColumns = m_flexTable.getCellCount( row );

			// Calculate the height of the DropZone found in each cell.
			for (col = 0; col < numColumns; ++col)
			{
				// Get the DropZone for this cell.
				widget = m_flexTable.getWidget( row, col );
				
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
			}
			
			// Make the minimum height 50 pixels.
			if ( maxHeight < 50 )
				maxHeight = 50;
			
			// Make all of the drop zones the same height
			for (col = 0; col < numColumns; ++col)
			{
				// Get the DropZone for this cell.
				widget = m_flexTable.getWidget( row, col );
				
				// Is this widget a DropZone
				if ( widget instanceof DropZone )
				{
					DropZone dropZone;
					
					// Yes, set the height of the drop zone.
					dropZone = (DropZone) widget;
					dropZone.setZoneHeight( maxHeight, Unit.PX );
				}
			}
		}
	}// end adjustTableHeight()
	
	
	/**
	 * Check to see if this widget contains the given DropZone.
	 */
	@Override
	public boolean containsDropZone( DropZone dropZone )
	{
		int row;
		int numColumns;
		int numRows;
		
		numRows = m_flexTable.getRowCount();

		for (row = 0; row < numRows; ++row)
		{
			int col;

			numColumns = m_flexTable.getCellCount( row );
	
			// For every cell, check to see if that cell holds the given DropZone
			for (col = 0; col < numColumns; ++col)
			{
				Widget widget;
	
				// Get the DropZone for this cell.
				widget = m_flexTable.getWidget( row, col );
				
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
		}

		// If we get here, we don't hold the given drop zone.
		return false;
	}
	
	
	/**
	 * Create a configuration string that represents this widget and that can be stored in the db.
	 */
	@Override
	public String createConfigString()
	{
		String configStr;
		ArrayList<DropWidget> childWidgets;
		DropWidget nextWidget;
		Widget widget;
		int row;
		int numRows;
		
		// Get the configuration string for the properties of this table.
		configStr = m_properties.createConfigString();
		
		numRows = m_flexTable.getRowCount();
		
		for (row = 0; row < numRows; ++row)
		{
			int col;
			int numColumns;

			configStr += "tableRow;";
			
			numColumns = m_flexTable.getCellCount( row );
	
			// For every cell, get the widgets that live in that cell.
			for (col = 0; col < numColumns; ++col)
			{
				ColWidthUnit units;
				
				configStr += "tableCol";
				configStr += ",colWidth=" + m_properties.getColWidth( col );
				units = m_properties.getColWidthUnit( col );
				configStr += ",widthUnits=" + String.valueOf( units.getValue() ) + ";";
				
				// Get the DropZone for this cell.
				widget = m_flexTable.getWidget( row, col );
				
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
			}

			configStr += "tableRowEnd;";
		}
		
		configStr += "tableEnd;";
		
		return configStr;
	}// end createConfigString()
	
	
	/**
	 * Return the drag proxy object that should be displayed when the user drags this item.
	 */
	@Override
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
	@Override
	public void getPropertiesDlgBox( int xPos, int yPos, DlgBoxClient dBoxClient )
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
		
		dBoxClient.onSuccess( m_tableDlgBox );
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
	 * 
	 */
	@Override
	public void onAttach()
	{
		super.onAttach();
		setTableWidthAsync();
	}
	

	/**
	 * Set the DropZone this widget lives in.
	 */
	@Override
	public void setParentDropZone( DropZone dropZone )
	{
		int row;
		int numRows;

		super.setParentDropZone( dropZone );
		
		numRows = m_flexTable.getRowCount();
		
		for (row = 0; row < numRows; ++row)
		{
			int col;
			int numColumns;

			numColumns = m_flexTable.getCellCount( row );
	
			// For every cell, tell the DropZone in that cell who its parent DropZone is.
			for (col = 0; col < numColumns; ++col)
			{
				Widget widget;
	
				// Get the DropZone for this cell.
				widget = m_flexTable.getWidget( row, col );
				
				// Is this widget a DropZone
				if ( widget instanceof DropZone )
				{
					DropZone nextDropZone;
					
					// Yes
					nextDropZone = (DropZone) widget;
					nextDropZone.setParentDropZone( dropZone );
					
				}
			}// end for()
		}
		
		// Adjust the width of each column so the table fits into the new drop zone
		setTableWidthAsync();
	}

	/*
	 * Asynchronously sets the width of the table and adjusts the width
	 * of each column.
	 */
	private void setTableWidthAsync() {
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				setTableWidthNow();
			}
		} );
	}
	
	/*
	 * Synchronously sets the width of the table and adjusts the width
	 * of each column.
	 */
	private void setTableWidthNow()
	{
		int row;
		int colWidth;
		int width;
		String colWidthStr;
		CellFormatter cellFormatter;
		
		if ( isAttached() == false )
			return;
		
		width = getOffsetWidth() - 19;
		m_flexTable.getElement().setAttribute( "width", String.valueOf( width ) );
		
		// Set the width of each column.
		cellFormatter = m_flexTable.getFlexCellFormatter();
		colWidth = width / m_properties.getNumColumnsInt();
		colWidthStr = String.valueOf( colWidth );
		for (row = 0; row < m_flexTable.getRowCount(); ++row)
		{
			int i;
			
			for (i = 0; i < m_flexTable.getCellCount( row ); ++i )
			{
				cellFormatter.setWidth( row, i, colWidthStr );

				// Set the width of the drop zone for this cell
				{
					Widget widget;

					// Get the DropZone for this cell.
					widget = m_flexTable.getWidget( row, i );
					
					// Is this widget a DropZone?
					if ( widget instanceof DropZone )
					{
						DropZone dropZone;
						
						// Yes, set the width of the drop zone.
						dropZone = (DropZone) widget;
						dropZone.setZoneWidth( colWidth, Unit.PX );
					}
				}
				
				// Set the vertical alignment of this cell to "top".
				cellFormatter.setVerticalAlignment( row, i, HasVerticalAlignment.ALIGN_TOP );
			}
		}
	}
		
	/**
	 * Create the appropriate ui based on the given properties.
	 */
	@Override
	public void updateWidget( Object props )
	{
		int numColumns;
		int numRows;
		int row;
		
		// Save the properties that were passed to us.
		if ( props instanceof PropertiesObj )
			m_properties.copy( (PropertiesObj) props );
		
		numRows = m_properties.getNumRowsInt();
		numColumns = m_properties.getNumColumnsInt();
		
		// Have we already created a FlexTable?
		if ( m_flexTable == null )
		{
			int col;
			
			// No
			m_flexTable = new FlexTable();
			m_flexTable.setCellSpacing( 0 );
			m_flexTable.addStyleName( "lpeTable" );
			
			m_mainPanel.add( m_flexTable );
			
			// Add the appropriate number of rows and cols to the table.
			for (row = 0; row < numRows; ++row)
			{
				m_flexTable.insertRow( 0 );
				
				// Add the appropriate number of columns to the row.
				for (col = 0; col < numColumns; ++col)
				{
					DropZone	dropZone;
					
					m_flexTable.addCell( 0 );
					dropZone = new DropZone( m_lpe, "lpeTableDropZone" );
					dropZone.setParentDropZone( getParentDropZone() );
					m_flexTable.setWidget( 0, col, dropZone );
				}
			}
		}
		else
		{
			// Do we need to remove rows from the existing table?
			if ( numRows < m_flexTable.getRowCount() )
			{
				// Yes
				while ( numRows < m_flexTable.getRowCount() )
				{
					int rowIndex;
					
					// Remove the last row in the table.
					rowIndex = m_flexTable.getRowCount() - 1;
					m_flexTable.removeRow( rowIndex );
				}
			}
			// Do we need to add rows to the existing table?
			else if ( numRows > m_flexTable.getRowCount() )
			{
				// Yes
				while ( numRows > m_flexTable.getRowCount() )
				{
					int col;
					int rowIndex;
					
					rowIndex = m_flexTable.getRowCount();
					m_flexTable.insertRow( rowIndex );
					
					// Add the appropriate number of columns to the row.
					for (col = 0; col < numColumns; ++col)
					{
						DropZone	dropZone;
						
						m_flexTable.addCell( rowIndex );
						dropZone = new DropZone( m_lpe, "lpeTableDropZone" );
						dropZone.setParentDropZone( getParentDropZone() );
						m_flexTable.setWidget( rowIndex, col, dropZone );
					}
				}
			}
			
			// Adjust the number of columns in each row as needed.
			for (row = 0; row < m_flexTable.getRowCount(); ++row)
			{
				// Do we need to delete columns from this row?
				if ( numColumns < m_flexTable.getCellCount( row ) )
				{
					// Yes.
					while ( numColumns < m_flexTable.getCellCount( row ) )
					{
						m_flexTable.removeCell( row, m_flexTable.getCellCount( row )-1 );
					}
				}
				// Do we need to add columns to this row?
				else if ( numColumns > m_flexTable.getCellCount( row ) )
				{
					// Yes
					while( numColumns > m_flexTable.getCellCount( row ) )
					{
						DropZone dropZone;
						
						m_flexTable.addCell( row );
						dropZone = new DropZone( m_lpe, "lpeTableDropZone" );
						dropZone.setParentDropZone( getParentDropZone() );
						m_flexTable.setWidget( row, m_flexTable.getCellCount( row )-1, dropZone );
					}
				}
			}
		}
		
		// Set the width of the table
		setTableWidthAsync();
	}// end updateWidget()
}// end TableDropWidget
