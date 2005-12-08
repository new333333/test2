package com.sitescape.ef.module.workflow;
import java.util.List;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.context.exe.ContextInstance;

public class Notify extends AbstractActionHandler {
	  private static final long serialVersionUID = 1L;
	  private String ids;
	  private String subject, body;
	  public void setPrincipals(String ids) {
		  //need to split into list of Longs for loading
		  this.ids = ids;
	  }
	  public void setSubject(String subject) {
		  this.subject = subject;
	  }
	  public void setBody(String body) {
		  this.body = body;
	  }
	  public void execute( ExecutionContext executionContext ) throws Exception {
		  Token token = executionContext.getToken();
		  ContextInstance ctx = executionContext.getContextInstance();
		  Long entryId = (Long)ctx.getVariable("entryId");
		  String state = token.getNode().getName();
		  System.out.println("Send notification for workflow:" + 
				  subject + " " + body + " " + ids);
	  }

}
