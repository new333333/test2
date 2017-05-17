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
package org.kablink.teaming.gwt.client.binderviews;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingFilrImageBundle;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.widgets.VibeFlowPanel;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ResizeComposite;

/**
 * Class that holds the widget displayed for an empty team workspaces
 * view.
 * 
 * @author drfoster@novell.com
 */
public class EmptyTeamsComposite extends ResizeComposite {
	private GwtTeamingFilrImageBundle	m_filrImages;	// Access to Filr's images.
	private GwtTeamingMessages			m_messages;		// Access to Vibe's messages.
	private String						m_company;		// Initialized with the company name (i.e., Novell.)
	private String						m_product;		// Initialized with the current product name (i.e., Filr or Vibe.)
	private VibeFlowPanel				m_rootPanel;	// The panel containing everything about the composite.

	/**
	 * Constructor method.
	 * 
	 * @param ct
	 */
	public EmptyTeamsComposite() {
		// Initialize the super class...
		super();
		
		// ...initialize the data members requiring it...
		m_filrImages = GwtTeaming.getFilrImageBundle();
		m_messages   = GwtTeaming.getMessages();
		m_company    = m_messages.companyNovell();
		m_product    =
				(GwtClientHelper.isLicenseFilr() ?
					m_messages.productFilr() :
					m_messages.productVibe());
		
		// ...create the content...
		m_rootPanel = new VibeFlowPanel();
		m_rootPanel.addStyleName("vibe-emptyWidget-rootPanel");
		createContent();
		
		// ...and initialize the composite with the panel.
		initWidget(m_rootPanel);
	}

	/*
	 * Creates the content of the composite.
	 */
	private void createContent() {
		// Generate the header icon...
		VibeFlowPanel fp = new VibeFlowPanel();
		fp.addStyleName("vibe-emptyWidget-headImage");
		m_rootPanel.add(fp);
		Image i = GwtClientHelper.buildImage(m_filrImages.teams_transparent_72().getSafeUri().asString());
		fp.add(i);

		// ...generate the header text...
		fp = new VibeFlowPanel();
		fp.addStyleName("vibe-emptyWidget-head");
		m_rootPanel.add(fp);
		fp.getElement().setInnerText(m_messages.teamsList());

		// ...generate the sub header...
		fp = new VibeFlowPanel();
		fp.addStyleName("vibe-emptyWidget-headSub");
		m_rootPanel.add(fp);
		fp.getElement().setInnerText(m_messages.emptyTeams_SubHead());

		// ...generate the main body of information...
		fp = new VibeFlowPanel();
		fp.addStyleName("vibe-emptyWidget-info");
		m_rootPanel.add(fp);
		fp.getElement().setInnerText(m_messages.emptyTeams_Info_1(m_company, m_product));

		fp = new VibeFlowPanel();
		fp.addStyleName("vibe-emptyWidget-info");
		m_rootPanel.add(fp);
		fp.getElement().setInnerText(m_messages.emptyTeams_Info_2(m_company, m_product));
	}
}
