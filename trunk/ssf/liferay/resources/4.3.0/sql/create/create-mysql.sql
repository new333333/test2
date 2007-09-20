drop database if exists lportal;
create database lportal character set utf8;
use lportal;

create table Account_ (
	accountId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	parentAccountId bigint,
	name varchar(75) null,
	legalName varchar(75) null,
	legalId varchar(75) null,
	legalType varchar(75) null,
	sicCode varchar(75) null,
	tickerSymbol varchar(75) null,
	industry varchar(75) null,
	type_ varchar(75) null,
	size_ varchar(75) null
);

create table Address (
	addressId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	classNameId bigint,
	classPK bigint,
	street1 varchar(75) null,
	street2 varchar(75) null,
	street3 varchar(75) null,
	city varchar(75) null,
	zip varchar(75) null,
	regionId bigint,
	countryId bigint,
	typeId integer,
	mailing tinyint,
	primary_ tinyint
);

create table BlogsCategory (
	categoryId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	parentCategoryId bigint,
	name varchar(75) null,
	description longtext null
);

create table BlogsEntry (
	entryId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	categoryId bigint,
	title varchar(150) null,
	content longtext null,
	displayDate datetime null
);

create table BookmarksEntry (
	entryId bigint not null primary key,
	companyId bigint,
	userId bigint,
	createDate datetime null,
	modifiedDate datetime null,
	folderId bigint,
	name varchar(75) null,
	url longtext null,
	comments longtext null,
	visits integer
);

create table BookmarksFolder (
	folderId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	createDate datetime null,
	modifiedDate datetime null,
	parentFolderId bigint,
	name varchar(75) null,
	description longtext null
);

create table CalEvent (
	eventId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	title varchar(75) null,
	description longtext null,
	startDate datetime null,
	endDate datetime null,
	durationHour integer,
	durationMinute integer,
	allDay tinyint,
	timeZoneSensitive tinyint,
	type_ varchar(75) null,
	repeating tinyint,
	recurrence longtext null,
	remindBy varchar(75) null,
	firstReminder integer,
	secondReminder integer
);

create table ClassName_ (
	classNameId bigint not null primary key,
	value varchar(75) null
);

create table Company (
	companyId bigint not null primary key,
	accountId bigint,
	webId varchar(75) null,
	key_ longtext null,
	virtualHost varchar(75) null,
	mx varchar(75) null,
	logoId bigint
);

create table Contact_ (
	contactId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	accountId bigint,
	parentContactId bigint,
	firstName varchar(75) null,
	middleName varchar(75) null,
	lastName varchar(75) null,
	prefixId integer,
	suffixId integer,
	male tinyint,
	birthday datetime null,
	smsSn varchar(75) null,
	aimSn varchar(75) null,
	icqSn varchar(75) null,
	jabberSn varchar(75) null,
	msnSn varchar(75) null,
	skypeSn varchar(75) null,
	ymSn varchar(75) null,
	employeeStatusId varchar(75) null,
	employeeNumber varchar(75) null,
	jobTitle varchar(100) null,
	jobClass varchar(75) null,
	hoursOfOperation varchar(75) null
);

create table Counter (
	name varchar(75) not null primary key,
	currentId bigint
);

create table Country (
	countryId bigint not null primary key,
	name varchar(75) null,
	a2 varchar(75) null,
	a3 varchar(75) null,
	number_ varchar(75) null,
	idd_ varchar(75) null,
	active_ tinyint
);

create table CyrusUser (
	userId bigint not null primary key,
	password_ varchar(75) not null
);

create table CyrusVirtual (
	emailAddress varchar(75) not null primary key,
	userId bigint
);

create table DLFileEntry (
	fileEntryId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	versionUserId bigint,
	versionUserName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	folderId bigint,
	name varchar(300) null,
	title varchar(300) null,
	description longtext null,
	version double,
	size_ integer,
	readCount integer,
	extraSettings longtext null
);

create table DLFileRank (
	fileRankId bigint not null primary key,
	companyId bigint,
	userId bigint,
	createDate datetime null,
	folderId bigint,
	name varchar(300) null
);

create table DLFileShortcut (
	fileShortcutId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	folderId bigint,
	toFolderId bigint,
	toName varchar(300) null
);

create table DLFileVersion (
	fileVersionId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	folderId bigint,
	name varchar(300) null,
	version double,
	size_ integer
);

create table DLFolder (
	folderId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	parentFolderId bigint,
	name varchar(100) null,
	description longtext null,
	lastPostDate datetime null
);

create table EmailAddress (
	emailAddressId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	classNameId bigint,
	classPK bigint,
	address varchar(75) null,
	typeId integer,
	primary_ tinyint
);

create table Group_ (
	groupId bigint not null primary key,
	companyId bigint,
	creatorUserId bigint,
	classNameId bigint,
	classPK bigint,
	parentGroupId bigint,
	liveGroupId bigint,
	name varchar(75) null,
	description longtext null,
	type_ varchar(75) null,
	friendlyURL varchar(100) null,
	active_ tinyint
);

create table Groups_Orgs (
	groupId bigint not null,
	organizationId bigint not null,
	primary key (groupId, organizationId)
);

create table Groups_Permissions (
	groupId bigint not null,
	permissionId bigint not null,
	primary key (groupId, permissionId)
);

create table Groups_Roles (
	groupId bigint not null,
	roleId bigint not null,
	primary key (groupId, roleId)
);

create table Groups_UserGroups (
	groupId bigint not null,
	userGroupId bigint not null,
	primary key (groupId, userGroupId)
);

create table IGFolder (
	folderId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	createDate datetime null,
	modifiedDate datetime null,
	parentFolderId bigint,
	name varchar(75) null,
	description longtext null
);

create table IGImage (
	imageId bigint not null primary key,
	companyId bigint,
	userId bigint,
	createDate datetime null,
	modifiedDate datetime null,
	folderId bigint,
	description longtext null,
	smallImageId bigint,
	largeImageId bigint
);

create table Image (
	imageId bigint not null primary key,
	modifiedDate datetime null,
	text_ longtext null,
	type_ varchar(75) null,
	height integer,
	width integer,
	size_ integer
);

create table JournalArticle (
	id_ bigint not null primary key,
	resourcePrimKey bigint,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	articleId varchar(75) null,
	version double,
	title varchar(100) null,
	description longtext null,
	content longtext null,
	type_ varchar(75) null,
	structureId varchar(75) null,
	templateId varchar(75) null,
	displayDate datetime null,
	approved tinyint,
	approvedByUserId bigint,
	approvedByUserName varchar(75) null,
	approvedDate datetime null,
	expired tinyint,
	expirationDate datetime null,
	reviewDate datetime null
);

create table JournalArticleImage (
	articleImageId bigint not null primary key,
	groupId bigint,
	articleId varchar(75) null,
	version double,
	elName varchar(75) null,
	languageId varchar(75) null,
	tempImage tinyint
);

create table JournalArticleResource (
	resourcePrimKey bigint not null primary key,
	groupId bigint,
	articleId varchar(75) null
);

create table JournalContentSearch (
	contentSearchId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	privateLayout tinyint,
	layoutId bigint,
	portletId varchar(200) null,
	articleId varchar(75) null
);

create table JournalStructure (
	id_ bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	structureId varchar(75) null,
	name varchar(75) null,
	description longtext null,
	xsd longtext null
);

create table JournalTemplate (
	id_ bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	templateId varchar(75) null,
	structureId varchar(75) null,
	name varchar(75) null,
	description longtext null,
	xsl longtext null,
	langType varchar(75) null,
	smallImage tinyint,
	smallImageId bigint,
	smallImageURL varchar(75) null
);

create table Layout (
	plid bigint not null primary key,
	groupId bigint,
	companyId bigint,
	privateLayout tinyint,
	layoutId bigint,
	parentLayoutId bigint,
	name longtext null,
	title longtext null,
	type_ varchar(75) null,
	typeSettings longtext null,
	hidden_ tinyint,
	friendlyURL varchar(100) null,
	iconImage tinyint,
	iconImageId bigint,
	themeId varchar(75) null,
	colorSchemeId varchar(75) null,
	wapThemeId varchar(75) null,
	wapColorSchemeId varchar(75) null,
	css longtext null,
	priority integer
);

create table LayoutSet (
	layoutSetId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	privateLayout tinyint,
	logo tinyint,
	logoId bigint,
	themeId varchar(75) null,
	colorSchemeId varchar(75) null,
	wapThemeId varchar(75) null,
	wapColorSchemeId varchar(75) null,
	css varchar(75) null,
	pageCount integer,
	virtualHost varchar(75) null
);

create table ListType (
	listTypeId integer not null primary key,
	name varchar(75) null,
	type_ varchar(75) null
);

create table MBBan (
	banId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	banUserId bigint
);

create table MBCategory (
	categoryId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	parentCategoryId bigint,
	name varchar(75) null,
	description longtext null,
	lastPostDate datetime null
);

create table MBDiscussion (
	discussionId bigint not null primary key,
	classNameId bigint,
	classPK bigint,
	threadId bigint
);

create table MBMessage (
	messageId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	categoryId bigint,
	threadId bigint,
	parentMessageId bigint,
	subject varchar(75) null,
	body longtext null,
	attachments tinyint,
	anonymous tinyint
);

create table MBMessageFlag (
	messageFlagId bigint not null primary key,
	userId bigint,
	messageId bigint,
	flag integer
);

create table MBStatsUser (
	statsUserId bigint not null primary key,
	groupId bigint,
	userId bigint,
	messageCount integer,
	lastPostDate datetime null
);

create table MBThread (
	threadId bigint not null primary key,
	categoryId bigint,
	rootMessageId bigint,
	messageCount integer,
	viewCount integer,
	lastPostByUserId bigint,
	lastPostDate datetime null,
	priority double
);

create table Organization_ (
	organizationId bigint not null primary key,
	companyId bigint,
	parentOrganizationId bigint,
	name varchar(100) null,
	recursable tinyint,
	regionId bigint,
	countryId bigint,
	statusId integer,
	comments longtext null
);

create table OrgGroupPermission (
	organizationId bigint not null,
	groupId bigint not null,
	permissionId bigint not null,
	primary key (organizationId, groupId, permissionId)
);

create table OrgGroupRole (
	organizationId bigint not null,
	groupId bigint not null,
	roleId bigint not null,
	primary key (organizationId, groupId, roleId)
);

create table OrgLabor (
	orgLaborId bigint not null primary key,
	organizationId bigint,
	typeId integer,
	sunOpen integer,
	sunClose integer,
	monOpen integer,
	monClose integer,
	tueOpen integer,
	tueClose integer,
	wedOpen integer,
	wedClose integer,
	thuOpen integer,
	thuClose integer,
	friOpen integer,
	friClose integer,
	satOpen integer,
	satClose integer
);

create table PasswordPolicy (
	passwordPolicyId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	defaultPolicy tinyint,
	name varchar(75) null,
	description longtext null,
	changeable tinyint,
	changeRequired tinyint,
	minAge bigint,
	checkSyntax tinyint,
	allowDictionaryWords tinyint,
	minLength integer,
	history tinyint,
	historyCount integer,
	expireable tinyint,
	maxAge bigint,
	warningTime bigint,
	graceLimit integer,
	lockout tinyint,
	maxFailure integer,
	lockoutDuration bigint,
	requireUnlock tinyint,
	resetFailureCount bigint
);

create table PasswordPolicyRel (
	passwordPolicyRelId bigint not null primary key,
	passwordPolicyId bigint,
	classNameId bigint,
	classPK bigint
);

create table PasswordTracker (
	passwordTrackerId bigint not null primary key,
	userId bigint,
	createDate datetime null,
	password_ varchar(75) null
);

create table Permission_ (
	permissionId bigint not null primary key,
	companyId bigint,
	actionId varchar(75) null,
	resourceId bigint
);

create table Phone (
	phoneId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	classNameId bigint,
	classPK bigint,
	number_ varchar(75) null,
	extension varchar(75) null,
	typeId integer,
	primary_ tinyint
);

create table PluginSetting (
	pluginSettingId bigint not null primary key,
	companyId bigint,
	pluginId varchar(75) null,
	pluginType varchar(75) null,
	roles longtext null,
	active_ tinyint
);

create table PollsChoice (
	choiceId bigint not null primary key,
	questionId bigint,
	name varchar(75) null,
	description varchar(1000) null
);

create table PollsQuestion (
	questionId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	title varchar(500) null,
	description longtext null,
	expirationDate datetime null,
	lastVoteDate datetime null
);

create table PollsVote (
	voteId bigint not null primary key,
	userId bigint,
	questionId bigint,
	choiceId bigint,
	voteDate datetime null
);

create table Portlet (
	id_ bigint not null primary key,
	companyId bigint,
	portletId varchar(200) null,
	roles longtext null,
	active_ tinyint
);

create table PortletPreferences (
	portletPreferencesId bigint not null primary key,
	ownerId bigint,
	ownerType integer,
	plid bigint,
	portletId varchar(200) null,
	preferences longtext null
);

create table RatingsEntry (
	entryId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	classNameId bigint,
	classPK bigint,
	score double
);

create table RatingsStats (
	statsId bigint not null primary key,
	classNameId bigint,
	classPK bigint,
	totalEntries integer,
	totalScore double,
	averageScore double
);

create table Region (
	regionId bigint not null primary key,
	countryId bigint,
	regionCode varchar(75) null,
	name varchar(75) null,
	active_ tinyint
);

create table Release_ (
	releaseId bigint not null primary key,
	createDate datetime null,
	modifiedDate datetime null,
	buildNumber integer,
	buildDate datetime null,
	verified tinyint
);

create table Resource_ (
	resourceId bigint not null primary key,
	codeId bigint,
	primKey varchar(300) null
);

create table ResourceCode (
	codeId bigint not null primary key,
	companyId bigint,
	name varchar(300) null,
	scope integer
);

create table Role_ (
	roleId bigint not null primary key,
	companyId bigint,
	classNameId bigint,
	classPK bigint,
	name varchar(75) null,
	description longtext null,
	type_ integer
);

create table Roles_Permissions (
	roleId bigint not null,
	permissionId bigint not null,
	primary key (roleId, permissionId)
);

create table SCFrameworkVersi_SCProductVers (
	productVersionId bigint not null,
	frameworkVersionId bigint not null,
	primary key (productVersionId, frameworkVersionId)
);

create table SCFrameworkVersion (
	frameworkVersionId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	name varchar(75) null,
	url varchar(1024) null,
	active_ tinyint,
	priority integer
);

create table SCLicense (
	licenseId bigint not null primary key,
	name varchar(75) null,
	url varchar(1024) null,
	openSource tinyint,
	active_ tinyint,
	recommended tinyint
);

create table SCLicenses_SCProductEntries (
	productEntryId bigint not null,
	licenseId bigint not null,
	primary key (productEntryId, licenseId)
);

create table SCProductEntry (
	productEntryId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	name varchar(75) null,
	type_ varchar(75) null,
	shortDescription longtext null,
	longDescription longtext null,
	pageURL varchar(1024) null,
	repoGroupId varchar(75) null,
	repoArtifactId varchar(75) null
);

create table SCProductVersion (
	productVersionId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	productEntryId bigint,
	version varchar(75) null,
	changeLog longtext null,
	downloadPageURL varchar(1024) null,
	directDownloadURL varchar(1024) null,
	repoStoreArtifact tinyint
);

create table ShoppingCart (
	cartId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	itemIds longtext null,
	couponCodes varchar(75) null,
	altShipping integer,
	insure tinyint
);

create table ShoppingCategory (
	categoryId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	parentCategoryId bigint,
	name varchar(75) null,
	description longtext null
);

create table ShoppingCoupon (
	couponId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	code_ varchar(75) null,
	name varchar(75) null,
	description longtext null,
	startDate datetime null,
	endDate datetime null,
	active_ tinyint,
	limitCategories longtext null,
	limitSkus longtext null,
	minOrder double,
	discount double,
	discountType varchar(75) null
);

create table ShoppingItem (
	itemId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	categoryId bigint,
	sku varchar(75) null,
	name varchar(200) null,
	description longtext null,
	properties longtext null,
	fields_ tinyint,
	fieldsQuantities longtext null,
	minQuantity integer,
	maxQuantity integer,
	price double,
	discount double,
	taxable tinyint,
	shipping double,
	useShippingFormula tinyint,
	requiresShipping tinyint,
	stockQuantity integer,
	featured_ tinyint,
	sale_ tinyint,
	smallImage tinyint,
	smallImageId bigint,
	smallImageURL varchar(75) null,
	mediumImage tinyint,
	mediumImageId bigint,
	mediumImageURL varchar(75) null,
	largeImage tinyint,
	largeImageId bigint,
	largeImageURL varchar(75) null
);

create table ShoppingItemField (
	itemFieldId bigint not null primary key,
	itemId bigint,
	name varchar(75) null,
	values_ longtext null,
	description longtext null
);

create table ShoppingItemPrice (
	itemPriceId bigint not null primary key,
	itemId bigint,
	minQuantity integer,
	maxQuantity integer,
	price double,
	discount double,
	taxable tinyint,
	shipping double,
	useShippingFormula tinyint,
	status integer
);

create table ShoppingOrder (
	orderId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	number_ varchar(75) null,
	tax double,
	shipping double,
	altShipping varchar(75) null,
	requiresShipping tinyint,
	insure tinyint,
	insurance double,
	couponCodes varchar(75) null,
	couponDiscount double,
	billingFirstName varchar(75) null,
	billingLastName varchar(75) null,
	billingEmailAddress varchar(75) null,
	billingCompany varchar(75) null,
	billingStreet varchar(75) null,
	billingCity varchar(75) null,
	billingState varchar(75) null,
	billingZip varchar(75) null,
	billingCountry varchar(75) null,
	billingPhone varchar(75) null,
	shipToBilling tinyint,
	shippingFirstName varchar(75) null,
	shippingLastName varchar(75) null,
	shippingEmailAddress varchar(75) null,
	shippingCompany varchar(75) null,
	shippingStreet varchar(75) null,
	shippingCity varchar(75) null,
	shippingState varchar(75) null,
	shippingZip varchar(75) null,
	shippingCountry varchar(75) null,
	shippingPhone varchar(75) null,
	ccName varchar(75) null,
	ccType varchar(75) null,
	ccNumber varchar(75) null,
	ccExpMonth integer,
	ccExpYear integer,
	ccVerNumber varchar(75) null,
	comments longtext null,
	ppTxnId varchar(75) null,
	ppPaymentStatus varchar(75) null,
	ppPaymentGross double,
	ppReceiverEmail varchar(75) null,
	ppPayerEmail varchar(75) null,
	sendOrderEmail tinyint,
	sendShippingEmail tinyint
);

create table ShoppingOrderItem (
	orderItemId bigint not null primary key,
	orderId bigint,
	itemId varchar(75) null,
	sku varchar(75) null,
	name varchar(200) null,
	description longtext null,
	properties longtext null,
	price double,
	quantity integer,
	shippedDate datetime null
);

create table Subscription (
	subscriptionId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	classNameId bigint,
	classPK bigint,
	frequency varchar(75) null
);

create table TagsAsset (
	assetId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	classNameId bigint,
	classPK bigint,
	startDate datetime null,
	endDate datetime null,
	publishDate datetime null,
	expirationDate datetime null,
	mimeType varchar(75) null,
	title varchar(75) null,
	url varchar(75) null,
	height integer,
	width integer
);

create table TagsAssets_TagsEntries (
	assetId bigint not null,
	entryId bigint not null,
	primary key (assetId, entryId)
);

create table TagsEntry (
	entryId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	name varchar(75) null
);

create table TagsProperty (
	propertyId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	entryId bigint,
	key_ varchar(75) null,
	value varchar(75) null
);

create table TagsSource (
	sourceId bigint not null primary key,
	parentSourceId bigint,
	name varchar(75) null,
	acronym varchar(75) null
);

create table User_ (
	userId bigint not null primary key,
	companyId bigint,
	createDate datetime null,
	modifiedDate datetime null,
	defaultUser tinyint,
	contactId bigint,
	password_ varchar(75) null,
	passwordEncrypted tinyint,
	passwordReset tinyint,
	passwordModifiedDate datetime null,
	graceLoginCount integer,
	screenName varchar(75) null,
	emailAddress varchar(75) null,
	portraitId bigint,
	languageId varchar(75) null,
	timeZoneId varchar(75) null,
	greeting varchar(75) null,
	comments longtext null,
	loginDate datetime null,
	loginIP varchar(75) null,
	lastLoginDate datetime null,
	lastLoginIP varchar(75) null,
	lastFailedLoginDate datetime null,
	failedLoginAttempts integer,
	lockout tinyint,
	lockoutDate datetime null,
	agreedToTermsOfUse tinyint,
	active_ tinyint
);

create table UserGroup (
	userGroupId bigint not null primary key,
	companyId bigint,
	parentUserGroupId bigint,
	name varchar(75) null,
	description longtext null
);

create table UserGroupRole (
	userId bigint not null,
	groupId bigint not null,
	roleId bigint not null,
	primary key (userId, groupId, roleId)
);

create table UserIdMapper (
	userIdMapperId bigint not null primary key,
	userId bigint,
	type_ varchar(75) null,
	description varchar(75) null,
	externalUserId varchar(75) null
);

create table Users_Groups (
	userId bigint not null,
	groupId bigint not null,
	primary key (userId, groupId)
);

create table Users_Orgs (
	userId bigint not null,
	organizationId bigint not null,
	primary key (userId, organizationId)
);

create table Users_Permissions (
	userId bigint not null,
	permissionId bigint not null,
	primary key (userId, permissionId)
);

create table Users_Roles (
	userId bigint not null,
	roleId bigint not null,
	primary key (userId, roleId)
);

create table Users_UserGroups (
	userId bigint not null,
	userGroupId bigint not null,
	primary key (userId, userGroupId)
);

create table UserTracker (
	userTrackerId bigint not null primary key,
	companyId bigint,
	userId bigint,
	modifiedDate datetime null,
	sessionId varchar(200) null,
	remoteAddr varchar(75) null,
	remoteHost varchar(75) null,
	userAgent varchar(200) null
);

create table UserTrackerPath (
	userTrackerPathId bigint not null primary key,
	userTrackerId bigint,
	path longtext null,
	pathDate datetime null
);

create table Website (
	websiteId bigint not null primary key,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	classNameId bigint,
	classPK bigint,
	url varchar(75) null,
	typeId integer,
	primary_ tinyint
);

create table WikiNode (
	nodeId bigint not null primary key,
	groupId bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	modifiedDate datetime null,
	name varchar(75) null,
	description longtext null,
	lastPostDate datetime null
);

create table WikiPage (
	pageId bigint not null primary key,
	resourcePrimKey bigint,
	companyId bigint,
	userId bigint,
	userName varchar(75) null,
	createDate datetime null,
	nodeId bigint,
	title varchar(75) null,
	version double,
	content longtext null,
	format varchar(75) null,
	head tinyint
);

create table WikiPageResource (
	resourcePrimKey bigint not null primary key,
	nodeId bigint,
	title varchar(75) null
);


insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (1, 'Canada', 'CA', 'CAN', '124', '001', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (2, 'China', 'CN', 'CHN', '156', '086', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (3, 'France', 'FR', 'FRA', '250', '033', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (4, 'Germany', 'DE', 'DEU', '276', '049', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (5, 'Hong Kong', 'HK', 'HKG', '344', '852', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (6, 'Hungary', 'HU', 'HUN', '348', '036', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (7, 'Israel', 'IL', 'ISR', '376', '972', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (8, 'Italy', 'IT', 'ITA', '380', '039', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (9, 'Japan', 'JP', 'JPN', '392', '081', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (10, 'South Korea', 'KP', 'KOR', '410', '082', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (11, 'Netherlands', 'NL', 'NLD', '528', '031', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (12, 'Portugal', 'PT', 'PRT', '620', '351', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (13, 'Russia', 'RU', 'RUS', '643', '007', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (14, 'Singapore', 'SG', 'SGP', '702', '065', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (15, 'Spain', 'ES', 'ESP', '724', '034', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (16, 'Turkey', 'TR', 'TUR', '792', '090', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (17, 'Vietnam', 'VM', 'VNM', '704', '084', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (18, 'United Kingdom', 'GB', 'GBR', '826', '044', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (19, 'United States', 'US', 'USA', '840', '001', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (20, 'Afghanistan', 'AF', 'AFG', '4', '093', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (21, 'Albania', 'AL', 'ALB', '8', '355', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (22, 'Algeria', 'DZ', 'DZA', '12', '213', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (23, 'American Samoa', 'AS', 'ASM', '16', '684', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (24, 'Andorra', 'AD', 'AND', '20', '376', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (25, 'Angola', 'AO', 'AGO', '24', '244', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (26, 'Anguilla', 'AI', 'AIA', '660', '264', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (27, 'Antarctica', 'AQ', 'ATA', '10', '672', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (28, 'Antigua', 'AG', 'ATG', '28', '268', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (29, 'Argentina', 'AR', 'ARG', '32', '054', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (30, 'Armenia', 'AM', 'ARM', '51', '374', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (31, 'Aruba', 'AW', 'ABW', '533', '297', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (32, 'Australia', 'AU', 'AUS', '36', '061', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (33, 'Austria', 'AT', 'AUT', '40', '043', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (34, 'Azerbaijan', 'AZ', 'AZE', '31', '994', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (35, 'Bahamas', 'BS', 'BHS', '44', '242', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (36, 'Bahrain', 'BH', 'BHR', '48', '973', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (37, 'Bangladesh', 'BD', 'BGD', '50', '880', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (38, 'Barbados', 'BB', 'BRB', '52', '246', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (39, 'Belarus', 'BY', 'BLR', '112', '375', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (40, 'Belgium', 'BE', 'BEL', '56', '032', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (41, 'Belize', 'BZ', 'BLZ', '84', '501', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (42, 'Benin', 'BJ', 'BEN', '204', '229', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (43, 'Bermuda', 'BM', 'BMU', '60', '441', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (44, 'Bhutan', 'BT', 'BTN', '64', '975', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (45, 'Bolivia', 'BO', 'BOL', '68', '591', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (46, 'Bosnia-Herzegovina', 'BA', 'BIH', '70', '387', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (47, 'Botswana', 'BW', 'BWA', '72', '267', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (48, 'Brazil', 'BR', 'BRA', '76', '055', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (49, 'British Virgin Islands', 'VG', 'VGB', '92', '284', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (50, 'Brunei', 'BN', 'BRN', '96', '673', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (51, 'Bulgaria', 'BG', 'BGR', '100', '359', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (52, 'Burkina Faso', 'BF', 'BFA', '854', '226', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (53, 'Burma (Myanmar)', 'MM', 'MMR', '104', '095', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (54, 'Burundi', 'BI', 'BDI', '108', '257', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (55, 'Cambodia', 'KH', 'KHM', '116', '855', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (56, 'Cameroon', 'CM', 'CMR', '120', '237', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (57, 'Cape Verde Island', 'CV', 'CPV', '132', '238', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (58, 'Cayman Islands', 'KY', 'CYM', '136', '345', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (59, 'Central African Republic', 'CF', 'CAF', '140', '236', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (60, 'Chad', 'TD', 'TCD', '148', '235', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (61, 'Chile', 'CL', 'CHL', '152', '056', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (62, 'Christmas Island', 'CX', 'CXR', '162', '061', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (63, 'Cocos Islands', 'CC', 'CCK', '166', '061', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (64, 'Colombia', 'CO', 'COL', '170', '057', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (65, 'Comoros', 'KM', 'COM', '174', '269', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (66, 'Republic of Congo', 'CD', 'COD', '180', '242', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (67, 'Democratic Republic of Congo', 'CG', 'COG', '178', '243', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (68, 'Cook Islands', 'CK', 'COK', '184', '682', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (69, 'Costa Rica', 'CI', 'CRI', '188', '506', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (70, 'Croatia', 'HR', 'HRV', '191', '385', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (71, 'Cuba', 'CU', 'CUB', '192', '053', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (72, 'Cyprus', 'CY', 'CYP', '196', '357', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (73, 'Czech Republic', 'CZ', 'CZE', '203', '420', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (74, 'Denmark', 'DK', 'DNK', '208', '045', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (75, 'Djibouti', 'DJ', 'DJI', '262', '253', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (76, 'Dominica', 'DM', 'DMA', '212', '767', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (77, 'Dominican Republic', 'DO', 'DOM', '214', '809', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (78, 'Ecuador', 'EC', 'ECU', '218', '593', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (79, 'Egypt', 'EG', 'EGY', '818', '020', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (80, 'El Salvador', 'SV', 'SLV', '222', '503', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (81, 'Equatorial Guinea', 'GQ', 'GNQ', '226', '240', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (82, 'Eritrea', 'ER', 'ERI', '232', '291', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (83, 'Estonia', 'EE', 'EST', '233', '372', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (84, 'Ethiopia', 'ET', 'ETH', '231', '251', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (85, 'Faeroe Islands', 'FO', 'FRO', '234', '298', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (86, 'Falkland Islands', 'FK', 'FLK', '238', '500', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (87, 'Fiji Islands', 'FJ', 'FJI', '242', '679', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (88, 'Finland', 'FI', 'FIN', '246', '358', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (89, 'French Guiana', 'GF', 'GUF', '254', '594', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (90, 'French Polynesia', 'PF', 'PYF', '258', '689', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (91, 'Gabon', 'GA', 'GAB', '266', '241', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (92, 'Gambia', 'GM', 'GMB', '270', '220', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (93, 'Georgia', 'GE', 'GEO', '268', '995', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (94, 'Ghana', 'GH', 'GHA', '288', '233', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (95, 'Gibraltar', 'GI', 'GIB', '292', '350', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (96, 'Greece', 'GR', 'GRC', '300', '030', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (97, 'Greenland', 'GL', 'GRL', '304', '299', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (98, 'Grenada', 'GD', 'GRD', '308', '473', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (99, 'Guadeloupe', 'GP', 'GLP', '312', '590', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (100, 'Guam', 'GU', 'GUM', '316', '671', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (101, 'Guatemala', 'GT', 'GTM', '320', '502', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (102, 'Guinea', 'GN', 'GIN', '324', '224', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (103, 'Guinea-Bissau', 'GW', 'GNB', '624', '245', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (104, 'Guyana', 'GY', 'GUY', '328', '592', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (105, 'Haiti', 'HT', 'HTI', '332', '509', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (106, 'Honduras', 'HN', 'HND', '340', '504', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (107, 'Iceland', 'IS', 'ISL', '352', '354', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (108, 'India', 'IN', 'IND', '356', '091', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (109, 'Indonesia', 'ID', 'IDN', '360', '062', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (110, 'Iran', 'IR', 'IRN', '364', '098', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (111, 'Iraq', 'IQ', 'IRQ', '368', '964', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (112, 'Ireland', 'IE', 'IRL', '372', '353', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (113, 'Ivory Coast', 'CI', 'CIV', '384', '225', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (114, 'Jamaica', 'JM', 'JAM', '388', '876', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (115, 'Jordan', 'JO', 'JOR', '400', '962', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (116, 'Kazakhstan', 'KZ', 'KAZ', '398', '007', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (117, 'Kenya', 'KE', 'KEN', '404', '254', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (118, 'Kiribati', 'KI', 'KIR', '408', '686', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (119, 'Kuwait', 'KW', 'KWT', '414', '965', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (120, 'North Korea', 'KP', 'PRK', '408', '850', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (121, 'Kyrgyzstan', 'KG', 'KGZ', '471', '996', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (122, 'Laos', 'LA', 'LAO', '418', '856', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (123, 'Latvia', 'LV', 'LVA', '428', '371', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (124, 'Lebanon', 'LB', 'LBN', '422', '961', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (125, 'Lesotho', 'LS', 'LSO', '426', '266', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (126, 'Liberia', 'LR', 'LBR', '430', '231', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (127, 'Libya', 'LY', 'LBY', '434', '218', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (128, 'Liechtenstein', 'LI', 'LIE', '438', '423', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (129, 'Lithuania', 'LT', 'LTU', '440', '370', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (130, 'Luxembourg', 'LU', 'LUX', '442', '352', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (131, 'Macau', 'MO', 'MAC', '446', '853', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (132, 'Macedonia', 'MK', 'MKD', '807', '389', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (133, 'Madagascar', 'MG', 'MDG', '450', '261', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (134, 'Malawi', 'MW', 'MWI', '454', '265', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (135, 'Malaysia', 'MY', 'MYS', '458', '060', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (136, 'Maldives', 'MV', 'MDV', '462', '960', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (137, 'Mali', 'ML', 'MLI', '466', '223', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (138, 'Malta', 'MT', 'MLT', '470', '356', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (139, 'Marshall Islands', 'MH', 'MHL', '584', '692', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (140, 'Martinique', 'MQ', 'MTQ', '474', '596', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (141, 'Mauritania', 'MR', 'MRT', '478', '222', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (142, 'Mauritius', 'MU', 'MUS', '480', '230', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (143, 'Mayotte Island', 'YT', 'MYT', '175', '269', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (144, 'Mexico', 'MX', 'MEX', '484', '052', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (145, 'Micronesia', 'FM', 'FSM', '583', '691', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (146, 'Moldova', 'MD', 'MDA', '498', '373', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (147, 'Monaco', 'MC', 'MCO', '492', '377', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (148, 'Mongolia', 'MN', 'MNG', '496', '976', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (149, 'Montserrat', 'MS', 'MSR', '500', '664', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (150, 'Morocco', 'MA', 'MAR', '504', '212', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (151, 'Mozambique', 'MZ', 'MOZ', '508', '258', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (152, 'Myanmar (Burma)', 'MM', 'MMR', '104', '095', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (153, 'Namibia', 'NA', 'NAM', '516', '264', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (154, 'Nauru', 'NR', 'NRU', '520', '674', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (155, 'Nepal', 'NP', 'NPL', '524', '977', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (156, 'Netherlands Antilles', 'AN', 'ANT', '530', '599', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (157, 'New Caledonia', 'NC', 'NCL', '540', '687', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (158, 'New Zealand', 'NZ', 'NZL', '554', '064', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (159, 'Nicaragua', 'NE', 'NER', '558', '505', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (160, 'Niger', 'NE', 'NER', '562', '227', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (161, 'Nigeria', 'NG', 'NGA', '566', '234', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (162, 'Niue', 'NU', 'NIU', '570', '683', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (163, 'Norfolk Island', 'NF', 'NFK', '574', '672', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (164, 'Norway', 'NO', 'NOR', '578', '047', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (165, 'Oman', 'OM', 'OMN', '512', '968', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (166, 'Pakistan', 'PK', 'PAK', '586', '092', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (167, 'Palau', 'PW', 'PLW', '585', '680', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (168, 'Palestine', 'PS', 'PSE', '275', '970', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (169, 'Panama', 'PA', 'PAN', '591', '507', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (170, 'Papua New Guinea', 'PG', 'PNG', '598', '675', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (171, 'Paraguay', 'PY', 'PRY', '600', '595', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (172, 'Peru', 'PE', 'PER', '604', '051', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (173, 'Philippines', 'PH', 'PHL', '608', '063', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (174, 'Poland', 'PL', 'POL', '616', '048', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (175, 'Puerto Rico', 'PR', 'PRI', '630', '787', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (176, 'Qatar', 'QA', 'QAT', '634', '974', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (177, 'Reunion Island', 'RE', 'REU', '638', '262', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (178, 'Romania', 'RO', 'ROU', '642', '040', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (179, 'Rwanda', 'RW', 'RWA', '646', '250', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (180, 'St. Helena', 'SH', 'SHN', '654', '290', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (181, 'St. Kitts', 'KN', 'KNA', '659', '869', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (182, 'St. Lucia', 'LC', 'LCA', '662', '758', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (183, 'St. Pierre & Miquelon', 'PM', 'SPM', '666', '508', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (184, 'St. Vincent', 'VC', 'VCT', '670', '784', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (185, 'San Marino', 'SM', 'SMR', '674', '378', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (186, 'Sao Tome & Principe', 'ST', 'STP', '678', '239', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (187, 'Saudi Arabia', 'SA', 'SAU', '682', '966', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (188, 'Senegal', 'SN', 'SEN', '686', '221', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (189, 'Serbia', 'CS', 'SCG', '891', '381', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (190, 'Seychelles', 'SC', 'SYC', '690', '248', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (191, 'Sierra Leone', 'SL', 'SLE', '694', '249', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (192, 'Slovakia', 'SK', 'SVK', '703', '421', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (193, 'Slovenia', 'SI', 'SVN', '705', '386', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (194, 'Solomon Islands', 'SB', 'SLB', '90', '677', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (195, 'Somalia', 'SO', 'SOM', '706', '252', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (196, 'South Africa', 'ZA', 'ZAF', '710', '027', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (197, 'Sri Lanka', 'LK', 'LKA', '144', '094', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (198, 'Sudan', 'SD', 'SDN', '736', '095', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (199, 'Suriname', 'SR', 'SUR', '740', '597', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (200, 'Swaziland', 'SZ', 'SWZ', '748', '268', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (201, 'Sweden', 'SE', 'SWE', '752', '046', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (202, 'Switzerland', 'CH', 'CHE', '756', '041', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (203, 'Syria', 'SY', 'SYR', '760', '963', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (204, 'Taiwan', 'TW', 'TWN', '158', '886', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (205, 'Tajikistan', 'TJ', 'TJK', '762', '992', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (206, 'Tanzania', 'TZ', 'TZA', '834', '255', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (207, 'Thailand', 'TH', 'THA', '764', '066', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (208, 'Togo', 'TG', 'TGO', '768', '228', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (209, 'Tonga', 'TO', 'TON', '776', '676', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (210, 'Trinidad & Tobago', 'TT', 'TTO', '780', '868', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (211, 'Tunisia', 'TN', 'TUN', '788', '216', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (212, 'Turkmenistan', 'TM', 'TKM', '795', '993', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (213, 'Turks & Caicos', 'TC', 'TCA', '796', '649', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (214, 'Tuvalu', 'TV', 'TUV', '798', '688', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (215, 'Uganda', 'UG', 'UGA', '800', '256', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (216, 'Ukraine', 'UA', 'UKR', '804', '380', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (217, 'United Arab Emirates', 'AE', 'ARE', '784', '971', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (218, 'Uruguay', 'UY', 'URY', '858', '598', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (219, 'Uzbekistan', 'UZ', 'UZB', '860', '998', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (220, 'Vanuatu', 'VU', 'VUT', '548', '678', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (221, 'Vatican City', 'VA', 'VAT', '336', '039', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (222, 'Venezuela', 'VE', 'VEN', '862', '058', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (223, 'Wallis & Futuna', 'WF', 'WLF', '876', '681', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (224, 'Western Samoa', 'EH', 'ESH', '732', '685', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (225, 'Yemen', 'YE', 'YEM', '887', '967', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (226, 'Yugoslavia', 'MK', 'MKD', '446', '381', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (227, 'Zambia', 'ZM', 'ZMB', '894', '260', 1);
insert into Country (countryId, name, a2, a3, number_, idd_, active_) values (228, 'Zimbabwe', 'ZW', 'ZWE', '716', '263', 1);

insert into Region (regionId, countryId, regionCode, name, active_) values (1, 19, 'AL', 'Alabama', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (2, 19, 'AK', 'Alaska', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (3, 19, 'AZ', 'Arizona', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (4, 19, 'AR', 'Arkansas', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (5, 19, 'CA', 'California', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (6, 19, 'CO', 'Colorado', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (7, 19, 'CT', 'Connecticut', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (8, 19, 'DC', 'District of Columbia', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (9, 19, 'DE', 'Delaware', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (10, 19, 'FL', 'Florida', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (11, 19, 'GA', 'Georgia', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (12, 19, 'HI', 'Hawaii', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (13, 19, 'ID', 'Idaho', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (14, 19, 'IL', 'Illinois', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (15, 19, 'IN', 'Indiana', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (16, 19, 'IA', 'Iowa', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (17, 19, 'KS', 'Kansas', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (18, 19, 'KY', 'Kentucky ', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (19, 19, 'LA', 'Louisiana ', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (20, 19, 'ME', 'Maine', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (21, 19, 'MD', 'Maryland', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (22, 19, 'MA', 'Massachusetts', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (23, 19, 'MI', 'Michigan', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (24, 19, 'MN', 'Minnesota', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (25, 19, 'MS', 'Mississippi', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (26, 19, 'MO', 'Missouri', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (27, 19, 'MT', 'Montana', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (28, 19, 'NE', 'Nebraska', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (29, 19, 'NV', 'Nevada', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (30, 19, 'NH', 'New Hampshire', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (31, 19, 'NJ', 'New Jersey', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (32, 19, 'NM', 'New Mexico', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (33, 19, 'NY', 'New York', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (34, 19, 'NC', 'North Carolina', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (35, 19, 'ND', 'North Dakota', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (36, 19, 'OH', 'Ohio', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (37, 19, 'OK', 'Oklahoma ', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (38, 19, 'OR', 'Oregon', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (39, 19, 'PA', 'Pennsylvania', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (40, 19, 'PR', 'Puerto Rico', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (41, 19, 'RI', 'Rhode Island', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (42, 19, 'SC', 'South Carolina', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (43, 19, 'SD', 'South Dakota', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (44, 19, 'TN', 'Tennessee', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (45, 19, 'TX', 'Texas', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (46, 19, 'UT', 'Utah', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (47, 19, 'VT', 'Vermont', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (48, 19, 'VA', 'Virginia', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (49, 19, 'WA', 'Washington', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (50, 19, 'WV', 'West Virginia', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (51, 19, 'WI', 'Wisconsin', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (52, 19, 'WY', 'Wyoming', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (53, 1, 'AL', 'Alberta', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (54, 1, 'BC', 'British Columbia', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (55, 1, 'MB', 'Manitoba', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (56, 1, 'NB', 'New Brunswick', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (57, 1, 'NL', 'Newfoundland and Labrador', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (58, 1, 'NT', 'Northwest Territories', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (59, 1, 'NS', 'Nova Scotia', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (60, 1, 'NU', 'Nunavut', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (61, 1, 'ON', 'Ontario', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (62, 1, 'PE', 'Prince Edward Island', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (63, 1, 'QC', 'Quebec', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (64, 1, 'SK', 'Saskatchewan', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (65, 1, 'YT', 'Yukon', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (66, 15, 'AN', 'Andalusia', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (67, 15, 'AR', 'Aragon', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (68, 15, 'AS', 'Asturias', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (69, 15, 'IB', 'Balearic Islands', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (70, 15, 'PV', 'Basque Country', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (71, 15, 'CN', 'Canary Islands', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (72, 15, 'CB', 'Cantabria', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (73, 15, 'CL', 'Castile and Leon', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (74, 15, 'CM', 'Castile-La Mancha', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (75, 15, 'CT', 'Catalonia', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (76, 15, 'CE', 'Ceuta', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (77, 15, 'EX', 'Extremadura', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (78, 15, 'GA', 'Galicia', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (79, 15, 'LO', 'La Rioja', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (80, 15, 'M', 'Madrid', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (81, 15, 'ML', 'Melilla', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (82, 15, 'MU', 'Murcia', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (83, 15, 'NA', 'Navarra', 1);
insert into Region (regionId, countryId, regionCode, name, active_) values (84, 15, 'VC', 'Valencia', 1);

##
## List types for accounts
##

insert into ListType (listTypeId, name, type_) values (10000, 'Billing', 'com.liferay.portal.model.Account.address');
insert into ListType (listTypeId, name, type_) values (10001, 'Other', 'com.liferay.portal.model.Account.address');
insert into ListType (listTypeId, name, type_) values (10002, 'P.O. Box', 'com.liferay.portal.model.Account.address');
insert into ListType (listTypeId, name, type_) values (10003, 'Shipping', 'com.liferay.portal.model.Account.address');

insert into ListType (listTypeId, name, type_) values (10004, 'E-mail', 'com.liferay.portal.model.Account.emailAddress');
insert into ListType (listTypeId, name, type_) values (10005, 'E-mail 2', 'com.liferay.portal.model.Account.emailAddress');
insert into ListType (listTypeId, name, type_) values (10006, 'E-mail 3', 'com.liferay.portal.model.Account.emailAddress');

insert into ListType (listTypeId, name, type_) values (10007, 'Fax', 'com.liferay.portal.model.Account.phone');
insert into ListType (listTypeId, name, type_) values (10008, 'Local', 'com.liferay.portal.model.Account.phone');
insert into ListType (listTypeId, name, type_) values (10009, 'Other', 'com.liferay.portal.model.Account.phone');
insert into ListType (listTypeId, name, type_) values (10010, 'Toll-Free', 'com.liferay.portal.model.Account.phone');
insert into ListType (listTypeId, name, type_) values (10011, 'TTY', 'com.liferay.portal.model.Account.phone');

insert into ListType (listTypeId, name, type_) values (10012, 'Intranet', 'com.liferay.portal.model.Account.website');
insert into ListType (listTypeId, name, type_) values (10013, 'Public', 'com.liferay.portal.model.Account.website');

##
## List types for contacts
##

insert into ListType (listTypeId, name, type_) values (11000, 'Business', 'com.liferay.portal.model.Contact.address');
insert into ListType (listTypeId, name, type_) values (11001, 'Other', 'com.liferay.portal.model.Contact.address');
insert into ListType (listTypeId, name, type_) values (11002, 'Personal', 'com.liferay.portal.model.Contact.address');

insert into ListType (listTypeId, name, type_) values (11003, 'E-mail', 'com.liferay.portal.model.Contact.emailAddress');
insert into ListType (listTypeId, name, type_) values (11004, 'E-mail 2', 'com.liferay.portal.model.Contact.emailAddress');
insert into ListType (listTypeId, name, type_) values (11005, 'E-mail 3', 'com.liferay.portal.model.Contact.emailAddress');

insert into ListType (listTypeId, name, type_) values (11006, 'Business', 'com.liferay.portal.model.Contact.phone');
insert into ListType (listTypeId, name, type_) values (11007, 'Business Fax', 'com.liferay.portal.model.Contact.phone');
insert into ListType (listTypeId, name, type_) values (11008, 'Mobile', 'com.liferay.portal.model.Contact.phone');
insert into ListType (listTypeId, name, type_) values (11009, 'Other', 'com.liferay.portal.model.Contact.phone');
insert into ListType (listTypeId, name, type_) values (11010, 'Pager', 'com.liferay.portal.model.Contact.phone');
insert into ListType (listTypeId, name, type_) values (11011, 'Personal', 'com.liferay.portal.model.Contact.phone');
insert into ListType (listTypeId, name, type_) values (11012, 'Personal Fax', 'com.liferay.portal.model.Contact.phone');
insert into ListType (listTypeId, name, type_) values (11013, 'TTY', 'com.liferay.portal.model.Contact.phone');

insert into ListType (listTypeId, name, type_) values (11014, 'Dr.', 'com.liferay.portal.model.Contact.prefix');
insert into ListType (listTypeId, name, type_) values (11015, 'Mr.', 'com.liferay.portal.model.Contact.prefix');
insert into ListType (listTypeId, name, type_) values (11016, 'Mrs.', 'com.liferay.portal.model.Contact.prefix');
insert into ListType (listTypeId, name, type_) values (11017, 'Ms.', 'com.liferay.portal.model.Contact.prefix');

insert into ListType (listTypeId, name, type_) values (11020, 'II', 'com.liferay.portal.model.Contact.suffix');
insert into ListType (listTypeId, name, type_) values (11021, 'III', 'com.liferay.portal.model.Contact.suffix');
insert into ListType (listTypeId, name, type_) values (11022, 'IV', 'com.liferay.portal.model.Contact.suffix');
insert into ListType (listTypeId, name, type_) values (11023, 'Jr.', 'com.liferay.portal.model.Contact.suffix');
insert into ListType (listTypeId, name, type_) values (11024, 'PhD.', 'com.liferay.portal.model.Contact.suffix');
insert into ListType (listTypeId, name, type_) values (11025, 'Sr.', 'com.liferay.portal.model.Contact.suffix');

insert into ListType (listTypeId, name, type_) values (11026, 'Blog', 'com.liferay.portal.model.Contact.website');
insert into ListType (listTypeId, name, type_) values (11027, 'Business', 'com.liferay.portal.model.Contact.website');
insert into ListType (listTypeId, name, type_) values (11028, 'Other', 'com.liferay.portal.model.Contact.website');
insert into ListType (listTypeId, name, type_) values (11029, 'Personal', 'com.liferay.portal.model.Contact.website');

##
## List types for organizations
##

insert into ListType (listTypeId, name, type_) values (12000, 'Billing', 'com.liferay.portal.model.Organization.address');
insert into ListType (listTypeId, name, type_) values (12001, 'Other', 'com.liferay.portal.model.Organization.address');
insert into ListType (listTypeId, name, type_) values (12002, 'P.O. Box', 'com.liferay.portal.model.Organization.address');
insert into ListType (listTypeId, name, type_) values (12003, 'Shipping', 'com.liferay.portal.model.Organization.address');

insert into ListType (listTypeId, name, type_) values (12004, 'E-mail', 'com.liferay.portal.model.Organization.emailAddress');
insert into ListType (listTypeId, name, type_) values (12005, 'E-mail 2', 'com.liferay.portal.model.Organization.emailAddress');
insert into ListType (listTypeId, name, type_) values (12006, 'E-mail 3', 'com.liferay.portal.model.Organization.emailAddress');

insert into ListType (listTypeId, name, type_) values (12007, 'Fax', 'com.liferay.portal.model.Organization.phone');
insert into ListType (listTypeId, name, type_) values (12008, 'Local', 'com.liferay.portal.model.Organization.phone');
insert into ListType (listTypeId, name, type_) values (12009, 'Other', 'com.liferay.portal.model.Organization.phone');
insert into ListType (listTypeId, name, type_) values (12010, 'Toll-Free', 'com.liferay.portal.model.Organization.phone');
insert into ListType (listTypeId, name, type_) values (12011, 'TTY', 'com.liferay.portal.model.Organization.phone');

insert into ListType (listTypeId, name, type_) values (12012, 'Administrative', 'com.liferay.portal.model.Organization.service');
insert into ListType (listTypeId, name, type_) values (12013, 'Contracts', 'com.liferay.portal.model.Organization.service');
insert into ListType (listTypeId, name, type_) values (12014, 'Donation', 'com.liferay.portal.model.Organization.service');
insert into ListType (listTypeId, name, type_) values (12015, 'Retail', 'com.liferay.portal.model.Organization.service');
insert into ListType (listTypeId, name, type_) values (12016, 'Training', 'com.liferay.portal.model.Organization.service');

insert into ListType (listTypeId, name, type_) values (12017, 'Full Member', 'com.liferay.portal.model.Organization.status');
insert into ListType (listTypeId, name, type_) values (12018, 'Provisional Member', 'com.liferay.portal.model.Organization.status');

insert into ListType (listTypeId, name, type_) values (12019, 'Intranet', 'com.liferay.portal.model.Organization.website');
insert into ListType (listTypeId, name, type_) values (12020, 'Public', 'com.liferay.portal.model.Organization.website');


insert into Counter values ('com.liferay.counter.model.Counter', 10000);






insert into Company (companyId, accountId, webId, virtualHost, mx) values (1, 6, 'liferay.com', 'localhost', 'liferay.com');
insert into Account_ (accountId, companyId, userId, userName, createDate, modifiedDate, parentAccountId, name, legalName, legalId, legalType, sicCode, tickerSymbol, industry, type_, size_) values (6, 1, 4, '', now(), now(), 0, 'Liferay', 'Liferay, Inc.', '', '', '', '', '', '', '');


insert into ClassName_ (classNameId, value) values (7, 'com.liferay.portal.model.Organization');
insert into ClassName_ (classNameId, value) values (8, 'com.liferay.portal.model.User');


insert into Role_ (roleId, companyId, classNameId, classPK, name, description, type_) values (9, 1, 0, 0, 'Administrator', '', 1);
insert into Role_ (roleId, companyId, classNameId, classPK, name, description, type_) values (10, 1, 0, 0, 'Guest', '', 1);
insert into Role_ (roleId, companyId, classNameId, classPK, name, description, type_) values (11, 1, 0, 0, 'Power User', '', 1);
insert into Role_ (roleId, companyId, classNameId, classPK, name, description, type_) values (12, 1, 0, 0, 'User', '', 1);


insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (13, 1, 4, 0, 0, 0, 0, 'Guest', '/guest', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (14, 1, 13, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (15, 1, 13, 0, 0, 'brochure', '01', 0);

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (16, 1, 4, 0, 0, 0, 0, 'Support', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (17, 1, 16, 1, 0, 'classic', '01', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (18, 1, 16, 0, 0, 'classic', '01', 0);
insert into Layout (plid, groupId, companyId, privateLayout, layoutId, parentLayoutId, name, type_, typeSettings, hidden_, friendlyURL, priority) values (19, 16, 1, 1, 1, 0, '<?xml version="1.0"?>\n\n<root>\n  <name>Support</name>\n</root>', 'portlet', 'layout-template-id=2_columns_ii\ncolumn-1=3,\ncolumn-2=19,', 0, '', 0);


insert into Organization_ (organizationId, companyId, parentOrganizationId, name, recursable, regionId, countryId, statusId, comments) values (20, 1, 0, 'Liferay USA', 0, 5, 19, 12017, '');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (21, 1, 4, 7, 20, 0, 0, '21', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (22, 1, 21, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (23, 1, 21, 0, 0, 'classic', '01', 0);

insert into Organization_ (organizationId, companyId, parentOrganizationId, name, recursable, regionId, countryId, statusId, comments) values (24, 1, 20, 'Liferay Los Angeles', 0, 5, 19, 12017, '');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (25, 1, 4, 7, 24, 0, 0, '25', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (26, 1, 25, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (27, 1, 25, 0, 0, 'classic', '01', 0);

insert into Organization_ (organizationId, companyId, parentOrganizationId, name, recursable, regionId, countryId, statusId, comments) values (28, 1, 20, 'Liferay San Francisco', 0, 5, 19, 12017, '');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (29, 1, 4, 7, 28, 0, 0, '29', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (30, 1, 29, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (31, 1, 29, 0, 0, 'classic', '01', 0);

insert into Organization_ (organizationId, companyId, parentOrganizationId, name, recursable, regionId, countryId, statusId, comments) values (32, 1, 20, 'Liferay Chicago', 0, 14, 19, 12017, '');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (33, 1, 4, 7, 32, 0, 0, '33', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (34, 1, 33, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (35, 1, 33, 0, 0, 'classic', '01', 0);

insert into Organization_ (organizationId, companyId, parentOrganizationId, name, recursable, regionId, countryId, statusId, comments) values (36, 1, 20, 'Liferay New York', 0, 33, 19, 12017, '');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (37, 1, 4, 7, 36, 0, 0, '37', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (38, 1, 37, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (39, 1, 37, 0, 0, 'classic', '01', 0);

insert into Organization_ (organizationId, companyId, parentOrganizationId, name, recursable, regionId, countryId, statusId, comments) values (40, 1, 0, 'Liferay Europe', 0, 0, 4, 12017, '');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (41, 1, 4, 7, 40, 0, 0, '41', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (42, 1, 41, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (43, 1, 41, 0, 0, 'classic', '01', 0);

insert into Organization_ (organizationId, companyId, parentOrganizationId, name, recursable, regionId, countryId, statusId, comments) values (44, 1, 40, 'Liferay Frankfurt', 0, 0, 4, 12017, '');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (45, 1, 4, 7, 44, 0, 0, '45', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (46, 1, 45, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (47, 1, 45, 0, 0, 'classic', '01', 0);

insert into Organization_ (organizationId, companyId, parentOrganizationId, name, recursable, regionId, countryId, statusId, comments) values (48, 1, 40, 'Liferay Madrid', 0, 0, 15, 12017, '');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (49, 1, 4, 7, 48, 0, 0, '49', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (50, 1, 49, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (51, 1, 49, 0, 0, 'classic', '01', 0);

insert into Organization_ (organizationId, companyId, parentOrganizationId, name, recursable, regionId, countryId, statusId, comments) values (52, 1, 0, 'Liferay Asia', 0, 0, 2, 12017, '');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (53, 1, 4, 7, 52, 0, 0, '53', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (54, 1, 53, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (55, 1, 53, 0, 0, 'classic', '01', 0);

insert into Organization_ (organizationId, companyId, parentOrganizationId, name, recursable, regionId, countryId, statusId, comments) values (56, 1, 52, 'Liferay Dalian', 0, 0, 2, 12017, '');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (57, 1, 4, 7, 56, 0, 0, '57', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (58, 1, 57, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (59, 1, 57, 0, 0, 'classic', '01', 0);

insert into Organization_ (organizationId, companyId, parentOrganizationId, name, recursable, regionId, countryId, statusId, comments) values (60, 1, 52, 'Liferay Hong Kong', 0, 0, 2, 12017, '');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (61, 1, 4, 7, 60, 0, 0, '61', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (62, 1, 61, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (63, 1, 61, 0, 0, 'classic', '01', 0);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (4, 1, now(), now(), 1, 5, 'password', 0, 0, '4', 'default@liferay.com', 'Welcome!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (5, 1, 4, '', now(), now(), 6, 0, '', '', '', 1, '1970-01-01');

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (2, 1, now(), now(), 0, 3, 'test', 0, 0, 'joebloggs', 'test@liferay.com', 'Welcome Joe Bloggs!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (3, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Joe', '', 'Bloggs', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (64, 1, 2, 8, 2, 0, 0, '64', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (65, 1, 64, 1, 0, 'classic', '01', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (66, 1, 64, 0, 0, 'classic', '01', 0);
insert into Layout (plid, groupId, companyId, privateLayout, layoutId, parentLayoutId, name, type_, typeSettings, hidden_, friendlyURL, priority) values (67, 64, 1, 1, 1, 0, '<?xml version="1.0"?>\n\n<root>\n  <name>Home A1</name>\n</root>', 'portlet', 'column-1=71_INSTANCE_OY0d,61,65,\ncolumn-2=9,29,79,8,\nlayout-template-id=2_columns_ii\n', 0, '', 0);

insert into Users_Groups values (2, 13);
insert into Users_Groups values (2, 16);

insert into Users_Orgs (userId, organizationId) values (2, 20);
insert into Users_Orgs (userId, organizationId) values (2, 24);

insert into Users_Roles values (2, 9);
insert into Users_Roles values (2, 11);
insert into Users_Roles values (2, 12);


insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (68, 1, now(), now(), 0, 69, 'test', 0, 0, 'lax1', 'test.lax.1@liferay.com', 'Welcome Test LAX 1!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (69, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 1', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (70, 1, 68, 8, 68, 0, 0, '70', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (71, 1, 70, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (72, 1, 70, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (68, 16);

insert into Users_Orgs (userId, organizationId) values (68, 20);
insert into Users_Orgs (userId, organizationId) values (68, 24);

insert into Users_Roles values (68, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (73, 1, now(), now(), 0, 74, 'test', 0, 0, 'lax2', 'test.lax.2@liferay.com', 'Welcome Test LAX 2!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (74, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 2', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (75, 1, 73, 8, 73, 0, 0, '75', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (76, 1, 75, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (77, 1, 75, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (73, 16);

insert into Users_Orgs (userId, organizationId) values (73, 20);
insert into Users_Orgs (userId, organizationId) values (73, 24);

insert into Users_Roles values (73, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (78, 1, now(), now(), 0, 79, 'test', 0, 0, 'lax3', 'test.lax.3@liferay.com', 'Welcome Test LAX 3!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (79, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 3', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (80, 1, 78, 8, 78, 0, 0, '80', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (81, 1, 80, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (82, 1, 80, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (78, 16);

insert into Users_Orgs (userId, organizationId) values (78, 20);
insert into Users_Orgs (userId, organizationId) values (78, 24);

insert into Users_Roles values (78, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (83, 1, now(), now(), 0, 84, 'test', 0, 0, 'lax4', 'test.lax.4@liferay.com', 'Welcome Test LAX 4!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (84, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 4', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (85, 1, 83, 8, 83, 0, 0, '85', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (86, 1, 85, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (87, 1, 85, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (83, 16);

insert into Users_Orgs (userId, organizationId) values (83, 20);
insert into Users_Orgs (userId, organizationId) values (83, 24);

insert into Users_Roles values (83, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (88, 1, now(), now(), 0, 89, 'test', 0, 0, 'lax5', 'test.lax.5@liferay.com', 'Welcome Test LAX 5!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (89, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 5', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (90, 1, 88, 8, 88, 0, 0, '90', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (91, 1, 90, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (92, 1, 90, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (88, 16);

insert into Users_Orgs (userId, organizationId) values (88, 20);
insert into Users_Orgs (userId, organizationId) values (88, 24);

insert into Users_Roles values (88, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (93, 1, now(), now(), 0, 94, 'test', 0, 0, 'lax6', 'test.lax.6@liferay.com', 'Welcome Test LAX 6!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (94, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 6', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (95, 1, 93, 8, 93, 0, 0, '95', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (96, 1, 95, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (97, 1, 95, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (93, 16);

insert into Users_Orgs (userId, organizationId) values (93, 20);
insert into Users_Orgs (userId, organizationId) values (93, 24);

insert into Users_Roles values (93, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (98, 1, now(), now(), 0, 99, 'test', 0, 0, 'lax7', 'test.lax.7@liferay.com', 'Welcome Test LAX 7!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (99, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 7', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (100, 1, 98, 8, 98, 0, 0, '100', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (101, 1, 100, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (102, 1, 100, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (98, 16);

insert into Users_Orgs (userId, organizationId) values (98, 20);
insert into Users_Orgs (userId, organizationId) values (98, 24);

insert into Users_Roles values (98, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (103, 1, now(), now(), 0, 104, 'test', 0, 0, 'lax8', 'test.lax.8@liferay.com', 'Welcome Test LAX 8!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (104, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 8', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (105, 1, 103, 8, 103, 0, 0, '105', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (106, 1, 105, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (107, 1, 105, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (103, 16);

insert into Users_Orgs (userId, organizationId) values (103, 20);
insert into Users_Orgs (userId, organizationId) values (103, 24);

insert into Users_Roles values (103, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (108, 1, now(), now(), 0, 109, 'test', 0, 0, 'lax9', 'test.lax.9@liferay.com', 'Welcome Test LAX 9!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (109, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 9', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (110, 1, 108, 8, 108, 0, 0, '110', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (111, 1, 110, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (112, 1, 110, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (108, 16);

insert into Users_Orgs (userId, organizationId) values (108, 20);
insert into Users_Orgs (userId, organizationId) values (108, 24);

insert into Users_Roles values (108, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (113, 1, now(), now(), 0, 114, 'test', 0, 0, 'lax10', 'test.lax.10@liferay.com', 'Welcome Test LAX 10!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (114, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 10', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (115, 1, 113, 8, 113, 0, 0, '115', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (116, 1, 115, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (117, 1, 115, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (113, 16);

insert into Users_Orgs (userId, organizationId) values (113, 20);
insert into Users_Orgs (userId, organizationId) values (113, 24);

insert into Users_Roles values (113, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (118, 1, now(), now(), 0, 119, 'test', 0, 0, 'lax11', 'test.lax.11@liferay.com', 'Welcome Test LAX 11!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (119, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 11', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (120, 1, 118, 8, 118, 0, 0, '120', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (121, 1, 120, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (122, 1, 120, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (118, 16);

insert into Users_Orgs (userId, organizationId) values (118, 20);
insert into Users_Orgs (userId, organizationId) values (118, 24);

insert into Users_Roles values (118, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (123, 1, now(), now(), 0, 124, 'test', 0, 0, 'lax12', 'test.lax.12@liferay.com', 'Welcome Test LAX 12!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (124, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 12', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (125, 1, 123, 8, 123, 0, 0, '125', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (126, 1, 125, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (127, 1, 125, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (123, 16);

insert into Users_Orgs (userId, organizationId) values (123, 20);
insert into Users_Orgs (userId, organizationId) values (123, 24);

insert into Users_Roles values (123, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (128, 1, now(), now(), 0, 129, 'test', 0, 0, 'lax13', 'test.lax.13@liferay.com', 'Welcome Test LAX 13!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (129, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 13', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (130, 1, 128, 8, 128, 0, 0, '130', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (131, 1, 130, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (132, 1, 130, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (128, 16);

insert into Users_Orgs (userId, organizationId) values (128, 20);
insert into Users_Orgs (userId, organizationId) values (128, 24);

insert into Users_Roles values (128, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (133, 1, now(), now(), 0, 134, 'test', 0, 0, 'lax14', 'test.lax.14@liferay.com', 'Welcome Test LAX 14!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (134, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 14', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (135, 1, 133, 8, 133, 0, 0, '135', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (136, 1, 135, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (137, 1, 135, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (133, 16);

insert into Users_Orgs (userId, organizationId) values (133, 20);
insert into Users_Orgs (userId, organizationId) values (133, 24);

insert into Users_Roles values (133, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (138, 1, now(), now(), 0, 139, 'test', 0, 0, 'lax15', 'test.lax.15@liferay.com', 'Welcome Test LAX 15!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (139, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 15', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (140, 1, 138, 8, 138, 0, 0, '140', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (141, 1, 140, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (142, 1, 140, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (138, 16);

insert into Users_Orgs (userId, organizationId) values (138, 20);
insert into Users_Orgs (userId, organizationId) values (138, 24);

insert into Users_Roles values (138, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (143, 1, now(), now(), 0, 144, 'test', 0, 0, 'lax16', 'test.lax.16@liferay.com', 'Welcome Test LAX 16!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (144, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 16', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (145, 1, 143, 8, 143, 0, 0, '145', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (146, 1, 145, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (147, 1, 145, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (143, 16);

insert into Users_Orgs (userId, organizationId) values (143, 20);
insert into Users_Orgs (userId, organizationId) values (143, 24);

insert into Users_Roles values (143, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (148, 1, now(), now(), 0, 149, 'test', 0, 0, 'lax17', 'test.lax.17@liferay.com', 'Welcome Test LAX 17!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (149, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 17', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (150, 1, 148, 8, 148, 0, 0, '150', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (151, 1, 150, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (152, 1, 150, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (148, 16);

insert into Users_Orgs (userId, organizationId) values (148, 20);
insert into Users_Orgs (userId, organizationId) values (148, 24);

insert into Users_Roles values (148, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (153, 1, now(), now(), 0, 154, 'test', 0, 0, 'lax18', 'test.lax.18@liferay.com', 'Welcome Test LAX 18!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (154, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 18', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (155, 1, 153, 8, 153, 0, 0, '155', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (156, 1, 155, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (157, 1, 155, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (153, 16);

insert into Users_Orgs (userId, organizationId) values (153, 20);
insert into Users_Orgs (userId, organizationId) values (153, 24);

insert into Users_Roles values (153, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (158, 1, now(), now(), 0, 159, 'test', 0, 0, 'lax19', 'test.lax.19@liferay.com', 'Welcome Test LAX 19!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (159, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 19', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (160, 1, 158, 8, 158, 0, 0, '160', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (161, 1, 160, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (162, 1, 160, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (158, 16);

insert into Users_Orgs (userId, organizationId) values (158, 20);
insert into Users_Orgs (userId, organizationId) values (158, 24);

insert into Users_Roles values (158, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (163, 1, now(), now(), 0, 164, 'test', 0, 0, 'lax20', 'test.lax.20@liferay.com', 'Welcome Test LAX 20!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (164, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 20', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (165, 1, 163, 8, 163, 0, 0, '165', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (166, 1, 165, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (167, 1, 165, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (163, 16);

insert into Users_Orgs (userId, organizationId) values (163, 20);
insert into Users_Orgs (userId, organizationId) values (163, 24);

insert into Users_Roles values (163, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (168, 1, now(), now(), 0, 169, 'test', 0, 0, 'lax21', 'test.lax.21@liferay.com', 'Welcome Test LAX 21!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (169, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 21', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (170, 1, 168, 8, 168, 0, 0, '170', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (171, 1, 170, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (172, 1, 170, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (168, 16);

insert into Users_Orgs (userId, organizationId) values (168, 20);
insert into Users_Orgs (userId, organizationId) values (168, 24);

insert into Users_Roles values (168, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (173, 1, now(), now(), 0, 174, 'test', 0, 0, 'lax22', 'test.lax.22@liferay.com', 'Welcome Test LAX 22!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (174, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 22', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (175, 1, 173, 8, 173, 0, 0, '175', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (176, 1, 175, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (177, 1, 175, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (173, 16);

insert into Users_Orgs (userId, organizationId) values (173, 20);
insert into Users_Orgs (userId, organizationId) values (173, 24);

insert into Users_Roles values (173, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (178, 1, now(), now(), 0, 179, 'test', 0, 0, 'lax23', 'test.lax.23@liferay.com', 'Welcome Test LAX 23!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (179, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 23', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (180, 1, 178, 8, 178, 0, 0, '180', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (181, 1, 180, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (182, 1, 180, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (178, 16);

insert into Users_Orgs (userId, organizationId) values (178, 20);
insert into Users_Orgs (userId, organizationId) values (178, 24);

insert into Users_Roles values (178, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (183, 1, now(), now(), 0, 184, 'test', 0, 0, 'lax24', 'test.lax.24@liferay.com', 'Welcome Test LAX 24!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (184, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 24', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (185, 1, 183, 8, 183, 0, 0, '185', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (186, 1, 185, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (187, 1, 185, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (183, 16);

insert into Users_Orgs (userId, organizationId) values (183, 20);
insert into Users_Orgs (userId, organizationId) values (183, 24);

insert into Users_Roles values (183, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (188, 1, now(), now(), 0, 189, 'test', 0, 0, 'lax25', 'test.lax.25@liferay.com', 'Welcome Test LAX 25!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (189, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 25', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (190, 1, 188, 8, 188, 0, 0, '190', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (191, 1, 190, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (192, 1, 190, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (188, 16);

insert into Users_Orgs (userId, organizationId) values (188, 20);
insert into Users_Orgs (userId, organizationId) values (188, 24);

insert into Users_Roles values (188, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (193, 1, now(), now(), 0, 194, 'test', 0, 0, 'lax26', 'test.lax.26@liferay.com', 'Welcome Test LAX 26!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (194, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 26', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (195, 1, 193, 8, 193, 0, 0, '195', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (196, 1, 195, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (197, 1, 195, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (193, 16);

insert into Users_Orgs (userId, organizationId) values (193, 20);
insert into Users_Orgs (userId, organizationId) values (193, 24);

insert into Users_Roles values (193, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (198, 1, now(), now(), 0, 199, 'test', 0, 0, 'lax27', 'test.lax.27@liferay.com', 'Welcome Test LAX 27!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (199, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 27', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (200, 1, 198, 8, 198, 0, 0, '200', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (201, 1, 200, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (202, 1, 200, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (198, 16);

insert into Users_Orgs (userId, organizationId) values (198, 20);
insert into Users_Orgs (userId, organizationId) values (198, 24);

insert into Users_Roles values (198, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (203, 1, now(), now(), 0, 204, 'test', 0, 0, 'lax28', 'test.lax.28@liferay.com', 'Welcome Test LAX 28!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (204, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 28', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (205, 1, 203, 8, 203, 0, 0, '205', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (206, 1, 205, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (207, 1, 205, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (203, 16);

insert into Users_Orgs (userId, organizationId) values (203, 20);
insert into Users_Orgs (userId, organizationId) values (203, 24);

insert into Users_Roles values (203, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (208, 1, now(), now(), 0, 209, 'test', 0, 0, 'lax29', 'test.lax.29@liferay.com', 'Welcome Test LAX 29!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (209, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 29', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (210, 1, 208, 8, 208, 0, 0, '210', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (211, 1, 210, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (212, 1, 210, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (208, 16);

insert into Users_Orgs (userId, organizationId) values (208, 20);
insert into Users_Orgs (userId, organizationId) values (208, 24);

insert into Users_Roles values (208, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (213, 1, now(), now(), 0, 214, 'test', 0, 0, 'lax30', 'test.lax.30@liferay.com', 'Welcome Test LAX 30!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (214, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 30', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (215, 1, 213, 8, 213, 0, 0, '215', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (216, 1, 215, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (217, 1, 215, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (213, 16);

insert into Users_Orgs (userId, organizationId) values (213, 20);
insert into Users_Orgs (userId, organizationId) values (213, 24);

insert into Users_Roles values (213, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (218, 1, now(), now(), 0, 219, 'test', 0, 0, 'lax31', 'test.lax.31@liferay.com', 'Welcome Test LAX 31!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (219, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 31', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (220, 1, 218, 8, 218, 0, 0, '220', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (221, 1, 220, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (222, 1, 220, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (218, 16);

insert into Users_Orgs (userId, organizationId) values (218, 20);
insert into Users_Orgs (userId, organizationId) values (218, 24);

insert into Users_Roles values (218, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (223, 1, now(), now(), 0, 224, 'test', 0, 0, 'lax32', 'test.lax.32@liferay.com', 'Welcome Test LAX 32!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (224, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 32', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (225, 1, 223, 8, 223, 0, 0, '225', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (226, 1, 225, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (227, 1, 225, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (223, 16);

insert into Users_Orgs (userId, organizationId) values (223, 20);
insert into Users_Orgs (userId, organizationId) values (223, 24);

insert into Users_Roles values (223, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (228, 1, now(), now(), 0, 229, 'test', 0, 0, 'lax33', 'test.lax.33@liferay.com', 'Welcome Test LAX 33!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (229, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 33', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (230, 1, 228, 8, 228, 0, 0, '230', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (231, 1, 230, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (232, 1, 230, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (228, 16);

insert into Users_Orgs (userId, organizationId) values (228, 20);
insert into Users_Orgs (userId, organizationId) values (228, 24);

insert into Users_Roles values (228, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (233, 1, now(), now(), 0, 234, 'test', 0, 0, 'lax34', 'test.lax.34@liferay.com', 'Welcome Test LAX 34!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (234, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 34', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (235, 1, 233, 8, 233, 0, 0, '235', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (236, 1, 235, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (237, 1, 235, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (233, 16);

insert into Users_Orgs (userId, organizationId) values (233, 20);
insert into Users_Orgs (userId, organizationId) values (233, 24);

insert into Users_Roles values (233, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (238, 1, now(), now(), 0, 239, 'test', 0, 0, 'lax35', 'test.lax.35@liferay.com', 'Welcome Test LAX 35!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (239, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 35', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (240, 1, 238, 8, 238, 0, 0, '240', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (241, 1, 240, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (242, 1, 240, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (238, 16);

insert into Users_Orgs (userId, organizationId) values (238, 20);
insert into Users_Orgs (userId, organizationId) values (238, 24);

insert into Users_Roles values (238, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (243, 1, now(), now(), 0, 244, 'test', 0, 0, 'lax36', 'test.lax.36@liferay.com', 'Welcome Test LAX 36!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (244, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 36', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (245, 1, 243, 8, 243, 0, 0, '245', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (246, 1, 245, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (247, 1, 245, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (243, 16);

insert into Users_Orgs (userId, organizationId) values (243, 20);
insert into Users_Orgs (userId, organizationId) values (243, 24);

insert into Users_Roles values (243, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (248, 1, now(), now(), 0, 249, 'test', 0, 0, 'lax37', 'test.lax.37@liferay.com', 'Welcome Test LAX 37!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (249, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 37', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (250, 1, 248, 8, 248, 0, 0, '250', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (251, 1, 250, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (252, 1, 250, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (248, 16);

insert into Users_Orgs (userId, organizationId) values (248, 20);
insert into Users_Orgs (userId, organizationId) values (248, 24);

insert into Users_Roles values (248, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (253, 1, now(), now(), 0, 254, 'test', 0, 0, 'lax38', 'test.lax.38@liferay.com', 'Welcome Test LAX 38!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (254, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 38', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (255, 1, 253, 8, 253, 0, 0, '255', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (256, 1, 255, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (257, 1, 255, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (253, 16);

insert into Users_Orgs (userId, organizationId) values (253, 20);
insert into Users_Orgs (userId, organizationId) values (253, 24);

insert into Users_Roles values (253, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (258, 1, now(), now(), 0, 259, 'test', 0, 0, 'lax39', 'test.lax.39@liferay.com', 'Welcome Test LAX 39!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (259, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 39', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (260, 1, 258, 8, 258, 0, 0, '260', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (261, 1, 260, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (262, 1, 260, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (258, 16);

insert into Users_Orgs (userId, organizationId) values (258, 20);
insert into Users_Orgs (userId, organizationId) values (258, 24);

insert into Users_Roles values (258, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (263, 1, now(), now(), 0, 264, 'test', 0, 0, 'lax40', 'test.lax.40@liferay.com', 'Welcome Test LAX 40!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (264, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 40', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (265, 1, 263, 8, 263, 0, 0, '265', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (266, 1, 265, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (267, 1, 265, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (263, 16);

insert into Users_Orgs (userId, organizationId) values (263, 20);
insert into Users_Orgs (userId, organizationId) values (263, 24);

insert into Users_Roles values (263, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (268, 1, now(), now(), 0, 269, 'test', 0, 0, 'lax41', 'test.lax.41@liferay.com', 'Welcome Test LAX 41!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (269, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 41', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (270, 1, 268, 8, 268, 0, 0, '270', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (271, 1, 270, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (272, 1, 270, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (268, 16);

insert into Users_Orgs (userId, organizationId) values (268, 20);
insert into Users_Orgs (userId, organizationId) values (268, 24);

insert into Users_Roles values (268, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (273, 1, now(), now(), 0, 274, 'test', 0, 0, 'lax42', 'test.lax.42@liferay.com', 'Welcome Test LAX 42!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (274, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 42', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (275, 1, 273, 8, 273, 0, 0, '275', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (276, 1, 275, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (277, 1, 275, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (273, 16);

insert into Users_Orgs (userId, organizationId) values (273, 20);
insert into Users_Orgs (userId, organizationId) values (273, 24);

insert into Users_Roles values (273, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (278, 1, now(), now(), 0, 279, 'test', 0, 0, 'lax43', 'test.lax.43@liferay.com', 'Welcome Test LAX 43!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (279, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 43', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (280, 1, 278, 8, 278, 0, 0, '280', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (281, 1, 280, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (282, 1, 280, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (278, 16);

insert into Users_Orgs (userId, organizationId) values (278, 20);
insert into Users_Orgs (userId, organizationId) values (278, 24);

insert into Users_Roles values (278, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (283, 1, now(), now(), 0, 284, 'test', 0, 0, 'lax44', 'test.lax.44@liferay.com', 'Welcome Test LAX 44!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (284, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 44', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (285, 1, 283, 8, 283, 0, 0, '285', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (286, 1, 285, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (287, 1, 285, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (283, 16);

insert into Users_Orgs (userId, organizationId) values (283, 20);
insert into Users_Orgs (userId, organizationId) values (283, 24);

insert into Users_Roles values (283, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (288, 1, now(), now(), 0, 289, 'test', 0, 0, 'lax45', 'test.lax.45@liferay.com', 'Welcome Test LAX 45!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (289, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 45', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (290, 1, 288, 8, 288, 0, 0, '290', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (291, 1, 290, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (292, 1, 290, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (288, 16);

insert into Users_Orgs (userId, organizationId) values (288, 20);
insert into Users_Orgs (userId, organizationId) values (288, 24);

insert into Users_Roles values (288, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (293, 1, now(), now(), 0, 294, 'test', 0, 0, 'lax46', 'test.lax.46@liferay.com', 'Welcome Test LAX 46!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (294, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 46', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (295, 1, 293, 8, 293, 0, 0, '295', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (296, 1, 295, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (297, 1, 295, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (293, 16);

insert into Users_Orgs (userId, organizationId) values (293, 20);
insert into Users_Orgs (userId, organizationId) values (293, 24);

insert into Users_Roles values (293, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (298, 1, now(), now(), 0, 299, 'test', 0, 0, 'lax47', 'test.lax.47@liferay.com', 'Welcome Test LAX 47!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (299, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 47', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (300, 1, 298, 8, 298, 0, 0, '300', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (301, 1, 300, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (302, 1, 300, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (298, 16);

insert into Users_Orgs (userId, organizationId) values (298, 20);
insert into Users_Orgs (userId, organizationId) values (298, 24);

insert into Users_Roles values (298, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (303, 1, now(), now(), 0, 304, 'test', 0, 0, 'lax48', 'test.lax.48@liferay.com', 'Welcome Test LAX 48!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (304, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 48', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (305, 1, 303, 8, 303, 0, 0, '305', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (306, 1, 305, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (307, 1, 305, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (303, 16);

insert into Users_Orgs (userId, organizationId) values (303, 20);
insert into Users_Orgs (userId, organizationId) values (303, 24);

insert into Users_Roles values (303, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (308, 1, now(), now(), 0, 309, 'test', 0, 0, 'lax49', 'test.lax.49@liferay.com', 'Welcome Test LAX 49!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (309, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 49', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (310, 1, 308, 8, 308, 0, 0, '310', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (311, 1, 310, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (312, 1, 310, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (308, 16);

insert into Users_Orgs (userId, organizationId) values (308, 20);
insert into Users_Orgs (userId, organizationId) values (308, 24);

insert into Users_Roles values (308, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (313, 1, now(), now(), 0, 314, 'test', 0, 0, 'lax50', 'test.lax.50@liferay.com', 'Welcome Test LAX 50!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (314, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 50', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (315, 1, 313, 8, 313, 0, 0, '315', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (316, 1, 315, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (317, 1, 315, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (313, 16);

insert into Users_Orgs (userId, organizationId) values (313, 20);
insert into Users_Orgs (userId, organizationId) values (313, 24);

insert into Users_Roles values (313, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (318, 1, now(), now(), 0, 319, 'test', 0, 0, 'lax51', 'test.lax.51@liferay.com', 'Welcome Test LAX 51!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (319, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 51', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (320, 1, 318, 8, 318, 0, 0, '320', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (321, 1, 320, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (322, 1, 320, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (318, 16);

insert into Users_Orgs (userId, organizationId) values (318, 20);
insert into Users_Orgs (userId, organizationId) values (318, 24);

insert into Users_Roles values (318, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (323, 1, now(), now(), 0, 324, 'test', 0, 0, 'lax52', 'test.lax.52@liferay.com', 'Welcome Test LAX 52!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (324, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 52', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (325, 1, 323, 8, 323, 0, 0, '325', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (326, 1, 325, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (327, 1, 325, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (323, 16);

insert into Users_Orgs (userId, organizationId) values (323, 20);
insert into Users_Orgs (userId, organizationId) values (323, 24);

insert into Users_Roles values (323, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (328, 1, now(), now(), 0, 329, 'test', 0, 0, 'lax53', 'test.lax.53@liferay.com', 'Welcome Test LAX 53!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (329, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 53', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (330, 1, 328, 8, 328, 0, 0, '330', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (331, 1, 330, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (332, 1, 330, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (328, 16);

insert into Users_Orgs (userId, organizationId) values (328, 20);
insert into Users_Orgs (userId, organizationId) values (328, 24);

insert into Users_Roles values (328, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (333, 1, now(), now(), 0, 334, 'test', 0, 0, 'lax54', 'test.lax.54@liferay.com', 'Welcome Test LAX 54!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (334, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 54', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (335, 1, 333, 8, 333, 0, 0, '335', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (336, 1, 335, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (337, 1, 335, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (333, 16);

insert into Users_Orgs (userId, organizationId) values (333, 20);
insert into Users_Orgs (userId, organizationId) values (333, 24);

insert into Users_Roles values (333, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (338, 1, now(), now(), 0, 339, 'test', 0, 0, 'lax55', 'test.lax.55@liferay.com', 'Welcome Test LAX 55!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (339, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 55', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (340, 1, 338, 8, 338, 0, 0, '340', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (341, 1, 340, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (342, 1, 340, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (338, 16);

insert into Users_Orgs (userId, organizationId) values (338, 20);
insert into Users_Orgs (userId, organizationId) values (338, 24);

insert into Users_Roles values (338, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (343, 1, now(), now(), 0, 344, 'test', 0, 0, 'lax56', 'test.lax.56@liferay.com', 'Welcome Test LAX 56!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (344, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 56', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (345, 1, 343, 8, 343, 0, 0, '345', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (346, 1, 345, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (347, 1, 345, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (343, 16);

insert into Users_Orgs (userId, organizationId) values (343, 20);
insert into Users_Orgs (userId, organizationId) values (343, 24);

insert into Users_Roles values (343, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (348, 1, now(), now(), 0, 349, 'test', 0, 0, 'lax57', 'test.lax.57@liferay.com', 'Welcome Test LAX 57!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (349, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 57', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (350, 1, 348, 8, 348, 0, 0, '350', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (351, 1, 350, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (352, 1, 350, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (348, 16);

insert into Users_Orgs (userId, organizationId) values (348, 20);
insert into Users_Orgs (userId, organizationId) values (348, 24);

insert into Users_Roles values (348, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (353, 1, now(), now(), 0, 354, 'test', 0, 0, 'lax58', 'test.lax.58@liferay.com', 'Welcome Test LAX 58!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (354, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 58', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (355, 1, 353, 8, 353, 0, 0, '355', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (356, 1, 355, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (357, 1, 355, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (353, 16);

insert into Users_Orgs (userId, organizationId) values (353, 20);
insert into Users_Orgs (userId, organizationId) values (353, 24);

insert into Users_Roles values (353, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (358, 1, now(), now(), 0, 359, 'test', 0, 0, 'lax59', 'test.lax.59@liferay.com', 'Welcome Test LAX 59!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (359, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 59', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (360, 1, 358, 8, 358, 0, 0, '360', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (361, 1, 360, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (362, 1, 360, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (358, 16);

insert into Users_Orgs (userId, organizationId) values (358, 20);
insert into Users_Orgs (userId, organizationId) values (358, 24);

insert into Users_Roles values (358, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (363, 1, now(), now(), 0, 364, 'test', 0, 0, 'lax60', 'test.lax.60@liferay.com', 'Welcome Test LAX 60!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (364, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 60', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (365, 1, 363, 8, 363, 0, 0, '365', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (366, 1, 365, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (367, 1, 365, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (363, 16);

insert into Users_Orgs (userId, organizationId) values (363, 20);
insert into Users_Orgs (userId, organizationId) values (363, 24);

insert into Users_Roles values (363, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (368, 1, now(), now(), 0, 369, 'test', 0, 0, 'lax61', 'test.lax.61@liferay.com', 'Welcome Test LAX 61!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (369, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 61', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (370, 1, 368, 8, 368, 0, 0, '370', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (371, 1, 370, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (372, 1, 370, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (368, 16);

insert into Users_Orgs (userId, organizationId) values (368, 20);
insert into Users_Orgs (userId, organizationId) values (368, 24);

insert into Users_Roles values (368, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (373, 1, now(), now(), 0, 374, 'test', 0, 0, 'lax62', 'test.lax.62@liferay.com', 'Welcome Test LAX 62!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (374, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 62', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (375, 1, 373, 8, 373, 0, 0, '375', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (376, 1, 375, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (377, 1, 375, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (373, 16);

insert into Users_Orgs (userId, organizationId) values (373, 20);
insert into Users_Orgs (userId, organizationId) values (373, 24);

insert into Users_Roles values (373, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (378, 1, now(), now(), 0, 379, 'test', 0, 0, 'lax63', 'test.lax.63@liferay.com', 'Welcome Test LAX 63!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (379, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 63', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (380, 1, 378, 8, 378, 0, 0, '380', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (381, 1, 380, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (382, 1, 380, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (378, 16);

insert into Users_Orgs (userId, organizationId) values (378, 20);
insert into Users_Orgs (userId, organizationId) values (378, 24);

insert into Users_Roles values (378, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (383, 1, now(), now(), 0, 384, 'test', 0, 0, 'lax64', 'test.lax.64@liferay.com', 'Welcome Test LAX 64!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (384, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 64', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (385, 1, 383, 8, 383, 0, 0, '385', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (386, 1, 385, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (387, 1, 385, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (383, 16);

insert into Users_Orgs (userId, organizationId) values (383, 20);
insert into Users_Orgs (userId, organizationId) values (383, 24);

insert into Users_Roles values (383, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (388, 1, now(), now(), 0, 389, 'test', 0, 0, 'lax65', 'test.lax.65@liferay.com', 'Welcome Test LAX 65!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (389, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 65', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (390, 1, 388, 8, 388, 0, 0, '390', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (391, 1, 390, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (392, 1, 390, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (388, 16);

insert into Users_Orgs (userId, organizationId) values (388, 20);
insert into Users_Orgs (userId, organizationId) values (388, 24);

insert into Users_Roles values (388, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (393, 1, now(), now(), 0, 394, 'test', 0, 0, 'lax66', 'test.lax.66@liferay.com', 'Welcome Test LAX 66!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (394, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 66', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (395, 1, 393, 8, 393, 0, 0, '395', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (396, 1, 395, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (397, 1, 395, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (393, 16);

insert into Users_Orgs (userId, organizationId) values (393, 20);
insert into Users_Orgs (userId, organizationId) values (393, 24);

insert into Users_Roles values (393, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (398, 1, now(), now(), 0, 399, 'test', 0, 0, 'lax67', 'test.lax.67@liferay.com', 'Welcome Test LAX 67!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (399, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 67', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (400, 1, 398, 8, 398, 0, 0, '400', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (401, 1, 400, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (402, 1, 400, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (398, 16);

insert into Users_Orgs (userId, organizationId) values (398, 20);
insert into Users_Orgs (userId, organizationId) values (398, 24);

insert into Users_Roles values (398, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (403, 1, now(), now(), 0, 404, 'test', 0, 0, 'lax68', 'test.lax.68@liferay.com', 'Welcome Test LAX 68!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (404, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 68', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (405, 1, 403, 8, 403, 0, 0, '405', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (406, 1, 405, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (407, 1, 405, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (403, 16);

insert into Users_Orgs (userId, organizationId) values (403, 20);
insert into Users_Orgs (userId, organizationId) values (403, 24);

insert into Users_Roles values (403, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (408, 1, now(), now(), 0, 409, 'test', 0, 0, 'lax69', 'test.lax.69@liferay.com', 'Welcome Test LAX 69!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (409, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 69', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (410, 1, 408, 8, 408, 0, 0, '410', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (411, 1, 410, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (412, 1, 410, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (408, 16);

insert into Users_Orgs (userId, organizationId) values (408, 20);
insert into Users_Orgs (userId, organizationId) values (408, 24);

insert into Users_Roles values (408, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (413, 1, now(), now(), 0, 414, 'test', 0, 0, 'lax70', 'test.lax.70@liferay.com', 'Welcome Test LAX 70!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (414, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 70', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (415, 1, 413, 8, 413, 0, 0, '415', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (416, 1, 415, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (417, 1, 415, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (413, 16);

insert into Users_Orgs (userId, organizationId) values (413, 20);
insert into Users_Orgs (userId, organizationId) values (413, 24);

insert into Users_Roles values (413, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (418, 1, now(), now(), 0, 419, 'test', 0, 0, 'lax71', 'test.lax.71@liferay.com', 'Welcome Test LAX 71!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (419, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 71', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (420, 1, 418, 8, 418, 0, 0, '420', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (421, 1, 420, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (422, 1, 420, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (418, 16);

insert into Users_Orgs (userId, organizationId) values (418, 20);
insert into Users_Orgs (userId, organizationId) values (418, 24);

insert into Users_Roles values (418, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (423, 1, now(), now(), 0, 424, 'test', 0, 0, 'lax72', 'test.lax.72@liferay.com', 'Welcome Test LAX 72!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (424, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 72', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (425, 1, 423, 8, 423, 0, 0, '425', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (426, 1, 425, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (427, 1, 425, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (423, 16);

insert into Users_Orgs (userId, organizationId) values (423, 20);
insert into Users_Orgs (userId, organizationId) values (423, 24);

insert into Users_Roles values (423, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (428, 1, now(), now(), 0, 429, 'test', 0, 0, 'lax73', 'test.lax.73@liferay.com', 'Welcome Test LAX 73!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (429, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 73', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (430, 1, 428, 8, 428, 0, 0, '430', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (431, 1, 430, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (432, 1, 430, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (428, 16);

insert into Users_Orgs (userId, organizationId) values (428, 20);
insert into Users_Orgs (userId, organizationId) values (428, 24);

insert into Users_Roles values (428, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (433, 1, now(), now(), 0, 434, 'test', 0, 0, 'lax74', 'test.lax.74@liferay.com', 'Welcome Test LAX 74!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (434, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 74', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (435, 1, 433, 8, 433, 0, 0, '435', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (436, 1, 435, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (437, 1, 435, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (433, 16);

insert into Users_Orgs (userId, organizationId) values (433, 20);
insert into Users_Orgs (userId, organizationId) values (433, 24);

insert into Users_Roles values (433, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (438, 1, now(), now(), 0, 439, 'test', 0, 0, 'lax75', 'test.lax.75@liferay.com', 'Welcome Test LAX 75!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (439, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 75', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (440, 1, 438, 8, 438, 0, 0, '440', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (441, 1, 440, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (442, 1, 440, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (438, 16);

insert into Users_Orgs (userId, organizationId) values (438, 20);
insert into Users_Orgs (userId, organizationId) values (438, 24);

insert into Users_Roles values (438, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (443, 1, now(), now(), 0, 444, 'test', 0, 0, 'lax76', 'test.lax.76@liferay.com', 'Welcome Test LAX 76!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (444, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 76', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (445, 1, 443, 8, 443, 0, 0, '445', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (446, 1, 445, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (447, 1, 445, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (443, 16);

insert into Users_Orgs (userId, organizationId) values (443, 20);
insert into Users_Orgs (userId, organizationId) values (443, 24);

insert into Users_Roles values (443, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (448, 1, now(), now(), 0, 449, 'test', 0, 0, 'lax77', 'test.lax.77@liferay.com', 'Welcome Test LAX 77!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (449, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 77', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (450, 1, 448, 8, 448, 0, 0, '450', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (451, 1, 450, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (452, 1, 450, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (448, 16);

insert into Users_Orgs (userId, organizationId) values (448, 20);
insert into Users_Orgs (userId, organizationId) values (448, 24);

insert into Users_Roles values (448, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (453, 1, now(), now(), 0, 454, 'test', 0, 0, 'lax78', 'test.lax.78@liferay.com', 'Welcome Test LAX 78!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (454, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 78', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (455, 1, 453, 8, 453, 0, 0, '455', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (456, 1, 455, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (457, 1, 455, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (453, 16);

insert into Users_Orgs (userId, organizationId) values (453, 20);
insert into Users_Orgs (userId, organizationId) values (453, 24);

insert into Users_Roles values (453, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (458, 1, now(), now(), 0, 459, 'test', 0, 0, 'lax79', 'test.lax.79@liferay.com', 'Welcome Test LAX 79!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (459, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 79', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (460, 1, 458, 8, 458, 0, 0, '460', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (461, 1, 460, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (462, 1, 460, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (458, 16);

insert into Users_Orgs (userId, organizationId) values (458, 20);
insert into Users_Orgs (userId, organizationId) values (458, 24);

insert into Users_Roles values (458, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (463, 1, now(), now(), 0, 464, 'test', 0, 0, 'lax80', 'test.lax.80@liferay.com', 'Welcome Test LAX 80!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (464, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 80', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (465, 1, 463, 8, 463, 0, 0, '465', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (466, 1, 465, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (467, 1, 465, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (463, 16);

insert into Users_Orgs (userId, organizationId) values (463, 20);
insert into Users_Orgs (userId, organizationId) values (463, 24);

insert into Users_Roles values (463, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (468, 1, now(), now(), 0, 469, 'test', 0, 0, 'lax81', 'test.lax.81@liferay.com', 'Welcome Test LAX 81!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (469, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 81', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (470, 1, 468, 8, 468, 0, 0, '470', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (471, 1, 470, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (472, 1, 470, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (468, 16);

insert into Users_Orgs (userId, organizationId) values (468, 20);
insert into Users_Orgs (userId, organizationId) values (468, 24);

insert into Users_Roles values (468, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (473, 1, now(), now(), 0, 474, 'test', 0, 0, 'lax82', 'test.lax.82@liferay.com', 'Welcome Test LAX 82!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (474, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 82', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (475, 1, 473, 8, 473, 0, 0, '475', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (476, 1, 475, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (477, 1, 475, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (473, 16);

insert into Users_Orgs (userId, organizationId) values (473, 20);
insert into Users_Orgs (userId, organizationId) values (473, 24);

insert into Users_Roles values (473, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (478, 1, now(), now(), 0, 479, 'test', 0, 0, 'lax83', 'test.lax.83@liferay.com', 'Welcome Test LAX 83!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (479, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 83', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (480, 1, 478, 8, 478, 0, 0, '480', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (481, 1, 480, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (482, 1, 480, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (478, 16);

insert into Users_Orgs (userId, organizationId) values (478, 20);
insert into Users_Orgs (userId, organizationId) values (478, 24);

insert into Users_Roles values (478, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (483, 1, now(), now(), 0, 484, 'test', 0, 0, 'lax84', 'test.lax.84@liferay.com', 'Welcome Test LAX 84!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (484, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 84', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (485, 1, 483, 8, 483, 0, 0, '485', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (486, 1, 485, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (487, 1, 485, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (483, 16);

insert into Users_Orgs (userId, organizationId) values (483, 20);
insert into Users_Orgs (userId, organizationId) values (483, 24);

insert into Users_Roles values (483, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (488, 1, now(), now(), 0, 489, 'test', 0, 0, 'lax85', 'test.lax.85@liferay.com', 'Welcome Test LAX 85!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (489, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 85', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (490, 1, 488, 8, 488, 0, 0, '490', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (491, 1, 490, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (492, 1, 490, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (488, 16);

insert into Users_Orgs (userId, organizationId) values (488, 20);
insert into Users_Orgs (userId, organizationId) values (488, 24);

insert into Users_Roles values (488, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (493, 1, now(), now(), 0, 494, 'test', 0, 0, 'lax86', 'test.lax.86@liferay.com', 'Welcome Test LAX 86!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (494, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 86', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (495, 1, 493, 8, 493, 0, 0, '495', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (496, 1, 495, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (497, 1, 495, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (493, 16);

insert into Users_Orgs (userId, organizationId) values (493, 20);
insert into Users_Orgs (userId, organizationId) values (493, 24);

insert into Users_Roles values (493, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (498, 1, now(), now(), 0, 499, 'test', 0, 0, 'lax87', 'test.lax.87@liferay.com', 'Welcome Test LAX 87!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (499, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 87', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (500, 1, 498, 8, 498, 0, 0, '500', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (501, 1, 500, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (502, 1, 500, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (498, 16);

insert into Users_Orgs (userId, organizationId) values (498, 20);
insert into Users_Orgs (userId, organizationId) values (498, 24);

insert into Users_Roles values (498, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (503, 1, now(), now(), 0, 504, 'test', 0, 0, 'lax88', 'test.lax.88@liferay.com', 'Welcome Test LAX 88!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (504, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 88', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (505, 1, 503, 8, 503, 0, 0, '505', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (506, 1, 505, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (507, 1, 505, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (503, 16);

insert into Users_Orgs (userId, organizationId) values (503, 20);
insert into Users_Orgs (userId, organizationId) values (503, 24);

insert into Users_Roles values (503, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (508, 1, now(), now(), 0, 509, 'test', 0, 0, 'lax89', 'test.lax.89@liferay.com', 'Welcome Test LAX 89!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (509, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 89', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (510, 1, 508, 8, 508, 0, 0, '510', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (511, 1, 510, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (512, 1, 510, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (508, 16);

insert into Users_Orgs (userId, organizationId) values (508, 20);
insert into Users_Orgs (userId, organizationId) values (508, 24);

insert into Users_Roles values (508, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (513, 1, now(), now(), 0, 514, 'test', 0, 0, 'lax90', 'test.lax.90@liferay.com', 'Welcome Test LAX 90!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (514, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 90', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (515, 1, 513, 8, 513, 0, 0, '515', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (516, 1, 515, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (517, 1, 515, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (513, 16);

insert into Users_Orgs (userId, organizationId) values (513, 20);
insert into Users_Orgs (userId, organizationId) values (513, 24);

insert into Users_Roles values (513, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (518, 1, now(), now(), 0, 519, 'test', 0, 0, 'lax91', 'test.lax.91@liferay.com', 'Welcome Test LAX 91!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (519, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 91', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (520, 1, 518, 8, 518, 0, 0, '520', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (521, 1, 520, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (522, 1, 520, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (518, 16);

insert into Users_Orgs (userId, organizationId) values (518, 20);
insert into Users_Orgs (userId, organizationId) values (518, 24);

insert into Users_Roles values (518, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (523, 1, now(), now(), 0, 524, 'test', 0, 0, 'lax92', 'test.lax.92@liferay.com', 'Welcome Test LAX 92!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (524, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 92', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (525, 1, 523, 8, 523, 0, 0, '525', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (526, 1, 525, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (527, 1, 525, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (523, 16);

insert into Users_Orgs (userId, organizationId) values (523, 20);
insert into Users_Orgs (userId, organizationId) values (523, 24);

insert into Users_Roles values (523, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (528, 1, now(), now(), 0, 529, 'test', 0, 0, 'lax93', 'test.lax.93@liferay.com', 'Welcome Test LAX 93!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (529, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 93', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (530, 1, 528, 8, 528, 0, 0, '530', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (531, 1, 530, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (532, 1, 530, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (528, 16);

insert into Users_Orgs (userId, organizationId) values (528, 20);
insert into Users_Orgs (userId, organizationId) values (528, 24);

insert into Users_Roles values (528, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (533, 1, now(), now(), 0, 534, 'test', 0, 0, 'lax94', 'test.lax.94@liferay.com', 'Welcome Test LAX 94!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (534, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 94', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (535, 1, 533, 8, 533, 0, 0, '535', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (536, 1, 535, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (537, 1, 535, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (533, 16);

insert into Users_Orgs (userId, organizationId) values (533, 20);
insert into Users_Orgs (userId, organizationId) values (533, 24);

insert into Users_Roles values (533, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (538, 1, now(), now(), 0, 539, 'test', 0, 0, 'lax95', 'test.lax.95@liferay.com', 'Welcome Test LAX 95!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (539, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 95', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (540, 1, 538, 8, 538, 0, 0, '540', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (541, 1, 540, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (542, 1, 540, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (538, 16);

insert into Users_Orgs (userId, organizationId) values (538, 20);
insert into Users_Orgs (userId, organizationId) values (538, 24);

insert into Users_Roles values (538, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (543, 1, now(), now(), 0, 544, 'test', 0, 0, 'lax96', 'test.lax.96@liferay.com', 'Welcome Test LAX 96!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (544, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 96', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (545, 1, 543, 8, 543, 0, 0, '545', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (546, 1, 545, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (547, 1, 545, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (543, 16);

insert into Users_Orgs (userId, organizationId) values (543, 20);
insert into Users_Orgs (userId, organizationId) values (543, 24);

insert into Users_Roles values (543, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (548, 1, now(), now(), 0, 549, 'test', 0, 0, 'lax97', 'test.lax.97@liferay.com', 'Welcome Test LAX 97!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (549, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 97', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (550, 1, 548, 8, 548, 0, 0, '550', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (551, 1, 550, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (552, 1, 550, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (548, 16);

insert into Users_Orgs (userId, organizationId) values (548, 20);
insert into Users_Orgs (userId, organizationId) values (548, 24);

insert into Users_Roles values (548, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (553, 1, now(), now(), 0, 554, 'test', 0, 0, 'lax98', 'test.lax.98@liferay.com', 'Welcome Test LAX 98!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (554, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 98', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (555, 1, 553, 8, 553, 0, 0, '555', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (556, 1, 555, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (557, 1, 555, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (553, 16);

insert into Users_Orgs (userId, organizationId) values (553, 20);
insert into Users_Orgs (userId, organizationId) values (553, 24);

insert into Users_Roles values (553, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (558, 1, now(), now(), 0, 559, 'test', 0, 0, 'lax99', 'test.lax.99@liferay.com', 'Welcome Test LAX 99!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (559, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 99', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (560, 1, 558, 8, 558, 0, 0, '560', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (561, 1, 560, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (562, 1, 560, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (558, 16);

insert into Users_Orgs (userId, organizationId) values (558, 20);
insert into Users_Orgs (userId, organizationId) values (558, 24);

insert into Users_Roles values (558, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (563, 1, now(), now(), 0, 564, 'test', 0, 0, 'lax100', 'test.lax.100@liferay.com', 'Welcome Test LAX 100!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (564, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'LAX 100', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (565, 1, 563, 8, 563, 0, 0, '565', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (566, 1, 565, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (567, 1, 565, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (563, 16);

insert into Users_Orgs (userId, organizationId) values (563, 20);
insert into Users_Orgs (userId, organizationId) values (563, 24);

insert into Users_Roles values (563, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (568, 1, now(), now(), 0, 569, 'test', 0, 0, 'sfo1', 'test.sfo.1@liferay.com', 'Welcome Test SFO 1!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (569, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'SFO 1', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (570, 1, 568, 8, 568, 0, 0, '570', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (571, 1, 570, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (572, 1, 570, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (568, 16);

insert into Users_Orgs (userId, organizationId) values (568, 20);
insert into Users_Orgs (userId, organizationId) values (568, 28);

insert into Users_Roles values (568, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (573, 1, now(), now(), 0, 574, 'test', 0, 0, 'sfo2', 'test.sfo.2@liferay.com', 'Welcome Test SFO 2!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (574, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'SFO 2', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (575, 1, 573, 8, 573, 0, 0, '575', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (576, 1, 575, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (577, 1, 575, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (573, 16);

insert into Users_Orgs (userId, organizationId) values (573, 20);
insert into Users_Orgs (userId, organizationId) values (573, 28);

insert into Users_Roles values (573, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (578, 1, now(), now(), 0, 579, 'test', 0, 0, 'sfo3', 'test.sfo.3@liferay.com', 'Welcome Test SFO 3!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (579, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'SFO 3', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (580, 1, 578, 8, 578, 0, 0, '580', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (581, 1, 580, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (582, 1, 580, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (578, 16);

insert into Users_Orgs (userId, organizationId) values (578, 20);
insert into Users_Orgs (userId, organizationId) values (578, 28);

insert into Users_Roles values (578, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (583, 1, now(), now(), 0, 584, 'test', 0, 0, 'sfo4', 'test.sfo.4@liferay.com', 'Welcome Test SFO 4!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (584, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'SFO 4', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (585, 1, 583, 8, 583, 0, 0, '585', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (586, 1, 585, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (587, 1, 585, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (583, 16);

insert into Users_Orgs (userId, organizationId) values (583, 20);
insert into Users_Orgs (userId, organizationId) values (583, 28);

insert into Users_Roles values (583, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (588, 1, now(), now(), 0, 589, 'test', 0, 0, 'sfo5', 'test.sfo.5@liferay.com', 'Welcome Test SFO 5!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (589, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'SFO 5', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (590, 1, 588, 8, 588, 0, 0, '590', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (591, 1, 590, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (592, 1, 590, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (588, 16);

insert into Users_Orgs (userId, organizationId) values (588, 20);
insert into Users_Orgs (userId, organizationId) values (588, 28);

insert into Users_Roles values (588, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (593, 1, now(), now(), 0, 594, 'test', 0, 0, 'sfo6', 'test.sfo.6@liferay.com', 'Welcome Test SFO 6!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (594, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'SFO 6', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (595, 1, 593, 8, 593, 0, 0, '595', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (596, 1, 595, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (597, 1, 595, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (593, 16);

insert into Users_Orgs (userId, organizationId) values (593, 20);
insert into Users_Orgs (userId, organizationId) values (593, 28);

insert into Users_Roles values (593, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (598, 1, now(), now(), 0, 599, 'test', 0, 0, 'sfo7', 'test.sfo.7@liferay.com', 'Welcome Test SFO 7!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (599, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'SFO 7', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (600, 1, 598, 8, 598, 0, 0, '600', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (601, 1, 600, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (602, 1, 600, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (598, 16);

insert into Users_Orgs (userId, organizationId) values (598, 20);
insert into Users_Orgs (userId, organizationId) values (598, 28);

insert into Users_Roles values (598, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (603, 1, now(), now(), 0, 604, 'test', 0, 0, 'sfo8', 'test.sfo.8@liferay.com', 'Welcome Test SFO 8!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (604, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'SFO 8', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (605, 1, 603, 8, 603, 0, 0, '605', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (606, 1, 605, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (607, 1, 605, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (603, 16);

insert into Users_Orgs (userId, organizationId) values (603, 20);
insert into Users_Orgs (userId, organizationId) values (603, 28);

insert into Users_Roles values (603, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (608, 1, now(), now(), 0, 609, 'test', 0, 0, 'sfo9', 'test.sfo.9@liferay.com', 'Welcome Test SFO 9!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (609, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'SFO 9', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (610, 1, 608, 8, 608, 0, 0, '610', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (611, 1, 610, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (612, 1, 610, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (608, 16);

insert into Users_Orgs (userId, organizationId) values (608, 20);
insert into Users_Orgs (userId, organizationId) values (608, 28);

insert into Users_Roles values (608, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (613, 1, now(), now(), 0, 614, 'test', 0, 0, 'sfo10', 'test.sfo.10@liferay.com', 'Welcome Test SFO 10!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (614, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'SFO 10', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (615, 1, 613, 8, 613, 0, 0, '615', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (616, 1, 615, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (617, 1, 615, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (613, 16);

insert into Users_Orgs (userId, organizationId) values (613, 20);
insert into Users_Orgs (userId, organizationId) values (613, 28);

insert into Users_Roles values (613, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (618, 1, now(), now(), 0, 619, 'test', 0, 0, 'ord1', 'test.ord.1@liferay.com', 'Welcome Test ORD 1!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (619, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'ORD 1', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (620, 1, 618, 8, 618, 0, 0, '620', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (621, 1, 620, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (622, 1, 620, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (618, 16);

insert into Users_Orgs (userId, organizationId) values (618, 20);
insert into Users_Orgs (userId, organizationId) values (618, 32);

insert into Users_Roles values (618, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (623, 1, now(), now(), 0, 624, 'test', 0, 0, 'ord2', 'test.ord.2@liferay.com', 'Welcome Test ORD 2!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (624, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'ORD 2', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (625, 1, 623, 8, 623, 0, 0, '625', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (626, 1, 625, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (627, 1, 625, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (623, 16);

insert into Users_Orgs (userId, organizationId) values (623, 20);
insert into Users_Orgs (userId, organizationId) values (623, 32);

insert into Users_Roles values (623, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (628, 1, now(), now(), 0, 629, 'test', 0, 0, 'ord3', 'test.ord.3@liferay.com', 'Welcome Test ORD 3!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (629, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'ORD 3', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (630, 1, 628, 8, 628, 0, 0, '630', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (631, 1, 630, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (632, 1, 630, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (628, 16);

insert into Users_Orgs (userId, organizationId) values (628, 20);
insert into Users_Orgs (userId, organizationId) values (628, 32);

insert into Users_Roles values (628, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (633, 1, now(), now(), 0, 634, 'test', 0, 0, 'ord4', 'test.ord.4@liferay.com', 'Welcome Test ORD 4!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (634, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'ORD 4', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (635, 1, 633, 8, 633, 0, 0, '635', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (636, 1, 635, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (637, 1, 635, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (633, 16);

insert into Users_Orgs (userId, organizationId) values (633, 20);
insert into Users_Orgs (userId, organizationId) values (633, 32);

insert into Users_Roles values (633, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (638, 1, now(), now(), 0, 639, 'test', 0, 0, 'ord5', 'test.ord.5@liferay.com', 'Welcome Test ORD 5!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (639, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'ORD 5', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (640, 1, 638, 8, 638, 0, 0, '640', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (641, 1, 640, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (642, 1, 640, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (638, 16);

insert into Users_Orgs (userId, organizationId) values (638, 20);
insert into Users_Orgs (userId, organizationId) values (638, 32);

insert into Users_Roles values (638, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (643, 1, now(), now(), 0, 644, 'test', 0, 0, 'ord6', 'test.ord.6@liferay.com', 'Welcome Test ORD 6!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (644, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'ORD 6', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (645, 1, 643, 8, 643, 0, 0, '645', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (646, 1, 645, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (647, 1, 645, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (643, 16);

insert into Users_Orgs (userId, organizationId) values (643, 20);
insert into Users_Orgs (userId, organizationId) values (643, 32);

insert into Users_Roles values (643, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (648, 1, now(), now(), 0, 649, 'test', 0, 0, 'ord7', 'test.ord.7@liferay.com', 'Welcome Test ORD 7!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (649, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'ORD 7', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (650, 1, 648, 8, 648, 0, 0, '650', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (651, 1, 650, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (652, 1, 650, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (648, 16);

insert into Users_Orgs (userId, organizationId) values (648, 20);
insert into Users_Orgs (userId, organizationId) values (648, 32);

insert into Users_Roles values (648, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (653, 1, now(), now(), 0, 654, 'test', 0, 0, 'ord8', 'test.ord.8@liferay.com', 'Welcome Test ORD 8!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (654, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'ORD 8', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (655, 1, 653, 8, 653, 0, 0, '655', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (656, 1, 655, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (657, 1, 655, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (653, 16);

insert into Users_Orgs (userId, organizationId) values (653, 20);
insert into Users_Orgs (userId, organizationId) values (653, 32);

insert into Users_Roles values (653, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (658, 1, now(), now(), 0, 659, 'test', 0, 0, 'ord9', 'test.ord.9@liferay.com', 'Welcome Test ORD 9!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (659, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'ORD 9', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (660, 1, 658, 8, 658, 0, 0, '660', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (661, 1, 660, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (662, 1, 660, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (658, 16);

insert into Users_Orgs (userId, organizationId) values (658, 20);
insert into Users_Orgs (userId, organizationId) values (658, 32);

insert into Users_Roles values (658, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (663, 1, now(), now(), 0, 664, 'test', 0, 0, 'ord10', 'test.ord.10@liferay.com', 'Welcome Test ORD 10!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (664, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'ORD 10', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (665, 1, 663, 8, 663, 0, 0, '665', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (666, 1, 665, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (667, 1, 665, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (663, 16);

insert into Users_Orgs (userId, organizationId) values (663, 20);
insert into Users_Orgs (userId, organizationId) values (663, 32);

insert into Users_Roles values (663, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (668, 1, now(), now(), 0, 669, 'test', 0, 0, 'nyc1', 'test.nyc.1@liferay.com', 'Welcome Test NYC 1!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (669, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'NYC 1', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (670, 1, 668, 8, 668, 0, 0, '670', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (671, 1, 670, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (672, 1, 670, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (668, 16);

insert into Users_Orgs (userId, organizationId) values (668, 20);
insert into Users_Orgs (userId, organizationId) values (668, 36);

insert into Users_Roles values (668, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (673, 1, now(), now(), 0, 674, 'test', 0, 0, 'nyc2', 'test.nyc.2@liferay.com', 'Welcome Test NYC 2!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (674, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'NYC 2', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (675, 1, 673, 8, 673, 0, 0, '675', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (676, 1, 675, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (677, 1, 675, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (673, 16);

insert into Users_Orgs (userId, organizationId) values (673, 20);
insert into Users_Orgs (userId, organizationId) values (673, 36);

insert into Users_Roles values (673, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (678, 1, now(), now(), 0, 679, 'test', 0, 0, 'nyc3', 'test.nyc.3@liferay.com', 'Welcome Test NYC 3!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (679, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'NYC 3', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (680, 1, 678, 8, 678, 0, 0, '680', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (681, 1, 680, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (682, 1, 680, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (678, 16);

insert into Users_Orgs (userId, organizationId) values (678, 20);
insert into Users_Orgs (userId, organizationId) values (678, 36);

insert into Users_Roles values (678, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (683, 1, now(), now(), 0, 684, 'test', 0, 0, 'nyc4', 'test.nyc.4@liferay.com', 'Welcome Test NYC 4!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (684, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'NYC 4', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (685, 1, 683, 8, 683, 0, 0, '685', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (686, 1, 685, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (687, 1, 685, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (683, 16);

insert into Users_Orgs (userId, organizationId) values (683, 20);
insert into Users_Orgs (userId, organizationId) values (683, 36);

insert into Users_Roles values (683, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (688, 1, now(), now(), 0, 689, 'test', 0, 0, 'nyc5', 'test.nyc.5@liferay.com', 'Welcome Test NYC 5!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (689, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'NYC 5', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (690, 1, 688, 8, 688, 0, 0, '690', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (691, 1, 690, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (692, 1, 690, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (688, 16);

insert into Users_Orgs (userId, organizationId) values (688, 20);
insert into Users_Orgs (userId, organizationId) values (688, 36);

insert into Users_Roles values (688, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (693, 1, now(), now(), 0, 694, 'test', 0, 0, 'nyc6', 'test.nyc.6@liferay.com', 'Welcome Test NYC 6!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (694, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'NYC 6', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (695, 1, 693, 8, 693, 0, 0, '695', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (696, 1, 695, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (697, 1, 695, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (693, 16);

insert into Users_Orgs (userId, organizationId) values (693, 20);
insert into Users_Orgs (userId, organizationId) values (693, 36);

insert into Users_Roles values (693, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (698, 1, now(), now(), 0, 699, 'test', 0, 0, 'nyc7', 'test.nyc.7@liferay.com', 'Welcome Test NYC 7!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (699, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'NYC 7', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (700, 1, 698, 8, 698, 0, 0, '700', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (701, 1, 700, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (702, 1, 700, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (698, 16);

insert into Users_Orgs (userId, organizationId) values (698, 20);
insert into Users_Orgs (userId, organizationId) values (698, 36);

insert into Users_Roles values (698, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (703, 1, now(), now(), 0, 704, 'test', 0, 0, 'nyc8', 'test.nyc.8@liferay.com', 'Welcome Test NYC 8!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (704, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'NYC 8', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (705, 1, 703, 8, 703, 0, 0, '705', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (706, 1, 705, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (707, 1, 705, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (703, 16);

insert into Users_Orgs (userId, organizationId) values (703, 20);
insert into Users_Orgs (userId, organizationId) values (703, 36);

insert into Users_Roles values (703, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (708, 1, now(), now(), 0, 709, 'test', 0, 0, 'nyc9', 'test.nyc.9@liferay.com', 'Welcome Test NYC 9!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (709, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'NYC 9', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (710, 1, 708, 8, 708, 0, 0, '710', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (711, 1, 710, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (712, 1, 710, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (708, 16);

insert into Users_Orgs (userId, organizationId) values (708, 20);
insert into Users_Orgs (userId, organizationId) values (708, 36);

insert into Users_Roles values (708, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (713, 1, now(), now(), 0, 714, 'test', 0, 0, 'nyc10', 'test.nyc.10@liferay.com', 'Welcome Test NYC 10!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (714, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'NYC 10', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (715, 1, 713, 8, 713, 0, 0, '715', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (716, 1, 715, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (717, 1, 715, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (713, 16);

insert into Users_Orgs (userId, organizationId) values (713, 20);
insert into Users_Orgs (userId, organizationId) values (713, 36);

insert into Users_Roles values (713, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (718, 1, now(), now(), 0, 719, 'test', 0, 0, 'fra1', 'test.fra.1@liferay.com', 'Welcome Test FRA 1!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (719, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'FRA 1', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (720, 1, 718, 8, 718, 0, 0, '720', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (721, 1, 720, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (722, 1, 720, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (718, 16);

insert into Users_Orgs (userId, organizationId) values (718, 40);
insert into Users_Orgs (userId, organizationId) values (718, 44);

insert into Users_Roles values (718, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (723, 1, now(), now(), 0, 724, 'test', 0, 0, 'fra2', 'test.fra.2@liferay.com', 'Welcome Test FRA 2!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (724, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'FRA 2', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (725, 1, 723, 8, 723, 0, 0, '725', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (726, 1, 725, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (727, 1, 725, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (723, 16);

insert into Users_Orgs (userId, organizationId) values (723, 40);
insert into Users_Orgs (userId, organizationId) values (723, 44);

insert into Users_Roles values (723, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (728, 1, now(), now(), 0, 729, 'test', 0, 0, 'fra3', 'test.fra.3@liferay.com', 'Welcome Test FRA 3!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (729, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'FRA 3', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (730, 1, 728, 8, 728, 0, 0, '730', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (731, 1, 730, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (732, 1, 730, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (728, 16);

insert into Users_Orgs (userId, organizationId) values (728, 40);
insert into Users_Orgs (userId, organizationId) values (728, 44);

insert into Users_Roles values (728, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (733, 1, now(), now(), 0, 734, 'test', 0, 0, 'fra4', 'test.fra.4@liferay.com', 'Welcome Test FRA 4!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (734, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'FRA 4', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (735, 1, 733, 8, 733, 0, 0, '735', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (736, 1, 735, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (737, 1, 735, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (733, 16);

insert into Users_Orgs (userId, organizationId) values (733, 40);
insert into Users_Orgs (userId, organizationId) values (733, 44);

insert into Users_Roles values (733, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (738, 1, now(), now(), 0, 739, 'test', 0, 0, 'fra5', 'test.fra.5@liferay.com', 'Welcome Test FRA 5!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (739, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'FRA 5', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (740, 1, 738, 8, 738, 0, 0, '740', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (741, 1, 740, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (742, 1, 740, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (738, 16);

insert into Users_Orgs (userId, organizationId) values (738, 40);
insert into Users_Orgs (userId, organizationId) values (738, 44);

insert into Users_Roles values (738, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (743, 1, now(), now(), 0, 744, 'test', 0, 0, 'fra6', 'test.fra.6@liferay.com', 'Welcome Test FRA 6!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (744, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'FRA 6', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (745, 1, 743, 8, 743, 0, 0, '745', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (746, 1, 745, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (747, 1, 745, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (743, 16);

insert into Users_Orgs (userId, organizationId) values (743, 40);
insert into Users_Orgs (userId, organizationId) values (743, 44);

insert into Users_Roles values (743, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (748, 1, now(), now(), 0, 749, 'test', 0, 0, 'fra7', 'test.fra.7@liferay.com', 'Welcome Test FRA 7!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (749, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'FRA 7', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (750, 1, 748, 8, 748, 0, 0, '750', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (751, 1, 750, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (752, 1, 750, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (748, 16);

insert into Users_Orgs (userId, organizationId) values (748, 40);
insert into Users_Orgs (userId, organizationId) values (748, 44);

insert into Users_Roles values (748, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (753, 1, now(), now(), 0, 754, 'test', 0, 0, 'fra8', 'test.fra.8@liferay.com', 'Welcome Test FRA 8!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (754, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'FRA 8', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (755, 1, 753, 8, 753, 0, 0, '755', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (756, 1, 755, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (757, 1, 755, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (753, 16);

insert into Users_Orgs (userId, organizationId) values (753, 40);
insert into Users_Orgs (userId, organizationId) values (753, 44);

insert into Users_Roles values (753, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (758, 1, now(), now(), 0, 759, 'test', 0, 0, 'fra9', 'test.fra.9@liferay.com', 'Welcome Test FRA 9!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (759, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'FRA 9', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (760, 1, 758, 8, 758, 0, 0, '760', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (761, 1, 760, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (762, 1, 760, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (758, 16);

insert into Users_Orgs (userId, organizationId) values (758, 40);
insert into Users_Orgs (userId, organizationId) values (758, 44);

insert into Users_Roles values (758, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (763, 1, now(), now(), 0, 764, 'test', 0, 0, 'fra10', 'test.fra.10@liferay.com', 'Welcome Test FRA 10!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (764, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'FRA 10', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (765, 1, 763, 8, 763, 0, 0, '765', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (766, 1, 765, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (767, 1, 765, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (763, 16);

insert into Users_Orgs (userId, organizationId) values (763, 40);
insert into Users_Orgs (userId, organizationId) values (763, 44);

insert into Users_Roles values (763, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (768, 1, now(), now(), 0, 769, 'test', 0, 0, 'mad1', 'test.mad.1@liferay.com', 'Welcome Test MAD 1!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (769, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'MAD 1', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (770, 1, 768, 8, 768, 0, 0, '770', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (771, 1, 770, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (772, 1, 770, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (768, 16);

insert into Users_Orgs (userId, organizationId) values (768, 40);
insert into Users_Orgs (userId, organizationId) values (768, 48);

insert into Users_Roles values (768, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (773, 1, now(), now(), 0, 774, 'test', 0, 0, 'mad2', 'test.mad.2@liferay.com', 'Welcome Test MAD 2!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (774, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'MAD 2', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (775, 1, 773, 8, 773, 0, 0, '775', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (776, 1, 775, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (777, 1, 775, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (773, 16);

insert into Users_Orgs (userId, organizationId) values (773, 40);
insert into Users_Orgs (userId, organizationId) values (773, 48);

insert into Users_Roles values (773, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (778, 1, now(), now(), 0, 779, 'test', 0, 0, 'mad3', 'test.mad.3@liferay.com', 'Welcome Test MAD 3!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (779, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'MAD 3', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (780, 1, 778, 8, 778, 0, 0, '780', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (781, 1, 780, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (782, 1, 780, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (778, 16);

insert into Users_Orgs (userId, organizationId) values (778, 40);
insert into Users_Orgs (userId, organizationId) values (778, 48);

insert into Users_Roles values (778, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (783, 1, now(), now(), 0, 784, 'test', 0, 0, 'mad4', 'test.mad.4@liferay.com', 'Welcome Test MAD 4!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (784, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'MAD 4', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (785, 1, 783, 8, 783, 0, 0, '785', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (786, 1, 785, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (787, 1, 785, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (783, 16);

insert into Users_Orgs (userId, organizationId) values (783, 40);
insert into Users_Orgs (userId, organizationId) values (783, 48);

insert into Users_Roles values (783, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (788, 1, now(), now(), 0, 789, 'test', 0, 0, 'mad5', 'test.mad.5@liferay.com', 'Welcome Test MAD 5!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (789, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'MAD 5', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (790, 1, 788, 8, 788, 0, 0, '790', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (791, 1, 790, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (792, 1, 790, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (788, 16);

insert into Users_Orgs (userId, organizationId) values (788, 40);
insert into Users_Orgs (userId, organizationId) values (788, 48);

insert into Users_Roles values (788, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (793, 1, now(), now(), 0, 794, 'test', 0, 0, 'mad6', 'test.mad.6@liferay.com', 'Welcome Test MAD 6!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (794, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'MAD 6', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (795, 1, 793, 8, 793, 0, 0, '795', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (796, 1, 795, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (797, 1, 795, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (793, 16);

insert into Users_Orgs (userId, organizationId) values (793, 40);
insert into Users_Orgs (userId, organizationId) values (793, 48);

insert into Users_Roles values (793, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (798, 1, now(), now(), 0, 799, 'test', 0, 0, 'mad7', 'test.mad.7@liferay.com', 'Welcome Test MAD 7!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (799, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'MAD 7', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (800, 1, 798, 8, 798, 0, 0, '800', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (801, 1, 800, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (802, 1, 800, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (798, 16);

insert into Users_Orgs (userId, organizationId) values (798, 40);
insert into Users_Orgs (userId, organizationId) values (798, 48);

insert into Users_Roles values (798, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (803, 1, now(), now(), 0, 804, 'test', 0, 0, 'mad8', 'test.mad.8@liferay.com', 'Welcome Test MAD 8!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (804, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'MAD 8', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (805, 1, 803, 8, 803, 0, 0, '805', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (806, 1, 805, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (807, 1, 805, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (803, 16);

insert into Users_Orgs (userId, organizationId) values (803, 40);
insert into Users_Orgs (userId, organizationId) values (803, 48);

insert into Users_Roles values (803, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (808, 1, now(), now(), 0, 809, 'test', 0, 0, 'mad9', 'test.mad.9@liferay.com', 'Welcome Test MAD 9!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (809, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'MAD 9', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (810, 1, 808, 8, 808, 0, 0, '810', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (811, 1, 810, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (812, 1, 810, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (808, 16);

insert into Users_Orgs (userId, organizationId) values (808, 40);
insert into Users_Orgs (userId, organizationId) values (808, 48);

insert into Users_Roles values (808, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (813, 1, now(), now(), 0, 814, 'test', 0, 0, 'mad10', 'test.mad.10@liferay.com', 'Welcome Test MAD 10!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (814, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'MAD 10', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (815, 1, 813, 8, 813, 0, 0, '815', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (816, 1, 815, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (817, 1, 815, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (813, 16);

insert into Users_Orgs (userId, organizationId) values (813, 40);
insert into Users_Orgs (userId, organizationId) values (813, 48);

insert into Users_Roles values (813, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (818, 1, now(), now(), 0, 819, 'test', 0, 0, 'dlc1', 'test.dlc.1@liferay.com', 'Welcome Test DLC 1!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (819, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'DLC 1', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (820, 1, 818, 8, 818, 0, 0, '820', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (821, 1, 820, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (822, 1, 820, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (818, 16);

insert into Users_Orgs (userId, organizationId) values (818, 52);
insert into Users_Orgs (userId, organizationId) values (818, 56);

insert into Users_Roles values (818, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (823, 1, now(), now(), 0, 824, 'test', 0, 0, 'dlc2', 'test.dlc.2@liferay.com', 'Welcome Test DLC 2!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (824, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'DLC 2', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (825, 1, 823, 8, 823, 0, 0, '825', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (826, 1, 825, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (827, 1, 825, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (823, 16);

insert into Users_Orgs (userId, organizationId) values (823, 52);
insert into Users_Orgs (userId, organizationId) values (823, 56);

insert into Users_Roles values (823, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (828, 1, now(), now(), 0, 829, 'test', 0, 0, 'dlc3', 'test.dlc.3@liferay.com', 'Welcome Test DLC 3!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (829, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'DLC 3', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (830, 1, 828, 8, 828, 0, 0, '830', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (831, 1, 830, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (832, 1, 830, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (828, 16);

insert into Users_Orgs (userId, organizationId) values (828, 52);
insert into Users_Orgs (userId, organizationId) values (828, 56);

insert into Users_Roles values (828, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (833, 1, now(), now(), 0, 834, 'test', 0, 0, 'dlc4', 'test.dlc.4@liferay.com', 'Welcome Test DLC 4!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (834, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'DLC 4', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (835, 1, 833, 8, 833, 0, 0, '835', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (836, 1, 835, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (837, 1, 835, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (833, 16);

insert into Users_Orgs (userId, organizationId) values (833, 52);
insert into Users_Orgs (userId, organizationId) values (833, 56);

insert into Users_Roles values (833, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (838, 1, now(), now(), 0, 839, 'test', 0, 0, 'dlc5', 'test.dlc.5@liferay.com', 'Welcome Test DLC 5!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (839, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'DLC 5', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (840, 1, 838, 8, 838, 0, 0, '840', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (841, 1, 840, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (842, 1, 840, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (838, 16);

insert into Users_Orgs (userId, organizationId) values (838, 52);
insert into Users_Orgs (userId, organizationId) values (838, 56);

insert into Users_Roles values (838, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (843, 1, now(), now(), 0, 844, 'test', 0, 0, 'dlc6', 'test.dlc.6@liferay.com', 'Welcome Test DLC 6!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (844, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'DLC 6', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (845, 1, 843, 8, 843, 0, 0, '845', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (846, 1, 845, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (847, 1, 845, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (843, 16);

insert into Users_Orgs (userId, organizationId) values (843, 52);
insert into Users_Orgs (userId, organizationId) values (843, 56);

insert into Users_Roles values (843, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (848, 1, now(), now(), 0, 849, 'test', 0, 0, 'dlc7', 'test.dlc.7@liferay.com', 'Welcome Test DLC 7!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (849, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'DLC 7', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (850, 1, 848, 8, 848, 0, 0, '850', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (851, 1, 850, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (852, 1, 850, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (848, 16);

insert into Users_Orgs (userId, organizationId) values (848, 52);
insert into Users_Orgs (userId, organizationId) values (848, 56);

insert into Users_Roles values (848, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (853, 1, now(), now(), 0, 854, 'test', 0, 0, 'dlc8', 'test.dlc.8@liferay.com', 'Welcome Test DLC 8!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (854, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'DLC 8', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (855, 1, 853, 8, 853, 0, 0, '855', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (856, 1, 855, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (857, 1, 855, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (853, 16);

insert into Users_Orgs (userId, organizationId) values (853, 52);
insert into Users_Orgs (userId, organizationId) values (853, 56);

insert into Users_Roles values (853, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (858, 1, now(), now(), 0, 859, 'test', 0, 0, 'dlc9', 'test.dlc.9@liferay.com', 'Welcome Test DLC 9!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (859, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'DLC 9', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (860, 1, 858, 8, 858, 0, 0, '860', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (861, 1, 860, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (862, 1, 860, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (858, 16);

insert into Users_Orgs (userId, organizationId) values (858, 52);
insert into Users_Orgs (userId, organizationId) values (858, 56);

insert into Users_Roles values (858, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (863, 1, now(), now(), 0, 864, 'test', 0, 0, 'dlc10', 'test.dlc.10@liferay.com', 'Welcome Test DLC 10!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (864, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'DLC 10', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (865, 1, 863, 8, 863, 0, 0, '865', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (866, 1, 865, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (867, 1, 865, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (863, 16);

insert into Users_Orgs (userId, organizationId) values (863, 52);
insert into Users_Orgs (userId, organizationId) values (863, 56);

insert into Users_Roles values (863, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (868, 1, now(), now(), 0, 869, 'test', 0, 0, 'hkg1', 'test.hkg.1@liferay.com', 'Welcome Test HKG 1!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (869, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'HKG 1', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (870, 1, 868, 8, 868, 0, 0, '870', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (871, 1, 870, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (872, 1, 870, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (868, 16);

insert into Users_Orgs (userId, organizationId) values (868, 52);
insert into Users_Orgs (userId, organizationId) values (868, 60);

insert into Users_Roles values (868, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (873, 1, now(), now(), 0, 874, 'test', 0, 0, 'hkg2', 'test.hkg.2@liferay.com', 'Welcome Test HKG 2!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (874, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'HKG 2', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (875, 1, 873, 8, 873, 0, 0, '875', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (876, 1, 875, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (877, 1, 875, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (873, 16);

insert into Users_Orgs (userId, organizationId) values (873, 52);
insert into Users_Orgs (userId, organizationId) values (873, 60);

insert into Users_Roles values (873, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (878, 1, now(), now(), 0, 879, 'test', 0, 0, 'hkg3', 'test.hkg.3@liferay.com', 'Welcome Test HKG 3!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (879, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'HKG 3', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (880, 1, 878, 8, 878, 0, 0, '880', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (881, 1, 880, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (882, 1, 880, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (878, 16);

insert into Users_Orgs (userId, organizationId) values (878, 52);
insert into Users_Orgs (userId, organizationId) values (878, 60);

insert into Users_Roles values (878, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (883, 1, now(), now(), 0, 884, 'test', 0, 0, 'hkg4', 'test.hkg.4@liferay.com', 'Welcome Test HKG 4!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (884, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'HKG 4', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (885, 1, 883, 8, 883, 0, 0, '885', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (886, 1, 885, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (887, 1, 885, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (883, 16);

insert into Users_Orgs (userId, organizationId) values (883, 52);
insert into Users_Orgs (userId, organizationId) values (883, 60);

insert into Users_Roles values (883, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (888, 1, now(), now(), 0, 889, 'test', 0, 0, 'hkg5', 'test.hkg.5@liferay.com', 'Welcome Test HKG 5!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (889, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'HKG 5', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (890, 1, 888, 8, 888, 0, 0, '890', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (891, 1, 890, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (892, 1, 890, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (888, 16);

insert into Users_Orgs (userId, organizationId) values (888, 52);
insert into Users_Orgs (userId, organizationId) values (888, 60);

insert into Users_Roles values (888, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (893, 1, now(), now(), 0, 894, 'test', 0, 0, 'hkg6', 'test.hkg.6@liferay.com', 'Welcome Test HKG 6!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (894, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'HKG 6', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (895, 1, 893, 8, 893, 0, 0, '895', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (896, 1, 895, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (897, 1, 895, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (893, 16);

insert into Users_Orgs (userId, organizationId) values (893, 52);
insert into Users_Orgs (userId, organizationId) values (893, 60);

insert into Users_Roles values (893, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (898, 1, now(), now(), 0, 899, 'test', 0, 0, 'hkg7', 'test.hkg.7@liferay.com', 'Welcome Test HKG 7!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (899, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'HKG 7', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (900, 1, 898, 8, 898, 0, 0, '900', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (901, 1, 900, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (902, 1, 900, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (898, 16);

insert into Users_Orgs (userId, organizationId) values (898, 52);
insert into Users_Orgs (userId, organizationId) values (898, 60);

insert into Users_Roles values (898, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (903, 1, now(), now(), 0, 904, 'test', 0, 0, 'hkg8', 'test.hkg.8@liferay.com', 'Welcome Test HKG 8!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (904, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'HKG 8', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (905, 1, 903, 8, 903, 0, 0, '905', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (906, 1, 905, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (907, 1, 905, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (903, 16);

insert into Users_Orgs (userId, organizationId) values (903, 52);
insert into Users_Orgs (userId, organizationId) values (903, 60);

insert into Users_Roles values (903, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (908, 1, now(), now(), 0, 909, 'test', 0, 0, 'hkg9', 'test.hkg.9@liferay.com', 'Welcome Test HKG 9!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (909, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'HKG 9', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (910, 1, 908, 8, 908, 0, 0, '910', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (911, 1, 910, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (912, 1, 910, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (908, 16);

insert into Users_Orgs (userId, organizationId) values (908, 52);
insert into Users_Orgs (userId, organizationId) values (908, 60);

insert into Users_Roles values (908, 12);

insert into User_ (userId, companyId, createDate, modifiedDate, defaultUser, contactId, password_, passwordEncrypted, passwordReset, screenName, emailAddress, greeting, loginDate, failedLoginAttempts, agreedToTermsOfUse, active_) values (913, 1, now(), now(), 0, 914, 'test', 0, 0, 'hkg10', 'test.hkg.10@liferay.com', 'Welcome Test HKG 10!', now(), 0, 1, 1);
insert into Contact_ (contactId, companyId, userId, userName, createDate, modifiedDate, accountId, parentContactId, firstName, middleName, lastName, male, birthday) values (914, 1, 2, 'Joe Bloggs', now(), now(), 6, 0, 'Test', '', 'HKG 10', 1, '1970-01-01');

insert into Group_ (groupId, companyId, creatorUserId, classNameId, classPK, parentGroupId, liveGroupId, name, friendlyURL, active_) values (915, 1, 913, 8, 913, 0, 0, '915', '', 1);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (916, 1, 915, 1, 0, 'classic', '01', 0);
insert into LayoutSet (layoutSetId, companyId, groupId, privateLayout, logo, themeId, colorSchemeId, pageCount) values (917, 1, 915, 0, 0, 'classic', '01', 0);

insert into Users_Groups values (913, 16);

insert into Users_Orgs (userId, organizationId) values (913, 52);
insert into Users_Orgs (userId, organizationId) values (913, 60);

insert into Users_Roles values (913, 12);



insert into Release_ (releaseId, createDate, modifiedDate, buildNumber, verified) values (1, now(), now(), 4300, 1);


commit;


create index IX_93D5AD4E on Address (companyId);
create index IX_ABD7DAC0 on Address (companyId, classNameId);
create index IX_71CB1123 on Address (companyId, classNameId, classPK);
create index IX_923BD178 on Address (companyId, classNameId, classPK, mailing);
create index IX_9226DBB4 on Address (companyId, classNameId, classPK, primary_);
create index IX_5BC8B0D4 on Address (userId);

create index IX_C49DD10C on BlogsCategory (parentCategoryId);

create index IX_B0608DF4 on BlogsEntry (categoryId);
create index IX_72EF6041 on BlogsEntry (companyId);
create index IX_81A50303 on BlogsEntry (groupId);

create index IX_443BDC38 on BookmarksEntry (folderId);

create index IX_7F703619 on BookmarksFolder (groupId);
create index IX_967799C0 on BookmarksFolder (groupId, parentFolderId);

create index IX_12EE4898 on CalEvent (groupId);
create index IX_4FDDD2BF on CalEvent (groupId, repeating);
create index IX_FCD7C63D on CalEvent (groupId, type_);

create index IX_B27A301F on ClassName_ (value);

create index IX_12566EC2 on Company (mx);
create index IX_975996C0 on Company (virtualHost);
create index IX_EC00543C on Company (webId);

create index IX_66D496A3 on Contact_ (companyId);

create index IX_25D734CD on Country (active_);

create index IX_24A846D1 on DLFileEntry (folderId);
create index IX_8F6C75D0 on DLFileEntry (folderId, name);

create index IX_CE705D48 on DLFileRank (companyId, userId, folderId, name);
create index IX_40B56512 on DLFileRank (folderId, name);
create index IX_EED06670 on DLFileRank (userId);

create index IX_E56EC6AD on DLFileShortcut (folderId);
create index IX_CA2708A2 on DLFileShortcut (toFolderId, toName);

create index IX_9CD91DB6 on DLFileVersion (folderId, name);
create index IX_6C5E6512 on DLFileVersion (folderId, name, version);

create index IX_A74DB14C on DLFolder (companyId);
create index IX_F2EA1ACE on DLFolder (groupId);
create index IX_49C37475 on DLFolder (groupId, parentFolderId);
create index IX_51556082 on DLFolder (parentFolderId, name);

create index IX_1BB072CA on EmailAddress (companyId);
create index IX_49D2DEC4 on EmailAddress (companyId, classNameId);
create index IX_551A519F on EmailAddress (companyId, classNameId, classPK);
create index IX_2A2CB130 on EmailAddress (companyId, classNameId, classPK, primary_);
create index IX_7B43CD8 on EmailAddress (userId);

create index IX_D0D5E397 on Group_ (companyId, classNameId, classPK);
create index IX_5BDDB872 on Group_ (companyId, friendlyURL);
create index IX_5AA68501 on Group_ (companyId, name);
create index IX_16218A38 on Group_ (liveGroupId);

create index LIFERAY_001 on Groups_Permissions (permissionId);

create index IX_206498F8 on IGFolder (groupId);
create index IX_1A605E9F on IGFolder (groupId, parentFolderId);

create index IX_4438CA80 on IGImage (folderId);

create index IX_DFF98523 on JournalArticle (companyId);
create index IX_9356F865 on JournalArticle (groupId);
create index IX_68C0F69C on JournalArticle (groupId, articleId);
create index IX_8DBF1387 on JournalArticle (groupId, articleId, approved);
create index IX_85C52EEC on JournalArticle (groupId, articleId, version);
create index IX_2E207659 on JournalArticle (groupId, structureId);
create index IX_8DEAE14E on JournalArticle (groupId, templateId);

create index IX_158B526F on JournalArticleImage (groupId, articleId, version);
create index IX_D67292FC on JournalArticleImage (groupId, articleId, version, elName, languageId);
create index IX_D4121315 on JournalArticleImage (tempImage);

create index IX_88DF994A on JournalArticleResource (groupId, articleId);

create index IX_6838E427 on JournalContentSearch (groupId, articleId);
create index IX_20962903 on JournalContentSearch (groupId, privateLayout);
create index IX_7CC7D73E on JournalContentSearch (groupId, privateLayout, articleId);
create index IX_B3B318DC on JournalContentSearch (groupId, privateLayout, layoutId);
create index IX_C3AA93B8 on JournalContentSearch (groupId, privateLayout, layoutId, portletId, articleId);

create index IX_B97F5608 on JournalStructure (groupId);
create index IX_AB6E9996 on JournalStructure (groupId, structureId);
create index IX_8831E4FC on JournalStructure (structureId);

create index IX_77923653 on JournalTemplate (groupId);
create index IX_1701CB2B on JournalTemplate (groupId, structureId);
create index IX_E802AA3C on JournalTemplate (groupId, templateId);
create index IX_1B12CA20 on JournalTemplate (templateId);

create index IX_705F5AA3 on Layout (groupId, privateLayout);
create index IX_BC2C4231 on Layout (groupId, privateLayout, friendlyURL);
create index IX_7162C27C on Layout (groupId, privateLayout, layoutId);
create index IX_6DE88B06 on Layout (groupId, privateLayout, parentLayoutId);

create index IX_A40B8BEC on LayoutSet (groupId);
create index IX_48550691 on LayoutSet (groupId, privateLayout);
create index IX_5ABC2905 on LayoutSet (virtualHost);

create index IX_2932DD37 on ListType (type_);

create index IX_69951A25 on MBBan (banUserId);
create index IX_5C3FF12A on MBBan (groupId);
create index IX_8ABC4E3B on MBBan (groupId, banUserId);
create index IX_48814BBA on MBBan (userId);

create index IX_BC735DCF on MBCategory (companyId);
create index IX_BB870C11 on MBCategory (groupId);
create index IX_ED292508 on MBCategory (groupId, parentCategoryId);

create index IX_33A4DE38 on MBDiscussion (classNameId, classPK);

create index IX_3C865EE5 on MBMessage (categoryId);
create index IX_138C7F1E on MBMessage (categoryId, threadId);
create index IX_75B95071 on MBMessage (threadId);
create index IX_A7038CD7 on MBMessage (threadId, parentMessageId);

create index IX_D180D4AE on MBMessageFlag (messageId);
create index IX_7B2917BE on MBMessageFlag (userId);
create index IX_94A83834 on MBMessageFlag (userId, messageId);

create index IX_A00A898F on MBStatsUser (groupId);
create index IX_FAB5A88B on MBStatsUser (groupId, messageCount);
create index IX_9168E2C9 on MBStatsUser (groupId, userId);
create index IX_847F92B5 on MBStatsUser (userId);

create index IX_CB854772 on MBThread (categoryId);

create index IX_A425F71A on OrgGroupPermission (groupId);
create index IX_6C53DA4E on OrgGroupPermission (permissionId);

create index IX_4A527DD3 on OrgGroupRole (groupId);
create index IX_AB044D1C on OrgGroupRole (roleId);

create index IX_6AF0D434 on OrgLabor (organizationId);

create index IX_834BCEB6 on Organization_ (companyId);
create index IX_E301BDF5 on Organization_ (companyId, name);
create index IX_418E4522 on Organization_ (companyId, parentOrganizationId);

create index IX_2C1142E on PasswordPolicy (companyId, defaultPolicy);
create index IX_3FBFA9F4 on PasswordPolicy (companyId, name);

create index IX_C3A17327 on PasswordPolicyRel (classNameId, classPK);
create index IX_ED7CF243 on PasswordPolicyRel (passwordPolicyId, classNameId, classPK);

create index IX_326F75BD on PasswordTracker (userId);

create index IX_4D19C2B8 on Permission_ (actionId, resourceId);
create index IX_F090C113 on Permission_ (resourceId);

create index IX_9F704A14 on Phone (companyId);
create index IX_A2E4AFBA on Phone (companyId, classNameId);
create index IX_9A53569 on Phone (companyId, classNameId, classPK);
create index IX_812CE07A on Phone (companyId, classNameId, classPK, primary_);
create index IX_F202B9CE on Phone (userId);

create index IX_B9746445 on PluginSetting (companyId);
create index IX_7171B2E8 on PluginSetting (companyId, pluginId, pluginType);

create index IX_EC370F10 on PollsChoice (questionId);
create index IX_D76DD2CF on PollsChoice (questionId, name);

create index IX_9FF342EA on PollsQuestion (groupId);

create index IX_D5DF7B54 on PollsVote (choiceId);
create index IX_12112599 on PollsVote (questionId);
create index IX_1BBFD4D3 on PollsVote (questionId, userId);

create index IX_80CC9508 on Portlet (companyId);
create index IX_12B5E51D on Portlet (companyId, portletId);

create index IX_E4F13E6E on PortletPreferences (ownerId, ownerType, plid);
create index IX_C7057FF7 on PortletPreferences (ownerId, ownerType, plid, portletId);
create index IX_F15C1C4F on PortletPreferences (plid);

create index IX_16184D57 on RatingsEntry (classNameId, classPK);
create index IX_B47E3C11 on RatingsEntry (userId, classNameId, classPK);

create index IX_A6E99284 on RatingsStats (classNameId, classPK);

create index IX_2D9A426F on Region (active_);
create index IX_16D87CA7 on Region (countryId);
create index IX_11FB3E42 on Region (countryId, active_);

create index IX_717FDD47 on ResourceCode (companyId);
create index IX_A32C097E on ResourceCode (companyId, name, scope);
create index IX_AACAFF40 on ResourceCode (name);

create index IX_2578FBD3 on Resource_ (codeId);
create index IX_67DE7856 on Resource_ (codeId, primKey);

create index IX_449A10B9 on Role_ (companyId);
create index IX_A88E424E on Role_ (companyId, classNameId, classPK);
create index IX_EBC931B8 on Role_ (companyId, name);

create index LIFERAY_002 on Roles_Permissions (permissionId);

create index IX_C98C0D78 on SCFrameworkVersion (companyId);
create index IX_272991FA on SCFrameworkVersion (groupId);
create index IX_6E1764F on SCFrameworkVersion (groupId, active_);

create index IX_1C841592 on SCLicense (active_);
create index IX_5327BB79 on SCLicense (active_, recommended);

create index IX_5D25244F on SCProductEntry (companyId);
create index IX_72F87291 on SCProductEntry (groupId);
create index IX_98E6A9CB on SCProductEntry (groupId, userId);

create index IX_8377A211 on SCProductVersion (productEntryId);

create index IX_C28B41DC on ShoppingCart (groupId);
create index IX_FC46FE16 on ShoppingCart (groupId, userId);
create index IX_54101CC8 on ShoppingCart (userId);

create index IX_5F615D3E on ShoppingCategory (groupId);
create index IX_1E6464F5 on ShoppingCategory (groupId, parentCategoryId);

create index IX_DC60CFAE on ShoppingCoupon (code_);
create index IX_3251AF16 on ShoppingCoupon (groupId);

create index IX_C8EACF2E on ShoppingItem (categoryId);
create index IX_1C717CA6 on ShoppingItem (companyId, sku);

create index IX_6D5F9B87 on ShoppingItemField (itemId);

create index IX_EA6FD516 on ShoppingItemPrice (itemId);

create index IX_119B5630 on ShoppingOrder (groupId, userId, ppPaymentStatus);
create index IX_D7D6E87A on ShoppingOrder (number_);

create index IX_B5F82C7A on ShoppingOrderItem (orderId);

create index IX_786D171A on Subscription (companyId, classNameId, classPK);
create index IX_2E1A92D4 on Subscription (companyId, userId, classNameId, classPK);
create index IX_54243AFD on Subscription (userId);

create index IX_1AB6D6D2 on TagsAsset (classNameId, classPK);

create index IX_10563688 on TagsEntry (companyId, name);

create index IX_C134234 on TagsProperty (companyId);
create index IX_EB974D08 on TagsProperty (companyId, key_);
create index IX_5200A629 on TagsProperty (entryId);
create index IX_F505253D on TagsProperty (entryId, key_);

create index IX_524FEFCE on UserGroup (companyId);
create index IX_23EAD0D on UserGroup (companyId, name);
create index IX_69771487 on UserGroup (companyId, parentUserGroupId);

create index IX_1B988D7A on UserGroupRole (groupId);
create index IX_887A2C95 on UserGroupRole (roleId);
create index IX_887BE56A on UserGroupRole (userId);
create index IX_4D040680 on UserGroupRole (userId, groupId);

create index IX_E60EA987 on UserIdMapper (userId);
create index IX_D1C44A6E on UserIdMapper (userId, type_);

create index IX_29BA1CF5 on UserTracker (companyId);
create index IX_46B0AE8E on UserTracker (sessionId);
create index IX_E4EFBA8D on UserTracker (userId);

create index IX_14D8BCC0 on UserTrackerPath (userTrackerId);

create index IX_3A1E834E on User_ (companyId);
create index IX_6EF03E4E on User_ (companyId, defaultUser);
create index IX_615E9F7A on User_ (companyId, emailAddress);
create index IX_765A87C6 on User_ (companyId, password_);
create index IX_C5806019 on User_ (companyId, screenName);
create index IX_9782AD88 on User_ (companyId, userId);
create index IX_5ADBE171 on User_ (contactId);

create index LIFERAY_003 on Users_Permissions (permissionId);

create index IX_96F07007 on Website (companyId);
create index IX_4F0F0CA7 on Website (companyId, classNameId);
create index IX_F960131C on Website (companyId, classNameId, classPK);
create index IX_1AA07A6D on Website (companyId, classNameId, classPK, primary_);
create index IX_F75690BB on Website (userId);

create index IX_5D6FE3F0 on WikiNode (companyId);
create index IX_B480A672 on WikiNode (groupId);

create index IX_C8A9C476 on WikiPage (nodeId);
create index IX_E7F635CA on WikiPage (nodeId, head);
create index IX_997EEDD2 on WikiPage (nodeId, title);
create index IX_E745EA26 on WikiPage (nodeId, title, head);
create index IX_3D4AF476 on WikiPage (nodeId, title, version);

create index IX_21277664 on WikiPageResource (nodeId, title);


