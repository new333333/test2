package com.sitescape.util.search;

import org.dom4j.Branch;
import org.dom4j.Element;


public class Order
{
	protected String fieldName;
	protected boolean ascending;
	
	public Order(String fieldName, boolean ascending)
	{
		this.fieldName = fieldName;
		this.ascending = ascending;
	}
	
	public static Order asc(String fieldName)
	{
		return new Order(fieldName, true);
	}
	
	public static Order desc(String fieldName)
	{
		return new Order(fieldName, false);
	}
	
	public Element toQuery(Branch root)
	{
		Element child = root.addElement(Constants.FIELD_NAME_ATTRIBUTE);
		if(ascending) {
			child.addAttribute(Constants.ASCENDING_ATTRIBUTE, Constants.ASCENDING_TRUE);
		}
		child.setText(fieldName);
		return child;
	}
}
