/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.web.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.ResourceDriverConfig.DriverType;
import org.kablink.teaming.fi.connection.ResourceDriverManagerUtil;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.resourcedriver.RDException;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;

/**
 * Helper class dealing with Cloud Folders.
 * 
 * @author drfoster@novell.com
 */
public class CloudFolderHelper {
	protected static Log m_logger = LogFactory.getLog(CloudFolderHelper.class);

	// Until this is all working and we decide to ship it, Cloud
	// Folders default to being turned off.
	public static final boolean CLOUD_FOLDERS_ENABLED = SPropsUtil.getBoolean("cloud.folders.enabled", false);

	/*
	 * Inner class that encapsulates the results of creating a Cloud
	 * Folder root.
	 */
	private static class CreateCFRootResult {
		private ResourceDriverConfig	m_rdConfig;		//
		private RDException				m_rdException;	//
		
		/**
		 * Constructor method.
		 */
		public CreateCFRootResult() {
			// Initialize the super class.
			super();
		}
		
		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public ResourceDriverConfig getResourceDriverConfig() {return m_rdConfig;   }
		public RDException          getRDException()          {return m_rdException;}
		
		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setResourceDriverConfig(ResourceDriverConfig rdConfig)    {m_rdConfig    = rdConfig;   }
		public void setRDException(         RDException          rdException) {m_rdException = rdException;}
	}
	
	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private CloudFolderHelper() {
		// Nothing to do.
	}

	/**
	 * Create a Cloud Folder from the given data.
	 * 
	 * @param bs
	 * @param owner
	 * @param name
	 * @param rootName
	 * @param parentBinderId
	 * 
	 * @return
	 * 
	 * @throws WriteFilesException
	 * @throws WriteEntryDataException
	 */
	public static Binder createCloudFolder(
		AllModulesInjected	bs,
		User				owner,
		String				name,
		String				rootName,
		Long				parentBinderId)
			throws
				WriteFilesException,
				WriteEntryDataException {
		// Find the template binder for Cloud Folders.
		Long templateId = null;
		List<TemplateBinder> listOfTemplateBinders = bs.getTemplateModule().getTemplates(true);
		if (MiscUtil.hasItems(listOfTemplateBinders)) {
			for (TemplateBinder nextTemplateBinder:  listOfTemplateBinders) {
				String internalId = nextTemplateBinder.getInternalId();
				if ((null != internalId) && internalId.equalsIgnoreCase(ObjectKeys.DEFAULT_FOLDER_FILR_ROOT_CLOUD_FOLDER_CONFIG)) {
					templateId = nextTemplateBinder.getId();
					break;
				}
			}
		}

		// Did we find the template?
		Binder binder;
		if (null != templateId) {
			// Yes!  Can we create the Cloud Folder?
			binder = bs.getFolderModule().createCloudFolder(
				templateId,
				parentBinderId,
				name,
				owner,
				rootName);
			
			if (null != binder) {
				// Yes!  Store its Cloud Folder root name in its
				// properties...
				Long binderId = binder.getId();
				bs.getBinderModule().setProperty(
					binderId,
					ObjectKeys.BINDER_PROPERTY_CLOUD_FOLDER_ROOT,
					rootName);
				
				// ...turn on JITS on it...
				saveJitsSettings(
					bs,
					binderId,
					true,
					getDefaultJitsAclMaxAge(),
					getDefaultJitsResultsMaxAge());
				
				// ...and set it to NOT inherit rights from it's parent.
				bs.getAdminModule().setWorkAreaFunctionMembershipInherited(
					binder,
					false);	// false -> Don't inherit.			
			}
		}
		
		else {
			binder = null;
			m_logger.error("CloudFolderHelper.createCloudFolder():  Could not find the template binder for a Cloud Folder");
		}
		
		return binder;
	}

	/**
	 * Create a Cloud Folder root from the given data.
	 * 
	 * @param bs
	 * @param name
	 * @param uncPath
	 * @param memberIds
	 * 
	 * @return
	 * 
	 * @throws RDException
	 */
	public static ResourceDriverConfig createCloudFolderRoot(final AllModulesInjected bs, final String name, final String uncPath, final Set<Long> memberIds) throws RDException {
		// As admin, create the Cloud Folder root.
		CreateCFRootResult reply = ((CreateCFRootResult) RunasTemplate.runasAdmin(
			new RunasCallback() {
				@Override
				public Object doAs() {
					CreateCFRootResult reply = createCloudFolderRootImpl(bs, name, uncPath, memberIds);
					RDException rdException = reply.getRDException();
					if (null != rdException) {
						throw rdException;
					}
					return reply;
				}
			},
			RequestContextHolder.getRequestContext().getZoneName()));

		// Did we catch an exception during the create?
		RDException rdException = reply.getRDException();
		if (null != rdException) {
			// Yes!  Propagate it.
			throw rdException;
		}
		
		// Return the ResourceDriverConfig we created.
		return reply.getResourceDriverConfig();
	}

	/*
	 * Create a Cloud Folder root from the given data.
	 */
	@SuppressWarnings("unchecked")
	private static CreateCFRootResult createCloudFolderRootImpl(AllModulesInjected bs, String name, String uncPath, Set<Long> memberIds) {
		CreateCFRootResult reply = new CreateCFRootResult();
		
		// Does a Cloud Folder root already exist with the give name?
		if (null != findCloudFolderRootByName(bs, name)) {
			// Yes!  Do not allow this.
			reply.setRDException(
				new RDException(
					NLT.get(RDException.DUPLICATE_RESOURCE_DRIVER_NAME, new String[] {name}),
					name));
		}
		
		else {
			// No, a Cloud Folder root doesn't exist with the give
			// name!
			Map options = new HashMap();
			options.put(ObjectKeys.RESOURCE_DRIVER_READ_ONLY, Boolean.FALSE);
			
			// Always prevent the top level folder from being deleted.
			// This is forced so that the folder could not accidentally
			// be deleted if the external disk was off line.
			options.put(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE, Boolean.FALSE);
	
			// Add this resource driver.
			reply.setResourceDriverConfig(
				bs.getResourceDriverModule().addResourceDriver(
					name,
					DriverType.cloud_folders, 
					uncPath,
					memberIds,
					options));
		}

		return reply;
	}

	/**
	 * Delete the given Cloud Folder.
	 * 
	 * @param bs
	 * @param id
	 * @param rootName
	 * @param deleteSource
	 */
	public static void deleteCloudFolder(final AllModulesInjected bs, final Long id, final String rootName, final boolean deleteSource) {
		bs.getFolderModule().deleteCloudFolder(id, deleteSource);
		
		RunasTemplate.runasAdmin(
			new RunasCallback() {
				@Override
				public Object doAs() {
					bs.getResourceDriverModule().deleteResourceDriver(rootName);
					return null;
				}
			},
			RequestContextHolder.getRequestContext().getZoneName());
	}

	/**
	 * Delete the given Cloud Folder.
	 * 
	 * @param bs
	 * @param folder
	 * @param deleteSource
	 */
	public static void deleteCloudFolder(AllModulesInjected bs, Folder folder, boolean deleteSource) {
		// Always use the initial form of the method.
		deleteCloudFolder(bs, folder.getId(), getCloudFolderRoot(folder), deleteSource);
	}

	/**
	 * Finds a Cloud Folder root with the give ID.
	 * 
	 * @param bs
	 * @param id
	 * 
	 * @return
	 */
	public static ResourceDriverConfig findCloudFolderRootById(AllModulesInjected bs, Long id) {
		if (null == id) {
			return null;
		}
		
		// Get a list of the currently defined Net Folder Roots
		List<ResourceDriverConfig> drivers = bs.getResourceDriverModule().getAllCloudFolderResourceDriverConfigs();
		if (MiscUtil.hasItems(drivers)) {
			for (ResourceDriverConfig driver:  drivers) {
				if (id.equals(driver.getId())) {
					return driver;
				}
			}
		}
		
		// If we get here we did not find a Cloud Folder root with the
		// given ID.
		return null;
	}

	/**
	 * Finds a Cloud Folder root with the give server UNC.
	 * 
	 * @param bs
	 * @param serverUNC
	 * 
	 * @return
	 */
	public static ResourceDriverConfig findCloudFolderRootByUNC(AllModulesInjected bs, String serverUNC) {
		if (!(MiscUtil.hasString(serverUNC))) {
			return null;
		}
		
		// Get a list of the currently defined Net Folder Roots
		List<ResourceDriverConfig> drivers = bs.getResourceDriverModule().getAllCloudFolderResourceDriverConfigs();
		if (MiscUtil.hasItems(drivers)) {
			for (ResourceDriverConfig driver:  drivers) {
				if (serverUNC.equalsIgnoreCase(driver.getRootPath())) {
					return driver;
				}
			}
		}
		
		// If we get here we did not find a Cloud Folder root with the
		// given UNC.
		return null;
	}
	
	/**
	 * Finds a Cloud Folder root with the given name.
	 * 
	 * @param bs
	 * @param name
	 * 
	 * @return
	 */
	public static ResourceDriverConfig findCloudFolderRootByName(AllModulesInjected bs, String name) {
		if (!(MiscUtil.hasString(name))) {
			return null;
		}
		
		// Get a list of the currently defined Net Folder Roots
		List<ResourceDriverConfig> drivers = bs.getResourceDriverModule().getAllCloudFolderResourceDriverConfigs();
		if (MiscUtil.hasItems(drivers)) {
			for (ResourceDriverConfig driver:  drivers) {
				if (name.equalsIgnoreCase(driver.getName())) {
					return driver;
				}
			}
		}
		
		// If we get here we did not find a Cloud Folder root with the
		// given name.
		return null;
	}
	
	/**
	 * Return all the Cloud Folders that are associated with the given
	 * Cloud Folder server.
	 * 
	 * @param bs
	 * @param rootName
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Long> getAllCloudFolders(AllModulesInjected bs, String rootName) {
		// Add the criteria for finding all top-level Cloud Folders.
		FilterControls filterCtrls = new FilterControls();
		filterCtrls.add(ObjectKeys.FIELD_BINDER_MIRRORED, Boolean.TRUE);
		if (MiscUtil.hasString(rootName)) {
			filterCtrls.add(ObjectKeys.FIELD_BINDER_RESOURCE_DRIVER_NAME, rootName);
		}
		filterCtrls.add(ObjectKeys.FIELD_BINDER_IS_HOME_DIR, Boolean.FALSE);

		// Get the list of Cloud Folders for the given criteria.
		List<Long> listOfCloudFolderIds = new ArrayList<Long>();
		List<Folder> results = getCoreDao().loadObjects(Folder.class, filterCtrls, RequestContextHolder.getRequestContext().getZone().getId());
		if (MiscUtil.hasItems(results)) {
			// We only want to return top-level Cloud Folders.
			for (Folder nextFolder:  results) {
				if (nextFolder.isTop() && (!(nextFolder.isDeleted()))) {
					listOfCloudFolderIds.add(nextFolder.getId());
				}
			}
		}

		return listOfCloudFolderIds;
	}
	
	/**
	 * If a binder is a Cloud Folder, returns the name of its Cloud
	 * Folder root.  Otherwise, returns null.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static String getCloudFolderRoot(Binder binder) {
		boolean isCloudFolder = ((null != binder) && (binder instanceof Folder) && binder.isAclExternallyControlled());
		String reply = null;
		if (isCloudFolder) {
			String cfRoot = ((String) binder.getProperty(ObjectKeys.BINDER_PROPERTY_CLOUD_FOLDER_ROOT));
			if (MiscUtil.hasString(cfRoot)) {
				reply = cfRoot;
			}
		}
		return reply;
	}
	
	/*
	 * Returns a CoreDao object. 
	 */
	private static CoreDao getCoreDao() {
		return ((CoreDao) SpringContextUtil.getBean("coreDao"));
	}

	/*
	 */
	private static long getDefaultJitsAclMaxAge() {
		return NetFolderHelper.getDefaultJitsAclMaxAge();
	}
	
	/*
	 */
	private static long getDefaultJitsResultsMaxAge() {
		return NetFolderHelper.getDefaultJitsResultsMaxAge();
	}
	
	/**
	 * Returns true if a binder is a Cloud Folder and false otherwise.
	 * 
	 * @param binder
	 * 
	 * @return
	 */
	public static boolean isCloudFolder(Binder binder) {
		return MiscUtil.hasString(getCloudFolderRoot(binder));
	}
	
	/**
	 * Modifies a Cloud Folder.
	 * 
	 * @param bs
	 * @param id
	 * @param name
	 * @param rootName
	 * 
	 * @throws AccessControlException
	 * @throws WriteFilesException
	 * @throws WriteEntryDataException
	 */
	public static void modifyCloudFolder(
		AllModulesInjected	bs,
		Long				id,
		String				name,
		String				rootName)
			throws
				AccessControlException,
				WriteFilesException,
				WriteEntryDataException {
		// Modify the binder with the Cloud Folder information.
		bs.getFolderModule().modifyCloudFolder(id, name, rootName);
	}

	/**
	 * Modifies a Cloud Folder root.
	 * 
	 * @param bs
	 * @param rootName
	 * @param uncPath
	 * @param memberIds
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static ResourceDriverConfig modifyCloudFolderRoot(AllModulesInjected bs, String rootName, String uncPath, Set<Long> memberIds) {
		Map options = new HashMap();
		options.put(ObjectKeys.RESOURCE_DRIVER_READ_ONLY, Boolean.FALSE);

		// Always prevent the top level folder from being deleted
		// This is forced so that the folder could not accidentally be
		// deleted if the external disk was off line
		options.put(ObjectKeys.RESOURCE_DRIVER_SYNCH_TOP_DELETE, Boolean.FALSE);

		// Modify the resource driver...
		ResourceDriverConfig rdConfig = ResourceDriverManagerUtil.getResourceDriverManager().getDriverConfig(rootName);
		rdConfig = bs.getResourceDriverModule().modifyResourceDriver(
			rootName,
			DriverType.cloud_folders, 
			uncPath,
			memberIds,
			options);

		// ...and return it.
		return rdConfig;
	}
	
	/*
	 * Save the JITS settings for the given binder.
	 */
	@SuppressWarnings("unchecked")
	private static void saveJitsSettings(AllModulesInjected	bs, Long binderId, boolean jitsEnabled, long aclMaxAge, long resultsMaxAge) {
		if (null != binderId) {
			Map formData   = new HashMap();
			formData.put(ObjectKeys.FIELD_BINDER_JITS_ENABLED,         Boolean.toString(jitsEnabled)  );
			formData.put(ObjectKeys.FIELD_BINDER_JITS_ACL_MAX_AGE,     String.valueOf(  aclMaxAge)    );
			formData.put(ObjectKeys.FIELD_BINDER_JITS_RESULTS_MAX_AGE, String.valueOf(  resultsMaxAge));
			MapInputData mid = new MapInputData(formData);

			try {
				bs.getBinderModule().modifyBinder(binderId, mid, new HashMap(), new HashSet(), null);
			}
			catch (Exception ex) {
				m_logger.error("CloudFolderHelper.saveJitsSettings():  Call to modifyBinder() failed. " + ex.toString());
			}
		}
	}
}
