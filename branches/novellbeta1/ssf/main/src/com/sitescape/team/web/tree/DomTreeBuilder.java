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
import java.util.List;

import org.dom4j.Element;

public interface DomTreeBuilder {
	public static final int TYPE_WORKSPACE=1;
	public static final int TYPE_FOLDER=2;
	public static final int TYPE_PEOPLE=3;
	public static final int TYPE_FAVORITES=4;
	public static final int TYPE_TEMPLATE=5;
	public static final int TYPE_SKIPLIST=6;

	public static final String SKIP_TUPLE="tuple";
	public static final String SKIP_PAGE="page";
	public static final String SKIP_BINDER_ID="binderId";
	
	public static final String PAGE_DELIMITER="//";

	public static final String NODE_ROOT="root";
	public static final String NODE_CHILD="child";
	public static final String NODE_TYPE_WORKSPACE="workspace";
	public static final String NODE_TYPE_FOLDER="folder";
	public static final String NODE_TYPE_PEOPLE="people";
	public static final String NODE_TYPE_FAVORITES="favorites";
	public static final String NODE_TYPE_TEMPLATE="template";
	public static final String NODE_TYPE_RANGE="range";
	
	public Element setupDomElement(int type, Object source, Element element);
	public boolean supportsType(int type, Object source);
	public String getPage();
	public void setPage(String page);
	public List getTuple();

}
