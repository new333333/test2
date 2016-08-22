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
import org.kablink.teaming.gwt.client.widgets.VibeEntityViewPanel;

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
	protected          ViewReady			m_viewReady;							// Stores a ViewReady created for the classes that extends it.
	protected final static GwtTeamingMessages	m_messages = GwtTeaming.getMessages();	// Access to the GWT localized string resource.

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
	public ViewBase(UIObject parent, ViewReady viewReady) {
		super();
		m_viewReady = viewReady;
		m_parent = parent;
	}

	/**
	 * Intercepts the initWidget call to the composite so that a
	 * ViewBase specific style can be added to it.
	 * 
	 * Overrides the ResizeComposite.initWidget() method. 
	 */
	@Override
	protected void initWidget(Widget widget) {
		super.initWidget(widget);
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
//		GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ": onResize()");
		super.onResize();
		setViewSize();
	}

	protected boolean scrollEntireView() {
		return false;
	}


	/**
	 * Sets the size of the view based on the MainContentLayoutPanel
	 * that holds it.
	 */
	public void setViewSize() {
		if (!scrollEntireView()) {
			UIObject parent = m_parent;
			if (parent == null) {
				parent = GwtTeaming.getMainPage().getMainContentLayoutPanel();
			}
			int width;
			int height;
			if (parent instanceof VibeEntityViewPanel) {
				width = ((VibeEntityViewPanel)parent).getContainingWidth(this);
				height = ((VibeEntityViewPanel)parent).getContainingHeight(this) + GwtConstants.BINDER_VIEW_EMBEDDED_ADJUST;
			} else {
				width = parent.getOffsetWidth();
				height = parent.getOffsetHeight() + GwtConstants.BINDER_VIEW_ADJUST;
			}
			//GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize(). New size: (" + width + "," + height + ")");
			if (width!=getOffsetWidth()) {
				setWidth(Math.max(width, getMinimumViewHeight()) + "px");
			}
			if (height!=getOffsetHeight()) {
				setHeight(Math.max(height, getMinimumViewHeight()) + "px");
			}
		}
	}

	public int getMinimumViewHeight() {
		return 0;
	}

	protected int getMinimumViewWidth() {
		return 0;
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
