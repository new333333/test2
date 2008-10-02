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
import org.apache.velocity.VelocityContext;

import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.util.InvokeUtil;
import com.sitescape.team.util.ObjectPropertyNotFoundException;
import com.sitescape.util.Validator;
/**
 *
 * @author Jong Kim
 */
public abstract class AbstractNotifyBuilder implements NotifyBuilder {
    
 
    public abstract String getDefaultTemplate();
    public void buildElement(NotifyVisitor visitor, String template, VelocityContext ctx) {
    	if (Validator.isNull(template)) template = getDefaultTemplate();
		String name = (String)ctx.get("property_name");
		if (Validator.isNull(name)) {
			build(visitor, template, ctx);
		} else {
			CustomAttribute attribute = visitor.getEntity().getCustomAttribute(name);
			if (attribute != null) {
				build(visitor, template, ctx, attribute);
			}
			else build(visitor, template, ctx, name);
		}
    }
    protected void build(NotifyVisitor visitor, String template, VelocityContext ctx, CustomAttribute attr) {
    	build(visitor, template, ctx);
    }
    protected void build(NotifyVisitor visitor, String template, VelocityContext ctx, String propertyName) {
	   	try {
	   		Object obj = InvokeUtil.invokeGetter(visitor.getEntity(), propertyName);
		   	if (obj != null) {
		   		ctx.put("ssObject", obj);
		   		build(visitor, template, ctx);
		   	}
		} catch (ObjectPropertyNotFoundException ex) {
	   		build(visitor, template, ctx);	   		
	   	}
    }
    protected void build(NotifyVisitor visitor, String template, VelocityContext ctx) {
    	try {
    		visitor.processTemplate(template, ctx);
    	} catch (Exception ex) {
    		NotifyBuilderUtil.logger.error("Error processing template " + template, ex);
    	}
    } 
}
