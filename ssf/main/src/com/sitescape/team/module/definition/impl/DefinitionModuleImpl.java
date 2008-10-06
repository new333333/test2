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
package com.sitescape.team.module.definition.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Locale;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectExistsException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.calendar.TimeZoneHelper;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.Restrictions;
import com.sitescape.team.domain.Application;
import com.sitescape.team.domain.ApplicationGroup;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CommaSeparatedValue;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.DefinitionInvalidException;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.NoBinderByTheIdException;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.NoPrincipalByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.definition.DefinitionConfigurationBuilder;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.workflow.WorkflowModule;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.survey.Survey;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.TreeHelper;
import com.sitescape.team.web.util.MarkupUtil;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.StringUtil;
import com.sitescape.util.Validator;

/**
 * @author hurley
 *
 */
public class DefinitionModuleImpl extends CommonDependencyInjection implements DefinitionModule, InitializingBean  {
	private Document definitionConfig;
	private Element configRoot;
	private DefinitionConfigurationBuilder definitionBuilderConfig;
	private static final String[] defaultDefAttrs = new String[]{"internalId", "type"};

	protected BinderModule binderModule;
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
   	protected BinderModule getBinderModule() {
		return binderModule;
	}
   	protected WorkflowModule workflowModule;

	public void setWorkflowModule(WorkflowModule workflowModule) {
		this.workflowModule = workflowModule;
	}
	protected WorkflowModule getWorkflowModule() {
		return workflowModule;
	}
    public void afterPropertiesSet() {
		this.definitionConfig = definitionBuilderConfig.getAsMergedDom4jDocument();
		this.configRoot = this.definitionConfig.getRootElement();


    }
    /*
     *  (non-Javadoc)
 	 * Use operation so we can keep the logic out of application
     * @see com.sitescape.team.module.definition.DefinitionModule#testAccess(java.lang.String)
     */
   	public boolean testAccess(Binder binder, Integer type, DefinitionOperation operation) {
   		try {
   			checkAccess(binder, type, operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		} catch (NotSupportedException ac) {
   			return false;
   		}

   	}
   	protected void checkAccess(Binder binder, Integer type, DefinitionOperation operation) throws AccessControlException {
   		Binder top = RequestContextHolder.getRequestContext().getZone();
   		if (type.equals(Definition.FOLDER_ENTRY) || type.equals(Definition.FOLDER_VIEW) || type.equals(Definition.WORKSPACE_VIEW)) {
   			if (binder == null) {
   				if (getAccessControlManager().testOperation(top, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS)) return;
   				getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
   			} else {
  				if (getAccessControlManager().testOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS)) return;
  				getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
   			}
   		} else if (type.equals(Definition.WORKFLOW)) {
   			if (binder ==  null) {
   				if (getAccessControlManager().testOperation(top, WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS)) return;
   				getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
   			} else {
  				if (getAccessControlManager().testOperation(binder, WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS)) return;
   				getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
   			}
   		} else {
   			accessControlManager.checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
   		}

   	}
   	protected void checkAccess(Definition def, DefinitionOperation operation) throws AccessControlException {
   		if (def.getBinderId() == null) checkAccess(null, def.getType(), operation);
   		else checkAccess(getCoreDao().loadBinder(def.getBinderId(), def.getZoneId()), def.getType(), operation);
   	}

	public Definition addDefinition(InputStream indoc, Binder binder, String name, String title, boolean replace) throws AccessControlException, Exception {
/*The current xsd is really for the configuration file.  The export defintions don't follow all the rules,
  xsd:sequence in particular.  Until we either fix this or build a new xsd, this validating code is disabled.
		SAXReader xIn = new SAXReader(true);
        // The following code turns on XML schema-based validation
        // features specific to Apache Xerces2 parser. Therefore it
        // will not work when a different parser is used.
		xIn.setFeature("http://apache.org/xml/features/validation/schema", true); // Enables XML Schema validation
		xIn.setFeature("http://apache.org/xml/features/validation/schema-full-checking",true); // Enables full (if slow) schema checking
		xIn.setProperty(
                "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                DirPath.getDTDDirPath() + File.separator + "definition_builder_config.xsd");
*/
		SAXReader xIn = new SAXReader(false);
		Document doc = xIn.read(indoc);
		String type = doc.getRootElement().attributeValue("type");
	   	checkAccess(binder, Integer.valueOf(type), DefinitionOperation.manageDefinition);
    	Definition def = doAddDefinition(doc, binder, name, title, replace);
    	return def;

	}
	public Definition copyDefinition(String id, Binder binder, String name, String title) throws AccessControlException {
		Definition srcDef = getDefinition(id);
		Document doc = (Document)srcDef.getDefinition().clone();
		doc.getRootElement().addAttribute("internalId", "");
		doc.getRootElement().addAttribute("databaseId", "");
		String type = doc.getRootElement().attributeValue("type");
	   	checkAccess(binder, Integer.valueOf(type), DefinitionOperation.manageDefinition);
    	return doAddDefinition(doc, binder, name, title, false);
	}
	public Definition addDefinition(Binder binder, String name, String title, Integer type, InputDataAccessor inputData) throws AccessControlException {
	   	checkAccess(binder, type, DefinitionOperation.manageDefinition);

		Definition newDefinition = new Definition();
		newDefinition.setName(name);
		newDefinition.setTitle(title);
		newDefinition.setType(type);
		newDefinition.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		if (binder != null) newDefinition.setBinderId(binder.getId());
		getCoreDao().save(newDefinition);
		Document doc = getInitialDefinition(name, title, type, inputData);
		setDefinition(newDefinition, doc);
		return newDefinition;

	}

    protected Definition doAddDefinition(Document doc, Binder binder, String name, String title, boolean replace) {
    	Element root = doc.getRootElement();
		if (Validator.isNull(name)) name = root.attributeValue("name");
		if (Validator.isNull(name)) name = DefinitionUtils.getPropertyValue(root, "name");
		if (Validator.isNull(title)) title = root.attributeValue("caption");
		if (Validator.isNull(title)) title = DefinitionUtils.getPropertyValue(root, "caption");
		if (Validator.isNull(name)) {
			name=title;
		}
		Integer type = Integer.valueOf(root.attributeValue("type"));

		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		String id = root.attributeValue("databaseId", "");
		String internalId = root.attributeValue("internalId", null);
		if (binder != null) internalId = null; //reserved only at system level
		Definition def=null;
		if (Validator.isNotNull(internalId)) {
			//make sure doesn't exist
			try {
				def = getCoreDao().loadReservedDefinition(internalId, zoneId);
				//already exists
				if (!replace || !type.equals(def.getType()) ) return null;
				def.setName(name);
				def.setTitle(title);
				def.setVisibility(Definition.VISIBILITY_PUBLIC);
				setDefinition(def, doc);
				return def;

			} catch (NoDefinitionByTheIdException nd) {}

		}
		// import - try reusing existing guid;
		// see if already exists in this zone
		if (Validator.isNotNull(id)) {
			try {
				def = getCoreDao().loadDefinition(id, null);
				//see if belong to this binder and zone
				if (def.getZoneId().equals(zoneId) &&
						((binder == null && def.getBinderId() == null) ||
								(binder != null && binder.getId().equals(def.getBinderId())))) {
						if (!replace || !type.equals(def.getType())) return null;
						//	update it
						def.setName(name);
						def.setTitle(title);
						def.setInternalId(internalId);
						setDefinition(def, doc);
						return def;
				}
				id = null;
			} catch (NoDefinitionByTheIdException nd) {
			}
		}

		try {
			def = getCoreDao().loadDefinitionByName(binder, name, zoneId);
			//found a definition using the name for this binder and zone
			if (!replace || !type.equals(def.getType())) return null;
			def.setTitle(title);
			def.setInternalId(internalId);
			setDefinition(def,doc);
			return def;

		} catch (NoDefinitionByTheIdException nd) {
		}

		//doesn't exist at all
		//try to create in this zone using new GUID
		def = new Definition();
		def.setZoneId(zoneId);
		def.setName(name);
		def.setTitle(title);
		def.setType(type);
		def.setInternalId(internalId);
		if (binder != null) def.setBinderId(binder.getId());
		if (Validator.isNull(id))
			getCoreDao().save(def);
		else {
			def.setId(id);
			getCoreDao().replicate(def);
		}
		setDefinition(def,doc);

		return def;
	}
    protected void setDefinition(Definition def, Document doc) {

   		//Make sure the definition name and caption remain consistent
    	doc.getRootElement().addAttribute("internalId", def.getInternalId());
    	doc.getRootElement().addAttribute("databaseId", def.getId());
    	doc.getRootElement().addAttribute("name", def.getName());
   		Element newPropertiesEle = (Element)doc.getRootElement().selectSingleNode("./properties/property[@name='name']");
   		if (newPropertiesEle != null) newPropertiesEle.addAttribute("value", def.getName());
   		doc.getRootElement().addAttribute("caption", def.getTitle());
   		newPropertiesEle = (Element)doc.getRootElement().selectSingleNode("./properties/property[@name='caption']");
   		if (newPropertiesEle != null) newPropertiesEle.addAttribute("value", def.getTitle());

    	//Write out the new definition file
    	def.setDefinition(doc);

    	//If this is a workflow definition, build the corresponding JBPM workflow definition
    	if (def.getType() == Definition.WORKFLOW) {
    		//Use the definition id as the workflow process name
    		getWorkflowModule().modifyProcessDefinition(def.getId(), def);
    	}
    }
    //should be called after all imports are done, to handle definition cross references
    public void updateDefinitionReferences(String defId) {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	Definition def = getCoreDao().loadDefinition(defId, zoneId);
    	Binder binder = null;
    	if (def.getBinderId() != null) binder = getCoreDao().loadBinder(def.getBinderId(), zoneId);
    	Document doc = def.getDefinition();
    	if (doc == null) return;
    	Map<Long, Principal> principalMap = new HashMap();
    	Map<String, Definition> definitionMap = new HashMap();
    	Element export = (Element)doc.getRootElement().selectSingleNode("./export-mappings");
    	if (export == null) return;
    	List<Element> exportElements = export.selectNodes("./export");
    	for (Element exp:exportElements) {
    		String strId = exp.attributeValue("principalId");
    		if (Validator.isNotNull(strId)) {
				try {
					Long id = Long.valueOf(strId);
					principalMap.put(id, getProfileDao().findPrincipalByName(exp.getTextTrim(), zoneId));
				} catch (NumberFormatException nb) {
				} catch (NoPrincipalByTheNameException np) {
				}

    		} else {
    			strId = exp.attributeValue("definitionId");
    	   		if (Validator.isNotNull(strId)) {
    	   			try {
    	   				definitionMap.put(strId, getDefinitionByName(binder, true, exp.getTextTrim()));
    	   			} catch (NoDefinitionByTheIdException nd) {
    	   			}
    	   		}
    		}
    	}
		List<Element> remoteApps = doc.getRootElement().selectNodes("//property[@name='remoteApp']");
		for (Element remoteApp:remoteApps) {
			String appId = remoteApp.attributeValue("value", "");
			if (Validator.isNotNull(appId)) {
				Principal p = principalMap.get(appId);
				if (p == null || !(p instanceof Application))
					remoteApp.addAttribute("value", "");
				else
					remoteApp.addAttribute("value", p.getId().toString());
			}
		}
		if (def.getType() == Definition.FOLDER_ENTRY) {
			List<Element> props = doc.getRootElement().selectNodes("//properties/property[@name='replyStyle']");
			for (Element prop:props) {
				String styleId = prop.attributeValue("value", "");
				Definition namedDef = definitionMap.get(styleId);
				if (namedDef == null)
					prop.addAttribute("value", "");
				else
					prop.addAttribute("value", namedDef.getId());
			}
		}
		if (def.getType() == Definition.WORKFLOW) {
			List<Element> conditions = doc.getRootElement().selectNodes("//workflowEntryDataUserList | //workflowCondition ");
			for (Element condition:conditions) {
				String entryId = condition.attributeValue("definitionId", "");
				Definition namedDef = definitionMap.get(entryId);
				if (namedDef == null)
					condition.addAttribute("definitionId", "");
				else
					condition.addAttribute("definitionId", namedDef.getId());
			}
			List<Element> props = doc.getRootElement().selectNodes("//item[@name='startProcess']/properties/property[@name='definitionId']");
			for (Element prop:props) {
				String entryId = prop.attributeValue("value", "");
				Definition namedDef = definitionMap.get(entryId);
				if (namedDef == null)
					prop.addAttribute("value", "");
				else
					prop.addAttribute("value", namedDef.getId());
			}
			props = doc.getRootElement().selectNodes("//property[@name='userGroupAccess'] | //property[@name='userGroupNotification']");
			for (Element prop:props) {
				String entryIds = prop.attributeValue("value", "");
				if (Validator.isNull(entryIds)) continue;
				Set<Long> ids = LongIdUtil.getIdsAsLongSet(entryIds);
				StringBuffer buf = new StringBuffer();
				for (Long id:ids) {
					Principal p = principalMap.get(id);
					if (p == null) continue;
					buf.append(p.getId().toString());
					buf.append(" ");
				}
				prop.addAttribute("value", buf.toString());
			}
    	}
		//Write out the new definition file
    	export.detach();
		def.setDefinition(doc);

    }

    public Document getDefinitionAsXml(Definition def) {
    	//convert enty definitionId references to names
    	Document srcDoc = def.getDefinition();
    	Document outDoc;
    	if (srcDoc != null) outDoc = (Document)srcDoc.clone();
    	else outDoc = DocumentHelper.createDocument();

    	Set<Long> principalIds = new HashSet();
    	Set<String>definitionIds = new HashSet();
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		List<Element> remoteApps = outDoc.getRootElement().selectNodes("//property[@name='remoteApp']");
		for (Element remoteApp:remoteApps) {
			String appId = remoteApp.attributeValue("value", "");
			if (Validator.isNotNull(appId)) {
				try {
					Long id = Long.valueOf(appId);
					principalIds.add(id);
				} catch (NumberFormatException nb) {}
			}
		}
		if (def.getType() == Definition.FOLDER_ENTRY) {
			List<Element> replyStyles = outDoc.getRootElement().selectNodes("//property[@name='replyStyle']");
			for (Element styleElement:replyStyles) {
				String styleId = styleElement.attributeValue("value", "");
				if (Validator.isNotNull(styleId)) {
					definitionIds.add(styleId);
				}
			}
		}

		if (def.getType() == Definition.WORKFLOW) {
			List<Element> conditions = outDoc.getRootElement().selectNodes("//workflowEntryDataUserList | //workflowCondition ");
			for (Element condition:conditions) {
				String entryId = condition.attributeValue("definitionId", "");
				if (Validator.isNotNull(entryId)) {
					definitionIds.add(entryId);
				}
			}
			List<Element> props = outDoc.getRootElement().selectNodes("//item[@name='startProcess']/properties/property[@name='definitionId']");
			for (Element prop:props) {
				String entryId = prop.attributeValue("value", "");
				if (Validator.isNotNull(entryId)) {
					definitionIds.add(entryId);
				}
			}
			props = outDoc.getRootElement().selectNodes("//property[@name='userGroupAccess'] | //property[@name='userGroupNotification']");
			for (Element prop:props) {
				String entryId = prop.attributeValue("value", "");
				principalIds.addAll(LongIdUtil.getIdsAsLongSet(entryId));
			}
    	}
		//build export properties
		Element exports = outDoc.getRootElement().addElement("export-mappings");
		List<Principal> principals = getProfileDao().loadPrincipals(principalIds, zoneId, true);
		for (Principal p:principals) {
			Element principalEle = exports.addElement("export");
			principalEle.addAttribute("principalId", p.getId().toString());
			principalEle.setText(p.getName());
		}
		List<Definition> definitions = getCoreDao().loadObjects(definitionIds, Definition.class, zoneId);
		for (Definition d:definitions) {
			Element definitionlEle = exports.addElement("export");
			definitionlEle.addAttribute("definitionId", d.getId());
			definitionlEle.setText(d.getName());
		}
    	return outDoc;
    }
    public Definition getDefinition(String id) {
		// Controllers need access to definitions.  Allow world read
 		return coreDao.loadDefinition(id, RequestContextHolder.getRequestContext().getZoneId());
	}
	public Definition getDefinitionByReservedId(String internalId) {
		return getCoreDao().loadReservedDefinition(internalId, RequestContextHolder.getRequestContext().getZoneId());
		
	}
    //lookup definition by name going up tree including public
	public Definition getDefinitionByName(Binder binder, Boolean includeAncestors, String name) {
		List<Definition> defs;
		if (binder == null) {
  	    	Map params = new HashMap();
   	    	params.put("name", name);
   	    	params.put("zoneId", RequestContextHolder.getRequestContext().getZoneId());  //need zone without binder
  	    	defs = coreDao.loadObjects("from com.sitescape.team.domain.Definition where binderId is null and zoneId=:zoneId and name=:name", params);

		} else if (includeAncestors.equals(Boolean.TRUE)) {
   	    	Map params = new HashMap();
   	    	params.put("binderId", getAncestorIds(binder));
   	    	params.put("name", name);
  	    	params.put("zoneId", RequestContextHolder.getRequestContext().getZoneId());  //need zone without binder
  	    	defs = coreDao.loadObjects("from com.sitescape.team.domain.Definition where (binderId in (:binderId) or binderId is null) and zoneId=:zoneId and name=:name", params);
  	 	} else {
  	    	FilterControls filter = new FilterControls();
  	 		filter.add("binderId", binder.getId());
 	 		filter.add("name", name);
  	 		defs =  coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
 	 	}
   	 	//find the first one
		if (defs.size() == 0) throw new NoDefinitionByTheIdException(name);
		//should only be 1 if binder is null
		if (binder == null || defs.size() == 1) return defs.get(0);
		//find the one matching the binder the closest to this one
		while (binder != null) {
			for (Definition def:defs) {
				if (binder.getId().equals(def.getBinderId())) return def;
			}
			binder = binder.getParentBinder();
		}
		//if the definition was the top level, should have already found it with defs ==1
		throw new NoDefinitionByTheIdException(name); //shouldn't get here
	}

	protected DefinitionConfigurationBuilder getDefinitionBuilderConfig() {
        return definitionBuilderConfig;
    }
    public void setDefinitionBuilderConfig(DefinitionConfigurationBuilder definitionBuilderConfig) {
        this.definitionBuilderConfig = definitionBuilderConfig;
    }


	public void modifyVisibility(String id, Integer visibility, Long binderId) {
		if (visibility == null) return;
		Definition def = getDefinition(id);
   		checkAccess(def, DefinitionOperation.manageDefinition);

   		if (binderId == null) {
   			if (def.getBinderId() == null) 	{
	   			//already global
   				def.setVisibility(visibility);
   			} else {
   				//want to move to global
   		   		checkAccess(null, def.getType(), DefinitionOperation.manageDefinition);
   		   		//see if name will be unique
   		   		try {
   		   			getCoreDao().loadDefinitionByName(null, def.getName(), def.getZoneId());
   		   			//already exists
   		   			throw new ObjectExistsException("errorcode.name.exists");
   		   		} catch (NoDefinitionByTheIdException nd) {
   		   			def.setVisibility(visibility);
   					def.setBinderId(null);
	   			}
   			}
	   	} else {
	   		if (binderId.equals(def.getBinderId())) {
		   		//just changing visibility
	   			def.setVisibility(visibility);
	   		} else {
	   			//moving
	   			Binder binder = getCoreDao().loadBinder(binderId, def.getZoneId());
	   			//check access at destination
	   			checkAccess(binder, def.getType(), DefinitionOperation.manageDefinition);
   		   		try {
   		   			getCoreDao().loadDefinitionByName(binder, def.getName(), def.getZoneId());
   		   			//already exists
   		   			throw new ObjectExistsException("errorcode.name.exists");
   		   		} catch (NoDefinitionByTheIdException nd) {
   		   			def.setVisibility(visibility);
   					def.setBinderId(binderId);
	   			}
	   		}
	   	}

	}


	public void modifyDefinitionProperties(String id, InputDataAccessor inputData) {
		Definition def = getDefinition(id);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		//Store the properties in the definition document
		Document defDoc = def.getDefinition();
		if (def != null && defDoc != null) {
			//name and caption are special cased
			if (inputData.exists("propertyId_name")) {
				String  definitionName = inputData.getSingleValue("propertyId_name");
				if (Validator.isNotNull(definitionName)) def.setName(definitionName);
			}
			if (inputData.exists("propertyId_caption")) {
				String definitionCaption = inputData.getSingleValue("propertyId_caption");
				if (Validator.isNotNull(definitionCaption)) def.setTitle(definitionCaption);
			}

			String type = String.valueOf(def.getType());
			Element definition = (Element) this.configRoot.selectSingleNode("item[@definitionType='"+type+"']");
			if (definition != null) {
				//Add the properties
				processProperties(def.getId(), definition, defDoc.getRootElement(), inputData);
			}
			setDefinition(def, defDoc);

			//When any change is made, validate the the definition is at the same level as the configuration file
			validateDefinitionAttributes(def);
			validateDefinitionTree(def);

		}
	}


	//Rouitine to make sure a definition has all of the proper attributes as defined in the config file
	//  This is useful to propagate new attributes added to the config definition xml file
	private void validateDefinitionAttributes(Definition def) {
		Document defDoc = def.getDefinition();
		if (updateDefinitionAttributes(defDoc)) setDefinition(def, defDoc);
	}
	private boolean updateDefinitionAttributes(Document defDoc) {
		boolean defChanged = false;
		Element defRoot = defDoc.getRootElement();

		//Look at all of the items to see if any of their attributes are missing
		Iterator itDefItems = defRoot.elementIterator("item");
		while (itDefItems.hasNext()) {
			Element defItem = (Element) itDefItems.next();
			//Find the matching element in the configuration xml file
			Element configItem = (Element) this.configRoot.selectSingleNode("item[@name='"+defItem.attributeValue("name", "")+"']");
			if (configItem != null) {
				//Check to see if there are new attributes from the config file that should be copied into the definition
				Iterator itConfigItemAttributes = configItem.attributeIterator();
				while (itConfigItemAttributes.hasNext()) {
					Attribute attr = (Attribute) itConfigItemAttributes.next();
					//If the attribute does not exist in the definition item, copy it from the config file
					if (defItem.attributeValue(attr.getName()) == null)
					{
						// (rsordillo) Do not add non-required Attributes to new item
						if (attr.getName().equals("canBeDeleted") ||
								attr.getName().equals("category") ||
								attr.getName().equals("multipleAllowed"))
							continue;
						defItem.addAttribute(attr.getName(), attr.getValue());
						defChanged = true;
					}
				}
			}
		}
		return defChanged;
	}

	public void setDefinitionLayout(String id, InputDataAccessor inputData) {
		Definition def = getDefinition(id);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		Document defDoc = def.getDefinition();

		if (inputData.exists("xmlData") && def != null) {
			Document appletDef;
			try {
				appletDef = DocumentHelper.parseText(inputData.getSingleValue("xmlData"));
			} catch(Exception e) {
				return;
			}
			if (appletDef == null) return;

	    	//Iterate through the current definition looking for states
			List states = defDoc.getRootElement().selectNodes("//item[@name='state']");
	    	if (states == null) return;

	    	Iterator itStates = states.iterator();
	        while (itStates.hasNext()) {
	            Element state = (Element) itStates.next();
	            Element stateName = (Element) state.selectSingleNode("properties/property[@name='name']");
	            if (stateName != null) {
	            	String name = stateName.attributeValue("value", "");
	            	if (!name.equals("")) {
			            Element appletState = (Element) appletDef.getRootElement().selectSingleNode("//item[@name='state']/properties/property[@name='name' and @value='"+name+"']");
			            if (appletState != null) {
			            	String x = appletState.getParent().getParent().attributeValue("x", "");
			            	String y = appletState.getParent().getParent().attributeValue("y", "");
			            	if (!x.equals("") && !y.equals("")) {
			            		state.addAttribute("x", x);
			            		state.addAttribute("y", y);
			            	}
			            }
	            	}
	            }
	        }
			setDefinition(def, defDoc);
		}
	}

	public void deleteDefinition(String id) {
		Definition def = getDefinition(id);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		getCoreDao().delete(def);
		if (def.getType() == Definition.WORKFLOW) {
			//jbpm defs are named with the string id of the ss definitions
			getWorkflowModule().deleteProcessDefinition(def.getId());
		}
	}
	/**
	 * Load the default definition for a definition type.  If it doesn't exist, create it
	 * @param type
	 * @return
	 */
	public Definition addDefaultDefinition(Integer type) {
		// no access needed, just fills indefaults
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		String definitionTitle=null;
		String internalId=null;
		String definitionName=null;
		switch (type) {
			case Definition.FOLDER_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class,
							new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_FOLDER_DEF, type}), zoneId);
				if (!result.isEmpty()) return (Definition)result.get(0);
				definitionTitle = "__definition_default_folder";
				definitionName="_discussionFolder";
				internalId = ObjectKeys.DEFAULT_FOLDER_DEF;
				break;
			}
			case Definition.FOLDER_ENTRY: {
				List result = getCoreDao().loadObjects(Definition.class,
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_FOLDER_ENTRY_DEF, type}), zoneId);
				if (!result.isEmpty()) return (Definition)result.get(0);
				definitionTitle = "__definition_default_folder_entry";
				internalId = ObjectKeys.DEFAULT_FOLDER_ENTRY_DEF;
				definitionName="_discussionEntry";
				break;
			}
			case Definition.WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class,
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_WORKSPACE_DEF, type}), zoneId);
				if (!result.isEmpty()) return (Definition)result.get(0);
				definitionTitle = "__definition_default_workspace";
				internalId = ObjectKeys.DEFAULT_WORKSPACE_DEF;
				definitionName="_workspace";
				break;
			}

			case Definition.USER_WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class,
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_USER_WORKSPACE_DEF, type}), zoneId);
				if (!result.isEmpty()) return (Definition)result.get(0);
				definitionTitle = "__definition_default_user_workspace";
				internalId = ObjectKeys.DEFAULT_USER_WORKSPACE_DEF;
				definitionName="_userWorkspace";
				break;
			}

			case Definition.PROFILE_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class,
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_PROFILES_DEF, type}), zoneId);
				if (!result.isEmpty()) return (Definition)result.get(0);
				internalId = ObjectKeys.DEFAULT_PROFILES_DEF;
				definitionTitle = "__definition_default_profiles";
				definitionName="_profiles";
				break;
			}
			case Definition.PROFILE_ENTRY_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class,
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_USER_DEF, type}), zoneId);
				if (!result.isEmpty()) return (Definition)result.get(0);
				internalId = ObjectKeys.DEFAULT_USER_DEF;
				definitionTitle = "__definition_default_user";
				definitionName="_user";
				break;
			}
			case Definition.PROFILE_GROUP_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class,
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_GROUP_DEF, type}), zoneId);
				if (!result.isEmpty()) return (Definition)result.get(0);
				internalId = ObjectKeys.DEFAULT_GROUP_DEF;
				definitionTitle = "__definition_default_group";
				definitionName="_group";
				break;
			}
			case Definition.PROFILE_APPLICATION_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class,
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_APPLICATION_DEF, type}), zoneId);
				if (!result.isEmpty()) return (Definition)result.get(0);
				internalId = ObjectKeys.DEFAULT_APPLICATION_DEF;
				definitionTitle = "__definition_default_application";
				definitionName="_application";
				break;
			}
			case Definition.PROFILE_APPLICATION_GROUP_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class,
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_APPLICATION_GROUP_DEF, type}), zoneId);
				if (!result.isEmpty()) return (Definition)result.get(0);
				internalId = ObjectKeys.DEFAULT_APPLICATION_GROUP_DEF;
				definitionTitle = "__definition_default_application_group";
				definitionName="_applicationGroup";
				break;
			}
		}
		Document doc = getInitialDefinition(definitionName, definitionTitle, type, new MapInputData(new HashMap()));
		doc.getRootElement().addAttribute("internalId", internalId);
		return doAddDefinition(doc, null, definitionName, definitionTitle, true);
	}

/*
	private Definition loadDef(String key, String internalId, String zoneName) {
		try {
	        Resource resource =  new ClassPathResource("config"  + File.separator +  key);
			InputStream fIn = resource.getInputStream();
	        SAXReader xIn = new SAXReader();
			Document doc = xIn.read(fIn);
			fIn.close();
			String id = addDefinition(doc);
			Definition def = getCoreDao().loadDefinition(id, zoneName);
			def.setInternalId(internalId);
			return def;
		} catch (Exception ex) {
			logger.error("Error creating default definition:" + ex.getLocalizedMessage());
			return null;
		}

	}
*/

	protected Document getInitialDefinition(String name, String title, int type, InputDataAccessor inputData) {
		Element definition = (Element) this.configRoot.selectSingleNode("item[@definitionType='"+type+"']");
		if (definition == null) {return null;}

		//We found the definition. Now build the default definition
		Document newTree = DocumentHelper.createDocument();
		Element ntRoot = newTree.addElement("definition");
		ntRoot.addAttribute("name", name);
		ntRoot.addAttribute("caption", title);
		ntRoot.addAttribute("type", String.valueOf(type));
		int id = 1;
		id = populateNewDefinitionTree(definition, ntRoot, this.configRoot, id, true);
		ntRoot.addAttribute("nextId", Integer.toString(id));

		//Add the properties
		processProperties("0", definition, ntRoot, inputData);

		//Copy any additional attributes from the configuration file
		updateDefinitionAttributes(newTree);

		return newTree;
	}
	public Definition setDefaultBinderDefinition(Binder binder) {
		//no access - fixing up stuff
		//Create an empty binder definition
		int definitionType;
		if (binder.getEntityType().equals(EntityType.workspace)) {
			if ((binder.getDefinitionType() != null) &&
					(binder.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW)) {
				definitionType = Definition.USER_WORKSPACE_VIEW;
			} else {
				definitionType = Definition.WORKSPACE_VIEW;
			}
		} else if (binder.getEntityType().equals(EntityType.profiles)) {
			definitionType = Definition.PROFILE_VIEW;
		} else {
				definitionType = Definition.FOLDER_VIEW;
		}
		Definition def = addDefaultDefinition(definitionType);
		binder.setEntryDef(def);
		binder.setDefinitionType(definitionType);
		if (Validator.isNull(binder.getIconName())) {
			String icon = DefinitionUtils.getPropertyValue(def.getDefinition().getRootElement(), "icon");
			if (Validator.isNotNull(icon)) binder.setIconName(icon);
		}
		return def;
	}
	public Definition setDefaultEntryDefinition(Entry entry) {
		//no access - fixing up stuff
		//Create an empty entry definition
		int definitionType;
		if (entry instanceof User) {
			definitionType = Definition.PROFILE_ENTRY_VIEW;
		} else	if (entry instanceof Group) {
			definitionType = Definition.PROFILE_GROUP_VIEW;
		} else	if (entry instanceof Application) {
			definitionType = Definition.PROFILE_APPLICATION_VIEW;
		} else	if (entry instanceof ApplicationGroup) {
			definitionType = Definition.PROFILE_APPLICATION_GROUP_VIEW;
		} else {
			definitionType = Definition.FOLDER_ENTRY;
		}
		Definition def = addDefaultDefinition(definitionType);
		if (entry != null) {
			entry.setEntryDef(def);
			entry.setDefinitionType(definitionType);
			if (Validator.isNull(entry.getIconName())) {
				String icon = DefinitionUtils.getPropertyValue(def.getDefinition().getRootElement(), "icon");
				if (Validator.isNotNull(icon)) entry.setIconName(icon);
			}
		}
		return def;
	}

	/**
	 * Adds an item to an item in a definition tree.
	 *
	 * @param This call takes 4 parameters: def, itemId, itemNameToAdd, formData
	 *        def - contains the definition that is being modified
	 *        itemId - the id of the item being added to
	 *        itemNameToAdd - the name of the item to be added
	 *        formData - a Map of the values to e set in the newly added item
	 *                   The Map should contain each property value indexed by the
	 *                     property name prefixed by "propertyId_".
	 *
	 * @return the next element in the iteration.
	 * @exception NoSuchElementException iteration has no more elements.
	 */
	public Element addItem(String defId, String itemId, String itemNameToAdd, InputDataAccessor inputData)
			throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
	   	checkAccess(def, DefinitionOperation.manageDefinition);

		Document definitionTree = def.getDefinition();

		Element newItem = addItemToDefinitionDocument(def.getId(), definitionTree, itemId, itemNameToAdd, inputData);
		if (newItem != null) {
			//Save the updated document
			setDefinition(def, definitionTree);
			//definitionTree.asXML();
		}
		return newItem;
	}

	protected Element addItemToDefinitionDocument(String defId, Document definitionTree, String itemId,
			String itemNameToAdd, InputDataAccessor inputData) throws DefinitionInvalidException {

		Element newItem = null;

		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			//Find the element to add to
			Element item = (Element) root.selectSingleNode("//item[@id='"+itemId+"']");
			if (item != null) {
				//Find the requested new item in the configuration document
				Element itemEleToAdd = (Element) this.configRoot.selectSingleNode("item[@name='"+itemNameToAdd+"']");

				Map uniqueNames = getUniqueNameMap(this.configRoot, root, itemNameToAdd);
				if (inputData.exists("propertyId_name")) {
					String name = inputData.getSingleValue("propertyId_name");
					if (Validator.isNull(name) && itemEleToAdd.attributeValue("type", "").equals("data"))
						throw new DefinitionInvalidException("definition.error.nullname");
					if (uniqueNames.containsKey(name)) {
						//This name is not unique
						throw new DefinitionInvalidException("definition.error.nameNotUnique", new Object[] {defId, name});
					}
				}

				if (itemEleToAdd != null) {
					//Add the item
					newItem = item.addElement("item");
					//Copy the attributes from the config item to the new item
					Iterator attrs = itemEleToAdd.attributeIterator();
					while (attrs.hasNext()) {
						Attribute attr = (Attribute) attrs.next();

						// (rsordillo) Do not add non-required Attributes to new item
						if (attr.getName().equals("canBeDeleted")
						|| attr.getName().equals("multipleAllowed"))
							continue;
						newItem.addAttribute(attr.getName(), attr.getValue());
					}

					//Get the next id number from the root
					int nextId = Integer.valueOf(root.attributeValue("nextId")).intValue();
					newItem.addAttribute("id", (String) Integer.toString(nextId));
					root.addAttribute("nextId", (String) Integer.toString(++nextId));

					//Process the properties (if any)
					processProperties(defId, itemEleToAdd, newItem, inputData);
					processJsps(newItem, inputData);
					//See if this is a "dataView" type
					if (newItem.attributeValue("type", "").equals("dataView")) {
						checkDataView(root, newItem);
					}
					int nextItemId = Integer.valueOf(root.attributeValue("nextId")).intValue();;
					nextItemId = populateNewDefinitionTree(itemEleToAdd, newItem, this.configRoot, nextItemId, false);
					root.addAttribute("nextId", Integer.toString(nextItemId));
				}
			}
		}
		return newItem;
	}

	private void processProperties(String defId, Element configEle, Element newItem, InputDataAccessor inputData) {
		//Check to see if there are new attributes from the config file that should be copied into the definition
		Iterator itAttributes = configEle.attributeIterator();
		while (itAttributes.hasNext()) {
			Attribute attr = (Attribute) itAttributes.next();
			//If the attribute does not exist in the new item, copy it from the config file
			if (newItem.attributeValue(attr.getName()) == null)
			{
				// (rsordillo) Do not add non-required Attributes to new item
				if (attr.getName().equals("canBeDeleted")
				|| attr.getName().equals("multipleAllowed"))
					continue;
				newItem.addAttribute(attr.getName(), attr.getValue());
			}
		}

		//Copy the properties from the definition
		Element configProperties = configEle.element("properties");
		if (configProperties != null) {
			//Remove the previous list of properties
			Iterator itProperties = newItem.selectNodes("properties").iterator();
			while (itProperties.hasNext()) {
				newItem.remove((Element) itProperties.next());
			}
			//Add a fresh "properties" element
			Element newPropertiesEle = newItem.addElement("properties");

			//Set the values of each property from the form data
			Iterator itConfigProperties = configProperties.elementIterator("property");
			while (itConfigProperties.hasNext()) {
				Element configProperty = (Element) itConfigProperties.next();
				String attrName = configProperty.attributeValue("name");
				String type = configProperty.attributeValue("type", "");
				String characterMask = configProperty.attributeValue("characterMask", "");
				if (inputData.exists("propertyId_"+attrName)) {
					String[] values = (String[]) inputData.getValues("propertyId_"+attrName);
					for (int i = 0; i < values.length; i++) {
						String value = values[i];
						if (!characterMask.equals("")) {
							//See if the user entered a valid name
							if (!value.equals("") && !value.matches(characterMask)) {
								//The value is not well formed, go complain to the user
								throw new DefinitionInvalidException("definition.error.invalidCharacter", new Object[] {"\""+value+"\""});
							}
						}

						Element newPropertyEle = newPropertiesEle.addElement("property");
						//just copy name and value
						newPropertyEle.addAttribute("name", attrName);
						if (type.equals("text") || type.equals("remoteApp") || type.equals("subProcess")) {
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("textarea")) {
							newPropertyEle.setText(value);
						} else if (type.equals("integer")) {
							if (value.matches("[^0-9]+?")) {
								//The value is not a valid integer
								throw new DefinitionInvalidException("definition.error.notAnInteger", new Object[] {defId, configProperty.attributeValue("caption")});
							}
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("selectbox") || type.equals("itemSelect") ||
								type.equals("radio") || type.equals("replyStyle") ||
								type.equals("iconList") || type.equals("repositoryList") ||
								type.equals("folderSelect")) {
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("boolean") || type.equals("checkbox")) {
							if (value == null) {value = "false";}
							if (value.equalsIgnoreCase("on")) {
								value = "true";
							} else {
								value = "false";
							}
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("userGroupSelect")) {
							String [] v= StringUtil.split(value);
							for (int vals=0; vals < v.length; ++vals) {
								if (v[vals].matches("[^0-9]+?")) {
									//The value is not a valid integer
									throw new DefinitionInvalidException("definition.error.notAnInteger", new Object[] {defId, configProperty.attributeValue("caption")});
								}
							}
							newPropertyEle.addAttribute("value", value);
						}

					}
				} else if (type.equals("workflowCondition")) {
					//Workflow conditions typically have 4 bits of data to capture:
					//  the definition id, the element name, the operation, and the operand value
					if (inputData.exists("conditionDefinitionId") &&
							inputData.exists("conditionElementName") &&
							inputData.exists("conditionElementOperation")) {
						Element newPropertyEle = configProperty.createCopy();
						newPropertiesEle.add(newPropertyEle);
						String conditionDefinitionId = inputData.getSingleValue("conditionDefinitionId");
						String conditionElementName = inputData.getSingleValue("conditionElementName");
						String conditionElementOperation = inputData.getSingleValue("conditionElementOperation");
						Element workflowCondition = newPropertyEle.addElement("workflowCondition");
						workflowCondition.addAttribute("definitionId", conditionDefinitionId);
						workflowCondition.addAttribute("elementName", conditionElementName);
						workflowCondition.addAttribute("operation", conditionElementOperation);
						if (inputData.exists("operationDuration") &&
								inputData.exists("operationDurationType")) {
							String operationDuration = inputData.getSingleValue("operationDuration");
							String operationDurationType = inputData.getSingleValue("operationDurationType");
							workflowCondition.addAttribute("duration", operationDuration);
							workflowCondition.addAttribute("durationType", operationDurationType);
						}
						if (inputData.exists("conditionElementValue")) {
							String[] conditionValues = (String[])inputData.getValues("conditionElementValue");
							for (int j = 0; j < conditionValues.length; j++) {
								String conditionValue = conditionValues[j];
								workflowCondition.addElement("value").setText(conditionValue);
							}
						}
					}
				} else if (type.equals("workflowEntryDataUserList")) {
					//Workflow conditions typically have 4 bits of data to capture:
					//  the definition id, the element name, the operation, and the operand value
					if (inputData.exists("conditionDefinitionId") &&
							inputData.exists("conditionElementName")) {
						String conditionDefinitionId = inputData.getSingleValue("conditionDefinitionId");
						if (Validator.isNotNull(conditionDefinitionId)) {
							Element newPropertyEle = configProperty.createCopy();
							newPropertiesEle.add(newPropertyEle);
							String[] conditionElementNames = (String[]) inputData.getValues("conditionElementName");
							for (int i=0; i<conditionElementNames.length; ++i) {
								if (Validator.isNull(conditionElementNames[i])) continue;
								Element workflowCondition = newPropertyEle.addElement("workflowEntryDataUserList");
								workflowCondition.addAttribute("definitionId", conditionDefinitionId);
								workflowCondition.addAttribute("elementName", conditionElementNames[i]);
							}
						}
					}
				} else {
					if (type.equals("boolean") || type.equals("checkbox")) {
						String value = "false";
						Element newPropertyEle = newPropertiesEle.addElement("property");
						newPropertyEle.addAttribute("name", attrName);
						newPropertyEle.addAttribute("value", value);
					}
				}
			}
		}
	}
	private void processJsps(Element item, InputDataAccessor inputData) {
		Element jsps = (Element)item.selectSingleNode("./jsps");
		if (jsps != null) item.remove(jsps); //keep it lean
		String value = inputData.getSingleValue("jspName_custom");
		Boolean inherit = Boolean.FALSE;
		if ("on".equals(inputData.getSingleValue("jspName_custom_inherit"))) inherit=Boolean.TRUE;
		if (Validator.isNull(value) && !inherit) return;
		jsps = item.addElement("jsps");
		Element jsp = jsps.addElement("jsp");
		jsp.addAttribute("name", "custom");
		if (inherit) {
			jsp.addAttribute("inherit", inherit.toString());
		} else {
			jsp.addAttribute("value", value);
		}
	}

	public void modifyItem(String defId, String itemId, InputDataAccessor inputData) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		Document definitionTree = def.getDefinition();

		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			//Find the element to modify
			Element item = (Element) root.selectSingleNode("//item[@id='"+itemId+"']");
			if (item != null) {
				String itemNamePropertyValue = DefinitionUtils.getPropertyValue(item, "name");
				if (itemNamePropertyValue == null) itemNamePropertyValue="";

				//Find the selected item type in the configuration document
				String itemType = item.attributeValue("name", "");
				Map uniqueNames = getUniqueNameMap(this.configRoot, root, itemType);
				if (inputData.exists("propertyId_name")) {
					String name = inputData.getSingleValue("propertyId_name");
					if (Validator.isNull(name)) throw new DefinitionInvalidException("definition.error.nullname");
					//See if the item name is being changed
					if (!name.equals(itemNamePropertyValue) &&
							uniqueNames.containsKey(name)) {
						//This name is not z
						throw new DefinitionInvalidException("definition.error.nameNotUnique", new Object[] {defId, name});
					} else if (!name.equals(itemNamePropertyValue)) {
						//The name is being changed. Check if this is a workflow state
						if (itemType.equals("state") && "workflowProcess".equals(item.getParent().attributeValue("name"))) {
							if (checkStateInUse(def, itemNamePropertyValue)) throw new DefinitionInvalidException("definition.error.cannotModifyState", new Object[] {def.getId()});
							getWorkflowModule().modifyStateName(def.getId(), itemNamePropertyValue, name);
							//change transition to names
							List<Element> properties = item.selectNodes("../item[@name='state']/item[@name='transitions']/item[@type='transition']/properties/property[@name='toState' and @value='"+itemNamePropertyValue+"']");
							for (Element prop:properties) {
								prop.addAttribute("value", name);
							}
							//change start state
							properties = item.selectNodes("../properties/property[@name='initialState' and @value='"+itemNamePropertyValue+"']");
							for (Element prop:properties) {
								prop.addAttribute("value", name);
							}
							//change end state
							properties = item.selectNodes("../properties/property[@name='endState' and @value='"+itemNamePropertyValue+"']");
							for (Element prop:properties) {
								prop.addAttribute("value", name);
							}
							//change parallel start state
							properties = item.selectNodes("../item[@name='parallelThread']/properties/property[@name='startState' and @value='"+itemNamePropertyValue+"']");
							for (Element prop:properties) {
								prop.addAttribute("value", name);
							}
							//change parallel end state
							properties = item.selectNodes("../item[@name='parallelThread']/properties/property[@name='endState' and @value='"+itemNamePropertyValue+"']");
							for (Element prop:properties) {
								prop.addAttribute("value", name);
							}

						} else if (itemType.equals("parallelThread") && "workflowProcess".equals(item.getParent().attributeValue("name"))) {
							if (checkThreadInUse(def, itemNamePropertyValue)) throw new DefinitionInvalidException("definition.error.cannotModifyThread", new Object[] {def.getId()});
							//change start/stop
							List<Element> properties = item.selectNodes("../item[@name='state']/item[@name='onEntry' or @name='onExit']/item[@name='startParallelThread' or @name='stopParallelThread']/properties/property[@name='name' and @value='"+itemNamePropertyValue+"']");
							for (Element prop:properties) {
								prop.addAttribute("value", name);
							}

						}
					}
				}
				Element itemTypeEle = (Element) this.configRoot.selectSingleNode("item[@name='"+itemType+"']");
				if (itemTypeEle != null) {
					//Set the values of each property from the form data
					processProperties(defId, itemTypeEle, item, inputData);
					processJsps(item, inputData);
					//See if this is a "dataView" type
					if ("dataView".equals(item.attributeValue("type"))) {
						checkDataView(root, item);
					}
					setDefinition(def, definitionTree);
				}
			}
		}
	}
	private void checkDataView(Element root, Element item) {
		//This item is shadowing one of the form data items. Capture its form item name
		String newItemNamePropertyValue = DefinitionUtils.getPropertyValue(item, "name");
		if (!Validator.isNull(newItemNamePropertyValue)) {
			//Find the form item with this name
			Iterator itFormItems = root.selectNodes("//item/properties/property[@value='"+newItemNamePropertyValue+"']").iterator();
			while (itFormItems.hasNext()) {
				//Look for the form item with a "name" property
				Element formItemProperty = (Element) itFormItems.next();
				if (formItemProperty.attributeValue("name", "").equals("name")) {
					//This is a "name" property. Now see if it under the form tree
					Element parentElement = formItemProperty.getParent();
					while (parentElement != null) {
						if (parentElement.getName().equals("item") && parentElement.attributeValue("type", "").equals("form")) {
							//Found it. This item is part of the "form" tree.
							break;
						}
						parentElement = parentElement.getParent();
					}
					if (parentElement != null) {
						//Get the type of the item that is being shadowed
						String shadowItemName = formItemProperty.getParent().getParent().attributeValue("name", "");
						item.addAttribute("formItem", shadowItemName);
					}
				}
			}
		}

	}
	private boolean checkStateInUse(Definition def, String state) {
		//This is a workflow state. Make sure no entries are using that state
		FilterControls fc = new FilterControls();
		fc.add("definition", def);
		fc.add("state", state);
		List inUse = getCoreDao().loadObjects(WorkflowState.class, fc, def.getZoneId());
		if (!inUse.isEmpty()) return true;
		return false;

	}
	private boolean checkThreadInUse(Definition def, String threadName) {
		//This is a workflow state. Make sure no entries are using that state
		FilterControls fc = new FilterControls();
		fc.add("definition", def);
		fc.add("threadName", threadName);
		List inUse = getCoreDao().loadObjects(WorkflowState.class, fc, def.getZoneId());
		if (!inUse.isEmpty()) return true;
		return false;
	}

	public void deleteItem(String defId, String itemId) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
	   	checkAccess(def, DefinitionOperation.manageDefinition);

		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			//Find the element to delete
			Element item = (Element) root.selectSingleNode("//item[@id='"+itemId+"']");
			if (item != null) {
				//Find the selected item type in the configuration document
				String itemType = item.attributeValue("name", "");
				if (itemType.equals("state") && "workflowProcess".equals(item.getParent().attributeValue("name"))) {
					//This is a workflow state. Make sure no entries are using that state
					String state = DefinitionUtils.getPropertyValue(item, "name");
					if (checkStateInUse(def, state)) throw new DefinitionInvalidException("definition.error.cannotModifyState", new Object[] {def.getId()});
				}
				if (itemType.equals("parallelThread") && "workflowProcess".equals(item.getParent().attributeValue("name"))) {
					//This is a workflow state. Make sure no entries are using that state
					String threadName = DefinitionUtils.getPropertyValue(item, "name");
					if (checkThreadInUse(def, threadName)) throw new DefinitionInvalidException("definition.error.cannotModifyThread", new Object[] {def.getId()});
				}


				Element itemTypeEle = (Element) this.configRoot.selectSingleNode("item[@name='"+itemType+"']");
				//Check that this element is allowed to be deleted
				if (itemTypeEle == null || !itemTypeEle.attributeValue("canBeDeleted", "true").equalsIgnoreCase("false")) {
					Element parent = item.getParent();
					//Delete the item from the definition tree
					item.detach();
					//Check to make sure there are any items marked as "cannot be deleted"
					Iterator itItems = item.selectNodes("./descendant::item").listIterator();
					if (itItems != null) {
						while (itItems.hasNext()) {
							Element item2 = (Element) itItems.next();
							String itemType2 = item2.attributeValue("name", "");
							//Element itemTypeEle2 = (Element) this.definitionConfig.getRootElement().selectSingleNode("item[@name='"+itemType2+"']");
							Element itemTypeEle2 = (Element) this.configRoot.selectSingleNode("item[@name='"+itemType2+"']");
							if (itemTypeEle2 != null && itemTypeEle2.attributeValue("canBeDeleted", "true").equalsIgnoreCase("false")) {
								//This item cannot be deleted. Add it back on the parent
								parent.add(item2.detach());
							}
						}
					}
					setDefinition(def, definitionTree);
				}
			}
		}
	}

	public void moveItem(String defId, String sourceItemId, String targetItemId, String position) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		if (sourceItemId.equals(targetItemId)) return;
		Document definitionTree = def.getDefinition();
		if (definitionTree == null) return;
		Element root = definitionTree.getRootElement();
		//Find the element to move
		Element sourceItem = (Element) root.selectSingleNode("//item[@id='"+sourceItemId+"']");
		if (sourceItem == null) //The item to be moved is no longer defined as a valid item
			throw new DefinitionInvalidException("definition.error.noElement", new Object[] {defId});

		Element targetItem = (Element) root.selectSingleNode("//item[@id='"+targetItemId+"']");
		if (targetItem == null) 	//Target item is no longer defined as a valid item
			throw new DefinitionInvalidException("definition.error.noElement", new Object[] {defId});
		//We have found both the source and the target ids; do the move
		if (position.equals("into")) {
			if (checkTargetOptions(definitionTree, targetItem, sourceItem, false)) {
				//Detach the source item
				sourceItem.detach();
				//Add it to the target element
				targetItem.add(sourceItem);
			} else {
				//The target item is not designed to accept this item as a child
				throw new DefinitionInvalidException("definition.error.illegalMoveInto", new Object[] {defId});
			}
		} else if (position.equals("above")) {
			//Get the parent of the target item
			Element targetParent = (Element) targetItem.getParent();
			if (checkTargetOptions(definitionTree, targetParent, sourceItem, false)) {
				//Detach the source item
				sourceItem.detach();
				List targetParentContent = targetParent.content();
				int i = targetParentContent.indexOf(targetItem);
				if (i < 0) {i = 0;}
				targetParentContent.add(i,sourceItem);
			} else {
				//The target item is not designed to accept this item as a child
				throw new DefinitionInvalidException("definition.error.illegalMoveInto", new Object[] {defId});
			}
		} else if (position.equals("below")) {
			//Get the parent of the target item
			Element targetParent = (Element) targetItem.getParent();
			if (checkTargetOptions(definitionTree, targetParent, sourceItem, false)) {
				//Detach the source item
				sourceItem.detach();
				List targetParentContent = targetParent.content();
				int i = targetParentContent.indexOf(targetItem);
				targetParentContent.add(i+1,sourceItem);
			} else {
				//The target item is not designed to accept this item as a child
				throw new DefinitionInvalidException("definition.error.illegalMoveInto", new Object[] {defId});
			}
		}
		//Write the new document back into the definition
		setDefinition(def, definitionTree);
	}

	public void copyItem(String defId, String sourceItemId, String targetItemId) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		if (sourceItemId.equals(targetItemId)) return;
		Document definitionTree = def.getDefinition();
		if (definitionTree == null) return;
		Element root = definitionTree.getRootElement();
		//Find the element to move
		Element sourceItem = (Element) root.selectSingleNode("//item[@id='"+sourceItemId+"']");
		if (sourceItem == null) //The item to be moved is no longer defined as a valid item
			throw new DefinitionInvalidException("definition.error.noElement", new Object[] {defId});

		Element targetItem = (Element) root.selectSingleNode("//item[@id='"+targetItemId+"']");
		if (targetItem == null) 	//Target item is no longer defined as a valid item
			throw new DefinitionInvalidException("definition.error.noElement", new Object[] {defId});

		//We have found both the source and the target ids; do the move
		//Check that the target area is allowed to receive one of these types
		if (checkTargetOptions(definitionTree, targetItem, sourceItem, true)) {
			//Detach the source item
			Element newItem = (Element)sourceItem.clone();
			int nextId = Integer.valueOf(root.attributeValue("nextId")).intValue();
			newItem.addAttribute("id", (String) Integer.toString(nextId++));
			List<Element> subItems = newItem.selectNodes(".//item");
			for (Element sub:subItems) {
				sub.addAttribute("id", (String) Integer.toString(nextId++));
			}
			root.addAttribute("nextId", (String) Integer.toString(nextId));

			//Add it to the target element
			targetItem.add(newItem);
			//Write the new document back into the definition
			setDefinition(def, definitionTree);
		} else {
			//The target item is not designed to accept this item as a child
			throw new DefinitionInvalidException("definition.error.illegalMoveInto", new Object[] {defId});
		}
	}

	//Routine to check that the source item is allowed to be added to the target item type
	private boolean checkTargetOptions(Document definitionTree, Element target, Element source, boolean copyOperation) {
		String targetItemType = target.attributeValue("name");
		String sourceItemType = source.attributeValue("name");

		//check against base config document
		Element targetItem = (Element) this.configRoot.selectSingleNode("item[@name='"+targetItemType+"']");
		Element sourceItem = (Element) this.configRoot.selectSingleNode("item[@name='"+sourceItemType+"']");
		if (targetItem == null || sourceItem == null) {return false;}
		Boolean found=false;
		//Check the list of options (types: "option" and "option_select")
		Element options = targetItem.element("options");
		if (options != null) {
			Iterator itOptions = options.elementIterator("option");
			while (itOptions.hasNext()) {
				Element option = (Element) itOptions.next();
				if (option.attributeValue("name").equals(sourceItemType)) {
					found= true;
					break;
				}
			}
			if (!found) {
				itOptions = options.elementIterator("option_select");
				while (itOptions.hasNext()) {
					Element option = (Element) itOptions.next();
					String optionPath = option.attributeValue("path", "");
					Iterator itOptionsSelect = this.configRoot.selectNodes(optionPath).iterator();
					while (itOptionsSelect.hasNext()) {
						Element optionSelect = (Element) itOptionsSelect.next();
						if (optionSelect.attributeValue("name").equals(sourceItemType)) {
							found= true;
							break;
						}
					}
				}
			}
		}
		//None found, this isn't allowed
		if (!found) return false;
		if (copyOperation) {
			if ("false".equals(sourceItem.attributeValue("multipleAllowed")) &&
				target.getDocument().getRootElement().selectSingleNode("//item[@name='"+sourceItemType+"']") != null) return false;

			if ("false".equals(sourceItem.attributeValue("multipleAllowedInParent")) &&
					target.selectSingleNode("item[@name='"+sourceItemType+"']") != null) return false;

			//now check unique constraints
			String uniquePath = sourceItem.attributeValue("unique");
			if (Validator.isNull(uniquePath)) return true;
			//There is a request for uniqueness, so get the list from the definition file
			List<Element> items = target.selectNodes(uniquePath);
			if (items.isEmpty()) return true;
			return false;
		}
		return true;
	}

	private int populateNewDefinitionTree(Element source, Element target, final Element configRoot, int id, boolean includeDefault) {
		//See if the source has any options that are required
		Element options = source.element("options");
		if (options == null) return id;
		List lOptions = options.selectNodes("option");
		if (lOptions == null) return id;

		Iterator iOptions = lOptions.iterator();
		while (iOptions.hasNext()) {
			Element nextOption = (Element)iOptions.next();
			if (nextOption.attributeValue("initial", "").equals("true") ||
					(includeDefault && nextOption.attributeValue("default", "").equals("true"))) {
				//This option is required. Copy it to the target
				Element item = target.addElement("item");
				String name = nextOption.attributeValue("name");
				item.addAttribute("name", name);
				Element itemElement = (Element) configRoot.selectSingleNode("item[@name='"+name+"']");
				if (itemElement == null) {continue;}
				//Copy all of the attributes that should be in the definition
				String caption = itemElement.attributeValue("caption", nextOption.attributeValue("name"));

				item.addAttribute("caption", caption);

				String itemType = itemElement.attributeValue("type", "");
				if (!itemType.equals("")) item.addAttribute("type", itemType);
				item.addAttribute("id", Integer.toString(id));

				// Get the properties to be copied
				// (rsordillo) will add each 'property' Element 1 at a time. We want to remove some property
				// Attributes that are not needed for runtime.
				setDefinitionProperties(item, itemElement);

				//Bump up the unique id
				id++;

				//Now see if this item has some required options of its own
				id = populateNewDefinitionTree(itemElement, item, configRoot, id, includeDefault);
			}
		}
		Iterator iOptionSelects = options.selectNodes("option_select").iterator();
		while (iOptionSelects.hasNext()) {
			Element nextOptionSelect = (Element)iOptionSelects.next();
			if (nextOptionSelect.attributeValue("initial", "").equals("true") ||
					(includeDefault && nextOptionSelect.attributeValue("default", "").equals("true"))) {
				//This option_select is required. Process it and copy its items to the target
				String optionPath = nextOptionSelect.attributeValue("path", "");
				Iterator itOptionSelectItems = configRoot.selectNodes(optionPath).iterator();
				while (itOptionSelectItems.hasNext()) {
					nextOptionSelect = (Element)itOptionSelectItems.next();
					Element item = target.addElement("item");
					item.addAttribute("name", (String)nextOptionSelect.attributeValue("name"));
					Element itemElement = (Element) configRoot.selectSingleNode("item[@name='"+nextOptionSelect.attributeValue("name")+"']");
					if (itemElement == null) {continue;}
					String caption = itemElement.attributeValue("caption", nextOptionSelect.attributeValue("name"));
					item.addAttribute("caption", caption);
					item.addAttribute("id", Integer.toString(id));

					// Get the properties to be copied
					// (rsordillo) will add each 'property' Element 1 at a time. We want to remove some property
					// Attributes that are not needed for runtime.
					setDefinitionProperties(item, itemElement);


					id++;

					//Now see if this item has some required options of its own
					id = populateNewDefinitionTree(itemElement, item, configRoot, id, includeDefault);
				}
			}
		}
		return id;
	}

	//Rouitine to make sure a definition has all of the proper options as defined in the config file
	//  This is useful to propagate new items added to the config definition xml file
	private void validateDefinitionTree(Definition def) {
		Document defDoc = def.getDefinition();
		if (updateDefinitionTree(defDoc)) setDefinition(def, defDoc);
	}
	private boolean updateDefinitionTree(Document defDoc) {
		boolean defChanged = false;
		Element defRoot = defDoc.getRootElement();
		int startingId = Integer.valueOf(defRoot.attributeValue("nextId", "1")).intValue();
		int nextId = startingId;


		//Get the definition root element to check it
		Element configRootDefinitionEle = (Element) this.configRoot.selectSingleNode("//definition[@definitionType='"+
				defRoot.attributeValue("definitionType")+"']");
		if (configRootDefinitionEle != null) {
			//See if there are any items missing from the top definition item
			nextId = updateDefinitionTreeElement("definition", defRoot, defRoot, this.configRoot, nextId);

			//Look at all of the items to see if any of their options are missing
			Iterator itDefItems = defRoot.elementIterator("item");
			while (itDefItems.hasNext()) {
				Element defItem = (Element) itDefItems.next();
				nextId = updateDefinitionTreeElement("item", defItem, defItem, this.configRoot, nextId);
			}
			if (nextId != startingId) defChanged = true;
		}
		return defChanged;
	}
	private int updateDefinitionTreeElement(String elementType, Element source, Element target, Element configRoot, int id) {
		//Find the element type
		Element configItemElement;
		if (elementType.equals("definition")) {
			configItemElement = (Element) configRoot.selectSingleNode("item[@definitionType='"+
					source.attributeValue("definitionType")+"']");
		} else {
			configItemElement = (Element) configRoot.selectSingleNode("item[@name='"+
					source.attributeValue("name")+"']");
		}
		//See if the source has any required options that are missing
		Element options = configItemElement.element("options");
		if (options == null) return id;
		List lOptions = options.selectNodes("option");
		if (lOptions == null) return id;
		Iterator iOptions = lOptions.iterator();
		while (iOptions.hasNext()) {
			Element nextOption = (Element)iOptions.next();
			Element nextOptionConfigItem = (Element) configRoot.selectSingleNode("item[@name='"+
					nextOption.attributeValue("name")+"']");
			if (nextOption.attributeValue("initial", "").equals("true") &&
					nextOptionConfigItem.attributeValue("canBeDeleted", "").equals("false")) {
				//This option is required. See if it exists
				if (source.selectSingleNode("./item[@name='"+nextOptionConfigItem.attributeValue("name")+"']") == null) {
					//This option is missing. Copy it to the target
					Element item = target.addElement("item");
					item.addAttribute("name", (String)nextOption.attributeValue("name"));
					Element itemElement = (Element) configRoot.selectSingleNode("item[@name='"+nextOption.attributeValue("name")+"']");
					if (itemElement == null) {continue;}
					//Copy all of the attributes that should be in the definition
					String caption = itemElement.attributeValue("caption", nextOption.attributeValue("name"));
					item.addAttribute("caption", caption);
					String itemType = itemElement.attributeValue("type", "");
					if (!itemType.equals("")) item.addAttribute("type", itemType);
					item.addAttribute("id", Integer.toString(id));

					// Get the properties to be copied
					// (rsordillo) will add each 'property' Element 1 at a time. We want to remove some property
					// Attributes that are not needed for runtime.
					setDefinitionProperties(item, itemElement);

					//Bump up the unique id
					id++;

					//Now see if this item has some required options of its own
					id = updateDefinitionTreeElement("item", itemElement, item, configRoot, id);
				}
			}
		}
		Iterator iOptionSelects = options.selectNodes("option_select").iterator();
		while (iOptionSelects.hasNext()) {
			Element nextOptionSelect = (Element)iOptionSelects.next();
			if (nextOptionSelect.attributeValue("initial", "").equals("true") &&
					nextOptionSelect.attributeValue("canBeDeleted", "").equals("false")) {
				//This option_select is required. See if it exists
				if (source.selectSingleNode("./item[@name='"+nextOptionSelect.attributeValue("name")+"']") == null) {
					//This option_select is missing. Process it and copy its items to the target
					String optionPath = nextOptionSelect.attributeValue("path", "");
					Iterator itOptionSelectItems = configRoot.selectNodes(optionPath).iterator();
					while (itOptionSelectItems.hasNext()) {
						nextOptionSelect = (Element)itOptionSelectItems.next();
						Element item = target.addElement("item");
						item.addAttribute("name", (String)nextOptionSelect.attributeValue("name"));
						Element itemElement = (Element) configRoot.selectSingleNode("item[@name='"+nextOptionSelect.attributeValue("name")+"']");
						if (itemElement == null) {continue;}
						String caption = itemElement.attributeValue("caption", nextOptionSelect.attributeValue("name"));
						item.addAttribute("caption", caption);
						item.addAttribute("id", Integer.toString(id));

						// Get the properties to be copied
						// (rsordillo) will add each 'property' Element 1 at a time. We want to remove some property
						// Attributes that are not needed for runtime.
						setDefinitionProperties(item, itemElement);


						id++;

						//Now see if this item has some required options of its own
						id = updateDefinitionTreeElement("item", itemElement, item, configRoot, id);
					}
				}
			}
		}
		return id;
	}

	private Map getUniqueNameMap(final Element configRoot, Element definitionTree, String itemType) {
		Map uniqueNames = new HashMap();

		Element itemTypeEle = (Element) configRoot.selectSingleNode("item[@name='"+itemType+"']");
		if (itemTypeEle != null) {
			//See if this item requires a unique name
			String uniquePath = DefinitionUtils.getPropertyValue(itemTypeEle, "name", "unique");
			if (!Validator.isNull(uniquePath)) {
				//There is a request for uniqueness, so get the list from the definition file
				Iterator itNames = definitionTree.selectNodes(uniquePath).iterator();
				while (itNames.hasNext()) {
					//Find the name property of all items in the specified path
					String itemEleNameValue = DefinitionUtils.getPropertyValue((Element)itNames.next(), "name");
					if (!Validator.isNull(itemEleNameValue)) {
						//We found a name, so add it to the map
						uniqueNames.put(itemEleNameValue, itemEleNameValue);
					}
				}
			}
		}
		return uniqueNames;
	}


    public Document getDefinitionConfig() {
    	return this.definitionConfig;
    }

    public Map getEntryData(Document definitionTree, InputDataAccessor inputData, Map fileItems) {
		//access check not needed = have tree already

    	// entryData will contain the Map of entry data as gleaned from the input data
		Map entryDataAll = new HashMap();
		Map entryData = new HashMap();
		List fileData = new ArrayList();
		entryDataAll.put(ObjectKeys.DEFINITION_ENTRY_DATA, entryData);
		entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA, fileData);

		if (definitionTree != null) {
			//root is the root of the entry's definition
			Element root = definitionTree.getRootElement();

			//Get a list of all of the form items in the definition (i.e., from the "form" section of the definition)
			Element entryFormItem = (Element)root.selectSingleNode("item[@type='form']");
			if (entryFormItem != null) {
				//While going through the entry's elements, keep track of the current form name (needed to process date elements)
				List<Element> itItems = entryFormItem.selectNodes(".//item[@type='data']");
				//see if title is generated and save source
				boolean titleGenerated = false;
				String titleSource = null;
				Element titleEle = (Element)entryFormItem.selectSingleNode(".//item[@name='title']");
				if (titleEle != null) {
					titleGenerated = GetterUtil.get(DefinitionUtils.getPropertyValue(titleEle, "generated"), false);
					if (titleGenerated) {
						titleSource=DefinitionUtils.getPropertyValue(titleEle, "itemSource");
					}
				}
				for (Element nextItem: itItems) {
					String itemName = (String) nextItem.attributeValue("name", "");
					//Get the form element name (property name)
					String nameValue = DefinitionUtils.getPropertyValue(nextItem, "name");
					if (Validator.isNull(nameValue)) {nameValue = nextItem.attributeValue("name");}

					//We have the element name, see if it has a value in the input data
					if (itemName.equals("description") || itemName.equals("htmlEditorTextarea")) {
						if (inputData.exists(nameValue)) {
							Description description = new Description();
							description.setText(inputData.getSingleValue(nameValue));
							//Deal with any markup language transformations before storing the description
							MarkupUtil.scanDescriptionForUploadFiles(description, nameValue, fileData);
							MarkupUtil.scanDescriptionForAttachmentFileUrls(description);
							MarkupUtil.scanDescriptionForICLinks(description);
							entryData.put(nameValue, description);
						}
					} else if (itemName.equals("folderBranding") || itemName.equals("workspaceBranding")) {
						if (inputData.exists(nameValue)) {
							Description description = new Description();
							description.setText(inputData.getSingleValue(nameValue));
							//Deal with any markup language transformations before storing the description
							MarkupUtil.scanDescriptionForUploadFiles(description, nameValue, fileData);
							MarkupUtil.scanDescriptionForAttachmentFileUrls(description);
							MarkupUtil.scanDescriptionForICLinks(description);
							entryData.put(nameValue, description.getText());
						}
					} else if (itemName.equals("folderAttributeList")) {
						//The values are the names of the attribute sets
						if (inputData.exists(nameValue)) {
							String[] values = inputData.getValues(nameValue);
							List<String> valuesList = new ArrayList();
							for (int i = 0; i < values.length; i++) {
								if (!values[i].equals("")) {
									if (!inputData.exists(nameValue + "__delete__" + values[i]) ||
											!inputData.getSingleValue(nameValue + "__delete__" + values[i]).equals("on")) {
										if (!values[i].contains("__")) valuesList.add(values[i]);
									}
								}
							}
							String[] valuesTrimmed = new String[valuesList.size()];
							for (int i = 0; i < valuesList.size(); i++) valuesTrimmed[i] = valuesList.get(i);
							entryData.put(nameValue, valuesTrimmed);
							//Now see if there are any attributes added in a set
							for (String setName : valuesList) {
								if (inputData.exists(nameValue+ENTRY_ATTRIBUTES_SET+setName)) {
									String[] values2 = inputData.getValues(nameValue+ENTRY_ATTRIBUTES_SET+setName);
									List<String> valuesList2 = new ArrayList();
									for (int i = 0; i < values2.length; i++) {
										if (!values2[i].equals("")) {
											if (!inputData.exists(nameValue + "__delete__" + setName + "__" + values2[i]) ||
													!inputData.getSingleValue(nameValue + "__delete__" + setName + "__" + values2[i]).equals("on")) {
												if (!values2[i].contains("__")) valuesList2.add(values2[i]);
											}
										}
									}
									String[] valuesTrimmed2 = new String[valuesList2.size()];
									for (int i = 0; i < valuesList2.size(); i++) valuesTrimmed2[i] = valuesList2.get(i);
									entryData.put(nameValue+ENTRY_ATTRIBUTES_SET+setName, valuesTrimmed2);
									if (inputData.exists(nameValue+ENTRY_ATTRIBUTES_SET_MULTIPLE_ALLOWED+setName) &&
											inputData.getSingleValue(nameValue+ENTRY_ATTRIBUTES_SET_MULTIPLE_ALLOWED+setName).equals("on")) {
										entryData.put(nameValue+ENTRY_ATTRIBUTES_SET_MULTIPLE_ALLOWED+setName, true);
									} else {
										entryData.put(nameValue+ENTRY_ATTRIBUTES_SET_MULTIPLE_ALLOWED+setName, false);
									}
								}
							}
						}
					} else if (itemName.equals("entryAttributes")) {
						//The values are the names of the attribute sets
						if (inputData.exists(nameValue)) {
							String[] values = inputData.getValues(nameValue);
							List<String> valuesList = new ArrayList();
							for (int i = 0; i < values.length; i++) {
								if (!values[i].equals("")) {
									valuesList.add(values[i]);
								}
							}
							String[] valuesTrimmed = new String[valuesList.size()];
							for (int i = 0; i < valuesList.size(); i++) valuesTrimmed[i] = valuesList.get(i);
							entryData.put(nameValue, valuesTrimmed);
							//Now see if there are any attributes added in a set
							for (String setName : valuesList) {
								if (inputData.exists(nameValue+ENTRY_ATTRIBUTES_SET+setName)) {
									String[] values2 = inputData.getValues(nameValue+ENTRY_ATTRIBUTES_SET+setName);
									List<String> valuesList2 = new ArrayList();
									for (int i = 0; i < values2.length; i++) {
										if (!values2[i].equals("")) {
											valuesList2.add(values2[i]);
										}
									}
									String[] valuesTrimmed2 = new String[valuesList2.size()];
									for (int i = 0; i < valuesList2.size(); i++) valuesTrimmed2[i] = valuesList2.get(i);
									entryData.put(nameValue+ENTRY_ATTRIBUTES_SET+setName, valuesTrimmed2);
								}
							}
						}
					} else if (itemName.equals("date") || itemName.equals("date_time")) {
						if (inputData.exists(nameValue)) {
							//Use the helper routine to parse the date into a date object
							Date date = inputData.getDateValue(nameValue);
							if (date != null) {entryData.put(nameValue, date);}
						}
					} else if (itemName.equals("event")) {
					    //Ditto for event helper routine
					    Boolean hasDur = Boolean.FALSE;
					    if (GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "hasDuration"), false)) {
					    	hasDur = Boolean.TRUE;
					    }
					    Boolean hasRecur = Boolean.FALSE;
					    if (GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "hasRecurrence"), false)) {
					    	hasRecur = Boolean.TRUE;
					    }
					    Event event = inputData.getEventValue(nameValue, hasDur, hasRecur);
					    if (event != null) {
					        event.setName(nameValue);
					        entryData.put(nameValue, event);
					    }
					} else if (itemName.equals("survey")) {
						if (inputData.exists(nameValue)) {
							//Use the helper routine to parse the date into a date object
							Survey survey = inputData.getSurveyValue(nameValue);
							if (survey != null) entryData.put(nameValue, survey);
						}
					} else if (itemName.equals("user_list") || itemName.equals("group_list") ||
								itemName.equals("team_list") || itemName.equals("userListSelectbox")) {
						if (inputData.exists(nameValue)) {
							Set<Long> ids = LongIdUtil.getIdsAsLongSet(inputData.getValues(nameValue));
							CommaSeparatedValue v = new CommaSeparatedValue();
							v.setValue(ids);
							entryData.put(nameValue, v);
						}
					} else if (itemName.equals("places")) {
						Set<Long> longIdsToRemove = new HashSet();
						String toRemovePlacesParamName = WebKeys.URL_ID_CHOICES_REMOVE + "_" + nameValue;
						if (inputData.exists(toRemovePlacesParamName)) {
							String[] idChoicesToRemove = inputData.getValues(toRemovePlacesParamName);
							for (int i = 0; i < idChoicesToRemove.length; i++) {
								try {
									//	validate as long
									longIdsToRemove.add(Long.parseLong(idChoicesToRemove[i]));
								} catch (NumberFormatException ne) {}
							}
						}

						if (inputData.exists(WebKeys.URL_ID_CHOICES)) {
							java.util.Collection<Long> longIds = TreeHelper.getSelectedIds(inputData, nameValue);
							CommaSeparatedValue v = new CommaSeparatedValue();
							longIds.removeAll(longIdsToRemove);
							v.setValue(longIds);
							entryData.put(nameValue, v);
						} else if (inputData.exists(nameValue)) {
							Set<Long> ids = LongIdUtil.getIdsAsLongSet(inputData.getValues(nameValue));
							ids.removeAll(longIdsToRemove);
							CommaSeparatedValue v = new CommaSeparatedValue();
							v.setValue(ids);
							entryData.put(nameValue, v);
						}
					} else if (itemName.equals("guestName")) {
						if (inputData.exists(nameValue)) {
					    	entryData.put(nameValue, inputData.getValues(nameValue));
						}
					} else if (itemName.equals("selectbox")) {
						if (inputData.exists(nameValue)) {
					    	String multiple = DefinitionUtils.getPropertyValue(nextItem, "multipleAllowed");
					    	if ("true".equals(multiple)) {
					    		entryData.put(nameValue, inputData.getValues(nameValue));
					    	} else {
					    		entryData.put(nameValue, inputData.getSingleValue(nameValue));
					    	}
						}
					} else if (itemName.equals("checkbox")) {
						if (inputData.exists(nameValue)) {
							entryData.put(nameValue, Boolean.valueOf(GetterUtil.getBoolean(inputData.getSingleValue(nameValue), false)));
						}
					} else if (itemName.equals("profileTimeZone")) {
						if (inputData.exists(nameValue)) {
							Object val = inputData.getSingleObject(nameValue);
							if (val == null) {
								entryData.put(nameValue, null);
							} else if (val instanceof TimeZone) {
								entryData.put(nameValue, TimeZoneHelper.fixTimeZone((TimeZone)val));
							} else {
								String sVal = inputData.getSingleValue(nameValue);
								if (Validator.isNull(sVal)) entryData.put(nameValue, null);
								else entryData.put(nameValue, TimeZoneHelper.getTimeZone(sVal));
							}
						}
					} else if (itemName.equals("profileLocale")) {
						if (inputData.exists(nameValue)) {
							Object val = inputData.getSingleObject(nameValue);
							if (val == null) {
								entryData.put(nameValue, null);
							} else if (val instanceof Locale) {
								entryData.put(nameValue, (Locale)val);
							} else {
								String sVal = inputData.getSingleValue(nameValue);
								if (Validator.isNull(sVal)) entryData.put(nameValue, null);
								else {
									String[] vals = sVal.split("_");
									if (vals.length == 1) entryData.put(nameValue, new Locale(vals[0]));
									else if (vals.length == 2) entryData.put(nameValue, new Locale(vals[0], vals[1]));
									else if (vals.length >= 3) entryData.put(nameValue, new Locale(vals[0], vals[1], vals[2]));
								}
							}
						}
					} else if (itemName.equals("file") || itemName.equals("graphic") ||
							itemName.equals("profileEntryPicture")) {
					    if (fileItems != null && fileItems.containsKey(nameValue)) {
					    	MultipartFile myFile = (MultipartFile)fileItems.get(nameValue);
					    	String fileName = myFile.getOriginalFilename();
					    	if (fileName.equals("")) continue;
					    	String repositoryName = DefinitionUtils.getPropertyValue(nextItem, "storage");
					    	if (Validator.isNull(repositoryName)) repositoryName = RepositoryUtil.getDefaultRepositoryName();
					    	FileUploadItem fui;
					    	if (titleGenerated && nameValue.equals(titleSource) &&
					    			(itemName.equals("file") || itemName.equals("graphic")))
					    		fui = new FileUploadItem(FileUploadItem.TYPE_TITLE, nameValue, myFile, repositoryName);
					    	else fui = new FileUploadItem(FileUploadItem.TYPE_FILE, nameValue, myFile, repositoryName);
						    	//See if there is a scaling request for this graphic file. If yes, pass along the hieght and width
			    			fui.setMaxWidth(GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "maxWidth"), 0));
			    			fui.setMaxHeight(GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "maxHeight"), 0));
					    	// TODO The following piece of code may need a better conditional
					    	// statement than this, since we probably do not want to generate
					    	// thumbnails for all graphic-type file uploads. Or do we?
					    	if (itemName.equals("graphic")) {
					    		fui.setGenerateThumbnail(true);
					    		fui.setIsSquareThumbnail(true);
					    	} else if (itemName.equals("profileEntryPicture")) {
					    		fui.setGenerateThumbnail(true);
					    	} else if (fileName.endsWith(".jpg")) {
					    		fui.setGenerateThumbnail(true);
					    		fui.setIsSquareThumbnail(true);
					    	}
					    	if(inputData.exists(ObjectKeys.PI_SYNCH_TO_SOURCE)) {
					    		fui.setSynchToRepository(Boolean.parseBoolean(inputData.getSingleValue(ObjectKeys.PI_SYNCH_TO_SOURCE)));
					    	}
					    	fileData.add(fui);
					    }
					} else if (itemName.equals("attachFiles")) {
					    if (fileItems != null) {
					    	boolean blnCheckForFileUntilTrue = true;
					    	int intFileCount = 1;
					    	while (blnCheckForFileUntilTrue) {
								String fileEleName = nameValue + Integer.toString(intFileCount);
								if (fileItems.containsKey(fileEleName)) {
							    	MultipartFile myFile = (MultipartFile)fileItems.get(fileEleName);
							    	String fileName = myFile.getOriginalFilename();
							    	if (fileName != null && !fileName.equals("")) {
								    	// Different repository can be specified for each file uploaded.
								    	// If not specified, use the statically selected one.
								    	String repositoryName = null;
								    	if (inputData.exists(nameValue + "_repos" + Integer.toString(intFileCount)))
								    		repositoryName = inputData.getSingleValue(nameValue + "_repos" + Integer.toString(intFileCount));
								    	if (repositoryName == null) {
									    	repositoryName = DefinitionUtils.getPropertyValue(nextItem, "storage");
									    	if (Validator.isNull(repositoryName)) repositoryName = RepositoryUtil.getDefaultRepositoryName();
								    	}
								    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_ATTACHMENT, null, myFile, repositoryName);
								    	if(inputData.exists(ObjectKeys.PI_SYNCH_TO_SOURCE)) {
								    		fui.setSynchToRepository(Boolean.parseBoolean(inputData.getSingleValue(ObjectKeys.PI_SYNCH_TO_SOURCE)));
								    	}
								    	fileData.add(fui);
							    	}
							    	intFileCount++;
								} else {
									blnCheckForFileUntilTrue = false;
								}
					    	}
					    }
					} else if (itemName.equals("mashupCanvas")) {
						Boolean showBranding = false;
						Boolean hideMasthead = false;
						Boolean hideSidebar = false;
						Boolean hideToolbar = false;
						Boolean hideFooter = false;
						if (inputData.exists(nameValue + MASHUP_SHOW_BRANDING)) showBranding = true;
						if (inputData.exists(nameValue + MASHUP_HIDE_MASTHEAD)) hideMasthead = true;
						if (inputData.exists(nameValue + MASHUP_HIDE_SIDEBAR)) hideSidebar = true;
						if (inputData.exists(nameValue + MASHUP_HIDE_TOOLBAR)) hideToolbar = true;
						if (inputData.exists(nameValue + MASHUP_HIDE_FOOTER)) hideFooter = true;
						if (inputData.exists(nameValue + "__idCounter")) {
							int idCounter = Integer.valueOf(inputData.getSingleValue(nameValue + "__idCounter"));
							String value = "";
							for (int i = 0; i <= idCounter; i++) {
								String nextValue = inputData.getSingleValue(nameValue + "__" + String.valueOf(i));
								if (nextValue != null && !nextValue.equals("")) {
									if (!value.equals("")) value = value + ";";
									String type = nextValue.split(",")[0];
					        		String[] mashupItemValues = nextValue.split(",");
					        		String attrs = "";
									Map mashupItemAttributes = new HashMap();
									if (mashupItemValues.length > 0) {
										//Build a map of attributes
										for (int j = 0; j < mashupItemValues.length; j++) {
											String[] valueSet = mashupItemValues[j].split("=");
											if (valueSet.length == 2) {
												mashupItemAttributes.put(valueSet[0], valueSet[1]);
												attrs += ","+valueSet[0]+"="+valueSet[1];
											}
										}
									}
									if (type == null || type.equals("")) {
										//Delete empty or badly formed items
										nextValue = "";
									} else if (type.equals(ObjectKeys.MASHUP_TYPE_TABLE)) {
										int colCount = 2;
										if (mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_COLS)) 
											colCount = Integer.valueOf((String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_COLS));
										String[] colWidths = new String[colCount];
										if (mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_COL_WIDTHS)) {
											String colWidths2 = (String)mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_COL_WIDTHS);
											for (int j = 0; j < colCount; j++) {
												String colWidth = colWidths2;
												if (colWidths2.indexOf("|") >= 0) {
													colWidth = colWidths2.substring(0, colWidths2.indexOf("|"));
													colWidths2 = colWidths2.substring(colWidths2.indexOf("|")+1, colWidths2.length());
												}
												colWidths[j] = colWidth;
											}
										}
										nextValue = ObjectKeys.MASHUP_TYPE_TABLE_START + attrs + ";";
										for (int j = 0; j < colCount; j++) {
											nextValue += ObjectKeys.MASHUP_TYPE_TABLE_COL + ",";
											nextValue += ObjectKeys.MASHUP_ATTR_COL_WIDTH + "=" + colWidths[j] + ";";
										}
										nextValue += ObjectKeys.MASHUP_TYPE_TABLE_END;
									} else if (type.equals(ObjectKeys.MASHUP_TYPE_TABLE_END_DELETE)) {
										//This is a request to delete a table
										// Delete all the way back to the "tableStart"
										// Only delete empty tables
										int j = value.lastIndexOf(ObjectKeys.MASHUP_TYPE_TABLE_START + ",");
										if (j >= 0) {
											value = value.substring(0, j);
										} else {
											value="";
										}
										nextValue = "";
									}
									value = value + nextValue;
								}
							}
							entryData.put(nameValue, value);
							entryData.put(nameValue + "__showBranding", showBranding);
							entryData.put(nameValue + "__hideMasthead", hideMasthead);
							entryData.put(nameValue + "__hideSidebar", hideSidebar);
							entryData.put(nameValue + "__hideToolbar", hideToolbar);
							entryData.put(nameValue + "__hideFooter", hideFooter);
						}
					} else {
						if (inputData.exists(nameValue)) entryData.put(nameValue, inputData.getSingleValue(nameValue));
					}
				}
			}
		}

    	return entryDataAll;
    }
    public List<Definition> getAllDefinitions() {
		// Controllers need access to definitions.  Allow world read
    	return coreDao.loadDefinitions(RequestContextHolder.getRequestContext().getZoneId());
    }

    public List<Definition> getAllDefinitions(Integer type) {
		// Controllers need access to definitions.  Allow world read
    	FilterControls filter = new FilterControls();
    	filter.add("type", type);
    	return coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
     }

    public List<Definition> getDefinitions(Long binderId, Boolean includeAncestors) {
		// Controllers need access to definitions.  Allow world read
       	if (binderId == null) {
        	FilterControls filter = new FilterControls()
        		.add(Restrictions.isNull("binderId"));
        	return coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
    	}
    	try {
    		Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
   	 		if (includeAncestors.equals(Boolean.TRUE)) {
   	 			Map params = new HashMap();
   	 			params.put("binderId", getAncestorIds(binder));
   	 			params.put("zoneId", RequestContextHolder.getRequestContext().getZoneId());
   	 			return coreDao.loadObjects("from com.sitescape.team.domain.Definition where (binderId is null and zoneId=:zoneId) or binderId in (:binderId)", params);
   	 		} else {
   	 			FilterControls filter = new FilterControls()
   	 			.add(Restrictions.eq("binderId", binder.getId()));
   	 			return coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
   	 		}
    	} catch (NoBinderByTheIdException nb) {
  	 		if (includeAncestors.equals(Boolean.TRUE)) {
   	 	       	return getDefinitions(null, Boolean.TRUE);
  	 		} else {
    	 		return new ArrayList();
   	 		}

    	}
    }

    public List<Definition> getDefinitions(Long binderId, Boolean includeAncestors, Integer type) {
		// Controllers need access to definitions.  Allow world read
    	if (binderId == null) {
        	FilterControls filter = new FilterControls()
         		.add(Restrictions.eq("type", type))
        		.add(Restrictions.isNull("binderId"));
        	return coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
    	}
    	Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
   	 	if (includeAncestors.equals(Boolean.TRUE)) {
  	       	Map params = new HashMap();
  	    	params.put("type", type);
   	 		params.put("binderId", getAncestorIds(binder));
   	 		params.put("zoneId", RequestContextHolder.getRequestContext().getZoneId());

   	    	return coreDao.loadObjects("from com.sitescape.team.domain.Definition where ((binderId is null and zoneId=:zoneId) or binderId in (:binderId)) and type=:type", params);
  	 	} else {
  	      	FilterControls filter = new FilterControls()
  	      		.add(Restrictions.eq("type", type))
  	      		.add(Restrictions.eq("binderId", binder.getId()));
 	    	return coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
  	 	}

    }
    private List<Long> getAncestorIds(Binder binder) {
    	ArrayList<Long> bs = new ArrayList();
    	while (binder != null) {
    	   	bs.add(binder.getId());
    	   	binder = binder.getParentBinder();
    	}
    	return bs;
    }
	//Routine to get the data elements for use in search queries
    public Map getEntryDefinitionElements(String id) {
		//Get a map for the results
		//access doesn't seem needed
    	Map dataElements = new TreeMap();
    	Definition def=null;
		try {
			def = getCoreDao().loadDefinition(id, RequestContextHolder.getRequestContext().getZoneId());
		} catch (NoDefinitionByTheIdException nd) {
			return dataElements;
		}

		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			//root is the root of the entry's definition
			Element root = definitionTree.getRootElement();

			//Get a list of all of the form items in the definition (i.e., from the "form" section of the definition)
			Element entryFormItem = (Element)root.selectSingleNode("item[@type='form']");
			if (entryFormItem != null) {
				List<Element> itItems = entryFormItem.selectNodes(".//item[@type='data']");
				for (Element nextItem: itItems) {
					//Get a map to store the results in
					Map itemData = new HashMap();

					String itemName = (String) nextItem.attributeValue("name", "");
					itemData.put("type", itemName);

					String nameValue = DefinitionUtils.getPropertyValue(nextItem, "name");
					if (Validator.isNull(nameValue)) nameValue = itemName;
					itemData.put("name", nameValue);

					String captionValue = DefinitionUtils.getPropertyValue(nextItem, "caption");
					if (Validator.isNull(captionValue)) captionValue = nameValue;
					itemData.put("caption", NLT.getDef(captionValue).replaceAll("&", "&amp;"));

					//We have the element name, see if it has option values
					if (itemName.equals("selectbox")) {
						Map valueMap = new LinkedHashMap();
						Iterator itSelectionItems = nextItem.selectNodes("item[@name='selectboxSelection']").iterator();
						while (itSelectionItems.hasNext()) {
							Element selection = (Element) itSelectionItems.next();
							//Get the element name (property name)
							String selectionNameValue = DefinitionUtils.getPropertyValue(selection, "name");
							String selectionCaptionValue = DefinitionUtils.getPropertyValue(selection, "caption");
							if (Validator.isNotNull(selectionNameValue)) {
								if (Validator.isNull(selectionCaptionValue)) {selectionCaptionValue = selectionNameValue;}
								valueMap.put(selectionNameValue, NLT.getDef(selectionCaptionValue).replaceAll("&", "&amp;"));
							}
						}
						itemData.put("length", new Integer(valueMap.size()).toString());
						if (valueMap.size() > 10) itemData.put("length", "10");
						itemData.put("values", valueMap);

					} else if (itemName.equals("radio")) {
						Map valueMap = new TreeMap();
						Iterator itSelectionItems = nextItem.selectNodes("item[@name='radioSelection']").iterator();
						while (itSelectionItems.hasNext()) {
							Element selection = (Element) itSelectionItems.next();
							//Get the element name (property name)
							String selectionNameValue = DefinitionUtils.getPropertyValue(selection, "name");
							String selectionCaptionValue = DefinitionUtils.getPropertyValue(selection, "caption");
							if (Validator.isNotNull(selectionNameValue)) {
								if (Validator.isNull(selectionCaptionValue)) {selectionCaptionValue = selectionNameValue;}
								valueMap.put(selectionNameValue, NLT.getDef(selectionCaptionValue).replaceAll("&", "&amp;"));
							}
						}
						itemData.put("length", new Integer(valueMap.size()).toString());
						if (valueMap.size() > 10) itemData.put("length", "10");
						itemData.put("values", valueMap);
					}

					//Add this element to the results
					dataElements.put(nameValue, itemData);
				}
			}
		}

    	return dataElements;
    }

	//Routine to get the data elements for use in search queries
    public Map getWorkflowDefinitionStates(String id) {
		//Get a map for the results
    	Map dataStates = new TreeMap();
    	Definition def=null;
		try {
			def = getCoreDao().loadDefinition(id, RequestContextHolder.getRequestContext().getZoneId());
		} catch (NoDefinitionByTheIdException nd) {
			return dataStates;
		}

		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			//root is the root of the entry's definition
			Element root = definitionTree.getRootElement();

			//Get a list of all of the state items in the definition
			Iterator itItems = root.selectNodes("item[@name='workflowProcess']/item[@name='state']").listIterator();
			while (itItems.hasNext()) {
				//Get a map to store the results in
				Map itemData = new HashMap();

				Element nextItem = (Element) itItems.next();
				String itemName = (String) nextItem.attributeValue("name", "");
				itemData.put("type", itemName);

				String nameValue = DefinitionUtils.getPropertyValue(nextItem, "name");
				if (Validator.isNull(nameValue)) nameValue = itemName;

				String captionValue = DefinitionUtils.getPropertyValue(nextItem, "caption");
				if (Validator.isNull(captionValue)) captionValue = nameValue;
				itemData.put("caption", NLT.getDef(captionValue).replaceAll("&", "&amp;"));

				//Add this state to the results
				dataStates.put(nameValue, itemData);
			}
		}

    	return dataStates;
    }

    /**
     * Manipulate base configuration properties/property Elements before adding to entry configuration.
     * This will reduce the size of the entry configuration by not adding un-required Element data.
     *
     * @param parent		What Element we will add the properties Element too
     * @param configItem	Base configuration Item Element that we are copying properties Element from
     *
     */
    private void setDefinitionProperties(Element parent, final Element configItem)    {
    	Element properties = parent.addElement("properties");
    	List<Element> propertyItems = propertyItems = configItem.selectNodes("properties/property");
		for (Element configProperty:propertyItems)
		{
			Element property = properties.addElement("property");
			property.addAttribute("name", configProperty.attributeValue("name"));
			property.addAttribute("value", configProperty.attributeValue("value", ""));
		}

		return;
    }

	public void walkDefinition(DefinableEntity entry, DefinitionVisitor visitor, Map args) {
		SimpleProfiler.startProfiler("DefinitionModuleImpl.walkDefinition");
		//access check not needed = assumed okay from entry
        Definition def = entry.getEntryDef();
        if(def == null) return;
        String flagElementPath = "./" + visitor.getFlagElementName();
        Document definitionTree = def.getDefinition();
        if (definitionTree != null) {
            Element root = definitionTree.getRootElement();

            //Get a list of all of the items in the definition
			Element entryFormItem = (Element)root.selectSingleNode("item[@type='form']");
            if (entryFormItem != null) {
                List<Element> items = entryFormItem.selectNodes(".//item[@type='data']");
                if (items != null) {
                    for (Element nextItem:items) {

                    	Element flagElem = (Element) nextItem.selectSingleNode(flagElementPath);
                    	if (flagElem == null) {
                        	 // The current item in the entry definition does not contain
                        	 // the flag element. Check the corresponding item in the default
                        	 // config definition to see if it has it.
                        	 // This two level mechanism allows entry definition (more specific
                        	 // one) to override the settings in the default config definition
                        	 // (more general one). This overriding works in its
                        	 // entirity only, that is, partial overriding is not supported.
     						//Find the item in the base configuration definition to see if it is a data item
                    		String itemName = (String) nextItem.attributeValue("name");
     						Element configItem = this.definitionBuilderConfig.getItem(this.definitionConfig, itemName);
     						if (configItem != null) flagElem = (Element) configItem.selectSingleNode(flagElementPath);
                    	}

                    	if (flagElem != null) {
                        	 Map oArgs = DefinitionUtils.getOptionalArgs(flagElem);
                        	 //add in caller supplied arguments
                        	 if (args != null) oArgs.putAll(args);
                        	 visitor.visit(nextItem, flagElem, oArgs);
                        }
                    }
                }
            }
        }
		SimpleProfiler.stopProfiler("DefinitionModuleImpl.walkDefinition");
    }

}
