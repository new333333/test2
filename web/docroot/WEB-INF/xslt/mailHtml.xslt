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
<xsl:if test="@summary = 'true'">
<xsl:if test="topFolder/@changeCount = '1'">
<xsl:value-of select="topFolder/@changeCount"/>&nbsp;
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'ChangedOne'"/>
</xsl:call-template>
&nbsp;<a><xsl:attribute name="href"><xsl:value-of select="topFolder/@href"/></xsl:attribute>
<xsl:value-of select="topFolder/@title"/>
</a>
</xsl:if>
<xsl:if test="topFolder/@changeCount != '1'">
<xsl:value-of select="topFolder/@changeCount"/>&nbsp;
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'ChangedMany'"/>
</xsl:call-template>
&nbsp;<a><xsl:attribute name="href"><xsl:value-of select="topFolder/@href"/></xsl:attribute>
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
</xsl:if>
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
<xsl:if test="../../@summary = 'true'">
<a href="#toc"><span style="font-family:
  arial, helvetica, sans-serif; font-size: 13px;">
<xsl:call-template name="getString">
  <xsl:with-param name="stringName" select="'TOC'"/>
</xsl:call-template>
</span></a>
</xsl:if>
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
		</xsl:call-template>,&nbsp;<xsl:value-of select="@notifyBy"/>
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
  none;" >		<xsl:call-template name="getString">
  			<xsl:with-param name="stringName" select="'reply'"/>
		</xsl:call-template>
</a></xsl:if>
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
		<b><xsl:call-template name="getString">
  			<xsl:with-param name="stringName" select="'fromLabel'"/>
		</xsl:call-template></b>
		&nbsp;<xsl:value-of select="@notifyFrom"/><br/>
		<b><xsl:call-template name="getString">
  			<xsl:with-param name="stringName" select="'dateLabel'"/>
		</xsl:call-template></b>
		&nbsp;<xsl:value-of select="@notifyDate"/><br/>
		<xsl:for-each select="attribute">
		<b><xsl:value-of select="@caption"/></b>&nbsp;
		<xsl:choose>
		<xsl:when test="@type = 'description' or @type = 'htmlEditorTextarea'">
		<br/>
			<xsl:if test="@format != 2">
				<xsl:value-of select="." disable-output-escaping="yes"/>
			</xsl:if>
			<xsl:if test="@format = 2">
				<xsl:value-of select="."/>
			</xsl:if>
		</xsl:when>
		<xsl:when test="@type = 'attachFiles' or @type = 'file'">
			<xsl:for-each select="file">
			<xsl:choose>
			<xsl:when test="@href != ''">
				<br/>&nbsp;&nbsp;&nbsp;<a href="{@href}">
				<xsl:value-of select="."/>
				</a></xsl:when>
			<xsl:otherwise>
				<br/>&nbsp;&nbsp;&nbsp;<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="@type = 'graphic'">
			<xsl:for-each select="graphic">
			<xsl:choose>
			<xsl:when test="@href != ''">
				<br/>&nbsp;&nbsp;&nbsp;<a href="{@href}">
				<xsl:value-of select="."/>
				</a></xsl:when>
			<xsl:otherwise>
				<br/>&nbsp;&nbsp;&nbsp;<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="@type = 'event'">
			<xsl:value-of select="startDate"/><xsl:if test="endDate != ''"> - <xsl:value-of select="endDate"/></xsl:if><br/>
			<xsl:if test="frequency != ''"><xsl:value-of select="frequency" disable-output-escaping="yes"/><br/></xsl:if>
		</xsl:when>
		<xsl:when test="@type = 'survey'">
			<ul>
			<xsl:for-each select="question">
				<li>			
					<xsl:value-of select="text"/>:
					<ol>
						<xsl:for-each select="answer">
							<li><xsl:value-of select="text"/> (<xsl:value-of select="votesCount"/>)</li>
						</xsl:for-each>
					</ol>
				</li>
			</xsl:for-each>
			</ul>
		</xsl:when>
		<xsl:when test="value">
			<xsl:for-each select="value">
				<br/>&nbsp;&nbsp;&nbsp;<xsl:value-of select="."/>
			</xsl:for-each>
		</xsl:when>
		<xsl:when test="@type = 'text'">
				<xsl:value-of select="." disable-output-escaping="yes"/>
		</xsl:when>
		<xsl:otherwise>
		<xsl:value-of select="."/>
		</xsl:otherwise>
		</xsl:choose>		
		<br/>
		</xsl:for-each>
</div>
<br/>

</xsl:for-each>

</xsl:for-each>



</xsl:template>

</xsl:stylesheet>