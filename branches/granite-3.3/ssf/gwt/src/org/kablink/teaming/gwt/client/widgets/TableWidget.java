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

import org.kablink.teaming.gwt.client.lpe.ColWidthUnit;
import org.kablink.teaming.gwt.client.lpe.ConfigItem;
import org.kablink.teaming.gwt.client.lpe.TableColConfig;
import org.kablink.teaming.gwt.client.lpe.TableConfig;
import org.kablink.teaming.gwt.client.lpe.TableProperties;
import org.kablink.teaming.gwt.client.lpe.TableRowConfig;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;

/**
 * 
 * @author jwootton
 *
 */
public class TableWidget extends VibeWidget
{
	private VibeFlowPanel m_layoutPanel;
	private FlexTable m_table;
	private String m_style;
	private WidgetStyles m_widgetStyles;

	/**
	 * 
	 */
	public TableWidget( TableConfig config, WidgetStyles widgetStyles )
	{
		TableProperties properties;
		
		properties = config.getProperties();
		m_style = config.getLandingPageStyle();
		m_widgetStyles = widgetStyles;
		
		init( properties );
		
		// Add a widget to this table as defined in the TableConfig data
		addChildWidgetsToTable( config );

		// All composites must call initWidget() in their constructors.
		initWidget( m_layoutPanel );
	}

	/**
	 * Create a widget for every child defined in TableRowConfig and add the children
	 * to the given row.
	 */
	private void addChildWidgetsFromConfigToRow( TableRowConfig tableRowConfigData, int row )
	{
		// Is the given row valid?
		if ( row < m_table.getRowCount() )
		{
			int col;
			
			// Yes
			// Get the items that belong in this row.
			for (col = 0; col < m_table.getCellCount( row ) && col < tableRowConfigData.numItems(); ++col)
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
		if ( col < m_table.getCellCount( row ) )
		{
			int i;
			ResizeComposite widget;
			VibeFlowPanel flowPanel;
			
			// Yes
			// Get the panel that holds all the widgets for this cell.
			flowPanel = (VibeFlowPanel) m_table.getWidget( row, col );
			
			if ( flowPanel != null )
			{
				if ( configData instanceof TableColConfig )
				{
					TableColConfig tableColConfig;
					
					tableColConfig = (TableColConfig) configData;
					for (i = 0; i < tableColConfig.numItems(); ++i)
					{
						ConfigItem configItem;
						
						// Get the next piece of configuration information.
						configItem = tableColConfig.get( i );
						
						// Create the appropriate widget based on the given ConfigItem.
						widget = configItem.createWidget( m_widgetStyles );
						if ( widget != null )
							flowPanel.add( widget );
						else
						{
							Label label;
							
							label = new Label( "widget: " + configItem.getClass().getName() );
							flowPanel.add( label );
						}
					}
				}
				else
				{
					// Create the appropriate Widget based on the configuration data.
					widget = configData.createWidget( m_widgetStyles );
					
					// Add the widget to the appropriate col.
					flowPanel.add( widget );
				}
			}
		}
	}
	
	
	/**
	 * Create a widget for every child of this table as defined in the TableConfig data.
	 */
	public void addChildWidgetsToTable( TableConfig configData )
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
	}
	
	/**
	 * Create the table based on the given properties.
	 */
	private void createTable( TableProperties props )
	{
		int row;
		int col;
		int numColumns;
		int numRows;
		int width;
		String widthStr;
		CellFormatter cellFormatter;
		
		numRows = props.getNumRowsInt();
		numColumns = props.getNumColumnsInt();
		
		m_table = new FlexTable();
		m_table.addStyleName( "tableWidget" + m_style );
		m_table.setCellPadding( 4 );
		
		cellFormatter = m_table.getFlexCellFormatter();

		// Add the appropriate number of rows to the table.
		for (row = 0; row < numRows; ++row)
		{
			m_table.insertRow( 0 );

			// Add the appropriate number of columns to the table.
			for (col = 0; col < numColumns; ++col)
			{
				ColWidthUnit unit;
				Element tdElement;
				VibeFlowPanel flowPanel;
				
				m_table.addCell( 0 );

				// Turn borders on/off
				if ( props.getShowBorderValue() )
				{
					cellFormatter.removeStyleName( 0, col, "landingPageWidgetNoBorder" );
					cellFormatter.addStyleName( 0, col, "landingPageWidgetShowBorder" );
					
					GwtClientHelper.setElementBorderStyles( cellFormatter.getElement( 0, col ), m_widgetStyles );
				}
				else
				{
					cellFormatter.removeStyleName( 0, col, "landingPageWidgetShowBorder" );
					cellFormatter.addStyleName( 0, col, "landingPageWidgetNoBorder" );
				}

				// Get the width unit for this column.
				unit = props.getColWidthUnit( col );
				
				// Get the width of this column.
				widthStr = props.getColWidth( col );
				
				// Are we dealing with percentage?
				if ( unit == ColWidthUnit.PERCENTAGE )
				{
					// Yes
					try
					{
						width = Integer.parseInt( widthStr );
					}
					catch (Exception ex)
					{
						// Error parsing the width, default to 25%
						width = 25;
					}
					
					// IE does not allow a width of 0%.  If the width is 0 set it to 1.
					if ( width == 0 )
						width = 1;
					
					widthStr = String.valueOf( width );
				}
				
				widthStr = widthStr + unit.getHtmlUnit();

				// IE chokes if we call cellFormatter.setWidth(...) and pass in "*" for the width.
				// That is why we call tdElement.setAttribute(...)
				//cellFormatter.setWidth( 0, i, widthStr );
				tdElement = cellFormatter.getElement( 0, col );
				tdElement.setAttribute( "width", widthStr );
				
				// Set the vertical alignment of this cell to "top".
				cellFormatter.setVerticalAlignment( 0, col, HasVerticalAlignment.ALIGN_TOP );
				
				// Create a VibeFlowPanel that will hold all of the widgets that live in this cell
				flowPanel = new VibeFlowPanel();
				m_table.setWidget( 0, col, flowPanel );
			}
		}
		
		m_layoutPanel.add( m_table );
	}
	
	
	/**
	 * 
	 */
	private void init( TableProperties properties )
	{
		m_layoutPanel = new VibeFlowPanel();
		m_layoutPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		m_layoutPanel.addStyleName( "tableWidgetMainPanel" + m_style );
		
		createTable( properties );
	}
}
