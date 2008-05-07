# upgrade Quartz DB tables to be compatible with Quartz 1.6.0
# David LaPalomento

alter table SSQRTZ_TRIGGERS add priority integer null after prev_fire_time;
alter table SSQRTZ_TRIGGERS add JOB_DATA BLOB NULL after MISFIRE_INSTR;
# Default priority is 5 for Quartz 1.6.0 according to their wiki, but use only to set values for existing rows
alter table SSQRTZ_FIRED_TRIGGERS add PRIORITY INTEGER NOT NULL default 5 after FIRED_TIME;
alter table SSQRTZ_FIRED_TRIGGERS alter PRIORITY drop default;