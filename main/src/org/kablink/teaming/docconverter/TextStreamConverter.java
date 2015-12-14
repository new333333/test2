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
package org.kablink.teaming.docconverter;

import org.kablink.teaming.util.SPropsUtil;

import org.springframework.beans.factory.InitializingBean;

/**
 * ?
 * 
 * @author drfoster@novell.com
 */
public abstract class TextStreamConverter extends StreamConverter<String> implements InitializingBean {
	protected String	m_excludedExtensions = "";
	protected String[]	m_additionalExclusions;

	/**
	 * Constructor method.
	 */
	public TextStreamConverter() {
		// Initialize the super class.
		super();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// Nothing to do.
	}

	/**
	 * Returns a String contain the excluded extensions for this
	 * converter.
	 * 
	 * @return
	 */
	public String getExcludedExtensions() {
		return m_excludedExtensions;
	}
	
	/**
	 * Sets the excluded extensions for this converter.
	 * 
	 * @param excludedExtensions
	 */
	public void setExcludedExtensions(String excludedExtensions) {
		// Are there any additional exclusions specified in the
		// ssf*.properties files?
		String[] additionalExclusions = getAdditionalExclusions();
		int c = ((null == additionalExclusions) ? 0 : additionalExclusions.length);
		if (0 < c) {
			// Yes!  Scan them...
			StringBuffer eeBuf = new StringBuffer((null == excludedExtensions) ? "" : excludedExtensions);
			for (int i = 0; i < c; i +=1) {
				// ...appending each to the excluded extensions.
				if ((0 < i) || (0 < eeBuf.length())) {
					eeBuf.append(",");
				}
				eeBuf.append(additionalExclusions[i]);
			}
			excludedExtensions = eeBuf.toString();
		}
		m_excludedExtensions = excludedExtensions;
	}
	
	/**
	 * By default, there are no additional exclusions.  Each class that
	 * extends this class this may define one, however.
	 * 
	 * @return
	 */
	public String getAdditionalExclusionsKey() {
		return null;
	}

	/*
	 * Returns a String[] of any additional file extensions that are to
	 * be excluded from OpenOffice text conversions specified in the
	 * ssf*.properties files.
	 */
	private String[] getAdditionalExclusions() {
		initAdditionalExclusions();
		return m_additionalExclusions;
	}

	/*
	 * Initializes the static array of extensions that are to be
	 * excluded by the OpenOffice text converter.
	 */
	private void initAdditionalExclusions() {
		// If we've already initialized the excluded extensions...
		if (null != m_additionalExclusions) {
			// ...bail.
			return;
		}
		
		// If there is no key defined to access any additional
		// extensions in the ssf*.properties files...
		String key = getAdditionalExclusionsKey();
		if ((null == key) || (0 == key.length())) {
			// ...define an empty array of them.
			m_additionalExclusions = new String[0];
			return;
		}

		// Are there any excluded extensions specified in the
		// ssf*.properties files?
		String excludeThese = SPropsUtil.getString(key, "");
		if ((null != excludeThese) && (0 < excludeThese.length())) {
			// Yes!  Extract and count them...
			m_additionalExclusions = excludeThese.toLowerCase().split(",");
			int count = ((null == m_additionalExclusions) ? 0 : m_additionalExclusions.length);
			
			// ...and ensure none of them start with a period.
			for (int i = 0; i < count; i += 1) {
				String excludeThis = m_additionalExclusions[i];
				if (excludeThis.startsWith(".")) {
					m_additionalExclusions[i] = excludeThis.substring(1);
				}
			}
		}
		else {
			// No, there aren't any excluded extensions specified!
			m_additionalExclusions = new String[0];
		}
	}
}
