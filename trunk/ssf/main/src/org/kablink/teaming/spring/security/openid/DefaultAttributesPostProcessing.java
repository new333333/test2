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
package org.kablink.teaming.spring.security.openid;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.util.StringUtil;
import org.kablink.util.Validator;
import org.springframework.security.openid.OpenIDAttribute;

/**
 * @author jong
 *
 */
public class DefaultAttributesPostProcessing implements AttributesPostProcessing {

	/* (non-Javadoc)
	 * @see org.kablink.teaming.spring.security.openid.VibeAttributesPostProcessing#postProcess(java.util.Map)
	 */
	@Override
	public void postProcess(List<OpenIDAttribute> openidAttributes, Map<String, String> vibeAttributes) {
		// If this default behavior needs to be altered, write a custom class and specify it in ssf-ext.properties file.
		Set<String> keys = new HashSet<String>();
		keys.addAll(vibeAttributes.keySet());
		for(String vibeAttrName:keys) {
			if(vibeAttrName.equals("title")) {
				// Vibe does not allow modifying title directly on user object.
				// So let's see if there's something useful we can do with it before discarding it.
				String firstName = vibeAttributes.get("firstName");
				String lastName = vibeAttributes.get("lastName");
				if(Validator.isNull(firstName) || Validator.isNull(lastName)) {
					// Either first name or last name is missing. Let's see if we can use the title for this.
					String[] parts = StringUtil.split(vibeAttributes.get("title"), " ");
					if(parts.length > 0) { // Title contains something
						boolean usedForLastName = false;
						if(Validator.isNull(lastName)) {
							lastName = parts[parts.length-1].trim();
							vibeAttributes.put("lastName", lastName);
							usedForLastName = true;
						}
						if(Validator.isNull(firstName)) {
							if((usedForLastName && parts.length > 1) || !usedForLastName) {
								firstName = parts[0].trim();
								vibeAttributes.put("firstName", firstName);
							}
						}
					}
				}
				vibeAttributes.remove("title");
			}
		}
	}

}
