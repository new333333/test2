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
package org.kablink.teaming.module.profile.impl;

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
import java.util.Locale;

import org.dom4j.Element;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.calendar.TimeZoneHelper;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.SFQuery;
import org.kablink.teaming.domain.Application;
import org.kablink.teaming.domain.ApplicationGroup;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.ChangeLog;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.GroupPrincipal;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.domain.AuditType;
import org.kablink.teaming.jobs.UserTitleChange;
import org.kablink.teaming.module.binder.impl.AbstractEntryProcessor;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.binder.processor.BinderProcessor;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.ldap.LdapSyncResults.PartialLdapSyncResults;
import org.kablink.teaming.module.profile.index.ProfileIndexUtils;
import org.kablink.teaming.module.profile.processor.ProfileCoreProcessor;
import org.kablink.teaming.module.shared.ChangeLogUtils;
import org.kablink.teaming.module.shared.EntryBuilder;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.module.shared.XmlUtils;
import org.kablink.teaming.util.LongIdUtil;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

/**
 * ?
 * 
 * @author Jong Kim
 */
@SuppressWarnings("unchecked")
public class DefaultProfileCoreProcessor extends AbstractEntryProcessor
	implements ProfileCoreProcessor {
	DateFormat dateFmt = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, NLT.getTeamingLocale() );
	//inside write transaction    
	@Override
	public void deleteBinder(Binder binder, boolean deleteMirroredSource, Map options, boolean skipDbLog) {
		if(logger.isDebugEnabled())
			logger.debug("Deleting binder [" + binder.getPathName() + "]");
		if (!binder.isDeleted()) super.deleteBinder(binder, deleteMirroredSource, options, skipDbLog);
		else {
			//if binder is marked deleted, we are called from cleanup code without a transaction 
			final ProfileBinder pBinder = (ProfileBinder)binder;

			//loop through all entries and record delete
			Boolean done=Boolean.FALSE;
			while (!done) {
				done = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
					@Override
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
	@Override
	public void deleteBinder_delete(Binder binder, boolean deleteMirroredSource, Map ctx) {
		//don't remove from parent, cause needs zone pointer for request context setup on zone delete
		//mark for delete now and continue later
		binder.setDeleted(true);
	} 
	    
	//inside write transaction    
	@Override
	protected void deleteBinder_processFiles(Binder binder, Map ctx) {
		getFileModule().deleteFiles(binder, binder, false, null);
	}

 
       //*******************************************************************/
  	//not supported
	@Override
	public void moveBinder(Binder source, Binder destination, Map options) {
		throw new NotSupportedException("errorcode.notsupported.moveBinder");
	
	}
    //*******************************************************************/
  	//not supported
	public Binder copyBinder(Binder source, Binder destination, InputDataAccessor inputData, Map options)  {
		throw new NotSupportedException("errorcode.notsupported.copyBinder");
	
	}
	@Override
	public boolean checkMoveBinderQuota(Binder source, Binder destination) {
		return false;
	}
	@Override
	public boolean checkMoveEntryQuota(Binder source, Binder destination, FolderEntry entry) {
		return false;
	}
    //*******************************************************************/
    //inside write transaction    
    @Override
	protected void addBinder_fillIn(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
    	super.addBinder_fillIn(parent,binder, inputData, entryData, ctx);
    	Integer type = binder.getDefinitionType();
    	if ((type != null) && ((type.intValue() == Definition.USER_WORKSPACE_VIEW) ||
    			type.intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW)) {
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
    @Override
	protected void addBinder_postSave(Binder parent, Binder binder, InputDataAccessor inputData, Map entryData, Map ctx) {
    	Integer type = binder.getDefinitionType();
    	if ((type != null) && ((type.intValue() == Definition.USER_WORKSPACE_VIEW) ||
    			type.intValue() == Definition.EXTERNAL_USER_WORKSPACE_VIEW)) {
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
   @Override
protected void addEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
        ((Principal)entry).setZoneId(binder.getZoneId());
        doProfileEntryFillin(entry, inputData, entryData);
        super.addEntry_fillIn(binder, entry, inputData, entryData, ctx);
     }
    //inside write transaction
    @Override
	protected void addEntry_postSave(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {
    	//make user the user is owner so create_modify access works
    	if (entry instanceof User) {
    		entry.getCreation().setPrincipal((User)entry);
    		entry.getModification().setPrincipal((User)entry);
    	}
    	super.addEntry_postSave(binder, entry, inputData, entryData, ctx);
    }
       
    //***********************************************************************************************************	
    @Override
	protected void modifyEntry_setCtx(Entry entry, Map ctx) {
    	super.modifyEntry_setCtx(entry, ctx);
    	if (entry instanceof GroupPrincipal) {
    		ctx.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, new HashSet(((GroupPrincipal)entry).getMembers()));
    	}
       	//save search title
    	if (entry instanceof User) {
    		ctx.put(ObjectKeys.FIELD_USER_WS_SEARCHTITLE, ((User)entry).getSearchTitle());
    		ctx.put(ObjectKeys.FIELD_USER_WS_TITLE, ((User)entry).getWSTitle());
    	}
    }
    //inside write transaction
   @Override
protected void modifyEntry_fillIn(Binder binder, Entry entry, InputDataAccessor inputData, Map entryData, Map ctx) {  
    	//see if we have updates to fields not covered by definition build
    	doProfileEntryFillin(entry, inputData, entryData);
    	super.modifyEntry_fillIn(binder, entry, inputData, entryData, ctx);
    }
   //inside write transaction
   @Override
protected void modifyEntry_postFillIn(Binder binder, Entry entry, InputDataAccessor inputData, 
   		Map entryData, Map<FileAttachment,String> fileRenamesTo, Map ctx) {
	   super.modifyEntry_postFillIn(binder, entry, inputData, entryData, fileRenamesTo, ctx);
	   if (entry instanceof User) {
		   //need to schedule a reindex of entries with my title
		   checkUserTitle((User)entry, ctx);
	   }
		   
   }
   @Override
protected void modifyEntry_indexAdd(Binder binder, Entry entry, 
   		InputDataAccessor inputData, List fileUploadItems, 
   		Collection<FileAttachment> filesToIndex, Map ctx) {
	   if((entry instanceof Group) && ((Group)entry).isLdapContainer())
		   return; // Do NOT index container group
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
	   String originalWSTitle = (String)ctx.get(ObjectKeys.FIELD_USER_WS_TITLE);
	   String originalSearchTitle = (String)ctx.get(ObjectKeys.FIELD_USER_WS_SEARCHTITLE);
	   List excludeIds=null;
	   if (!user.getWSTitle().equals(originalWSTitle) || !user.getSearchTitle().equals(originalSearchTitle)) {
		   //need to update user workspace titles and index
		   if (user.getWorkspaceId() != null) {
			   Workspace ws = (Workspace)getCoreDao().load(Workspace.class, user.getWorkspaceId());
			   if (ws != null) {
				   BinderProcessor processor = (BinderProcessor)getProcessorManager().getProcessor(ws, ws.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
				   Map updates = new HashMap();
				   updates.put(ObjectKeys.FIELD_ENTITY_TITLE, user.getWSTitle());
				   updates.put(ObjectKeys.FIELD_WS_SEARCHTITLE, user.getSearchTitle());
				   try {
					   processor.modifyBinder(ws, new MapInputData(updates), null, null, null);
					   excludeIds = new ArrayList();
					   excludeIds.add(ws.getId());
					   //modifyBinder will also reindex direct child binders, so remove them
					   List<Binder> binders = ws.getBinders();
					   for (Binder child:binders) {
						   excludeIds.add(child.getId());
					   }
				   	} catch (WriteFilesException wf) {
			   		} catch (WriteEntryDataException wf) {};
				   
			   }
		   }
	   }
	   //index by title only, need to update those entries
	   if (!user.getTitle().equals((String)ctx.get(ObjectKeys.FIELD_ENTITY_TITLE))) {
		   List entryIds = getCoreDao().loadObjects("select id from org.kablink.teaming.domain.FolderEntry where creation.principal=" +
				   user.getId() + " or modification.principal=" + user.getId(), null);
		   List binderIds = getCoreDao().loadObjects("select id from org.kablink.teaming.domain.Binder where creation.principal=" +
				   	user.getId() + " or modification.principal=" + user.getId(), null);
		   //remove binders already processed in modifyBinder above
		   if (excludeIds != null) binderIds.removeAll(excludeIds);
		   String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		   String jobClass = SZoneConfig.getString(zoneName, "userConfiguration/property[@name='" + UserTitleChange.USER_TITLE_JOB + "']");
		   UserTitleChange job=null;
		   if (Validator.isNotNull(jobClass)) {
			   try {
				   job = (UserTitleChange)ReflectHelper.getInstance(jobClass);
			   } catch (Exception ex) {
				   logger.error("Cannot instantiate UserTitleChange custom class", ex);
			   }
	   		}
	   		if (job == null) { 
	   			String className = SPropsUtil.getString("job.user.title.change.class", "org.kablink.teaming.jobs.DefaultUserTitleChange");
	   			job = (UserTitleChange)ReflectHelper.getInstance(className);
	   		}
			job.schedule(user, binderIds, entryIds); 	
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
           	// Handle the disabled attribute
       		if ( inputData.exists( ObjectKeys.FIELD_PRINCIPAL_DISABLED ) && !entryData.containsKey( ObjectKeys.FIELD_PRINCIPAL_DISABLED ) )
       		{
        		String disabled;
        		
        		disabled = inputData.getSingleValue( ObjectKeys.FIELD_PRINCIPAL_DISABLED );
    			entryData.put( ObjectKeys.FIELD_PRINCIPAL_DISABLED, Boolean.valueOf( disabled ) );
        	} 

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
    			Object lc = inputData.getSingleObject(ObjectKeys.FIELD_USER_LOCALE);
    			if (lc instanceof Locale)
    	   			entryData.put(ObjectKeys.FIELD_USER_LOCALE, lc);
    			else {
					String sVal = inputData.getSingleValue(ObjectKeys.FIELD_USER_LOCALE);
					if (Validator.isNull(sVal)) entryData.put(ObjectKeys.FIELD_USER_LOCALE, null);
					else {
						String[] vals = sVal.split("_");
						if (vals.length == 1) entryData.put(ObjectKeys.FIELD_USER_LOCALE, new Locale(vals[0]));
						else if (vals.length == 2) entryData.put(ObjectKeys.FIELD_USER_LOCALE, new Locale(vals[0], vals[1]));
						else if (vals.length >= 3) entryData.put(ObjectKeys.FIELD_USER_LOCALE, new Locale(vals[0], vals[1], vals[2]));
					}
   			}
    		}
    		// Unfortunately, we don't have the ability to express in the definition that a field value should be in 
    		// lower case. Consequently, we need to special handle these values here to make sure they are converted
    		// to lower case before being stored in the database. 
    		// TODO Enhance definition builder to be able to express this.
    		if(entryData.containsKey(ObjectKeys.FIELD_USER_EMAIL))
    			entryData.put(ObjectKeys.FIELD_USER_EMAIL, ((String)entryData.get(ObjectKeys.FIELD_USER_EMAIL)).toLowerCase());
    		if(entryData.containsKey(ObjectKeys.FIELD_USER_EMAIL_MOBILE))
    			entryData.put(ObjectKeys.FIELD_USER_EMAIL_MOBILE, ((String)entryData.get(ObjectKeys.FIELD_USER_EMAIL_MOBILE)).toLowerCase());
    		if(entryData.containsKey(ObjectKeys.FIELD_USER_EMAIL_TEXT))
    			entryData.put(ObjectKeys.FIELD_USER_EMAIL_TEXT, ((String)entryData.get(ObjectKeys.FIELD_USER_EMAIL_TEXT)).toLowerCase());
    			
    		if (inputData.exists(ObjectKeys.FIELD_USER_EMAIL) && !entryData.containsKey(ObjectKeys.FIELD_USER_EMAIL)) {
    			entryData.put(ObjectKeys.FIELD_USER_EMAIL, inputData.getSingleValue(ObjectKeys.FIELD_USER_EMAIL).toLowerCase());
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_EMAIL_MOBILE) && !entryData.containsKey(ObjectKeys.FIELD_USER_EMAIL_MOBILE)) {
    			entryData.put(ObjectKeys.FIELD_USER_EMAIL_MOBILE, inputData.getSingleValue(ObjectKeys.FIELD_USER_EMAIL_MOBILE).toLowerCase());
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_EMAIL_TEXT) && !entryData.containsKey(ObjectKeys.FIELD_USER_EMAIL_TEXT)) {
    			entryData.put(ObjectKeys.FIELD_USER_EMAIL_TEXT, inputData.getSingleValue(ObjectKeys.FIELD_USER_EMAIL_TEXT).toLowerCase());
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_TIMEZONE) && !entryData.containsKey(ObjectKeys.FIELD_USER_TIMEZONE)) {
    			Object tz = inputData.getSingleObject(ObjectKeys.FIELD_USER_TIMEZONE);
    			if (tz instanceof TimeZone)
    				entryData.put(ObjectKeys.FIELD_USER_TIMEZONE, TimeZoneHelper.fixTimeZone((TimeZone)tz));
    			else {
					String sVal = inputData.getSingleValue(ObjectKeys.FIELD_USER_TIMEZONE);
					if (Validator.isNull(sVal)) entryData.put(ObjectKeys.FIELD_USER_TIMEZONE, null);
					else entryData.put(ObjectKeys.FIELD_USER_TIMEZONE, TimeZoneHelper.fixTimeZone(TimeZone.getTimeZone(sVal)));
    			}
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_PASSWORD) && !entryData.containsKey(ObjectKeys.FIELD_USER_PASSWORD)) {
    			entryData.put(ObjectKeys.FIELD_USER_PASSWORD, inputData.getSingleValue(ObjectKeys.FIELD_USER_PASSWORD));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_SKYPEID) && !entryData.containsKey(ObjectKeys.FIELD_USER_SKYPEID)) {
    			entryData.put(ObjectKeys.FIELD_USER_SKYPEID, inputData.getSingleValue(ObjectKeys.FIELD_USER_SKYPEID));
    		}
    		if (inputData.exists(ObjectKeys.FIELD_USER_TWITTERID) && !entryData.containsKey(ObjectKeys.FIELD_USER_TWITTERID)) {
    			entryData.put(ObjectKeys.FIELD_USER_TWITTERID, inputData.getSingleValue(ObjectKeys.FIELD_USER_TWITTERID));
    		}
        	if ( inputData.exists( ObjectKeys.FIELD_USER_EXT_ACCOUNT_STATE ) && !entryData.containsKey( ObjectKeys.FIELD_USER_EXT_ACCOUNT_STATE ) )
        	{
        		entryData.put( ObjectKeys.FIELD_USER_EXT_ACCOUNT_STATE, inputData.getSingleObject( ObjectKeys.FIELD_USER_EXT_ACCOUNT_STATE ) );
        	}
    	} else if(entry instanceof Application) {
        	if (inputData.exists(ObjectKeys.FIELD_APPLICATION_POST_URL) && !entryData.containsKey(ObjectKeys.FIELD_APPLICATION_POST_URL)) {
    			entryData.put(ObjectKeys.FIELD_APPLICATION_POST_URL, inputData.getSingleValue(ObjectKeys.FIELD_APPLICATION_POST_URL));
        	}
        	if (inputData.exists(ObjectKeys.FIELD_APPLICATION_TIMEOUT) && !entryData.containsKey(ObjectKeys.FIELD_APPLICATION_TIMEOUT)) {
        		String timeout = inputData.getSingleValue(ObjectKeys.FIELD_APPLICATION_TIMEOUT);
    			entryData.put(ObjectKeys.FIELD_APPLICATION_TIMEOUT, Integer.valueOf(timeout));
        	}
        	if (inputData.exists(ObjectKeys.FIELD_APPLICATION_TRUSTED) && !entryData.containsKey(ObjectKeys.FIELD_APPLICATION_TRUSTED)) {
        		String trusted = inputData.getSingleValue(ObjectKeys.FIELD_APPLICATION_TRUSTED);
    			entryData.put(ObjectKeys.FIELD_APPLICATION_TRUSTED, Boolean.valueOf(trusted));
        	}
        	if (inputData.exists(ObjectKeys.FIELD_APPLICATION_MAX_IDLE_TIME) && !entryData.containsKey(ObjectKeys.FIELD_APPLICATION_MAX_IDLE_TIME)) {
        		String maxIdleTime = inputData.getSingleValue(ObjectKeys.FIELD_APPLICATION_MAX_IDLE_TIME);
    			entryData.put(ObjectKeys.FIELD_APPLICATION_MAX_IDLE_TIME, Integer.valueOf(maxIdleTime));
        	}
        	if (inputData.exists(ObjectKeys.FIELD_APPLICATION_SAME_ADDR_POLICY) && !entryData.containsKey(ObjectKeys.FIELD_APPLICATION_SAME_ADDR_POLICY)) {
        		String sameAddrPolicy = inputData.getSingleValue(ObjectKeys.FIELD_APPLICATION_SAME_ADDR_POLICY);
    			entryData.put(ObjectKeys.FIELD_APPLICATION_SAME_ADDR_POLICY, Boolean.valueOf(sameAddrPolicy));
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
        			exists = getCoreDao().loadObjects("from org.kablink.teaming.domain.Principal where zoneId=:zoneId and foreignName in (:plist)", params);
        		else
           			exists = getCoreDao().loadObjects("from org.kablink.teaming.domain.Principal where zoneId=:zoneId and name in (:plist)", params);
        		Set members = new HashSet();
        		for (int x=0;x<exists.size(); ++x) {
				   Principal p = (Principal)exists.get(x);
				   if (p.isActive()) members.add(p);
        		}
	   			entryData.put(ObjectKeys.FIELD_GROUP_PRINCIPAL_MEMBERS, members);
        	}

        	if ( inputData.exists( ObjectKeys.FIELD_GROUP_DYNAMIC ) && !entryData.containsKey( ObjectKeys.FIELD_GROUP_DYNAMIC ) )
        	{
        		entryData.put( ObjectKeys.FIELD_GROUP_DYNAMIC, inputData.getSingleObject( ObjectKeys.FIELD_GROUP_DYNAMIC ) );
        	}

        	if ( inputData.exists( ObjectKeys.FIELD_GROUP_LDAP_QUERY ) && !entryData.containsKey( ObjectKeys.FIELD_GROUP_LDAP_QUERY ) )
        	{
        		entryData.put( ObjectKeys.FIELD_GROUP_LDAP_QUERY, inputData.getSingleValue( ObjectKeys.FIELD_GROUP_LDAP_QUERY ) );
        	}
        	
        	if ( inputData.exists( ObjectKeys.FIELD_GROUP_LDAP_CONTAINER ) && !entryData.containsKey( ObjectKeys.FIELD_GROUP_LDAP_CONTAINER ) )
        	{
        		entryData.put( ObjectKeys.FIELD_GROUP_LDAP_CONTAINER, inputData.getSingleObject( ObjectKeys.FIELD_GROUP_LDAP_CONTAINER ) );
        	}

        	if ( inputData.exists( ObjectKeys.FIELD_GROUP_TYPE ) && !entryData.containsKey( ObjectKeys.FIELD_GROUP_TYPE ) )
        	{
        		entryData.put( ObjectKeys.FIELD_GROUP_TYPE, inputData.getSingleObject( ObjectKeys.FIELD_GROUP_TYPE ) );
        	}
    	}
    	
    	if(entry instanceof UserPrincipal) {
    		if(((UserPrincipal) entry).getIdentityInfo() == null) {
    			IdentityInfo identityInfo = (IdentityInfo)inputData.getSingleObject(ObjectKeys.FIELD_USER_PRINCIPAL_IDENTITY_INFO);
    			if(identityInfo == null)
    				identityInfo = new IdentityInfo(); // Create one with default settings
    			((UserPrincipal) entry).setIdentityInfo(identityInfo);
    		}
    	}
    	
   		if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_THEME) && !entryData.containsKey(ObjectKeys.FIELD_PRINCIPAL_THEME)) {
			entryData.put(ObjectKeys.FIELD_PRINCIPAL_THEME, inputData.getSingleValue(ObjectKeys.FIELD_PRINCIPAL_THEME));
		}
   		if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME) && !entryData.containsKey(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME)) {
    		entryData.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, inputData.getSingleValue(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME).toLowerCase());
    	} 
       	if (inputData.exists(ObjectKeys.FIELD_PRINCIPAL_NAME) && !entryData.containsKey(ObjectKeys.FIELD_PRINCIPAL_NAME)) {
    		entryData.put(ObjectKeys.FIELD_PRINCIPAL_NAME, inputData.getSingleValue(ObjectKeys.FIELD_PRINCIPAL_NAME));
    	}

   		if ( inputData.exists( ObjectKeys.FIELD_PRINCIPAL_TYPELESS_DN ) && !entryData.containsKey( ObjectKeys.FIELD_PRINCIPAL_TYPELESS_DN ) )
   		{
   			String value;
   			
   			value = inputData.getSingleValue( ObjectKeys.FIELD_PRINCIPAL_TYPELESS_DN );
   			if ( value != null )
   				value = value.toLowerCase();
   			
   			entryData.put( ObjectKeys.FIELD_PRINCIPAL_TYPELESS_DN, value );
   		}

       	// Handle the ldapGuid attribute
   		if (inputData.exists( ObjectKeys.FIELD_PRINCIPAL_LDAPGUID ) && !entryData.containsKey( ObjectKeys.FIELD_PRINCIPAL_LDAPGUID ) )
   		{
    		entryData.put( ObjectKeys.FIELD_PRINCIPAL_LDAPGUID, inputData.getSingleValue( ObjectKeys.FIELD_PRINCIPAL_LDAPGUID ) );
    	} 

       	// Handle the objectSid attribute
   		if ( inputData.exists( ObjectKeys.FIELD_PRINCIPAL_OBJECTSID ) && !entryData.containsKey( ObjectKeys.FIELD_PRINCIPAL_OBJECTSID ) )
   		{
    		entryData.put( ObjectKeys.FIELD_PRINCIPAL_OBJECTSID, inputData.getSingleValue( ObjectKeys.FIELD_PRINCIPAL_OBJECTSID ) );
    	} 

       	// Handle the samAccountName attribute
   		if ( inputData.exists( ObjectKeys.FIELD_PRINCIPAL_SAM_ACCOUNT_NAME ) && !entryData.containsKey( ObjectKeys.FIELD_PRINCIPAL_SAM_ACCOUNT_NAME ) )
   		{
    		entryData.put( ObjectKeys.FIELD_PRINCIPAL_SAM_ACCOUNT_NAME, inputData.getSingleValue( ObjectKeys.FIELD_PRINCIPAL_SAM_ACCOUNT_NAME ).toLowerCase() );
    	} 

       	// Handle the domainName attribute
   		if ( inputData.exists( ObjectKeys.FIELD_PRINCIPAL_DOMAIN_NAME ) && !entryData.containsKey( ObjectKeys.FIELD_PRINCIPAL_DOMAIN_NAME ) )
   		{
    		entryData.put( ObjectKeys.FIELD_PRINCIPAL_DOMAIN_NAME, inputData.getSingleValue( ObjectKeys.FIELD_PRINCIPAL_DOMAIN_NAME ) );
    	}

        // Handle the netbiosName attribute
   		if ( inputData.exists( ObjectKeys.FIELD_PRINCIPAL_NETBIOS_NAME ) && !entryData.containsKey( ObjectKeys.FIELD_PRINCIPAL_NETBIOS_NAME ) )
   		{
    		entryData.put( ObjectKeys.FIELD_PRINCIPAL_NETBIOS_NAME, inputData.getSingleValue( ObjectKeys.FIELD_PRINCIPAL_NETBIOS_NAME ) );
    	}

   		String name = (String)entryData.get(ObjectKeys.FIELD_PRINCIPAL_NAME);
       	if (Validator.isNotNull(name)) {
       		//remove blanks
          	name = name.trim();
          	if (!name.equals(((Principal)entry).getName())) {
          		entryData.put(ObjectKeys.FIELD_PRINCIPAL_NAME, name.toLowerCase());
          		//setting the name -  force foreign name to be same if not supplied
          		//preserve case on foreign name
               	String foreignName = (String)entryData.get(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME);
          		if (Validator.isNull(foreignName)) entryData.put(ObjectKeys.FIELD_PRINCIPAL_FOREIGNNAME, name.toLowerCase()); 
          	}
       	}
       	
    }
    //***********************************************************************************************************
    
   	@Override
	protected boolean indexEntries_validate(Binder binder, Entry entry) {
   		// DRF (20150323):  As of a change I'm making for Inverness,
   		//    we now index all entries, including the Job Processing
   		//    agent.  Prior to this change, if entry was the Job
   		//    Processing agent, we returned false here.
   		return true;
   	}
   	@Override
	protected void indexEntries_load(Binder binder, List entries)  {
   		// Bulk load any collections that need to be indexed.
   		getProfileDao().bulkLoadCollections((List<Principal>)entries);
   	}
	@Override
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
    @Override
	protected void deleteEntry_delete(Binder parentBinder, Entry entry, Map ctx) {
    	
       	if (entry instanceof User) {
       		User p = (User)entry;
       		//mark deleted, cause their ids are used all over
       		//profileDao will delete all associations and groups
       		if (!p.isDeleted()) {
	       		p.setDeleted(true);
	      		Map updatesCtx = new HashMap();
	       		updatesCtx.put(ObjectKeys.FIELD_ENTITY_TITLE, p.getTitle());
	       		String newName = Validator.replacePathCharacters(NLT.get("profile.deleted.label") + " " + 
	       				dateFmt.format(entry.getModification().getDate()) + " " + 
	       				entry.getModification().getDate().getTime()); //need long value for uniqueness otherwise time isn't enough
	       		p.setName(newName); //mark as deleted - change name incase re-added -unique key
	      		p.setForeignName(newName); //clear name incase re-added - unique key
	      	    p.setTitle(p.getTitle() + " (" + newName + ")");
	      	    p.setLdapGuid( "" );
	       		checkUserTitle(p, updatesCtx);
       		}
       	}
       	
    	getProfileDao().delete((Principal)entry);   
    }

    //***********************************************************************************************************    
 
    @Override
	public org.apache.lucene.document.Document buildIndexDocumentFromEntry(Binder binder, Entry entry, Collection tags) {
    	org.apache.lucene.document.Document indexDoc = super.buildIndexDocumentFromEntry(binder, entry, tags);
    	
		if (entry instanceof User) {
			User user = (User)entry;
			ProfileIndexUtils.addName(indexDoc, user, false);
			ProfileIndexUtils.addWorkspaceId(indexDoc, user);
			ProfileIndexUtils.addAvatarId(indexDoc, user);
			ProfileIndexUtils.addPersonFlag(indexDoc, user);
			ProfileIndexUtils.addIdentityInfo(indexDoc, user);
			ProfileIndexUtils.addEmail(indexDoc, user);
			ProfileIndexUtils.addDisabled(indexDoc, user);
			ProfileIndexUtils.addSiteAdmin(indexDoc, user);
			ProfileIndexUtils.addIsLdapContainer( indexDoc, (User)entry );
			ProfileIndexUtils.addIsTeamGroup( indexDoc, (User)entry );
		} else if(entry instanceof Group) {
	        ProfileIndexUtils.addName(indexDoc, (Group)entry, false);	
	        ProfileIndexUtils.addDynamic(indexDoc, (Group)entry, false);	
			ProfileIndexUtils.addIdentityInfo( indexDoc, (UserPrincipal)entry );
			ProfileIndexUtils.addDisabled(indexDoc, (Group)entry);
			ProfileIndexUtils.addIsLdapContainer( indexDoc, (Group)entry );
			ProfileIndexUtils.addIsFromLdap( indexDoc, (Group)entry );
			ProfileIndexUtils.addIsTeamGroup( indexDoc, (Group)entry );
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
	@Override
	public void syncEntry(final Principal entry, final InputDataAccessor inputData, Map options) {
		final Map ctx = new HashMap();
		if (options != null) ctx.putAll(options);
		syncEntry_setCtx(entry, ctx);
		Map entryDataAll = modifyEntry_toEntryData(entry, inputData, null, ctx);
	    final Map entryData = (Map) entryDataAll.get(ObjectKeys.DEFINITION_ENTRY_DATA);
	        
        // The following part requires update database transaction.
	    Boolean changed = null;
        int tryMaxCount = 1 + SPropsUtil.getInt("select.database.transaction.retry.max.count", ObjectKeys.SELECT_DATABASE_TRANSACTION_RETRY_MAX_COUNT);
        int tryCount = 0;
        while(true) {
        	tryCount++;
        	try {
		        changed = (Boolean)getTransactionTemplate().execute(new TransactionCallback() {
		        	@Override
					public Object doInTransaction(TransactionStatus status) {
		        		boolean result1 = syncEntry_fillIn(entry, inputData, entryData, ctx);
			                
		        		boolean result2 = syncEntry_postFillIn(entry, inputData, entryData, ctx);
		        		if (result1 || result2) return Boolean.TRUE;
		        		return Boolean.FALSE;
		        	}});
		        break; // successful transaction
        	}
        	catch(HibernateOptimisticLockingFailureException e) {
        		if(tryCount < tryMaxCount) {
        			if(logger.isDebugEnabled())
        				logger.warn("(" + tryCount + ") 'sync entry' failed due to optimistic locking failure - Retrying in new transaction", e);
        			else 
        				logger.warn("(" + tryCount + ") 'sync entry' failed due to optimistic locking failure - Retrying in new transaction: " + e.toString());
        			getCoreDao().refresh(entry);        		
        		}
        		else {
    				logger.error("(" + tryCount + ") 'sync entry' failed due to optimistic locking failure - Aborting", e);
        			throw e;
        		}
        	}
        }
        if(Boolean.TRUE.equals(changed)) 
	    	modifyEntry_indexAdd(entry.getParentBinder(), entry, inputData, null, null, ctx);		
		
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
	 * is an InputDataAccessor of updates.  Only index entries that change.
	 * Store the list of entries that were sync'd in syncResults.
	 */
	@Override
	public Map syncEntries(
		final Map entries,
		final Map options,
		PartialLdapSyncResults syncResults ) {
		if (entries.isEmpty()) return null;
	    
        // The following part requires update database transaction.
        Map changedEntries = (Map)getTransactionTemplate().execute(new TransactionCallback() {
        	@Override
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
        	    	if (result1 || result2)
        	    	{
        	    		changes.put(entry, inputData);
        	    	}
       	    } 
        	    return changes;
        	}});
        
	    for (Iterator i=changedEntries.entrySet().iterator(); i.hasNext();) {
	    	Map.Entry mEntry = (Map.Entry)i.next();
	    	Entry entry = (Entry)mEntry.getKey();
	    	InputDataAccessor inputData = (InputDataAccessor)mEntry.getValue();
	    	modifyEntry_indexAdd(entry.getParentBinder(), entry, inputData, null, null, null);
	    	
	    	// Add this entry to the sync results.
	    	if ( syncResults != null && entry instanceof Principal )
	    	{
	    		Principal principal;
	    		
	    		principal = (Principal) entry;
	    		syncResults.addResult( principal.getName() + " (" + principal.getForeignName() + ")" );
	    	}
	    }
		
	    return changedEntries;
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
    
    /**
     * Store the results of the sync in syncResults.
     * @param binder
     * @param definition
     * @param clazz
     * @param inputAccessors
     * @param options
     * @param syncResults
     * @return
     */
    @Override
    public List syncNewEntries(
    	final Binder binder,
    	final Definition definition,
    	final Class clazz,
    	final List inputAccessors,
    	Map options,
    	PartialLdapSyncResults syncResults,
    	final IdentityInfo identityInfo) {
	   if (inputAccessors.isEmpty()) return new ArrayList();
	   SimpleProfiler.start("DefaultProfileCoreProcessor.syncNewEntries");
	    // The following part requires update database transaction.
   		final Map ctx = new HashMap();
   		if (options != null) ctx.putAll(options);
		Map<Principal, InputDataAccessor> newEntries = (Map)getTransactionTemplate().execute(new TransactionCallback() {
	        	@Override
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

	        			if(entry instanceof UserPrincipal) 
	        				((UserPrincipal)entry).setIdentityInfo(identityInfo);
	        			
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
        				getCoreDao().executeUpdate("Update org.kablink.teaming.domain.Principal " +
        					" set creation_principal=id,modification_principal=id where id in (" + inList + ")");

	                return newEntries;
	        	}
	        });
		List<Long> ids = new ArrayList();
		for (Principal p: newEntries.keySet()) {
			ids.add(p.getId());
			
			// Add this entry to the sync results.
			if ( syncResults != null )
			{
				syncResults.addResult( p.getName() + " (" + p.getForeignName() + ")" );
			}
		}
		getCoreDao().evict(newEntries);

		//since we changed the creation/modification principal need reload
		List<UserPrincipal> ps = getProfileDao().loadUserPrincipals(ids, binder.getZoneId(), false);
		//we don't have any tags yet, so set to null to prevent database lookup 
		ctx.put(ObjectKeys.INPUT_FIELD_TAGS, new ArrayList());
		for (Principal p:ps) {
			addEntry_indexAdd(p.getParentBinder(), p, newEntries.get(p), null, ctx);
		}
		SimpleProfiler.stop("DefaultProfileCoreProcessor.syncNewEntries");
	    return ps; 
		
	}

    @Override
	protected String getEntryPrincipalField() {
    	return Constants.DOCID_FIELD;
    }
	@Override
	public ChangeLog processChangeLog(DefinableEntity entry, String operation) {
		if (entry instanceof Binder) return processChangeLog((Binder)entry, operation);
		ChangeLog changes = ChangeLogUtils.createAndBuild(entry, operation);
		Element element = changes.getEntityRoot();
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
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_EMAIL_BCC, ObjectKeys.XTAG_TYPE_STRING, user.getEmailAddress(User.BCC_EMAIL));
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_ORGANIZATION, ObjectKeys.XTAG_TYPE_STRING, user.getOrganization());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_PHONE, ObjectKeys.XTAG_TYPE_STRING, user.getPhone());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_SKYPEID, ObjectKeys.XTAG_TYPE_STRING, user.getSkypeId());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_TWITTERID, ObjectKeys.XTAG_TYPE_STRING, user.getTwitterId());
			XmlUtils.addCustomAttribute(element, ObjectKeys.XTAG_USER_STATUS, ObjectKeys.XTAG_TYPE_STRING, user.getStatus());

			XmlUtils.addProperty(element, ObjectKeys.XTAG_USER_DISPLAYSTYLE, user.getDisplayStyle());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_USER_LOCALE, user.getLocale());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_USER_PASSWORD, user.getPassword());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_USER_DIGESTSEED, user.getDigestSeed());
			XmlUtils.addProperty(element, ObjectKeys.XTAG_USER_FIRST_LOGINDATE, user.getFirstLoginDate());

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
		ChangeLogUtils.save(changes);
		return changes;
	}    
	
    @Override
	protected void addEntry_indexAdd(Binder binder, Entry entry, 
    		InputDataAccessor inputData, List fileUploadItems, Map ctx){
    	// As part of the fix for bug 874784, we need to index container groups because we need to
    	// be able to assign a container rights to a net folder.
//    	if((entry instanceof Group) && ((Group)entry).isLdapContainer())
//    		return; // Do NOT index container group
    	
    	super.addEntry_indexAdd(binder, entry, inputData, fileUploadItems, ctx);
    }
    
    @Override
	protected List<Long> indexEntries_getEntryIds(Binder binder) {
   		return getProfileDao().getAllPrincipalIds(binder.getZoneId(), true);	// true -> Include disabled.
    }
    
    @Override
    protected List<Entry> indexEntries_loadEntries(List<Long> ids, Long zoneId) {
    	CoreDao cd = getCoreDao();
    	List<Entry> reply = cd.loadObjects(ids, Principal.class, zoneId);
    	if ((null != reply) && (!(reply.isEmpty()))) {
			// Resolve any proxies.
    		List<Entry> resolved = new ArrayList<Entry>();
    		for (Entry entry:  reply) {
    			if (entry instanceof UserPrincipal) {
    				Long entryId = entry.getId();
    				EntityType et = entry.getEntityType();
	    			if (et.equals(EntityType.user) && (!(entry instanceof User))) {
	    				entry = ((User) cd.load(User.class, entryId));
	    			}
	    			else if (et.equals(EntityType.group) && (!(entry instanceof Group))) {
	    				entry = ((Group) cd.load(Group.class, entryId));
	    			}
	    			else if (et.equals(EntityType.application) && (!(entry instanceof Application))) {
	    				entry = ((Application) cd.load(Application.class, entryId));
	    			}
	    			else if (et.equals(EntityType.applicationGroup) && (!(entry instanceof ApplicationGroup))) {
	    				entry = ((ApplicationGroup) cd.load(ApplicationGroup.class, entryId));
	    			}
    			}
    			resolved.add(entry);
    		}
    		reply = resolved;
    	}
    	return reply;
    }
}
