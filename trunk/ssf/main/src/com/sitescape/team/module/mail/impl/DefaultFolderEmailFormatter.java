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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentSource;
import org.springframework.util.FileCopyUtils;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.context.request.RequestContextUtil;
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
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.DefinitionUtils;
import com.sitescape.team.module.definition.notify.Notify;
import com.sitescape.team.module.definition.notify.NotifyBuilderUtil;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.mail.FolderEmailFormatter;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.shared.AccessUtils;
import com.sitescape.team.module.shared.MapInputData;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.DirPath;
import com.sitescape.team.util.NLT;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.DefinitionHelper;
import com.sitescape.util.GetterUtil;
import com.sitescape.util.Html;
import com.sitescape.util.StringUtil;
import com.sitescape.util.Validator;
/**
 * @author Janet McCann
 *
 */
public class DefaultFolderEmailFormatter extends CommonDependencyInjection implements FolderEmailFormatter {
	private Log logger = LogFactory.getLog(getClass());
    private FolderModule folderModule;
    private BinderModule binderModule;
    protected DefinitionModule definitionModule;
    protected MailModule mailModule;
	private TransformerFactory transFactory = TransformerFactory.newInstance();

	protected Map transformers = new HashMap();
    public DefaultFolderEmailFormatter () {
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
				buf.append(NLT.get("strings.xml.newEntry", notify.getLocale()));
			} else if (checkDate(entry.getWorkflowChange(), entry.getModification()) >= 0) {
				buf.append(NLT.get("strings.xml.workflowEntry", notify.getLocale()));
			} else {
				buf.append(NLT.get("strings.xml.modifiedEntry", notify.getLocale()));
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

	protected void doFolder(Element element, Folder folder) {
		element.addAttribute("name", folder.getId().toString());
		element.addAttribute("title", folder.getTitle());
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getEntityIdentifier().getEntityId().toString());
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, folder.getEntityType().toString());
		element.addAttribute("href", adapterUrl.toString());
		PostingDef post = folder.getPosting();
		if (post != null) {
			element.addAttribute("replyTo", post.getEmailAddress());
		}

	}

	protected void doEntry(final Element element, final FolderEntry entry, final Notify notifyDef, boolean hasChanges) {
		HistoryStamp stamp;
		String title = null;
		if (entry.getCreation() != null && entry.getCreation().getPrincipal() != null) title = entry.getCreation().getPrincipal().getTitle();
		if (Validator.isNull(title)) element.addAttribute("notifyFrom",NLT.get("entry.noTitle", notifyDef.getLocale()));
		else element.addAttribute("notifyFrom", title);
		if (hasChanges) {
			//style sheet will translate these tags
			element.addAttribute("hasChanges", "true");
			if (checkDate(entry.getCreation(), notifyDef.getStartDate()) > 0) {
				element.addAttribute("notifyType", "newEntry");
				stamp = entry.getCreation();
			} else if (checkDate(entry.getWorkflowChange(), entry.getModification()) >= 0) {
				stamp = entry.getWorkflowChange();
				element.addAttribute("notifyType", "workflowEntry");
			} else {
				element.addAttribute("notifyType", "modifiedEntry");
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
		else element.addAttribute("notifyDate", notifyDef.getDateFormat().format(date));

		element.addAttribute("name", entry.getId().toString());
		element.addAttribute("title", entry.getTitle());			    
		element.addAttribute("docNumber", entry.getDocNumber());			    
		element.addAttribute("docLevel", String.valueOf(entry.getDocLevel()));
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, entry.getParentBinder().getId().toString());
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, entry.getEntityType().toString());
		element.addAttribute("href", adapterUrl.toString());
		
		final String fullOrSummaryAttribute = (notifyDef.getType().name());
		
		DefinitionModule.DefinitionVisitor visitor = new DefinitionModule.DefinitionVisitor() {
			public void visit(Element entryElement, Element flagElement, Map args)
			{
				String include = flagElement.attributeValue(fullOrSummaryAttribute);
				if("true".equals(include)) {
					String fieldBuilder = flagElement.attributeValue("notifyBuilder");
					String itemName = entryElement.attributeValue("name");
					String nameValue = DefinitionUtils.getPropertyValue(entryElement, "name");									
					if (Validator.isNull(nameValue)) {nameValue = itemName;}
                    String captionValue;
                    if (!args.containsKey("caption")) {
                        captionValue = DefinitionUtils.getPropertyValue(entryElement, "caption");
                       	if (Validator.isNull(captionValue)) {
                            	captionValue = entryElement.attributeValue("caption");
                        }
                    } else {
                       	captionValue = (String) args.get("caption");
                    }
                    
                    Map selectboxSelections = DefinitionHelper.findSelectboxSelectionsAsMap(entryElement);
                    if (selectboxSelections != null && !selectboxSelections.isEmpty()) {
                    	Map selectboxSelectionsNLTed = new HashMap();
                    	Iterator<Map.Entry<String, String>> selectboxSelectionsIt = selectboxSelections.entrySet().iterator();
                    	while (selectboxSelectionsIt.hasNext()) {
                    		Map.Entry<String, String> selectboxSelEntry = selectboxSelectionsIt.next();
                    		selectboxSelectionsNLTed.put(selectboxSelEntry.getKey(), NLT.getDef(selectboxSelEntry.getValue(), notifyDef.getLocale()));
                    		
                    	}
                    	args.put("_selectboxSelectionsCaptions", selectboxSelectionsNLTed);
                    }
                                      
                    args.put("_caption", NLT.getDef(captionValue, notifyDef.getLocale()));
                    args.put("_itemName", itemName);
                    NotifyBuilderUtil.buildElement(element, notifyDef, entry,
                                    			   nameValue, fieldBuilder, args);
                }
			}
			public String getFlagElementName() { return "notify"; }
		};

		definitionModule.walkDefinition(entry, visitor, null);	
	}
	// get cached template.  If not cached yet,load it
	protected Transformer getTransformer(String zoneName, String type) throws TransformerConfigurationException {
		//convert mail templates into cached transformer temlates
		Templates trans;
		trans = (Templates)transformers.get(zoneName + ":" + type);
		if (trans == null) {
			String templateName = mailModule.getMailProperty(zoneName, type);
			Source xsltSource = new StreamSource(new File(DirPath.getXsltDirPath(),templateName));
			trans = transFactory.newTemplates(xsltSource);
			//replace name with actual template
			if (GetterUtil.getBoolean(mailModule.getMailProperty(zoneName, MailModule.Property.NOTIFY_TEMPLATE_CACHE_DISABLED), false) == false)
				transformers.put(zoneName + ":" + type, trans);
		} 
		return trans.newTransformer();
	}

	protected String doTransform(Document document, String zoneName, String type, Locale locale, Notify.NotifyType notifyType) {
		StreamResult result = new StreamResult(new StringWriter());
		try {
			Transformer trans = getTransformer(zoneName, type);
			trans.setParameter("Lang", locale.toString());
//			trans.setParameter("TOC", Boolean.valueOf(oneEntry).toString());
			trans.transform(new DocumentSource(document), result);
		} catch (Exception ex) {
			return ex.getLocalizedMessage()==null? ex.getMessage():ex.getLocalizedMessage();
		}
		return result.getWriter().toString();
	}

	public Map buildNotificationMessage(Folder folder, Collection entries,  Notify notify) {
	    Map result = new HashMap();
	    if (notify.getStartDate() == null) return result;
		Set seenIds = new TreeSet();
		Document mailDigest = DocumentHelper.createDocument();
		
    	Element rootElement = mailDigest.addElement("mail");
       	rootElement.addAttribute("summary", String.valueOf(Notify.NotifyType.summary.equals(notify.getType())));
		Element element;
		Folder lastFolder=null;
		Element fElement=null;
		ArrayList parentChain = new ArrayList();
		element = rootElement.addElement("topFolder");
		element.addAttribute("changeCount", String.valueOf(entries.size()));
      	element.addAttribute("title", folder.getTitle());
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_VIEW_PERMALINK);
		adapterUrl.setParameter(WebKeys.URL_BINDER_ID, folder.getEntityIdentifier().getEntityId().toString());
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, folder.getEntityType().toString());
		element.addAttribute("href", adapterUrl.toString());

		for (Iterator i=entries.iterator();i.hasNext();) {
			parentChain.clear();
			FolderEntry entry = (FolderEntry)i.next();	
			if (!entry.getParentFolder().equals(lastFolder)) {
				fElement = rootElement.addElement("folder");
				doFolder(fElement, entry.getParentFolder());
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
				seenIds.add(parent.getId());
			}
					
			seenIds.add(entry.getId());
			element = fElement.addElement("folderEntry");
			doEntry(element, entry, notify, true);
		}
		
		
//		result.put(FolderEmailFormatter.PLAIN, doTransform(mailDigest, folder.getZoneName(), MailModule.NOTIFY_TEMPLATE_TEXT, notify.getLocale(), notify.isSummary()));
		result.put(FolderEmailFormatter.HTML, doTransform(mailDigest, RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.NOTIFY_TEMPLATE_HTML.getKey(), notify.getLocale(), notify.getType()));
		
		return result;
	}
	public Map buildNotificationMessage(Folder folder, FolderEntry entry,  Notify notify) {
	    Map result = new HashMap();
	    if (notify.getStartDate() == null) return result;
		Document mailDigest = DocumentHelper.createDocument();
		
    	Element rootElement = mailDigest.addElement("mail");
		Element element;
		Element fElement=null;
		ArrayList parentChain = new ArrayList();
		Folder topFolder = folder.getTopFolder();
		if (topFolder == null) topFolder = folder;
		element = rootElement.addElement("topFolder");
     	element.addAttribute("title", folder.getTitle());
		fElement = rootElement.addElement("folder");
		doFolder(fElement, entry.getParentFolder());
 		
		FolderEntry parent = entry.getParentEntry();
		while (parent != null) {
			parentChain.add(parent);
			parent=parent.getParentEntry();
		}
		for (int pos=parentChain.size()-1; pos>=0; --pos) {
			element = fElement.addElement("folderEntry");
			parent = (FolderEntry)parentChain.get(pos);
			doEntry(element, parent, notify, false);
		}
					
		element = fElement.addElement("folderEntry");
		doEntry(element, entry, notify, true);
		
//		result.put(FolderEmailFormatter.PLAIN, doTransform(mailDigest, folder.getZoneName(), MailModule.NOTIFY_TEMPLATE_TEXT, notify.getLocale(), false));
		result.put(FolderEmailFormatter.HTML, doTransform(mailDigest, RequestContextHolder.getRequestContext().getZoneName(), MailModule.Property.NOTIFY_TEMPLATE_HTML.getKey(), notify.getLocale(), notify.getType()));
		
		return result;
	}
	public List postMessages(Folder folder, PostingDef pDef, Message[] msgs, Session session) {
		Object content;
		Map fileItems = new HashMap();
		List iCalendars = new ArrayList();
		Map inputData = new HashMap();
		Definition definition = pDef.getDefinition();
		if (definition == null) definition = folder.getDefaultEntryDef();
		String defId=null;
		if (definition != null) defId = definition.getId();
		InternetAddress from=null;
		String title;
		List errors = new ArrayList();
		Integer option = pDef.getReplyPostingOption();
		if (option == null) option = PostingDef.POST_AS_A_REPLY;
		
		for (int i=0; i<msgs.length; ++i) {
			try {
				from = (InternetAddress)msgs[i].getFrom()[0];
				title = msgs[i].getSubject();
				if (title == null) title = "";
				User fromUser = getFromUser(from);
				RequestContext oldCtx = RequestContextHolder.getRequestContext();
				try {
					//need to setup user context for request
					RequestContextUtil.setThreadContext(fromUser).resolve();
					
					inputData.put(ObjectKeys.INPUT_FIELD_POSTING_FROM, from.toString()); 
					inputData.put(ObjectKeys.FIELD_ENTITY_TITLE, title);
					content = msgs[i].getContent();
				
					if (msgs[i].isMimeType("text/plain")) {
						processText(content, inputData);
					} else if (msgs[i].isMimeType("text/html")) {
						processHTML(content, inputData);
					} else if (msgs[i].isMimeType(MailModule.CONTENT_TYPE_CALENDAR)) {
						processICalendar(content, iCalendars);						
					} else if (content instanceof MimeMultipart) {
						processMime((MimeMultipart)content, inputData, fileItems, iCalendars);
					}
					//parse subject to see if this is a reply
					if (title.startsWith(MailModule.REPLY_SUBJECT)) {
						String flag = MailModule.REPLY_SUBJECT+folder.getId().toString()+":";
						//see if for this folder
						if (title.startsWith(flag)) {
							if (option == PostingDef.RETURN_TO_SENDER) 
						   		throw new NotSupportedException("errorcode.notsupported.postingReplies");
							 							
							String docId = title.substring(flag.length());
							Long id=null;
							int index = docId.indexOf(" ");
							if (index == -1) id=Long.valueOf(docId);
							else id=Long.valueOf(docId.substring(0, index));
							if (option.longValue() == PostingDef.POST_AS_A_REPLY.longValue())
								folderModule.addReply(folder.getId(), id, defId, new MapInputData(inputData), fileItems, null);
							else
								folderModule.addEntry(folder.getId(), defId, new MapInputData(inputData), fileItems, null);
							msgs[i].setFlag(Flags.Flag.DELETED, true);
						}
					} else {
						List entryIdsFromICalendars = new ArrayList();
						
						// process attachments
						Iterator fileItemsIt = fileItems.entrySet().iterator();
						while (fileItemsIt.hasNext()) {
							Map.Entry me = (Map.Entry)fileItemsIt.next();
							FileHandler fileHandler = (FileHandler)me.getValue();
							
							if ((!(fileHandler.getOriginalFilename() != null && fileHandler.getOriginalFilename().toLowerCase().endsWith(MailModule.ICAL_FILE_EXTENSION))) &&
									(!(fileHandler.getContentType() != null && fileHandler.getContentType().toLowerCase().startsWith(MailModule.CONTENT_TYPE_CALENDAR))) ) {
								continue;
							}
							
							try {
								List entryIds = getIcalModule().parseToEntries(folder.getId(), fileHandler.getInputStream());
								entryIdsFromICalendars.addAll(entryIds);
								if (!entryIds.isEmpty()) {
									fileItemsIt.remove();
								}
							} catch (Exception e) {
								// can't import ical, ignore error, it's probably wrong file format
								logger.warn(e);
							}
						}
						
						// process inline iCalendars
						Iterator icalIt = iCalendars.iterator();
						while (icalIt.hasNext()) {
							InputStream icalStream = (InputStream)icalIt.next();
							try {
								List entryIds = getIcalModule().parseToEntries(folder.getId(), icalStream);
								entryIdsFromICalendars.addAll(entryIds);
								if (!entryIds.isEmpty()) {
									icalIt.remove();
								}								
							} catch (Exception e) {
								// can't import ical, ignore error, it's probably wrong file format
								logger.warn(e);
							}
						}
							
						if (!fileItems.isEmpty() || entryIdsFromICalendars.isEmpty()) {
							folderModule.addEntry(folder.getId(), defId, new MapInputData(inputData), fileItems, null);
						}
						msgs[i].setFlag(Flags.Flag.DELETED, true);
					}

				} finally {
					//reset context
					RequestContextHolder.setRequestContext(oldCtx);
					fileItems.clear();
					inputData.clear();
					iCalendars.clear();
					
				}
			} catch (Exception ex) {
				logger.error("Error posting the message from: " + from + " Error: " + (ex.getLocalizedMessage()==null? ex.getMessage():ex.getLocalizedMessage()));
				//if fails and from self, don't reply or we will get it back
				errors.add(postError(pDef, msgs[i], from, ex));
			}
		}
		return errors;
	}
	private Message postError(PostingDef pDef, Message msg, InternetAddress from, Exception error) {
		try {
			msg.setFlag(Flags.Flag.DELETED, true);
			if (!pDef.getEmailAddress().equals(from.getAddress())) {
				String errorMsg = NLT.get("errorcode.postMessage.failed", new Object[]{Html.stripHtml((error.getLocalizedMessage()==null? error.getMessage():error.getLocalizedMessage()))});
				Message reject = msg.reply(false);
				reject.setText(errorMsg);
				reject.setFrom(new InternetAddress(pDef.getEmailAddress()));
				reject.setContent(msg.getContent(), msg.getContentType());
				reject.setSubject(reject.getSubject() + " " + errorMsg); 
				return reject;
			} 
		} catch (Exception ex2) {}
		return null;
	}
	private User getFromUser(InternetAddress from) {
		//try to map email address to a user
		String fromEmail = from.getAddress();	
		List<Principal> ps = getProfileDao().loadPrincipalByEmail(fromEmail, RequestContextHolder.getRequestContext().getZoneId());
		User user = null;
		for (Principal p:ps) {
            //Make sure it is a user
            try {
            	User principal = (User)getProfileDao().loadUser(p.getId(), RequestContextHolder.getRequestContext().getZoneId());
            	if (user == null) user = principal;
            	else if (!principal.equals(user)) {
        			logger.error("Multiple users with same email address, cannot use for incoming email");
        			break;
            	}
            } catch (Exception ignoreEx) {};  
		}
		if (user != null) return user;
		
		return getProfileDao().getReservedUser(ObjectKeys.ANONYMOUS_POSTING_USER_INTERNALID, RequestContextHolder.getRequestContext().getZoneId());
	}
	private void processText(Object content, Map inputData) {
		if (inputData.containsKey(ObjectKeys.FIELD_ENTITY_DESCRIPTION)) return;
		String[] val = new String[1];
		val[0] = (String)content;
		inputData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, val);			
	}
	private void processHTML(Object content, Map inputData) {
		String[] val = new String[1];
		val[0] = (String)content;
		inputData.put(ObjectKeys.FIELD_ENTITY_DESCRIPTION, val);			
	}	
	private void processICalendar(Object content, List iCalendars) throws IOException {
		try {
			iCalendars.add((InputStream)content);
		} catch (ClassCastException e) {
			// ignore
		}
	}
	
	private void processMime(MimeMultipart content, Map inputData, Map fileItems, List iCalendars) throws MessagingException, IOException {
		int count = content.getCount();
		for (int i=0; i<count; ++i ) {
			BodyPart part = content.getBodyPart(i);
			if (part.isMimeType(MailModule.CONTENT_TYPE_CALENDAR)) {
				processICalendar(part.getContent(), iCalendars);
			} else { 
				//old mailers may not use disposition, and instead put the name in the content-type
				//java mail handles this.
				String fileName = part.getFileName();
				if (Validator.isNotNull(fileName)) {
					fileItems.put(ObjectKeys.INPUT_FIELD_ENTITY_ATTACHMENTS + Integer.toString(fileItems.size() + 1), new FileHandler(part));
				} else if (part.isMimeType("text/html")) {
					processHTML(part.getContent(), inputData);
				} else if (part.isMimeType("text/plain")) {
					processText(part.getContent(), inputData);
				} else {
					Object bContent = part.getContent();
					if (bContent instanceof MimeMultipart) processMime((MimeMultipart)bContent, inputData, fileItems, iCalendars);
				}
			}
			
		}
	}
	
	public class FileHandler implements org.springframework.web.multipart.MultipartFile {
		BodyPart part;
		String fileName;
		String type;
		int size;
		
		public FileHandler(BodyPart part) throws MessagingException {
			this.part = part;
			fileName = part.getFileName();
			type = part.getContentType();
			size = part.getSize();
		}
		/**
		 * Return the name of the parameter in the multipart form.
		 * @return the name of the parameter
		 */
		public String getName() {return "attachment";}

		/**
		 * Return whether the uploaded file is empty in the sense that
		 * no file has been chosen in the multipart form.
		 * @return whether the uploaded file is empty
		 */
		public boolean isEmpty() {return false;}
		
		/**
		 * Return the original filename in the client's filesystem.
		 * This may contain path information depending on the browser used,
		 * but it typically will not with any other than Opera.
		 * @return the original filename, or null if empty
		 */
		public String getOriginalFilename() {return fileName;}
		
		
		/**
		 * Return the content type of the file.
		 * @return the content type, or null if empty or not defined
		 */
		public String getContentType() {return type;}

		/**
		 * Return the size of the file in bytes.
		 * @return the size of the file, or 0 if empty
		 */
		public long getSize() {return size;}
		
		/**
		 * Return the contents of the file as an array of bytes.
		 * @return the contents of the file as bytes,
		 * or an empty byte array if empty
		 * @throws IOException in case of access errors
		 * (if the temporary store fails)
		 */
		public byte[] getBytes() throws IOException {
			byte [] results = new byte[size];
			try {
				part.getInputStream().read(results);
			} catch (MessagingException me) {
				throw new IOException(me.getLocalizedMessage()==null? me.getMessage():me.getLocalizedMessage());
			}
			return results;
		}

		/**
		 * Return an InputStream to read the contents of the file from.
		 * The user is responsible for closing the stream.
		 * @return the contents of the file as stream,
		 * or an empty stream if empty
		 * @throws IOException in case of access errors
		 * (if the temporary store fails)
		 */
		public InputStream getInputStream() throws IOException {
			try {
				return part.getInputStream();
			} catch (MessagingException me) {
				throw new IOException(me.getLocalizedMessage()==null? me.getMessage():me.getLocalizedMessage());
			}
		}
		
		/**
		 * Transfer the received file to the given destination file.
		 * <p>This may either move the file in the filesystem, copy the file in the
		 * filesystem, or save memory-held contents to the destination file.
		 * If the destination file already exists, it will be deleted first.
		 * <p>If the file has been moved in the filesystem, this operation cannot
		 * be invoked again. Therefore, call this method just once to be able to
		 * work with any storage mechanism.
		 * @param dest the destination file
		 * @throws IOException in case of reading or writing errors
		 * @throws java.lang.IllegalStateException if the file has already been moved
		 * in the filesystem as is not available anymore for another transfer
		*/
		public void transferTo(File dest) throws IOException, IllegalStateException {
			//copied from org.springframework.web.multipart.commons.CommonsMultiPart
//			if (!isAvailable()) {
//				throw new IllegalStateException("File has already been moved - cannot be transferred again");
//			}

			if (dest.exists() && !dest.delete()) {
				throw new IOException(
						"Destination file [" + dest.getAbsolutePath() + "] already exists and could not be deleted");
			}

			try {
				
				FileOutputStream out = new FileOutputStream(dest);
				InputStream in = getInputStream();
				FileCopyUtils.copy(in, out);
/*				dest.this.fileItem.write(dest);
				if (logger.isDebugEnabled()) {
					String action = "transferred";
					if (!this.fileItem.isInMemory()) {
						action = isAvailable() ? "copied" : "moved";
					}
					logger.debug("Multipart file '" + getName() + "' with original filename [" +
							getOriginalFilename() + "], stored " + getStorageDescription() + ": " +
							action + " to [" + dest.getAbsolutePath() + "]");
				}
*/
				}
			catch (IOException ex) {
				throw ex;
			}
			catch (Exception ex) {
				logger.error("Could not transfer to file", ex);
				throw new IOException("Could not transfer to file: " + (ex.getLocalizedMessage()==null? ex.getMessage():ex.getLocalizedMessage()));
			}
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
