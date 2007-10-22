package com.sitescape.team.samples.extendedws.server;

import com.sitescape.team.domain.Binder;
import com.sitescape.team.remoting.ws.FacadeImpl;

public class FacadeImpl2 extends FacadeImpl implements Facade2 {

	public String getBinderTitle(long binderId) {
		Binder binder =  getBinderModule().getBinder(binderId);
		return binder.getTitle();
	}

}
