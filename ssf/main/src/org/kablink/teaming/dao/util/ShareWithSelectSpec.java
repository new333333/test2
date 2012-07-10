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
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.util.search.Constants;

/**
 * @author jong
 *
 */
public class ShareWithSelectSpec {
	public Long sharerId;
	public Long entityId;
	public Date startDateBegin;
	public boolean startDateBeginInclusive = true;
	public Date startDateEnd;
	public boolean startDateEndInclusive = false;
	public Date endDateBegin;
	public boolean endDateBeginInclusive = true;
	public Date endDateEnd;
	public boolean endDateEndInclusive = false;
	public Collection<Long> recipientUsers;
	public Collection<Long> recipientGroups;
	public Collection<Long> recipientTeams;
	public Collection<String> onRights;
	public boolean onRightsDisjunctive = true;
	
	public void setSharerId(Long sharerId) {
		this.sharerId = sharerId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public void setStartDateRange(Date startDateBegin, boolean startDateBeginInclusive, Date startDateEnd, boolean startDateEndInclusive) {
		this.startDateBegin = startDateBegin;
		this.startDateBeginInclusive = startDateBeginInclusive;
		this.startDateEnd = startDateEnd;
		this.startDateEndInclusive = startDateEndInclusive;
	}
	public void setEndDateRange(Date endDateBegin, boolean endDateBeginInclusive, Date endDateEnd, boolean endDateEndInclusive) {
		this.endDateBegin = endDateBegin;
		this.endDateBeginInclusive = endDateBeginInclusive;
		this.endDateEnd = endDateEnd;
		this.endDateEndInclusive = endDateEndInclusive;
	}
	public void setRecipients(Collection<Long> recipientUsers,  Collection<Long> recipientGroups,  Collection<Long> recipientTeams) {
		this.recipientUsers = recipientUsers;
		this.recipientGroups = recipientGroups;
		this.recipientTeams = recipientTeams;
	}
	public void setRecipientsFromUserMembership(Long userId) {
		User user = getProfileDao().loadUser(userId,  RequestContextHolder.getRequestContext().getZoneId());
		Set<Long> userIds = new HashSet();
		userIds.add(userId);
	    Set<Long> groupIds = getProfileDao().getPrincipalIds(user);
	    groupIds.remove(userId);
		List<Map> myTeams = getBinderModule().getTeamMemberships(user.getId());
		Set<Long> teamIds = new HashSet();
		for(Map binder : myTeams) {
			try {
				teamIds.add(Long.valueOf((String)binder.get(Constants.DOCID_FIELD)));
			} catch (Exception ignore) {};
		}
		setRecipients(userIds, groupIds, teamIds);
	}
	public void setOnRightsWithNames(Collection<String> onRights, boolean onRightsDisjunctive) {
		this.onRights = onRights;
		this.onRightsDisjunctive = onRightsDisjunctive;
	}
	
	public void setOnRightsWithOperations(Collection<WorkAreaOperation> onRights, boolean onRightsDisjunctive) {
		Set<String> rightNames = new HashSet<String>();
		for(WorkAreaOperation wao:onRights)
			rightNames.add(wao.getName());
		setOnRightsWithNames(rightNames, onRightsDisjunctive);
	}
	
	private ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}
	
	private BinderModule getBinderModule() {
		return (BinderModule) SpringContextUtil.getBean("binderModule");
	}
}
