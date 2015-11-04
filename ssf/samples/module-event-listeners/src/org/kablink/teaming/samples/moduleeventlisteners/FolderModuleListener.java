/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.samples.moduleeventlisteners;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.FolderDao;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.ReservedByAnotherUserException;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.InputDataAccessor;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.util.BuiltInUsersHelper;
import org.kablink.teaming.web.util.DefinitionHelper;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class FolderModuleListener {

	protected Log logger = LogFactory.getLog(getClass());
	
	
	/******************* Listener for AddEntry method ********************/
	
	/**
	 * Pre-event method for FolderModule.AddEntry.
	 * <p>
	 * Intercept the execution of a module method. Called before the system invokes
	 * the module method.
	 * <p>
	 * Pre-event method takes exactly the same set of arguments and in the same 
	 * order as the module method. The return type for pre-event method is typially 
	 * <code>void</code>. But it can optionally define a return type of 
	 * <code>boolean</code> and make a decision at runtime as to whether the chain
	 * of execution should proceed or stop by returning <code>true</code> or
	 * <code>false</code> respectively. The way in which the system handles aborted
	 * execution depends on the nature and the return type of the corresponding
	 * module method. For example, if the module method is designed to return
	 * nothing, stopped processing of the chain may not be immediately noticeable.
	 * However, if the module method is designed to return non-null object all the
	 * time, then aborted execution of the pipeline may result in an exception 
	 * (eg. NullPointerException) because the pipeline is not returning the value
	 * that the application layer is expecting. It is up to the descretion of the
	 * person who develops or plugs in the listener to decide what makes or does
	 * not make sense on a per case basis.
	 * <p>
	 * The pre-event method can throw one of the checked exceptions defined by the
	 * corresponding module method that the pre-event method is intercepting an 
	 * execution of. Or it could throw any unchecked exception of its own.
	 * However, it is strongly recommended that in nearly all cases any exception 
	 * thrown by the pre-event method should be a subclass of 
	 * <code>org.kablink.teaming.exception.UncheckedCodedException</code>.
	 * It provides the facility for localizing error messages and the error message
	 * will be presented to end user in well formatted manner.
	 * 
	 * @return nothing in this case
	 * @throws Exception in case of errors
	 */
	public void preAddEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Map options) throws WriteFilesException, WriteEntryDataException {
		logger.info("preAddEntry: About to add an entry to the folder " + folderId + " with definition " + definitionId + ". Here's textual input data -");
		if(definitionId == null) {
			Folder folder = getFolderDao().loadFolder(folderId, RequestContextHolder.getRequestContext().getZoneId());
			definitionId = folder.getDefaultEntryDef().getId();
		}
		Set<String> textualInputDataKeys = DefinitionHelper.getTextualInputDataKeys(definitionId, inputData);
		for(String key:textualInputDataKeys) {
			logger.info(key + " : " + inputData.getSingleValue(key));
		}
	}
		
	/**
	 * Post-event method for FolderModule.AddEntry.
	 * <p>
	 * Intercept the execution of a module method. Called after the system successfully
	 * invoked the module method.
	 * <p>
	 * With this post-event method, each listener can post-process an execution, 
	 * getting applied in inverse order of the execution chain. The post-event 
	 * method is invoked ONLY IF the earlier execution of the module method 
	 * returned successfully.
	 * <p>
	 * Post-event method takes exactly the same set of arguments and in the same
	 * order as the module method. However, if the return type of the module method
	 * is anything but <code>void</code>, an extra argument with the same type as
	 * the return type of the module method MUST be appended to the argument list
	 * (eg. see the last argument of type <code>FolderEntry</code> in the argument
	 * list below). At runtime, the system takes the return value from the successful
	 * execution of the module method, and passes it to the post-event method so
	 * that the listener can make use of the value if needed. This mechanism works
	 * for return values of both object (eg. FolderEntry) and primitive types 
	 * (eg. boolean). However, if the return type of the module method is 
	 * <code>void</code>, this extra argument MUST NOT be specified in the signature
	 * of the post-event method.
	 * <p>
	 * The post-event method can throw one of the checked exceptions defined by the
	 * corresponding module method that the post-event method is intercepting an 
	 * execution of. Or it could throw any unchecked exception of its own.
	 * However, it is strongly recommended that in nearly all cases any exception 
	 * thrown by the post-event method should be a subclass of 
	 * <code>org.kablink.teaming.exception.UncheckedCodedException</code>.
	 * It provides the facility for localizing error messages and the error message
	 * will be presented to end user in well formatted manner.
	 * <p>
	 * It is also important to note that throwing an exception from a post-event
	 * method does NOT automatically undo the state changes that the corresponding 
	 * module method has already made to the system. That is, by the time this 
	 * method is invoked, the transaction surrounding the module method (if any) 
	 * may have already been committed and merely throwing an exception from the 
	 * post-event method will not alter the outcome of the previously successful
	 * execution of the module method. The exception thrown out of this method,
	 * however, is likely to cause an error message to be displayed on the user
	 * interface. If that's not a desirable effect, do not throw an exception from
	 * this method. 
	 * 
	 * @param entry newly created entry returned from module method execution
	 * @throws Exception in case of errors
	 */
	public void postAddEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Map options, FolderEntry entry) throws WriteFilesException, WriteEntryDataException {
		// This method illustrates accepting the result of the module event 
		// as an object (FolderEntry). 
		
		logger.info("postAddEntry: A new entry is created with ID " + entry.getId());
		
		// Figure out who added this entry.
		@SuppressWarnings("unused")
		String userName = RequestContextHolder.getRequestContext().getUserName();
		
		// Given the user name and the newly added entry, we can do something useful
		// here. For example, making a web services call to a remote system to 
		// synchronize the data, or posting this data to another web site, etc.
	}
	
	/**
	 * After-completion method for FolderModule.AddEntry.
	 * 
	 * Callback after completion of request processing, that is, after executing
	 * pre-event method, module method, and post-event method. Unlike post-event
	 * method, after-completion method will be called on any outcome of module
	 * method execution, thus allows for proper resource cleanup.
	 * <p>
	 * Will only be called if this listener's pre-event method (that is,
	 * <code>preAddEntry</code> method) has successfully completed and has not 
	 * returned <code>false</code>.
	 * <p>
	 * After-completion method takes exactly the same set of arguments and in the 
	 * same order as the module method. In addition, an argument of <code>Throwable</code>
	 * type is appended to the argument list. At runtime, if the module method
	 * throws an exception, the system takes the exception object and passes it
	 * to the after-completion method so that the listener can examine the error.
	 * <p>
	 * Unlike with the pre and post event methods, the after-completion method can
	 * throw an exception of any type. However, an exception thrown out of this
     * method will not be propagated back up to the caller and no error message
     * will be displayed on the user interface.
	 * 
	 * @param ex exception thrown on module method execution, if any
	 * @throws Exception in case of errors
	 */
	public void afterCompletionAddEntry(Long folderId, String definitionId, InputDataAccessor inputData, 
    		Map fileItems, Map options, Throwable ex) throws Exception {
		logger.info("afterCompletionAddEntry: " + ((ex==null)? "Successful" : ex.toString()));		
	}
	
	/******************* Listener for modifyEntry method ********************/

    public void preModifyEntry(Long folderId, Long entryId, InputDataAccessor inputData, 
    		Map fileItems, Collection<String> deleteAttachments, Map<FileAttachment,String> fileRenamesTo, Map options) 
    	throws AccessControlException, WriteFilesException, WriteEntryDataException, ReservedByAnotherUserException {
		logger.info("preModifyEntry: About to modify the entry (id=" + entryId + ") in the folder (id=" + folderId + "). Here's textual input data -");
		FolderEntry entry = getFolderDao().loadFolderEntry(entryId, RequestContextHolder.getRequestContext().getZoneId());
		String definitionId = entry.getEntryDefId();
		Set<String> textualInputDataKeys = DefinitionHelper.getTextualInputDataKeys(definitionId, inputData);
		for(String key:textualInputDataKeys) {
			logger.info(key + " : " + inputData.getSingleValue(key));
		}
    }

	/******************* Listener for ReserveEntry method ********************/
	
	/**
	 * Pre-event method for FolderModule.ReserveEntry.
	 * <p>
	 * For general idea, see the method description of <code>preAddEntry</code> method.
	 * The only major difference from <code>preAddEntry</code> is that this method
	 * returns a boolean value indicating whether the system should continue with the
	 * normal execution of the pipeline or not. The value of <code>true</code> means
	 * normal continuation while <code>false</code> indicates that the execution
	 * pipeline must stop. Any other return value or type or no return value 
	 * (<code>void</code> type) means the same thing as returning <code>true</code>.
	 * 
	 * @return <code>true</code> if the execution chain should proceed with the next
	 * listener or the module method itself.
	 * @throws Exception in case of errors
	 */
	public boolean preReserveEntry(Long folderId, Long entryId) throws Exception {
		// Get the name of the user who is requesting to reserve this entry.
		String userName = RequestContextHolder.getRequestContext().getUserName();
		
		// If the requestor is not me (ie, admin) and the ID of the entry that 
		// the user is trying to reserve is 12, then keep the request from
		// proceeding by returning false from here. The entry 12 means so much
		// to me that I do not want anyone but me to be able to reserve it!
		if(!userName.equals(BuiltInUsersHelper.getAdminName()) && entryId.longValue() == 12) {
			logger.info("preReserveEntry: No, you can't do this");
			return false;
		}
		else {
			logger.info("preReserveEntry: OK, that's fine");
			return true;
		}
	}
	
	/**
	 * Post-event method for FolderModule.ReserveEntry.
	 * <p>
	 * For general idea, see the method description of <code>postAddEntry</code> method.
	 * The only major difference from <code>postAddEntry</code> is that this method
	 * does not append an extra argument to the argument list. This is because the 
	 * module method this listens on (ie, <code>reserveEntry</code>) returns no value
	 * (ie, <code>void</code> type). It is illegal to append an argument of type 
	 * <code>void</code> or <code>Void</code> in this case.
	 * 
	 * @throws Exception in case of errors
	 */
	public void postReserveEntry(Long folderId, Long entryId) throws Exception {
		// This method does not append an extra argument because the corresponding 
		// module method (reserveEntry) does not return anything (ie, void type). 
		
		logger.info("postReserveEntry");
	}
	
	/**
	 * After-completion method for FolderModule.ReserveEntry.
	 * <p>
	 * For general idea, see the method description of <code>afterCompletionAddEntry</code>
	 * method.
	 * 
	 * @throws Exception in case of errors
	 */
	public void afterCompletionReserveEntry(Long folderId, Long entryId, Throwable ex) {
		logger.info("afterCompletionReserveEntry");
	}
	
	private FolderDao getFolderDao() {
		return (FolderDao) SpringContextUtil.getBean("folderDao");
	}
}
