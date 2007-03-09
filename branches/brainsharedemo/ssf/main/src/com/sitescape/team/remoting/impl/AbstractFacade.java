package com.sitescape.team.remoting.impl;

import java.io.IOException;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.ic.ICBroker;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.dashboard.DashboardModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.definition.notify.Notify;
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.module.file.WriteFilesException;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.ldap.LdapModule;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.sample.EmployeeModule;
import com.sitescape.team.module.workflow.WorkflowModule;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.remoting.Facade;
import com.sitescape.team.rss.RssGenerator;
import com.sitescape.team.util.AllBusinessServicesInjected;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.WebUrlUtil;

/**
 * POJO implementation of Facade interface.
 * 
 * Important: This class is NOT tied to any specific remoting protocol 
 * such as SOAP. Therefore don't ever put protocol or tool specific code 
 * (such as capability that utilizes Axis engine directly) into this class.  
 * 
 * @author jong
 *
 */
public abstract class AbstractFacade implements Facade, AllBusinessServicesInjected {

	protected final Log logger = LogFactory.getLog(getClass());

	private EmployeeModule employeeModule;
	private WorkspaceModule workspaceModule;
	private FolderModule folderModule;
	private AdminModule adminModule;
	private ProfileModule profileModule;
	private DefinitionModule definitionModule;
	private WorkflowModule workflowModule;
	private BinderModule binderModule;
	private LdapModule ldapModule;
	private FileModule fileModule;
	private RssGenerator rssGenerator;
	private DashboardModule dashboardModule;
	private ICBroker icBroker;

	public RssGenerator getRssGenerator() {
		return rssGenerator;
	}

	public void setRssGenerator(RssGenerator rssGenerator) {
		this.rssGenerator = rssGenerator;
	}

	public void setEmployeeModule(EmployeeModule employeeModule) {
		this.employeeModule = employeeModule;
	}
	
	public EmployeeModule getEmployeeModule() {
		return employeeModule;
	}
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	
	public BinderModule getBinderModule() {
		return binderModule;
	}

	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}
	
	public WorkspaceModule getWorkspaceModule() {
		return workspaceModule;
	}

	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
	
	public FolderModule getFolderModule() {
		return folderModule;
	}
	
	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}
	
	public AdminModule getAdminModule() {
		return adminModule;
	}

	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	public ProfileModule getProfileModule() {
		return profileModule;
	}
	
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	
	public DefinitionModule getDefinitionModule() {
		return definitionModule;
	}

	public WorkflowModule getWorkflowModule() {
		return workflowModule;
	}

	public void setWorkflowModule(WorkflowModule workflowModule) {
		this.workflowModule = workflowModule;
	}
	
	public void setLdapModule(LdapModule ldapModule) {
		this.ldapModule = ldapModule;
	}
	
	public LdapModule getLdapModule() {
		return ldapModule;
	}
	
	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
	}
	
	public FileModule getFileModule() {
		return fileModule;
	}
	
	public void setDashboardModule(DashboardModule dashboardModule) {
		this.dashboardModule = dashboardModule;
	}
	
	public DashboardModule getDashboardModule() {
		return dashboardModule;
	}

	public ICBroker getIcBroker() {
		return icBroker;
	}

	public void setIcBroker(ICBroker icBroker) {
		this.icBroker = icBroker;
	}
	
	public String getDefinitionAsXML(String definitionId) {
		return getDefinitionModule().getDefinition(definitionId).getDefinition().getRootElement().asXML();
	}
	
	public String getDefinitionConfigAsXML() {
		return getDefinitionModule().getDefinitionConfig().getRootElement().asXML();
	}

	public String getFolderEntryAsXML(long binderId, long entryId) {
		Long bId = new Long(binderId);
		Long eId = new Long(entryId);
		
		// Retrieve the raw entry.
		FolderEntry entry = 
			getFolderModule().getEntry(bId, eId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = doc.addElement("entry");
		
		// Handle structured fields of the entry known at compile time. 
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("binderId", entry.getParentBinder().getId().toString());
		entryElem.addAttribute("definitionId", entry.getEntryDef().getId());
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("docNumber", entry.getDocNumber());
		entryElem.addAttribute("docLevel", String.valueOf(entry.getDocLevel()));
		String entryUrl = WebUrlUtil.getEntryViewURL(entry);
		entryElem.addAttribute("href", entryUrl);
		
		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.getRootElement().asXML();
		
		/*
		System.out.println("*** XML representation for entry " + entry.getId());
		System.out.println(xml);
		System.out.println("*** Pretty XML representation for entry " + entry.getId());
		prettyPrint(doc);
		*/
		
		return xml;
	}
	
	public long addFolderEntry(long binderId, String definitionId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getFolderModule().addEntry(new Long(binderId), definitionId, 
				new DomInputData(doc), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	public abstract void uploadFolderFile(long binderId, long entryId, 
			String fileUploadDataItemName, String fileName);
	
	public void modifyFolderEntry(long binderId, long entryId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			getFolderModule().modifyEntry(new Long(binderId), new Long(entryId), 
				new DomInputData(doc), new HashMap(), null, null);
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}			
	}
	
	public void deleteFolderEntry(long binderId, long entryId) {
		getFolderModule().deleteEntry(new Long(binderId), new Long(entryId));
	}
	
	public long addReply(long binderId, long parentId, String definitionId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getFolderModule().addReply(new Long(binderId), new Long(parentId), 
				definitionId, new DomInputData(doc), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}
	}
	
	public String getPrincipalAsXML(long binderId, long principalId) {
		Long bId = new Long(binderId);
		Long pId = new Long(principalId);
		
		// Retrieve the raw entry.
		Principal entry = 
			getProfileModule().getEntry(bId, pId);

		Document doc = DocumentHelper.createDocument();
		
		Element entryElem = doc.addElement("entry");
		
		// Handle structured fields of the entry known at compile time. 
		entryElem.addAttribute("id", entry.getId().toString());
		entryElem.addAttribute("binderId", entry.getParentBinder().getId().toString());
		entryElem.addAttribute("definitionId", entry.getEntryDef().getId());
		entryElem.addAttribute("title", entry.getTitle());
		entryElem.addAttribute("disabled", Boolean.toString(entry.isDisabled()));
		entryElem.addAttribute("reserved", Boolean.toString(entry.isReserved()));
		
		// Handle custom fields driven by corresponding definition. 
		addCustomElements(entryElem, entry);
		
		String xml = doc.getRootElement().asXML();
		
		return xml;
	}
	
	public long addUser(long binderId, String definitionId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getProfileModule().addUser(new Long(binderId), definitionId, new DomInputData(doc), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public long addGroup(long binderId, String definitionId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			return getProfileModule().addGroup(new Long(binderId), definitionId, new DomInputData(doc), null).longValue();
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public void modifyPrincipal(long binderId, long principalId, String inputDataAsXML) {
		Document doc = getDocument(inputDataAsXML);
		
		try {
			getProfileModule().modifyEntry(new Long(binderId), new Long(principalId), new DomInputData(doc));
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public void deletePrincipal(long binderId, long principalId) {
		try {
			getProfileModule().deleteEntry(new Long(binderId), new Long(principalId));
		}
		catch(WriteFilesException e) {
			throw new RemotingException(e);
		}

	}
	
	public String getWorkspaceTreeAsXML(long binderId, int levels) {
		com.sitescape.team.domain.Binder binder = getBinderModule().getBinder(new Long(binderId));

		Document tree;
		String treeKey = "";
		if (binder instanceof Workspace) {
			tree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), new WsDomTreeBuilder(binder, true, this, treeKey), levels);
		} 
		else {
			//com.sitescape.team.domain.Folder topFolder = ((com.sitescape.team.domain.Folder)binder).getTopFolder();
			tree = getFolderModule().getDomFolderTree(binder.getId(), new WsDomTreeBuilder(binder, false, this, treeKey), levels);
			
			//if (topFolder == null) topFolder = (com.sitescape.team.domain.Folder)binder;
			//tree = getFolderModule().getDomFolderTree(topFolder.getId(), new WsDomTreeBuilder(topFolder, false, this, treeKey));
		}

		String xml = tree.getRootElement().asXML();
		//System.out.println(xml);
		return xml;
	}
	
	private Document getDocument(String xml) {
		// Parse XML string into a document tree.
		try {
			return DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			logger.error(e);
			throw new IllegalArgumentException(e.toString());
		}
	}
	
	private void prettyPrint(Document doc) {
		OutputFormat format = OutputFormat.createPrettyPrint();
		try {
			XMLWriter writer = new XMLWriter(System.out, format);
			writer.write(doc);
		}
		catch(IOException e) {}
	}
	
	private void addCustomElements(Element entryElem, com.sitescape.team.domain.Entry entry) {
		// This is a kludge (hack) that uses the implementation of the existing 
		// email notification mechanism to quickly demonstrate the concept only.
		// TODO To be rewritten
		
		Notify notify = new Notify();
		notify.setFull(true);
		User user = RequestContextHolder.getRequestContext().getUser();
		Locale locale = user.getLocale();
		notify.setLocale(locale);
		notify.setDateFormat(DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.FULL, locale));
		
		getDefinitionModule().getNotifyElementForEntity(entryElem, notify, entry);
		
	}
	
}
