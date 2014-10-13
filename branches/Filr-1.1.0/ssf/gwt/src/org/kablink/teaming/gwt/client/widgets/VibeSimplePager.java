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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.rpc.shared.FolderRowsRpcResponseData.TotalCountType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.Range;

/**
 * Class used to provide a simple pager for Vibe's data and cell
 * tables.
 * 
 * @author drfoster@novell.com
 */
public class VibeSimplePager extends SimplePager {
	private TotalCountType						m_totalCountType;						//
	
	private final static GwtTeamingMessages		m_messages = GwtTeaming.getMessages();	// Access to the GWT localized string resource.
	private final static SimplePager.Resources	SIMPLE_PAGER_RESOURCES = GWT.create(SimplePager.Resources.class);
	
	/**
	 * Constructor method.
	 */
	public VibeSimplePager() {
		// Simply initialize the super class.
		super(
			TextLocation.CENTER,
			SIMPLE_PAGER_RESOURCES,
			false,	// false -> No fast forward button...
			0,		//          ...hence no fast forward rows needed.
			true);	// true -> Show last page button.
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public TotalCountType getTotalCountType() {return m_totalCountType;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setTotalCountType(TotalCountType totalCountType) {m_totalCountType = totalCountType;}
	
	/**
	 * Set the page start index.  We override this method to
	 * fix the problem that the last page display will be
	 * weird without this.
	 * 
	 * Overrides the SimplePager.setPageStart() method.
	 * 
	 * @param index
	 */
	@Override
	public void setPageStart(int index) {
	  if (getDisplay() != null) {
	    Range range = getDisplay().getVisibleRange();
	    int pageSize = range.getLength();

	    // Removed the min to show fixed ranges.
	    // if (isRangeLimited && display.isRowCountExact()) {
	    //	   index = Math.min(index, display.getRowCount() - pageSize);
	    // }

	    index = Math.max(0, index);
	    if (index != range.getStart()) {
	      getDisplay().setVisibleRange(index, pageSize);
	    }
	  }
	}
	
	/**
	 * Get the text to display in the pager that reflects the state of the pager.
	 *
	 * Overrides the SimplePager.createText() method.
	 * 
	 * @return the text
	 */
	@Override
	protected String createText() {
		HasRows display = getDisplay();
		
		int pageStart;
		int endIndex;
		int dataSize = display.getRowCount();
		if (0 == dataSize) {
			pageStart =
			endIndex  = 0;
		}
		else {
			Range range = display.getVisibleRange();
			pageStart = (range.getStart() + 1);
			int pageSize = range.getLength();
			endIndex = Math.min(dataSize, (pageStart + pageSize - 1));
			endIndex = Math.max(pageStart, endIndex);
		}
		NumberFormat formatter = NumberFormat.getFormat("#,###");
		String pageStartS = formatter.format(pageStart);
		String endIndexS  = formatter.format(endIndex );
		String dataSizeS  = formatter.format(dataSize );
		
		String text;
		if (display.isRowCountExact()) {
		     text = m_messages.vibeSimplePager_Of(    pageStartS, endIndexS, dataSizeS);
		}
		else {
			if (null == m_totalCountType) {
				m_totalCountType = TotalCountType.OF_OVER;
			}
			switch (m_totalCountType) {
			case APPROXIMATE:  text = m_messages.vibeSimplePager_OfApproximately(pageStartS, endIndexS, dataSizeS); break;
			case AT_LEAST:     text = m_messages.vibeSimplePager_OfAtLeast(      pageStartS, endIndexS, dataSizeS); break;
			case EXACT:
			case OF_OVER:
			default:           text = m_messages.vibeSimplePager_OfOver(         pageStartS, endIndexS, dataSizeS); break;
			}
		}
		return text;
	  }
}
