drop database sitescape;
create database sitescape;
go
use sitescape;
create table SS_Attachments (id char(32) not null, type char(1) not null, lockVersion numeric(19,0) not null, folderEntry numeric(19,0) null, principal numeric(19,0) null, binder numeric(19,0) null, ownerType varchar(16) null, ownerId numeric(19,0) null, owningBinderKey varchar(255) null, owningBinderId numeric(19,0) null, name varchar(64) null, creation_date datetime null, creation_principal numeric(19,0) null, modification_date datetime null, modification_principal numeric(19,0) null, fileName nvarchar(255) null, fileLength numeric(19,0) null, lastVersion int null, repositoryName varchar(128) null, filelock_id varchar(128) null, filelock_subject nvarchar(192) null, filelock_owner numeric(19,0) null, filelock_expirationDate datetime null, filelock_ownerInfo nvarchar(256) null, filelock_dirty tinyint null, versionNumber int null, versionName nvarchar(256) null, parentAttachment char(32) null, primary key (id));
create table SS_AuditTrail (id char(32) not null, type char(1) not null, entityId numeric(19,0) null, entityType varchar(16) null, owningBinderId numeric(19,0) null, owningBinderKey varchar(255) null, startBy numeric(19,0) null, startDate datetime null, endBy numeric(19,0) null, endDate datetime null, description nvarchar(512) null, transactionType varchar(16) null, fileId char(32) null, tokenId numeric(19,0) null, state nvarchar(64) null, threadName nvarchar(64) null, definitionId varchar(32) null, ended tinyint null, primary key (id));
create table SS_ChangeLogs (id char(32) not null, zoneId numeric(19,0) null, docNumber varchar(512) null, userName nvarchar(82) null, userId numeric(19,0) null, operationDate datetime null, operation varchar(32) null, version numeric(19,0) null, xmlString ntext null, entityId numeric(19,0) null, entityType varchar(16) null, owningBinderId numeric(19,0) null, owningBinderKey varchar(255) null, primary key (id));
create table SS_CustomAttributes (id char(32) not null, type char(1) not null, folderEntry numeric(19,0) null, principal numeric(19,0) null, binder numeric(19,0) null, ownerType varchar(16) null, ownerId numeric(19,0) null, owningBinderKey varchar(255) null, owningBinderId numeric(19,0) null, name varchar(64) null, stringValue nvarchar(2000) null, description_text ntext null, description_format int null, longValue numeric(19,0) null, dateValue datetime null, serializedValue image null, xmlValue ntext null, booleanValue tinyint null, valueType int null, position int null, parent char(32) null, primary key (id));
create table SS_Dashboards (id char(32) not null, type varchar(1) not null, lockVersion numeric(19,0) not null, nextComponentId int null, showComponents tinyint null, version int null, properties image null, creation_date datetime null, creation_principal numeric(19,0) null, modification_date datetime null, modification_principal numeric(19,0) null, owner_id numeric(19,0) null, owner_type int null, binderId numeric(19,0) null, portletName varchar(256) null, primary key (id));
create table SS_DefinitionMap (binder numeric(19,0) not null, definition char(32) not null, position int not null, primary key (binder, position));
create table SS_Definitions (id char(32) not null, lockVersion numeric(19,0) not null, name nvarchar(64) null, title nvarchar(128) null, type int null, internalId varchar(32) null, visibility int null, zoneId numeric(19,0) not null, encoding image null, creation_date datetime null, creation_principal numeric(19,0) null, modification_date datetime null, modification_principal numeric(19,0) null, primary key (id));
create table SS_Events (id char(32) not null, lockVersion numeric(19,0) not null, folderEntry numeric(19,0) null, principal numeric(19,0) null, binder numeric(19,0) null, ownerType varchar(16) null, ownerId numeric(19,0) null, owningBinderKey varchar(255) null, owningBinderId numeric(19,0) null, name varchar(64) null, dtStart datetime not null, dtEnd datetime null, frequency int null, repeatInterval int null, until datetime null, repeatCount int null, weekStart int null, timeZoneSensitive tinyint null, days varchar(100) null, monthDay varchar(100) null, yearDay varchar(100) null, weekNo varchar(100) null, months varchar(100) null, minutes varchar(100) null, hours varchar(100) null, duration varchar(32) not null, creation_date datetime null, creation_principal numeric(19,0) null, modification_date datetime null, modification_principal numeric(19,0) null, timeZone varchar(16) null, primary key (id));
create table SS_FolderEntries (id numeric(19,0) identity not null, lockVersion numeric(19,0) not null, deleted tinyint null, reserved_date datetime null, reserved_principal numeric(19,0) null, entry_level int null, entry_sortKey varchar(255) null unique, owningBinderKey varchar(255) null, lastActivity datetime null, logVersion numeric(19,0) null, replyCount int null, nextDescendant int null, totalReplyCount int null, postedBy nvarchar(256) null, topEntry numeric(19,0) null, parentEntry numeric(19,0) null, wrk_date datetime null, wrk_principal numeric(19,0) null, parentBinder numeric(19,0) null, description_text ntext null, description_format int null, title nvarchar(255) null, normalTitle nvarchar(255) null, iconName varchar(64) null, entryDef char(32) null, creation_date datetime null, creation_principal numeric(19,0) null, modification_date datetime null, modification_principal numeric(19,0) null, definitionType int null, ratings_average double precision null, ratings_count numeric(19,0) null, popularity numeric(19,0) null, primary key (id));
create table SS_Forums (id numeric(19,0) identity not null, type varchar(16) not null, lockVersion numeric(19,0) not null, zoneId numeric(19,0) not null, library tinyint null, deleted tinyint null, uniqueTitles tinyint null, logVersion numeric(19,0) null, upgradeVersion int null, mirrored tinyint null, binderCount int null, resourceDriverName varchar(64) null, resourcePath nvarchar(1024) null, parentBinder numeric(19,0) null, owner numeric(19,0) null, name nvarchar(128) null, pathName nvarchar(1024) null, notify_teamOn tinyint null, notify_style int null, notify_email ntext null, notify_fromAddress nvarchar(128) null, notify_subject nvarchar(128) null, posting char(32) null, properties image null, functionMembershipInherited tinyint not null, definitionsInherited tinyint null, definitionType int null, teamMembershipInherited tinyint null, ratings_average double precision null, ratings_count numeric(19,0) null, popularity numeric(19,0) null, description_text ntext null, description_format int null, title nvarchar(255) null, normalTitle nvarchar(255) null, iconName varchar(64) null, internalId varchar(32) null, nextBinderNumber int null, binder_level int null, binder_sortKey varchar(255) null unique, entryDef char(32) null, creation_date datetime null, creation_principal numeric(19,0) null, modification_date datetime null, modification_principal numeric(19,0) null, topFolder numeric(19,0) null, nextEntryNumber int null, entryRootKey char(15) null, searchTitle nvarchar(255) null, templateTitle nvarchar(128) null, tDescription_text ntext null, tDescription_format int null, primary key (id));
create table SS_FunctionOperations (functionId numeric(19,0) not null, operationName varchar(128) not null, primary key (functionId, operationName));
create table SS_Functions (id numeric(19,0) identity not null, lockVersion numeric(19,0) not null, zoneId numeric(19,0) not null, name varchar(128) not null, primary key (id), unique (zoneId, name));
create table SS_LibraryEntries (binderId numeric(19,0) not null, type numeric(19,0) not null, name nvarchar(255) not null, entityId numeric(19,0) null, primary key (binderId, type, name));
create table SS_LicenseStats (id char(32) not null, zoneId numeric(19,0) not null, snapshotDate datetime null, internalUserCount numeric(19,0) null, externalUserCount numeric(19,0) null, checksum numeric(19,0) null, primary key (id));
create table SS_Notifications (binderId numeric(19,0) not null, principalId numeric(19,0) not null);
create table SS_Postings (id char(32) not null, lockVersion numeric(19,0) not null, enabled tinyint null, emailAddress nvarchar(256) null, password varchar(64) null, zoneId numeric(19,0) not null, binder numeric(19,0) null, definition char(32) null, replyPostingOption int null, primary key (id));
create table SS_PrincipalMembership (userId numeric(19,0) not null, groupId numeric(19,0) not null, primary key (userId, groupId));
create table SS_Principals (id numeric(19,0) identity not null, type varchar(16) not null, lockVersion numeric(19,0) not null, workspaceId numeric(19,0) null, internalId varchar(32) null, deleted tinyint null, disabled tinyint null, zoneId numeric(19,0) null, logVersion numeric(19,0) null, emailAddress nvarchar(256) null, name nvarchar(128) null, foreignName nvarchar(128) null, theme varchar(64) null, parentBinder numeric(19,0) null, description_text ntext null, description_format int null, title nvarchar(255) null, normalTitle nvarchar(255) null, iconName varchar(64) null, definitionType int null, entryDef char(32) null, creation_date datetime null, creation_principal numeric(19,0) null, modification_date datetime null, modification_principal numeric(19,0) null, displayStyle varchar(16) null, loginDate datetime null, firstName nvarchar(64) null, lastName nvarchar(64) null, middleName nvarchar(64) null, organization nvarchar(256) null, phone varchar(128) null, zonName nvarchar(100) null, password varchar(64) null, timeZone varchar(80) null, locale varchar(32) null, digestSeed numeric(19,0) null, primary key (id), unique (zoneId, foreignName), unique (zoneId, name));
create table SS_Ratings (principalId numeric(19,0) not null, entityId numeric(19,0) not null, entityType int not null, rating numeric(19,0) null, readCount numeric(19,0) null, primary key (principalId, entityId, entityType));
create table SS_SeenMap (principalId numeric(19,0) not null, seenMap image not null, lastPrune datetime null, primary key (principalId));
create table SS_Subscriptions (principalId numeric(19,0) not null, entityId numeric(19,0) not null, entityType int not null, style int null, primary key (principalId, entityId, entityType));
create table SS_Tags (id char(32) not null, entity_id numeric(19,0) null, entity_type int null, owner_id numeric(19,0) null, owner_type int null, name nvarchar(64) null, isPublic tinyint null, primary key (id));
create table SS_UserProperties (principalId numeric(19,0) not null, binderId numeric(19,0) not null, properties image not null, primary key (principalId, binderId));
create table SS_WorkAreaFunctionMembers (workAreaFunctionMembershipId numeric(19,0) not null, memberId numeric(19,0) not null, primary key (workAreaFunctionMembershipId, memberId));
create table SS_WorkAreaFunctionMemberships (id numeric(19,0) identity not null, lockVersion numeric(19,0) not null, workAreaId numeric(19,0) not null, workAreaType varchar(16) not null, zoneId numeric(19,0) not null, functionId numeric(19,0) not null, primary key (id));
create table SS_WorkflowMap (binder numeric(19,0) not null, workflowDefinition char(32) not null, entryDefinition varchar(32) not null, primary key (binder, entryDefinition));
create table SS_WorkflowResponses (id char(32) not null, folderEntry numeric(19,0) null, principal numeric(19,0) null, binder numeric(19,0) null, ownerType varchar(16) null, ownerId numeric(19,0) null, owningBinderKey varchar(255) null, owningBinderId numeric(19,0) null, name varchar(64) null, response nvarchar(2000) null, definitionId varchar(32) null, responderId numeric(19,0) null, responseDate datetime null, primary key (id));
create table SS_WorkflowStates (tokenId numeric(19,0) not null, lockVersion numeric(19,0) not null, folderEntry numeric(19,0) null, principal numeric(19,0) null, binder numeric(19,0) null, ownerType varchar(16) null, ownerId numeric(19,0) null, owningBinderKey varchar(255) null, owningBinderId numeric(19,0) null, wrk_date datetime null, wrk_principal numeric(19,0) null, state nvarchar(64) null, threadName nvarchar(64) null, timerId numeric(19,0) null, definition char(32) null, primary key (tokenId));
create index owningAttachment_Att on SS_Attachments (parentAttachment);
create index entityOwner_Att on SS_Attachments (ownerType, ownerId);
create index ownerBinder_Att on SS_Attachments (binder);
create index ownerPrincipal_Att on SS_Attachments (principal);
create index ownerFolderEntry_Att on SS_Attachments (folderEntry);
create index owningBinder_Att on SS_Attachments (owningBinderId);
alter table SS_Attachments add constraint FKA1AD4C3194B81781 foreign key (parentAttachment) references SS_Attachments;
alter table SS_Attachments add constraint FKA1AD4C3193118767 foreign key (creation_principal) references SS_Principals;
alter table SS_Attachments add constraint FKA1AD4C319C2BF2C9 foreign key (binder) references SS_Forums;
alter table SS_Attachments add constraint FKA1AD4C31DB0761E4 foreign key (modification_principal) references SS_Principals;
alter table SS_Attachments add constraint FKA1AD4C3165C1AB13 foreign key (folderEntry) references SS_FolderEntries;
alter table SS_Attachments add constraint FKA1AD4C317488E8C7 foreign key (principal) references SS_Principals;
alter table SS_Attachments add constraint FKA1AD4C31F93A11B4 foreign key (filelock_owner) references SS_Principals;
create index owningBinder_cAtt on SS_CustomAttributes (owningBinderId);
create index ownerBinder_cAtt on SS_CustomAttributes (binder);
create index ownerPrincipal_cAtt on SS_CustomAttributes (principal);
create index owningAttribute_cAtt on SS_CustomAttributes (parent);
create index ownerFolderEntry_cAtt on SS_CustomAttributes (folderEntry);
create index entityOwner_cAtt on SS_CustomAttributes (ownerType, ownerId);
alter table SS_CustomAttributes add constraint FK476EDFC79C2BF2C9 foreign key (binder) references SS_Forums;
alter table SS_CustomAttributes add constraint FK476EDFC765C1AB13 foreign key (folderEntry) references SS_FolderEntries;
alter table SS_CustomAttributes add constraint FK476EDFC77488E8C7 foreign key (principal) references SS_Principals;
alter table SS_CustomAttributes add constraint FK476EDFC76C866300 foreign key (parent) references SS_CustomAttributes;
create index ownerKey_Dash on SS_Dashboards (owner_id, owner_type);
alter table SS_Dashboards add constraint FKFA9653BE93118767 foreign key (creation_principal) references SS_Principals;
alter table SS_Dashboards add constraint FKFA9653BEDB0761E4 foreign key (modification_principal) references SS_Principals;
alter table SS_DefinitionMap add constraint FK170F74A9C2BF2C9 foreign key (binder) references SS_Forums;
alter table SS_DefinitionMap add constraint FK170F74ACC076F9B foreign key (definition) references SS_Definitions;
alter table SS_Definitions add constraint FK7B56F60193118767 foreign key (creation_principal) references SS_Principals;
alter table SS_Definitions add constraint FK7B56F601DB0761E4 foreign key (modification_principal) references SS_Principals;
create index ownerFolderEntry_Event on SS_Events (folderEntry);
create index entityOwner_Event on SS_Events (ownerType, ownerId);
create index ownerBinder_Event on SS_Events (binder);
create index ownerPrincipal_Event on SS_Events (principal);
create index owningBinder_Event on SS_Events (owningBinderId);
alter table SS_Events add constraint FKDE0E53F893118767 foreign key (creation_principal) references SS_Principals;
alter table SS_Events add constraint FKDE0E53F89C2BF2C9 foreign key (binder) references SS_Forums on delete cascade;
alter table SS_Events add constraint FKDE0E53F8DB0761E4 foreign key (modification_principal) references SS_Principals;
alter table SS_Events add constraint FKDE0E53F865C1AB13 foreign key (folderEntry) references SS_FolderEntries on delete cascade;
alter table SS_Events add constraint FKDE0E53F87488E8C7 foreign key (principal) references SS_Principals on delete cascade;
create index owningEntry_fEntry on SS_FolderEntries (topEntry);
create index owningFolder_fEntry on SS_FolderEntries (parentBinder);
create index owningBinderKey_fEntry on SS_FolderEntries (owningBinderKey);
alter table SS_FolderEntries add constraint FKA6632C83F7719C70 foreign key (reserved_principal) references SS_Principals;
alter table SS_FolderEntries add constraint FKA6632C832592FE8C foreign key (topEntry) references SS_FolderEntries;
alter table SS_FolderEntries add constraint FKA6632C8393118767 foreign key (creation_principal) references SS_Principals;
alter table SS_FolderEntries add constraint FKA6632C83DB0761E4 foreign key (modification_principal) references SS_Principals;
alter table SS_FolderEntries add constraint FKA6632C838BB3889B foreign key (entryDef) references SS_Definitions;
alter table SS_FolderEntries add constraint FKA6632C839BAA0457 foreign key (parentEntry) references SS_FolderEntries;
alter table SS_FolderEntries add constraint FKA6632C83A3644438 foreign key (wrk_principal) references SS_Principals;
alter table SS_FolderEntries add constraint FKA6632C83475453F3 foreign key (parentBinder) references SS_Forums;
create index owningBinder_Binder on SS_Forums (parentBinder);
alter table SS_Forums add constraint FKDF668A5193118767 foreign key (creation_principal) references SS_Principals;
alter table SS_Forums add constraint FKDF668A51B356319A foreign key (posting) references SS_Postings;
alter table SS_Forums add constraint FKDF668A51DB0761E4 foreign key (modification_principal) references SS_Principals;
alter table SS_Forums add constraint FKDF668A518BB3889B foreign key (entryDef) references SS_Definitions;
alter table SS_Forums add constraint FKDF668A51E6DE6B4C foreign key (owner) references SS_Principals;
alter table SS_Forums add constraint FKDF668A51475453F3 foreign key (parentBinder) references SS_Forums;
alter table SS_Forums add constraint FKDF668A518D8ADE6 foreign key (topFolder) references SS_Forums;
alter table SS_FunctionOperations add constraint FKBEF256434C758408 foreign key (functionId) references SS_Functions;
alter table SS_Notifications add constraint FK9131F92926FD3D64 foreign key (binderId) references SS_Forums;
alter table SS_Notifications add constraint FK9131F9296EADA262 foreign key (principalId) references SS_Principals;
alter table SS_Postings add constraint FKB05DE4909C2BF2C9 foreign key (binder) references SS_Forums;
alter table SS_Postings add constraint FKB05DE490CC076F9B foreign key (definition) references SS_Definitions;
alter table SS_PrincipalMembership add constraint FK176F6225AEB5AABF foreign key (userId) references SS_Principals;
alter table SS_PrincipalMembership add constraint FK176F6225197D4C44 foreign key (groupId) references SS_Principals;
alter table SS_Principals add constraint FK7693816493118767 foreign key (creation_principal) references SS_Principals;
alter table SS_Principals add constraint FK76938164DB0761E4 foreign key (modification_principal) references SS_Principals;
alter table SS_Principals add constraint FK769381648BB3889B foreign key (entryDef) references SS_Definitions;
alter table SS_Principals add constraint FK76938164475453F3 foreign key (parentBinder) references SS_Forums;
alter table SS_WorkAreaFunctionMembers add constraint FK72202A04A3D23110 foreign key (workAreaFunctionMembershipId) references SS_WorkAreaFunctionMemberships;
alter table SS_WorkflowMap add constraint FK33D2499E5123E49A foreign key (workflowDefinition) references SS_Definitions;
alter table SS_WorkflowMap add constraint FK33D2499E9C2BF2C9 foreign key (binder) references SS_Forums;
create index ownerBinder on SS_WorkflowResponses (binder);
create index owningBinder on SS_WorkflowResponses (owningBinderId);
create index ownerPrincipal on SS_WorkflowResponses (principal);
create index entityOwner on SS_WorkflowResponses (ownerType, ownerId);
create index ownerFolderEntry on SS_WorkflowResponses (folderEntry);
alter table SS_WorkflowResponses add constraint FKC44C5F149C2BF2C9 foreign key (binder) references SS_Forums;
alter table SS_WorkflowResponses add constraint FKC44C5F1465C1AB13 foreign key (folderEntry) references SS_FolderEntries on delete cascade;
alter table SS_WorkflowResponses add constraint FKC44C5F147488E8C7 foreign key (principal) references SS_Principals;
create index ownerFolderEntry_wState on SS_WorkflowStates (folderEntry);
create index ownerPrincipal_wState on SS_WorkflowStates (principal);
create index owningBinder_wState on SS_WorkflowStates (owningBinderId);
create index entityOwner_wState on SS_WorkflowStates (ownerType, ownerId);
create index ownerBinder_wState on SS_WorkflowStates (binder);
alter table SS_WorkflowStates add constraint FK8FA8AA809C2BF2C9 foreign key (binder) references SS_Forums;
alter table SS_WorkflowStates add constraint FK8FA8AA8065C1AB13 foreign key (folderEntry) references SS_FolderEntries on delete cascade;
alter table SS_WorkflowStates add constraint FK8FA8AA80A3644438 foreign key (wrk_principal) references SS_Principals;
alter table SS_WorkflowStates add constraint FK8FA8AA807488E8C7 foreign key (principal) references SS_Principals;
alter table SS_WorkflowStates add constraint FK8FA8AA80CC076F9B foreign key (definition) references SS_Definitions;
create table JBPM_ACTION (ID_ numeric(19,0) identity not null, class char(1) not null, NAME_ varchar(255) null, ISPROPAGATIONALLOWED_ tinyint null, ACTIONEXPRESSION_ varchar(255) null, ISASYNC_ tinyint null, REFERENCEDACTION_ numeric(19,0) null, ACTIONDELEGATION_ numeric(19,0) null, EVENT_ numeric(19,0) null, PROCESSDEFINITION_ numeric(19,0) null, TIMERNAME_ varchar(255) null, DUEDATE_ varchar(255) null, REPEAT_ varchar(255) null, TRANSITIONNAME_ nvarchar(255) null, TIMERACTION_ numeric(19,0) null, EXPRESSION_ varchar(4000) null, EVENTINDEX_ int null, EXCEPTIONHANDLER_ numeric(19,0) null, EXCEPTIONHANDLERINDEX_ int null, primary key (ID_));
create table JBPM_BYTEARRAY (ID_ numeric(19,0) identity not null, NAME_ varchar(255) null, FILEDEFINITION_ numeric(19,0) null, primary key (ID_));
create table JBPM_BYTEBLOCK (PROCESSFILE_ numeric(19,0) not null, BYTES_ varbinary(1024) null, INDEX_ int not null, primary key (PROCESSFILE_, INDEX_));
create table JBPM_COMMENT (ID_ numeric(19,0) identity not null, VERSION_ int not null, ACTORID_ varchar(255) null, TIME_ datetime null, MESSAGE_ varchar(4000) null, TOKEN_ numeric(19,0) null, TASKINSTANCE_ numeric(19,0) null, TOKENINDEX_ int null, TASKINSTANCEINDEX_ int null, primary key (ID_));
create table JBPM_DECISIONCONDITIONS (DECISION_ numeric(19,0) not null, TRANSITIONNAME_ nvarchar(255) null, EXPRESSION_ varchar(255) null, INDEX_ int not null, primary key (DECISION_, INDEX_));
create table JBPM_DELEGATION (ID_ numeric(19,0) identity not null, CLASSNAME_ varchar(4000) null, CONFIGURATION_ varchar(4000) null, CONFIGTYPE_ varchar(255) null, PROCESSDEFINITION_ numeric(19,0) null, primary key (ID_));
create table JBPM_EVENT (ID_ numeric(19,0) identity not null, EVENTTYPE_ varchar(255) null, TYPE_ char(1) null, GRAPHELEMENT_ numeric(19,0) null, NODE_ numeric(19,0) null, PROCESSDEFINITION_ numeric(19,0) null, TRANSITION_ numeric(19,0) null, TASK_ numeric(19,0) null, primary key (ID_));
create table JBPM_EXCEPTIONHANDLER (ID_ numeric(19,0) identity not null, EXCEPTIONCLASSNAME_ varchar(4000) null, TYPE_ char(1) null, GRAPHELEMENT_ numeric(19,0) null, NODE_ numeric(19,0) null, GRAPHELEMENTINDEX_ int null, PROCESSDEFINITION_ numeric(19,0) null, TRANSITION_ numeric(19,0) null, TASK_ numeric(19,0) null, primary key (ID_));
create table JBPM_LOG (ID_ numeric(19,0) identity not null, CLASS_ char(1) not null, INDEX_ int null, DATE_ datetime null, TOKEN_ numeric(19,0) null, PARENT_ numeric(19,0) null, SWIMLANEINSTANCE_ numeric(19,0) null, TASKINSTANCE_ numeric(19,0) null, MESSAGE_ varchar(4000) null, TASKOLDACTORID_ varchar(255) null, TASKACTORID_ varchar(255) null, CHILD_ numeric(19,0) null, VARIABLEINSTANCE_ numeric(19,0) null, OLDLONGIDCLASS_ varchar(255) null, OLDLONGIDVALUE_ numeric(19,0) null, NEWLONGIDCLASS_ varchar(255) null, NEWLONGIDVALUE_ numeric(19,0) null, OLDBYTEARRAY_ numeric(19,0) null, NEWBYTEARRAY_ numeric(19,0) null, OLDDOUBLEVALUE_ double precision null, NEWDOUBLEVALUE_ double precision null, OLDLONGVALUE_ numeric(19,0) null, NEWLONGVALUE_ numeric(19,0) null, EXCEPTION_ varchar(4000) null, ACTION_ numeric(19,0) null, OLDDATEVALUE_ datetime null, NEWDATEVALUE_ datetime null, OLDSTRINGIDCLASS_ varchar(255) null, OLDSTRINGIDVALUE_ varchar(255) null, NEWSTRINGIDCLASS_ varchar(255) null, NEWSTRINGIDVALUE_ varchar(255) null, OLDSTRINGVALUE_ varchar(4000) null, NEWSTRINGVALUE_ varchar(4000) null, TRANSITION_ numeric(19,0) null, SOURCENODE_ numeric(19,0) null, DESTINATIONNODE_ numeric(19,0) null, NODE_ numeric(19,0) null, ENTER_ datetime null, LEAVE_ datetime null, DURATION_ numeric(19,0) null, primary key (ID_));
create table JBPM_MESSAGE (ID_ numeric(19,0) identity not null, CLASS_ char(1) not null, DESTINATION_ varchar(255) null, EXCEPTION_ varchar(4000) null, ISSUSPENDED_ tinyint null, TOKEN_ numeric(19,0) null, TEXT_ nvarchar(255) null, ACTION_ numeric(19,0) null, TRANSITIONNAME_ nvarchar(255) null, TASKINSTANCE_ numeric(19,0) null, NODE_ numeric(19,0) null, primary key (ID_));
create table JBPM_MODULEDEFINITION (ID_ numeric(19,0) identity not null, CLASS_ char(1) not null, NAME_ varchar(4000) null, PROCESSDEFINITION_ numeric(19,0) null, STARTTASK_ numeric(19,0) null, primary key (ID_));
create table JBPM_MODULEINSTANCE (ID_ numeric(19,0) identity not null, CLASS_ char(1) not null, PROCESSINSTANCE_ numeric(19,0) null, TASKMGMTDEFINITION_ numeric(19,0) null, NAME_ varchar(255) null, primary key (ID_));
create table JBPM_NODE (ID_ numeric(19,0) identity not null, CLASS_ char(1) not null, NAME_ nvarchar(255) null, PROCESSDEFINITION_ numeric(19,0) null, ISASYNC_ tinyint null, ACTION_ numeric(19,0) null, SUPERSTATE_ numeric(19,0) null, DECISIONEXPRESSION_ varchar(255) null, DECISIONDELEGATION numeric(19,0) null, SUBPROCESSDEFINITION_ numeric(19,0) null, SIGNAL_ int null, CREATETASKS_ tinyint null, ENDTASKS_ tinyint null, NODECOLLECTIONINDEX_ int null, primary key (ID_));
create table JBPM_POOLEDACTOR (ID_ numeric(19,0) identity not null, ACTORID_ varchar(255) null, SWIMLANEINSTANCE_ numeric(19,0) null, primary key (ID_));
create table JBPM_PROCESSDEFINITION (ID_ numeric(19,0) identity not null, NAME_ varchar(255) null, VERSION_ int null, ISTERMINATIONIMPLICIT_ tinyint null, STARTSTATE_ numeric(19,0) null, primary key (ID_));
create table JBPM_PROCESSINSTANCE (ID_ numeric(19,0) identity not null, VERSION_ int not null, START_ datetime null, END_ datetime null, ISSUSPENDED_ tinyint null, PROCESSDEFINITION_ numeric(19,0) null, ROOTTOKEN_ numeric(19,0) null, SUPERPROCESSTOKEN_ numeric(19,0) null, primary key (ID_));
create table JBPM_RUNTIMEACTION (ID_ numeric(19,0) identity not null, VERSION_ int not null, EVENTTYPE_ varchar(255) null, TYPE_ char(1) null, GRAPHELEMENT_ numeric(19,0) null, PROCESSINSTANCE_ numeric(19,0) null, ACTION_ numeric(19,0) null, PROCESSINSTANCEINDEX_ int null, primary key (ID_));
create table JBPM_SWIMLANE (ID_ numeric(19,0) identity not null, NAME_ varchar(255) null, ACTORIDEXPRESSION_ varchar(255) null, POOLEDACTORSEXPRESSION_ varchar(255) null, ASSIGNMENTDELEGATION_ numeric(19,0) null, TASKMGMTDEFINITION_ numeric(19,0) null, primary key (ID_));
create table JBPM_SWIMLANEINSTANCE (ID_ numeric(19,0) identity not null, NAME_ varchar(255) null, ACTORID_ varchar(255) null, SWIMLANE_ numeric(19,0) null, TASKMGMTINSTANCE_ numeric(19,0) null, primary key (ID_));
create table JBPM_TASK (ID_ numeric(19,0) identity not null, NAME_ varchar(255) null, PROCESSDEFINITION_ numeric(19,0) null, DESCRIPTION_ varchar(4000) null, ISBLOCKING_ tinyint null, ISSIGNALLING_ tinyint null, DUEDATE_ varchar(255) null, ACTORIDEXPRESSION_ varchar(255) null, POOLEDACTORSEXPRESSION_ varchar(255) null, TASKMGMTDEFINITION_ numeric(19,0) null, TASKNODE_ numeric(19,0) null, STARTSTATE_ numeric(19,0) null, ASSIGNMENTDELEGATION_ numeric(19,0) null, SWIMLANE_ numeric(19,0) null, TASKCONTROLLER_ numeric(19,0) null, primary key (ID_));
create table JBPM_TASKACTORPOOL (POOLEDACTOR_ numeric(19,0) not null, TASKINSTANCE_ numeric(19,0) not null, primary key (TASKINSTANCE_, POOLEDACTOR_));
create table JBPM_TASKCONTROLLER (ID_ numeric(19,0) identity not null, TASKCONTROLLERDELEGATION_ numeric(19,0) null, primary key (ID_));
create table JBPM_TASKINSTANCE (ID_ numeric(19,0) identity not null, CLASS_ char(1) not null, NAME_ varchar(255) null, DESCRIPTION_ varchar(4000) null, ACTORID_ varchar(255) null, CREATE_ datetime null, START_ datetime null, END_ datetime null, DUEDATE_ datetime null, PRIORITY_ int null, ISCANCELLED_ tinyint null, ISSUSPENDED_ tinyint null, ISOPEN_ tinyint null, ISSIGNALLING_ tinyint null, ISBLOCKING_ tinyint null, TASK_ numeric(19,0) null, TOKEN_ numeric(19,0) null, SWIMLANINSTANCE_ numeric(19,0) null, TASKMGMTINSTANCE_ numeric(19,0) null, primary key (ID_));
create table JBPM_TIMER (ID_ numeric(19,0) identity not null, NAME_ varchar(255) null, DUEDATE_ datetime null, REPEAT_ varchar(255) null, TRANSITIONNAME_ nvarchar(255) null, EXCEPTION_ varchar(4000) null, ISSUSPENDED_ tinyint null, ACTION_ numeric(19,0) null, TOKEN_ numeric(19,0) null, PROCESSINSTANCE_ numeric(19,0) null, TASKINSTANCE_ numeric(19,0) null, GRAPHELEMENTTYPE_ varchar(255) null, GRAPHELEMENT_ numeric(19,0) null, primary key (ID_));
create table JBPM_TOKEN (ID_ numeric(19,0) identity not null, VERSION_ int not null, NAME_ nvarchar(255) null, START_ datetime null, END_ datetime null, NODEENTER_ datetime null, NEXTLOGINDEX_ int null, ISABLETOREACTIVATEPARENT_ tinyint null, ISTERMINATIONIMPLICIT_ tinyint null, ISSUSPENDED_ tinyint null, NODE_ numeric(19,0) null, PROCESSINSTANCE_ numeric(19,0) null, PARENT_ numeric(19,0) null, SUBPROCESSINSTANCE_ numeric(19,0) null, primary key (ID_));
create table JBPM_TOKENVARIABLEMAP (ID_ numeric(19,0) identity not null, TOKEN_ numeric(19,0) null, CONTEXTINSTANCE_ numeric(19,0) null, primary key (ID_));
create table JBPM_TRANSITION (ID_ numeric(19,0) identity not null, NAME_ nvarchar(255) null, PROCESSDEFINITION_ numeric(19,0) null, FROM_ numeric(19,0) null, TO_ numeric(19,0) null, FROMINDEX_ int null, primary key (ID_));
create table JBPM_VARIABLEACCESS (ID_ numeric(19,0) identity not null, VARIABLENAME_ varchar(255) null, ACCESS_ varchar(255) null, MAPPEDNAME_ varchar(255) null, PROCESSSTATE_ numeric(19,0) null, TASKCONTROLLER_ numeric(19,0) null, INDEX_ int null, SCRIPT_ numeric(19,0) null, primary key (ID_));
create table JBPM_VARIABLEINSTANCE (ID_ numeric(19,0) identity not null, CLASS_ char(1) not null, NAME_ varchar(255) null, CONVERTER_ char(1) null, TOKEN_ numeric(19,0) null, TOKENVARIABLEMAP_ numeric(19,0) null, PROCESSINSTANCE_ numeric(19,0) null, BYTEARRAYVALUE_ numeric(19,0) null, DATEVALUE_ datetime null, DOUBLEVALUE_ double precision null, LONGIDCLASS_ varchar(255) null, LONGVALUE_ numeric(19,0) null, STRINGIDCLASS_ varchar(255) null, STRINGVALUE_ nvarchar(255) null, TASKINSTANCE_ numeric(19,0) null, primary key (ID_));
alter table JBPM_ACTION add constraint FK_ACTION_EVENT foreign key (EVENT_) references JBPM_EVENT;
alter table JBPM_ACTION add constraint FK_ACTION_EXPTHDL foreign key (EXCEPTIONHANDLER_) references JBPM_EXCEPTIONHANDLER;
alter table JBPM_ACTION add constraint FK_ACTION_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION;
alter table JBPM_ACTION add constraint FK_CRTETIMERACT_TA foreign key (TIMERACTION_) references JBPM_ACTION;
alter table JBPM_ACTION add constraint FK_ACTION_ACTNDEL foreign key (ACTIONDELEGATION_) references JBPM_DELEGATION;
alter table JBPM_ACTION add constraint FK_ACTION_REFACT foreign key (REFERENCEDACTION_) references JBPM_ACTION;
alter table JBPM_BYTEARRAY add constraint FK_BYTEARR_FILDEF foreign key (FILEDEFINITION_) references JBPM_MODULEDEFINITION;
alter table JBPM_BYTEBLOCK add constraint FK_BYTEBLOCK_FILE foreign key (PROCESSFILE_) references JBPM_BYTEARRAY;
alter table JBPM_COMMENT add constraint FK_COMMENT_TOKEN foreign key (TOKEN_) references JBPM_TOKEN;
alter table JBPM_COMMENT add constraint FK_COMMENT_TSK foreign key (TASKINSTANCE_) references JBPM_TASKINSTANCE;
alter table JBPM_DECISIONCONDITIONS add constraint FK_DECCOND_DEC foreign key (DECISION_) references JBPM_NODE;
alter table JBPM_DELEGATION add constraint FK_DELEGATION_PRCD foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION;
alter table JBPM_EVENT add constraint FK_EVENT_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION;
alter table JBPM_EVENT add constraint FK_EVENT_NODE foreign key (NODE_) references JBPM_NODE;
alter table JBPM_EVENT add constraint FK_EVENT_TRANS foreign key (TRANSITION_) references JBPM_TRANSITION;
alter table JBPM_EVENT add constraint FK_EVENT_TASK foreign key (TASK_) references JBPM_TASK;
alter table JBPM_LOG add constraint FK_LOG_SOURCENODE foreign key (SOURCENODE_) references JBPM_NODE;
alter table JBPM_LOG add constraint FK_LOG_TOKEN foreign key (TOKEN_) references JBPM_TOKEN;
alter table JBPM_LOG add constraint FK_LOG_OLDBYTES foreign key (OLDBYTEARRAY_) references JBPM_BYTEARRAY;
alter table JBPM_LOG add constraint FK_LOG_NEWBYTES foreign key (NEWBYTEARRAY_) references JBPM_BYTEARRAY;
alter table JBPM_LOG add constraint FK_LOG_CHILDTOKEN foreign key (CHILD_) references JBPM_TOKEN;
alter table JBPM_LOG add constraint FK_LOG_DESTNODE foreign key (DESTINATIONNODE_) references JBPM_NODE;
alter table JBPM_LOG add constraint FK_LOG_TASKINST foreign key (TASKINSTANCE_) references JBPM_TASKINSTANCE;
alter table JBPM_LOG add constraint FK_LOG_SWIMINST foreign key (SWIMLANEINSTANCE_) references JBPM_SWIMLANEINSTANCE;
alter table JBPM_LOG add constraint FK_LOG_PARENT foreign key (PARENT_) references JBPM_LOG;
alter table JBPM_LOG add constraint FK_LOG_NODE foreign key (NODE_) references JBPM_NODE;
alter table JBPM_LOG add constraint FK_LOG_ACTION foreign key (ACTION_) references JBPM_ACTION;
alter table JBPM_LOG add constraint FK_LOG_VARINST foreign key (VARIABLEINSTANCE_) references JBPM_VARIABLEINSTANCE;
alter table JBPM_LOG add constraint FK_LOG_TRANSITION foreign key (TRANSITION_) references JBPM_TRANSITION;
alter table JBPM_MESSAGE add constraint FK_MSG_TOKEN foreign key (TOKEN_) references JBPM_TOKEN;
alter table JBPM_MESSAGE add constraint FK_CMD_NODE foreign key (NODE_) references JBPM_NODE;
alter table JBPM_MESSAGE add constraint FK_CMD_ACTION foreign key (ACTION_) references JBPM_ACTION;
alter table JBPM_MESSAGE add constraint FK_CMD_TASKINST foreign key (TASKINSTANCE_) references JBPM_TASKINSTANCE;
alter table JBPM_MODULEDEFINITION add constraint FK_TSKDEF_START foreign key (STARTTASK_) references JBPM_TASK;
alter table JBPM_MODULEDEFINITION add constraint FK_MODDEF_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION;
alter table JBPM_MODULEINSTANCE add constraint FK_TASKMGTINST_TMD foreign key (TASKMGMTDEFINITION_) references JBPM_MODULEDEFINITION;
alter table JBPM_MODULEINSTANCE add constraint FK_MODINST_PRCINST foreign key (PROCESSINSTANCE_) references JBPM_PROCESSINSTANCE;
alter table JBPM_NODE add constraint FK_PROCST_SBPRCDEF foreign key (SUBPROCESSDEFINITION_) references JBPM_PROCESSDEFINITION;
alter table JBPM_NODE add constraint FK_NODE_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION;
alter table JBPM_NODE add constraint FK_NODE_ACTION foreign key (ACTION_) references JBPM_ACTION;
alter table JBPM_NODE add constraint FK_DECISION_DELEG foreign key (DECISIONDELEGATION) references JBPM_DELEGATION;
alter table JBPM_NODE add constraint FK_NODE_SUPERSTATE foreign key (SUPERSTATE_) references JBPM_NODE;
create index IDX_PLDACTR_ACTID on JBPM_POOLEDACTOR (ACTORID_);
alter table JBPM_POOLEDACTOR add constraint FK_POOLEDACTOR_SLI foreign key (SWIMLANEINSTANCE_) references JBPM_SWIMLANEINSTANCE;
alter table JBPM_PROCESSDEFINITION add constraint FK_PROCDEF_STRTSTA foreign key (STARTSTATE_) references JBPM_NODE;
alter table JBPM_PROCESSINSTANCE add constraint FK_PROCIN_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION;
alter table JBPM_PROCESSINSTANCE add constraint FK_PROCIN_ROOTTKN foreign key (ROOTTOKEN_) references JBPM_TOKEN;
alter table JBPM_PROCESSINSTANCE add constraint FK_PROCIN_SPROCTKN foreign key (SUPERPROCESSTOKEN_) references JBPM_TOKEN;
alter table JBPM_RUNTIMEACTION add constraint FK_RTACTN_PROCINST foreign key (PROCESSINSTANCE_) references JBPM_PROCESSINSTANCE;
alter table JBPM_RUNTIMEACTION add constraint FK_RTACTN_ACTION foreign key (ACTION_) references JBPM_ACTION;
alter table JBPM_SWIMLANE add constraint FK_SWL_ASSDEL foreign key (ASSIGNMENTDELEGATION_) references JBPM_DELEGATION;
alter table JBPM_SWIMLANE add constraint FK_SWL_TSKMGMTDEF foreign key (TASKMGMTDEFINITION_) references JBPM_MODULEDEFINITION;
alter table JBPM_SWIMLANEINSTANCE add constraint FK_SWIMLANEINST_TM foreign key (TASKMGMTINSTANCE_) references JBPM_MODULEINSTANCE;
alter table JBPM_SWIMLANEINSTANCE add constraint FK_SWIMLANEINST_SL foreign key (SWIMLANE_) references JBPM_SWIMLANE;
alter table JBPM_TASK add constraint FK_TSK_TSKCTRL foreign key (TASKCONTROLLER_) references JBPM_TASKCONTROLLER;
alter table JBPM_TASK add constraint FK_TASK_ASSDEL foreign key (ASSIGNMENTDELEGATION_) references JBPM_DELEGATION;
alter table JBPM_TASK add constraint FK_TASK_TASKNODE foreign key (TASKNODE_) references JBPM_NODE;
alter table JBPM_TASK add constraint FK_TASK_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION;
alter table JBPM_TASK add constraint FK_TASK_STARTST foreign key (STARTSTATE_) references JBPM_NODE;
alter table JBPM_TASK add constraint FK_TASK_TASKMGTDEF foreign key (TASKMGMTDEFINITION_) references JBPM_MODULEDEFINITION;
alter table JBPM_TASK add constraint FK_TASK_SWIMLANE foreign key (SWIMLANE_) references JBPM_SWIMLANE;
alter table JBPM_TASKACTORPOOL add constraint FK_TSKACTPOL_PLACT foreign key (POOLEDACTOR_) references JBPM_POOLEDACTOR;
alter table JBPM_TASKACTORPOOL add constraint FK_TASKACTPL_TSKI foreign key (TASKINSTANCE_) references JBPM_TASKINSTANCE;
alter table JBPM_TASKCONTROLLER add constraint FK_TSKCTRL_DELEG foreign key (TASKCONTROLLERDELEGATION_) references JBPM_DELEGATION;
create index IDX_TASK_ACTORID on JBPM_TASKINSTANCE (ACTORID_);
alter table JBPM_TASKINSTANCE add constraint FK_TASKINST_TMINST foreign key (TASKMGMTINSTANCE_) references JBPM_MODULEINSTANCE;
alter table JBPM_TASKINSTANCE add constraint FK_TASKINST_TOKEN foreign key (TOKEN_) references JBPM_TOKEN;
alter table JBPM_TASKINSTANCE add constraint FK_TASKINST_SLINST foreign key (SWIMLANINSTANCE_) references JBPM_SWIMLANEINSTANCE;
alter table JBPM_TASKINSTANCE add constraint FK_TASKINST_TASK foreign key (TASK_) references JBPM_TASK;
alter table JBPM_TIMER add constraint FK_TIMER_TOKEN foreign key (TOKEN_) references JBPM_TOKEN;
alter table JBPM_TIMER add constraint FK_TIMER_PRINST foreign key (PROCESSINSTANCE_) references JBPM_PROCESSINSTANCE;
alter table JBPM_TIMER add constraint FK_TIMER_ACTION foreign key (ACTION_) references JBPM_ACTION;
alter table JBPM_TIMER add constraint FK_TIMER_TSKINST foreign key (TASKINSTANCE_) references JBPM_TASKINSTANCE;
alter table JBPM_TOKEN add constraint FK_TOKEN_PARENT foreign key (PARENT_) references JBPM_TOKEN;
alter table JBPM_TOKEN add constraint FK_TOKEN_NODE foreign key (NODE_) references JBPM_NODE;
alter table JBPM_TOKEN add constraint FK_TOKEN_PROCINST foreign key (PROCESSINSTANCE_) references JBPM_PROCESSINSTANCE;
alter table JBPM_TOKEN add constraint FK_TOKEN_SUBPI foreign key (SUBPROCESSINSTANCE_) references JBPM_PROCESSINSTANCE;
alter table JBPM_TOKENVARIABLEMAP add constraint FK_TKVARMAP_CTXT foreign key (CONTEXTINSTANCE_) references JBPM_MODULEINSTANCE;
alter table JBPM_TOKENVARIABLEMAP add constraint FK_TKVARMAP_TOKEN foreign key (TOKEN_) references JBPM_TOKEN;
alter table JBPM_TRANSITION add constraint FK_TRANSITION_TO foreign key (TO_) references JBPM_NODE;
alter table JBPM_TRANSITION add constraint FK_TRANS_PROCDEF foreign key (PROCESSDEFINITION_) references JBPM_PROCESSDEFINITION;
alter table JBPM_TRANSITION add constraint FK_TRANSITION_FROM foreign key (FROM_) references JBPM_NODE;
alter table JBPM_VARIABLEACCESS add constraint FK_VARACC_TSKCTRL foreign key (TASKCONTROLLER_) references JBPM_TASKCONTROLLER;
alter table JBPM_VARIABLEACCESS add constraint FK_VARACC_SCRIPT foreign key (SCRIPT_) references JBPM_ACTION;
alter table JBPM_VARIABLEACCESS add constraint FK_VARACC_PROCST foreign key (PROCESSSTATE_) references JBPM_NODE;
alter table JBPM_VARIABLEINSTANCE add constraint FK_VARINST_TK foreign key (TOKEN_) references JBPM_TOKEN;
alter table JBPM_VARIABLEINSTANCE add constraint FK_VARINST_TKVARMP foreign key (TOKENVARIABLEMAP_) references JBPM_TOKENVARIABLEMAP;
alter table JBPM_VARIABLEINSTANCE add constraint FK_VARINST_PRCINST foreign key (PROCESSINSTANCE_) references JBPM_PROCESSINSTANCE;
alter table JBPM_VARIABLEINSTANCE add constraint FK_VAR_TSKINST foreign key (TASKINSTANCE_) references JBPM_TASKINSTANCE;
alter table JBPM_VARIABLEINSTANCE add constraint FK_BYTEINST_ARRAY foreign key (BYTEARRAYVALUE_) references JBPM_BYTEARRAY;
CREATE TABLE SSQRTZ_CALENDARS (  CALENDAR_NAME VARCHAR (80)  NOT NULL ,  CALENDAR IMAGE NOT NULL,  PRIMARY KEY (CALENDAR_NAME));
CREATE TABLE SSQRTZ_CRON_TRIGGERS (  TRIGGER_NAME VARCHAR (80)  NOT NULL ,  TRIGGER_GROUP VARCHAR (80)  NOT NULL ,  CRON_EXPRESSION VARCHAR (80)  NOT NULL ,  TIME_ZONE_ID VARCHAR (80),  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP));
CREATE TABLE SSQRTZ_FIRED_TRIGGERS (  ENTRY_ID VARCHAR (95)  NOT NULL ,  TRIGGER_NAME VARCHAR (80)  NOT NULL ,  TRIGGER_GROUP VARCHAR (80)  NOT NULL ,  IS_VOLATILE VARCHAR (1)  NOT NULL ,  INSTANCE_NAME VARCHAR (80)  NOT NULL ,  FIRED_TIME BIGINT NOT NULL ,  STATE VARCHAR (16)  NOT NULL,  JOB_NAME VARCHAR (80)  NULL ,  JOB_GROUP VARCHAR (80)  NULL ,  IS_STATEFUL VARCHAR (1)  NULL ,  REQUESTS_RECOVERY VARCHAR (1)  NULL,  PRIMARY KEY (ENTRY_ID));
CREATE TABLE SSQRTZ_PAUSED_TRIGGER_GRPS (  TRIGGER_GROUP VARCHAR (80)  NOT NULL,  PRIMARY KEY (TRIGGER_GROUP));
CREATE TABLE SSQRTZ_SCHEDULER_STATE (  INSTANCE_NAME VARCHAR (80)  NOT NULL ,  LAST_CHECKIN_TIME BIGINT NOT NULL ,  CHECKIN_INTERVAL BIGINT NOT NULL ,  RECOVERER VARCHAR (80)  NULL,PRIMARY KEY (INSTANCE_NAME));
CREATE TABLE SSQRTZ_LOCKS (  LOCK_NAME VARCHAR (40)  NOT NULL,PRIMARY KEY (LOCK_NAME));
CREATE TABLE SSQRTZ_JOB_DETAILS (  JOB_NAME VARCHAR (80)  NOT NULL ,  JOB_GROUP VARCHAR (80)  NOT NULL ,  DESCRIPTION VARCHAR (120) NULL ,  JOB_CLASS_NAME VARCHAR (128)  NOT NULL ,  IS_DURABLE VARCHAR (1)  NOT NULL ,  IS_VOLATILE VARCHAR (1)  NOT NULL ,  IS_STATEFUL VARCHAR (1)  NOT NULL ,  REQUESTS_RECOVERY VARCHAR (1)  NOT NULL ,  JOB_DATA IMAGE NULL,  PRIMARY KEY (JOB_NAME,JOB_GROUP));
CREATE TABLE SSQRTZ_JOB_LISTENERS (  JOB_NAME VARCHAR (80)  NOT NULL ,  JOB_GROUP VARCHAR (80)  NOT NULL ,  JOB_LISTENER VARCHAR (80)  NOT NULL,  PRIMARY KEY (JOB_NAME,JOB_GROUP,JOB_LISTENER));
CREATE TABLE SSQRTZ_SIMPLE_TRIGGERS (  TRIGGER_NAME VARCHAR (80)  NOT NULL ,  TRIGGER_GROUP VARCHAR (80)  NOT NULL ,  REPEAT_COUNT BIGINT NOT NULL ,  REPEAT_INTERVAL BIGINT NOT NULL ,  TIMES_TRIGGERED BIGINT NOT NULL,  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP));
CREATE TABLE SSQRTZ_BLOB_TRIGGERS (  TRIGGER_NAME VARCHAR (80)  NOT NULL ,  TRIGGER_GROUP VARCHAR (80)  NOT NULL ,  BLOB_DATA IMAGE NULL,  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP));
CREATE TABLE SSQRTZ_TRIGGER_LISTENERS (  TRIGGER_NAME VARCHAR (80)  NOT NULL ,  TRIGGER_GROUP VARCHAR (80)  NOT NULL ,  TRIGGER_LISTENER VARCHAR (80)  NOT NULL,  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_LISTENER));
CREATE TABLE SSQRTZ_TRIGGERS (  TRIGGER_NAME VARCHAR (80)  NOT NULL ,  TRIGGER_GROUP VARCHAR (80)  NOT NULL ,  JOB_NAME VARCHAR (80)  NOT NULL ,  JOB_GROUP VARCHAR (80)  NOT NULL ,  IS_VOLATILE VARCHAR (1)  NOT NULL ,  DESCRIPTION VARCHAR (120) NULL ,  NEXT_FIRE_TIME BIGINT NULL ,  PREV_FIRE_TIME BIGINT NULL ,  TRIGGER_STATE VARCHAR (16)  NOT NULL ,  TRIGGER_TYPE VARCHAR (8)  NOT NULL ,  START_TIME BIGINT NOT NULL ,  END_TIME BIGINT NULL ,  CALENDAR_NAME VARCHAR (80)  NULL ,  MISFIRE_INSTR SMALLINT NULL,  PRIMARY KEY (TRIGGER_NAME,TRIGGER_GROUP));
ALTER TABLE SSQRTZ_CRON_TRIGGERS ADD
  CONSTRAINT FK_SSQRTZ_CRON_TRIGGERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) REFERENCES SSQRTZ_TRIGGERS (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) ON DELETE CASCADE;
ALTER TABLE SSQRTZ_JOB_LISTENERS ADD
  CONSTRAINT FK_SSQRTZ_JOB_LISTENERS_SSQRTZ_JOB_DETAILS FOREIGN KEY
  (
    JOB_NAME,
    JOB_GROUP
  ) REFERENCES SSQRTZ_JOB_DETAILS (
    JOB_NAME,
    JOB_GROUP
  ) ON DELETE CASCADE;
ALTER TABLE SSQRTZ_SIMPLE_TRIGGERS ADD
  CONSTRAINT FK_SSQRTZ_SIMPLE_TRIGGERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) REFERENCES SSQRTZ_TRIGGERS (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) ON DELETE CASCADE;
ALTER TABLE SSQRTZ_TRIGGER_LISTENERS ADD
  CONSTRAINT FK_SSQRTZ_TRIGGER_LISTENERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) REFERENCES SSQRTZ_TRIGGERS (
    TRIGGER_NAME,
    TRIGGER_GROUP
  ) ON DELETE CASCADE;
ALTER TABLE SSQRTZ_TRIGGERS ADD
  CONSTRAINT FK_SSQRTZ_TRIGGERS_SSQRTZ_JOB_DETAILS FOREIGN KEY
  (
    JOB_NAME,
    JOB_GROUP
  ) REFERENCES SSQRTZ_JOB_DETAILS (
    JOB_NAME,
    JOB_GROUP
  );
 ALTER TABLE SSQRTZ_BLOB_TRIGGERS ADD CONSTRAINT FK_SSQRTZ_BLOB_TRIGGERS_SSQRTZ_TRIGGERS FOREIGN KEY
  (TRIGGER_NAME,TRIGGER_GROUP) REFERENCES SSQRTZ_TRIGGERS (TRIGGER_NAME,TRIGGER_GROUP);
INSERT INTO SSQRTZ_LOCKS VALUES('TRIGGER_ACCESS');
INSERT INTO SSQRTZ_LOCKS VALUES('JOB_ACCESS');
INSERT INTO SSQRTZ_LOCKS VALUES('CALENDAR_ACCESS');
INSERT INTO SSQRTZ_LOCKS VALUES('STATE_ACCESS');
INSERT INTO SSQRTZ_LOCKS VALUES('MISFIRE_ACCESS');
