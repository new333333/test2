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
package com.sitescape.team.module.admin.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.mail.internet.InternetAddress;

import org.apache.lucene.search.Query;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.InvalidArgumentException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.OrderBy;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityDashboard;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.jobs.EmailPosting;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.jobs.SendEmail;
import com.sitescape.team.mail.MailManager;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.binder.BinderComparator;
import com.sitescape.team.module.binder.BinderProcessor;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.shared.ObjectBuilder;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.search.BasicIndexUtils;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.search.QueryBuilder;
import com.sitescape.team.search.SearchObject;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.FunctionExistsException;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;

/**
 * @author Janet McCann
 *
 */
public class AdminModuleImpl extends CommonDependencyInjection implements AdminModule {
	private static final String[] defaultDefAttrs = new String[]{ObjectKeys.FIELD_INTERNALID, ObjectKeys.FIELD_ZONE, ObjectKeys.FIELD_ENTITY_DEFTYPE};

	protected MailManager mailManager;
	/**
	 * Setup by spring
	 * @param mailManager
	 */
	public void setMailManager(MailManager mailManager) {
    	this.mailManager = mailManager;
    }
	protected MailManager getMailManager() {
		return mailManager;
	}
    protected DefinitionModule definitionModule;
	/**
	 * Setup by spring
	 * @param definitionModule
	 */
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
   	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}

	protected WorkspaceModule workspaceModule;
	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}
   	protected WorkspaceModule getWorkspaceModule() {
		return workspaceModule;
	}

	protected FolderModule folderModule;
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
   	protected FolderModule getFolderModule() {
		return folderModule;
	}
 
   	/**
   	 * Use method names as operation so we can keep the logic out of application
	 * and easisly change the required rights
	 */
   	public boolean testAccess(WorkArea workArea, String operation) {
   		try {
   			checkAccess(workArea, operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}
   	}
   	protected void checkAccess(WorkArea workArea, String operation) {
   		if (workArea instanceof TemplateBinder) {
			getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getZone(), WorkAreaOperation.SITE_ADMINISTRATION);
   		} else if (operation.startsWith("setWorkAreaFunctionMembership")) {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		} else if (operation.startsWith("deleteWorkAreaFunctionMembership")) {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		} else if (operation.startsWith("setWorkAreaFunctionMembershipInherited")) {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		} else if (operation.startsWith("setWorkAreaOwner")) {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);        
		} else {
			accessControlManager.checkOperation(workArea, WorkAreaOperation.READ_ENTRIES);
		}
		
	}
   	/**
	 * Use method names as operation so we can keep the logic out of application
	 * and easisly change the required rights
   	 * 
   	 */
  	public boolean testAccess(String operation) {
   		try {
   			checkAccess(operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}
   	}
    protected void checkAccess(String operation) {
   		Binder top = RequestContextHolder.getRequestContext().getZone();
 
        if ("modifyPosting".equals(operation)) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("addPosting")) {
	    	getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("deletePosting")) {
	    	getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
 		} else if (operation.startsWith("setPostingSchedule")) {
	    	getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("addTemplate")) {
	    	getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("deleteTemplate")) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("modifyTemplate")) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("addFunction")) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("modifyFunction")) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("deleteFunction")) {
			getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
		} else if (operation.startsWith("getFunctions") || operation.startsWith("getPostingSchedule")) {
			//anyone can get them 

			return;
		} else {
			accessControlManager.checkOperation(top, WorkAreaOperation.READ_ENTRIES);
		}

		
   	}

    public List getPostings() {
    	//TODO: access check
    	return coreDao.loadPostings(RequestContextHolder.getRequestContext().getZoneId());
    }
    public void modifyPosting(String postingId, Map updates) {
    	checkAccess("modifyPosting");
    	PostingDef post = coreDao.loadPosting(postingId, RequestContextHolder.getRequestContext().getZoneId());
    	ObjectBuilder.updateObject(post, updates);
    }
    public void addPosting(Map updates) {
    	checkAccess("addPosting");
    	PostingDef post = new PostingDef();
    	post.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
       	ObjectBuilder.updateObject(post, updates);
       	post.setEmailAddress(post.getEmailAddress().toLowerCase());
      	coreDao.save(post);   	
    }
    public void deletePosting(String postingId) {
    	checkAccess("deletePosting");
    	PostingDef post = coreDao.loadPosting(postingId, RequestContextHolder.getRequestContext().getZoneId());
    	if (post.getBinder() != null) {
    		post.getBinder().setPosting(null);
    	}
       	coreDao.delete(post);
    }
    public ScheduleInfo getPostingSchedule() {
      	//let anyone get it;
       	return getPostingObject().getScheduleInfo(RequestContextHolder.getRequestContext().getZoneId());
    }
    public void setPostingSchedule(ScheduleInfo config) throws ParseException {
       	checkAccess("setPostingSchedule");
    	getPostingObject().setScheduleInfo(config);
    }	     
    private EmailPosting getPostingObject() {
    	String emailPostingClass = getMailManager().getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailManager.POSTING_JOB);
        try {
            Class processorClass = ReflectHelper.classForName(emailPostingClass);
            EmailPosting job = (EmailPosting)processorClass.newInstance();
            return job;
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException(
                    "Invalid EmailPosting class name '" + emailPostingClass + "'",
                    e);
        } catch (InstantiationException e) {
            throw new ConfigurationException(
                    "Cannot instantiate EmailPosting of type '"
                            + emailPostingClass + "'");
        } catch (IllegalAccessException e) {
            throw new ConfigurationException(
                    "Cannot instantiate EmailPosting of type '"
                            + emailPostingClass + "'");
        }
    }
    //These should be created when the zone is created, but just incase provide minimum backup
	public TemplateBinder addDefaultTemplate(int type) {
	   	//This is called as a side effect to bootstrap
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		List defs = new ArrayList();
		TemplateBinder config = new TemplateBinder();
		Definition entryDef;
		switch (type) {
			case Definition.FOLDER_VIEW: {
				List result = getCoreDao().loadObjects(TemplateBinder.class, 
							new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_FOLDER_CONFIG, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (TemplateBinder)result.get(0);
				config.setTemplateTitle("__configuration_default_folder");
				config.setTemplateDescription("__configuration_default_folder_description");
				config.setInternalId(ObjectKeys.DEFAULT_FOLDER_CONFIG);
				entryDef = getDefinitionModule().addDefaultDefinition(Definition.FOLDER_VIEW);
				config.setEntryDef(entryDef);
				defs.add(entryDef);
				defs.add(getDefinitionModule().addDefaultDefinition(Definition.FOLDER_ENTRY));
				break;
			}
			case Definition.WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(TemplateBinder.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_WORKSPACE_CONFIG, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (TemplateBinder)result.get(0);
				config.setTemplateTitle("__configuration_default_workspace");
				config.setTemplateDescription("__configuration_default_workspace_description");
				config.setInternalId(ObjectKeys.DEFAULT_WORKSPACE_CONFIG);
				entryDef = getDefinitionModule().addDefaultDefinition(type);
				config.setEntryDef(entryDef);
				defs.add(entryDef);
				break;
			}
			case Definition.USER_WORKSPACE_VIEW: {
				List result = getCoreDao().loadObjects(TemplateBinder.class, 
						new FilterControls(defaultDefAttrs, new Object[]{ObjectKeys.DEFAULT_USER_WORKSPACE_CONFIG, zoneId, Integer.valueOf(type)}));
				if (!result.isEmpty()) return (TemplateBinder)result.get(0);
				
				config.setTemplateTitle("__configuration_default_user_workspace");
				config.setTemplateDescription("__configuration_default_user_workspace_description");
				config.setInternalId(ObjectKeys.DEFAULT_USER_WORKSPACE_CONFIG);
				entryDef = getDefinitionModule().addDefaultDefinition(type);
				config.setEntryDef(entryDef);
				defs.add(entryDef);
				break;
			}
		default: {
			throw new InvalidArgumentException("Invalid type:" + type);
			}
		}
		
		Map updates = new HashMap();
		config.setTitle(config.getTemplateTitle());
		config = doAddTemplate(config, type, updates);
		config.setDefinitionsInherited(false);
		config.setDefinitions(defs);
		
		if (type == Definition.USER_WORKSPACE_VIEW) {
			//get default folder definition
			HistoryStamp stamp = new HistoryStamp(RequestContextHolder.getRequestContext().getUser());
			TemplateBinder def = addDefaultTemplate(Definition.FOLDER_VIEW);
			TemplateBinder newDef = new TemplateBinder(def);
			copyBinderAttributes(def, newDef);
			newDef.setCreation(stamp);
			newDef.setModification(stamp);
			newDef.setFunctionMembershipInherited(true);
			newDef.setPathName(config.getPathName() + "/" + newDef.getTitle());
			config.addBinder(newDef);
			
		}
		return config;
	 }
	protected Binder copyBinderAttributes(Binder source, Binder destination) {
		//need real id for linkage
		if (destination.getId() == null) getCoreDao().save(destination);
		EntityDashboard dashboard = getCoreDao().loadEntityDashboard(source.getEntityIdentifier());
		if (dashboard != null) {
			EntityDashboard myDashboard = new EntityDashboard(dashboard);
			myDashboard.setOwnerIdentifier(destination.getEntityIdentifier());
			getCoreDao().save(myDashboard);
		  }
		//TODO: copy all attachments
		Set<Attachment> atts = source.getAttachments();
		if (atts != null) {
			for (Attachment at:atts) {
				if (at instanceof FileAttachment);
			}
		}
		Map catts = source.getCustomAttributes();
		for (Iterator iter=catts.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			CustomAttribute ca = (CustomAttribute)me.getValue();
			switch (ca.getValueType()) {
				case CustomAttribute.EVENT: {
					Event event = (Event)ca.getValue();
					if (event != null) {
						Event newE = (Event)event.clone();
						newE.setId(null);
						getCoreDao().save(newE);
						destination.addCustomAttribute(newE.getName(), newE);
					}
					break;
				}
				case CustomAttribute.ATTACHMENT: {
					break;
				}
				case CustomAttribute.SET: {
					//should be the same type
					Set values = ca.getValueSet();
					if (!values.isEmpty()) {
						Set newV = new HashSet();
						for (Iterator it=values.iterator(); it.hasNext();) {
							Object val = iter.next();
							if (val == null) continue;
							if (val instanceof Event) {
								Event newE = (Event)((Event)val).clone();
								newE.setId(null);
								getCoreDao().save(newE);
								newV.add(newE);
							} else if (val instanceof Attachment) {
								
							} else {
								newV.add(val);
							}
						}
						destination.addCustomAttribute(ca.getName(), newV);
					}
					break;
				} 
				default: {
					destination.addCustomAttribute(ca.getName(), ca.getValue());
					break;
				}
			}
			
		}
		return destination;
		
		
	}
	//add top level template
	 public Long addTemplate(int type, Map updates) {
	    checkAccess("addTemplate");
		TemplateBinder template = new TemplateBinder();
		Definition entryDef = getDefinitionModule().addDefaultDefinition(type);
		template.setEntryDef(entryDef);
		List definitions = new ArrayList();
		definitions.add(entryDef);
		template.setDefinitionsInherited(false);
		template.setDefinitions(definitions);
		template.setFunctionMembershipInherited(true);
		doAddTemplate(template, type, updates);
	    return template.getId();
	 }
	 public Long addTemplate(Element config) {
		 checkAccess("addTemplate");
		 TemplateBinder template = new TemplateBinder();
		 template.setDefinitionsInherited(false);
		 template.setFunctionMembershipInherited(true);
		 template.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		 Integer type = Integer.valueOf(config.attributeValue("type"));
		 template.setDefinitionType(type);
		 template.setCreation(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
		 template.setModification(template.getCreation());
		 template.setName(getPropertyValue(config, "name"));
		 template.setInternalId(getPropertyValue(config, "internalId"));
		 template.setTemplateDescription(getPropertyValue(config, "templateDescription"));
		 template.setTemplateTitle(getPropertyValue(config, "templateTitle"));
		 template.setTitle(getPropertyValue(config, "title"));
		 if (Validator.isNull(template.getTitle())) template.setTitle(template.getTemplateTitle());
		 template.setDescription(getPropertyValue(config, "description"));
		 String bVal = getPropertyValue(config, "library");
		 if (!Validator.isNull(bVal)) {
			 template.setLibrary(GetterUtil.get(bVal, false));
		 }
		 bVal = getPropertyValue(config, "uniqueTitles");
		 if (!Validator.isNull(bVal)) {
			 template.setUniqueTitles(GetterUtil.get(bVal, false));
		 }	
		 template.setPathName("/" + template.getTitle());
		 
		 String entryDefName = getPropertyValue(config, "entryDef");
		 Definition def = null;
		 if (Validator.isNotNull(entryDefName)) {
			 FilterControls fc = new FilterControls();
			 fc.add(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId());
			 fc.add("name", entryDefName);
			 fc.add("type", type);
			 List results = getCoreDao().loadObjects(Definition.class, fc);
			 if (!results.isEmpty()) {
				 def = (Definition)results.get(0);
			 }			 
		 }
		 if (def != null) template.setEntryDef(def);
		 else template.setEntryDef(getDefinitionModule().addDefaultDefinition(type));
		 List definitions = new ArrayList();
		 Map workflows = new HashMap();
		 //get default definitions for this template
		 List nodes = config.selectNodes("./definition");
		 if (nodes.isEmpty()) {
			 template.setDefinitionsInherited(true);
		 } else {
			 for (int i=0; i<nodes.size(); ++i) {
				 Element element = (Element)nodes.get(i);
				 String name = element.attributeValue("name");
				 if (Validator.isNull(name)) continue;
				 FilterControls fc = new FilterControls();
				 fc.add(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId());
				 fc.add("name", name);
				 List<Definition> results = getCoreDao().loadObjects(Definition.class, fc);
				 if (results.isEmpty()) continue;
				 Definition cDef = results.get(0);
				 definitions.add(cDef);
				 name=element.attributeValue("workflow");
				 if (Validator.isNotNull(name)) {
					 //lookup workflow
					 fc = new FilterControls();
					 fc.add(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId());
					 fc.add("name", name);
					 results = getCoreDao().loadObjects(Definition.class, fc);
					 if (!results.isEmpty()) {
						 workflows.put(cDef.getId(), results.get(0));
					 }
					 
				 }
			 }
			 template.setDefinitionsInherited(false);
			 template.setDefinitions(definitions);
			 template.setWorkflowAssociations(workflows);
		 }
		 
		 getCoreDao().save(template);
		 template.setupRoot();
		 //see if any child configs need to be copied
		 nodes = config.selectNodes("./property[@name='template']");
		 for (int i=0; i<nodes.size(); ++i) {
			 Element element = (Element)nodes.get(i);
			 String name = element.getStringValue();
			 if (Validator.isNull(name)) continue;
			 FilterControls fc = new FilterControls();
			 fc.add(ObjectKeys.FIELD_ZONE, RequestContextHolder.getRequestContext().getZoneId());
			 fc.add(ObjectKeys.FIELD_BINDER_NAME, name);
			 List results = getCoreDao().loadObjects(TemplateBinder.class, fc);
			 if (results.isEmpty()) continue;
			 TemplateBinder child = (TemplateBinder)results.get(0);
			 addTemplate(template, child);
		 }
		 //	need to write this out so binderKey updated incases where this is inside another transaction
		 //sqlserver complains of uniquekey constraint violations when another template is added before this one is flushed.
		 getCoreDao().flush();	
		 return template.getId();
	 }
	 private String getPropertyValue(Element element, String name) {
		 Element variableEle = (Element)element.selectSingleNode("./property[@name='" + name + "']");
		 if (variableEle == null) return null;
		 return variableEle.getStringValue();   	
	 }
	 protected TemplateBinder doAddTemplate(TemplateBinder template, int type, Map updates) {
		 template.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		 template.setDefinitionType(type);
		 template.setCreation(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
		 template.setModification(template.getCreation());
		 ObjectBuilder.updateObject(template, updates);
		 if (Validator.isNull(template.getTitle())) template.setTitle(template.getTemplateTitle());
		 template.setPathName("/" + template.getTitle());
		 getCoreDao().save(template);
		 template.setupRoot();
		 return template;
	 }

	 //clone a top level template as a child of another template
	 public Long addTemplate(Long parentId, Long srcConfigId) {
	    checkAccess("addTemplate");
	    TemplateBinder parentConfig = (TemplateBinder)getCoreDao().loadBinder(parentId, RequestContextHolder.getRequestContext().getZoneId());
	    TemplateBinder srcConfig = (TemplateBinder)getCoreDao().loadBinder(srcConfigId, RequestContextHolder.getRequestContext().getZoneId());
	    return addTemplate(parentConfig, srcConfig);
	 }
	 protected Long addTemplate(TemplateBinder parentConfig, TemplateBinder srcConfig) {
		 TemplateBinder config = new TemplateBinder(srcConfig);
		 config.setCreation(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
		 config.setModification(config.getCreation());
		 if (Validator.isNull(config.getTitle())) config.setTitle(config.getTemplateTitle());
		 config.setPathName(parentConfig.getPathName() + "/" + config.getTitle());
		 //by default, inherit from parent
     	 copyBinderAttributes(srcConfig, config);
      	 getCoreDao().updateFileName(parentConfig, config, null, config.getTitle());

		 //get childen before adding new children incase parent and source are the same
		 List<TemplateBinder> children = new ArrayList(srcConfig.getBinders());
		 parentConfig.addBinder(config);
	     if (!config.isFunctionMembershipInherited()) {
	    	 //copy to new template
	    	 List<WorkAreaFunctionMembership> wfms = getWorkAreaFunctionMemberships(srcConfig);
	    	 for (WorkAreaFunctionMembership fm: wfms) {
	    		WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
		       	membership.setZoneId(fm.getZoneId());
		       	membership.setWorkAreaId(config.getWorkAreaId());
		       	membership.setWorkAreaType(config.getWorkAreaType());
		       	membership.setFunctionId(fm.getFunctionId());
		       	membership.setMemberIds(new HashSet(fm.getMemberIds()));
		        getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
	    	}	    
	     }

		 for (TemplateBinder c:children) {
			 addTemplate(config, c);
		 }
		 return config.getId();
	 }
	   public Long addTemplateFromBinder(Long binderId) throws AccessControlException, WriteFilesException {
		   checkAccess("addTemplate");
		   Long zoneId =  RequestContextHolder.getRequestContext().getZoneId();
		   Binder binder = (Binder)getCoreDao().loadBinder(binderId, zoneId);
		   TemplateBinder config = templateFromBinder(null, binder);
		   return config.getId();
		}
		protected TemplateBinder templateFromBinder(TemplateBinder parent, Binder binder) {
			//get binder setup
			if (binder.getDefinitionType() == null) {
				getDefinitionModule().setDefaultBinderDefinition(binder);
			}	
			TemplateBinder config = new TemplateBinder(binder);
			config.setCreation(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
			config.setModification(config.getCreation());
			if (parent == null) {
				config.setPathName("/" + config.getTitle());				
			} else {
				config.setPathName(parent.getPathName() + "/" + config.getTitle());				
			}
			copyBinderAttributes(binder, config);
	      	if (parent != null) {
				parent.addBinder(config);
	      		getCoreDao().updateFileName(parent, config, null, config.getTitle());
	      	} else {
	      		config.setupRoot();
	      	}
			if (!config.isFunctionMembershipInherited()) {
				//copy binders memberships to new Template
				List<WorkAreaFunctionMembership> wfms = getWorkAreaFunctionMemberships(binder);
				for (WorkAreaFunctionMembership fm: wfms) {
					WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
					membership.setZoneId(fm.getZoneId());
					membership.setWorkAreaId(config.getWorkAreaId());
					membership.setWorkAreaType(config.getWorkAreaType());
					membership.setFunctionId(fm.getFunctionId());
					membership.setMemberIds(new HashSet(fm.getMemberIds()));
					getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);	
				}	    		
			}
			List<Binder> children = binder.getBinders();    
			for (Binder child: children) {
				templateFromBinder(config, child);	    	
			}
			return config;
			
	}
	public void modifyTemplate(Long id, Map updates) {
		checkAccess("modifyTemplate");
		TemplateBinder config = (TemplateBinder)getCoreDao().loadBinder(id, RequestContextHolder.getRequestContext().getZoneId());
		ObjectBuilder.updateObject(config, updates);
	}
	public TemplateBinder getTemplate(Long id) {
		//TODO: is there access
		return (TemplateBinder)getCoreDao().loadBinder(id, RequestContextHolder.getRequestContext().getZoneId());
	}
	public List getTemplates() {
		//TODO: is there access
		return getCoreDao().loadConfigurations(RequestContextHolder.getRequestContext().getZoneId());
	}
	public List getTemplates(int type) {
		//TODO: is there access
		return getCoreDao().loadConfigurations( RequestContextHolder.getRequestContext().getZoneId(), type);
	}
	
	public Long addBinderFromTemplate(Long configId, Long parentBinderId, String title, String name) throws AccessControlException, WriteFilesException {
	    //after children are added, resolve relative selections
		Binder binder = addBinderFromTemplateInternal(configId, parentBinderId, title, name);
		//flush changes so we can use them to fix up dashboards
		IndexSynchronizationManager.applyChanges();
		List<Binder>binders = new ArrayList();
		binders.add(binder);
		while (!binders.isEmpty()) {
			Binder b = binders.remove(0);
			binders.addAll(b.getBinders());
			EntityDashboard dashboard = getCoreDao().loadEntityDashboard(b.getEntityIdentifier());
			if (dashboard != null) {
			//	this should be moved
				DashboardHelper.resolveRelativeBinders(b, dashboard);
			}
		}
		return binder.getId();

	}
	protected Binder addBinderFromTemplateInternal(Long configId, Long parentBinderId, String title, String name) throws AccessControlException, WriteFilesException {
    	//modules do the access checking
	   Long zoneId =  RequestContextHolder.getRequestContext().getZoneId();
	   TemplateBinder cfg = (TemplateBinder)getCoreDao().loadBinder(configId, zoneId);
	   Binder parentBinder = (Binder)getCoreDao().loadBinder(parentBinderId, zoneId);
	   Binder binder;
	   Definition def = cfg.getDefaultViewDef();
	   if (def == null) {
		   def = getDefinitionModule().addDefaultDefinition(cfg.getDefinitionType());
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
		//if not null, use icon from template.  Otherwise try icon from definition when binder is created.
		if (Validator.isNotNull(cfg.getIconName())) entryData.put(ObjectKeys.FIELD_ENTITY_ICONNAME, cfg.getIconName());
		if (Validator.isNotNull(name)) entryData.put(ObjectKeys.FIELD_BINDER_NAME, name);
		//get binder created
	   if (cfg.getDefinitionType() == Definition.WORKSPACE_VIEW) {
   			binder = getCoreDao().loadBinder(getWorkspaceModule().addWorkspace(parentBinderId, def.getId(), inputData, fileItems), zoneId);
	   } else if (cfg.getDefinitionType() == Definition.USER_WORKSPACE_VIEW) {
  			binder = getCoreDao().loadBinder(getWorkspaceModule().addWorkspace(parentBinderId, def.getId(), inputData, fileItems), zoneId);
	   } else {
		   if (parentBinder instanceof Workspace)
			   binder = getCoreDao().loadBinder(getWorkspaceModule().addFolder(parentBinderId, def.getId(), inputData, fileItems), zoneId);
		   else
			   binder = getCoreDao().loadBinder(getFolderModule().addFolder(parentBinderId, def.getId(), inputData, fileItems), zoneId);
	   }

	   copyBinderAttributes(cfg, binder);
	   if (!cfg.isDefinitionsInherited()) {
	    	binder.setDefinitionsInherited(false);
	    	binder.setDefinitions(cfg.getDefinitions());
	    	binder.setWorkflowAssociations(cfg.getWorkflowAssociations());
    	}
	    if (!cfg.isFunctionMembershipInherited()) {
	    	binder.setFunctionMembershipInherited(false);
	    	List<WorkAreaFunctionMembership> wfms = getWorkAreaFunctionMemberships(cfg);
	    	for (WorkAreaFunctionMembership fm: wfms) {
	    		WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
		       	membership.setZoneId(fm.getZoneId());
		       	membership.setWorkAreaId(binder.getWorkAreaId());
		       	membership.setWorkAreaType(binder.getWorkAreaType());
		       	membership.setFunctionId(fm.getFunctionId());
		       	membership.setMemberIds(new HashSet(fm.getMemberIds()));
		        getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
	    		
	    	}	    	
	    	getCoreDao().flush();
	    	//need to reindex acls
    		indexMembership(binder, true);
	    } else {
	    	//	do child configs
	    	//	first flush updates, addBinder does a refresh which overwrites changes
	    	getCoreDao().flush();
	    }
	    List<TemplateBinder> children = cfg.getBinders();    
	    for (TemplateBinder child: children) {
	    	addBinderFromTemplateInternal(child.getId(), binder.getId(), NLT.getDef(child.getTitle()), null);	    	
	    }
	    return binder;
	}
	
 
	public void addFunction(String name, Set operations) {
		checkAccess("addFunction");
		Function function = new Function();
		function.setName(name);
		function.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		function.setOperations(operations);
		
		List zoneFunctions = functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
		if (zoneFunctions.contains(function)) {
			//Role already exists
			throw new FunctionExistsException(function.getName());
		}
		functionManager.addFunction(function);
	 
    }
    public void modifyFunction(Long id, Map updates) {
		checkAccess("modifyFunction");
		Function function = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), id);
		ObjectBuilder.updateObject(function, updates);
		functionManager.updateFunction(function);			
    }
    public void deleteFunction(Long id) {
		checkAccess("deleteFunction");
		Function f = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), id);
		functionManager.deleteFunction(f);
    }
    public List getFunctions() {
		//let anyone read them			
        return  functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
    }
    public void setWorkAreaFunctionMemberships(WorkArea workArea, Map functionMemberships) {
		List binders = new ArrayList();
		
		checkAccess(workArea, "setWorkAreaFunctionMembership");
		
		Iterator itFunctions = functionMemberships.entrySet().iterator();
		while (itFunctions.hasNext()) {
			Map.Entry fm = (Map.Entry)itFunctions.next();
			setWorkAreaFunctionMembership(workArea, (Long)fm.getKey(), (Set)fm.getValue());
		}
		//flush so next query works
		getCoreDao().flush();
		if (functionMemberships.size() > 0) indexMembership(workArea, true);
		
	}

	private void indexMembership(WorkArea workArea, boolean cascade) {
		ArrayList<Query> updateQueries = new ArrayList();
		ArrayList<String> updateIds = new ArrayList();
		if (!(workArea instanceof Binder)) return;
		// Now, create a query which can be used by the index update method to modify all the
		// entries, replies, attachments, and binders(workspaces) in the index with this new 
		// Acl list.
		Binder binder = (Binder)workArea;
		List<Binder> binders = new ArrayList();
		if (cascade) {
			binders = getInheritingDescendentBinderIds(binder, binders);
		} else {
			binders.add(binder);
		}
		//find descendent binders with the same owner, and re-index together
		//the owner may be part of the acl set, so cannot always share
		while (!binders.isEmpty()) {
			Binder top = binders.get(0);
			binders.remove(0);
			Set readAclIds =  AccessUtils.getReadAccessIds(top);
			//collect ids of binders with the same owner
			StringBuffer aIds = new StringBuffer();
			if (!readAclIds.isEmpty()) {
				for(Iterator i = readAclIds.iterator(); i.hasNext();) {
					aIds.append(i.next()).append(" ");
				}
			} else {
				aIds.append(BasicIndexUtils.READ_ACL_ALL);
			}
			List ids = new ArrayList();
			ids.add(top.getId());
			List<Binder> others = new ArrayList(binders);
			for (Binder b:others) {
				//have same owner, have same acl
				if (b.getOwnerId() != null && b.getOwnerId().equals(top.getOwnerId())) {
					binders.remove(b);
					ids.add(b.getId());
				}
			}
			Query q = buildQueryforUpdate(ids);
			// add this query and list of ids to the lists we'll pass to updateDocs.
			updateQueries.add(q);
			updateIds.add(aIds.toString());
		}
		if (updateQueries.size() > 0) {
		LuceneSession luceneSession = getLuceneSessionFactory().openSession();
			try {
				
				luceneSession.updateDocuments(updateQueries, BasicIndexUtils.FOLDER_ACL_FIELD,
						updateIds);
			} finally {
				luceneSession.close();
			}
		}
		
	}

	private void setWorkAreaFunctionMembership(WorkArea workArea, Long functionId, Set memberIds) {
     	Workspace zone = RequestContextHolder.getRequestContext().getZone();
      	WorkAreaFunctionMembership membership =
      		getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership(zone.getId(), workArea, functionId);
		if (membership == null) { 
	       	membership = new WorkAreaFunctionMembership();
	       	membership.setZoneId(zone.getId());
	       	membership.setWorkAreaId(workArea.getWorkAreaId());
	       	membership.setWorkAreaType(workArea.getWorkAreaType());
	       	membership.setFunctionId(functionId);
	       	membership.setMemberIds(memberIds);
	        getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
	        processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
		} else {
			Set mems = membership.getMemberIds();
			mems.clear();
			mems.addAll(memberIds);
			membership.setMemberIds(mems);
			getWorkAreaFunctionMembershipManager().updateWorkAreaFunctionMembership(membership);
    	    processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
		}
	}
	
	// a recursive routine which walks down the tree
	// from here and builds a list of the binders
	// who inherit acls from their parents.  The tree is pruned at
	// the highest branch that does not inherit from it's parent.
	private List getInheritingDescendentBinderIds(Binder binder, List binders) {
  		binders.add(binder);
		List childBinder = binder.getBinders();
 		for (int i=0; i<childBinder.size(); ++i) {
			Binder c = (Binder)childBinder.get(i);
			if (c.isFunctionMembershipInherited()) {
				binders = getInheritingDescendentBinderIds(c, binders);
			}
    	}
    	return binders;
	}
	
	
	private Query buildQueryforUpdate(List folderIds) {
		Document qTree = DocumentHelper.createDocument();
		Element qTreeRootElement = qTree.addElement(QueryBuilder.QUERY_ELEMENT);
		Element qTreeOrElement = qTreeRootElement.addElement(QueryBuilder.OR_ELEMENT);
    	Element qTreeAndElement = qTreeOrElement.addElement(QueryBuilder.AND_ELEMENT);
    	Element idsOrElement = qTreeAndElement.addElement((QueryBuilder.OR_ELEMENT));
    	//get all the entrys, replies and attachments
    	// Folderid's and doctypes:{entry, binder, attachment}
    	for (Iterator iter = folderIds.iterator(); iter.hasNext();) {
    		Element field = idsOrElement.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.BINDER_ID_FIELD);
			Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
			child.setText(iter.next().toString());
    	}
    	String[] types = new String[] {"entry","binder","attachment"};
    	
    	Element typeOrElement = qTreeAndElement.addElement((QueryBuilder.OR_ELEMENT));
    	for (int i =0; i < types.length; i++) {
    		Element field = typeOrElement.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
			Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
			child.setText(types[i]);
    	}
    	// Get all the binder's themselves
    	// OR Doctype=binder and binder id's
    	Element andElement = qTreeOrElement.addElement((QueryBuilder.AND_ELEMENT));
    	Element orOrElement = andElement.addElement((QueryBuilder.OR_ELEMENT));
    	Element field = andElement.addElement(QueryBuilder.FIELD_ELEMENT);
		field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,BasicIndexUtils.DOC_TYPE_FIELD);
		Element child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
		child.setText("binder");
	   	for (Iterator iter = folderIds.iterator(); iter.hasNext();) {
    		field = orOrElement.addElement(QueryBuilder.FIELD_ELEMENT);
			field.addAttribute(QueryBuilder.FIELD_NAME_ATTRIBUTE,EntityIndexUtils.DOCID_FIELD);
			child = field.addElement(QueryBuilder.FIELD_TERMS_ELEMENT);
			child.setText(iter.next().toString());
    	}
    	
    	QueryBuilder qb = new QueryBuilder(getProfileDao().getPrincipalIds(RequestContextHolder.getRequestContext().getUser()));
    	SearchObject so = qb.buildQuery(qTree);

		return so.getQuery();
	}
	
    public void deleteWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
		checkAccess(workArea, "deleteWorkAreaFunctionMembership");

        WorkAreaFunctionMembership wfm = getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership
   				(RequestContextHolder.getRequestContext().getZoneId(), workArea, functionId);
        if (wfm != null) {
	        getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);
	        indexMembership(workArea, true);
	        processAccessChangeLog(workArea, ChangeLog.ACCESSDELETE);

        }
    }
    public void setWorkAreaOwner(WorkArea workArea, Long userId) {
    	checkAccess(workArea, "setWorkAreaOwner");
    	if (!workArea.getOwnerId().equals(userId)) {
    		User user = getProfileDao().loadUser(userId, RequestContextHolder.getRequestContext().getZoneId());
    		workArea.setOwner(user);
    		//need to update access, since owner has changed
   			indexMembership(workArea, false);
    	}
   		
    }
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
		// open to anyone - only way to get parentMemberships
    	// checkAccess(workArea, "getWorkAreaFunctionMembership");

        return getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership
       		(RequestContextHolder.getRequestContext().getZoneId(), workArea, functionId);
    }
    
	public List getWorkAreaFunctionMemberships(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		//checkAccess(workArea, "getWorkAreaFunctionMemberships");

        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(
        		RequestContextHolder.getRequestContext().getZoneId(), workArea);
	}

	public List getWorkAreaFunctionMembershipsInherited(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		// checkAccess(workArea, "getWorkAreaFunctionMembershipsInherited");
	    WorkArea source = workArea;
	    if (!workArea.isFunctionMembershipInherited()) return new ArrayList();
	    while (source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    	//template binders have this problem, cause they are not connected to a 
	    	//root until instanciated, but want to inherit from a future parent
	    	if (source == null) return new ArrayList();
	    }
 
        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), source);
	}

	//Routine to return the workarea that access control is being inherited from
	public WorkArea getWorkAreaFunctionInheritance(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		// checkAccess(workArea, "getWorkAreaFunctionMembershipsInherited");
	    WorkArea source = workArea;
	    if (!workArea.isFunctionMembershipInherited()) return source;
	    while (source != null && source.isFunctionMembershipInherited()) {
	    	source = source.getParentWorkArea();
	    }
        return source;
	}

	public void setWorkAreaFunctionMembershipInherited(WorkArea workArea, boolean inherit) 
    throws AccessControlException {
    	checkAccess(workArea, "setWorkAreaFunctionMembershipInherited");
    	if (inherit) {
    		//remove them
    	   	List current = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(RequestContextHolder.getRequestContext().getZoneId(), workArea);
    	   	for (int i=0; i<current.size(); ++i) {
    	        WorkAreaFunctionMembership wfm = (WorkAreaFunctionMembership)current.get(i);
   		        getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);
    	   	}
    		
    	} else if (workArea.isFunctionMembershipInherited() && !inherit) {
    		//copy parent values as beginning values
    		List current = getWorkAreaFunctionMembershipsInherited(workArea);
           	for (int i=0; i<current.size(); ++i) {
    			WorkAreaFunctionMembership wf = (WorkAreaFunctionMembership)current.get(i);
    			WorkAreaFunctionMembership membership = new WorkAreaFunctionMembership();
    			membership.setZoneId(wf.getZoneId());
    			membership.setWorkAreaId(workArea.getWorkAreaId());
    			membership.setWorkAreaType(workArea.getWorkAreaType());
    			membership.setFunctionId(wf.getFunctionId());
    			membership.setMemberIds(new HashSet(wf.getMemberIds()));    		
    	        getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
     		}
    	}
    	//see if there is a real change
    	if (workArea.isFunctionMembershipInherited()  != inherit) {
    		workArea.setFunctionMembershipInherited(inherit);
    		//just changed from not inheritting to inherit = need to update index
    		//if changed from inherit to not, index remains the same
    		if (inherit) indexMembership(workArea, true);

    		processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
    	}
    } 
	private void processAccessChangeLog(WorkArea workArea, String operation) {
        if ((workArea instanceof Binder) && !(workArea instanceof TemplateBinder)) {
        	Binder binder = (Binder)workArea;
        	User user = RequestContextHolder.getRequestContext().getUser();
        	binder.incrLogVersion();
        	binder.setModification(new HistoryStamp(user));
        	BinderProcessor processor = (BinderProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
        	processor.processChangeLog(binder, operation);
       	}
		
	}
	//return binders this user is a team_member of
	public List getTeamMemberships(Long userId) {
		//team membership is implemented by a reserved role
        User current = RequestContextHolder.getRequestContext().getUser();
        
        User user;
        if ((userId == null) || current.getId().equals(userId)) user = current;
        else user = getProfileDao().loadUser(userId, current.getZoneId());
		List<WorkAreaFunctionMembership> wfm = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(
				RequestContextHolder.getRequestContext().getZoneId(), WorkAreaOperation.TEAM_MEMBER, getProfileDao().getPrincipalIds(user));
	    Set ids = new HashSet();
	    for (WorkAreaFunctionMembership w: wfm) {
	    	ids.add(w.getWorkAreaId());
	    }
	    List wa = getCoreDao().loadObjects(ids, Binder.class, user.getZoneId());
	    
	    //can certainly see own memberships
	    Set result = new TreeSet(new BinderComparator(current.getLocale(),BinderComparator.SortByField.searchTitle));
	    if (user == current) result.addAll(wa);
	    else {
	    	for (int i=0; i<wa.size(); ++i) {
	    		Binder b = (Binder) wa.get(i);
	    		try {
	    			//TODO: is this what we want??
	    			getAccessControlManager().checkOperation(user, b, WorkAreaOperation.READ_ENTRIES);
	    			result.add(b);
	    		} catch (Exception ex) {};
	    	}
	    }
	    wa.clear();
	    wa.addAll(result);
	    return wa;
	    
    }
    public Map sendMail(Set ids, Set emailAddresses, String subject, Description body, List entries, boolean sendAttachments) throws Exception {
    	User user = RequestContextHolder.getRequestContext().getUser();
		Set userIds = getProfileDao().explodeGroups(ids, user.getZoneId());
		//TODO is there accesschecking on sending email address
		List users = getCoreDao().loadObjects(userIds, User.class, user.getZoneId());
		Set emailSet = new HashSet();
		List distribution = new ArrayList();
		List errors = new ArrayList();
		Map result = new HashMap();
		result.put(ObjectKeys.SENDMAIL_ERRORS, errors);
		result.put(ObjectKeys.SENDMAIL_DISTRIBUTION, distribution);
		//add email address listed 
		Object[] errorParams = new Object[3];
		for (Iterator iter=emailAddresses.iterator(); iter.hasNext();) {
			String e = (String)iter.next();
			if (!Validator.isNull(e)) {
				try {
					emailSet.add(new InternetAddress(e.trim()));
					distribution.add(e);
				} catch (Exception ex) {
					errorParams[0] = "";
					errorParams[1] = e;
					errorParams[2] = ex.getLocalizedMessage();
					errors.add(NLT.get("errorcode.badToAddress", errorParams));							
				}
			}
		}
		for (Iterator iter=users.iterator(); iter.hasNext();) {
			User e = (User)iter.next();
			try {
				emailSet.add(new InternetAddress(e.getEmailAddress().trim()));
				distribution.add(e.getEmailAddress());
			} catch (Exception ex) {
				errorParams[0] = e.getTitle();
				errorParams[1] = e.getEmailAddress();
				errorParams[2] = ex.getLocalizedMessage();
				errors.add(NLT.get("errorcode.badToAddress", errorParams));							
			}
		}
		if (emailSet.isEmpty()) {
			//no-one to send tos
			errors.add(0, NLT.get("errorcode.noRecipients", errorParams));
			result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_FAILED);
			return result;			
		}
    	Map message = new HashMap();
       	try {
    		message.put(SendEmail.FROM, new InternetAddress(user.getEmailAddress()));
    	} catch (Exception ex) {
			errorParams[0] = user.getTitle();
			errorParams[1] = user.getEmailAddress();
			errorParams[2] = ex.getLocalizedMessage();
			errors.add(0, NLT.get("errorcode.badFromAddress", errorParams));
			result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_FAILED);
			//cannot send without valid from address
			return result;
    	}
    	if (body.getFormat() == Description.FORMAT_HTML)
    		message.put(SendEmail.HTML_MSG, body.getText());
    	else 
       		message.put(SendEmail.TEXT_MSG, body.getText());
    		
    	message.put(SendEmail.SUBJECT, subject);
 		message.put(SendEmail.TO, emailSet);
 		if (entries != null) {

			if (sendAttachments) {
	 			List attachments = new ArrayList();
	 			
	 			Iterator entriesIt = entries.iterator();
	 			while (entriesIt.hasNext()) {
	 				DefinableEntity entry = (DefinableEntity)entriesIt.next();
					attachments.addAll(entry.getFileAttachments());
	 			}
				message.put(SendEmail.ATTACHMENTS, attachments);
			}
 			
 			
 			List iCalendars = new ArrayList();
 			Iterator entriesIt = entries.iterator();
 			while (entriesIt.hasNext()) {
 				DefinableEntity entry = (DefinableEntity)entriesIt.next();
 				if (entry.getEvents() != null && !entry.getEvents().isEmpty()) {
 					iCalendars.add(getIcalGenerator().getICalendarForEntryEvents(entry));
 				}
 			}
 			if (!iCalendars.isEmpty()) {
 				message.put(SendEmail.ICALENDARS, iCalendars);
 			}
 		}
 				
		boolean sent = getMailManager().sendMail(RequestContextHolder.getRequestContext().getZone(), message, user.getTitle() + " email");
		if (sent) result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_SENT);
		else result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_SCHEDULED);
		return result;
    }


   public List getChanges(Long binderId, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("binderId", binderId);
	   if (!Validator.isNull(operation)) {
		   filter.add("operation", operation);
	   }
	   OrderBy order = new OrderBy();
	   order.addColumn("entityId");
	   order.addColumn("operationDate");	   
	   filter.setOrderBy(order);
	   
	   return filterChangeLogs(getCoreDao().loadObjects(ChangeLog.class, filter));
	   //need to filter for access
   }
   public List getChanges(Long entityId, String entityType, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("entityId", entityId);
	   filter.add("entityType", entityType);
	   if (!Validator.isNull(operation)) {
		   filter.add("operation", operation);
	   }
	   OrderBy order = new OrderBy();
	   order.addColumn("operationDate");	   
	   filter.setOrderBy(order);
	   
	   return filterChangeLogs(getCoreDao().loadObjects(ChangeLog.class, filter)); 
   	
   }
   private List filterChangeLogs(List<ChangeLog> changeLogs) {
	   User user = RequestContextHolder.getRequestContext().getUser();
	   if (user.isSuper()) return changeLogs;
	   // get the current users acl set
	   Set<Long> userAclSet = getProfileDao().getPrincipalIds(user);
	   Set userStringIds = new HashSet();
	   for (Long id:userAclSet) {
		   userStringIds.add(id.toString());
	   }
	   List result = new ArrayList();
	   for (ChangeLog log: changeLogs) {
		   Document doc = log.getDocument();
		   if (doc == null) continue;
		   Element root = doc.getRootElement();
		   if (root == null) continue;
		   if (AccessUtils.checkAccess(root, userStringIds)) result.add(log);
	   }
	   return result;

   }

}
