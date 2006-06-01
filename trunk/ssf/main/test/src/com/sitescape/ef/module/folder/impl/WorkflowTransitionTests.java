package com.sitescape.ef.module.folder.impl;

import java.util.Iterator;

import org.dom4j.io.SAXReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.context.request.RequestContext;
import com.sitescape.ef.dao.impl.CoreDaoImpl;
import com.sitescape.ef.dao.impl.FolderDaoImpl;
import com.sitescape.ef.dao.impl.ProfileDaoImpl;
import com.sitescape.ef.domain.Definition;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.NoWorkspaceByTheNameException;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.WorkflowSupport;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.module.workflow.impl.WorkflowModuleImpl;
import com.sitescape.util.Validator;

public class WorkflowTransitionTests extends AbstractTransactionalDataSourceSpringContextTests {

	protected WorkflowModuleImpl wfi;
	protected CoreDaoImpl cdi;
	protected FolderDaoImpl fdi;
	protected ProfileDaoImpl pdi;
	private static String zoneName ="testZone";
	private static String adminGroup = "administrators";
	private static String adminUser = "administrator";
	protected String[] getConfigLocations() {
		return new String[] {"/com/sitescape/ef/module/folder/impl/applicationContext-workflowTransition.xml"};
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
	public void setCoreDao(CoreDaoImpl cdi) {
		this.cdi = cdi;
	}
	
	public void setProfileDaoImpl(ProfileDaoImpl pdi) {
		this.pdi = pdi;
	}
	public void testManualTransition() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");
		FolderEntry entry = createEntry(folder);
		
		Definition workflowDef = importDef("testManual");
		wfi.addEntryWorkflow(entry, entry.getEntityIdentifier(), workflowDef);
		WorkflowState ws = checkState(entry, "start");
		wfi.modifyWorkflowState(ws.getId(), ws.getState(), "state1");
		ws = checkState(entry, "state1");
		wfi.modifyWorkflowState(ws.getId(), ws.getState(), "state2");
		ws = checkState(entry, "state2");
		
		
	}
	private WorkflowState checkState(FolderEntry entry, String stateName) {
		for (Iterator iter=entry.getWorkflowStates().iterator(); iter.hasNext();) {
			WorkflowState ws = (WorkflowState)iter.next();
			if (ws.getState().equals(stateName)) return ws;
		}
		return null;
	}
	private Workspace createZone(String name) {
		Workspace top;
		try { 
			top = cdi.findTopWorkspace(name);
		} catch (NoWorkspaceByTheNameException nw) {
			top = new Workspace();
			top.setName(name);
			top.setZoneName(name);
			cdi.save(top);
			ProfileBinder profiles = new ProfileBinder();
			profiles.setName("_profiles");
			profiles.setZoneName(name);
			profiles.setParentBinder(top);
			//	generate id for top
			cdi.save(profiles);
			User user = new User();
			user.setName(adminUser);
			user.setZoneName(name);
			user.setParentBinder(profiles);
			cdi.save(user);
		}
		RequestContext rc = new RequestContext(name, adminUser);
		rc.setUser(pdi.findUserByName(adminUser, name));
		RequestContextHolder.setRequestContext(rc);
		return top;
		
	}
	private Folder createFolder(Workspace top, String name) {
		Folder folder = new Folder();
		folder.setName(name);
		folder.setZoneName(top.getZoneName());
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
	private Definition importDef(String name) {
		Definition def = new Definition();
		def.setZoneName(zoneName);
		def.setType(Definition.WORKFLOW);
		def.setName(name);
		def.setTitle(name);
	   	try {
           Resource r = new ClassPathResource("com/sitescape/ef/module/folder/impl/" + name);
           SAXReader xIn = new SAXReader();
           def.setDefinition(xIn.read(r.getInputStream()));   
    	} catch (Exception fe) {
    		fe.printStackTrace();
    	}
		cdi.save(def);
		wfi.modifyProcessDefinition(def.getId(), def);
    	return def;		
	}
}
