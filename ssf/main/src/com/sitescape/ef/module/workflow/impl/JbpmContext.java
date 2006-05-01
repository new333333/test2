package com.sitescape.ef.module.workflow.impl;
import org.jbpm.db.JbpmSession;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.exe.ProcessInstance;
import org.hibernate.Session;
import org.jbpm.graph.exe.Token;
import org.jbpm.db.SchedulerSession;
//temporary until we upgrade to jbpm3.1 which requires a new hibernate
//which requires a new liferay version.
public class JbpmContext {
	JbpmSession session;
	
	public JbpmContext(JbpmSession session) {
		this.session = session;
		
	}
	public GraphSession getGraphSession() {
		return session.getGraphSession();
	}
	public void close() {
		
	}
	public ProcessInstance loadProcessInstanceForUpdate(long id) {
		return session.getGraphSession().loadProcessInstance(id);
	}
	public void save(ProcessInstance pI) {
	    session.getGraphSession().saveProcessInstance(pI);
   	}
	public void save(Token token) {
        //need to make this call to save logs
        session.getGraphSession().saveProcessInstance(token.getProcessInstance());
	}
	public Session getSession() {
		return session.getSession();
		
	}
	public Token loadTokenForUpdate(long id) {
    	return session.getGraphSession().loadToken(id);

	}
	public Token loadToken(long id) {
    	return session.getGraphSession().loadToken(id);
		
	}
	public SchedulerSession getSchedulerSession() {
   		return new SchedulerSession(session);

	}
}
