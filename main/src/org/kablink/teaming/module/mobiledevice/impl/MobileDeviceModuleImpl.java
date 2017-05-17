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
package org.kablink.teaming.module.mobiledevice.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.dao.util.MobileDeviceSelectSpec;
import org.kablink.teaming.domain.MobileDevice;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.mobiledevice.MobileDeviceModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.GwtUIHelper;
import org.kablink.teaming.web.util.MiscUtil;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This module gives us the transaction semantics to deal with the
 * 'Mobile Device' features.  
 *
 * @author drfoster@novell.com
 */
@SuppressWarnings("unchecked")
public class MobileDeviceModuleImpl extends CommonDependencyInjection implements MobileDeviceModule {
	private AdminModule			m_adminModule;			//
	private ProfileModule		m_profileModule;		//
	private TransactionTemplate	m_transactionTemplate;	//

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
    protected AdminModule         getAdminModule()         {if (null == m_adminModule)   {m_adminModule   = ((AdminModule)   SpringContextUtil.getBean("adminModule")  );} return m_adminModule;  }
	protected ProfileModule       getProfileModule()       {if (null == m_profileModule) {m_profileModule = ((ProfileModule) SpringContextUtil.getBean("profileModule"));} return m_profileModule;}
    protected TransactionTemplate getTransactionTemplate() {return m_transactionTemplate;}
	
    /**
     * Set'er methods.
     * 
     * @param
     */
    public void setAdminModule(        AdminModule         adminModule)         {m_adminModule         = adminModule;        }
	public void setProfileModule(      ProfileModule       profileModule)       {m_profileModule       = profileModule;      }
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {m_transactionTemplate = transactionTemplate;}


    private Long getLoggedInUserId() {
        return RequestContextHolder.getRequestContext().getUserId();
    }

    /**
     * Creates a new MobileDevice for a user.
     *
     * No transaction.
     * 
     * @param mobileDevice
     */
    @Override
	public void addMobileDevice(final MobileDevice mobileDevice) {
        if (!getLoggedInUserId().equals(mobileDevice.getUserId())) {
            // Validate that the user can manage MobileDevice's.
            getAdminModule().checkAccess(AdminOperation.manageFunction);
        }

    	// Validate the contents of the MobileDevice.
    	validateMobileDevice(mobileDevice);

    	// Add the MobileDevice to the database.
        getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				getCoreDao().save(mobileDevice);
				return null;
			}
		});
    }
    
    /**
     * Deletes all the MobileDevice's for a user.
     *
     * No transaction.
     * 
     * @param userId
     */
	@Override
    public void deleteAllMobileDevices(Long userId) {
    	// Validate that the user can manage MobileDevice's.
        if (!getLoggedInUserId().equals(userId)) {
        	getAdminModule().checkAccess(AdminOperation.manageFunction);
        }
    	
    	// Are there any MobileDevice's for this user ID?
    	final List<MobileDevice> mdList = getMobileDeviceList(userId);
    	if (!(MiscUtil.hasItems(mdList))) {
    		// No!  Bail.
    		return;
    	}
    	
		// Delete the MobileDevices.
		getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				for (MobileDevice md:  mdList) {
					getCoreDao().delete(md);
				}
				return null;
			}
		});
    }
    
    /**
     * Deletes an existing MobileDevice.
     *
     * No transaction.
     * 
     * @param userId
     * @param deviceId
     */
    @Override
	public void deleteMobileDevice(Long userId, String deviceId) {
    	// Validate that the user can manage MobileDevice's.
        if (!getLoggedInUserId().equals(userId)) {
            getAdminModule().checkAccess(AdminOperation.manageFunction);
        }
    	
    	// Does a MobileDevice with these ID's exist?
    	final MobileDevice mobileDevice = getMobileDevice(userId, deviceId);
    	if (null == mobileDevice) {
    		// No!  Bail.
    		return;
    	}
		
		// Delete the MobileDevice.
		getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				getCoreDao().delete(mobileDevice);
				return null;
			}
		});
    }
    
    @Override
	public void deleteMobileDevice(MobileDevice mobileDevice) {
    	// Always use the initial form of the method.
    	deleteMobileDevice(mobileDevice.getUserId(), mobileDevice.getDeviceId());
    }
    
    /**
     * Returns a specific MobileDevices for a user, if its defined.
     * 
     * @param userId
     * @param deviceId
     * 
     * @return
     */
    @Override
	public MobileDevice getMobileDevice(Long userId, String deviceId) {
    	// Validate the parameters.
		if (null == userId) {
			throw new IllegalArgumentException("getMobileDevice() requires userId.");
		}
		if (!(MiscUtil.hasString(deviceId))) {
			throw new IllegalArgumentException("getMobileDevice() requires deviceId.");
		}

		// Query for the matching mobile device.
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		FilterControls filter = new FilterControls();
		filter.add("deviceId", deviceId);
		filter.add("userId",   userId  );
		List<MobileDevice> mobileDevices = getCoreDao().loadObjects(MobileDevice.class, filter, zoneId);
		
		// Return the first matching device, if there are any and null
		// otherwise.
		MobileDevice reply;
		if (MiscUtil.hasItems(mobileDevices))
		     reply = mobileDevices.get(0);
		else reply = null;
    	return reply;
    }
    
    /**
     * Returns a user's MobileDevices, if any are defined.
     * 
     * @param userId	Optional.  null -> All MobileDevice's, system wide.
     * @param options	Optional.  Specifications for sorting, paging and filtering.
     * 
     * @return
     */
    @Override
	public Map getMobileDevices(Long userId, Map options) {
    	// If we have a user ID...
    	boolean systemWide = (null == userId);
		if (!systemWide) {
			// ...validate it.
			validateUserId(userId);
		}

		// Determine the sort/filter defaults to use for the query.
		if (null == options) {
			options = new HashMap();
		}
		boolean sortAscend  = (!(GwtUIHelper.getOptionBoolean(options, ObjectKeys.SEARCH_SORT_DESCEND, false))      );
		String  sortBy      =    GwtUIHelper.getOptionString( options, ObjectKeys.SEARCH_SORT_BY,      "description");
		int     start       =    GwtUIHelper.getOptionInt(    options, ObjectKeys.SEARCH_OFFSET,       (-1)         );
		int     length      =    GwtUIHelper.getOptionInt(    options, ObjectKeys.SEARCH_MAX_HITS,     (-1)         );
		String  quickFilter =    GwtUIHelper.getOptionString( options, ObjectKeys.SEARCH_QUICK_FILTER, ""           );
		
		// Query for the matching mobile devices.
		Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
		MobileDeviceSelectSpec mdss = new MobileDeviceSelectSpec(sortAscend, sortBy, start, length, userId, null, quickFilter);
		Map reply = getCoreDao().findMobileDevices(mdss, zoneId);
		
		// Return them.
		if (null == reply) {
			reply = new HashMap();
			reply.put(ObjectKeys.SEARCH_ENTRIES,     new ArrayList<MobileDevice>());
			reply.put(ObjectKeys.SEARCH_COUNT_TOTAL, new Long(0));
		}
    	return reply;
    }
    
	@Override
	public List<MobileDevice> getMobileDeviceList(Long userId) {
		List<MobileDevice> reply;
		Map mdMap = getMobileDevices(userId, null);
		if (null != mdMap)
		     reply = ((List<MobileDevice>) mdMap.get(ObjectKeys.SEARCH_ENTRIES));
		else reply = null;
		if (null == reply) {
			reply = new ArrayList<MobileDevice>();
		}
		return reply;
	}
    
    @Override
	public List<MobileDevice> getMobileDeviceList() {
		List<MobileDevice> reply;
		Map mdMap = getMobileDevices(null, null);
		if (null != mdMap)
		     reply = ((List<MobileDevice>) mdMap.get(ObjectKeys.SEARCH_ENTRIES));
		else reply = null;
		if (null == reply) {
			reply = new ArrayList<MobileDevice>();
		}
		return reply;
	}
    
	@Override
	public Map getMobileDevices(Map options) {
		// Always use the initial form of the method.
		return getMobileDevices(null, options);
	}
    
    /**
     * Modifies a MobileDevice.
     *
     * No transaction.
     * 
     * @param mobileDevice
     */
    @Override
	public void modifyMobileDevice(final MobileDevice mobileDevice) {
    	// Always use the implementation form of the method.
    	modifyMobileDeviceImpl(mobileDevice, true);	// true -> Enforce that the user has mobile device management rights.
    }

    /*
     * Implementation method to modify a MobileDevice.
     */
	private void modifyMobileDeviceImpl(final MobileDevice mobileDevice, boolean enforceRights) {
		if (enforceRights) {
            if (!getLoggedInUserId().equals(mobileDevice.getUserId())) {
    	    	// Validate that the user can manage MobileDevice's.
	        	getAdminModule().checkAccess(AdminOperation.manageFunction);
            }
		}

    	// Validate the contents of the MobileDevice.
    	validateMobileDevice(mobileDevice);

    	// Update the MobileDevice in the database.
        getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				getCoreDao().update(mobileDevice);
				return null;
			}
		});
    }

    /**
     * Scans the mobile devices assigned to a User object and makes
     * sure the user title stored in them match that from the User.
     * 
     * @param userId
     */
	@Override
	public void setMatchingUserTitles(User user) {
		// Does this user have any mobile devices? 
		Long userId = user.getId();
		List<MobileDevice> mdList = getMobileDeviceList(userId);
		if (MiscUtil.hasItems(mdList)) {
			// Yes!  If its the current user matching their devices to
			// their title, we don't enforce manage rights.  Otherwise,
			// we do.  Do we need to enforce manage rights?
			User    currentUser   = RequestContextHolder.getRequestContext().getUser();
			boolean enforceRights = (!(userId.equals(currentUser.getId())));
 			
			// Scan the mobile devices.
			String userTitle = user.getTitle();
			for (MobileDevice md:  mdList) {
				// Do we need to modify this mobile device because the
				// user's title doesn't match?
				if (!(md.getUserTitle().equals(userTitle))) {
					// Yes!  Modify it.
					md.setUserTitle(userTitle);
					modifyMobileDeviceImpl(md, enforceRights);
				}
			}
		}
	}
    /*
     * Validates the contents of a MobileDevice and return's its
     * corresponding User.
     */
	private User validateMobileDevice(MobileDevice mobileDevice) {
		// Validate the MobileDevice.
		if (null == mobileDevice) {
			throw new IllegalArgumentException("MobileDevice not specified.");
		}
		String deviceId = mobileDevice.getDeviceId();
		if (!(MiscUtil.hasString(deviceId))) {
			throw new IllegalArgumentException("MobileDevice requires deviceId.");
		}

		// Validate we can access the userId as a User.
        User reply = validateUserId(mobileDevice.getUserId());
        
        // Validate that the User's title in the MobileDevice matches
        // that from the User.
        validateUserTitle(mobileDevice, reply);

        // Return the User object we retrieved from the MobileDevice.
        return reply;
	}

	/*
	 * Validates the give user ID and returns the corresponding User
	 * object.
	 */
	private User validateUserId(Long userId) {
		// Validate that we have a user ID.
		if (null == userId) {
			throw new IllegalArgumentException("MobileDevice requires userId.");
		}

		// Validate that it maps to a valid User...
        Principal recipient = getProfileModule().getEntry(userId);
        if (!recipient.getEntityType().equals(EntityType.user)) {
            throw new NoUserByTheIdException(userId);
        }
        User reply = ((User) recipient);
        
        // ...and return it.
        return reply;
	}
	
	/*
	 * Validates that the user title in a MobileDevice matches that of
	 * the User.
	 */
	private void validateUserTitle(MobileDevice mobileDevice, User user) {
		// If the title's don't match exactly...
		String mdUserTitle = mobileDevice.getUserTitle();
		if (null == mdUserTitle) mdUserTitle = "";
		
		String userTitle = user.getTitle();
		if (null == userTitle) userTitle = "";
		
		if (!(mdUserTitle.equals(userTitle))) {
			// ...store the title from the User into the MobileDevice.
			mobileDevice.setUserTitle(userTitle);
		}
	}
}
