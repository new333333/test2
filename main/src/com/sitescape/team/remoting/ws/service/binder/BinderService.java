package com.sitescape.team.remoting.ws.service.binder;

public interface BinderService {
	public long binder_addBinder(String accessToken, long parentId, String definitionId, String inputDataAsXML);
	
	public String binder_getTeamMembersAsXML(String accessToken, long binderId);
	
	public void binder_setTeamMembers(String accessToken, long binderId, String[] memberNames);
	/**
	 * 
	 * @param accessToken
	 * @param binderId
	 * @param definitionIds
	 * @param workflowAssociations <Pairs of entryDefinitionId,workflowDefinitionId
	 */
	public void binder_setDefinitions(String accessToken, long binderId, String[] definitionIds, String[] workflowAssociations);
	/**
	 * Set function membership for a binder.  Can mix and match memberName and/or members
	 * <workAreaFunctionMemberships>
	 * <workAreaFunctionMembership>
	 * <property name="functionName">__role.visitor</property>
	 * <property name="memberName">kelly</property>
	 * <property name="memberName">jenny</property>
	 * <property name="members">1,2,3</property>
	 * </workAreaFunctionMembership>
	 * </workAreaFunctionMemberships>
	 * @param accessToken
	 * @param binderId
	 * @param inputDataAsXml
	 */
	public void binder_setFunctionMembership(String accessToken, long binderId, String inputDataAsXml);
	public void binder_setFunctionMembershipInherited(String accessToken, long binderId, boolean inherit);
	public void binder_setOwner(String accessToken, long binderId, long userId);
	public void binder_indexBinder(String accessToken, long binderId);
}