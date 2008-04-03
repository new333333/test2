package com.sitescape.team.remoting.ws.service.binder;

import java.util.List;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.server.ServiceLifecycle;

import com.sitescape.team.util.SpringContextUtil;


public class BinderServiceEndpoint implements ServiceLifecycle, BinderService {
	private BinderService binderService;

	protected BinderService getBinderService() {
		return binderService;
	}
	
	public long addBinder(String accessToken, long parentId, String definitionId, String inputDataAsXML) {
		return getBinderService().addBinder(accessToken, parentId, definitionId, inputDataAsXML);
	}
	public String getTeamMembersAsXML(String accessToken, long binderId) {
		return getBinderService().getTeamMembersAsXML(accessToken, binderId);
	}

	public void setTeamMembers(String accessToken, long binderId, List<Long> memberIds) {
		getBinderService().setTeamMembers(accessToken, binderId, memberIds);
	}
	public void init(Object context) throws ServiceException {
		this.binderService = (BinderService) SpringContextUtil.getBean("binderService");
	}
	
	public void destroy() {
	}
}
