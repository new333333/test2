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

import com.google.gwt.user.client.ui.*;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * Wraps a GWT FlowPanel to so that it can participate in various
 * layout panels.
 *  
 * @author jwootton
 */
public class VibeFlowPanel extends FlowPanel
	implements ProvidesResize, RequiresResize, VibeEntityViewPanel
{
	private boolean fillParent = false;

	/**
	 * Constructor method.
	 */
	public VibeFlowPanel()
	{
		// Initialize the super class.
		super();
	}

	public VibeFlowPanel(boolean fillParent) {
		this.fillParent = fillParent;
	}

	public void setViewSize() {
		if (fillParent) {
			UIObject parent = getParent();
			int width;
			int height;
			if (parent instanceof VibeEntityViewPanel) {
				width = ((VibeEntityViewPanel)parent).getContainingWidth(this);
				height = ((VibeEntityViewPanel)parent).getContainingHeight(this);
			} else {
				width = parent.getOffsetWidth();
				height = parent.getOffsetHeight();
			}
			GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize().  Parent: " + parent.getClass().getSimpleName() + "; Parent size: (" + width + ", " + height + ")");
			height += GwtConstants.CONTENT_WIDTH_ADJUST;
			width += GwtConstants.CONTENT_WIDTH_ADJUST;
			GwtClientHelper.consoleLog(this.getClass().getSimpleName() + ".setViewSize(). New size: (" + width + "," + height + ")");
			setPixelSize(width, height);
		}
	}


	/**
	 */
	@Override
	public void onResize()
	{
		onResizeAsync();
	}//end onResize()

	/*
	 * Asynchronously resizes the flow panel.
	 */
	private void onResizeAsync()
	{
		GwtClientHelper.deferCommand(new ScheduledCommand() {
			@Override
			public void execute() {
				onResizeNow();
			}
		});
	}//end onResizeAsync()
	
	/*
	 * Synchronously resizes the flow panel.
	 */
	private void onResizeNow()
	{
		WidgetCollection children = getChildren();
		for (Widget child : children)
	    {
	    	if (child instanceof RequiresResize)
	        {
	        	((RequiresResize) child).onResize();
	        }
	    }
		setViewSize();
	}//end onResizeNow()

	@Override
	public void showWidget(Widget widget) {
		add(widget);
	}

	public void setMinPixelSize(int width, int height) {
		getElement().getStyle().setProperty("minWidth", "" + width + "px");
		getElement().getStyle().setProperty("minHeight", "" + height + "px");
	}

	@Override
	public int getContainingHeight(Widget widget) {
		return getOffsetHeight();
	}

	@Override
	public int getContainingWidth(Widget widget) {
		return getOffsetWidth();
	}
}
