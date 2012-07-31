/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.datatable;

import java.util.List;

import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.ShareStringValue;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Data table cell that represents a list of assignments (for calendar
 * entries, tasks or milestones.)
 * 
 * @author drfoster@novell.com
 */
public class ShareStringValueCell extends AbstractCell<List<ShareStringValue>> {
	/**
	 * Constructor method.
	 */
	public ShareStringValueCell() {
		// Initialize the super class.
		super();
	}

	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param ssvList
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, List<ShareStringValue> ssvList, SafeHtmlBuilder sb) {
		// If we weren't given a List<ShareStringValue>...
		if ((null == ssvList) || ssvList.isEmpty()) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			GwtClientHelper.renderEmptyHtml(sb);
			return;
		}

		// Create the panel to hold the HTML of the strings.
		VibeFlowPanel renderPanel = new VibeFlowPanel();

		// Scan the string values.
		int svsIndex = 0;
		for (ShareStringValue ssv:  ssvList) {
			// Generate a panel to hold the string.
			VibeFlowPanel fp = new VibeFlowPanel();
			fp.addStyleName("vibe-dataTableShareStringValue-panel displayBlock verticalAlignTop");
			if (0 < svsIndex) {
				fp.addStyleName("margintop3px");
			}
			svsIndex += 1;
			InlineLabel il = new InlineLabel(ssv.getValue());
			il.addStyleName("vibe-dataTableShareStringValue-label");
			il.setWordWrap(false);
			fp.add(il);
			renderPanel.add(fp);
		}
		
		// Finally, render the panel with the list of strings into the
		// cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(renderPanel.getElement().getInnerHTML());
		sb.append(rendered);
	}
}
