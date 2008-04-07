package com.sitescape.team.remoting.ws.service.binder;
import java.util.List;
public interface BinderService {
	public long addBinder(String accessToken, long parentId, String definitionId, String inputDataAsXML);
	
	public String getTeamMembersAsXML(String accessToken, long binderId);
	
	public void setTeamMembers(String accessToken, long binderId, List<Long> memberIds);
	/**
	 * 
	 * @param accessToken
	 * @param binderId
	 * @param definitionIds
	 * @param workflowAssociations <Paris of entryDefinitionId,workflowDefinitionId
	 */
	public void setDefinitions(String accessToken, long binderId, List<String>definitionIds, List<String>workflowAssociations);
	/**
	 * Set function membership for a binder.  Can mix and match memberName and members
	 * <workAreaFunctionMembership>
	 * <property name="functionName">__role.visitor</property>
	 * <property name="memberName">kelly</property>
	 * <property name="memberName">jenny</property>
	 * <property name="members">1,2,3</property>
	 * </workAreaFunctionMembership>
	 * @param accessToken
	 * @param binderId
	 * @param inputDataAsXml
	 */
	public void setFunctionMembership(String accessToken, long binderId, String inputDataAsXml);
	public void setFunctionMembershipInherited(String accessToken, long binderId, boolean inherit);
	public void setOwner(String accessToken, long binderId, long userId);

}