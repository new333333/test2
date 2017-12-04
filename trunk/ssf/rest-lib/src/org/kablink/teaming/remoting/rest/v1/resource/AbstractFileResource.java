package org.kablink.teaming.remoting.rest.v1.resource;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.kablink.teaming.UserQuotaException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.fi.FileNotFoundException;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.ConflictException;
import org.kablink.teaming.remoting.rest.v1.exc.InternalServerErrorException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.exc.RestExceptionWrapper;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FileVersionProperties;
import org.kablink.teaming.rest.v1.model.UserQuota;
import org.kablink.teaming.runas.RunasCallback;
import org.kablink.teaming.runas.RunasTemplate;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.util.Validator;
import org.kablink.util.api.ApiErrorCode;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * User: david
 * Date: 5/22/12
 * Time: 10:39 AM
 */
abstract public class AbstractFileResource extends AbstractResource {
    protected static enum FileType {
        main,
        thumbnail,
        scaled
    }

    protected Date dateFromISO8601(String modDateISO8601) {
        if (Validator.isNotNull(modDateISO8601)) {
            DateTime dateTime = ISODateTimeFormat.dateTimeParser().parseDateTime(modDateISO8601);
            return dateTime.toDate();
        } else {
            return null;
        }
    }

    protected FileProperties createEntryWithAttachment(Folder folder, String fileName, String modDateISO8601, String expectedMd5, boolean replaceExisting, InputStream is) throws WriteFilesException, WriteEntryDataException {
        if(!folder.isLibrary()) {
            throw new NotFoundException(ApiErrorCode.NOT_SUPPORTED, "This folder is not a library folder.");
        }
        if (fileName==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing file_name query parameter");
        }
        fileName = Normalizer.normalize(fileName, Normalizer.Form.NFC);

        org.kablink.teaming.domain.FolderEntry entry = getLibraryFolderEntryByName(folder, fileName);

        try {
            if(entry != null) {
                if (!replaceExisting) {
                    throw new ConflictException(ApiErrorCode.FILE_EXISTS, "A file with the name already exists.", ResourceUtil.buildFileProperties(entry.getPrimaryFileAttachment()));
                }
                // An entry containing a file with this name exists.
                if(logger.isDebugEnabled())
                    logger.debug("createNew: updating existing file '" + fileName + "' + owned by " + entry.getEntityIdentifier().toString() + " in folder " + folder.getId());
                FolderUtils.modifyLibraryEntry(entry, fileName, null, is, null, dateFromISO8601(modDateISO8601), expectedMd5, true, null, null);
            }
            else {
                // We need to create a new entry
                if(logger.isDebugEnabled())
                    logger.debug("createNew: creating new file '" + fileName + "' + in folder " + folder.getId());
                entry = FolderUtils.createLibraryEntry(folder, fileName, is, dateFromISO8601(modDateISO8601), expectedMd5, true);
            }
        } catch (UserQuotaException e) {
            throw new RestExceptionWrapper(e, e, e, new UserQuota(e.getDiskSpaceQuota(), e.getDiskSpaceUsed()));
        } catch (WriteFilesException e) {
            if (e.getApiErrorCode()==ApiErrorCode.USER_QUOTA_EXCEEDED) {
                Exception rootException = e.getErrors().getProblems().get(0).getException();
                if (rootException instanceof UserQuotaException) {
                    throw new RestExceptionWrapper(e, e, e,
                            new UserQuota(((UserQuotaException)rootException).getDiskSpaceQuota(),
                                    ((UserQuotaException)rootException).getDiskSpaceUsed()));
                }
            }
            throw e;
        }
        return ResourceUtil.buildFileProperties(findFileAttachmentByName(entry, fileName));
    }

    protected FileProperties writeNewFileContent(
            EntityIdentifier.EntityType entityType,
            long entityId,
            String filename,
            String dataName,
            String modDateISO8601,
            String expectedMd5,
            InputStream is) throws WriteFilesException, WriteEntryDataException {
        if (filename==null || filename.length()==0) {
            throw new BadRequestException(ApiErrorCode.INVALID_ENTITY_TYPE, "The file_name query parameter must be specified.");
        }
        filename = Normalizer.normalize(filename, Normalizer.Form.NFC);
        Date modDate = dateFromISO8601(modDateISO8601);
        DefinableEntity entity = findDefinableEntity(entityType, entityId);
        FileAttachment fa = findFileAttachmentByName(entity, filename);
        if (fa != null) {
            throw new ConflictException(ApiErrorCode.FILE_EXISTS, "A file named " + filename + " already exists in the " + entityType + ".", ResourceUtil.buildFileProperties(fa));
        }
        modifyDefinableEntityWithFile(entity, dataName, filename, is, modDate, expectedMd5);
        fa = entity.getFileAttachment(filename);
        return ResourceUtil.buildFileProperties(fa);
    }

    protected FileProperties updateExistingFileContent(
            DefinableEntity entity,
            FileAttachment attachment,
            String dataName,
            String modDateISO8601,
            String expectedMd5,
            boolean forceOverwrite,
            Integer lastVersionNumber,
            Integer lastMajorVersionNumber,
            Integer lastMinorVersionNumber,
            InputStream is)
            throws WriteFilesException, WriteEntryDataException {
        Date modDate = dateFromISO8601(modDateISO8601);
        boolean allowOverwrite = forceOverwrite;
        if (!allowOverwrite) {
            if (lastVersionNumber==null && (lastMajorVersionNumber==null || lastMinorVersionNumber==null)) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "You must specify one of: lastVersionNumber, lastMajorVersionNumber and lastMinorVersionNumber, or forceOverwrite.");
            }
        }
        if (forceOverwrite || isFileVersionCorrect(attachment, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber)) {
            modifyDefinableEntityWithFile(entity, dataName, attachment.getFileItem().getName(), is, modDate, expectedMd5);
        } else {
            throw new ConflictException(ApiErrorCode.FILE_VERSION_CONFLICT, "Specified version number does not reflect the current state of the file",
                    ResourceUtil.buildFileProperties(attachment));
        }
        return ResourceUtil.buildFileProperties(attachment);
    }

    protected FileProperties writeFileContentByName(
            EntityIdentifier.EntityType entityType,
            long entityId,
            String filename,
            String dataName,
            String modDateISO8601,
            String expectedMd5,
            Boolean update,
            Integer lastVersionNumber,
            Integer lastMajorVersionNumber,
            Integer lastMinorVersionNumber,
            InputStream is)
            throws WriteFilesException, WriteEntryDataException {
        Date modDate = dateFromISO8601(modDateISO8601);
        DefinableEntity entity = findDefinableEntity(entityType, entityId);
        filename = Normalizer.normalize(filename, Normalizer.Form.NFC);
        FileAttachment fa = findFileAttachmentByName(entity, filename);
        if (fa != null) {
            if (update!=null && !update) {
                throw new ConflictException(ApiErrorCode.FILE_EXISTS, "A file named " + filename + " already exists in the " + entityType.name() + ".", ResourceUtil.buildFileProperties(fa));
            }
            if (isFileVersionCorrect(fa, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber)) {
                modifyDefinableEntityWithFile(entity, dataName, filename, is, modDate, expectedMd5);
            } else {
                throw new ConflictException(ApiErrorCode.FILE_VERSION_CONFLICT, "Specified version number does not reflect the current state of the file",
                        ResourceUtil.buildFileProperties(fa));
            }
        } else {
            if (update!=null && update) {
                throw new NoFileByTheNameException(filename);
            }
            validateArgumentsForNewFile(lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
            modifyDefinableEntityWithFile(entity, dataName, filename, is, modDate, expectedMd5);
            fa = entity.getFileAttachment(filename);
        }
        return ResourceUtil.buildFileProperties(fa);
    }

    protected void validateArgumentsForNewFile(Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber) {
        // These arguments should not be specified when creating a new file.
        if (lastVersionNumber == null && lastMajorVersionNumber == null && lastMinorVersionNumber == null)
            return;
        throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Cannot specify lastVersionNumber, lastMajorVersionNumber or lastMinorVersionNumber for a new file.");
    }

    protected EntityIdentifier.EntityType entityTypeFromString(String entityTypeStr)
            throws BadRequestException {
        try {
            return EntityIdentifier.EntityType.valueOf(entityTypeStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(ApiErrorCode.INVALID_ENTITY_TYPE, "Entity type '" + entityTypeStr + "' is invalid");
        }
    }

    protected FileAttachment getFileAttachment(String fileId)
            throws NoFileByTheIdException {
        FileAttachment fa = getFileModule().getFileAttachmentById(fileId);
        if (fa == null)
            throw new NoFileByTheIdException(fileId);
        else if (fa instanceof VersionAttachment)
            throw new NoFileByTheIdException(fileId, "The specified file ID represents a file version rather than a file");
        else {
            DefinableEntity entity = fa.getOwner().getEntity();
            if (entity instanceof FolderEntry) {
                if (_isPreDeleted((FolderEntry)entity)) {
                    throw new NoFileByTheIdException(fileId);
                }
            } else if (entity instanceof Binder) {
                if (_isPreDeleted((Binder)entity)) {
                    throw new NoFileByTheIdException(fileId);
                }
            }
            return fa;
        }
    }

    protected FileAttachment getFileAttachment(DefinableEntity entity, String filename)
            throws NotFoundException {
        FileAttachment fa = findFileAttachmentByName(entity, filename);
        if (fa != null)
            return fa;
        else
            throw new NotFoundException(ApiErrorCode.FILE_NOT_FOUND, "File '" + filename + "' is not found for entity '" + entity.getId() + "' of type '" + entity.getEntityType().name() + "'");
    }

    protected Response readFileContent(String entityType, long entityId, String filename, Date ifModifiedSinceDate) {
        return readFileContent(entityType, entityId, filename, ifModifiedSinceDate, FileType.main);
    }

    protected Response readFileContent(String entityType, long entityId, String filename, Date ifModifiedSinceDate, FileType type)
            throws BadRequestException {
        EntityIdentifier.EntityType et = entityTypeFromString(entityType);
        final Binder binder;
        final DefinableEntity entity;
        FileAttachment fa;
        if (et == EntityIdentifier.EntityType.folderEntry) {
            entity = getFolderModule().getEntry(null, entityId);
            binder = entity.getParentBinder();
        } else if (et == EntityIdentifier.EntityType.user) {
            entity = getProfileModule().getEntry(entityId);
            if (!(entity instanceof User))
                throw new BadRequestException(ApiErrorCode.NOT_USER, "Entity ID '" + entityId + "' does not represent a user");
            binder = entity.getParentBinder();
        } else if (et == EntityIdentifier.EntityType.group) {
            entity = getProfileModule().getEntry(entityId);
            if (!(entity instanceof Group))
                throw new BadRequestException(ApiErrorCode.NOT_GROUP, "Entity ID '" + entityId + "' does not represent a group");
            binder = entity.getParentBinder();
        } else if (et == EntityIdentifier.EntityType.workspace || et == EntityIdentifier.EntityType.folder || et == EntityIdentifier.EntityType.profiles) {
            entity = getBinderModule().getBinder(entityId);
            binder = (Binder) entity;
        } else {
            throw new BadRequestException(ApiErrorCode.INVALID_ENTITY_TYPE, "Entity type '" + entityType + "' is unknown or not supported by this method");
        }
        fa = getFileAttachment(entity, filename);
        Date lastModDate = fa.getModification().getDate();
        if (ifModifiedSinceDate != null) {
            // If-Modified-Since header was specified (i.e., conditional GET)
            if (!ifModifiedSinceDate.before(lastModDate)) {
                // The file has not been modified since the specified date.
                return Response.status(Response.Status.NOT_MODIFIED).build();
            }
        }
        String mt;
        InputStream is;
        if (type==FileType.thumbnail) {
            is = getConvertedFileModule().getThumbnailInputStream(binder, entity, fa);
            mt = "image/jpeg";
        } else if (type==FileType.scaled) {
            is = getConvertedFileModule().getScaledInputStream(binder, entity, fa);
            mt = "image/jpeg";
        } else {
            try {
                is = getFileModule().readFile(binder, entity, fa);
            } catch (FileNotFoundException e) {
                if (et == EntityIdentifier.EntityType.folderEntry && binder.isMirrored()) {
                    try {
                        RunasCallback callback = new RunasCallback()
                        {
                            @Override
                            public Object doAs()
                            {
                                getFolderModule().deleteEntry(binder.getId(), entity.getId());
                                return null;
                            }
                        };

                        RunasTemplate.runasAdmin(
                                callback,
                                RequestContextHolder.getRequestContext().getZoneName());
                        
                        // If still here, the metadata object cleanup was successful
                        logger.info("Successfully purged orphaned folder entry '" + entity.getId() + "' for file '" + filename + "'");
                    } catch (AccessControlException e1) {
                    } catch (RuntimeException e2) {
                    	// Under heavy concurrent load, it's possible for the previous delete/cleanup attempt to fail
                    	// because the entry object loaded from the database at the beginning of this method is no 
                    	// longer found in the database (that is, it was already deleted by another client a moment
                    	// ago). In such case, the underlying data access layer typically throws 
                    	// HibernateOptimisticLockingFailureException. However, for better reliability and generality,
                    	// we will explicitly check if the entry object is indeed not in the database, rather than
                    	// relying on the data access layer implementation specific exception.
                    	getCoreDao().evict(entity);
                    	try {
                    		getFolderModule().getEntry(null, entityId);
                    		// If still here, it means that the entry object is still in the database, which in turn
                    		// indicates that the attempt to purge the orphaned entry from the database failed for a 
                    		// reason other than non-existing entry. Pass it up.
                    		logger.warn("Failed to purge orphaned file entry '" + entityId + "' for file '" + filename + "'");
                    		throw e2;
                    	}
                    	catch(NoFolderEntryByTheIdException e3) {
                    		// The entry object is no longer in the database, most likely because some other request
                    		// deleted it since the last time we loaded it.
                    		logger.info("The orphaned file entry '" + entityId + "' is no longer found for file '" + filename + "'");
                    	}
                    }
                }
                throw e;
            }
            mt = new MimetypesFileTypeMap().getContentType(filename);
        }
        return Response.ok(is, mt).lastModified(lastModDate).build();
    }

    protected void deleteFile(EntityIdentifier.EntityType entityType, long entityId, String filename)
            throws WriteFilesException, WriteEntryDataException, BadRequestException {
        DefinableEntity entity = findDefinableEntity(entityType, entityId);
        FileAttachment fa = findFileAttachmentByName(entity, filename);
        if (fa==null) {
            throw new NoFileByTheNameException(filename);
        }
        if (entity instanceof FolderEntry) {
            FolderUtils.deleteFileInFolderEntry(this, (FolderEntry) entity, fa);
        } else if (entity instanceof Principal) {
            List deletes = new ArrayList();
            deletes.add(fa.getId());
            getProfileModule().modifyEntry(entityId, new EmptyInputData(), null, deletes, null, null);
        } else if (entity instanceof Binder) {
            List deletes = new ArrayList();
            deletes.add(fa.getId());
            getBinderModule().modifyBinder(entityId, new EmptyInputData(), null, deletes, null);
        } else {
            throw new BadRequestException(ApiErrorCode.INVALID_ENTITY_TYPE, "Entity type '" + entity.getClass().getName() + "' is unknown or not supported by this method");
        }
    }

    protected void modifyDefinableEntityWithFile(DefinableEntity entity, String dataName, String filename, InputStream is, Date modDate, String expectedMd5) throws WriteFilesException, WriteEntryDataException {
        try {
            if (entity instanceof FolderEntry) {
                FileUtils.modifyFolderEntryWithFile((FolderEntry) entity, dataName, filename, is, modDate, expectedMd5);
            } else if (entity instanceof Principal) {
                FileUtils.modifyPrincipalWithFile((Principal) entity, dataName, filename, is, modDate, expectedMd5);
            } else if (entity instanceof Binder) {
                FileUtils.modifyBinderWithFile((Binder) entity, dataName, filename, is);
            } else {
                throw new InternalServerErrorException(ApiErrorCode.SERVER_ERROR, "Don't know how to save file in entity of type: " + entity.getClass().getName());
            }
        } catch (UserQuotaException e) {
            throw new RestExceptionWrapper(e, e, e, new UserQuota(e.getDiskSpaceQuota(), e.getDiskSpaceUsed()));
        } catch (WriteFilesException e) {
            if (e.getApiErrorCode()==ApiErrorCode.USER_QUOTA_EXCEEDED) {
                Exception rootException = e.getErrors().getProblems().get(0).getException();
                if (rootException instanceof UserQuotaException) {
                    throw new RestExceptionWrapper(e, e, e,
                            new UserQuota(((UserQuotaException)rootException).getDiskSpaceQuota(),
                                    ((UserQuotaException)rootException).getDiskSpaceUsed()));
                }
            }
            throw e;
        }
    }

    protected List<FileVersionProperties> fileVersionsFromFileAttachment(FileAttachment fa) {
        Set<VersionAttachment> vas = fa.getFileVersions();
        List<FileVersionProperties> list = new ArrayList<FileVersionProperties>(vas.size());
        for (VersionAttachment va : vas) {
            list.add(ResourceUtil.fileVersionFromFileAttachment(va));
        }
        return list;
    }

    protected org.kablink.teaming.domain.FolderEntry synchronizeFolderEntry(final org.kablink.teaming.domain.FolderEntry entry, boolean mirroredOnly) {
        org.kablink.teaming.domain.FolderEntry retEntry;
        Folder folder = entry.getParentFolder();
        if (folder.isMirrored() || !mirroredOnly) {
            RunasCallback callback = new RunasCallback()
            {
                @Override
                public org.kablink.teaming.domain.FolderEntry doAs()
                {
                    return getFolderModule().refreshFromRepository(entry);
            }
            };

            retEntry = (FolderEntry) RunasTemplate.runasAdmin(
                    callback,
                    RequestContextHolder.getRequestContext().getZoneName());
        } else {
            retEntry = _getFolderEntry(entry.getId());
        }
        return retEntry;
    }

    protected org.kablink.teaming.domain.FolderEntry _getFolderEntry(long id) {
        org.kablink.teaming.domain.FolderEntry hEntry = getFolderModule().getEntry(null, id);
        if (_isPreDeleted(hEntry)) {
            throw new NoFolderEntryByTheIdException(id);
        }
        return hEntry;
    }

    protected boolean isFileVersionCorrect(FileAttachment attachment, Integer lastVersionNumber, Integer lastMajorVersionNumber, Integer lastMinorVersionNumber) {
        AnyOwner owner = attachment.getOwner();
        if (lastVersionNumber!=null && owner.getEntity() instanceof FolderEntry) {
            FolderEntry entry = synchronizeFolderEntry((FolderEntry) owner.getEntity(), true);
            attachment = entry.getPrimaryFileAttachment();
        }
        return FileUtils.matchesTopMostVersion(attachment, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber);
    }

}
