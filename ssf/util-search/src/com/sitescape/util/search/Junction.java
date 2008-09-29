package com.sitescape.util.search;

import java.util.LinkedList;
import java.util.List;

import org.dom4j.Branch;
import org.dom4j.Element;


abstract public class Junction implements Criterion
{
	List<Criterion> criteria;
	
	public Junction()
	{
		this.criteria = new LinkedList<Criterion>();
	}
	
	public Junction add(Criterion crit)
	{
		criteria.add(crit);
		return this;
	}
	
	public Element toQuery(Branch parent)
	{
		Element root = parent.addElement(this.getOp());
		for(Criterion crit : criteria)
		{
			crit.toQuery(root);
		}
		return root;
	}
	
	abstract String getOp();
	
	public static class Disjunction extends Junction
	{
		public Disjunction()
		{
			super();
		}
		
		public String getOp()
		{
			return Constants.OR_ELEMENT;
		}
	}

	public static class Conjunction extends Junction
	{
		public Conjunction()
		{
			super();
		}

		public String getOp()
		{
			return Constants.AND_ELEMENT;
		}
	}
	public static class Not extends Junction
	{
		public Not()
		{
			super();
		}

		public String getOp()
		{
			return Constants.NOT_ELEMENT;
		}
	}
}
