function updateMenu_b9vcafr() {
  if ((parent.theMenu) && (parent.theMenu.amBusy == false))
  {
    var level0ID = parent.theMenu.currentSubRoot;
    var level1ID;
    var level2ID;
    var level3ID;
    var level4ID;
    level2ID = parent.theMenu.findEntry('/b9vcafr.html', 'url', 'right');
    if ((level2ID != -1) && (parent.theMenu.entry[level2ID].FirstChild < 0))
    {
      level3ID = parent.theMenu.addChild(level2ID, "Document", "Prerequisites", "../b9vchn5.html", "zHTML xD.0000.0000.0001.0001.", "Prerequisites");
      level3ID = parent.theMenu.addChild(level2ID, "Document", "Steps for Installing Novell Teaming", "../b9vchn6.html", "zHTML xD.0000.0000.0001.0002.", "Steps for Installing Novell Teaming");
      level3ID = parent.theMenu.addChild(level2ID, "Document", "Database Planning", "../b9vchn7.html", "zHTML xD.0000.0000.0001.0003.", "Database Planning");
      level3ID = parent.theMenu.addChild(level2ID, "Document", "File System Planning", "../b9vchn8.html", "zHTML xD.0000.0000.0001.0004.", "File System Planning");
      level3ID = parent.theMenu.addChild(level2ID, "Document", "Network Planning", "../bbn8hra.html", "zHTML xD.0000.0000.0001.0005.", "Network Planning");
      level3ID = parent.theMenu.addChild(level2ID, "Document", "Memory Guidelines", "../b9vchnf.html", "zHTML xD.0000.0000.0001.0006.", "Memory Guidelines");
      level3ID = parent.theMenu.addChild(level2ID, "Document", "Security Guidelines", "../bbn5klr.html", "zHTML xD.0000.0000.0001.0007.", "Security Guidelines");
      level3ID = parent.theMenu.addChild(level2ID, "Document", "Load-Balancing and Clustering Novell Teaming", "../bbnqb4c.html", "zHTML xD.0000.0000.0001.0008.", "Load-Balancing and Clustering Novell Teaming");
      level3ID = parent.theMenu.addChild(level2ID, "Document", "Running the Installer", "../installer.html", "zHTML xD.0000.0000.0001.0009.", "Running the Installer");
      level3ID = parent.theMenu.addChild(level2ID, "Document", "Installing a Standalone Lucene Index Server", "../bbijvt7.html", "zHTML xD.0000.0000.0001.0010.", "Installing a Standalone Lucene Index Server");
      level3ID = parent.theMenu.addChild(level2ID, "Document", "Starting and Stopping Novell Teaming", "../b9vchnb.html", "zHTML xD.0000.0000.0001.0011.", "Starting and Stopping Novell Teaming");
      level3ID = parent.theMenu.addChild(level2ID, "Document", "Novell Teaming Backup and Restore Procedures", "../bcghun2.html", "zHTML xD.0000.0000.0001.0012.", "Novell Teaming Backup and Restore Procedures");
      parent.theMenu.reload();
    }
  } else {
     setTimeout("updateMenu_b9vcafr();", 100);
  }
}

updateMenu_b9vcafr();
