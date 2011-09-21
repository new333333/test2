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

import org.kablink.teaming.gwt.client.lpe.ConfigItem;
import org.kablink.teaming.gwt.client.lpe.TableConfig;
import org.kablink.teaming.gwt.client.lpe.TableProperties;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;

/**
 * 
 * @author jwootton
 *
 */
public class TableWidget extends VibeWidget
{

	/**
	 * 
	 */
	public TableWidget( TableConfig config )
	{
		TableProperties properties;
		
		properties = null;
		if ( config != null )
			properties = config.getProperties();
		
		init( properties );
		
		// Add a widget to this table as defined in the TableConfig data
		addChildWidgetsToTable( config );
	}

	/**
	 * Create a widget for every child of this table as defined in the TableConfig data.
	 */
	public void addChildWidgetsToTable( TableConfig configData )
	{
		int i;
		int col;
		
		col = 0;
		for (i = 0; i < configData.numItems(); ++i)
		{
			ConfigItem configItem;
			
			// Get the next ConfigItem.
			configItem = configData.get( i );

//!!!
/*
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
*/
		}
	}
	
	/**
	 * Create the table based on the given properties.
	 */
	private void createTable( TableProperties props )
	{
		int i;
		int numColumns;
		int width;
		String widthStr;
		CellFormatter cellFormatter;
		
		numColumns = props.getNumColumnsInt();
	
//!!
/*
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
				m_flexTable.addCell( 0 );
			}
		}
		
		cellFormatter = m_flexTable.getFlexCellFormatter();
		for (i = 0; i < m_flexTable.getCellCount( 0 ); ++i )
		{
			ColWidthUnit unit;
			Element tdElement;
			
			// Get the width unit for this column.
			unit = props.getColWidthUnit( i );
			
			// Get the width of this column.
			widthStr = props.getColWidth( i );
			
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
			tdElement = cellFormatter.getElement( 0, i );
			tdElement.setAttribute( "width", widthStr );
			
			// Set the vertical alignment of this cell to "top".
			cellFormatter.setVerticalAlignment( 0, i, HasVerticalAlignment.ALIGN_TOP );
		}
*/
	}

	
	/**
	 * 
	 */
	private void init( TableProperties properties )
	{
		FlowPanel wrapperPanel;

		wrapperPanel = new FlowPanel();
		wrapperPanel.addStyleName( "dropWidgetWrapperPanel" );

//!!!
/*
		m_mainPanel = new FlowPanel();
		m_mainPanel.addStyleName( "lpeDropWidget" );
		
		// Create the table
		createTable( properties );
		
		// All composites must call initWidget() in their constructors.
		wrapperPanel.add( m_mainPanel );
		initWidget( wrapperPanel );
*/
	}
	
}
