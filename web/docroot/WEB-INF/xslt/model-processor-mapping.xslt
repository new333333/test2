<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" encoding="utf-8" indent="yes"/>

<xsl:param name="merge" select="''"/>
<xsl:variable name="doc2" select="document($merge)"/>
<xsl:template match="/">
<model-processor-mapping>

<processors> 	
	<xsl:apply-templates select="/model-processor-mapping/processors"/>
</processors> 	
<default-mappings>
	<xsl:apply-templates select="/model-processor-mapping/default-mappings"/>
</default-mappings>

</model-processor-mapping>
</xsl:template>

<xsl:template match="processors">
	<xsl:copy-of select="$doc2/model-processor-mapping/processors/processor | ./processor[not(@class=$doc2/model-processor-mapping/processors/processor/@class)]"/>
</xsl:template>

<xsl:template match="default-mappings">
	<xsl:for-each select="model-class">
		<model-class name="{@name}">
		<xsl:variable name="name" select="@name"/>
		<xsl:copy-of select="$doc2/model-processor-mapping/default-mappings/model-class[@name=$name]/mapping"/>
		<xsl:copy-of select="./mapping[not(@processor-key=$doc2/model-processor-mapping/default-mappings/model-class[@name=$name]/mapping/@processor-key)]"/>
		</model-class>
	</xsl:for-each>

</xsl:template>

</xsl:stylesheet>

