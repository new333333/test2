package com.sitescape.ef.search;

import java.io.Serializable;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;


public class SearchObject implements Serializable {

	
	private SortField[] sortBy = null;
	private String queryString = null;
	private Query query = null;
	
	/**
	 * 
	 */
	public SearchObject() {
		super();
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
		return query;
	}

	/**
	 * @param query The query to set.
	 */
	public void setQuery(Query query) {
		this.query = query;
	}
}

