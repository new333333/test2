alter table SS_Attachments drop constraint FKA1AD4C3193118767;
alter table SS_Attachments drop constraint FKA1AD4C31DB0761E4;
alter table SS_Attachments drop constraint FKA1AD4C31F93A11B4;
alter table SS_Dashboards drop constraint FKFA9653BE93118767;
alter table SS_Dashboards drop constraint FKFA9653BEDB0761E4;
alter table SS_Definitions drop constraint FK7B56F60193118767;
alter table SS_Definitions drop constraint FK7B56F601DB0761E4;
alter table SS_Events drop constraint FKDE0E53F893118767;
alter table SS_Events drop constraint FKDE0E53F8DB0761E4;
alter table SS_FolderEntries drop constraint FKA6632C83F7719C70;
alter table SS_FolderEntries drop constraint FKA6632C8393118767;
alter table SS_FolderEntries drop constraint FKA6632C83DB0761E4;
alter table SS_FolderEntries drop constraint FKA6632C83A3644438;
alter table SS_Forums drop constraint FKDF668A5193118767;
alter table SS_Forums drop constraint FKDF668A51DB0761E4;
alter table SS_Notifications drop constraint FK9131F9296EADA262;
alter table SS_PrincipalMembership drop constraint FK176F6225AEB5AABF;
alter table SS_Principals drop constraint FK7693816493118767;
alter table SS_Principals drop constraint FK76938164DB0761E4;
alter table SS_WorkflowStates drop constraint FK8FA8AA80A3644438;
alter table SS_Forums drop column upgradeVersion;
