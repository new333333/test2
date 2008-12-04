/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */

package org.kablink.teaming.jobs;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.kablink.teaming.extension.ExtensionDeployer;
import org.kablink.teaming.util.DirPath;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.util.FileUtil;
import org.kablink.util.Validator;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 */
public class DefaultDeployExtension extends SimpleTriggerJob implements DeployExtension {
	 
    /*
     * The bulk of this code is taken from org.jbpm.scheduler.impl.SchedulerThread
     * @see org.kablink.teaming.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
     */

	public void doExecute(JobExecutionContext context) throws JobExecutionException {	
		ExtensionDeployer work = (ExtensionDeployer)SpringContextUtil.getBean("extDeployer");
		String pathName = jobDataMap.getString(DEPLOY_EXTENSION_PATHNAME);
		if (Validator.isNull(pathName)) pathName = DirPath.getExtensionDeployPath() + File.separator + Utils.getZoneKey();
		File deployDir = new File(pathName);		
		if (!deployDir.exists()) deployDir.mkdirs();
			
		File[] extensions = deployDir.listFiles(new FileOnlyFilter());
		if (extensions != null) {
			for (int i=0; i<extensions.length; ++i) {
				File successDir = new File(deployDir, "deployed");
				if (!successDir.exists()) successDir.mkdirs();
				File extension = extensions[i];
				try {
					work.deploy(extension);
					FileUtil.move(extension, new File(successDir, extension.getName()));					
				} catch (IOException e) {
					logger.warn("Unable to open extension WAR at " + extension.getPath(),
						e);
					File failedDir = new File(deployDir, "failed");
					if (!failedDir.exists()) failedDir.mkdirs();
					FileUtil.move(extension, new File(failedDir, extension.getName()));	
				}
			}
		}
	}

	public void remove(Long zoneId) {
		removeJob(zoneId.toString(), DEPLOY_EXTENSION_GROUP);		
	}
    public void schedule(Long zoneId, String pathName, int seconds) {
		schedule(new JobDescription(zoneId, pathName, seconds));
    }
    protected class JobDescription extends SimpleJobDescription {
    	String pathName;
    	JobDescription(Long zoneId, String pathName, int seconds) {
    		super(zoneId, zoneId.toString(), DEPLOY_EXTENSION_GROUP, DEPLOY_EXTENSION_DESCRIPTION, seconds );
    		this.pathName = pathName;
    	}
    	protected JobDataMap getData() {
    		JobDataMap data = super.getData();
   			data.put(DEPLOY_EXTENSION_PATHNAME, pathName);	
    		return data;
		}
    }
	protected class FileOnlyFilter implements FileFilter {
	    public boolean accept(File file) {
	        return (!file.isDirectory());
	    }
	}

}
