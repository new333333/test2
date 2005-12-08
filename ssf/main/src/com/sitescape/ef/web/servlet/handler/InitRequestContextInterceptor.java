package com.sitescape.ef.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.sitescape.ef.context.request.RequestContextUtil;

public class InitRequestContextInterceptor extends HandlerInterceptorAdapter {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
	    
		// TODO We need to obtain zone name and user name appropriately. 
		
		RequestContextUtil.setThreadContext("liferay.com", "wf_admin");
		
	    return true;
	}
	
    public void afterCompletion(javax.servlet.http.HttpServletRequest request,
            javax.servlet.http.HttpServletResponse response,
            java.lang.Object handler,
            java.lang.Exception ex)
     throws java.lang.Exception {
        
    	RequestContextUtil.clearThreadContext();
    }

}
