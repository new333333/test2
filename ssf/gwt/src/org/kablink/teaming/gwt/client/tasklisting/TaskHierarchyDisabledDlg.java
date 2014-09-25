/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.tasklisting;

import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Implements a dialog for telling the user the reasons why task
 * hierarchy manipulation is disabled.
 *  
 * @author drfoster@novell.com
 */
public class TaskHierarchyDisabledDlg extends DlgBox {
	private GwtTeamingMessages	m_messages;					// Access to the GWT UI messages.
	private VerticalPanel		m_taskHierarchyDisabledVP;	// Once displayed, the table of reasons task hierarchy manipulation is disabled.
	private List<String>		m_reasons;					// The reasons why task hierarchy manipulation is disabled.

	/*
	 * Inner class that wraps labels displayed in the dialog's content.
	 */
	private class DlgLabel extends Label {
		public DlgLabel(String label, String addedStyle) {
			super(label);
			addStyleName("taskHierarchyDisabledDlg_Label");
			if (!(GwtClientHelper.hasString(addedStyle))) {
				addedStyle = "gwtUI_nowrap";
			}
			addStyleName(addedStyle);
		}
		
		@SuppressWarnings("unused")
		public DlgLabel(String label) {
			this(label, null);
		}
	}

	/**
	 * Class constructor.
	 * 
	 * @param reasons
	 */
	public TaskHierarchyDisabledDlg(List<String> reasons) {
		// Initialize the superclass...
		super(false, true, (-1), (-1), DlgButtonMode.Close);

		// ...initialize everything else...
		m_messages = GwtTeaming.getMessages();
		m_reasons  = reasons;
	
		// ...and create the dialog's content.
		createAllDlgContent(
			m_messages.taskHierarchyDisabledDlgHeader(),
			getSimpleSuccessfulHandler(),	// The dialog's EditSuccessfulHandler.
			getSimpleCanceledHandler(),		// The dialog's EditCanceledHandler.
			null);							// Data passed via global data members.
	}
	
	/**
	 * Creates all the controls that make up the dialog.
	 * 
	 * Implements the DlgBox.createContent() abstract method.
	 * 
	 * @param ignored
	 * 
	 * @return
	 */
	@Override
	public Panel createContent(Object ignored) {
		// Create a panel to hold the dialog's content...
		m_taskHierarchyDisabledVP = new VerticalPanel();
		m_taskHierarchyDisabledVP.addStyleName("taskHierarchyDisabledDlg_VP");

		// ...add a banner about what's being displayed...
		m_taskHierarchyDisabledVP.add(
			new DlgLabel(
				m_messages.taskHierarchyDisabledDlgBanner(),
				"taskHierarchyDisabledDlg_Banner"));

		// ...add a bulleted list of reasons as an HTML string...
		StringBuffer html = new StringBuffer("<ul class=\"taskHierarchyDisabledDlg_UL\">");
		for (String reason:  m_reasons) {
			html.append("<li class=\"taskHierarchyDisabledDlg_LI\">" + SafeHtmlUtils.htmlEscape(reason) + "</li>");
		}
		html.append("</ul>");
		m_taskHierarchyDisabledVP.add(new HTML(html.toString()));

		// ...and finally, return the panel the with the dialog's
		// ...contents.
		return m_taskHierarchyDisabledVP;
	}
	
	/**
	 * Returns the edited List<ToolbarItem>.
	 * 
	 * Implements the DlgBox.getDataFromDlg() abstract method.
	 * 
	 * @return
	 */
	@Override
	public Object getDataFromDlg() {
		// Not used.  Return something non-null.
		return Boolean.TRUE;
	}

	/**
	 * Returns the Widget to give the focus to.
	 * 
	 * Implements the DlgBox.getFocusWidget() abstract method.
	 * 
	 * @return
	 */
	@Override
	public FocusWidget getFocusWidget() {
		// There is no specific focus widget for this dialog.
		return null;
	}
}
