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

package com.sitescape.team.module.mail.impl;

import java.beans.BeanInfo;
import java.io.File;
import java.io.StringWriter;
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
import java.util.Properties;

import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;

import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NotificationDef;
import com.sitescape.team.domain.PostingDef;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.Subscription;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WorkflowControlledEntry;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.definition.DefinitionConfigurationBuilder;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.definition.notify.Notify;
import com.sitescape.team.module.definition.notify.NotifyBuilderUtil;
import com.sitescape.team.module.definition.notify.NotifyVisitor;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.mail.EmailFormatter;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.shared.AccessUtils;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.DirPath;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.StringUtil;
import com.sitescape.util.Validator;
/**
 * @author Janet McCann
 *
 */
public class DefaultEmailFormatter extends CommonDependencyInjection implements org.springframework.beans.factory.InitializingBean , EmailFormatter {
    private FolderModule folderModule;
    private BinderModule binderModule;
    protected DefinitionModule definitionModule;
    protected MailModule mailModule;
	private TransformerFactory transFactory = TransformerFactory.newInstance();
	protected Properties velocityProperties;
	protected Map transformers = new HashMap();
    public DefaultEmailFormatter () {
	}
    public void setDefinitionModule(DefinitionModule definitionModule) {
        this.definitionModule = definitionModule;
    }
    public void setFolderModule(FolderModule folderModule) {
    	this.folderModule = folderModule;
    }
    public void setBinderModule(BinderModule binderModule) {
    	this.binderModule = binderModule;
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
   public void setVelocityProperties(Properties velocityProperties) {
        this.velocityProperties = velocityProperties;
    }

   public void afterPropertiesSet() throws Exception {
	    try {
	    	velocityProperties.put("file.resource.loader.path", DirPath.getVelocityDirPath());
	    	velocityProperties.put("file.resource.loader.modificationCheckInterval", "2");
	    	Velocity.init(velocityProperties);
	    } catch (Exception ex) {};

   }
   /**
	 * Determine which users have access to the entry.
	 * Return a map from locale to a collection of email Addresses
	 */
	public Map buildDistributionList(FolderEntry entry, Collection subscriptions, int style) {
		List entries = new ArrayList();
		entries.add(entry);
		Map<User, String[]> userMap = getUserList(entry.getRootFolder(), entries, subscriptions,  style);
		Map languageMap = new HashMap();
		//check access to folder/entry and build lists of users to receive mail
		Set email = new HashSet();
		for (Map.Entry<User, String[]> me:userMap.entrySet()) {
			try {
				User u = me.getKey();
				AccessUtils.readCheck(u, entry);
				email = (Set)languageMap.get(u.getLocale());
				if (email != null) {
					addAddresses(email, u, me.getValue(), style);
				} else {
					email = new HashSet();
					addAddresses(email, u, me.getValue(), style);
					languageMap.put(u.getLocale(), email);
				}
			} catch (Exception ex) {};
		}
		NotificationDef nDef = entry.getRootFolder().getNotificationDef();
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
				
			Locale l = Locale.getDefault();
			Set address = (Set)languageMap.get(l);
			if (address != null) address.addAll(emailSet);
			else languageMap.put(l, emailSet);
		}
		return languageMap;
	}
	private void addAddresses(Set email, User u, String[] types, int style) {
		if (types == null) {  //this happens on a push from the folder
			if (style == Subscription.MESSAGE_STYLE_TXT_EMAIL_NOTIFICATION) {
				//see if there is a text address
				String a = u.getTxtEmailAddress();
				if (Validator.isNull(a)) a = u.getEmailAddress();
				email.add(a);
			} else {
				email.add(u.getEmailAddress());
			}
		} else {
			for (int i=0;i<types.length;++i) {
				email.add(u.getEmailAddress(types[i]));
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
	public List buildDistributionList(Folder folder, Collection entries, Collection subscriptions, int style) {
		List result = new ArrayList();
		Map<User, String[]> userMap = getUserList(folder, entries, subscriptions,  style);
		if (!userMap.isEmpty()) {
			//check access to folder/entry and build lists of users to receive mail
			List checkList = new ArrayList();
			for (Map.Entry<User, String[]> me:userMap.entrySet()) {
				AclChecker check = new AclChecker(me.getKey(), me.getValue());
				check.checkEntries(entries);
				checkList.add(check);
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
	private Map<User, String[]> getUserList(Folder folder, Collection entries, Collection subscriptions, int style) {
		if (folder.getNotificationDef().getStyle() == style) {
			Set userIds = new HashSet();
			Set groupIds = new HashSet();
			for (Iterator iter=folder.getNotificationDef().getDistribution().iterator(); iter.hasNext();) {
				Principal p = (Principal)iter.next();
				if (p.getEntityType().equals(EntityType.group))
					groupIds.add(p.getId());
				else
					userIds.add(p.getId());
			}
			if (folder.getNotificationDef().isTeamOn()) {
				Set teamIds = folder.getTeamMemberIds();
				List team = getProfileDao().loadUserPrincipals(teamIds, folder.getZoneId(), true);
				for (Iterator iter=team.iterator(); iter.hasNext();) {
					Principal p = (Principal)iter.next();
					if (p.getEntityType().equals(EntityType.group))
						groupIds.add(p.getId());
					else
						userIds.add(p.getId());
				}
				
			}
			//expand groups so we can remove users
			userIds.addAll(getProfileDao().explodeGroups(groupIds, folder.getZoneId()));
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
				//user may reenable - who knows
				if (notify.hasStyle(style)) {
					userIds.add(notify.getId().getPrincipalId());
					//The first in the list takes priority - should be entry subscription
					if (!userSubs.containsKey(notify.getId().getPrincipalId())) userSubs.put(notify.getId().getPrincipalId(), notify.getStyle(style));
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
	/**
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
			
		Locale l = Locale.getDefault();
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
		Map languageMap = new HashMap();
		languageMap.put(check.getUser().getLocale(), email);
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
					languageMap.put(c.getUser().getLocale(), email);
				}
				checkList.remove(c);
			}
		}
		
		return new Object[] {check.getEntries(), languageMap};
		
	}

	public String getSubject(Folder folder, FolderEntry entry, Notify notify) {
		if (entry == null) {
			String subject = folder.getNotificationDef().getSubject();
			if (Validator.isNull(subject))
				subject = mailModule.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.NOTIFY_SUBJECT);
			//	if not specified, use a localized default
			if (Validator.isNull(subject))
				return NLT.get("notify.subject", notify.getLocale()) + " " + folder.toString();
			return subject;
		} else {
			StringBuffer buf = new StringBuffer();
			buf.append(NLT.get("notify.subject.entry", notify.getLocale()));
			buf.append(":");
			if (checkDate(entry.getCreation(), notify.getStartDate()) > 0) {
				buf.append(NLT.get("notify.newEntry", notify.getLocale()));
			} else if (checkDate(entry.getWorkflowChange(), entry.getModification()) >= 0) {
				buf.append(NLT.get("notify.workflowEntry", notify.getLocale()));
			} else {
				buf.append(NLT.get("notify.modifiedEntry", notify.getLocale()));
			} 
			buf.append(" - ");
			if (Notify.NotifyType.text.equals(notify.getType())) {
				buf.append(entry.toString());
			} else {
				buf.append(folder.toString() + "/" + entry.toString());				
			}
			return buf.toString();
		}
	}
	
	public String getFrom(Folder folder, Notify notify) {
		String from = folder.getNotificationDef().getFromAddress();
		if (Validator.isNull(from))
			from = mailModule.getMailProperty(RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.NOTIFY_FROM);
		return from;
	}

	private Map<User, String[]> getUsers(Collection<Subscription> subscriptions, int style) {
		Map<Long, String[]> userIds = new HashMap();
	
		for (Subscription notify: subscriptions) {
			if (notify.hasStyle(style)) {
				userIds.put(notify.getId().getPrincipalId(), notify.getStyle(style));
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
		if (entry.getCreation() != null && entry.getCreation().getPrincipal() != null) title = entry.getCreation().getPrincipal().getTitle();
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

	public Map buildNotificationMessage(Folder folder, Collection entries,  Notify notify) {
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
		for (Iterator i=entries.iterator();i.hasNext();) {
			parentChain.clear();
			FolderEntry entry = (FolderEntry)i.next();	
			if (!entry.getParentFolder().equals(lastFolder)) {
				fElement = rootElement.addElement("folder"); //build TOC
				fElement.addAttribute("title", entry.getParentFolder().getTitle());
				doFolderDigest(entry.getParentFolder(), entryWriter, notify);
				lastFolder = entry.getParentFolder();
			}
			//make sure change of entries exist from topentry down to changed entry
			//since entries are sorted by sortKey, we should have processed an changed parents
			//already
			FolderEntry parent = entry.getParentEntry();
			while ((parent != null) && (!seenIds.contains(parent.getId()))) {
				parentChain.add(parent);
				parent=parent.getParentEntry();
			}
			for (int pos=parentChain.size()-1; pos>=0; --pos) {
				element = fElement.addElement("folderEntry");
				parent = (FolderEntry)parentChain.get(pos);
				doEntry(element, parent, notify, false);
				doDigestEntry(parent, notify, entryWriter, element);
				seenIds.add(parent.getId());
			}
					
			seenIds.add(entry.getId());
			element = fElement.addElement("folderEntry");
			doEntry(element, entry, notify, true);
			doDigestEntry(entry, notify, entryWriter, element);
		}
		
			
//		result.put(FolderEmailFormatter.PLAIN, doTransform(mailDigest, folder.getZoneName(), MailModule.NOTIFY_TEMPLATE_TEXT, notify.getLocale(), notify.isSummary()));
		doTOC(folder, toc, notify, tocWriter);
		result.put(EmailFormatter.HTML, tocWriter.toString() + entryWriter.toString());
		
		return result;
	}
	protected void doDigestEntry(FolderEntry entry, Notify notify, StringWriter writer, Element element) {
		Definition def = entry.getEntryDef();
		if (def == null) return;
		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();
			Map params = new HashMap();
			params.put("ssElement", element);
			//	Get a list of all of the items in the definition
			Element entryItem = (Element)root.selectSingleNode("//item[@name='entryView']");
			if (entryItem == null) return;
			NotifyBuilderUtil.buildElements(entry, entryItem, notify, writer, params, this.definitionBuilderConfig, true);
		}
	}
	protected void doTOC(Folder folder, Document document, Notify notifyDef, StringWriter writer) {
		try {
		    VelocityContext ctx = new VelocityContext();
           	NotifyVisitor visitor = new NotifyVisitor(folder, notifyDef, null, writer, null, definitionBuilderConfig);
			ctx.put("ssDocument", document);
			ctx.put("ssVisitor", visitor);
			Velocity.mergeTemplate("digestTOC.vtl", ctx, writer);
		} catch (Exception ex) {
			NotifyBuilderUtil.logger.error("Error processing template", ex);
		}
		
	}
	protected void doFolderDigest(Folder folder, StringWriter writer, Notify notifyDef) {
		try {
		    VelocityContext ctx = new VelocityContext();
           	NotifyVisitor visitor = new NotifyVisitor(folder, notifyDef, null, writer, null, definitionBuilderConfig);
			ctx.put("ssVisitor", visitor);
			Velocity.mergeTemplate("folder.vtl", ctx, writer);
		} catch (Exception ex) {
			NotifyBuilderUtil.logger.error("Error processing template", ex);
		}

	}
	public Map buildNotificationMessage(Folder folder, FolderEntry entry,  Notify notify) {
	    Map result = new HashMap();
	    if (notify.getStartDate() == null) return result;

		final StringWriter writer = new StringWriter();
		doEntry(entry, notify, writer);
		result.put(EmailFormatter.HTML, writer.toString());
		
		return result;

	}
	protected void doEntry(FolderEntry entry, Notify notify, StringWriter writer) {
		if (entry == null) return;
		doEntry(entry.getParentEntry(), notify, writer); 
		Definition def = entry.getEntryDef();
		if (def == null) return;
		Document definitionTree = def.getDefinition();
		if (definitionTree != null) {
			Element root = definitionTree.getRootElement();

			//	Get a list of all of the items in the definition
			Element entryItem = (Element)root.selectSingleNode("//item[@name='entryView']");
			if (entryItem == null) return;
			NotifyBuilderUtil.buildElements(entry, entryItem, notify, writer, null, this.definitionBuilderConfig, true);
		}
	}
	protected class AclChecker {
		private User user;
		private String [] emails;
		//keep an ordered list for easier comparision
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
