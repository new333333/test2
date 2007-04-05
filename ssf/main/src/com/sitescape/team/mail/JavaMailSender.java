/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.mail;
import javax.mail.Session;
import org.springframework.beans.factory.BeanNameAware;

public interface JavaMailSender extends 
	org.springframework.mail.javamail.JavaMailSender {
	public String getDefaultFrom();
	public Session getSession();
	public void setSession(Session session);
	public String getName();
	public void setName(String name);
	
}
