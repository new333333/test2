package com.sitescape.ef.util;

import com.sitescape.util.KeyValuePair;

import java.io.InputStream;

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
		),
		
		new KeyValuePair(
		    "-//SiteScape//DTD Object Index Mapping 1.0.0//EN",
			"object-index-mapping_1_0_0.dtd"
		)
	};

	public InputSource resolveEntity(String publicId, String systemId) {
	    if(publicId != null) {
			for (int i = 0; i < IDS.length; i++) {
				if (publicId.equals(IDS[i].getKey())) {
					InputStream is =
						getClass().getClassLoader().getResourceAsStream(
							"com/sitescape/ef/resources/" + IDS[i].getValue());
	
					return new InputSource(is);
	
				}
			}
	    }
	    
	    if(systemId != null) {
	        String dtdOrXsdFile = systemId.substring(systemId.lastIndexOf("/") + 1);
			InputStream is =
				getClass().getClassLoader().getResourceAsStream(
					"com/sitescape/ef/resources/" + dtdOrXsdFile);

			return new InputSource(is);
	    }
	   
	    return null;
	}

}