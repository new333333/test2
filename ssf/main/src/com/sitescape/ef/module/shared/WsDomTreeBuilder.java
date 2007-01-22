package com.sitescape.ef.module.shared;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.EntityIdentifier.EntityType;
import com.sitescape.ef.util.AllBusinessServicesInjected;
import com.sitescape.ef.util.ReflectHelper;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.util.Validator;

public class WsDomTreeBuilder implements DomTreeBuilder {
	static protected Log logger = LogFactory.getLog(WsDomTreeBuilder.class);
	static DomTreeHelper defaultHelper = new WsTreeHelper();
	static Map actionMapper = new HashMap();

	Binder bottom;
	boolean check;
	AllBusinessServicesInjected bs;
	DomTreeHelper helper = defaultHelper;
			
	public boolean supportsType(int type) {
		return helper.supportsType(type);
	}

	
	public WsDomTreeBuilder(Binder bottom, boolean checkChildren, AllBusinessServicesInjected bs) {
		this.bottom = bottom;
		this.check = checkChildren;
		this.bs = bs;
	}
	public WsDomTreeBuilder(Binder bottom, boolean checkChildren, AllBusinessServicesInjected bs,
			DomTreeHelper helper) {
		this.bottom = bottom;
		this.check = checkChildren;
		this.bs = bs;
		//register helper if not already done
		if (helper == null) return;
		synchronized (actionMapper) {
			actionMapper.put(helper.getTreeNameKey(), helper.getClass().getName());
		}
		this.helper = helper;
	}
	public WsDomTreeBuilder(Binder bottom, boolean checkChildren, AllBusinessServicesInjected bs,
			String key) {
		this.bottom = bottom;
		this.check = checkChildren;
		this.bs = bs;
		if (Validator.isNull(key)) return;
		String processorClassName;
		synchronized (actionMapper) {
			processorClassName = (String)actionMapper.get(key);
		}
        Class processorClass;
        try {
            processorClass = ReflectHelper.classForName(processorClassName);
            helper = (DomTreeHelper)processorClass.newInstance();
        } catch (Exception e) {
        	logger.error("DomTree missing processor: " + key);
        }
		
	}
	public Element setupDomElement(int type, Object source, Element element) {
		if (!helper.supportsType(type)) return null;
		Binder binder = (Binder) source;
		element.addAttribute("title", binder.getTitle());
		element.addAttribute("id", binder.getId().toString());
			//only need this information if this is the bottom of the tree
		if (check && (bottom == null ||  bottom.equals(binder.getParentBinder()))) {
			if (helper.hasChildren(bs, source, type)) {
				element.addAttribute("hasChildren", "true");
			} else {	
				element.addAttribute("hasChildren", "false");
			}
		}
		if ((type == DomTreeBuilder.TYPE_WORKSPACE)) {
			Workspace ws = (Workspace)source;
			String icon = ws.getIconName();
			String imageClass = "ss_twIcon";
			if (icon == null || icon.equals("")) {
				icon = "/icons/workspace.gif";
				imageClass = "ss_twImg";
			}
			element.addAttribute("type", DomTreeBuilder.NODE_TYPE_WORKSPACE);
			element.addAttribute("image", icon);
			element.addAttribute("imageClass", imageClass);
			element.addAttribute("action", helper.getAction(DomTreeBuilder.TYPE_WORKSPACE));
			element.addAttribute("displayOnly", helper.getDisplayOnly(DomTreeBuilder.TYPE_WORKSPACE));
					
		} else if ((type == DomTreeBuilder.TYPE_FOLDER)) {
			Folder f = (Folder)source;
			String icon = f.getIconName();
			if (icon == null || icon.equals("")) icon = "/icons/folder.png";
			element.addAttribute("type", DomTreeBuilder.NODE_TYPE_FOLDER);
			element.addAttribute("image", icon);
			element.addAttribute("imageClass", "ss_twIcon");
			element.addAttribute("action", helper.getAction(DomTreeBuilder.TYPE_FOLDER));
			element.addAttribute("displayOnly", helper.getDisplayOnly(DomTreeBuilder.TYPE_FOLDER));
		} else return null;
		return element;
	}
	
	protected static class WsTreeHelper implements DomTreeHelper {
		public boolean supportsType(int type) {
			if (type == DomTreeBuilder.TYPE_WORKSPACE) {return true;}
			if (type == DomTreeBuilder.TYPE_FOLDER) {return true;}
			return false;
		}
		public boolean hasChildren(AllBusinessServicesInjected bs, Object source, int type) {
			return bs.getBinderModule().hasBinders((Binder)source);
		}
	
		public String getAction(int type) {
			if (type == DomTreeBuilder.TYPE_WORKSPACE) return WebKeys.ACTION_VIEW_WS_LISTING;
			else return WebKeys.ACTION_VIEW_FOLDER_LISTING;
		}
		public String getURL(int type) {return "";}
		public String getDisplayOnly(int type) {return "false";}
		public String getTreeNameKey() {return null;};
	}
}	
