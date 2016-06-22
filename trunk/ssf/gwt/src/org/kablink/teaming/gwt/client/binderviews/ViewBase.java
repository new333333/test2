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
package org.kablink.teaming.gwt.client.binderviews;

import com.google.gwt.user.client.ui.UIObject;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.MainContentLayoutPanel;

import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

/**
 * Base class that binder views MUST extend so that they'll say when
 * the view is ready to be used.
 * 
 * @author drfoster@novell.com
 */
public abstract class ViewBase extends ResizeComposite
	implements
		// Event handlers implemented by this class.
		ContributorIdsRequestEvent.Handler
{
	protected   final        ViewReady			m_viewReady;							// Stores a ViewReady created for the classes that extends it.
	protected final static GwtTeamingMessages	m_messages = GwtTeaming.getMessages();	// Access to the GWT localized string resource.

	// The following are used when setting the view's size to account
	// for padding, ...
	private int m_contentHeightAdjust	= GwtConstants.CONTENT_HEIGHT_ADJUST;
	private int m_contentWidthAdjust	= GwtConstants.CONTENT_WIDTH_ADJUST;
	protected UIObject m_parent;

	/**
	 * Callback interface used to interact with a view asynchronously
	 * after it loads. 
	 */
	public interface ViewClient {
		void onSuccess(ViewBase view);
		void onUnavailable();
	}

	/**
	 * Constructor method.
	 * 
	 * MUST be called by classes that extend this base class.
	 * 
	 * @param viewReady
	 */
	public ViewBase(ViewReady viewReady) {
		super();
		m_viewReady = viewReady;
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public int getContentHeightAdjust() {return m_contentHeightAdjust;}
	public int getContentWidthAdjust()  {return m_contentWidthAdjust; }

	/**
	 * Set'er methods.
	 * 
	 * @param contentHeightAdjust
	 * @param contentWidthAdjust
	 */
	public void setContentHeightAdjust(int contentHeightAdjust) {m_contentHeightAdjust = contentHeightAdjust;}
	public void setContentWidthAdjust( int contentWidthAdjust)  {m_contentWidthAdjust = contentWidthAdjust;  }

	/**
	 * Intercepts the initWidget call to the composite so that a
	 * ViewBase specific style can be added to it.
	 * 
	 * Overrides the ResizeComposite.initWidget() method. 
	 */
	@Override
	protected void initWidget(Widget widget) {
		GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ": initWidget() for " + widget.getClass().getSimpleName());
		super.initWidget(widget);
		addStyleName("vibe-viewBase");
	}
	
	/**
	 * Handles ContributorIdsRequestEvent's received by this class.
	 * 
	 * Implements the ContributorIdsRequestEvent.Handler.onContributorIdsRequest() method.
	 * 
	 * @param event
	 */
	@Override
	public abstract void onContributorIdsRequest(ContributorIdsRequestEvent event);
	
	/**
	 * Manages resizing the view.
	 * 
	 * Overrides the RequiresResize.onResize() method.
	 */
	@Override
	public void onResize() {
		super.onResize();
		setViewSize();
	}

	public void setParent(UIObject parent) {
		m_parent = parent;
	}

	/**
	 * Sets the size of the view based on the MainContentLayoutPanel
	 * that holds it.
	 */
	public void setViewSize() {
//		UIObject parent = m_parent;
//		if (parent==null) {
//			parent = GwtTeaming.getMainPage().getMainContentLayoutPanel();
//		}
//		GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize(). Parent=" + parent.getClass().getSimpleName() + "; Parent height: " + parent.getOffsetWidth());
//		setPixelSize((parent.getOffsetWidth() + m_contentWidthAdjust), (parent.getOffsetHeight() + m_contentHeightAdjust));
	}
	
	/**
	 * Called by classes that extend this base class so that it can
	 * inform the world that its view is ready to go.
	 */
	public void viewReady() {
		if (null != m_viewReady) {
			m_viewReady.viewReady();
		}
	}
}
