<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ext="http://exslt.org/common" exclude-result-prefixes="ext"
                version="1.0">

    <!-- used for cleaning data from the register of interests -->
    <!-- step one removes any instances of any breaks within element text -->

    <xsl:output method="xml" encoding="utf-8" indent="yes"/>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*|node()" />
        </xsl:copy>
    </xsl:template>

    <!-- note that the br tag is being misused in the data (they've wrapped it) -->
    <xsl:template match="item/br">
        <xsl:text> </xsl:text>
        <xsl:apply-templates/>
    </xsl:template>

</xsl:stylesheet>