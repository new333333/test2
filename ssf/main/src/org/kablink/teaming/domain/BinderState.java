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
package org.kablink.teaming.domain;

import java.util.Date;

/**
 * @author jong
 *
 */
public class BinderState extends ZonedObject {

	/*
	 * Owning binder ID
	 */
	private Long binderId;
	/*
	 * Last time sync completed on this particular binder.
	 * This does NOT tell whether the sync was full (whole branch) or JIT 
	 * (single level only), or whether the sync was successful or not.
	 */
	private Date lastSyncTime; 
	
	/*
	 * Last time full sync initiated on this binder completed.
	 * This property is relevant only to those binders on which full sync starts,
	 * which are almost always net folder roots (currently, Filr does not allow
	 * starting full sync on any folder other than net folder roots).
	 * Those binders that are synchronized indirectly from a full sync triggered 
	 * on one of their ancestor binders do not get this property value set.
	 * This property value is set if and only if full sync completes, which is
	 * defined by whether the sync process was able to enumerate all the members
	 * of the data tree starting from the top all the way down to leaves.
	 */
	private Date lastFullSyncTime;

	protected BinderState() {
		// Use by Hibernate only
	}
	
	public BinderState(Long binderId) {
		setBinderId(binderId);
	}
	
	public Long getBinderId() {
		return binderId;
	}

	public void setBinderId(Long binderId) {
		this.binderId = binderId;
	}

	public Date getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(Date lastSyncTime) {
		this.lastSyncTime = lastSyncTime;
	}

	public Date getLastFullSyncTime() {
		return lastFullSyncTime;
	}

	public void setLastFullSyncTime(Date lastFullSyncTime) {
		this.lastFullSyncTime = lastFullSyncTime;
	}
}
