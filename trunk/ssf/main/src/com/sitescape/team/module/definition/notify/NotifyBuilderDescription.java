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
package com.sitescape.team.module.definition.notify;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.velocity.VelocityContext;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Description;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.MarkupUtil;
import com.sitescape.util.Html;
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
    		value = MarkupUtil.markupStringReplacement(null, null, null, null, visitor.getEntity(), ((Description)obj).getText(), WebKeys.MARKUP_EXPORT);	
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
    		if (visitor.isHtml())
    			value = MarkupUtil.markupStringReplacement(null, null, null, null, visitor.getEntity(), obj.getText(), WebKeys.MARKUP_EXPORT);
    		else 
    			value = obj.getText();
    	}
    	if (!visitor.isHtml()) value = replaceHtml(value);
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
    	value.replaceAll("&nbsp;", " ");
    	return  Html.stripHtml(value);
    }
}