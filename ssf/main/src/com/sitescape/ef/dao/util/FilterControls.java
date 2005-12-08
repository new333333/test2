
package com.sitescape.ef.dao.util;

/**
 * @author Janet McCann
 * Keep controls for a simple equality query.
 */
public class FilterControls implements Cloneable {

	private String [] filterNames;
	private Object [] filterValues;
	private int begin=-1;
	private int end=-1;
	private OrderBy orderBy;
	public FilterControls() {
		filterNames = new String[0];
		filterValues = new Object[0];
	}
	public FilterControls(String name, Object value) {
		filterNames = new String[]{name};
		filterValues = new Object [] {value};
	}
	public FilterControls(String[] filterNames, Object[] filterValues) {
		this.filterNames = filterNames;
		this.filterValues = filterValues;
	}
	public FilterControls(String[] filterNames, Object[] filterValues, OrderBy filterOrder) {
		this.filterNames = filterNames;
		this.filterValues = filterValues;
		this.orderBy = filterOrder;
	}
	public FilterControls(String[] filterNames, Object[] filterValues, int begin, int end) {
		this.filterNames = filterNames;
		this.filterValues = filterValues;
		this.begin = begin;
		this.end = end;
	}
	public String[] getFilterNames() {
		return this.filterNames;
	}
	public void setFilterNames(String []filterNames) {
		this.filterNames = filterNames;
	}
	public void appendFilter(String alias, StringBuffer filter) {
     	if ((filterNames != null) && (filterNames.length > 0)) {
  	 		int count = filterNames.length;
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
	public String getWhereString(String alias) {
		StringBuffer where = new StringBuffer();
    	if ((filterNames != null) && (filterNames.length > 0)) {
    		 String name;
    		 int count=filterNames.length;
    		 for (int i=0; i<count; ++i) {
   	 			if (i > 0) where.append(" and ");
   	 			name = filterNames[i];
   	 			int pos = name.lastIndexOf('(');
   	 			if (pos == -1)
   	 				where.append(alias + "." + name + "=? ");
   	 			else {
   	 				++pos;
   	 			where.append(name.substring(0, pos) + alias + "." + name.substring(pos, name.length()) + "=? ");
   	 			}
   	 		}
    	}
    	return where.toString();
  
	}
	public Object[] getFilterValues() {
		return this.filterValues;
	}
	public void setFilterValues(Object []filterValues) {
		this.filterValues = filterValues;
	}
	public int getBeginPos() {
		return begin;
	}
	public void setBeginPos(int begin) {
		this.begin = begin;
	}
	public int getEndPos() {
		return end;
	}
	public void setEndPos(int end) {
		this.end = end;
	}
	public OrderBy getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(OrderBy orderBy) {
		this.orderBy = orderBy;
	}

}
