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
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;

import com.sitescape.team.lucene.CJKAnalyzer;
import com.sitescape.team.lucene.SsfQueryAnalyzer;
import com.sitescape.team.util.LanguageTaster;
import com.sitescape.team.util.SPropsUtil;

public class SearchObject {

	private static final int DEFAULT_MAX_BOOLEAN_CLAUSES = 10000;
	
	
	protected Log logger = LogFactory.getLog(getClass());
	private SortField[] sortBy = null;
	private String queryString = null;
	private String language = LanguageTaster.DEFAULT;
	
	// QueryParser is not thread-safe, let try thread local variable, it should be fine
	private static ThreadLocal<QueryParser> queryParser = new ThreadLocal<QueryParser>();
	private static ThreadLocal<QueryParser> queryParserARABIC = new ThreadLocal<QueryParser>();
	private static ThreadLocal<QueryParser> queryParserCJK = new ThreadLocal<QueryParser>();
	
	
	/**
	 * 
	 */
	public SearchObject() {
		super();
		BooleanQuery.setMaxClauseCount(SPropsUtil.getInt("lucene.max.booleans", DEFAULT_MAX_BOOLEAN_CLAUSES));
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
	
	public void setLanguage(String lang) {
		this.language = lang;
	}
	
	private String getLanguage() {
		return this.language;
	}

	private QueryParser getParser() {
		String lang = getLanguage();
		if (lang.equalsIgnoreCase(LanguageTaster.DEFAULT))
			return (QueryParser)queryParser.get();
		else if (lang.equalsIgnoreCase(LanguageTaster.CJK)) {
			if (queryParserCJK.get() == null) {
				logger.debug("QueryParser instantiating new CJK QP");
				QueryParser qp = new QueryParser(BasicIndexUtils.ALL_TEXT_FIELD,new CJKAnalyzer());
				qp.setDefaultOperator(QueryParser.AND_OPERATOR);
				queryParserCJK.set(qp);
				return qp;
			} else {
				return (QueryParser)queryParserCJK.get();
			}
		} else {
			if (queryParserARABIC.get() == null) {
				logger.debug("QueryParser instantiating new ARABIC QP");
				Analyzer analyzer = new SsfQueryAnalyzer();
				String aName = SPropsUtil.getString("lucene.arabic.analyzer", "");
				if (!aName.equalsIgnoreCase("")) {
					//load the arabic analyzer here
					
				}
				QueryParser qp = new QueryParser(BasicIndexUtils.ALL_TEXT_FIELD, analyzer);
				qp.setDefaultOperator(QueryParser.AND_OPERATOR);
				queryParserARABIC.set(qp);
				return qp;
			} else {
				return (QueryParser)queryParserARABIC.get();
			}
		}
	}
	
	/**
	 * @return Returns the query.
	 */
	public synchronized Query getQuery() {
		try {
			long startTime = System.currentTimeMillis();
			Query retQ = getParser().parse(queryString);
			long endTime = System.currentTimeMillis();
			logger.debug("QueryParser instantiating new QP took " + (endTime - startTime) + " milliseconds");
			return retQ;
		} catch (ParseException pe){ 
			logger.info("Parser exception, can't parse: " + queryString);
			return new BooleanQuery();}
	}

}

