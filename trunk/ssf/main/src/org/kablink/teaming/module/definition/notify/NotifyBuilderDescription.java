/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.definition.notify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.VelocityContext;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.util.Html;

/**
*
* @author Janet McCann
*/
public class NotifyBuilderDescription extends AbstractNotifyBuilder {
	
    public String getDefaultTemplate() {
    	return "description.vm";
    }
    public void build(NotifyVisitor visitor, String template, VelocityContext ctx, CustomAttribute attr) {
    	Object obj = attr.getValue();
    	String value;
    	if (obj instanceof Description && !visitor.isHtml())
    		value = MarkupUtil.markupStringReplacement(null, null, null, null, visitor.getEntity(), ((Description)obj).getText(), WebKeys.MARKUP_EMAIL);	
    	else
    		value = obj.toString();	
    	if (!visitor.isHtml()) value = replaceHtml(value);
    	ctx.put("ssDescription_markup", value);
    	super.build(visitor, template, ctx);
    }
    public void build(NotifyVisitor visitor, String template, VelocityContext ctx) {
    	ctx.remove("property_name");
    	Description obj = visitor.getEntity().getDescription();
    	String value="";
    	if (obj != null) {
    		if (visitor.isHtml()) {
    			value = MarkupUtil.markupStringReplacement(null, null, null, null, visitor.getEntity(), obj.getText(), WebKeys.MARKUP_EMAIL);
    			value = MarkupUtil.markupSectionsReplacement(value);
    		} else {
    			value = obj.getText();
    		}
    	}
    	if (visitor.isHtml()) {
    		if ((null != obj) && (Description.FORMAT_NONE == obj.getFormat())) {
    			value = Html.plainTextToHTML(value);
    		}
    	} else {
    		value = replaceHtml(value);
    	}
     	ctx.put("ssDescription_markup", value);
    	super.build(visitor, template, ctx);
    }
    private String replaceHtml(String value) {
    	//Now, replace the url with special markup version
    	Pattern returnPattern = Pattern.compile("<(br|p)[ /]*>", Pattern.CASE_INSENSITIVE);

    	Matcher m3 = returnPattern.matcher(value);
    	if (m3.find()) {
    		value = m3.replaceAll("\r\n");
    	}
    	value = value.replaceAll("&nbsp;", " ");
    	value = value.replaceAll("&#39;",  "'");
    	return  Html.stripHtml(value);
    }
}