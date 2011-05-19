<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="utf-8" indent="yes"/>

<xsl:param name="merge" select="''"/>
<xsl:variable name="doc2" select="document($merge)"/>
<xsl:template match="/">
<zoneConfiguration>
 	<xsl:apply-templates select="/zoneConfiguration"/>
<mailConfiguration>
	 <xsl:apply-templates select="/zoneConfiguration/mailConfiguration"/>
</mailConfiguration>
<ldapConfiguration>
	<xsl:apply-templates select="/zoneConfiguration/ldapConfiguration"/>
</ldapConfiguration>
<folderConfiguration>
	<xsl:apply-templates select="/zoneConfiguration/folderConfiguration"/>
</folderConfiguration>
<extensionConfiguration>
	<xsl:apply-templates select="/zoneConfiguration/extensionConfiguration"/>
</extensionConfiguration>
<licenseConfiguration>
	<xsl:apply-templates select="/zoneConfiguration/licenseConfiguration"/>
</licenseConfiguration>
<workflowConfiguration>
	<xsl:apply-templates select="/zoneConfiguration/workflowConfiguration"/>
</workflowConfiguration>

</zoneConfiguration>
</xsl:template>

<xsl:template match="zoneConfiguration">
	<xsl:copy-of select="$doc2/zoneConfiguration/property | ./property[not(@name=$doc2/zoneConfiguration/property/@name)]"/>
	<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/defaultZone">
			<xsl:copy-of select="$doc2/zoneConfiguration/defaultZone"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./defaultZone" />
		</xsl:otherwise>
	</xsl:choose>
	<xsl:copy-of select="$doc2/zoneConfiguration/zone | ./property[not(@name=$doc2/zoneConfiguration/zone/@name)]"/>
	<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/defaultGroupsOnAcctCreation">
			<xsl:copy-of select="$doc2/zoneConfiguration/defaultGroupsOnAcctCreation"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./defaultGroupsOnAcctCreation" />
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/xssConfiguration">
			<xsl:copy-of select="$doc2/zoneConfiguration/xssConfiguration"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./xssConfiguration" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
<xsl:template match="folderConfiguration">
	<xsl:copy-of select="$doc2/zoneConfiguration/folderConfiguration/property | ./property[not(@name=$doc2/zoneConfiguration/folderConfiguration/property/@name)]"/>
</xsl:template>
<xsl:template match="extensionConfiguration">
	<xsl:copy-of select="$doc2/zoneConfiguration/extensionConfiguration/property | ./property[not(@name=$doc2/zoneConfiguration/extensionConfiguration/property/@name)]"/>
</xsl:template>
<xsl:template match="licenseConfiguration">
	<xsl:copy-of select="$doc2/zoneConfiguration/licenseConfiguration/property | ./property[not(@name=$doc2/zoneConfiguration/licenseConfiguration/property/@name)]"/>
</xsl:template>
<xsl:template match="workflowConfiguration">
	<xsl:copy-of select="$doc2/zoneConfiguration/workflowConfiguration/property | ./property[not(@name=$doc2/zoneConfiguration/workflowConfiguration/property/@name)]"/>
</xsl:template>
<xsl:template match="mailConfiguration">
	<xsl:copy-of select="$doc2/zoneConfiguration/mailConfiguration/property | ./property[not(@name=$doc2/zoneConfiguration/mailConfiguration/property/@name)]"/>
	<xsl:copy-of select="$doc2/zoneConfiguration/mailConfiguration/account"/>
	<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/mailConfiguration/notify">
				<xsl:copy-of select="$doc2/zoneConfiguration/mailConfiguration/notify"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./notify" />
		</xsl:otherwise>
	</xsl:choose>
		<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/mailConfiguration/posting">
			<xsl:copy-of select="$doc2/zoneConfiguration/mailConfiguration/posting"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./posting" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>
<xsl:template match="mailConfiguration">
	<xsl:copy-of select="$doc2/zoneConfiguration/mailConfiguration/property | ./property[not(@name=$doc2/zoneConfiguration/mailConfiguration/property/@name)]"/>
	<xsl:copy-of select="$doc2/zoneConfiguration/mailConfiguration/account"/>
	<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/mailConfiguration/notify">
				<xsl:copy-of select="$doc2/zoneConfiguration/mailConfiguration/notify"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./notify" />
		</xsl:otherwise>
	</xsl:choose>
		<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/mailConfiguration/posting">
			<xsl:copy-of select="$doc2/zoneConfiguration/mailConfiguration/posting"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./posting" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

<xsl:template match="ldapConfiguration">
	<xsl:copy-of select="$doc2/zoneConfiguration/ldapConfiguration/property | ./property[not(@name=$doc2/zoneConfiguration/ldapConfiguration/property/@name)]"/>
	<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/ldapConfiguration/userAttribute">
			<xsl:copy-of select="$doc2/zoneConfiguration/ldapConfiguration/userAttribute"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./userAttribute" />
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/ldapConfiguration/userFilter">
			<xsl:copy-of select="$doc2/zoneConfiguration/ldapConfiguration/userFilter"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./userFilter" />
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/ldapConfiguration/groupFilter">
			<xsl:copy-of select="$doc2/zoneConfiguration/ldapConfiguration/groupFilter"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./groupFilter" />
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/ldapConfiguration/userMapping">
			<xsl:copy-of select="$doc2/zoneConfiguration/ldapConfiguration/userMapping"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./userMapping" />
		</xsl:otherwise>
	</xsl:choose>
	<xsl:choose>
		<xsl:when test="$doc2/zoneConfiguration/ldapConfiguration/groupMapping">
			<xsl:copy-of select="$doc2/zoneConfiguration/ldapConfiguration/groupMapping"/>
		</xsl:when>
		<xsl:otherwise>
			<xsl:copy-of select="./groupMapping" />
		</xsl:otherwise>
	</xsl:choose>
</xsl:template>

</xsl:stylesheet>

