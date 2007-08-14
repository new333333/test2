/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.module.definition.notify;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebUrlUtil;
/**
* Handle graphic type fields in mail notification.  
* @author Janet McCann
*/
public class NotifyBuilderGraphic extends AbstractNotifyBuilder {

	   protected boolean build(Element element, Notify notifyDef, CustomAttribute attribute, Map args) {
		   DefinableEntity entry = attribute.getOwner().getEntity();
		   Set files = attribute.getValueSet();
		   for (Iterator iter=files.iterator(); iter.hasNext();) {
		    	Element value = element.addElement("graphic");		    		
		    	FileAttachment att = (FileAttachment)iter.next();
		    	if (att != null && att.getFileItem() != null) {
		    		value.setText(att.getFileItem().getName());
		    		if (notifyDef.isAttachmentsIncluded())	
		    			notifyDef.addAttachment(att);
		    		else if (entry instanceof FolderEntry) {
		    			FolderEntry fEntry = (FolderEntry)entry;
		    			String webUrl = DefinitionUtils.getViewPermalinkURL(fEntry, att); 
		    			value.addAttribute("href", webUrl);
		    		}
		    	}
	    	}
	    	return true;
	    }
}
