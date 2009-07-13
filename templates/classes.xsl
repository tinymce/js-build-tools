<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="html" indent="yes"/>
	<xsl:param name="target" />

	<xsl:template match="/">
		<html>
		<head>
			<title>Class</title>
			<link href="css/general.css" rel="stylesheet" type="text/css" />
		</head>
		<body>
			<xsl:choose>
				<xsl:when test="$target='top_level'">
					<h1>Top level</h1>

					<ul>
						<xsl:for-each select="/model/class">
							<li>
								<a target="overview">
									<xsl:attribute name="href">class_<xsl:value-of select="@fullname" />.html</xsl:attribute>
									<xsl:value-of select="@fullname" />
								</a>
							</li>
						</xsl:for-each>
					</ul>
				</xsl:when>

				<xsl:otherwise>
					<h1>Classes - <xsl:value-of select="$target" /></h1>

					<ul>
						<xsl:for-each select="//namespace[@fullname=$target]/class">
							<li>
								<a target="overview">
									<xsl:attribute name="href">class_<xsl:value-of select="@fullname" />.html</xsl:attribute>
									<xsl:value-of select="@fullname" />
								</a>
							</li>
						</xsl:for-each>
					</ul>
				</xsl:otherwise>
			</xsl:choose>
		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
