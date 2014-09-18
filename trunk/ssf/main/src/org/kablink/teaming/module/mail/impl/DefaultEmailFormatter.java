/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.mail.impl;

import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Entry;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NotificationDef;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.Subscription;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.WorkflowControlledEntry;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.definition.DefinitionConfigurationBuilder;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.definition.DefinitionUtils;
import org.kablink.teaming.module.definition.notify.Notify;
import org.kablink.teaming.module.definition.notify.NotifyBuilderUtil;
import org.kablink.teaming.module.definition.notify.NotifyVisitor;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.mail.EmailFormatter;
import org.kablink.teaming.module.mail.EmailUtil;
import org.kablink.teaming.module.mail.MailModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.smtp.SMTPManager;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.EmailHelper;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;

/**
 * ?
 *  
 * @author Janet McCann
 */
@SuppressWarnings("unchecked")
public class DefaultEmailFormatter extends CommonDependencyInjection implements EmailFormatter {
	public static Log logger = LogFactory.getLog(DefaultEmailFormatter.class);
    protected DefinitionModule definitionModule;
    protected MailModule mailModule;
	private BinderModule binderModule;
	protected Map transformers = new HashMap();
    public DefaultEmailFormatter () {
	}
    public void setDefinitionModule(DefinitionModule definitionModule) {
        this.definitionModule = definitionModule;
    }
 	public void setMailModule(MailModule mailModule) {
		this.mailModule = mailModule;
	}
	private IcalModule icalModule;
	public IcalModule getIcalModule() {
		return icalModule;
	}
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}	
	private DefinitionConfigurationBuilder definitionBuilderConfig;
	public DefinitionConfigurationBuilder getDefinitionBuilderConfig() {
        return definitionBuilderConfig;
    }
    public void setDefinitionBuilderConfig(DefinitionConfigurationBuilder definitionBuilderConfig) {
        this.definitionBuilderConfig = definitionBuilderConfig;
    }
    private ZoneModule zoneModule;
    public ZoneModule getZoneModule() {
    	return zoneModule;
    }
    public void setZoneModule(ZoneModule zoneModule) {
    	this.zoneModule = zoneModule;
    }
	private SMTPManager smtpService;
	public void setSmtpService(SMTPManager smtpService) {
		this.smtpService = smtpService;
	}
	public SMTPManager getSmtpService()
	{
		return smtpService;
	}

    /**
     * 
     */
    public BinderModule getBinderModule()
    {
    	return binderModule;
    }

	/**
	 * 
	 */
    public void setBinderModule( BinderModule binderModule )
    {
    	this.binderModule = binderModule;
    }
    
   /**
	 * Determine which users have access to the entry.
	 * Return a map from locale to a collection of email Addresses
	 */
	@Override
	public Map<Locale, Collection> buildDistributionList(Entry entry, Collection subscriptions, int style, boolean redacted) {
		FolderEntry fEntry = (FolderEntry)entry;
		List entries = new ArrayList();
		entries.add(entry);
		Map<User, String[]> userMap = getUserList(fEntry.getRootFolder(), entries, subscriptions, style, redacted);
		Map languageMap = new HashMap();
		//check access to folder/entry and build lists of users to receive mail
		Set email = new HashSet();
		for (Map.Entry<User, String[]> me:userMap.entrySet()) {
			try {
				User u = me.getKey();
				AccessUtils.readCheck(u, entry);
				boolean limitedView = Utils.canUserOnlySeeCommonGroupMembers(u);
				if ((redacted && limitedView) || (!redacted && !limitedView)) {
					email = (Set)languageMap.get(u.getLocale());
					if (email != null) {
						addAddresses(email, u, me.getValue(), style);
					} else {
						email = new HashSet();
						addAddresses(email, u, me.getValue(), style);
						if (0 < email.size()) {
							languageMap.put(u.getLocale(), email);
						}
					}
				}
			} catch (Exception ex) {};
		}
		
		if (!redacted) {
			NotificationDef nDef = fEntry.getRootFolder().getNotificationDef();
			if (nDef.getStyle() == style) {
				//add in email address only subscriptions
		 		String addrs = nDef.getEmailAddress();
		 		if (Validator.isNull(addrs)) return languageMap;
				String [] emailAddrs = StringUtil.split(addrs);
				//done if no-one is interested
				if (emailAddrs.length == 0)  return languageMap;
	
				Set emailSet = new HashSet();
				//add email address listed 
				for (int j=0; j<emailAddrs.length; ++j) {
					if (!Validator.isNull(emailAddrs[j]))
						emailSet.add(emailAddrs[j].trim());
				}
					
				Locale l = NLT.getTeamingLocale();
				Set address = (Set)languageMap.get(l);
				if (address != null) address.addAll(emailSet);
				else languageMap.put(l, emailSet);
			}
		}
		return languageMap;
	}
	private void addAddresses(Set email, User u, String[] types, int style) {
		String a = null;
		String uName = u.getName();
		if (types == null) {  //this happens on a push from the folder
			if (style == Subscription.MESSAGE_STYLE_TXT_EMAIL_NOTIFICATION) {
				//see if there is a text address
				a = u.getTxtEmailAddress();
				if (Validator.isNull(a)) {
					a = u.getEmailAddress();
				}
			} else {
				a = u.getEmailAddress();
			}
			if (Validator.isNull(a)) {
				logger.debug("DefaultEmailFormatter.addAddresses(User '" + uName + "' ignored, no email address):  1");
			} else {
				email.add(a);
			}
		} else {
			int aCount = 0;
			int tCount = types.length;
			for (int i=0;i<tCount;++i) {
				a = u.getEmailAddress(types[i]);
				if (!Validator.isNull(a)) {
					aCount += 1;
					email.add(a);
				}
			}
			if ((0 == aCount) && (0 < tCount)) {
				logger.debug("DefaultEmailFormatter.addAddresses(User '" + uName + "' ignored, no email address):  2");
			}
		}
		
	}
	/**
	 * Determine which users have access to which entries.
	 * Return a list of Object[].  Each Object[0] contains a list of entries,
	 * Object[1] contains a map.  The map maps locales to a list of emailAddress of users
	 * using that locale that have access to the entries.
	 * The list of entries will maintain the order used to do lookup.  This is important
	 * when actually building the digest message	
	 */
	@Override
	public List buildDistributionList(Binder binder, Collection entries, Collection subscriptions, int style, boolean redacted) {
		Folder folder = (Folder)binder;
		List result = new ArrayList();
		Map<User, String[]> userMap = getUserList(folder, entries, subscriptions, style, redacted);
		if (!userMap.isEmpty()) {
			//check access to folder/entry and build lists of users to receive mail
			List checkList = new ArrayList();
			for (Map.Entry<User, String[]> me:userMap.entrySet()) {
				User u = me.getKey();
				boolean limitedView = Utils.canUserOnlySeeCommonGroupMembers(u);
				if ((redacted && limitedView) || (!redacted && !limitedView)) {
					AclChecker check = new AclChecker(u, me.getValue());
					check.checkEntries(entries);
					checkList.add(check);
				}
			}
			//	get a map containing a list of users mapped to a list of entries
			while (!checkList.isEmpty()) {
				Object [] lists = mapEntries(checkList, style);
				result.add(lists);
			}
		}
		if (folder.getNotificationDef().getStyle() == style) {
			//add in email address only subscriptions
			return doEmailAddrs(folder, entries, result);
		}
		return result;
	}
	private Map<User, String[]> getUserList(Folder folder, Collection entries, Collection subscriptions, int style, boolean redacted) {
		if (folder.getNotificationDef().getStyle() == style) {
			Set userIds = new HashSet();
			Set groupIds = new HashSet();
			for (Iterator iter=folder.getNotificationDef().getDistribution().iterator(); iter.hasNext();) {
				Principal p = (Principal)iter.next();
				if (p.getEntityType().equals(EntityType.group)) {
					groupIds.add(p.getId());
				} else {
					boolean limitedView = isPrincipalLimitedView(p);
					if ((redacted && limitedView) || (!redacted && !limitedView)) {
						userIds.add(p.getId());
					}
				}
			}
			if (folder.getNotificationDef().isTeamOn()) {
				Set teamIds = getBinderModule().getTeamMemberIds( folder );
				List team = getProfileDao().loadUserPrincipals(teamIds, folder.getZoneId(), true);
				for (Iterator iter=team.iterator(); iter.hasNext();) {
					Principal p = (Principal)iter.next();
					if (p.getEntityType().equals(EntityType.group)) {
						groupIds.add(p.getId());
					} else {
						boolean limitedView = isPrincipalLimitedView(p);
						if ((redacted && limitedView) || (!redacted && !limitedView)) {
							userIds.add(p.getId());
						}
					}
				}
				
			}
			//expand groups so we can remove users
			boolean sendingToAllUsersIsAllowed = EmailHelper.canSendToAllUsers();
			Set<Long> explodedGroups = getProfileDao().explodeGroups(groupIds, folder.getZoneId(), sendingToAllUsersIsAllowed);
			List<Principal> principals = getProfileDao().loadPrincipals(explodedGroups, folder.getZoneId(), true);
			for (Principal p : principals) {
				if (p.getEntityType().equals(EntityType.user)) {
					boolean limitedView = isPrincipalLimitedView(p);
					if ((redacted && limitedView) || (!redacted && !limitedView)) {
						userIds.add(p.getId());
					}
				}
			}
			Map<Long, String[]> userSubs = new HashMap();
			//Remove users wanting nothing first.  The user could appear 2X in the list, 1 for folder subscription, 1 for entry
			//so process removes first
			for (Subscription notify: (Collection<Subscription>)subscriptions) {
				if (notify.hasStyle(Subscription.DISABLE_ALL_NOTIFICATIONS)) {
					//user wants to disable admin settings
					userIds.remove(notify.getId().getPrincipalId());
				}
			}
			//Add users wanting the same style messages 
			for (Subscription notify: (Collection<Subscription>)subscriptions) {
				// User may re-enable - who knows.
				if (notify.hasStyle(style)) {
					Principal p;
					try {
						p = getProfileDao().loadPrincipal(notify.getId().getPrincipalId(), folder.getZoneId(), true);
					}
					catch (Exception e) {
						logger.error("EXCEPTION:  Could not access user for user list: " + getExMessage(e));
						logger.debug("EXCEPTION", e);
						p = null;
					}
					if ((null != p) && p.getEntityType().equals(EntityType.user)) {
						boolean limitedView = isPrincipalLimitedView(p);
						if ((redacted && limitedView) || (!redacted && !limitedView)) {
							userIds.add(p.getId());
							//The first in the list takes priority - should be entry subscription
							if (!userSubs.containsKey(p.getId())) userSubs.put(p.getId(), notify.getEmailTypes(style));
						}
					}
				} 
			}
			List<User> us = getProfileDao().loadUsers(userIds, folder.getZoneId());
			Map<User, String[]> userMap=new HashMap();
			for (User u:us) {
				userMap.put(u, userSubs.get(u.getId()));
			}
			return userMap;
		} else {
			//done if no-one is interested
			if (subscriptions.isEmpty()) return new HashMap();
			//Users wanting this style messages
			return getUsers(subscriptions, style);
		}
		
	}

	/*
	 * Returns true if a Principal can be cast to a User and that user
	 * can only see common group members and false otherwise.
	 */
	private static boolean isPrincipalLimitedView(Principal p) {
		if (!EntityType.user.equals(p.getEntityType())) return false;
		User pUser;
		try {
			pUser = ((User) p);
		}
		catch (Exception ex) {
			try {
				ProfileDao profileDao = (ProfileDao) SpringContextUtil.getBean("profileDao");
				pUser = (User) profileDao.loadUserPrincipal(p.getId(), RequestContextHolder.getRequestContext().getZoneId(), false);
			} catch (Exception ex2) {
				pUser = null;
				logger.error("DefaultEmailFormatter.isPrincipalLimitedView(EXCEPTION:  Cannot cast '" + p.getId() + "' to User)");
				logger.debug("DefaultEmailFormatter.isPrincipalLimitedView(EXCEPTION)", ex);
			}
		}
		boolean limitedView = ((null == pUser) ? false : Utils.canUserOnlySeeCommonGroupMembers((User)pUser));
		return limitedView;
	}
	
	/*
	 * Add email only subscriptions to the lists
	 */
	private List doEmailAddrs(Folder folder, Collection entries, List result) {
 		NotificationDef nDef = folder.getNotificationDef();
 		String addrs = nDef.getEmailAddress();
 		if (Validator.isNull(addrs)) return result;
		String [] emailAddrs = StringUtil.split(addrs);
		//done if no-one is interested
		if (emailAddrs.length == 0)  return result;

		Set emailSet = new HashSet();
		//add email address listed 
		for (int j=0; j<emailAddrs.length; ++j) {
			if (!Validator.isNull(emailAddrs[j]))
				emailSet.add(emailAddrs[j].trim());
		}
			
		Locale l = NLT.getTeamingLocale();
		//see if an entry already exists for the entire list
		boolean done = false;
		for (int i=0; i<result.size(); ++i) {
			Object[] objs = (Object[])result.get(i);
			List es = (List)objs[0];
			//	if this is the full entry list
			if (es.size() == entries.size()) {
				Map lang = (Map)objs[1];
				Set email = (Set)lang.get(l);
				if (email == null) {
					lang.put(l, emailSet);
				} else {
					email.addAll(emailSet);
				}
				done = true;
				break;
			}
		}
		if (!done) {
			Map lang = new HashMap();
			lang.put(l, emailSet);
			result.add(new Object[] {entries, lang});
		}
		
		return result;
	}
	/**
	 * 
	 * @param checkList
	 * @return List of users, list of entries they all have access to
	 */
	private Object[] mapEntries(List checkList, int style) {
		
		AclChecker check = (AclChecker)checkList.get(0);
		checkList.remove(0);
		Set email = new HashSet();
		//separate into languages
		addAddresses(email, check.getUser(), check.getEmails(), style);
		Map<Locale, Collection> languageMap = new HashMap();
		if (0 < email.size()) {
			languageMap.put(check.getUser().getLocale(), email);
		}
		//make a copy so we can alter original
		List toDo = new ArrayList(checkList);
		//compare the list of entries each user has access to
		for (int i=0; i<toDo.size(); ++i) {
			AclChecker c = (AclChecker)toDo.get(i);
			if (check.compareEntries(c)) {
				email = (Set)languageMap.get(c.getUser().getLocale());
				if (email != null) {
					addAddresses(email, c.getUser(), c.getEmails(), style);
				} else {
					email = new HashSet();
					addAddresses(email, c.getUser(), c.getEmails(), style);
					if (0 < email.size()) {
						languageMap.put(c.getUser().getLocale(), email);
					}
				}
				checkList.remove(c);
			}
		}
		
		return new Object[] {check.getEntries(), languageMap};
		
	}

	@Override
	public String getSubject(Binder binder, Entry entry, Notify notify) {
		if (entry == null) {
			StringBuffer buf = new StringBuffer();
			buf.append("[");
			buf.append(NLT.get("notify.subject.entry", notify.getLocale()));
			buf.append(":");
			buf.append(NLT.get("notify.digest", notify.getLocale()));
			buf.append("] ");
			String subject = binder.getNotificationDef().getSubject();
			if (Validator.isNull(subject)) {
				subject = mailModule.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.NOTIFY_SUBJECT);
			}
			if (Validator.isNull(subject)) {
				subject = binder.getTitle();
			}
			buf.append(subject);
			return buf.toString();
		} else {
			String family = DefinitionUtils.getFamily(entry.getEntryDefDoc());
			if (ObjectKeys.FAMILY_CALENDAR.equals(family) || ObjectKeys.FAMILY_TASK.equals(family)) {
				//subscribers to calendar want the subject to look like ical subject
				return entry.getTitle();
			}
			StringBuffer buf = new StringBuffer();
			buf.append("[");
			buf.append(NLT.get("notify.subject.entry", notify.getLocale()));
			buf.append(":");
			if (checkDate(entry.getCreation(), notify.getStartDate()) > 0) {
				buf.append(NLT.get("notify.newEntry", notify.getLocale()));
			} else if (checkDate(((FolderEntry)entry).getWorkflowChange(), entry.getModification()) >= 0) {
				buf.append(NLT.get("notify.workflowEntry", notify.getLocale()));
			} else {
				buf.append(NLT.get("notify.modifiedEntry", notify.getLocale()));
			} 
			buf.append("] ");
			buf.append(entry.getTitle());
			return buf.toString();
		}
	}
	
	@Override
	public String getFrom(Binder binder, Notify notify) {
		String from = binder.getNotificationDef().getFromAddress();
		if (Validator.isNull(from))
			from = mailModule.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.NOTIFY_FROM);
		return from;
	}

	private Map<User, String[]> getUsers(Collection<Subscription> subscriptions, int style) {
		Map<Long, String[]> userIds = new HashMap();
	
		for (Subscription notify: subscriptions) {
			if (notify.hasStyle(style)) {
				userIds.put(notify.getId().getPrincipalId(), notify.getEmailTypes(style));
			} 
		}
		
 		List<User> users = getProfileDao().loadUsers(userIds.keySet(),  RequestContextHolder.getRequestContext().getZoneId());
 		//return map of users to subscriptions
 		Map<User, String[]> results =new HashMap();
 		for (User u:users) {
 			results.put(u, userIds.get(u.getId()));
 		}
 		return results;
		
	}
	private int checkDate(HistoryStamp dt1, Date dt2) {
		if (dt1 == null) return -1;
		Date date = dt1.getDate();
		if (date == null) return -1;
		return date.compareTo(dt2);
	}
	private int checkDate(HistoryStamp dt1, HistoryStamp dt2) {
		if (dt2 == null) return 1;
		return checkDate(dt1, dt2.getDate());
	}

	protected void doEntry(Element element, FolderEntry entry, Notify notifyDef, boolean hasChanges) {
		HistoryStamp stamp;
		String title = null;
		if (entry.getCreation() != null && entry.getCreation().getPrincipal() != null) {
			Principal p = entry.getCreation().getPrincipal();
			p = Utils.fixProxy(p);
			title = p.getTitle();
		}
		if (Validator.isNull(title)) element.addAttribute("notifyFrom",NLT.get("entry.noTitle", notifyDef.getLocale()));
		else element.addAttribute("notifyFrom", title);
		if (hasChanges) {
			//template will translate these tags
			element.addAttribute("hasChanges", "true");
			if (checkDate(entry.getCreation(), notifyDef.getStartDate()) > 0) {
				element.addAttribute("notifyType", "notify.newEntry");
				stamp = entry.getCreation();
			} else if (checkDate(entry.getWorkflowChange(), entry.getModification()) >= 0) {
				stamp = entry.getWorkflowChange();
				element.addAttribute("notifyType", "notify.workflowEntry");
			} else {
				element.addAttribute("notifyType", "notify.modifiedEntry");
				stamp = entry.getModification();
			} 
		} else {
			stamp = entry.getModification();				
			element.addAttribute("hasChanges", "false");
		}
		if (stamp == null) stamp = new HistoryStamp();
		Principal p = stamp.getPrincipal();
		p = Utils.fixProxy(p);
		title = null;
		if (p != null) title = p.getTitle();
		if (Validator.isNull(title)) element.addAttribute("notifyBy",NLT.get("entry.noTitle", notifyDef.getLocale()));
		else element.addAttribute("notifyBy", title);
		
		Date date = stamp.getDate();
		if (date == null) element.addAttribute("notifyDate", "");
		else element.addAttribute("notifyDate", notifyDef.getDateTimeFormat().format(date));

		element.addAttribute("name", entry.getId().toString());
		element.addAttribute("title", entry.getTitle());			    
		element.addAttribute("docNumber", entry.getDocNumber());			    
		element.addAttribute("docLevel", String.valueOf(entry.getDocLevel()));

	}
	public String getReplyTo(Binder binder) {
		if (binder.getPostingEnabled() && 
				getCoreDao().loadZoneConfig(binder.getZoneId()).getMailConfig().isSimpleUrlPostingEnabled() &&
				getSmtpService().isEnabled()) {
			List<SimpleName> names = getCoreDao().loadSimpleNames(binder.getId(), binder.getZoneId());
			for (SimpleName name:names) {
				if (Validator.isNotNull(name.getEmailAddress())) {
					String hostname = getZoneModule().getVirtualHost(RequestContextHolder.getRequestContext().getZoneName());
					if(Validator.isNull(hostname)) {
						try {
					        InetAddress addr = InetAddress.getLocalHost();
					        // Get hostname
					        hostname = addr.getHostName();
					    } catch (UnknownHostException e) {
							hostname = "localhost";
					    }
					}
					return name.getEmailAddress()+ "@" + hostname;
				}
			}
		}
		//check old posting
		if (binder.getPosting() != null && binder.getPosting().isEnabled()) return binder.getPosting().getEmailAddress();
		return null;
	}
	@Override
	public Map buildMessage(Binder binder, Collection entries,  Notify notify) {
	    Map result = new HashMap();
	    if (notify.getStartDate() == null) return result;
		Set seenIds = new TreeSet();

		Document toc = DocumentHelper.createDocument();		
    	Element rootElement = toc.addElement("toc");
		Element element;
		Folder lastFolder=null;
		Element fElement=null;
		ArrayList parentChain = new ArrayList();
		element = rootElement.addElement("topFolder");
		element.addAttribute("changeCount", String.valueOf(entries.size()));
		final StringWriter entryWriter = new StringWriter();
		final StringWriter tocWriter = new StringWriter();
		final StringWriter entryWriterText = new StringWriter();
		final StringWriter tocWriterText = new StringWriter();
		Map params = new HashMap();
		for (Iterator i=entries.iterator();i.hasNext();) {
			parentChain.clear();
			FolderEntry entry = (FolderEntry)i.next();	
			if (!entry.getParentFolder().equals(lastFolder)) {
				fElement = rootElement.addElement("folder"); //build TOC
				fElement.addAttribute("title", entry.getParentFolder().getTitle());
				doFolderDigest(entry.getParentFolder(), entryWriter, NotifyVisitor.WriterType.HTML, notify);
				doFolderDigest(entry.getParentFolder(), entryWriterText, NotifyVisitor.WriterType.TEXT, notify);
				lastFolder = entry.getParentFolder();
				params.put("ssReplyTo", getReplyTo(lastFolder));
				
			}
			//make sure chain of entries exist from top entry down to changed entry
			//since entries are sorted by sortKey, we should have processed any changed parents
			//already
			FolderEntry parent = entry.getParentEntry();
			while ((parent != null) && (!seenIds.contains(parent.getId()))) {
				parentChain.add(parent);
				parent=parent.getParentEntry();
			}
			for (int pos=parentChain.size()-1; pos>=0; --pos) {
				element = fElement.addElement("folderEntry");
				parent = (FolderEntry)parentChain.get(pos);
				boolean hasChanges = false;
				if (notify.getStartDate() != null) {
					if (parent.getModification().getDate().after(notify.getStartDate())) {
						hasChanges = true;
					}
				}
				doEntry(element, parent, notify, hasChanges);
				params.put("ssElement", element);
				doDigestEntry(parent, notify, entryWriter, NotifyVisitor.WriterType.HTML, params, entries.contains(parent));
				doDigestEntry(parent, notify, entryWriterText, NotifyVisitor.WriterType.TEXT, params, entries.contains(parent));
				seenIds.add(parent.getId());
			}
					
			seenIds.add(entry.getId());
			element = fElement.addElement("folderEntry");
			doEntry(element, entry, notify, true);
			params.put("ssElement", element);
			doDigestEntry(entry, notify, entryWriter, NotifyVisitor.WriterType.HTML, params, entries.contains(entry));
			doDigestEntry(entry, notify, entryWriterText, NotifyVisitor.WriterType.TEXT, params, entries.contains(entry));
		}
		
			
		doTOC((Folder)binder, toc, notify, tocWriter, NotifyVisitor.WriterType.HTML);
		doTOC((Folder)binder, toc, notify, tocWriterText, NotifyVisitor.WriterType.TEXT);
		EmailUtil.putHTML(result, EmailFormatter.HTML, (tocWriter.toString()     + entryWriter.toString()));
		EmailUtil.putText(result, EmailFormatter.TEXT, (tocWriterText.toString() + entryWriterText.toString()));
		
		return result;
	}
	protected void doDigestEntry(FolderEntry entry, Notify notify, StringWriter writer, 
			NotifyVisitor.WriterType type, Map params, Boolean isNewEntry) {
		NotifyBuilderUtil.addVelocityTemplate(entry, notify, writer, type, params, "header.vm");
		params.put("org.kablink.teaming.notify.params.showAvatar", Boolean.TRUE);
		params.put("org.kablink.teaming.notify.params.showAvatarNew", isNewEntry);
		NotifyBuilderUtil.buildElements(entry, notify, writer, type, params);
		NotifyBuilderUtil.addVelocityTemplate(entry, notify, writer, type, params, "footer.vm");
	}
	protected void doTOC(Folder folder, Document document, Notify notifyDef, StringWriter writer, NotifyVisitor.WriterType type) {
		try {
		    VelocityContext ctx = NotifyBuilderUtil.getVelocityContext();
           	NotifyVisitor visitor = new NotifyVisitor(folder, notifyDef, null, writer, type, null);
			ctx.put("ssDocument", document);
			ctx.put("ssVisitor", visitor);
			visitor.processTemplate("digestTOC.vm", ctx);
		} catch (Exception ex) {
			NotifyBuilderUtil.logger.error("EXCEPTION:  Error processing template", ex);
		}
		
	}
	protected void doFolderDigest(Folder folder, StringWriter writer, NotifyVisitor.WriterType type, Notify notifyDef) {
		try {
		    VelocityContext ctx = NotifyBuilderUtil.getVelocityContext();
           	NotifyVisitor visitor = new NotifyVisitor(folder, notifyDef, null, writer, type, null);
			ctx.put("ssVisitor", visitor);
			visitor.processTemplate("folder.vm", ctx);
		} catch (Exception ex) {
			NotifyBuilderUtil.logger.error("EXCEPTION:  Error processing template", ex);
		}

	}
	@Override
	public Map buildMessage(Binder binder, Entry entry,  Notify notify) {
		Map result = new HashMap();
	    if (notify.getStartDate() == null) return result;
		StringWriter writer = new StringWriter();
		Map params = new HashMap();
		params.put("ssReplyTo", getReplyTo(entry.getParentBinder()));
		if (Notify.NotifyType.interactive.equals(notify.getType())) {
			params.put("org.kablink.teaming.notify.params.replies",getFolderDao().loadEntryDescendants((FolderEntry)entry));
			params.put("org.kablink.teaming.notify.params.showAvatar", Boolean.TRUE);
			params.put("org.kablink.teaming.notify.params.showAvatarNew", Boolean.TRUE);

			NotifyBuilderUtil.addVelocityTemplate(entry, notify, writer, NotifyVisitor.WriterType.HTML, params, "header.vm");
			NotifyBuilderUtil.buildElements(entry, notify, writer, NotifyVisitor.WriterType.HTML, params);
			NotifyBuilderUtil.addVelocityTemplate(entry, notify, writer, NotifyVisitor.WriterType.HTML, params, "footer.vm");
			EmailUtil.putHTML(result, EmailFormatter.HTML, writer.toString());
			writer = new StringWriter();
			NotifyBuilderUtil.addVelocityTemplate(entry, notify, writer, NotifyVisitor.WriterType.TEXT, params, "header.vm");
			NotifyBuilderUtil.buildElements(entry, notify, writer, NotifyVisitor.WriterType.TEXT, params);
			NotifyBuilderUtil.addVelocityTemplate(entry, notify, writer, NotifyVisitor.WriterType.TEXT, params, "footer.vm");
			EmailUtil.putText(result, EmailFormatter.TEXT, writer.toString());
			
		} else {
			doEntry((FolderEntry)entry, notify, writer, NotifyVisitor.WriterType.HTML, params);
			EmailUtil.putHTML(result, EmailFormatter.HTML, writer.toString());
			writer = new StringWriter();
			doEntry((FolderEntry)entry, notify, writer, NotifyVisitor.WriterType.TEXT, params);
			EmailUtil.putText(result, EmailFormatter.TEXT, writer.toString());
		}

		
		return result;

	}
	
	private void doEntry(FolderEntry entry, Notify notify, StringWriter writer, NotifyVisitor.WriterType type, 
			Map params) {
		//Build the list of direct ancestors of the changed entry, from the top down 
		List<FolderEntry> parentEntryList = new ArrayList<FolderEntry>();
		FolderEntry parentEntry = entry.getParentEntry();
		while (parentEntry != null) {
			parentEntryList.add(0, parentEntry);
			parentEntry = parentEntry.getParentEntry();
		}
		params.put("org.kablink.teaming.notify.params.relatedEntries", parentEntryList);
		NotifyBuilderUtil.addVelocityTemplate(entry, notify, writer, type, params, "header.vm");
		params.put("org.kablink.teaming.notify.params.showAvatar", Boolean.TRUE);
		params.put("org.kablink.teaming.notify.params.showAvatarNew", Boolean.TRUE);
		NotifyBuilderUtil.buildElements(entry, notify, writer, type, params);
		NotifyBuilderUtil.addVelocityTemplate(entry, notify, writer, type, params, "footer.vm");
	}
	
	private String getExMessage(Exception ex) {
		if (Validator.isNotNull(ex.getLocalizedMessage())) return ex.getLocalizedMessage();
		return ex.getMessage();
	}
	
	/**
	 * Build a message for and entry and replies you can see.
	 * Don't use for email notifications which go to multiple users who may not be able to see
	 * the same replies.  This is for interactive sendMail situations where we cannot control
	 * who gets the mail, but do control what the current user can see
	 * 
	 * @param folder
	 * @param entry
	 * @param replies
	 * @param notify
	 * 
	 * @return
	 */
	protected class AclChecker {
		private User user;
		private String [] emails;
		// Keep an ordered list for easier comparison.
		protected List entries = new ArrayList();
		
		protected AclChecker(User user, String[] emails) {
			this.user = user;
			this.emails = emails;
		}
		protected User getUser() {
			return user;
		}
		protected String[] getEmails() {
			return emails;
		}
		protected void checkEntries(Collection entries) {
			for (Iterator iter=entries.iterator(); iter.hasNext(); ) {
				WorkflowControlledEntry e = (WorkflowControlledEntry)iter.next();
				try {
					AccessUtils.readCheck(user, e);
					this.entries.add(e);
				} catch (Exception ex) {};
			}
		}
		protected List getEntries() {
			return entries;
		}
		protected boolean compareEntries(AclChecker c) {
			if (c.entries.size() != entries.size()) return false;
			// address compare is okay, working from same input
			// lists are processed in the same order, so results should be in the same order
			for (int i=0; i<entries.size(); ++i) {
				if (entries.get(i) != c.entries.get(i)) return false;
			}
			return true;
		}
		
	}
}
