
package com.sitescape.ef.jobs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
	protected Log logger = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see com.sitescape.ef.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		File file = new File((String)jobDataMap.get("mailMessage"));
		try {
			FileInputStream fs = new FileInputStream(file);
			String name = (String)jobDataMap.get("mailSender");
			if (mail.sendMail(name, fs) == true) {
				context.setResult(CleanupJobListener.DeleteJob);
			}
		} catch (ConfigurationException cf) {
			throw new JobExecutionException(cf);
		} catch (FileNotFoundException fe) {
			//remove job
			context.setResult(CleanupJobListener.DeleteJob);
			logger.error("Mail file missing (" + file.getName() + ") - job cancelled");
		}
    }

}
