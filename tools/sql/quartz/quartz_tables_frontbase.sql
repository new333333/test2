
create table SSQRTZ_CALENDARS (
CALENDAR_NAME varchar(80) not null,
CALENDAR blob not null,
primary key (CALENDAR_NAME)
);

create table SSQRTZ_CRON_TRIGGERS (
TRIGGER_NAME varchar(80) not null,
TRIGGER_GROUP varchar(80) not null,
CRON_EXPRESSION varchar(80) not null,
TIME_ZONE_ID varchar(80),
primary key (TRIGGER_NAME, TRIGGER_GROUP)
);

create table SSQRTZ_PAUSED_TRIGGER_GRPS (
TRIGGER_GROUP  varchar(80) not null, 
primary key (TRIGGER_GROUP)
);

create table SSQRTZ_FIRED_TRIGGERS(
ENTRY_ID varchar(95) not null,
TRIGGER_NAME varchar(80) not null,
TRIGGER_GROUP varchar(80) not null,
IS_VOLATILE bit not null,
INSTANCE_NAME varchar(80) not null,
FIRED_TIME longint not null,
PRIORITY int not null,
STATE varchar(16) not null,
JOB_NAME varchar(80),
JOB_GROUP varchar(80),
IS_STATEFUL bit not null,
REQUESTS_RECOVERY bit not null,
primary key (ENTRY_ID)
);

create table SSQRTZ_SCHEDULER_STATE (
INSTANCE_NAME varchar(80) not null,
LAST_CHECKIN_TIME longint not null,
CHECKIN_INTERVAL longint not null,
primary key (INSTANCE_NAME)
);

create table SSQRTZ_LOCKS (
LOCK_NAME  varchar(40) not null, 
primary key (LOCK_NAME)
);

insert into SSQRTZ_LOCKS values('TRIGGER_ACCESS');
insert into SSQRTZ_LOCKS values('JOB_ACCESS');
insert into SSQRTZ_LOCKS values('CALENDAR_ACCESS');
insert into SSQRTZ_LOCKS values('STATE_ACCESS');

create table SSQRTZ_JOB_DETAILS (
JOB_NAME varchar(80) not null,
JOB_GROUP varchar(80) not null,
DESCRIPTION varchar(120),
JOB_CLASS_NAME varchar(128) not null,
IS_DURABLE bit not null,
IS_VOLATILE bit not null,
IS_STATEFUL bit not null,
REQUESTS_RECOVERY bit not null,
JOB_DATA blob,
primary key (JOB_NAME, JOB_GROUP)
);


create table SSQRTZ_JOB_LISTENERS (
JOB_NAME varchar(80) not null,
JOB_GROUP varchar(80) not null,
JOB_LISTENER varchar(80) not null,
primary key (JOB_NAME, JOB_GROUP, JOB_LISTENER)
);

create table SSQRTZ_SIMPLE_TRIGGERS (
TRIGGER_NAME varchar(80) not null,
TRIGGER_GROUP varchar(80) not null,
REPEAT_COUNT longint not null,
REPEAT_INTERVAL longint not null,
TIMES_TRIGGERED longint not null,
primary key (TRIGGER_NAME, TRIGGER_GROUP)
);

create table SSQRTZ_BLOB_TRIGGERS (
TRIGGER_NAME varchar(80) not null,
TRIGGER_GROUP varchar(80) not null,
BLOB_DATA blob,
primary key (TRIGGER_NAME, TRIGGER_GROUP)
);

create table SSQRTZ_TRIGGER_LISTENERS (
TRIGGER_NAME varchar(80) not null,
TRIGGER_GROUP varchar(80) not null,
TRIGGER_LISTENER varchar(80) not null,
primary key (TRIGGER_NAME, TRIGGER_GROUP, TRIGGER_LISTENER)
);

create table SSQRTZ_TRIGGERS (
TRIGGER_NAME varchar(80) not null,
TRIGGER_GROUP varchar(80) not null,
JOB_NAME varchar(80) not null,
JOB_GROUP varchar(80) not null,
IS_VOLATILE bit not null,
DESCRIPTION varchar(120),
NEXT_FIRE_TIME longint,
PREV_FIRE_TIME longint,
PRIORITY int,
JOB_DATA blob,
TRIGGER_STATE varchar(16) not null,
TRIGGER_TYPE varchar(8) not null,
START_TIME longint not null,
END_TIME longint,
CALENDAR_NAME varchar(80),
MISFIRE_INSTR integer,
primary key (TRIGGER_NAME, TRIGGER_GROUP)
);

alter table SSQRTZ_CRON_TRIGGERS
add constraint SSFK_cron_triggers_triggers foreign key (TRIGGER_NAME,TRIGGER_GROUP)
references SSQRTZ_TRIGGERS (TRIGGER_NAME,TRIGGER_GROUP);


alter table SSQRTZ_JOB_LISTENERS
add constraint SSFK_job_listeners_job_details foreign key (JOB_NAME,JOB_GROUP)
references SSQRTZ_JOB_DETAILS (JOB_NAME,JOB_GROUP);


alter table SSQRTZ_SIMPLE_TRIGGERS
add constraint SSFK_simple_triggers_triggers foreign key (TRIGGER_NAME,TRIGGER_GROUP)
references SSQRTZ_TRIGGERS (TRIGGER_NAME,TRIGGER_GROUP);


alter table SSQRTZ_TRIGGER_LISTENERS
add constraint SSFK_trigger_listeners_triggers foreign key (TRIGGER_NAME,TRIGGER_GROUP)
references SSQRTZ_TRIGGERS (TRIGGER_NAME,TRIGGER_GROUP);


alter table SSQRTZ_TRIGGERS
add constraint SSFK_triggers_job_details foreign key (JOB_NAME,JOB_GROUP)
references SSQRTZ_JOB_DETAILS (JOB_NAME,JOB_GROUP);


alter table SSQRTZ_BLOB_TRIGGERS
add constraint SSFK_blob_triggers_triggers foreign key (TRIGGER_NAME,TRIGGER_GROUP)
references SSQRTZ_TRIGGERS (TRIGGER_NAME,TRIGGER_GROUP);
