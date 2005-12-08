
package com.sitescape.ef.jobs;

import javax.mail.internet.MimeMessage;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.sitescape.ef.ConfigurationException;
import com.sitescape.ef.module.mail.MailModule;
import com.sitescape.ef.util.SpringContextUtil;
/**
 * @author Janet McCann
 *
 */
public class SendMail extends SSStatefulJob {

	/* (non-Javadoc)
	 * @see com.sitescape.ef.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		try {
			MimeMessage msg = (MimeMessage)jobDataMap.get("mailMessage");
			if (mail.sendMail(msg) == true) {
				context.setResult(CleanupJobListener.DeleteJob);
			}
		} catch (ConfigurationException cf) {
			throw new JobExecutionException(cf);
		}
    }

}
