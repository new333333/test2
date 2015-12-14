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
package org.kablink.teaming.jobs;

import java.util.Date;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * A Net Folder Server used to be called a net folder root
 */
public class DefaultNetFolderServerSynchronization extends SSCronTriggerJob 
	implements NetFolderServerSynchronization
{
	/**
	 * 
	 */
	public class SyncJobDescription extends CronJobDescription 
	{
		private Long m_serverId;
		
		/**
		 * 
		 * @param zoneId
		 * @param serverId
		 */
		public SyncJobDescription( Long zoneId, Long serverId )
		{
			super(
				zoneId,
				serverId.toString(),
				SYNCHRONIZATION_GROUP,
				SYNCHRONIZATION_DESCRIPTION + serverId,
				false,
				SPropsUtil.getInt("job.net.folder.server.synchronization.priority", 4));
			this.m_serverId = serverId;
		}
		
		/**
		 * 
		 * @return
		 */
		public Long getServerId() 
		{
    		return m_serverId;
    	}
		
		/**
		 * 
		 */
    	@Override
		@SuppressWarnings("unchecked")
		public ScheduleInfo getDefaultScheduleInfo() 
    	{
    		ScheduleInfo info;
    	
    		info = super.getDefaultScheduleInfo();
    		info.getDetails().put( "lastNotification", new Date() );
    		info.getDetails().put( "serverId", m_serverId );
    		info.getDetails().put( USERID, RequestContextHolder.getRequestContext().getUserId() );
    		info.getDetails().put( ZONEID, zoneId );
    		
    		return info;
    	}
	}

	/**
	 * 
	 */
    @Override
	public void doExecute( JobExecutionContext context ) throws JobExecutionException 
    {
    	ResourceDriverModule resourceDriverModule;

    	resourceDriverModule = (ResourceDriverModule)SpringContextUtil.getBean( "resourceDriverModule" );

    	try 
    	{
			Long serverId = null;
		
			try 
			{
				ResourceDriverConfig rdConfig;
				
				serverId = new Long( jobDataMap.getLong( "serverId" ) );
				rdConfig = (ResourceDriverConfig) getCoreDao().load( ResourceDriverConfig.class, serverId );
			} 
			catch ( Exception ex )
			{
				serverId = new Long( -1 );
			}
			
			if ( serverId.equals( new Long( -1 ) ) )
			{
				deleteJob( context );
			}
			else
			{
				resourceDriverModule.enqueueSynchronize( serverId, true );
			}
		} 
    	catch ( NoBinderByTheIdException nf )
    	{
			// Apparently the net folder server on which this scheduler is defined has been removed.
			// This is not an error. So simply remove the job.
			deleteJob( context );
		} 
    }
	
    /**
     * 
     */
	@Override
	public ScheduleInfo getScheduleInfo( Long serverId )
	{
		return getScheduleInfo( new SyncJobDescription( RequestContextHolder.getRequestContext().getZoneId(), serverId ) );
	}
	
	/**
	 * 
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setScheduleInfo( ScheduleInfo info, Long serverId )
	{
		info.getDetails().put( USERID, RequestContextHolder.getRequestContext().getUserId() );
		info.setZoneId( RequestContextHolder.getRequestContext().getZoneId() );
		info.getDetails().put( ZONEID, RequestContextHolder.getRequestContext().getZoneId() );
		info.getDetails().put( "serverId", serverId );
		
		setScheduleInfo( new SyncJobDescription( info.getZoneId(), serverId ), info );
	}
	
	@Override
	public void deleteJob(Long netFolderServerId) {
		// Try deleting the job directly
		if(super.deleteJob(netFolderServerId.toString(), SYNCHRONIZATION_GROUP))
			return; // no error
			
		// For whatever reason, could not delete the job (probably because the job is currently executing or something)
		ScheduleInfo si = this.getScheduleInfo(netFolderServerId);
		// Now, here's a backup strategy - Enable the job if currently disabled. This way, the job will
		// run, realize that the binder is gone, and self-clean itself by removing the job.
		if (si != null && !si.isEnabled()) {
			si.setEnabled(true);
			this.setScheduleInfo(si, netFolderServerId);
		}
	}
}
