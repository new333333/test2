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

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Enumeration used to communicate the action to take when a file link
 * is activated.
 * 
 * Note:
 *    FileLinkAction.java:     The ssf/main version of this enumeration.
 *    GwtFileLinkAction.java:  The GWT UI version of this enumeration.
 * See the warnings below.
 * 
 * @author drfoster@novell.com
 */
public enum GwtFileLinkAction implements IsSerializable {
	// *** WARNING *** WARNING *** WARNING *** WARNING ***
	// ***
	// *** 1. An ordinal value from this is stored in the user's
	// ***    preferences.  The values should not be changed.
	// *** 2. This the GWT UI version of this enumeration.  There
	// ***    is an equivalent version in the ssf/main side of the
	// ***    fence.  The two version of this enumeration MUST BE KEPT
	// ***    IN SYNC.
	// ***
	// *** WARNING *** WARNING *** WARNING *** WARNING ***
	
	DOWNLOAD,					// When activated, the link will download (i.e., open) the file.
	VIEW_DETAILS,				// When activated, the link will view the details of the file.
	VIEW_HTML_ELSE_DETAILS,		// When activated, the link will view a file's HTML if the format is supported, otherwise it will view the details of the file. 
	VIEW_HTML_ELSE_DOWNLOAD;	// When activated, the link will view a file's HTML if the format is supported, otherwise it will download (i.e., open) the file.
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean isDownload()             {return GwtFileLinkAction.DOWNLOAD.equals(               this);}
	public boolean isViewDetails()          {return GwtFileLinkAction.VIEW_DETAILS.equals(           this);}
	public boolean isViewHtmlElseDetails()  {return GwtFileLinkAction.VIEW_HTML_ELSE_DETAILS.equals( this);}
	public boolean isViewHtmlElseDownload() {return GwtFileLinkAction.VIEW_HTML_ELSE_DOWNLOAD.equals(this);}
	
	/**
	 * Converts the ordinal value of a GwtFileLinkAction to its
	 * enumeration equivalent.
	 * 
	 * @param ordinal
	 * 
	 * @return
	 */
	public static GwtFileLinkAction getEnum(int ordinal) {
		GwtFileLinkAction reply;
		try                                      {reply = GwtFileLinkAction.values()[ordinal];}
		catch (ArrayIndexOutOfBoundsException e) {reply = GwtFileLinkAction.DOWNLOAD;         }
		return reply;
	}
}
