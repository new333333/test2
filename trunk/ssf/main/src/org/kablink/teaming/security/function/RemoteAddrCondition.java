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
package org.kablink.teaming.security.function;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.util.StringUtil;

public class RemoteAddrCondition extends Condition {

	private static Log logger = LogFactory.getLog(RemoteAddrCondition.class);
		
	private static final String DELIMITER = ",";
	private static ConcurrentHashMap<String, Object> patternCache = new ConcurrentHashMap<String, Object>();
	
	private static final Object IGNORE = new Object();
	
	private String[] includeAddressExpressions;
	private String[] excludeAddressExpressions;
	private boolean dirty = false;
	
	// For use by Hibernate only.
	protected RemoteAddrCondition() {
	}

	@Override
	protected String getEncodedSpec() {
		if(dirty) {
			toEncodedSpec();
			dirty = false;
		}
		return super.getEncodedSpec();
	}

	@Override
	protected void setEncodedSpec(String encodedSpec) {
		super.setEncodedSpec(encodedSpec);
		toExpressions();
	}

	// Applications use this constructor.
	public RemoteAddrCondition(String title, String[] includeAddressExpressions, String[] excludeAddressExpressions) {
		setTitle(title);
		setIncludeAddressExpressions(includeAddressExpressions);
		setExcludeAddressExpressions(excludeAddressExpressions);
	}
	
	public String[] getIncludeAddressExpressions() {
		if(includeAddressExpressions == null)
			includeAddressExpressions = new String[0];
		return includeAddressExpressions; // This is always up-to-date
	}

	public void setIncludeAddressExpressions(String[] includeAddressExpressions) {
		if(includeAddressExpressions == null)
			includeAddressExpressions = new String[0];
		this.includeAddressExpressions = includeAddressExpressions;
		this.dirty = true;
	}

	public String[] getExcludeAddressExpressions() {
		if(excludeAddressExpressions == null)
			excludeAddressExpressions = new String[0];
		return excludeAddressExpressions; // This is always up-to-date
	}

	public void setExcludeAddressExpressions(String[] excludeAddressExpressions) {
		if(excludeAddressExpressions == null)
			excludeAddressExpressions = new String[0];
		this.excludeAddressExpressions = excludeAddressExpressions;
		this.dirty = true;
	}
	
	/* This method expects a single argument of String type, where the value 
	 * represents a remote IP address of a client.
	 * @see org.kablink.teaming.security.function.Condition#evaluate(java.lang.Object[])
	 */
	@Override
	public boolean evaluate() {
		String remoteAddr = ZoneContextHolder.getClientAddr();
		String[] includes = getIncludeAddressExpressions();
		String[] excludes = getExcludeAddressExpressions();
		boolean included = false;
		// Check inclusion rules
		for(String include : includes) {
			if(getPattern(include) == null)
				continue; // Treat this situation as if the particular rule didn't exist in the first place.
			if(getPattern(include).matcher(remoteAddr).matches()) {
				// A inclusion rule matched. Skip the rest of the inclusion rules, and
				// go check the exclusion rules.  
				included = true;
				break;
			}
		}
		if(included) {
			// Check exclusion rules
			for(String exclude : excludes) {
				if(getPattern(exclude) == null)
					continue; // Treat this situation as if the particular rule didn't exist in the first place.
				if(getPattern(exclude).matcher(remoteAddr).matches()) {
					// A exclusion rule matched. Reject it.
					return false;
				}
			}
			// No exclusion rule matched (or no exclusion rule existed). 
			return true;
		}
		else {
			// None of the inclusion rule matched (or no inclusion rule existed).
			return false;
		}
	}

	private Pattern getPattern(String exp) {
		// Cache pattern objects since pattern compilation is expensive.
		Object pattern = patternCache.get(exp);
		if(pattern == null) {
			try {
				pattern = Pattern.compile(exp);
			}
			catch (PatternSyntaxException e) {
				logger.error("'" + exp + "' is not a valid regular expression. It will be ignored: " + e.toString());
				pattern = IGNORE;
			}
			patternCache.put(exp, pattern);
		}
		if(pattern == IGNORE)
			return null;
		else
			return (Pattern) pattern;
	}

	private void toEncodedSpec() {
		String[] includes = getIncludeAddressExpressions();
		String[] excludes = getExcludeAddressExpressions();
		StringBuilder sb = new StringBuilder();
		sb.append("includes=")
		.append(StringUtil.merge(includes, DELIMITER))
		.append(" excludes=")
		.append(StringUtil.merge(excludes, DELIMITER));
		super.setEncodedSpec(sb.toString());
	}
	
	private void toExpressions() {
		String str = super.getEncodedSpec();
		int excludesIndex = str.indexOf("excludes=");
		String includesStr = str.substring(9, excludesIndex-1);
		String excludesStr = str.substring(excludesIndex+9);
		this.includeAddressExpressions = StringUtil.split(includesStr, DELIMITER);
		this.excludeAddressExpressions = StringUtil.split(excludesStr, DELIMITER);
	}
}
