
package com.sitescape.ef.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.NoUserByTheIdException;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.module.mail.MailModule;
import org.springframework.mail.SimpleMailMessage;
/**
 * @author Janet McCann
 *
 */
public class SendMail extends SSStatefulJob {

	/* (non-Javadoc)
	 * @see com.sitescape.ef.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailModule mail = (MailModule)ctx.getBean("mailModule");
		try {
			SimpleMailMessage msg = (SimpleMailMessage)jobDataMap.get("mailMessage");
			if (mail.sendMail(msg) == true) {
				context.setResult(CleanupJobListener.DeleteJob);
			}
		} catch (ConfigurationException cf) {
			throw new JobExecutionException(cf);
		}
    }

}
