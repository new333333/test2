package com.sitescape.ef.module.mail;
import javax.mail.Session;
import org.springframework.beans.factory.BeanNameAware;

public interface JavaMailSender extends 
	org.springframework.mail.javamail.JavaMailSender, BeanNameAware {
	public String getBeanName();
	public String getDefaultFrom();
	public Session getSession();
}
