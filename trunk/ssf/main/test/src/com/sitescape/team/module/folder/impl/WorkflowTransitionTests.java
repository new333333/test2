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
package com.sitescape.team.module.folder.impl;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.dom4j.io.SAXReader;
import org.jbpm.JbpmContext;
import org.jbpm.scheduler.exe.Timer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.impl.FolderDaoImpl;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Definition;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.module.workflow.impl.WorkflowFactory;
import com.sitescape.team.module.workflow.impl.WorkflowModuleImpl;
import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.util.cal.Duration;

public class WorkflowTransitionTests extends AbstractTestBase {

	protected WorkflowModuleImpl wfi;
	protected FolderDaoImpl fdi;
	private static String zoneName ="testZone";
	protected String[] getConfigLocations() {
		return new String[] {"/com/sitescape/team/module/folder/impl/applicationContext-workflowTransition.xml"};
	}
	
	/*
	 * This method is provided to set the CoreDaoImpl instance being tested
	 * by the Dependency Injection, which is done automatically by the
	 * superclass.
	 */
	public void setWorkflowModule(WorkflowModuleImpl wfi) {
		this.wfi = wfi;
	}
	public void setFolderDao(FolderDaoImpl fdi) {
		this.fdi = fdi;
	}
	public void testManualTransition() {
		try {
			Workspace top = createZone(zoneName);
			Folder folder = createFolder(top, "testFolder");
			FolderEntry entry = createEntry(folder);
		
			Definition workflowDef = importWorkflow(top, "testManual");
			wfi.addEntryWorkflow(entry, entry.getEntityIdentifier(), workflowDef);
			WorkflowState ws = checkState(entry, "start");
			wfi.modifyWorkflowState(entry, ws, "state1");
			checkState(ws, "state1");
			wfi.modifyWorkflowState(entry, ws, "state2");
			checkState(ws, "state2");
			wfi.modifyWorkflowState(entry, ws, "end");
			checkState(ws, "end");
		
			entry = createEntry(folder);		
			wfi.addEntryWorkflow(entry, entry.getEntityIdentifier(), workflowDef);
			ws = checkState(entry, "start");
			wfi.modifyWorkflowState(entry, ws, "state2");
			checkState(ws, "state2");
			wfi.modifyWorkflowState(entry, ws, "end");
			checkState(ws, "end");
		} finally {
			
		}
		
	}
	/**
	 * Test setting variables on transition or when entering/exitting a state.
	 * Also tests transitions on variables.
	 *
	 */
	public void testVariableTransition() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");

		try {
			FolderEntry entry = createEntry(folder);
		
			Definition workflowDef = importWorkflow(top, "testTransitionOnVariable");
			wfi.addEntryWorkflow(entry, entry.getEntityIdentifier(), workflowDef);
			WorkflowState ws = checkState(entry, "start");
			//This transition will set a variable, which will cause state1 to
			//automatically transition to state2.  On entry to state2, 
			//a variable will be set again.  This new setting will cause state2 to transition
			//to state end.
			wfi.modifyWorkflowState(entry, ws, "state1");
			checkState(ws, "end");

			entry = createEntry(folder);
			wfi.addEntryWorkflow(entry, entry.getEntityIdentifier(), workflowDef);
			ws = checkState(entry, "start");
			//This transition will set a variable, which will cause state3 to
			//automatically transition to state4.  On EXIT from state3, 
			//a variable will be set again.  This new setting will cause state4 to transition
			//to state end.
			wfi.modifyWorkflowState(entry, ws, "state3");
			checkState(ws, "end");
		} finally {
			
		}
		
	}
	/**
	 * Test transitions when entry data is modified.
	 *
	 */
	public void testDataTransition() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");

		try {
			Definition commandDef = importCommand(top, "testEntry");
		
			Definition workflowDef = importWorkflow(top, "testDataTransitions");
			FolderEntry entry = createEntry(folder);
			entry.setEntryDef(commandDef);
			wfi.addEntryWorkflow(entry, entry.getEntityIdentifier(), workflowDef);
			WorkflowState ws = checkState(entry, "start");
			wfi.modifyWorkflowState(entry, ws, "singleSelect");
			checkState(ws, "singleSelect");
			//Set the value of the radio field and check transitions
			CustomAttribute attr = entry.addCustomAttribute("singleSelect", new String[] {"blue"});
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "blue");
			wfi.modifyWorkflowState(entry, ws, "start");
			attr.setValue(new String[] {"purple"});
			wfi.modifyWorkflowState(entry, ws, "singleSelect");
			//should go right to no
			checkState(ws, "purple");
			wfi.modifyWorkflowState(entry, ws, "start");

			//check radio button
			wfi.modifyWorkflowState(entry, ws, "radio");
			checkState(ws, "radio");
			//Set the value of the radio field and check transitions
			attr = entry.addCustomAttribute("radio", "yes");
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "yes");
			wfi.modifyWorkflowState(entry, ws, "start");
			attr.setValue("no");
			wfi.modifyWorkflowState(entry, ws, "radio");
			//should go right to no
			checkState(ws, "no");
			wfi.modifyWorkflowState(entry, ws, "start");
			attr.setValue("maybe");
			wfi.modifyWorkflowState(entry, ws, "radio");
			//should go right to maybe
			checkState(ws, "maybe");
			wfi.modifyWorkflowState(entry, ws, "start");

			//check checkBox
			wfi.modifyWorkflowState(entry, ws, "start");
			wfi.modifyWorkflowState(entry, ws, "checkBox");
			checkState(ws, "checkBox");
			//Set the value of the radio field and check transitions
			attr = entry.addCustomAttribute("checkBox", Boolean.FALSE);
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "notChecked");
			wfi.modifyWorkflowState(entry, ws, "start");
			attr.setValue(Boolean.TRUE);
			wfi.modifyWorkflowState(entry, ws, "checkBox");
			//should go right to no
			checkState(ws, "checked");
			wfi.modifyWorkflowState(entry, ws, "start");

			wfi.modifyWorkflowState(entry, ws, "end");
		} finally {
			
		}
		
	}
	/**
	 * Test transitions on a date field
	 * This isn't an exact real scenerio since we are forcing the checks by
	 * calling "modifyWorkflowStateOnUpdate", instead of allowing the system timer
	 * to do the work.
	 *
	 */
	public void testDateTransition() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");

		try {
			Definition commandDef = importCommand(top, "testEntry");
		
			Definition workflowDef = importWorkflow(top, "testDateTransitions");
			FolderEntry entry = createEntry(folder);
			entry.setEntryDef(commandDef);
			wfi.addEntryWorkflow(entry, entry.getEntityIdentifier(), workflowDef);
			WorkflowState ws = checkState(entry, "start");
			wfi.modifyWorkflowState(entry, ws, "doDates");
			checkState(ws, "doDates");
			Date passed = new Date(0);
			CustomAttribute attr = entry.addCustomAttribute("datePassed", passed);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "datePassed");
			entry.removeCustomAttribute("datePassed");
			wfi.modifyWorkflowState(entry, ws, "doDates");
			checkState(ws, "doDates");
			passed = new Date();
			passed.setTime(passed.getTime() + 50000);
			attr = entry.addCustomAttribute("dateBefore", passed);
			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "dateBefore");
			entry.removeCustomAttribute("dateBefore");
			wfi.modifyWorkflowState(entry, ws, "doDates");
			checkState(ws, "doDates");
			passed = new Date();
			passed.setTime(passed.getTime() - 70000);
			attr = entry.addCustomAttribute("dateAfter", passed);
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "dateAfter");
	} finally {
		}
		
	}

	public void testEventNoRecurTransition() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");

		try {
			Definition commandDef = importCommand(top, "testEntry");
		
			Definition workflowDef = importWorkflow(top, "testDateTransitions");
			FolderEntry entry = createEntry(folder);
			entry.setEntryDef(commandDef);
			wfi.addEntryWorkflow(entry, entry.getEntityIdentifier(), workflowDef);
			WorkflowState ws = checkState(entry, "start");
			wfi.modifyWorkflowState(entry, ws, "doNoRecur");
			checkState(ws, "doNoRecur");
			
			//event started 1 minute ago
			GregorianCalendar cal = new GregorianCalendar();
			Event event = new Event();
			cdi.save(event);
			Date date = new Date();
			cal.setTimeInMillis(date.getTime()-60000);
			CustomAttribute attr = entry.addCustomAttribute("eventStarted", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "eventStarted");
			entry.removeCustomAttribute("eventStarted");
			wfi.modifyWorkflowState(entry, ws, "doNoRecur");
			checkState(ws, "doNoRecur");

			//event started 1 minute ago and lasted 30 seconds
			cal = new GregorianCalendar();
			date = new Date();
			cal.setTimeInMillis(date.getTime()-60*1000);
			event = new Event(cal, new Duration(0,0,30));
			cdi.save(event);
			attr = entry.addCustomAttribute("eventEnded", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "eventEnded");
			entry.removeCustomAttribute("eventEnded");
			wfi.modifyWorkflowState(entry, ws, "doNoRecur");

			//event starts in 59 minutes and lasts 30 seconds
			cal = new GregorianCalendar();
			date = new Date();
			//Test 1 hour before trigger
			cal.setTimeInMillis(date.getTime()+59*60*1000);
			event = new Event(cal, new Duration(0,0,30));
			cdi.save(event);
			attr = entry.addCustomAttribute("eventBeforeStart", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "eventBeforeStart");
			entry.removeCustomAttribute("eventBeforeStart");
			wfi.modifyWorkflowState(entry, ws, "doNoRecur");

			//event started 26 minutes ago, and lasts for 30 minues
			cal = new GregorianCalendar();
			date = new Date();
			//check for 5 minutes before end 
			cal.setTimeInMillis(date.getTime()-26*60*1000);
			event = new Event(cal, new Duration(0,30,0));
			cdi.save(event);
			attr = entry.addCustomAttribute("eventBeforeEnd", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "eventBeforeEnd");
			entry.removeCustomAttribute("eventBeforeEnd");
			wfi.modifyWorkflowState(entry, ws, "doNoRecur");
			
			//event started 1 day and 1 minute ago and lasted 30 seconds
			cal = new GregorianCalendar();
			date = new Date();
			//test 1 day after trigger
			cal.setTimeInMillis(date.getTime()-24*61*60*1000);
			event = new Event(cal, new Duration(0,0,30));
			cdi.save(event);
			attr = entry.addCustomAttribute("eventAfterStart", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "eventAfterStart");
			entry.removeCustomAttribute("eventAfterStart");
			wfi.modifyWorkflowState(entry, ws, "doNoRecur");

			//event started 2 minutes ago and lasted 30 seconds
			cal = new GregorianCalendar();
			date = new Date();
			//test 1 minute after end
			cal.setTimeInMillis(date.getTime()-120*1000);
			event = new Event(cal, new Duration(0,0,30));
			cdi.save(event);
			attr = entry.addCustomAttribute("eventAfterEnd", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "eventAfterEnd");
			entry.removeCustomAttribute("eventAfterEnd");
			wfi.modifyWorkflowState(entry, ws, "doNoRecur");
	} finally {
		}
		
	}
	public void testEventRecurTransition() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");
		
		try {
			Definition commandDef = importCommand(top, "testEntry");
		
			Definition workflowDef = importWorkflow(top, "testDateTransitions");
			FolderEntry entry = createEntry(folder);
			entry.setEntryDef(commandDef);
			wfi.addEntryWorkflow(entry, entry.getEntityIdentifier(), workflowDef);
			WorkflowState ws = checkState(entry, "start");
			wfi.modifyWorkflowState(entry, ws, "doRecur");
			checkState(ws, "doRecur");
			
			GregorianCalendar cal = new GregorianCalendar();
			Event event = new Event();
			cdi.save(event);
			Date date = new Date();
			//event started 1 second ago, lasts 1 second and repeats every seconds
			cal.setTimeInMillis(date.getTime()-1000);
			event.setDuration(new Duration(0, 0, 1));
			event.setFrequency("SECONDLY");
			CustomAttribute attr = entry.addCustomAttribute("eventStarted", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "eventStarted");
			entry.removeCustomAttribute("eventStarted");
			wfi.modifyWorkflowState(entry, ws, "doRecur");
			checkState(ws, "doRecur");

			//event starts hourly and lasts 30 seconds.  Schedule 1st occurence
			//2 hours and 2 minutes earlier
			cal = new GregorianCalendar();
			date = new Date();
			//Test 1 hour before trigger
			cal.setTimeInMillis(date.getTime()-2*60*60*1000-2*60*1000);
			event = new Event(cal, new Duration(0,0,30));
			event.setFrequency("HOURLY");
			cdi.save(event);
			attr = entry.addCustomAttribute("eventBeforeStart", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "eventBeforeStart");
			entry.removeCustomAttribute("eventBeforeStart");
			wfi.modifyWorkflowState(entry, ws, "doRecur");

			//recurring events startout with the next occurance.  
			//Cannot wait for time to pass, so check that it doesn't reach state

			//event started 1 minute ago and lasted 30 seconds
			//doesn't repeat for another day.
			cal = new GregorianCalendar();
			date = new Date();
			cal.setTimeInMillis(date.getTime()-60*1000);
			event = new Event(cal, new Duration(0,0,30));
			event.setFrequency("DAILY");
			cdi.save(event);
			attr = entry.addCustomAttribute("eventEnded", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			if (ws.getTimerId() == null)
				throw new RuntimeException("Expecting wait for timer at " + ws.getState());		
			checkState(ws, "doRecur");
			JbpmContext c = WorkflowFactory.getContext();
			try {
				c.getSession().delete(c.getSession().load(Timer.class, ws.getTimerId()));
			} finally {c.close();};
     		ws.setTimerId(null);
    		entry.removeCustomAttribute("eventEnded");
			wfi.modifyWorkflowState(entry, ws, "doRecur");

			//event started 25 minutes ago, and lasts for 30 minues
			cal = new GregorianCalendar();
			date = new Date();
			//check for 5 minutes before end 
			cal.setTimeInMillis(date.getTime()-25*60*1000);
			event = new Event(cal, new Duration(0,30,0));
			event.setFrequency("HOURLY");
			cdi.save(event);
			attr = entry.addCustomAttribute("eventBeforeEnd", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			if (ws.getTimerId() == null)
				throw new RuntimeException("Expecting wait for timer at " + ws.getState());		
			checkState(ws, "doRecur");
			c = WorkflowFactory.getContext();
			try {
				c.getSession().delete(c.getSession().load(Timer.class, ws.getTimerId()));
			} finally {c.close();};
    		ws.setTimerId(null);
 			entry.removeCustomAttribute("eventBeforeEnd");
			wfi.modifyWorkflowState(entry, ws, "doRecur");
			
			//event started 1 day and 1 minute ago and lasted 30 seconds
			cal = new GregorianCalendar();
			date = new Date();
			//test 1 day after trigger
			cal.setTimeInMillis(date.getTime()-24*61*60*1000);
			event = new Event(cal, new Duration(0,0,30));
			event.setFrequency("HOURLY");
			cdi.save(event);
			attr = entry.addCustomAttribute("eventAfterStart", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "doRecur");
			WorkflowFactory.getContext().getSession().delete(WorkflowFactory.getContext().getSession().load(Timer.class, ws.getTimerId()));
    		ws.setTimerId(null);
			entry.removeCustomAttribute("eventAfterStart");
			wfi.modifyWorkflowState(entry, ws, "doRecur");

			//event started 2 minutes ago and lasted 30 seconds
			cal = new GregorianCalendar();
			date = new Date();
			//test 1 minute after end
			cal.setTimeInMillis(date.getTime()-2*60*1000);
			event = new Event(cal, new Duration(0,0,30));
			event.setFrequency("HOURLY");
			cdi.save(event);
			attr = entry.addCustomAttribute("eventAfterEnd", event);			
			wfi.modifyWorkflowStateOnUpdate(entry);
			checkState(ws, "doRecur");
			WorkflowFactory.getContext().getSession().delete(WorkflowFactory.getContext().getSession().load(Timer.class, ws.getTimerId()));
    		ws.setTimerId(null);
			entry.removeCustomAttribute("eventAfterEnd");
			wfi.modifyWorkflowState(entry, ws, "doRecur");
	} finally {
			
		}
		
	}
	private WorkflowState checkState(FolderEntry entry, String stateName) {
		for (Iterator iter=entry.getWorkflowStates().iterator(); iter.hasNext();) {
			WorkflowState ws = (WorkflowState)iter.next();
			if (ws.getState().equals(stateName)) return ws;
		}
		return null;
	}
	private void  checkState(WorkflowState ws, String stateName) throws RuntimeException {
		if (ws.getState().equals(stateName)) return;
		throw new RuntimeException("Invalid transition, expecting state " + stateName + " at " + ws.getState());		
	}
	protected Workspace createZone(String name) {
		Workspace top=super.createZone(name);
		RequestContext rc = new RequestContext(name, adminUser);
		rc.setUser(pdi.findUserByName(adminUser, name));
		RequestContextHolder.setRequestContext(rc);
		return top;
		
	}
	private Folder createFolder(Workspace top, String name) {
		Folder folder = new Folder();
		folder.setName(name);
		folder.setZoneId(top.getZoneId());
		cdi.save(folder);
		top.addBinder(folder);
		return folder;
		
	}
	private FolderEntry createEntry(Folder top) {
		FolderEntry entry = new FolderEntry();
		top.addEntry(entry);
		cdi.save(entry);
		return entry;
		
	}
	private Definition importWorkflow(Workspace top, String name) {
		Definition def = new Definition();
		def.setZoneId(top.getId());
		def.setType(Definition.WORKFLOW);
		def.setName(name);
		def.setTitle(name);
	   	try {
           Resource r = new ClassPathResource("com/sitescape/team/module/folder/impl/" + name);
           SAXReader xIn = new SAXReader();
           def.setDefinition(xIn.read(r.getInputStream()));   
    	} catch (Exception fe) {
			logger.error(fe.getMessage(), fe);
    	}
		cdi.save(def);
		wfi.modifyProcessDefinition(def.getId(), def);
    	return def;		
	}
	private Definition importCommand(Workspace top, String name) {
		Definition def = new Definition();
		def.setZoneId(top.getId());
		def.setType(Definition.FOLDER_ENTRY);
		def.setName(name);
		def.setTitle(name);
	   	try {
           Resource r = new ClassPathResource("com/sitescape/team/module/folder/impl/" + name);
           SAXReader xIn = new SAXReader();
           def.setDefinition(xIn.read(r.getInputStream()));   
    	} catch (Exception fe) {
			logger.error(fe.getMessage(), fe);
    	}
    	//keep the database id, referenced from workflow definition for
    	//entry data transitions
		String id = def.getDefinition().getRootElement().attributeValue("databaseId", "");
		//import - try reusing existing guid
		def.setId(id);
		cdi.replicate(def);
    	return def;		
	}
}
