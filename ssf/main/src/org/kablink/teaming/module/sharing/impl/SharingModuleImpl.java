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
package org.kablink.teaming.module.sharing.impl;

import java.util.*;

import org.kablink.teaming.InvalidEmailAddressException;
import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.ShareItem.RecipientType;
import org.kablink.teaming.jobs.ExpiredShareHandler;
import org.kablink.teaming.jobs.ZoneSchedule;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.BinderModule.BinderOperation;
import org.kablink.teaming.module.binder.processor.BinderProcessor;
import org.kablink.teaming.module.file.ConvertedFileModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.folder.FolderModule.FolderOperation;
import org.kablink.teaming.module.folder.processor.FolderCoreProcessor;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.AccessControlManager;
import org.kablink.teaming.security.AccessControlNonCodedException;
import org.kablink.teaming.security.function.WorkArea;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.GangliaMonitoring;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.ShareLists;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.TagUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.util.ListUtil;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.util.api.ApiErrorCode;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * This module gives us the transaction semantics to deal with the "Shared with Me" features.  
 *
 * @author Peter Hurley
 */
public class SharingModuleImpl extends CommonDependencyInjection implements SharingModule, ZoneSchedule {
    private static long MILLISEC_IN_A_DAY = 86400000;

	private AdminModule adminModule;
	private FolderModule folderModule;
	private BinderModule binderModule;
	private ProfileModule profileModule;
	private ConvertedFileModule convertedFileModule;
	private TransactionTemplate transactionTemplate;
	
    protected ConvertedFileModule getConvertedFileModule() {
    	if (null == convertedFileModule) {
    		convertedFileModule = ((ConvertedFileModule) SpringContextUtil.getBean("convertedFileModule"));
    	}
		return convertedFileModule;
	}
	public void setConvertedFileModule(ConvertedFileModule convertedFileModule) {
		this.convertedFileModule = convertedFileModule;
	}

    protected TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

    @Override
	public void checkAccess(ShareItem shareItem, SharingOperation operation)
	    	throws AccessControlException {
		EntityIdentifier entityIdentifier = shareItem.getSharedEntityIdentifier();
    	checkAccess(shareItem, entityIdentifier, operation);
    }
    
    @Override
	@SuppressWarnings("unchecked")
	public void checkAccess(ShareItem shareItem, EntityIdentifier entityIdentifier, SharingOperation operation)
	    	throws AccessControlException {
    	User user = RequestContextHolder.getRequestContext().getUser();
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
		AccessControlManager accessControlManager = getAccessControlManager();
		if (accessControlManager == null) {
			accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
		}
    	Long allUsersId = Utils.getAllUsersGroupId();
    	Long allExtUsersId = Utils.getAllExtUsersGroupId();

		Principal recipient = null;
		if (shareItem.getRecipientType().equals(RecipientType.group)) {
			recipient = getProfileModule().getEntry(shareItem.getRecipientId());
            if (!recipient.getEntityType().equals(EntityType.group)) {
                throw new NoGroupByTheIdException(shareItem.getRecipientId());
            }
   		} else if (shareItem.getRecipientType().equals(RecipientType.user)) {
            if (shareItem.getRecipientId().equals(user.getId())) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Can't share with yourself.");
            }
            recipient = getProfileModule().getEntry(shareItem.getRecipientId());
            if (!recipient.getEntityType().equals(EntityType.user)) {
                throw new NoUserByTheIdException(shareItem.getRecipientId());
            }
        }

		switch (operation) {
		case addShareItem:
			//Make sure sharing is enabled at the zone level for this type of user
			if (shareItem.getRecipientType().equals(RecipientType.group) && recipient != null) {
				if (allUsersId.equals(recipient.getId())) {
					accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_ALL_INTERNAL);
				} else if (allExtUsersId.equals(recipient.getId())) {
					accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_ALL_EXTERNAL);
				}

				//Distinguish between internal and external groups
				if (recipient.getIdentityInfo().isInternal()) {
					accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_INTERNAL);
				} else {
					accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_EXTERNAL);
				}
			} else if (shareItem.getRecipientType().equals(RecipientType.user) && recipient != null) {
				//Users can be guest (i.e., shared), internal, or external
				if (((User)recipient).isShared()) {
					if (!zoneConfig.getAuthenticationConfig().isAllowAnonymousAccess()) {
						//Anonymous has not been enabled by the administrator
						throw new AccessControlException();
					}
					accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_PUBLIC);
					//Also make sure the rights being given out are no more than Viewer
					if (!validateGuestAccessRights(shareItem)) {
						//There are rights that are not allowed for sharing with guest. Throw an error
						throw new AccessControlException();
					}
				} else if (recipient.getIdentityInfo().isInternal()) {
					accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_INTERNAL);
				} else {
					accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_EXTERNAL);
				}
			} else if (shareItem.getRecipientType().equals(RecipientType.team)) {
				//Sharing with team not allowed yet. Teams need to be identified as internal, external, or public
				throw new AccessControlException();
			} else if (shareItem.getRecipientType().equals(RecipientType.publicLink)) {
				//Check that the current user is enabled for this at the zone level
				accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_LINK_SHARING);
			}
			
			//Check the setting of the Share Forward right
			boolean tryingToAllowShareForward = shareItem.getRightSet().getRight(WorkAreaOperation.ALLOW_SHARING_FORWARD);
			if (tryingToAllowShareForward) {
				//The share item is trying to give out the share forward right. See if the user is allowed to do this
				accessControlManager.checkOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_FORWARD);
			}

			//Check that the user has the right to share this entity
			if (entityIdentifier.getEntityType().equals(EntityType.folderEntry)) {
				FolderEntry fe = getFolderModule().getEntry(null, entityIdentifier.getEntityId());
				//First, see if this is an entry in a Net Folder. If so, we must test the root level permissions.
				if (fe.isAclExternallyControlled()) {
					//This is in a net folder. we must check if the admin allows the requested operation at the root level.
					Binder topFolder = fe.getParentBinder();
					while (topFolder != null) {
						if (topFolder.getParentBinder() != null &&
								!topFolder.getParentBinder().getEntityType().name().equals(EntityType.folder.name())) {
							//We have found the top folder (i.e., the net folder root)
							break;
						}
						topFolder = topFolder.getParentBinder();
					}
					//Now check if the root folder allows the requested operation
					if (topFolder != null) {
						//Check if the binder allows share forward
						if (tryingToAllowShareForward) {
							//Test if entry allows sharing forward
							if (!accessControlManager.testRightGrantedBySharing(user, 
									(WorkArea)fe, WorkAreaOperation.ALLOW_SHARING_FORWARD)) {
								//The entry didn't have the right due to sharing, so now check the parent folder
								if (!binderModule.testAccess(topFolder, BinderOperation.allowSharingForward)) {
									throw new AccessControlException("errorcode.sharing.forward.notAllowed", new Object[] {});
								}
							}
						}
						if (shareItem.getRecipientType().equals(RecipientType.group) && recipient != null) {
							if (recipient.getIdentityInfo().isInternal()) {
								if (!accessControlManager.testRightGrantedBySharing(user, 
										(WorkArea)fe, WorkAreaOperation.ALLOW_SHARING_INTERNAL)) {
									if (!binderModule.testAccess(topFolder, BinderOperation.allowSharing)) {
										throw new AccessControlException("errorcode.sharing.topNetfolder.notAllowed", new Object[] {});
									}
								}
							} else {
								if (!accessControlManager.testRightGrantedBySharing(user, 
										(WorkArea)fe, WorkAreaOperation.ALLOW_SHARING_EXTERNAL)) {
									if (!binderModule.testAccess(topFolder, BinderOperation.allowSharingExternal)) {
										throw new AccessControlException("errorcode.sharing.topNetfolder.notAllowed", new Object[] {});
									}
								}
							}
						} else if (shareItem.getRecipientType().equals(RecipientType.user) && recipient != null) {
							if (((User)recipient).isShared()) {
								if (!accessControlManager.testRightGrantedBySharing(user, 
										(WorkArea)fe, WorkAreaOperation.ALLOW_SHARING_PUBLIC)) {
									if (!binderModule.testAccess(topFolder, BinderOperation.allowSharingPublic)) {
										throw new AccessControlException("errorcode.sharing.topNetfolder.notAllowed", new Object[] {});
									}
								}
							} else if (recipient.getIdentityInfo().isInternal()) {
								if (!accessControlManager.testRightGrantedBySharing(user, 
										(WorkArea)fe, WorkAreaOperation.ALLOW_SHARING_INTERNAL)) {
									if (!binderModule.testAccess(topFolder, BinderOperation.allowSharing)) {
										throw new AccessControlException("errorcode.sharing.topNetfolder.notAllowed", new Object[] {});
									}
								}
							} else {
								if (!accessControlManager.testRightGrantedBySharing(user, 
										(WorkArea)fe, WorkAreaOperation.ALLOW_SHARING_EXTERNAL)) {
									if (!binderModule.testAccess(topFolder, BinderOperation.allowSharingExternal)) {
										throw new AccessControlException("errorcode.sharing.topNetfolder.notAllowed", new Object[] {});
									}
								}
							}
						} else if (shareItem.getRecipientType().equals(RecipientType.team)) {
							//Sharing with team not allowed yet. Teams need to be identified as internal, external, or public
							throw new AccessControlException();
						}
					} else {
						//The root folder does not exist
						throw new AccessControlException("errorcode.sharing.topNetfolder.notAllowed", new Object[] {});
					}
				}
				//Check if the entry allows share forward
				if (tryingToAllowShareForward) {
					//Test if entry allows sharing forward
					if (!folderModule.testAccess(fe, FolderOperation.allowSharingForward)) {
						throw new AccessControlException("errorcode.sharing.forward.notAllowed", new Object[] {});
					}
				}
				//Now check that the entry itself allows the requested operation
				//First test if the user is allowed to do all of the rights in the rights list
				List<FolderOperation> ops = shareItem.getRightSet().getFolderEntryRights();
				for (FolderOperation op : ops) {
					folderModule.checkAccess(fe, op);
				}
				if (shareItem.getRecipientType().equals(RecipientType.group) && recipient != null) {
					if (recipient.getIdentityInfo().isInternal()) {
						if (folderModule.testAccess(fe, FolderOperation.allowSharing)) {
							return;
						}
					} else {
						if (folderModule.testAccess(fe, FolderOperation.allowSharingExternal)) {
							return;
						}
					}
				} else if (shareItem.getRecipientType().equals(RecipientType.user) && recipient != null) {
					if (((User)recipient).isShared()) {
						if (folderModule.testAccess(fe, FolderOperation.allowSharingPublic)) {
							return;
						}
					} else if (recipient.getIdentityInfo().isInternal()) {
						if (folderModule.testAccess(fe, FolderOperation.allowSharing)) {
							return;
						}
					} else {
						if (folderModule.testAccess(fe, FolderOperation.allowSharingExternal)) {
							return;
						}
					}
				} else if (shareItem.getRecipientType().equals(RecipientType.team)) {
					//Sharing with team not allowed yet. Teams need to be identified as internal, external, or public
					throw new AccessControlException();
				}
				else if ( shareItem.getRecipientType().equals( RecipientType.publicLink ) )
				{
					// Is sharing with the public enabled?
					if ( folderModule.testAccess( fe, FolderOperation.allowSharingPublicLinks ) )
					{
						// Yes
						return;
					}
				}
			} else if (entityIdentifier.getEntityType().equals(EntityType.folder) ||
					entityIdentifier.getEntityType().equals(EntityType.workspace)) {
				Binder binder = getBinderModule().getBinder(entityIdentifier.getEntityId());
				//If this is a Filr Net Folder, check if sharing is allowed at the folder level
				if (binder.isAclExternallyControlled() && 
						!SPropsUtil.getBoolean("sharing.netFolders.allowed", false)) {
					//See if this Filr folder is in the user's personal workspace. We allow sharing of the home folder
					if (!Utils.isWorkareaInProfilesTree(binder)) {
						throw new AccessControlException("errorcode.sharing.netfolders.notAllowed", new Object[] {});
					}
				}
				//Check if the binder allows share forward
				if (tryingToAllowShareForward) {
					//Test if entry allows sharing forward
					if (!binderModule.testAccess(binder, BinderOperation.allowSharingForward)) {
						throw new AccessControlException("errorcode.sharing.forward.notAllowed", new Object[] {});
					}
				}
				//Now check the access to the binder
				//First test if the user is allowed to do all of the rights in the rights list
				List<Object> ops = shareItem.getRightSet().getFolderRights();
				for (Object op : ops) {
					if (op instanceof WorkAreaOperation) {
						accessControlManager.checkOperation(binder, (WorkAreaOperation)op);
					} else if (op instanceof BinderOperation) {
						binderModule.checkAccess(binder, (BinderOperation)op);
					} else if (op instanceof FolderOperation && binder instanceof Folder) {
						folderModule.checkAccess((Folder)binder, (FolderOperation)op);
					} else {
						throw new AccessControlException();
					}
				}
				if (shareItem.getRecipientType().equals(RecipientType.group) && recipient != null) {
					if (recipient.getIdentityInfo().isInternal()) {
						if (binderModule.testAccess(binder, BinderOperation.allowSharing)) {
							return;
						}
					} else {
						if (binderModule.testAccess(binder, BinderOperation.allowSharingExternal)) {
							return;
						}
					}
				} else if (shareItem.getRecipientType().equals(RecipientType.user) && recipient != null) {
					if (((User)recipient).isShared()) {
						if (binderModule.testAccess(binder, BinderOperation.allowSharingPublic)) {
							return;
						}
					} else if (recipient.getIdentityInfo().isInternal()) {
						if (binderModule.testAccess(binder, BinderOperation.allowSharing)) {
							return;
						}
					} else {
						if (binderModule.testAccess(binder, BinderOperation.allowSharingExternal)) {
							return;
						}
					}
				} else if (shareItem.getRecipientType().equals(RecipientType.team)) {
					//Sharing with team not allowed yet. Teams need to be identified as internal, external, or public
					throw new AccessControlException();
				}
			}
			break;
		case modifyShareItem:
			//The share creator and the entity owner can modify a shareItem
			if (!user.getId().equals(shareItem.getSharerId()))
			{
				//The user is not the creator of the share. Only the share item creator is allowed to modify it
				// Check for site administrator.  It is ok if the admin changes a share item he didn't create.
				if ( accessControlManager.testOperation( user, zoneConfig, WorkAreaOperation.ZONE_ADMINISTRATION ) == false )
				{
					// User is not a site administrator
					throw new AccessControlException();
				}
			}
			//Now check if this user is still allowed to add a share of this entity
			checkAccess(shareItem, SharingOperation.addShareItem);
			return;
		case deleteShareItem:
			//The share creator, the entity owner, or the site admin can delete a shareItem
			if (user.getId().equals(shareItem.getSharerId())) {
				//The user is the creator of the share
				return;
			}
			//Check if this is the owner of the entity
			if (entityIdentifier.getEntityType().equals(EntityType.folderEntry)) {
				FolderEntry fe = getFolderModule().getEntry(null, entityIdentifier.getEntityId());
				if (user.getId().equals(fe.getCreation().getPrincipal().getId())) {
					//This is the owner of the entry. Allow the modification.
					if (folderModule.testAccess(fe, FolderOperation.changeACL)) {
						return;
					}
				}
			} else if (entityIdentifier.getEntityType().equals(EntityType.folder) ||
					entityIdentifier.getEntityType().equals(EntityType.workspace)) {
				Binder binder = getBinderModule().getBinder(entityIdentifier.getEntityId());
				if (user.getId().equals(binder.getCreation().getPrincipal().getId())) {
					//This is the owner of the binder. Allow the modification.
					if (binderModule.testAccess(binder, BinderOperation.changeACL)) {
						return;
					}
				}
			}
			//Check for site administrator
			if (accessControlManager.testOperation(user, zoneConfig, WorkAreaOperation.ZONE_ADMINISTRATION)) {
				//This is a site administrator
				return;
			}
			break;
		default:
			throw new NotSupportedException(operation.toString(),
					"checkAccess");
		}
		//No access was found
		throw new AccessControlException();
	}

    @Override
	public boolean testAccess(ShareItem shareItem, SharingOperation operation) {
		EntityIdentifier entityIdentifier = shareItem.getSharedEntityIdentifier();
    	return testAccess(shareItem, entityIdentifier, operation);
    }
    @Override
	public boolean testAccess(ShareItem shareItem, EntityIdentifier entityIdentifier, SharingOperation operation) {
    	try {
    		checkAccess(shareItem, entityIdentifier, operation);
    		return true;
    	}
    	catch (Exception e) {
    		return false;
    	}
    }

    /**
     * Returns true if the current user can share the given
     * DefinableEntity and false otherwise.
     * 
     * @param de
     * 
     * @return
     */
    @Override
	public boolean testAddShareEntity(DefinableEntity de) {
        return _testAddShareEntity(de, new ShareOp [] {
                new ShareOp(WorkAreaOperation.ENABLE_SHARING_INTERNAL, FolderOperation.allowSharing, BinderOperation.allowSharing),
                new ShareOp(WorkAreaOperation.ENABLE_SHARING_EXTERNAL, FolderOperation.allowSharingExternal, BinderOperation.allowSharingExternal),
        });
	}

    /**
     * Returns true if the current user can share the given
     * DefinableEntity with internal users and false otherwise.
     *
     * @param de
     *
     * @return
     */
    @Override
	public boolean testAddShareEntityInternal(DefinableEntity de) {
        return _testAddShareEntity(de, new ShareOp [] {
                new ShareOp(WorkAreaOperation.ENABLE_SHARING_INTERNAL, FolderOperation.allowSharing, BinderOperation.allowSharing),
        });
	}

    /**
     * Returns true if the current user can share the given
     * DefinableEntity with external users and false otherwise.
     *
     * @param de
     *
     * @return
     */
    @Override
	public boolean testAddShareEntityExternal(DefinableEntity de) {
        return _testAddShareEntity(de, new ShareOp [] {
                new ShareOp(WorkAreaOperation.ENABLE_SHARING_EXTERNAL, FolderOperation.allowSharingExternal, BinderOperation.allowSharingExternal),
        });
	}

    /**
     * Returns true if the current user can share the given
     * DefinableEntity with the public and false otherwise.
     * 
     * @param de
     * 
     * @return
     */
	@Override
	public boolean testAddShareEntityPublic(DefinableEntity de) {
        return _testAddShareEntity(de, new ShareOp [] {
                new ShareOp(WorkAreaOperation.ENABLE_SHARING_PUBLIC, FolderOperation.allowSharingPublic, BinderOperation.allowSharingPublic)
        });
	}
	
    
    /**
     * Returns true if the current user can share public links of the
     * given DefinableEntity and false otherwise.
     * 
     * @param de
     * 
     * @return
     */
	@Override
	public boolean testAddShareEntityPublicLinks(DefinableEntity de) {
        return _testAddShareEntity(de, new ShareOp [] {
                new ShareOp(WorkAreaOperation.ENABLE_LINK_SHARING, FolderOperation.allowSharingPublicLinks, BinderOperation.allowSharingPublicLinks)
        });
	}
	
    
    /**
     * Returns true if the current user can share forward the given
     * DefinableEntity and false otherwise.
     * 
     * @param de
     * 
     * @return
     */
	@Override
	public boolean testShareEntityForward(DefinableEntity de) {
        return _testAddShareEntity(de, new ShareOp [] {
                new ShareOp(WorkAreaOperation.ENABLE_SHARING_FORWARD,FolderOperation.allowSharingForward,BinderOperation.allowSharingForward)
        });
	}

    /**
     * Returns true if the current user can share the given
     * DefinableEntity and false otherwise.
     *
     * @param de
     *
     * @return
     */
    @SuppressWarnings("unused")
    private boolean _testAddShareEntity(DefinableEntity de, ShareOp [] ops) {
        boolean reply = false;
		User user = RequestContextHolder.getRequestContext().getUser();

        try {
            // Is sharing enabled at the zone level for this type of user.
            Long					zoneId               = RequestContextHolder.getRequestContext().getZoneId();
            ZoneConfig				zoneConfig           = getCoreDao().loadZoneConfig(zoneId);
            AccessControlManager	accessControlManager = getAccessControlManager();
            if (null == accessControlManager) {
                accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
            }

            // Is the entity a folder entry?
            if (de.getEntityType().equals(EntityType.folderEntry)) {
                // Yes!  Does the user have "share internal" rights on it and is the user enabled for doing this?
                FolderEntry fe = ((FolderEntry) de);
                Binder parentBinderToTest = null;
                if (fe.isAclExternallyControlled()) {
                    //This is in a net folder. we must check if the admin allows the requested operation at the root level.
                    parentBinderToTest = fe.getParentBinder();
                    //Find the root net folder
                    while (parentBinderToTest != null) {
                        if (parentBinderToTest.getParentBinder() != null &&
                                !parentBinderToTest.getParentBinder().getEntityType().name().equals(EntityType.folder.name())) {
                            //We have found the top folder (i.e., the net folder root)
                            break;
                        }
                        parentBinderToTest = parentBinderToTest.getParentBinder();
                    }
                }

                for (ShareOp op : ops) {
                    //Look for at least one type of sharing being enabled: internal, external or public
                    // Does the user have "share internal" rights and is the user enabled to do this?
                    if (accessControlManager.testOperation(zoneConfig, op.workAreaOperation) &&
                            folderModule.testAccess(fe, op.folderOperation)) {
                        // Yes!
                        reply = true;
                    } else {
                    	//Cannot get at the entry directly, so try its parent folder
                        if (parentBinderToTest != null) {
                            reply = binderModule.testAccess(parentBinderToTest, op.binderOperation);
                        }
                    	
                    }
                    if (reply) {
                        break;
                    }
                }
            }

            // No, the entity isn't a folder entry!  Is it a folder or
            // workspace (i.e., a binder)?
            else if (de.getEntityType().equals(EntityType.folder) || de.getEntityType().equals(EntityType.workspace)) {
                // Yes!  Does the user have "share internal" rights on it?
                Binder binder = ((Binder) de);
                //If this is a Filr Net Folder, check if sharing is allowed at the folder level
                //Also check that the folder isn't a Net Folder.
                //Sharing Net Folders is not allowed unless it is in the user's own user workspace
                if (!binder.isAclExternallyControlled() ||
                        SPropsUtil.getBoolean("sharing.netFolders.allowed", false) ||
                        Utils.isWorkareaInProfilesTree(binder)) {
                    for (ShareOp op : ops) {
                        if (accessControlManager.testOperation(zoneConfig, op.workAreaOperation) &&
                            binderModule.testAccess(binder, op.binderOperation)) {
                            // Yes!
                            reply = true;
                            break;
                        }
                    }
                }
            }
        }
        catch (AccessControlException ace) {
            // AccessControlException implies sharing isn't allowed.
            reply = false;
        }

        // If we get here, reply contains true if the user can add a
        // share to the given entity and false otherwise.  Return it.
        return reply;
    }
    
    //Routine to validate the rights being given to guest in a share
	public boolean validateGuestAccessRights(ShareItem shareItem) {
		List<WorkAreaOperation> rights = shareItem.getRightSet().getRights();
		for (WorkAreaOperation wao : rights) {
			//The following rights are OK for guest. All other rights are not allowed
			if (!WorkAreaOperation.READ_ENTRIES.equals(wao) && 
					!WorkAreaOperation.VIEW_BINDER_TITLE.equals(wao) && 
					!WorkAreaOperation.ADD_REPLIES.equals(wao)) {
				return false;
			}
		}
		return true;
	}
	
    
	@Override
	public boolean isShareForwardingEnabled() {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
		AccessControlManager accessControlManager = getAccessControlManager();
		if (accessControlManager == null) {
			accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
		}
		
		return accessControlManager.testOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_FORWARD);
	}

	@Override
	public boolean isSharingEnabled() {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
		AccessControlManager accessControlManager = getAccessControlManager();
		if (accessControlManager == null) {
			accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
		}
		
		return (
			accessControlManager.testOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_EXTERNAL) ||
			accessControlManager.testOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_INTERNAL) ||
			accessControlManager.testOperation(zoneConfig, WorkAreaOperation.ENABLE_SHARING_PUBLIC));
	}

	@Override
	public boolean isSharingPublicLinksEnabled() {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
		AccessControlManager accessControlManager = getAccessControlManager();
		if (accessControlManager == null) {
			accessControlManager = ((AccessControlManager) SpringContextUtil.getBean("accessControlManager"));
		}
		
		return accessControlManager.testOperation(zoneConfig, WorkAreaOperation.ENABLE_LINK_SHARING);
	}

    //NO transaction
	@Override
	public void addShareItem(final ShareItem shareItem) {
        determineExpiration(shareItem);

		// Access check (throws error if not allowed)
		checkAccess(shareItem, SharingOperation.addShareItem);

        verifyRecipient(shareItem);

        getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				getCoreDao().save(shareItem);
				
				//Log this share action in the change log
				addShareItemChangeLogEntry(shareItem, ChangeLog.SHARE_ADD);

				return null;
			}
		});
		
		//Index the entity that is being shared
		indexSharedEntity(shareItem);
		
		if(shareItem.getSharedEntityIdentifier().getEntityType() == EntityIdentifier.EntityType.folderEntry)
			GangliaMonitoring.incrementFilesShared();
		else if(shareItem.getSharedEntityIdentifier().getEntityType() == EntityIdentifier.EntityType.folder)
			GangliaMonitoring.incrementFoldersShared();
	}

    /**
     * Returns true if there are public shares that are active and
     * false otherwise.
     * 
     * @return
     */
    //NO transaction
	@Override
    public boolean arePublicSharesActive() {
		// Can we access the Guest user?
		User guest = getProfileModule().getGuestUser();
		if (null != guest) {
			// Yes!  Read the shares that have been made with Guest.
			ShareItemSelectSpec	spec = new ShareItemSelectSpec();
			spec.setRecipients(guest.getId(), null, null);
			List<ShareItem> shareItems = getShareItems(spec);
			
			// Did we find any?
			if (MiscUtil.hasItems(shareItems)) {
				// Yes!  Scan them.
				for (ShareItem si:  shareItems) {
					// Is this an active share that's part of a public
					// share?
					if ((!(si.isDeleted())) && (!(si.isExpired())) && si.isLatest() && si.getIsPartOfPublicShare()) {
						// Yes!  Return true.
						return true;
					}
				}
			}
		}
		
		// If we get here, we didn't find any active public shares.
		// Return false.
    	return false;
    }
    

    //NO transaction
	@Override
	public ShareItem modifyShareItem(final ShareItem latestShareItem, final Long previousShareItemId) {
		if(latestShareItem == null)
			throw new IllegalArgumentException("Latest share item must be specified");
		if(previousShareItemId == null)
			throw new IllegalArgumentException("Previous share item ID must be specified");
		if(latestShareItem.getId() != null)
			throw new IllegalArgumentException("Latest share item must be transient");

        determineExpiration(latestShareItem);
		
		// Access check (throws error if not allowed)
		checkAccess(latestShareItem, SharingOperation.modifyShareItem);

        ShareItem retItem = getTransactionTemplate().execute(new TransactionCallback<ShareItem>() {
			@Override
			public ShareItem doInTransaction(TransactionStatus status) {
				// Are we dealing with a "public link" share item?
				if ( latestShareItem.getRecipientType() != RecipientType.publicLink )
				{
					// No
					// Update previous snapshot
					try {
						ShareItem previousShareItem = getProfileDao().loadShareItem(previousShareItemId);
						
						previousShareItem.setLatest(false);
						
						getCoreDao().update(previousShareItem);
					}
					catch(NoShareItemByTheIdException e) {
						// The previous snapshot isn't found.
						logger.warn("Previous share item with id '" + previousShareItemId + "' is not found.");
					}

					// Create a new ShareItem
					getCoreDao().save(latestShareItem);

					//Log this share action in the change log
					addShareItemChangeLogEntry(latestShareItem, ChangeLog.SHARE_MODIFY);
				}
				else
				{
					// Yes, we can't delete the old ShareItem.  We need to update it.
					try
					{
						ShareItem previousShareItem;
						
						previousShareItem = getProfileDao().loadShareItem( previousShareItemId );
						
						// The only things that can be modified are the comments and end date.
						previousShareItem.setComment( latestShareItem.getComment() );
						previousShareItem.setDaysToExpire( latestShareItem.getDaysToExpire() );
						previousShareItem.setEndDate( latestShareItem.getEndDate() );
						previousShareItem.setStartDate( latestShareItem.getStartDate() );
						
						getCoreDao().update( previousShareItem );

						// Log this share action in the change log
						addShareItemChangeLogEntry( previousShareItem, ChangeLog.SHARE_MODIFY );
                        return previousShareItem;
					}
					catch( NoShareItemByTheIdException e )
					{
						// The "public link" share item was not found
						logger.warn( "Public link share item with id '" + previousShareItemId + "' is not found." );
					}
				}

				return null;
			}
		});
        if (retItem==null) {
            retItem = latestShareItem;
        }

		//Index the entity that is being shared
		indexSharedEntity(retItem);
        return retItem;
	}

    private void verifyRecipient(ShareItem shareItem) {
        if (shareItem.getRecipientType()==RecipientType.user) {
            Principal recipient = getProfileModule().getEntry(shareItem.getRecipientId());
            if (!recipient.getEntityType().equals(EntityType.user)) {
                throw new NoUserByTheIdException(shareItem.getRecipientId());
            }
            if (!recipient.isActive()) {
                throw new NoUserByTheIdException(shareItem.getRecipientId());
            }
            if (!recipient.getIdentityInfo().isInternal()) {
                String email = recipient.getEmailAddress();
                if (!isExternalAddressValid(email)) {
                    throw new InvalidEmailAddressException(email);
                }
            }
        } else if (shareItem.getRecipientType()==RecipientType.group) {
            Principal recipient = getProfileModule().getEntry(shareItem.getRecipientId());
            if (!recipient.getEntityType().equals(EntityType.group)) {
                throw new NoGroupByTheIdException(shareItem.getRecipientId());
            }
            if (!recipient.isActive()) {
                throw new NoUserByTheIdException(shareItem.getRecipientId());
            }
            if (recipient.getIdentityInfo().isFromLdap() && !getAdminModule().isSharingWithLdapGroupsEnabled()) {
                throw new AccessControlNonCodedException("System settings do not allow for sharing with LDAP groups.");
            }
        } else if (shareItem.getRecipientType()==RecipientType.publicLink) {
            if (shareItem.getSharedEntityIdentifier().getEntityType() != EntityType.folderEntry) {
                throw new IllegalArgumentException("Public links are only allowed for folder entries.");
            }
        }
    }

    private void determineExpiration(ShareItem latestShareItem) {
        int daysToExpire = latestShareItem.getDaysToExpire();
        if (daysToExpire>0) {
            long milliSecToExpire = daysToExpire * MILLISEC_IN_A_DAY;
            // Calculate the end date based on the days-to-expire.
            Date now = new Date();
            latestShareItem.setEndDate(new Date( now.getTime() + milliSecToExpire ));
        }
    }

    /**
     * Deletes a share item with access checking.
     * 
     * @param shareItemId
     */
    //NO transaction
	@Override
	public void deleteShareItem(Long shareItemId) {
		final ShareItem shareItem;
		try {
			shareItem = getProfileDao().loadShareItem(shareItemId);
		}
		catch(NoShareItemByTheIdException e) {
			// already gone, ok
			return;
		}

		//Access check (throws error if not allowed)
		checkAccess(shareItem, SharingOperation.deleteShareItem);
		
		//Now delete the shareItem
		getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
                shareItem.setDeletedDate(new Date());
				getCoreDao().update(shareItem);

				//Log this share action in the change log
				addShareItemChangeLogEntry(shareItem, ChangeLog.SHARE_DELETE);
				
				return null;
			}
		});

		//Index the entity that is being shared
		indexSharedEntity(shareItem);
	
		//See if there are any cached HTML files to be deleted
		if (shareItem.getRecipientType().equals(ShareItem.RecipientType.publicLink)) {
			DefinableEntity entity = getSharedEntity(shareItem);
			Binder binder = entity.getParentBinder();
			if (entity instanceof Binder) binder = (Binder) entity;
			Set<FileAttachment> atts = entity.getFileAttachments();
			for (FileAttachment fa : atts) {
				getConvertedFileModule().deleteCacheHtmlFile(shareItem, binder, entity, fa);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#getShareItem(java.lang.Long)
	 */
	@Override
	public ShareItem getShareItem(Long shareItemId)
			throws NoShareItemByTheIdException {
		// Access check
		// There is no access check on getting shareItems due to performance concerns. 
		// We are counting on the UI to not show items that the user is not allowed to see.
		return getProfileDao().loadShareItem(shareItemId);
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#getShareItems(java.util.Collection)
	 */
	@Override
	public List<ShareItem> getShareItems(Collection<Long> shareItemIds) {
		// Access check?
		// There is no access check on getting shareItems due to performance concerns. 
		// We are counting on the UI to not show items that the user is not allowed to see.
		return getProfileDao().loadShareItems(shareItemIds);
	}
    
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.sharing.SharingModule#getShareItems(org.kablink.teaming.dao.util.ShareItemSelectSpec)
	 */
	@Override
	public List<ShareItem> getShareItems(ShareItemSelectSpec selectSpec) {
		// Access check?
		// There is no access check on getting shareItems due to performance concerns. 
		// We are counting on the UI to not show items that the user is not allowed to see.
		
		if(selectSpec.accountForInheritance && selectSpec.sharedEntityIdentifiers != null && selectSpec.sharedEntityIdentifiers.size() > 0) {
			Collection<EntityIdentifier> orig = selectSpec.sharedEntityIdentifiers;
			HashSet<EntityIdentifier> copy = new HashSet<EntityIdentifier>(orig);
			for(EntityIdentifier entityIdentifier:orig) {
				addShareRightInheritingParents(entityIdentifier, copy);
			}
			selectSpec.sharedEntityIdentifiers = copy;
			List<ShareItem> result = getProfileDao().findShareItems(selectSpec, RequestContextHolder.getRequestContext().getZoneId());
			selectSpec.sharedEntityIdentifiers = orig; // Restore the original
			return result;
		}
		else {
			return getProfileDao().findShareItems(selectSpec, RequestContextHolder.getRequestContext().getZoneId());
		}
	}

    @Override
	public void hideSharedEntitiesForCurrentUser(Collection<EntityIdentifier> ids, boolean recipient) {
        String tagName;
        if (recipient) {
            tagName = ObjectKeys.HIDDEN_SHARED_WITH_TAG;
        } else {
            tagName = ObjectKeys.HIDDEN_SHARED_BY_TAG;
        }
        FolderModule folderModule = getFolderModule();
        BinderModule binderModule = getBinderModule();
        for (EntityIdentifier id : ids) {
            if (id.getEntityType().equals(EntityType.folderEntry)) {
                folderModule.setTag(null, id.getEntityId(), tagName, false);
            } else {
                binderModule.setTag(id.getEntityId(), tagName, false);
            }
        }
        updateUserHiddenShareModTime(recipient);
    }

    @Override
    public void unhideSharedEntitiesForCurrentUser(Collection<EntityIdentifier> ids, boolean recipient) {
        String tagName;
        if (recipient) {
            tagName = ObjectKeys.HIDDEN_SHARED_WITH_TAG;
        } else {
            tagName = ObjectKeys.HIDDEN_SHARED_BY_TAG;
        }
        FolderModule folderModule = getFolderModule();
        BinderModule binderModule = getBinderModule();
        Collection<Tag> tags;
        for (EntityIdentifier id : ids) {
            if (id.getEntityType().equals(EntityType.folderEntry)) {
                tags = folderModule.getTags(folderModule.getEntry(null, id.getEntityId()));
                for (Tag tag : tags) {
                    if (!tag.isPublic() && tag.getName().equals(tagName)) {
                        folderModule.deleteTag(null, id.getEntityId(), tag.getId());
                    }
                }
            } else {
                tags = binderModule.getTags(binderModule.getBinder(id.getEntityId()));
                for (Tag tag : tags) {
                    if (!tag.isPublic() && tag.getName().equals(tagName)) {
                        binderModule.deleteTag(id.getEntityId(), tag.getId());
                    }
                }
            }
        }
        updateUserHiddenShareModTime(recipient);
    }

    @Override
	public Date getHiddenShareModTimeForCurrentUser(boolean recipient) {
        String tagName;
        if (recipient) {
            tagName = ObjectKeys.HIDDEN_SHARED_WITH_TAG;
        } else {
            tagName = ObjectKeys.HIDDEN_SHARED_BY_TAG;
        }
        return (Date)getProfileModule().getUserProperties(RequestContextHolder.getRequestContext().getUserId()).getProperty(tagName);
    }

    private void addShareRightInheritingParents(EntityIdentifier entityIdentifier, Set<EntityIdentifier> set) {
		DefinableEntity entity;
		
		try {
			entity = loadDefinableEntity(entityIdentifier, true);
		}
		catch(Exception e) {
			logger.warn("Error loading shared entity '" + entityIdentifier.toString() + "': " + e.toString());
			return;
		}
		
		if(entity instanceof FolderEntry) {
			FolderEntry entry = (FolderEntry) entity;
			if(!entry.hasEntryAcl() || (entry.hasEntryAcl() && entry.isIncludeFolderAcl())) {
				// This entry inherits the parent's ACLs.
				set.add(entry.getParentFolder().getEntityIdentifier());
				WorkArea workArea = entry.getParentFolder();
	        	while(workArea.isFunctionMembershipInherited()) {
	        		workArea = workArea.getParentWorkArea();
	        		if(workArea instanceof DefinableEntity)
	        			set.add(((DefinableEntity)workArea).getEntityIdentifier());
	        	}
			}
		}
		else if(entity instanceof Folder) {
	   		WorkArea workArea = (Folder) entity;
        	while(workArea.isFunctionMembershipInherited()) {
        		workArea = workArea.getParentWorkArea();
        		if(workArea instanceof DefinableEntity)
        			set.add(((DefinableEntity)workArea).getEntityIdentifier());
        	}
		}
		else {
			logger.warn("Invalid shared entity '" + entityIdentifier + "'");
		}
	}
    
	/**
	 * Returns true if a DefinableEnity is tagged as a hidden share and
	 * false otherwise.
	 * 
     * @param siEntity
     * @param recipient
     * 
     * @return
	 */
    @Override
	public boolean isSharedEntityHidden(DefinableEntity siEntity, boolean recipient) {
		// Does the entity have any personal tags defined on it?
		Map<String, SortedSet<Tag>>	tagsMap;
		if (siEntity.getEntityType().equals(EntityType.folderEntry))
		     tagsMap = TagUtil.uniqueTags(getFolderModule().getTags((FolderEntry) siEntity));
		else tagsMap = TagUtil.uniqueTags(getBinderModule().getTags((Binder)      siEntity));
		Set<Tag> personalTagsSet = ((null == tagsMap) ? null : tagsMap.get(ObjectKeys.PERSONAL_ENTITY_TAGS));
		if (MiscUtil.hasItems(personalTagsSet)) {
			// Yes!  What personal tag would be used to mark this
			// entity as being hidden?
			String hideTag;
			if (recipient)
			     hideTag = ObjectKeys.HIDDEN_SHARED_WITH_TAG;
			else hideTag = ObjectKeys.HIDDEN_SHARED_BY_TAG;
			
			// Scan the personal tags.
			for (Tag tag:  personalTagsSet) {
				// Does this tag mark the entity as being hidden?
				if (tag.getName().equals(hideTag)) {
					// Yes!  Return true.
					return true;
				}
			}
		}
		
		// If we get here, the entity is not marked as being hidden.
		// Return false.
		return false;
	}


	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.sharing.SharingModule#getSharedEntity(org.kablink.teaming.domain.ShareItem)
	 */
	@Override
	public DefinableEntity getSharedEntity(ShareItem shareItem) {
		return loadDefinableEntity(shareItem.getSharedEntityIdentifier(), true);
	}

    @Override
	public DefinableEntity getSharedEntityWithoutAccessCheck(ShareItem shareItem) {
        return loadDefinableEntity(shareItem.getSharedEntityIdentifier(), false);
    }

	private DefinableEntity loadDefinableEntity(EntityIdentifier entityIdentifier, boolean accessCheck) {
		EntityIdentifier.EntityType entityType = entityIdentifier.getEntityType();
		if(entityType == EntityIdentifier.EntityType.folderEntry) {
			return accessCheck ?
                    getFolderModule().getEntry(null, entityIdentifier.getEntityId()) :
                    getFolderModule().getEntryWithoutAccessCheck(null, entityIdentifier.getEntityId());
		}
		else if(entityType == EntityIdentifier.EntityType.folder || entityType == EntityIdentifier.EntityType.workspace) {
			return accessCheck ?
                    getBinderModule().getBinder(entityIdentifier.getEntityId()) :
                    getBinderModule().getBinderWithoutAccessCheck(entityIdentifier.getEntityId());
		}
		else {
			throw new IllegalArgumentException("Unsupported entity type '" + entityType.name() + "' for sharing");
		}
	}
	
	@Override
	public DefinableEntity getSharedRecipient(ShareItem shareItem) {
		ShareItem.RecipientType recipientType = shareItem.getRecipientType();
		if(recipientType == RecipientType.user || recipientType == RecipientType.group) {
			return getProfileModule().getEntry(shareItem.getRecipientId());
			
		} else if(recipientType == RecipientType.team) {
			return getBinderModule().getBinder(shareItem.getRecipientId());
			
		} else {
			throw new IllegalArgumentException("Unsupported recipient type '" + recipientType.name() + "' for sharing");
		}
	}

    public AdminModule getAdminModule() {
        if (adminModule == null) {
            adminModule = (AdminModule) SpringContextUtil.getBean("adminModule");
        }
        return adminModule;
    }

    public void setAdminModule(AdminModule adminModule) {
        this.adminModule = adminModule;
    }

    protected FolderModule getFolderModule() {
		if (folderModule == null) {
			folderModule = (FolderModule) SpringContextUtil.getBean("folderModule");
		}
		return folderModule;
	}

	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}

	protected BinderModule getBinderModule() {
		if (binderModule == null) {
			binderModule = (BinderModule) SpringContextUtil.getBean("binderModule");
		}
		return binderModule;
	}

	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	
	protected ProfileModule getProfileModule() {
		if (profileModule == null) {
			profileModule = (ProfileModule) SpringContextUtil.getBean("profileModule");
		}
		return profileModule;
	}
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	//Routine to re-index an entity after a change in sharing
	protected void indexSharedEntity(final ShareItem shareItem) {
		// Indexing a binder requires 'binderAdministration' right that most users do not have unless
		// they own the binder. Indexing of a share entity takes place only as side effect of some
		// changes to sharing. Appropriate access checking is performed at the higher level as user
		// attempts some changes to sharing. Consequently, we can safely run this method in admin
		// context so that this method won't throw access violation without compromising security.
		
		RunasCallback callback = new RunasCallback() {
			@Override
			public Object doAs() {
				DefinableEntity entity = getSharedEntity(shareItem);
				if (entity.getEntityType() == EntityType.folderEntry) {
					folderModule.indexEntry((FolderEntry) entity, Boolean.TRUE);
				}
				else if (entity.getEntityType() == EntityIdentifier.EntityType.folder || 
						entity.getEntityType() == EntityIdentifier.EntityType.workspace) {
					// Sharing a binder can give the recipient access not only to the binder being explicitly
					// shared but also to the sub-binders as long as those sub-binders inherit ACLs from
					// their parents. 
					Binder binder = (Binder) entity;
					loadBinderProcessor(binder).indexFunctionMembership(binder, true, Boolean.FALSE, false);
				}
				return null;
			}
		};
		
		RunasTemplate.runasAdmin(callback, RequestContextHolder.getRequestContext().getZoneName());
	}

	protected ExpiredShareHandler getExpiredShareHandler(Workspace zone) {
		String className = SPropsUtil.getString("job.expired.share.handler.class", "org.kablink.teaming.jobs.DefaultExpiredShareHandler");
		return (ExpiredShareHandler) ReflectHelper.getInstance(className);
	}
	
	//called on zone startup
	@Override
    public void startScheduledJobs(Workspace zone) {
 	   	if (zone.isDeleted()) return;
 	   	ExpiredShareHandler job = getExpiredShareHandler(zone);
    	job.schedule(zone.getId(), SPropsUtil.getInt("job.expired.share.handler.interval.minutes", 5));
	}

	//called on zone delete
	@Override
	public void stopScheduledJobs(Workspace zone) {
		ExpiredShareHandler job = getExpiredShareHandler(zone);
   		job.remove(zone.getId());
	}

	//NO transaction
	@Override
	public void handleExpiredShareItem(final ShareItem shareItem) {
		if(shareItem.isExpirationHandled())
			return; // Already handled
		
		//Re-index the entity that has been shared and now expired.
		indexSharedEntity(shareItem);		

		// Mark the share item as handled
		getTransactionTemplate().execute(new TransactionCallback<Object>() {
			@Override
			public Object doInTransaction(TransactionStatus status) {
				shareItem.setExpirationHandled(true);
				getCoreDao().update(shareItem);
				return null;
			}
		});
	}

	private BinderProcessor loadBinderProcessor(Binder binder) {
		return (BinderProcessor)getProcessorManager().getProcessor(binder, binder.getProcessorKey(BinderProcessor.PROCESSOR_KEY));
	}
	
	private void addShareItemChangeLogEntry(ShareItem shareItem, String action) {
		EntityIdentifier entityIdentifier = shareItem.getSharedEntityIdentifier();
		if (entityIdentifier.getEntityType().equals(EntityType.folderEntry)) {
			FolderEntry fe = getFolderModule().getEntry(null, entityIdentifier.getEntityId());
			FolderCoreProcessor processor = (FolderCoreProcessor)getProcessorManager().getProcessor(
					fe.getParentFolder(), fe.getParentFolder().getProcessorKey(FolderCoreProcessor.PROCESSOR_KEY));
			processor.processChangeLog(fe, action);
		} else if (entityIdentifier.getEntityType().equals(EntityType.folder) ||
				entityIdentifier.getEntityType().equals(EntityType.workspace)) {
			Binder binder = getBinderModule().getBinder(entityIdentifier.getEntityId());
			BinderProcessor processor = loadBinderProcessor(binder);
			processor.processChangeLog(binder, action);
		}
	}

    private void updateUserHiddenShareModTime(boolean recipient) {
        String tagName;
        if (recipient) {
            tagName = ObjectKeys.HIDDEN_SHARED_WITH_TAG;
        } else {
            tagName = ObjectKeys.HIDDEN_SHARED_BY_TAG;
        }
        getProfileModule().setUserProperty(RequestContextHolder.getRequestContext().getUserId(), tagName, new Date());
    }

    private static class ShareOp {
        WorkAreaOperation workAreaOperation;
        FolderOperation folderOperation;
        BinderOperation binderOperation;

        private ShareOp(WorkAreaOperation workAreaOperation, FolderOperation folderOperation, BinderOperation binderOperation) {
            this.workAreaOperation = workAreaOperation;
            this.folderOperation = folderOperation;
            this.binderOperation = binderOperation;
        }
    }
    
    /**
     * Returns true if the given email address is valid for sharing
     * with based on the current sharing blacklist/whitelist.
     * 
     * @param ema
     * 
     * @return
     */
    @Override
    public boolean isExternalAddressValid(String ema, ShareLists shareLists) {
    	return getExternalAddressStatus(ema, shareLists).isValid();
    }
    
    @Override
    public boolean isExternalAddressValid(String ema) {
    	// Always use the initial form of the method.
    	return isExternalAddressValid(ema, getShareLists());
    }
    
    /**
     * Returns an ExternalAddressStatus value for the status of sharing
     * with the given email address based on the current sharing
     * blacklist/whitelist.
     * 
     * @param ema
     * 
     * @return
     */
    @Override
    public ExternalAddressStatus getExternalAddressStatus(String ema, ShareLists shareLists) {
    	// Do we have a sharing blacklist/whitelist with list
    	// validation enabled?
        if ((null == shareLists) || shareLists.isDisable()) {
        	// No!  Then the address is considered valid.
        	return ExternalAddressStatus.valid;
        }
        
    	// Do we have any email addresses to validate against?
    	boolean      isWhitelist = shareLists.isWhitelist();
    	List<String> list = shareLists.getEmailAddresses();
    	if (MiscUtil.hasItems(list)) {
    		// Yes!  Scan them.
	    	for (String emaScan:  list) {
        		// Does the email address we were given match this one?
	    		if (emaScan.equalsIgnoreCase(ema)) {
	    			// Yes!  If we're validating a whitelist, the email
	    			// address is valid.  Otherwise, it failed the
	    			// blacklist check.
        			if (isWhitelist)
       			         return ExternalAddressStatus.valid;
        			else return ExternalAddressStatus.failsBlacklistEMA;
	    		}
	    	}
    	}
    	
    	// Do we have any domains to validate against?
    	list = shareLists.getDomains();
    	if (MiscUtil.hasItems(list)) {
    		// Yes!  Extract the domain from the email address we were
    		// given.
        	int    atPos  = ema.indexOf('@');
       		String domain = ema.substring(atPos + 1);
        	
        	// Scan the domains.
        	for (String domainScan:  list) {
        		// Does the email address contain this domain?
        		if (domainScan.equalsIgnoreCase(domain)) {
        			// Yes!  If we're validating a whitelist, the email
        			// address is valid.  Otherwise, it failed the
        			// blacklist check.
        			if (isWhitelist)
        			     return ExternalAddressStatus.valid;
        			else return ExternalAddressStatus.failsBlacklistDomain;
        		}
        	}
    	}
    	
    	// If we get here and are doing a whitelist validation, the
    	// email address is invalid because we didn't match it above.
    	// Otherwise, it's valid because it didn't fail the blacklist
    	// validation above.
    	if (isWhitelist)
             return ExternalAddressStatus.failsWhitelist;
    	else return ExternalAddressStatus.valid;
    }
    
    @Override
    public ExternalAddressStatus getExternalAddressStatus(String ema) {
        // Always use the initial form of the method.
        return getExternalAddressStatus(ema, getShareLists());
    }
    
    /**
     * Returns the ShareLists object stored in the ZoneConfig.
     * 
     * @return
     */
    @Override
	public ShareLists getShareLists() {
        Long		zoneId    = RequestContextHolder.getRequestContext().getZoneId();
        ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(zoneId);
        ShareLists shareLists = zoneConfig.getShareLists();
        if (null == shareLists) {
        	shareLists = new ShareLists();
        }
        return shareLists;
    }

    /**
     * Stores/updates a ShareLists object in the ZoneConfig.
     * 
     * @param shareLists
     */
    @Override
	public void setShareLists(ShareLists shareLists) {
   		Binder top = RequestContextHolder.getRequestContext().getZone();
   		getBinderModule().checkAccess(top, BinderOperation.manageConfiguration);
		
  		ZoneConfig zoneConfig = getCoreDao().loadZoneConfig(RequestContextHolder.getRequestContext().getZoneId());
  		zoneConfig.setShareLists(shareLists);
    }
    
    /**
     * Validates that the ShareItem's in a List<ShareItem> refer to a
     * valid definable entity.  If they don't, they are deleted from
     * the SS_ShareItem table and removed from the list.
     * 
     * Will Optionally:
     * 1) Populate a List<DefinableEntity> of the valid shared
     *    entities.
     * 2) Validate the user has access to the shared entities
     *    that the List<DefinableEntity> is populated with.
     * 
     * @param shares
     * @param sharedEntities (optional)
     */
    @Override
    public void validateShareItems(List<ShareItem> shares) {
    	// Always use the implementation form of the method.
    	validateShareItemsImpl(shares, null, true);
    }
    @Override
    public void validateShareItems(List<ShareItem> shares, List<DefinableEntity> sharedEntities) {
    	// Always use the implementation form of the method.
    	validateShareItemsImpl(shares, sharedEntities, true);
    }
    @Override
    public void validateShareItemsWithoutAccessCheck(List<ShareItem> shares, List<DefinableEntity> sharedEntities) {
    	// Always use the implementation form of the method.
    	validateShareItemsImpl(shares, sharedEntities, false);
    }
    
    /*
     * Implements the various validateShareItems() methods.
     */
    private void validateShareItemsImpl(List<ShareItem> shares, List<DefinableEntity> sharedEntities, boolean doCheckAccess) {
    	// If we weren't given any shares...
    	int c = ((null == shares) ? 0 : shares.size());
    	if (0 == c) {
    		// ...there's nothing to validate.
    		return;
    	}

    	// Collect the shared entity IDs from the shares.
    	List<Long> binderIds = new ArrayList<Long>();
    	List<Long> entryIds  = new ArrayList<Long>();
    	for (ShareItem share:  shares) {
    		EntityIdentifier eid = share.getSharedEntityIdentifier();
    		Long             id  = eid.getEntityId();
    		if (eid.getEntityType().equals(EntityType.folderEntry))
    		     ListUtil.addLongToListLongIfUnique(entryIds,  id);
    		else ListUtil.addLongToListLongIfUnique(binderIds, id);
    	}

    	// Access the referenced Binder's WITHOUT access checks...
    	Set<Binder> binderSet;
    	if (binderIds.isEmpty())
    	     binderSet = null;
    	else binderSet = getBinderModule().getBinders(binderIds, false);
    	if (null == binderSet) {
    		binderSet = new HashSet<Binder>();
    	}
    	
    	// ...and FolderEntry's WIHTOUT access checks.
    	Set<FolderEntry> entrySet;
    	if (entryIds.isEmpty())
    	     entrySet = null;
    	else entrySet = getFolderModule().getEntries(entryIds, false);
    	if (null == entrySet) {
    		entrySet = new HashSet<FolderEntry>();
    	}

    	// Scan the shares again.
    	CoreDao cd = getCoreDao();
    	User user = RequestContextHolder.getRequestContext().getUser();
    	for (int i = (c - 1); i >= 0; i -= 1) {
    		// Were we able to find this share's DefinableEntity?
    		ShareItem        share = shares.get(i);
    		EntityIdentifier eid   = share.getSharedEntityIdentifier();
    		EntityType       eit   = eid.getEntityType();
    		Long             id    = eid.getEntityId();
    		DefinableEntity  de;
    		if (eit.equals(EntityType.folderEntry))
    		     de = findEntryById( entrySet,  id);
    		else de = findBinderById(binderSet, id);
    		if (null == de) {
    			// No!  Then we consider it invalid.  Remove it from
    			// the share list and database.
				logger.error("SharingModuleImpl.validateShareItemsImpl():  The " + eit.name() + " (id:" + id + ") referenced by ShareItem (id:" + share.getId() + ") is missing.  The ShareItem is being deleted.");
    			shares.remove(i);
    			cd.purgeShares(share);
    		}

    		else if (null != sharedEntities) {
        		// Yes, it's valid and the caller wants it returned!
    			// Are we tracking it yet?
            	if (null == findSharedEntityInList(sharedEntities, eid)) {
        			// No!  Access check it if requested and add it to
        			// the shared entity list.
	                try {
	                	if (doCheckAccess) {
	                		AccessUtils.readCheck(user, de);
	                	}
	               		sharedEntities.add(de);
	                } catch (Exception ignoreMe) {};
            	}
    		}
    	}
    }
    
    /*
     * Scans a Set<Binder> for a Binder with the given ID.  If one is
     * found, it's returned.  Otherwise, null is returned.
     */
    private static Binder findBinderById(Set<Binder> binderSet, Long id) {
    	if ((null != binderSet) && (null != id)) {
	    	for (Binder binder:  binderSet) {
	    		if (binder.getId().equals(id)) {
	    			return binder;
	    		}
	    	}
    	}
    	return null;
    }
    
    /*
     * Scans a Set<FolderEntry> for a FolderEntry with the given ID.
     * If one is found, it's returned.  Otherwise, null is returned.
     */
    private static FolderEntry findEntryById(Set<FolderEntry> entrySet, Long id) {
    	if ((null != entrySet) && (null != id)) {
	    	for (FolderEntry fe:  entrySet) {
	    		if (fe.getId().equals(id)) {
	    			return fe;
	    		}
	    	}
    	}
    	return null;
    }
    
    /**
     * Scans a List<DefinableEntity> for the one matching an
     * EntityIdentifier.  If found, it's returned.  Otherwise, null is
     * returned.
     * 
     * @param sharedEntities
     * @param eid
     * 
     * @return
     */
    @Override
    public DefinableEntity findSharedEntityInList(List<DefinableEntity> sharedEntities, EntityIdentifier eid) {
    	// Do we have list and an identifier to search for?
    	if ((null != sharedEntities) && (null != eid)) {
    		// Yes!  Scan the entities.
    		for (DefinableEntity sharedEntity:  sharedEntities) {
    			// Is this the entity in question?
    			if (sharedEntity.getEntityType().equals(eid.getEntityType()) && sharedEntity.getId().equals(eid.getEntityId())) {
    				// Yes!  Return it.
    				return sharedEntity;
    			}
    		}
    	}
    	
    	// If we get here, we couldn't find the entity request.  Return
    	// null.
		return null;
    }
}
