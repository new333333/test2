use sitescape;
alter table SS_EmailLog drop column comment;
alter table SS_EmailLog add column comments longtext;
