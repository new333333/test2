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

import java.util.Map;
import java.util.TreeMap;

import org.kablink.teaming.SingletonViolationException;
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
		
		Class checkerClass;
		for(int i = 0; i < classNames.length; i++) {
			checkerClass = ReflectHelper.classForName(classNames[i]);
			checkers[i] = (StringCheck) checkerClass.newInstance();
		}
	}

    private static StringCheckUtil getInstance() {
    	return instance;
    }

	public static String check(String input) throws StringCheckException {
		return getInstance().checkAll(input);
	}
	
	public static Map<String,String[]> check(Map<String,String[]> input) throws StringCheckException {
		return getInstance().checkAll(input);
	}

	private String checkAll(String input) throws StringCheckException {
		for(int i = 0; i < checkers.length; i++) {
			input = checkers[i].check(input);
		}
		return input;
	}
	
	private Map<String,String[]> checkAll(Map<String,String[]> input) throws StringCheckException {
		for(int i = 0; i < checkers.length; i++) {
			input = checkAll(checkers[i], input);
		}
		return input;
	}

	private Map<String,String[]> checkAll(StringCheck checker, Map<String,String[]> input) throws StringCheckException {
		Map output = new TreeMap<String, String[]>();
		
		String[] value=null,newValue=null;
		for(String key : input.keySet()) {
			value = input.get(key);
			if(value != null) {
				newValue = new String[value.length];
				for(int i = 0; i < value.length; i++) {
					newValue[i] = checker.check(value[i]);
				}
			}
			output.put(key, newValue);
		}	
		
		return output;
	}
}
