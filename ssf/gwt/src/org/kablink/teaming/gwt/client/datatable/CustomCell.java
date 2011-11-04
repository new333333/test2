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
package org.kablink.teaming.gwt.client.datatable;

import org.kablink.teaming.gwt.client.util.EntryEventInfo;
import org.kablink.teaming.gwt.client.util.EntryLinkInfo;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Data table cell that represents a custom value from an entry.
 * 
 * @author drfoster@novell.com
 */
public class CustomCell extends AbstractCell<Object> {
	/**
	 * Constructor method.
	 */
	public CustomCell() {
		super();
	}

	/**
	 * Called to render an instance of this cell.
	 * 
	 * @param context
	 * @param fileId
	 * @param sb
	 * 
	 * Overrides AbstractCell.render()
	 */
	@Override
	public void render(Context context, Object obj, SafeHtmlBuilder sb) {
		// If the object we're rendering is null...
		if (null == obj) {
			// ...bail.  Cell widgets can pass null to cells if the
			// ...underlying data contains a null, or if the data
			// ...arrives out of order.
			return;
		}

		// Render the data into a flow panel...
		VibeFlowPanel fp = new VibeFlowPanel();
		if      (obj instanceof EntryEventInfo) renderEvent( fp, ((EntryEventInfo) obj));
		else if (obj instanceof EntryLinkInfo)  renderLink(  fp, ((EntryLinkInfo)  obj));
		else if (obj instanceof String)         renderString(fp, ((String)         obj));
		
		// ...and render that into the cell.
		SafeHtml rendered = SafeHtmlUtils.fromTrustedString(fp.getElement().getInnerHTML());
		sb.append(rendered);
	}

	/*
	 * Renders an EntryEventInfo into a flow panel.
	 */
	private void renderEvent(VibeFlowPanel fp, EntryEventInfo eei) {
//!		...this needs to be implemented...
		InlineLabel label = new InlineLabel("custom event");
		label.setWordWrap(false);
		fp.add(label);
	}
	
	/*
	 * Renders an EntryLinkInfo into a flow panel.
	 */
	private void renderLink(VibeFlowPanel fp, EntryLinkInfo eli) {
//!		...this needs to be implemented...
		InlineLabel label = new InlineLabel("custom link");
		label.setWordWrap(false);
		fp.add(label);
	}
	
	/*
	 * Renders a String into a flow panel.
	 */
	private void renderString(VibeFlowPanel fp, String value) {
		InlineLabel label = new InlineLabel(value);
		label.setWordWrap(false);
		fp.add(label);
	}
}
