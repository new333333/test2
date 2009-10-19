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

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.search.SortField;
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
import org.kablink.teaming.lucene.LanguageTaster;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.search.Constants;


import static org.kablink.util.search.Constants.*;

public class QueryBuilder {

	private static final String DEFAULT = LanguageTaster.DEFAULT;
	
	private static final String TEAM_PREFIX=Constants.TEAM_ACL_FIELD + ":";
	private static final String FOLDER_PREFIX=Constants.FOLDER_ACL_FIELD + ":";
	private static final String ENTRY_PREFIX=Constants.ENTRY_ACL_FIELD + ":";
	private static final String ENTRY_ALL=ENTRY_PREFIX+Constants.READ_ACL_ALL;
	private static final String ENTRY_ALL_USERS=ENTRY_PREFIX+Constants.READ_ACL_ALL_USERS;
	private static final String BINDER_OWNER_PREFIX=Constants.BINDER_OWNER_ACL_FIELD + ":";

	private Set userPrincipals;
	private Set applicationPrincipals;
	
	private static Log logger = LogFactory.getLog(QueryBuilder.class);

	private QueryBuilder() {
	}

	public QueryBuilder(boolean useAcls) {
		if(useAcls) {
			this.userPrincipals = getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser());
			Application app = RequestContextHolder.getRequestContext().getApplication();
			if(app != null && !app.isTrusted()) {
				this.applicationPrincipals = getProfileDao().getPrincipalIds(app);
			} else {
				this.applicationPrincipals = null;
			}
		} else {
			this.userPrincipals = null;
			this.applicationPrincipals = null;
		}
	}

	public QueryBuilder(Long asUserId) {
		User asUser = getProfileDao().loadUser(asUserId, RequestContextHolder.getRequestContext().getZoneId());
		this.userPrincipals = (getProfileDao().getPrincipalIds(asUser));
		this.applicationPrincipals = null;
	}

	protected ProfileDao getProfileDao() {
		return (ProfileDao)SpringContextUtil.getBean("profileDao");
	}

	protected AccessControlManager getAccessControlManager() {
		return (AccessControlManager)SpringContextUtil.getBean("accessControlManager");
	}

	protected CoreDao getCoreDao() {
		return (CoreDao)SpringContextUtil.getBean("coreDao");
	}

	public SearchObject buildQuery(Document domQuery) {
		return buildQuery(domQuery, false);
	}
	
	public SearchObject buildQueryDeleted(Document domQuery) {
		return buildQuery(domQuery, false, null, true);
	}

	public SearchObject buildQuery(Document domQuery, Long asUserId) {
		return buildQuery(domQuery, false, asUserId, false);
	}

	public SearchObject buildQuery(Document domQuery, boolean ignoreAcls) {
		return buildQuery(domQuery, ignoreAcls, null, false);
	}

	public SearchObject buildQuery(Document domQuery, boolean ignoreAcls, Long asUserId, boolean deleted) {
		SearchObject so = new SearchObject();

		Element root = domQuery.getRootElement();
		if (!root.getText().equals(QUERY_ELEMENT)) {
			//return "Bad Query Dom Object";
		}
		
		String lang = root.attributeValue(LANGUAGE_ATTRIBUTE);
		if ((lang == null) || (lang.equals(""))) lang = DEFAULT;
		so.setLanguage(lang);
		
		parseRootElement(root, so);
		
		//If searching as a different user, add in the acl for that user
		if (asUserId != null) {
			QueryBuilder aclQ = new QueryBuilder(asUserId);
			String acls = getAclClauseForIds(aclQ.userPrincipals, asUserId);
			if (acls.length() != 0) {
				String q = so.getQueryString();
				if (q.equalsIgnoreCase("(  )"))
					q = "";
				if (q.length() > 0)
					q += "AND ";
				q += acls;
				so.setQueryString(q);
			}
		}


		
		String q = so.getQueryString();
		// add acl check to every query. (If it's the superuser doing this query, then this clause
		// will return the empty string.
		
		if (!ignoreAcls) { 
			String acls = getAclClause();
			if (acls.length() != 0) {
				q = so.getQueryString();
				if (q.equalsIgnoreCase("(  )"))
					q = "";
				if (q.length() > 0)
					q += "AND ";
				q += acls;
				so.setQueryString(q);
			}
		}
		
		// add deleted clause to every query.  Check to see if the deleted option was passed in
		if (q.equalsIgnoreCase("(  )"))
			q = " "; // if it's an empty clause - delete it
		if (q.length() > 0)
			q += " AND ";  // if there's a clause there, then AND this to it
		String deletedClause = getDeletedClause(deleted);
		q += deletedClause;
		so.setQueryString(q);
		
		if(logger.isDebugEnabled())
			logger.debug(org.kablink.teaming.util.Constants.NEWLINE + 
					"XML query =>" + 
					org.kablink.teaming.util.Constants.NEWLINE + 
					domQuery.asXML() + 
					org.kablink.teaming.util.Constants.NEWLINE + 
					"Lucene query =>" +
					org.kablink.teaming.util.Constants.NEWLINE + 
					so.getQueryString());
					
		return so;
	}

	private void parseRootElement(Element element, SearchObject so) {
		String qString = "";

		for (Iterator i = element.elementIterator(); i.hasNext();) {
			Element elem = (Element) i.next();

			String operator = elem.getName();
			qString += parseElement(elem, operator, so);

		}
		so.setQueryString(qString);
	}

	private String parseElement(Element element, String op, SearchObject so) {

		String qString = "";
		String resString = "";

		String operator = element.getName();

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
						System.out
								.println("DOM TREE is not properly formatted!");
					}
				}
			}
			qString += " )";
		} else if (operator.equalsIgnoreCase(NOT_ELEMENT)) {
			List elements = element.elements();
			if (elements.size() > 1) {
				// error, only one term can be NOT'ed at a time
				System.out.println("Problem in the NOT element");
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
		} else if (operator.equals(null)) {
			return qString;
		}
		return qString;
	}

	private String processFIELD(Element element) {

		boolean exact = false;
		String termText = "";

		String fieldName = element.attributeValue(FIELD_NAME_ATTRIBUTE);
		String exactPhrase = element.attributeValue(EXACT_PHRASE_ATTRIBUTE);
		String nearText = element.attributeValue(NEAR_ATTRIBUTE);

		if ((exactPhrase != null)
				&& (exactPhrase.equalsIgnoreCase(EXACT_PHRASE_TRUE)))
			exact = true;
		else
			exact = false;

		List children = element.elements();
		Node child = (Node) children.get(0);
		if (child.getName().equalsIgnoreCase(FIELD_TERMS_ELEMENT)) {
			if (exact) {
				termText = fieldName + ":\"" + child.getText() + "\"";
				if (nearText != null) {
					termText += "~" + nearText;
				}
			} else {
				termText = fieldName + ":(" + child.getText() + ")";
			}
		}
		return termText;
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
				User user = RequestContextHolder.getRequestContext().getUser();
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
				User user = RequestContextHolder.getRequestContext().getUser();
				ptagString += Constants.ACL_TAG_FIELD
						+ ":"
						+ BasicIndexUtils.buildAclTag(tagName, user.getId()
								.toString()) + " ";
			}
		}
		return ptagString;
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
			//throw error
			System.out.println("Range element must have start and finish");
		}
		for (int i = 0; i < 2; i++) {
			Node child = (Node) children.get(i);
			if (child.getName().equalsIgnoreCase(RANGE_START)) {
				startText = child.getText();
			} else if (child.getName().equalsIgnoreCase(RANGE_FINISH)) {
				finishText = child.getText();
			} else {
				//throw error
				System.out.println("Range has bad children");
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

	private String getAclClause()
	{
		//KEEP THIS AND getAclClauseForIds IN SYNC WITH ACCESSUTILS.CHECKACCESS 
		
		//if this is the super user or the synchronization agent, but not a remote application, then don't add any acl controls.
		
		User user = RequestContextHolder.getRequestContext().getUser();
		if ((user.isSuper() || 
				ObjectKeys.SYNCHRONIZATION_AGENT_INTERNALID.equals(user.getInternalId())) && 
				applicationPrincipals == null) 
			return "";

		String clause = getAclClauseForIds(userPrincipals, user.getId());
		if(applicationPrincipals != null) {
			String otherClause = getAclClauseForIds(applicationPrincipals, null);
			clause = clause + " AND " + otherClause;
		}
		return clause;
	}

	private String getAclClauseForIds(Set principalIds, Long userId)
	{
      	User user = getProfileDao().loadUser(userId, RequestContextHolder.getRequestContext().getZoneId());;
      	WorkArea zone = getCoreDao().loadZoneConfig(user.getZoneId());
		//check user can see all users
		boolean canOnlySeeGroupMembers = getAccessControlManager().testOperation(user, zone, WorkAreaOperation.ONLY_SEE_GROUP_MEMBERS);
		boolean overrideCanOnlySeeGroupMembers = getAccessControlManager().testOperation(user, zone, WorkAreaOperation.OVERRIDE_ONLY_SEE_GROUP_MEMBERS);

		StringBuffer qString = new StringBuffer();
		
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
		// folderAcl:1,2,3...
		if (widen) {
			// entryAcl:all
			qString.append("(");
			qString.append("(" + ENTRY_ALL  + " AND "); //(entryAcl:all AND
			qString.append("(" + idField(principalIds, FOLDER_PREFIX)+ "))"); //(folderAcl:1 OR folderAcl:2))
			qString.append(" OR (" + ENTRY_ALL  + " AND " +						// OR (entryAcl:all AND folderAcl:team AND (teamAcl:1 OR teamAcl:2))
								FOLDER_PREFIX + Constants.READ_ACL_TEAM + " AND " +
								"(" + idField(principalIds, TEAM_PREFIX) + "))");
			if(userId != null) {
				qString.append(" OR (" + ENTRY_ALL  + " AND " +						// OR (entryAcl:all AND folderAcl:own AND bOwnerAcl:<user>)
						FOLDER_PREFIX + Constants.READ_ACL_BINDER_OWNER + " AND " +
						BINDER_OWNER_PREFIX + userId.toString() + ")");
			}
			if (!canOnlySeeGroupMembers || overrideCanOnlySeeGroupMembers) {
				qString.append(" OR (" + ENTRY_ALL_USERS + ")"); //OR (entryAcl:allUsers)
			}
			qString.append(" OR (" + idField(principalIds, ENTRY_PREFIX) + ")"); //OR (entryAcl:1 OR entryAcl:2)
			qString.append(" OR (" + ENTRY_PREFIX + Constants.READ_ACL_TEAM + " AND " + //OR (entryAcl:team AND (teamAcl:1 OR teamAcl:2))
								"(" + idField(principalIds, TEAM_PREFIX) + "))");
			qString.append(")");
		} else {
			qString.append("(");
			qString.append("((" + idField(principalIds, FOLDER_PREFIX) + ")"); //((folderAcl:1 OR folderAcl:2)
			qString.append(" OR ");
			qString.append("(" + FOLDER_PREFIX + Constants.READ_ACL_TEAM + " AND " + //OR (folderAcl:team AND (teamAcl:1 OR teamAcl:2))
					"(" + idField(principalIds, TEAM_PREFIX) + "))");
			if(userId != null) {
				qString.append(" OR (" + FOLDER_PREFIX + Constants.READ_ACL_BINDER_OWNER + " AND " + //OR (folderAcl:own AND bOwnerAcl:<user>)
						BINDER_OWNER_PREFIX + userId.toString() + ")");
			}
			qString.append(") AND (");			//) AND (
			qString.append("(" + ENTRY_ALL);	//(entryAcl:all OR entryAcl:allUsers OR entryAcl:1 OR entryAcl:2)
			if (!canOnlySeeGroupMembers || overrideCanOnlySeeGroupMembers) {
				qString.append(" OR " + ENTRY_ALL_USERS);
			}
			qString.append(" OR " +	
						idField(principalIds, ENTRY_PREFIX) + ")");
			qString.append(" OR ");
			qString.append("("  + ENTRY_PREFIX + Constants.READ_ACL_TEAM + " AND " + //OR (entryAcl:team AND (teamAcl:1 OR teamAcl:2))
					"(" + idField(principalIds, TEAM_PREFIX) + "))");
			qString.append("))");
		}
		return qString.toString();
	}
	
	private String getDeletedClause(boolean searchDeleted)
	{

		StringBuffer qString = new StringBuffer();
		
		//if (searchDeleted) {
		//	qString.append("(");
		//} else {
			qString.append(" NOT ");
		//}
		qString.append(Constants.DELETED_FIELD  + ":true "); //_deleted:true
		//qString.append(")");
		
		return qString.toString();
	}
	
	private String idField(Collection<Long>ids, String prefix) {
		StringBuffer buf = new StringBuffer("");
		if (ids != null) {
			boolean first = true;
			for (Long id:ids) {
				if (!first) {
					buf.append(" OR ");
				}
				buf.append(prefix + id);
				first = false;
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
		SAXReader reader = new SAXReader();
		try {
			URL url = new URL("file:///c|/v8/query.txt");
			document = reader.read(url);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		buildQuery(document);
	}
}
