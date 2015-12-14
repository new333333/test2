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
package org.kablink.teaming.gwt.server.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.gwt.client.util.CloudFolderType;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for the GWT UI server code that services requests
 * dealing with common services based folders.
 *
 * @author drfoster@novell.com
 */
public class GwtCloudFolderHelper {
	protected static Log m_logger = LogFactory.getLog(GwtCloudFolderHelper.class);

	// UNC paths used for the various cloud folder services supported.
	private final static String BOXDOTNET_UNC	= "\\\\boxdotnet";
	private final static String DROPBOX_UNC		= "\\\\dropbox";
	private final static String GOOGLEDRIVE_UNC	= "\\\\googledrive";
	private final static String SKYDRIVE_UNC	= "\\\\skydrive";
	
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtCloudFolderHelper() {
		// Nothing to do.
	}

	/**
	 * Maps a CloudFolderType to its appropriate base UNC path.
	 * 
	 * @param cft
	 * 
	 * @return
	 */
	public static String getBaseUNCPathForService(CloudFolderType cft) {
		String reply;
		switch (cft) {
		case BOXDOTNET:    reply = BOXDOTNET_UNC;   break;
		case DROPBOX:      reply = DROPBOX_UNC;     break;
		case GOOGLEDRIVE:  reply = GOOGLEDRIVE_UNC; break;
		case SKYDRIVE:     reply = SKYDRIVE_UNC;    break;
		default:           reply = null;            break;
		}
		return reply;
	}

	/**
	 * Returns the CloudFolderType implied by a Cloud Folder's root name.
	 * 
	 * @param cfRoot
	 * 
	 * @return
	 */
	public static CloudFolderType getCloudFolderTypeFromRoot(String cfRoot) {
		if (MiscUtil.hasString(cfRoot)) {
			for (CloudFolderType cft:  CloudFolderType.values()) {
				if (0 == cfRoot.indexOf(cft.name() + ".")) {
					return cft;
				}
			}
			return CloudFolderType.OTHER;
		}
		return CloudFolderType.NOT_A_CLOUD_FOLDER;
	}
}
