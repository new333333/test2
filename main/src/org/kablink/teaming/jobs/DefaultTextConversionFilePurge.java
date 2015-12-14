/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.jobs;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.ZoneConfig;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.util.FileStore;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author jong
 *
 */
public class DefaultTextConversionFilePurge extends SSCronTriggerJob implements TextConversionFilePurge {

	private static final Log logger = LogFactory.getLog(DefaultTextConversionFilePurge.class);
	
	@Override
	protected void doExecute(JobExecutionContext context)
			throws JobExecutionException {
		
		//See if it is time to purge the text conversion file cache folder
		Long maxDirSize = SPropsUtil.getLongObject("max.textConversion.cache.size", 0L);
		if (maxDirSize > 0) {
			//There is a limit for the text conversion cache. Go check if it is exceeded
			FileStore cacheFileStoreText = new FileStore(SPropsUtil.getString("cache.file.store.dir"), ObjectKeys.CONVERTER_DIR_TEXT);
			File cacheDir = new File(cacheFileStoreText.getRootPath() + File.separator + Utils.getZoneKey());
			if (!Utils.getZoneKey().equals("") && cacheDir != null && cacheDir.exists()) {
				//Get the dir size
				long dirSize = FileUtils.sizeOfDirectory(cacheDir);
				if (dirSize > maxDirSize) {
					String cacheDirPath = cacheDir.getAbsolutePath();
					logger.info("Deleting text conversion cache directory ("+cacheDirPath+") - current cache directory size: " + String.valueOf(dirSize));
					try {
						FileUtils.deleteDirectory(cacheDir);
					} catch(Exception e) {
						logger.warn("Could not delete text conversion cache directory ("+cacheDirPath+") - " + e.getMessage());
					}
				}
			}
		}
	}

	@Override
	public void enable(boolean enable, Long zoneId) {
		enable(enable, new CronJobDescription(zoneId, zoneId.toString(),TEXT_CONVERSION_FILE_PURGE_GROUP, TEXT_CONVERSION_FILE_PURGE_DESCRIPTION));
	}

	@Override
	public ScheduleInfo getScheduleInfo(Long zoneId) {
		return getScheduleInfo(new CronJobDescription(zoneId, zoneId.toString(),TEXT_CONVERSION_FILE_PURGE_GROUP, TEXT_CONVERSION_FILE_PURGE_DESCRIPTION));
	}

	@Override
	public void setScheduleInfo(ScheduleInfo schedulerInfo) {
		setScheduleInfo(new CronJobDescription(schedulerInfo.getZoneId(), schedulerInfo.getZoneId().toString(),TEXT_CONVERSION_FILE_PURGE_GROUP, TEXT_CONVERSION_FILE_PURGE_DESCRIPTION), schedulerInfo);
	}

}
