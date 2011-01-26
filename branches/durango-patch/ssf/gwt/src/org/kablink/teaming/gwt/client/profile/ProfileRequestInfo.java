/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

import com.google.gwt.core.client.JavaScriptObject;


/**
 * This class wraps a JavaScript object that holds information about the request we are working with.
 * @author nbjensen
 *
 */
public class ProfileRequestInfo extends JavaScriptObject
{
	/**
	 * Overlay types always have a protected, zero-arg constructors.
	 */
	protected ProfileRequestInfo()
	{
	}// end RequestInfo()

	
	/**
	 * Return the adapted url.  This class is an overlay on the JavaScript object called profileRequestInfo.
	 */
	public final native String getAdaptedUrl() /*-{ return this.adaptedUrl; }-*/;


	/**
	 * Return the binder id.  This class is an overlay on the JavaScript object called profileRequestInfo.
	 */
	public final native String getBinderId() /*-{ return this.binderId; }-*/;
	
	
	/**
	 * Return the user's name.  This class is an overlay on the JavaScript object called profileRequestInfo.
	 */
	public final native String getUserName() /*-{ return this.profileUserName; }-*/;

	/**
	 * Return the path to Teaming's images.  This class is an overlay on the JavaScript object called profileRequestInfo.
	 */
	public final native String getImagesPath() /*-{ return this.imagesPath; }-*/;

	
	/**
	 * Return the path to Teaming's JavaScript.  This class is an overlay on the JavaScript object called profileRequestInfo.
	 */
	public final native String getJSPath() /*-{ return this.jsPath; }-*/;

	
	/**
	 * Return the "my workspace" url.  This class is an overlay on the JavaScript object called profileRequestInfo.
	 */
	public final native String getMyWorkspaceUrl() /*-{ return this.myWSUrl; }-*/;
	
	/**
	 * Return the get the current user's workspaceId
	 */
	public final native String getCurrentUserWorkspaceId() /*-{ return this.currentUserWorkspaceId; }-*/;
	
	public final native String getUserId() /*-{ return this.userId; }-*/;
	
	public final native String getUserLoginId() /*-{ return this.userLoginId; }-*/;
	
	public final native boolean isBinderAdmin() /*-{ return this.isBinderAdmin; }-*/;
	
	public final native boolean isQuotasEnabled() /*-{ return this.isQuotasEnabled; }-*/;
	
	public final native String getQuotasUserMaximum() /*-{ return this.quotasUserMaximum; }-*/;
	
	public final native String getQuotasDiskSpacedUsed() /*-{ return this.quotasDiskSpacedUsed; }-*/;
	
	public final native boolean isDiskQuotaExceeded() /*-{ return this.isQuotasDiskQuotaExceeded; }-*/;
	
	public final native String getUserDescription() /*-{ return this.userDescription; }-*/;
	
	public final native boolean isModifyAllowed() /*-{ return this.isModifyAllowed }-*/;
	
	public final native String getModifyUrl() /*-{ return this.modifyUrl }-*/;
	
	public final native boolean isDiskQuotaHighWaterMarkExceeded() /*-{ return this.isDiskQuotaHighWaterMarkExceeded; }-*/;

	public final native String getDeleteUserUrl() /*-{ return this.deleteUserUrl }-*/;

	public final native String getQuotaMessage() /*-{ return this.quotasDiskMessage }-*/;
	
	/**
	 * Is the workspace being referenced owned by the current user
	 * @return
	 */
	public final boolean isOwner() {
		if(getCurrentUserWorkspaceId() == getBinderId()) {
			return true;
		} 
		return false;
	}

}// end RequestInfo
