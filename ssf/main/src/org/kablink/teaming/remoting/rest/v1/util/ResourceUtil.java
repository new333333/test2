/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.remoting.rest.v1.util;

import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.rest.v1.model.FileVersionProperties;
import org.kablink.teaming.rest.v1.model.HistoryStamp;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.WebUrlUtil;

/**
 * This class contains utility methods that are shared among multiple resource types.
 * Do not place in this class any methods that are used by a single resource type.
 * 
 * @author jong
 *
 */
public class ResourceUtil {

	public static FileVersionProperties fileVersionFromFileAttachment(VersionAttachment va) {
		return new FileVersionProperties(
				va.getId(),
				new HistoryStamp(Utils.redactUserPrincipalIfNecessary(va.getCreation().getPrincipal()).getId(), va.getCreation().getDate()),
				new HistoryStamp(Utils.redactUserPrincipalIfNecessary(va.getModification().getPrincipal()).getId(), va.getModification().getDate()),
				Long.valueOf(va.getFileItem().getLength()),
				Integer.valueOf(va.getVersionNumber()),
				Integer.valueOf(va.getMajorVersion()),
				Integer.valueOf(va.getMinorVersion()),
				va.getFileItem().getDescription().getText(), 
				va.getFileStatus(),
				WebUrlUtil.getFileUrl((String)null, WebKeys.ACTION_READ_FILE, va)
				);
	}
}
