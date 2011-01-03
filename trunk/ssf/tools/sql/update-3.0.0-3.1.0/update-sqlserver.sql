use sitescape;
ALTER TABLE SS_Events ALTER COLUMN dtStart datetime null;
ALTER TABLE SS_Events add dtCalcStart datetime null;
ALTER TABLE SS_Events add dtCalcEnd datetime null;
