package com.sitescape.util.search;

import java.util.Arrays;
import java.util.Collection;

import org.dom4j.Branch;
import org.dom4j.Element;


public class Restrictions
{
	public static Criterion eq(String field, String value)
	{
		return new LikeCriterion(field, value, true);
	}
	
	public static Criterion like(String field, String value)
	{
		return new LikeCriterion(field, value, false);
	}
	
	public static Criterion in(String field, Collection<String> values)
	{
		return new InCriterion(field, values);
	}
	
	public static Criterion in(String field, String[] values)
	{
		return new InCriterion(field, Arrays.asList(values));
	}
	
	public static Criterion between(String field, String lo, String hi)
	{
		return new BetweenCriterion(field, lo, hi);
	}
	
	public static Junction.Conjunction conjunction()
	{
		return new Junction.Conjunction();
	}

	public static Junction.Disjunction disjunction()
	{
		return new Junction.Disjunction();
	}

	
	abstract static class FieldCriterion implements Criterion
	{
		protected String fieldName;
		
		public FieldCriterion(String fieldName)
		{
			this.fieldName = fieldName;
		}
		
		protected Element toQuery(Branch parent, String value)
		{
			Element root = parent.addElement(Constants.FIELD_ELEMENT);
			root.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, fieldName);
        	Element child = root.addElement(Constants.FIELD_TERMS_ELEMENT);
    		child.setText(value);
    		
			return root;
		}
	}
	
	static class LikeCriterion extends FieldCriterion
	{
		private String value;
		private boolean exact;

		public LikeCriterion(String fieldName, String value, boolean exact)
		{
			super(fieldName);
			this.value = value;
			this.exact = exact;
		}
		
		public Element toQuery(Branch parent)
		{
			Element element = super.toQuery(parent, value);
			if(exact) {
				element.addAttribute(Constants.EXACT_PHRASE_ATTRIBUTE, "true");
			}
			return element;
		}
	}
	
	static class InCriterion extends FieldCriterion
	{
		private Collection<String> values;
		
		public InCriterion(String fieldName, Collection<String> values)
		{
			super(fieldName);
			this.values = values;
		}
		
		public Element toQuery(Branch parent)
		{
			Element root = parent.addElement(Constants.OR_ELEMENT);
			for(String value : values)
			{
				super.toQuery(root, value);
			}
			
			return root;
		}
	}
	
	static class BetweenCriterion implements Criterion
	{
		protected String fieldName;
		protected String lo;
		protected String hi;

		public BetweenCriterion(String fieldName, String lo, String hi)
		{
			this.fieldName = fieldName;
			this.lo = lo;
			this.hi = hi;
		}

		public Element toQuery(Branch parent)
		{
			Element root = parent.addElement(Constants.RANGE_ELEMENT);
			root.addAttribute(Constants.FIELD_NAME_ATTRIBUTE, fieldName);
			root.addAttribute(Constants.INCLUSIVE_ATTRIBUTE, Constants.INCLUSIVE_TRUE);
			Element child = root.addElement(Constants.RANGE_START);
			child.setText(lo);
			child = root.addElement(Constants.RANGE_FINISH);
			child.setText(hi);

			return root;
		}
	}
}
