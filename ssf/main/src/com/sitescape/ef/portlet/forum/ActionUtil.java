/**
 * Copyright (c) 2000-2005 Liferay, LLC. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sitescape.ef.portlet.forum;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.NoFolderByTheIdException;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;


/**
 * <a href="ActionUtil.java.html"><b><i>View Source</i></b></a>
 *
 * @author  PHurley
 * @version $Revision: 1.0 $
 *
 */
public class ActionUtil {

	/**
	 * Return forumId from request.  If not in request, retrieve portlet preference.
	 * If forumId is not found, throw NoFolderByTheIdException
	 * @param formData
	 * @param req
	 * @return
	 * @throws NoFolderByTheIdException
	 */
	public static Long getForumId(Map formData, PortletRequest req) throws NoFolderByTheIdException {
		String forumId = "";
		if (formData.containsKey(WebKeys.FORUM_URL_FORUM_ID)) {
			Object obj = formData.get(WebKeys.FORUM_URL_FORUM_ID);
			if (obj instanceof String[]) {
				forumId = ((String[]) formData.get(WebKeys.FORUM_URL_FORUM_ID))[0];
			} else {
				forumId = (String) formData.get(WebKeys.FORUM_URL_FORUM_ID);
			}
		}
		if (forumId.equals("")) {
			//Get the preferences settings to see if there is a forum defined
			PortletPreferences prefs = req.getPreferences();
			forumId = prefs.getValue("forumId", "");
		}
		req.setAttribute(WebKeys.FORUM_URL_FORUM_ID,forumId);
		try {
			return Long.valueOf(forumId);
		} catch (NumberFormatException nf) {
			throw new NoFolderByTheIdException(new Long(0), nf);
		}
	}
	public static Long getForumId(Map formData, HttpServletRequest req) throws NoFolderByTheIdException {
		Long forumId = getForumId(formData);
		req.setAttribute(WebKeys.FORUM_URL_FORUM_ID,forumId.toString());
		return forumId;
	}
	public static Long getForumId(Map formData) throws NoFolderByTheIdException {
		String forumId = "";
		if (formData.containsKey(WebKeys.FORUM_URL_FORUM_ID)) {
			Object obj = formData.get(WebKeys.FORUM_URL_FORUM_ID);
			if (obj instanceof String[]) {
				forumId = ((String[]) formData.get(WebKeys.FORUM_URL_FORUM_ID))[0];
			} else {
				forumId = (String) formData.get(WebKeys.FORUM_URL_FORUM_ID);
			}
		}
		try {
			return Long.valueOf(forumId);
		} catch (NumberFormatException nf) {
			throw new NoFolderByTheIdException(new Long(0), nf);
		}
	}
	public static Map getEntryDefsAsMap(Folder folder) {
		Map defaultEntryDefinitions = new HashMap();
		Iterator itDefaultEntryDefinitions = folder.getEntryDefs().listIterator();
		while (itDefaultEntryDefinitions.hasNext()) {
			Definition entryDef = (Definition) itDefaultEntryDefinitions.next();
			defaultEntryDefinitions.put(entryDef.getId(), entryDef);
		}
		return defaultEntryDefinitions;
	}

	public static String getStringValue(Map formData, String key) {
		String val;
		Object obj = formData.get(key);
		if (obj == null) return "";
		if (obj instanceof String[]) {
			val = ((String[]) formData.get(key))[0];
		} else {
			val = (String)obj;
		}
		return val;
	}
	public static String[] getStringArray(Map formData, String key) {
		String[] val;
		Object obj = formData.get(key);
		if (obj == null) return new String[0];
		if (obj instanceof String[]) {
			val = (String[])obj;
		} else {
			val = new String[0];
			val[0] = (String)obj;
		}
		return val;
	}
	public static Integer getIntegerValue(Map formData, String key) {
		Integer val=null;
		String sVal;
		Object obj = formData.get(key);
		if (obj == null) return null;
		if (obj instanceof String[]) {
			sVal = ((String[]) formData.get(key))[0];
		} else {
			sVal = (String) obj;
		}
		try {
			val = Integer.valueOf(sVal);
		} catch (NumberFormatException nf) {
			return null;
		}
		return val;
	}
	public static Set getLongSet(Map formData, String key) {
		Set result = new HashSet();
		Object obj = formData.get(key);
		if (obj == null) return result;
		if (obj instanceof String[]) {
			String vals[] = (String[])obj;
			for (int i=0; i<vals.length; ++i) {
				obj = vals[i];
				if (!obj.equals("")) result.add(Long.valueOf((String)obj));
			}
		} else {
			if (!obj.equals("")) result.add(Long.valueOf((String)obj));
		}
		return result;
	}
}