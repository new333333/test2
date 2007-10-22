package com.sitescape.team.samples.extendedws.server;

import com.sitescape.team.remoting.ws.JaxRpcFacade;

public class JaxRpcFacade2 extends JaxRpcFacade implements Facade2 {

	// Unfortunately, the "facade" variable is a private variable in the super
	// class. So we have this clumzy workaround where we declare another
	// variable to hold a reference to the same implementing class that we can 
	// access from here.
	private Facade2 facade2;

	protected void onInit() {
		super.onInit();
		this.facade2 = (Facade2) getWebApplicationContext().getBean("wsFacade");
	}

	public String getBinderTitle(long binderId) {
		return this.facade2.getBinderTitle(binderId);
	}
}
