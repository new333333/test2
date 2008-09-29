package com.sitescape.team.module.template.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.index.Term;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Dashboard;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityDashboard;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoBinderByTheNameException;
import com.sitescape.team.domain.NoDefinitionByTheIdException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.UserPrincipal;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.binder.processor.BinderProcessor;
import com.sitescape.team.module.dashboard.DashboardModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.shared.EntryBuilder;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.shared.ObjectBuilder;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.module.template.TemplateService;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.StatusTicket;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;
import com.sitescape.util.search.Constants;

public class TemplateModuleImpl extends CommonDependencyInjection implements
		TemplateService {
	private QName schemaLocationAttr = new QName("schemaLocation", new Namespace("xsi", "http://www.w3.org/2001/XMLSchema-instance"));
	private String schemaLocation = "http://www.icecore.org/template";
	private String schemaVersion = "0.1";
	private static final String[] defaultDefAttrs = new String[]{ObjectKeys.FIELD_INTERNALID, ObjectKeys.FIELD_ENTITY_DEFTYPE};

    private DefinitionModule definitionModule;
    @Required
    public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}

	private AdminModule adminModule;
	@Required
	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}

	private BinderModule binderModule;
	@Required
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}

   	private FileModule fileModule;
   	@Required
   	public void setFileModule(FileModule fileModule) {
   		this.fileModule = fileModule;
   	}

   	private DashboardModule dashboardModule;
   	@Required
   	public void setDashboardModule(DashboardModule dashboardModule) {
		this.dashboardModule = dashboardModule;
	}

   	private TransactionTemplate transactionTemplate;
   	@Required
   	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
   	
	public void setSchemaLocationAttr(QName schemaLocationAttr) {
		this.schemaLocationAttr = schemaLocationAttr;
	}

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	private BinderProcessor loadBinderProcessor(Binder binder) {
		return (BinderProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
	}

	public void updateDefaultTemplates(Long topId) {
		Workspace top = (Workspace)getCoreDao().loadBinder(topId, topId);
		
		//default definitions stored in separate config file
		String startupConfig = SZoneConfig.getString(top.getName(), "property[@name='startupConfig']", "config/startup.xml");
		SAXReader reader = new SAXReader(false);  
		InputStream in=null;
		try {
			in = new ClassPathResource(startupConfig).getInputStream();
			Document cfg = reader.read(in);
			in.close();
			//Now setup configurations
			List elements = cfg.getRootElement().selectNodes("templateFile");
			for (int i=0; i<elements.size(); ++i) {
				Element element = (Element)elements.get(i);
				String file = element.getTextTrim();
				reader = new SAXReader(false);  
				try {
					in = new ClassPathResource(file).getInputStream();
					Document doc = reader.read(in);
					addTemplate(doc,true);
					getCoreDao().flush();
				} catch (Exception ex) {
					logger.error("Cannot add template:" + file + " " + ex.getMessage());
					return; //cannot continue, rollback is enabled
				} finally {
					if (in!=null) in.close();
				}
			}
		} catch (Exception ex) {
			logger.error("Cannot read startup configuration:", ex);
		}
	}
    //These should be created when the zone is created, but just in case provide minimum backup
	public TemplateBinder addDefaultTemplate(int type) {
	   	//This is called as a side effect to bootstrap
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		List defs = new ArrayList();
		TemplateBinder config = new TemplateBinder();
		Definition entryDef;
		switch (type) {
			case Definition.FOLDER_VIEW: {
				List result = getCoreDao().loadObjects(TemplateBinder.class, 
							new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_FOLDER_CONFIG, Integer.valueOf(type)}), zoneId);
				if (!result.isEmpty()) return (TemplateBinder)result.get(0);
				config.setTemplateTitle("__template_default_folder");
				config.setTemplateDescription("__template_default_folder_description");
				config.setInternalId(ObjectKeys.DEFAULT_FOLDER_CONFIG);
				entryDef = definitionModule.addDefaultDefinition(Definition.FOLDER_VIEW);
				config.setEntryDef(entryDef);
				defs.add(entryDef);
				defs.add(definitionModule.addDefaultDefinition(Definition.FOLDER_ENTRY));
				break;
			}
			case Definition.WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(TemplateBinder.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_WORKSPACE_CONFIG, Integer.valueOf(type)}), zoneId);
				if (!result.isEmpty()) return (TemplateBinder)result.get(0);
				config.setTemplateTitle("__template_workspace");
				config.setTemplateDescription("__template_workspace_description");
				config.setInternalId(ObjectKeys.DEFAULT_WORKSPACE_CONFIG);
				entryDef = definitionModule.addDefaultDefinition(type);
				config.setEntryDef(entryDef);
				defs.add(entryDef);
				break;
			}
			case Definition.USER_WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(TemplateBinder.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_USER_WORKSPACE_CONFIG, Integer.valueOf(type)}), zoneId);
				if (!result.isEmpty()) return (TemplateBinder)result.get(0);
				
				config.setTemplateTitle("__template_user_workspace");
				config.setTemplateDescription("__template_user_workspace_description");
				config.setInternalId(ObjectKeys.DEFAULT_USER_WORKSPACE_CONFIG);
				entryDef = definitionModule.addDefaultDefinition(type);
				config.setEntryDef(entryDef);
				defs.add(entryDef);
				break;
			}
		default: {
			throw new IllegalArgumentException("Invalid type:" + type);
			}
		}
		
		Map updates = new HashMap();
		config.setTitle(config.getTemplateTitle());
		config = doAddTemplate(config, type, updates, RequestContextHolder
				.getRequestContext().getZone(), RequestContextHolder
				.getRequestContext().getUser());
		config.setDefinitionsInherited(false);
		config.setDefinitions(defs);
		
		if (type == Definition.USER_WORKSPACE_VIEW) {
			//get default folder definition
			HistoryStamp stamp = new HistoryStamp(RequestContextHolder.getRequestContext().getUser());
			TemplateBinder def = addDefaultTemplate(Definition.FOLDER_VIEW);
			TemplateBinder newDef = new TemplateBinder(def);
			newDef.setCreation(stamp);
			newDef.setModification(stamp);
			newDef.setOwner(null);
			newDef.setFunctionMembershipInherited(true);
			newDef.setPathName(config.getPathName() + "/" + newDef.getTitle());
			getCoreDao().save(newDef);  //generateId for binderKey needed by custom attributes
			config.addBinder(newDef);
			copyBinderAttributes(def, newDef);
			
		}
		return config;
	 }
	//add top level template
	 public Long addTemplate(int type, Map updates) {
	    checkAccess(TemplateOperation.manageTemplate);
		TemplateBinder template = new TemplateBinder();
		String name = (String)updates.get(ObjectKeys.FIELD_BINDER_NAME);
		//only top level needs a name
		if (Validator.isNull(name)) {
			 name = (String)updates.get(ObjectKeys.FIELD_TEMPLATE_TITLE);
			 if (Validator.isNull(name)) {
				 throw new IllegalArgumentException(NLT.get("general.required.name"));
			 }
		 }
		template.setName(name);
		if (!validateTemplateName(null, name)) throw new NotSupportedException("errorcode.notsupported.duplicateTemplateName", new Object[]{name});
		Definition entryDef = definitionModule.addDefaultDefinition(type);
		template.setEntryDef(entryDef);
		if (type == Definition.FOLDER_VIEW) template.setLibrary(true);
		List<Definition> definitions = new ArrayList<Definition>();
		definitions.add(entryDef);
		template.setDefinitionsInherited(false);
		template.setDefinitions(definitions);
		template.setFunctionMembershipInherited(true);
       	String icon = DefinitionUtils.getPropertyValue(entryDef.getDefinition().getRootElement(), "icon");
       	if (Validator.isNotNull(icon))
			template.setIconName(icon);
		doAddTemplate(template, type, updates, RequestContextHolder
				.getRequestContext().getZone(), RequestContextHolder
				.getRequestContext().getUser());
	    return template.getId();
	 }
	 
	 //add top level template
	 public Long addTemplate(Document doc, boolean replace) {
		 checkAccess(TemplateOperation.manageTemplate);
		 return addTemplate(doc, replace,
				RequestContextHolder.getRequestContext().getZone()).getId();
	 }
	 
	 
	 protected void doTemplate(TemplateBinder template, Element config, Workspace zone, UserPrincipal user) {
		 Integer type = Integer.valueOf(config.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_TYPE));
		 template.setLibrary(GetterUtil.get(XmlUtils.getProperty(config, ObjectKeys.XTAG_BINDER_LIBRARY), false));
		 template.setUniqueTitles(GetterUtil.get(XmlUtils.getProperty(config, ObjectKeys.XTAG_BINDER_UNIQUETITLES), false));
		 template.setDefinitionsInherited(GetterUtil.get(XmlUtils.getProperty(config, ObjectKeys.XTAG_BINDER_INHERITDEFINITIONS), false));
		 template.setFunctionMembershipInherited(GetterUtil.get(XmlUtils.getProperty(config, ObjectKeys.XTAG_BINDER_INHERITFUNCTIONMEMBERSHIP), true));
		 template.setTeamMembershipInherited(GetterUtil.get(XmlUtils.getProperty(config, ObjectKeys.XTAG_BINDER_INHERITTEAMMEMBERS), true));
		 //get attribute from document
		 Map updates = XmlUtils.getCustomAttributes(config);
		 doAddTemplate(template, type, updates, zone, user);
		 //setup after template is saved
		 if (!template.isTeamMembershipInherited()) XmlUtils.getTeamMembersFromXml(template, config, this);
		 if (!template.isFunctionMembershipInherited()) XmlUtils.getFunctionMembershipFromXml(template, config, this);
		 template.setEntryDef(template.getDefaultViewDef());
		 Element dashboardConfig = (Element)config.selectSingleNode(ObjectKeys.XTAG_ELEMENT_TYPE_DASHBOARD);
		 if (dashboardConfig != null) {
			dashboardModule.createEntityDashboard(template.getEntityIdentifier(), dashboardConfig);
		 }
	 }
	 protected TemplateBinder doAddTemplate(TemplateBinder template, int type, Map updates, Workspace zone, UserPrincipal user) {
		 template.setZoneId(zone.getZoneId());
		 template.setDefinitionType(type);
		 template.setCreation(new HistoryStamp(user));
		 template.setModification(template.getCreation());
		 EntryBuilder.updateEntry(template, updates);
		 if (Validator.isNull(template.getTitle())) template.setTitle(template.getTemplateTitle());
		 if (template.isRoot()) {
			 template.setPathName("/" + template.getTitle());
		 } else {
			 template.setPathName(template.getParentBinder().getPathName() + "/" + template.getTitle());
		 }
		 getCoreDao().save(template);
		 if (template.isRoot()) {
			 template.setupRoot();
			 getCoreDao().flush(); //flush cause binderSortKey is null before setup
		 }
		 return template;
	 }

	 //clone a top level template as a child of another template
	 public Long addTemplate(Long parentId, Long srcConfigId) {
		 checkAccess(TemplateOperation.manageTemplate);
	    TemplateBinder parentConfig = (TemplateBinder)getCoreDao().loadBinder(parentId, RequestContextHolder.getRequestContext().getZoneId());
	    TemplateBinder srcConfig = (TemplateBinder)getCoreDao().loadBinder(srcConfigId, RequestContextHolder.getRequestContext().getZoneId());
	    return addTemplate(parentConfig, srcConfig);
	 }
		protected Binder copyBinderAttributes(Binder source, Binder destination) {
			EntityDashboard dashboard = getCoreDao().loadEntityDashboard(source.getEntityIdentifier());
			if (dashboard != null) {
				EntityDashboard myDashboard = new EntityDashboard(dashboard);
				myDashboard.setOwnerIdentifier(destination.getEntityIdentifier());
				getCoreDao().save(myDashboard);
			  }
			//copy all file attachments
			fileModule.copyFiles(source, source, destination, destination);
			EntryBuilder.copyAttributes(source, destination);
			return destination;		
		}
	 protected Long addTemplate(TemplateBinder parentConfig, TemplateBinder srcConfig) {
		 TemplateBinder config = new TemplateBinder(srcConfig);
		 config.setCreation(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
		 config.setModification(config.getCreation());
		 config.setOwner(null);
		 if (Validator.isNull(config.getTitle())) config.setTitle(config.getTemplateTitle());
		 config.setPathName(parentConfig.getPathName() + "/" + config.getTitle());
      	 getCoreDao().updateFileName(parentConfig, config, null, config.getTitle());
		 //get childen before adding new children incase parent and source are the same
		 List<TemplateBinder> children = new ArrayList<TemplateBinder>(srcConfig.getBinders());
      	 
		 parentConfig.addBinder(config);  //setup binder_sortKey
		 getCoreDao().save(config); // generateId for binderKey needed by custom attributes
    	 copyBinderAttributes(srcConfig, config);
	     if (!config.isFunctionMembershipInherited()) {
	    	 //copy to new template
	    	 getWorkAreaFunctionMembershipManager().copyWorkAreaFunctionMemberships(config.getZoneId(), srcConfig, config);
	     }

		 for (TemplateBinder c:children) {
			 addTemplate(config, c);
		 }
		 return config.getId();
	 }
	 protected boolean validateTemplateName(Long binderId, String name) {
		 if (Validator.isNull(name)) return false;
			try {
				TemplateBinder binder = getTemplateByName(name);
				if (binderId != null && binder.getId().equals(binderId)) return true;
			} catch (NoBinderByTheNameException nb) {
				return true;
			} catch (Exception ex) {};
		 return false;
	 }
	   public Long addTemplateFromBinder(Long binderId) throws AccessControlException, WriteFilesException {
		   checkAccess(TemplateOperation.manageTemplate);
		   Long zoneId =  RequestContextHolder.getRequestContext().getZoneId();
		   Binder binder = (Binder)getCoreDao().loadBinder(binderId, zoneId);
		   TemplateBinder config = templateFromBinder(null, binder);
		   return config.getId();
		}
		protected TemplateBinder templateFromBinder(TemplateBinder parent, Binder binder) {
			//get binder setup
			if (binder.getDefinitionType() == null) {
				definitionModule.setDefaultBinderDefinition(binder);
			}	
			TemplateBinder config = new TemplateBinder(binder);
			if (parent == null) {
				//need a name				   
				String name = config.getName();
				if (Validator.isNull(name)) name = config.getTemplateTitle();
				int next=1;
				while (!validateTemplateName(null, name)) {
					name = name + next++;
				}
				config.setName(name);
			} else {
				config.setName(null);
			}
			config.setCreation(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
			config.setModification(config.getCreation());
			config.setOwner(null);
			if (parent == null) {
				config.setPathName("/" + config.getTitle());				
			} else {
				config.setPathName(parent.getPathName() + "/" + config.getTitle());				
			}
			getCoreDao().save(config); //need id for binderKey
	      	if (parent != null) {
				parent.addBinder(config);
	      		getCoreDao().updateFileName(parent, config, null, config.getTitle());
	      	} else {
	      		config.setupRoot();
	      		getCoreDao().flush();//flush cause binderSortKey is null before setup
	      	}
			copyBinderAttributes(binder, config);
			if (!config.isFunctionMembershipInherited()) {
				getWorkAreaFunctionMembershipManager().copyWorkAreaFunctionMemberships(binder.getZoneId(), binder, config);
			}
			List<Binder> children = binder.getBinders();    
			for (Binder child: children) {
				templateFromBinder(config, child);	    	
			}
			return config;
			
	}
	public void modifyTemplate(Long id, Map updates) {
		checkAccess(TemplateOperation.manageTemplate);
		TemplateBinder config = getCoreDao().loadTemplate(id, RequestContextHolder.getRequestContext().getZoneId());
		if (config.isRoot() && updates.containsKey(ObjectKeys.FIELD_BINDER_NAME)) { //ignore name for others
			String name = (String)updates.get(ObjectKeys.FIELD_BINDER_NAME);
			if (Validator.isNull(name)) {
				throw new IllegalArgumentException(NLT.get("general.required.name"));
			} else if (!validateTemplateName(id, name)) {
				try {
					TemplateBinder binder = getTemplateByName(name);
					if (!binder.getId().equals(id)) {
						throw new NotSupportedException("errorcode.notsupported.duplicateTemplateName", new Object[]{name});
					}
				} catch (org.springframework.dao.IncorrectResultSizeDataAccessException in) {
					throw new NotSupportedException("errorcode.notsupported.duplicateTemplateName", new Object[]{name});
				}
			}
			config.setName(name);
		 }
		if (updates.containsKey(ObjectKeys.FIELD_TEMPLATE_TITLE)) {
			config.setTemplateTitle((String)updates.get(ObjectKeys.FIELD_TEMPLATE_TITLE));
		}
		if (updates.containsKey(ObjectKeys.FIELD_TEMPLATE_DESCRIPTION)) {
			config.setTemplateDescription((Description)updates.get(ObjectKeys.FIELD_TEMPLATE_DESCRIPTION));
		}

		ObjectBuilder.updateObject(config, updates);
	}
	
	/**
	 * Returns the schema location URI from the configured schema location and
	 * version number.
	 * 
	 * @return the schema location URI from the configured schema location and
	 *         version number.
	 */
	protected String getSchemaLocationUri() {
    	return schemaLocation + "-" + schemaVersion;
    }
	
	public Document getTemplateAsXml(TemplateBinder binder) {
		Document doc = DocumentHelper.createDocument();
		Element element = doc.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_TEMPLATE);
		element.addAttribute(schemaLocationAttr, getSchemaLocationUri() + " " + getSchemaLocationUri());
		getTemplateAsXml(binder, element);
		return doc;
	}
	protected void getTemplateAsXml(TemplateBinder binder, Element element) {
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, binder.getDefinitionType().toString());
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID, binder.getInternalId());
		
		XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_TEMPLATE_TITLE, ObjectKeys.XTAG_TYPE_STRING, binder.getTemplateTitle());
		XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_TEMPLATE_DESCRIPTION, ObjectKeys.XTAG_TYPE_DESCRIPTION, binder.getTemplateDescription());
		XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_ENTITY_TITLE, ObjectKeys.XTAG_TYPE_STRING, binder.getTitle());
		XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_ENTITY_DESCRIPTION, ObjectKeys.XTAG_TYPE_DESCRIPTION, binder.getDescription());
		XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_ENTITY_ICONNAME, ObjectKeys.XTAG_TYPE_STRING, binder.getIconName());			
		if (binder.isRoot()) XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_BINDER_NAME, ObjectKeys.XTAG_TYPE_STRING, binder.getName());

		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_LIBRARY, binder.isLibrary());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_UNIQUETITLES, binder.isUniqueTitles());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITFUNCTIONMEMBERSHIP, binder.isFunctionMembershipInherited());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITDEFINITIONS, binder.isDefinitionsInherited());

 		// The controller for createing a new binder ignores this, but leave here anyway 
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITTEAMMEMBERS, binder.isTeamMembershipInherited());
		if (!binder.isTeamMembershipInherited()) {
			//store as names, not ids
			Set<Long> ids = binder.getTeamMemberIds();
			List<UserPrincipal> members = getProfileDao().loadUserPrincipals(ids, binder.getZoneId(), true);
			for (Principal p:members) {
				XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_TEAMMEMBER_NAME, p.getName());	    				 
			}
		}

		Set<Map.Entry> mes = binder.getCustomAttributes().entrySet();
		for (Map.Entry me: mes) {
			CustomAttribute attr = (CustomAttribute)me.getValue();
			attr.toXml(element);
		}
		
		if (!binder.isFunctionMembershipInherited()) {
			 //need to convert all ids to names??
	    	 List<WorkAreaFunctionMembership> wfms = adminModule.getWorkAreaFunctionMemberships(binder);
	    	 for (WorkAreaFunctionMembership fm: wfms) {
	    		 Set ids = fm.getMemberIds();
	    		 List<UserPrincipal> members = getProfileDao().loadUserPrincipals(ids, binder.getZoneId(), true);
	    		 try {
	    			 Element e = element.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_FUNCTION_MEMBERSHIP);
	    			 Function function = getFunctionManager().getFunction(binder.getZoneId(), fm.getFunctionId());
	    			 XmlUtils.addProperty(e, ObjectKeys.XTAG_WA_FUNCTION_NAME, function.getName());
	    			 for (Principal p:members) {
	    				 //Not sure is their is a universal separator that could be used,..
		    			 XmlUtils.addProperty(e, ObjectKeys.XTAG_WA_MEMBER_NAME, p.getName());	    				 
	    			 }
	    			 StringBuffer idsString = new StringBuffer();
	    			 if (ids.contains(ObjectKeys.OWNER_USER_ID))
	    				 idsString.append(ObjectKeys.OWNER_USER_ID.toString() + " ");
	    			 if (ids.contains(ObjectKeys.TEAM_MEMBER_ID))
	    				 idsString.append(ObjectKeys.TEAM_MEMBER_ID.toString() + " ");
	    			 XmlUtils.addProperty(e, ObjectKeys.XTAG_WA_MEMBERS, idsString);
	    		 } catch (NoObjectByTheIdException no) {continue;} 
	    		 
	    	 }	    
		}
		List<Definition> defs = binder.getDefinitions();
		if (defs.isEmpty() || binder.isDefinitionsInherited()) {
			Definition def = binder.getEntryDef();
			if (def != null) {
				XmlUtils.addDefinitionReference(element, def);
			}
			
		} else {
			for (Definition def:defs) {
				XmlUtils.addDefinitionReference(element, def);
			}
		}
		Map<String,Definition> workflows = binder.getWorkflowAssociations();
		for (Map.Entry<String,Definition> me: workflows.entrySet()) {
			//log the name the workflow refers to
			try {
				Definition entryDef = getCoreDao().loadDefinition(me.getKey(), binder.getZoneId());
				Element wf = XmlUtils.addDefinitionReference(element, me.getValue());
				//log the definitionId the workflow refers to
				XmlUtils.addDefinitionReference(wf, entryDef);
			} catch (NoDefinitionByTheIdException nd) {};
	
		}
		
		Dashboard dashboard = dashboardModule.getEntityDashboard(binder.getEntityIdentifier());
		if (dashboard != null) dashboard.asXml(element);
		
		List<TemplateBinder> children = binder.getBinders();
		for (TemplateBinder child:children) {
			Element childElement = element.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_TEMPLATE);
			getTemplateAsXml(child, childElement);
			
		}

	}
	public TemplateBinder getTemplate(Long id) {
		//public
		return getCoreDao().loadTemplate(id, RequestContextHolder.getRequestContext().getZoneId());
	}
	public TemplateBinder getTemplateByName(String name) {
		//public
		return getCoreDao().loadTemplateByName(name, RequestContextHolder.getRequestContext().getZoneId());
	}
	public List<TemplateBinder> getTemplates() {
		//world read
		return getCoreDao().loadTemplates(RequestContextHolder.getRequestContext().getZoneId());
	}
	public List<TemplateBinder> getTemplates(int type) {
		//world read
		return getCoreDao().loadTemplates( RequestContextHolder.getRequestContext().getZoneId(), type);
	}
	//no transaction - Adding the top binder can lead to optimisitic lock exceptions.
	//In order to reduce the risk, we try to shorten the transaction time by managing it ourselves
	public Long addBinder(final Long configId, final Long parentBinderId, final String title, final String name) throws AccessControlException {
		//The first add is independent of the others.  In this case the transaction is short 
		//and managed by processors.  
		
		TemplateBinder cfg = getCoreDao().loadTemplate(configId, RequestContextHolder.getRequestContext().getZoneId());
		Binder parent = getCoreDao().loadBinder(parentBinderId, RequestContextHolder.getRequestContext().getZoneId());
		Map ctx = new HashMap();
		//force a lock so contention on the sortKey is reduced
		ctx.put(ObjectKeys.INPUT_OPTION_FORCE_LOCK, Boolean.TRUE);
		final Binder top = addBinderInternal(cfg, parent, title, name, ctx);
		//commit index, binder commited
		IndexSynchronizationManager.applyChanges();
		//now that we have registered the sortKey in the parent binder, we use a longer transaction to complete 
		//it - there shouldn't be any contention here since the binder is new and doesn't need to reference its parent
		transactionTemplate.execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	        //need to reload incase addFolder/workspace used retry loop where session cache is flushed by exception
	        TemplateBinder cfg = getCoreDao().loadTemplate(configId, RequestContextHolder.getRequestContext().getZoneId());
 			addBinderFinish(cfg, top);
 			return null;
  	     }});

		IndexSynchronizationManager.discardChanges();
	 	//need to reindex binder tree, cause of copy Attributes code
		IndexSynchronizationManager.deleteDocuments(new Term(Constants.ENTRY_ANCESTRY, top.getId().toString()));
	 	loadBinderProcessor(top).indexTree(top, null, StatusTicket.NULL_TICKET);
	 	//top will be evicted, reread
		getCoreDao().refresh(top);

	 	//flush changes so we can use them to fix up dashboards
		IndexSynchronizationManager.applyChanges();
	 	//after children are added, resolve relative selections
		transactionTemplate.execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		List<Binder>binders = new ArrayList();
        		binders.add(top);
        		while (!binders.isEmpty()) {
        			Binder b = binders.remove(0);
        			binders.addAll(b.getBinders());
        			try {
        				EntityDashboard dashboard = getCoreDao().loadEntityDashboard(b.getEntityIdentifier());
        				if (dashboard != null) {
        					DashboardHelper.resolveRelativeBinders(b, dashboard);
        				}
        			} catch (Exception ex) {
        				//at this point just log errors  index has already been updated
        				//	if throw errors, rollback will take effect and must manualy remove from index
        				logger.error("Error adding dashboard " + ex.getLocalizedMessage());
        			}
        		}
        		return null;
	     }});

		return top.getId();

	}
	protected void addBinderFinish(TemplateBinder cfg, Binder binder) throws AccessControlException {
		   Map props = cfg.getProperties();
		   if (props != null) binder.setProperties(props);
		   copyBinderAttributes(cfg, binder);
		   //first flush updates, addBinder might do a refresh which overwrites changes
		   getCoreDao().flush();
		   List<TemplateBinder> children = cfg.getBinders();    
		   for (TemplateBinder child: children) {
			   Binder childBinder = addBinderInternal(child, binder, NLT.getDef(child.getTitle()), null, null);	
			   addBinderFinish(child, childBinder);
		   }

	}

	protected Binder addBinderInternal(TemplateBinder cfg, Binder parentBinder, String title, String name, Map ctx) throws AccessControlException {
	   Long zoneId =  RequestContextHolder.getRequestContext().getZoneId();
	   Binder binder;
	   Definition def = cfg.getDefaultViewDef();
	   if (def == null) {
		   def = definitionModule.addDefaultDefinition(cfg.getDefinitionType());
	   }
	   Map fileItems = new HashMap();
	   Map entryData = new HashMap();
	   InputDataAccessor inputData = new MapInputData(entryData);
	   if (Validator.isNull(title)) title = NLT.getDef(cfg.getTitle());
	   if (Validator.isNull(title)) title = NLT.getDef(cfg.getTemplateTitle());
	   entryData.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
	   String description=null;
	   if (cfg.getDescription() != null) description = NLT.getDef(cfg.getDescription().getText());
	   if (Validator.isNotNull(description)) entryData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, description);
	   entryData.put(ObjectKeys.FIELD_BINDER_LIBRARY, Boolean.toString(cfg.isLibrary()));
	   entryData.put(ObjectKeys.FIELD_BINDER_UNIQUETITLES, Boolean.toString(cfg.isUniqueTitles()));
	   entryData.put(ObjectKeys.FIELD_BINDER_INHERITTEAMMEMBERS, Boolean.toString(cfg.isTeamMembershipInherited()));
	   //if not null, use icon from template.  Otherwise try icon from definition when binder is created.
	   if (Validator.isNotNull(cfg.getIconName())) entryData.put(ObjectKeys.FIELD_ENTITY_ICONNAME, cfg.getIconName());
	   if (Validator.isNotNull(name)) entryData.put(ObjectKeys.FIELD_BINDER_NAME, name);
	   if (!cfg.isDefinitionsInherited()) {
		   entryData.put(ObjectKeys.INPUT_FIELD_DEFINITIONS, cfg.getDefinitions());
		   entryData.put(ObjectKeys.INPUT_FIELD_WORKFLOWASSOCIATIONS, cfg.getWorkflowAssociations());
	   }
	   if (!cfg.isFunctionMembershipInherited()) {
			entryData.put(ObjectKeys.INPUT_FIELD_FUNCTIONMEMBERSHIPS, adminModule.getWorkAreaFunctionMemberships(cfg));
	   }	    	
	   //get binder created
	   try {
			   binder = getCoreDao().loadBinder(binderModule.addBinder(parentBinder.getId(), def.getId(), inputData, fileItems, ctx), zoneId);
	   } catch (WriteFilesException wf) {
		   //don't fail, but log it
  			logger.error("Error creating binder from template: ", wf);
  			binder = getCoreDao().loadBinder(wf.getEntityId(), zoneId);
	   }
	   return binder;
	}
	/**
   	 * Use operation so we can keep the logic out of application
	 * and easisly change the required rights
	 */
   	public boolean testAccess(TemplateOperation operation) {
   		try {
   			checkAccess(operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}
   	}
   	public void checkAccess(TemplateOperation operation) {
   		switch (operation) {
   		case manageTemplate:
   	   		Binder top = RequestContextHolder.getRequestContext().getZone();
   			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
   			break;
   		default:
   			throw new NotSupportedException(operation.toString(), "checkAccess");

   		}
   	}
	public TemplateBinder addTemplate(Document document, boolean replace,
			Workspace zone) {
		Element config = document.getRootElement();
		// check name
		String prefix = "";
		if (StringUtils.isNotBlank(config.getNamespaceURI())) {
			// namespace URI has been declared, associate a prefix
			config.addNamespace("_ns", config.getNamespaceURI());
			prefix = "_ns:";
		}
		String name = config.selectSingleNode(
				prefix + ObjectKeys.XTAG_ELEMENT_TYPE_ATTRIBUTE + "[@name='"
						+ ObjectKeys.XTAG_BINDER_NAME + "']").getText();
		if (Validator.isNull(name)) {
			name = (String) XmlUtils.getCustomAttribute(config,
					ObjectKeys.XTAG_TEMPLATE_TITLE);
			if (Validator.isNull(name)) {
				throw new IllegalArgumentException(NLT
						.get("general.required.name"));
			}
		}
		String internalId = config.attributeValue(new QName(
				ObjectKeys.XTAG_ATTRIBUTE_INTERNALID, config.getNamespace()));
		if (Validator.isNotNull(internalId)) {
			try {
				// see if it exists
				Binder binder = getCoreDao().loadReservedBinder(internalId,
						zone.getZoneId());
				if (binder instanceof TemplateBinder) {
					// if it exists, delete it
					binderModule.deleteBinder(binder.getId());
				} else {
					throw new ConfigurationException(
							"Reserved binder exists with same internal id");
				}
			} catch (NoBinderByTheNameException nb) {
				// okay doesn't exists
			}
			;
		} else {
			@SuppressWarnings("unchecked")
			List<TemplateBinder> binders = getCoreDao().loadObjects(
					TemplateBinder.class,
					new FilterControls(ObjectKeys.FIELD_BINDER_NAME, name),
					zone.getZoneId());
			if (!binders.isEmpty()) {
				if (replace)
					binderModule.deleteBinder(binders.get(0).getId());
				else
					throw new NotSupportedException(
							"errorcode.notsupported.duplicateTemplateName",
							new Object[] { name });
			}
		}
		TemplateBinder template = new TemplateBinder();
		// only top level needs a name
		template.setName(name);
		template.setInternalId((internalId));
		doTemplate(template, config, zone, profileDao.findUserByName(
				SZoneConfig.getAdminUserName(zone.getName()), zone.getName()));
		// see if any child configs need to be copied
		@SuppressWarnings("unchecked")
		List<Element> nodes = config.selectNodes("./"
				+ ObjectKeys.XTAG_ELEMENT_TYPE_TEMPLATE);
		for (int i = 0; i < nodes.size(); ++i) {
			Element element = nodes.get(i);
			TemplateBinder child = new TemplateBinder();
			template.addBinder(child);
			doTemplate(child, element, zone, profileDao.findUserByName(
					SZoneConfig.getAdminUserName(zone.getName()), zone
							.getName()));
		}
		// need to flush, if multiple loaded in 1 transaction the binderKey may
		// not have been
		// flushed which could result in duplicates on the next save when
		// loading multiple nfor updatetTemplates
		getCoreDao().flush();
		return template;
	}
}
