package com.sitescape.team.search;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Criteria
{
	Junction root;
	List<Order> orders;
	
	public Criteria()
	{
		root = new Junction.Conjunction();
		orders = new LinkedList<Order>();
	}
	
	public Criteria add(Criterion crit)
	{
		root.add(crit);
		return this;
	}
	
	public Criteria addOrder(Order order)
	{
		orders.add(order);
		return this;
	}
	
	public Document toQuery()
	{
		Document doc = DocumentHelper.createDocument();
		Element rootElement = doc.addElement(QueryBuilder.QUERY_ELEMENT);
		root.toQuery(rootElement);
		if(orders.size() > 0) {
			Element sortBy = rootElement.addElement(QueryBuilder.SORTBY_ELEMENT);
			for(Order o : orders) {
				o.toQuery(sortBy);
			}
		}
		return doc;
	}
}
