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

<xsl:if test="topFolder/@changeCount = '1'">
<xsl:value-of select="topFolder/@changeCount"/>&nbsp;
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'ChangedOne'"/>
</xsl:call-template>
<a><xsl:attribute name="href"><xsl:value-of select="topFolder/@href"/></xsl:attribute>
<xsl:value-of select="topFolder/@title"/>
</a>
</xsl:if>
<xsl:if test="topFolder/@changeCount != '1'">
<xsl:value-of select="topFolder/@changeCount"/>&nbsp;
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'ChangedMany'"/>
</xsl:call-template>
<a><xsl:attribute name="href"><xsl:value-of select="topFolder/@href"/></xsl:attribute>
<xsl:value-of select="topFolder/@title"/>
</a>
</xsl:if>
<br/><br/>
<span
  style="font-family: arial, helvetica, sans-serif;
  font-size: 13px;"><a name="toc"></a>
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'TOC'"/>
</xsl:call-template>
:<br/></span>
<span style="font-family: arial, helvetica, sans-serif; font-size: 13px;">
<xsl:for-each select="folder">
		<br/><xsl:value-of select="@title"/><br/>
<xsl:for-each select="folderEntry">
		&nbsp;&nbsp;&nbsp;&nbsp;
		<xsl:if test="@hasChanges = 'true' or @docLevel = '1'">
		<a><xsl:attribute name="href">#id<xsl:value-of select="@name"/></xsl:attribute>
		<xsl:if test="@docLevel != '1'">&nbsp;&nbsp;&nbsp;</xsl:if>
             <xsl:value-of select="@docNumber"/>&nbsp;&nbsp;
			 <xsl:value-of select="@title"/>
         <xsl:if test="@hasChanges = 'true'">&nbsp;(
		<xsl:call-template name="getString">
		  <xsl:with-param name="stringName" select="@notifyType"/>
		</xsl:call-template>
		)</xsl:if>
 		</a><br/>
		</xsl:if>

</xsl:for-each>
</xsl:for-each>
</span>
<hr size="1" color="black" noshade="true"/>

<xsl:for-each select="folder">
<a name="f{@name}"/>
<span style="font-family: arial, helvetica,
  sans-serif; font-size: 13px;"><b>
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'folderLabel'"/>
</xsl:call-template>
&nbsp;<a><xsl:attribute name="href"><xsl:value-of select="@href"/></xsl:attribute>
	<xsl:value-of select="@title"/></a>
</b></span>
<br/>
<xsl:for-each select="folderEntry">
<div style="border-bottom: thin solid #cccccc;">
<xsl:if test="@hasChanges = 'true' or @docLevel = '1'">
<a href="#toc"><span style="font-family:
  arial, helvetica, sans-serif; font-size: 13px;">
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'TOC'"/>
</xsl:call-template>
</span></a>
<br/>		
		<a name="id{@name}"/>
<table border="0" width="100%">
<tr><td valign="top" style="font-family: arial, helvetica, sans-serif; font-size: 13px;">
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
</td>
<td nowrap="nowrap" valign="top" align="right">
		<xsl:if test="../@replyTo != ''">&nbsp;&nbsp;<a href="mailto:{../@replyTo}?subject=RE: DocId:{../@name}:{@name}" 
		style="font-size: 11px; color: #3366cc; font-weight:bold;
border-top: 1px solid #d5d5d5; border-left:
  1px solid #d5d5d5;
border-right: 1px solid #666666;
  border-bottom: 1px solid #666666;
background-color:
  #e5e5e5; padding: 3px;
font-family: arial, helvetica,
  sans-serif;
margin-left: 0px; margin-right: 6px; margin-bottom,
  margin-top: 2px;
line-height: 200%; text-decoration:
  none;" >ReplyTo</a></xsl:if>
</td>
</tr></table>
		</xsl:if>
		<xsl:if test="@hasChanges = 'false' and @docLevel != '1'">
		<span style="font-family: arial, helvetica, sans-serif; font-size: 13px;">
             <xsl:value-of select="@docNumber"/>&nbsp;&nbsp;
			 <xsl:value-of select="@title"/>
  		</span>
		</xsl:if>
		<br/>
		<xsl:call-template name="getString">
  			<xsl:with-param name="stringName" select="'fromLabel'"/>
		</xsl:call-template>
		:&nbsp;<xsl:value-of select="@notifyBy"/><br/>
		<xsl:call-template name="getString">
  			<xsl:with-param name="stringName" select="'dateLabel'"/>
		</xsl:call-template>
		:&nbsp;<xsl:value-of select="@notifyDate"/><br/>
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
		<xsl:when test="@type = 'attachFiles'">
			<ul>
			<xsl:for-each select="file">
			<li>&nbsp;&nbsp;&nbsp;<xsl:choose>
			<xsl:when test="@href != ''">
				<a href="{@href}">
				<xsl:value-of select="."/><br/>
				</a></xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/><br/>
				</xsl:otherwise>
			</xsl:choose>
			</li>
			</xsl:for-each>
			</ul>
		</xsl:when>
		<xsl:when test="@type = 'file'">
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
			<xsl:choose>
			<xsl:when test="@href != ''">
				<img src="{@href}" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="."/><br/>
				</xsl:otherwise>
			</xsl:choose>
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