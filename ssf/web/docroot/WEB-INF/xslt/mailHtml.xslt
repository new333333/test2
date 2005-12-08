<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="utf-8" indent="yes"/>
<xsl:template match="/">
<doc>
 <xsl:apply-templates select="/mail"/>
</doc>
</xsl:template>

<xsl:template match="mail">
<xsl:value-of select="topFolder/@changeCount"/>&nbsp;changed entries in <xsl:value-of select="topFolder/@title"/>
<br/><br/>
<span
  style="font-family: arial, helvetica, sans-serif;
  font-size: 13px;"><a name="toc"></a>Table of contents:<br/></span>

<xsl:for-each select="folder">
<xsl:for-each select="folderEntry">
		<xsl:if test="@hasChanges = 'true' or @docLevel = '1'">
		<a><xsl:attribute name="href">#id<xsl:value-of select="@name"/></xsl:attribute>
		<xsl:if test="@docLevel != '1'">&nbsp;&nbsp;&nbsp;</xsl:if>
		<span style="font-family: arial, helvetica, sans-serif; font-size: 13px;">
             <xsl:value-of select="@docNumber"/>&nbsp;&nbsp;
			 <xsl:value-of select="@title"/>
         <xsl:if test="@hasChanges = 'true'">&nbsp;(<xsl:value-of select="@notifyType"/>)</xsl:if>
 		</span></a><br/>
		</xsl:if>

</xsl:for-each>
</xsl:for-each>
<hr size="1" color="black" noshade="true"/>
<span style="font-family: arial, helvetica,
  sans-serif; font-size: 13px;"><b>Forum:&nbsp;<xsl:value-of select="topFolder/@title"/></b></span>
<br/><br/>
<a href="#toc"><span style="font-family:
  arial, helvetica, sans-serif; font-size: 13px;">Table
  of contents</span></a>
<br/>
<xsl:for-each select="folder">
<xsl:for-each select="folderEntry">
		<xsl:if test="@hasChanges = 'true' or @docLevel = '1'">
		<a name="id{@name}"/>
		<a href="{@url}">
		<span style="font-family: arial, helvetica, sans-serif; font-size: 13px;">
             <xsl:value-of select="@docNumber"/>&nbsp;&nbsp;
			 <xsl:value-of select="@title"/>
  		</span></a>
         <xsl:if test="@hasChanges = 'true'">&nbsp;(<xsl:value-of select="@notifyType"/>)</xsl:if>
		<br/>
		From:&nbsp;<xsl:value-of select="@notifyBy"/><br/>
		Date:&nbsp;<xsl:value-of select="@notifyDate"/><br/>
		</xsl:if>
		<xsl:if test="@hasChanges = 'false' and @docLevel != '1'">
		<span style="font-family: arial, helvetica, sans-serif; font-size: 13px;">
             <xsl:value-of select="@docNumber"/>&nbsp;&nbsp;
			 <xsl:value-of select="@title"/>
  		</span>
		</xsl:if>
		<br/>
		<xsl:for-each select="attribute">
		<xsl:value-of select="@caption"/>&nbsp;
		<xsl:choose>
		<xsl:when test="@type = 'select'">
			<xsl:for-each select="value">
				&nbsp;&nbsp;&nbsp;<xsl:value-of select="."/><br/>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="@type = 'attachFiles'">
			<xsl:for-each select="file">
				&nbsp;&nbsp;&nbsp;<xsl:value-of select="."/><br/>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="@type = 'event'">
			<xsl:value-of select="startDate"/>-<xsl:value-of select="endDate"/><br/>
		</xsl:when>
		<xsl:when test="@type = 'description' or @type = 'htmlEditorTextarea'">
			<xsl:if test="@format != 2">
				<xsl:value-of select="." disable-output-escaping="yes"/>
			</xsl:if>
			<xsl:if test="@format = 2">
				<xsl:value-of select="."/>
			</xsl:if>
		<br/>
		</xsl:when>
		<xsl:otherwise>
		<xsl:value-of select="."/><br/>
		</xsl:otherwise>
		</xsl:choose>
		</xsl:for-each>

</xsl:for-each>
</xsl:for-each>



</xsl:template>

</xsl:stylesheet>