/**
 * 
 */
package com.sitescape.team.liferay;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.User;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.service.RoleService;
import com.liferay.portal.service.UserService;
import com.sitescape.util.Pair;

/**
 * @author dml
 *
 */
public class MockUserService implements UserService {
	
	private Map<Long, Pair<User, List<Role>>> users = new HashMap<Long, Pair<User, List<Role>>>();
	@Autowired(required=true)
	private RoleService roleService;

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#addGroupUsers(long, long[])
	 */
	public void addGroupUsers(long arg0, long[] arg1) throws SystemException,
			PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#addPasswordPolicyUsers(long, long[])
	 */
	public void addPasswordPolicyUsers(long arg0, long[] arg1)
			throws SystemException, PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#addRoleUsers(long, long[])
	 */
	public void addRoleUsers(long roleId, long[] userIds) throws SystemException,
			PortalException, RemoteException {
		for (Long id : userIds) {
			Pair<User, List<Role>> u = users.get(id);
			if (u != null) {
				u.getSecond().add(roleService.getRole(roleId));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.liferay.portal.service.UserService#addUser(long, boolean,
	 *      java.lang.String, java.lang.String, boolean, java.lang.String,
	 *      java.lang.String, java.util.Locale, java.lang.String,
	 *      java.lang.String, java.lang.String, int, int, boolean, int, int,
	 *      int, java.lang.String, long, long, boolean)
	 */
	public synchronized User addUser(	long companyId, 
									boolean autoPassword,
									java.lang.String password1, 
									java.lang.String password2,
									boolean autoScreenName, 
									java.lang.String screenName,
									java.lang.String emailAddress, 
									java.util.Locale locale,
									java.lang.String firstName, 
									java.lang.String middleName,
									java.lang.String lastName, 
									int prefixId, 
									int suffixId,
									boolean male, 
									int birthdayMonth, 
									int birthdayDay, 
									int birthdayYear,
									java.lang.String jobTitle, 
									long organizationId, 
									long locationId,
									boolean sendEmail) throws SystemException, PortalException,
			RemoteException {
		final User user = new UserImpl();
		user.setCompanyId(companyId);
		user.setPassword(password1);
		user.setScreenName(screenName);
		user.setEmailAddress(emailAddress);
		user.setCreateDate(new Date());
		user.setActive(true);
		user.setAgreedToTermsOfUse(true);
		user.setLanguageId(locale.getCountry());
		user.setPrimaryKey(users.size() + 1);
		users.put(user.getPrimaryKey(), new Pair<User, List<Role>>(user, new ArrayList<Role>()));
		return user;		
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#addUserGroupUsers(long, long, long[])
	 */
	public void addUserGroupUsers(long arg0, long arg1, long[] arg2)
			throws SystemException, PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#deleteRoleUser(long, long)
	 */
	public void deleteRoleUser(long arg0, long arg1) throws SystemException,
			PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#deleteUser(long)
	 */
	public void deleteUser(long arg0) throws SystemException, PortalException,
			RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#getGroupUsers(long)
	 */
	public List<?> getGroupUsers(long arg0) throws SystemException,
			PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#getRoleUsers(long)
	 */
	public List<?> getRoleUsers(long arg0) throws SystemException,
			PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#getUserByEmailAddress(long, java.lang.String)
	 */
	public User getUserByEmailAddress(long arg0, String arg1)
			throws SystemException, PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#getUserById(long)
	 */
	public User getUserById(long arg0) throws SystemException, PortalException,
			RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#getUserByScreenName(long, java.lang.String)
	 */
	public User getUserByScreenName(long arg0, String arg1)
			throws SystemException, PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#hasGroupUser(long, long)
	 */
	public boolean hasGroupUser(long arg0, long arg1) throws SystemException,
			PortalException, RemoteException {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#hasRoleUser(long, long)
	 */
	public boolean hasRoleUser(long roleId, long userId) throws SystemException,
			PortalException, RemoteException {
		Pair<User, List<Role>> u = users.get(userId);
		if (u == null) {
			return false;
		}
		Role r = roleService.getRole(roleId);
		return u.getSecond().contains(r);
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#setGroupUsers(long, long[])
	 */
	public void setGroupUsers(long arg0, long[] arg1) throws SystemException,
			PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#setRoleUsers(long, long[])
	 */
	public void setRoleUsers(long arg0, long[] arg1) throws SystemException,
			PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#setUserGroupUsers(long, long[])
	 */
	public void setUserGroupUsers(long arg0, long[] arg1)
			throws SystemException, PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#unsetGroupUsers(long, long[])
	 */
	public void unsetGroupUsers(long arg0, long[] arg1) throws SystemException,
			PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#unsetPasswordPolicyUsers(long, long[])
	 */
	public void unsetPasswordPolicyUsers(long arg0, long[] arg1)
			throws SystemException, PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#unsetRoleUsers(long, long[])
	 */
	public void unsetRoleUsers(long arg0, long[] arg1) throws SystemException,
			PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#unsetUserGroupUsers(long, long, long[])
	 */
	public void unsetUserGroupUsers(long arg0, long arg1, long[] arg2)
			throws SystemException, PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#updateActive(long, boolean)
	 */
	public User updateActive(long arg0, boolean arg1) throws SystemException,
			PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#updateAgreedToTermsOfUse(long, boolean)
	 */
	public User updateAgreedToTermsOfUse(long arg0, boolean arg1)
			throws SystemException, PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#updateLockout(long, boolean)
	 */
	public User updateLockout(long arg0, boolean arg1) throws SystemException,
			PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#updateOrganizations(long, long, long)
	 */
	public void updateOrganizations(long arg0, long arg1, long arg2)
			throws SystemException, PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#updatePassword(long, java.lang.String, java.lang.String, boolean)
	 */
	public User updatePassword(long arg0, String arg1, String arg2, boolean arg3)
			throws SystemException, PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#updatePortrait(long, byte[])
	 */
	public void updatePortrait(long arg0, byte[] arg1) throws SystemException,
			PortalException, RemoteException {
		return;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.UserService#updateUser(long, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, int, int, boolean, int, int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, long, long)
	 */
	public User updateUser(long arg0, String arg1, String arg2, String arg3,
			String arg4, String arg5, String arg6, String arg7, String arg8,
			String arg9, String arg10, int arg11, int arg12, boolean arg13,
			int arg14, int arg15, int arg16, String arg17, String arg18,
			String arg19, String arg20, String arg21, String arg22,
			String arg23, String arg24, long arg25, long arg26)
			throws SystemException, PortalException, RemoteException {
		return null;
	}

}
