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
package org.kablink.teaming.taglib;

import java.util.HashMap;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.SpringContextUtil;


/**
 * Checks whether an entry is modifiable.
 * 
 * 
 * @author Dennis Foster
 * 
 */

public class IfEntryModifiableTag extends BodyTagSupport {
	
	private Boolean invert = Boolean.FALSE;
	
	private HashMap entryMap = null;
	
	public int doStartTag() throws JspTagException {
		boolean result = false;
		
		try {
			// If we don't have an entryMap...
			if(entryMap == null) {
				// ...bail.
				return SKIP_BODY;
			}	

			// If the entryMap doesn't contain a binderId...
			Long binderId = null;
			Object binderIdO = entryMap.get("_binderId");
			if ((null != binderIdO) && (binderIdO instanceof String)) {
				binderId = Long.parseLong((String) binderIdO);
			}
			if (null == binderId) {
				// ...bail.
				return SKIP_BODY;
			}
			
			// If the entryMap doesn't contain an entryId...
			Long entryId = null;
			Object entryIdO = entryMap.get("_docId");
			if ((null != entryIdO) && (entryIdO instanceof String)) {
				entryId = Long.parseLong((String) entryIdO);
			}
			if (null == entryId) {
				// ...bail.
				return SKIP_BODY;
			}

			// Access the entry...
	    	FolderModule folderModule = (FolderModule)SpringContextUtil.getBean("folderModule");
	    	FolderEntry folderEntry = folderModule.getEntry(binderId, entryId);

	    	// ...and check whether we can modify it.
			AccessUtils.modifyCheck(folderEntry);
			result = true;
		} catch (Exception e) {
			if (!(e instanceof AccessControlException)) {
				throw new JspTagException(e.getLocalizedMessage());
			}
		} finally {
			this.entryMap = null;
		}

		// If we're supposed to invert the result...
		if(invert.booleanValue()) {
			// ...invert it.
			result = ! result;
		}
		
		// Finally, return the appropriate EVAL_* value.
		if(result) {
			return EVAL_BODY_INCLUDE;
		}
		return EVAL_PAGE;
	}
	
	public int doAfterBody() {
		return SKIP_BODY;
	}
	
	public int doEndTag() {
		return EVAL_BODY_INCLUDE;
	}

	public void setEntryMap(HashMap entryMap) {
		this.entryMap = entryMap;
	}
	
	public void setInvert(Boolean invert)
	{
		this.invert = invert;
	}
}
