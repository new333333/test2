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

package org.kablink.teaming.gwt.client.binderviews.folderdata;

import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to specify column widths in a data table.
 * 
 * @author drfoster@novell.com
 */
public class ColumnWidth implements IsSerializable {
	private int		m_width;	//
	private Unit	m_units;	//

	/**
	 * Constructor method.
	 */
	public ColumnWidth() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param width
	 * @param units
	 */
	public ColumnWidth(int width, Unit units) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setWidth(width);
		setUnits(units);
	}
	
	public ColumnWidth(long width, Unit units) {
		// Always use an alternate form of the constructor.
		this(((int) width), units);
	}
	
	public ColumnWidth(double width, Unit units) {
		// Always use an alternate form of the constructor.
		this(((int) width), units);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param width
	 */
	public ColumnWidth(int width) {
		// Always use an alternate form of the constructor.
		this(width, Unit.PCT);
	}
	public ColumnWidth(long width) {
		// Always use an alternate form of the constructor.
		this(((int) width), Unit.PCT);
	}
	public ColumnWidth(double width) {
		// Always use an alternate form of the constructor.
		this(((int) width), Unit.PCT);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int  getWidth() {return m_width;}
	public Unit getUnits() {return m_units;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setWidth(int    width) {m_width =        width; }
	public void setWidth(long   width) {m_width = ((int) width);}
	public void setWidth(double width) {m_width = ((int) width);}
	public void setUnits(Unit   units) {m_units =        units; }

	/**
	 * Returns a Column width as a String for use in a style
	 * 
	 * @return
	 */
	public String getWidthStyle() {
		return (getWidth() + getUnits().getType());
	}
	
	public static String getWidthStyle(ColumnWidth cw) {
		return ((null == cw) ? null : cw.getWidthStyle());
	}

	/**
	 * Returns true of a Map<String, ColumnWidth> contains any percent
	 * based widths and false otherwise.
	 * 
	 * @param columnWidths
	 * 
	 * @return
	 */
	public static boolean hasPercentWidths(Map<String, ColumnWidth> columnWidths) {
		// Were we given a Map?
		if (null != columnWidths) {
			// Yes!  Scan the ColumnWidth's in the Map.
			for (String cName:  columnWidths.keySet()) {
				// Is this ColumnWidth percentage based?
				ColumnWidth cw = columnWidths.get(cName);
				if (Unit.PCT == cw.getUnits()) {
					// Yes!  Return true.
					return true;
				}
			}
		}

		// If we get here, there were no percentage base ColumnWidths
		// in the Map.  Return false.
		return false;
	}
}
