package com.sitescape.team.remoting.ws.service.binder;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.util.SpringContextUtil;


public class BinderServiceEndpoint implements ServiceLifecycle, BinderService {
	private BinderService binderService;

	protected BinderService getBinderService() {
		return binderService;
	}
	
	public long addBinder(String accessToken, long parentId, String definitionId, String inputDataAsXML) {
		return getBinderService().addBinder(accessToken, parentId, definitionId, inputDataAsXML);
	}
	public void setDefinitions(String accessToken, long binderId, String[] definitionIds, String[] workflowAssociations) {
		getBinderService().setDefinitions(accessToken, binderId, definitionIds, workflowAssociations);

	}
	public String getTeamMembersAsXML(String accessToken, long binderId) {
		return getBinderService().getTeamMembersAsXML(accessToken, binderId);
	}

	public void setTeamMembers(String accessToken, long binderId, String[] memberNames) {
		getBinderService().setTeamMembers(accessToken, binderId, memberNames);
	}
	public void setFunctionMembership(String accessToken, long binderId, String inputDataAsXml) {
		getBinderService().setFunctionMembership(accessToken, binderId, inputDataAsXml);
	}
	public void setFunctionMembershipInherited(String accessToken, long binderId, boolean inherit) {
		getBinderService().setFunctionMembershipInherited(null, binderId, inherit);
	}
	public void setOwner(String accessToken, long binderId, long userId) {
		getBinderService().setOwner(null, binderId, userId);
	}
	public void indexBinder(String accessToken, long binderId) {
		getBinderService().indexBinder(accessToken, binderId);
	}
	public void init(Object context) throws ServiceException {
		this.binderService = (BinderService) SpringContextUtil.getBean("binderService");
	}
	
	public void destroy() {
	}

}
