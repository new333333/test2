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
package org.kablink.teaming.gwt.client.util;

import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Enumeration used to represent the size of a binder icon.
 * 
 * @author drfoster@novell.com
 */
public enum BinderIconSize implements IsSerializable {
	SMALL,
	MEDIUM,
	LARGE,
	
	UNDEFINED;
	
	// The following are used as the height and width of the displayed
	// binder images.
	public final static int AS_BINDER_HEIGHT_INT		=  1;
	public final static int AS_BINDER_WIDTH_INT			=  1;
	
	public final static int BINDER_HEIGHT_SMALL_INT		= (-1);	// 25:  (-1) -> No vertical   scaling.  The native height is used.
	public final static int BINDER_WIDTH_SMALL_INT		= (-1);	// 25:  (-1) -> No horizontal scaling.  The native width  is used.
	
	public final static int BINDER_HEIGHT_MEDIUM_INT	= (-1);	// 36
	public final static int BINDER_WIDTH_MEDIUM_INT		= (-1);	// 36
	
	public final static int BINDER_HEIGHT_LARGE_INT		= (-1);	// 48
	public final static int BINDER_WIDTH_LARGE_INT		= (-1);	// 48
	
	/**
	 * Converts the ordinal value of a BinderIconSize to its
	 * enumeration equivalent.
	 * 
	 * @param iconSizeOrdinal
	 * 
	 * @return
	 */
	public static BinderIconSize getEnum(int iconSizeOrdinal) {
		BinderIconSize iconSize;
		try                                      {iconSize = BinderIconSize.values()[iconSizeOrdinal];}
		catch (ArrayIndexOutOfBoundsException e) {iconSize = BinderIconSize.SMALL;                    }
		return iconSize;
	}

	/**
	 * Get'er methods.
	 * 
	 * The following are used to control the icon sizes displayed in
	 * various places within the Vibe UI.
	 * 
	 * @return
	 */
	public static BinderIconSize getActivityStreamIconSize()     {return BinderIconSize.MEDIUM;}
	public static BinderIconSize getChildBindersWidgetIconSize() {return BinderIconSize.SMALL; }
	public static BinderIconSize getDialogCaptionIconSize()      {return BinderIconSize.LARGE; }
	public static BinderIconSize getBreadCrumbIconSize()         {return BinderIconSize.MEDIUM;}
	public static BinderIconSize getListViewIconSize()           {return BinderIconSize.SMALL; }
	public static BinderIconSize getSidebarTreeIconSize()        {
		BinderIconSize reply;
		if (WorkspaceTreeControl.siteNavigationAvailable())
		     reply = BinderIconSize.SMALL;
		else reply = BinderIconSize.MEDIUM;
		return reply;
	}
	
	/**
	 * Returns the height to use for an icon of this size.
	 * 
	 * @return
	 */
	public int getBinderIconHeight() {
		int reply;
		switch (this) {
		default:
		case SMALL:   reply = BINDER_HEIGHT_SMALL_INT;  break;
		case MEDIUM:  reply = BINDER_HEIGHT_MEDIUM_INT; break;
		case LARGE:   reply = BINDER_HEIGHT_LARGE_INT;  break;
		}
		return reply;
	}
	
	/**
	 * Returns the width to use for an icon of this size.
	 * 
	 * @return
	 */
	public int getBinderIconWidth() {
		int reply;
		switch (this) {
		default:
		case SMALL:   reply = BINDER_WIDTH_SMALL_INT;  break;
		case MEDIUM:  reply = BINDER_WIDTH_MEDIUM_INT; break;
		case LARGE:   reply = BINDER_WIDTH_LARGE_INT;  break;
		}
		return reply;
	}
}
