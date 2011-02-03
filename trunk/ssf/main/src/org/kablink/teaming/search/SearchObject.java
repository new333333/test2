/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.search;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.kablink.teaming.lucene.ChineseAnalyzer;
import org.kablink.teaming.lucene.analyzer.NullAnalyzer;
import org.kablink.teaming.lucene.analyzer.SsfQueryAnalyzer;
import org.kablink.teaming.lucene.analyzer.VibeQueryAnalyzer;
import org.kablink.teaming.lucene.util.LanguageTaster;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.search.Constants;


public class SearchObject {

	private static final int DEFAULT_MAX_BOOLEAN_CLAUSES = 10000;
	
	
	protected Log logger = LogFactory.getLog(getClass());
	private SortField[] sortBy = null;
	
	private String queryString = null; // old
	
	private String language = LanguageTaster.DEFAULT;
	
	private Query luceneQuery; // new
	
	// QueryParser is not thread-safe, let try thread local variable, it should be fine
	private static ThreadLocal<QueryParser> queryParser = new ThreadLocal<QueryParser>();
	private static ThreadLocal<QueryParser> queryParserARABIC = new ThreadLocal<QueryParser>();
	private static ThreadLocal<QueryParser> queryParserHEBREW = new ThreadLocal<QueryParser>();
	private static ThreadLocal<QueryParser> queryParserCJK = new ThreadLocal<QueryParser>();
	private static ThreadLocal<QueryParser> queryParserWSA = new ThreadLocal<QueryParser>();
	
	
	/**
	 * 
	 */
	public SearchObject() {
		super();
		BooleanQuery.setMaxClauseCount(SPropsUtil.getInt("lucene.max.booleans", DEFAULT_MAX_BOOLEAN_CLAUSES));
		if (queryParser.get() == null) {
			logger.debug("QueryParser instantiating new QP");
			QueryParser qp = new QueryParser(Constants.ALL_TEXT_FIELD, VibeQueryAnalyzer.getInstance());
			qp.setDefaultOperator(QueryParser.AND_OPERATOR);
			queryParser.set(qp);
			qp = new QueryParser(Constants.ALL_TEXT_FIELD, new WhitespaceAnalyzer());
			queryParserWSA.set(qp);
		}
	}
	
	/**
	 * @return Returns the queryString.
	 */
	public String getQueryStringDoNotUse() {
		return queryString;
	}
	/**
	 * @param queryString The queryString to set.
	 */
	public void setQueryStringDoNotUse(String queryString) {
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
	
	public String getLanguage() {
		return this.language;
	}

	/**
	 * @return Returns the query.
	 */
	public Query getQueryDoNotUse() {
		return parse(getParser(), queryString);
	}

	public Query parseQueryString(String qStr) {
		return parse(getParser(), qStr);
	}
	
	public Query parseQueryStringWSA(String qStr) {
		return parse(getParserWSA(), qStr);
	}
	
	public void setLuceneQuery(Query luceneQuery) {
		this.luceneQuery = luceneQuery;
	}
	
	public Query getLuceneQuery() {
		return luceneQuery;
	}
	
	private QueryParser getParserWSA() {
		return (QueryParser) queryParserWSA.get();
	}
	
	private QueryParser getParser() {
		String lang = getLanguage();
		if (lang.equalsIgnoreCase(LanguageTaster.DEFAULT))
			return (QueryParser)queryParser.get();
		else if (lang.equalsIgnoreCase(LanguageTaster.CJK)) {
			if (queryParserCJK.get() == null) {
				logger.debug("QueryParser instantiating new CJK QP");
				QueryParser qp = new QueryParser(Constants.ALL_TEXT_FIELD, getCJKAnalyzer());
				qp.setDefaultOperator(QueryParser.AND_OPERATOR);
				queryParserCJK.set(qp);
				return qp;
			} else {
				return (QueryParser)queryParserCJK.get();
			}
		} else if (lang.equalsIgnoreCase(LanguageTaster.ARABIC)) {
			if (queryParserARABIC.get() == null) {
				logger.debug("QueryParser instantiating new ARABIC QP");
				Analyzer analyzer = VibeQueryAnalyzer.getInstance();
				String aName = SPropsUtil.getString("lucene.arabic.analyzer", "");
				if (!aName.equalsIgnoreCase("")) {
					//load the arabic analyzer here
					try {
						Class arabicClass = ReflectHelper.classForName(aName);
				 		analyzer = (Analyzer)arabicClass.newInstance();
					} catch (Exception e) {
						logger.error("Could not initialize arabic analyzer class: " + e.toString());
					}
				}
				QueryParser qp = new QueryParser(Constants.ALL_TEXT_FIELD, analyzer);
				qp.setDefaultOperator(QueryParser.AND_OPERATOR);
				queryParserARABIC.set(qp);
				return qp;
			} else {
				return (QueryParser)queryParserARABIC.get();
			}
		} else {
			if (queryParserHEBREW.get() == null) {
				logger.debug("QueryParser instantiating new HEBREW QP");
				Analyzer analyzer = VibeQueryAnalyzer.getInstance();
				String aName = SPropsUtil.getString("lucene.hebrew.analyzer", "");
				if (!aName.equalsIgnoreCase("")) {
					//load the hebrew analyzer here
					try {
						Class hebrewClass = ReflectHelper.classForName(aName);
				 		analyzer = (Analyzer)hebrewClass.newInstance();
					} catch (Exception e) {
						logger.error("Could not initialize hebrew analyzer class: " + e.toString());
					}
				}
				QueryParser qp = new QueryParser(Constants.ALL_TEXT_FIELD, analyzer);
				qp.setDefaultOperator(QueryParser.AND_OPERATOR);
				queryParserHEBREW.set(qp);
				return qp;
			} else {
				return (QueryParser)queryParserHEBREW.get();
			}
		}
	}
	
	private Query parse(QueryParser parser, String qStr) {
		try {
			long startTime = System.currentTimeMillis();
			Query retQ = parser.parse(qStr);
			long endTime = System.currentTimeMillis();
			if(logger.isDebugEnabled())
				logger.debug("QueryParser instantiating new QP took " + (endTime - startTime) + " milliseconds");
			return retQ;
		} catch (ParseException pe){ 
			logger.warn("Parser exception, can't parse: " + qStr);
			return new BooleanQuery();}	
	}
	
	private Analyzer getCJKAnalyzer() {
		PerFieldAnalyzerWrapper retAnalyzer = new PerFieldAnalyzerWrapper(new NullAnalyzer());
		retAnalyzer.addAnalyzer(Constants.ALL_TEXT_FIELD, new ChineseAnalyzer());
		retAnalyzer.addAnalyzer(Constants.TITLE_FIELD, new ChineseAnalyzer());
		retAnalyzer.addAnalyzer(Constants.DESC_TEXT_FIELD, new ChineseAnalyzer());
		return retAnalyzer;
	}
}

