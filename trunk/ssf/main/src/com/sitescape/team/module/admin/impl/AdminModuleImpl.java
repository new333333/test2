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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.LinkedHashSet;

import javax.mail.internet.InternetAddress;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.NoObjectByTheIdException;
import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.OrderBy;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Dashboard;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityDashboard;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoBinderByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.Statistics;
import com.sitescape.team.domain.TemplateBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.jobs.EmailPosting;
import com.sitescape.team.jobs.ScheduleInfo;
import com.sitescape.team.jobs.SendEmail;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.binder.AccessUtils;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.binder.BinderProcessor;
import com.sitescape.team.module.dashboard.DashboardModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.shared.EntryBuilder;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.shared.ObjectBuilder;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.search.IndexSynchronizationManager;
import com.sitescape.team.security.AccessControlException;
import com.sitescape.team.security.function.Function;
import com.sitescape.team.security.function.FunctionExistsException;
import com.sitescape.team.security.function.WorkArea;
import com.sitescape.team.security.function.WorkAreaFunctionMembership;
import com.sitescape.team.security.function.WorkAreaOperation;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.web.util.DashboardHelper;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Validator;

/**
 * @author Janet McCann
 *
 */
public class AdminModuleImpl extends CommonDependencyInjection implements AdminModule {
	private static final String[] defaultDefAttrs = new String[]{ObjectKeys.FIELD_INTERNALID, ObjectKeys.FIELD_ZONE, ObjectKeys.FIELD_ENTITY_DEFTYPE};

	protected MailModule mailModule;
	/**
	 * Setup by spring
	 * @param mailModule
	 */
	public void setMailModule(MailModule mailModule) {
    	this.mailModule = mailModule;
    }
	protected MailModule getMailModule() {
		return mailModule;
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
 
	protected BinderModule binderModule;
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
   	protected BinderModule getBinderModule() {
		return binderModule;
	}
	protected DashboardModule dashboardModule;
	public void setDashboardModule(DashboardModule dashboardModule) {
		this.dashboardModule = dashboardModule;
	}
   	protected DashboardModule getDashboardModule() {
		return dashboardModule;
	}
   	protected FileModule fileModule;
   	public void setFileModule(FileModule fileModule) {
   		this.fileModule = fileModule;
   	}
   	protected FileModule getFileModule() {
   		return fileModule;
   	}	
	private IcalModule icalModule;
	public IcalModule getIcalModule() {
		return icalModule;
	}
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}	
	private TransactionTemplate transactionTemplate;
    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

   	/**
   	 * Use operation so we can keep the logic out of application
	 * and easisly change the required rights
	 */
   	public boolean testAccess(WorkArea workArea, AdminOperation operation) {
   		try {
   			checkAccess(workArea, operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}
   	}
   	public void checkAccess(WorkArea workArea, AdminOperation operation) {
   		if (workArea instanceof TemplateBinder) {
			getAccessControlManager().checkOperation(RequestContextHolder.getRequestContext().getZone(), WorkAreaOperation.SITE_ADMINISTRATION);
   		} else {
   			switch (operation) {
   			case manageFunctionMembership:
    				getAccessControlManager().checkOperation(workArea, WorkAreaOperation.CHANGE_ACCESS_CONTROL);
   				break;
   			default:
   				throw new NotSupportedException(operation.toString(), "checkAccess");
  					
   			}
   		}		
	}
	private BinderProcessor loadBinderProcessor(Binder binder) {
		return (BinderProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
	}

   	/**
	 * Use operation so we can keep the logic out of application
	 * and easisly change the required rights
   	 * 
   	 */
  	public boolean testAccess(AdminOperation operation) {
   		try {
   			checkAccess(operation);
   			return true;
   		} catch (AccessControlException ac) {
   			return false;
   		}
   	}
  	public void checkAccess(AdminOperation operation) {
   		Binder top = RequestContextHolder.getRequestContext().getZone();
		switch (operation) {
			case manageFunction:
			case managePosting:
			case manageTemplate:
  				getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
   				break;
			case report:
   				if (getAccessControlManager().testOperation(top, WorkAreaOperation.GENERATE_REPORTS)) break;
 				getAccessControlManager().checkOperation(top, WorkAreaOperation.SITE_ADMINISTRATION);
   				break;
			default:
   				throw new NotSupportedException(operation.toString(), "checkAccess");
		}

   	}

    public List<PostingDef> getPostings() {
    	return coreDao.loadPostings(RequestContextHolder.getRequestContext().getZoneId());
    }
    public void modifyPosting(String postingId, Map updates) {
    	checkAccess(AdminOperation.managePosting);
    	PostingDef post = coreDao.loadPosting(postingId, RequestContextHolder.getRequestContext().getZoneId());
    	ObjectBuilder.updateObject(post, updates);
    }
    public void addPosting(Map updates) {
    	checkAccess(AdminOperation.managePosting);
    	PostingDef post = new PostingDef();
    	post.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
       	ObjectBuilder.updateObject(post, updates);
       	post.setEmailAddress(post.getEmailAddress().toLowerCase());
      	coreDao.save(post);   	
    }
    public void deletePosting(String postingId) {
    	checkAccess(AdminOperation.managePosting);
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
       	checkAccess(AdminOperation.managePosting);
    	getPostingObject().setScheduleInfo(config);
    }	     
    private EmailPosting getPostingObject() {
    	String emailPostingClass = getMailModule().getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.POSTING_JOB);
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
				config.setTemplateTitle("__template_default_folder");
				config.setTemplateDescription("__template_default_folder_description");
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
				config.setTemplateTitle("__template_workspace");
				config.setTemplateDescription("__template_workspace_description");
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
				
				config.setTemplateTitle("__template_user_workspace");
				config.setTemplateDescription("__template_user_workspace_description");
				config.setInternalId(ObjectKeys.DEFAULT_USER_WORKSPACE_CONFIG);
				entryDef = getDefinitionModule().addDefaultDefinition(type);
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
		config = doAddTemplate(config, type, updates);
		config.setDefinitionsInherited(false);
		config.setDefinitions(defs);
		
		if (type == Definition.USER_WORKSPACE_VIEW) {
			//get default folder definition
			HistoryStamp stamp = new HistoryStamp(RequestContextHolder.getRequestContext().getUser());
			TemplateBinder def = addDefaultTemplate(Definition.FOLDER_VIEW);
			TemplateBinder newDef = new TemplateBinder(def);
			newDef.setCreation(stamp);
			newDef.setModification(stamp);
			newDef.setFunctionMembershipInherited(true);
			newDef.setPathName(config.getPathName() + "/" + newDef.getTitle());
			getCoreDao().save(newDef);  //generateId for binderKey needed by custom attributes
			config.addBinder(newDef);
			copyBinderAttributes(def, newDef);
			
		}
		return config;
	 }
	protected Binder copyBinderAttributes(Binder source, Binder destination) {
		EntityDashboard dashboard = getCoreDao().loadEntityDashboard(source.getEntityIdentifier());
		if (dashboard != null) {
			EntityDashboard myDashboard = new EntityDashboard(dashboard);
			myDashboard.setOwnerIdentifier(destination.getEntityIdentifier());
			getCoreDao().save(myDashboard);
		  }
		//copy all file attachments
		getFileModule().copyFiles(source, source, destination, destination);
		Map catts = source.getCustomAttributes();
		for (Iterator iter=catts.entrySet().iterator(); iter.hasNext();) {
			Map.Entry me = (Map.Entry)iter.next();
			CustomAttribute ca = (CustomAttribute)me.getValue();
			if (Statistics.ATTRIBUTE_NAME.equals(ca.getName())) continue;
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
					//only support file attachments now
					FileAttachment sfa = (FileAttachment)ca.getValue();
					if (sfa == null) continue;
					FileAttachment dfa = destination.getFileAttachment(sfa.getFileItem().getName());
					//attach as custom attribute
					if (dfa != null) source.addCustomAttribute(ca.getName(), dfa);
					break;
				}
				case CustomAttribute.ORDEREDSET:
				case CustomAttribute.SET: {
					//should be the same type
					Set values = ca.getValueSet();
					if (!values.isEmpty()) {
						Set newV;
						if (ca.getValueType() == CustomAttribute.ORDEREDSET) newV = new LinkedHashSet(); 
						else newV = new HashSet();
						for (Iterator it=values.iterator(); it.hasNext();) {
							Object val = iter.next();
							if (val == null) continue;
							if (val instanceof Event) {
								Event newE = (Event)((Event)val).clone();
								newE.setId(null);
								getCoreDao().save(newE);
								newV.add(newE);
							} else if (val instanceof FileAttachment) {
								//only support file attachments now
								FileAttachment sfa = (FileAttachment)val;
								FileAttachment dfa = destination.getFileAttachment(sfa.getFileItem().getName());
								//attach as custom attribute
								if (dfa != null) newV.add(dfa);
								
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
	    checkAccess(AdminOperation.manageTemplate);
		TemplateBinder template = new TemplateBinder();
		Definition entryDef = getDefinitionModule().addDefaultDefinition(type);
		template.setEntryDef(entryDef);
		if (type == Definition.FOLDER_VIEW) template.setLibrary(true);
		List definitions = new ArrayList();
		definitions.add(entryDef);
		template.setDefinitionsInherited(false);
		template.setDefinitions(definitions);
		template.setFunctionMembershipInherited(true);
       	String icon = DefinitionUtils.getPropertyValue(entryDef.getDefinition().getRootElement(), "icon");
       	if (Validator.isNotNull(icon)) template.setIconName(icon);
		doAddTemplate(template, type, updates);
	    return template.getId();
	 }
	 
		//add top level template
	 public Long addTemplate(Document doc) {
		 checkAccess(AdminOperation.manageTemplate);
		 Element config = doc.getRootElement();
		 String internalId = config.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID);
		 if (Validator.isNotNull(internalId)) {
			 try {
			 //	see if it exists
				 Binder binder = getCoreDao().loadReservedBinder(internalId, RequestContextHolder.getRequestContext().getZoneId());
				 if (binder instanceof TemplateBinder) {
					 //if it exists, delete it
					 getBinderModule().deleteBinder(binder.getId());
				 } else {
					 throw new ConfigurationException("Reserved binder exists with same internal id");
				 }
			 } catch (NoBinderByTheNameException nb ) {
				 //	okay doesn't exists
			 }; 
		 }
		 TemplateBinder template = new TemplateBinder();
		 template.setInternalId((internalId));
		 doTemplate(template, config);
		 //see if any child configs need to be copied
		 List nodes = config.selectNodes("./" + ObjectKeys.XTAG_ELEMENT_TYPE_TEMPLATE);
		 for (int i=0; i<nodes.size(); ++i) {
			 Element element = (Element)nodes.get(i);
			 TemplateBinder child = new TemplateBinder();
			 template.addBinder(child);
			 doTemplate(child, element);
		 }
		 //need to flush, if multiple loaded in 1 transaction the binderKey may not have been
		 //flushed which could result in duplicates on the next save
		 getCoreDao().flush();
		 return template.getId();
	 }
	 protected void doTemplate(TemplateBinder template, Element config) {
		 Integer type = Integer.valueOf(config.attributeValue(ObjectKeys.XTAG_ATTRIBUTE_TYPE));

		 template.setLibrary(GetterUtil.get(XmlUtils.getProperty(config, ObjectKeys.XTAG_BINDER_LIBRARY), false));
		 template.setUniqueTitles(GetterUtil.get(XmlUtils.getProperty(config, ObjectKeys.XTAG_BINDER_UNIQUETITLES), false));
		 template.setDefinitionsInherited(GetterUtil.get(XmlUtils.getProperty(config, ObjectKeys.XTAG_BINDER_INHERITDEFINITIONS), false));
		 template.setFunctionMembershipInherited(GetterUtil.get(XmlUtils.getProperty(config, ObjectKeys.XTAG_BINDER_INHERITFUNCTIONMEMBERSHIP), true));
		 template.setTeamMembershipInherited(GetterUtil.get(XmlUtils.getProperty(config, ObjectKeys.XTAG_BINDER_INHERITTEAMMEMBERS), true));
		 //get attribute from document
		 Map updates = XmlUtils.getAttributes(config);
		 doAddTemplate(template, type, updates);
		 //setup after template is saved
		 if (!template.isTeamMembershipInherited()) XmlUtils.getTeamMembersFromXml(template, config, this);
		 XmlUtils.getDefinitionsFromXml(template, config, this);
		 if (!template.isFunctionMembershipInherited()) XmlUtils.getFunctionMembershipFromXml(template, config, this);
		 template.setEntryDef(template.getDefaultViewDef());
		 Element dashboardConfig = (Element)config.selectSingleNode(ObjectKeys.XTAG_ELEMENT_TYPE_DASHBOARD);
		 if (dashboardConfig != null) {
			getDashboardModule().createEntityDashboard(template.getEntityIdentifier(), dashboardConfig);
		 }
	 }
	 protected TemplateBinder doAddTemplate(TemplateBinder template, int type, Map updates) {
		 template.setZoneId(RequestContextHolder.getRequestContext().getZoneId());
		 template.setDefinitionType(type);
		 template.setCreation(new HistoryStamp(RequestContextHolder.getRequestContext().getUser()));
		 template.setModification(template.getCreation());
		 EntryBuilder.updateEntry(template, updates);
		 if (Validator.isNull(template.getTitle())) template.setTitle(template.getTemplateTitle());
		 if (template.isRoot()) {
			 template.setPathName("/" + template.getTitle());
		 } else {
			 template.setPathName(template.getParentBinder().getPathName() + "/" + template.getTitle());
		 }
		 getCoreDao().save(template);
		 if (template.isRoot()) template.setupRoot();
		 return template;
	 }

	 //clone a top level template as a child of another template
	 public Long addTemplate(Long parentId, Long srcConfigId) {
		 checkAccess(AdminOperation.manageTemplate);
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
      	 getCoreDao().updateFileName(parentConfig, config, null, config.getTitle());
		 //get childen before adding new children incase parent and source are the same
		 List<TemplateBinder> children = new ArrayList(srcConfig.getBinders());
      	 
		 getCoreDao().save(config); // generateId for binderKey needed by custom attributes
		 parentConfig.addBinder(config);
     	 copyBinderAttributes(srcConfig, config);
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
		   checkAccess(AdminOperation.manageTemplate);
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
			getCoreDao().save(config); //need id for binderKey
	      	if (parent != null) {
				parent.addBinder(config);
	      		getCoreDao().updateFileName(parent, config, null, config.getTitle());
	      	} else {
	      		config.setupRoot();
	      	}
			copyBinderAttributes(binder, config);
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
		checkAccess(AdminOperation.manageTemplate);
		TemplateBinder config = (TemplateBinder)getCoreDao().loadBinder(id, RequestContextHolder.getRequestContext().getZoneId());
		ObjectBuilder.updateObject(config, updates);
	}
	public Document getTemplateAsXml(TemplateBinder binder) {
		Document doc = DocumentHelper.createDocument();
		Element element = doc.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_TEMPLATE);
		getTemplateAsXml(binder, element);
		return doc;
	}
	protected void getTemplateAsXml(TemplateBinder binder, Element element) {
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_TYPE, binder.getDefinitionType().toString());
		element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID, binder.getInternalId());
		
		XmlUtils.addAttributeCData(element, ObjectKeys.XTAG_TEMPLATE_TITLE, ObjectKeys.XTAG_TYPE_STRING, binder.getTemplateTitle());
		XmlUtils.addAttributeCData(element, ObjectKeys.XTAG_TEMPLATE_DESCRIPTION, ObjectKeys.XTAG_TYPE_DESCRIPTION, binder.getTemplateDescription());
		XmlUtils.addAttributeCData(element, ObjectKeys.XTAG_ENTITY_TITLE, ObjectKeys.XTAG_TYPE_STRING, binder.getTitle());
		XmlUtils.addAttributeCData(element, ObjectKeys.XTAG_ENTITY_DESCRIPTION, ObjectKeys.XTAG_TYPE_DESCRIPTION, binder.getDescription());
		XmlUtils.addAttribute(element, ObjectKeys.XTAG_ENTITY_ICONNAME, ObjectKeys.XTAG_TYPE_STRING, binder.getIconName());			
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_LIBRARY, binder.isLibrary());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_UNIQUETITLES, binder.isUniqueTitles());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITFUNCTIONMEMBERSHIP, binder.isFunctionMembershipInherited());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITDEFINITIONS, binder.isDefinitionsInherited());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_BINDER_INHERITTEAMMEMBERS, binder.isTeamMembershipInherited());
		if (!binder.isTeamMembershipInherited()) {
			//store as names, not ids
			Set<Long> ids = binder.getTeamMemberIds();
			List<Principal> members = getProfileDao().loadPrincipals(ids, binder.getZoneId(), true);
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
	    	 List<WorkAreaFunctionMembership> wfms = getWorkAreaFunctionMemberships(binder);
	    	 for (WorkAreaFunctionMembership fm: wfms) {
	    		 Set ids = fm.getMemberIds();
	    		 List<Principal> members = getProfileDao().loadPrincipals(ids, binder.getZoneId(), true);
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
				Element e = element.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_DEFINITION);
				e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, def.getName());
				e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID, def.getInternalId());
				e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_ID, def.getId().toString());
			}
			
		} else {
			for (Definition def:defs) {
				Element e = element.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_DEFINITION);
				e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, def.getName());
				e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID, def.getInternalId());
				e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_ID, def.getId().toString());
			}
		}
		Map<String,Definition> workflows = binder.getWorkflowAssociations();
		for (Map.Entry me: workflows.entrySet()) {
			Element e = element.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_DEFINITION);
			Definition def = (Definition)me.getValue();
			e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_NAME, def.getName());
			e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID, def.getInternalId());
			e.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_ID, def.getId().toString());
			//log the definitionId the workflow refers to
			XmlUtils.addProperty(e, ObjectKeys.XTAG_ENTITY_DEFINITION, me.getKey());
			
		}
		
		
		Dashboard dashboard = getDashboardModule().getEntityDashboard(binder.getEntityIdentifier());
		if (dashboard != null) dashboard.asXml(element);
		
		List<TemplateBinder> children = binder.getBinders();
		for (TemplateBinder child:children) {
			Element childElement = element.addElement(ObjectKeys.XTAG_ELEMENT_TYPE_TEMPLATE);
			getTemplateAsXml(child, childElement);
			
		}

	}
	public TemplateBinder getTemplate(Long id) {
		//TODO: is there access
		return (TemplateBinder)getCoreDao().loadBinder(id, RequestContextHolder.getRequestContext().getZoneId());
	}
	public List<TemplateBinder> getTemplates() {
		//world read
		return getCoreDao().loadConfigurations(RequestContextHolder.getRequestContext().getZoneId());
	}
	public List<TemplateBinder> getTemplates(int type) {
		//world read
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
			try {
				EntityDashboard dashboard = getCoreDao().loadEntityDashboard(b.getEntityIdentifier());
				if (dashboard != null) {
					DashboardHelper.resolveRelativeBinders(b, dashboard);
				}
			} catch (Exception ex) {
				//at this point just log errors  index has already been updated
				//if throw errors, rollback will take effect and must manualy remove from index
				logger.error("Error adding dashboard " + ex.getLocalizedMessage());
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
	   Map props = cfg.getProperties();
	   if (props != null) binder.setProperties(props);
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
	    	//need to reindex acls - since new shouldn't have any children
	       	loadBinderProcessor(binder).indexFunctionMembership(binder, false);
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
	
 
	public void addFunction(String name, Set<WorkAreaOperation> operations) {
		checkAccess(AdminOperation.manageFunction);
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
		checkAccess(AdminOperation.manageFunction);
		Function function = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), id);
		if (updates.containsKey("name") && function.getName() != updates.get("name")) {
			List zoneFunctions = functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
			//make sure unqiue - do after find or hibernate will update
			zoneFunctions.remove(function);
			function.setName((String)updates.get("name"));
			if (zoneFunctions.contains(function)) {
				//Role already exists
				throw new FunctionExistsException(function.getName());
			}
			
		}
		ObjectBuilder.updateObject(function, updates);
		functionManager.updateFunction(function);			
    }
    public void deleteFunction(Long id) {
		checkAccess(AdminOperation.manageFunction);
		Function f = functionManager.getFunction(RequestContextHolder.getRequestContext().getZoneId(), id);
		functionManager.deleteFunction(f);
    }
    public List<Function> getFunctions() {
		//let anyone read them			
        return  functionManager.findFunctions(RequestContextHolder.getRequestContext().getZoneId());
    }
    public void setWorkAreaFunctionMemberships(final WorkArea workArea, final Map<Long, Set<Long>> functionMemberships) {
		checkAccess(workArea, AdminOperation.manageFunctionMembership);
		final Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		//get list of current readers to compare for indexing
		List<WorkAreaFunctionMembership>wfms = 
	       		getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.READ_ENTRIES);
       	TreeSet<Long> origional = new TreeSet();
        for (WorkAreaFunctionMembership wfm:wfms) {
        	origional.addAll(wfm.getMemberIds());
    	}
        //first remove any that are not in the new list
        getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		//get list of current memberships
        		List<WorkAreaFunctionMembership>wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(zoneId, workArea);
                		for( WorkAreaFunctionMembership wfm:wfms) {
        			if (!functionMemberships.containsKey(wfm.getFunctionId()))
        				getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(wfm);       	
        		}
        		for (Map.Entry<Long, Set<Long>> fm : functionMemberships.entrySet()) {
        			WorkAreaFunctionMembership membership=null;
        			//find in current list
        			for (int i=0; i<wfms.size(); ++i) {
        				WorkAreaFunctionMembership wfm = wfms.get(i);
        				if (wfm.getFunctionId().equals(fm.getKey())) {
        					membership = wfm;
        					break;	        	
        				}
        			}
        			Set members = fm.getValue();
        			if (membership == null) { 
        				membership = new WorkAreaFunctionMembership();
        				membership.setZoneId(zoneId);
        				membership.setWorkAreaId(workArea.getWorkAreaId());
        				membership.setWorkAreaType(workArea.getWorkAreaType());
        				membership.setFunctionId(fm.getKey());
        				membership.setMemberIds(members);
        				getWorkAreaFunctionMembershipManager().addWorkAreaFunctionMembership(membership);
        			} else if (members == null || members.isEmpty()) {
        				getWorkAreaFunctionMembershipManager().deleteWorkAreaFunctionMembership(membership);       	
        			} else {
        				Set mems = membership.getMemberIds();
        				if (!mems.equals(members)) {
        					mems.clear();
        					mems.addAll(members);
        					membership.setMemberIds(mems);
        				}
        			}
        		}
				processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
				return null;
        	}});
		//get new list of readers
      	wfms = getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMembershipsByOperation(zoneId, workArea, WorkAreaOperation.READ_ENTRIES);
      	TreeSet<Long> current = new TreeSet();
      	for (WorkAreaFunctionMembership wfm:wfms) {
      		current.addAll(wfm.getMemberIds());
      	}
      	//only reindex if readers were affected.  Do outside transaction
		if (!origional.equals(current) && (workArea instanceof Binder)) {
			Binder binder = (Binder)workArea;
			loadBinderProcessor(binder).indexFunctionMembership(binder, true);
		}
	}


     public void setWorkAreaOwner(final WorkArea workArea, final Long userId) {
    	 checkAccess(workArea, AdminOperation.manageFunctionMembership);
    	if (!workArea.getOwnerId().equals(userId)) {
    		getTransactionTemplate().execute(new TransactionCallback() {
   		        	public Object doInTransaction(TransactionStatus status) {
   		        		User user = getProfileDao().loadUser(userId, RequestContextHolder.getRequestContext().getZoneId());
   		        		workArea.setOwner(user);
   		        		return null;
   		       }});
    		//do outside transaction
    		//need to update access, since owner has changed - assume read access is effected
			if (workArea instanceof Binder) {
				Binder binder = (Binder)workArea;
				loadBinderProcessor(binder).indexFunctionMembership(binder, false);
			}
    	}
    }
    public WorkAreaFunctionMembership getWorkAreaFunctionMembership(WorkArea workArea, Long functionId) {
		// open to anyone - only way to get parentMemberships
    	// checkAccess(workArea, "getWorkAreaFunctionMembership");

        return getWorkAreaFunctionMembershipManager().getWorkAreaFunctionMembership
       		(RequestContextHolder.getRequestContext().getZoneId(), workArea, functionId);
    }
    
	public List<WorkAreaFunctionMembership> getWorkAreaFunctionMemberships(WorkArea workArea) {
		// open to anyone - only way to get parentMemberships
		//checkAccess(workArea, "getWorkAreaFunctionMemberships");

        return getWorkAreaFunctionMembershipManager().findWorkAreaFunctionMemberships(
        		RequestContextHolder.getRequestContext().getZoneId(), workArea);
	}

	public List<WorkAreaFunctionMembership> getWorkAreaFunctionMembershipsInherited(WorkArea workArea) {
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

	public void setWorkAreaFunctionMembershipInherited(final WorkArea workArea, final boolean inherit) 
    throws AccessControlException {
    	checkAccess(workArea, AdminOperation.manageFunctionMembership);
        Boolean index = (Boolean) getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
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
             		processAccessChangeLog(workArea, ChangeLog.ACCESSMODIFY);
             		//just changed from not inheritting to inherit = need to update index
            		//if changed from inherit to not, index remains the same
              		if (inherit) return Boolean.TRUE;

            	}
        		return Boolean.FALSE;
        	}});
        //index outside of transaction
        if (index && (workArea instanceof Binder)) {
			Binder binder = (Binder)workArea;
			loadBinderProcessor(binder).indexFunctionMembership(binder, true);
		}
     } 
	private void processAccessChangeLog(WorkArea workArea, String operation) {
        if ((workArea instanceof Binder) && !(workArea instanceof TemplateBinder)) {
        	Binder binder = (Binder)workArea;
        	User user = RequestContextHolder.getRequestContext().getUser();
        	binder.incrLogVersion();
        	binder.setModification(new HistoryStamp(user));
        	loadBinderProcessor(binder).processChangeLog(binder, operation);
       	}
	}

    public Map<String, Object> sendMail(Collection<Long> ids, Collection<String> emailAddresses, String subject, Description body, Collection<DefinableEntity> entries, boolean sendAttachments) throws Exception {
    	User user = RequestContextHolder.getRequestContext().getUser();
		Set emailSet = new HashSet();
		List distribution = new ArrayList();
		List errors = new ArrayList();
		Map result = new HashMap();
		result.put(ObjectKeys.SENDMAIL_ERRORS, errors);
		result.put(ObjectKeys.SENDMAIL_DISTRIBUTION, distribution);
		//add email address listed 
		Object[] errorParams = new Object[3];
		if (emailAddresses != null) {
			for (String e: emailAddresses) {
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
		}
		Set<Long> userIds = getProfileDao().explodeGroups(ids, user.getZoneId());
		List<User> users = getCoreDao().loadObjects(userIds, User.class, user.getZoneId());
		for (User e:users) {
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
				List iCalendars = new ArrayList();
				 	 			
	 			for (DefinableEntity entry:entries) {
					attachments.addAll(entry.getFileAttachments());
					if (entry.getEvents() != null && !entry.getEvents().isEmpty()) {
	 					iCalendars.add(getIcalModule().generate(entry, entry.getEvents(), getMailModule().getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.DEFAULT_TIMEZONE)));
	 				}
	 	 		}
				message.put(SendEmail.ATTACHMENTS, attachments);
	 			if (!iCalendars.isEmpty()) {
	 				message.put(SendEmail.ICALENDARS, iCalendars);
	 			}
			}
 			
 			
 		}
		boolean sent = getMailModule().sendMail(RequestContextHolder.getRequestContext().getZone(), message, user.getTitle() + " email");
		if (sent) result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_SENT);
		else result.put(ObjectKeys.SENDMAIL_STATUS, ObjectKeys.SENDMAIL_STATUS_SCHEDULED);
		return result;
    }


   public List<ChangeLog> getChanges(Long binderId, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("owningBinderId", binderId);
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
   public List<ChangeLog> getChanges(EntityIdentifier entityIdentifier, String operation) {
	   FilterControls filter = new FilterControls();
	   filter.add("entityId", entityIdentifier.getEntityId());
	   filter.add("entityType", entityIdentifier.getEntityType().name());
	   if (!Validator.isNull(operation)) {
		   filter.add("operation", operation);
	   }
	   OrderBy order = new OrderBy();
	   order.addColumn("operationDate");	   
	   filter.setOrderBy(order);
	   
	   return filterChangeLogs(getCoreDao().loadObjects(ChangeLog.class, filter)); 
   	
   }
   private List<ChangeLog> filterChangeLogs(List<ChangeLog> changeLogs) {
	   User user = RequestContextHolder.getRequestContext().getUser();
	   if (user.isSuper()) return changeLogs;
	   // get the current users acl set
	   Set<Long> userAclSet = getProfileDao().getPrincipalIds(user);
	   Set userStringIds = new HashSet();
	   for (Long id:userAclSet) {
		   userStringIds.add(id.toString());
	   }
	   List<ChangeLog> result = new ArrayList();
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
