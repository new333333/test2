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

import java.util.List;

import org.apache.velocity.VelocityContext;
import org.dom4j.Element;

import com.sitescape.team.ObjectKeys;
/**
 *
 * @author Janet McCann
 */
public class NotifyBuilderEntry extends AbstractNotifyBuilder {
    public String getDefaultTemplate() {
    	return "entry.vm";
    }
    protected void build(NotifyVisitor visitor, String template, VelocityContext ctx) {
    	if (!Notify.NotifyType.summary.equals(visitor.getNotifyDef().getType())) {
    		super.build(visitor, template, ctx);
    	} else {
	    	String family = (String)visitor.getParam("ssFamily");
	    	if (ObjectKeys.FAMILY_CALENDAR.equals(family) || ObjectKeys.FAMILY_TASK.equals(family)) {
	    		processDigestCalendar(visitor, template, ctx);
	    	} else {
	    		processDigest(visitor, template, ctx);
	    	}
    	}
    } 
    protected void processDigest(NotifyVisitor visitor, String template, VelocityContext ctx) {
    	doTitle(visitor, ctx);
    	doWorkflow(visitor, ctx);
    	doDescription(visitor, ctx);
    	doAttachments(visitor, ctx);
    	
    }
    protected void processDigestCalendar(NotifyVisitor visitor, String template, VelocityContext ctx) {
       	doTitle(visitor, ctx);
      	doEvents(visitor, ctx);
      	doDescription(visitor, ctx);
    	doAttachments(visitor, ctx);
  	
    }
    protected void doTitle(NotifyVisitor visitor, VelocityContext ctx) {
       	try {
    		NotifyBuilderUtil.getVelocityEngine().mergeTemplate("digestTitle.vm", ctx, visitor.getWriter());
    	} catch (Exception ex) {
    		NotifyBuilderUtil.logger.error("Error processing template " + "digestTitle", ex);
    	}
    	
    }
    protected void doWorkflow(NotifyVisitor visitor, VelocityContext ctx) {
       	Element item = visitor.getItem();
        List<Element> entryWorkflow = item.selectNodes(".//item[@name='entryWorkflow']");
        if (entryWorkflow == null || entryWorkflow.isEmpty()) return;
        Element workflow = (Element)entryWorkflow.get(0);
        visitor.visit(workflow);   	
   	
    }
    protected void doDescription(NotifyVisitor visitor, VelocityContext ctx) {
       	Element item = visitor.getItem();
        List<Element> descriptions = item.selectNodes(".//item[@name='descriptionView']");
        if (descriptions == null || descriptions.isEmpty()) return;
        Element description = (Element)descriptions.get(0);
        visitor.visit(description);   	
    }
    protected void doAttachments(NotifyVisitor visitor, VelocityContext ctx) {
       	Element item = visitor.getItem();
        List<Element> descriptions = item.selectNodes(".//item[@name='entryAttachments']");
        if (descriptions == null || descriptions.isEmpty()) return;
        Element description = (Element)descriptions.get(0);
        visitor.visit(description);   	
    }
    protected void doEvents(NotifyVisitor visitor, VelocityContext ctx) {
       	Element item = visitor.getItem();
        List<Element> events = item.selectNodes(".//item[@name='entryDataItem' and @formItem='event']");
        if (events == null || events.isEmpty()) return;
        for (Element event:events) {
        	visitor.visit(event);   
        }
    }
}
