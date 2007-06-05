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
package com.sitescape.team.search.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.lucene.document.DateTools;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.task.TaskHelper;
import com.sitescape.team.util.LanguageTaster;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sun.star.beans.GetDirectPropertyTolerantResult;

/*********************************************************************
 * Object to hold a named search filter
 * @author Peter Hurley
 *
 */
public class SearchFilterToSearchBooleanConverter {  
	
	//Routine to convert a search filter into the form that Lucene wants 
   	public static Document convertSearchFilterToSearchBoolean(Document searchFilter, String currentBinderId) {
		//Build the search query
		Document qTree = DocumentHelper.createDocument();
		Element qTreeRootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
		String lang = LanguageTaster.DEFAULT;
		// create main AND element for all terms 
		// one terms block is for user defined filters, additional blocks are for acl definitions 
		// and kind of entry specification e.g. folder, user or workspace, it has sense only with AND
    	Element qTreeAndElement = qTreeRootElement.addElement(QueryBuilder.AND_ELEMENT);
    	    	
    	Element sfRootElement = searchFilter.getRootElement();
    	
    	Iterator filterTermsIt = sfRootElement.selectNodes(SearchFilterKeys.FilterTerms).iterator();
    	while (filterTermsIt.hasNext()) {
    		Element filterTerms = (Element) filterTermsIt.next();
    		convertFilterTerms(qTreeAndElement, filterTerms, lang, currentBinderId);
    	}
    	
    	return qTree;
	}

	private static void convertFilterTerms(Element parent, Element filterTerms, String lang, String currentBinderId) {
//		 each terms block can have information if the children should be join with AND
		// if not defined use OR as default
		String joiner = QueryBuilder.OR_ELEMENT;
		if (filterTerms.attributeValue(SearchFilterKeys.FilterAnd, "").equals(Boolean.TRUE.toString())) {
			joiner = QueryBuilder.AND_ELEMENT;
		}

		List liFilterTerms = filterTerms.selectNodes(SearchFilterKeys.FilterTerms);
		List liFilterTermsTerm = filterTerms.selectNodes("./" + SearchFilterKeys.FilterTerm);
		
		if (!liFilterTerms.isEmpty() || !liFilterTermsTerm.isEmpty()) {
			Element block = parent.addElement(joiner);
			
			Iterator filterTermsIt = liFilterTerms.iterator();
			while (filterTermsIt.hasNext()) {
	    		Element filterTermsChild = (Element)filterTermsIt.next();
	    		convertFilterTerms(block, filterTermsChild, lang, currentBinderId);
	    	}
			
			Iterator filterTermsTermIt = liFilterTermsTerm.iterator();
			while (filterTermsTermIt.hasNext()) {
	    		// add term to current block
	    		Element filterTerm = (Element) filterTermsTermIt.next();
	    		String filterType = filterTerm.attributeValue(SearchFilterKeys.FilterType, "");
	    		if (filterType.equals(SearchFilterKeys.FilterTypeSearchText)) {
	    			lang = checkLanguage(filterTerm.getText(),parent, lang);
	    			addSearchTextField(block, filterTerm.getText());
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeCreatorByName)) {
	    			lang = checkLanguage(filterTerm.getText(),parent, lang);
	    			addAuthorField(block, filterTerm.getText());
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeTags)) {
	    			lang = checkLanguage(filterTerm.getText(),parent, lang);
	    			addTagsField(block, filterTerm.getText());
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeEntryDefinition) || filterType.equals(SearchFilterKeys.FilterTypeCreatorById)) {	    			
	    			parseAndAddEntryField(block, filterTerm);
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeTopEntry)) {
	    			addTopEntryField(block);
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeWorkflow)) {
	    			parseAndAddWorkflowField(block, filterTerm);
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeFolders)) {
	    			addFolderField(block, filterTerm.attributeValue(SearchFilterKeys.FilterFolderId, ""));
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeAncestry)) {
	    			addAncestryField(block, filterTerm.attributeValue(SearchFilterKeys.FilterFolderId, ""));
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeFoldersList)) {
	    			addFoldersListField(block, filterTerm);
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeAncestriesList)) {
	    			addAncestriesListField(block, filterTerm);
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeEntryId)) {
	    			addEntryIdField(block, filterTerm.attributeValue(SearchFilterKeys.FilterEntryId, ""));
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeBinderParent)) {
	    			addBinderParentIdField(block, filterTerm.attributeValue(SearchFilterKeys.FilterBinderId, ""));    	    			
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeEntityTypes)) {
	    			parseAndAddEntityTypesField(block, filterTerm);
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeEntryTypes)) {
	    			parseAndAddEntryTypesField(block, filterTerm);    	    			
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeDocTypes)) {
	    			parseAndAddDocTypesField(block, filterTerm);
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeElement)) {
	    			addElementField(block, filterTerm);
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeCommunityTagSearch)) {
	    			lang = checkLanguage(filterTerm.getText(),parent, lang);
	    			addCommunityTagField(block, filterTerm.getText());
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypePersonalTagSearch)) {
	    			lang = checkLanguage(filterTerm.getText(),parent, lang);
	    			addPersonalTagField(block, filterTerm.getText());
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeEvent)) {
	    			addEventField(block, filterTerm);    	    			
		    	} else if (filterType.equals(SearchFilterKeys.FilterTypeDate)) {
		    		addDateRange(block, filterTerm.attributeValue(SearchFilterKeys.FilterElementName, ""), filterTerm.attributeValue(SearchFilterKeys.FilterStartDate, ""),filterTerm.attributeValue(SearchFilterKeys.FilterEndDate, ""));
		    	} else if (filterType.equals(SearchFilterKeys.FilterTypeRelative)) {
	    			String filterRelativeType = filterTerm.attributeValue(SearchFilterKeys.FilterRelativeType, "");
	    			if (filterRelativeType.equals(SearchFilterKeys.FilterTypeDate)) {
	    				createRelativeDateRange(block, new Integer(filterTerm.getTextTrim()));
	    			} else if (filterRelativeType.equals(SearchFilterKeys.FilterTypeCreatorById)) {
	    				createRelativeUser(block);
	    			} else if (filterRelativeType.equals(SearchFilterKeys.FilterTypePlace)) {
	    				createRelativePlace(block, filterTerm.getTextTrim(), currentBinderId);
	    			}
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeItemTypes)) {
	    			addItemTypesField(block, filterTerm);
	    		} else if (filterType.equals(SearchFilterKeys.FilterTypeTaskStatus)) {
	    			addTaskStatus(block, filterTerm);
	    		} 
        	}
		}
	}

	private static void createRelativeUser(Element block) {
   		Long currentUserId = RequestContextHolder.getRequestContext().getUserId();
		Element andField = block.addElement(QueryBuilder.AND_ELEMENT);
		Element field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.CREATORID_FIELD);
		Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(currentUserId.toString());
	}

	private static void createRelativeDateRange(Element block, Integer daysNumber) {
   		DateTime now = new DateTime();
   		DateTime startDate = now.minusDays(daysNumber);
   		addDateRange(block, EntityIndexUtils.MODIFICATION_DAY_FIELD, DateTools.dateToString(startDate.toDate(), DateTools.Resolution.DAY), null);
	}
   	
   	private static void createRelativePlace(Element block, String subfolders, String binderId) {
   		if (Boolean.FALSE.toString().equals(subfolders)) {
   			addFolderField(block, binderId);
   		} else {
   			addAncestryField(block, binderId);
   		}
	}

	private static void addEventField(Element block, Element filterTerm) {
		Element andField = block;
		Element orField2 = andField.addElement(QueryBuilder.OR_ELEMENT);
		Iterator itEventDate = filterTerm.selectNodes(SearchFilterKeys.FilterEventDate).iterator();
		while (itEventDate.hasNext()) {
			String eventDate = ((Element) itEventDate.next()).getText();
			if (!eventDate.equals("")) {
				Element field2 = orField2.addElement(QueryBuilder.FIELD_ELEMENT);
				field2.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.EVENT_DATES_FIELD);
				Element child2 = field2.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
				child2.setText(eventDate);
			}
		}
	}
   	
	private static String formatStartDate(String dateAsString) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Date d = null;
		try {
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.forTimeZone(user.getTimeZone()));
			DateTime date = fmt.parseDateTime(dateAsString);
			date = date.withMillisOfDay(0).withZone(DateTimeZone.UTC);
			d = date.toDate();
		} catch (Exception e) {
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withZone(DateTimeZone.forTimeZone(user.getTimeZone()));
			DateTime date = fmt.parseDateTime(dateAsString);
			date = date.withZone(DateTimeZone.UTC);
			d = date.toDate();
		}
		
		
		return DateTools.dateToString(d, DateTools.Resolution.SECOND);
	}
	
	private static String formatEndDate(String dateAsString) {
		User user = RequestContextHolder.getRequestContext().getUser();
		Date d = null;
		try {
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd").withZone(DateTimeZone.forTimeZone(user.getTimeZone()));
			DateTime date = fmt.parseDateTime(dateAsString);
			date = date.withMillisOfDay(SearchFilterKeys.MILIS_IN_THE_DAY).withZone(DateTimeZone.UTC);
			d = date.toDate();
		} catch (Exception e) {
			DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").withZone(DateTimeZone.forTimeZone(user.getTimeZone()));
			DateTime date = fmt.parseDateTime(dateAsString);
			date = date.withZone(DateTimeZone.UTC);
			d = date.toDate();
		}
		return DateTools.dateToString(d, DateTools.Resolution.SECOND);
	}
		
   	private static void addDateRange(Element block, String fieldName, String startDate, String endDate) {
   		
   		Element range = block.addElement(QueryBuilder.RANGE_ELEMENT);
   		range.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, fieldName);
   		String formattedStartDate = SearchFilterKeys.MinimumSystemDate;
   		if (startDate != null && !startDate.equals("")) {
   			formattedStartDate = formatStartDate(startDate);
   		}
   		String formattedEndDate = SearchFilterKeys.MaximumSystemDate;
   		if (endDate != null && !endDate.equals("")) {
   			formattedEndDate = formatEndDate(endDate);
   		}
   		
		Element start = range.addElement(QueryBuilder.RANGE_START);
		start.setText(formattedStartDate);
   		
		Element end = range.addElement(QueryBuilder.RANGE_FINISH);
		end.setText(formattedEndDate);
	}

	private static void addPersonalTagField(Element block, String personalTag) {
		Element field = block.addElement(QueryBuilder.PERSONALTAGS_ELEMENT);
	    String [] strTagArray = personalTag.split("\\s");
	    for (int k = 0; k < strTagArray.length; k++) {
	    	String strTag = strTagArray[k];
	    	if (strTag.equals("")) continue;
    		Element child = field.addElement(QueryBuilder.TAG_ELEMENT);
    	    child.addAttribute(QueryBuilder.TAG_NAME_ATTRIBUTE, strTag);
	    }
	}

	private static void addCommunityTagField(Element block, String text) {
		Element field = block.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, BasicIndexUtils.TAG_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(text);
	}

	private static void addElementField(Element block, Element filterTerm) {
    	//Search for an element value
		String elementName = filterTerm.attributeValue(SearchFilterKeys.FilterElementName, "");
		Element andField = block.addElement(QueryBuilder.AND_ELEMENT);
		Element field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, elementName);
	    Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    child.setText(filterTerm.getText());
	}

	private static void parseAndAddDocTypesField(Element block, Element filterTerm) {
    	//Add an OR field with all of the desired docId types
		Element andField = block;
		Element orField2 = andField.addElement(QueryBuilder.OR_ELEMENT);
		Iterator itTermTypes = filterTerm.selectNodes(SearchFilterKeys.FilterDocType).iterator();
		while (itTermTypes.hasNext()) {
			String entityTypeName = ((Element) itTermTypes.next()).getText();
			addDocType(orField2, entityTypeName);
		}
	}
	
	private static void addDocType(Element block, String docType) {
		if (docType == null || docType.equals("")) {
			return;
		}
		
		Element field2 = block.addElement(QueryBuilder.FIELD_ELEMENT);
		field2.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, BasicIndexUtils.DOC_TYPE_FIELD);
		field2.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
		Element child2 = field2.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child2.setText(docType);

	}

	private static void parseAndAddEntityTypesField(Element block, Element filterTerm) {
		//Add an OR field with all of the desired entity types
		Element andField = block;
		Element orField2 = andField.addElement(QueryBuilder.OR_ELEMENT);
		Iterator itTermTypes = filterTerm.selectNodes(SearchFilterKeys.FilterEntityType).iterator();
		while (itTermTypes.hasNext()) {
			String entityTypeName = ((Element) itTermTypes.next()).getText();
			addEntityType(orField2, entityTypeName);
		}
   	}
	
	private static void addEntityType(Element block, String entityType) {
		if (entityType == null || entityType.equals("")) {
			return;
		}

		Element field2 = block.addElement(QueryBuilder.FIELD_ELEMENT);
		field2.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.ENTITY_FIELD);
		field2.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
		Element child2 = field2.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child2.setText(entityType);
   	}
	
	
	private static void parseAndAddEntryTypesField(Element block, Element filterTerm) {
		//Add an OR field with all of the desired entity types
		Element andField = block;
		Element orField2 = andField.addElement(QueryBuilder.OR_ELEMENT);
		Iterator itTermTypes = filterTerm.selectNodes(SearchFilterKeys.FilterEntryType).iterator();
		while (itTermTypes.hasNext()) {
			String entryTypeName = ((Element) itTermTypes.next()).getText();
			addEntryType(orField2, entryTypeName);
		}
   	}
	
	private static void addEntryType(Element block, String entryType) {
		if (entryType == null || entryType.equals("")) {
			return;
		}

		Element field2 = block.addElement(QueryBuilder.FIELD_ELEMENT);
		field2.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.ENTRY_FIELD);
		Element child2 = field2.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child2.setText(entryType);
   	}
	
	private static void addFolderField(Element block, String folderId) {
		Element field;
		Element child;
		Element andField = block;
		if (!folderId.equals("")) {
			andField = block.addElement(QueryBuilder.AND_ELEMENT);
			field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.BINDER_ID_FIELD);
	    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	child.setText(folderId);
		}
	}
	
	private static void addFoldersListField(Element block, Element filterTerm) {
		Element andField = block;
	
		if (filterTerm.selectNodes(SearchFilterKeys.FilterFolderId).size() > 0) {
			Element orField2 = andField.addElement(QueryBuilder.OR_ELEMENT);
			Iterator itTermStates = filterTerm.selectNodes(SearchFilterKeys.FilterFolderId).iterator();			
			while (itTermStates.hasNext()) {
				String stateName = ((Element) itTermStates.next()).getText();
				if (!stateName.equals("")) {
					Element field2 = orField2.addElement(QueryBuilder.FIELD_ELEMENT);
					field2.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.BINDER_ID_FIELD);
					field2.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
					Element child2 = field2.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
					child2.setText(stateName);
				}
			}
		}
	}
	
	private static void addAncestryField(Element block, String folderId) {
		Element field;
		Element child;
		Element andField = block;
		if (!folderId.equals("")) {
			andField = block.addElement(QueryBuilder.AND_ELEMENT);
			field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.ENTRY_ANCESTRY);
	    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	child.setText(folderId);
		}
	}
	
	
	
	private static void addAncestriesListField(Element block, Element filterTerm) {
		Element andField = block;
	
		if (filterTerm.selectNodes(SearchFilterKeys.FilterFolderId).size() > 0) {
			Element orField2 = andField.addElement(QueryBuilder.OR_ELEMENT);
			Iterator itTermStates = filterTerm.selectNodes(SearchFilterKeys.FilterFolderId).iterator();			
			while (itTermStates.hasNext()) {
				String stateName = ((Element) itTermStates.next()).getText();
				if (!stateName.equals("")) {
					Element field2 = orField2.addElement(QueryBuilder.FIELD_ELEMENT);
					field2.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.ENTRY_ANCESTRY);
					field2.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
					Element child2 = field2.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
					child2.setText(stateName);
				}
			}
		}
	}
	
	private static void parseAndAddWorkflowField(Element block, Element filterTerm) {
		//This is a workflow state term. Build booleans from the state name.
		String defId = filterTerm.attributeValue(SearchFilterKeys.FilterWorkflowDefId, "");
		Element field;
		Element child;
		Element andField = block;
		if (!defId.equals("")) {
			andField = block.addElement(QueryBuilder.AND_ELEMENT);
			field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.WORKFLOW_PROCESS_FIELD);
	    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	child.setText(defId);
		}
		
    	//Add an OR field with all of the desired states
		if (filterTerm.selectNodes(SearchFilterKeys.FilterWorkflowStateName).size()>0) {
			Element orField2 = andField.addElement(QueryBuilder.OR_ELEMENT);
			Iterator itTermStates = filterTerm.selectNodes(SearchFilterKeys.FilterWorkflowStateName).iterator();			
			while (itTermStates.hasNext()) {
				String stateName = ((Element) itTermStates.next()).getText();
				if (!stateName.equals("")) {
					Element field2 = orField2.addElement(QueryBuilder.FIELD_ELEMENT);
					field2.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.WORKFLOW_STATE_FIELD);
					field2.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
					Element child2 = field2.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
					child2.setText(stateName);
				}
			}
		}
	}
	
   	private static void addTaskStatus(Element block, Element filterTerm) {
		Element andField = block;
		
		if (filterTerm.selectNodes(SearchFilterKeys.FilterTaskStatusName).size() > 0) {
			Element orField2 = andField.addElement(QueryBuilder.OR_ELEMENT);
			Iterator itTermStates = filterTerm.selectNodes(SearchFilterKeys.FilterTaskStatusName).iterator();
			while (itTermStates.hasNext()) {
				String statusName = ((Element) itTermStates.next()).getText();
				if (!statusName.equals("")) {
					Element field2 = orField2.addElement(QueryBuilder.FIELD_ELEMENT);
					field2.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, TaskHelper.STATUS_TASK_ENTRY_ATTRIBUTE_NAME);
					field2.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
					Element child2 = field2.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
					child2.setText(statusName);
				}
			}
		}
	}
	
	private static void addEntryIdField(Element block, String entryId) {
		Element andField = block;
		if (!entryId.equals("")) {
			andField = block.addElement(QueryBuilder.AND_ELEMENT);
			Element field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.DOCID_FIELD);
			Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	child.setText(entryId);
		}
	}
	
	private static void addBinderParentIdField(Element block, String binderId) {
		Element field;
		Element child;
		Element andField = block;
		if (!binderId.equals("")) {
			andField = block.addElement(QueryBuilder.AND_ELEMENT);
			field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.BINDERS_PARENT_ID_FIELD);
	    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    	child.setText(binderId);
		}
	}

	private static void addTopEntryField(Element block) {
		//This is asking for top entries only (e.g., no replies or attachments)
    	//Look only for entryType=entry
		Element field;
		Element child;
       	field = block.addElement(QueryBuilder.FIELD_ELEMENT);
       	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.ENTRY_TYPE_FIELD);
       	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
       	child.setText(EntityIndexUtils.ENTRY_TYPE_ENTRY);
       	//Look only for docType=entry
       	field = block.addElement(QueryBuilder.FIELD_ELEMENT);
    	field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(BasicIndexUtils.DOC_TYPE_ENTRY);		
	}

	private static void parseAndAddEntryField(Element block, Element filterTerm) {
		//This is an entry term. Build booleans from the element name and values.
		String defId = filterTerm.attributeValue(SearchFilterKeys.FilterEntryDefId, "");
		String elementName = filterTerm.attributeValue(SearchFilterKeys.FilterElementName, "");
		if (elementName.equals("")) {
			//If no element name is specified, search for all entries with this definition id
			Element field;
			Element child;
			Element andField = block;
			if (defId != null && !defId.equals("")) {
				andField = block.addElement(QueryBuilder.AND_ELEMENT);
				field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
				field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.COMMAND_DEFINITION_FIELD);
		    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		    	child.setText(defId);
			}
		} else {
			Iterator itTermValues = filterTerm.selectNodes(SearchFilterKeys.FilterElementValue).iterator();
			while (itTermValues.hasNext()) {
				String value = ((Element) itTermValues.next()).getText();
				if (!value.equals("")) {
					Element field;
					Element child;
					Element andField = block;
	    			if (defId != null &&!defId.equals("")) {
	    				andField = block.addElement(QueryBuilder.AND_ELEMENT);
		    			field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
		    			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.COMMAND_DEFINITION_FIELD);
		    	    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		    	    	child.setText(defId);
	    			}
	    			
	    	    	field = andField.addElement(QueryBuilder.FIELD_ELEMENT);
	    			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, elementName);
	    				    	    			
	    			if (value.contains("*")) {
	    				field.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "false");
	    			} else {
	    				field.addAttribute(QueryBuilder.EXACT_PHRASE_ATTRIBUTE, "true");
	    			}
	    			child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
	    			if (SearchFilterKeys.CurrentUserId.equals(value.toString())) {
	    				child.setText(RequestContextHolder.getRequestContext().getUserId().toString());
	    			} else {
	    				child.setText(value);
	    			}
				}
			}
		}
	}

	private static void addTagsField(Element block, String text) {
		Element subOr = block.addElement(QueryBuilder.OR_ELEMENT);
		Element field = subOr.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, BasicIndexUtils.TAG_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(text);
		field = subOr.addElement(QueryBuilder.PERSONALTAGS_ELEMENT);
		child = field.addElement(QueryBuilder.TAG_ELEMENT);
	    child.addAttribute(QueryBuilder.TAG_NAME_ATTRIBUTE, text);
	}

	private static void addAuthorField(Element block, String text) {
		Element subOr = block.addElement(QueryBuilder.OR_ELEMENT);
		
		Element field = subOr.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.CREATOR_NAME_FIELD);
    	Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(text);
    	
    	field = subOr.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, EntityIndexUtils.CREATOR_TITLE_FIELD);
    	child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
    	child.setText(text);		
	}

	private static void addSearchTextField(Element block, String text) {
		Element field = block.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE, BasicIndexUtils.ALL_TEXT_FIELD);
		Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText(text);
	}
	
	private static void addItemTypesField(Element block, Element filterTerm) {
		Element subOr = block.addElement(QueryBuilder.OR_ELEMENT);
		
		Iterator itTermValues = filterTerm.selectNodes(SearchFilterKeys.FilterItemType).iterator();
		while (itTermValues.hasNext()) {
			String itemType = ((Element) itTermValues.next()).getText();
			
			if (itemType.equals("workspace")) {
				Element subAnd = subOr.addElement(QueryBuilder.AND_ELEMENT);
				addEntityType(subAnd, EntityType.workspace.name());
				addDocType(subAnd, BasicIndexUtils.DOC_TYPE_BINDER);
			} else if (itemType.equals("folder")) {
				Element subAnd = subOr.addElement(QueryBuilder.AND_ELEMENT);
				addEntityType(subAnd, EntityType.folder.name());
				addDocType(subAnd, BasicIndexUtils.DOC_TYPE_BINDER);
			} else if (itemType.equals("user")) {
				Element subAnd = subOr.addElement(QueryBuilder.AND_ELEMENT);
				addEntityType(subAnd, EntityType.user.name());
				addDocType(subAnd, BasicIndexUtils.DOC_TYPE_ENTRY);
				addEntryType(subAnd, EntityIndexUtils.ENTRY_TYPE_USER);
			} else if (itemType.equals("attachment")) {
				Element subAnd = subOr.addElement(QueryBuilder.AND_ELEMENT);
				addEntityType(subAnd, EntityType.folderEntry.name());
				addEntryType(subAnd, EntityIndexUtils.ENTRY_TYPE_ENTRY);
				addDocType(subAnd, BasicIndexUtils.DOC_TYPE_ATTACHMENT);
			} else if (itemType.equals("entry")) {
				Element subAnd = subOr.addElement(QueryBuilder.AND_ELEMENT);
				addEntityType(subAnd, EntityType.folderEntry.name());
				addEntryType(subAnd, EntityIndexUtils.ENTRY_TYPE_ENTRY);
				addDocType(subAnd, BasicIndexUtils.DOC_TYPE_ENTRY);
			} else if (itemType.equals("reply")) {
				Element subAnd = subOr.addElement(QueryBuilder.AND_ELEMENT);
				addEntityType(subAnd, EntityType.folderEntry.name());
				addEntryType(subAnd, EntityIndexUtils.ENTRY_TYPE_REPLY);
				addDocType(subAnd, BasicIndexUtils.DOC_TYPE_ENTRY);
			}
		}
	}
	
	private static String checkLanguage(String text, Element qTreeRootElement, String lang) {
		if (!lang.equalsIgnoreCase(LanguageTaster.DEFAULT)) {
			return lang;
		}
		lang = LanguageTaster.taste(text.toCharArray()); 
		if (lang.equalsIgnoreCase(LanguageTaster.DEFAULT))
			return lang;
		Element langNode = qTreeRootElement.addElement(QueryBuilder.LANGUAGE_ELEMENT);
		langNode.addAttribute(QueryBuilder.LANGUAGE_ATTRIBUTE, lang);
		return lang;
	}
	
}