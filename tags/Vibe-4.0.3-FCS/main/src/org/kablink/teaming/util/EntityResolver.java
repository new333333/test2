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


import java.io.InputStream;

import org.kablink.util.KeyValuePair;
import org.xml.sax.InputSource;

public class EntityResolver implements org.xml.sax.EntityResolver {

	public static KeyValuePair[] IDS = {
		new KeyValuePair(
			"-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN",
			"ejb-jar_2_0.dtd"
		),

		new KeyValuePair(
			"-//ObjectWeb//DTD JOnAS 3.2//EN",
			"jonas-ejb-jar_3_2.dtd"
		),

		new KeyValuePair(
			"-//Macromedia, Inc.//DTD jrun-ejb-jar 4.0//EN",
			"jrun-ejb-jar.dtd"
		),

		new KeyValuePair(
			"-//Liferay//DTD DISPLAY 2.0.0//EN",
			"liferay-display_2_0_0.dtd"
		),

		new KeyValuePair(
			"-//Liferay//DTD PORTLET 2.0.0//EN",
			"liferay-portlet_2_0_0.dtd"
		),

		new KeyValuePair(
			"-//Liferay//DTD PORTLET 2.2.0//EN",
			"liferay-portlet_2_2_0.dtd"
		),

		new KeyValuePair(
			"-//Liferay//DTD SKIN 2.0.0//EN",
			"liferay-skin_2_0_0.dtd"
		),

		new KeyValuePair(
			"-//Pramati Technologies //DTD Pramati J2ee Server 3.0//EN",
			"pramati-j2ee-server_3_0.dtd"
		),

		new KeyValuePair(
			"-//Sun Microsystems, Inc.//DTD " +
				"Sun ONE Application Server 7.0 EJB 2.0//EN",
			"sun-ejb-jar_2_0-0.dtd"
		),

		new KeyValuePair(
			"-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN",
			"web-app_2_3_dtd"
		),

		new KeyValuePair(
			"-//BEA Systems, Inc.//DTD WebLogic 7.0.0 EJB//EN",
			"weblogic-ejb-jar.dtd"
		)
	};

	public InputSource resolveEntity(String publicId, String systemId) {
	    if(publicId != null) {
			for (int i = 0; i < IDS.length; i++) {
				if (publicId.equals(IDS[i].getKey())) {
					InputStream is =
						getClass().getClassLoader().getResourceAsStream(
							"dtd/" + IDS[i].getValue());
	
					return new InputSource(is);
	
				}
			}
	    }
	    
	    if(systemId != null) {
	        String dtdOrXsdFile = systemId.substring(systemId.lastIndexOf("/") + 1);
			InputStream is =
				getClass().getClassLoader().getResourceAsStream(
					"dtd/" + dtdOrXsdFile);

			// If failed to load the resource above, try loading it using
			// the context class loader of the thread. 
			if(is == null) {
				is = Thread.currentThread().getContextClassLoader().getResourceAsStream("dtd/" + dtdOrXsdFile);
			}
			
			return new InputSource(is);
	    }
	   
	    return null;
	}

}