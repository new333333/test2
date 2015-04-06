/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.lucene.util.LanguageTaster;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.dao.SecurityDao;
import org.kablink.teaming.security.function.Condition;
import org.kablink.teaming.security.function.WorkAreaFunctionMembershipManager;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.util.search.Constants;

import static org.kablink.util.search.Constants.*;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings({"unchecked", "unused"})
public class QueryBuilder {
	private static final String DEFAULT = LanguageTaster.DEFAULT;
	
	private static final String TEAM_PREFIX=Constants.TEAM_ACL_FIELD + ":";
	private static final String FOLDER_PREFIX=Constants.FOLDER_ACL_FIELD + ":";
	private static final String ENTRY_PREFIX=Constants.ENTRY_ACL_FIELD + ":";
	private static final String ROOT_PREFIX=Constants.ROOT_FOLDER_ACL_FIELD + ":";
	private static final String SHARED_PREFIX=Constants.SHARED_IDS + ":";
	private static final String ENTRY_ALL=ENTRY_PREFIX+Constants.READ_ACL_ALL;
	private static final String ENTRY_ALL_GLOBAL=ENTRY_PREFIX+Constants.READ_ACL_GLOBAL;
	private static final String FOLDER_ALL_GLOBAL=FOLDER_PREFIX+Constants.READ_ACL_GLOBAL;
	private static final String TEAM_ALL_GLOBAL=TEAM_PREFIX+Constants.READ_ACL_GLOBAL;
	private static final String ENTRY_ALL_USERS=ENTRY_PREFIX+Constants.READ_ACL_ALL_USERS;
	private static final String BINDER_OWNER_PREFIX=Constants.BINDER_OWNER_ACL_FIELD + ":";
	private static final String ENTRY_OWNER_PREFIX=Constants.ENTRY_OWNER_ACL_FIELD + ":";

	private boolean useAcls;
	private boolean canSeeProfilesBinder;
	private boolean preDeleted;
	private User user;
	private Application application;
	private Set userPrincipals;
	private Set applicationPrincipals;
	
	private static Log logger = LogFactory.getLog(QueryBuilder.class);
	
	public QueryBuilder(boolean useAcls, boolean preDeleted) {
		this(useAcls, preDeleted, null);
	}

	public QueryBuilder(boolean useAcls, boolean preDeleted, Long asUserId) {
		this.useAcls = useAcls;
		this.preDeleted = preDeleted;
		if(asUserId != null) {
			this.user = getProfileDao().loadUser(asUserId, RequestContextHolder.getRequestContext().getZoneId());
			this.application = null;
		}
		else {
			this.user = RequestContextHolder.getRequestContext().getUser();
			this.application = RequestContextHolder.getRequestContext().getApplication();			
		}
		Long profileBinderId = getProfileModule().getProfileBinderId();
		this.canSeeProfilesBinder = getBinderModule().checkAccess(profileBinderId, this.user);
		if(useAcls) {
			this.userPrincipals = getProfileDao().getAllPrincipalIds(user);
			if(application != null && !application.isTrusted()) {
				this.applicationPrincipals = getProfileDao().getAllPrincipalIds(application);
			} else {
				this.applicationPrincipals = null;
			}
		} else {
			this.userPrincipals = null;
			this.applicationPrincipals = null;
		}
	}

	protected WorkspaceModule getWorkspaceModule() {
		return (WorkspaceModule)SpringContextUtil.getBean("workspaceModule");
	}

	protected ProfileModule getProfileModule() {
		return (ProfileModule)SpringContextUtil.getBean("profileModule");
	}

	protected BinderModule getBinderModule() {
		return (BinderModule)SpringContextUtil.getBean("binderModule");
	}

	protected static ProfileDao getProfileDao() {
		return (ProfileDao)SpringContextUtil.getBean("profileDao");
	}

	protected AccessControlManager getAccessControlManager() {
		return (AccessControlManager)SpringContextUtil.getBean("accessControlManager");
	}

	protected CoreDao getCoreDao() {
		return (CoreDao)SpringContextUtil.getBean("coreDao");
	}
	
	protected SecurityDao getSecurityDao() {
		return (SecurityDao)SpringContextUtil.getBean("securityDao");
	}
	
	protected WorkAreaFunctionMembershipManager getWorkAreaFunctionMembershipManager() {
		return (WorkAreaFunctionMembershipManager)SpringContextUtil.getBean("WorkAreaFunctionMembershipManager");
	}

	public SearchObject buildQuery(Document domQuery) {
		SearchObject so = new SearchObject();

		Element root = domQuery.getRootElement();
		if (!root.getText().equals(QUERY_ELEMENT)) {
			//return "Bad Query Dom Object";
		}
		
		String lang = root.attributeValue(LANGUAGE_ATTRIBUTE);
		if ((lang == null) || (lang.equals(""))) lang = DEFAULT;
		so.setLanguage(lang);
		
		//Add on the ACL clauses that filter out anything the user is not allowed to see.
		handleRootElement(root, so);
		
		if(useAcls) {
			// add acl check to every query. 
			String acls = getAclClause(so);
			so.setAclQueryStr(acls);	
		}
		
		// add preDeleted clause to every query.  Check to see if the preDeleted option was passed in
		Query lq = so.getLuceneQuery();
		if(!(lq instanceof BooleanQuery)) {
			BooleanQuery bq = new BooleanQuery();
			bq.add(lq, BooleanClause.Occur.MUST);
			lq = bq;
		}
		Query preDeletedQ = new TermQuery(new Term(Constants.PRE_DELETED_FIELD, Constants.TRUE));
		if(preDeleted)
			((BooleanQuery) lq).add(preDeletedQ, BooleanClause.Occur.MUST);
		else
			((BooleanQuery) lq).add(preDeletedQ, BooleanClause.Occur.MUST_NOT);
		so.setLuceneQuery(lq);
		
		if(logger.isDebugEnabled())
			logger.debug(org.kablink.teaming.util.Constants.NEWLINE + 
					
					"XML query =>" + 
					org.kablink.teaming.util.Constants.NEWLINE + 
					((SPropsUtil.getBoolean("querybuilder.debug.format.dom", false))? XmlUtil.asPrettyString(domQuery) : domQuery.asXML()) +
					org.kablink.teaming.util.Constants.NEWLINE + 
					
					"Lucene query =>" +
					org.kablink.teaming.util.Constants.NEWLINE + 
					so.getLuceneQuery().toString() +
					org.kablink.teaming.util.Constants.NEWLINE +
					
					"Base ACL query =>" +
					org.kablink.teaming.util.Constants.NEWLINE + 
					so.getBaseAclQueryStr() + 
					org.kablink.teaming.util.Constants.NEWLINE +
					
					"Extended ACL query =>" +
					org.kablink.teaming.util.Constants.NEWLINE + 
					so.getExtendedAclQueryStr() +
					org.kablink.teaming.util.Constants.NEWLINE +
					
					"ACL query =>" +
					org.kablink.teaming.util.Constants.NEWLINE + 
					so.getAclQueryStr()
					);
					
		return so;
	}

	private void parseRootElementDoNotUse(Element element, SearchObject so) {
		String qString = "";

		for (Iterator i = element.elementIterator(); i.hasNext();) {
			Element elem = (Element) i.next();

			String operator = elem.getName();
			qString += parseElement(elem, operator, so);

		}
		so.setQueryStringDoNotUse(qString);
	}

	private void handleRootElement(Element element, SearchObject so) {
		Query query = null;
		int count = 0;
		
		for (Iterator i = element.elementIterator(); i.hasNext();) {
			Element elem = (Element) i.next();

			String operator = elem.getName();
			Query q = handleElement(elem, so);
			
			if(q == null)
				continue;
			else
				count++;
			
			if(count == 1) {
				query = q;
			}
			else if(count == 2) {
				Query first = query;
				BooleanQuery bq = new BooleanQuery();
				bq.add(first, BooleanClause.Occur.MUST);
				bq.add(q, BooleanClause.Occur.MUST);
				query = bq;
			}
			else {
				((BooleanQuery) query).add(q, BooleanClause.Occur.MUST);
			}
		}
		logger.debug("Number of first level elements: " + count);
		if(query == null)
			query = new BooleanQuery();
		so.setLuceneQuery(query);
	}

	private String parseElement(Element element, String op, SearchObject so) {

		String qString = "";
		String resString = "";

		String operator = element.getName();

		if (operator == null)
			return qString;

		if (operator.equalsIgnoreCase(AND_ELEMENT)
				|| (operator.equalsIgnoreCase(OR_ELEMENT))) {
			qString += "( ";
			List elements = element.elements();
			int elemCount = elements.size();

			if (elemCount > 0) {
				for (int j = 0; j < elemCount; j++) {
					Node node = (Node) elements.get(j);
					if (node instanceof Element) {
						resString = parseElement((Element) node, operator, so);
						qString += resString;
						if (j < (elemCount - 1) && (resString != "")) {
							qString += " " + operator + " ";
						} else {
							if (!so.getLanguage().equalsIgnoreCase(LanguageTaster.DEFAULT))
								qString = this.fixQStringforLang(qString, operator);
						}
					} else {
						logger.error("DOM TREE is not properly formatted" +
								org.kablink.teaming.util.Constants.NEWLINE + 
								element.asXML()); 
						throw new IllegalArgumentException("Invalid query in XML");
					}
				}
			}
			qString += " )";
		} else if (operator.equalsIgnoreCase(NOT_ELEMENT)) {
			List elements = element.elements();
			if (elements.size() > 1) {
				// error, only one term can be NOT'ed at a time
				logger.error("Problem in the NOT element" +
						org.kablink.teaming.util.Constants.NEWLINE + 
						element.asXML()); 
				throw new IllegalArgumentException("Invalid query in XML");
			}
			Node node = (Node) elements.get(0);
			qString += " NOT (";
			qString += parseElement((Element) node, operator, so);
			qString += ")";
		} else if (operator.equals(SORTBY_ELEMENT)) {
			processSORTBY(element, so);
		} else if (operator.equals(LANGUAGE_ELEMENT)) {
			processLANG(element,so);
		} else if (operator.equals(RANGE_ELEMENT)) {
			qString += "(" + processRANGE(element) + ")";
		} else if (operator.equals(PERSONALTAGS_ELEMENT)) {
			qString += "(" + processPERSONALTAGS(element) + ")";
		} else if (operator.equals(FIELD_ELEMENT)) {
			qString += processFIELD(element);
		}
		return qString;
	}

	private Query handleElement(Element element, SearchObject so) {

		Query query = null;

		String operator = element.getName();
		
		if(operator == null)
			return query;

		if (operator.equalsIgnoreCase(AND_ELEMENT)
				|| (operator.equalsIgnoreCase(OR_ELEMENT))) {
			List elements = element.elements();
			int elemCount = elements.size();

			if (elemCount > 0) {
				for (int j = 0; j < elemCount; j++) {
					Node node = (Node) elements.get(j);
					if (node instanceof Element) {
						Query subQuery = handleElement((Element) node, so);
						if(subQuery == null)
							continue;
						if(query == null)
							query = new BooleanQuery();
						if(node.getName().equalsIgnoreCase(NOT_ELEMENT)) {
							if(operator.equalsIgnoreCase(AND_ELEMENT)) {
								((BooleanQuery) query).add(subQuery, BooleanClause.Occur.MUST_NOT);
							}
							else {
								logger.error("NOT must be preceded by AND" +
										org.kablink.teaming.util.Constants.NEWLINE + 
										element.asXML()); 
								throw new IllegalArgumentException("Invalid query in XML");	
							}
						}
						else { 
							((BooleanQuery) query).add(subQuery, (operator.equalsIgnoreCase(AND_ELEMENT)? BooleanClause.Occur.MUST : BooleanClause.Occur.SHOULD));
						}					
					} else {
						logger.error("DOM TREE is not properly formatted" +
								org.kablink.teaming.util.Constants.NEWLINE + 
								element.asXML()); 
						throw new IllegalArgumentException("Invalid query in XML");
					}
				}
			}
		} else if (operator.equalsIgnoreCase(NOT_ELEMENT)) {
			List elements = element.elements();
			if (elements.size() > 1) {
				// error, only one term can be NOT'ed at a time
				logger.error("Problem in the NOT element" +
						org.kablink.teaming.util.Constants.NEWLINE + 
						element.asXML()); 
				throw new IllegalArgumentException("Invalid query in XML");
			}
			Node node = (Node) elements.get(0);
			// There is no standalone query datastructure that encapsulates the negation.
			// Factoring of the negation is accomplished by adding the negated query into
			// a parent boolean query. This step is performed by the caller.
			query = handleElement((Element) node, so);
		} else if (operator.equals(SORTBY_ELEMENT)) {
			processSORTBY(element, so);
		} else if (operator.equals(LANGUAGE_ELEMENT)) {
			processLANG(element,so);
		} else if (operator.equals(RANGE_ELEMENT)) {
			query = handleRANGE(element);
		} else if (operator.equals(PERSONALTAGS_ELEMENT)) {
			query = handlePERSONALTAGS(element);
		} else if (operator.equals(FIELD_ELEMENT)) {
			query = handleFIELD(element, so);
		}
		return query;
	}

	private String processFIELD(Element element) {

		boolean exact = false;
		String termText = "";

		String fieldName = element.attributeValue(FIELD_NAME_ATTRIBUTE);
		String exactPhrase = element.attributeValue(EXACT_PHRASE_ATTRIBUTE);

		if ((exactPhrase != null)
				&& (exactPhrase.equalsIgnoreCase(EXACT_PHRASE_TRUE)))
			exact = true;
		else
			exact = false;

		List children = element.elements();
		Node child = (Node) children.get(0);
		if (child.getName().equalsIgnoreCase(FIELD_TERMS_ELEMENT)) {
			if (exact) {
				if(fieldName != null && !fieldName.equals(""))
					termText = fieldName + ":\"" + child.getText() + "\"";
				else
					termText = "\"" + child.getText() + "\"";
			} else {
				if(fieldName != null && !fieldName.equals(""))
					termText = fieldName + ":(" + child.getText() + ")";
				else
					termText = child.getText();
			}
		}
		return termText;
	}

	private Query handleFIELD(Element element, SearchObject so) {

		boolean exact;
		Query query = null;

		String fieldName = element.attributeValue(FIELD_NAME_ATTRIBUTE);
		// Deal with deprecated _allText field
		if("_allText".equals(fieldName)) 
			fieldName = null;
		
		String exactPhrase = element.attributeValue(EXACT_PHRASE_ATTRIBUTE);

		// Deal with exactphrase attribute. The name of the attribute is slightly mis-leading for
		// historical reason, but regardless of that, here's the semantics as currently defined:
		// If exactphrase=true, it means that the supplied term value is NOT parsed or analyzed,
		// and instead treated as a single term irrespective of whether the value contains spaces or not.
		// Furthermore, if exactphrase=false, then it's possible to use some additional advanced
		// search techniques. For example, you can specify phrase term by enclosing term value with
		// a pair of double quotes. Also, wildcard match is available by appending * or ? character
		// to the term.
		
		// Make exactphrase=true as the default.
		if ((exactPhrase != null)
				&& (exactPhrase.equalsIgnoreCase(EXACT_PHRASE_FALSE)))
			exact = false;
		else
			exact = true;

		List children = element.elements();
		Node child = (Node) children.get(0);
		String text = getText(child);
		if(text == null || text.equals(""))
			return null;
		if (child.getName().equalsIgnoreCase(FIELD_TERMS_ELEMENT)) {
			if (exact) {
				// For exact match, it makes no sense to provide a term without
				// specifying a field name. But when it happens, I guess we want
				// to default it to "some" field rather than throwing an error.
				if(fieldName == null || fieldName.equals(""))
					fieldName = Constants.GENERAL_TEXT_FIELD;
				query = new TermQuery(new Term(fieldName, text));
			} else {
				String queryStr;
				if(fieldName != null && !fieldName.equals(""))
					queryStr = fieldName + ":(" + text + ")";
				else
					queryStr = text;
				queryStr = queryStr.replaceAll("_allText:", "");
				query = so.parseQueryString(queryStr);
			}
		}
		return query;
	}

	private String getText(Node node) {
		String text = node.getText();
		if(text == null)
			return null;
		else
			return text.trim();
	}
	
	private void processSORTBY(Element element, SearchObject so) {

		boolean descending = true;
		//SortField[] fields = null;

		List children = element.elements();
		int kidCount = children.size();
		SortField[] fields = new SortField[kidCount];
		for (int i = 0; i < kidCount; i++) {
			Element child = (Element) children.get(i);
			if (child.getName().equalsIgnoreCase(FIELD_NAME_ATTRIBUTE)) {
				descending = false;
				String ascending = child.attributeValue(ASCENDING_ATTRIBUTE);

				if ((ascending != null)
						&& (ascending.equalsIgnoreCase(ASCENDING_TRUE)))
					descending = false;
				else
					descending = true;
		    	Locale locale = user.getLocale();
		    	fields[i] = new SortField(child.getText(), locale, descending);
				//fields[i] = new SortField(child.getText(), SortField.AUTO, descending);
			}
		}
		so.setSortBy(fields);
		//return fields;
	}
	
	private void processLANG(Element element, SearchObject so) {
		String lang = element.attributeValue(LANGUAGE_ATTRIBUTE);
		if (lang.equals("")) lang = DEFAULT;
		so.setLanguage(lang);
		//return fields;
	}

	private String processPERSONALTAGS(Element element) {
		String ptagString = "";

		List children = element.elements();
		int kidCount = children.size();

		for (int i = 0; i < kidCount; i++) {
			Element child = (Element) children.get(i);
			if (child.getName().equalsIgnoreCase(TAG_ELEMENT)) {
				String tagName = child.attributeValue(TAG_NAME_ATTRIBUTE);
				if (tagName == null || tagName.equals(""))
					continue;
				// Always check for aclreaddef
				if (i > 0)
					ptagString += " OR ";
				/*
				 * ptagString += " " + BasicIndexUtils.ACL_TAG_FIELD + ":" +
				 * BasicIndexUtils.buildAclTag(tagName,
				 * BasicIndexUtils.READ_ACL_ALL) + " ";
				 */
				ptagString += Constants.ACL_TAG_FIELD
						+ ":"
						+ BasicIndexUtils.buildAclTag(tagName, user.getId()
								.toString()) + " ";
			}
		}
		return ptagString;
	}

	private Query handlePERSONALTAGS(Element element) {
		Query query = null;

		List children = element.elements();
		int kidCount = children.size();

		for (int i = 0; i < kidCount; i++) {
			Element child = (Element) children.get(i);
			if (child.getName().equalsIgnoreCase(TAG_ELEMENT)) {
				String tagName = child.attributeValue(TAG_NAME_ATTRIBUTE);
				if (tagName == null || tagName.equals(""))
					continue;

				TermQuery tQuery = new TermQuery(new Term(Constants.ACL_TAG_FIELD, BasicIndexUtils.buildAclTag(tagName, user.getId().toString())));
				
				if(query == null) { // This is first term query
					query = tQuery;
				}
				else { // This is second or subsequent term query. We need to OR them.
					if(query instanceof TermQuery) {
						// This is second term query.
						Query firstQuery = query;
						BooleanQuery bQuery = new BooleanQuery();
						bQuery.add(firstQuery, BooleanClause.Occur.SHOULD);
						query = bQuery;
					}
					((BooleanQuery) query).add(tQuery, BooleanClause.Occur.SHOULD);
				}
			}
		}
		return query;
	}

	private String processRANGE(Element element) {

		boolean inclusive = false;
		String termText = "";
		String startText = "";
		String finishText = "";

		String inclusiveText = element.attributeValue(INCLUSIVE_ATTRIBUTE);
		String fieldName = element.attributeValue(FIELD_NAME_ATTRIBUTE);

		if ((inclusiveText != null)
				&& (inclusiveText.equalsIgnoreCase(INCLUSIVE_TRUE)))
			inclusive = true;
		else
			inclusive = false;

		List children = element.elements();
		int kidCount = children.size();
		if (kidCount != 2) {
			logger.error("Range element must have start and finish" +
					org.kablink.teaming.util.Constants.NEWLINE + 
					element.asXML()); 
			throw new IllegalArgumentException("Invalid query in XML");
		}
		for (int i = 0; i < 2; i++) {
			Node child = (Node) children.get(i);
			if (child.getName().equalsIgnoreCase(RANGE_START)) {
				startText = child.getText();
			} else if (child.getName().equalsIgnoreCase(RANGE_FINISH)) {
				finishText = child.getText();
			} else {
				logger.error("Range has bad children" +
						org.kablink.teaming.util.Constants.NEWLINE + 
						element.asXML()); 
				throw new IllegalArgumentException("Invalid query in XML");
			}
		}
		if (inclusive)
			termText = new String(fieldName + ":[ \"" + startText + "\" TO \""
					+ finishText + "\" ]");
		else
			termText = new String(fieldName + ":{ \"" + startText + "\" TO \""
					+ finishText + "\" }");

		return termText;
	}

	private Query handleRANGE(Element element) {

		boolean inclusive = false;
		String termText = "";
		String startText = "";
		String finishText = "";

		String inclusiveText = element.attributeValue(INCLUSIVE_ATTRIBUTE);
		String fieldName = element.attributeValue(FIELD_NAME_ATTRIBUTE);

		if ((inclusiveText != null)
				&& (inclusiveText.equalsIgnoreCase(INCLUSIVE_TRUE)))
			inclusive = true;
		else
			inclusive = false;

		List children = element.elements();
		int kidCount = children.size();
		if (kidCount != 2) {
			logger.error("Range element must have start and finish" +
					org.kablink.teaming.util.Constants.NEWLINE + 
					element.asXML()); 
			throw new IllegalArgumentException("Invalid query in XML");
		}
		for (int i = 0; i < 2; i++) {
			Node child = (Node) children.get(i);
			if (child.getName().equalsIgnoreCase(RANGE_START)) {
				startText = child.getText();
			} else if (child.getName().equalsIgnoreCase(RANGE_FINISH)) {
				finishText = child.getText();
			} else {
				logger.error("Range has bad children" +
						org.kablink.teaming.util.Constants.NEWLINE + 
						element.asXML()); 
				throw new IllegalArgumentException("Invalid query in XML");
			}
		}
		
		// Currently, we have only a single boolean flag for both ends. We might want to
		// extend this in the future to allow for different value for each end.
		return new TermRangeQuery(fieldName, startText, finishText, inclusive, inclusive);
	}

	public String buildAclClause() {
		return getAclClause(null);
	}
	
	private String getAclClause(SearchObject so)
	{
		//KEEP THIS AND getAclClauseForIds IN SYNC WITH ACCESSUTILS.CHECKACCESS 
		
		//if this is the super user or the synchronization agent, but not a remote application, then don't add any acl controls.
		
		if ((user.isSuper() || 
				ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID.equals(user.getInternalId()) ||
				ObjectKeys.FILE_SYNC_AGENT_INTERNALID.equals(user.getInternalId())) && 
				applicationPrincipals == null) {
			return null;
		}

		StringBuilder extendedAclClause = new StringBuilder();
		getExtendedAclClauseForIds(userPrincipals, user.getId(), extendedAclClause, so);
		if(so != null)
			so.setExtendedAclQueryStr(extendedAclClause.toString());
		
		StringBuilder baseAclClause = new StringBuilder();
		getAclClauseForIds(userPrincipals, user.getId(), baseAclClause, so);
		if(applicationPrincipals != null) {
			baseAclClause.append(" AND ");
			getAclClauseForIds(applicationPrincipals, null, baseAclClause, so);
		}	
		if(so != null)
			so.setBaseAclQueryStr(baseAclClause.toString());
		
		return "(" + extendedAclClause.toString() + ") OR " + baseAclClause.toString();
	}
	
	private StringBuilder getExtendedAclClauseForIds(Set principalIds, Long userId, StringBuilder sb, SearchObject so) {
		Long allUsersGroupId = Utils.getAllUsersGroupId();
		Long allExtUsersGroupId = Utils.getAllExtUsersGroupId();
		//Add the shared entries outside of the root folder restrictions
		sb.append(idField(principalIds, SHARED_PREFIX, new ArrayList<Long>()));
		if (user.getIdentityInfo().isInternal() && !user.isShared()) {
			sb.append(" OR ").append(SHARED_PREFIX).append(String.valueOf(allUsersGroupId));
		} else if (!user.getIdentityInfo().isInternal() && !user.isShared()) {
			sb.append(" OR ").append(SHARED_PREFIX).append(String.valueOf(allExtUsersGroupId));
		}
		return sb;
	}

	private StringBuilder getAclClauseForIds(Set principalIds, Long userId, StringBuilder qString, SearchObject so)
	{
		Long allUsersGroupId = Utils.getAllUsersGroupId();
		Long allExtUsersGroupId = Utils.getAllExtUsersGroupId();
      	Set principalIds2 = new HashSet(principalIds);
      	User user = null;
      	if (userId != null) {
      		user = getProfileDao().loadUser(userId, RequestContextHolder.getRequestContext().getZoneId());
      	} else {
      		user = RequestContextHolder.getRequestContext().getUser();
      	}
      	
      	//Get the conditions that the current user passes
		List<Condition> conditions = getSecurityDao().findFunctionConditions(RequestContextHolder.getRequestContext().getZoneId());
      	List<Long> conditionsMet = getConditionsMet(conditions);
      	
		//check user can see all users
      	boolean canOnlySeeCommonGroupMembers = Utils.canUserOnlySeeCommonGroupMembers(user);
		if (canOnlySeeCommonGroupMembers) {
			if (allUsersGroupId != null && principalIds2.contains(allUsersGroupId)) {
				//This user is not allowed to see all users, so remove the AllUsers group id
				principalIds2.remove(allUsersGroupId);
			}
		}
		
		/*
		 * if widen(the default), then acl query is:
		 * access to folder ((entryAcl:all and folderAcl:1,2,3) OR (entryAcl:all and folderAcl:team and teamAcl:1,2,3) OR (entryAcl:all and folderAcl:own and bOwnerAcl:<user>) OR
		 * access to entry (entryAcl:1,2,3) OR (entryAcl:team AND teamAcl:1,2,3)) 
		 * 
		 * if !widen, then acl query is: 
		 * access to folder (((folderAcl:1,2,3) OR (folderAcl:team and teamAcl:1,2,3) OR (folderAcl:own and bOwnerAcl:<user>)) AND
		 * access to entry ((entryAcl:all,1,2,3) OR (entryAcl:team and teamAcl:1,2,3)))
		 * 
		 *  
		 */
		boolean widen = SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false);
		String folderPrincipals = idField(principalIds2, FOLDER_PREFIX, conditionsMet);
		String teamPrincipals = idField(principalIds2, TEAM_PREFIX, new ArrayList()); //Don't need condition explosion for _teamAcl
		if (principalIds.contains(allUsersGroupId)) {
			if (folderPrincipals.equals("")) {
				folderPrincipals = getConditionExp(FOLDER_PREFIX, Constants.READ_ACL_GLOBAL, conditionsMet);
			} else {
				folderPrincipals = folderPrincipals.trim() + " OR ";
				folderPrincipals += getConditionExp(FOLDER_PREFIX, Constants.READ_ACL_GLOBAL, conditionsMet);
			}
			if (teamPrincipals.equals("")) {
				teamPrincipals = getConditionExp(TEAM_PREFIX, Constants.READ_ACL_GLOBAL, conditionsMet);
			} else {
				teamPrincipals = teamPrincipals.trim() + " OR ";
				teamPrincipals += getConditionExp(TEAM_PREFIX, Constants.READ_ACL_GLOBAL, conditionsMet);
			}
		}
		String entryAll = getConditionExp(ENTRY_PREFIX, Constants.READ_ACL_ALL, conditionsMet);

		//Start with the Net Folder Root acl
		String rootPrincipals = idField(principalIds, ROOT_PREFIX, new ArrayList<Long>());
		rootPrincipals += " OR " + ROOT_PREFIX + Constants.ROOT_FOLDER_ALL;
		if (user.getIdentityInfo().isInternal() && !user.isShared()) {
			rootPrincipals += " OR " + ROOT_PREFIX + String.valueOf(allUsersGroupId);
		} else if (!user.getIdentityInfo().isInternal() && !user.isShared()) {
			rootPrincipals += " OR " + ROOT_PREFIX + String.valueOf(allExtUsersGroupId);
		}
		qString.append("((" + rootPrincipals + ") AND ");
		
		if(so != null)
			so.setNetFolderRootAclQueryStr(rootPrincipals);
		
		// folderAcl:1,2,3...
		if (widen) {
			// entryAcl:all
			qString.append("(");
			qString.append("(" + entryAll  + " AND "); //(entryAcl:all AND
			qString.append("(" + folderPrincipals + "))"); //(folderAcl:1 OR folderAcl:2))
			qString.append(" OR (" + entryAll  + " AND (" +						// OR (entryAcl:all AND folderAcl:team AND (teamAcl:1 OR teamAcl:2))
					getConditionExp(FOLDER_PREFIX, Constants.READ_ACL_TEAM, conditionsMet) + ") AND " +
					"(" + teamPrincipals + "))");
			if(userId != null) {
				qString.append(" OR (" + entryAll  + " AND (" +						// OR (entryAcl:all AND folderAcl:own AND bOwnerAcl:<user>)
						getConditionExp(FOLDER_PREFIX, Constants.READ_ACL_BINDER_OWNER, conditionsMet) + ") AND " +
						BINDER_OWNER_PREFIX + userId.toString() + ")");
				qString.append(" OR (" + getConditionExp(ENTRY_PREFIX, Constants.READ_ACL_BINDER_OWNER, conditionsMet) + " AND " +
						ENTRY_OWNER_PREFIX + userId.toString() + ")");
			}
			if (!canOnlySeeCommonGroupMembers && this.canSeeProfilesBinder) {
				qString.append(" OR (" + ENTRY_ALL_USERS + ")"); //OR (entryAcl:allUsers)
			}
			qString.append(" OR (" + idField(principalIds2, ENTRY_PREFIX, conditionsMet) + ")"); //OR (entryAcl:1 OR entryAcl:2)
			qString.append(" OR ((" + getConditionExp(ENTRY_PREFIX, Constants.READ_ACL_TEAM, conditionsMet) + ") AND " + //OR (entryAcl:team AND (teamAcl:1 OR teamAcl:2))
								"(" + teamPrincipals + "))");
			qString.append(")");
		} else {
			qString.append("(");
			qString.append("((" + folderPrincipals + ")"); //((folderAcl:1 OR folderAcl:2)
			qString.append(" OR ");
			qString.append("((" + getConditionExp(FOLDER_PREFIX, Constants.READ_ACL_TEAM, conditionsMet) + " AND " + //OR (folderAcl:team AND (teamAcl:1 OR teamAcl:2))
					"(" + teamPrincipals + "))");
			if(userId != null) {
				qString.append(" OR (" + getConditionExp(FOLDER_PREFIX, Constants.READ_ACL_BINDER_OWNER, conditionsMet) + ") AND " + //OR (folderAcl:own AND bOwnerAcl:<user>)
						BINDER_OWNER_PREFIX + userId.toString() + ")");
				qString.append(" OR (" + getConditionExp(ENTRY_PREFIX, Constants.READ_ACL_BINDER_OWNER, conditionsMet) + " AND " + //OR (entryAcl:own AND eOwnerAcl:<user>)
						ENTRY_OWNER_PREFIX + userId.toString() + ")");
			}
			qString.append(") AND (");			//) AND (
			qString.append("(" + entryAll);	//(entryAcl:all OR entryAcl:allUsers OR entryAcl:1 OR entryAcl:2)
			if (!canOnlySeeCommonGroupMembers && this.canSeeProfilesBinder) {
				qString.append(" OR " + ENTRY_ALL_USERS);
			}
			qString.append(" OR " +	
						idField(principalIds2, ENTRY_PREFIX, conditionsMet) + ")");
			qString.append(" OR ");
			qString.append("(("  + getConditionExp(ENTRY_PREFIX, Constants.READ_ACL_TEAM, conditionsMet) + ") AND " + //OR (entryAcl:team AND (teamAcl:1 OR teamAcl:2))
					"(" + teamPrincipals + "))");
			qString.append("))");
		}
		qString.append(")");
		return qString;
	}
	
	//Routine to get a list of conditions that the current user meets (if any)
	private List<Long> getConditionsMet(List<Condition> conditions) {
		List<Long> cIds = new ArrayList<Long>();
		for (Condition c : conditions) {
			if (c.evaluate()) {
				//This user matches this condition, so add it to the list
				cIds.add(c.getId());
			}
		}
		return cIds;
	}
	
	//Routine to get the conditional clauses to a global ACL string
	private String getConditionExp(String aclField, String aclString, List<Long> conditionsMet) {
		StringBuffer qString = new StringBuffer();
		qString.append(" (").append(aclField).append(aclString);
		for (Long cId : conditionsMet) {
			qString.append(" OR ").append(aclField).append(aclString)
				.append(Constants.CONDITION_ACL_PREFIX).append(String.valueOf(cId));
		}
		qString.append(") ");
		return qString.toString();
	}
	
	private String getPreDeletedClauseDoNotUse(boolean searchPreDeleted)
	{

		StringBuffer qString = new StringBuffer();

		if (!searchPreDeleted) {
			qString.append(" NOT ");
		}
		qString.append(Constants.PRE_DELETED_FIELD  + ":true "); //_preDeleted:true
		
		return qString.toString();
	}
	
	private String idField(Collection<Long>ids, String prefix, List<Long> conditionsMet) {
		StringBuffer buf = new StringBuffer("");
		if (ids != null) {
			boolean first = true;
			for (Long id:ids) {
				if (!first) {
					buf.append(" OR ");
				}
				buf.append(prefix + id);
				first = false;
				//If there are conditions that have been met, add a "c" id for each condition
				for (Long cId : conditionsMet) {
					buf.append(" OR ");
					buf.append(prefix + id + Constants.CONDITION_ACL_PREFIX + String.valueOf(cId));
					
					//TODO if more than one condition per entry gets supported, 
					//  then this code must be expanded to build all of the combinations of conditions 
					//  (e.g., for user id=1 and conditions c1 and c2, the list would be 1c1, 1c2, 1c1c2)
					//  Currently we don't support more than on condition per entry, so we don't expand this list
				}
			}
		}
		return buf.toString();
		
	}
	
	private String fixQStringforLang(String qstring, String operator) {
		String suffix = " " + operator + " ";
		if (qstring.endsWith(suffix)) {
			qstring = qstring.substring(0, qstring.length() - suffix.length());
		}
		return qstring;
	}
	public void test() {
		Document document = null;
		SAXReader reader = XmlUtil.getSAXReader();
		try {
			URL url = new URL("file:///c|/v8/query.txt");
			document = reader.read(url);
		} catch (Exception e) {
			logger.error(e.toString());
		}
		buildQuery(document);
	}
}
