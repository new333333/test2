package com.sitescape.team.client.ws.template;

import com.sitescape.team.client.ws.WebServiceClient;

public class TemplateService extends WebServiceClient implements TemplateServiceSoap {

	public TemplateService() {
		super("TemplateService");
	}

	public long addBinder(String accessToken, long parentBinderId, long binderConfigId, String title) {
		Long binderId = (Long)
			fetch("addBinder", new Object[] {accessToken, new Long(parentBinderId), new Long(binderConfigId), title});
		return binderId.longValue();
	}

}
