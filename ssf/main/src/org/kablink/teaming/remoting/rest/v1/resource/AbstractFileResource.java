package org.kablink.teaming.remoting.rest.v1.resource;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.EmptyInputData;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.ConflictException;
import org.kablink.teaming.remoting.rest.v1.exc.InternalServerErrorException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FileVersionProperties;
import org.kablink.util.HttpHeaders;
import org.kablink.util.Validator;
import org.kablink.util.api.ApiErrorCode;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.io.InputStream;
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

        org.kablink.teaming.domain.FolderEntry entry = getFolderModule().getLibraryFolderEntryByFileName(folder, fileName);

        if(entry != null) {
            if (!replaceExisting) {
                throw new ConflictException(ApiErrorCode.FILE_EXISTS, "A file with the name already exists.");
            }
            // An entry containing a file with this name exists.
            if(logger.isDebugEnabled())
                logger.debug("createNew: updating existing file '" + fileName + "' + owned by " + entry.getEntityIdentifier().toString() + " in folder " + folder.getId());
            FolderUtils.modifyLibraryEntry(entry, fileName, is, null, dateFromISO8601(modDateISO8601), expectedMd5, true, null, null);
        }
        else {
            // We need to create a new entry
            if(logger.isDebugEnabled())
                logger.debug("createNew: creating new file '" + fileName + "' + in folder " + folder.getId());
            entry = FolderUtils.createLibraryEntry(folder, fileName, is, dateFromISO8601(modDateISO8601), expectedMd5, true);
        }
        return ResourceUtil.buildFileProperties(entry.getFileAttachment(fileName));
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
        Date modDate = dateFromISO8601(modDateISO8601);
        DefinableEntity entity = findDefinableEntity(entityType, entityId);
        FileAttachment fa = entity.getFileAttachment(filename);
        if (fa!=null) {
            throw new ConflictException(ApiErrorCode.FILE_EXISTS, "A file named " + filename + " already exists in the " + entityType + ".");
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
        if (forceOverwrite || FileUtils.matchesTopMostVersion(attachment, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber)) {
            modifyDefinableEntityWithFile(entity, dataName, attachment.getFileItem().getName(), is, modDate, expectedMd5);
        } else {
            throw new ConflictException(ApiErrorCode.FILE_VERSION_CONFLICT, "Specified version number does not reflect the current state of the file");
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
        FileAttachment fa = entity.getFileAttachment(filename);
        if (fa != null) {
            if (update!=null && !update) {
                throw new ConflictException(ApiErrorCode.FILE_EXISTS, "A file named " + filename + " already exists in the " + entityType.name() + ".");
            }
            if (FileUtils.matchesTopMostVersion(fa, lastVersionNumber, lastMajorVersionNumber, lastMinorVersionNumber)) {
                modifyDefinableEntityWithFile(entity, dataName, filename, is, modDate, expectedMd5);
            } else {
                throw new ConflictException(ApiErrorCode.FILE_VERSION_CONFLICT, "Specified version number does not reflect the current state of the file");
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

    protected FileAttachment findFileAttachment(String fileId)
            throws NoFileByTheIdException {
        FileAttachment fa = getFileModule().getFileAttachmentById(fileId);
        if (fa == null)
            throw new NoFileByTheIdException(fileId);
        else if (fa instanceof VersionAttachment)
            throw new NoFileByTheIdException(fileId, "The specified file ID represents a file version rather than a file");
        else {
            DefinableEntity entity = fa.getOwner().getEntity();
            if (entity instanceof FolderEntry) {
                if (((FolderEntry)entity).isPreDeleted()) {
                    throw new NoFileByTheIdException(fileId);
                }
            } else if (entity instanceof Binder) {
                if (entity instanceof Folder && ((Folder)entity).isPreDeleted()) {
                    throw new NoFileByTheIdException(fileId);
                }
                if (entity instanceof Workspace && ((Workspace)entity).isPreDeleted()) {
                    throw new NoFileByTheIdException(fileId);
                }
            }
            return fa;
        }
    }

    protected FileAttachment getFileAttachment(DefinableEntity entity, String filename)
            throws NotFoundException {
        FileAttachment fa = entity.getFileAttachment(filename);
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
        Binder binder;
        DefinableEntity entity;
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
            is = getFileModule().readFile(binder, entity, fa);
            mt = new MimetypesFileTypeMap().getContentType(filename);
        }
        return Response.ok(is, mt).lastModified(lastModDate).build();
    }

    protected void deleteFile(EntityIdentifier.EntityType entityType, long entityId, String filename)
            throws WriteFilesException, WriteEntryDataException, BadRequestException {
        DefinableEntity entity = findDefinableEntity(entityType, entityId);
        FileAttachment fa = entity.getFileAttachment(filename);
        if (fa==null) {
            throw new NoFileByTheNameException(filename);
        }
        if (entity instanceof FolderEntry) {
            FolderUtils.deleteFileInFolderEntry((FolderEntry) entity, fa);
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
        if (entity instanceof FolderEntry) {
            FileUtils.modifyFolderEntryWithFile((FolderEntry) entity, dataName, filename, is, modDate, expectedMd5);
        } else if (entity instanceof Principal) {
            FileUtils.modifyPrincipalWithFile((Principal) entity, dataName, filename, is, modDate, expectedMd5);
        } else if (entity instanceof Binder) {
            FileUtils.modifyBinderWithFile((Binder) entity, dataName, filename, is);
        } else {
            throw new InternalServerErrorException(ApiErrorCode.SERVER_ERROR, "Don't know how to save file in entity of type: " + entity.getClass().getName());
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
}
