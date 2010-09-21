/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.presence;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.service.GwtRpcServiceAsync;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;

public class PresenceControl extends Composite {
	private String m_binderId;
	private boolean m_bShowStatusText;
	private boolean m_bClickStartsIm;
	private boolean m_bHideIfUnknown;
	private Anchor m_presenceA = null;
	private String m_presenceAStyleName = null;
	private ClickHandler m_presenceAClickHandler = null;
	private FlowPanel panel;
	
	public PresenceControl(String binderId, boolean bShowStatusText, boolean bClickStartsIm, boolean bHideIfUnknown) {
		m_binderId = binderId;
		m_bShowStatusText = bShowStatusText;
		m_bClickStartsIm = bClickStartsIm;
		m_bHideIfUnknown = bHideIfUnknown;

		panel = new FlowPanel();

		getPresenceInfo();

		initWidget(panel);
	}

	/**
	 * Add a click handler to the presence anchor. 
	 */
	public void addClickHandler( ClickHandler clickHandler )
	{
		m_presenceAClickHandler = clickHandler;
		if ( m_presenceA != null )
			m_presenceA.addClickHandler( clickHandler );
	}
	
	private void getPresenceInfo() {
		m_presenceA = new Anchor();

		AsyncCallback<GwtPresenceInfo> callback = new AsyncCallback<GwtPresenceInfo>() {
			public void onFailure(Throwable t) {
				panel.setVisible(false);
			}

			public void onSuccess(GwtPresenceInfo pi) {
				Image presenceImage;
				String statusText = pi.getStatusText();
				switch (pi.getStatus()) {
					case GwtPresenceInfo.STATUS_AVAILABLE:
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceAvailable16());
						if (statusText == null) {
							statusText = GwtTeaming.getMessages().presenceAvailable();
						}
						break;
					case GwtPresenceInfo.STATUS_IDLE:
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceAway16());
						if (statusText == null) {
							statusText = GwtTeaming.getMessages().presenceIdle();
						}
						break;
					case GwtPresenceInfo.STATUS_AWAY:
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceAway16());
						if (statusText == null) {
							statusText = GwtTeaming.getMessages().presenceAway();
						}
						break;
					case GwtPresenceInfo.STATUS_BUSY:
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceBusy16());
						if (statusText == null) {
							statusText = GwtTeaming.getMessages().presenceBusy();
						}
						break;
					case GwtPresenceInfo.STATUS_OFFLINE:
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceOffline16());
						if (statusText == null) {
							statusText = GwtTeaming.getMessages().presenceOffline();
						}
						break;
					default:
						statusText = "";
						presenceImage = new Image(GwtTeaming.getImageBundle().presenceUnknown16());
				}
				if (pi.getStatus() != GwtPresenceInfo.STATUS_UNKNOWN || !m_bHideIfUnknown) {
					if ( m_presenceAStyleName != null )
						m_presenceA.addStyleName( m_presenceAStyleName );
					else
						m_presenceA.addStyleName("presenceImgA");
					presenceImage.getElement().setAttribute( "align", "absmiddle" );
					m_presenceA.getElement().appendChild(presenceImage.getElement());
					m_presenceA.setVisible(true);
					
					if ( m_presenceAClickHandler != null )
						m_presenceA.addClickHandler( m_presenceAClickHandler );
					
					if (m_bClickStartsIm) {
						m_presenceA.addClickHandler(new InstantMessageClickHandler(m_binderId));
						m_presenceA.setTitle(GwtTeaming.getMessages().qViewInstantMessageTitle() + " (" + statusText + ")");
					} else {
						m_presenceA.setTitle(statusText);
					}
					panel.add(m_presenceA);
					if (m_bShowStatusText && statusText != null && statusText.length() > 0) {
						InlineLabel statusTextLabel = new InlineLabel("(" + statusText + ")");
						panel.add(statusTextLabel);
					}
				} else {
					panel.setVisible(false);
				}
			}
		};

		GwtRpcServiceAsync rpcService = GwtTeaming.getRpcService();		
		rpcService.getPresenceInfo(m_binderId, callback);
	}
	
	
	/**
	 * Set the style used with the presence anchor. 
	 */
	public void setAnchorStyleName( String styleName )
	{
		m_presenceAStyleName = styleName;
		
		if ( m_presenceA != null )
		{
			m_presenceA.removeStyleName( "presenceImgA" );
			m_presenceA.addStyleName( styleName );
		}
	}
}
