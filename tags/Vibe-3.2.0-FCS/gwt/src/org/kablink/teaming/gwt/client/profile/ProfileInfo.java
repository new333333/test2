/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.client.profile;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponseData;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ProfileInfo implements IsSerializable, VibeRpcResponseData  {

	private String title;
	private String binderId;
	private String userId;
	private ArrayList<String> pictureUrls = new ArrayList<String>();
	private ArrayList<String> pictureScaledUrls = new ArrayList<String>();
	
	private ArrayList<ProfileCategory> categories = new ArrayList<ProfileCategory>();
	private boolean pictureEnabled = false;
	private boolean conferencingEnabled = false;
	private boolean presenceEnabled = false;
	
	/**
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	public ProfileInfo() {
		
	}
	
	public ArrayList<ProfileCategory> getCategories() {
		return categories;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getTitle() {
		return this.title;
	}

	public ProfileCategory get(String name) {
		
		ProfileCategory category = null;
		
		for(ProfileCategory cat: categories) {
			if( cat.getName().equals(name)) {
				category = cat;
				break;
			}
		}
		
		return category;
	}
	
	public ProfileCategory get(int index) {
		return categories.get(index);
	}

	public void add(ProfileCategory cat) {
		this.categories.add(cat);
	}

	public String getBinderId() {
		return binderId;
	}

	public void setBinderId(String binderId) {
		this.binderId = binderId;
	}

	public String getPictureUrl() {
		String pictureUrl = null;
		if((!pictureUrls.isEmpty())){ pictureUrl = pictureUrls.get(0); };
		return pictureUrl;
	}

	public String getPictureScaledUrl() {
		String pictureScaledUrl = null;
		if((!pictureScaledUrls.isEmpty())){ pictureScaledUrl = pictureScaledUrls.get(0); };
		return pictureScaledUrl;
	}

	public List<String> getPicutres() {
		return pictureUrls;
	}
	
	public List<String> getPicutreScaleds() {
		return pictureScaledUrls;
	}
	
	public void addPictureUrl(String pictureUrl) {
		this.pictureUrls.add(pictureUrl);
	}
	
	public void addPictureScaledUrl(String pictureScaledUrl) {
		this.pictureScaledUrls.add(pictureScaledUrl);
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setPictureEnabled(boolean enabled) {
		this.pictureEnabled = enabled;
	}

	public boolean isPictureEnabled() {
		return pictureEnabled;
	}
	
	public void setConferencingEnabled(boolean enabled) {
		this.conferencingEnabled = enabled;
	}
	public boolean isConferencingEnabled() {
		return conferencingEnabled;
	}
	
	public void setPresenceEnabled(boolean enabled) {
		this.presenceEnabled = enabled;
	}
	
	public boolean isPresenceEnabled() {
		return presenceEnabled;
	}
}
