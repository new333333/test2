package com.sitescape.team.search;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Criteria
{
	Junction root;
	
	public Criteria()
	{
		root = new Junction.Conjunction();
	}
	
	public Criteria add(Criterion crit)
	{
		root.add(crit);
		return this;
	}
	
	
	public Document toQuery()
	{
		Document doc = DocumentHelper.createDocument();
		Element rootElement = doc.addElement(QueryBuilder.QUERY_ELEMENT);
		root.toQuery(rootElement);
		
		return doc;
	}
}
