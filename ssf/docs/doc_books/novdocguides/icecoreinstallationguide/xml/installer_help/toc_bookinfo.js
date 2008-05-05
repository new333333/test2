function updateMenu_bookinfo() {
  if ((parent.theMenu) && (parent.theMenu.amBusy == false))
  {
    var level0ID = parent.theMenu.currentSubRoot;
    var level1ID;
    var level2ID;
    var level3ID;
    var level4ID;
    level1ID = parent.theMenu.findEntry('/bookinfo.html', 'url', 'right');
    if (level1ID == -1)
    {
      level1ID = parent.theMenu.addChild(level0ID, "", "Novell Teaming 1.0.3 Installation Guide", "../bookinfo.html", "zFLDR xB.0000.0000.", "Novell Teaming 1.0.3 Installation Guide");
      level2ID = parent.theMenu.addChild(level1ID, "Folder", "Installing Novell Teaming", "../b9vcafr.html", "zFLDR xC.0000.0000.0001.", "Installing Novell Teaming");
      parent.theMenu.entry[level2ID].FirstChild = -2;
      parent.theMenu.entry[level2ID].onToggle = 'parent.theMenu.loadScript("../b9vcafr.html","../toc_b9vcafr.html", true)';
      level2ID = parent.theMenu.addChild(level1ID, "Document", "A Sample installer.xml File", "../bbevpqp.html", "zHTML xC.0000.0000.0002.", "A Sample installer.xml File");
      level2ID = parent.theMenu.addChild(level1ID, "Document", "Glossary", "../b9rtisc.html", "zHTML xC.0000.0000.0003.", "Glossary");
      level2ID = parent.theMenu.addChild(level1ID, "Document", "Legal Notices", "../legal.html", "zHTML xC.0000.0000.0004.", "Legal Notices");
      parent.theMenu.entry[level1ID].setSelected(true);
      parent.theMenu.reload();
    }
  } else {
     setTimeout("updateMenu_bookinfo();", 100);
  }
}

updateMenu_bookinfo();
