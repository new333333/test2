connect sitescape/sitescape;
ALTER TABLE SS_Events MODIFY dtStart timestamp;
ALTER TABLE SS_Events add dtCalcStart timestamp;
ALTER TABLE SS_Events add dtCalcEnd timestamp;
