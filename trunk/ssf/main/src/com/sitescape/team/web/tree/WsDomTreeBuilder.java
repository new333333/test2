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
package com.sitescape.team.web.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.web.util.PermaLinkUtil;
import com.sitescape.util.Validator;

public class WsDomTreeBuilder implements DomTreeBuilder {
	static protected Log logger = LogFactory.getLog(WsDomTreeBuilder.class);
	static DomTreeHelper defaultHelper = new WsActionTreeHelper();
	static Map actionMapper = new HashMap();

	Binder bottom;
	boolean check;
	AllModulesInjected bs;
	DomTreeHelper helper = defaultHelper;
	String page="";
	List tuple;
			

	private void initHelper(DomTreeHelper helper) {
		//register helper if not already done
		if (helper == null) return;
		synchronized (actionMapper) {
			actionMapper.put(helper.getTreeNameKey(), helper.getClass().getName());
		}
		this.helper = helper;
		
	}
	private void initHelper(String key) {
    	if (Validator.isNull(key)) return;
		String processorClassName;
		synchronized (actionMapper) {
			processorClassName = (String)actionMapper.get(key);
		}
        Class processorClass;
        try {
            processorClass = ReflectHelper.classForName(processorClassName);
            this.helper = ((DomTreeHelper)processorClass.newInstance());
        } catch (Exception e) {
        	logger.error("DomTree missing processor: " + key);
        }
	}

	public WsDomTreeBuilder(Binder bottom, boolean checkChildren, AllModulesInjected bs) {
		this.bottom = bottom;
		this.check = checkChildren;
		this.bs = bs;
	}
	public WsDomTreeBuilder(Binder bottom, boolean checkChildren, AllModulesInjected bs,
			DomTreeHelper helper) {
		this(bottom, checkChildren, bs);
		initHelper(helper);
	}
	public WsDomTreeBuilder(Binder bottom, boolean checkChildren, AllModulesInjected bs,
			String key) {
		this(bottom, checkChildren, bs);
		initHelper(key);
	}
	public WsDomTreeBuilder(Binder bottom, boolean checkChildren, AllModulesInjected bs,
			String key, String page) {
		this(bottom, checkChildren, bs);
		initHelper(key);
		initPage(page);
	}

	public WsDomTreeBuilder(Binder bottom, boolean checkChildren, AllModulesInjected bs,
			DomTreeHelper helper, String page) {
		this(bottom, checkChildren, bs);
		initHelper(helper);
		initPage(page);
		
	}

	private void initPage(String page) {
		this.page = page;
		if (Validator.isNull(page)) return;
    	String tuple1 = "";
    	String tuple2 = "";
		int i = page.indexOf(DomTreeBuilder.PAGE_DELIMITER);
    	if (!page.equals("") && i >= 0) {
    		this.page = page.substring(0, i);
    		i += DomTreeBuilder.PAGE_DELIMITER.length();
    		String tuple = page.substring(i, page.length());
    		i = tuple.indexOf(DomTreeBuilder.PAGE_DELIMITER);
    		tuple1 = tuple.substring(0, i);
    		i += DomTreeBuilder.PAGE_DELIMITER.length();
    		tuple2 = tuple.substring(i, tuple.length());
    	}
    	this.tuple = new ArrayList(Arrays.asList(tuple1, tuple2));		
	}
	public boolean supportsType(int type, Object source) {
		return helper.supportsType(type, source);
	}
	public Element setupDomElement(int type, Object source, Element element) {
		if (!helper.supportsType(type, source)) return null;
		if (type == DomTreeBuilder.TYPE_SKIPLIST) {
			//This is a skiplist pair
			List tuple = (List) ((Map)source).get(DomTreeBuilder.SKIP_TUPLE);
			String pageTuple = tuple.get(0) + DomTreeBuilder.PAGE_DELIMITER + tuple.get(1);
			String title = tuple.get(0) + " <--> " + tuple.get(1);
			String page = (String) ((Map)source).get(DomTreeBuilder.SKIP_PAGE);
			String binderId = (String) ((Map)source).get(DomTreeBuilder.SKIP_BINDER_ID);
			String icon = "/icons/range.gif";
			String imageClass = "ss_twImg8";
			element.addAttribute("title", title);
			element.addAttribute("id", binderId + "." + page);
			element.addAttribute("hasChildren", "true");
			element.addAttribute("type", DomTreeBuilder.NODE_TYPE_RANGE);
			element.addAttribute("image", icon);
			element.addAttribute("imageClass", imageClass);
			element.addAttribute("page", page);
			element.addAttribute("pageTuple", pageTuple);
			element.addAttribute("tuple1", (String)tuple.get(0));
			element.addAttribute("tuple2", (String)tuple.get(1));
			element.addAttribute("action", "");
			element.addAttribute("displayOnly", "true");
		} else {
			Binder binder = (Binder) source;
			element.addAttribute("title", binder.getSearchTitle());
			if (getPage().equals("")) {
				element.addAttribute("id", binder.getId().toString());
			} else {
				element.addAttribute("id", binder.getId().toString() + "." + getPage());
			}
				//only need this information if this is the bottom of the tree
			if (check && (bottom == null ||  bottom.equals(binder.getParentBinder()))) {
				if (helper.hasChildren(bs, source, type)) {
					element.addAttribute("hasChildren", "true");
				} else {	
					element.addAttribute("hasChildren", "false");
				}
			}
			if (element.isRootElement()) {
				//save identifier of tree helper to use on ajax callbacks
				element.addAttribute("treeKey", helper.getTreeNameKey());
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
				element.addAttribute("action", helper.getAction(DomTreeBuilder.TYPE_WORKSPACE, source));
				element.addAttribute("displayOnly", helper.getDisplayOnly(DomTreeBuilder.TYPE_WORKSPACE, source));
				element.addAttribute("permaLink", PermaLinkUtil.getURL(binder));
						
			} else if ((type == DomTreeBuilder.TYPE_FOLDER)) {
				Folder f = (Folder)source;
				String icon = f.getIconName();
				if (icon == null || icon.equals("")) icon = "/icons/folder.png";
				element.addAttribute("type", DomTreeBuilder.NODE_TYPE_FOLDER);
				element.addAttribute("image", icon);
				element.addAttribute("imageClass", "ss_twIcon");
				element.addAttribute("action", helper.getAction(DomTreeBuilder.TYPE_FOLDER, source));
				element.addAttribute("displayOnly", helper.getDisplayOnly(DomTreeBuilder.TYPE_FOLDER, source));
				element.addAttribute("permaLink", PermaLinkUtil.getURL(f));
			} else return null;
		}
		//add any extra attributes
		helper.customize(bs, source, type, element);
		return element;
	}
	
	public String getPage() {
		if (this.page == null) return "";
		return this.page;
	}
	
	public void setPage(String page) {
		this.page = page;
	}
	
	public List getTuple() {
		if (this.tuple == null) return new ArrayList(Arrays.asList("", ""));
		return this.tuple;
	}
	
}	
