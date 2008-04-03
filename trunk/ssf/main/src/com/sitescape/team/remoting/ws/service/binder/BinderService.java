package com.sitescape.team.remoting.ws.service.binder;
import java.util.List;
public interface BinderService {
	public long addBinder(String accessToken, long parentId, String definitionId, String inputDataAsXML);
	
	public String getTeamMembersAsXML(String accessToken, long binderId);
	
	public void setTeamMembers(String accessToken, long binderId, List<Long> memberIds);
}
