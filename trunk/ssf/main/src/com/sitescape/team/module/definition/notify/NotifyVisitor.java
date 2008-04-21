package com.sitescape.team.module.definition.notify;

import java.io.Writer;
import java.util.List;
import java.util.Collections;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import java.util.Map;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;

public class NotifyVisitor {
	protected Log logger = LogFactory.getLog(getClass());
	DefinableEntity entity;
	Notify notifyDef; 
	Writer writer;
	Element currentElement;
	List items=null;
	Map params;
	public NotifyVisitor(DefinableEntity entity, Notify notifyDef, Element currentElement, Writer writer, Map params) {
		this.entity = entity;
		this.notifyDef = notifyDef;
		this.currentElement = currentElement;
		this.writer = writer;
		this.params = params;
	}
 	
 	public DefinableEntity getEntity() {
 		return entity;
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
 		return writer;
 	}
 	//parameters set for entity
 	public Object getParam(String name) {
 		if (params == null) return null;
 		return params.get(name);
 	}
	public void visit() {
		try {
			NotifyBuilderUtil.buildElements(entity, currentElement, notifyDef, writer, params, false);
		} catch (Exception ex) {
			NotifyBuilderUtil.logger.error("Error processing template:", ex);
		}
  	}
	//process this item
	public void visit(Element nextItem) {
		try {
			NotifyBuilderUtil.buildElements(entity, nextItem, notifyDef, writer, params, true);
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
	public String getPermaLink(Long binderId, Long entityId, String type) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		if (entityId != null) adapterUrl.setParameter(WebKeys.URL_ENTITY_ID, binderId.toString());
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, type);
		return adapterUrl.toString();
	}
	public String getPermaLink(Long binderId, Long entityId, String type, FileAttachment file) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, binderId.toString());
		if (entityId != null) adapterUrl.setParameter(WebKeys.URL_ENTITY_ID, binderId.toString());
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, type);
		adapterUrl.setParameter(WebKeys.URL_FILE_ID, file.getId().toString());
		return adapterUrl.toString();
	}
}
