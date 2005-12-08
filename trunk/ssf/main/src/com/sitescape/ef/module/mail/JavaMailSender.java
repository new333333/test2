package com.sitescape.ef.module.mail;
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
