/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.module.mobiledevice;

import java.util.List;
import java.util.Map;

import org.kablink.teaming.domain.MobileDevice;
import org.kablink.teaming.domain.User;

/**
 * <code>MobileDeviceModule</code> provides 'Mobile Device' related
 * operations.
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unchecked")
public interface MobileDeviceModule {
    /**
     * Creates a new MobileDevice for a user.
     * 
     * @param mobileDevice
     */
    public void addMobileDevice(MobileDevice mobileDevice);
    
    /**
     * Deletes all the MobileDevice's for a user.
     * 
     * @param userId
     */
    public void deleteAllMobileDevices(Long userId);
    
    /**
     * Deletes an existing MobileDevice.
     * 
     * @param userId
     * @param deviceId
     */
    public void deleteMobileDevice(Long userId, String deviceId);
    public void deleteMobileDevice(MobileDevice mobileDevice);
    
    /**
     * Returns a specific MobileDevice for a user, if its defined.
     * 
     * @param userId
     * @param deviceId
     * 
     * @return
     */
    public MobileDevice getMobileDevice(Long userId, String deviceId);
    
    /**
     * Returns a list of MobileDevice's.
     * 
     * @param userId
     * 
     * @return
     */
    public List<MobileDevice> getMobileDeviceList(           );	// Returns MobileDevice's system wide.
    public List<MobileDevice> getMobileDeviceList(Long userId);	// Returns MobileDevice's for the given user.
    
    /**
     * Returns a list of MobileDevice's.
     * 
	 * Returns a Map containing:
	 * 		Key:  ObjectKeys.SEARCH_ENTRIES:      List<MobileDevice> of the MobileDevice's.
	 *		Key:  ObjectKeys.SEARCH_COUNT_TOTAL:  Long of the total entries available that satisfy the selection specifications.
	 * 
     * @param userId
     * @param options
     * 
     * @return
     */
    public Map getMobileDevices(             Map options);	// Returns MobileDevice's system wide.
    public Map getMobileDevices(Long userId, Map options);	// Returns MobileDevice's for the given user.
    
    /**
     * Modifies a user's MobileDevice.
     * 
     * @param mobileDevice
     */
    public void modifyMobileDevice(MobileDevice mobileDevice);

    /**
     * Scans the mobile devices assigned to a User object and makes
     * sure the user title stored in them match that from the User.
     * 
     * @param userId
     */
    public void setMatchingUserTitles(User user);
}
