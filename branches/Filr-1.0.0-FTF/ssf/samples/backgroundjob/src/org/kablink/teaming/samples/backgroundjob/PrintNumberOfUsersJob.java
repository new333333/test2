/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.samples.backgroundjob;

import java.util.Date;

import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;


public class PrintNumberOfUsersJob extends QuartzJobBean {
	// ProfileModule is wired in the Spring context config file and the Spring
	// automatically injects it at startup time.
	private ProfileModule profileModule;
	private String zoneName;
	private String userName;
	
	public ProfileModule getProfileModule() {
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getZoneName() {
		return zoneName;
	}
	public void setZoneName(String zoneName) {
		this.zoneName = zoneName;
	}
	
	// This is the actual method that gets executed by the scheduler.
	protected void executeInternal(final JobExecutionContext ctx) throws JobExecutionException {
		// This sample job executes in the context of the specified user within 
		// the specified zone. If your job does not require any user context 
		// (eg. it is doing something totally outside of Teaming domain model 
		// such as interacting with a 3rd party remote server, etc.), then do
		// not use RunasTemplate wrapper.
		RunasTemplate.runas(new RunasCallback() {
			public Object doAs() {
				doIt(ctx);
				return null;
			}
		}, zoneName, userName);		
	}

	private void doIt(JobExecutionContext ctx) {
		// This method simply prints the current count of the registered users in the system.
		
		int count = getProfileModule().getUsers().size();
		
		System.out.println("(" + new Date().toString() + ") " 
				+ this.getClass().getSimpleName() 
				+ " - Number of registered users in the system is " 
				+ count);
	}
}
