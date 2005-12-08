package com.sitescape.ef.search;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

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


import java.net.URL;

public class QueryBuilder {
	
	private static final String FIELD_NAME_ATTRIBUTE = "fieldname";
	private static final String EXACT_PHRASE_ATTRIBUTE = "exactphrase";
	private static final String NEAR_ATTRIBUTE = "near";
	private static final String INCLUSIVE_ATTRIBUTE = "inclusive";
	private static final String DISTANCE_ATTRIBUTE = "distance";
	private static final String ASCENDING_ATTRIBUTE = "ascending";

	
	private static final String AND_ELEMENT = "AND";
    private static final String OR_ELEMENT = "OR";
    private static final String LIKE_ELEMENT = "LIKE";
    private static final String NOT_ELEMENT = "NOT";
    private static final String SORTBY_ELEMENT = "SORTBY";
    private static final String RANGE_ELEMENT = "RANGE";
    private SearchObject searchObject = new SearchObject();

	private SearchObject buildQuery(Document domQuery) {
		
		Element root = domQuery.getRootElement();
		if (!root.getText().equals("QUERY")) {
			//return "Bad Query Dom Object";
		}
		for (Iterator i = root.attributeIterator(); i.hasNext();){
			//String attribute = (String)i.next();
		}
		
		parseRootElement(root);
		//String qString = parseRootElement(root);
		
		return searchObject;
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
	
	private void parseRootElement(Element element) {
		String qString = "";
		
		for (Iterator i = element.elementIterator();i.hasNext();) {
			Element elem = (Element)i.next();
			
			String operator = elem.getName();
			qString += parseElement(elem,operator);
			
		}
		searchObject.setQueryString(qString);
	}
	
	private String parseElement(Element element, String op) {
		
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
							qString += parseElement((Element)node, operator);
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
				qString += parseElement((Element)node, operator);
				qString += "))";
			}
			else if (operator.equals(LIKE_ELEMENT)) {
				List elements = element.elements();
				if (elements.size() > 1) {
					// error, only one term can be NOT'ed at a time
					System.out.println("Problem in the NOT element");
				}
				Node node = (Node)elements.get(0);
				qString += parseElement((Element)node, operator);
				qString += "~";
			}			
			else if (operator.equals(SORTBY_ELEMENT)) {
				processSORTBY(element);
			}
			else if (operator.equals(RANGE_ELEMENT)) {
				qString += "(" + processRANGE(element) + ")";
			}
			else if (operator.equals("FIELD")) {
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
		
		if ((exactPhrase != null) && (exactPhrase.equalsIgnoreCase("true")))
			exact = true;
		else
			exact = false;
		
		List children = element.elements();
		Node child = (Node)children.get(0);
		if (child.getName().equalsIgnoreCase("terms")) {
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
	
	private void processSORTBY(Element element) {

		boolean ascend = false;
		//SortField[] fields = null;
				
		List children = element.elements();
		int kidCount = children.size();
		SortField[] fields = new SortField[kidCount];
		for (int i = 0; i < kidCount; i++) {
			Element child = (Element)children.get(i);
			if (child.getName().equalsIgnoreCase("fieldname")) {
				ascend = false;
				String ascending = child.attributeValue(ASCENDING_ATTRIBUTE);
				
				if ((ascending != null) && (ascending.equalsIgnoreCase("true")))
					ascend = true;
				else
					ascend = false;
				fields[i] = new SortField(child.getText(),ascend);
			}
		}
		searchObject.setSortBy(fields);
		//return fields;
	}

	private String processRANGE(Element element) {

		boolean inclusive = false;
		String termText = "";
		String startText = "";
		String finishText = "";
		
		String inclusiveText = element.attributeValue(INCLUSIVE_ATTRIBUTE);
		String fieldName = element.attributeValue(FIELD_NAME_ATTRIBUTE);
		
		if ((inclusiveText != null) && (inclusiveText.equalsIgnoreCase("true")))
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
			if (child.getName().equalsIgnoreCase("start")) {
				startText=child.getText();
			}
			else if (child.getName().equalsIgnoreCase("finish")){
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
		SAXReader reader = new SAXReader();
		try {
			URL url = new URL("file:///c|/v8/query.txt");
			document = reader.read(url);
		} catch (Exception e) {System.out.println(e.toString());}
		buildQuery(document);
	}
}
