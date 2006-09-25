<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="utf-8" indent="yes"/>

<xsl:param name="Lang" select="'en_US'"/>
<xsl:param name="TOC" select="'true'"/>
<xsl:variable name="StringFile" select="document('strings.xml')"/>
<xsl:variable name="PrimaryLang" select="substring-before($Lang,'_')"/>

<xsl:template name="getString">
  <xsl:param name="stringName"/>
  <xsl:variable name="str" select="$StringFile/strings/str[@name=$stringName]"/>     
  <xsl:choose>
    <xsl:when test="$str/lang[@name=$Lang]">
      <xsl:value-of select="$str/lang[@name=$Lang][1]"/>
    </xsl:when>
    <xsl:when test="$str/lang[@name=$PrimaryLang]">
      <xsl:value-of select="$str/lang[@name=$PrimaryLang][1]"/>
    </xsl:when>
    <xsl:when test="$str">
      <xsl:value-of select="$str/lang[1]"/>
    </xsl:when>
    <xsl:otherwise>
      <xsl:message terminate="no">
        <xsl:text>Warning: no string named '</xsl:text>
        <xsl:value-of select="$stringName"/>
        <xsl:text>' found.</xsl:text>
      </xsl:message>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="/">
<doc>
 <xsl:apply-templates select="/mail"/>
</doc>
</xsl:template>

<xsl:template match="mail">
<xsl:if test="$TOC = 'true'">
<xsl:if test="topFolder/@changeCount = '1'">
<xsl:value-of select="topFolder/@changeCount"/>&nbsp;
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'ChangedOne'"/>
</xsl:call-template><xsl:value-of select="topFolder/@title"/>
</xsl:if>
<xsl:if test="topFolder/@changeCount != '1'">
<xsl:value-of select="topFolder/@changeCount"/>&nbsp;
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'ChangedMany'"/>
</xsl:call-template><xsl:value-of select="topFolder/@title"/>
</xsl:if>
<br/><br/>
<span
  style="font-family: arial, helvetica, sans-serif;
  font-size: 13px;"><a name="toc"></a>
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'TOC'"/>
</xsl:call-template>

<br/></span>

<xsl:for-each select="folder">
<xsl:for-each select="folderEntry">
		<xsl:if test="@hasChanges = 'true' or @docLevel = '1'">
		<a><xsl:attribute name="href">#id<xsl:value-of select="@name"/></xsl:attribute>
		<xsl:if test="@docLevel != '1'">&nbsp;&nbsp;&nbsp;</xsl:if>
		<span style="font-family: arial, helvetica, sans-serif; font-size: 13px;">
             <xsl:value-of select="@docNumber"/>&nbsp;&nbsp;
			 <xsl:value-of select="@title"/>
         <xsl:if test="@hasChanges = 'true'">&nbsp;(
		<xsl:call-template name="getString">
		  <xsl:with-param name="stringName" select="@notifyType"/>
		</xsl:call-template>
		)</xsl:if>
 		</span></a><br/>
		</xsl:if>

</xsl:for-each>
</xsl:for-each>
<hr size="1" color="black" noshade="true"/>
<a href="#toc"><span style="font-family:
  arial, helvetica, sans-serif; font-size: 13px;">
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'TOC'"/>
</xsl:call-template>
</span></a>
<br/><br/>
</xsl:if>
<span style="font-family: arial, helvetica,
  sans-serif; font-size: 13px;"><b>
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'forumLabel'"/>
</xsl:call-template>
:&nbsp;<xsl:value-of select="topFolder/@title"/></b></span>
<br/>
<xsl:for-each select="folder">
<xsl:for-each select="folderEntry">
<div style="border-bottom: thin solid #cccccc;">
		<xsl:if test="@hasChanges = 'true' or @docLevel = '1'">
		<a name="id{@name}"/>
		<a href="{@href}">
		<span style="font-family: arial, helvetica, sans-serif; font-size: 13px;">
             <xsl:value-of select="@docNumber"/>&nbsp;&nbsp;
			 <xsl:value-of select="@title"/>
  		</span></a>
         <xsl:if test="@hasChanges = 'true'">&nbsp;(
		<xsl:call-template name="getString">
		  <xsl:with-param name="stringName" select="@notifyType"/>
		</xsl:call-template>
	    )</xsl:if>
		<br/>
		<xsl:call-template name="getString">
  			<xsl:with-param name="stringName" select="'fromLabel'"/>
		</xsl:call-template>
		:&nbsp;<xsl:value-of select="@notifyBy"/><br/>
		<xsl:call-template name="getString">
  			<xsl:with-param name="stringName" select="'dateLabel'"/>
		</xsl:call-template>
		:&nbsp;<xsl:value-of select="@notifyDate"/><br/>
		</xsl:if>
		<xsl:if test="@hasChanges = 'false' and @docLevel != '1'">
		<span style="font-family: arial, helvetica, sans-serif; font-size: 13px;">
             <xsl:value-of select="@docNumber"/>&nbsp;&nbsp;
			 <xsl:value-of select="@title"/>
  		</span>
		</xsl:if>
		<xsl:for-each select="attribute">
		<xsl:value-of select="@caption"/>&nbsp;
		<xsl:choose>
		<xsl:when test="@type = 'selectbox'">
			<xsl:for-each select="value">
				&nbsp;&nbsp;&nbsp;<xsl:value-of select="."/><br/>
			</xsl:for-each>
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
		<xsl:when test="@type = 'attachFiles' or @type = 'file'">
			<br/>
			<xsl:for-each select="file">
			<xsl:choose>
			<xsl:when test="@href != ''">
				<a href="{@href}">
				<xsl:value-of select="."/><br/>
				</a></xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/><br/>
				</xsl:otherwise>
			</xsl:choose>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="@type = 'graphic'">
			<xsl:for-each select="graphic">
				<xsl:if test="@href != ''">
					<img src="{@href}" />
				</xsl:if>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="@type = 'event'">
			<xsl:value-of select="startDate"/>-<xsl:value-of select="endDate"/><br/>
		</xsl:when>
		<xsl:otherwise>
		<xsl:value-of select="."/><br/>
		</xsl:otherwise>
		</xsl:choose>
		</xsl:for-each>
</div>
<br/>

</xsl:for-each>
</xsl:for-each>



</xsl:template>

</xsl:stylesheet>