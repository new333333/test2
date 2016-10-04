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
package org.kablink.teaming.util;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.module.definition.notify.NotifyBuilderUtil;

/**
 * Helper methods for the non-GWT UI server code in dealing with email
 * templates.
 *
 * @author drfoster@novell.com
 */
public class EmailTemplatesHelper {
	protected static Log m_logger = LogFactory.getLog(LandingPageHelper.class);
	
	// The file extension used for Velocity templates.
	public static final String TEMPLATE_EXTENSION	= ".vm";
	
	// Relative path to where the default email template files can be
	// found.
	private static final String EMAIL_TEMPLATES_DEFAULT			= "velocity";
	private static final String EMAIL_TEMPLATES_DEFAULT_PATH	= (File.separator + "WEB-INF" + File.separator + EMAIL_TEMPLATES_DEFAULT);
	
	// Relative path to where the customized email template files can
	// be found.
	private static final String EMAIL_TEMPLATES_CUSTOMIZED		= "velocity-ext";
	private static final String EMAIL_TEMPLATES_CUSTOMIZED_PATH	= EMAIL_TEMPLATES_CUSTOMIZED;

	/**
	 * Returns a File reference to the default email templates
	 * directory.
	 * 
	 * @return
	 */
	public static File getEmailTemplatesDefault() {
		File reply = new File(getEmailTemplatesDefaultPath());
		return reply;
	}
	
	/**
	 * Returns the path to where the default email templates are
	 * stored.
	 * 
	 * @param addTrailingSeparator
	 * 
	 * @return
	 */
	public static String getEmailTemplatesDefaultPath(boolean addTrailingSeparator) {
		String reply = SpringContextUtil.getServletContext().getRealPath(EMAIL_TEMPLATES_DEFAULT_PATH);
		if (addTrailingSeparator) {
			reply += File.separator;
		}
		return reply;
	}
	
	public static String getEmailTemplatesDefaultPath() {
		// Always use the initial form of the method.
		return getEmailTemplatesDefaultPath(false);	// false -> Don't include a trailing path separator.
	}

	/**
	 * Returns a File reference to the customized email templates
	 * directory.  Will optionally create it if it doesn't exist.
	 * 
	 * @param createIfNotThere
	 * 
	 * @return
	 */
	public static File getEmailTemplatesCustomized(boolean createIfNotThere) {
		File reply = new File(getEmailTemplatesCustomizedPath());
		if (createIfNotThere && (!(reply.exists()))) {
			reply.mkdirs();
		}
		return reply;
	}
	
	public static File getEmailTemplatesCustomized() {
		return getEmailTemplatesCustomized(true);
	}
	
	/**
	 * Returns the path to where the customized email templates are
	 * stored.
	 * 
	 * @param addTrailingSeparator
	 * 
	 * @return
	 */
	public static String getEmailTemplatesCustomizedPath(boolean addTrailingSeparator) {
		String reply = (SPropsUtil.getDirPath("data.root.dir") + EMAIL_TEMPLATES_CUSTOMIZED_PATH + File.separator + RequestContextHolder.getRequestContext().getZoneName());
		if (addTrailingSeparator) {
			reply += File.separator;
		}
		return reply;
	}
	
	public static String getEmailTemplatesCustomizedPath() {
		// Always use the initial form of the method.
		return getEmailTemplatesCustomizedPath(false);	// false -> Don't include a trailing path separator.
	}

	/**
	 * Forces the VelocityEngine in use for the current zone to be
	 * reset (i.e., have its cache emptied, ...)
	 */
	public static void resetVelocityEngine() {
		NotifyBuilderUtil.resetCachedZoneVelocityEngine(
			getEmailTemplatesCustomizedPath(
				false));
	}
}
