/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.remoting.ws.model;
import java.io.Serializable;
import java.util.HashMap;

public class Subscription implements Serializable {
	//storage only, externally as an array
	private HashMap<Integer, SubscriptionStyle> subscriptionMap = new HashMap();
	private Long entityId;
	
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}
	public void addStyle(int style, String[]emailTypes) {
		subscriptionMap.put(Integer.valueOf(style), new SubscriptionStyle(style, emailTypes));
	}
	public void addStyle(SubscriptionStyle subscription) {
		subscriptionMap.put(Integer.valueOf(subscription.getStyle()), subscription);
	}
	public void removeStyle(int style) {
		subscriptionMap.remove(Integer.valueOf(style));
	}
	public SubscriptionStyle[] getStyles() {
		SubscriptionStyle[] array = new SubscriptionStyle[subscriptionMap.size()];
		return subscriptionMap.values().toArray(array);
	}
	public void setStyles(SubscriptionStyle[] subscriptions) {
		this.subscriptionMap = new HashMap<Integer,SubscriptionStyle>();
		if (subscriptions != null) {
			for(int i = 0; i < subscriptions.length; i++) {
				this.subscriptionMap.put(Integer.valueOf(subscriptions[i].getStyle()), subscriptions[i]);
			}
		}
	}
}
