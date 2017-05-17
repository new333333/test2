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

import org.kablink.teaming.gwt.client.widgets.PropertiesObj;



/**
 * This class holds all of the properties needed to define a table widget in a landing page.
 * @author jwootton
 *
 */
public class TableProperties
	implements PropertiesObj
{
	private boolean	m_showBorder;
	private int m_numRows;
	private String[] m_colWidths;
	private ColWidthUnit[] m_colWidthUnits;
	
	/**
	 * 
	 */
	public TableProperties()
	{
		m_showBorder = false;
		
		// Default to 1 row.
		setNumRows( 1 );
		
		// Default to 2 columns.
		setNumColumns( 2 );
	}// end TableProperties()
	
	
	/**
	 * 
	 */
	public void copy( PropertiesObj props )
	{
		if ( props instanceof TableProperties )
		{
			TableProperties tableProps;
			int i;
			int numCols;
			
			tableProps = (TableProperties) props;
			setShowBorder( tableProps.getShowBorderValue() );
			
			setNumRows( tableProps.getNumRowsInt() );
			
			numCols = tableProps.getNumColumnsInt();
			setNumColumns( numCols );
			
			for (i = 0; i < numCols; ++i)
			{
				setColWidth( i, tableProps.getColWidth( i ) );
				setColWidthUnit( i, tableProps.getColWidthUnit( i ) );
			}
		}
	}// end copy()
	
	
	/**
	 * Return the properties as a string that can be stored in the db.
	 */
	public String createConfigString()
	{
		String str;
		String colWidthsStr;
		String unitsStr;
		int i;
		
		// The string should look like: "tableStart,showBorder=1,rows=2, cols=3,colWidths=33%7C33%7C33;widthUnits=1%7C%2%7C%1"
		str = "tableStart,";
		if ( m_showBorder )
			str += "showBorder=1,";
		
		// Add the number of rows.
		str += "rows=" + getNumRowsStr() + ",";
		
		str += "cols=" + getNumColumnsStr() + ",";
		
		str += "colWidths=";
		colWidthsStr = "";
		for (i = 0; i < getNumColumnsInt(); ++i)
		{
			String colWidth;
			
			colWidth = getColWidth( i );
			if ( colWidth != null )
			{
				if ( i > 0 )
					colWidthsStr += "|";
				
				colWidthsStr += colWidth;
			}
		}
		str += ConfigData.encodeConfigData( colWidthsStr ) + ",";
		
		str += "widthUnits=";
		unitsStr = "";
		for (i = 0; i < getNumColumnsInt(); ++i)
		{
			ColWidthUnit unit;
			
			unit = getColWidthUnit( i );
			if ( unit != ColWidthUnit.UNDEFINED )
			{
				if ( i > 0 )
					unitsStr += "|";
				
				unitsStr += String.valueOf( unit.getValue() );
			}
		}
		str += ConfigData.encodeConfigData( unitsStr );
		
		str += ";";

		return str;
	}// end createConfigString()
	
	
	/**
	 * Return the width of the given column as a string.
	 */
	public String getColWidth( int col )
	{
		String width;
		
		// Is the requested column valid?
		if ( col < getNumColumnsInt() )
		{
			// Yes
			width = m_colWidths[col];
		}
		else
		{
			// The requested column is invalid.  Just return 0.
			width = "0";
		}
		
		return width;
	}// end getColWidthStr()
	
	/**
	 * Return the width unit for the given column
	 */
	public ColWidthUnit getColWidthUnit( int col )
	{
		if ( col < getNumColumnsInt() )
		{
			return m_colWidthUnits[col];
		}
		
		return ColWidthUnit.PERCENTAGE;
	}
	
	
	/**
	 * Return the value of the "Number of columns" property as a string.
	 */
	public String getNumColumnsStr()
	{
		return( String.valueOf( getNumColumnsInt() ) );
	}// end getNumColumnsStr()


	/**
	 * Return the value of the "Number of columns" property as an int.
	 */
	public int getNumColumnsInt()
	{
		return( m_colWidths.length );
	}// end getNumColumnsInt()
	
	
	/**
	 * Return the value of the "number of rows" property as a string.
	 */
	public String getNumRowsStr()
	{
		return String.valueOf( getNumRowsInt() );
	}
	
	/**
	 * Return the value of the "number of rows" property as an int
	 */
	public int getNumRowsInt()
	{
		return m_numRows;
	}
	
	
	/**
	 * Return the value of the "show border" property
	 */
	public boolean getShowBorderValue()
	{
		return m_showBorder;
	}// end getShowBorderValue()
	
	
	/**
	 * 
	 */
	public void setColWidth( int col, String width )
	{
		// Is the column valid?
		if ( col < getNumColumnsInt() )
		{
			// Yes
			m_colWidths[col] = width;
		}
	}// end setColWidth()
	
	/**
	 * 
	 */
	public void setColWidthUnit( int col, ColWidthUnit unit )
	{
		// Is the column valid?
		if ( col < getNumColumnsInt() )
		{
			// Yes
			m_colWidthUnits[col] = unit;
		}
	}
	
	
	/**
	 * 
	 */
	public void setNumColumns( int numColumns )
	{
		int defaultWidth;
		int i;
		
		m_colWidths = new String[numColumns];
		m_colWidthUnits = new ColWidthUnit[numColumns];
		
		// Set the default width of each column.
		if ( numColumns > 0 )
		{
			defaultWidth = 100 / numColumns;
			for (i = 0; i < numColumns; ++i)
			{
				m_colWidths[i] = String.valueOf( defaultWidth );
				m_colWidthUnits[i] = ColWidthUnit.PERCENTAGE;
			}
		}
	}// end setNumColumns()
	
	
	/**
	 * 
	 */
	public void setNumRows( int numRows )
	{
		if ( numRows > 0 )
			m_numRows = numRows;
	}
	
	/**
	 * 
	 */
	public void setShowBorder( boolean show )
	{
		m_showBorder = show;
	}// end setShowBorder()
}// end TableProperties
