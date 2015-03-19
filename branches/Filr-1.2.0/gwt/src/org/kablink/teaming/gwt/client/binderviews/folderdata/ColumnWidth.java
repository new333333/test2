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
package org.kablink.teaming.gwt.client.binderviews.folderdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;

/**
 * Class used to specify column widths in a data table.
 * 
 * @author drfoster@novell.com
 */
public class ColumnWidth {
	private double	m_width;	//
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
	public ColumnWidth(double width, Unit units) {
		// Initialize this object...
		this();
		
		// ...and store the parameters.
		setWidth(width);
		setUnits(units);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param width
	 */
	public ColumnWidth(double width) {
		// Always use an alternate form of the constructor.
		this(width, Unit.PCT);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isPCT()    {return (Unit.PCT == m_units);}
	public boolean isPX()     {return (Unit.PX  == m_units);}
	public double  getWidth() {return m_width;              }
	public Unit    getUnits() {return m_units;              }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setWidth(double width) {m_width = width;}
	public void setUnits(Unit   units) {m_units = units;}

	/**
	 * Returns a copy of a ColumnWidth.
	 * 
	 * @return
	 */
	public ColumnWidth copyColumnWidth() {
		return new ColumnWidth(getWidth(), getUnits());
	}

	/**
	 * Returns a copy of a Map<String, ColumnWidth>.
	 * 
	 * @param cwList
	 * 
	 * @return
	 */
	public static Map<String, ColumnWidth> copyColumnWidths(Map<String, ColumnWidth> cwList) {
		// If we weren't given a list...
		if (null == cwList) {
			// ...don't return one.
			return null;
		}

		// Copy the list into a newly allocated
		// Map<String, ColumnWidth>()...
		Map<String, ColumnWidth> reply = new HashMap<String, ColumnWidth>();
		for (String cName:  cwList.keySet()) {
			ColumnWidth cw = cwList.get(cName);
			reply.put(cName, cw.copyColumnWidth());
		}
		
		// ...and return that.
		return reply;
	}

	/**
	 * Returns true of a provide ColumnWidth equals this one.
	 * 
	 * @param cw
	 * 
	 * @return
	 */
	public boolean equals(ColumnWidth cw) {
		// If we weren't given one to compare to...
		if (null == cw) {
			// ...they can't be equal.
			return false;
		}
		
		// Return true if the data members are equal and false
		// otherwise.
		return (cw.getWidth() == getWidth() && cw.getUnits().equals(getUnits()));
	}

	/**
	 * Returns a Column width as a String for use in a style.
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
	 * Returns true if a Map<String, ColumnWidth> contains any percent
	 * based widths and false otherwise.
	 * 
	 * @param folderColumns
	 * @param columnWidths
	 * @param defaultColumnWidth
	 * 
	 * @return
	 */
	public static boolean hasPCTWidths(List<FolderColumn> folderColumns, Map<String, ColumnWidth> columnWidths, ColumnWidth defaultColumnWidth) {
		// Were we given a column list and a column widths map?
		if ((null != folderColumns) && (null != columnWidths)) {
			// Yes!  Scan the columns.
			for (FolderColumn fc:  folderColumns) {
				// Is this column percent based?
				ColumnWidth cw = columnWidths.get(fc.getColumnName());
				if (null == cw) {
					cw = defaultColumnWidth;
				}
				if ((null != cw) && cw.isPCT()) {
					// Yes!  Return true.
					return true;
				}
			}
		}

		// If we get here, there were no percent based ColumnWidths in
		// the map for any of the columns.  Return false.
		return false;
	}

	/**
	 * Given a width string as returned by getWidthStyle(), returns an
	 * equivalent ColumnWidth object.
	 * 
	 * @param cwS
	 * 
	 * @return
	 * 
	 * @throws NumberFormatException
	 */
	public static ColumnWidth parseWidthStyle(String cwS) throws NumberFormatException {
		Unit units = Unit.PX;
		int unitPos = cwS.indexOf('%');
		if (0 < unitPos)
			 units   = Unit.PCT;
		else unitPos = cwS.indexOf("px");
		if (0 < unitPos) {
			cwS = cwS.substring(0, unitPos);
		}
		return new ColumnWidth(Double.parseDouble(cwS), units);
	}

	/**
	 * Returns a count of the columns that use a percentage width.
	 * 
	 * @param folderColumns
	 * @param columnWidths
	 * @param defaultColumnWidth
	 * 
	 * @return
	 */
	public static int pctColumns(List<FolderColumn> folderColumns, Map<String, ColumnWidth> columnWidths, ColumnWidth defaultColumnWidth) {
		// Were we given a column list and a column widths map?
		int pctTotal = 0;
		if ((null != folderColumns) && (null != columnWidths)) {
			// Yes!  Scan the columns.
			for (FolderColumn fc:  folderColumns) {
				// Is this column percent based?
				ColumnWidth cw = columnWidths.get(fc.getColumnName());
				if (null == cw) {
					cw = defaultColumnWidth;
				}
				if ((null != cw) && cw.isPCT()) {
					// Yes!  Add its width to the total.
					pctTotal += 1;
				}
			}
		}
		
		// If we get here, pctTotal contains a count of all the percent
		// based columns.  Return it.
		return pctTotal;
	}

	/**
	 * Returns a double value with pctWidth scaled based on a
	 * percentage total of pctTotal.
	 * 
	 * @param pctWidth
	 * @param pctTotal
	 * 
	 * @return
	 */
	public static double scalePCTWidth(double pctWidth, double pctTotal) {
		return ((pctWidth / pctTotal) * ((double) 100));
	}
	
	/**
	 * Returns the sum of the percent widths in a column widths Map.
	 * 
	 * @param folderColumns
	 * @param columnWidths
	 * @param defaultColumnWidth
	 * 
	 * @return
	 */
	public static double sumPCTWidths(List<FolderColumn> folderColumns, Map<String, ColumnWidth> columnWidths, ColumnWidth defaultColumnWidth) {
		// Were we given a column list and a column widths map?
		double pctTotal = 0;
		if ((null != folderColumns) && (null != columnWidths)) {
			// Yes!  Scan the columns.
			for (FolderColumn fc:  folderColumns) {
				// Is this column percent based?
				ColumnWidth cw = columnWidths.get(fc.getColumnName());
				if (null == cw) {
					cw = defaultColumnWidth;
				}
				if ((null != cw) && cw.isPCT()) {
					// Yes!  Add its width to the total.
					pctTotal += cw.getWidth();
				}
			}
		}
		
		// If we get here, pctTotal contains the total value of all the
		// percent based columns.  Return it.
		return pctTotal;
	}
	
	/**
	 * Returns the sum of the pixel widths in a column widths Map.
	 * 
	 * @param folderColumns
	 * @param columnWidths
	 * @param defaultColumnWidth
	 * 
	 * @return
	 */
	public static int sumPXWidths(List<FolderColumn> folderColumns, Map<String, ColumnWidth> columnWidths, ColumnWidth defaultColumnWidth) {
		// Were we given a column list and a column widths map?
		int pxTotal = 0;
		if ((null != folderColumns) && (null != columnWidths)) {
			// Yes!  Scan the columns.
			for (FolderColumn fc:  folderColumns) {
				// Is this column pixel based?
				ColumnWidth cw = columnWidths.get(fc.getColumnName());
				if (null == cw) {
					cw = defaultColumnWidth;
				}
				if ((null != cw) && cw.isPX()) {
					// Yes!  Add its width to the total.
					pxTotal += cw.getWidth();
				}
			}
		}
		
		// If we get here, pctTotal contains the total value of all the
		// pixel based columns.  Return it.
		return pxTotal;
	}
}
