alter table SSQRTZ_TRIGGERS add PRIORITY integer null;
alter table SSQRTZ_TRIGGERS add JOB_DATA image null;
alter table SSQRTZ_FIRED_TRIGGERS add PRIORITY integer null;
alter table SSQRTZ_SCHEDULER_STATE drop column RECOVERER;
alter table SS_Events alter column timeZone varchar(80);
create index owningBinder_audit on SS_AuditTrail (owningBinderId);
create index entityOwner_audit on SS_AuditTrail (entityId, entityType);
create index entityTransaction_audit on SS_AuditTrail (startDate, entityId, entityType, transactionType);
create index owningBinder_clog on SS_ChangeLogs (owningBinderId);
create index entityOwner_clog on SS_ChangeLogs (entityId, entityType);
create index address_email on SS_EmailAddresses (address);
create index indexingJournal_nodeIndex on SS_IndexingJournal (nodeName, indexName);
create index notifyStatus_full on SS_NotifyStatus (zoneId, lastModified, lastFullSent);
create index notifyStatus_digest on SS_NotifyStatus (zoneId, lastModified, lastDigestSent);
create index access_shared on SS_SharedEntity (sharedDate, accessId, accessType);
create index binderId_simpleName on SS_SimpleName (binderId);
create index emailAddress_simpleName on SS_SimpleName (emailAddress);
create index userId_tokenInfoSession on SS_TokenInfo (userId);
create index owningBinder_wfhistory on SS_WorkflowHistory (owningBinderId);
create index entityTransaction_wfhistory on SS_WorkflowHistory (startDate, entityId, entityType);
create index entityOwner_wfhistory on SS_WorkflowHistory (entityId, entityType);
create unique index definition_name on SS_Definitions (zoneId, name, binderId);
go
update SSQRTZ_TRIGGERS set PRIORITY=5 where PRIORITY is null;
update SSQRTZ_FIRED_TRIGGERS set PRIORITY=5 where PRIORITY is null;