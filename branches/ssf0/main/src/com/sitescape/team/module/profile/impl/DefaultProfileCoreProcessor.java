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
package com.sitescape.team.module.profile.impl;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.dom4j.Element;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.calendar.TimeZoneHelper;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.dao.util.SFQuery;
import com.sitescape.team.domain.Application;
import com.sitescape.team.domain.ApplicationGroup;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.ChangeLog;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.Entry;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.GroupPrincipal;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserPrincipal;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.domain.AuditTrail.AuditType;
import com.sitescape.team.jobs.UserTitleChange;
import com.sitescape.team.module.binder.impl.AbstractEntryProcessor;
import com.sitescape.team.module.binder.processor.BinderProcessor;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.profile.index.ProfileIndexUtils;
import com.sitescape.team.module.profile.processor.ProfileCoreProcessor;
import com.sitescape.team.module.shared.ChangeLogUtils;
import com.sitescape.team.module.shared.EntityIndexUtils;
import com.sitescape.team.module.shared.EntryBuilder;
import com.sitescape.team.module.shared.InputDataAccessor;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.module.shared.XmlUtils;
import com.sitescape.team.util.LongIdUtil;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.ReflectHelper;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SimpleProfiler;
import com.sitescape.util.Validator;
/**
 *
 * @author Jong Kim
 */
public class DefaultProfileCoreProcessor extends AbstractEntryProcessor
	implements ProfileCoreProcessor {
	DateFormat dateFmt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
	//inside write transaction    
	public void deleteBinder(Binder binder, boolean deleteMirroredSource, Map options) {
		if(logger.isDebugEnabled())
			logger.debug("Deleting binder [" + binder.getPathName() + "]");
		if (!binder.isDeleted()) super.deleteBinder(binder, deleteMirroredSource, options);
		else {
			//if binder is marked deleted, we are called from cleanup code without a transaction 
			final ProfileBinder pBinder = (ProfileBinder)binder;

			//loop through all entries and record delete
			Boolean done=Boolean.FALSE;
			while (!done) {
				done = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
					public Object doInTransaction(TransactionStatus status) {
						SFQuery query = getProfileDao().queryAllPrincipals(new FilterControls(), pBinder.getZoneId()); 
						try {
							int count = 0;
							List entries = new ArrayList();
							while (query.hasNext()) {
								Object obj = query.next();
								if (obj instanceof Object[])
									obj = ((Object [])obj)[0];
				 				Principal entry = (Principal)obj;
				 				if(logger.isDebugEnabled())
				 					logger.debug("Deleting entry [" + entry.getTitle() + "], id=" + entry.getId());
				 				//create history - using timestamp and version from folder delete
				 				try {
				 					entry.setModification(pBinder.getModification());
				 					entry.incrLogVersion();
				 					processChangeLog(entry, ChangeLog.DELETEENTRY);
				 				} catch (Exception ex) {
				 					logger.warn("Error logging entry " + entry.toString(), ex);
				 				}
									
				 				getFileModule().deleteFiles(pBinder, entry, false, null);
				 				entries.add(entry);
				 				++count;
									
				 				//commit after 100
				 				if (count == 100) {
				 					//mark processed entries as deleted, so not read again
				 					//evict entries so not updated
				 					getProfileDao().markEntriesDeleted(pBinder, entries);
				 					return Boolean.FALSE;
								}
							}
							//mark delete and flush from cache, cause handled in bulk way
		 					getProfileDao().markEntriesDeleted(pBinder, entries);
							getCoreDao().flush();  //flush before bulk updates
							//finally remove folder and its entries
							getProfileDao().delete(pBinder);
							//delete binder
							return Boolean.TRUE;
						} catch (Exception ex) {
							//don't want the transaction to clear the session
							logger.warn("Error delete folder " + pBinder.getPathName(), ex);
							return Boolean.TRUE;
						} finally {
							query.close();
						}
					}
		        });
			}
	     };
	}
	    
	//inside write transaction    
	public void deleteBinder_delete(Binder binder, boolean deleteMirroredSource, Map ctx) {
		//don't remove from parent, cause needs zone pointer for request context setup on zone delete
		//mark for delete now and continue later
		binder.setDeleted(true);
	} 
	    
	//inside write transaction    
	protected void deleteBinder_processFiles(Binder binder, Map ctx) {
		getFileModule().deleteFiles(binder, binder, false, null);
	}

 
       //*******************************************************************/
  	//not supported
	public void moveBinder(Binder source, Binder destination, Map options) {
		throw new NotSupportedException("errorcode.notsupported.moveBinder");
	
	}
    //*******************************************************************/
  	//not supported
	public Binder copyBinder(Binder source, Binder destination, InputDataAccessor inputData, Map options)  {
		throw new NotSupportedException("errorcode.notsupported.copyBinder");
	
	}
    //*******************************************************************/
    //inside write transaction    
    protected void addBinder_fillIn(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
    	super.addBinder_fillIn(parent,binder, inputData, entryData, ctx);
    	Integer type = binder.getDefinitionType();
    	if ((type != null) && (type.intValue() == Definition.USER_WORKSPACE_VIEW)) {
    		Principal u = binder.getCreation().getPrincipal(); //creator is user
    		if (!(u instanceof User)) {
    			u = getProfileDao().loadUserPrincipal(u.getId(), u.getZoneId(), false);
    		}
    		if (u instanceof User) {
    			((Workspace)binder).setSearchTitle(((User)u).getSearchTitle());
    		}
    	}
   	
    }
    //inside write transaction    
    protected void addBinder_postSave(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
    	Integer type = binder.getDefinitionType();
    	if ((type != null) && (type.intValue() == Definition.USER_WORKSPACE_VIEW)) {
    		Principal u = binder.getCreation().getPrincipal(); //creator is user
    		if (!(u instanceof User)) {
    			u = getProfileDao().loadUserPrincipal(u.getId(), u.getZoneId(), false);
    		}
    		if (u instanceof User) {
    			//do this here, since we have a transaction running
    			u.setWorkspaceId(binder.getId());
    		}
    	}
    	super.addBinder_postSave(parent, binder, inputData, entryData, ctx);
    	
    }
    //***********************************************************************************************************
            
    //inside write transaction
   protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
        ((Principal)entry).setZoneId(binder.getZoneId());
        doProfileEntryFillin(entry, inputData, entryData);
        super.addEntry_fillIn(binder, entry, inputData, entryData, ctx);
     }
    //inside write transaction
    protected void addEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	//make user the user is owner so create_modify access works
    	if (entry instanceof User) {
    		entry.getCreation().setPrincipal((User)entry);
    		entry.getModification().setPrincipal((User)entry);
    	}
    	super.addEntry_postSave(binder, entry, inputData, entryData, ctx);
    }
       
    //***********************************************************************************************************	
    protected void modifyEntry_setCtx(Entry entry, Map ctx) {
    	if (entry instanceof GroupPrincipal) {
    		ctx.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, new HashSet(((GroupPrincipal)entry).getMembers()));
    	}
    	super.modifyEntry_setCtx(entry, ctx);
    }
    //inside write transaction
   protected void modifyEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
    	//see if we have updates to fields not covered by definition build
    	doProfileEntryFillin(entry, inputData, entryData);
    	super.modifyEntry_fillIn(binder, entry, inputData, entryData, ctx);
    }
   //inside write transaction
   protected void modifyEntry_postFillIn(Binder binder, Entry entry, InputDataAccessor inputData, 
   		Map entryData, Map<FileAttachment,String> fileRenamesTo, Map ctx) {
	   super.modifyEntry_postFillIn(binder, entry, inputData, entryData, fileRenamesTo, ctx);
	   if (entry instanceof User) {
		   //need to schedule a reindex of entries with my title
		   checkUserTitle((User)entry, ctx);
	   }
		   
   }
   protected void modifyEntry_indexAdd(Binder binder, Entry entry, 
   		InputDataAccessor inputData, List fileUploadItems, 
   		Collection<FileAttachment> filesToIndex, Map ctx) {
	   //index self
	   super.modifyEntry_indexAdd(binder, entry, 
		   		inputData, fileUploadItems, filesToIndex, ctx);
/* Only needed if index group membership
 * 	   if ((entry instanceof Group) && ctx.containsKey(ObjectKeys.FIELD_GROUP_MEMBERS)) {
		   Group g = (Group)entry;
		   Collection<Principal> newMembers = g.getMembers();
		   Collection<Principal> oldMembers = (Collection<Principal>)ctx.get(ObjectKeys.FIELD_GROUP_MEMBERS);
		   Set<Principal> remM = CollectionUtil.differences(oldMembers, newMembers);
		   Set<Principal> newM = CollectionUtil.differences(newMembers, oldMembers);
		   Set<Long> entryIds = new HashSet();
		   for (Principal p:remM) entryIds.add(p.getId());
		   for (Principal p:newM) entryIds.add(p.getId());
		   //need to reindex		   
		   List<User> users = getProfileDao().loadUsers(entryIds, binder.getZoneId());
		   indexEntries(users);		   
	   }
*/
   }
   protected void checkUserTitle(User user, Map ctx) {
	   String originalTitle = (String)ctx.get(ObjectKeys.FIELD_ENTITY_TITLE);
	   if (!user.getTitle().equals(originalTitle)) {
		   //need to update user workspace
		   List excludeIds=null;
		   if (user.getWorkspaceId() != null) {
			   Workspace ws = (Workspace)getCoreDao().load(Workspace.class, user.getWorkspaceId());
			   if (ws != null) {
				   BinderProcessor processor = (BinderProcessor)getProcessorManager().getProcessor(ws, ws.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
				   Map updates = new HashMap();
				   updates.put(ObjectKeys.FIELD_ENTITY_TITLE, user.getTitle());
				   updates.put(ObjectKeys.FIELD_BINDER_SEARCHTITLE, user.getSearchTitle());
				   try {
					   processor.modifyBinder(ws, new MapInputData(updates), null, null, null);
					   excludeIds = new ArrayList();
					   excludeIds.add(ws.getId());
					   //modifyBinder will also reindex direct child binders, so remove them
					   List<Binder> binders = ws.getBinders();
					   for (Binder child:binders) {
						   excludeIds.add(child.getId());
					   }
				   } catch (WriteFilesException wf) {};
				   
			   }
		   }
		   List entryIds = getCoreDao().loadObjects("select id from com.sitescape.team.domain.FolderEntry where creation.principal=" +
				   user.getId() + " or modification.principal=" + user.getId(), null);
		   List binderIds = getCoreDao().loadObjects("select id from com.sitescape.team.domain.Binder where creation.principal=" +
				   	user.getId() + " or modification.principal=" + user.getId(), null);
		   //remove binders already processed in modifyBinder above
		   if (excludeIds != null) binderIds.removeAll(excludeIds);
		   String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		   String jobClass = SZoneConfig.getString(zoneName, "userConfiguration/property[@name='" + UserTitleChange.USER_TITLE_JOB + "']");
		   if (Validator.isNull(jobClass)) jobClass = "com.sitescape.team.jobs.DefaultUserTitleChange";
		   try {
			   Class processorClass = ReflectHelper.classForName(jobClass);
			   UserTitleChange job = (UserTitleChange)processorClass.newInstance();
			   job.schedule(user, binderIds, entryIds);
 	
		   } catch (ClassNotFoundException e) {
			   throw new ConfigurationException(
					   "Invalid UserTitleChange class name '" + jobClass + "'", e);
		   } catch (InstantiationException e) {
			   throw new ConfigurationException(
					   "Cannot instantiate UserTitleChange of type '"  + jobClass + "'");
		   } catch (IllegalAccessException e) {
			   throw new ConfigurationException(
					   "Cannot instantiate UserTitleChange of type '" + jobClass + "'");
		   }
	   }	   
   }
   /**
     * Handle fields that are not covered by the definition builder
     * @param entry
     * @param inputData
     * @param entryData
     */
    protected void doProfileEntryFillin(Entry entry, InputDataAccessor inputData, Map entryData) {
    	if (entry instanceof User) {
    		if (inputData.exists(ObjectKeys.FIELD_USER_DISPLAYSTYLE) && !entryData.containsKey(ObjectKeys.FIELD_USER_DISPLAYSTYLE)) {
    			entryData.put(ObjectKeys.FIELD_USER_DISPLAYSTYLE, inputData.getSingleValue(ObjectKeys.FIELD_USER_DISPLAYSTYLE));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_FIRSTNAME) && !entryData.containsKey(ObjectKeys.FIELD_USER_FIRSTNAME)) {
    			entryData.put(ObjectKeys.FIELD_USER_FIRSTNAME, inputData.getSingleValue(ObjectKeys.FIELD_USER_FIRSTNAME));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_LASTNAME) && !entryData.containsKey(ObjectKeys.FIELD_USER_LASTNAME)) {
    			entryData.put(ObjectKeys.FIELD_USER_LASTNAME, inputData.getSingleValue(ObjectKeys.FIELD_USER_LASTNAME));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_MIDDLENAME) && !entryData.containsKey(ObjectKeys.FIELD_USER_MIDDLENAME)) {
    			entryData.put(ObjectKeys.FIELD_USER_MIDDLENAME, inputData.getSingleValue(ObjectKeys.FIELD_USER_MIDDLENAME));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_LOCALE) && !entryData.containsKey(ObjectKeys.FIELD_USER_LOCALE)) {
    			entryData.put(ObjectKeys.FIELD_USER_LOCALE, inputData.getSingleObject(ObjectKeys.FIELD_USER_LOCALE));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_EMAIL) && !entryData.containsKey(ObjectKeys.FIELD_USER_EMAIL)) {
    			entryData.put(ObjectKeys.FIELD_USER_EMAIL, inputData.getSingleValue(ObjectKeys.FIELD_USER_EMAIL));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_EMAIL_MOBILE) && !entryData.containsKey(ObjectKeys.FIELD_USER_EMAIL_MOBILE)) {
    			entryData.put(ObjectKeys.FIELD_USER_EMAIL_MOBILE, inputData.getSingleValue(ObjectKeys.FIELD_USER_EMAIL_MOBILE));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_EMAIL_TEXT) && !entryData.containsKey(ObjectKeys.FIELD_USER_EMAIL_TEXT)) {
    			entryData.put(ObjectKeys.FIELD_USER_EMAIL_TEXT, inputData.getSingleValue(ObjectKeys.FIELD_USER_EMAIL_TEXT));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_TIMEZONE) && !entryData.containsKey(ObjectKeys.FIELD_USER_TIMEZONE)) {
    			entryData.put(ObjectKeys.FIELD_USER_TIMEZONE, TimeZoneHelper.fixTimeZone((TimeZone)inputData.getSingleObject(ObjectKeys.FIELD_USER_TIMEZONE)));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_PASSWORD) && !entryData.containsKey(ObjectKeys.FIELD_USER_PASSWORD)) {
    			entryData.put(ObjectKeys.FIELD_USER_PASSWORD, inputData.getSingleValue(ObjectKeys.FIELD_USER_PASSWORD));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_SKYPEID) && !entryData.containsKey(ObjectKeys.FIELD_USER_SKYPEID)) {
    			entryData.put(ObjectKeys.FIELD_USER_SKYPEID, inputData.getSingleValue(ObjectKeys.FIELD_USER_SKYPEID));
    		}
    	} else if(entry instanceof Application) {
        	if (inputData.exists(ObjectKeys.FIELD_APPLICATION_POST_URL) && !entryData.containsKey(ObjectKeys.FIELD_APPLICATION_POST_URL)) {
    			entryData.put(ObjectKeys.FIELD_APPLICATION_POST_URL, inputData.getSingleValue(ObjectKeys.FIELD_APPLICATION_POST_URL));
        	}
    	} else { // Group or ApplicationGroup 
        	if (inputData.exists(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS) && !entryData.containsKey(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS)) {
    			entryData.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, inputData.getSingleObject(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS));
        	}
        	//hack to get member names from input and convert to set - Mostly for user/group load
        	if (inputData.exists(ObjectKeys.INPUT_FIELD_GROUP_PRINCIPAL_MEMBERNAME) && !entryData.containsKey(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS)) {
        		String[] sNames = inputData.getValues(ObjectKeys.INPUT_FIELD_GROUP_PRINCIPAL_MEMBERNAME);
        		
         		//see if they exist
        		Map params = new HashMap();
        		params.put("plist", sNames);
        		params.put("zoneId", ((GroupPrincipal)entry).getZoneId());
        		List exists;
        		if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME))
        			exists = getCoreDao().loadObjects("from com.sitescape.team.domain.Principal where zoneId=:zoneId and foreignName in (:plist)", params);
        		else
           			exists = getCoreDao().loadObjects("from com.sitescape.team.domain.Principal where zoneId=:zoneId and name in (:plist)", params);
        		Set members = new HashSet();
        		for (int x=0;x<exists.size(); ++x) {
				   Principal p = (Principal)exists.get(x);
				   if (p.isActive()) members.add(p);
        		}
	   			entryData.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, members);
        	}
    	} 
    	
   		if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_THEME) && !entryData.containsKey(ObjectKeys.FIELD_PRINCIPAL_THEME)) {
			entryData.put(ObjectKeys.FIELD_PRINCIPAL_THEME, inputData.getSingleValue(ObjectKeys.FIELD_PRINCIPAL_THEME));
		}
   		if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME) && !entryData.containsKey(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME)) {
    		entryData.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, inputData.getSingleValue(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME));
    	} 
       	if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_NAME) && !entryData.containsKey(ObjectKeys.FIELD_PRINCIPAL_NAME)) {
    		entryData.put(ObjectKeys.FIELD_PRINCIPAL_NAME, inputData.getSingleValue(ObjectKeys.FIELD_PRINCIPAL_NAME));
    	}
       	String name = (String)entryData.get(ObjectKeys.FIELD_PRINCIPAL_NAME);
       	String foreignName = (String)entryData.get(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME);
       	if (Validator.isNotNull(name)) {
       		//remove blanks
          	name = name.trim();
          	entryData.put(ObjectKeys.FIELD_PRINCIPAL_NAME, name.toLowerCase());
          	//setting the name - see if new entry and force foreign name to be same
       		if (Validator.isNull(((Principal)entry).getName())) {
       			//preserve case on foreign name
       			if (Validator.isNull(foreignName)) entryData.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, name); 
       		}
       	}
       	
    }
    //***********************************************************************************************************
    
   	protected SFQuery indexEntries_getQuery(Binder binder) {
   		return getProfileDao().queryAllPrincipals(new FilterControls(), binder.getZoneId());
   	}
   	protected boolean indexEntries_validate(Binder binder, Entry entry) {
   		Principal p = (Principal)entry;
   		//don't index job processor
   		if (p.isReserved() && ObjectKeys.JOB_PROCESSOR_INTERNALID.equals(p.getInternalId())) return false;
   		return true;
   	}
   	protected void indexEntries_load(Binder binder, List entries)  {
   		// bulkd load any collections that neeed to be indexed
   		getProfileDao().bulkLoadCollections((List<Principal>)entries);
   	}
	protected Map indexEntries_loadTags(Binder binder, List<Entry> entries) {
		List<EntityIdentifier> uIds = new ArrayList();
		List<EntityIdentifier> gIds = new ArrayList();
		for (Entry e: entries) {
			EntityIdentifier id = e.getEntityIdentifier();
			if (id.getEntityType().equals(EntityIdentifier.EntityType.user))
				uIds.add(id);
			else if (id.getEntityType().equals(EntityIdentifier.EntityType.group))
				gIds.add(id);
		}
		if (uIds.isEmpty()) return getCoreDao().loadAllTagsByEntity(gIds);
		else if (gIds.isEmpty()) return  getCoreDao().loadAllTagsByEntity(uIds);
		Map result = getCoreDao().loadAllTagsByEntity(uIds);
		result.putAll(getCoreDao().loadAllTagsByEntity(gIds));
		return result;
	}

    //***********************************************************************************************************           
    protected  Entry entry_load(Binder parentBinder, Long entryId) {
        return getProfileDao().loadPrincipal(entryId, parentBinder.getZoneId(), false);        
    }
          
    //***********************************************************************************************************
    protected void deleteEntry_delete(Binder parentBinder, Entry entry, Map ctx) {
    	
       	if (entry instanceof User) {
       		User p = (User)entry;
       		//mark deleted, cause their ids are used all over
       		//profileDao will delete all associations and groups
       		p.setDeleted(true);
      		Map updatesCtx = new HashMap();
       		updatesCtx.put(ObjectKeys.FIELD_ENTITY_TITLE, p.getTitle());
       		String newName = Validator.replacePathCharacters(NLT.get("profile.deleted.label") + " " + 
       				dateFmt.format(entry.getModification().getDate()) + " " + 
       				entry.getModification().getDate().getTime()); //need long value for uniqueness otherwise time isn't enough
       		p.setName(newName); //mark as deleted - change name incase re-added -unique key
      		p.setForeignName(newName); //clear name incase re-added - unique key
      	    p.setTitle(p.getTitle() + " (" + newName + ")");
       		checkUserTitle(p, updatesCtx);
       	}
    	getProfileDao().delete((Principal)entry);   
    }

    //***********************************************************************************************************    
 
    protected org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, Collection tags) {
    	org.apache.lucene.document.Document indexDoc = super.buildIndexDocumentFromEntry(binder, entry, tags);
    	
		if (entry instanceof User) {
			User user = (User)entry;
			ProfileIndexUtils.addName(indexDoc, user, false);
			ProfileIndexUtils.addEmail(indexDoc, user, false);
			ProfileIndexUtils.addZonName(indexDoc, user, false);
			ProfileIndexUtils.addWorkspaceId(indexDoc, user);
		} else if(entry instanceof Group) {
	        ProfileIndexUtils.addName(indexDoc, (Group)entry, false);	
		} else if(entry instanceof Application) {
	        ProfileIndexUtils.addName(indexDoc, (Application)entry, false);	
		} else if(entry instanceof ApplicationGroup) {
	        ProfileIndexUtils.addName(indexDoc, (ApplicationGroup)entry, false);			
		}
        ProfileIndexUtils.addReservedId(indexDoc, (Principal)entry, false);	
		
       return indexDoc;
    }
    /**
     * Use to synchronize a user with an outside source.
     * Don't index if not changed - could be on a schedule and don't want to
     * reindex all users unnecessarily
     * Files are not handled here
     */
	public void syncEntry(final Principal entry, final InputDataAccessor inputData, Map options) {
		final Map ctx = new HashMap();
		if (options != null) ctx.putAll(options);
		syncEntry_setCtx(entry, ctx);
		Map entryDataAll = modifyEntry_toEntryData(entry, inputData, null, ctx);
	    final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
	        
        // The following part requires update database transaction.
        Boolean changed = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		boolean result1 = syncEntry_fillIn(entry, inputData, entryData, ctx);
	                
        		boolean result2 = syncEntry_postFillIn(entry, inputData, entryData, ctx);
        		if (result1 || result2) return Boolean.TRUE;
        		return Boolean.FALSE;
        	}});
	    if (changed.equals(Boolean.TRUE)) modifyEntry_indexAdd(entry.getParentBinder(), entry, inputData, null, null, ctx);		
		
	}
	protected void syncEntry_setCtx(Entry entry, Map ctx) {
		modifyEntry_setCtx(entry, ctx);
	}
	protected boolean syncEntry_fillIn(Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
	        for (Iterator iter=entryData.entrySet().iterator(); iter.hasNext();) {
	        	Map.Entry mEntry = (Map.Entry)iter.next();
	        	//need to generate id for the event so its id can be saved in customAttr
	        	if (entry.getCustomAttribute((String)mEntry.getKey()) == null) {
	        			Object obj = mEntry.getValue();
	        			if (obj instanceof Event)
	        				getCoreDao().save(obj);
	        	}
	        }
	        doProfileEntryFillin(entry, inputData, entryData);
	        boolean changed = EntryBuilder.updateEntry(entry, entryData);
	        //don't want to do this unless a real change
	        if (changed) {
	 	       User user = RequestContextHolder.getRequestContext().getUser();
	 	       entry.setModification(new HistoryStamp(user));
	 	       entry.incrLogVersion();
	 	       processChangeLog(entry, ChangeLog.MODIFYENTRY);
	 	       getReportModule().addAuditTrail(AuditType.modify, entry);

	        }
	        return changed;
		
	}

	protected boolean syncEntry_postFillIn(Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
  		if (entry.isTop() && entry.getParentBinder().isUniqueTitles()) getCoreDao().updateTitle(entry.getParentBinder(), entry, 
  				(String)ctx.get(ObjectKeys.FIELD_ENTITY_NORMALIZED_TITLE), entry.getNormalTitle());		
  		if (entry instanceof User) {
  			//need to schedule a reindex of entries with my title
  			checkUserTitle((User)entry, ctx);
  		}
  		return false;		
	}
	/**
	 * Synchronize a list of entries.  The map key is the entry, value
	 * is an InputDataAccessor of updates.  Only index entries that change
	 */
	public void syncEntries(final Map entries, final Map options) {
		if (entries.isEmpty()) return;
	    
        // The following part requires update database transaction.
        Map changedEntries = (Map)getTransactionTemplate().execute(new TransactionCallback() {
        	public Object doInTransaction(TransactionStatus status) {
        		Map changes = new HashMap();
                 for (Iterator i=entries.entrySet().iterator(); i.hasNext();) {
                	 Map.Entry mEntry = (Map.Entry)i.next();
                	 Principal entry = (Principal)mEntry.getKey();
                	 Map ctx = new HashMap();
                	 if (options != null) ctx.putAll(options);
                	 syncEntry_setCtx(entry, ctx);
                	 InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
        	    	
                	 Map entryDataAll = modifyEntry_toEntryData(entry, inputData, null, ctx);
                	 Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
                	 boolean result1 = syncEntry_fillIn(entry, inputData, entryData, ctx);
                	 boolean result2 = syncEntry_postFillIn(entry, inputData, entryData, ctx);
        	    	if (result1 || result2) changes.put(entry, inputData);
       	    } 
        	    return changes;
        	}});
        
	    for (Iterator i=changedEntries.entrySet().iterator(); i.hasNext();) {
	    	Map.Entry mEntry = (Map.Entry)i.next();
	    	Entry entry = (Entry)mEntry.getKey();
	    	InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
	    	modifyEntry_indexAdd(entry.getParentBinder(), entry, inputData, null, null, null);	
	    }
		
	}
    protected Map syncNewEntry_toEntryData(Binder binder, Definition def, InputDataAccessor inputData, Map fileItems, Map ctx) {
    	return addEntry_toEntryData(binder, def, inputData, fileItems, ctx);
    }
    protected Entry syncNewEntry_create(Definition def, Class clazz, Map ctx)  {
    	return addEntry_create(def, clazz, ctx);
    }
    protected void syncNewEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	addEntry_fillIn(binder, entry, inputData, entryData, ctx);
    }
    protected void syncNewEntry_preSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
		addEntry_preSave(binder, entry, inputData, entryData, ctx);
	}
    protected void syncNewEntry_save(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
		//don't change owner - will do in bulk to save all these updates
		addEntry_save(binder, entry, inputData, entryData, ctx);
	}
    protected void syncNewEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
		//don't change owner - will do in bulk to save all these updates
		super.addEntry_postSave(binder, entry, inputData, entryData, ctx);
	}
    protected void syncNewEntry_startWorkflow(Entry entry, Map ctx) {
    	addEntry_startWorkflow(entry, ctx);
    }
   public List syncNewEntries(final Binder binder, final Definition definition, final Class clazz, final List inputAccessors, Map options) {
	   if (inputAccessors.isEmpty()) return new ArrayList();
	   SimpleProfiler.startProfiler("DefaultProfileCoreProcessor.syncNewEntries");
	    // The following part requires update database transaction.
   		final Map ctx = new HashMap();
   		if (options != null) ctx.putAll(options);
		Map<Principal, InputDataAccessor> newEntries = (Map)getTransactionTemplate().execute(new TransactionCallback() {
	        	public Object doInTransaction(TransactionStatus status) {
	        		Map newEntries = new HashMap();
	           		StringBuffer inList = new StringBuffer();
          			for (int i=0; i<inputAccessors.size(); ++i) {
	        			InputDataAccessor inputData = (InputDataAccessor)inputAccessors.get(i);
	        			Map entryDataAll = syncNewEntry_toEntryData(binder, definition, inputData, null, ctx);
	        			Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
	   	        
	        			Entry entry = syncNewEntry_create(definition, clazz, ctx);
	        			//	need to set entry/binder information before generating file attachments
	        			//	Attachments/Events need binder info for AnyOwner
	        			syncNewEntry_fillIn(binder, entry, inputData, entryData, ctx);
	                
	        			syncNewEntry_preSave(binder, entry, inputData, entryData, ctx);    

	        			syncNewEntry_save(binder, entry, inputData, entryData, ctx);      
	       	    		inList.append(entry.getId().toString() + ",");
	       	    	 	                
	        			syncNewEntry_postSave(binder, entry, inputData, entryData, ctx);
	        		
	        			newEntries.put(entry, inputData);
	        			syncNewEntry_startWorkflow(entry, ctx);
	        			ctx.clear();
	        		}
        			inList.deleteCharAt(inList.length()-1);
        			//set creator to user, but do in bulk to reduce database update operations
        			if (definition.getType() == Definition.PROFILE_ENTRY_VIEW)
        				getCoreDao().executeUpdate("Update com.sitescape.team.domain.Principal " +
        					" set creation_principal=id,modification_principal=id where id in (" + inList + ")");

	                return newEntries;
	        	}
	        });
		List<Long> ids = new ArrayList();
		for (Principal p: newEntries.keySet()) {
			ids.add(p.getId());
		}
		getCoreDao().evict(newEntries);

		//since we changed the creation/modification principal need reload
		List<UserPrincipal> ps = getProfileDao().loadUserPrincipals(ids, binder.getZoneId(), false);
		//we don't have any tags yet, so set to null to prevent database lookup 
		ctx.put(ObjectKeys.INPUT_FIELD_TAGS, new ArrayList());
		for (Principal p:ps) {
			addEntry_indexAdd(p.getParentBinder(), p, newEntries.get(p), null, ctx);
		}
		SimpleProfiler.stopProfiler("DefaultProfileCoreProcessor.syncNewEntries");
	    return ps; 
		
	}

    protected String getEntryPrincipalField() {
    	return EntityIndexUtils.DOCID_FIELD;
    }
	public ChangeLog processChangeLog(DefinableEntity entry, String operation) {
		if (entry instanceof Binder) return processChangeLog((Binder)entry, operation);
		ChangeLog changes = new ChangeLog(entry, operation);
		Element element = ChangeLogUtils.buildLog(changes, entry);
		//add principal fields
		Principal prin = (Principal)entry;
		XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_PRINCIPAL_NAME, ObjectKeys.XTAG_TYPE_STRING, prin.getName());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_PRINCIPAL_FOREIGNNAME, prin.getForeignName());
		XmlUtils.addProperty(element, ObjectKeys.XTAG_PRINCIPAL_DISABLED, String.valueOf(prin.isDisabled()));
		if (!Validator.isNull(prin.getInternalId())) {
			element.addAttribute(ObjectKeys.XTAG_ATTRIBUTE_INTERNALID, prin.getInternalId());
		}
		if (prin instanceof User) {
			User user = (User)prin;
			//attributes are available through the definintion builder
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_FIRSTNAME, ObjectKeys.XTAG_TYPE_STRING, user.getFirstName());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_MIDDLENAME, ObjectKeys.XTAG_TYPE_STRING, user.getMiddleName());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_LASTNAME, ObjectKeys.XTAG_TYPE_STRING, user.getLastName());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_ZONNAME, ObjectKeys.XTAG_TYPE_STRING, user.getZonName());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_TIMEZONE, ObjectKeys.XTAG_TYPE_STRING, user.getTimeZone().getID());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_EMAIL, ObjectKeys.XTAG_TYPE_STRING, user.getEmailAddress());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_EMAIL_TEXT, ObjectKeys.XTAG_TYPE_STRING, user.getEmailAddress(User.TEXT_EMAIL));
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_EMAIL_MOBILE, ObjectKeys.XTAG_TYPE_STRING, user.getEmailAddress(User.MOBILE_EMAIL));
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_ORGANIZATION, ObjectKeys.XTAG_TYPE_STRING, user.getOrganization());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_PHONE, ObjectKeys.XTAG_TYPE_STRING, user.getPhone());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_SKYPEID, ObjectKeys.XTAG_TYPE_STRING, user.getSkypeId());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_STATUS, ObjectKeys.XTAG_TYPE_STRING, user.getStatus());

			XmlUtils.addProperty(element, ObjectKeys.XTAG_USER_DISPLAYSTYLE, user.getDisplayStyle());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_USER_LOCALE, user.getLocale());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_USER_PASSWORD, user.getPassword());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_USER_DIGESTSEED, user.getDigestSeed());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_USER_LOGINDATE, user.getLoginDate());

		} else if(prin instanceof Group) {
			Group group = (Group)prin;
			XmlUtils.addProperty(element, ObjectKeys.XTAG_GROUP_MEMBERS, LongIdUtil.getIdsAsString(group.getMembers()));
		} else if(prin instanceof Application) {
			Application application = (Application)prin;
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_APPLICATION_POSTURL, ObjectKeys.XTAG_TYPE_STRING, application.getPostUrl());
		} else if(prin instanceof ApplicationGroup) {
			ApplicationGroup group = (ApplicationGroup)prin;
			XmlUtils.addProperty(element, ObjectKeys.XTAG_APPLICATION_GROUP_MEMBERS, LongIdUtil.getIdsAsString(group.getMembers()));			
		}
		getCoreDao().save(changes);
		return changes;
	}    
}
