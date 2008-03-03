package com.sitescape.team.tools.ws;

public class TemplateService extends WebServiceClient implements
		com.sitescape.team.remoting.ws.service.template.TemplateService {

	public TemplateService() {
		super("TemplateService");
	}

	public long addBinder(String accessToken, long parentBinderId, long binderConfigId, String title) {
		Long binderId = (Long)
			fetch("addBinder", new Object[] {accessToken, new Long(parentBinderId), new Long(binderConfigId), title});
		return binderId.longValue();
	}

}
