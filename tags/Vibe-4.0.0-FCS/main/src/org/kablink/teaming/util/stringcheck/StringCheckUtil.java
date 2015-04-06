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
package org.kablink.teaming.util.stringcheck;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.survey.Survey;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.springframework.beans.factory.InitializingBean;


public class StringCheckUtil implements InitializingBean {

	private static StringCheckUtil instance; // A singleton instance
	
	private static StringCheck[] checkers;
	
	public StringCheckUtil() {
		if(instance != null)
			throw new SingletonViolationException(StringCheckUtil.class);
		
		instance = this;
	}
	
	public void afterPropertiesSet() throws Exception {
		String[] classNames = SPropsUtil.getStringArray("string.check.checker.classes", ",");
	
		checkers = new StringCheck[classNames.length];
		
		for(int i = 0; i < classNames.length; i++) {
			checkers[i] = (StringCheck) ReflectHelper.getInstance(classNames[i]);
		}
	}

    private static StringCheckUtil getInstance() {
    	return instance;
    }

	public static String check(String input) throws StringCheckException {
		return getInstance().checkAll(input, false);
	}
	
	//Only if checkOnly=true and the check fails will StringCheckException be thrown.
	public static String check(String input, boolean checkOnly) throws StringCheckException {
		return getInstance().checkAll(input, checkOnly);
	}
	
	public static String checkForQuotes(String input, boolean checkOnly) throws StringCheckException {
		return getInstance().replaceQuotes(input, checkOnly);
	}
	
	public static LinkedHashSet<String> check(LinkedHashSet<String> stringSet) throws StringCheckException {
		if (stringSet == null ) return stringSet;
		LinkedHashSet<String> result = new LinkedHashSet<String>();
		for (String s : stringSet) {
			result.add(getInstance().checkAll(s, false));
		}
		return result;
	}

	public static Set<String> check(Set<String> stringSet) throws StringCheckException {
		if (stringSet == null ) return stringSet;
		Set<String> result = new HashSet<String>();
		for (String s : stringSet) {
			result.add(getInstance().checkAll(s, false));
		}
		return result;
	}

	public static List<String> check(List<String> stringList) throws StringCheckException {
		if (stringList == null ) return stringList;
		List<String> result = new ArrayList<String>();
		for (String s : stringList) {
			result.add(getInstance().checkAll(s, false));
		}
		return result;
	}

	public static Map<String,?> check(Map<String,?> input) throws StringCheckException {
		return getInstance().checkAll(input, false);
	}

	//Only if checkOnly=true and the check fails will StringCheckException be thrown.
	public static Map<String,?> check(Map<String,?> input, boolean checkOnly) throws StringCheckException {
		return getInstance().checkAll(input, checkOnly);
	}

	public static String[] check(String[] input) throws StringCheckException {
		if (input == null) return input;
		String[] result = new String[input.length];
		for(int i = 0; i < input.length; i++)
			result[i] = getInstance().checkAll(input[i], false);
		return result;
	}
	
	public static Description check(Description desc) throws StringCheckException {
		if (desc != null) {
			desc.setText(getInstance().checkAll(desc.getText(), false));
		}
		return desc;
	}
	
	public static Survey check(Survey survey) throws StringCheckException {
		if (survey != null) {
			if (!survey.toString().equals(getInstance().checkAll(survey.toString(), false))) {
				throw new StringCheckException();
			}
		}
		return survey;
	}
	
	public static String checkUrl(String url) throws StringCheckException {
		String urlTag = "<a href=\"" + url + "\">xss</a>";
		if (!urlTag.equals(getInstance().checkAll(urlTag, false))) {
			throw new StringCheckException("errorcode.xss.invalidUrl", new String[]{url});
		}
		return url;
	}
	
	//Only if checkOnly=true and the check fails will StringCheckException be thrown.
	private String checkAll(String input, boolean checkOnly) throws StringCheckException {
		boolean changed = false;
		for(int i = 0; i < checkers.length; i++) {
			String newInput = checkers[i].check(input, checkOnly);
			if (input != null && !input.equals(newInput)) {
				changed = true;
				input = newInput;
			}
		}
		if (checkOnly && changed) {
			//If just checking, signal that a change occurred by throwing an exception
			throw new StringCheckException();
		}
		return input;
	}
	
	//Only if checkOnly=true and the check fails will StringCheckException be thrown.
	private String replaceQuotes(String input, boolean checkOnly) throws StringCheckException {
		boolean changed = false;
		for(int i = 0; i < checkers.length; i++) {
			String newInput = checkers[i].checkForQuotes(input, checkOnly);
			if (input != null && !input.equals(newInput)) {
				changed = true;
				input = newInput;
			}
		}
		if (checkOnly && changed) {
			//If just checking, signal that a change occurred by throwing an exception
			throw new StringCheckException();
		}
		return input;
	}
	
	//Only if checkOnly=true and the check fails will StringCheckException be thrown.
	private Map<String,?> checkAll(Map<String,?> input, boolean checkOnly) throws StringCheckException {
		for(int i = 0; i < checkers.length; i++) {
			input = checkAll(checkers[i], input, checkOnly);
		}
		return input;
	}

	@SuppressWarnings("unchecked")
	//Only if checkOnly=true and the check fails will StringCheckException be thrown.
	private Map<String,?> checkAll(StringCheck checker, Map<String,?> input, boolean checkOnly) throws StringCheckException {
		Map output = new TreeMap();
		boolean changed = false;
		
		Object value, newValue;
		
		for(String key : input.keySet()) {
			value = input.get(key);
			if(value == null) {
				newValue = null;
			}
			else if(value instanceof String) {
				newValue = checker.check((String)value, checkOnly);
			}
			else if(value instanceof String[]) {
				String[] valueSA = (String[]) value;
				String[] newValueSA = new String[valueSA.length];
				for(int i = 0; i < valueSA.length; i++)
					newValueSA[i] = checker.check(valueSA[i], checkOnly);
				newValue = newValueSA;
			}
			else {
				newValue = value;
			}
			if (value != null && !value.equals(newValue)) {
				changed = true;
			}
			output.put(key, newValue);
		}	
		if (checkOnly && changed) {
			//If just checking, signal that a change occurred by throwing an exception
			throw new StringCheckException();
		}
		return output;
	}
}
