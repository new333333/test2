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
package org.kablink.teaming.gwt.client.datatable;

/**
 * Constants used by the various Vibe data table implementations (i.e.,
 * VibeDataGrid's, VibeCellTable's, ...
 * 
 * @author drfoster@novell.com
 */
public class VibeDataTableConstants {
	// The following are used as event names that are captured by the
	// various cell handlers. 
	public final static String CELL_EVENT_CLICK		= "click";
	public final static String CELL_EVENT_KEYDOWN	= "keydown";
	public final static String CELL_EVENT_MOUSEOUT	= "mouseout";
	public final static String CELL_EVENT_MOUSEOVER	= "mouseover";
	
	// The following are used to name widgets stored in various cells.
	public final static String CELL_WIDGET_ATTRIBUTE					= "n-cellWidget";
	public final static String CELL_WIDGET_ENTRY_ACTION_MENU_IMAGE		= "entryActionMenuImg";
	public final static String CELL_WIDGET_ENTRY_COMMENTS_PANEL			= "entryCommentsPanel";
	public final static String CELL_WIDGET_EMAIL_ADDRESS_LABEL			= "emailAddressLabel";
	public final static String CELL_WIDGET_EMAIL_ADDRESS_LABEL_NOLINK	= "emailAddressLabelNoLink";
	public final static String CELL_WIDGET_ENTITY_ID					= "n-entityId";
	public final static String CELL_WIDGET_ENTITY_TITLE					= "n-entityTitle";
	public final static String CELL_WIDGET_ENTRY_DOWNLOAD_LABEL			= "entryDownloadLabel";
	public final static String CELL_WIDGET_ENTRY_PIN_IMAGE				= "entryPinImg";
	public final static String CELL_WIDGET_ENTRY_TITLE_LABEL			= "entryTitleLabel";
	public final static String CELL_WIDGET_ENTRY_TITLE_LABEL_NOLINK		= "entryTitleLabelNoLink";
	public final static String CELL_WIDGET_ENTRY_UNSEEN_IMAGE			= "entryUnseenImg";
	public final static String CELL_WIDGET_ENTRY_VIEW_ANCHOR			= "entryViewAnchor";
	public final static String CELL_WIDGET_ENTRY_VIEW_LABEL				= "entryViewLabel";
	public final static String CELL_WIDGET_INDEX						= "n-cellWidgetIndex";
	public final static String CELL_WIDGET_GUEST_AVATAR					= "guestAvatar";
	public final static String CELL_WIDGET_PRESENCE						= "presenceControl";
	public final static String CELL_WIDGET_PRESENCE_LABEL				= "presenceLabel";
	public final static String CELL_WIDGET_TASK_FOLDER					= "taskFolder";
}
