<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ext="http://exslt.org/common" exclude-result-prefixes="ext"
                version="1.0">

    <!-- used for cleaning data from the register of interests -->

    <xsl:output method="xml" encoding="utf-8" indent="yes"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="item">
        <item>
            <xsl:value-of select="normalize-space()"/>
        </item>
    </xsl:template>

</xsl:stylesheet>