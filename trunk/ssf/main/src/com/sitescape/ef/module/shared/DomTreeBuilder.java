package com.sitescape.ef.module.shared;
import org.dom4j.Element;

public interface DomTreeBuilder {
	public static final int TYPE_WORKSPACE=1;
	public static final int TYPE_FOLDER=2;
	public static final int TYPE_PEOPLE=3;
	public static final int TYPE_FAVORITES=4;
	public static final int TYPE_TEMPLATE=5;
	
	public static final String NODE_ROOT="root";
	public static final String NODE_CHILD="child";
	public static final String NODE_TYPE_WORKSPACE="workspace";
	public static final String NODE_TYPE_FOLDER="folder";
	public static final String NODE_TYPE_PEOPLE="people";
	public static final String NODE_TYPE_FAVORITES="favorites";
	public static final String NODE_TYPE_TEMPLATE="template";
	
	public Element setupDomElement(int type, Object source, Element element);
	public boolean supportsType(int type, Object source);

}
