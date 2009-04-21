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
package org.kablink.teaming.module.definition.notify;

import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.dom4j.Element;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.PermaLinkUtil;

public class NotifyVisitor {
	protected Log logger = LogFactory.getLog(getClass());
	DefinableEntity entity;
	Notify notifyDef; 
	Element currentElement;
	List items=null;
	Map params;
	public enum WriterType {
		HTML,
		TEXT
	}
	WriterType currentWriterType;
	Writer currentWriter;
	
	public NotifyVisitor(DefinableEntity entity, Notify notifyDef, Element currentElement, Writer writer, WriterType writerType, Map params) {
		this.entity = entity;
		this.notifyDef = notifyDef;
		this.currentElement = currentElement;
		this.currentWriter = writer;
		this.currentWriterType = writerType;
		this.params = params;
	}
 	
 	public DefinableEntity getEntity() {
 		return entity;
 	}
 	public Boolean isHtml() {
 		return WriterType.HTML.equals(currentWriterType);
 	}
 	public Notify getNotifyDef() {
 		return notifyDef;
 	}
 	public Element getItem() {
 		return currentElement;
 	}
 	public List getItems() {
 		if (items == null) {
 			items = currentElement.elements("item");
 			if (items == null) items = Collections.EMPTY_LIST;
 		}
 		return items;
 		
 	}
 	public Writer getWriter() {
 		return currentWriter;
 	}
 	//parameters set for entity
 	public Object getParam(String name) {
 		if (params == null) return null;
 		return params.get(name);
 	}
 	//start processing templates.
 	public void processTemplate(String template, VelocityContext ctx) throws Exception {
 		NotifyBuilderUtil.getVelocityEngine().mergeTemplate(template, ctx, currentWriter);

 	}
 	//used to process contents
	public void visit() {
		try {
			NotifyBuilderUtil.buildElements(entity, currentElement, notifyDef, currentWriter, currentWriterType, params,  false);
		} catch (Exception ex) {
			NotifyBuilderUtil.logger.error("Error processing template:", ex);
		}
  	}
	//process this item
	public void visit(Element nextItem) {
		try {
			NotifyBuilderUtil.buildElements(entity, nextItem, notifyDef, currentWriter, currentWriterType, params, true);
		} catch (Exception ex) {
			NotifyBuilderUtil.logger.error("Error processing template:", ex);
		}
  	}
	//Used to process replies
	public void visit(DefinableEntity entry) {
		try {
			NotifyBuilderUtil.buildElements(entry, notifyDef, currentWriter, currentWriterType, new HashMap());
		} catch (Exception ex) {
			NotifyBuilderUtil.logger.error("Error processing template:", ex);
		}
  	}
	public String getNLT(String tag) {
		return NLT.get(tag, notifyDef.getLocale());
	}
	public String getNLTDef(String tag) {
		return NLT.getDef(tag, notifyDef.getLocale());
	}
	public String getPermaLink(DefinableEntity entity) {
		return PermaLinkUtil.getPermalink(entity);
	}
	public String getFileLink(FileAttachment attachment) {
		return PermaLinkUtil.getFilePermalink(attachment);
	}

}
