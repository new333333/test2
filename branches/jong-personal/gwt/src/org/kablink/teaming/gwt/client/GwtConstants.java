/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client;

/**
 * Various constants used throughout the GWT UI.
 * 
 * @author drfoster@novell.com
 */
public class GwtConstants {
	public static final int		PANEL_PADDING				= 8;		// Number of pixels of padding used around our various widgets.
	
	public static final int		CONTENT_WIDTH_ADJUST		= ( -8);	// Adjust all content area controls by this horizontally...
	public static final int		CONTENT_HEIGHT_ADJUST		= (-20);	// ...and this vertically.

	public static final int		BINDER_VIEW_ADJUST			= ( -8);

	public static final double	HEADER_HEIGHT				= 200;		// Default height of the header panel.
	public static final int		WORKSPACE_TREE_WIDTH		= 250;		// Must match the definition of the workspaceTreeWidth style. 
	public static final int		SIDEBAR_TREE_WIDTH_ADJUST	=  16;		// Based on empirical evidence (left, right spacing, ...)
	public static final double	SIDEBAR_TREE_WIDTH			= (WORKSPACE_TREE_WIDTH + SIDEBAR_TREE_WIDTH_ADJUST);	// Default width of the sidebar tree panel.
	public static final double	DESKTOP_APP_DOWNLOAD_HEIGHT	= 50;		// Height of the desktop application download control.
	
	// Default height and width of popup windows launched from URLs.
	public final static int DEFAULT_POPUP_WIDTH		= 1024;
	public final static int DEFAULT_POPUP_HEIGHT	=  768;
	
	// Defines the maximum lengths a binder or file name can be.
	public final static int MAX_BINDER_NAME_LENGTH				= 255;
	public final static int MAX_FILE_NAME_LENGTH				= 225;
	public final static int MAX_PROXY_IDENTITY_NAME_LENGTH		= 255;
	public final static int MAX_PROXY_IDENTITY_PASSWORD_LENGTH	= 255;
	public final static int MAX_PROXY_IDENTITY_TITLE_LENGTH		= 255;
	
	/*
	 * Constructor method. 
	 */
	private GwtConstants() {
		// Inhibits this class from being instantiated.
	}
}
