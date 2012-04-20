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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.BinderInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Blog folder view.
 * 
 * @author jwootton@novell.com
 */
public class BlogFolderView extends FolderViewBase
{
	private List<HandlerRegistration> m_registeredEventHandlers;	// Event handlers that are currently registered.

	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[]
    {
	};
	
	/**
	 * Constructor method.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 */
	private BlogFolderView( BinderInfo folderInfo, ViewReady viewReady )
	{
		// Simply initialize the super class.
		super( folderInfo, viewReady, "vibe-blogFolder", false );
	}
	
	/**
	 * Called to construct the view.
	 * 
	 * Implements the FolderViewBase.constructView() method.
	 */
	@Override
	public void constructView()
	{
	}

	/**
	 * Returns true for panels that are to be included and false
	 * otherwise.
	 * 
	 * Overrides the FolderViewBase.includePanel() method.
	 * 
	 * @param folderPanel
	 * 
	 * @return
	 */
	@Override
	protected boolean includePanel( FolderPanels folderPanel )
	{
		boolean reply;

		// In the blog folder view, we show the following:
		// 1. Breadcrumb
		// 2. Description
		// 3. List of binders control
		// 4. List of blog entries
		// 5. Footer
		switch ( folderPanel )
		{
		case BREADCRUMB:
		case DESCRIPTION:
		case ENTRY_MENU:
		case FOOTER:
			reply = true;
			break;
			
		default:
			reply = false;
			break;
		}
		
		return reply;
	}

	/**
	 * Called when the blog folder view is attached.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach()
	{
		// Let the widget attach and then register our event handlers.
		super.onAttach();
		registerEvents();
	}
	
	/**
	 * Called when the blog folder view is detached.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach()
	{
		// Let the widget detach and then unregister our event
		// handlers.
		super.onDetach();
		unregisterEvents();
	}
	
	/*
	 * Registers any global event handlers that need to be registered.
	 */
	private void registerEvents()
	{
		// If we having allocated a list to track events we've
		// registered yet...
		if ( null == m_registeredEventHandlers )
		{
			// ...allocate one now.
			m_registeredEventHandlers = new ArrayList<HandlerRegistration>();
		}

		// If the list of registered events is empty...
		if ( m_registeredEventHandlers.isEmpty() )
		{
			// ...register the events.
			EventHelper.registerEventHandlers(
										GwtTeaming.getEventBus(),
										m_registeredEvents,
										this,
										m_registeredEventHandlers );
		}
	}

	/**
	 * Called from the base class to reset the content of this
	 * discussion folder view.
	 * 
	 * Implements the FolderViewBase.resetView() method.
	 */
	@Override
	public void resetView()
	{
		getFlowPanel().clear();
		//!!! What to do here?
		//populateViewAsync();
	}
	
	/**
	 * Synchronously sets the size of the view.
	 * 
	 * Implements the FolderViewBase.resizeView() method.
	 */
	@Override
	public void resizeView()
	{
		//!!!m_taskListing.resize();
	}

	/*
	 * Unregisters any global event handlers that may be registered.
	 */
	private void unregisterEvents()
	{
		// If we have a non-empty list of registered events...
		if ( null != m_registeredEventHandlers && !m_registeredEventHandlers.isEmpty() )
		{
			// ...unregister them.  (Note that this will also empty the
			// ...list.)
			EventHelper.unregisterEventHandlers( m_registeredEventHandlers );
		}
	}
	
	/**
	 * Called when everything about the view (tool panels, ...) is
	 * complete.
	 * 
	 * Overrides the FolderViewBase.viewComplete() method.
	 */
	@Override
	public void viewComplete()
	{
		// Tell the task listing to resize itself now that it can
		// determine how big everything is.
		//!!!m_taskListing.resize();
	}
	
	/**
	 * Loads the BlogFolderView split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param folderInfo
	 * @param viewReady
	 * @param vClient
	 */
	public static void createAsync( final BinderInfo folderInfo, final ViewReady viewReady, final ViewClient vClient )
	{
		GWT.runAsync( BlogFolderView.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				BlogFolderView blogFolderView;
				
				blogFolderView = new BlogFolderView( folderInfo, viewReady );
				vClient.onSuccess( blogFolderView );
			}
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( m_messages.codeSplitFailure_BlogFolderView() );
				vClient.onUnavailable();
			}
		});
	}
}
