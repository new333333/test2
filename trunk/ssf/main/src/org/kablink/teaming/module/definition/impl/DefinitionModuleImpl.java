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
package org.kablink.teaming.module.definition.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Locale;

import javax.mail.internet.InternetAddress;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpSession;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import org.kablink.teaming.DefinitionExistsException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.context.request.SessionContext;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.Restrictions;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CommaSeparatedValue;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.DefinitionInvalidException;
import org.kablink.teaming.domain.Description;
import org.kablink.teaming.domain.EncryptedValue;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.domain.NoPrincipalByTheNameException;
import org.kablink.teaming.domain.PackedValue;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.EntryDataErrors;
import org.kablink.teaming.module.binder.impl.EntryDataErrors.Problem;
import org.kablink.teaming.module.definition.DefinitionConfigurationBuilder;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workflow.WorkflowProcessUtils;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.survey.Survey;
import org.kablink.teaming.util.FileUploadItem;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.ResolveIds;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.util.LocaleUtils;
import org.kablink.teaming.util.cache.DefinitionCache;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.util.XmlUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.TreeHelper;
import org.kablink.teaming.web.util.DefinitionHelper;
import org.kablink.teaming.web.util.MarkupUtil;
import org.kablink.util.GetterUtil;
import org.kablink.util.Html;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.multipart.MultipartFile;

import org.w3c.tidy.Tidy;
import org.w3c.tidy.TidyMessage;

/**
 * ?
 * 
 * @author hurley
 */
@SuppressWarnings({"deprecation", "unchecked", "unused"})
public class DefinitionModuleImpl extends CommonDependencyInjection implements DefinitionModule, InitializingBean  {
	private static String[] entryInputDataMap;
	private static int      entryInputDataMapCount = (-1);
	
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
	protected ProfileModule profileModule;
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
   	protected ProfileModule getProfileModule() {
   		if (profileModule == null) {
   			profileModule = (ProfileModule) SpringContextUtil.getBean("profileModule");
   		}
		return profileModule;
	}

   	protected WorkflowModule workflowModule;

	public void setWorkflowModule(WorkflowModule workflowModule) {
		this.workflowModule = workflowModule;
	}
	protected WorkflowModule getWorkflowModule() {
		return workflowModule;
	}
    @Override
	public void afterPropertiesSet() {
		this.definitionConfig = definitionBuilderConfig.getAsMergedDom4jDocument();
		this.configRoot = this.definitionConfig.getRootElement();


    }
    /*
     *  (non-Javadoc)
 	 * Use operation so we can keep the logic out of application
     * @see org.kablink.teaming.module.definition.DefinitionModule#testAccess(java.lang.String)
     */
   	@Override
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
   				getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);
   			} else {
  				if (getAccessControlManager().testOperation(binder, WorkAreaOperation.MANAGE_ENTRY_DEFINITIONS)) return;
  				getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
   			}
   		} else if (type.equals(Definition.WORKFLOW)) {
   			if (binder ==  null) {
   				if (getAccessControlManager().testOperation(top, WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS)) return;
   				getAccessControlManager().checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);
   			} else {
  				if (getAccessControlManager().testOperation(binder, WorkAreaOperation.MANAGE_WORKFLOW_DEFINITIONS)) return;
   				getAccessControlManager().checkOperation(binder, WorkAreaOperation.BINDER_ADMINISTRATION);
   			}
   		} else {
   			accessControlManager.checkOperation(getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId()), WorkAreaOperation.ZONE_ADMINISTRATION);
   		}

   	}
   	protected void checkAccess(Definition def, DefinitionOperation operation) throws AccessControlException {
   		if (ObjectKeys.RESERVED_BINDER_ID.equals(def.getBinderId())) checkAccess(null, def.getType(), operation);
   		else checkAccess(getCoreDao().loadBinder(def.getBinderId(), def.getZoneId()), def.getType(), operation);
   	}

   	@Override
	public Definition addDefinition(InputStream indoc, Binder binder, String name, String title, boolean replace) 
	throws AccessControlException,DocumentException {
   		List errors = new ArrayList();
   		return addDefinition(indoc, binder, name, title, replace, errors);
   	}
   	
	@Override
	public Definition addDefinition(InputStream indoc, Binder binder, String name, String title, boolean replace, List errors) 
		throws AccessControlException,DocumentException {
	/*The current xsd is really for the configuration file.  The export defintions don't follow all the rules,
	  xsd:sequence in particular.  Until we either fix this or build a new xsd, this validating code is disabled.
		SAXReader xIn = XmlUtil.getSAXReader(true);
        // The following code turns on XML schema-based validation
        // features specific to Apache Xerces2 parser. Therefore it
        // will not work when a different parser is used.
		xIn.setFeature("http://apache.org/xml/features/validation/schema", true); // Enables XML Schema validation
		xIn.setFeature("http://apache.org/xml/features/validation/schema-full-checking",true); // Enables full (if slow) schema checking
		xIn.setProperty(
                "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation",
                DirPath.getDTDDirPath() + File.separator + "definition_builder_config.xsd");
	 */
		SAXReader xIn = XmlUtil.getSAXReader(false);
		Document doc = xIn.read(indoc);
		String type = doc.getRootElement().attributeValue("type");
	   	checkAccess(binder, Integer.valueOf(type), DefinitionOperation.manageDefinition);
    	return doAddDefinition(doc, binder, name, title, replace, errors);

	}
	@Override
	public Definition addDefinition(Document defDoc, Binder binder, boolean replace) {
		String type = defDoc.getRootElement().attributeValue("type");
	   	checkAccess(binder, Integer.valueOf(type), DefinitionOperation.manageDefinition);
    	return doAddDefinition(defDoc, binder, null, null, replace);
	}

	@Override
	public Definition copyDefinition(String id, Binder binder, String name, String title) throws AccessControlException {
		Definition srcDef = getDefinition(id);
		Document doc = (Document)srcDef.getDefinitionForModificationPurpose();
		doc.getRootElement().addAttribute("internalId", "");
		doc.getRootElement().addAttribute("databaseId", "");
		String type = doc.getRootElement().attributeValue("type");
	   	checkAccess(binder, Integer.valueOf(type), DefinitionOperation.manageDefinition);
    	return doAddDefinition(doc, binder, name, title, false);
	}
	@Override
	public Definition addDefinition(Binder binder, String name, String title, Integer type, InputDataAccessor inputData) throws AccessControlException {
	   	checkAccess(binder, type, DefinitionOperation.manageDefinition);

		Definition newDefinition = new Definition();
		newDefinition.setName(name);
		newDefinition.setTitle(title);
		newDefinition.setType(type);
		newDefinition.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		if (binder != null) newDefinition.setBinderId(binder.getId());
    	newDefinition.setCreation(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
		getCoreDao().save(newDefinition);
		Document doc = getInitialDefinition(name, title, type, inputData);
		setDefinition(newDefinition, doc);
		return newDefinition;

	}

    protected Definition doAddDefinition(Document doc, Binder binder, String name, String title, boolean replace) {
    	List errors = new ArrayList();
    	return doAddDefinition(doc, binder, name, title, replace, errors);
    }
    protected Definition doAddDefinition(Document doc, Binder binder, String name, String title, boolean replace, List errors) {
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
				if (!replace) errors.add(NLT.get("definition.error.alreadyExistsByName") + " (" + name + ": " + title + ")");
				if (!type.equals(def.getType()) ) 
					throw new DefinitionInvalidException("definition.error.internalAlreadyExists", new Object[] {internalId});
				if (!replace) return def;
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
						( (binder == null &&  ObjectKeys.RESERVED_BINDER_ID.equals(def.getBinderId()) ) ||
						  (binder != null && binder.getId() == null ) ||
						  (binder != null && binder.getId().equals(def.getBinderId())))) {
					if (!replace) errors.add(NLT.get("definition.error.alreadyExistsByName") + " (" + name + ": " + title + ")");
					if (!type.equals(def.getType())) 
						throw new DefinitionInvalidException("definition.error.idAlreadyExists", new Object[] {id});
					if (!replace) return def;
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
			if (!replace) errors.add(NLT.get("definition.error.alreadyExistsByName") + " (" + name + ": " + title + ")");
			if (!type.equals(def.getType())) 
				throw new DefinitionInvalidException("definition.error.nameNotUnique", new Object[] {name});
			if (!replace) return def;

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
		else def.setBinderId(ObjectKeys.RESERVED_BINDER_ID);
    	def.setCreation(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
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
   		doc.getRootElement().addAttribute("caption", Html.replaceSpecialChars(def.getTitle()));
   		newPropertiesEle = (Element)doc.getRootElement().selectSingleNode("./properties/property[@name='caption']");
   		if (newPropertiesEle != null) newPropertiesEle.addAttribute("value", Html.replaceSpecialChars(def.getTitle()));

    	//Write out the new definition file
    	def.setDefinition(doc);
    	def.setModification(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
    	
    	//the document in the cache is no longer valid, it needs to be reloaded
       	DefinitionCache.invalidate(def.getId());
       	
		//If this is a workflow definition, build the corresponding JBPM workflow definition
    	if (def.getType() == Definition.WORKFLOW) {
    		
    		//the definition object needs to be reloaded in order for the workflow process definition to update properly
    		Definition definition = getCoreDao().loadDefinition(def.getId(), RequestContextHolder.getRequestContext().getZoneId());

    		//Use the definition id as the workflow process name
    		getWorkflowModule().modifyProcessDefinition(definition.getId(), definition);
    	}
    }
    //should be called after all imports are done, to handle definition cross references
    @Override
	public void updateDefinitionReferences(String defId) {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	Definition def = getCoreDao().loadDefinition(defId, zoneId);
    	Binder binder = null;
    	Long binderId = def.getBinderId();
    	if (binderId != null && ObjectKeys.RESERVED_BINDER_ID.longValue() != binderId.longValue())
    	{
    		binder = getCoreDao().loadBinder(def.getBinderId(), zoneId);
    	}
    	Document doc = def.getDefinitionForModificationPurpose();
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

    @Override
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
		List<Definition> definitions = filterDefinitions(getCoreDao().loadObjects(definitionIds, Definition.class, zoneId));
		for (Definition d:definitions) {
			Element definitionlEle = exports.addElement("export");
			definitionlEle.addAttribute("definitionId", d.getId());
			definitionlEle.setText(d.getName());
		}
    	return outDoc;
    }
    @Override
	public Definition getDefinitionByName(String name) {
		// Controllers need access to definitions.  Allow world read
 		return coreDao.loadDefinitionByName(null, name, RequestContextHolder.getRequestContext().getZoneId());
	}
    @Override
	public Definition getDefinition(String id) {
		// Controllers need access to definitions.  Allow world read
 		return coreDao.loadDefinition(id, RequestContextHolder.getRequestContext().getZoneId());
	}
	@Override
	public Definition getDefinitionByReservedId(String internalId) {
		return getCoreDao().loadReservedDefinition(internalId, RequestContextHolder.getRequestContext().getZoneId());
		
	}
    //lookup definition by name going up tree including public
	@Override
	public Definition getDefinitionByName(Binder binder, Boolean includeAncestors, String name) {
		List<Definition> defs;
		if (binder == null) {
  	    	FilterControls filter = new FilterControls();
  	 		filter.add("binderId", ObjectKeys.RESERVED_BINDER_ID);
 	 		filter.add("name", name);
  	 		defs =  coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
		} else if (includeAncestors.equals(Boolean.TRUE)) {
   	    	Map params = new HashMap();
   	    	List ids = getAncestorIds(binder);
   	    	ids.add(ObjectKeys.RESERVED_BINDER_ID);
   	    	params.put("binderId", ids);
   	    	params.put("name", name);
  	    	params.put("zoneId", RequestContextHolder.getRequestContext().getZoneId());  //need zone without binder
  	    	defs = coreDao.loadObjects("from org.kablink.teaming.domain.Definition where binderId in (:binderId) and zoneId=:zoneId and name=:name", params);
  	 	} else {
  	    	FilterControls filter = new FilterControls();
  	 		filter.add("binderId", binder.getId());
 	 		filter.add("name", name);
  	 		defs =  coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
 	 	}
		defs = Utils.validateDefinitions(defs, binder);
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


	@Override
	public void modifyVisibility(String id, Integer visibility, Long binderId) {
		if (visibility == null) return;
		Definition def = getDefinition(id);
   		checkAccess(def, DefinitionOperation.manageDefinition);

   		if (binderId == null || ObjectKeys.RESERVED_BINDER_ID.equals(binderId)) {
   			if (ObjectKeys.RESERVED_BINDER_ID.equals(def.getBinderId())) 	{
	   			//already global
   				def.setVisibility(visibility);
   			} else {
   				//want to move to global
   		   		checkAccess(null, def.getType(), DefinitionOperation.manageDefinition);
   		   		//see if name will be unique
   		   		try {
   		   			getCoreDao().loadDefinitionByName(null, def.getName(), def.getZoneId());
   		   			//already exists
   		   			throw new DefinitionExistsException();
   		   		} catch (NoDefinitionByTheIdException nd) {
   		   			def.setVisibility(visibility);
   		   			def.setBinderId(ObjectKeys.RESERVED_BINDER_ID);
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
   		   			throw new DefinitionExistsException();
   		   		} catch (NoDefinitionByTheIdException nd) {
   		   			def.setVisibility(visibility);
   					def.setBinderId(binderId);
	   			}
	   		}
	   	}

	}


	@Override
	public void modifyDefinitionProperties(String id, InputDataAccessor inputData) {
		Definition def = getDefinition(id);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		//Store the properties in the definition document
		Document defDoc = def.getDefinitionForModificationPurpose();
		if (def != null && defDoc != null) {
			//name and caption are special cased
			if (inputData.exists("propertyId_name")) {
				String  definitionName = inputData.getSingleValue("propertyId_name");
				if (Validator.isNotNull(definitionName)) def.setName(definitionName);
			}
			if (inputData.exists("propertyId_caption")) {
				String definitionCaption = inputData.getSingleValue("propertyId_caption");
				if (Validator.isNotNull(definitionCaption)) {
					def.setTitle(Html.replaceSpecialChars(definitionCaption));
				}
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
		Document defDoc = def.getDefinitionForModificationPurpose();
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

	@Override
	public void setDefinitionLayout(String id, InputDataAccessor inputData) {
		Definition def = getDefinition(id);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		Document defDoc = def.getDefinitionForModificationPurpose();

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

	@Override
	public void deleteDefinition(String id) {
		Definition def = getDefinition(id);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		getCoreDao().delete(def);
		if (def.getType() == Definition.WORKFLOW) {
			//jbpm defs are named with the string id of the ss definitions
			getWorkflowModule().deleteProcessDefinition(def.getId());
		}
	}
	
	@Override
	public boolean checkDefInUse(String id) {
		Definition def = getDefinition(id);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
	   	
	   	return getCoreDao().checkInUse(def);
	}
	
	/**
	 * Load the default definition for a definition type.  If it doesn't exist, create it
	 * @param type
	 * @return
	 */
	@Override
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

			case Definition.EXTERNAL_USER_WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(Definition.class,
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_EXTERNAL_USER_WORKSPACE_DEF, type}), zoneId);
				if (!result.isEmpty()) return (Definition)result.get(0);
				definitionTitle = "__definition_default_external_user_workspace";
				internalId = ObjectKeys.DEFAULT_EXTERNAL_USER_WORKSPACE_DEF;
				definitionName="_externalUserWorkspace";
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
	        SAXReader xIn = XmlUtil.getSAXReader();
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
		ntRoot.addAttribute("caption", Html.replaceSpecialChars(title));
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
	@Override
	public Definition setDefaultBinderDefinition(Binder binder) {
		//no access - fixing up stuff
		//Create an empty binder definition
		int definitionType;
		if (binder.getEntityType().equals(EntityType.workspace)) {
			if ((binder.getDefinitionType() != null) &&
					(binder.getDefinitionType().intValue() == Definition.USER_WORKSPACE_VIEW)) {
				definitionType = Definition.USER_WORKSPACE_VIEW;
			} else if ((binder.getDefinitionType() != null) &&
					(binder.getDefinitionType().intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW)) {
				definitionType = Definition.EXTERNAL_USER_WORKSPACE_VIEW;
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
	@Override
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
	@Override
	public Element addItem(String defId, String itemId, String itemNameToAdd, InputDataAccessor inputData)
			throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
	   	checkAccess(def, DefinitionOperation.manageDefinition);

		Document definitionTree = def.getDefinitionForModificationPurpose();

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
						//Check if this item is inside a "conditional" element
						if (!checkIfInConditional(item, root)) {
							//This name is not unique and is not in a conditional element
							throw new DefinitionInvalidException("definition.error.nameNotUnique", new Object[] {defId, name});
						}
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
						if (attr.getName().equals("canBeDeleted") || DefinitionHelper.checkIfMultipleAllowed(itemEleToAdd, root)) {
							continue;
						}
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
			if (newItem.attributeValue(attr.getName()) == null) {
				if (attr.getName().equals("canBeDeleted") || attr.getName().equals("multipleAllowed"))
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
				String characterLength = configProperty.attributeValue("characterLength", "");
				if (inputData.exists("propertyId_"+attrName)) {
					String[] values = (String[]) inputData.getValues("propertyId_"+attrName);
					for (int i = 0; i < values.length; i++) {
						String value = values[i];
						if (!characterLength.equals("")) {
							//See if this field is within length
							Integer cLen = Integer.valueOf(characterLength);
							if (value.length() > cLen) {
								//The string is too long
								throw new DefinitionInvalidException("definition.error.stringTooLong", new Object[] {"\""+value+"\""});
							}
						}
						if (!characterMask.equals("")) {
							//See if the user entered a valid name
							if (!value.equals("") && !value.matches(characterMask)) {
								//The value is not well formed, go complain to the user
								throw new DefinitionInvalidException("definition.error.invalidCharacter", new Object[] {"\""+value+"\""});
							}
							if (configEle.attributeValue("type", "").equals("data")) {
								//For data properties, check that the name isn't a reserved name
								if (checkIfNameReserved(value)) {
									throw new DefinitionInvalidException("definition.error.reservedName", new Object[] {"\""+value+"\""});
								}
							}
						}

						Element newPropertyEle = newPropertiesEle.addElement("property");
						//just copy name and value
						newPropertyEle.addAttribute("name", attrName);
						if (type.equals("text") || type.equals("remoteApp") || type.equals("subProcess")) {
							newPropertyEle.addAttribute("value", Html.replaceSpecialChars(value));
						} else if (type.equals("textarea")) {
							newPropertyEle.setText(value);
						} else if (type.equals("integer")) {
							if (!value.equals("") && !value.matches("^[0-9]+$")) {
								//The value is not a valid integer
								throw new DefinitionInvalidException("definition.error.notAnInteger", new Object[] {value, configProperty.attributeValue("caption")});
							}
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("selectbox") || type.equals("itemSelect") ||
								type.equals("radio") || type.equals("replyStyle") ||
								type.equals("iconList") || type.equals("repositoryList") ||
								type.equals("folderSelect") || type.equals("locale")) {
							newPropertyEle.addAttribute("value", value);
						} else if (type.startsWith("familySelectbox")) {
							if (Utils.checkIfFilr()) {
								//This is Filr, check for a valid family type
								if (!Utils.checkIfFilrFamily(type, value)) {
									throw new DefinitionInvalidException("definition.error.familyInvalid");
								}
							}
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
								if (!v[vals].matches("^[0-9 ]+$")) {
									//The value is not a valid integer
									throw new DefinitionInvalidException("definition.error.notAnInteger", new Object[] {v[vals], configProperty.attributeValue("caption")});
								}
							}
							newPropertyEle.addAttribute("value", value);
						} else if (type.equals("workflowStatesList")) {
							//Workflow states list typically has 2 bits of data to capture:
							//  the workflow id and the state names
							newPropertyEle.addAttribute("workflowDefinitionId", value);
							String[] workflowStateNames = (String[]) inputData.getValues("workflowStateNames");
							if (workflowStateNames != null) {
								for (int j=0; j<workflowStateNames.length; ++j) {
									if (Validator.isNull(workflowStateNames[j])) continue;
									Element workflowCondition = newPropertyEle.addElement("workflowState");
									workflowCondition.addAttribute("name", Html.replaceSpecialChars(workflowStateNames[j].trim()));
								}
							}
						}
					}
				} else if (type.equals("workflowCondition")) {
					//Workflow conditions typically have 4 bits of data to capture:
					//  the definition id, the element name, the operation, and the operand value
					if (inputData.exists("conditionDefinitionId") && 
							!inputData.getSingleValue("conditionDefinitionId").equals("-") &&
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
							if (!operationDuration.matches("^[0-9]+$")) {
								//The value is not a valid integer
								throw new DefinitionInvalidException("definition.error.notAnInteger", new Object[] {operationDuration, configProperty.attributeValue("caption")});
							}
							if (operationDuration.length() > 10) {
								//The value is not a valid integer
								throw new DefinitionInvalidException("definition.error.stringTooLong", new Object[] {operationDuration, configProperty.attributeValue("caption")});
							}
							workflowCondition.addAttribute("duration", operationDuration);
							workflowCondition.addAttribute("durationType", operationDurationType);
						}
						if (inputData.exists("conditionElementValue")) {
							String[] conditionValues = (String[])inputData.getValues("conditionElementValue");
							for (int j = 0; j < conditionValues.length; j++) {
								String conditionValue = conditionValues[j];
								workflowCondition.addElement("value").setText(Html.replaceSpecialChars(conditionValue));
							}
						}
					} else if (inputData.exists("conditionDefinitionId") && 
							inputData.getSingleValue("conditionDefinitionId").equals("-")) {
						if (inputData.exists("previous_conditionDefinitionId") && 
								inputData.exists("previous_conditionElementName") &&
								inputData.exists("previous_conditionElementOperation")) {
							//There was a previous value that we should preserve
							String conditionDefinitionId = inputData.getSingleValue("previous_conditionDefinitionId");
							String conditionElementName = inputData.getSingleValue("previous_conditionElementName");
							String conditionElementOperation = inputData.getSingleValue("previous_conditionElementOperation");
							Element newPropertyEle = configProperty.createCopy();
							newPropertiesEle.add(newPropertyEle);
							Element workflowCondition = newPropertyEle.addElement("workflowCondition");
							workflowCondition.addAttribute("definitionId", conditionDefinitionId);
							workflowCondition.addAttribute("elementName", conditionElementName);
							workflowCondition.addAttribute("operation", conditionElementOperation);
							if (inputData.exists("previous_operationDuration") &&
									inputData.exists("previous_operationDurationType")) {
								String operationDuration = inputData.getSingleValue("previous_operationDuration");
								String operationDurationType = inputData.getSingleValue("previous_operationDurationType");
								if (!operationDuration.matches("^[0-9]+$")) {
									//The value is not a valid integer
									throw new DefinitionInvalidException("definition.error.notAnInteger", new Object[] {operationDuration, configProperty.attributeValue("caption")});
								}
								if (operationDuration.length() > 10) {
									//The value is not a valid integer
									throw new DefinitionInvalidException("definition.error.stringTooLong", new Object[] {operationDuration, configProperty.attributeValue("caption")});
								}
								workflowCondition.addAttribute("duration", operationDuration);
								workflowCondition.addAttribute("durationType", operationDurationType);
							}
							if (inputData.exists("previous_conditionElementValue")) {
								String[] conditionValues = (String[])inputData.getValues("previous_conditionElementValue");
								for (int j = 0; j < conditionValues.length; j++) {
									String conditionValue = conditionValues[j];
									workflowCondition.addElement("value").setText(Html.replaceSpecialChars(conditionValue));
								}
							}
						}
					}
				} else if (type.equals("transitionOnDate")) {
					String dateStr = (String) inputData.getSingleValue("date_date");
					String timeStr = (String) inputData.getSingleValue("date_time");
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
					try {
						Date date = formatter.parse(dateStr + " " + timeStr);
						String value = String.valueOf(date.getTime());
						Element newPropertyEle = newPropertiesEle.addElement("property");
						newPropertyEle.addAttribute("name", "date");
						newPropertyEle.addAttribute("value", value);
					} catch (ParseException e) {
						throw new DefinitionInvalidException("definition.error.invalidDateFormat", new Object[] {"\"" + dateStr + " " + timeStr + "\""});
					}
					
				} else if (type.equals("workflowSetEntryDataValue")) {
					//Workflow conditions and set data values typically have 4 bits of data to capture:
					//  the definition id, the element name, the operation, and the operand value
					if (inputData.exists("conditionDefinitionId") && 
							!inputData.getSingleValue("conditionDefinitionId").equals("-") &&
							inputData.exists("conditionElementName") &&
							inputData.exists("conditionElementOperation")) {
						Element newPropertyEle = configProperty.createCopy();
						newPropertiesEle.add(newPropertyEle);
						String conditionDefinitionId = inputData.getSingleValue("conditionDefinitionId");
						String conditionElementName = inputData.getSingleValue("conditionElementName");
						String conditionElementOperation = inputData.getSingleValue("conditionElementOperation");
						Element workflowSetEntryDataValue = newPropertyEle.addElement("workflowSetEntryDataValue");
						workflowSetEntryDataValue.addAttribute("definitionId", conditionDefinitionId);
						workflowSetEntryDataValue.addAttribute("elementName", conditionElementName);
						workflowSetEntryDataValue.addAttribute("operation", conditionElementOperation);
						if (inputData.exists("operationDuration") &&
								inputData.exists("operationDurationType")) {
							String operationDuration = inputData.getSingleValue("operationDuration");
							String operationDurationType = inputData.getSingleValue("operationDurationType");
							if (!operationDuration.matches("^[0-9]+$")) {
								//The value is not a valid integer
								throw new DefinitionInvalidException("definition.error.notAnInteger", new Object[] {operationDuration, configProperty.attributeValue("caption")});
							}
							if (operationDuration.length() > 10) {
								//The value is not a valid integer
								throw new DefinitionInvalidException("definition.error.stringTooLong", new Object[] {operationDuration, configProperty.attributeValue("caption")});
							}
							workflowSetEntryDataValue.addAttribute("duration", operationDuration);
							workflowSetEntryDataValue.addAttribute("durationType", operationDurationType);
						}
						if (inputData.exists("conditionElementValue")) {
							String[] conditionValues = (String[])inputData.getValues("conditionElementValue");
							for (int j = 0; j < conditionValues.length; j++) {
								String conditionValue = conditionValues[j];
								workflowSetEntryDataValue.addElement("value").setText(Html.replaceSpecialChars(conditionValue));
							}
						}
					} else if (inputData.exists("conditionDefinitionId") && 
							inputData.getSingleValue("conditionDefinitionId").equals("-")) {
						if (inputData.exists("previous_conditionDefinitionId") && 
								inputData.exists("previous_conditionElementName") &&
								inputData.exists("previous_conditionElementOperation")) {
							//There was a previous value that we should preserve
							String conditionDefinitionId = inputData.getSingleValue("previous_conditionDefinitionId");
							String conditionElementName = inputData.getSingleValue("previous_conditionElementName");
							String conditionElementOperation = inputData.getSingleValue("previous_conditionElementOperation");
							Element newPropertyEle = configProperty.createCopy();
							newPropertiesEle.add(newPropertyEle);
							Element workflowSetEntryDataValue = newPropertyEle.addElement("workflowSetEntryDataValue");
							workflowSetEntryDataValue.addAttribute("definitionId", conditionDefinitionId);
							workflowSetEntryDataValue.addAttribute("elementName", conditionElementName);
							workflowSetEntryDataValue.addAttribute("operation", conditionElementOperation);
							if (inputData.exists("previous_operationDuration") &&
									inputData.exists("previous_operationDurationType")) {
								String operationDuration = inputData.getSingleValue("previous_operationDuration");
								String operationDurationType = inputData.getSingleValue("previous_operationDurationType");
								if (!operationDuration.matches("^[0-9]+$")) {
									//The value is not a valid integer
									throw new DefinitionInvalidException("definition.error.notAnInteger", new Object[] {operationDuration, configProperty.attributeValue("caption")});
								}
								if (operationDuration.length() > 10) {
									//The value is not a valid integer
									throw new DefinitionInvalidException("definition.error.stringTooLong", new Object[] {operationDuration, configProperty.attributeValue("caption")});
								}
								workflowSetEntryDataValue.addAttribute("duration", operationDuration);
								workflowSetEntryDataValue.addAttribute("durationType", operationDurationType);
							}
							if (inputData.exists("previous_conditionElementValue")) {
								String[] conditionValues = (String[])inputData.getValues("previous_conditionElementValue");
								for (int j = 0; j < conditionValues.length; j++) {
									String conditionValue = conditionValues[j];
									workflowSetEntryDataValue.addElement("value").setText(Html.replaceSpecialChars(conditionValue));
								}
							}
						}
					}
				} else if (type.equals("workflowEntryDataUserList")) {
					//Workflow conditions typically have 4 bits of data to capture:
					//  the definition id, the element name, the operation, and the operand value
					if (inputData.exists("conditionDefinitionId") &&
							!inputData.getSingleValue("conditionDefinitionId").equals("-") &&
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
								workflowCondition.addAttribute("elementName", conditionElementNames[i].trim());
							}
						}
					} else {
						//See if there was a previous value that we should preserve
						if (inputData.exists("previous_conditionDefinitionId") &&
								inputData.exists("previous_conditionElementName")) {
							String conditionDefinitionId = inputData.getSingleValue("previous_conditionDefinitionId");
							if (Validator.isNotNull(conditionDefinitionId)) {
								Element newPropertyEle = configProperty.createCopy();
								newPropertiesEle.add(newPropertyEle);
								String[] conditionElementNames0 = (String[]) inputData.getValues("previous_conditionElementName");
								if (conditionElementNames0.length > 0) {
									String[] conditionElementNames = conditionElementNames0[0].split(",");
									for (int i=0; i<conditionElementNames.length; ++i) {
										if (Validator.isNull(conditionElementNames[i])) continue;
										Element workflowCondition = newPropertyEle.addElement("workflowEntryDataUserList");
										workflowCondition.addAttribute("definitionId", conditionDefinitionId);
										workflowCondition.addAttribute("elementName", conditionElementNames[i].trim());
									}
								}
							}
						}
					}
				} else {
					if (type.equals("boolean") || type.equals("checkbox")) {
						String value = "false";
						Element newPropertyEle = newPropertiesEle.addElement("property");
						newPropertyEle.addAttribute("name", attrName);
						newPropertyEle.addAttribute("value", Html.replaceSpecialChars(value));
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
		if (value.contains("./") || value.contains("~")) {
			//Illegal value, ignore it
			throw new DefinitionInvalidException("definition.error.invalidCharacter", new Object[] {"\""+value+"\""});
		}
		jsps = item.addElement("jsps");
		Element jsp = jsps.addElement("jsp");
		jsp.addAttribute("name", "custom");
		if (inherit) {
			jsp.addAttribute("inherit", inherit.toString());
		} else {
			jsp.addAttribute("value", value);
		}
	}

	@Override
	public void modifyItem(String defId, String itemId, InputDataAccessor inputData) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		Document definitionTree = def.getDefinitionForModificationPurpose();

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
						//Check if this item is in a conditional element
						if (!checkIfInConditional(item, root)) {
							//This name is not unique and is not in a conditional element
							throw new DefinitionInvalidException("definition.error.nameNotUnique", new Object[] {defId, name});
						}
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
					
					//if we are modifying the readAccess then we should reindex all entries associated with this workflow
					if (itemType.equals("readAccess")){
						Element ele = item.getParent().getParent();
						String stateValue = DefinitionUtils.getPropertyValue(ele, "name");
					    
						List<Long> ids = getFolderDao().findFolderIdsFromWorkflowState(def.getId(), stateValue);
						final Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
						List<FolderEntry> folderEntries = new ArrayList<FolderEntry>();
						if(ids != null && ids.size() > 0) {
							for(Long id: ids){
								FolderEntry entry = getFolderDao().loadFolderEntry(id, zoneId);
								folderEntries.add(entry);
							}
						}
						
						FolderModule folderModule = (FolderModule)SpringContextUtil.getBean("folderModule");
						for(FolderEntry fEntry: folderEntries){
							folderModule.indexEntry(fEntry, false);
						}
					}
					
					if (itemType.equals("transitionOnElapsedTime") || itemType.equals("transitionOnDate") || 
							itemType.equals("transitionOnEntryData")) {
						//modifying timers. Check to see if any conditions need to be processed
						Element ele = item.getParent().getParent();
						String stateValue = DefinitionUtils.getPropertyValue(ele, "name");
					    
						List<Long> ids = getFolderDao().findFolderIdsFromWorkflowState(def.getId(), stateValue);
						final Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
						if (ids != null && ids.size() > 0) {
							for(Long id: ids){
								FolderEntry entry = getFolderDao().loadFolderEntry(id, zoneId);
								Set<WorkflowState> states = entry.getWorkflowStates();
								for (WorkflowState state : states) {
									if (state.getDefinition().equals(def) &&
											state.getState().equals(stateValue)) {
										WorkflowProcessUtils.processConditions(entry, false, false);
									}
								}
							}
						}
					}
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
	//Routine to see if an item is inside a "conditional" element
	private boolean checkIfInConditional(Element item, Element root) {
		Element parentItem = item;
		while (!parentItem.equals(root)) {
			String name = parentItem.attributeValue("name", "");
			if (name.equals("conditional") || 
					name.equals("conditionalView") || 
					name.equals("conditionalProfileFormItem") || 
					name.equals("conditionalProfileViewItem")) return true;
			parentItem = parentItem.getParent();
			if (parentItem == null) break;
		}
		return false;
	}
	private boolean checkIfNameReserved(String name) {
		if (ReservedItemNames.contains(" " + name.toLowerCase() + " ")) return true;
		else return false;
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

	@Override
	public void deleteItem(String defId, String itemId) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
	   	checkAccess(def, DefinitionOperation.manageDefinition);

		Document definitionTree = def.getDefinitionForModificationPurpose();
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			//Find the element to delete
			Element item = (Element) root.selectSingleNode("//item[@id='"+itemId+"']");
			if (item != null) {
				//Find the selected item type in the configuration document
				String itemType = item.attributeValue("name", "");
				if (def.getType() == Definition.WORKFLOW && itemType.equals("state") && 
						"workflowProcess".equals(item.getParent().attributeValue("name"))) {
					//This is a workflow state. Make sure no entries are using that state
					String state = DefinitionUtils.getPropertyValue(item, "name");
					if (checkStateInUse(def, state)) throw new DefinitionInvalidException("definition.error.cannotModifyState", new Object[] {def.getId()});

				} else if (itemType.equals("transitionOnElapsedTime") && 
						"transitions".equals(item.getParent().attributeValue("name")) &&
						"state".equals(item.getParent().getParent().attributeValue("name")) &&
						"workflowProcess".equals(item.getParent().getParent().getParent().attributeValue("name"))) {
					//This is a timer transition in a workflow state. Make sure no entries are using that state
					String state = DefinitionUtils.getPropertyValue(item.getParent().getParent(), "name");
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
					if (def.getType() == Definition.WORKFLOW && "state".equals(itemType)) {
						//This is a workflow state. Delete any transitions to this state
						String state = DefinitionUtils.getPropertyValue(item, "name");
						List<Element> transitions = root.selectNodes("//item[@type='transition']");
						for (Element transitionEle : transitions) {
							String toState = DefinitionUtils.getPropertyValue(transitionEle, "toState");
							if (toState != null && toState.equals(state)) {
								//This is a transition to the state that is being deleted, so delete it.
								transitionEle.detach();
							}
						}
					}
					setDefinition(def, definitionTree);
				}
			}
		}
	}

	@Override
	public void moveItem(String defId, String sourceItemId, String targetItemId, String position) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		if (sourceItemId.equals(targetItemId)) return;
		Document definitionTree = def.getDefinitionForModificationPurpose();
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

	@Override
	public void copyItem(String defId, String sourceItemId, String targetItemId) throws DefinitionInvalidException {
		Definition def = getDefinition(defId);
	   	checkAccess(def, DefinitionOperation.manageDefinition);
		if (sourceItemId.equals(targetItemId)) return;
		Document definitionTree = def.getDefinitionForModificationPurpose();
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
		Document defDoc = def.getDefinitionForModificationPurpose();
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
			if (nextId != startingId) {
				defChanged = true;
				defRoot.addAttribute("nextId", String.valueOf(nextId));
			}
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


    @Override
	public Document getDefinitionConfig() {
    	return this.definitionConfig;
    }

    @Override
	public Map getEntryData(Document definitionTree, InputDataAccessor inputData, Map fileItems) {
    	return getEntryData(definitionTree, inputData, fileItems, false, null);
    }
    @Override
	public Map getEntryData(Document definitionTree, InputDataAccessor inputData, Map fileItems, boolean fieldsOnly, Map options) {
		//access check not needed = have tree already
        User user = RequestContextHolder.getRequestContext().getUser();

    	// entryData will contain the Map of entry data as gleaned from the input data
		Map entryDataAll = new HashMap();
		Map entryData = new HashMap();
		List fileData = new ArrayList();
		EntryDataErrors entryDataErrors = new EntryDataErrors();
		entryDataAll.put(ObjectKeys.DEFINITION_ENTRY_DATA, entryData);
		entryDataAll.put(ObjectKeys.DEFINITION_FILE_DATA, fileData);
		entryDataAll.put(ObjectKeys.DEFINITION_ERRORS, entryDataErrors);

		if (definitionTree != null) {
			//root is the root of the entry's definition
			Element root = definitionTree.getRootElement();

			//Get a list of all of the form items in the definition (i.e., from the "form" section of the definition)
			Element entryFormItem = (Element)root.selectSingleNode("item[@type='form']");
			if (entryFormItem != null) {
				//see if title is generated and save source
				boolean titleGenerated = false;
				String titleSource = null;
				Element titleEle = (Element)entryFormItem.selectSingleNode(".//item[@name='title']");
				if (titleEle != null) {
					titleGenerated = GetterUtil.get(DefinitionUtils.getPropertyValue(titleEle, "generated"), false);
					if (titleGenerated) {
						titleSource=DefinitionUtils.getPropertyValue(titleEle, "itemSource");
					}
				} else {
					//There is no title field. See if the title value is passed in anyway
					if (inputData.exists("title")) {
						entryData.put("title", inputData.getSingleValue("title"));
					} else {
						Element familyProperty = (Element) root.selectSingleNode("//properties/property[@name='family']");
						if (familyProperty != null) {
							String family = familyProperty.attributeValue("value", "");
							if (family.equals(Definition.VIEW_STYLE_MINIBLOG)) {
						        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, 
						        		DateFormat.SHORT, user.getLocale());
						        dateFormat.setTimeZone(user.getTimeZone());
								String mbTitle = dateFormat.format(new Date());
								entryData.put("title", mbTitle);
							}
						}
					}
				}
				//Before processing the definition items, process any default attributes that aren't in the definition
				String itemName = "text";
				String nameValue = "_zoneUUID";
				//Process this special value
				processInputDataItem(itemName, nameValue, inputData, entryData,
			    		fileItems, fileData, entryDataErrors, null, titleGenerated, titleSource);
				
				//By convention, we assume the element called "branding" is the folder branding
				itemName = "folderBranding";
				nameValue = "branding";
				//Process this special value
				processInputDataItem(itemName, nameValue, inputData, entryData,
			    		fileItems, fileData, entryDataErrors, null, titleGenerated, titleSource);
				
				//While going through the entry's elements, keep track of the current form name (needed to process date elements)
				List<Element> itItems = entryFormItem.selectNodes(".//item[@type='data']");
				boolean attachFilesSeen = false;
				for (Element nextItem: itItems) {
					itemName = (String) nextItem.attributeValue("name", "");
					if (itemName.equals("attachFiles")) attachFilesSeen = true;
										
					//Get the form element name (property name)
					nameValue = DefinitionUtils.getPropertyValue(nextItem, "name");
					if (Validator.isNull(nameValue)) {nameValue = nextItem.attributeValue("name");}
					
					//Process the data item depending on its item name
					processInputDataItem(itemName, nameValue, inputData, entryData,
				    		fileItems, fileData, entryDataErrors, nextItem, titleGenerated, titleSource);
				}
				//After processing the definition items, process any attributes that were missed
				if (!attachFilesSeen) {
					//Always try to make sure any files attached get saved. They might be from the "addImage" widget
					itemName = "attachFiles";
					nameValue = "ss_attachFile";
					//Process this special value
					processInputDataItem(itemName, nameValue, inputData, entryData,
				    		fileItems, fileData, entryDataErrors, null, titleGenerated, titleSource);
				}
				if (options == null || !Boolean.TRUE.equals(options.get(ObjectKeys.INPUT_OPTION_NO_DEFAULTS))) {
					//See if there are any default settings for select boxes that were missed
					for (Element nextItem: itItems) {
						itemName = (String) nextItem.attributeValue("name", "");
						if (itemName.equals("selectbox")) {
							//Get the form element name (property name)
							nameValue = DefinitionUtils.getPropertyValue(nextItem, "name");
							if (!Validator.isNull(nameValue) && (!entryData.containsKey(nameValue) || entryData.get(nameValue) == null)) {
								//There is no value for this item. See if there was a form element 
								if (!inputData.exists("__selectboxSpecified_" + nameValue)) {
									//There wasn't a form item, so go see if there is a default for this field
									List<Element> defItems = nextItem.selectNodes(".//item[@name='selectboxSelection']/properties/property[@name='default' and @value='true']");
									if (!defItems.isEmpty()) {
										//There are some defaults. Go add them to the entryData list
										List<String> valuesList = new ArrayList();
										for (Element defItem : defItems) {
											valuesList.add(DefinitionUtils.getPropertyValue(defItem.getParent().getParent(), "name"));
										}
										if (valuesList.size() == 1) {
											String value = valuesList.get(0);
											entryData.put(nameValue, value);
										} else if (valuesList.size() > 1) {
											String[] values = new String[valuesList.size()];
											for (int i = 0; i < valuesList.size(); i++) {
												values[i] = valuesList.get(i);
											}
											entryData.put(nameValue, values);
										}
									}
								}
							}
						}
					}
				}
			}
		}

    	return entryDataAll;
    }
    
    //Routine to process entry data depending on the type of data it is
    private void processInputDataItem(String itemName, String nameValue, InputDataAccessor inputData, Map entryData,
    		Map fileItems, List fileData, EntryDataErrors entryDataErrors, Element nextItem, boolean titleGenerated, String titleSource) {
        User user = RequestContextHolder.getRequestContext().getUser();
		String nameValuePerUser = nameValue + "." + user.getName();
		String s_userVersionAllowed = "false";
		if (nextItem != null) s_userVersionAllowed = DefinitionUtils.getPropertyValue(nextItem, "userVersionAllowed");
		boolean userVersionAllowed = false;
		if (s_userVersionAllowed != null && "true".equals(s_userVersionAllowed)) 
			userVersionAllowed = true;
		String s_fieldModificationAllowed = "false";
		if (nextItem != null) s_fieldModificationAllowed = DefinitionUtils.getPropertyValue(nextItem, "fieldModificationAllowed");
		boolean fieldModificationAllowed = false;
		if (s_fieldModificationAllowed != null && "true".equals(s_fieldModificationAllowed)) 
			fieldModificationAllowed = true;
		//We have the element name, see if it has a value in the input data
		if (itemName.equals("description") || itemName.equals("htmlEditorTextarea")) {
			if (inputData.exists(nameValue)) {
				Description description = inputData.getDescriptionValue(nameValue);
				if (description != null) {
					String format = inputData.getSingleValue(nameValue + ".format");
					if (format != null) {
						description.setFormat(Integer.valueOf(format));
					}
					//Make sure the text is legal html
					tidyCheckText(description, entryDataErrors);
					
					//Deal with any markup language transformations before storing the description
					MarkupUtil.scanDescriptionForUploadFiles(description, nameValue, fileData);
					MarkupUtil.scanDescriptionForAttachmentFileUrls(description);
					MarkupUtil.scanDescriptionForICLinks(description);
					MarkupUtil.scanDescriptionForYouTubeLinks(description);
					MarkupUtil.scanDescriptionForExportTitleUrls(description);
					//Before storing this, check for xss problems
					description = StringCheckUtil.check(description);
					if (!inputData.isFieldsOnly() || fieldModificationAllowed) entryData.put(nameValue, description);
				}
			}
		} else if (itemName.equals("folderBranding") || itemName.equals("workspaceBranding")) {
			if (inputData.exists(nameValue)) {
				Description description = inputData.getDescriptionValue(nameValue);
				if (description != null) {
					String format = inputData.getSingleValue(nameValue + ".format");
					if (format != null) {
						description.setFormat(Integer.valueOf(format));
					}
					//Make sure the text is legal html
					tidyCheckText(description, entryDataErrors);
					
					//Deal with any markup language transformations before storing the description
					MarkupUtil.scanDescriptionForUploadFiles(description, nameValue, fileData);
					MarkupUtil.scanDescriptionForAttachmentFileUrls(description);
					MarkupUtil.scanDescriptionForICLinks(description);
					MarkupUtil.scanDescriptionForYouTubeLinks(description);
					MarkupUtil.scanDescriptionForExportTitleUrls(description);
					if (!inputData.isFieldsOnly() || fieldModificationAllowed)
					{
						//Before storing this, check for xss problems
						description = StringCheckUtil.check(description);
						entryData.put(nameValue, description.getText());
						
						// Add any extended branding we might have.
						if ( inputData.exists( "brandingExt" ) )
						{
							String brandingExt;
	
							brandingExt = mapInputData( inputData.getSingleValue( "brandingExt" ) );
							//Before storing this, check for xss problems
							brandingExt = StringCheckUtil.check(brandingExt);
							entryData.put( "brandingExt", brandingExt );
						}
					}
				}
			}
		} else if (itemName.equals("url")) {
			if (inputData.exists(nameValue)) {
				String value = inputData.getSingleValue(nameValue);
				String urlTag = "<a href=\"" + value + "\">xss</a>";
				if (urlTag.equalsIgnoreCase(StringCheckUtil.check(urlTag))) {
					//There was no indication of XSS code, so store the result
					entryData.put(nameValue, value);
				} else {
					entryData.put(nameValue, NLT.get("error.invalidUrl"));
				}
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
				if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
					entryData.put(nameValue, StringCheckUtil.check(valuesTrimmed));
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
						if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
							entryData.put(nameValue+ENTRY_ATTRIBUTES_SET+setName, StringCheckUtil.check(valuesTrimmed2));
					} else {
						//There aren't any attributes for this set. Clear any old values
						if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
							entryData.put(nameValue+ENTRY_ATTRIBUTES_SET+setName, null);
					}
					if (inputData.exists(nameValue+ENTRY_ATTRIBUTES_SET_MULTIPLE_ALLOWED+setName) &&
							inputData.getSingleValue(nameValue+ENTRY_ATTRIBUTES_SET_MULTIPLE_ALLOWED+setName).equals("on")) {
						if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
							entryData.put(nameValue+ENTRY_ATTRIBUTES_SET_MULTIPLE_ALLOWED+setName, true);
					} else {
						if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
							entryData.put(nameValue+ENTRY_ATTRIBUTES_SET_MULTIPLE_ALLOWED+setName, false);
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
				if (!inputData.isFieldsOnly() || fieldModificationAllowed) {
					entryData.put(nameValue, StringCheckUtil.check(valuesTrimmed));
				}
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
						if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
							entryData.put(nameValue+ENTRY_ATTRIBUTES_SET+setName, StringCheckUtil.check(valuesTrimmed2));
					}
				}
			}
		} else if (itemName.equals("date") || itemName.equals("date_time")) {
			if (inputData.exists(nameValue)) {
				//Use the helper routine to parse the date into a date object
				Date date = inputData.getDateValue(nameValue);
				if (date != null || inputData.exists(nameValue + "_dateExistedBefore")) {
					if (!inputData.isFieldsOnly() || fieldModificationAllowed) entryData.put(nameValue, date);
				}
			}
			if (userVersionAllowed && inputData.exists(nameValuePerUser)) {
				//Use the helper routine to parse the date into a date object
				Date date = inputData.getDateValue(nameValuePerUser);
				if (date != null) {
					entryData.put(nameValuePerUser, date);
				} else {
					entryData.put(nameValuePerUser, null);
				}
			}
		} else if (itemName.equals("event")) {
		    //Ditto for event helper routine
		    Boolean hasDur = Boolean.FALSE;
		    if (nextItem != null && GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "hasDuration"), false)) {
		    	hasDur = Boolean.TRUE;
		    }
		    Boolean hasRecur = Boolean.FALSE;
		    if (nextItem != null && GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "hasRecurrence"), false)) {
		    	hasRecur = Boolean.TRUE;
		    }
		    Event event = inputData.getEventValue(nameValue, hasDur, hasRecur);
		    if (event != null || inputData.exists(nameValue + "_dateExistedBefore")) {
		        if (event != null) event.setName(nameValue);
		        if (!inputData.isFieldsOnly() || fieldModificationAllowed) entryData.put(nameValue, event);
		    }
		} else if (itemName.equals("survey")) {
			if (inputData.exists(nameValue)) {
				//Use the helper routine to parse the date into a date object
				Survey survey = inputData.getSurveyValue(nameValue);
				if (survey != null) {
					if (!inputData.isFieldsOnly() || fieldModificationAllowed) {
						entryData.put(nameValue, StringCheckUtil.check(survey));
					}
				}
			}
		} else if (itemName.equals("user_list") || itemName.equals("group_list") ||
					itemName.equals("team_list") || itemName.equals("userListSelectbox")) {
			if (inputData.exists(nameValue + ".principalNames")) {
				Set<Long> ids = ResolveIds.getPrincipalNamesAsLongIdSet(inputData.getValues(nameValue + ".principalNames"), true);
				CommaSeparatedValue v = new CommaSeparatedValue();
				v.setValue(ids);
				if (!inputData.isFieldsOnly() || fieldModificationAllowed) {
					entryData.put(nameValue, v);
				}
			} else if (inputData.exists(nameValue)) {
				Set<Long> ids = LongIdUtil.getIdsAsLongSet(inputData.getValues(nameValue));
				CommaSeparatedValue v = new CommaSeparatedValue();
				v.setValue(ids);
				if (!inputData.isFieldsOnly() || fieldModificationAllowed) {
					entryData.put(nameValue, v);
				}
			}
		} else if (itemName.equals("external_user_list")) {
			if (inputData.exists(nameValue)) {
				String[] values = inputData.getValues(nameValue);
				int valuesCount = ((null == values) ? 0 : values.length);
				String value;
				if (1 == valuesCount) {
					value = values[0];
					if (StringUtil.isPackedString(value)) {
						values = StringUtil.unpack(value);
						valuesCount = ((null == values) ? 0 : values.length);
					}
				}
				List<String> valuesList = new ArrayList();
				for (int i = 0; i < valuesCount; i += 1) {
					value = values[i];
					if (null != value) {
						value = value.trim();
						if (0 < value.length()) {
							valuesList.add(value);
						}
					}
				}
				PackedValue v = new PackedValue();
				valuesList = StringCheckUtil.check(valuesList);
				v.setValue(valuesList.toArray(new String[0]));
				if (!inputData.isFieldsOnly() || fieldModificationAllowed) {
					entryData.put(nameValue, v);
				}
			}
		} else if (itemName.equals("profileEmailAddress") || 
				itemName.equals("profileMobileEmailAddress") ||
				itemName.equals("profileTxtEmailAddress") ||
				itemName.equals("profileBccEmailAddress")) {
			if (inputData.exists(nameValue)) {
				//Check if this is a valid email address
				String addr = (String)inputData.getSingleValue(nameValue);
				if (!"".equals(addr)) {
					try {
						InternetAddress ia = new InternetAddress(addr);
						ia.validate();
					} catch(Exception e) {
						//This is an invalid address
						addr = NLT.get("email.badEmailAddress");
					}
				}
				entryData.put(nameValue, StringCheckUtil.check(addr));
			}

		} else if (itemName.equals("email_list") && inputData.exists(nameValue)) {
			String val = inputData.getSingleValue(nameValue);
			if (val != null) {
				String[] ids = val.split("[\\s,]");
				LinkedHashSet<String> v = new LinkedHashSet();
				for (int i = 0; i < ids.length; i++) {
					String addr = ids[i].trim();
					if (!"".equals(addr)) {
						try {
							InternetAddress ia = new InternetAddress(addr);
							ia.validate();
						} catch(Exception e) {
							//This is an invalid address
							addr = NLT.get("email.badEmailAddress");
						}
						v.add(addr);
					}
				}
				if (!inputData.isFieldsOnly() || fieldModificationAllowed) {
					entryData.put(nameValue, StringCheckUtil.check(v));
				}
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
				if (!inputData.isFieldsOnly() || fieldModificationAllowed) entryData.put(nameValue, v);
			} else if (inputData.exists(nameValue)) {
				Set<Long> ids = LongIdUtil.getIdsAsLongSet(inputData.getValues(nameValue));
				ids.removeAll(longIdsToRemove);
				CommaSeparatedValue v = new CommaSeparatedValue();
				v.setValue(ids);
				if (!inputData.isFieldsOnly() || fieldModificationAllowed) entryData.put(nameValue, v);
			}
		} else if (itemName.equals("guestName")) {
			if (inputData.exists(nameValue)) {
				if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
					entryData.put(nameValue, StringCheckUtil.check(inputData.getValues(nameValue)));
			}
		} else if (itemName.equals("captcha")) {
	    	boolean guestOnly = true;
	    	if (nextItem != null) guestOnly = DefinitionUtils.getPropertyBooleanValue(nextItem, "guestOnly");
			if ( !guestOnly || user.isShared() ) {
				String kaptchaResponse = inputData.getSingleValue("_captcha");
				String kaptchaExpected = null;
				SessionContext ctx = RequestContextHolder.getRequestContext().getSessionContext();
				Object session = ctx.getSessionObject();
				if (session != null && session instanceof HttpSession) {
					kaptchaExpected = (String)((HttpSession)session).getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
				} else if (session != null && session instanceof PortletSession) {
					kaptchaExpected = (String)((PortletSession)session).getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY, javax.portlet.PortletSession.APPLICATION_SCOPE);
				}
	    		if ( kaptchaExpected == null || 
	    				kaptchaResponse == null || 
	    				!kaptchaExpected.equalsIgnoreCase( kaptchaResponse  ) ) {
					// The text entered by the user did not match the text used to create the kaptcha image.
	    			boolean ignoreCaptcha = ((null == kaptchaExpected) && (null == kaptchaResponse));
	    			if (!ignoreCaptcha) {
						String gwtCommentNoCaptchaS = inputData.getSingleValue(ObjectKeys.FIELD_ENTITY_GWT_COMMENT_ENTRY);
						ignoreCaptcha = ((null != gwtCommentNoCaptchaS) && gwtCommentNoCaptchaS.equals(String.valueOf(Boolean.TRUE)));
						if (!ignoreCaptcha) {
							entryDataErrors.addProblem(new Problem(Problem.INVALID_CAPTCHA_RESPONSE, null));
						}
	    			}
				}
			}
		} else if (itemName.equals("selectbox")) {
	    	String multiple = "";
	    	if (nextItem != null) multiple = DefinitionUtils.getPropertyValue(nextItem, "multipleAllowed");
			if (inputData.exists(nameValue)) {
		    	if ("true".equals(multiple)) {
		    		if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
		    			entryData.put(nameValue, StringCheckUtil.check(inputData.getValues(nameValue)));
		    	} else {
		    		if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
		    			entryData.put(nameValue, StringCheckUtil.check(inputData.getSingleValue(nameValue)));
		    	}
			} else {
				if ("true".equals(multiple)) {
					//There are no selections set, and multiple is allowed. See if the user might be trying to clear the selections
					if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
		    			entryData.put(nameValue, null);
				}
			}
			if (userVersionAllowed && inputData.exists(nameValuePerUser)) {
		    	if ("true".equals(multiple)) {
		    		entryData.put(nameValuePerUser, StringCheckUtil.check(inputData.getValues(nameValuePerUser)));
		    	} else {
		    		entryData.put(nameValuePerUser, StringCheckUtil.check(inputData.getSingleValue(nameValuePerUser)));
		    	}
			}
		} else if (itemName.equals("checkbox")) {
			if (inputData.exists(nameValue)) {
				if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
					entryData.put(nameValue, Boolean.valueOf(GetterUtil.getBoolean(inputData.getSingleValue(nameValue), false)));
			}
			if (userVersionAllowed && inputData.exists(nameValuePerUser)) {
				entryData.put(nameValuePerUser, Boolean.valueOf(GetterUtil.getBoolean(inputData.getSingleValue(nameValuePerUser), false)));
			}
		} else if (itemName.equals("profileTimeZone")) {
			if (inputData.exists(nameValue)) {
				Object val = inputData.getSingleObject(nameValue);
				if (val == null) {
					if (!inputData.isFieldsOnly() || fieldModificationAllowed) entryData.put(nameValue, null);
				} else if (val instanceof TimeZone) {
					if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
						entryData.put(nameValue, TimeZoneHelper.fixTimeZone((TimeZone)val));
				} else {
					String sVal = inputData.getSingleValue(nameValue);
					if (Validator.isNull(sVal)) {
						if (!inputData.isFieldsOnly() || fieldModificationAllowed) {
							entryData.put(nameValue, null);
						}
					} else {
						if (!inputData.isFieldsOnly() || fieldModificationAllowed) {
							entryData.put(nameValue, TimeZoneHelper.getTimeZone(sVal));
						}
					}
				}
			}
		} else if (itemName.equals("profileLocale")) {
			if (inputData.exists(nameValue)) {
				Object val = inputData.getSingleObject(nameValue);
				if (val == null) {
					//See if there is a default specified
					String defaultValue = "";
					if (nextItem != null) DefinitionUtils.getPropertyValue(nextItem, "default");
					if (!Validator.isNull(defaultValue)) {
						String[] vals = defaultValue.split("_");
						vals = StringCheckUtil.check(vals);
						if (vals.length == 1) {
							if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
								entryData.put(nameValue, new Locale(vals[0]));
						} else if (vals.length == 2) {
							if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
								entryData.put(nameValue, new Locale(vals[0], vals[1]));
						} else if (vals.length >= 3) {
							if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
								entryData.put(nameValue, new Locale(vals[0], vals[1], vals[2]));
						}
					} else {
						Locale userLocale = null;
			    		String language = LocaleUtils.getLocaleLanguage();
			    		String country = LocaleUtils.getLocaleCountry();
			    		if (!language.equals("")) {
			    			if (!country.equals("")) userLocale = new Locale(language, country);
			    			else userLocale = new Locale(language);
			    		}
			    		if (!inputData.isFieldsOnly() || fieldModificationAllowed) entryData.put(nameValue, userLocale);
					}
				} else if (val instanceof Locale) {
					if (!inputData.isFieldsOnly() || fieldModificationAllowed) entryData.put(nameValue, (Locale)val);
				} else {
					String sVal = inputData.getSingleValue(nameValue);
					if (Validator.isNull(sVal)) entryData.put(nameValue, null);
					else {
						String[] vals = sVal.split("_");
						vals = StringCheckUtil.check(vals);
						if (vals.length == 1) {
							if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
								entryData.put(nameValue, new Locale(vals[0]));
						} else if (vals.length == 2) {
							if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
								entryData.put(nameValue, new Locale(vals[0], vals[1]));
						} else if (vals.length >= 3) {
							if (!inputData.isFieldsOnly() || fieldModificationAllowed) 
								entryData.put(nameValue, new Locale(vals[0], vals[1], vals[2]));
						}
					}
				}
			}
		} else if (itemName.equals("file") || itemName.equals("graphic") ||
				itemName.equals("profileEntryPicture")) {
		    if (fileItems != null && fileItems.containsKey(nameValue)) {
		    	MultipartFile myFile = (MultipartFile)fileItems.get(nameValue);
		    	String fileName = myFile.getOriginalFilename();
		    	if (fileName.equals("")) return;
		    	String repositoryName = "";
		    	if (nextItem != null) 
		    		repositoryName = DefinitionUtils.getPropertyValue(nextItem, "storage");
		    	if (Validator.isNull(repositoryName)) repositoryName = RepositoryUtil.getDefaultRepositoryName();
		    	FileUploadItem fui;
		    	if (titleGenerated && nameValue.equals(titleSource) &&
		    			(itemName.equals("file") || itemName.equals("graphic")))
		    		fui = new FileUploadItem(FileUploadItem.TYPE_TITLE, nameValue, myFile, repositoryName);
		    	else fui = new FileUploadItem(FileUploadItem.TYPE_FILE, nameValue, myFile, repositoryName);
			    	//See if there is a scaling request for this graphic file. If yes, pass along the height and width
		    	Description fileDescription = new Description();
		    	if (inputData.exists(nameValue + ".description")) {
		    		fileDescription.setText(inputData.getSingleValue(nameValue + ".description"));
		    	}
		    	fui.setDescription(fileDescription);
		    	if (nextItem != null) fui.setMaxWidth(GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "maxWidth"), 0));
		    	if (nextItem != null) fui.setMaxHeight(GetterUtil.get(DefinitionUtils.getPropertyValue(nextItem, "maxHeight"), 0));
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
		    	int intFileCount = 0;
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
						    	repositoryName = "";
						    	if (nextItem != null) 
						    		repositoryName = DefinitionUtils.getPropertyValue(nextItem, "storage");
						    	if (Validator.isNull(repositoryName)) repositoryName = RepositoryUtil.getDefaultRepositoryName();
					    	}
					    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_ATTACHMENT, null, myFile, repositoryName);
					    	Description fileDescription = new Description();
					    	if (inputData.exists(nameValue + Integer.toString(intFileCount) + ".description")) {
					    		fileDescription.setText(inputData.getSingleValue(nameValue + Integer.toString(intFileCount) + ".description"));
					    	}
					    	fui.setDescription(fileDescription);
					    	if(inputData.exists(ObjectKeys.PI_SYNCH_TO_SOURCE)) {
					    		fui.setSynchToRepository(Boolean.parseBoolean(inputData.getSingleValue(ObjectKeys.PI_SYNCH_TO_SOURCE)));
					    	}
					    	fileData.add(fui);
				    	}
				    	intFileCount++;
					} else {
						intFileCount++;
						if (intFileCount > 5) blnCheckForFileUntilTrue = false;
					}
		    	}
		    }
		} else if (itemName.equals("mashupCanvas")) {
			if (inputData.exists(nameValue)) {
				Boolean showBranding = false;
				Boolean showFavoritesAndTeams = false;
				Boolean showNavigation = false;
				Boolean hideToolbar = false;
				String value = "";
				Document propertiesDoc = null;
				
				if ( inputData.exists( nameValue + MASHUP_PROPERTIES ) )
				{
					String propertiesXML;
					
					propertiesXML = inputData.getSingleValue( nameValue + MASHUP_PROPERTIES );
					
					if ( propertiesXML == null )
						propertiesXML = "<landingPageData><background /></landingPageData>";
				
					try
					{
						propertiesDoc = DocumentHelper.parseText( propertiesXML );
					}
		    		catch(Exception e)
		    		{
		    		}
				}
				
				if (inputData.exists(nameValue + MASHUP_SHOW_BRANDING)) {
					String val = inputData.getSingleValue( nameValue + MASHUP_SHOW_BRANDING );
					if (!val.toLowerCase().equals("false")) showBranding = true;
				}
				
				if (inputData.exists(nameValue + MASHUP_SHOW_FAVORITES_AND_TEAMS)) {
					String val = inputData.getSingleValue( nameValue + MASHUP_SHOW_FAVORITES_AND_TEAMS );
					if (!val.toLowerCase().equals("false")) showFavoritesAndTeams = true;
				}
				
				if (inputData.exists(nameValue + MASHUP_SHOW_NAVIGATION)) {
					String val = inputData.getSingleValue( nameValue + MASHUP_SHOW_NAVIGATION );
					if (!val.toLowerCase().equals("false")) showNavigation = true;
				}
				
				if (inputData.exists(nameValue + MASHUP_HIDE_TOOLBAR)) {
					String val = inputData.getSingleValue( nameValue + MASHUP_HIDE_TOOLBAR );
					if (!val.toLowerCase().equals("false")) hideToolbar = true;
				}
				
				// Get the landing page mashup configuration string.
				if ( inputData.exists( nameValue ) )
				{
					value = inputData.getSingleValue( nameValue );
					value = DefinitionHelper.fixUpMashupConfiguration( value, nameValue, fileData );
				}
	
				if ( !inputData.isFieldsOnly() || fieldModificationAllowed )
				{
					entryData.put( nameValue, value );
					entryData.put(nameValue + DefinitionModule.MASHUP_SHOW_BRANDING, showBranding);
					entryData.put(nameValue + DefinitionModule.MASHUP_SHOW_FAVORITES_AND_TEAMS, showFavoritesAndTeams);
					entryData.put(nameValue + DefinitionModule.MASHUP_SHOW_NAVIGATION, showNavigation);
					entryData.put(nameValue + DefinitionModule.MASHUP_HIDE_TOOLBAR, hideToolbar);
					entryData.put( nameValue + DefinitionModule.MASHUP_PROPERTIES, propertiesDoc );
				}
			}
		} else if (itemName.equals("profileConferencingPwd")) {
			if (inputData.exists(nameValue)) {
				if (!inputData.isFieldsOnly() || fieldModificationAllowed) {
					String value = inputData.getSingleValue(nameValue);
					if (!value.equals("********")) {				
						EncryptedValue v = new EncryptedValue();
						v.setValue(value);
						entryData.put(nameValue, v);
					}
				}
			}
		} else if (itemName.equals("profileManageGroups")) {
			//Ignore this item. It gets handled after the Add or Modify is finished.
		} else {
			try {
				if (inputData.exists(nameValue)) {
					if (!inputData.isFieldsOnly() || fieldModificationAllowed) {
						if (inputData.getValues(nameValue).length > 1) {
							entryData.put(nameValue, StringCheckUtil.check(mapInputData(inputData.getValues(nameValue))));
						} else {
							entryData.put(nameValue, StringCheckUtil.check(mapInputData(inputData.getSingleValue(nameValue))));
						}
					}
				}
				if (userVersionAllowed && inputData.exists(nameValuePerUser)) 
					entryData.put(nameValuePerUser, StringCheckUtil.check(inputData.getSingleValue(nameValuePerUser)));
			}
			
			catch (Exception ex) {
				logger.debug("processInputDataItem():  Exception processing default item:  ", ex);
				logger.debug("...itemName:  " + itemName);
				logger.debug("...nameValue:  " + nameValue);
				logger.debug("...inputData.isFieldsOnly():  " + inputData.isFieldsOnly());
				logger.debug("...fieldModificationAllowed:  " + fieldModificationAllowed);
				Object o = inputData.getSingleObject(nameValue);
				logger.debug("...inputData.get(" + nameValue + ") has value:  " + (null != o));
				if (null != o) {
					Class[] ocA = o.getClass().getClasses();
					if (null == ocA) ocA = new Class[0];
					logger.debug("...inputData.get(" + nameValue + ").class:  " + o.getClass().getName() + ", class count:  " + ocA.length);
					for (int i = 0; i < ocA.length; i += 1) {
						logger.debug("...inputData.get(" + nameValue + ").class[" + i + "]:  " + ocA[i].getName());
						i += 1;
					}
				}
			}
		}    	
    }
    
    @Override
	public void tidyCheckText(Description description, EntryDataErrors entryDataErrors) {
		String text = mapInputData(description.getText());
		if (SPropsUtil.getBoolean("HTML.validate", true) && description.getFormat() == Description.FORMAT_HTML) {
			ByteArrayInputStream sr = new ByteArrayInputStream(text.getBytes());
			ByteArrayOutputStream sw = new ByteArrayOutputStream();
			TidyMessageListener tml = new TidyMessageListener();
			Tidy tidy = new Tidy();
			tidy.setQuiet(true);
			tidy.setShowWarnings(false);
			tidy.setMessageListener(tml);
			tidy.setPrintBodyOnly(true);
			tidy.setFixUri(false);
			tidy.setFixComments(false);
			tidy.setAsciiChars(false);
			tidy.setBreakBeforeBR(false);
			tidy.setBurstSlides(false);
			tidy.setDropEmptyParas(false);
			tidy.setDropFontTags(false);
			tidy.setDropProprietaryAttributes(false);
			tidy.setEncloseBlockText(false);
			tidy.setEncloseText(false);
			tidy.setIndentAttributes(false);
			tidy.setIndentCdata(false);
			tidy.setIndentContent(false);
			tidy.setLiteralAttribs(true);
			tidy.setLogicalEmphasis(false);
			tidy.setLowerLiterals(false);
			tidy.setMakeClean(false);
			tidy.setMakeBare(false);
			tidy.setInputEncoding("UTF8");
			tidy.setOutputEncoding("UTF8");
			tidy.setRawOut(true);
			tidy.setSmartIndent(false);
			tidy.setTidyMark(false);
			tidy.setWord2000(true);	// Allows <o:p> constructs as per MS Outlook, MS Word, ...
			tidy.setWrapAsp(false);
			tidy.setWrapAttVals(false);
			tidy.setWrapJste(false);
			tidy.setWrapPhp(false);
			tidy.setWrapScriptlets(false);
			tidy.setWrapSection(false);
			tidy.setWraplen(1000000);
			org.w3c.dom.Document doc = tidy.parseDOM(sr, sw);
			if (tml.isErrors() || tidy.getParseErrors() > 0) {
				entryDataErrors.addProblem(new Problem(Problem.INVALID_HTML, null));
				description.setText("");
			} else {
				if (!text.equals("")) {
					//If the original value was not empty, then store the corrected html
					description.setText(sw.toString().trim());
				}
			}
		} else {
			//HTML validation is turned off, just use whatever the user passed in
			description.setText(text);
		}
    }
    
    @Override
	public List<Definition> getAllDefinitions() {
		// Controllers need access to definitions.  Allow world read
    	return coreDao.loadDefinitions(RequestContextHolder.getRequestContext().getZoneId());
    }

    @Override
	public List<Definition> getAllDefinitions(Integer type) {
		// Controllers need access to definitions.  Allow world read
    	Binder binder = null;
    	FilterControls filter = new FilterControls();
    	filter.add("type", type);
    	List<Definition> defs = coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
    	return Utils.validateDefinitions(defs, binder);
     }

    @Override
	public List<Definition> getDefinitions(Long binderId, Boolean includeAncestors) {
		// Controllers need access to definitions.  Allow world read
       	if (binderId == null) {
       		Binder binder = null;
        	FilterControls filter = new FilterControls()
        		.add(Restrictions.eq("binderId", ObjectKeys.RESERVED_BINDER_ID));
        	List<Definition> defs = coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
        	return Utils.validateDefinitions(defs, binder);
    	}
    	try {
    		Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
   	 		if (includeAncestors.equals(Boolean.TRUE)) {
   	 			Map params = new HashMap();
   	 			List ids = getAncestorIds(binder);
   	 			ids.add(ObjectKeys.RESERVED_BINDER_ID);
   	 			params.put("binderId", ids);
   	 			params.put("zoneId", RequestContextHolder.getRequestContext().getZoneId());
   	 			List<Definition> defs = filterDefinitions(coreDao.loadObjects("from org.kablink.teaming.domain.Definition where binderId in (:binderId) and zoneId=:zoneId", params));
   	 			return Utils.validateDefinitions(defs, binder);
   	 		} else {
   	 			FilterControls filter = new FilterControls().add(Restrictions.eq("binderId", binder.getId()));
   	 			List<Definition> defs = coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
   	 			return Utils.validateDefinitions(defs, binder);
   	 		}
    	} catch (NoBinderByTheIdException nb) {
  	 		if (includeAncestors.equals(Boolean.TRUE)) {
   	 	       	return getDefinitions(null, Boolean.TRUE);
  	 		} else {
    	 		return new ArrayList();
   	 		}

    	}
    }

    @Override
	public List<Definition> getDefinitions(Long binderId, Boolean includeAncestors, Integer type) {
		// Controllers need access to definitions.  Allow world read
    	if (binderId == null) {
    		Binder binder = null;
        	FilterControls filter = new FilterControls()
         		.add(Restrictions.eq("type", type))
         		.add(Restrictions.eq("binderId", ObjectKeys.RESERVED_BINDER_ID));
        	List<Definition> defs = coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
        	return Utils.validateDefinitions(defs, binder);
    	}
    	Binder binder = getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
    	List<Definition> defaultEntryDefinitions = binder.getEntryDefinitions();
    	List<Definition> defs = new ArrayList<Definition>();
   	 	if (includeAncestors.equals(Boolean.TRUE)) {
  	       	Map params = new HashMap();
  	    	params.put("type", type);
  	    	List ids = getAncestorIds(binder);
  	    	ids.add(ObjectKeys.RESERVED_BINDER_ID);
  	    	params.put("binderId", ids);
   	 		params.put("zoneId", RequestContextHolder.getRequestContext().getZoneId());

   	 		defs = filterDefinitions(coreDao.loadObjects("from org.kablink.teaming.domain.Definition where binderId in (:binderId) and zoneId=:zoneId  and type=:type", params));
  	 	} else {
  	      	FilterControls filter = new FilterControls()
  	      		.add(Restrictions.eq("type", type))
  	      		.add(Restrictions.eq("binderId", binder.getId()));
  	      	defs = coreDao.loadDefinitions(filter, RequestContextHolder.getRequestContext().getZoneId());
  	 	}
   	 	for (Definition def : defaultEntryDefinitions) {
   	 		//Make sure to include the default defs in use by this binder
   	 		if (def.getType() == type && !defs.contains(def)) defs.add(def);
   	 	}
 		return Utils.validateDefinitions(defs, binder);

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
    @Override
	public Map getEntryDefinitionElements(String id) {
		//Get a map for the results
		//access doesn't seem needed
    	Map dataElements = new TreeMap();
    	Document definitionTree = null;
		try {
			definitionTree = DefinitionCache.getDocumentWithId(id);
		} catch (NoDefinitionByTheIdException nd) {
			return dataElements;
		}

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
					if (Validator.isNull(captionValue)) captionValue = Html.replaceSpecialChars(nameValue);
					itemData.put("caption", NLT.getDef(captionValue).replaceAll("&", "&amp;"));

					//We have the element name, see if it has option values
					if (itemName.equals("selectbox")) {
						String multipleAllowed = DefinitionUtils.getPropertyValue(nextItem, "multipleAllowed");
						if ("true".equals(multipleAllowed)) {
							itemData.put("multipleAllowed", Boolean.TRUE);
						}
						Map valueMap = new LinkedHashMap();
						Iterator itSelectionItems = nextItem.selectNodes("item[@name='selectboxSelection']").iterator();
						while (itSelectionItems.hasNext()) {
							Element selection = (Element) itSelectionItems.next();
							//Get the element name (property name)
							String selectionNameValue = DefinitionUtils.getPropertyValue(selection, "name");
							String selectionCaptionValue = DefinitionUtils.getPropertyValue(selection, "caption");
							if (Validator.isNotNull(selectionNameValue)) {
								if (Validator.isNull(selectionCaptionValue)) {selectionCaptionValue = Html.replaceSpecialChars(selectionNameValue);}
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
								if (Validator.isNull(selectionCaptionValue)) {selectionCaptionValue = Html.replaceSpecialChars(selectionNameValue);}
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
    
    @Override
	public List<Long> getBindersUsingEntryDef(String entryDefId, String sourceName) {
    	List results = new ArrayList();
    	Long binderId = Long.valueOf(2267);
    	results.add(binderId);
    	return results;
    }

	//Routine to get the data elements for use in search queries
    @Override
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
				if (Validator.isNull(captionValue)) captionValue = Html.replaceSpecialChars(nameValue);
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
    	List<Element> propertyItems = configItem.selectNodes("properties/property");
		for (Element configProperty:propertyItems)
		{
			Element property = properties.addElement("property");
			property.addAttribute("name", configProperty.attributeValue("name"));
			property.addAttribute("value", configProperty.attributeValue("value", ""));
		}

		return;
    }

	@Override
	public void walkDefinition(DefinableEntity entry, DefinitionVisitor visitor, Map args) {
		if(entry.getEntryDefId() == null)
			return;
        Document definitionTree = entry.getEntryDefDoc();
		walkDefinition(definitionTree, visitor, args);
	}
	
	@Override
	public void walkDefinition(Document definitionTree, DefinitionVisitor visitor, Map args) {
		SimpleProfiler.start("DefinitionModuleImpl.walkDefinition");
		//access check not needed = assumed okay from entry
        String flagElementPath = "./" + visitor.getFlagElementName();
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
		SimpleProfiler.stop("DefinitionModuleImpl.walkDefinition");
    }

	private List<Definition> filterDefinitions(List<Definition> definitions) {
		if(!ReleaseInfo.isLicenseRequiredEdition()) {
			for(int i = 0; i < definitions.size();) {
				Definition def = (Definition) definitions.get(i);
				if("_mirroredFileEntry".equals(def.getName()) || "_mirroredFileFolder".equals(def.getName()))
					definitions.remove(i);
				else
					i++;
			}
		}
		return definitions;
	}	

	/*
	 * Maps the input data String's in a String[] as per the mappings
	 * defined in the ssf*.properties files.
	 */
	private static String[] mapInputData(String[] aIn) {
		String[] aOut;
		int aiCount = ((null == aIn) ? 0 : aIn.length);
		aOut = new String[aiCount];
		for (int i = 0; i < aiCount; i += 1) {
			aOut[i] = mapInputData(aIn[i]);
		}
		return aOut;
	}
	
	/*
	 * Maps an input data String as per the mappings defined in the
	 * ssf*.properties files.
	 */
	private static String mapInputData(String sIn) {
		// If there is no String to map...
		if ((null == sIn) || (0 == sIn.length())) {
			// ...simply return what we were given.
			return sIn;
		}

		// If there are no mappings defined...
		initEntryInputDataMap();
		if (0 == entryInputDataMapCount) {
			// ...simply return what we were given.
			return sIn;
		}
		
		// Scan the mappings...
		StringBuffer sbIn = new StringBuffer(sIn);
		int changeCount = 0;
		for (int i = 0; i < entryInputDataMapCount; i += 2) {
			String fromS = entryInputDataMap[i]; int fromLen = fromS.length();
			String toS   = entryInputDataMap[i + 1];
			
			// ...replacing all occurences of each pattern with its
			// ...replacement value.
			int pos;
			while(true) {
				pos = sbIn.indexOf(fromS);
				if (0 > pos) {
					break;
				}
				sbIn = sbIn.replace(pos, (pos + fromLen), toS);
				changeCount += 1;
			}
		}
		
		// If we didn't perform any mappings...
		String sOut;
		if (0 == changeCount) {
			// ...return what we were given...
			sOut = sIn;
		}
		
		else {
			// ...otherwise, sbIn refers to a StringBuffer containing
			// ...the input String with the mappings having been done.
			// ...Return it.
			sOut = sbIn.toString();
		}
		
		return sOut;
	}
	
	/*
	 * Initializes the entry input data map if it hasn't already been
	 * initialized.
	 */
	private static void initEntryInputDataMap() {
		if ((-1) == entryInputDataMapCount) {
			String entryInputDataMapS = SPropsUtil.getString("entry.inputdata.map", "");
			if ((null != entryInputDataMapS) && (0 < entryInputDataMapS.length())) {
				entryInputDataMap = entryInputDataMapS.split(",");
				entryInputDataMapCount = ((null == entryInputDataMap) ? 0 : entryInputDataMap.length);
				if (1 == (entryInputDataMapCount % 2)) entryInputDataMapCount -= 1;
			}
			else {
				entryInputDataMap = new String[0];
				entryInputDataMapCount = 0;
			}
		}
	}
	
	private class TidyMessageListener implements org.w3c.tidy.TidyMessageListener {
		private int errorCount = 0;
		@Override
		public void messageReceived(TidyMessage message) {
			message.toString();
			errorCount++;
		}
		public boolean isErrors() {
			if (errorCount > 0) return true;
			return false;
		}
	}
	
    @Override
	public Set<String> filterInputDataKeysByDataType(Document definitionTree, InputDataAccessor inputData, List<String> dataTypes) {
    	// IMPORTANT: This method MUST be kept in synch with getEntryData() method.
    	
    	Set<String> result = new HashSet<String>();
    	
    	// Let's first handle "known" attributes that may not be in the definition.
    	// We don't want these known attributes to be excluded just because they
    	// are not defined in the definition.
    	// Since we're using a set, it won't be a problem even if the same attribute
    	// was actually found in the definition later on (hence put in twice).
    	if(dataTypes.contains("title") && inputData.exists("title"))
    		result.add("title");
    	if(dataTypes.contains("description") && inputData.exists("description"))
    		result.add("description");
    	if(dataTypes.contains("text") && inputData.exists("_zoneUUID"))
    		result.add("_zoneUUID");
    	
    	// Now handle those data elements in the definition.
    	if(definitionTree != null) {
			Element root = definitionTree.getRootElement();
			Element entryFormItem = (Element)root.selectSingleNode("item[@type='form']");
			if (entryFormItem != null) {
				String itemName;
				String itemDataType;
				String nameValue;
				List<Element> itItems = entryFormItem.selectNodes(".//item[@type='data']");
				for (Element nextItem: itItems) {
					itemName = (String) nextItem.attributeValue("name");
					if(!Validator.isNull(itemName)) {
						itemDataType = (String) nextItem.attributeValue("dataType");
						if(Validator.isNull(itemDataType)) {
							Element configItem = this.definitionBuilderConfig.getItem(this.definitionConfig, itemName);
							itemDataType = (String) configItem.attributeValue("dataType");
						}
						if(itemDataType != null && dataTypes.contains(itemDataType)) {
							nameValue = DefinitionUtils.getPropertyValue(nextItem, "name");
							if (Validator.isNull(nameValue))
								nameValue = nextItem.attributeValue("name");
							if(nameValue != null && inputData.exists(nameValue))
								result.add(nameValue);
						}
					}
				}				
			}
    	}
    	return result;
    }
}
