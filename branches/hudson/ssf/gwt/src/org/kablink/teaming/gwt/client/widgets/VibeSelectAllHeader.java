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

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.client.Event;

/**
 * Class used to represent a select all check box in a data table's
 * header.
 * 
 * @author drfoster@novell.com
 */
public class VibeSelectAllHeader extends Header<Boolean> {
	private boolean m_checked;	//

	/**
	 * Constructor method.
	 * 
	 * @param cell
	 */
	public VibeSelectAllHeader(CheckboxCell cell) {
		// Initialize the super class.
		super(cell);
	}

	/**
	 * Get'er methods.
	 * 
	 * Overrides the Header.getValue() method.
	 * 
	 * @return
	 */
	@Override
	public Boolean getValue() {return m_checked;}
	 
	/**
	 * Set'er methods.
	 * 
	 * Set the state of the selection.  If a row is unselected, we
	 * can call this method to deselect the header checkbox
	 * 
	 * @param checked
	 */
	public void setValue(boolean checked) {m_checked = checked;}
	 
	/**
	 * Called to handle events captured by a check box header.
	 * 
	 * @param context
	 * @param elem
	 * @param event
	 * 
	 * Overrides the Header.onBroserEvent() method.
	 */
	@Override
	public void onBrowserEvent(Context context, Element elem, NativeEvent event) {
		Event evt = Event.as(event);
		int eventType = evt.getTypeInt();
		switch (eventType) {
		case Event.ONCHANGE:
			m_checked = (!m_checked);
		}
		super.onBrowserEvent(context, elem, event);
	}
}
