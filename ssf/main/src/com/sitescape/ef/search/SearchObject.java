package com.sitescape.ef.search;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

public class SearchObject implements Serializable {

	protected Log logger = LogFactory.getLog(getClass());
	private SortField[] sortBy = null;
	private String queryString = null;
	private Query query = null;
	private static QueryParser qp = null;
	
	/**
	 * 
	 */
	public SearchObject() {
		super();
		//synchronized(SearchObject.class){
		if (qp == null) {
			logger.info("QueryParser instantiating new QP");
			qp = new QueryParser(BasicIndexUtils.ALL_TEXT_FIELD,new WhitespaceAnalyzer());
			qp.setDefaultOperator(QueryParser.AND_OPERATOR);
		}
		//}
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
	public Query getQuery() {
		try {
			long startTime = System.currentTimeMillis();
			Query retQ = qp.parse(queryString);
			long endTime = System.currentTimeMillis();
			logger.info("QueryParser took " + (endTime - startTime) + " milliseconds");
			return retQ;
		} catch (ParseException pe){ return new BooleanQuery();}
	}

	/**
	 * @param query The query to set.
	 */
	public void setQuery(Query query) {
		this.query = query;
	}
}

