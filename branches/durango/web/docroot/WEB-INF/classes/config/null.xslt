<?xml version="1.0" encoding="UTF-8"?>
<!-- Converts OpenOffice.org files (which are XHTML) to text -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xhtml="http://www.w3.org/1999/xhtml" version="1.0" >
 <xsl:output method="text" encoding="UTF-8" indent="yes"/>

<xsl:template match="/">
  <xsl:apply-templates select="//xhtml:head//xhtml:meta"/>
  <xsl:apply-templates select="//xhtml:head//xhtml:title"/>
  <xsl:apply-templates select="//xhtml:body//text()"/>
</xsl:template>

<xsl:template match="xhtml:meta[@name='generator']"/>

<xsl:template match="xhtml:meta[@name!='generator']">
 <xsl:text> </xsl:text><xsl:value-of select="@content"/> <xsl:text> </xsl:text>
</xsl:template>

<xsl:template match="xhtml:title">
 <xsl:text> </xsl:text><xsl:value-of select="."/> <xsl:text> </xsl:text>
 </xsl:template>
 
<xsl:template match="text()">
 <xsl:text> </xsl:text><xsl:value-of select="."/> <xsl:text> </xsl:text>
 </xsl:template>
</xsl:stylesheet>