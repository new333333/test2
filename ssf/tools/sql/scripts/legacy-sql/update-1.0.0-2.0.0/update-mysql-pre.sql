alter table SS_Attachments drop foreign key FKA1AD4C3193118767;
alter table SS_Attachments drop foreign key FKA1AD4C31DB0761E4;
alter table SS_Attachments drop foreign key FKA1AD4C31F93A11B4;
alter table SS_Dashboards drop foreign key FKFA9653BE93118767;
alter table SS_Dashboards drop foreign key FKFA9653BEDB0761E4;
alter table SS_Definitions drop foreign key FK7B56F60193118767;
alter table SS_Definitions drop foreign key FK7B56F601DB0761E4;
alter table SS_Events drop foreign key FKDE0E53F893118767;
alter table SS_Events drop foreign key FKDE0E53F8DB0761E4;
alter table SS_FolderEntries drop foreign key FKA6632C83F7719C70;
alter table SS_FolderEntries drop foreign key FKA6632C8393118767;
alter table SS_FolderEntries drop foreign key FKA6632C83DB0761E4;
alter table SS_FolderEntries drop foreign key FKA6632C83A3644438;
alter table SS_Forums drop foreign key FKDF668A5193118767;
alter table SS_Forums drop foreign key FKDF668A51DB0761E4;
alter table SS_Notifications drop foreign key FK9131F9296EADA262;
alter table SS_PrincipalMembership drop foreign key FK176F6225AEB5AABF;
alter table SS_Principals drop foreign key FK7693816493118767;
alter table SS_Principals drop foreign key FK76938164DB0761E4;
alter table SS_WorkflowStates drop foreign key FK8FA8AA80A3644438;
alter table SS_Forums drop column upgradeVersion;
