/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.dom4j.Element;
import org.kablink.teaming.dao.util.SSBlobSerializableType;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.extuser.ExternalUserUtil;
import org.kablink.teaming.module.file.ConvertedFileModule;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.PermaLinkUtil;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
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
 	//parameters set for entity
 	public void setParam(String name, Object value) {
 		params.put(name, value);
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
		return PermaLinkUtil.getPermalinkForEmail(entity);
	}
	public String getInvitePermaLink(DefinableEntity entity, String encodedExternalUserId) {
		return getPermalinkWithEncodedExternalUserId(entity, encodedExternalUserId);
	}
	private String getPermalinkWithEncodedExternalUserId(DefinableEntity entity, String encodedExternalUserId) {
		String param = (ExternalUserUtil.QUERY_FIELD_NAME_EXTERNAL_USER_ENCODED_TOKEN + "=" + encodedExternalUserId);
		String url   = (PermaLinkUtil.getPermalinkForEmail(entity) + "&" + param);
		return url;
	}
	public String getFileLink(FileAttachment attachment) {
		return PermaLinkUtil.getFilePermalinkForEmail(attachment);
	}
	public String getUserTitle(Principal p) {
		if (this.notifyDef.isRedacted()) {
			return NLT.get("user.redacted.title");
		} else {
			return Utils.getUserTitle(p);
		}
	}
	public String getUserName(Principal p) {
		if (this.notifyDef.isRedacted()) {
			return NLT.get("user.redacted.title");
		} else {
			return Utils.getUserName(p);
		}
	}
	public boolean isAvatarShown() {
		boolean result = SPropsUtil.getBoolean("email.showAvatarInHeader", Boolean.FALSE);
		return result;
	}
	public String getUserThumbnailInlineImage(Principal p) {
		String s_photo = "";
		String ext = "";
		if (!this.notifyDef.isRedacted()) {
			try {
				Set photos = null;
				CustomAttribute ca = p.getCustomAttribute("picture");
				if (ca != null) photos = ca.getValueSet();
				if (photos != null) {
					FileAttachment photo = (FileAttachment)photos.iterator().next();
					String fileName = photo.getFileItem().getName();
					if (fileName.lastIndexOf(".") > 0) {
						ext = fileName.substring(fileName.lastIndexOf(".")+1);
					}
					ByteArrayOutputStream baos = new ByteArrayOutputStream(
							SSBlobSerializableType.OUTPUT_BYTE_ARRAY_INITIAL_SIZE);
					ConvertedFileModule convertedFileModule = ((ConvertedFileModule) SpringContextUtil.getBean("convertedFileModule"));
					convertedFileModule.readThumbnailFile(p.getParentBinder(), p, photo, baos);
					try {
						s_photo = new String(Base64.encodeBase64(baos.toByteArray()),"utf-8");
					} catch (UnsupportedEncodingException e) {
						s_photo = "";
					}
				}
			}
			
			catch (Exception ex) {
				NotifyBuilderUtil.logger.error("Error processing thumbnail image for '" + p.getTitle() + "' :", ex);
				s_photo = "";
				ext     = "";
			}
		}
		if (!s_photo.equals("") && !ext.equals("")) {
			s_photo = "<img src=\"data:image/" + ext + ";base64," + s_photo + "\"/>";
		}
		return s_photo;
	}
	public String getDocNumber() {
		String result = null;
		if (entity instanceof FolderEntry) {
			FolderEntry entry = (FolderEntry) entity;
			Binder binder = entity.getParentBinder();
			Map ssFolderColumns = (Map) binder.getProperty("folderColumns");
			if (ssFolderColumns == null) {
				ssFolderColumns = new java.util.HashMap();
			}
			if (ssFolderColumns.containsKey("number")) {
				//This folder is showing numbers
				result = entry.getDocNumber();
			} else if (!entry.isTop()) {
				//The folder isn't showing doc numbers, but this is a reply, so get it's reply number only
				String docNum = entry.getDocNumber();
		  		result = docNum.substring(docNum.indexOf(".") + 1, docNum.length());
			}
		}
		return result;
	}
	public Boolean isHasChanges() {
		boolean result = false;
		if (notifyDef.getStartDate() != null) {
			if (entity instanceof FolderEntry) {
				FolderEntry entry = (FolderEntry)entity;
				if (notifyDef.getStartDate().before(entry.getCreation().getDate())) {
					result = true;
				} else if (entry.getWorkflowChange() != null && notifyDef.getStartDate().before(entry.getWorkflowChange().getDate())) {
					result = true;
				} else if (notifyDef.getStartDate().before(entry.getModification().getDate())) {
					result = true;
				} 
			}
		}
		return result;
	}
	public String getChangeType() {
		String result = "";
		if (notifyDef.getStartDate() != null) {
			if (entity instanceof FolderEntry) {
				FolderEntry entry = (FolderEntry)entity;
				if (notifyDef.getStartDate().before(entry.getCreation().getDate())) {
					result = "notify.newEntry";
				} else if (entry.getWorkflowChange() != null && notifyDef.getStartDate().before(entry.getWorkflowChange().getDate())) {
					result = "notify.workflowEntry";
				} else {
					result = "notify.modifiedEntry";
				} 
			}
		}
		return result;
	}
	
	public boolean isAttachmentOverQuota(FileAttachment att) {
		boolean result =  Utils.testSendMailAttachmentSize(att);
		return !result;
	}

	public boolean isAttachmentsOverQuota(Set<FileAttachment> atts) {
		boolean result = Utils.testSendMailAttachmentsSize(atts);
		return !result;
	}
}
