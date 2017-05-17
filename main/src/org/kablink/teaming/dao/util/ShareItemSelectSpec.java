/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.dao.util;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.shared.SearchUtils;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.StringUtil;
import org.kablink.util.search.Constants;

/**
 * This object encapsulates select specifications used to retrieve only those share items
 * that fulfill specified criteria.
 * 
 * Each specification deals only with a single attribute of share item, and there might be
 * multiple methods offered in this class that manipulate the same attribute. In such case,
 * the last method invoked overrides all the effects of other method invocations made earlier
 * for the same attribute.
 * 
 * When multiple specifications are made for multiple attributes of share item, those 
 * specifications are combined together through logical operator AND. In other word, only
 * those share items that meet ALL of the specifications will be retrieved from the database.
 * 
 * @author jong
 *
 */
public class ShareItemSelectSpec {
	public Boolean latest;
	
	public Collection<Long> sharerIds;
	
	public Collection<EntityIdentifier> sharedEntityIdentifiers;
	
	public Date startDateMin;
	public boolean startDateMinInclusive = true;
	public Date startDateMax;
	public boolean startDateMaxInclusive = true;
	
	public String orderByFieldName;
	public boolean orderByDescending = true;
	
	public String[] commentLikes;
	public boolean commentLikesDisjunctive = false;
	
	public Date endDateMin;
	public boolean endDateMinInclusive = false;
	public Date endDateMax;
	public boolean endDateMaxInclusive = false;
	
	public Collection<Long> recipientUserIds;
	public Collection<Long> recipientGroupIds;
	public Collection<Long> recipientTeamIds;
	
	public Collection<String> onRights;
	public boolean onRightsDisjunctive = true;
	
	public boolean accountForInheritance = false;

    public Boolean deleted = Boolean.FALSE;

    public ShareItem.RecipientType recipientType;

    /**
	 * Defines selection criterion with a specific value of "latest" attribute.
	 * 
	 * @return
	 */
	public void setLatest(boolean latest) {
		this.latest = latest;
	}

	/**
	 * Defines selection criterion around 'sharer' attribute.
	 * 
	 * @param sharerId
	 */
	public void setSharerId(Long sharerId) {
		this.sharerIds = new HashSet();
		this.sharerIds.add(sharerId);
	}
	
	/**
	 * Defines selection criterion around 'sharer' attribute.
	 * 
	 * @param sharerIds
	 */
	public void setSharerIds(Collection<Long> sharerIds) {
		this.sharerIds = sharerIds;
	}
	
	/**
	 * Defines selection criterion around 'shared entity' attribute.
	 * 
	 * @param sharedEntityIdentifier
	 */
	public void setSharedEntityIdentifier(EntityIdentifier sharedEntityIdentifier) {
		this.sharedEntityIdentifiers = new HashSet();
		this.sharedEntityIdentifiers.add(sharedEntityIdentifier);
	}
	
	/**
	 * Defines selection criterion around 'shared entity' attribute.
	 * 
	 * @param sharedEntityIdentifiers
	 */
	public void setSharedEntityIdentifiers(Collection<EntityIdentifier> sharedEntityIdentifiers) {
		this.sharedEntityIdentifiers = sharedEntityIdentifiers;
	}
	
	/**
	 * Defines selection criterion around 'start date' attribute.
	 * 
	 * @param startDateMin
	 * @param startDateMinInclusive
	 * @param startDateMax
	 * @param startDateMaxInclusive
	 */
	public void setStartDateRange(Date startDateMin, Boolean startDateMinInclusive, Date startDateMax, Boolean startDateMaxInclusive) {
		this.startDateMin = startDateMin;
		if(startDateMinInclusive != null)
			this.startDateMinInclusive = startDateMinInclusive.booleanValue();
		this.startDateMax = startDateMax;
		if(startDateMaxInclusive != null)
			this.startDateMaxInclusive = startDateMaxInclusive.booleanValue();
	}

	/**
	 * Defines selection criterion around 'comment' attribute.
	 * 
	 * @param quickFilter
	 */
	public void setCommentLikes(String quickFilter) {
		String[] strs = StringUtil.split(quickFilter);
		if(strs != null && strs.length > 0) {
			this.commentLikes = strs;
			this.commentLikesDisjunctive = false;
		}
		else {
			this.commentLikes = null;
		}
	}
	
	/**
	 * Defines selection criterion around 'comment' attribute.
	 * 
	 * @param commentLikes
	 * @param commentLikesDisjunctive
	 */
	public void setCommentLikes(String[] commentLikes, boolean commentLikesDisjunctive) {
		this.commentLikes = commentLikes;
		this.commentLikesDisjunctive = commentLikesDisjunctive;
	}
	
	/**
	 * Defines selection criterion around 'end date' attribute.
	 * 
	 * @param endDateMin
	 * @param endDateMinInclusive
	 * @param endDateMax
	 * @param endDateMaxInclusive
	 */
	public void setEndDateRange(Date endDateMin, Boolean endDateMinInclusive, Date endDateMax, Boolean endDateMaxInclusive) {
		this.endDateMin = endDateMin;
		if(endDateMinInclusive != null)
			this.endDateMinInclusive = endDateMinInclusive.booleanValue();
		this.endDateMax = endDateMax;
		if(endDateMaxInclusive != null)
			this.endDateMaxInclusive = endDateMaxInclusive.booleanValue();
	}
	
	/**
	 * Defines selection criterion around 'end date' attribute in such a way that it would filter out all expired items.
	 * 
	 */
	public void excludeExpired() {
		setEndDateRange(new Date(), null, null, null);
	}
	
	/**
	 * Defines selection criterion around 'recipient' attribute.
	 * 
	 * @param recipientUserId
	 * @param recipientGroupId
	 * @param recipientTeamId
	 */
	public void setRecipients(Long recipientUserId, Long recipientGroupId, Long recipientTeamId) {
		this.recipientUserIds = null;
		this.recipientGroupIds = null;
		this.recipientTeamIds = null;
		if(recipientUserId != null) {
			this.recipientUserIds = new HashSet();
			this.recipientUserIds.add(recipientUserId);
		}
		if(recipientGroupId != null) {
			this.recipientGroupIds = new HashSet();
			this.recipientGroupIds.add(recipientGroupId);
		}
		if(recipientTeamId != null) {
			this.recipientTeamIds = new HashSet();
			this.recipientTeamIds.add(recipientTeamId);
		}
	}

	/**
	 * Defines selection criterion around 'recipient' attribute.
	 * 
	 * @param recipientUserIds
	 * @param recipientGroupIds
	 * @param recipientTeamIds
	 */
	public void setRecipients(Collection<Long> recipientUserIds,  Collection<Long> recipientGroupIds,  Collection<Long> recipientTeamIds) {
		this.recipientUserIds = recipientUserIds;
		this.recipientGroupIds = recipientGroupIds;
		this.recipientTeamIds = recipientTeamIds;
	}
	
	/**
	 * Defines selection criterion around 'recipient' attribute.
	 * 
	 * @param userId
	 */
	public void setRecipientsFromUserMembership(Long userId) {
		User user = getProfileDao().loadUser(userId,  RequestContextHolder.getRequestContext().getZoneId());
		Set<Long> userIds = new HashSet();
		userIds.add(userId);
	    Set<Long> groupIds = getProfileDao().getApplicationLevelPrincipalIds(user);
	    groupIds.remove(userId);
		List<Map> myTeams = getBinderModule().getTeamMemberships(user.getId(), SearchUtils.fieldNamesList(Constants.DOCID_FIELD));
		Set<Long> teamIds = new HashSet();
		for(Map binder : myTeams) {
			try {
				teamIds.add(Long.valueOf((String)binder.get(Constants.DOCID_FIELD)));
			} catch (Exception ignore) {};
		}
		setRecipients(userIds, groupIds, teamIds);
	}
	
	/**
	 * Defines selection criterion around 'right' attribute.
	 * 
	 * @param onRights
	 * @param onRightsDisjunctive
	 */
	public void setOnRightsWithNames(Collection<String> onRights, boolean onRightsDisjunctive) {
		this.onRights = onRights;
		this.onRightsDisjunctive = onRightsDisjunctive;
	}
	
	/**
	 * Defines selection criterion around 'right' attribute.
	 * 
	 * @param onRights
	 * @param onRightsDisjunctive
	 */
	public void setOnRightsWithOperations(Collection<WorkAreaOperation> onRights, boolean onRightsDisjunctive) {
		Set<String> rightNames = new HashSet<String>();
		for(WorkAreaOperation wao:onRights)
			rightNames.add(wao.getName());
		setOnRightsWithNames(rightNames, onRightsDisjunctive);
	}
	
	/**
	 * Specify order-by clause.
	 * 
	 * @param orderByFieldName
	 * @param descending
	 */
	public void setOrder(String orderByFieldName, boolean orderByDescending) {
		this.orderByFieldName = orderByFieldName;
		this.orderByDescending = orderByDescending;
	}
	
	/**
	 *  Defines selection criterion around whether to account for inherited share rights or not.
	 *  
	 * @param accountForInheritance
	 */
	public void setAccountForInheritance(boolean accountForInheritance) {
		this.accountForInheritance = accountForInheritance;
	}
	
	private ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}
	
	private BinderModule getBinderModule() {
		return (BinderModule) SpringContextUtil.getBean("binderModule");
	}
}
