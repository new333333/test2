/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.util;

import java.io.File;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.web.util.MiscUtil;

public class DirPath {
	
	public static String getXsltDirPath() {
		return getWebappDirPathHidden("xslt");
	}

	public static String getVelocityDirPath() {
		return getWebappDirPathHidden("velocity");
	}
	public static String getRssDirPath() {
		return getWebappDirPathHidden("rss");
	}
	
	public static String getThumbnailDirPath() {
		return getImagesDirPath() + File.separator + "thumbnails";
	}
	
	public static String getWebinfClassesDirPath() {
		return getWebinfDirPath() + File.separator + "classes";
	}
	public static String getWebinfTmpDirPath() {
		return getWebinfDirPath() + File.separator + "tmp";
	}
	public static String getDTDDirPath() {
		return getWebinfClassesDirPath() + File.separator + "dtd";
	}
	public static String getExtensionWebPath() {
		return getWebappDirPathVisible("ext");
	}
	public static String getExtensionBasePath() {
		return getWebappDirPathHidden("ext");
	}
	public static String getCustomJspsBasePath() {
		return getWebappDirPathHidden("jsp/custom_jsps");
	}
	public static String getWebappRootDirPath() {
		return SpringContextUtil.getWebappRootDirPath();
	}
	
	public static String getWebinfDirPath() {
    	return getWebappRootDirPath() + File.separator + "WEB-INF";
    }
    
	public static String getGroovyScriptPath() {
		return getWebinfDirPath() + File.separator + "groovyscript";
	}
	
    private static String getWebappDirPathVisible(String subdirName) {
    	return getWebappRootDirPath() + File.separator + subdirName;
    }
    
    private static String getWebappDirPathHidden(String subdirName) {
    	return getWebinfDirPath() + File.separator + subdirName;
    }
    
	private static String getImagesDirPath() {
		String staticPath = ObjectKeys.STATIC_DIR + File.separator + SPropsUtil.getString(ObjectKeys.STATIC_DIR_PROPERTY, "xxx") + File.separator;
		return getWebappDirPathVisible(staticPath + "images");
	}
	
}
