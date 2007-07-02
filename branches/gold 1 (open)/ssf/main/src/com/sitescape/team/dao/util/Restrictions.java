package com.sitescape.team.dao.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Restrictions {
	abstract static class SingleFieldCriterion implements Criterion
	{
		String name;

		public SingleFieldCriterion(String name) {
			this.name = name;
		}
		
		public List<Object> getParameterValues()
		{
			return new LinkedList<Object>();
		}
		protected String getFieldName(String alias)
		{
  			int pos = name.lastIndexOf('(');
  			if (pos == -1)
  				return alias + "." + name;
  			else {
  				++pos;
  	  			return name.substring(0, pos) + alias + "." + name.substring(pos, name.length());
  			}
		}
	}
	abstract static class SingleValueCriterion extends SingleFieldCriterion
	{
		Object value;
		public SingleValueCriterion(String name, Object value) {
			super(name);
			this.value = value;
		}
		
		public List<Object> getParameterValues()
		{
			return Arrays.asList(value);
		}
		public String toSQLString(String alias)
		{
			return getFieldName(alias) + getComparator() + "? ";
		}
		abstract protected String getComparator();
	}
	static class EqCriterion extends SingleValueCriterion
	{
		public EqCriterion(String name, Object value)
		{
			super(name, value);
		}
		protected String getComparator() { return "="; }
	}
	
	static class NotNullCriterion extends SingleFieldCriterion
	{
		public NotNullCriterion(String name)
		{
			super(name);
		}
		public String toSQLString(String alias)
		{
			return getFieldName(alias) + " is not null";
		}
	}
	
	public static Criterion eq(String name, Object value)
	{
		return new EqCriterion(name, value);
	}

	public static Criterion notNull(String name)
	{
		return new NotNullCriterion(name);
	}
}
