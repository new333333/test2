package com.sitescape.team.search;

import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.search.SortField;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.User;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.util.CollectionUtil;
import com.sitescape.team.util.SPropsUtil;

public class QueryBuilder {

	public static final String FIELD_NAME_ATTRIBUTE = "fieldname";

	public static final String EXACT_PHRASE_ATTRIBUTE = "exactphrase";

	public static final String NEAR_ATTRIBUTE = "near";

	public static final String INCLUSIVE_ATTRIBUTE = "inclusive";

	public static final String DISTANCE_ATTRIBUTE = "distance";

	public static final String ASCENDING_ATTRIBUTE = "ascending";

	public static final String TAG_NAME_ATTRIBUTE = "tagname";

	public static final String QUERY_ELEMENT = "QUERY";

	public static final String AND_ELEMENT = "AND";

	public static final String OR_ELEMENT = "OR";

	public static final String LIKE_ELEMENT = "LIKE";

	public static final String NOT_ELEMENT = "NOT";

	public static final String SORTBY_ELEMENT = "SORTBY";

	public static final String RANGE_ELEMENT = "RANGE";

	public static final String RANGE_START = "START";

	public static final String RANGE_FINISH = "FINISH";

	public static final String USERACL_ELEMENT = "USERACL";

//	public static final String GROUP_VISIBILITY_ELEMENT = "GROUPVIS";

//	public static final String GROUP_VISIBILITY_ATTRIBUTE = "visibility";

	public static final String RELATIVE_DATE_RANGE_ELEMENT = "DATERANGE";

	public static final String PAST_RANGE_ATTRIBUTE = "past";

	public static final String RANGE_DAYCOUNT_ATTRIBUTE = "dayCount";

	public static final String PERSONALTAGS_ELEMENT = "PERSONALTAGS";

	public static final String FIELD_ELEMENT = "FIELD";

	public static final String FIELD_TERMS_ELEMENT = "TERMS";

	public static final String ASCENDING_TRUE = "TRUE";

	public static final String INCLUSIVE_TRUE = "TRUE";

	public static final String EXACT_PHRASE_TRUE = "TRUE";

	public static final String TAG_ELEMENT = "TAG";

	private static final long DAYMILLIS = 1000 * 60 * 60 * 24;

	private Set principalIds;

	private QueryBuilder() {
	}

	public QueryBuilder(Set principalIds) {
		this.principalIds = principalIds;
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
		for (Iterator i = root.attributeIterator(); i.hasNext();) {
			//String attribute = (String)i.next();
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
		/*
		 * 	For testing only	
		 QueryParser qp = new QueryParser("contents", new WhitespaceAnalyzer());
		 
		 try {
		 Query  query = qp.parse(searchObject.getQueryString());
		 System.out.println("Query is: " + query.toString());
		 Sort sort = new Sort(searchObject.getSortBy());
		 System.out.println("Sort is: " + sort.toString());
		 } catch (ParseException pe) { System.out.println("ParseException thrown, Query was: " + searchObject.getQuery() + " Error was: " + pe.toString());}
		 */

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
						qString += parseElement((Element) node, operator, so);
						if (j < (elemCount - 1)) {
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
			qString += "( NOT (";
			qString += parseElement((Element) node, operator, so);
			qString += "))";
		} else if (operator.equals(LIKE_ELEMENT)) {
			List elements = element.elements();
			if (elements.size() > 1) {
				// Only one at a time
				System.out.println("Problem in the LIKE element");
			}
			Node node = (Node) elements.get(0);
			qString += parseElement((Element) node, operator, so);
			qString += "~";
		} else if (operator.equals(SORTBY_ELEMENT)) {
			processSORTBY(element, so);
		} else if (operator.equals(RANGE_ELEMENT)) {
			qString += "(" + processRANGE(element) + ")";
		} else if (operator.equals(USERACL_ELEMENT)) {
			return qString;
		} else if (operator.equals(PERSONALTAGS_ELEMENT)) {
			qString += "(" + processPERSONALTAGS(element) + ")";
		} else if (operator.equals(RELATIVE_DATE_RANGE_ELEMENT)) {
			qString += "(" + processDATERANGE(element) + ")";
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
				fields[i] = new SortField(child.getText(), descending);
			}
		}
		so.setSortBy(fields);
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

	private String processDATERANGE(Element element) {

		String retStr = "";
		boolean past = true;
		int count = 1;

		String pastText = element.attributeValue(PAST_RANGE_ATTRIBUTE);
		String countText = element.attributeValue(RANGE_DAYCOUNT_ATTRIBUTE);

		if (pastText.equalsIgnoreCase("false"))
			past = false;

		count = new Integer(countText).intValue();

		Date dateToday = new Date();
		String today = EntityIndexUtils.formatDayString(dateToday);
		long millis = dateToday.getTime();
		long offset = count * DAYMILLIS;

		if (past)
			offset *= -1;

		millis += offset;

		Date rangeDate = new Date(millis);

		String otherDate = EntityIndexUtils.formatDayString(rangeDate);

		if (past)
			retStr += EntityIndexUtils.MODIFICATION_DAY_FIELD + ":[ "
					+ otherDate + " TO " + today + " ]";
		else
			retStr += EntityIndexUtils.MODIFICATION_DAY_FIELD + ":[ " + today
					+ " TO " + otherDate + " ]";

		return retStr;
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
			termText = new String(fieldName + ":[ " + startText + " TO "
					+ finishText + " ]");
		else
			termText = new String(fieldName + ":{ " + startText + " TO "
					+ finishText + " }");

		return termText;
	}


	private String getAclClause() {

		String qString = "";
		//if this is the super user, then don't add any acl controls.
		User user = RequestContextHolder.getRequestContext().getUser();
		
		if (user.isSuper())
			return qString;

		
		/*
		 * if widen, then acl query is: (folderACL:1,2,3 AND entryAcl:all,1,2,3)
		 * 
		 * else ((folderAcl:1,2,3 AND entryAcl:all) OR (entryAcl:1,2,3))
		 */
		boolean widen = SPropsUtil.getBoolean(SPropsUtil.WIDEN_ACCESS, false);
		// folderAcl:1,2,3...
		qString += "(((";
		boolean first = true;
		for (Iterator i = principalIds.iterator(); i.hasNext();) {
			if (!first) {
				qString += " OR";
			}
			qString += " " + BasicIndexUtils.FOLDER_ACL_FIELD + ":" + i.next();
			first = false;
		}
		qString += ") AND ";
		if (widen) {
			// entryAcl:all
			qString += "( " + BasicIndexUtils.ENTRY_ACL_FIELD + ":"
					+ BasicIndexUtils.READ_ACL_ALL + " ))";
			qString += " OR (";
			// OR entryAcl:1,2,3
			first = true;
			for (Iterator i = principalIds.iterator(); i.hasNext();) {
				if (!first) {
					qString += " OR";
				}
				qString += " " + BasicIndexUtils.ENTRY_ACL_FIELD + ":"
						+ i.next();
				first = false;
			}
			qString += ")";
		} else {

			qString += "( " + BasicIndexUtils.ENTRY_ACL_FIELD + ":"
					+ BasicIndexUtils.READ_ACL_ALL;
			for (Iterator i = principalIds.iterator(); i.hasNext();) {

				qString += " OR " + BasicIndexUtils.ENTRY_ACL_FIELD + ":"
						+ i.next();
			}
			qString += "))";
		}
		qString += ")";
		return qString;
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
