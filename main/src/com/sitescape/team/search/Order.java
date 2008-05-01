package com.sitescape.team.search;

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
		Element child = root.addElement(QueryBuilder.FIELD_NAME_ATTRIBUTE);
		if(ascending) {
			child.addAttribute(QueryBuilder.ASCENDING_ATTRIBUTE, QueryBuilder.ASCENDING_TRUE);
		}
		child.setText(fieldName);
		return child;
	}
}
