package com.sitescape.ef.module.shared;
import org.dom4j.Element;

public interface DomTreeBuilder {
	public static final String TYPE_WORKSPACE="workspace";
	public static final String TYPE_FOLDER="folder";
	public static final String TYPE_FAVORITES="favorites";
	public static final String NODE_ROOT="root";
	public static final String NODE_CHILD="child";
	
	public Element setupDomElement(String type, Object source, Element element);
}
