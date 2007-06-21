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

package com.sitescape.team.dao.util;
import java.util.List;
import java.util.ArrayList;
/**
 * @author Janet McCann
 * Keep controls for a simple equality query.
 */
public class FilterControls implements Cloneable {

	private List<Object> filterValues = new ArrayList();
	private List<Criterion> criteria = new ArrayList<Criterion>();
	private OrderBy orderBy;
	public FilterControls() {
	}
	public FilterControls(String name, Object value)
	{
		add(name, value);
	}
	public FilterControls(String[] filterNames, Object[] filterValues) {
		for(int i = 0; i < filterNames.length; i++) {
			add(filterNames[i], filterValues[i]);
		}
	}
	public FilterControls(String[] filterNames, Object[] filterValues, OrderBy filterOrder) {
		this(filterNames, filterValues);
		this.orderBy = filterOrder;
	}
	public void add(String name, Object value) {
		Criterion crit = Restrictions.eq(name, value); 
		add(crit);
	}

	public void appendFilter(String alias, StringBuffer filter) {
	 	int count = criteria.size();
	 	if (count > 0) {
   	 		filter.append(" where ");
   	 		filter.append(getWhereString(alias));
      	}
		if (orderBy != null) filter.append( " order by " + orderBy.getOrderByClause(alias));
		
	}

	public String getOrderBy(String alias) {
		if (orderBy != null) return  " order by " + orderBy.getOrderByClause(alias);
		return null;
		
	}
	public String getFilterString(String alias) {
		StringBuffer filter = new StringBuffer();
		appendFilter(alias, filter);
		return filter.toString();
	}
	//return where string, but not "where" keyword
	protected String getWhereString(String alias) {
		StringBuffer where = new StringBuffer();
		int count=criteria.size();
   		for (int i=0; i<count; ++i) {
  			if (i > 0) where.append(" and ");
  			Criterion crit = criteria.get(i);
  			where.append(crit.toSQLString(alias));
    	}
    	return where.toString();
  
	}
	public List getFilterValues() {
		return this.filterValues;
	}
	public OrderBy getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
	}

	public FilterControls add(Criterion crit)
	{
		criteria.add(crit);
		filterValues.addAll(crit.getParameterValues());
		return this;
	}
}
