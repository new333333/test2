package com.sitescape.team.remoting.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.LoginInfo;
import com.sitescape.team.module.report.ReportModule;

public class LoginInfoInterceptor implements MethodInterceptor {
	
	private ReportModule reportModule;

	protected ReportModule getReportModule() {
		return reportModule;
	}

	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}

	public Object invoke(MethodInvocation invocation) throws Throwable {
		getReportModule().addLoginInfo(new LoginInfo(LoginInfo.AUTHENTICATOR_WS,
				RequestContextHolder.getRequestContext().getUserId()));		
		
		return invocation.proceed();
	}

}



