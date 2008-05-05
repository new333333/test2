/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.search;

import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.SortField;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.Application;
import com.sitescape.team.domain.User;
import com.sitescape.team.lucene.LanguageTaster;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SpringContextUtil;

public class QueryBuilder {

	public static final String FIELD_NAME_ATTRIBUTE = "fieldname";

	public static final String EXACT_PHRASE_ATTRIBUTE = "exactphrase";

	public static final String NEAR_ATTRIBUTE = "near";

	public static final String INCLUSIVE_ATTRIBUTE = "inclusive";

	public static final String DISTANCE_ATTRIBUTE = "distance";

	public static final String ASCENDING_ATTRIBUTE = "ascending";

	public static final String TAG_NAME_ATTRIBUTE = "tagname";
	
	public static final String LANGUAGE_ATTRIBUTE = "language";

	public static final String QUERY_ELEMENT = "QUERY";

	public static final String AND_ELEMENT = "AND";

	public static final String OR_ELEMENT = "OR";

	public static final String NOT_ELEMENT = "NOT";

	public static final String SORTBY_ELEMENT = "SORTBY";

	public static final String RANGE_ELEMENT = "RANGE";

	public static final String RANGE_START = "START";

	public static final String RANGE_FINISH = "FINISH";

	public static final String PERSONALTAGS_ELEMENT = "PERSONALTAGS";

	public static final String FIELD_ELEMENT = "FIELD";

	public static final String FIELD_TERMS_ELEMENT = "TERMS";

	public static final String LANGUAGE_ELEMENT = "LANGUAGE";
	
	public static final String ASCENDING_TRUE = "TRUE";

	public static final String INCLUSIVE_TRUE = "TRUE";

	public static final String EXACT_PHRASE_TRUE = "TRUE";

	public static final String TAG_ELEMENT = "TAG";

	private static final String DEFAULT = LanguageTaster.DEFAULT;
	
	private static final String TEAM_PREFIX=BasicIndexUtils.TEAM_ACL_FIELD + ":";
	private static final String FOLDER_PREFIX=BasicIndexUtils.FOLDER_ACL_FIELD + ":";
	private static final String ENTRY_PREFIX=BasicIndexUtils.ENTRY_ACL_FIELD + ":";
	private static final String ENTRY_ALL=ENTRY_PREFIX+BasicIndexUtils.READ_ACL_ALL;
	private static final String BINDER_OWNER_PREFIX=BasicIndexUtils.BINDER_OWNER_ACL_FIELD + ":";

	private Set userPrincipals;
	private Set applicationPrincipals;

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

	protected ProfileDao getProfileDao() {
		return (ProfileDao)SpringContextUtil.getBean("profileDao");
	}

	public SearchObject buildQuery(Document domQuery) {
		return buildQuery(domQuery, false);
	}

	public SearchObject buildQuery(Document domQuery, boolean ignoreAcls) {
		SearchObject so = new SearchObject();

		Element root = domQuery.getRootElement();
		if (!root.getText().equals(QUERY_ELEMENT)) {
			//return "Bad Query Dom Object";
		}

		parseRootElement(root, so);

		// add acl check to every query. (If it's the superuser doing this query, then this clause
		// will return the empty string.
		
		if (ignoreAcls) return so;
		
		String acls = getAclClause();
		if (acls.length() != 0) {
			String q = so.getQueryString();
			if (q.equalsIgnoreCase("(  )"))
				q = "";
			if (q.length() > 0)
				q += "AND ";
			q += acls;
			so.setQueryString(q);
		}
		
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
				fields[i] = new SortField(child.getText(), SortField.STRING, descending);
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
				ptagString += BasicIndexUtils.ACL_TAG_FIELD
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
		
		//if this is the super user, but not a remote application, then don't add any acl controls.
		
		User user = RequestContextHolder.getRequestContext().getUser();
		if (user.isSuper() && applicationPrincipals == null) return "";

		String clause = getAclClauseForIds(userPrincipals, user.getId());
		if(applicationPrincipals != null) {
			String otherClause = getAclClauseForIds(applicationPrincipals, null);
			clause = clause + " AND " + otherClause;
		}
		return clause;
	}

	private String getAclClauseForIds(Set principalIds, Long userId)
	{
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
								FOLDER_PREFIX + BasicIndexUtils.READ_ACL_TEAM + " AND " +
								"(" + idField(principalIds, TEAM_PREFIX) + "))");
			if(userId != null) {
				qString.append(" OR (" + ENTRY_ALL  + " AND " +						// OR (entryAcl:all AND folderAcl:own AND bOwnerAcl:<user>)
						FOLDER_PREFIX + BasicIndexUtils.READ_ACL_BINDER_OWNER + " AND " +
						BINDER_OWNER_PREFIX + userId.toString() + ")");
			}
			qString.append(" OR (" + idField(principalIds, ENTRY_PREFIX) + ")"); //OR (entryAcl:1 OR entryAcl:2)
			qString.append(" OR (" + ENTRY_PREFIX + BasicIndexUtils.READ_ACL_TEAM + " AND " + //OR (entryAcl:team AND (teamAcl:1 OR teamAcl:2))
								"(" + idField(principalIds, TEAM_PREFIX) + "))");
			qString.append(")");
		} else {
			qString.append("(");
			qString.append("((" + idField(principalIds, FOLDER_PREFIX) + ")"); //((folderAcl:1 OR folderAcl:2)
			qString.append(" OR ");
			qString.append("(" + FOLDER_PREFIX + BasicIndexUtils.READ_ACL_TEAM + " AND " + //OR (folderAcl:team AND (teamAcl:1 OR teamAcl:2))
					"(" + idField(principalIds, TEAM_PREFIX) + "))");
			if(userId != null) {
				qString.append(" OR (" + FOLDER_PREFIX + BasicIndexUtils.READ_ACL_BINDER_OWNER + " AND " + //OR (folderAcl:own AND bOwnerAcl:<user>)
						BINDER_OWNER_PREFIX + userId.toString() + ")");
			}
			qString.append(") AND (");												//) AND (
			qString.append("(" + ENTRY_ALL + " OR " +						//(entryAcl:all OR entryAcl:1 OR entryAcl:2)
						idField(principalIds, ENTRY_PREFIX) + ")");
			qString.append(" OR ");
			qString.append("("  + ENTRY_PREFIX + BasicIndexUtils.READ_ACL_TEAM + " AND " + //OR (entryAcl:team AND (teamAcl:1 OR teamAcl:2))
					"(" + idField(principalIds, TEAM_PREFIX) + "))");
			qString.append("))");
		}
		return qString.toString();
	}
	private String idField(Collection<Long>ids, String prefix) {
		StringBuffer buf = new StringBuffer("");
		boolean first = true;
		for (Long id:ids) {
			if (!first) {
				buf.append(" OR ");
			}
			buf.append(prefix + id);
			first = false;
		}
		return buf.toString();
		
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
