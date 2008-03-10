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

package com.sitescape.team.jobs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.util.Calendars;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SimpleTrigger;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sitescape.team.ConfigurationException;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.ical.util.ICalUtils;
import com.sitescape.team.mail.MailHelper;
import com.sitescape.team.module.mail.MailModule;
import com.sitescape.team.module.mail.MimeMessagePreparator;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.ByteArrayResource;
import com.sitescape.team.util.NLT;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SpringContextUtil;

/**
 * @author Janet McCann
 *
 */
public class DefaultSendEmail extends SSStatefulJob implements SendEmail {
	protected Log logger = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see com.sitescape.team.jobs.SSStatefulJob#doExecute(org.quartz.JobExecutionContext)
	 */
    public void doExecute(JobExecutionContext context) throws JobExecutionException {
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		Map message = (Map)jobDataMap.get("mailMessage");
		String name = (String)jobDataMap.get("mailSender");
		Date next = context.getNextFireTime();
		try {
			if (message.containsKey(SendEmail.MIME_MESSAGE)) {
				ByteArrayInputStream ios = new ByteArrayInputStream((byte[])message.get(SendEmail.MIME_MESSAGE));
				//send pre-composed message
				mail.sendMail(name, ios);
			} else {
				MimeHelper helper = new MimeHelper(message);
				mail.sendMail(name, helper);
			} 
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
			context.setResult("Success");
			return;
	   	} catch (MailSendException sx) {
    		logger.error("Error sending mail:" + sx.getLocalizedMessage());
    	} catch (MailAuthenticationException ax) {
       		logger.error("Authentication Exception:" + ax.getLocalizedMessage());				
		} catch (Exception ex) {
			//remove job
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJobOnError);
			throw new JobExecutionException(ex);
		}
		//see if we should give up
		if (next == null) {
			//end of schedule
			context.put(CleanupJobListener.CLEANUPSTATUS, CleanupJobListener.DeleteJob);
			context.setResult("Failed");
		} else {
		//will be rescheduled
		context.setResult("Failed");
		}
    }

    public boolean sendMail(String mailSenderName, Map message, String comment) {
		MimeHelper helper = new MimeHelper(message);
    	MailModule mail = (MailModule)SpringContextUtil.getBean("mailModule");
		try {
			mail.sendMail(mailSenderName, helper);
			return true;
	   	} catch (MailSendException sx) {
    		logger.error("Error sending mail:" + sx.getLocalizedMessage());
    	} catch (MailAuthenticationException ax) {
       		logger.error("Authentication Exception:" + ax.getLocalizedMessage());				
    	}
       	// at this point we want to schedule a retry
		Map newMessage = new HashMap();
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			helper.getMessage().writeTo(bos);
			newMessage.put(SendEmail.MIME_MESSAGE, bos.toByteArray());
		} catch (MessagingException io) {
			throw new MailPreparationException(NLT.get("errorcode.sendMail.cannotSerialize", new Object[] {io.getLocalizedMessage()}));
		} catch (IOException io) {
			throw new MailPreparationException(NLT.get("errorcode.sendMail.cannotSerialize", new Object[] {io.getLocalizedMessage()}));
		} finally {
			try {
				bos.close();
			} catch (Exception ex) {};
		}
		schedule(mailSenderName, newMessage, comment);
		return false;

    }	
    public void schedule(String mailSenderName, Map message, String comment) {
		Scheduler scheduler = (Scheduler)SpringContextUtil.getBean("scheduler");	 
		//each job is new = don't use verify schedule, cause this a unique
		GregorianCalendar start = new GregorianCalendar();
		start.add(Calendar.MINUTE, 1);
		
		//add time to jobName - may have multiple 
	 	String jobName =  "sendMail" + "-" + start.getTime().getTime();
	 	String className = this.getClass().getName();
	  	try {		
			JobDetail jobDetail = new JobDetail(jobName, SEND_MAIL_GROUP, 
					Class.forName(className),false, false, false);
			jobDetail.setDescription(trimDescription(comment));
			JobDataMap data = new JobDataMap();
			data.put("mailSender", mailSenderName);
			data.put("zoneId",RequestContextHolder.getRequestContext().getZoneId());
			data.put("mailMessage", message);
			
			jobDetail.setJobDataMap(data);
			jobDetail.addJobListener(getDefaultCleanupListener());
			//retry every hour
	  		SimpleTrigger trigger = new SimpleTrigger(jobName, SEND_MAIL_GROUP, jobName, SEND_MAIL_GROUP, start.getTime(), null, 24, 1000*60*60);
  			trigger.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_RESCHEDULE_NEXT_WITH_EXISTING_COUNT);
  			trigger.setDescription(comment);
  			trigger.setVolatility(false);
			scheduler.scheduleJob(jobDetail, trigger);				
 		} catch (Exception e) {
   			throw new ConfigurationException("Cannot start (job:group) " + jobName 
   					+ ":" + SEND_MAIL_GROUP, e);
   		}
    }	
    private class MimeHelper implements MimeMessagePreparator {
			MimeMessage message;
			String from;
			Map details;
			
			private MimeHelper(Map details) {
				this.details = details;
			}
			public MimeMessage getMessage() {
				return message;
			}
			public void setDefaultFrom(String from) {
				this.from = from;
			}
			public void prepare(MimeMessage mimeMessage) throws MessagingException {
				//make sure nothing saved yet
				message = null;
				//Get send tasks in email
				boolean sendVTODO = SPropsUtil.getBoolean("mail.sendVTODO", true);
			
				int multipartMode = MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED;
				
				Collection<net.fortuna.ical4j.model.Calendar> iCals = (Collection)details.get(SendEmail.ICALENDARS);
				if (iCals != null && iCals.size() > 0) {
					// Need to attach icalendar as alternative content,
					// if there is more then one icals then
					// all are merged and add ones to email as alternative content
					multipartMode = MimeMessageHelper.MULTIPART_MODE_MIXED;
				}
			
				MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipartMode);
				helper.setSubject((String)details.get(SendEmail.SUBJECT));
				if (details.containsKey(SendEmail.FROM)) 
					helper.setFrom((InternetAddress)details.get(SendEmail.FROM));
				else
					helper.setFrom(from);
				
				Collection<InternetAddress> addrs = (Collection)details.get(SendEmail.TO);
				for (InternetAddress a : addrs) {
					helper.addTo(a);
				}
				if (addrs.isEmpty()) {
					if (details.containsKey(SendEmail.FROM)) 
						helper.addTo((InternetAddress)details.get(SendEmail.FROM));
					else
						helper.addTo(from);
					helper.setSubject(NLT.get("errorcode.noRecipients") + " " + (String)details.get(SendEmail.SUBJECT));
				}
				String text = (String)details.get(SendEmail.TEXT_MSG);
				if (text == null) text="";
				String html = (String)details.get(SendEmail.HTML_MSG);
				if (html == null) html = "";
				
				// the next line creates ALTERNATIVE part, change to setText(String, boolean)
				// will couse error in iCalendar section (the ical is add as alternative content) 
				helper.setText(text, html);
				mimeMessage.addHeader(MailHelper.HEADER_CONTENT_TRANSFER_ENCODING, MailHelper.HEADER_CONTENT_TRANSFER_ENCODING_8BIT);
				Collection<FileAttachment> atts = (Collection)details.get(SendEmail.ATTACHMENTS);
				if (atts != null) {
					for (FileAttachment fAtt : atts) {
						FolderEntry entry = (FolderEntry)fAtt.getOwner().getEntity();
						DataSource ds = RepositoryUtil.getDataSourceVersioned(fAtt.getRepositoryName(), entry.getParentFolder(), 
								entry, fAtt.getFileItem().getName(), fAtt.getHighestVersion().getVersionName(), helper.getFileTypeMap());
						
						helper.addAttachment(fAtt.getFileItem().getName(), ds);
					}
				}
				
				
				if (iCals != null) {
					int c = 0;
					net.fortuna.ical4j.model.Calendar margedCalendars = null;
					for (final net.fortuna.ical4j.model.Calendar ical : iCals) {
						try {
							ByteArrayOutputStream icalOutputStream = ICalUtils.toOutputStraem(ical);
							
							String summary = ICalUtils.getSummary(ical);
							String fileName = summary + MailHelper.ICAL_FILE_EXTENSION;
							if (iCals.size() > 1) {
								fileName = summary + c + MailHelper.ICAL_FILE_EXTENSION;
							}
							
							String component = null;
							if (!ical.getComponents(Component.VTODO).isEmpty()) {
								component = Component.VTODO;
							}
							String contentType = MailHelper.getCalendarContentType(component, ICalUtils.getMethod(ical));
							DataSource dataSource = MailHelper.createDataSource(new ByteArrayResource(icalOutputStream.toByteArray()), contentType, fileName);

							MailHelper.addAttachment(fileName, new DataHandler(dataSource), helper);
							
							//If okay to send todo or not a todo build alternatative
							if (sendVTODO || !Component.VTODO.equals(component)) {
								// attach alternative iCalendar content
								if (iCals.size() == 1) {
									MailHelper.addAlternativeBodyPart(new DataHandler(dataSource), helper);
								} else {
									if (margedCalendars == null) {
										margedCalendars = new net.fortuna.ical4j.model.Calendar();
									}
									margedCalendars = Calendars.merge(margedCalendars, ical);
								}
							}
						
						} catch (IOException e) {
							logger.error(e);
						} catch (ValidationException e) {
							logger.error(e);
						}
						c++;
					}
					if (margedCalendars != null) {
						try { 
							String fileName = ICalUtils.getSummary(margedCalendars) + MailHelper.ICAL_FILE_EXTENSION;
							ByteArrayOutputStream icalOutputStream = ICalUtils.toOutputStraem(margedCalendars);
							String component = null;
							if (!margedCalendars.getComponents(Component.VTODO).isEmpty()) {
								component = Component.VTODO;
							}
							String contentType = MailHelper.getCalendarContentType(component, ICalUtils.getMethod(margedCalendars));
							DataSource dataSource = MailHelper.createDataSource(new ByteArrayResource(icalOutputStream.toByteArray()), contentType, fileName);
			
							MailHelper.addAlternativeBodyPart(new DataHandler(dataSource), helper);
						} catch (IOException e) {
							logger.error(e);
						} catch (ValidationException e) {
							logger.error("Unvalid calendar", e);
						}
					}					
				}
	
				//save message incase cannot connect and need to resend;
				message = mimeMessage;
			}

		}
    
	protected DataSource createDataSource(
		    final InputStreamSource inputStreamSource, final String contentType, final String name) {

			return new DataSource() {
				public InputStream getInputStream() throws IOException {
					return inputStreamSource.getInputStream();
				}
				public OutputStream getOutputStream() {
					throw new UnsupportedOperationException("Read-only javax.activation.DataSource");
				}
				public String getContentType() {
					return contentType;
				}
				public String getName() {
					return name;
				}
			};
		}
    
}

