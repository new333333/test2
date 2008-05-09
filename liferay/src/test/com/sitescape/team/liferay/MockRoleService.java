/**
 * 
 */
package com.sitescape.team.liferay;

import java.rmi.RemoteException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.liferay.portal.PortalException;
import com.liferay.portal.SystemException;
import com.liferay.portal.model.Role;
import com.liferay.portal.model.impl.RoleImpl;
import com.liferay.portal.service.RoleService;
import com.liferay.portal.util.RoleNames;

/**
 * @author dml
 *
 */
public class MockRoleService implements RoleService {
	
	private Map<Long, Role> rolesById = new HashMap<Long, Role>();
	private Map<String, Role> rolesByName = new HashMap<String, Role>();
	private SecureRandom random = new SecureRandom();
	
	public MockRoleService() {
		RoleImpl administrator = new RoleImpl();
		administrator.setName(RoleNames.ADMINISTRATOR);
		addRole(administrator);
	}
	
	private synchronized void addRole(Role role) {
		role.setRoleId(random.nextLong());
		Role r0 = rolesById.put(role.getRoleId(), role);
		while (r0 != null) {
			rolesById.put(r0.getRoleId(), r0);
			role.setRoleId(random.nextLong());
			r0 = rolesById.put(role.getRoleId(), role);
		}
		rolesByName.put(role.getName(), role);		
	}
	
	private synchronized void deleteRole(Long roleId) {
		Role r = rolesById.remove(roleId);
		rolesByName.remove(r.getName());
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.RoleService#addRole(java.lang.String, int)
	 */
	@Override
	public Role addRole(String name, int type) throws SystemException,
			PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.RoleService#deleteRole(long)
	 */
	@Override
	public void deleteRole(long roleId) throws SystemException,
			PortalException, RemoteException {
		deleteRole(new Long(roleId));
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.RoleService#getGroupRole(long, long)
	 */
	@Override
	public Role getGroupRole(long companyId, long groupId)
			throws SystemException, PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.RoleService#getRole(long)
	 */
	@Override
	public Role getRole(long roleId) throws SystemException, PortalException,
			RemoteException {
		return rolesById.get(roleId);
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.RoleService#getRole(long, java.lang.String)
	 */
	@Override
	public Role getRole(long companyId, String name) throws SystemException,
			PortalException, RemoteException {
		return rolesByName.get(name);
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.RoleService#getUserGroupRoles(long, long)
	 */
	@Override
	public List getUserGroupRoles(long userId, long groupId)
			throws SystemException, PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.RoleService#getUserRelatedRoles(long, java.util.List)
	 */
	@Override
	public List getUserRelatedRoles(long userId, List groups)
			throws SystemException, PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.RoleService#getUserRoles(long)
	 */
	@Override
	public List getUserRoles(long userId) throws SystemException,
			PortalException, RemoteException {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.RoleService#hasUserRole(long, long, java.lang.String, boolean)
	 */
	@Override
	public boolean hasUserRole(long userId, long companyId, String name,
			boolean inherited) throws SystemException, PortalException,
			RemoteException {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.RoleService#hasUserRoles(long, long, java.lang.String[], boolean)
	 */
	@Override
	public boolean hasUserRoles(long userId, long companyId, String[] names,
			boolean inherited) throws SystemException, PortalException,
			RemoteException {
		return false;
	}

	/* (non-Javadoc)
	 * @see com.liferay.portal.service.RoleService#updateRole(long, java.lang.String)
	 */
	@Override
	public Role updateRole(long roleId, String name) throws SystemException,
			PortalException, RemoteException {
		return null;
	}

}
