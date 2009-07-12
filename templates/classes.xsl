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
		</body>
		</html>
	</xsl:template>
</xsl:stylesheet>
