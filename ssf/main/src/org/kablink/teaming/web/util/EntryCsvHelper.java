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
package org.kablink.teaming.web.util;

import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Field;
import org.apache.tools.zip.ZipOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.definition.index.FieldBuilderUtil;
import org.kablink.teaming.module.workflow.WorkflowUtils;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

/**
 * ?
 * 
 * @author phurley@novell.com
 */
@SuppressWarnings({"unchecked", "unused"})
public class EntryCsvHelper {
	public static String DEFAULT_CSV_DELIMITER	= ",";
	public static String DEFAULT_CSV_FILENAME	= "folderCsv.csv";	// Must be a simple, ASCII filename.
	
	public static void folderToCsv(AllModulesInjected bs, Folder folder, Map options, OutputStream out) {
		if (options == null) options = new HashMap();
		if (!options.containsKey(ObjectKeys.CSV_OPTIONS_ATTRS_TO_SKIP)) {
			List<String> attrsToSkip = new ArrayList<String>();
			options.put(ObjectKeys.CSV_OPTIONS_ATTRS_TO_SKIP, attrsToSkip);
			attrsToSkip.add("guestName");
			attrsToSkip.add("captcha");
		}

		// What delimiter should we use between fields in the output?
		String csvDelim;
		if (options.containsKey(ObjectKeys.CSV_DELIMITER)) {
			csvDelim = ((String) options.get(ObjectKeys.CSV_DELIMITER));
			if (!(MiscUtil.hasString(csvDelim))) {
				csvDelim = DEFAULT_CSV_DELIMITER;
			}
		}
		else {
			csvDelim = DEFAULT_CSV_DELIMITER;
		}
		
		Map folderEntries = null;
		Long folderId = folder.getId();
		User user = RequestContextHolder.getRequestContext().getUser();
		
		//Get the entry definitions that this folder can add
		List<Definition> entryDefList = folder.getEntryDefinitions();
		List<Definition> workflowDefList = folder.getWorkflowDefinitions();
		
		//Build the list of columns for the CSV file
		List<Map<String,Object>> columnTemplate = new ArrayList<Map<String,Object>>();
		Map<String,Object> colMap;
		
		//Start with the standard columns
		colMap = new HashMap<String,Object>();
		colMap.put(ObjectKeys.CSV_TYPE, ObjectKeys.CSV_TYPE_TEXT);
		colMap.put(ObjectKeys.CSV_COL_HEADER, NLT.get("csv.entryId"));
		colMap.put(ObjectKeys.CSV_ATTR, ObjectKeys.CSV_ATTR_ENTRY_ID);
		columnTemplate.add(colMap);		//EntryId
		
		colMap = new HashMap<String,Object>();
		colMap.put(ObjectKeys.CSV_TYPE, ObjectKeys.CSV_TYPE_TEXT);
		colMap.put(ObjectKeys.CSV_COL_HEADER, NLT.get("csv.docNum"));
		colMap.put(ObjectKeys.CSV_ATTR, ObjectKeys.CSV_ATTR_DOC_NUM);
		columnTemplate.add(colMap);		//DocNum
		
		colMap = new HashMap<String,Object>();
		colMap.put(ObjectKeys.CSV_TYPE, ObjectKeys.CSV_TYPE_TEXT);
		colMap.put(ObjectKeys.CSV_COL_HEADER, NLT.get("csv.title"));
		colMap.put(ObjectKeys.CSV_ATTR, ObjectKeys.CSV_ATTR_TITLE);
		columnTemplate.add(colMap);		//Title
		
		colMap = new HashMap<String,Object>();
		colMap.put(ObjectKeys.CSV_TYPE, ObjectKeys.CSV_TYPE_PRINCIPAL);
		colMap.put(ObjectKeys.CSV_COL_HEADER, NLT.get("csv.author"));
		colMap.put(ObjectKeys.CSV_ATTR, ObjectKeys.CSV_ATTR_AUTHOR);
		columnTemplate.add(colMap);		//Author
		
		colMap = new HashMap<String,Object>();
		colMap.put(ObjectKeys.CSV_TYPE, ObjectKeys.CSV_TYPE_PRINCIPAL);
		colMap.put(ObjectKeys.CSV_COL_HEADER, NLT.get("csv.authorTitle"));
		colMap.put(ObjectKeys.CSV_ATTR, ObjectKeys.CSV_ATTR_AUTHOR_TITLE);
		columnTemplate.add(colMap);		//Author title
		
		colMap = new HashMap<String,Object>();
		colMap.put(ObjectKeys.CSV_TYPE, ObjectKeys.CSV_TYPE_DATE);
		colMap.put(ObjectKeys.CSV_COL_HEADER, NLT.get("csv.creationDate"));
		colMap.put(ObjectKeys.CSV_ATTR, ObjectKeys.CSV_ATTR_CREATION_DATE);
		columnTemplate.add(colMap);		//Creation date
		
		colMap = new HashMap<String,Object>();
		colMap.put(ObjectKeys.CSV_TYPE, ObjectKeys.CSV_TYPE_DATE);
		colMap.put(ObjectKeys.CSV_COL_HEADER, NLT.get("csv.modificationDate"));
		colMap.put(ObjectKeys.CSV_ATTR, ObjectKeys.CSV_ATTR_MODIFICATION_DATE);
		columnTemplate.add(colMap);		//Modification date
		
		colMap = new HashMap<String,Object>();
		colMap.put(ObjectKeys.CSV_TYPE, ObjectKeys.CSV_TYPE_TEXT);
		colMap.put(ObjectKeys.CSV_COL_HEADER, NLT.get("csv.description"));
		colMap.put(ObjectKeys.CSV_ATTR, ObjectKeys.CSV_ATTR_DESCRITION);
		columnTemplate.add(colMap);		//Description
		
		colMap = new HashMap<String,Object>();
		colMap.put(ObjectKeys.CSV_TYPE, ObjectKeys.CSV_TYPE_TEXT);
		colMap.put(ObjectKeys.CSV_COL_HEADER, NLT.get("csv.attachedFiles"));
		colMap.put(ObjectKeys.CSV_ATTR, ObjectKeys.CSV_ATTR_ATTACHED_FILES);
		columnTemplate.add(colMap);		//Attached files

		//Now add the elements from each definition
		for (Definition def : entryDefList) {
			addDefinitionElementsToTemplate(bs, def, columnTemplate, options);
		}
		
		//Finally, add the workflow column
		if (workflowDefList.size() > 0) {
			colMap = new HashMap<String,Object>();
			colMap.put(ObjectKeys.CSV_TYPE, ObjectKeys.CSV_TYPE_TEXT);
			colMap.put(ObjectKeys.CSV_COL_HEADER, NLT.get("csv.workflowState"));
			colMap.put(ObjectKeys.CSV_ATTR, ObjectKeys.CSV_ATTR_WORKFLOW);
			columnTemplate.add(colMap);		//Workflow
		}
		
		//Output the Header row CSV
		outputHeaderCsv(out, columnTemplate, csvDelim);

		options.put(ObjectKeys.SEARCH_MAX_HITS, ObjectKeys.SEARCH_MAX_HITS_FOLDER_ENTRIES_EXPORT);
		folderEntries = bs.getFolderModule().getEntries(folderId, options);
		if (folderEntries != null) {
			List<Map> folderEntriesList = (List) folderEntries.get(ObjectKeys.SEARCH_ENTRIES);
			
			for (Map se : folderEntriesList) {
				String entryIdStr = (String)se.get(Constants.DOCID_FIELD);
				if (entryIdStr != null && !entryIdStr.equals("")) {
    				Long entryId = Long.valueOf(entryIdStr);
    				FolderEntry entry = bs.getFolderModule().getEntry(folderId, entryId);
    				
    				//Output the CSV for this entry
    				outputEntryAsCsv(out, entry, columnTemplate, csvDelim);
				}
    		}

		}

	}
	
	private static void addDefinitionElementsToTemplate(AllModulesInjected bs, final Definition def, 
			final List<Map<String,Object>> columnTemplate, Map options) {
        // Add data fields driven by the entry's definition object. 
		final List<String> attrsToSkip;
		if (options.containsKey(ObjectKeys.CSV_OPTIONS_ATTRS_TO_SKIP)) {
			attrsToSkip = (List<String>)options.get(ObjectKeys.CSV_OPTIONS_ATTRS_TO_SKIP);
		} else {
			attrsToSkip = new ArrayList<String>();
		}
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			@Override
			public void visit(Element entryElement, Element flagElement, Map args)
			{
				String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
				if (Validator.isNull(nameValue)) {nameValue = entryElement.attributeValue("name");}
				String attrDefItemName = entryElement.attributeValue("name");
				String caption = DefinitionUtils.getPropertyValue(entryElement, "caption");									
				if (Validator.isNull(caption)) {
					caption = entryElement.attributeValue("name");
				} else {
					caption = NLT.get(caption);
				}
				if (nameValue != null && !nameValue.equals("") && 
						!nameValue.equals(ObjectKeys.CSV_ATTR_TITLE) && 
						!nameValue.equals(ObjectKeys.CSV_ATTR_DESCRITION) && 
						!attrsToSkip.contains(attrDefItemName)) {
					//Look to see if this attr is already in the template
					boolean foundAttr = false;
					for (Map<String,Object> cm : columnTemplate) {
						if (def.getId().equals(cm.get(ObjectKeys.CSV_DEF_ID)) && 
								nameValue.equals(ObjectKeys.CSV_ATTR)) {
							foundAttr = true;
							break;
						}
					}
					if (!foundAttr) {
						//Add this attr to the template
						Map<String,Object> colMap;
						colMap = new HashMap<String,Object>();
						colMap.put(ObjectKeys.CSV_TYPE, ObjectKeys.CSV_TYPE_TEXT);
						colMap.put(ObjectKeys.CSV_DEF_ID, def.getId());
						colMap.put(ObjectKeys.CSV_ATTR, nameValue);
						colMap.put(ObjectKeys.CSV_COL_HEADER, caption);
						columnTemplate.add(colMap);		//Custom Attribute
					}
				}
			}
			@Override
			public String getFlagElementName() { return "index"; }
		};
		bs.getDefinitionModule().walkDefinition(def.getDefinition(), visitor, null);

	}

	private static void outputHeaderCsv(OutputStream out, List<Map<String,Object>> columnTemplate, String csvDelim) {
		//Output the column headers according to the column template		
		boolean firstColSeen = false;
		for (Map<String,Object> cm : columnTemplate) {
			String headerText = (String)cm.get(ObjectKeys.CSV_COL_HEADER);
			String attrName = (String)cm.get(ObjectKeys.CSV_ATTR);
			try {
				if (firstColSeen) {
					out.write(csvDelim.getBytes());
				}
				firstColSeen = true;
				try {
					if (headerText == null) {
						out.write(attrName.getBytes("UTF-8"));
					} else {
						out.write(headerText.getBytes("UTF-8"));
					}
				} catch(Exception e) {
					//An error occurred on an attribute. Just leave it blank.
				}
			} catch(Exception e) {
				//An error occurred trying to output the comma separator. This must mean that the output stream is closed.
			}
		}
		try {
			out.write("\n".getBytes());
		} catch(Exception e) {
			//An error occurred trying to output the EOL. This must mean that the output stream is closed.
		}
	}
	
	private static void outputEntryAsCsv(OutputStream out, FolderEntry entry, List<Map<String,Object>> columnTemplate, String csvDelim) {
		Document defDoc = entry.getEntryDefDoc();
		String family = DefinitionUtils.getFamily(defDoc);
		//Output the attributes according to the column template		
		boolean firstColSeen = false;
		for (Map<String,Object> cm : columnTemplate) {
			DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String attrName = (String)cm.get(ObjectKeys.CSV_ATTR);
			try {
				if (firstColSeen) {
					out.write(csvDelim.getBytes());
				}
				firstColSeen = true;
				try {
					if (attrName.equals(ObjectKeys.CSV_ATTR_ENTRY_ID)) {
						out.write(String.valueOf(entry.getId()).getBytes("UTF-8"));
					} else if (attrName.equals(ObjectKeys.CSV_ATTR_DOC_NUM)) {
						out.write(entry.getDocNumber().getBytes("UTF-8"));
					} else if (attrName.equals(ObjectKeys.CSV_ATTR_TITLE)) {
						out.write(CsvHelper.checkText(entry.getTitle()).getBytes("UTF-8"));
					} else if (attrName.equals(ObjectKeys.CSV_ATTR_AUTHOR)) {
						Principal owner = entry.getOwner();
						out.write(CsvHelper.checkText(owner.getName()).getBytes("UTF-8"));
					} else if (attrName.equals(ObjectKeys.CSV_ATTR_AUTHOR_TITLE)) {
						Principal owner = entry.getOwner();
						out.write(CsvHelper.checkText(owner.getTitle()).getBytes("UTF-8"));
					} else if (attrName.equals(ObjectKeys.CSV_ATTR_CREATION_DATE)) {
						Date date = entry.getCreation().getDate();
						if (ObjectKeys.FAMILY_FILE.equals(family)) {
							FileAttachment fa = entry.getPrimaryFileAttachment();
							if (fa != null) {
								date = fa.getCreation().getDate();
							}
						}
						out.write(CsvHelper.checkText(dateFormatter.format(date)).getBytes("UTF-8"));
					} else if (attrName.equals(ObjectKeys.CSV_ATTR_MODIFICATION_DATE)) {
						Date date = entry.getModificationDate();
						if (ObjectKeys.FAMILY_FILE.equals(family)) {
							FileAttachment fa = entry.getPrimaryFileAttachment();
							if (fa != null) {
								date = fa.getModification().getDate();
							}
						}
						if (date != null) {
							out.write(CsvHelper.checkText(dateFormatter.format(entry.getModificationDate())).getBytes("UTF-8"));
						}
					} else if (attrName.equals(ObjectKeys.CSV_ATTR_DESCRITION)) {
						out.write(CsvHelper.checkText(entry.getDescription().getText()).getBytes("UTF-8"));
					} else if (attrName.equals(ObjectKeys.CSV_ATTR_ATTACHED_FILES)) {
						out.write(CsvHelper.checkText(getAttachedFileNames(entry)).getBytes("UTF-8"));
					} else if (attrName.equals(ObjectKeys.CSV_ATTR_WORKFLOW)) {
						out.write(CsvHelper.checkText(getWorkflowCsvText(entry)).getBytes("UTF-8"));
					} else {
						//This is a custom attribute. Figure out its type and output the value
						Element attrDefEle = DefinitionHelper.findAttribute(attrName, defDoc);
						CustomAttribute attr = entry.getCustomAttribute(attrName);
						if (attr != null) {
							out.write(CsvHelper.checkText(DefinitionHelper.GetCustomAttrAsString(attr)).getBytes("UTF-8"));
						}
					}
				} catch(Exception e) {
					//An error occurred on an attribute. Just leave it blank.
				}
			} catch(Exception e) {
				//An error occurred trying to output the comma separator. This must mean that the output stream is closed.
			}
		}
		try {
			out.write("\n".getBytes());
		} catch(Exception e) {
			//An error occurred trying to output the EOL. This must mean that the output stream is closed.
		}
	}
	
	private static String getAttachedFileNames(FolderEntry entry) {
		StringBuffer buf = new StringBuffer();
		Set<FileAttachment> atts = entry.getFileAttachments();
		boolean isFirst = true;
		for (FileAttachment fa : atts) {
			if (!isFirst) {
				buf.append(", ");
			}
			isFirst = false;
			String fileName = fa.getFileItem().getName();
			buf.append(fileName);
		}
		return buf.toString();
	}
	
	private static String getWorkflowCsvText(FolderEntry entry) {
		Document defDoc = entry.getEntryDefDoc();
		Set<WorkflowState> workflows = entry.getWorkflowStates();
		boolean isFirst = true;
		if (workflows.size() > 0) {
			StringBuffer buf = new StringBuffer();
			for (WorkflowState ws : workflows) {
				String workflowCaption = WorkflowUtils.getStateCaption(ws.getDefinition(), ws.getState());
				if (workflowCaption == null || workflowCaption.equals("")) {
					workflowCaption = ws.getState();
				}
				if (!isFirst) {
					buf.append(", ");
				}
				isFirst = false;
				buf.append(workflowCaption);
			}
			return buf.toString();
		}
		return "";
	}
}
