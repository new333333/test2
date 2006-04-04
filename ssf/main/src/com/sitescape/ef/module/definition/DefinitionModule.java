package com.sitescape.ef.module.definition;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.sitescape.ef.domain.DefinitionInvalidException;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.module.definition.notify.Notify;
import com.sitescape.ef.module.shared.InputDataAccessor;

/**
 * @author hurley
 *
 */
public interface DefinitionModule {
	public Definition getDefinition(String id);
	public Definition addDefinition(String name, String title, int type, Map formData);
	public String addDefinition(Document doc);
	public void deleteDefinition(String id);
	public Document getDefaultDefinition(String name, String title, int type, Map formData);
	public Document getDefinitionConfig();
	public void modifyDefinitionName(String id, String name, String caption);
	public void modifyDefinitionAttribute(String id, String key, String value);
	public void modifyDefinitionProperties(String id, Map formData);
	public void saveDefinitionLayout(String id, Map formData);

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
	public Element addItem(String defId, String itemId, String itemName, Map formData) throws DefinitionInvalidException;
	public Element addItemToDefinitionDocument(String defId, Document definitionTree, String itemId, String itemNameToAdd, Map formData) throws DefinitionInvalidException;
	public void modifyItem(String defId, String itemId, Map formData) throws DefinitionInvalidException;
	public void deleteItem(String defId, String itemId) throws DefinitionInvalidException;
	public void moveItem(String defId, String sourceItemId, String targetItemId, String position) throws DefinitionInvalidException;
	
	/**
	 * Routine to process the input data and return a map of only the entry data
	 * 
	 * @param def
	 * @param inputData
	 * @return
	 */
	public Map getEntryData(Definition def, InputDataAccessor inputData, Map fileItems);
	public List getDefinitions();
	
	public void addIndexFieldsForEntity(org.apache.lucene.document.Document indexDoc, DefinableEntity entity);
	public void addNotifyElementForEntity(Element element, Notify notifyDef, DefinableEntity entity, String[] categories);
	public Map getEntryDefinitionElements(String id);
}
