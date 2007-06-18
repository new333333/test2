package com.sitescape.team.module.license.impl;

import org.springframework.beans.factory.InitializingBean;

import com.sitescape.team.module.impl.CommonDependencyInjection;
import com.sitescape.team.module.license.LicenseModule;

public class AbstractLicenseModule extends CommonDependencyInjection
implements LicenseModule, InitializingBean {

	public void afterPropertiesSet() throws Exception {
		// TODO whatever you need to do to initialize the module... such as
		// initializing a background job, etc.
	}

}
