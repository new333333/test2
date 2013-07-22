/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.dao.util;

import java.util.List;
import java.util.ArrayList;

/**
 * Keep controls for a simple equality query.
 * 
 * @author Janet McCann
 */
@SuppressWarnings("unchecked")
public class FilterControls implements Cloneable {
	private List<Object> filterValues = new ArrayList();
	private List<Criterion> criteria = new ArrayList<Criterion>();
	private OrderBy orderBy;
	boolean doCheck=true;
	public FilterControls() {
	}
	public FilterControls(String name, Object value)
	{
		add(name, value);
	}
	//allow callers to disable zonecheck done at dao layer.  
	//This just reduces the need for more indexing by zoneId, when another id already restricts the search
	public void setZoneCheck(boolean doCheck) {
		this.doCheck = doCheck;
	}
	public boolean isZoneCheck() {
		return doCheck;
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
	public void add(String name, Object value, int pos) {
		Criterion crit = Restrictions.eq(name, value); 
		add(crit, pos);
	}
	public void addNotEQ(String name, Object value) {
		Criterion crit = Restrictions.notEq(name, value); 
		add(crit);
	}
	public void addNotEQ(String name, Object value, int pos) {
		Criterion crit = Restrictions.notEq(name, value); 
		add(crit, pos);
	}

	/**
	 * 
	 * @param name
	 */
	public void addIsNull( String name )
	{
		Criterion crit;
		
		crit = Restrictions.isNull( name );
		add( crit );
	}
	
	public void addNotNull(String name) {
		Criterion crit = Restrictions.notNull(name);
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
	public FilterControls add(Criterion crit, int pos)
	{
		criteria.add(pos, crit);
		filterValues.addAll(pos, crit.getParameterValues());
		return this;
	}
}
