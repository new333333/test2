package com.sitescape.ef.file;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.ef.InternalException;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.dao.CoreDao;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FileItem;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.HistoryStamp;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.VersionAttachment;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.repository.RepositoryServiceUtil;
import com.sitescape.ef.util.FileUploadItem;

/**
 * File manager is a wrapper around repository service, and adds metadata
 * management capability. 
 * 
 * @author jong
 *
 */
public class FileManager {

	protected Log logger = LogFactory.getLog(getClass());

	private CoreDao coreDao;
	
	protected CoreDao getCoreDao() {
		return coreDao;
	}
	public void setCoreDao(CoreDao coreDao) {
		this.coreDao = coreDao;
	}

	/**
	 * Writes the specified file to the system. 
	 * <p>
	 * If the file doesn't already exist, it creates it.
	 * <p> 
	 * If the file already exists and it is not currently checked out by anyone,
	 * it attempts to check out, update the file, and check it back in, which
	 * will create a new version of the file if the underlying repository 
	 * system supports versioning.<br>
	 * If the file is already checked out by the user, the content of the file
	 * is updated, but new version is not created until an explicit <code>checkin</code> 
	 * is performed by the user. In this case, the update can be subsequently 
	 * rolled back by the user by calling <code>uncheckout</code>.<br> 
	 * If the file is currently checked out by someone else, it throws 
	 * <code>CheckedOutByOtherException</code>. 
	 * 
	 * @param binder
	 * @param entry
	 * @param fui
	 * @throws RepositoryServiceException
	 */
    public void writeFile(Binder binder, Entry entry, FileUploadItem fui) 
    	throws CheckedOutByOtherException, RepositoryServiceException {
		int type = fui.getType();
		if(type != FileUploadItem.TYPE_FILE && type != FileUploadItem.TYPE_ATTACHMENT) {
			logger.error("Unrecognized file processing type " + type + " for ["
					+ fui.getName() + ","
					+ fui.getMultipartFile().getOriginalFilename() + "]");
			throw new InternalException();
		}
		
		String name = fui.getName();
		String fileName = fui.getMultipartFile().getOriginalFilename();

		// First, find out whether or not this is a new file for the entry.
		// It is important to note that, as far as identity/existence test
		// goes, the namespace is flat for a single instance of Entry.
		// In other words, regardless of the data elements used for accessing
		// the file, the files are treated identical globally within a single
		// Entry instance as long as their file names are identical. 
    	FileAttachment fAtt = entry.getFileAttachment(fui.getRepositoryServiceName(), fileName);

    	boolean isNew = false;
    	
    	if(fAtt == null) { // New file for the entry
    		fAtt = createFile(binder, entry, fui);
    		// Since file attachment is stored into custom attribute using
    		// its id value rather than association, this new object must
    		// be persisted here just in case it is to be put into custom
    		// attribute down below. 
    		getCoreDao().save(fAtt);
    		isNew = true;
    	}
    	else { // Existing file for the entry
			writeExistingFile(binder, entry, fui, fAtt);    		
    	}
    	
		if (type == FileUploadItem.TYPE_FILE) {
			// Find custom attribute by the attribute name. 
			Set fAtts = null;
			CustomAttribute ca = entry.getCustomAttribute(name);
			if(ca != null)
				fAtts = (Set) ca.getValue();
			else
				fAtts = new HashSet();

			// Simply because the file already exists for the entry does not 
			// mean that it is known through this particular data element
			// (i.e., custom attribute). So we need to make sure that it is
			// made visible through this element.
			fAtts.add(fAtt); // If it is already in the set, it will have no effect
			
			if(ca != null)
				ca.setValue(fAtts);
			else
				entry.addCustomAttribute(name, fAtts);
		} else if (type == FileUploadItem.TYPE_ATTACHMENT) {
			// Add the file attachment to the entry only if new file. 
			if(isNew) {
				entry.addAttachment(fAtt);
			}
		} 
    }
    
	/**
	 * If the specified file is checked out, returns <code>HistoryStamp</code>
	 * containing when/by whom the file was checked out. If the file is not
	 * checked out, it returns <code>null</code>.
	 * 
	 * @param binder
	 * @param entry
	 * @param fileName
	 * @return
	 */
	public HistoryStamp getCheckoutInfo(String repositoryServiceName, 
			Binder binder, Entry entry, String fileName) {
		FileAttachment fAtt = entry.getFileAttachment(repositoryServiceName, fileName);
		return fAtt.getCheckout();
	}
	
	/**
	 * Checkes out the specified file. 
	 * <p>
	 * If the file is already checked out by the user making this call, this
	 * operation is noop. If it is currently checked out by someone else, it
	 * throws <code>CheckedOutByOtherException</code>.
	 * 
	 * @param binder
	 * @param entry
	 * @param fileName
	 */
	/**
	 * @param binder
	 * @param entry
	 * @param fileName
	 * @throws CheckedOutByOtherException
	 */
	public void checkout(String repositoryServiceName, Binder binder, 
			Entry entry, String fileName) throws CheckedOutByOtherException, 
			NoSuchFileException, RepositoryServiceException {
    	FileAttachment fAtt = entry.getFileAttachment(repositoryServiceName, fileName);
    	
    	if(fAtt == null)
    		throw new NoSuchFileException(entry, fileName);
    	
        User user = RequestContextHolder.getRequestContext().getUser();
        
        HistoryStamp co = fAtt.getCheckout();
    	
    	if(co == null) { // The file is not checked out.
    		// Instruct the underlying repository system to actually check out
    		// the file. If the repository system does not support versioning,
    		// this call is noop, which means that the checkout/checkin is 
    		// implemented soley on our side using metadata only. In that case,
    		// checkout facility is only used to enforce exclusive locking on
    		// the resource, but actual versioning of the content does not
    		// take place. 
    		RepositoryServiceUtil.checkout(fAtt.getRepositoryServiceName(),
    				binder, entry, fileName);
    		// Mark our metadata that the file is checked out by the user.
    		fAtt.setCheckout(new HistoryStamp(user));
    	}
    	else { // The file is checked out by someone. 
    		if(user.equals(co.getPrincipal())) {
    			// The file is checked out by the same person calling this. 
    			// Nothing to do. 
    		}
    		else {
    			// The file is checked out by some other person. 
    			throw new CheckedOutByOtherException(entry, fileName, user);
    		}
    	}
	}
	
	/**
	 * Cancels the checkout for the specified file. 
	 * <p>
	 * If the file is not checked out by anyone, this method has no effect.<br>
	 * If the file is checked out by the user making this call, it cancels
	 * the checkout. If the underlying repository system supports versioning,
	 * this will restore the state of the file back to what it was prior to
	 * checking it out.<br>
	 * If the file is checked out by someone else, it throws 
	 * <code>CheckedOutByOtherException</code>.
	 * 
	 * @param binder
	 * @param entry
	 * @param fileName
	 * @throws CheckedOutByOtherException
	 * @throws RepositoryServiceException
	 */
	public void uncheckout(String repositoryServiceName, Binder binder, 
			Entry entry, String fileName) throws CheckedOutByOtherException, 
			NoSuchFileException, RepositoryServiceException {
    	FileAttachment fAtt = entry.getFileAttachment(repositoryServiceName, fileName);
    	
    	if(fAtt == null)
    		throw new NoSuchFileException(entry, fileName);
    	
        User user = RequestContextHolder.getRequestContext().getUser();
        
        HistoryStamp co = fAtt.getCheckout();
    	
    	if(co == null) { // The file is not checked out.
    		// Nothing to do
    	}
    	else { // The file is checked out by someone. 
    		if(user.equals(co.getPrincipal())) {
    			// The file is checked out by the same person calling this. 
        		RepositoryServiceUtil.uncheckout(fAtt.getRepositoryServiceName(),
        				binder, entry, fileName);
        		// Mark our metadata that the file is not checked out.
        		fAtt.setCheckout(null);
    		}
    		else {
    			// The file is checked out by some other person. 
    			throw new CheckedOutByOtherException(entry, fileName, user);
    		}
    	}
	}
	
	/**
	 * Checkes in the specified file. 
	 * <p>
	 * If the file is already checked in (i.e., currently not checked out), 
	 * this method has no effect.<br>
	 * If the file is checked out by the user making this call, it makes the
	 * changes made since previous checkout permanent by creating a new version
	 * of the file assuming that the underlying repository system supports
	 * versioning.<br>
	 * If the file is checked out by someone else, it throws
	 * <code>CheckedOutByOtherException</code>.
	 * 
	 * @param binder
	 * @param entry
	 * @param fileName
	 * @throws RepositoryServiceException
	 */
	public void checkin(String repositoryServiceName, Binder binder, 
			Entry entry, String fileName) throws CheckedOutByOtherException, 
			NoSuchFileException, RepositoryServiceException {
    	FileAttachment fAtt = entry.getFileAttachment(repositoryServiceName, fileName);
    	
    	if(fAtt == null)
    		throw new NoSuchFileException(entry, fileName);
    	
        User user = RequestContextHolder.getRequestContext().getUser();
        
        HistoryStamp co = fAtt.getCheckout();
    	
    	if(co == null) { // The file is not checked out.
    		// Nothing to do
    	}
    	else { // The file is checked out by someone. 
    		if(user.equals(co.getPrincipal())) {
    			// The file is checked out by the same person calling this. 
    			
    			RepositoryService service = RepositoryServiceUtil.lookupRepositoryService
    				(fAtt.getRepositoryServiceName());
    			Object session = service.openRepositorySession();
    			String versionName = null;
    			long contentLength = 0;
    			try {
    				versionName = service.checkin(session, binder, entry, fileName);
    				if(versionName != null)
    					contentLength = service.getContentLength(session, binder, entry, fileName, versionName);
    				else
    					contentLength = service.getContentLength(session, binder, entry, fileName);
    			} finally {
    				service.closeRepositorySession(session);
    			}

    			updateFileAttachment(fAtt, user, versionName, contentLength);		
    			
        		// Mark our metadata that the file is not checked out.
        		fAtt.setCheckout(null);
    		}
    		else {
    			// The file is checked out by some other person. 
    			throw new CheckedOutByOtherException(entry, fileName, user);
    		}
    	}
		
	}

    private void writeExistingFile(Binder binder, Entry entry, 
    		FileUploadItem fui, FileAttachment fAtt) 
		throws CheckedOutByOtherException, RepositoryServiceException {
        User user = RequestContextHolder.getRequestContext().getUser();

        String fileName = fui.getMultipartFile().getOriginalFilename();
        
		HistoryStamp co = getCheckoutInfo(fui.getRepositoryServiceName(), 
				binder, entry, fileName); 
		
        if(co == null) {
			// This file is not checked out by anyone. 
        	// In this case, we create a new version silently.
			RepositoryService service = RepositoryServiceUtil.lookupRepositoryService
			(fAtt.getRepositoryServiceName());
			Object session = service.openRepositorySession();
			String versionName = null;
			long contentLength = 0;
			try {
				service.checkout(session, binder, entry, fileName);
				service.update(session, binder, entry, fileName, fui.getMultipartFile());
				versionName = service.checkin(session, binder, entry, fileName);
				if(versionName != null)
					contentLength = service.getContentLength(session, binder, entry, fileName, versionName);
				else
					contentLength = service.getContentLength(session, binder, entry, fileName);
			} finally {
				service.closeRepositorySession(session);
			}
			
			updateFileAttachment(fAtt, user, versionName, contentLength);
		}
		else {
	   		if(user.equals(co.getPrincipal())) {
    			// The file is checked out by the same person calling this.
	   			// Update the file to the repository.
	   			RepositoryServiceUtil.update(binder, entry, fui);  			
    		}
    		else {
    			// The file is checked out by some other person. 
    			throw new CheckedOutByOtherException(entry, fileName, user);
    		}			
		}
    }

    private void updateFileAttachment(FileAttachment fAtt, 
			User user, String versionName, long contentLength) {
		fAtt.setModification(new HistoryStamp(user));
		
		FileItem fItem = fAtt.getFileItem();
		fItem.setLength(contentLength);
		
		if(versionName != null) {
			// The repository system supports versioning.        			
			int versionNumber = fAtt.getLastVersion().intValue() + 1;
			fAtt.setLastVersion(new Integer(versionNumber));
			
			VersionAttachment vAtt = new VersionAttachment();
			vAtt.setCreation(fAtt.getModification());
			vAtt.setModification(vAtt.getCreation());
			vAtt.setFileItem(fItem);
			vAtt.setVersionNumber(versionNumber);
			vAtt.setVersionName(versionName);
			fAtt.addFileVersion(vAtt);
		}
	}
	
    /**
	 * Creates a new file in the system.
	 * <p>
	 * This method adds the file to the repository system, creates a new
	 * <code>FileAttachment</code> object corresponding to the file being 
	 * created, and returns it so that it can be persisted by the caller.
	 * In other words, persisting the new metadata in our database is not
	 * a responsibility of this method.  
	 * 
	 * @param binder
	 * @param entry
	 * @param fui
	 * @return
	 * @throws RepositoryServiceException
	 */
	private FileAttachment createFile(Binder binder, Entry entry, 
			FileUploadItem fui) throws RepositoryServiceException {
    	// TODO Take care of file path info?
    	
        User user = RequestContextHolder.getRequestContext().getUser();

        String fileName = fui.getMultipartFile().getOriginalFilename();
	
		FileAttachment fAtt = new FileAttachment();
		fAtt.setOwner(entry);
		fAtt.setCreation(new HistoryStamp(user));
		fAtt.setModification(fAtt.getCreation());
    	fAtt.setRepositoryServiceName(fui.getRepositoryServiceName());
    	//set attribute name - null if not not named
    	fAtt.setName(fui.getName());
    	FileItem fItem = new FileItem();
    	fItem.setName(fileName);
    	fItem.setLength(fui.getMultipartFile().getSize());
    	fAtt.setFileItem(fItem);

		String versionName = RepositoryServiceUtil.create(binder, entry, fui);

		if(versionName != null) {
			// The repository system supports versioning. 
			fAtt.setLastVersion(new Integer(1));

			VersionAttachment vAtt = new VersionAttachment();
			vAtt.setCreation(fAtt.getCreation());
			vAtt.setModification(vAtt.getCreation());
			vAtt.setFileItem(fItem);
			
			vAtt.setVersionNumber(1);
			vAtt.setVersionName(versionName);
			fAtt.addFileVersion(vAtt);
		}

    	return fAtt;
	}
}
