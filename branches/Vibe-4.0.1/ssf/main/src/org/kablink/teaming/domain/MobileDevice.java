/**
' * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a mobile device assigned to a user.
 *
 * @author drfoster@novell.com
 */
public class MobileDevice extends ZonedObject implements Serializable {
	protected Boolean	wipeScheduled;	//
	protected Date		lastLogin;		//
	protected Date		lastWipe;		//
	protected Long		userId;			//
	protected String	description;	//
	protected String	deviceId;		//
	protected String	userTitle;		//

	/**
	 * Constructor method.
	 *
	 * For use by Hibernate only.
	 */
	protected MobileDevice() {
	}

	/**
	 * Constructor method.
	 *
	 * For user by application.
	 *
	 * @param userId
	 * @param userTitle
	 * @param deviceId
	 * @param description
	 * @param lastLogin
	 * @param lastWipe
	 * @param wipeScheduled
	 */
	public MobileDevice(
			Long	userId,
			String	userTitle,
			String	deviceId,
			String	description,
			Date	lastLogin,
			Date	lastWipe,
			Boolean	wipeScheduled) {
        this(userId, deviceId);
		this.description   = description;
		this.userTitle     = userTitle;
		this.lastLogin     = lastLogin;
		this.lastWipe      = lastWipe;
		this.wipeScheduled = wipeScheduled;
	}

	/**
	 * Constructor method.
	 *
	 * For user by application.
	 *
	 * @param userId
	 * @param deviceId
	 */
    public MobileDevice(Long userId, String deviceId) {
		if (userId == null) {
			throw new IllegalArgumentException("User ID must be specified");
		}
		if (deviceId == null) {
			throw new IllegalArgumentException("Device ID must be specified");
		}
		this.userId = userId;
		this.deviceId = deviceId;
	}

	/**
	 * Copy constructor.
	 *
	 * @param md
	 */
	public MobileDevice(MobileDevice md) {
		// Don't copy ID and zone ID. Copy just the data.
		this.userId        = md.userId;
		this.deviceId      = md.deviceId;
		this.description   = md.description;
		this.userTitle     = md.userTitle;
		this.lastLogin     = md.lastLogin;
		this.lastWipe      = md.lastWipe;
		this.wipeScheduled = md.wipeScheduled;
	}

	/**
	 * Get'er methods.
	 *
	 * @return
	 */
	public Boolean getWipeScheduled() {return wipeScheduled;}
	public Date    getLastLogin()     {return lastLogin;    }
	public Date    getLastWipe()      {return lastWipe;     }
	public Long    getUserId()        {return userId;       }
	public String  getDescription()   {return description;  }
	public String  getDeviceId()      {return deviceId;     }
	public String  getUserTitle()     {return userTitle;    }

	/**
	 * Set'er methods.
	 *
	 * @param
	 */
	public void setWipeScheduled(Boolean wipeScheduled) {this.wipeScheduled = wipeScheduled;}
	public void setLastLogin(    Date    lastLogin)     {this.lastLogin     = lastLogin;    }
	public void setLastWipe(     Date    lastWipe)      {this.lastWipe      = lastWipe;     }
	public void setUserId(       Long    userId)        {this.userId        = userId;       }
	public void setDescription(  String  description)   {this.description   = description;  }
	public void setDeviceId(     String  deviceId)      {this.deviceId      = deviceId;     }
	public void setUserTitle(    String  userTitle)     {this.userTitle     = userTitle;    }

	/**
	 * Compares two MobileDevice's, returning true if they're equal.
	 * 
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj == this) return true;
		if (obj instanceof MobileDevice) {
			MobileDevice pk = (MobileDevice) obj;
			if (pk.getUserId().equals(userId) &&
					pk.getDeviceId().equals(deviceId)) return true;
		}
		return false;
	}

	/**
	 * Generates and returns a hash code for this MobileDevice.
	 * 
	 * @return
	 */
	@Override
	public int hashCode() {
		return (31 * userId.hashCode() + deviceId.hashCode());
	}
}
