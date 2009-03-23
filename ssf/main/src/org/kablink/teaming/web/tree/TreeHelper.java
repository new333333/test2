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
package org.kablink.teaming.web.tree;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.Validator;

public class TreeHelper {

	public static Collection<Long> getSelectedIds(String[] values, String prefix) {
		HashSet<Long> ids = new HashSet();
		
		if (values == null) {
			return ids;
		}

		//Get the binders for reporting
		for (int i = 0; i < values.length; i++) {
			String[] valueSplited = values[i].split("\\s");
			for (int j = 0; j < valueSplited.length; j++) {
				if (Validator.isNotNull(valueSplited[j])) {
					String id=null;
					if (Validator.isNotNull(prefix)) {
						if (!valueSplited[j].startsWith(prefix + WebKeys.URL_ID_CHOICES_SEPARATOR)) continue;
						id = valueSplited[j].replace(prefix + WebKeys.URL_ID_CHOICES_SEPARATOR, "");
					} else {
						//just look for separator
						id = valueSplited[j].substring(valueSplited[j].indexOf(WebKeys.URL_ID_CHOICES_SEPARATOR) + 1);
					}
					try {
						ids.add(Long.valueOf(id));
					} catch (NumberFormatException nf) {};
				}
			}
		}
		return ids;
	}
	public static Collection<Long> getSelectedIds(Map params) {
		return getSelectedIds((String[])params.get(WebKeys.URL_ID_CHOICES), null);
	}
	public static Collection<Long> getSelectedIds(Map params, String prefix) {
		return getSelectedIds((String[])params.get(WebKeys.URL_ID_CHOICES), prefix);
	}
	public static Collection<Long> getSelectedIds(InputDataAccessor params, String prefix) {
		return getSelectedIds(params.getValues(WebKeys.URL_ID_CHOICES), prefix);
	}
	public static Collection<String> getSelectedStringIds(Map params, String prefix) {
		HashSet<String> ids = new HashSet();

		//Get the binders for reporting
		String[] values = (String[])params.get(WebKeys.URL_ID_CHOICES);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				String[] valueSplited = values[i].split("\\s");
				for (int j = 0; j < valueSplited.length; j++) {
					if (Validator.isNotNull(valueSplited[j])) {
						String id=null;
						if (Validator.isNotNull(prefix)) {
							if (!valueSplited[j].startsWith(prefix + WebKeys.URL_ID_CHOICES_SEPARATOR)) continue;
							id = valueSplited[j].replace(prefix + WebKeys.URL_ID_CHOICES_SEPARATOR, "");
						} else {
							//just look for separator
							id = valueSplited[j].substring(valueSplited[j].indexOf(WebKeys.URL_ID_CHOICES_SEPARATOR) + 1);
						}
						ids.add(id);
					}
				}
			}
		}
		return ids;
	}	
	public static Collection<String> getCheckedStringIds(Map params, String prefix) {
		HashSet<String> ids = new HashSet();

		//Step through the params looking for ids
		Set elements = params.keySet();
		Iterator itElements = elements.iterator();
		while (itElements.hasNext()) {
			String key = (String) itElements.next();
			String id = null;
			if (Validator.isNotNull(prefix)) {
				if (!key.startsWith(prefix + WebKeys.URL_ID_CHOICES_SEPARATOR)) continue;
				id = key.replace(prefix + WebKeys.URL_ID_CHOICES_SEPARATOR, "");
			} else {
				//just look for separator
				id = key.substring(key.indexOf(WebKeys.URL_ID_CHOICES_SEPARATOR) + 1);
			}
			ids.add(id);
		}

		return ids;
	}	
	public static Long getSelectedId(Map params) {
		Collection<Long>ids = getSelectedIds(params);
		if (ids.isEmpty()) return null;
		return ids.iterator().next();

	}
	public static String getSelectedIdsAsString(Map params) {
		//blank at begining and end
		StringBuffer ids = new StringBuffer(" ");

		//Get the binders for reporting
		String[] values = (String[])params.get(WebKeys.URL_ID_CHOICES);
		for (int i = 0; i < values.length; i++) {
			ids.append(values[i] + " ");
		}
		return ids.toString();
	}
	
}
