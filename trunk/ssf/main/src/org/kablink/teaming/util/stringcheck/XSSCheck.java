/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package org.kablink.teaming.util.stringcheck;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.StringPool;


public class XSSCheck implements StringCheck {

	private static final String MODE_STRIP = "strip"; // default mode
	private static final String MODE_DISALLOW = "disallow";
	
	private static final String PATTERN_STR = "(?i)<[\\s]*/?script.*?>|<[\\s]*/?embed.*?>|<[\\s]*/?object.*?>|<[\\s]*a[\\s]*href[^>]*javascript[\\s]*:[^(^)^>]*[(][^)]*[)][^>]*>[^<]*(<[\\s]*/[\\s]*a[^>]*>)*";
	
	private Pattern pattern;
	private boolean enable;
	private String mode;
	
	public XSSCheck() {
		pattern = Pattern.compile(PATTERN_STR);
		enable = SPropsUtil.getBoolean("xss.check.enable");
		mode = SPropsUtil.getString("xss.check.mode");

		// We do this not only to validate the input mode but also to enable
		// simple reference comparison for modes. 
		if(mode.equals(MODE_DISALLOW))
			mode = MODE_DISALLOW;
		else
			mode = MODE_STRIP;
	}
	
	/**
	 * Detect potential XSS threat. The exact behavior of this method
	 * is determined by the configuration settings in ssf.properties. 
	 * 
	 * @param input input data as string
	 * @return output data as string
	 * @throws XSSCheckException
	 */
	public String check(String input) throws XSSCheckException {
		if(enable)
			return doCheck(input);
		else
			return input;
	}
	
	// A subclass can override this method to provide custom or enhanced implementation.
	protected String doCheck(String input) throws XSSCheckException {
		if(input == null || input.equals(""))
			return input;
		
		CharSequence sequence = input.subSequence(0, input.length());

		Matcher matcher = pattern.matcher(sequence);

		// We can use much faster reference comparison rather than string 
		// value equality test due to the way we setup above.
		if(mode == MODE_DISALLOW) {
			if(matcher.find())
				throw new XSSCheckException();
			else
				return input;
		}
		else { // MODE == MODE_STRIP
			return matcher.replaceAll(StringPool.BLANK);
		}
	}
}
