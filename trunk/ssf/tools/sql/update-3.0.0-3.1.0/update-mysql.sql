use sitescape;
ALTER TABLE SS_Events MODIFY dtStart datetime;
ALTER TABLE SS_Events add dtCalcStart datetime;
ALTER TABLE SS_Events add dtCalcEnd datetime;
