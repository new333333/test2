package com.sitescape.ef.search;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import org.dom4j.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import org.apache.lucene.search.*;
import org.apache.lucene.queryParser.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.module.folder.index.IndexUtils;

import java.net.URL;

public class QueryBuilder {
	
	public static final String FIELD_NAME_ATTRIBUTE = "fieldname";
	public static final String EXACT_PHRASE_ATTRIBUTE = "exactphrase";
	public static final String NEAR_ATTRIBUTE = "near";
	public static final String INCLUSIVE_ATTRIBUTE = "inclusive";
	public static final String DISTANCE_ATTRIBUTE = "distance";
	public static final String ASCENDING_ATTRIBUTE = "ascending";

	
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
	public static final String FIELD_ELEMENT = "FIELD";
	public static final String FIELD_TERMS_ELEMENT = "TERMS";
	public static final String ASCENDING_TRUE = "TRUE";
	public static final String INCLUSIVE_TRUE = "TRUE";
	public static final String EXACT_PHRASE_TRUE = "TRUE";

    
	public SearchObject buildQuery(Document domQuery) {
		SearchObject so = new SearchObject();
		
		Element root = domQuery.getRootElement();
		if (!root.getText().equals(QUERY_ELEMENT)) {
			//return "Bad Query Dom Object";
		}
		for (Iterator i = root.attributeIterator(); i.hasNext();){
			//String attribute = (String)i.next();
		}
		
		parseRootElement(root,so);
		//String qString = parseRootElement(root);
		
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
		
		for (Iterator i = element.elementIterator();i.hasNext();) {
			Element elem = (Element)i.next();
			
			String operator = elem.getName();
			qString += parseElement(elem,operator,so);
			
		}
		so.setQueryString(qString);
	}
	
	private String parseElement(Element element, String op, SearchObject so) {
		
		String qString = "";				

		String operator = element.getName();
			
			if (operator.equalsIgnoreCase(AND_ELEMENT) || (operator.equalsIgnoreCase(OR_ELEMENT))) {
				qString += "( ";
				List elements = element.elements();
				int elemCount = elements.size();
				
				if  (elemCount > 1) {
					for ( int j = 0; j < elemCount; j++ ) {
						Node node = (Node)elements.get(j);
						if ( node instanceof Element ) {
							qString += parseElement((Element)node, operator, so);
							if (j < (elemCount - 1)) { 
								qString += " " + operator + " ";
							}
						} else {
							System.out.println("DOM TREE is not properly formatted!");
						}
					}
				}
				qString += " )";
			}
			else if (operator.equalsIgnoreCase(NOT_ELEMENT)) {
				List elements = element.elements();
				if (elements.size() > 1) {
					// error, only one term can be NOT'ed at a time
					System.out.println("Problem in the NOT element");
				}
				Node node = (Node)elements.get(0);
				qString += "( NOT (";
				qString += parseElement((Element)node, operator, so);
				qString += "))";
			}
			else if (operator.equals(LIKE_ELEMENT)) {
				List elements = element.elements();
				if (elements.size() > 1) {
					// Only one at a time
					System.out.println("Problem in the LIKE element");
				}
				Node node = (Node)elements.get(0);
				qString += parseElement((Element)node, operator,so);
				qString += "~";
			}			
			else if (operator.equals(SORTBY_ELEMENT)) {
				processSORTBY(element,so);
			}
			else if (operator.equals(RANGE_ELEMENT)) {
				qString += "(" + processRANGE(element) + ")";
			}
			else if (operator.equals(USERACL_ELEMENT)) {
				//Always check for aclreaddef
				User user = RequestContextHolder.getRequestContext().getUser();
				Set principalIds = user.computePrincipalIds();
				qString += "(";
				qString += " " + IndexUtils.READ_DEF_ACL_FIELD + ":" + IndexUtils.READ_ACL_ALL + " ";
				for(Iterator i = principalIds.iterator(); i.hasNext();) {
					qString += " OR " + IndexUtils.READ_ACL_FIELD + ":" + i.next();
				}
				qString += ")";
			}
			else if (operator.equals(FIELD_ELEMENT)) {
				qString += processFIELD(element);
			}
			else if (operator.equals(null)) {
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
		
		if ((exactPhrase != null) && (exactPhrase.equalsIgnoreCase(EXACT_PHRASE_TRUE)))
			exact = true;
		else
			exact = false;
		
		List children = element.elements();
		Node child = (Node)children.get(0);
		if (child.getName().equalsIgnoreCase(FIELD_TERMS_ELEMENT)) {
			if (exact) {
				termText = fieldName + ":\"" + child.getText() + "\"";
			    if (nearText != null) {
			    	termText += "~" + nearText;
			    }
			} else {
				termText = fieldName + ":" + child.getText();
			}
		}
		return termText;
	}
	
	private void processSORTBY(Element element, SearchObject so) {

		boolean ascend = false;
		//SortField[] fields = null;
				
		List children = element.elements();
		int kidCount = children.size();
		SortField[] fields = new SortField[kidCount];
		for (int i = 0; i < kidCount; i++) {
			Element child = (Element)children.get(i);
			if (child.getName().equalsIgnoreCase(FIELD_NAME_ATTRIBUTE)) {
				ascend = false;
				String ascending = child.attributeValue(ASCENDING_ATTRIBUTE);
				
				if ((ascending != null) && (ascending.equalsIgnoreCase(ASCENDING_TRUE)))
					ascend = true;
				else
					ascend = false;
				fields[i] = new SortField(child.getText(),ascend);
			}
		}
		so.setSortBy(fields);
		//return fields;
	}

	private String processRANGE(Element element) {

		boolean inclusive = false;
		String termText = "";
		String startText = "";
		String finishText = "";
		
		String inclusiveText = element.attributeValue(INCLUSIVE_ATTRIBUTE);
		String fieldName = element.attributeValue(FIELD_NAME_ATTRIBUTE);
		
		if ((inclusiveText != null) && (inclusiveText.equalsIgnoreCase(INCLUSIVE_TRUE)))
			inclusive = true;
		else
			inclusive = false;
		
		List children = element.elements();
		int kidCount = children.size();
		if (kidCount != 2) {
			//throw error
			System.out.println("Range element must have start and finish");
		}
		for (int i=0; i < 2; i++) {
			Node child = (Node)children.get(i);
			if (child.getName().equalsIgnoreCase(RANGE_START)) {
				startText=child.getText();
			}
			else if (child.getName().equalsIgnoreCase(RANGE_FINISH)){
				finishText=child.getText();
			} else {
				//throw error
				System.out.println("Range has bad children");
			}
		}
		if (inclusive)
			termText = new String(fieldName + ":[ " + startText + " TO " + finishText + " ]");
		else
			termText = new String(fieldName + ":{ " + startText + " TO " + finishText + " }");
		
		return termText;
	}
	
	public void test() {
		Document document = null;
		SearchObject so = new SearchObject();
		SAXReader reader = new SAXReader();
		try {
			URL url = new URL("file:///c|/v8/query.txt");
			document = reader.read(url);
		} catch (Exception e) {System.out.println(e.toString());}
		buildQuery(document);
	}
}
