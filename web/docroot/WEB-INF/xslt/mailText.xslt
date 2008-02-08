<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp "<xsl:text> </xsl:text>">
]>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="text" encoding="utf-8" indent="no"/>
<xsl:param name="TOC" select="'true'"/>

<xsl:template match="/">
 <xsl:apply-templates select="mail"/>
</xsl:template>

<xsl:template match="mail">
<xsl:if test="$TOC = 'true'">
<xsl:value-of select="topFolder/@changeCount"/>
<xsl:text> changed entries in </xsl:text>
<xsl:value-of select="topFolder/@title"/>
<xsl:text>
</xsl:text>
</xsl:if>
<xsl:for-each select="folder">
<xsl:text>
_________________________________________________________
Folder </xsl:text>
<xsl:value-of select="@title"/>
<xsl:text>
</xsl:text>
<xsl:for-each select="folderEntry">
<xsl:text>Entry </xsl:text>
<xsl:value-of select="@docNumber"/>&nbsp;
<xsl:value-of select="@title"/>

<xsl:if test="@hasChanges = 'true'"><xsl:text> (</xsl:text>
<xsl:value-of select="@notifyType"/><xsl:text>)</xsl:text>
</xsl:if>
<xsl:if test="@hasChanges = 'true' or @docLevel = '1'">
<xsl:text>
	From: </xsl:text>
<xsl:value-of select="@notifyBy"/>
<xsl:text>	
	Date: </xsl:text>
<xsl:value-of select="@notifyDate"/>
</xsl:if>
<xsl:text>

</xsl:text>
</xsl:for-each>
</xsl:for-each>
</xsl:template>


</xsl:stylesheet>