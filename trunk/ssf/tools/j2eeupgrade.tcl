
array set ::j2ee_Principals_class_MAP {
   id           {id int32}
   lockVersion  {lockVersion int32}
   type         {type "fixchar 1"}
   disabled     {disabled boolean}
   lcName       {lcName "varchar 82"}
   name         {name "varchar 82"}
   ntAccountInfo {ntAccountInfo clob}
   pwd          {pwd "varchar 64"}
   title        {title "varchar 1024"}
   emailAddress {emailAddress "varchar 256"}
   firstName    {firstName "varchar 64"}
   lastName     {lastName "varchar 64"}
   middleName   {middleName "varchar 64"}
   languageId {languageId "fixchar 2"}
   signature {signature "varchar 256"}
   creation_date {creation_date timestamp}
   creation_principal      {creation_principal int32}
   modification_date {modification_date timestamp}
   modification_principal     {modification_principal int32}
   description_text          {description_text clob}
   description_format       {description_format int32}
   homepage     {homepage "varchar 256"}
   organization {organization "varchar 256"}
   phone        {phone "varchar 128"}
   webPubDir    {webPubDir "varchar 256"}
   zoneName    {zoneName "varchar 100"}
   timeZoneId   {timeZoneId "varchar 10"}
   loginDate	{loginDate timestamp}
   preferredWorkspace {preferredWorkspace int32}
   defaultIdentity	{defaultIdentity boolean}
   reserved	{reserved boolean}
}

array set ::j2ee_Forum_class_MAP {
   id           {id int32}
   lockVersion {lockVersion int32}
   zoneName {zoneName "varchar 100"}
   type         {type "varchar 16"}
   name         {name "maxfname"}
   title        {title "varchar 1024"}
   creation_date {creation_date timestamp}
   creation_principal      {creation_principal int32}
   modification_date {modification_date timestamp}
   modification_principal     {modification_principal int32}
   owner_principal     {owner_principal int32}   
   owningWorkspace {owningWorkspace int32}
   topFolder  {topFolder int32}
   notify_contextLevel {notify_contextLevel int32}
   notify_summaryLines {notify_summaryLines int32}
   notify_teamOn   {notify_teamOn boolean}
   notify_lastNotification {notify_lastNotification timestamp}
   notify_event    {notify_event int32}
   notify_schedule {notify_schedule "varchar 256"}
   notify_email    {notify_email blob}
   defaultReplyDef {defaultReplyDef uuid}
   featureMask			{featureMask int32}
   functionMembershipInherited   {functionMembershipInherited boolean}
   parentFolder 		{parentFolder int32}
   displayStyle {displayStyle int32}
   folder_sortKey   {folder_sortKey "varchar 512"}
   folder_level		{folder_level int32}
   entryRoot_sortKey   {entryRoot_sortKey "varchar 512"}
   entryRoot_level		{entryRoot_level int32}   
   acl_inheritFromParent {acl_inheritFromParent boolean}
}

array set ::j2ee_Acls_class_MAP {
   id           			{id uuid}
   type						{type "varchar 16"}
   zoneName					{zoneName "varchar 128"}
   principal_type 			{principal_type int32}
   principal_name 			{principal_name "varchar 128"}
   resource_id 				{resource_id "varchar 1024"}
   resource_type			{resource_type int32}
   right_name				{right_name "varchar 64"}
   persistentPermission_id	{persistentPermission_id int32}
}

array set ::j2ee_Permissions_class_MAP {
   id           {id int32}
   type			{type "varchar 16"}
   lockVersion  {lockVersion int32}
   name 		{name "varchar 64"}
   title		{title "varchar 128"}
   resourceType	{resourceType int32"}
   zoneName		{zoneName "varchar 128"}
}

array set ::j2ee_FolderEntry_class_MAP {
   id           {id int32}
   lockVersion {lockVersion int32}
   title        {title "varchar 1024"}
   creation_date {creation_date timestamp}
   creation_principal      {creation_principal int32}
   modification_date {modification_date timestamp}
   modification_principal     {modification_principal int32}
   parentFolder {parentFolder int32}
   topEntry {topEntry int32}
   parentEntry {parentEntry int32}
   commandDef  {commandDef "varchar 32"}
   allowEdits   {allowEdits boolean}
   lastActivity {lastActivity timestamp}
   totalReplyCount   {totalReplyCount int32}
   reserved_principal  {reserved_principal int32}
   owningFolderSortKey {owningFolderSortKey "varchar 512"}
   description_text {description_text clob}
   description_format       {description_format int32}
   sendMail     {sendMail boolean}
   docContent   {docContent int32}
   wfp1_state {wfp1_state uuid}
   wfp1_principal {wfp1_principal int32}
   wfp1_date {wfp1_date timestamp}
   entry_level     {entry_level int32}
   entry_sortKey   {entry_sortKey "varchar 512"}
   nextDescendant {nextDescendant int32}
   replyCount {replyCount int32}
   acl_inheritFromParent {acl_inheritFromParent boolean}
}

array set ::j2ee_Attachments_class_MAP {
   id           {id uuid}
   lockVersion  {lockVersion int32}
   creation_date {creation_date timestamp}
   creation_principal       {creation_principal int32}
   modification_date {modification_date timestamp}
   modification_principal      {modification_principal int32}
   type         {type "fixchar 1"}
   folderEntry {folderEntry int32}
	principal 	{principal int32}
    ownerType 	{ownerType "varchar 16"}
    owner 		{owner int32}
    owningFolderSortKey {owningFolderSortKey "varchar 512"}
  name			{name "varchar 64"}
   fileName         {fileName "varchar 256"}
   fileLength         {fileLength int32}
   lastVersion  {lastVersion int32}
   versionNumber {versionNumber int32}
   versionName  {versionName "varchar 256"}
   active     {active boolean}
   title         {title "varchar 256"}
   bookmarkEntry {bookmarkEntry int32}
   parentAttachment {parentAttachment uuid}
}
array set ::j2ee_CustomAttributes_class_MAP {
   	id           {id uuid}
 	type 		{type "fixchar 1"}
    folderEntry {folderEntry int32}
	principal 	{principal int32}
    ownerType 	{ownerType "varchar 16"}
    owner 		{owner int32}
    owningFolderSortKey {owningFolderSortKey "varchar 512"}
    name 		{name "varchar 255"}
    stringValue {stringValue "varchar 4000"}
    description_text 	{description_text clob}
    description_format {description_format int32}
    longValue 	{longValue int32}
    dateValue 	{dateValue timestamp}
    serializedValue {serializedValue blob}
	xmlValue	{xmlValue blob}
    booleanValue {booleanValue boolean}
    valueType 	{valueType int32}
    parent 		{parent int32}
}
				
array set ::j2ee_Notification_class_MAP {
   id           {id uuid}
   lockVersion  {lockVersion int32}
   forum		{forum int32}
   notificationDef {notificationDef uuid32}
   style		{style int32}
   sendTo    {sendTo int32}
   emailAddress {emailAddress "varchar 256"}
   enabled      {enabled boolean}
}
#array set ::j2ee_Command_class_MAP {
#   id           {id uuid}
#   lockVersion  {lockVersion int32}
#   creation_date {creation_date timestamp}
#   creation_principal       {creation_principal int32}
#  modification_date {modification_date timestamp}
#   modification_principal      {modification_principal int32}
#   type         {type "fixchar 1"}
#   name			{name "varchar 64"}
#   entryType     {entryType int32}
#   retired       {retired boolean}
#   definition  {definition blob}
#   addName		{addName "varchar 64"}
#   showName		{showName "varchar 64"}
#   mailName		{mailName "varchar 64"}
#   workflowDef  {workflowDef uuid}
#   nextFieldId  {nextFieldId int32}
#   tag			{tag "varchar 16"}
#}

#array set ::j2ee_Workflow_class_MAP {
#   id           {id uuid}
#   lockVersion  {lockVersion int32}
#   creation_date {creation_date timestamp}
#   creation_principal       {creation_principal int32}
#   modification_date {modification_date timestamp}
#   modification_principal      {modification_principal int32}
#   name			{name "varchar 64"}
#   initialStateId     {initialStateId "varchar 32"}
#   definition  {definition blob}
#}

#array set ::j2ee_WorkflowState_class_MAP {
#   id           {id uuid}
#   lockVersion  {lockVersion int32}
#   name			{name "varchar 128"}
#   workflowDef {workflowDef uuid}
#   definition  {definition blob}
#}

set idCount 0
set startIdCount 0
set ::dialect [wimsql_info dialect]
if {$::dialect == "mssql"} {
    set l [lindex [wimsql "SELECT MAX(IDENTITYCOL) FROM SS_Principals;"] 0]
    set ::lastUserId [lindex $l 0]
    if {[isnull $::lastUserId]} {set ::lastUserId 0}
    set :l [lindex [wimsql "SELECT MAX(IDENTITYCOL) FROM SS_Forums;"] 0]
    set ::lastForumId [lindex $l 0]
    if {[isnull $::lastForumId]} {set ::lastForumId 0}
    set :l [lindex [wimsql "SELECT MAX(IDENTITYCOL) FROM SS_FolderEntries;"] 0]
    set ::lastDocshareId [lindex $l 0]
    if {[isnull $::lastDocshareId]} {set ::lastDocshareId 0}
} elseif {$dialect == "oracle"} {
    set startIdCount [lindex [wimsql "select hibernate_sequence.nextval from dual;"] 0]
    set idCount $::startIdCount
}

package require xml
package require dom
namespace eval ::mydom {
	dom createNodeCmd textNode textnode
    dom createNodeCmd cdataNode cdatanode
	dom createNodeCmd elementNode void
	dom createNodeCmd -returnNodeCmd elementNode object
}



proc newuuid {} {
    incr ::idCount
    return [format %032u $::idCount]

}

proc new_user_uuid {} {
    if {$::dialect != "mssql"} {
        return [incr ::idCount]
    } else {
        incr ::lastUserId
        return $::lastUserId
    }

}

proc new_forum_uuid {} {
    if {$::dialect != "mssql"} {
        return [incr ::idCount]
    } else {
        incr ::lastForumId
        return $::lastForumId
    }
}
proc new_folder_uuid {} {
    new_forum_uuid

}
proc new_docshare_uuid {} {
    if {$::dialect != "mssql"} {
        return [incr ::idCount]
    } else {
        incr ::lastDocshareId
        return $::lastDocshareId
    }
}
proc doClob {value} {
    if {$::dialect == "oracle"} {
        return "empty_clob()"
        if {![isnull $value] && [string bytelength $value] <= 4000} {
            return '[sql_quote_value $value]'
        }
    }
    if {![isnull $value]} {
        return [wimsql_rw clob $value]
    }
    return {}
}
proc doBlob {value} {
    if {$::dialect == "oracle"} {
        return "empty_blob()"
        if {![isnull $value] && [string bytelength $value] <= 4000} {
            return '[sql_quote_value $value]'
        }
    }
    if {![isnull $value]} {
        return [wimsql_rw blob $value]
    }
    return {}
}
#move from zone group membership to 
proc doGM {} {
    foreach GMS [wimsql "SELECT groupName,memberName FROM [wimsql_tableName "" _group m];"] {
        set group [lindex $GMS 0]
        set user [lindex $GMS 1]
      #  try {
            set gId [mapName $group]
       # } else {continue}
       # try {
            set uId [mapName $user]
        #} else {continue}
        wimsql_rw "INSERT INTO SS_PrincipalMembership (groupId,userId) VALUES ($gId,$uId);"

    }
    wimsql_rw commit
}

#create uuuid for users so we can store them in createdby/createdOn fields
#
proc mapName {name {defaultIt {1}}} {
    set name [string trim $name]
    if {![isnull $name]} {
        if {[info exists ::userIds($name)]} {
            uplevel {set UTYPE 3}
            return $::userIds($name)
        } elseif {[info exists ::groupIds($name)]} {
            uplevel {set UTYPE 2}
            return $::groupIds($name)
        }
    }
           
##how will this work out?
    if {$defaultIt} {
        uplevel {set UTYPE 2}
        return $::userIds(wf_admin)
    }
    error "No user found $name"
   

}
proc mapName2 {name {defaultIt {1}}} {
    set name [string trim $name]
    if {![isnull $name]} {
        if {[info exists ::userIds($name)]} {
            uplevel {set PRINCIPAL_TYPE 1}
            return $name
        } elseif {[info exists ::groupIds($name)]} {
            uplevel {set PRINCIPAL_TYPE 2}
            return $name
        }
    }
           
##how will this work out?
    if {$defaultIt} {
        uplevel {set PRINCIPAL_TYPE 1}
        return $defaultIt
    }
    error "No user found $name"
}
proc doUsers {userList} {
    set bunchIndex 0
	set bunchSize 500
    set map ::j2ee_Principals_class_MAP
    set map1 ::j2ee_Attachments_class_MAP
    array unset attrs
    array unset attrs1
	#setup default user
	set attrs(zoneName) "default"
	set attrs(name) ${::zoneName}.default
	set attrs(password) "password"
	set attrs(firstName) ""
	set attrs(lastName) ""
	set attrs(middleName) ""
	set attrs(title) ""
	set attrs(signature) ""
	set attrs(organization) ""
	set attrs(phone) ""
	set attrs(webPubDir) ""
	set attrs(type) "U"	
	set attrs(id) [new_user_uuid]
	set attrs(lockVersion) 1
	set attrs(disabled) 0
	set attrs(reserved) 1
	set attrs(defaultIdentity) 1
	set attrs(description_format) 2
    set attrs(creation_date) [date_time current] 
    set attrs(creation_principal) $::userIds(wf_admin)
    set attrs(modification_date) $attrs(creation_date)
    set attrs(modification_principal) $::userIds(wf_admin)

    set results [setupColVals $map attrs insert]
    set cmdList [lindex $results 1]
    set cmd [lindex $cmdList 0] 
    wimsql_rw "Insert into SS_Principals $cmd ;" [lindex $cmdList 1]
				
	set attrs(zoneName) $::zoneName
	set attrs(reserved) 0
	set attrs(defaultIdentity) 0
	
	unset attrs(id)
	user_property load -filter defaultSummit
	while {[llength [set bunchList [lrange $userList $bunchIndex [expr {$bunchIndex + $bunchSize - 1}]]]]} {
        ::profile::select -filter userName $bunchList -hint @

		foreach user $bunchList {      
            set attrs(disabled) [::profile::aval disabled $user]
            set attrs(lcName) [::profile::aval lcName $user]
            set attrs(ntAccountInfo) [doClob [::profile::aval ntAccountInfo $user] ]
          	if {[isnull $attrs(ntAccountInfo)]} {
          		unset attrs(ntAccountInfo)
          	}
            set attrs(pwd) $user          
            set attrs(name) $user
            set attrs(title) [::profile::aval title $user]
            set attrs(languageId) [::profile::aval nativeLanguage $user] 
            set attrs(emailAddress) [::profile::aval email $user]
            set attrs(firstName) [::profile::aval firstName $user]
 #don't set do layout gets automatically created??
 #           set attrs(lastLoginDate) [::profile::aval lastLogin $user]   
            set attrs(lastName) [::profile::aval lastName $user]     
            set attrs(middleName) [::profile::aval middleName $user]   
            set attrs(signature) [::profile::aval signature $user]
            set attrs(description_text) [doClob [::profile::aval comment $user]]
            if {[isnull $attrs(description_text)]} {
            	unset attrs(description_text)
            }
            set attrs(description_format) 2
			set attrs(preferredWorkspace) ""
			set summit [user_property get -for $user defaultSummit]
			if {![isnull $summit] && ![strequal $summit "__topsummit__"] && 
				[info exists ::forumIds($summit)]} {
				set attrs(preferredWorkspace) $::forumIds($summit)
			} 
#what about emailMedium/short??
            set attrs(homepage) [::profile::aval homepage $user]
            set attrs(organization) [::profile::aval org $user] 
            set attrs(phone) [::profile::aval phone $user]       
##is this needed
            set attrs(webPubDir) [::profile::aval webPubDir $user]    
            set attrs(creation_date) [::profile::aval createdOn $user]) 
            set attrs(creation_principal) [mapName [::profile::aval createdBy $user]]
            set attrs(modification_principal) [mapName [::profile::aval modifiedBy $user]]
            set attrs(modification_date) [::profile::aval modifiedOn $user]
 #           foreach a [array names attrs] {
 #               if {[isnull $attrs($a)]} {  
 #                   unset attrs($a)
 #               }
 #           }
            set results [setupColVals $map attrs update]
            set cmdList [lindex $results 1]
            set cmd [lindex $cmdList 0] 
            wimsql_rw "UPDATE SS_Principals $cmd  where id=$::userIds($user);" [lindex $cmdList 1]
            if {$::dialect == "oracle"} {
                set rowid [lindex [wimsql_rw "select rowid from SS_Principals where id=$::userIds($user);" ] 0]
                set data  [::profile::aval ntAccountInfo $user]
                if {![isnull $data]} {
                    wimsql_rw clob SS_Principals ntAccountInfo $rowid $data
                }
                set data [::profile::aval comment $user]
                if {![isnull $data]} {
                    wimsql_rw clob SS_Principals description_text $rowid $data
                }
            }
            # Store photo as attachment 
            set photos [::profile::aval photograph $user]
            if {![strequal $photos "default none"]} {
                set attrs1(id) [newuuid]
                set attrs1(lockVersion) 1
                set attrs1(name) "photograph"
                set attrs1(type) "F"
                set attrs1(creation_date) $attrs(creation_date)
                set attrs1(creation_principal) $attrs(creation_principal)
                set attrs1(modification_principal) $attrs(creation_principal)
                set attrs1(modification_date) $attrs(creation_date)
                set attrs1(fileNname) $photos
                set attrs1(fileLength) 0
                set attrs1(ownerType) "principal"
                set attrs1(owner) $::userIds($user)
                set attrs1(principal) $::userIds($user)
                set results [setupColVals $map1 attrs1 insert]
                set cmdList [lindex $results 1]
                set cmd [lindex $cmdList 0] 
                wimsql_rw "INSERT into SS_Attachments $cmd ;" [lindex $cmdList 1]
            }

            # Store resume as attachment 
            if {[file exists [avf_filejoin hidden_aca _user resumes $user]]} {
                set files [glob -nocomplain [avf_filejoin hidden_aca _user resumes $user/*]]
                set attrs1(id) [newuuid]
                set attrs1(lockVersion) 1
                set attrs1(name) "resume"
                set attrs1(type) "F"
                set attrs1(fileName) [lindex $files 0]
                set attrs1(creation_date) $attrs(creation_date)
                set attrs1(creation_principal) $attrs(creation_principal)
                set attrs1(modification_principal) $attrs(creation_principal)
                set attrs1(modification_date) $attrs(creation_date)
                try {
                	set attrs1(fileLength) [file size $attrs1(fileName)]
                } else {continue}
                set attrs1(ownerType) "principal"
                set attrs1(owner) $::userIds($user)           
                set attrs1(principal) $::userIds($user)
                set results [setupColVals $map1 attrs1 insert]
                set cmdList [lindex $results 1]
                set cmd [lindex $cmdList 0] 
                wimsql_rw "INSERT into SS_Attachments $cmd ;" [lindex $cmdList 1]
            }
        }

		::profile::aval_unload -type user 
	
		incr bunchIndex $bunchSize
    }
	user_property unload -filter defaultSummit
	wimsql_rw "update SS_Principals set emailAddress='[sql_quote_value test@${::zoneName}]' where name='wf_admin';"
    wimsql_rw commit
}

proc doGroups {groupList} {
    set bunchIndex 0
	set bunchSize 500
    set map ::j2ee_Principals_class_MAP
    array unset attrs
	set attrs(zoneName) $::zoneName
	set attrs(type) "G"
	set attrs(lockVersion) 1
	set attrs(disabled) 0
	set attrs(defaultIdentity) 0
 	set attrs(reserved) 0
	
	while {[llength [set bunchList [lrange $groupList $bunchIndex [expr {$bunchIndex + $bunchSize - 1}]]]]} {
        ::profile::select -type group -filter userName $bunchList -hint @
        foreach group $bunchList {      
            set attrs(id) [new_user_uuid]
            set attrs(lockVersion) 1
            #save id for latter mappings
            set ::groupIds($group) $attrs(id)
            set attrs(type) "G"
            set attrs(disabled) [::profile::aval -type group disabled $group]
            set attrs(lcName) [::profile::aval -type group lcName $group]
            set attrs(name) $group
            set attrs(title) [::profile::aval -type group title $group]
            set attrs(creation_date) [::profile::aval -type group createdOn $group]) 
            set attrs(creation_principal) [mapName [::profile::aval -type group createdBy $group] ]
            set attrs(modification_date) [::profile::aval -type group modifiedOn $group]
            set attrs(modification_principal) [mapName [::profile::aval -type group modifiedBy $group]]

            set attrs(description_text) [doClob ""]
            if {[isnull $attrs(description_text)]} {
				unset attrs(description_text)
            }
			set attrs(description_format) 2

#            foreach a [array names attrs] {
#                if {[isnull $attrs($a)]} {  
#                    unset attrs($a)
#                }
#            }
            set results [setupColVals $map attrs insert]
            set cmdList [lindex $results 1]
            set cmd [lindex $cmdList 0] 
            wimsql_rw "INSERT INTO SS_Principals $cmd;" [lindex $cmdList 1]
        }

		::profile::aval_unload -type group 
	
		incr bunchIndex $bunchSize
    }
    wimsql_rw commit
}
proc cleanup {} {
    #get List of zones
    wimsql_rw "delete from SS_FolderEntries;"
    wimsql_rw "update SS_Forums set topFolder=null;"

    wimsql_rw "update SS_Principals set preferredWorkspace=null;"
	wimsql_rw "delete from SS_Notifications;"
	wimsql_rw "delete from SS_WorkflowStates;"
	wimsql_rw "delete from SS_Workflows;"
    wimsql_rw "delete from SS_Forums;"
    wimsql_rw "delete from SS_Attachments;"
    wimsql_rw "delete from SS_PrincipalMembership;"
	wimsql_rw "delete from SS_RoleMembership;"
    wimsql_rw "delete from SS_Principals;"
    wimsql_rw commit
}
proc doAllZones {} {
    set zoneList [wimsql "select distinct(acaName) from table_map;"]
    foreach zone $zoneList {
        set zoneName [lindex $zone 0]
        doZone $zoneName
    }
}
proc doZone {zoneName {cName {liferay.com}}} {
    global Wgw_CurrentUser
	set ::zoneName $cName
    set path [file join $::Wgw_HiddenBaseDirectory $zoneName]
    if {![file isdirectory $path]} {return}
    set ::Wgw_CurrentACA $zoneName
    array unset ::Wgw_CurrentUser
    if {[Wgw_LoadUserProfile wf_admin ::Wgw_CurrentUser false] == -1} {
        puts "Cannot convert $zoneName. Missing wf_admin"
        return
    }
    puts "Converting zone $zoneName"
    Wgw_loadACAprofile $path
    array unset attrs
    set attrs(lockVersion) 1
    set attrs(name) $zoneName
    set attrs(buildId) [upgrade_mark get _build_id]
    set attrs(buildVersion) [upgrade_mark get _id]
    set attrs(patchLevel) [upgrade_mark get _patch_level]
    set attrs(buildDate) [clock format [clock scan [upgrade_mark get _build_date]] -format "%Y-%m-%d %T"]
    catch {set attrs(title) $::Wgw_EHallProp(eh.zonetitle)}

    array unset ::forumIds
    if {$::dialect == "mssql"} {
        wimsql_rw "SET IDENTITY_INSERT SS_Forums ON;"
        wimsql_rw commit
    }

    #get list of forums
    set forumList [lsort -dictionary [glob -nocomplain -type d [avf_filejoin hidden_base $::Wgw_CurrentACA *]]]
    #generate uuids for all forums and store so we can set owningworkspace ids as foreign keys
	#need id of preferred workspace to finish users
    foreach f $forumList {
        set forum [file tail $f]
		if { ![file exists [file join $f .webletlock]]} {
            continue
        }
		set class [wim property get -aca $zoneName -name $forum -meta class]
        if {[isnull $class]} {continue}
        
        set ::forumIds($forum) [new_forum_uuid]
        if {[strequal $class _admin] || [strequal $class summit]} {
            set type "WORKSPACE"
        } else {
            set type "BINDER"
        }
        wimsql_rw "INSERT INTO SS_Forums (id, lockVersion, owningWorkspace, name, type, featureMask, functionMembershipInherited, acl_inheritFromParent, zoneName) VALUES ($::forumIds($forum),1, $::forumIds(_admin), '$forum', '$type',0,0,0,'[sql_quote_value $::zoneName]');"
    }
    wimsql_rw "Update SS_Forums set owningWorkspace=null,name='[sql_quote_value $::zoneName]' where name='_admin';"
    wimsql_rw commit
    if {$::dialect == "mssql"} {
        wimsql_rw "SET IDENTITY_INSERT SS_Forums OFF;"
        wimsql_rw commit
    }
    
    #Convert Users
    set personList [::profile::select -type user]
    #need to insert these now to generate creation_principal/moderator IDs
    array unset ::userIds
    array unset ::groupIds
    if {$::dialect == "mssql"} {
        wimsql_rw "SET IDENTITY_INSERT SS_Principals ON;"
        wimsql_rw commit
    }

    foreach user $personList {      
        set ::userIds($user) [new_user_uuid]

        #need to save so foreign key constraings are met.
        wimsql_rw "INSERT INTO SS_Principals (id,lockVersion,type,zoneName) VALUES ($::userIds($user),1,'U','[sql_quote_value $::zoneName]');"
    }
    wimsql_rw commit
	doUsers $personList 

##Skipping old teams
    set groupList [::profile::select -type group -filter userType group]
	doGroups $groupList
    if {$::dialect == "mssql"} {
        wimsql_rw "SET IDENTITY_INSERT SS_Principals OFF;"
        wimsql_rw commit
    }

    #map group membership
    doGM


    if {[catch {

        foreach forum [array names ::forumIds] { 
			wim property load -aca $zoneName -name $forum
    		set class [wim property get -aca $zoneName -name $forum -meta class]
            if {[strequal $class _user]} {continue}
            if {[strequal $class _tasks]} {continue}
            array unset attrs
            puts "--Converting forum $forum"
            set attrs(creation_principal) [mapName [wim property get -aca $zoneName -name $forum owner]] 
            set attrs(modification_principal) $attrs(creation_principal)
            set attrs(title) [wim property get -aca $zoneName -name $forum -meta title]
          #should this be the teamId?? how is it different them creation_principal?
            set attrs(owner_principal) $attrs(creation_principal)
            set wsName [string trim [wim property get -aca $zoneName -name $forum parentSummit]]
            if {![isnull $wsName] && [info exists ::forumIds($wsName)] } {
                set attrs(owningWorkspace) $::forumIds($wsName)
            } elseif {![strequal $forum "_admin"]} {
            	set attrs(owningWorkspace) $::forumIds(_admin)
            }
#			array unset workflowIds
			#generate workflowIds cause referenced in commands
#			set w [wim property get -aca $zoneName -forum $forum workflowDataArray]
#			if {![isnull $w]} {
#				array set workflows $w
#				foreach wfp $workflows(processList) {
#					set ::workflowIds($wfp) [newuuid]
#			        wimsql_rw "INSERT INTO SS_Workflows (id, lockVersion) VALUES ('$::workflowIds($wfp)',1);"
#				}
#			}

#			array unset ::commandIds
#			array unset ::fieldIds
#			set cmdId [doCommands $zoneName $forum]
#			array unset ::stateIds
#			doWorkflows $zoneName $forum
#			if {![isnull $cmdId]} {
#				set attrs(defaultReplyCommandDef) $cmdId
#			}
		    set eRoot [B1036 $::forumIds($forum) 15]
		    set fRoot $eRoot
		    append fRoot "00001"
            switch -- $class {
                docshare - 
                docattr - 
                chatrooms - 
                contact - 
                _vfolders - 
                help_desk -
                moddisc - 
                onlydisc - 
                privdisc  - 
                _chatrooms - 
                attr
                {
                    set attrs(type) FOLDER
                    set RESOURCE_TYPE 3         

                    set PRINCIPAL_TYPE 3
                    set top [wim property get -aca $zoneName -name $forum topFolderId]
                    doFolders $forum $top 1 $fRoot {}
			
					aval_unload -name $forum                          
                }
                _admin -
                _summit97 -
                summit {
                    set attrs(type) WORKSPACE
                    set RESOURCE_TYPE 2
                }
                 default {
                    set attrs(type) FOLDER  
					set attrs(displayStyle) 1
					set attrs(entryRoot_level) 0
					set attrs(entryRoot_sortKey) $eRoot
					set attrs(folder_level) 1
					set attrs(folderRoot_sortKey) $fRoot
                    set RESOURCE_TYPE 3      
                    wimsql_rw "Insert into SS_FolderCounts (lockVersion,id,nextFolder,nextEntry) values
                    			(1,$::forumIds($forum), 1,1);"   
                }
            }
			doNotifications $zoneName $forum attrs

            set map ::j2ee_Forum_class_MAP
            set results [setupColVals $map attrs update]
            set cmdList [lindex $results 1]
            set cmd [lindex $cmdList 0] 
            wimsql_rw "UPDATE SS_Forums $cmd where id=$::forumIds($forum);" [lindex $cmdList 1]
			if {[info exists attrs(owningWorkspace)]} {
				wimsql_rw "Update SS_Forums set owningWorkspace=$attrs(owningWorkspace) where topFolder=$::forumIds($forum);"
			}
			doAcls $zoneName $forum $RESOURCE_TYPE
			wim property unload -aca $zoneName -name $forum
    
        }
    } eMsg]} {
        
        error "$eMsg $::errorInfo"
    }
   if {$::dialect == "oracle"} {
        set val [expr $::idCount - $::startIdCount]
        if {$val > 0} {
            wimsql_rw "alter sequence hibernate_sequence increment by $val;"
            wimsql_rw "select hibernate_sequence.nextval from dual;"
            wimsql_rw "alter sequence hibernate_sequence increment by 1;"
        }
    }
	wimsql_rw "delete from SS_Forums where type='BINDER';"
	#need to set default column values
    if {($::dialect == "frontbase") || ($::dialect == "frontbase-external")} {
		wimsql_rw "update SS_Forums set notify_teamOn=B'0' where notify_teamOn is null;"
		wimsql_rw "update SS_Principals set reserved=B'1' where name='wf_admin' or name='avf_admin';"
	} else {
		wimsql_rw "update SS_Forums set notify_teamOn=0 where notify_teamOn is null;"
		wimsql_rw "update SS_Principals set reserved=1 where name='wf_admin' or name='avf_admin';"
	}
	wimsql_rw "update SS_Forums set notify_contextLevel=2 where notify_contextLevel is null;"
	wimsql_rw "update SS_Forums set notify_summaryLines=0 where notify_summaryLines is null;"
    wimsql_rw commit
}

proc doFolders {forum folder level hKey parentID} {
    #get folder list
    set subRoots [aval -name $forum subRoots $folder]
    set folderBranch [lindex $subRoots 0]
    set entryBranch [lindex $subRoots 1]
    set subFolders [entry_list -name $forum -childof $folderBranch -hint *]

    array unset attrs
    set attrs(displayStyle) 1
	set attrs(entryRoot_level) 0
	set attrs(folder_sortKey) $hKey
	set attrs(folder_level) $level
	set attrs(featureMask) 0
    set map ::j2ee_Forum_class_MAP
    if {$level == 1} {
	    set attrs(entryRoot_sortKey) [B1036 $::forumIds($forum) 15]
    
	    set results [setupColVals $map attrs update]
        set cmdList [lindex $results 1]
        set cmd [lindex $cmdList 0] 
        wimsql_rw "UPDATE SS_Forums $cmd where id=$::forumIds($forum);" [lindex $cmdList 1]
		set attrs(id) $::forumIds($forum)
   } else {
	    set attrs(id) [new_folder_uuid]
 	    set attrs(entryRoot_sortKey) [B1036 $attrs(id) 15]
    	set attrs(lockVersion) 1
	    set ::folderIds($folder) $attrs(id)
	    set attrs(creation_principal) [mapName [aval  -name $forum createdBy $folder]] 
	    set attrs(creation_date) [aval -name $forum createdOn $folder]
	    set attrs(modification_principal) [mapName [aval -name $forum modifiedBy $folder]] 
	    set attrs(modification_date) [aval -name $forum modifiedOn $folder]
	    set attrs(title) [aval -name $forum title $folder]
        set attrs(parentFolder) $parentID
	    set attrs(topFolder) $::forumIds($forum)
		set attrs(functionMembershipInherited) 1
		set attrs(acl_inheritFromParent) 1
		set attrs(type) "FOLDER"
		set attrs(zoneName) $::zoneName
		set attrs(name) [namify -targetlength 128 ${forum}_$attrs(title)]
	    set results [setupColVals $map attrs insert]
    	set cmdList [lindex $results 1]
	    set cmd [lindex $cmdList 0] 
    	if {$::dialect == "mssql"} {
	        wimsql_rw "SET IDENTITY_INSERT SS_Forums ON;"
    	}
	    wimsql_rw "INSERT INTO SS_Forums $cmd ;" [lindex $cmdList 1]
	    if {$::dialect == "mssql"} {
	        wimsql_rw "SET IDENTITY_INSERT SS_Forums OFF;"
	    }
	}
	if {$::dialect == "mssql"} {
	    wimsql_rw "SET IDENTITY_INSERT SS_FolderEntries ON;"
	}	
    wimsql_rw "Insert into SS_FolderCounts (lockVersion,id,nextFolder,nextEntry) values
           			(1,$attrs(id), [expr [llength $subFolders]+1],[expr [aval -name $forum topLeafNumber $entryBranch]+1]);"  
     doEntries $forum $entryBranch $attrs(entryRoot_sortKey) $hKey $attrs(id) {} {}
    if {$::dialect == "mssql"} {
        wimsql_rw "SET IDENTITY_INSERT SS_FolderEntries OFF;"
    }
	set descd 1
	incr level
    foreach f $subFolders {
        if {[catch {doFolders $forum [lindex [aval -name $forum docContent $f] 1] $level $hKey[B1036 $descd 4] $attrs(id)} eMsg]} {
            puts "Error on folder $f $eMsg"
        }
		incr descd
        wimsql_rw commit
    }
    aval_unload -name $forum $subFolders
}
proc doEntries {forum root eRoot folderSortKey parentFolderID topDocShareID parentDocShareID} {

    set map ::j2ee_FolderEntry_class_MAP
    set eList [entry_list -name $forum -childof $root]
    set index 0
    while {$index < [llength $eList]} {
        set subList [lrange $eList $index [expr $index+999]]
        incr index 1000
        aval_load -name $forum -abstract $subList
        foreach entry $subList {
            array unset attrs
            set attrs(id) [new_docshare_uuid]
            set entryId $attrs(id)
            set attrs(lockVersion) 1
            set attrs(parentFolder) $parentFolderID
            if {[isnull $topDocShareID]} {
                set attrs(topEntry) $entryId
            } else {
                set attrs(topEntry) $topDocShareID
            }
            set attrs(parentEntry) $parentDocShareID
            set attrs(creation_principal) [mapName [aval  -name $forum createdBy $entry]] 
            set attrs(creation_date) [aval -name $forum createdOn $entry]
            set attrs(modification_principal) [mapName [aval -name $forum modifiedBy $entry]] 
            set attrs(modification_date) [aval -name $forum modifiedOn $entry]
            set attrs(title) [aval -name $forum title $entry]
            set attrs(replyCount) [aval -name $forum docLeafCount $entry]
            set attrs(nextDescendant) [expr 1+[aval -name $forum topLeafNumber $entry]]
            set attrs(entry_level) [expr [aval -name $forum docLevel $entry] -1]
            set key [aval -name $forum hKey $entry]
            set attrs(entry_sortKey) "${eRoot}[string range $key 8 end]"
            set attrs(allowEdits) [aval -name $forum allowEdits $entry]
            set attrs(lastActivity) [aval -name $forum lastActivity $entry]
            set attrs(totalReplyCount) [aval -name $forum replyCount $entry]
            set attrs(reserved_principal) [mapName [aval -name $forum reserveDoc $entry]]
            set attrs(sendMail) [aval -name $forum sendMail $entry]
            set attrs(owningFolderSortKey) $folderSortKey
            set attrs(acl_inheritFromParent) 1
			set abstract [read_abstract  -name $forum $entry]
            if {![isnull $abstract]} {set attrs(description_text) [doClob $abstract]}
            
            set attrs(description_format) 2
			set wfp [aval -name $forum workflowWfp $entry]
			if {![isnull $wfp] && [info exists ::workflowIds($wfp)]}  {
				set state [aval -name $forum workflowState $entry]
				if {![isnull $state] && [info exists ::stateIds(${wfp}.${state})]} {
					set attrs(wfp1_state) $::stateIds(${wfp}.${state})
				}
				set attrs(wfp1_date) [aval -name $forum workflowModifiedOn $entry]
			}

			set cmd  [aval -name $forum documentType $entry]
			if {![isnull $cmd] && [info exists ::commandIds($cmd)]} {
				set attrs(commandDef) $::commandIds($cmd)
			}

            set content [aval -name $forum docContent $entry]
            switch -- [lindex $content 0] {
                text/x-wgw-see-abstract {set attrs(docContent) 1}
                multipart/x-wgw-fileset {set attrs(docContent) 2}
                none/x-is-url {set attrs(docContent) 3}
                webDirFile {set attrs(docContent) 4}
                default {
                    puts "Unknown docContent $entry: $content"
                    set attrs(docContent) 1
                }
            }
            set results [setupColVals $map attrs insert]
            set cmdList [lindex $results 1]
            set cmd [lindex $cmdList 0] 
            wimsql_rw "INSERT INTO SS_FolderEntries $cmd ;" [lindex $cmdList 1]
            if {($::dialect == "oracle") && ![isnull $abstract]} {
                set rowid [lindex [wimsql_rw "select rowid from SS_FolderEntries where id=$entryId;" ] 0]
                wimsql_rw clob SS_FolderEntries description_text $rowid $abstract
			}
			doDocCustomAttributes $forum $entry $entryId  $folderSortKey
            set savedName {}
            set upLoad [aval -name $forum uploadFileInfo $entry]
            try {unset topAttachment}
            if {![isnull $upLoad]} {
                array unset attaches
                set attaches(id) [newuuid]
                set topAttachment $attaches(id)
                set attaches(lockVersion) 1
                set attaches(owner) $entryId
                set attaches(ownerType) "doc"
                set attaches(folderEntry) $entryId
				set attaches(owningFolderSortKey) $folderSortKey
                set attaches(creation_date) [lindex $upLoad 3]
                set attaches(creation_principal) [mapName [lindex $upLoad 2]]
                set attaches(modification_date) [lindex $upLoad 3]
                set attaches(modification_principal) $attaches(creation_principal)
                set attaches(fileName) [lindex $upLoad 0]
                set savedName [lindex $upLoad 0]
                set attaches(fileLength) [lindex $upLoad 1]
                set attaches(type) "F"
                set attaches(name) "primary"
                set attaches(lastVersion) [aval -name $forum lastUsedVersion $entry]
                set results [setupColVals j2ee_Attachments_class_MAP attaches insert]
                set cmdList [lindex $results 1]
                set cmd [lindex $cmdList 0] 
                wimsql_rw "INSERT INTO SS_Attachments $cmd ;" [lindex $cmdList 1]
            } elseif {$attrs(docContent) == 3} {
                array unset attaches
                set attaches(id) [newuuid]
                set attaches(lockVersion) 1
                set attaches(owner) $entryId
                set attaches(ownerType) "doc"
                set attaches(folderEntry) $entryId
				set attaches(owningFolderSortKey) $folderSortKey
                set attaches(creation_date) $attrs(creation_date)
                set attaches(creation_principal) $attrs(creation_principal)
                set attaches(modification_date) $attrs(creation_date)
                set attaches(modification_principal) $attrs(creation_principal)
                set attaches(fileName) [lindex $content 1]
                set savedName [lindex $content 1]
                set attaches(fileLength) 0
                set attaches(type) "U"
                set attaches(name) "primary"
                set attaches(lastVersion) 0
                set results [setupColVals j2ee_Attachments_class_MAP attaches insert]
                set cmdList [lindex $results 1]
                set cmd [lindex $cmdList 0] 
                wimsql_rw "INSERT INTO SS_Attachments $cmd ;" [lindex $cmdList 1]
            } elseif {$attrs(docContent) == 4} {
                array unset attaches
                set attaches(id) [newuuid]
                set attaches(lockVersion) 1
                set attaches(owner) $entryId
                set attaches(ownerType) "doc"
                set attaches(folderEntry) $entryId
				set attaches(owningFolderSortKey) $folderSortKey
                set attaches(name) "primary"
                set attaches(creation_date) $attrs(creation_date)
                set attaches(creation_principal) $attrs(creation_principal)
                set attaches(modification_date) $attrs(creation_date)
                set attaches(modification_principal) $attrs(creation_principal)
                set attaches(fileName) [lindex $content 1]/[lindex $content 2]
                set attaches(fileLength) 0
                set attaches(type) "F"
                set attaches(lastVersion) 0
                set results [setupColVals j2ee_Attachments_class_MAP attaches insert]
                set cmdList [lindex $results 1]
                set cmd [lindex $cmdList 0] 
                wimsql_rw "INSERT INTO SS_Attachments $cmd ;" [lindex $cmdList 1]
            }
            set versionFiles [aval -name $forum versionFiles $entry]
            if {![isnull $versionFiles] && [info exists topAttachment]} {
                array unset attaches
                set attaches(lockVersion) 1
                set attaches(owner) $entryId
                set attaches(ownerType) "doc"
                set attaches(folderEntry) $entryId
				set attaches(owningFolderSortKey) $folderSortKey
                set attaches(parentAttachment) $topAttachment
                set attaches(type) "V"
                set attaches(lastVersion) 0
                
                set vfl0 [lindex $versionFiles 0]
                #Look to see if this is an old style list
                #  The old style list ends with a comma followed by a 19 char date
                #  to check for an old style list, look for a comma 20 characters from the end
                if {![isnull $vfl0] && \
                    [string index $vfl0 [expr [string length $vfl0] - 20]] == ","} {
                  #This is the old style comma separated list
                    #Convert the list to the new format
                    foreach vf $versionFiles {
                        set vf [split $vf ","]
                        set fni [expr [llength $vf] - 4]
                        set fbi [expr [llength $vf] - 3]
                        set fcbi [expr [llength $vf] - 2]
                        set fcoi [expr [llength $vf] - 1]
                        #set fileName [lindex $vf 0]
                        #set versionNumber [lindex $vf 1]
                        #set originalFileName [join [lrange $vf 2 $fni] ","]
                        #set fileBytes [lindex $vf $fbi]
                        #set fileCreatedBy [lindex $vf $fcbi]
                        #set fileCreatedOnGmt [lindex $vf $fcoi]
                        set attaches(id) [newuuid]
                        set attaches(creation_date) [lindex $vf $fcoi]
                        set attaches(creation_principal) [mapName [lindex $vf $fcbi] ]
                        set attaches(modification_date) [lindex $vf $fcoi]
                        set attaches(modification_principal) $attaches(creation_principal)
                        set attaches(fileName) [join [lrange $vf 2 $fni] ","]
                        set attaches(fileLength) [lindex $vf $fbi]
                        set attaches(versionName) [lindex $vf 0]
                        set attaches(versionNumber) [lindex $vf 1]
                        set results [setupColVals j2ee_Attachments_class_MAP attaches insert]
                        set cmdList [lindex $results 1]
                        set cmd [lindex $cmdList 0] 
                        wimsql_rw "INSERT INTO SS_Attachments $cmd ;" [lindex $cmdList 1]
                    }
                } else {
                    foreach vf $versionFiles {
                        set attaches(id) [newuuid]
                        set attaches(creation_date) [lindex $vf 5]
                        set attaches(creation_principal) [mapName [lindex $vf 4] ]
                        set attaches(modification_date) [lindex $vf 5]
                        set attaches(modification_principal) $attaches(creation_principal)
                        set attaches(fileName) [lindex $vf 2]
                        set attaches(fileLength) [lindex $vf 3]
                        set attaches(versionName) [lindex $vf 0]
                        set attaches(versionNumber) [lindex $vf 1]
                        set results [setupColVals ::j2ee_Attachments_class_MAP attaches insert]
                        set cmdList [lindex $results 1]
                        set cmd [lindex $cmdList 0] 
                        wimsql_rw "INSERT INTO SS_Attachments $cmd ;" [lindex $cmdList 1]

                    }
                }
            }
		    if {[wim property get -name $forum uploadedFileDir] == "hidden"} {
				set base [avf_filejoin hidden_aca $forum]
			} else {
				set base [avf_filejoin visible_aca $forum]
			}
            set aFiles [lsort -dictionary [aval -name $forum attachmentFiles $entry]]
            if {![isnull $aFiles]} {
				set versionedList {}
				set parentFileName {}
				set currentList {}
				foreach af $aFiles {
                    if {[strequal $af $savedName]} {continue}
					#See if this is a version of the previous file
					if {[regexp {^(.*)_v([0-9]*)\.([^.]*)$} $af xxx fileRoot fileVersion fileExt]} {
						set fileName $af
	                    #This is a version file, see if it matches the parent
						while {[regexp {^(.*)_v([0-9]*)\.([^.]*)$} $fileName xxx fileRoot fileVersion fileExt]} {
	                        set fileName $fileRoot.$fileExt
						}
						if {[strequal $parentFileName $fileName]} {
							lappend currentList [list $af $fileName $fileVersion]
						} else {
							lappend versionedList $currentList 
							set currentList [list $af]
							set parentFileName $af
						}
					} else {
						lappend versionedList $currentList
						set parentFileName $af
						set currentList [list $af]
					}
				}
				lappend versionedList $currentList
						
                foreach af $versionedList {
					if {[llength $af] == 0} {continue}
	                array unset attaches
		            set attaches(lockVersion) 1
			        set attaches(ownerType) "doc"
				    set attaches(owner) $entryId
	                set attaches(folderEntry) $entryId
					set attaches(owningFolderSortKey) $folderSortKey
					set attaches(lastVersion) [expr [llength $af] -1]
                    set attaches(id) [newuuid]
                    set attaches(creation_date) $attrs(creation_date)
                    set attaches(creation_principal) $attrs(creation_principal)
                    set attaches(modification_date) $attrs(creation_date)
                    set attaches(modification_principal) $attrs(creation_principal)
                    set attaches(fileName) [lindex $af 0]
	                set attaches(type) "F"
					try {
						set attaches(fileLength) [file size [file join $base $entry/[lindex $af 0]]]
					} else {
						continue
					}
                    set results [setupColVals ::j2ee_Attachments_class_MAP attaches insert]
                    set cmdList [lindex $results 1]
                    set cmd [lindex $cmdList 0] 
                    wimsql_rw "INSERT INTO SS_Attachments $cmd ;" [lindex $cmdList 1]
					unset attaches(lastVersion)
					set attaches(parentAttachment) $attaches(id);

					for {set i 1} {$i < [llength $af]} {incr i} {
						set ver [lindex $af $i]
						#This is a version
	                    set attaches(id) [newuuid]
						set attaches(versionNumber) [lindex $ver 2]
						set attaches(type) "V"
						set attaches(versionName) [lindex $ver 0]
						set attaches(fileName) [lindex $ver 1]						
						try {
							set attaches(fileLength) [file size [file join $base $entry/[lindex $ver 1]]]
						} else {continue}
		                set results [setupColVals ::j2ee_Attachments_class_MAP attaches insert]
			            set cmdList [lindex $results 1]
					    set cmd [lindex $cmdList 0] 
				        wimsql_rw "INSERT INTO SS_Attachments $cmd ;" [lindex $cmdList 1]
					}
                }
            }

            set aFiles [aval -name $forum attachmentUrls $entry]
            if {![isnull $aFiles]} {
                array unset attaches
                set attaches(lockVersion) 1
                set attaches(ownerType) "doc"
                set attaches(owner) $entryId
                set attaches(folderEntry) $entryId
    			set attaches(owningFolderSortKey) $folderSortKey
                set attaches(type) "U"
                foreach af $aFiles {
                    if {[strequal $af $savedName]} {continue}
                    set attaches(id) [newuuid]
                    set attaches(creation_date) $attrs(creation_date)
                    set attaches(creation_principal) $attrs(creation_principal)
                    set attaches(modification_date) $attrs(creation_date)
                    set attaches(modification_principal) $attrs(creation_principal)
                    set attaches(title) $af
                     set results [setupColVals ::j2ee_Attachments_class_MAP attaches insert]
                    set cmdList [lindex $results 1]
                    set cmd [lindex $cmdList 0] 
                    wimsql_rw "INSERT INTO SS_Attachments $cmd ;" [lindex $cmdList 1]
                }
            }

            doEntries $forum $entry $eRoot $folderSortKey $parentFolderID $attrs(topEntry) $entryId

        }
        wimsql_rw commit
        aval_unload -name $forum $subList
    }
}
proc doDocCustomAttributes {forum entry entryId folderSortKey} {
	set attrs(type) "A"
	set attrs(ownerType) "doc"
	set attrs(owner) $entryId
	set attrs(folderEntry) $entryId
	set attrs(owningFolderSortKey) $folderSortKey
	set val [aval -name $forum expiration $entry]
	if {![isnull $val]} {
		set attrs(id) [newuuid]
		set attrs(valueType) 3
		set attrs(dateValue) $val
		set attrs(name) expiration
        set results [setupColVals ::j2ee_CustomAttributes_class_MAP attrs insert]
        set cmdList [lindex $results 1]
        set cmd [lindex $cmdList 0] 
        wimsql_rw "INSERT INTO SS_CustomAttributes $cmd ;" [lindex $cmdList 1]
        unset attrs(dateValue)
	}
	set val [aval -name $forum closedOn $entry]
	if {![isnull $val]} {
		set attrs(id) [newuuid]
		set attrs(valueType) 6
		set attrs(booleanValue) $val
		set attrs(name) closedOn
        set results [setupColVals ::j2ee_CustomAttributes_class_MAP attrs insert]
        set cmdList [lindex $results 1]
        set cmd [lindex $cmdList 0] 
        wimsql_rw "INSERT INTO SS_CustomAttributes $cmd ;" [lindex $cmdList 1]
        unset attrs(booleanValue)
	}
	set val [aval -name $forum rTypes $entry]
	if {![isnull $val]} {
		set attrs(id) [newuuid]
		set attrs(valueType) 8
		set xml {<?xml version="1.0" encoding="UTF-8"?>}
		append xml "<rTypes>"
		foreach v $val {
			append xml "<rType name=\"[lindex $v 0]\">[lindex $v 1]</rType>\n"
		}
		append xml "</rTypes>"

		set attrs(xmlValue) $xml
		set attrs(name) rTypes
        set results [setupColVals ::j2ee_CustomAttributes_class_MAP attrs insert]
        set cmdList [lindex $results 1]
        set cmd [lindex $cmdList 0] 
        wimsql_rw "INSERT INTO SS_CustomAttributes $cmd ;" [lindex $cmdList 1]
        unset attrs(xmlValue)
	}
	#build set of category selections	
 	set val [aval -name $forum category $entry]
	if {![isnull $val]} {
		set val [lsort -unique $val]
		set attrs(id) [newuuid]
		set attrs(valueType) 5
		set attrs(name) category
        set results [setupColVals ::j2ee_CustomAttributes_class_MAP attrs insert]
        set cmdList [lindex $results 1]
        set cmd [lindex $cmdList 0] 
        wimsql_rw "INSERT INTO SS_CustomAttributes $cmd ;" [lindex $cmdList 1]
        array unset multi
		array set multi [array get attrs]
		set multi(type) "L"
		set multi(valueType) 1
		set multi(parent) $attrs(id)
		foreach v $val {
			if {![isnull $v]} {
				set multi(id) [newuuid]
				set multi(stringValue) $v
		        set results [setupColVals ::j2ee_CustomAttributes_class_MAP multi insert]
	    	    set cmdList [lindex $results 1]
	        	set cmd [lindex $cmdList 0] 
	        	wimsql_rw "INSERT INTO SS_CustomAttributes $cmd ;" [lindex $cmdList 1]
	        }
		}
 	}	
 	set val [aval -name $forum docProps $entry]
	if {![isnull $val]} {
		set attrs(id) [newuuid]
		set attrs(valueType) 1
		set attrs(stringValue) $val
		set attrs(name)  docProps
        set results [setupColVals ::j2ee_CustomAttributes_class_MAP attrs insert]
        set cmdList [lindex $results 1]
        set cmd [lindex $cmdList 0] 
        wimsql_rw "INSERT INTO SS_CustomAttributes $cmd ;" [lindex $cmdList 1]
        unset attrs(stringValue)
	}
	array unset kvps
 	array set kvps [aval -name $forum attributes $entry]
	foreach n [array names kvps] {
		set val $kvps($n)
		if {![isnull $val]} {		
			set val [lsort -unique $val]
			set attrs(id) [newuuid]
			set attrs(name)  $n
			if {[llength $val] == 1} {
				set attrs(valueType) 1
				set attrs(stringValue) $val
	        	set results [setupColVals ::j2ee_CustomAttributes_class_MAP attrs insert]
    	    	set cmdList [lindex $results 1]
        		set cmd [lindex $cmdList 0] 
        		wimsql_rw "INSERT INTO SS_CustomAttributes $cmd ;" [lindex $cmdList 1]
        		unset attrs(stringValue)
        	} else {
				set attrs(valueType) 5
		        set results [setupColVals ::j2ee_CustomAttributes_class_MAP attrs insert]
        		set cmdList [lindex $results 1]
		        set cmd [lindex $cmdList 0] 
		        wimsql_rw "INSERT INTO SS_CustomAttributes $cmd ;" [lindex $cmdList 1]
		        array unset multi
				array set multi [array get attrs]
				set multi(type) "L"
				set multi(valueType) 1
				set multi(parent) $attrs(id)
				foreach v $val {
					if {![isnull $v]} {
						set multi(id) [newuuid]
						set multi(stringValue) $v
				        set results [setupColVals ::j2ee_CustomAttributes_class_MAP multi insert]
			    	    set cmdList [lindex $results 1]
			        	set cmd [lindex $cmdList 0] 
		    	    	wimsql_rw "INSERT INTO SS_CustomAttributes $cmd ;" [lindex $cmdList 1]
		    	    }
				}
		 	}	
        }
	}	
	array unset kvps
    array set kvps [aval -name $forum keyValuePairs $entry]
 	foreach n [array names kvps] {
		set val $kvps($n)
		if {![isnull $val]} {
			set attrs(id) [newuuid]
			set attrs(valueType) 1
			set attrs(description_text) $val
			set attrs(description_format) 2
			set attrs(name)  $n
	        set results [setupColVals ::j2ee_CustomAttributes_class_MAP attrs insert]
	        set cmdList [lindex $results 1]
	        set cmd [lindex $cmdList 0] 
	        wimsql_rw "INSERT INTO SS_CustomAttributes $cmd ;" [lindex $cmdList 1]
		}
	}	
    try {unset attrs(description_text)}
    try {unset attrs(description_format)}
}

proc doNotifications {zoneName forumName ats} {
	upvar $ats attrs
	set notifyEnabled [wim property get -aca $zoneName -name $forumName notifyEnabled]
	if {$notifyEnabled == "title"} {
		set attrs(notify_contextLevel) 1
	} elseif {$notifyEnabled == "summary"} {
		set attrs(notify_contextLevel) 3
	} else {
		set attrs(notify_contextLevel) 2
	}

	set	dayString ""
	foreach day [wim property get -aca $zoneName -name $forumName notifyDays] {
		switch -- $day {
			"sun" {lappend dayString 1}
			"mon" {lappend dayString 2}
			"tue" {lappend dayString 3}
			"wed" {lappend dayString 4}
			"thu" {lappend dayString 5}
			"fri" {lappend dayString 6}
			"sat" {lappend dayString 7}
		}
	}
	set dayString [join [lsort $dayString] ,]
	#NOT SURE HOW to move these
	#default to every 4 hours
	set timeString ""
	foreach time [wim property get -aca $zoneName -name $forumName notifyTimes] {
		if {[isnull $time]} continue
		set spec [split $time ":"]
		set hour [lindex $spec 0]
		if {[isnull $hour]} continue
		lappend timeString $hour
		set minute [lindex $spec 1]
	}
	if {![isnull $timeString]} {
		#seconds minutes hours dayOfMonth months days year"
		set attrs(notify_schedule) "0 $minute [join $timeString ","] ? * $dayString" 
	}
    set attrs(notify_summaryLines) [wim property get -aca $zoneName -name $forumName notifyWordCount]
    set notifySendToTeam [wim property get -aca $zoneName -name $forumName notifySendToTeam]
	if {$notifySendToTeam == "on"} {
		set attrs(notify_teamOn) 1
	} else {
		set attrs(notify_teamOn) 0
	} 

    set attrs(notify_lastNotification) [wim property get -aca $zoneName -name $forumName notifyDateOfLastMailing]
	
	set forumId $::forumIds($forumName)
	foreach group [wim property get -aca $zoneName -name $forumName notifyDefaultGroups] {
		try {
			set id $::groupIds($group)
			wimsql_rw "INSERT INTO SS_Notifications (id,lockVersion,binder,type,sendTo) values ('[newuuid]',1,$forumId,'N',$id);"
		}
	}

	foreach user [wim property get -aca $zoneName -name $forumName notifyDefaultUsers] {
		try {
			set id $::userIds($user)
			wimsql_rw "INSERT INTO SS_Notifications (id,lockVersion,binder,type,sendTo) values ('[newuuid]',1,$forumId,'N',$id);"
		}
	}

	set nattrs(lockVersion) 1
	set nattrs(binder) $forumId
	set nattrs(type) "U"
	set nattrs(style) 1
	set nattrs(disabled) 0
	foreach user [wim property get -aca $zoneName -name $forumName notifyEnabledUsers] {
		try {
			set nattrs(id) [newuuid]
			set nattrs(sendTo) $::userIds($user)
            set results [setupColVals ::j2ee_Notifications_class_MAP natts insert]
            set cmdList [lindex $results 1]
            set cmd [lindex $cmdList 0] 
			wimsql_rw "INSERT INTO SS_Notifications $cmd ;" [lindex $cmdList 1]
		}
	}

	set nattrs(style) 2
	foreach user [wim property get -aca $zoneName -name $forumName notifyEnabledUsersIndiv] {
		try {
			set nattrs(id) [newuuid]
			set nattrs(sendTo) $::userIds($user)
            set results [setupColVals ::j2ee_Notifications_class_MAP natts insert]
            set cmdList [lindex $results 1]
            set cmd [lindex $cmdList 0] 
			wimsql_rw "INSERT INTO SS_Notifications $cmd ;" [lindex $cmdList 1]
		}
	}

	set nattrs(disabled) 1
	unset nattrs(style) 
	#users explicitly overriding default lists to stop mail
	foreach user [wim property get -aca $zoneName -name $forumName notifyDisabledUsers] {
		try {
			set nattrs(id) [newuuid]
			set nattrs(sendTo) $::userIds($user)
            set results [setupColVals ::j2ee_Notifications_class_MAP natts insert]
            set cmdList [lindex $results 1]
            set cmd [lindex $cmdList 0] 
			wimsql_rw "INSERT INTO SS_Notifications $cmd ;" [lindex $cmdList 1]
		}
	}

    #Now, add in the names from the address list
    set mailAdrFile [avf_filejoin hidden_base $::Wgw_CurrentACA $forumName mail_notification_addresses.txt]
    if {[file exists $mailAdrFile]} {
		set mailAdrText [contents_of_file $mailAdrFile]
		set addrs {}
		foreach adr [split $mailAdrText "\n"] {
			set adr [string trim $adr]
			if {![isnull $adr] && [regexp {@} $adr]} {
				append addrs ${adr},
			}	
		}
		set addrs [string trim $addrs ","]
		if {![isnull $addrs]} {
			set attrs(notify_email) $addrs
		}
	}


}
proc doAcls {zoneName forum rscType} {
  return
    array set acls [wim property get -aca $zoneName -name $forum -filter _acl_g_*]
    set aclA(type) "RIGHT"
    set aclA(zoneName) $zoneName
    set aclA(resource_type) $rscType
    set aclA(resource_id) $forum
    set map ::j2ee_Acls_class_MAP
    foreach a [array names acls] {
    	set oldRightName [string range $a 7 end]
    	set oldRightRep ""
    	append oldRightRep "$oldRightName.$rscType"
    	#puts "---$oldRightRep---"
    	set rightName [toNewRightName $oldRightRep]
    	if {$rightName == ""} {
    		append rightName "<<$oldRightName>>"
    	}
    	#puts "---$rightName---"
    	set aclA(right_name) $rightName
        foreach {anon allowBasic allowHosts denyBasic denyHosts} $acls($a) break
        if {$anon} {
            try {
    			set aclA(principal_name) [mapName2 "anonymous"]    			
    			set aclA(principal_type) $PRINCIPAL_TYPE
                set aclA(id) [newuuid]
                set results [setupColVals $map aclA insert]
                set cmdList [lindex $results 1]
                set cmd [lindex $cmdList 0] 
                wimsql_rw "INSERT into SS_Acls $cmd;" [lindex $cmdList 1]
            }
        }
        foreach n $allowBasic {
            if {[catch {set aclA(principal_name) [mapName2 $n "anonymous"]}]} {continue}
    		set aclA(principal_type) $PRINCIPAL_TYPE
            set aclA(id) [newuuid]
            set results [setupColVals $map aclA  insert]
            set cmdList [lindex $results 1]
            set cmd [lindex $cmdList 0] 
            wimsql_rw "INSERT into SS_Acls $cmd;" [lindex $cmdList 1]
        }
    }
	
}

proc toNewRightName {oldRightRep} {
	switch -- $oldRightRep {
		"create_team_workspace.2" {return "createTeam"}
		"sendmailall.2" {return "sendMail"}
		"create_workspace .2" {return "create"}
		"help_desk_moderator.2" {return ""}
		"addatt.2" {return ""}
		"view.2" {return "view"}
		"read.2" {return ""}
		"delete.2" {return ""}
		"moderate.2" {return ""}
		"import.2" {return ""}
		"emailreply.2" {return ""}
		"modifyFolder.2" {return ""}
		"access.2" {return ""}
		"moderator.2" {return ""}
		"chat.2" {return ""}
		"deleteFolder.2" {return ""}
		"chat_moderator.2" {return ""}
		"emailcreate.2" {return ""}
		"report.2" {return ""}
		"sendmail.2" {return ""}
		"global_admin.2" {return ""}
		"createFolder.2" {return ""}
		"participate.2" {return "participate"}
		"contact_moderator.2" {return ""}
		"reply.2" {return ""}
		"modify.2" {return ""}
		"create.2" {return ""}
		"summit_admin.2" {return ""}

		"sendmail.3" {return "sendMail"}
		"resourceadmin.3" {return ""}
		"sendmailall.3" {return "sendMailAll"}
		"summit_admin.3" {return ""}
		"create_workspace.3" {return ""}
		"moderate.3" {return ""}
		"update.3" {return ""}
		"create.3" {return "createEntry"}
		"forumadmin.3" {return ""}
		"muse_private.3" {return ""}
		"emailreply.3" {return "addViaEmail"}
		"contact_moderator.3" {return ""}
		"modify.3" {return "modifyEntry"}
		"listteams.3" {return ""}
		"delete.3" {return "deleteEntry"}
		"import.3" {return ""}
		"deleteFolder.3" {return "deleteFolder"}
		"access.3" {return ""}
		"chat_moderator.3" {return ""}
		"createFolder.3" {return "createFolder"}
		"view.3" {return ""}
		"nonmember.3" {return ""}
		"chat.3" {return ""}
		"reply.3" {return "addReply"}
		"report.3" {return ""}
		"modifyFolder.3" {return "modifyFolder"}
		"read.3" {return "read"}
		"addatt.3" {return ""}
		"help_desk_moderator.3" {return ""}
		"muse_public.3" {return ""}
		"moderator.3" {return ""}
		"emailcreate.3" {return "createViaEmail"}
		
		"browse.4" {return ""}
		"delete.4" {return "delete"}
		"schedule.4" {return ""}
		"createFolder.4" {return ""}
		"deleteFolder.4" {return ""}
		"sendmail.4" {return "sendMail"}
		"access.4" {return ""}
		"moddel.4" {return ""}
		"modify.4" {return "modify"}
		"addatt.4" {return ""}
		"reply.4" {return ""}
		"chat_moderator.4" {return ""}
		"modifyFolder.4" {return ""}
		"create.4" {return "add"}
		"report.4" {return ""}
		"emailcreate.4" {return "createViaEmail"}
		"contact_moderator.4" {return ""}
		"moderate.4" {return ""}
		"chat.4" {return ""}
		"read.4" {return "read"}
		"import.4" {return ""}
		"emailreply.4" {return ""}		
	}
}

proc doWorkFlowNotification {notifyToken type notifyArray} {
	array set notify $notifyArray
 
 	$notifyToken setAttribute type $type
	if {[info exists notify(creator)] && ($notify(creator) == "yes")} {
		$notifyToken appendFromList [list creatorEnabled {} [list  [list #text true]]]
		if {[info exists notify(creatorSendDirective)] && ($notify(creatorSendDirective) == "yes")} {
			$notifyToken appendFromList [list creatorNotified {} [list  [list #text true]]]
		}
	}
	if {[info exists notify(appendTitle)] && ($notify(appendTitle) == "yes")} {
		$notifyToken appendFromList [list appendTitle {} [list  [list #text true]]]
	}

	if {[info exists notify(appendAbstract)] && ($notify(appendAbstract) == "yes")} {
		$notifyToken appendFromList [list appendDescription {} [list  [list #text true]]]
	}

	if {[info exists notify(attachments)] && ($notify(attachments) == "yes")} {
		$notifyToken appendFromList [list sendAttachments {} [list  [list #text true]]]
	}

	if {[info exists notify(all)] && ($notify(all) == "yes")} {
		$notifyToken appendFromList [list registeredUsers {} [list  [list #text true]]]
	}
	if {[info exists notify(subj)] && ![isnull $notify(subj)]} {
		$notifyToken appendFromList [list subjectLine {} [list  [list #text $notify(subj)]]]
	}
	if {[info exists notify(body)] && ![isnull $notify(body)]} {
		$notifyToken appendFromList [list body {} [list  [list #text $notify(body)]]]
	}
	if {[info exists notify(uslTypes)]} {
		foreach ut $notify(uslTypes) {
			if {$ut == "_ask"} {
				$notifyToken appendFromList [list promptForUserList {} [list  [list #text true]]]
			} else {
				set t [split $ut "."]
				#make sure command exists
				if {[info exists ::fieldIds($ut)]} {
					$notifyToken appendFromList [list commandFieldRef {commandId $::commandIds([lndex $t 0]) fieldId $::fieldIds($ut)} {}]
				}
			}
		}
	}
	if {[info exists notify(selectedUsers]} {
		foreach us $notify(selectedUsers) {
			if {[info exists $::userIds($us)]} {
				$notifyToken appendFromList [list principalRef {principalId $::userIds($us)} {}]
			} elseif {[info exists $::groupIds($us)]} {
				$notifyToken appendFromList [list principalRef {principalId $::groupIds($us)} {}]
			}
		}
	}
}
proc doState {docToken state wfp fromArray} {
	upvar $fromArray workflows
	
	set stateToken [$docToken createElement workflowState]
	if {[info exists workflows(query.${wfp}.${state})]} {
		$stateToken appendFromList [list query {} [list [list #text $workflows(query.${wfp}.${state})]]]
		if {[info exists workflows(queries.${wfp}.${state})]} {
			foreach r $workflows(queries.${wfp}.${state}) {
				$stateToken appendFromList [list response {} [list [list #text $r]]]
			}
		}
	}
	if {[info exists workflows(notify.${wfp}.enter.${state})]} {
		set notifyToken [$docToken createElement notificationDef]
		$stateToken appendChild $notifyToken
		doWorkFlowNotification $notifyToken 1 $workflows(notify.${wfp}.enter.${state})
	}
	if {[info exists workflows(notify.${wfp}.exit.${state})]} {
		set notifyToken [$docToken createElement notificationDef]
		$stateToken appendChild $notifyToken
		doWorkFlowNotification $notifyToken 2 $workflows(notify.${wfp}.exit.${state})
	}

	if {[info exists workflows(transition.${wfp}.$state)]} {
		try {unset timeout}
		try {unset timeoutDate}
		foreach te $workflows(transition.${wfp}.${state}) {
			if {[info exists ::stateIds(${wfp}.[lindex $te 1])]} {
				set transToken [$docToken createElement transitionDef]
				$stateToken appendChild $transToken
				$transToken appendFromList [list stateRef [list stateId $::stateIds(${wfp}.[lindex $te 1])] {}]
				foreach d [lindex $te 0] {
					switch -- [lindex $d 0] {
						manual {
							$transToken appendFromList [list onManualByGroup1 {} [list [list #text true]]]
						}
						manual2 { 
							$transToken appendFromList  [list onManualByGroup2 {} [list [list #text true]]]
						}
						reply  {
							$transToken appendFromList  [list onReply {} [list [list #text true]]]
						}
						everyone {
							$transToken appendFromList  [list onReplyAll {} [list [list #text true]]]
						}
						timeout {
							$transToken appendFromList [list inactiveDays {} [list [list #text [lindex $d 1]]]]
						}
						timeoutDate {
							#make sure command exists
							set t [split [lindex $d 1] "."]
							if {[info exists ::fieldIds([lindex $d 1])]} {
								$transToken appendFromList [list beforeDays {} [list [list #text [lindex $d 2]]]]
								$transToken appendFromList [list commandFieldRef [list fieldId $::fieldIds([lindex $d 1]) commandId $::commandIds([lindex $t 0])] {}]
							}
						}
						search -
						searchFound -
						searchNotFound {
							if {[info exists ::conditionIds([lindex $d 1])]} {
								$transToken appendFromList [list conditionRef [list conditionId [lindex $d 1]] {}]
							}
						}
						default {puts  "unknown condition [lindex $d 0]"}

					}
				}
			}
		}
	}
	return $stateToken

}
proc doSearchQuery {docToken qual} {
	set searchToken [$docToken createElement searchQuery]
	if {[lindex $qual 0] == "content"} {
		$searchToken setAttribute type 1
	} elseif {[lindex $qual 0] == "unseen"} {
		$searchToken setAttribute type 2
	} elseif {[lindex $qual 0] == "date"} {
		$searchToken setAttribute type 3
	} else {
		error "Unknown search type"
	}
	set val [lindex $qual 1]
	if {![isnull $val]} {
		$searchToken appendFromList [list searchDate {} [list [list #text $val]]]
	}
	set val [lindex $qual 2]
	if {![isnull $val]} {
		$searchToken appendFromList [list searchText {} [list [list #text $val]]]
	}
	set val [lindex $qual 3]
	set val [split $val ',']
	if {[llength $val] > 0} {
		set fieldToken [$docToken createElement searchField]
		$fieldToken setAttribute name creation_principal
		$searchToken appendChild $fieldToken
		foreach v $val {
			if {![isnull $val]} {
				$fieldToken appendFromList [list fieldValue {} [list [list #text $v]]]
			}
		}
	}
	set val [lindex $qual 4]
	set userV [split [lindex $val 0] ',']
	set val [concat $userV [lrange $val 1 end]]
	if {[llength $val] > 0} {
		set fieldToken [$docToken createElement searchField]
		$fieldToken setAttribute name keywords
		$searchToken appendChild $fieldToken
		foreach v $val {
			if {![isnull $val]} {
				$fieldToken appendFromList [list fieldValue {} [list [list #text $v]]]
			}
		}
	}
	set val [lindex $qual 7]
	if {![isnull $val]} {
		$searchToken appendFromList [list createdAfterDate {} [list [list #text $val]]]
	}

	set val [lindex $qual 8]
	if {![isnull $val]} {
		$searchToken appendFromList [list createdBeforeDate {} [list [list #text $val]]]
	}

	set val [lindex $qual 9]
	set val [split $val ',']
	if {![isnull [lindex $val 0]]} {
		$searchToken setAttribute maxResults $val
	} else {
		$searchToken setAttribute maxResults 0
	}

	if {![isnull [lindex $val 1]]} {
		$searchToken setAttribute startPosition $val
	} else {
		$searchToken setAttribute startPosition 0
	}
	#custom attributes
	set val [lindex $qual 10]


	set val [lindex $qual 11]
	if {![isnull $val]} {
		$searchToken setAttribute sort 2
	} else {
		$searchToken setAttribute sort 1
	}

	array set options [lindex $qual 12]
	if {[info exists options(useAdvancedSearchOperators)] && ($options(useAdvancedSearchOperators) == 1)} {
		$searchToken appendFromList [list advanced {} [list [list #text true]]]
	}
	array unset options(useAdvancedSearchOperators)
	foreach name [array names options] {
		if {![isnull $options($name)]} {
			$searchToken appendFromList [list option [list name $name] [list [list #text $options($name)]]]
		}
	}
	return $searchToken
}
proc doWorkflows {zoneName forum} {
	set w [wim property get -aca $zoneName -forum $forum workflowDataArray]
	if {[isnull $w]} {return}
	array set workflows $w
	
	foreach wfp $workflows(processList) {
		array unset attrs 
		set attrs(name) $workflows(name.$wfp)
	    set docToken [dom createDocument workflowElements]
		set rootToken [$docToken documentElement]

		foreach state $workflows(statesList.$wfp) {
			set id [newuuid]
			set ::stateIds($wfp.$state) $id
			wimsql_rw "INSERT into SS_WorkflowStates (id,lockVersion,workflowDef,name) values ('$id',1,'$::workflowIds($wfp)','[sql_quote_value $state]');"
		}
		set state $workflows(initialState.$wfp)
		if {[info exists ::stateIds(${wfp}.${state})]} {
			set attrs(initialStateId) $::stateIds($wfp.$state)
		}
		#used by stateDef
		array unset ::conditionIds
		set condId 1
		if {[info exists workflows(decisionsList.$wfp)]} {
			foreach cond $workflows(decisionsList.$wfp) {
				set condToken [$docToken createElement conditionDef]
				$rootToken appendChild $condToken
				$condToken setAttribute name $cond conditionId $condId
				set ::conditionIds($cond) $condId
				incr condId
				
				#need to break the query out if it exists
				if {[info exists workflows(decision.${wfp}.$cond)]} {
					if {![info exists workflows(decision_type.${wfp}.$cond)] || ($workflows(decision_type.${wfp}.$cond) != "searchNotFound")} {
						$condToken appendFromList [list searcyQueryResult {satisfiedWhenFound true} {}]
						$condToken appendChild [doSearchQuery $docToken $workflows(decision.${wfp}.$cond)]
					} else {
						$condToken appendFromList [list searcyQueryResult {satisfiedWhenFound false} {}]
						$condToken appendChild [doSearchQuery $docToken $workflows(decision.${wfp}.$cond)]
					}
				}
				if {[info exists workflows(decision_qual.${wfp}.$cond)]} {
					array unset conds
					array set conds $workflows(decision_qual.${wfp}.$cond)
					if {[info exists conds(states)]} {
						foreach s $conds(states) {
							if {[info exists ::stateIds(${wfp}.$s)]} {
								set crit [$docToken createElement criteria]
								$condToken appendChild $crit
								$crit setAttribute stateId $::stateIds(${wfp}.$s)
								if {[info exists conds(query.$s)]} {
									$crit appendFromList [list response {} [list [list #text $conds(query.$s)]]]
									if {[info exists conds(r.$s)]} {
										foreach u $conds(r.$s) {
											if {[info exists ::userIds($u)]} {
												$crit appendFromList [list principalRef {principalId $::userIds($u)} {}]
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		set notifySearch [array names workflows notify_*.${wfp}]
		if {[llength $notifySearch] != 0} {
			set notifyToken [$docToken createElement principalSelector]
			$rootToken appendChild $notifyToken
			if {[info exists workflows(notify_userSearchText.$wfp)]} {
				$notifyToken appendFromList [list namePattern {} [list [list #text $workflows(notify_userSearchText.$wfp)]]]
			}
			if {[info exists workflows(notify_userList.$wfp)]} {
				foreach u $workflows(notify_userList.$wfp) {
					if {[info exists ::userIds($u)]} {
						$notifyToken appendFromList [list principalRef [list principalId $::userIds($u)] {}]
					}
				}
			}
			if {[info exists workflows(notify_groupList.$wfp)]} {

				foreach u $workflows(notify_groupList.$wfp) {
					if {[info exists ::groupIds($u)]} {
						$notifyToken appendFromList [list principalRef [list principalId $::groupIds($u)] {}]
					}
				}
			}
			if {[info exists workflows(notify_teamList.$wfp)]} {
				foreach u $workflows(notify_teamList.$wfp) {
					if {[info exists ::groupIds($u)]} {
						$notifyToken appendFromList [list principalRef [list principalId $::groupIds($u)]  {}]
					}
				}
			}
		}

		#states need the conditionid to continue
		foreach state $workflows(statesList.$wfp) {
			set stateToken [doState $docToken $state $wfp workflows]
			set definition {<?xml version="1.0" encoding="UTF-8"?>}
			append definition "\n[$stateToken asXML -indent 2]"
	        if {$::dialect == "oracle"} {
		        set rowid [lindex [wimsql_rw "select rowid from SS_WorkflowStates where id='$::$::stateIds(${wfp}.$state)';" ] 0]
			    wimsql_rw blob WorkflowStates definition $rowid $definition 
			} else {
				set stateA(definition) [doBlob $definition]
				set results [setupColVals ::j2ee_WorkflowState_class_MAP stateA update]
				set cmdList [lindex $results 1]
				set cmd [lindex $cmdList 0] 
				wimsql_rw "update SS_WorkflowStates $cmd where id='$::stateIds(${wfp}.$state)';" [lindex $cmdList 1]
			}
		}

		set definition {<?xml version="1.0" encoding="UTF-8"?>}
		append definition "\n[$rootToken asXML -indent 2]"
		set attrs(definition) [doBlob $definition]
        set results [setupColVals ::j2ee_Workflow_class_MAP attrs update]
        set cmdList [lindex $results 1]
        set cmd [lindex $cmdList 0] 
        wimsql_rw "UPDATE SS_Workflows $cmd where id='$::workflowIds($wfp)';" [lindex $cmdList 1]
        if {$::dialect == "oracle"} {
            set rowid [lindex [wimsql_rw "select rowid from SS_Workflows where id='$::workflowIds($wfp)';" ] 0]
            wimsql_rw blob Workflows definition $rowid $definition 
        }

	}
}

proc doCommands {zoneName forum} {
	set c [wim property get -aca $zoneName -forum $forum customCommands]
	set e [wim property get -aca $zoneName -forum $forum enabledForumCommands]

    set defaultReplyCommand [wim property get -aca $zoneName -name $forum defaultReplyCommand]
	set defaultReplyId {}

	array set commands $c
	array unset attrs
	set attrs(type) "B"
	set attrs(retired) 0
	set attrs(nextFieldId) 1
	set attrs(lockVersion) 1
	set attrs(entryType) 1
    set map ::j2ee_Command_class_MAP
	foreach n [list addDoc addDisc addPoll addUrl] {
		if {[lsearch $e $n] >= 0} {
			if {[info exists commands(wfp.$n)] && [info exists ::workflowIds($commands(wfp.$n))]} {
				set attrs(id) [newuuid]
				set attrs(name) $::builtInCommands(${n}.name)
				set attrs(tag) $n
				set attrs(workflowDef) $::workflowIds($commands(wfp.$n))
				set results [setupColVals $map attrs insert]
				set cmdList [lindex $results 1]
				set cmd [lindex $cmdList 0] 
				wimsql_rw "INSERT INTO SS_Commands $cmd ;" [lindex $cmdList 1]
#				wimsql_rw "INSERT into SS_CommandMap (forum,commandDef) values ($::forumIds($forum),'$attrs(id)');"
			} else {
			#no custom workflow - just add
#				wimsql_rw "INSERT into SS_CommandMap (forum,commandDef) values ($::forumIds($forum),'$::builtInCommands($n)');"
			}
		}
	}
	if {[isnull $c]} return {}
	foreach cmd $commands(cmdList) {
		set fieldId 1
		array unset attrs
		set attrs(id) [newuuid]
		set ::commandIds($cmd) $attrs(id)
		set attrs(lockVersion) 1
		set attrs(name) $commands(name.$cmd)
		if {[strequal $attrs(name) $defaultReplyCommand]} {
			set defaultReplyId $attrs(id)
		}
		if {[info exists commands(entryType.$cmd)] && [strequal $commands(entryType.$cmd) "reply"]} {
			set attrs(entryType) 2
		} else {
			set attrs(entryType) 1
		}
		if {[info exists commands(retired.$cmd)]} {
			set attrs(retired) $commands(retired.$cmd)
		} else {
			set attrs(retired) 0
		}
		if {[info exists commands(wfp.${cmd})]} {
			try {set attrs(workflowDefId) $::workflowIds($commands(wfp.$cmd))}
		}
	    set docToken [dom createDocument commandElements]
		set rootToken [$docToken documentElement]

		array unset dataElements
		if {[info exists commands(type.$cmd)] && [strequal $commands(type.$cmd) "template"]} {
			set attrs(type) "T"
			foreach {t v} $commands(templates.$cmd) {
				switch -- $t {
					"cmdForm" {set attrs(addName) $v}
					"cmdMail" {set attrs(mailName) $v}
					"cmdView" {set attrs(showName) $v}
				}
			}
				

	        try {array set dataElements $commands(dataElements.$cmd)}
			#Check the input data for validity
			foreach ele [array names dataElements] {
			    array unset eleArray
				array set eleArray $dataElements($ele)
				switch $eleArray(type) {
					text {set type 1}
					user {set type 2}
					radio {set type 3}
					select {set type 4}
					checkbox {set type 5}
					number {set type 6}
					date {set type 7}
					dateWidget {set type 8}
					file {set type 9}
					attachments {set type 10}
					keywords {set type 11}
					default {continue}
				}
				set fieldToken [$docToken createElement commandField]
				$rootToken appendChild $fieldToken
				$fieldToken setAttribute fieldId $fieldId type $type name $ele 
				set ::fieldIds(${cmd}.${ele}) $fieldId
				incr fieldId
				if {[info exists eleArray(search)] && ($eleArray(search) == "yes")} {
					$fieldToken appendFromList [list searchable {} [list [list #text true]]]
				} else {
					$fieldToken appendFromList [list searchable {} [list [list #text true]]]
				}
		
				if {[info exists eleArray(caption)]} {
					$fieldToken appendFromList [list caption {} [list [list #text $eleArray(caption)]]]
				}
				if {[info exists eleArray(items)] && ![isnull $eleArray(items)]} {
					set optionsToken [$docToken createElement options]
					$fieldToken appendChild $optionsToken
					foreach it $eleArray(items) {
						$optionsToken appendFromList [list option {} [list [list #text $it]]]
					}
				}
			}
		} else {
			set attrs(type) "C"

	        try {array set dataElements $commands(cmd.$cmd)}
			#Check the input data for validity
			for {set i 0} {$i<[llength [array names dataElements]]} {incr i} {
			    array unset eleArray
				array set eleArray $dataElements($i)
				set input 0
				switch -- $eleArray(type) {
					"description" {set type 1; set input 1; set dataElementType 1; set eleArray(name) description}
					"title" {set type 2; set input 1; set dataElementType 1; set eleArray(name) title}
					"text" {set type 3; set input 1; set dataElementType 1}
					"textarea" {set type 4; set input 1; set dataElementType 1}
					"selectbox" {set type 10; set input 1; set dataElementType 4}
					"checkbox" {set type 11; set input 1; set dataElementType 5}
					"radio" {set type 12; set input 1; set dataElementType 3}
					"userlist" {set type 13; set input 1; set dataElementType 2}
					"attachments" {set type 14; set input 1; set dataElementType 10; set eleArray(name) fileAttachments}
					"file" {set type 15; set input 1; set dataElementType 9}
					"dateField" {set type 16; set input 1; set dataElementType 7}
					"keywords" {set type 17; set input 1; set dataElementType 11; set eleArray(name) categories}
					"banner" {set type 51}
					"toolbar" {set type 52}
					"navigation" {set type 53}
					"signature" {set type 54}
					"label" {set type 55}
					"html" {set type 56}
					"sendMail" {set type 57}
					"replies" {set type 58}
					"showattachments" {set type 59}
					"versions" {set type 60}
					"reserved" {set type 61}
					"customTop" {set type 62}
					"customBottom" {set type 63}
					"trailer" {set type 64}
					"workflow" {set type 65}
					"allowEdits" {set type 66}
					"image" {set type 67}
					"tableStart" {set type 80}
					"tableEnd" {set type 81}
					"tableRow" {set type 82}
					"tableElement" {set type 83}
				    "tableHeader" {set type 84}
					
					"optionaldata" {set type 100}
					default {puts "Type not found $eleArray(type)"; continue}    
				}
				
				set elementToken [$docToken createElement commandFormElement]
				$rootToken appendChild $elementToken
				$elementToken setAttribute fieldId $fieldId type $type position $i
				incr fieldId
				if {[info exists eleArray(name)] && ![isnull $eleArray(name)]} {
					$elementToken appendFromList [list name {} [list [list #text $eleArray(name)]]]
				}							
				if {[info exists eleArray(items)] && ![isnull $eleArray(items)]} {
					set optionsToken [$docToken createElement options]
					$elementToken appendChild $optionsToken
					foreach it $eleArray(items) {
						$optionsToken appendFromList [list option {} [list [list #text $it]]]
					}
				}
				if {[info exists eleArray(caption)]} {
					$elementToken appendFromList [list caption {} [list [list #text $eleArray(caption)]]]
				}
				set attrToken [$docToken createElement attributes]
				$elementToken appendChild $attrToken

				if {[info exists eleArray(image)]} {
					$attrToken appendFromList [list image {} [list [list #text $eleArray(image)]]]
				}
				if {[info exists eleArray(format)]} {
					set f [lindex $eleArray(format) 0]
					if {![isnull $f]} {
						$attrToken appendFromList [list format {} [list [list #text $f]]]
					}
					set f [lindex $eleArray(format) 1]
					if {![isnull $f]} {
						$attrToken appendFromList [list punctuation {} [list [list #text $f]]]
					}

					foreach {old new} [list size size cols columns rows rows] {
						if {[info exists eleArray($old)] && ![isnull $eleArray($old)]} {
							$attrToken appendFromList [list $new {} [list [list #text $eleArray($old)]]]
						}
					}

					if {[info exists eleArray(singleSelect)]} {
						if {$eleArray(singleSelect) == "yes"} {
							set eleArray(multiSelect) "no"
						} else {
							set eleArray(multiSelect) "yes"
						}
					}
					foreach {old new} [list req required widget useDateWidget initDateWidget widgitNow input showOnInput \
						output showOnOutput multiSelect multiSelect userList userList] {
						if {[info exists eleArray($old)]} {
							if {$eleArray($old) == "yes"} {										
								$attrToken appendFromList [list $new {} [list [list #text true]]]
							} else {
								$attrToken appendFromList [list $new {} [list [list #text false]]]
							}
						}
					}

				}
				
			}

		}

		set dataVal {<?xml version="1.0" encoding="UTF-8"?>}
	    append dataVal "\n[$rootToken asXML -indent 2]"
		set attrs(definition) [doBlob $dataVal]
		set attrs(nextFieldId) $fieldId
        set results [setupColVals ::j2ee_Command_class_MAP attrs insert]
        set cmdList [lindex $results 1]
        set cmd [lindex $cmdList 0] 
        wimsql_rw "INSERT into SS_Commands $cmd;" [lindex $cmdList 1]
        if {$::dialect == "oracle"} {
            set rowid [lindex [wimsql_rw "select rowid from SS_Commands where id='$attrs(id)';" ] 0]
            wimsql_rw blob Commands definition $rowid $dataVal 
        }
#		wimsql_rw "INSERT into SS_CommandMap (forum,commandDef) values ($::forumIds($forum),'$attrs(id)');"

		
	}
	
	return $defaultReplyId
}

proc setupColVals {map fromArray type {exclude {}}} {
    upvar #0 $map attrMap
    upvar #0 ${map}I attrMapI

    upvar $fromArray userMods
    set attrs {}
    set kvps {}
    set vals {}
    set notfound {}
    set dialect [wimsql_info dialect]
    foreach a [array names userMods] {
        if {![isnull $exclude]} {
            if {[lsearch $exclude $a] >=0} {continue}
        }
        try {
            set newAtt $attrMapI($a)
        } else {
            lappend notfound $a
            continue
        }

        set dType [lindex $attrMap($newAtt) 1] 
        if {($dType == "-") && ($type != "where")} {
            lappend kvps $newAtt 
            continue
        }
        if {$sqlDialect::noParams} {
           if {$dType == "int32"} {
                 lappend vals "$userMods($a)"
            } elseif {$dType == "timestamp"} {
                try {                    
                    lappend vals "TIMESTAMP'[fmtdate -format {%Y-%m-%d %T} $userMods($a)]'"
                } else {continue}
            } elseif {$dType == "boolean"} {                
                if {($userMods($a) == "1") || ($userMods($a) == "yes") || ($userMods($a) == "on")} {
                    if {$dialect != "oracle"} {
                        lappend vals "B'1'"
                    } else {
                        lappend vals "1"
                    }
                } else {
                    if {$dialect != "oracle"} {
                        lappend vals "B'0'"
                    } else {
                        lappend vals 0
                    }
                }
            } elseif {($dType == "clob") || ($dType=="blob")} {
                lappend vals "$userMods($a)"
            } else {
 				if {[isnull $userMods($a)]} {set userMods($a) " "}
                ::sqlDialect::checkLength $a [string bytelength $userMods($a)] $dType
                lappend vals '[sql_quote_value $userMods($a)]'
            }            

        } else {
            if {$dType == "timestamp"} {
                try {
                    lappend vals [fmtdate -format {%Y-%m-%d %T} $userMods($a)]
                } else {continue}

            } elseif {$dType == "boolean"} {
                if {($userMods($a) == "1") || ($userMods($a) == "yes") || ($userMods($a) == "on")} {
                    lappend vals 1
                } else {
                    lappend vals 0
                }
            } elseif {($dType == "clob") || ($dType == "blob")} {
                lappend vals $userMods($a)
            } else {
 				if {[isnull $userMods($a)] && ($dType != "int32")} { 
 					set userMods($a) " "
				} 
                lappend vals "$userMods($a)"
                if {($dType != "int32")} {
                    ::sqlDialect::checkLength $a [string bytelength $userMods($a)] $dType
                }
            }
        }            
        lappend attrs $newAtt
    }
    
    set cmdList {}
    if {![isnull $attrs]} {
        if {$type == "update"} {
            set sstring {}
            set comma ""
            if {$sqlDialect::noParams} {
                set index 0
                foreach att $attrs {
                    append sstring " $comma ${att}=[lindex $vals $index] "
                    incr index
                    set comma ","
                }   

                set cmdList [list "SET $sstring" ""]
            } else {
                foreach att $attrs {
                    append sstring " $comma ${att}=?"
                    set comma ","
                }   
                set cmdList [list "SET $sstring" $vals]
            }
        } else {
            if {$::sqlDialect::noParams} {
                set cmdList [list "([join $attrs ,]) VALUES ([join $vals ,])" ""]
            } else {
                set params "([string repeat "?," [expr [llength $vals] -1]]"
                append params "?)"
                set cmdList [list "([join $attrs ,]) VALUES $params" $vals]
            }      
        } 
    }
    return [list $attrs $cmdList $kvps $notfound]
}
proc sqlDialect::checkLength {name length dType} {
    variable maxvarchar 
    variable maxfname 
    variable maxtext 
    variable nmaxtextSeg
    variable maxpropid 
    variable maxname  
    variable maxvarchar
    set type [lindex $dType 0]
    set max [lindex $dType 1]

    switch  -- $type {
        maxtext -
        nmaxtext {set max $maxtext}
        512text {set max 512}
        maxfname {set max $maxfname}
        maxpropid {set max $maxpropid}
        datetime {set max 19}
        timestamp {set max 26}
        uuid {set max 32}
        maxname {set max $maxname}
        maxvarchar -
        nmaxvarchar {set max $maxvarchar}
        nmaxtextSeg {set max $maxtextSeg}
        clob -
		blob {return -1}
    }

    if {$length > $max} {
        error "Specified $name value length is $length, maximum allowed is $max."
    }
    return $max
}
# need longer string than wgw__b1032
proc B1036 { v {w 0} } {
    set r ""
    while {$v} {
        set r "[string index 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ [expr $v % 36]]$r"
        set v [expr $v / 36]
    }
    if {[isnull $r]} {set r 0}
    if {$w} {
        if {[string length $r] < $w} {
            set r "[string range 000000000000000 1 [expr $w - [string length $r]]]$r"
        }
    }
    return $r 
}
#			.append( format( getIP() ) ).append(sep)
#			.append( format( getJVM() ) ).append(sep)
#			.append( format( getHiTime() ) ).append(sep)
#			.append( format( getLoTime() ) ).append(sep)
#			.append( format( getCount() ) )

