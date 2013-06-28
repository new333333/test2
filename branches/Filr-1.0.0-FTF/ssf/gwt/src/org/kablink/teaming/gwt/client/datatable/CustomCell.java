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

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.EntryEventInfo;
import org.kablink.teaming.gwt.client.util.EntryLinkInfo;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * Data table cell that represents a custom value from an entry.
 * 
 * @author drfoster@novell.com
 */
public class CustomCell extends AbstractCell<Object> {
	protected final static GwtTeamingMessages	m_messages = GwtTeaming.getMessages();	// Access to the GWT localized string resource.
	
	/**
	 * Constructor method.
	 */
	public CustomCell() {
		super();
	}

	/*
	 * Creates an appends a styled InlineLabel to a VibeFlowPanel
	 * optionally followed by a hard line break.
	 */
	private static void appendIL(VibeFlowPanel fp, String label, String style, boolean appendBR) {
		InlineLabel il = new InlineLabel(label);
		if (GwtClientHelper.hasString(style)) {
			il.addStyleName(style);
		}
		il.setWordWrap(false);
		fp.add(il);
		if (appendBR) {
			GwtClientHelper.appendBR(fp);
		}
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
			GwtClientHelper.renderEmptyHtml(sb);
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
		// Is there any event information to display?
		String end     = eei.getEndDate();      boolean hasEnd     = GwtClientHelper.hasString(end  );
		String start   = eei.getStartDate();    boolean hasStart   = GwtClientHelper.hasString(start);
		int    durDays = eei.getDurationDays(); boolean hasDurDays = (0 < durDays);
		if (hasEnd || hasStart || hasDurDays) {
			if (eei.getAllDayEvent()) appendIL(fp, (m_messages.vibeDataTable_Event_AllDay()),                          "vibe-dataTableCustom-event vibe-dataTableCustom-event-allDay",  (hasStart || hasEnd || hasDurDays));
			if (hasStart)             appendIL(fp, (m_messages.vibeDataTable_Event_Start() + " " + start),             "vibe-dataTableCustom-event vibe-dataTableCustom-event-start",               (hasEnd || hasDurDays));
			if (hasEnd)               appendIL(fp, (m_messages.vibeDataTable_Event_End()   + " " + end),               "vibe-dataTableCustom-event vibe-dataTableCustom-event-end",                            hasDurDays );
			if (hasDurDays)           appendIL(fp, (m_messages.vibeDataTable_Event_Duration(String.valueOf(durDays))), "vibe-dataTableCustom-event vibe-dataTableCustom-event-duration", false                            );
		}
	}

	/*
	 * Renders an EntryLinkInfo into a flow panel.
	 */
	private void renderLink(VibeFlowPanel fp, EntryLinkInfo eli) {
		// If there's no HREF...
		String linkText = eli.getText();
		String href     = eli.getHref();
		String target   = eli.getTarget();
		if (!(GwtClientHelper.hasString(href))) {
			// ...we don't render anything.
			return;
		}

		// If there's no text for the link...
		if (!(GwtClientHelper.hasString(linkText))) {
			// ...display the HREF.
			linkText = href;
		}

		// Create the Anchor...
		Anchor a = new Anchor();
		a.addStyleName("vibe-dataTableCustom-anchor");
		a.setHref(href);
		if (GwtClientHelper.hasString(target)) {
			a.setTarget(target);
		}

		// ...create the label for the anchor...
		InlineLabel il = new InlineLabel(linkText);
		il.addStyleName("vibe-dataTableCustom-anchor-label");
		il.setWordWrap(false);

		// ...and tie it all together.
		a.getElement().appendChild(il.getElement());
		fp.add(a);
	}
	
	/*
	 * Renders a String into a flow panel.
	 */
	private void renderString(VibeFlowPanel fp, String value) {
		InlineLabel label = new InlineLabel(value);
		label.addStyleName("vibe-dataTableCustom-string");
		label.setWordWrap(false);
		fp.add(label);
	}
}
