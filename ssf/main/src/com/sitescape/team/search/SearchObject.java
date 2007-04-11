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
package com.sitescape.team.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import com.sitescape.team.lucene.SsfQueryAnalyzer;

public class SearchObject {//implements Serializable {

	protected Log logger = LogFactory.getLog(getClass());
	private SortField[] sortBy = null;
	private String queryString = null;
	
	// QueryParser is not thread-safe, let try thread local variable, it should be fine
	private static ThreadLocal queryParser = new ThreadLocal(); 
	
	/**
	 * 
	 */
	public SearchObject() {
		super();
		if (queryParser.get() == null) {
			logger.debug("QueryParser instantiating new QP");
			QueryParser qp = new QueryParser(BasicIndexUtils.ALL_TEXT_FIELD,new SsfQueryAnalyzer());
			qp.setDefaultOperator(QueryParser.AND_OPERATOR);
			queryParser.set(qp);
		}
	}
	
	/**
	 * @return Returns the queryString.
	 */
	public String getQueryString() {
		return queryString;
	}
	/**
	 * @param queryString The queryString to set.
	 */
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	/**
	 * @return Returns the sortBy.
	 */
	public Sort getSortBy() {
		if (sortBy == null)
			return null;
		return new Sort(sortBy);
	}
	/**
	 * @param sortBy The sortBy to set.
	 */
	public void setSortBy(SortField[] sortBy) {
		this.sortBy = sortBy;
	}

	/**
	 * @return Returns the query.
	 */
	public synchronized Query getQuery() {
		try {
			long startTime = System.currentTimeMillis();
			Query retQ = ((QueryParser)queryParser.get()).parse(queryString);
			long endTime = System.currentTimeMillis();
			return retQ;
		} catch (ParseException pe){ 
			System.out.println("Parser exception: "+pe+" queryString in parser: "+queryString);
			return new BooleanQuery();}
	}

}

