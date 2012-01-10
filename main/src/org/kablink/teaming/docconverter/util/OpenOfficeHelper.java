/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.docconverter.util;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.springframework.beans.factory.InitializingBean;

/**
 * Contains a set of utility methods to aid the OpenOffice converters.
 *  
 * @author drfoster@novell.com
 */
public class OpenOfficeHelper implements InitializingBean {
	private static OpenOfficeHelper m_instance;	// The singleton OpenOfficeHelper.
	
	private List<Converter>	m_indexConverters;	// Initialized with the OpenOffice index Converter's to use when the class singleton is instantiated.
	private List<Converter>	m_viewConverters;	// Initialized with the OpenOffice view  Converter's to use when the class singleton is instantiated.
	
	/**
	 * Specifies the type of OpenOffice conversion to be done.
	 */
	public enum ConvertType {
		INDEX,
		VIEW,
	}
	
	/*
	 * Inner class used to specify an instance of any OpenOffice
	 * conversion handler.
	 */
	private static class Converter {
		private String m_type;			// The OpenOffice conversion type for m_extensions[].
		private String[] m_extensions;	// The extension to use for the OpenOffice conversion type.
		private int m_extensionCount; 	// The count of String's in m_extensions.

		/*
		 * Class constructor.
		 */
		private Converter(String type) {
			// Always use the final form of the constructor.
			this(type, new String[0]);
		}

		/*
		 * Class constructor.
		 */
		private Converter(String type, String extensions) {
			// Always use the final form of the constructor.
			this(type, ((null == extensions) ? new String[0] : extensions.trim().split(",")));
		}

		/*
		 * Class constructor.
		 */
		private Converter(String type, String[] extensions) {
			// Store the parameters...
			m_type = type.trim();
			m_extensions = extensions;

			// ...count the extensions...
			m_extensionCount = ((null == m_extensions) ? 0 : m_extensions.length);
			
			// ...and make sure they've been normalized.
			for (int i = 0; i < m_extensionCount; i += 1) {
				m_extensions[i] = normalizeExtension(m_extensions[i]);
			}
		}

		/*
		 * Builds a List<Converter> for the OpenOffice converters
		 * specified by keyBase.
		 */
		private static List<Converter> buildConverterList(String keyBase) {
			// Scan the converters defined in the ssf*.properties files.
			ArrayList<Converter> reply = new ArrayList<Converter>();
			int count = SPropsUtil.getInt(keyBase + ".count", 0);
			for (int i = 0; i < count; i += 1) {
				// Does this converter contain a type?
				String eachBase = (keyBase + "." + i + ".");
				String type = SPropsUtil.getString(eachBase + "type", "");
				if (!(MiscUtil.hasString(type))) {
					// No!  Skip it.
					continue;
				}
				
				// Does this converter contain an extension list?
				String extensions = SPropsUtil.getString(eachBase + "extensions", "");
				if (!(MiscUtil.hasString(extensions))) {
					// No!  Skip it.
					continue;
				}

				// Construct a Converter and add it to the ArrayList.
				reply.add(new Converter(type, extensions));
			}

			// Is there a default conversion type specified?
			String type = SPropsUtil.getString(keyBase + ".other.type", "");
			if (MiscUtil.hasString(type)) {
				// Yes!  Construct a Converter for it and add it to the
				// ArrayList.
				reply.add(new Converter(type));
			}

			// If we get here, reply refers to the List<Converter> for
			// the given keyBase.  Return it.
			return reply; 
		}

		/*
		 * Returns the type of OpenOffice conversion for this Converter
		 * instance.
		 */
		private String getType() {
			return m_type;
		}

		/*
		 * Called to normalize the extension into a consistent format.
		 */
		private static String normalizeExtension(String extension) {
			if (null == extension) {
				extension = "";
			}
			else {
				extension = extension.trim();
				if (0 < extension.length()) {
					extension = extension.toLowerCase();
					if (!(extension.startsWith("."))) {
						extension = ("." + extension);
					}
				}
			}
			return extension;
		}
		
		/*
		 * Returns true if this Converter should be used for the given
		 * extension and false otherwise.
		 */
		private boolean useForExtension(String extension) {
			// If there aren't any extensions defined for this
			// Converter...
			if (0 == m_extensionCount) {
				// ...it's the catch all for all other extensions and
				// ...will be the last one in the list.  Return true.
				return true;
			}

			// If we don't have an extension to check...
			extension = normalizeExtension(extension);
			if (!(MiscUtil.hasString(extension))) {
				// ...don't use this Converter.
				return false;
			}
			
			// Scan the extensions in this Converter.
			for (int i = 0; i < m_extensionCount; i += 1) {
				// Is this extension for this Converter?
				if (extension.equals(m_extensions[i])) {
					// Yes!  Return true.
					return true;
				}
			}
			
			// If we get here, this Converter is not for use with this
			// extension.  Return false.
			return false;
		}
	}
	
	/**
	 * Class constructor.
	 */
	public OpenOfficeHelper() {
		// If we already have a singleton of this class...
		if(null != m_instance) {
			// ...something is wrong since we should only ever have
			// ...one.
			throw new SingletonViolationException(OpenOfficeHelper.class);
		}

		// this is our singleton instance.  Save it.
		m_instance = this;
	}

	/**
	 * Called after Spring loads the class for it to complete its
	 * initializations.
	 */
	public void afterPropertiesSet() throws Exception {
		loadIndexConverters();
		loadViewConverters();
	}
	
	/**
	 * Based on the ConvertType, returns the appropriate conversion
	 * type for given extension.
	 * 
	 * @param ct
	 * @param extension
	 * 
	 * @return
	 */
	public static String getConvertType(ConvertType ct, String extension) {
		return getInstance().getConvertTypeImpl(ct, extension);
	}
	
	/*
	 * Based on the ConvertType, returns the appropriate conversion
	 * type for given extension.
	 */
	private String getConvertTypeImpl(ConvertType ct, String extension) {
		// Get the List<Converter> to use for ConvertType. 
		List<Converter> convertersList = null;
		if      (ConvertType.INDEX == ct) convertersList = m_indexConverters;
		else if (ConvertType.VIEW == ct)  convertersList = m_viewConverters;
		int converters = ((null == convertersList) ? 0 : convertersList.size());

		// Do we have any Converter's?
		String reply = "";
		if (0 < converters) {
			// Yes! Scan them.
			for(int i = 0; i < converters; i += 1) {
				// Is this the Converter for the given extension?
				OpenOfficeHelper.Converter converter = convertersList.get(i);
				if (converter.useForExtension(extension)) {
					// Yes!  Return its type.
					reply = converter.getType();
					break;
				}
			}
		}
		
		// If we get here, reply refers to the convert type to use for
		// the given extension.  Return it.
		return reply;
	}

	/**
	 * Returns the extension portion of a file path.
	 * 
	 * @param filePath
	 * 
	 * @return
	 */
	public static String getExtension(String filePath) {
		int ifpExtPos = ((null == filePath) ? (-1) : filePath.lastIndexOf('.'));
		return ((0 < ifpExtPos) ? filePath.substring(ifpExtPos) : filePath);
	}
	
	/*
	 * Returns the singleton instance of this class.
	 */
    private static OpenOfficeHelper getInstance() {
    	return m_instance;
    }
    
	/*
	 * Called to load the index OpenOffice Converter's.
	 */
	private void loadIndexConverters() {
		// Can we load the index Converter's now?
		m_indexConverters = Converter.buildConverterList("openoffice.convert.index");
		if (0 == m_indexConverters.size()) {
			// No!  Assume defaults.
			m_indexConverters.add(new Converter("XHTML Impress File", ".odp,.sxi,.ppt"));
			m_indexConverters.add(new Converter("XHTML Calc File",    ".ods,.xls"));
			m_indexConverters.add(new Converter("XHTML Draw File",    ".odg"));
			m_indexConverters.add(new Converter("XHTML Writer File"));
		}
	}

	/*
	 * Called to load the view OpenOffice Converter's.
	 */
	private void loadViewConverters() {
		// Can we load the view Converter's now?
		m_viewConverters = Converter.buildConverterList("openoffice.convert.view");
		if (0 == m_viewConverters.size()) {
			// No!  Assume defaults.
			m_viewConverters.add(new Converter("impress_html_Export", ".odp,.sxi,.ppt"));
			m_viewConverters.add(new Converter("HTML (StarCalc)",     ".ods,.xls"));
			m_viewConverters.add(new Converter("draw_html_Export",    ".odg"));
			m_viewConverters.add(new Converter("HTML (StarWriter)"));
		}
	}
}
