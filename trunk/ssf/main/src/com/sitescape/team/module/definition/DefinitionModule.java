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
package com.sitescape.team.module.definition;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.xml.sax.SAXException;

import com.sitescape.team.ObjectExistsException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.DefinitionInvalidException;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.security.AccessControlException;
/**
 * @author hurley
 *
 */
public interface DefinitionModule {
	public static String INDEX_FIELDS_ONLY="com.sitescape.team.module.definition.indexFieldsOnly";
	public static String INDEX_CAPTION="indexCaption";
	public static String INDEX_CAPTION_VALUES="indexCaptionValues";
	public static String DEFINITION_ELEMENT="definitionElement";
	public static String CAPTION_FIELD_PREFIX="_caption_";
	public enum DefinitionOperation {
		manageDefinition,
	}
	public Definition addDefinition(Document defDoc, Binder binder, boolean replace);
	public Definition addDefinition(InputStream indoc, Binder binder, String name, String title, boolean replace) throws AccessControlException, Exception;
	public Definition addDefinition(Binder binder, String name, String title, Integer type, InputDataAccessor inputData) throws AccessControlException;
	/**
	 * Adds an item to an item in a definition tree.
	 *
	 * @param This call takes 4 parameters: def, itemId, itemNameToAdd, formData<br>
	 *        def - contains the definition that is being modified<br>
	 *        itemId - the id of the item being added to<br>
	 *        itemNameToAdd - the name of the item to be added<br>
	 *        formData - a Map of the values to e set in the newly added item
	 *                   The Map should contain each property value indexed by the 
	 *                     property name prefixed by "propertyId_".
	 * 
	 * @return the next element in the iteration.
	 * @exception NoSuchElementException iteration has no more elements.
	 */
	public Element addItem(String defId, String itemId, String itemName, InputDataAccessor inputData) throws DefinitionInvalidException, AccessControlException;
	public Definition addDefaultDefinition(Integer type);
	public Definition copyDefinition(String id, Binder binder, String name, String title) throws AccessControlException;
	public void copyItem(String defId, String sourceItemId, String targetItemId) throws DefinitionInvalidException, AccessControlException;
	public void deleteDefinition(String id) throws AccessControlException;
	public void deleteItem(String defId, String itemId) throws DefinitionInvalidException, AccessControlException;

	public Definition getDefinition(String id);
	public Definition getDefinitionByReservedId(String id);
	public Definition getDefinitionByName(Binder binder, Boolean includeAncestors, String name);
	public List<Definition> getAllDefinitions();
	public List<Definition> getAllDefinitions(Integer type);
	public List<Definition> getDefinitions(Long binderId, Boolean includeAncestors);
	public List<Definition> getDefinitions(Long binderId, Boolean includeAncestors, Integer type);
	public Document getDefinitionConfig();
	public Document getDefinitionAsXml(Definition def);
	
	public static String ENTRY_ATTRIBUTES_SET = "__set__";
	public static String ENTRY_ATTRIBUTES_SET_MULTIPLE_ALLOWED = "__setMultipleAllowed__";
	public static String MASHUP_HIDE_MASTHEAD = "__hideMasthead";
	public static String MASHUP_HIDE_SIDEBAR = "__hideSidebar";
	public static String MASHUP_SHOW_BRANDING = "__showBranding";
	public static String MASHUP_HIDE_TOOLBAR = "__hideToolbar";
	public static String MASHUP_HIDE_FOOTER = "__hideFooter";
	
	/**
	 * Routine to process the input data and return a map of only the entry data
	 * 
	 * @param def
	 * @param inputData
	 * @return
	 */
	public Map getEntryData(Document def, InputDataAccessor inputData, Map fileItems);
	public Map getEntryDefinitionElements(String id);
	public Map getWorkflowDefinitionStates(String id);

	public void modifyVisibility(String id, Integer visibility, Long binderId) throws AccessControlException,ObjectExistsException;
	public void modifyDefinitionProperties(String id, InputDataAccessor inputData) throws AccessControlException;
	public void modifyItem(String defId, String itemId, InputDataAccessor inputData) throws DefinitionInvalidException, AccessControlException;
	public void moveItem(String defId, String sourceItemId, String targetItemId, String position) throws DefinitionInvalidException, AccessControlException;
	public Definition setDefaultBinderDefinition(Binder binder);
	public Definition setDefaultEntryDefinition(Entry entry);
	public void setDefinitionLayout(String id, InputDataAccessor inputData) throws AccessControlException;

  	public boolean testAccess(Binder binder, Integer type, DefinitionOperation operation);
  	/**
  	 * After importing a definition, references to other definitions must be resolved.
  	 * 
  	 * @param defId
  	 */
  	public void updateDefinitionReferences(String defId);
  	public void walkDefinition(DefinableEntity entry, DefinitionModule.DefinitionVisitor visitor, Map args);

  	interface DefinitionVisitor
  	{
  		abstract public void visit(Element entryElement, Element flagElement, Map args);
  		abstract public String getFlagElementName();
  	}
}
